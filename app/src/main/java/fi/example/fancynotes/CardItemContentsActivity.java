package fi.example.fancynotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CardItemContentsActivity extends AppCompatActivity {

    private TextView tvDesc;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_item_contents);

        tvDesc = (TextView) findViewById(R.id.txtdesc);
        img = (ImageView) findViewById(R.id.itemthumbnail);

        //Receiving data

        Intent intent = getIntent();
        String description = intent.getExtras().getString("fi.example.fancynotes.description");
        Uri image = Uri.parse(getIntent().getExtras().getString("fi.example.fancynotes.thumbnail"));

        tvDesc.setText(description);
        img.setImageURI(image);
    }
}
