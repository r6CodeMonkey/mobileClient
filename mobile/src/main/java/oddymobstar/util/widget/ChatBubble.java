package oddymobstar.util.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import oddymobstar.crazycourier.R;

/**
 * Created by root on 13/04/15.
 */
public class ChatBubble extends LinearLayout {

    private TextView message;
    private TextView dateTime;
    private TextView author;

    public ChatBubble(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.chat_bubble, this, true);

        author = (TextView) view.findViewById(R.id.from);
        message = (TextView) view.findViewById(R.id.message);
        dateTime = (TextView) view.findViewById(R.id.datetime);


    }

    public ChatBubble(Context context) {
        super(context, null);


    }

    public void setMessage(String msg) {
        message.setText(msg);
    }

    public void setFrom(String from) {
        author.setText(from);

    }

    public void setDateTime(String dt) {
        dateTime.setText(dt);

    }


}
