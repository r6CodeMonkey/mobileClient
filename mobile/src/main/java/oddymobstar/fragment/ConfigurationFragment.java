package oddymobstar.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oddymobstar.adapter.ConfigurationAdapter;
import oddymobstar.crazycourier.R;

/**
 * Created by root on 24/04/15.
 */
public class ConfigurationFragment extends android.support.v4.app.ListFragment {


    private ConfigurationAdapter adapter;
    private Cursor cursor;

    public ConfigurationFragment() {

    }

    public void init(Cursor cursor) {
        this.cursor = cursor;

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.configuration_fragment, container, false);


        adapter = new ConfigurationAdapter(getActivity(), cursor, true);

        setListAdapter(adapter);


        return view;
    }


}
