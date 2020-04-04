package fi.example.fancynotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FilterTagsDialog extends AlertDialog{

    SharedPreferences sharedPreferences;
    String[] tagsArray;
    boolean[] checkedTags;
    ArrayList<Integer> mSelectedTags = new ArrayList<>();
    String tagsToBeAdded;

    protected FilterTagsDialog(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences("tags", MODE_PRIVATE);
        addTags();
    }

    public String getSelectedTags() {
        return tagsToBeAdded;
    }

    //Add tags found in shared preferences to tagsArray (String)
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

    //Choose tags to filter notes
    public void chooseTags() {

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
//                tagsToBeAdded = "";
//                for(int i = 0; i < mSelectedTags.size(); i++) {
//                    tagsToBeAdded = tagsToBeAdded + tagsArray[mSelectedTags.get(i)];
//                    if(i != mSelectedTags.size() - 1) {
//                        tagsToBeAdded +=",";
//
//                    }
//                }
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

    private String getTagsPrefs(String field) {
        return sharedPreferences.getString(field, "");
    }
}
