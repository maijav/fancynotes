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
import java.util.Optional;

public class CardViewActivity extends AppCompatActivity{

    String notes;
    String photo1;
    List<Note> noteList;
    DatabaseHelper mDatabaseHelper;
    int item1Id;
    int item2Id;
    int orderIdOfDeleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);
        mDatabaseHelper = new DatabaseHelper(this);

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
            Log.d("noteDATA", id + title + text);
            Log.d("ORDERID", orderId + " from cardview");

//            Log.d("noteDATA", id + title + text);
            noteList.add(new Note(id, orderId, title, text, backGround, imageUri, voiceUri,tags));
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
    public void onBackPressed() {
        Intent i= new Intent(CardViewActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
