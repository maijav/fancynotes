package fi.example.fancynotes;

import android.content.Intent;
import android.net.Uri;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewNoteActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    private Button addBtn;
    private EditText editTextNote;
    private EditText editTextTitle;
    private String noteBackground;
    static int RequestCode = 1;
    static Uri pickedImgUri;
    private Button addImgBtn;
    private LinearLayout addImgLayout;

    static int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);
        addBtn = (Button) findViewById(R.id.addButton);
        editTextNote = (EditText) findViewById(R.id.newNoteEditText);
        editTextTitle = (EditText) findViewById(R.id.newTitleEditText);
        addImgLayout = (LinearLayout) findViewById(R.id.addImgLayout);
        mDatabaseHelper = new DatabaseHelper(this);
        noteBackground = "note_placeholder";

        addImgBtn = new Button(this);
        addImgBtn.setText("add image");

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        addImgLayout.addView(addImgBtn);
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

        boolean insertData = mDatabaseHelper.addData(orderId,newEntryTitle, newEntryNote, noteBackground);

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

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("Image/*");
        startActivityForResult(galleryIntent, RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RequestCode && data != null){
            //the user has picked a suitable image
            //reference to image is saved to a Uri variable

            ImageView usersPhoto = new ImageView(this);

            pickedImgUri = data.getData();
            usersPhoto.setImageURI(pickedImgUri);

            addImgLayout.removeView(addImgBtn);
            addImgLayout.addView(usersPhoto);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500,500);
            usersPhoto.setLayoutParams(params);

        }
    }
}
