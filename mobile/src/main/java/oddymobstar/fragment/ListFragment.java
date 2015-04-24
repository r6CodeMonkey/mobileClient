package oddymobstar.fragment;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import oddymobstar.activity.DemoActivity;
import oddymobstar.adapter.CoreAdapter;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.util.widget.CreateView;

/**
 * Created by root on 14/04/15.
 */
public class ListFragment extends android.support.v4.app.ListFragment {


    /*
      can contain...Alliances or Topics.
     */
    public static final int MY_ALLIANCES = 0;


    private int type = MY_ALLIANCES;


    private AdapterView.OnItemClickListener onClickListener = null;
    private CursorAdapter adapter = null;
    private DBHelper dbHelper;

    private CreateView createView;

    private class LoadCursors extends AsyncTask<String, Void, String> {

        private Cursor cursor;

        @Override
        protected String doInBackground(String... params) {

            if (dbHelper == null) {
                dbHelper = DBHelper.getInstance(getActivity());
            }

            cursor = getCursor(type);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            adapter = getCursorAdapter(type, cursor);
            setListAdapter(adapter);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    public ListFragment() {
        setRetainInstance(true);
    }

    public int getType() {
        return type;
    }


    public void init(int type, AdapterView.OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.type = type;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment, container, false);

        createView = (CreateView) view.findViewById(R.id.create_view);

        if (dbHelper == null) {
            dbHelper = DBHelper.getInstance(getActivity());
        }

        new LoadCursors().execute("");

        setListAdapter(adapter);

        ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setOnItemClickListener(onClickListener);

        lv.setFastScrollEnabled(true);
        lv.setFastScrollAlwaysVisible(true);

        lv.setDivider(null);
        lv.setDividerHeight(0);

        Button button = (Button) view.findViewById(R.id.list_create);
        button.setTypeface(DemoActivity.getFont());

        lv.setFastScrollEnabled(true);
        lv.setFastScrollAlwaysVisible(true);

        return view;
    }

    private Cursor getCursor(int type) {

        switch (type) {
            case MY_ALLIANCES:
                return dbHelper.getAlliances();
        }
        return null;
    }

    private CursorAdapter getCursorAdapter(int type, Cursor cursor) {

        return new CoreAdapter(getActivity(), cursor, true, type);

    }


    public void refreshAdapter() {
        if (adapter != null) {
            switch (type) {
                case MY_ALLIANCES:
                    adapter.changeCursor(dbHelper.getAlliances());
                    break;
            }


        }
    }


    @Override
    public void onListItemClick(ListView arg0, View arg1, int arg2, long arg3) {
        onClickListener.onItemClick(arg0, arg1, arg2, arg3);
    }

    public String getCreateText() {
        return createView.getCreateText();
    }

    public void clear() {
        createView.clear();
    }

    public void clearAdapter() {
        setListAdapter(null);
        adapter = null;
    }


    public void onDestroy() {
        super.onDestroy();
        try {
            adapter.getCursor().close();
        } catch (Exception e) {

        }
    }

}
