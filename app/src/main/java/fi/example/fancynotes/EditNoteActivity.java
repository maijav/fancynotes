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
    String dateS;

    Button tagsEditButton;
    TextView chosenNewTags;
    SharedPreferences sharedPreferences;
    TagsDialog tagsDialog;

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


        Intent intent = getIntent();
        description = intent.getExtras().getString("fi.example.fancynotes.note");
        Uri image = Uri.parse(getIntent().getExtras().getString("fi.example.fancynotes.thumbnail"));
        id = intent.getExtras().getInt("fi.example.fancynotes.id");
        title = intent.getExtras().getString("fi.example.fancynotes.title");
        tags = intent.getExtras().getString("fi.example.fancynotes.tags");
        dateS = intent.getExtras().getString("fi.example.fancynotes.date");

        editTextNote.setText(description);
        editTextTitle.setText(title);

        tagsDialog = new TagsDialog(this);
        if(tags != null) {
            tagsDialog.updateTagsOnLoad(tags);
        }
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

    public void chooseNewTags(View v) {
        tagsDialog.chooseTags();
    }

}
