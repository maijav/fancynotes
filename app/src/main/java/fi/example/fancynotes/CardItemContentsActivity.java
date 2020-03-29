package fi.example.fancynotes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class CardItemContentsActivity extends AppCompatActivity {

    private TextView tvDesc;
    private ImageView img;
    DatabaseHelper mDatabaseHelper;
    int id;
    int orderId;
    String title;
    String description;
    private Intent intent;

    Button startAudio, stopAudio;
    String outputFileForAudio;
    MediaPlayer mediaPlayer;
    public static final int RECORD_AUDIO = 1000;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_item_contents);
        mDatabaseHelper = new DatabaseHelper(this);
        tvDesc = (TextView) findViewById(R.id.txtdesc);
        img = (ImageView) findViewById(R.id.itemthumbnail);
        stopAudio = (Button) findViewById(R.id.stopAudio);
        startAudio = (Button) findViewById(R.id.startAudio);
        stopAudio.setEnabled(false);
        //Receiving data
        Log.d("PAPA","HI");

        intent = getIntent();
        description = intent.getExtras().getString("fi.example.fancynotes.note");
        id = intent.getExtras().getInt("fi.example.fancynotes.id");
        orderId =  intent.getExtras().getInt("fi.example.fancynotes.orderid");
        title = intent.getExtras().getString("fi.example.fancynotes.title");
        outputFileForAudio = intent.getExtras().getString("fi.example.fancynotes.voiceUri");
        Log.d("AUDIONULL", outputFileForAudio);

        if(outputFileForAudio.equals("No Audio")) {
            Log.d("AUDIONULL", outputFileForAudio + " was indeed null");
            startAudio.setEnabled(false);
            startAudio.setVisibility(View.GONE);
            stopAudio.setVisibility(View.GONE);
        }

        tvDesc.setText(description);
    }

    public void startAudio(View v) {
        stopAudio.setEnabled(true);
        startAudio.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(outputFileForAudio);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
            stopAudio.setEnabled(false);
            startAudio.setEnabled(true);
        });
        mediaPlayer.start();

        Toast.makeText(getApplicationContext(), "Audio started", Toast.LENGTH_LONG).show();
    }

    public void stopAudio(View v) {
        stopAudio.setEnabled(false);
        startAudio.setEnabled(true);

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        String imgString = getIntent().getExtras().getString("fi.example.fancynotes.imageUri");
        if(imgString != null){
            Uri image = Uri.parse(imgString);
            img.setImageURI(image);
        }
    }

    public void editNote(View view) {
        Intent newIntent = new Intent(this, EditNoteActivity.class);
        newIntent.putExtra("fi.example.fancynotes.note", description);
        newIntent.putExtra("fi.example.fancynotes.thumbnail", title);
        newIntent.putExtra("fi.example.fancynotes.id", id);
        newIntent.putExtra("fi.example.fancynotes.orderid", orderId);
        newIntent.putExtra("fi.example.fancynotes.title", title);
        startActivity(newIntent);
    }

    public void deleteNote(View view) {

        int deleteData = mDatabaseHelper.deleteNote(id);
        if(deleteData == 1) {
            toastMessage("Data successfully deleted");
            Intent i = new Intent(this, CardViewActivity.class);
            startActivity(i);
        } else {
            toastMessage("Something went wrong!");
        }
        File file = new File(outputFileForAudio);
        boolean deleted = file.delete();
        Log.d("AUDIODELETED", deleted + " audio deleted");

        Intent i = new Intent(this, CardViewActivity.class);
        Log.d("ORDERIDAFTERDELETE", orderId + " FOUND " + orderId + " deleted one had");
        i.putExtra("fi.example.fancynotes.orderidofdeleted", orderId);

        startActivity(i);
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
