package oddymobstar.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;


/**
 * Created by root on 24/04/15.
 */
public class ConfigurationAdapter extends CursorAdapter {

    private Context context;
    private int layout = R.layout.core_list_item;


    public ConfigurationAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        this.context = context;
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View v = inflator.inflate(layout, null);

        bindView(v, context, cursor);

        return v;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.core_item_name);

        tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_NAME)) + " - " + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_VALUE)));
    }


}
