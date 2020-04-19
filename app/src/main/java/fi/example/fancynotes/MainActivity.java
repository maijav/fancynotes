package fi.example.fancynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

/**
 * The main activity of the project, that is always displayed first to the user upon opening the application.
 *
 * The main activity has buttons for settings and fetching notes from web and to create a new note and viewing all of the notes.
 * The settings button opens settings Dialog, new note opens NewNoteActivity and the All Notes opens the CardVewActivity.
 * If the user clicks the fetching button, a new MyService is created to fetch the notes from web and a toast is prompted when the fetching is done.
 *
 * @author Hanna Tuominen
 * @author Maija Visala
 * @version 3.0
 * @since 2020-03-09
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The default lifecycle method for on create where the toolbar and view is created and updated.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainAppBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    /**
     * When user presses the create new note button this method directs them to the NewNoteActivity via intent.
     * @param v the view from xml.
     */
    public void createNewNote(View v) {
        Intent i = new Intent(this, NewNoteActivity.class);
        startActivity(i);
    }

    /**
     * When user presses the create All notes button this method directs them to the CardViewActivity via intent.
     * @param v the view from xml.
     */
    public void seeAllNotes(View v) {
        Intent i = new Intent(this, CardViewActivity.class);
        startActivity(i);
    }

    /**
     * The onClick method called from the Options Menu when fetch is clicked.
     *
     * The method creates a new MyService service for fetching new notes from the web application.
     */
    public void onClick() {
        Intent i = new Intent(this, MyService.class);
        startService(i);
    }

    /**
     * Creates menuInflater with the welcome_activity_menu xml
     * @param menu the wanted menu
     * @return true for menu created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.welcome_activity_menu, menu);
        return true;
    }

    /**
     * Method to check what options have been clicked in the menu.
     *
     * User can choose between settingsMenu and fetchMenu and method does approppriate actions based on the choice.
     * If user clicks the settings menu a new SettingsDialog dialog is created and shown to the user and if the
     * user clicks the fetch button the onClick method is called to start a new service.
     *
     * @param item the menu item clicked
     * @return true for the clicked item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settingsMenu:
                SettingsDialog dialog = new SettingsDialog();
                dialog.show(getSupportFragmentManager(), "settingsDialog");
                return true;
            case R.id.fetchMenu:
                onClick();
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }
}
