package oddymobstar.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import oddymobstar.crazycourier.R;

/**
 * Created by root on 01/03/15.
 */
public class CreateDialog extends DialogFragment {

    public static final int CREATE_TOPIC = 0;
    public static final int CREATE_ALLIANCE = 1;

    private static int createSource;

    private View view;

    private static DialogInterface.OnClickListener createListener;

    public CreateDialog() {

    }

    public static CreateDialog newInstance(DialogInterface.OnClickListener listener, int source) {
        createListener = listener;
        createSource = source;

        return new CreateDialog();
    }

    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.create_dialog, null);
        builder.setView(view);
        builder.setTitle("Create");

        switch (createSource) {

            case CREATE_ALLIANCE:
                builder.setTitle("Create Alliance");
                break;
            case CREATE_TOPIC:
                builder.setTitle("Create Topic");
                break;

        }
//  dirty hack anyway.  who cares at present for demo
        //


        builder.setPositiveButton("CREATE", createListener);

        this.view = view;

        return builder.create();

    }

    public String getName() {
        return ((EditText) view.findViewById(R.id.create)).getText().toString();
    }



    /*


     */

}
