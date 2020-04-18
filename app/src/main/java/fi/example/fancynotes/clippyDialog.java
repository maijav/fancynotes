package fi.example.fancynotes;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;

public class clippyDialog extends AppCompatActivity {
    LinearLayout clippy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_clippy);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setDimAmount(0);
        clippy = (LinearLayout) findViewById(R.id.clippy);
        startAnimation();
    }

    public void startAnimation(){
        ObjectAnimator moveRightImg = ObjectAnimator.ofFloat(clippy, "translationX", 200f, 40f);
        moveRightImg.setDuration(1500);
        moveRightImg.start();
    }

    public void closeClippy(View v){
        this.finish();
    }
}
