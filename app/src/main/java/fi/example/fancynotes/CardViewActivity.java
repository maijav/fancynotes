package fi.example.fancynotes;

import android.app.LauncherActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardViewActivity extends AppCompatActivity{

    String notes;
    String photo1;
    List<Note> noteList;
    DatabaseHelper mDatabaseHelper;
    int item1Id;
    int item2Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);
        mDatabaseHelper = new DatabaseHelper(this);

        Cursor data = mDatabaseHelper.getAllData();

        noteList = new ArrayList<Note>();

        while(data.moveToNext()){
            int id = data.getInt(0);
            int orderId = data.getInt(1);
            String title = data.getString(2);
            String text = data.getString(3);
            Log.d("noteDATA", id + title + text);
            String backGround = "note_placeholder";
//            Log.d("noteDATA", id + title + text);
            noteList.add(new Note(id, orderId, title, text, backGround));
        }
//
//
//        Intent intent = getIntent();
//        if(intent.hasExtra("fi.example.fancynotes.notes") && intent.hasExtra("fi.example.fancynotes.photo")){
//            notes = intent.getExtras().getString("fi.example.fancynotes.notes");
//            photo1 = intent.getExtras().getString("fi.example.fancynotes.photo");
//
//            if(photo1 != null && notes != null) {
//                //photo = Uri.parse(photo1);
//                noteList.add(new Note(notes, photo1));
//            }
//        }

        RecyclerView myRv = (RecyclerView) findViewById(R.id.recyclerview_id);
        final RecyclerView_Adapter myAdapter = new RecyclerView_Adapter(this, noteList);
        myRv.setLayoutManager(new GridLayoutManager(this, 2));
        myRv.setAdapter(myAdapter);

        ItemTouchHelper moveItemHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN |
                ItemTouchHelper.UP | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, 0 ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                item1Id = noteList.get(dragged.getAdapterPosition()).getId();
                item2Id = noteList.get(target.getAdapterPosition()).getId();
                mDatabaseHelper.updateId(item1Id, 1000);
                mDatabaseHelper.updateId(item2Id, item1Id);
                mDatabaseHelper.updateId(1000, item2Id);
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
}
