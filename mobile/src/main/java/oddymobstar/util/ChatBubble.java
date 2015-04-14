package oddymobstar.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import oddymobstar.crazycourier.R;

/**
 * Created by root on 13/04/15.
 */
public class ChatBubble extends LinearLayout {

    public ChatBubble(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.chat_bubble, this, true);


    }

    public ChatBubble(Context context) {
        super(context, null);


    }

    public void setMessage(String message) {
        TextView tv = (TextView) this.findViewById(R.id.message);
        tv.setText(message);
    }

    public void setFrom(String from) {
        TextView tv = (TextView) this.findViewById(R.id.from);
        tv.setText(from);

    }

    public void setDateTime(String dateTime) {
        TextView tv = (TextView) this.findViewById(R.id.datetime);
        tv.setText(dateTime);

    }


}
