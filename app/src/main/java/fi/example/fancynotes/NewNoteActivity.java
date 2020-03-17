package fi.example.fancynotes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewNoteActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    private Button addBtn;
    private EditText editTextNote;
    private EditText editTextTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);
        addBtn = (Button) findViewById(R.id.addButton);
        editTextNote = (EditText) findViewById(R.id.newNoteEditText);
        editTextTitle = (EditText) findViewById(R.id.newTitleEditText);
        mDatabaseHelper = new DatabaseHelper(this);

    }


    public void addNote(String newEntryTitle, String newEntryNote) {
        boolean insertData = mDatabaseHelper.addData(newEntryTitle, newEntryNote);

        if(insertData) {
            toastMessage("Data successfully Inserted");
            Intent i = new Intent(this, CardViewActivity.class);
            startActivity(i);
        } else {
            toastMessage("Something went wrong!");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void addNewNote(View v) {
        Log.d("NewNoteActivityAdd","works");
        String newEntryNote = editTextNote.getText().toString();
        String newEntryTitle = editTextTitle.getText().toString();
        if(editTextNote.length() != 0) {
            addNote(newEntryTitle,newEntryNote);
        } else {
            toastMessage("You must put something in the note field.");
        }
    }
}
