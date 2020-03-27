package fi.example.fancynotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CardItemContentsActivity extends AppCompatActivity {

    private TextView tvDesc;
    private ImageView img;
    DatabaseHelper mDatabaseHelper;
    int id;
    int orderId;
    String title;
    String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_item_contents);
        mDatabaseHelper = new DatabaseHelper(this);
        tvDesc = (TextView) findViewById(R.id.txtdesc);
        img = (ImageView) findViewById(R.id.itemthumbnail);

        //Receiving data
        Log.d("PAPA","HI");

        Intent intent = getIntent();
        description = intent.getExtras().getString("fi.example.fancynotes.note");
        String imgString = getIntent().getExtras().getString("fi.example.fancynotes.imageUri");
        if(imgString != null){
            Uri image = Uri.parse(imgString);
            img.setImageURI(image);
        }
        id = intent.getExtras().getInt("fi.example.fancynotes.id");
        orderId =  intent.getExtras().getInt("fi.example.fancynotes.orderid");
        title = intent.getExtras().getString("fi.example.fancynotes.title");
        tvDesc.setText(description);

    }

    public void editNote(View view) {
        Intent newIntent = new Intent(this, EditNoteActivity.class);
        newIntent.putExtra("fi.example.fancynotes.note", description);
        newIntent.putExtra("fi.example.fancynotes.thumbnail", title);
        newIntent.putExtra("fi.example.fancynotes.id", id);
        newIntent.putExtra("fi.example.fancynotes.orderid", orderId);
        newIntent.putExtra("fi.example.fancynotes.title", title);
        startActivity(newIntent);
    }

    public void deleteNote(View view) {
        int deleteData = mDatabaseHelper.deleteNote(id);
        if(deleteData == 1) {
            toastMessage("Data successfully deleted");
            Intent i = new Intent(this, CardViewActivity.class);
            startActivity(i);
        } else {
            toastMessage("Something went wrong!");
        }
        Intent i = new Intent(this, CardViewActivity.class);
        Log.d("ORDERIDAFTERDELETE", orderId + " FOUND " + orderId + " deleted one had");
        i.putExtra("fi.example.fancynotes.orderidofdeleted", orderId);
        startActivity(i);
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
