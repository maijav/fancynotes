package fi.example.fancynotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * The ResetDialog class is used to create a new warning dialog prompted by clicking the reset
 * application button in the settings dialog.
 *
 * The Dialog warns the user about resetting the app it has two buttons, one for cancel and one for ok.
 *
 * If the user presses cancel, the application is not reset, but if the user presses ok the shared
 * preferences are cleared and the SQLite database is cleared of all of the content and then the dialog is closed.
 *
 * @author  Hanna Tuominen
 * @version 3.0
 * @since   2020-03-09
 */

public class ResetDialog extends DialogFragment {
    //the ok can cancel text views
    private TextView mActionOk, mActionCancel;
    //shared preferences in case they need to be deleted
    SharedPreferences sharedPreferences;
    // database helper in case it needs to be cleared
    DatabaseHelper mDatabaseHelper;

    /**
     * Create a new view and set the appropriate buttons and listeners to them.
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState bundle
     * @return return the created view for display
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //The view to display in the app
        View view = inflater.inflate(R.layout.dialog_resetapp,container,false);
        //Cancel button
        mActionCancel = view.findViewById(R.id.action_cancel);
        //Ok button
        mActionOk = view.findViewById(R.id.action_ok);
        //Database helper in case the database should be cleared
        mDatabaseHelper = new DatabaseHelper(getContext());

        /**
         * onClicklistener for cancelling the warning message. Closes the dialog and reopens the setting dialog.
         */
        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsDialog dialog = new SettingsDialog();
                dialog.show(getFragmentManager(), "SettingsDialog");
                getDialog().dismiss();
            }
        });

        /**
         * onClicklistener for the okay button, clears all of the sqlite database info and preferences.
         */
        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.removeAllData(getContext());
                sharedPreferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();

                sharedPreferences = getActivity().getSharedPreferences("tags", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                Toast.makeText(getContext(),"The application has been reset.",Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        return view;
    }
}
