package fi.example.fancynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Moi Hanski");
        System.out.println("Moi Maija");
    }

    public void createNewNote(View v) {
        Intent i = new Intent(this, NewNoteActivity.class);
        startActivity(i);
    }
}
