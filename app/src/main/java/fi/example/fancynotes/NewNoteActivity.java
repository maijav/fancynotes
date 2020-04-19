package fi.example.fancynotes;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.text.format.DateFormat;

/**
 * The NewNoteActivity class is used in creation of new notes for the application.
 *
 * The class is used by the mainActivity when user wishes to create a new note for the app.
 * In this class the user has the possibility to write the mandatory tittle and note text for the new note.
 * The user also has the possibility to add pictures, audio, tags and choose the color of their note
 * along with the possibility to make the note into a timed note which will prompt a push notification when
 * the picked time has arrived. The timed note can only be added if the user has the notification settings on.
 *
 * When user has chosen everything they need for the note they can press the 'add' button which
 * redirects the user to the cardViewActivity where the created new note is displayed after the
 * note has been saved to the SQLite backend.
 *
 * @author  Hanna Tuominen
 * @author  Maija Visala
 * @version 3.0
 * @since   2020-03-09
 */

public class NewNoteActivity extends AppCompatActivity implements CameraDialog_Fragment.NoticeDialogListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    DatabaseHelper mDatabaseHelper;

    private Button addBtn;
    private EditText editTextNote;
    private EditText editTextTitle;

    private String noteBackground;
    private String imageUri;
    String currentPhotoPath;

    static final int ImageRequestCode = 1;
    static final int ImageCaptureRequestCode = 1;
    static Uri pickedImgUri;

    private Button addImgBtn;
    private LinearLayout addImgLayout;
    private ImageView usersPhoto;

    static int orderId;
    Button stopRecord, startRecord;
    String outputFileForAudio;
    private MediaRecorder myAudioRecorder;
    public static final int RECORD_AUDIO = 1000;
    boolean hasRecorded = false;

    Button tagsButton;
    TextView chosenTags;
    SharedPreferences sharedPreferences;
    TagsDialog tagsDialog;

    private Button timedNoteBtn;
    private TextView chosenTimeTV;
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    Calendar sendForward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        addBtn = (Button) findViewById(R.id.addButton);
        editTextNote = (EditText) findViewById(R.id.newNoteEditText);
        editTextTitle = (EditText) findViewById(R.id.newTitleEditText);
        addImgLayout = (LinearLayout) findViewById(R.id.addImgLayout);

        tagsButton = findViewById(R.id.tagsButton);
        chosenTags = findViewById(R.id.chosenTags);

        timedNoteBtn = findViewById(R.id.timedNoteBtn);
        if(getSettingsPrefs("notif")){
            timedNoteBtn.setEnabled(false);
            timedNoteBtn.setBackgroundColor(getResources().getColor(R.color.colorlightGray));
        }

        chosenTimeTV = findViewById(R.id.chosenTimeTV);

        stopRecord = (Button) findViewById(R.id.stopRecord);
        startRecord = (Button) findViewById(R.id.startRecord);

        startRecord.setEnabled(true);
        stopRecord.setEnabled(false);

        if(!checkPermissionFromDevice()) {
            requestPermission();
        }

        mDatabaseHelper = new DatabaseHelper(this);
        noteBackground = "note_placeholder";
        imageUri = null;
        usersPhoto = new ImageView(this);

        addImgBtn = findViewById(R.id.addImgBtn);

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraDialog();
            }
        });

//        addImgLayout.addView(addImgBtn);
        tagsDialog = new TagsDialog(this);

        if(!getSettingsPrefs("clippy")){
            Intent clippyIntent = new Intent(this, clippyDialog.class);
            startActivity(clippyIntent);
            updateSettingsPrefs("clippy", true);
        }
    }


    /**
     * Method is called when user clicks the choose tags button in the xml.
     *
     * This opens a new tagsDialog dialog that shows all of the tags the user can choose from.
     * @param v the view from xml.
     */
    public void chooseTags(View v) {
        tagsDialog.chooseTags();
    }

    //Ask user, if they want to use phone camera or pick image from phone gallery
    public void showCameraDialog() {
        DialogFragment dialog = new CameraDialog_Fragment();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    //Get input for camera dialog
    @Override
    public void sendChoice(String input) {
        if(input.equals("Phone gallery")){
            openGallery();
        }else{
            PackageManager pm = this.getPackageManager();
            final boolean deviceHasCameraFlag = pm.hasSystemFeature((PackageManager.FEATURE_CAMERA_ANY));
            if( !deviceHasCameraFlag) {
                toastMessage("Device has no camera");
            }else{
                openCamera();
            }
        }
    }

    /**
     * Used to request permission from the user to write to the storage and record audio.
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                RECORD_AUDIO);
    }

    /**
     * Method checks the results of the permission prompt and gives a appropriate toast message.
     * @param requestCode the request code
     * @param permissions all of the permissions
     * @param grantResults all of the results
     */
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

    /**
     * Used to check the permission from the user to write to the storage and access to recording audio.
     * @return if the access is granted return true else false
     */
    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Method is used to set up the media recorder each time a new audio file needs to be created
     * with the wanted output file name.
     */
    private void setupMediaRecorder() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFileForAudio);
    }

    /**
     * The stop recording method is called when the stop recording button is enabled (only when the user is recoding voice)
     * and upon clicking it the recorder stops itself nad the appropriate buttons are set enabled and disabled.
     * After all of this a toast message is displayed to the user to notify them of the stopped recorder.
     * @param v the view from xml.
     */
    public void stopRecord(View v) {
        myAudioRecorder.stop();
        addBtn.setEnabled(true);
        stopRecord.setEnabled(false);
        startRecord.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
        hasRecorded = true;
    }

    /**
     * The method is called when the user clicks the startRecord button in the xml view.
     *
     * The method checks for permission first from the user, if there is already permission to record
     * and access the users camera and voice recording. If there is not, the user is prompted the question.
     * If there is permission the media recorder is set up and the recording begins and the appropriate buttons
     * are set enabled and disabled.
     * @param v the view from xml.
     */
    public void startRecord(View v) {
        addBtn.setEnabled(false);
        if(checkPermissionFromDevice()) {
            outputFileForAudio = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "_audio_recording.3gp";
            setupMediaRecorder();
            try {
                myAudioRecorder.prepare();
                myAudioRecorder.start();

            } catch(IOException e) {
                e.printStackTrace();
            }

            startRecord.setEnabled(false);
            stopRecord.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }

    }

    /**
     *  Method is called when the user clicks the timed note button in the activity.
     *
     *  Method creates a new Calendar date and sets the date pickers date as the current date and creates
     *  a new datepicker to be displayed to the user.
     * @param v the view from xml.
     */
    public void timedNote(View v) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(NewNoteActivity.this, NewNoteActivity.this, year, month, day);
        datePickerDialog.show();
    }

    /**
     *  Method is used to get the date pickers information for the year, month and date and then creating a time picker for the time to the date.
     *
     * @param datePicker date picker
     * @param i the year attribute
     * @param i1 the month attribute
     * @param i2 the day attribute
     */
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1; // months are indexed starting at 0
        dayFinal = i2;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(NewNoteActivity.this, NewNoteActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();

    }

    /**
     * Used to set the final hour and minute attributes and display the chosenTime in the textview.
     * @param timePicker time picker
     * @param i hour int
     * @param i1 minute int
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;
        chosenTimeTV.setText( dayFinal + "-" + monthFinal + "-" + yearFinal +  " " + hourFinal + ":" + minuteFinal);
    }

    /**
     * Add note is called when the addnewnote has passed it's check.
     *
     * The method gathers all of the needed information about the
     * tittle, text, image, order, audio, tags, date and note color and sends it forward to the
     * SQLite database via the database helper. After this the user is shown a toast message
     * telling them whether the adding the note to the database was successful or not.
     *
     * @param newEntryTitle the tittle of the newly created note
     * @param newEntryNote the text of the newly created note
     */
    public void addNote(String newEntryTitle, String newEntryNote) {
        orderId = Util.getNewOrderId(this);
        if(pickedImgUri != null){
            imageUri = pickedImgUri.toString();
            pickedImgUri = null;
        }

        // if no audio has been created make the file No audio for backend
        if(outputFileForAudio == null) {
            outputFileForAudio = "No Audio";
        }

        // set the wanted timed note date
        sendForward = Calendar.getInstance();
        sendForward.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);

        //Check if user has chosen timed note and if so, start worker for notification
        if(yearFinal != 0) {
            startTimedNoteWorker(sendForward.getTime());
        }

        // if the yearFinal is 0 set the dateForward to null for the backend
        Date dateToForward;
        if(yearFinal == 0) {
            dateToForward = null;
        } else  {
            dateToForward = sendForward.getTime();
        }

        //Add data to SQLite database
        boolean insertData = mDatabaseHelper.addData(orderId,newEntryTitle, newEntryNote, noteBackground, imageUri, outputFileForAudio, tagsDialog.getSelectedTags(), dateToForward);

        // Inform the user about the success of inserting of the note to the database.
        if(insertData) {
            toastMessage("Data successfully Inserted");
            Intent i = new Intent(this, CardViewActivity.class);
            startActivity(i);
        } else {
            toastMessage("Something went wrong!");
        }
    }

    /**
     * Method to create new toast messages with different messages.
     * @param message the message wanted to display.
     */
    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * Method used when clicking the 'add' button in the activity.
     *
     * The method checks if the tittle and text of the note has been set or not and proceeds accordingly.
     *
     * If the tittle and note has been set the method calls addNote method with their texts and if not
     * the method gives the user a toast message informing them that they need to set the tittle and note text.
     * @param v the view from xml.
     */
    public void addNewNote(View v) {
        String newEntryNote = editTextNote.getText().toString();
        String newEntryTitle = editTextTitle.getText().toString();
        if(editTextNote.length() != 0 && editTextTitle.length() != 0) {
            addNote(newEntryTitle,newEntryNote);
        } else {
            toastMessage("You must put something in the note field and title field.");
        }
    }

    public void chooseNoteBackground(View v) {
        switch (v.getId()) {
            case R.id.blueNote:
                noteBackground = "note_placeholder2";
                break;
            case R.id.pinkNote:
                noteBackground = "note_placeholder";
                break;
            case R.id.yellowNote:
                noteBackground = "note_placeholder3";
                break;
            default:
            throw new RuntimeException("Unknow button ID");
        }
    }

    //User can choose image from phone gallery
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ImageRequestCode);
    }

    //User can take images with phone camera
    public void openCamera(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {

                pickedImgUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pickedImgUri);
                startActivityForResult(takePictureIntent, ImageCaptureRequestCode);
            }
        }
    }

    //After user has chosen image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ImageRequestCode && data != null){
            //the user has picked an image from phone gallery
            //reference to image is saved to a Uri variable

            pickedImgUri = data.getData();
            usersPhoto.setImageURI(pickedImgUri);
        }else if (requestCode == ImageCaptureRequestCode && resultCode == RESULT_OK) {
            usersPhoto.setImageURI(pickedImgUri);
        }

        //If user has pressed back button
        if (resultCode == Activity.RESULT_CANCELED) {
            pickedImgUri = null;
        }

        if(pickedImgUri != null){
            addImgLayout.removeView(addImgBtn);
            addImgLayout.addView(usersPhoto);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 400);
        usersPhoto.setLayoutParams(params);
    }


    /**
     * onBackPressed method is used to redirect the user to the main activity upon clicking the
     * back button on their phone.
     *
     * When user clicks the back button on their phone the method checks if the user has been recording
     * a new audio note and removes it from the memory and then it redirects the user from NewNoteActivity
     * to the MainActivity.
     */
    @Override
    public void onBackPressed() {
        if(hasRecorded) {
            myAudioRecorder.stop();
            File file = new File(outputFileForAudio);
            boolean deleted = file.delete();
        }

        Intent i= new Intent(NewNoteActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Start worker to trigger notification on selected point of time
    public void startTimedNoteWorker(Date date){
        //set a tag to be able to cancel all work of this type if needed
        final String workTag = "notificationWork";

        long dateDiffInMills = calculateDelay(date);
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(dateDiffInMills, TimeUnit.MILLISECONDS)
                .addTag(workTag)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(notificationWork);
    }

    public long calculateDelay(Date date){
        Date date2 = new Date();

        return getDateDiff(date, date2);
    }

    public static long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date1.getTime() - date2.getTime();
        return diffInMillies;
    }

    /**
     * UpdateSettingsPrefs is a method used when the settings preferences need to be updated.
     *
     * It is used when the clippy has been showed the first time and it should not be shown again.
     *
     * @param field the name of the shared preference item to be saved
     * @param value the value of the shared preference item to be saved
     */

    private void updateSettingsPrefs(String field, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(field, value);
        editor.apply();
    }

    /**
     * getSettingsPrefs is used to get the 'settings' preferences that are needed such as
     * the clippy and notification settings.
     *
     * These settings are used to either show clippy or don't show it or for the user to have
     * possibility to make timed notes.
     *
     * @param field the wanted name for the boolean shared preferences
     * @return returns the wanted boolean value based on the field attribute
     */
    private Boolean getSettingsPrefs(String field) {
        return sharedPreferences.getBoolean(field, false);
    }

}
