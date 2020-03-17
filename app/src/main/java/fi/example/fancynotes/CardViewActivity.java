package fi.example.fancynotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);

        noteList = new ArrayList<Note>();
        noteList.add(new Note("Test", "Test"));
        noteList.add(new Note("Test", "Test"));
        noteList.add(new Note("Test", "Test"));

        Intent intent = getIntent();
        if(intent.hasExtra("fi.example.fancynotes.notes") && intent.hasExtra("fi.example.fancynotes.photo")){
            notes = intent.getExtras().getString("fi.example.fancynotes.notes");
            photo1 = intent.getExtras().getString("fi.example.fancynotes.photo");

            if(photo1 != null && notes != null) {
                //photo = Uri.parse(photo1);
                noteList.add(new Note(notes, photo1));
            }
        }


        RecyclerView myRv = (RecyclerView) findViewById(R.id.recyclerview_id);
        final RecyclerView_Adapter myAdapter = new RecyclerView_Adapter(this, noteList);
        myRv.setLayoutManager(new GridLayoutManager(this, 2));
        myRv.setAdapter(myAdapter);

        ItemTouchHelper moveItemHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN |
                ItemTouchHelper.UP | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, 0 ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();

                Collections.swap(noteList, position_dragged, position_target);

                myAdapter.notifyItemMoved(position_dragged, position_target);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        moveItemHelper.attachToRecyclerView(myRv);
    }
}
