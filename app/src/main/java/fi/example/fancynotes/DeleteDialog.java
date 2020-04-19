package fi.example.fancynotes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

/**
 * The DeleteDialog is created when the player tries to delete a chosen note.
 *
 * The dialog is created in the CardItemContentsActivity upon pressing the delete note button.
 * The dialog is used to warn the user about the fact that the note cannot be returned upon deletion.
 * If the user presses OK a new dialogDismissListener is created to inform that the note should be deleted
 * in the CardItemsContents Activity.
 *
 * The class contain an interface that is implemented in the callback in the CardItemsContentsActivity.
 *
 * @author Hanna Tuominen
 * @version 3.0
 * @since 2020-03-09
 */

public class DeleteDialog extends DialogFragment {
    private TextView mActionOk, mActionCancel;
    DatabaseHelper mDatabaseHelper;

    OnDialogDismissListener mCallback;

    /**
     * Container Activity must implement this interface
     */
    public interface OnDialogDismissListener {
        public void onDialogDismissListener();
    }

    /**
     * Lifecycle method for the container activity.
     *
     * @param context the context for the container activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDialogDismissListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDialogDismissListener");
        }
    }

    /**
     * Create a new view and set the appropriate buttons and listeners to them.
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState bundle
     * @return return the created view for display
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete,container,false);
        mActionCancel = view.findViewById(R.id.action_cancel);
        mActionOk = view.findViewById(R.id.action_ok);
        mDatabaseHelper = new DatabaseHelper(getContext());

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call the dialog dismiss listener in callback
                mCallback.onDialogDismissListener();
                getDialog().dismiss();
            }
        });

        return view;
    }
}
