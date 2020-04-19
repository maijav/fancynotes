package fi.example.fancynotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import static android.content.Context.MODE_PRIVATE;
/**
 * The TagsDialog class is used to create a new tags dialog prompted by clicking the add tags and edit tags
 *  buttons in the newNoteActivity and EditNoteActivity.
 *
 * The Dialog shows the user a list of all of the current tags that user has created that can be chosen from.
 * The user can choose existing tags or create new tags and the tags will be added to the notes.
 *
 * If the user presses cancel, the dialog is closed normally and user is returned to activity with an empty string.
 * If the user presses ok the tags will be updated according to the picked ones and displayed to be seen before the
 * final adding of the note.
 * If the user decides to create a new tag a new dialog is created and if the user saved that tag it will be added to the tag list and
 * automatically be chosen as one of the tags picked for the note.
 *
 * @author Hanna Tuominen
 * @version 3.0
 * @since 2020-03-09
 */
public class TagsDialog extends AlertDialog {
    //the chosen tags to be shown in the activity
    TextView chosenTags;
    // the list of all of the tags to choose from
    String[] tagsArray;
    // boolean array to list if the curren tag is chosen or not
    boolean[] checkedTags;
    // array list to keep track of all of the chosen tags
    ArrayList<Integer> mSelectedTags = new ArrayList<>();
    //shared preferences to get access to the list of tags
    SharedPreferences sharedPreferences;
    // the string used to display selected tags
    String tagsToBeAdded;

    /**
     * Constructor to create a new tags dialog
     * @param context the context of the app
     */
    protected TagsDialog(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences("tags", MODE_PRIVATE);
        //add tags to the dialog list
        addTags();
        //if the context is editNoteAcitvity then update the tags text with tags that are already chosen
        if(context.getClass().toString().contains("EditNoteActivity")) {
            chosenTags = (TextView) ((Activity)context).findViewById(R.id.chosenNewTags);
        } else {
            chosenTags = (TextView) ((Activity)context).findViewById(R.id.chosenTags);
        }
    }

    /**
     * Method is used to return the string of the selected tags to be saved or used.
     * @return
     */
    public String getSelectedTags() {
        return tagsToBeAdded;
    }

    /**
     * Method is used create the tags array to be displayed with the correct tags preselected.
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
     * Update the selected tags on the first load of the dialog
     * @param tags list of the tags preselected
     */
    public void updateTagsOnLoad(String tags) {
        int i = 0;
        for(String sTag: tagsArray) {
            if(tags.contains(sTag)) {
                checkedTags[i] = true;
                mSelectedTags.add(i);
            }
            i++;
        }
    }

    /**
     * Create a new alertDialog to the screen.
     */
    public void chooseTags() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Tags available to choose from");
        /**
         * update the selectedTags array when clicking the tag in the list.
         */
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
        /**
         * update the tags string to be added
         */
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                tagsToBeAdded = "";
                for(int i = 0; i < mSelectedTags.size(); i++) {
                    tagsToBeAdded = tagsToBeAdded + tagsArray[mSelectedTags.get(i)];
                    if(i != mSelectedTags.size() - 1) {
                        tagsToBeAdded +=",";
                    }
                }
                chosenTags.setText(tagsToBeAdded);
            }
        });

        /**
         * dismiss the dialog
         */
        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        /**
         * clear all selected tags, not in use at this moment.
         */
        mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for(int i = 0; i < checkedTags.length; i++) {
                    checkedTags[i] = false;
                    mSelectedTags.clear();
                    chosenTags.setText("");
                }
            }
        });

        /**
         * add new tags button
         */
        mBuilder.setNeutralButton("+", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                createNewTag();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * Create a new dialog for creating new tags to be added to the tags list.
     * If a new tag is added it is saved to the tags list and the tagsDialog is prompted open again
     * with updated sizes of the tags arrays. On dismiss the dialog is simply closed.
     */
    public void createNewTag() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("New tag");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        mBuilder.setView(input);

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //get the item in the new tag
                String item = input.getText().toString();
                //create a temp array for new tagsArray
                String[] temp = new String[tagsArray.length + 1];
                //create a new boolarray for checkedtags array
                boolean[] tempBoolArray = new boolean[checkedTags.length + 1];
                //update the temp arrays with the real array infos
                for(int i = 0; i < tagsArray.length; i++) {
                    temp[i] = tagsArray[i];
                    tempBoolArray[i] = checkedTags[i];
                }
                //add the new tag to the tags array
                temp[temp.length -1] = item;
                //set the new added tags to false
                tempBoolArray[tempBoolArray.length -1] = false;

                //update the real arrays with the temps
                tagsArray = temp;
                checkedTags = tempBoolArray;
                //update the shared preferences for the tags
                for(int i = 0; i < tagsArray.length; i++) {
                    updateTagsPrefs("tag"+i, tagsArray[i]);
                }
                //update the tagsarraylength to be the new length of the array
                updateTagsPrefs("tagArray-length", tagsArray.length +"");
                //add the new tag created to the selected tags to be auto chosen
                mSelectedTags.add(tagsArray.length -1);
                //update the tags array to have the new tags selected
                checkedTags[checkedTags.length -1] = true;
                //create the the tags dialog again with the updated info
                chooseTags();
            }
        });

        /**
         * Cancel the creation of new tags and dismiss the dialog.
         */
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    /**
     * Method is used to update the tags preferences string values for tags
     * @param field the wanted tag name
     * @param value the new value for the tag
     */
    private void updateTagsPrefs(String field, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(field, value);
        editor.apply();
    }

    /**
     * method is used to return wanted tag boolean info based on the name
     * @param field the name of the wanted tag
     * @return the boolean value based on the wanted tag
     */
    private String getTagsPrefs(String field) {
        return sharedPreferences.getString(field, "");
    }
}
