package fi.example.fancynotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import static android.content.Context.MODE_PRIVATE;


public class ResetDialog extends DialogFragment {
    private TextView mActionOk, mActionCancel;
    SharedPreferences sharedPreferences;
    DatabaseHelper mDatabaseHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_resetapp,container,false);
        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        mDatabaseHelper = new DatabaseHelper(getContext());

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsDialog dialog = new SettingsDialog();
                dialog.show(getFragmentManager(), "SettingsDialog");
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.removeAllData(getContext());
                sharedPreferences = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();

                sharedPreferences = getActivity().getSharedPreferences("tags", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                Log.d("SettingsDialog","Settings reset: ");
                Toast.makeText(getContext(),"The application has been reset.",Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        return view;
    }


}
