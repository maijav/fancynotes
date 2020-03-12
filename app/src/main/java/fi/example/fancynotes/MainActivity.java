package fi.example.fancynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);
        System.out.println("Moi Hanski");
        System.out.println("Moi Maija");
    }

    public void createNewNote(View v) {
        Intent i = new Intent(this, NewNoteActivity.class);
        startActivity(i);
    }

    public ArrayList<String> getStringArrayListFromSQLiteDatabase() {
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            //Get the value from the database in column 1 AKA "note"
            listData.add(data.getString(1));
        }
        return listData;
    }
}
