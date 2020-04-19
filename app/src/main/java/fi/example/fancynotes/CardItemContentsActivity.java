package fi.example.fancynotes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity displays the contents of an individual note. User can see the note text, title, tags, image,
 * audio buttons, set time for timed note and buttons for deleting and editing notes. If user has not added
 * optional features, the views are hidden.
 *
 * @author  Hanna Tuominen
 * @author  Maija Visala
 * @version 3.0
 * @since   2020-03-09
 */
public class CardItemContentsActivity extends AppCompatActivity implements DeleteDialog.OnDialogDismissListener{

    private TextView tvDesc;
    private TextView tvTitle;
    private TextView tvTags;
    private TextView tagsToDisplay;
    private TextView timeToDisplay;
    private TextView tagsText;
    private ImageView img;
    DatabaseHelper mDatabaseHelper;
    int id;
    int orderId;
    String title;
    String description;
    String dateS;
    Date time;
    private Intent intent;
    String tags ="";

    Button startAudio, stopAudio;
    String outputFileForAudio;
    MediaPlayer mediaPlayer;
    public static final int RECORD_AUDIO = 1000;
    int i = 1;

    /**
     * Lifecycle method for building the initial state of the activity.
     * Content for each card is fetched from intent extras.
     * @param savedInstanceState bundle object that is passed into method (used for restoring state if needed).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_item_contents);
        mDatabaseHelper = new DatabaseHelper(this);
        tvDesc = (TextView) findViewById(R.id.txtdesc);
        tvTitle = (TextView) findViewById(R.id.txtTitle);
        img = (ImageView) findViewById(R.id.noteImage);
        tagsToDisplay = findViewById(R.id.tagsToDisplay);
        tvTags = findViewById(R.id.tagsText);
        timeToDisplay = findViewById(R.id.timeToDisplay);
        stopAudio = (Button) findViewById(R.id.stopAudio);
        startAudio = (Button) findViewById(R.id.startAudio);
        stopAudio.setEnabled(false);

        intent = getIntent();
        description = intent.getExtras().getString("fi.example.fancynotes.note");
        id = intent.getExtras().getInt("fi.example.fancynotes.id");
        orderId =  intent.getExtras().getInt("fi.example.fancynotes.orderid");
        title = intent.getExtras().getString("fi.example.fancynotes.title");
        outputFileForAudio = intent.getExtras().getString("fi.example.fancynotes.voiceUri");
        tags = intent.getExtras().getString("fi.example.fancynotes.tags");
        dateS = intent.getExtras().getString("fi.example.fancynotes.date");

        Calendar date = Util.parseStringToCalendar(dateS);
        if(outputFileForAudio.equals("No Audio")) {
            startAudio.setEnabled(false);
            startAudio.setVisibility(View.GONE);
            stopAudio.setVisibility(View.GONE);
        }

        tvTitle.setText(title);
        tvDesc.setText(description);
        tagsToDisplay.setText(tags);
        if(date != null) {
            timeToDisplay.setText(Util.parseDateToString(date.getTime()));
        } else {
            timeToDisplay.setVisibility(View.GONE);
        }
    }

    /**
     * Create mediaplayer for playing audio the user has recorded.
     * Play audio and disable startAudio-button.
     * @param v the view from xml.
     */
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

    /**
     * Stop audio that user has recorded. Disable stopAudio-button and enable startAudio-button.
     * @param v the view from xml.
     */
    public void stopAudio(View v) {
        stopAudio.setEnabled(false);
        startAudio.setEnabled(true);

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    /**
     * Lifecycle method of the activity. Set possible image and tags when activity starts.
     */
    @Override
    public void onStart() {
        super.onStart();

        String imgString = getIntent().getExtras().getString("fi.example.fancynotes.imageUri");
        if(imgString != null && !imgString.isEmpty()){
            Uri image = Uri.parse(imgString);
            img.setImageURI(image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    900);
            img.setLayoutParams(params);
        }

        //check what elements are shown in the layout
        if(tags == null){
            tvTags.setVisibility(View.GONE);
            tagsToDisplay.setVisibility(View.GONE);
        }
        if(dateS == null){
            timeToDisplay.setVisibility(View.GONE);
        }
    }

    /**
     * Trigger new activity for editing current note. Send note data as intent extras.
     * @param view the view from xml.
     */
    public void editNote(View view) {
        Intent newIntent = new Intent(this, EditNoteActivity.class);
        newIntent.putExtra("fi.example.fancynotes.note", description);
        newIntent.putExtra("fi.example.fancynotes.thumbnail", title);
        newIntent.putExtra("fi.example.fancynotes.id", id);
        newIntent.putExtra("fi.example.fancynotes.orderid", orderId);
        newIntent.putExtra("fi.example.fancynotes.title", title);
        newIntent.putExtra("fi.example.fancynotes.tags", tags);
        if(time != null) {
            newIntent.putExtra("fi.example.fancynotes.date", time.toString());
        }
        startActivity(newIntent);
    }

    /**
     * Trigger dialog when user wishes to delete a note.
     * @param view the view from xml.
     */
    public void deleteNote(View view) {
        DeleteDialog dialog = new DeleteDialog();
        dialog.show(getSupportFragmentManager(), "settingsDialog");
    }

    /**
     * Call DatabaseHelper objects deleteNote-method to remove data from SQLite.
     * Show toast to inform user of the state of removing note.
     * Remove audio file.
     * After removing note, open CardViewActivity.
     */
    private void removeNote() {
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
        Intent i = new Intent(this, CardViewActivity.class);
        i.putExtra("fi.example.fancynotes.orderidofdeleted", orderId);

        startActivity(i);
    }

    /**
     * Method to create new toast messages with different messages.
     * @param message the message wanted to display.
     */
    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * When user wants to delete note, a warning dialog appears. Note is
     * removed only after user has confirmed.
     */
    @Override
    public void onDialogDismissListener() {
        removeNote();
    }
}
