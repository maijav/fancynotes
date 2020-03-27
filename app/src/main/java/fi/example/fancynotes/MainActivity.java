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

    Button startAudio, stopRecord, stopAudio, startRecord;
    File outputFileForAudio;
    private MediaRecorder myAudioRecorder;
    MediaPlayer mediaPlayer;
    public static final int RECORD_AUDIO = 1000;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);
        text = findViewById(R.id.fetchingText);
        fetch = findViewById(R.id.fetchButton);
        System.out.println("Moi Hanski");
        System.out.println("Moi Maija");

        startAudio = (Button) findViewById(R.id.startAudio);
        stopRecord = (Button) findViewById(R.id.stopRecord);
        stopAudio = (Button) findViewById(R.id.stopAudio);
        startRecord = (Button) findViewById(R.id.startRecord);

        startRecord.setEnabled(true);
        stopRecord.setEnabled(false);
        startAudio.setEnabled(true);
        stopAudio.setEnabled(false);


        if(!checkPermissionFromDevice()) {
            requestPermission();
        }

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void setupMediaRecorder() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFileForAudio.getAbsolutePath());
    }

    public void stopRecord(View v) {
        myAudioRecorder.stop();
        stopRecord.setEnabled(false);
        startAudio.setEnabled(true);
        startRecord.setEnabled(true);
        stopAudio.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
    }

    public void startRecord(View v) {
        if(checkPermissionFromDevice()) {
            outputFileForAudio = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording" + i + ".3gp");
            i++;
            setupMediaRecorder();
            try {
                myAudioRecorder.prepare();
                myAudioRecorder.start();

            } catch(IOException e) {
                e.printStackTrace();
            }

            startRecord.setEnabled(false);
            stopRecord.setEnabled(true);
            stopAudio.setEnabled(false);
            startRecord.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }

    }

    public void startAudio(View v) {
        startRecord.setEnabled(false);
        stopRecord.setEnabled(false);
        stopAudio.setEnabled(true);
        startAudio.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(String.valueOf(outputFileForAudio));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
        Toast.makeText(getApplicationContext(), "Audio started", Toast.LENGTH_LONG).show();
    }

    public void stopAudio(View v) {
        startRecord.setEnabled(true);
        stopRecord.setEnabled(false);
        stopAudio.setEnabled(false);
        startAudio.setEnabled(true);

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            setupMediaRecorder();
        }
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
