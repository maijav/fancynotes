package fi.example.fancynotes;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
        myAudioRecorder.setOutputFile(outputFileForAudio);
    }

    public void stopRecord(View v) {
        myAudioRecorder.stop();
        addBtn.setEnabled(true);
        stopRecord.setEnabled(false);
        startRecord.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
        hasRecorded = true;
    }

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

    public void timedNote(View v) {
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(NewNoteActivity.this, NewNoteActivity.this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1; // months are indexed starting at 0
        dayFinal = i2;
        Log.d("DATEE", i1 + " MONTH");
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(NewNoteActivity.this, NewNoteActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        chosenTimeTV.setText( " Day: " + dayFinal + " Month: " + monthFinal + " Year: " + yearFinal +  " Hour: " + hourFinal + " Minutes: " + minuteFinal);

    }

    public void addNote(String newEntryTitle, String newEntryNote) {
        orderId = Util.getNewOrderId(this);
        if(pickedImgUri != null){
            imageUri = pickedImgUri.toString();
            pickedImgUri = null;
        }

        if(outputFileForAudio == null) {
            outputFileForAudio = "No Audio";
        }

        sendForward = Calendar.getInstance();

        sendForward.set(yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal);
        Log.d("DATEE",  monthFinal + " MONTH " + sendForward.getTime() + " SENDFORWARDTIME");

        //Check if user has chosen timed note and if so, start worker for notification
        if(yearFinal != 0) {
            startTimedNoteWorker(sendForward.getTime());
        }

        Date dateToForward;
        if(yearFinal == 0) {
            dateToForward = null;
            Log.d("SHOULD", yearFinal +"");
        } else  {
            dateToForward = sendForward.getTime();
            Log.d("SHOULD", yearFinal +"");
        }

        //Add data to SQLite database
        boolean insertData = mDatabaseHelper.addData(orderId,newEntryTitle, newEntryNote, noteBackground, imageUri, outputFileForAudio, tagsDialog.getSelectedTags(), dateToForward);

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



    @Override
    public void onBackPressed() {
        if(hasRecorded) {
            myAudioRecorder.stop();
            File file = new File(outputFileForAudio);
            boolean deleted = file.delete();
            Log.d("AUDIODELETED", deleted + " audio deleted");
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


    private void updateSettingsPrefs(String field, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(field, value);
        editor.apply();
    }

    private Boolean getSettingsPrefs(String field) {
        return sharedPreferences.getBoolean(field, false);
    }

}
