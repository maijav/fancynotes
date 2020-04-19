package fi.example.fancynotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import static android.content.Context.MODE_PRIVATE;
/**
 * The SettingsDIalog class is used to create a new settings dialog prompted by clicking the settings
 *  button in the MainMenu in MainActivity.
 *
 * The Dialog shows the user the current settings of the notifications and a button to reset the application.
 *
 * If the user presses cancel, the dialog is closed normally and user is returned to main activity.
 * If the user changes the notification settings and presses ok, the settings preferences is updated accordingly.
 * If the user clicks the reset button a new ResetDialog is created to warn the user about resetting the app.
 *
 * @author Hanna Tuominen
 * @author Maija Visala
 * @version 3.0
 * @since 2020-03-09
 */

public class SettingsDialog extends DialogFragment {
    private TextView mActionOk, mActionCancel;
    SharedPreferences sharedPreferences;
    RadioButton allowNotif;
    RadioButton dontAllowNotif;
    private boolean allowNotifPicked;
    RadioGroup radioGroup;
    private Button resetApp;
    ResetDialog dialog;

    /**
     * Oncreate view creates a new dialog view to be shown for the user and presets it correctly to be shown.
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState bunddle
     * @return the created view to be displayed.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_settings,container,false);
        sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);

        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        allowNotif = view.findViewById(R.id.allowNotif);
        dontAllowNotif = view.findViewById(R.id.dontAllowNotif);
        radioGroup = view.findViewById(R.id.radio);

        //set the radio buttons correctly based on the preferences saved
        if(!getSettingsPrefs("notif")){
            allowNotif.setChecked(true);
        }else{
            dontAllowNotif.setChecked(true);
        }
        resetApp = view.findViewById(R.id.reset_app);

        //get this from saved memory when that is created.
        allowNotifPicked = getSettingsPrefs("notif");
        //set the checkbox to the past settings (true or false).

        /**
         * on checked change listener for the notification radiobutton group to set the allow
         * notification boolean correctly for saving to preferences
         */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.allowNotif) {
                    allowNotifPicked = false;
                } else if (checkedId == R.id.dontAllowNotif) {
                    allowNotifPicked = true;
                }

            }

        });
        /**
         * cancel on click listener to close the dialog
         */
        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        /**
         * Ok on click listener to update the preferences for notifications.
         */
        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save this to memory when ever that option been created
                updateSettingsPrefs("notif", allowNotifPicked);
                getDialog().dismiss();
            }
        });

        /**
         * reset app on click listener to prompt resetDialog
         */
        resetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ResetDialog();
                dialog.show(getFragmentManager(), "ResetDialog");
                getDialog().dismiss();
            }
        });
        return view;
    }

    /**
     * Method is used to update the shared preferences of settings and their boolean values.
     * @param field the name of the item
     * @param value the value of the item
     */
    private void updateSettingsPrefs(String field, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(field, value);
        editor.apply();
    }

    /**
     * Method is used to return wanted boolean preference from the settings preferences.
     * @param field the name of the item
     * @return boolean value via the wanted name
     */
    private Boolean getSettingsPrefs(String field) {
        return sharedPreferences.getBoolean(field, false);
    }

}
