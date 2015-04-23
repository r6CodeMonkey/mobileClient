package oddymobstar.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import oddymobstar.activity.DemoActivity;
import oddymobstar.crazycourier.R;

/**
 * Created by root on 13/04/15.
 */
public class PostButtons extends RelativeLayout {

    public PostButtons(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.post_buttons, this, true);

        if(!this.isInEditMode()) {

            Button tv = (Button) findViewById(R.id.cancel);

            tv.setTypeface(DemoActivity.getFont());

            tv = (Button) findViewById(R.id.post);


            tv.setTypeface(DemoActivity.getFont());

        }


    }

    public PostButtons(Context context) {
        super(context, null);
    }

}
