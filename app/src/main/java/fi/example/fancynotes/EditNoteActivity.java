package fi.example.fancynotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The EditNoteActivity activity is used to create a new activity upon pressing the edit note
 * button in the CardItemsContentsActivity. The activity is used to edit the notes text, tittle and tags.
 *
 * The activity gets the current id, tittle, tags and all of the other needed info via Intent from the CardItemContentsActivity.
 *
 * When user presses Ok the current note is updated via the database helper and the user is returned to the
 * cardView activity.
 *
 * @author  Hanna Tuominen
 * @version 3.0
 * @since   2020-03-09
 */

public class EditNoteActivity extends AppCompatActivity {
    //Database helper to update the note
    DatabaseHelper mDatabaseHelper;
    //Add button
    private Button addBtn;
    // edit text for the note text
    private EditText editTextNote;
    //Edit text for the tittle
    private EditText editTextTitle;

    //id of the note
    int id;
    //tittle of the note
    String title;
    String description;
    //tags of the note
    String tags;
    //date of the note
    String dateS;

    //tags edit button
    Button tagsEditButton;
    //text for the chosen new tags
    TextView chosenNewTags;
    //shared preferences for the tags list
    SharedPreferences sharedPreferences;
    //tags dialog to show the tags to choose from
    TagsDialog tagsDialog;

    /**
     * Called on creation to set up the activity correctly.
     * Updates all of the tag info that should be shown and the current texts on the note and tittle.
     *
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        addBtn = (Button) findViewById(R.id.addButton);
        editTextNote = (EditText) findViewById(R.id.editNoteEditText);
        editTextTitle = (EditText) findViewById(R.id.editTitleEditText);
        mDatabaseHelper = new DatabaseHelper(this);

        tagsEditButton = findViewById(R.id.tagsEditButton);
        chosenNewTags = findViewById(R.id.chosenNewTags);
        sharedPreferences = getSharedPreferences("tags", MODE_PRIVATE);


        //Get all of the needed info for editing the correct note
        Intent intent = getIntent();
        description = intent.getExtras().getString("fi.example.fancynotes.note");
        Uri image = Uri.parse(getIntent().getExtras().getString("fi.example.fancynotes.thumbnail"));
        id = intent.getExtras().getInt("fi.example.fancynotes.id");
        title = intent.getExtras().getString("fi.example.fancynotes.title");
        tags = intent.getExtras().getString("fi.example.fancynotes.tags");
        dateS = intent.getExtras().getString("fi.example.fancynotes.date");

        editTextNote.setText(description);
        editTextTitle.setText(title);

        //if the chosen tags list in not null then make them checked already based on the tags intent
        tagsDialog = new TagsDialog(this);
        if(tags != null) {
            tagsDialog.updateTagsOnLoad(tags);
        }
        chosenNewTags.setText(tags);

    }


    /**
     * When the updateNote has passed it's check the updateNote is called where the
     * wanted note is updated to the backend via database helper and the carview activity is called
     * to display the updated note in. If the data was successful or not, a toast message is shown.
     *
     * @param id the id of the note that should be updated
     * @param newEntryTitle the new tittle of the note that should be updated
     * @param newEntryNote the new text of the note that should be updated
     * @param tags the new tags of the note that should be updated
     */
    public void updateNote(int id, String newEntryTitle, String newEntryNote, String tags) {
        boolean insertData = mDatabaseHelper.updateNote(id, newEntryTitle, newEntryNote, tags);

        if(insertData) {
            toastMessage("Data successfully updated");
            Intent i = new Intent(this, CardViewActivity.class);
            startActivity(i);
        } else {
            toastMessage("Something went wrong!");
        }
    }

    /**
     * Used to create a simple toast message with a message text
     * @param message the message to be displayed
     */
    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * When user presses the update note button the method is called.
     *
     * The method gets the current texts from the title and note texts and selected tags and
     * sends them forward to the update note method if the note and tittle have something in them.
     * If either one of them are empty the user is prompted with a toast message informing them
     * That they need to have something in them.
     * @param v the view from xml.
     */
    public void updateNote(View v) {
        String updateEntryNote = editTextNote.getText().toString();
        String updateEntryTitle = editTextTitle.getText().toString();
        String tags = chosenNewTags.getText().toString();
        if(editTextNote.length() != 0) {
            updateNote(id, updateEntryTitle, updateEntryNote, tags);
        } else {
            toastMessage("You must put something in the note field.");
        }
    }

    /**
     * Used to create a new alert dialog with all of the possible tags listed in for the
     * user to choose new tags from or create new ones.
     *
     * @param v the view from xml.
     */
    public void chooseNewTags(View v) {
        tagsDialog.chooseTags();
    }

}
