package oddymobstar.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import oddymobstar.crazycourier.R;

/**
 * Created by root on 10/03/15.
 */
public class ChatDialog extends DialogFragment {


 private static DialogInterface.OnClickListener postListener;


 private View view;

 public ChatDialog(){

 }

    public static ChatDialog newInstance(DialogInterface.OnClickListener listener) {
        postListener = listener;
        return new ChatDialog();
    }

    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_dialog, null);
        builder.setView(view);
        builder.setTitle("Chat");



     /*   switch (createSource) {

            case CREATE_ALLIANCE:
                builder.setTitle("Create Alliance");
                break;
            case CREATE_TOPIC:
                builder.setTitle("Create Topic");
                break;

        }*/
//  dirty hack anyway.  who cares at present for demo
        //


        builder.setPositiveButton("POST", postListener);

        this.view = view;

        return builder.create();

    }

    public String getPost() {
        return ((EditText) view.findViewById(R.id.post)).getText().toString();
    }

}
