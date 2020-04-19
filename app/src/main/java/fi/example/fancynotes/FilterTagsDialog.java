package fi.example.fancynotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;

/**
 * Alert dialog that prompts user to choose which tags they want to filter
 * the notes with. User can choose from the tags that are created previously
 * and saved to shared preferences.
 *
 * @author Maija Visala
 * @version 3.0
 * @since 2020-03-09
 */
public class FilterTagsDialog extends AlertDialog{

    SharedPreferences sharedPreferences;
    String[] tagsArray;
    boolean[] checkedTags;
    ArrayList<Integer> mSelectedTags = new ArrayList<>();
    ArrayList<String> chosenTags;

    /**
     * Constructor for FilterTagsDialog. Fetches saved notes from shared preferences.
     * @param context the application context
     */
    protected FilterTagsDialog(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences("tags", MODE_PRIVATE);
        addTags();
    }

    /**
     * Get array that contains selected tags. Put tags array in intent extras and
     * send broadcast for other acivities to use.
     */
    public void getSelectedTags() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        Intent i = new Intent("filterTags");
        Bundle extra = new Bundle();
        extra.putSerializable("tagsArray", chosenTags);
        i.putExtra("extra", extra);
        manager.sendBroadcast(i);
    }

    /**
     * Add tags found in shared preferences to an array.
     * Create new array that specifies which tags are chosen (true) and which are not (false)
     */
    public void addTags() {
        int tagsArrayLength;
        try{
            tagsArrayLength = Integer.parseInt(getTagsPrefs("tagArray-length"));
        }catch (NumberFormatException e) {
            tagsArrayLength = 0;
        }

        tagsArray = new String[tagsArrayLength];
        for(int i = 0; i < tagsArray.length; i++) {
            tagsArray[i] = getTagsPrefs("tag"+i);
        }
        checkedTags = new boolean[tagsArray.length];
    }

    /**
     * Dialog where user can see tags that they have saved earlier and check the boxes
     * to pick the tags they want to filter notes list with.
     * When user clicks "OK" the selected tags are added to chosen tags array.
     * "Dismiss" button cancels the activity.
     */
    public void chooseTags() {
        chosenTags = new ArrayList<String>();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Filter notes with tags:");

        //Check boxes
        if(tagsArray.length > 0) {
            mBuilder.setMultiChoiceItems(tagsArray, checkedTags, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                    if(isChecked) {
                        mSelectedTags.add(position);
                    } else {
                        mSelectedTags.remove((Integer.valueOf(position)));
                    }
                }
            });
        }

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for(int i = 0; i < mSelectedTags.size(); i++) {
                    chosenTags.add(tagsArray[mSelectedTags.get(i)]);
                }
                getSelectedTags();
            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * Get tags data from shared preferences.
     * @param field to determine, which data is fetched from shared preferences.
     * @return tags in string format
     */
    private String getTagsPrefs(String field) {
        return sharedPreferences.getString(field, "");
    }
}
