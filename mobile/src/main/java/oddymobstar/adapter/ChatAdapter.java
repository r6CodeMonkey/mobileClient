package oddymobstar.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.util.widget.ChatBubble;

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

        String myMessage = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MY_MESSAGE));

        if (myMessage.equals("Y")) {

            TextView tv = (TextView) v.findViewById(R.id.message);
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.LEFT);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            tv.setLayoutParams(params);


            tv = (TextView) v.findViewById(R.id.from);
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.LEFT);

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            tv.setLayoutParams(params);


            tv = (TextView) v.findViewById(R.id.datetime);
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.RIGHT);

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.RIGHT;
            tv.setLayoutParams(params);


            ChatBubble chatBubble = (ChatBubble) v.findViewById(R.id.message_bubble);

            chatBubble.setBackground(context.getResources().getDrawable(R.drawable.speech));

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;

            chatBubble.setLayoutParams(params);
            chatBubble.setGravity(Gravity.LEFT);
        } else {
            TextView tv = (TextView) v.findViewById(R.id.message);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.RIGHT);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.RIGHT;
            tv.setLayoutParams(params);


            tv = (TextView) v.findViewById(R.id.from);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.LEFT);

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            tv.setLayoutParams(params);

            tv = (TextView) v.findViewById(R.id.datetime);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.LEFT);

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.LEFT;
            tv.setLayoutParams(params);

            ChatBubble chatBubble = (ChatBubble) v.findViewById(R.id.message_bubble);
            chatBubble.setBackground(context.getResources().getDrawable(R.drawable.speech2));

            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.RIGHT;

            chatBubble.setLayoutParams(params);
            chatBubble.setGravity(Gravity.RIGHT);
        }


        bindView(v, context, cursor);


        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ChatBubble tv = (ChatBubble) view.findViewById(R.id.message_bubble);

        tv.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MESSAGE_CONTENT)));
        java.util.Date date = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.MESSAGE_TIME)));
        tv.setDateTime(date.toString());
        tv.setFrom(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.MESSAGE_AUTHOR)));


    }
}
