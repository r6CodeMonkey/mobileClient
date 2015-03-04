package oddymobstar.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;

/**
 * Created by root on 27/02/15.
 */
public class CoreDialog extends DialogFragment {

    public static final int CONFIG = 0;
    public static final int GLOBAL_TOPICS = 1;
    public static final int MY_TOPICS = 2;
    public static final int MY_ALLIANCES = 3;


    /*
    passed in from parent
     */
    private static DBHelper dbHelper;
    private CoreDialogAdapter adapter;
    private static int coreSource;

    private static DialogInterface.OnClickListener coreListener;

    public CoreDialog() {

    }

    public static CoreDialog newInstance(DBHelper db, DialogInterface.OnClickListener listener, int source) {
        coreListener = listener;
        dbHelper = db;
        coreSource = source;

        return new CoreDialog();

    }

    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.core_dialog, null);
        builder.setView(view);
        builder.setTitle("Testing");

        switch (coreSource) {

            case CONFIG:
                builder.setTitle("Configuration");
                adapter = new CoreDialogAdapter(this.getActivity(), dbHelper.getConfigs(), false, coreSource);
                break;
            case GLOBAL_TOPICS:
                builder.setTitle("Global Topics");
                adapter = new CoreDialogAdapter(this.getActivity(), dbHelper.getGlobalTopics(), false, coreSource);
                break;
            case MY_ALLIANCES:
                builder.setTitle("Alliances");
                adapter = new CoreDialogAdapter(this.getActivity(), dbHelper.getAlliances(), false, coreSource);
                break;
            case MY_TOPICS:
                builder.setTitle("Topics");
                adapter = new CoreDialogAdapter(this.getActivity(), dbHelper.getTopics(), false, coreSource);


                break;

        }

        builder.setAdapter(adapter, coreListener);

        return builder.create();

    }

    public void dismiss() {
        super.dismiss();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
