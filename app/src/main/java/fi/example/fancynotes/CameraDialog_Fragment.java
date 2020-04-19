package fi.example.fancynotes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Dialog for prompting user to choose if they want to pick an image from phone gallery
 * or take a picture with phone camera.
 *
 * @author Maija Visala
 * @version 3.0
 * @since 2020-03-09
 */
public class CameraDialog_Fragment extends DialogFragment {

    /**
     * Interface for NoticeDialogListener
     */
    public interface NoticeDialogListener {
        public void sendChoice (String input);
    }

    public NoticeDialogListener listener;

    /**
     * Create new alert dialog that prompts user to choose if they want to pick an image from phone gallery
     * or take a picture with phone camera.
     * @param savedInstanceState bundle object that is passed into method (used for restoring state if needed).
     * @return a new alert dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        final String [] choice = getActivity().getResources().getStringArray(R.array.Choice);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_message);
        builder.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.sendChoice(choice[i]);

                getDialog().dismiss();
            }
        });
        return builder.create();
    }

    /**
     * Lifecycle method that instantiates the NoticeDialogListener to send events to the host
     *
     * @param context the context for the activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) getActivity();
        } catch (ClassCastException e) {
        }
    }

}
