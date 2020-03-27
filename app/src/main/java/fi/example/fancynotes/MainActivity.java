package fi.example.fancynotes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
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

        Intent i = new Intent(this, MyService.class);
        startService(i);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getExtras().getLong("id");
                String title = intent.getExtras().getString("title");
                String  text = intent.getExtras().getString("text");

                Toast.makeText(context, text + " is text", Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter("donaldduck"));
    }

}
