package fi.example.fancynotes;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

public class NewNoteActivity extends AppCompatActivity implements CameraDialog_Fragment.NoticeDialogListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);
        addBtn = (Button) findViewById(R.id.addButton);
        editTextNote = (EditText) findViewById(R.id.newNoteEditText);
        editTextTitle = (EditText) findViewById(R.id.newTitleEditText);
        addImgLayout = (LinearLayout) findViewById(R.id.addImgLayout);

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

        addImgBtn = new Button(this);
        addImgBtn.setText("add image");

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraDialog();
            }
        });

        addImgLayout.addView(addImgBtn);
    }

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


    public void addNote(String newEntryTitle, String newEntryNote) {
        Cursor data = mDatabaseHelper.getLatestInOrder();
        int orderIdFromData = 0;
        while(data.moveToNext()){
            orderIdFromData = data.getInt(1);
            Log.d("ORDERID", orderIdFromData + " DATANEXT");
        }

        orderId = orderIdFromData;
        Log.d("ORDERID", orderId + " found from db");
        if(orderId == 0) {
            orderId = 1;
            Log.d("ORDERID", orderId + " new one first note");
        } else {
            orderId++;
            Log.d("ORDERID", orderId + " new one not first note");
        }

        if(pickedImgUri != null){
            imageUri = pickedImgUri.toString();
        }

        if(outputFileForAudio == null) {
            outputFileForAudio = "No Audio";
        }

        boolean insertData = mDatabaseHelper.addData(orderId,newEntryTitle, newEntryNote, noteBackground, imageUri, outputFileForAudio);

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
        if(editTextNote.length() != 0) {
            addNote(newEntryTitle,newEntryNote);
        } else {
            toastMessage("You must put something in the note field.");
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
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
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
            Log.d("KAMERA", pickedImgUri.toString());
            usersPhoto.setImageURI(pickedImgUri);
        }else if (requestCode == ImageCaptureRequestCode && resultCode == RESULT_OK) {
            usersPhoto.setImageURI(pickedImgUri);
        }
        addImgLayout.removeView(addImgBtn);
        addImgLayout.addView(usersPhoto);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500,500);
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

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
