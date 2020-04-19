package fi.example.fancynotes;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Create clippy, a helper figure in new note activity. Clippy is animated to appear from
 * the right hand side of the screen. Clippy is closed when user clicks anywhere on the screen.
 *
 * @author  Maija Visala
 * @version 3.0
 * @since   2020-03-09
 */
public class clippyDialog extends AppCompatActivity {
    LinearLayout clippy;

    /**
     * Lifecycle method for building the initial state of the activity.
     * @param savedInstanceState bundle object that is passed into method (used for restoring state if needed).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_clippy);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setDimAmount(0);
        clippy = (LinearLayout) findViewById(R.id.clippy);
        startAnimation();
    }

    /**
     * Animation that makes Clippy to appear from the right hand side of the screen.
     */
    public void startAnimation(){
        ObjectAnimator moveRightImg = ObjectAnimator.ofFloat(clippy, "translationX", 200f, 40f);
        moveRightImg.setDuration(1500);
        moveRightImg.start();
    }

    /**
     * Method to close Clippy.
     * @param v the view from xml.
     */
    public void closeClippy(View v){
        this.finish();
    }
}
