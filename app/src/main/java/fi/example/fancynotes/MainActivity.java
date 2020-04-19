package fi.example.fancynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity{
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainAppBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mDatabaseHelper = new DatabaseHelper(this);

    }

    public void createNewNote(View v) {
        Intent i = new Intent(this, NewNoteActivity.class);
        startActivity(i);
    }

    public void seeAllNotes(View v) {
        Intent i = new Intent(this, CardViewActivity.class);
        startActivity(i);
    }

    public void onClick() {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.welcome_activity_menu, menu);
        return true;
    }

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
