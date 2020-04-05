package fi.example.fancynotes;

import android.app.LauncherActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CardViewActivity extends AppCompatActivity{

    String notes;
    String photo1;
    List<Note> noteList;
    DatabaseHelper mDatabaseHelper;
    FilterTagsDialog filterTagsDialog;
    int item1Id;
    int item2Id;
    int orderIdOfDeleted = 0;
    private ActionMenuView amvMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bottomMenu);
        amvMenu = (ActionMenuView) toolbar.findViewById(R.id.amvMenu);
        amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mDatabaseHelper = new DatabaseHelper(this);
        filterTagsDialog = new FilterTagsDialog(this);

        Cursor data = mDatabaseHelper.getAllData();

        noteList = new ArrayList<Note>();
        Intent intent = getIntent();
        if(intent.hasExtra("fi.example.fancynotes.orderidofdeleted")) {
            orderIdOfDeleted =  intent.getExtras().getInt("fi.example.fancynotes.orderidofdeleted");
            Log.d("ORDERIDAFTERDELETE",  " FOUND " + orderIdOfDeleted);
        }

        while(data.moveToNext()){
            int id = data.getInt(0);
            int orderId = data.getInt(1);
            if(orderId > orderIdOfDeleted && orderIdOfDeleted > 0) {
                mDatabaseHelper.updateOrderId(id,orderId-1);
                orderId--;
            }
            Log.d("ORDERIDAFTERDELETE", orderId + " FOUND " + orderIdOfDeleted + " deleted one had");
            String title = data.getString(2);
            String text = data.getString(3);
            String backGround = data.getString(4);
            String imageUri = data.getString(5);
            String voiceUri = data.getString(6);
            String tags = data.getString(7);
            String dateS = data.getString(8);
            Log.d("noteDATA", id + title + text + dateS);
            Log.d("ORDERID", orderId + " from cardview");

            Calendar date = Util.parseStringToCalendar(dateS);
//            Log.d("noteDATA", id + title + text);
            noteList.add(new Note(id, orderId, title, text, backGround, imageUri, voiceUri, tags, date));
        }

        RecyclerView myRv = (RecyclerView) findViewById(R.id.recyclerview_id);
        final RecyclerView_Adapter myAdapter = new RecyclerView_Adapter(this, noteList);
        myRv.setLayoutManager(new GridLayoutManager(this, 2));
        myRv.setAdapter(myAdapter);

        ItemTouchHelper moveItemHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN |
                ItemTouchHelper.UP | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, 0 ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                item1Id = noteList.get(dragged.getAdapterPosition()).getOrderId();
                item2Id = noteList.get(target.getAdapterPosition()).getOrderId();
                mDatabaseHelper.updateOrderId(noteList.get(dragged.getAdapterPosition()).getId(), item2Id);
                mDatabaseHelper.updateOrderId(noteList.get(target.getAdapterPosition()).getId(), item1Id);
                moveItem(dragged.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            private void moveItem(int oldPos, int newPos){
                Note item = noteList.get(oldPos);
                item1Id = item.getId();
                noteList.remove(oldPos);
                noteList.add(newPos, item);
                myAdapter.notifyDataSetChanged();
                myAdapter.notifyItemMoved(oldPos, newPos);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });
        moveItemHelper.attachToRecyclerView(myRv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cardview_bottom_menu, amvMenu.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_add:
                movetoNewNoteActivity();
                return true;
            case R.id.nav_home:
                onBackPressed();
                return true;
            case R.id.nav_search:
                filterNotes();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void movetoNewNoteActivity(){
        Intent i= new Intent(CardViewActivity.this, NewNoteActivity.class);
        startActivity(i);
    }

    public void filterNotes(){
        filterTagsDialog.chooseTags();
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<String> tagsArray = new ArrayList<String>();
                if(intent.hasExtra("extra")){
                    Bundle extra = intent.getBundleExtra("extra");
                    tagsArray = (ArrayList<String>) extra.getSerializable("tagsArray");
                }
                createFilteredNoteList(tagsArray);
            }
        }, new IntentFilter("filterTags"));

    }

    public void createFilteredNoteList(ArrayList<String> tagsArray){
        if(tagsArray.isEmpty()){
            showAllNotes();
        }else{
            ArrayList<Note> noteListTemp = new ArrayList<Note>();
            for(Note note: noteList){
                String noteTags = note.getTags();
                for(String tag: tagsArray){
                    if(noteTags != null && !noteTags.isEmpty()){
                        if(noteTags.contains(tag)){
                            noteListTemp.add(note);
                        }
                    }
                }
            }
            RecyclerView myRv = (RecyclerView) findViewById(R.id.recyclerview_id);
            final RecyclerView_Adapter myAdapter = new RecyclerView_Adapter(this, noteListTemp);
            myRv.setLayoutManager(new GridLayoutManager(this, 2));
            myRv.setAdapter(myAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i= new Intent(CardViewActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    public void showAllNotes(){
        RecyclerView myRv = (RecyclerView) findViewById(R.id.recyclerview_id);
        final RecyclerView_Adapter myAdapter = new RecyclerView_Adapter(this, noteList);
        myRv.setLayoutManager(new GridLayoutManager(this, 2));
        myRv.setAdapter(myAdapter);
    }

}
