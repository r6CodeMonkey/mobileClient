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
 * Created by timmytime on 18/05/15.
 */
public class BaseConfigurationAdapter extends CursorAdapter {


    public BaseConfigurationAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View v = inflator.inflate(R.layout.base_config_list_item, null);

        bindView(v, context, cursor);

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.core_item_name);

        tv.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_MARKUP)) + " - " + cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_VALUE)));

    }
}
