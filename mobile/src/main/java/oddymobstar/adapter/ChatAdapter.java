package oddymobstar.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.Date;

import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.util.ChatBubble;

/**
 * Created by root on 03/04/15.
 */
public class ChatAdapter extends CursorAdapter {

    private Context context;
    private int layout = R.layout.chat_row;


    public ChatAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View v = inflator.inflate(layout, null);

    /*    String myMessage = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MY_MESSAGE));

        if(myMessage.equals("Y")) {

            TextView tv = (TextView) v.findViewById(R.id.message);
            tv.setBackground(context.getResources().getDrawable(R.drawable.speech));
            tv.setTextColor(Color.BLACK);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;

            tv.setLayoutParams(params);
            tv.setGravity(Gravity.LEFT);
        } */


        bindView(v, context, cursor);


        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ChatBubble tv = (ChatBubble) view.findViewById(R.id.message_bubble);
        //we also need a name and date field in the chat....name is either blank, or ... date
        // TextView tv = (TextView) view.findViewById(R.id.message_bubble);

        // tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MESSAGE_CONTENT)));
        java.util.Date date = new Date(/*cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.MESSAGE_TIME))*/);

        //  tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_NAME))+"\n"+cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_VALUE)));
        tv.setFrom("Project Che");
        tv.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_NAME)) + "\n" + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_VALUE)));

        tv.setDateTime(date.toString());

    }
}
