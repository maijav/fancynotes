package fi.example.fancynotes;

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
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);
        addBtn = (Button) findViewById(R.id.addButton);
        editText = (EditText) findViewById(R.id.newNoteEditText);
        mDatabaseHelper = new DatabaseHelper(this);

    }


    public void addNote(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if(insertData) {
            toastMessage("Data successfully Inserted");
        } else {
            toastMessage("Something went wrong!");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void addNewNote(View v) {
        Log.d("NewNoteActivityAdd","works");
        String newEntry = editText.getText().toString();
        if(editText.length() != 0) {
            addNote(newEntry);
        } else {
            toastMessage("You must put something in the note field.");
        }
    }
}
