package fi.example.fancynotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import static android.content.Context.MODE_PRIVATE;

public class TagsDialog extends AlertDialog {
//    Button tagsButton;
    TextView chosenTags;
    String[] tagsArray;
    boolean[] checkedTags;
    ArrayList<Integer> mSelectedTags = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String tagsToBeAdded;

    protected TagsDialog(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences("tags", MODE_PRIVATE);
        addTags();
        Log.d("GOTHERE", context.getClass().toString());
        if(context.getClass().toString().contains("EditNoteActivity")) {
            chosenTags = (TextView) ((Activity)context).findViewById(R.id.chosenNewTags);
        } else {
            chosenTags = (TextView) ((Activity)context).findViewById(R.id.chosenTags);
        }
    }

    public String getSelectedTags() {
        return tagsToBeAdded;
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

    public void chooseTags() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
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
                chosenTags.setText(tagsToBeAdded);
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
                    chosenTags.setText("");
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("New tag");

        // Set up the input
        final EditText input = new EditText(getContext());
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
                mSelectedTags.add(tagsArray.length -1);
                checkedTags[checkedTags.length -1] = true;
                chooseTags();
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
