package fi.example.fancynotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import static android.content.Context.MODE_PRIVATE;

public class SettingsDialog extends DialogFragment {
    private TextView mActionOk, mActionCancel;
    SharedPreferences sharedPreferences;
    RadioButton allowNotif;
    RadioButton dontAllowNotif;
    private boolean allowNotifPicked;
    RadioGroup radioGroup;
    private Button resetApp;
    ResetDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_settings,container,false);
        sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);

        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        allowNotif = view.findViewById(R.id.allowNotif);
        dontAllowNotif = view.findViewById(R.id.dontAllowNotif);
        radioGroup = view.findViewById(R.id.radio);
        if(!getSettingsPrefs("notif")){
            allowNotif.setChecked(true);
        }else{
            dontAllowNotif.setChecked(true);
        }
        resetApp = view.findViewById(R.id.reset_app);


        //get this from saved memory when that is created.
        allowNotifPicked = getSettingsPrefs("notif");
        //set the checkbox to the past settings (true or false).

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
        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save this to memory when ever that option been created
                updateSettingsPrefs("notif", allowNotifPicked);
                getDialog().dismiss();
            }
        });

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

    private void updateSettingsPrefs(String field, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(field, value);
        editor.apply();
    }

    private Boolean getSettingsPrefs(String field) {
        return sharedPreferences.getBoolean(field, false);
    }

}
