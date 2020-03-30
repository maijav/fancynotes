package fi.example.fancynotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    private Button addBtn;
    private EditText editTextNote;
    private EditText editTextTitle;

    int id;
    String title;
    String description;
    String tags;

    Button tagsEditButton;
    TextView chosenNewTags;
    String[] tagsArray;
    boolean[] checkedTags;
    ArrayList<Integer> mSelectedTags = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String tagsToBeAdded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        addBtn = (Button) findViewById(R.id.addButton);
        editTextNote = (EditText) findViewById(R.id.editNoteEditText);
        editTextTitle = (EditText) findViewById(R.id.editTitleEditText);
        mDatabaseHelper = new DatabaseHelper(this);

        tagsEditButton = findViewById(R.id.tagsEditButton);
        chosenNewTags = findViewById(R.id.chosenNewTags);
        sharedPreferences = getSharedPreferences("tags", MODE_PRIVATE);
        addTags();


        Intent intent = getIntent();
        description = intent.getExtras().getString("fi.example.fancynotes.note");
        Uri image = Uri.parse(getIntent().getExtras().getString("fi.example.fancynotes.thumbnail"));
        id = intent.getExtras().getInt("fi.example.fancynotes.id");
        title = intent.getExtras().getString("fi.example.fancynotes.title");
        tags = intent.getExtras().getString("fi.example.fancynotes.tags");
        updateTagsOnLoad(tags);

        editTextNote.setText(description);
        editTextTitle.setText(title);
        chosenNewTags.setText(tags);
    }


    public void updateNote(int id, String newEntryTitle, String newEntryNote, String tags) {
        boolean insertData = mDatabaseHelper.updateNote(id, newEntryTitle, newEntryNote, tags);

        if(insertData) {
            toastMessage("Data successfully updated");
            Intent i = new Intent(this, CardViewActivity.class);
            startActivity(i);
        } else {
            toastMessage("Something went wrong!");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void updateNote(View v) {
        Log.d("UpdateNoteActivityAdd","works");
        String updateEntryNote = editTextNote.getText().toString();
        String updateEntryTitle = editTextTitle.getText().toString();
        String tags = chosenNewTags.getText().toString();
        if(editTextNote.length() != 0) {
            updateNote(id, updateEntryTitle, updateEntryNote, tags);
        } else {
            toastMessage("You must put something in the note field.");
        }
    }

    public void addTags() {
        int tagsArrayLength;
        try{
            tagsArrayLength = Integer.parseInt(getTagsPrefs("tagArray-length"));
        }catch (NumberFormatException e) {
            tagsArrayLength = 0;
        }

        tagsArray = new String[tagsArrayLength];
        for(int i = 0; i < tagsArray.length; i++) {
            tagsArray[i] = getTagsPrefs("tag"+i);
        }
        checkedTags = new boolean[tagsArray.length];

    }

    public void updateTagsOnLoad(String tags) {
        int i = 0;
        for(String sTag: tagsArray) {
            if(tags.contains(sTag)) {
                checkedTags[i] = true;
                mSelectedTags.add(i);
            }
            i++;
        }
    }

    public void chooseNewTags(View v) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditNoteActivity.this);
        mBuilder.setTitle("Tags available to choose from");
        if(tagsArray.length > 0) {
            mBuilder.setMultiChoiceItems(tagsArray, checkedTags, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                    if(isChecked) {
                        mSelectedTags.add(position);
                    } else {
                        mSelectedTags.remove((Integer.valueOf(position)));
                    }
                }
            });
        }

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                tagsToBeAdded = "";
                for(int i = 0; i < mSelectedTags.size(); i++) {
                    tagsToBeAdded = tagsToBeAdded + tagsArray[mSelectedTags.get(i)];
                    if(i != mSelectedTags.size() - 1) {
                        tagsToBeAdded +=",";

                    }
                }
                chosenNewTags.setText(tagsToBeAdded);
            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for(int i = 0; i < checkedTags.length; i++) {
                    checkedTags[i] = false;
                    mSelectedTags.clear();
                    chosenNewTags.setText("");
                }
            }
        });

        mBuilder.setNeutralButton("+", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                createNewTag();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public void createNewTag() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditNoteActivity.this);
        mBuilder.setTitle("New tag");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        mBuilder.setView(input);

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = input.getText().toString();
                String[] temp = new String[tagsArray.length + 1];
                boolean[] tempBoolArray = new boolean[checkedTags.length + 1];
                for(int i = 0; i < tagsArray.length; i++) {
                    temp[i] = tagsArray[i];
                    tempBoolArray[i] = checkedTags[i];
                }
                temp[temp.length -1] = item;
                tempBoolArray[tempBoolArray.length -1] = false;

                tagsArray = temp;
                checkedTags = tempBoolArray;
                for(int i = 0; i < tagsArray.length; i++) {
                    Log.d("TAGSTOPRINT", tagsArray[i] + " " + checkedTags[i]);
                    updateTagsPrefs("tag"+i, tagsArray[i]);
                }
                updateTagsPrefs("tagArray-length", tagsArray.length +"");
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void updateTagsPrefs(String field, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(field, value);
        editor.apply();
    }

    private String getTagsPrefs(String field) {
        return sharedPreferences.getString(field, "");
    }

}
