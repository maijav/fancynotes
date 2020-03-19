package fi.example.fancynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    DatabaseHelper mDatabaseHelper;
    TextView text;
    Button fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);
        text = findViewById(R.id.fetchingText);
        fetch = findViewById(R.id.fetchButton);
        System.out.println("Moi Hanski");
        System.out.println("Moi Maija");
    }

    public void createNewNote(View v) {
        Intent i = new Intent(this, NewNoteActivity.class);
        startActivity(i);
    }

    public void seeAllNotes(View v) {
        Intent i = new Intent(this, CardViewActivity.class);
        startActivity(i);
    }

    public void onClick(View view) {

    }

}
