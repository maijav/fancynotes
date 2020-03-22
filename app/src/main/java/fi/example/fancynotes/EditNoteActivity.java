package fi.example.fancynotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    private Button addBtn;
    private EditText editTextNote;
    private EditText editTextTitle;

    int id;
    String title;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        addBtn = (Button) findViewById(R.id.addButton);
        editTextNote = (EditText) findViewById(R.id.editNoteEditText);
        editTextTitle = (EditText) findViewById(R.id.editTitleEditText);
        mDatabaseHelper = new DatabaseHelper(this);


        Intent intent = getIntent();
        description = intent.getExtras().getString("fi.example.fancynotes.note");
        Uri image = Uri.parse(getIntent().getExtras().getString("fi.example.fancynotes.thumbnail"));
        id = intent.getExtras().getInt("fi.example.fancynotes.id");
        title = intent.getExtras().getString("fi.example.fancynotes.title");

        editTextNote.setText(description);
        editTextTitle.setText(title);

    }


    public void updateNote(int id, String newEntryTitle, String newEntryNote) {
        boolean insertData = mDatabaseHelper.updateNote(id, newEntryTitle, newEntryNote);

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
        String newEntryNote = editTextNote.getText().toString();
        String newEntryTitle = editTextTitle.getText().toString();
        if(editTextNote.length() != 0) {
            updateNote(id, newEntryTitle,newEntryNote);
        } else {
            toastMessage("You must put something in the note field.");
        }
    }
}
