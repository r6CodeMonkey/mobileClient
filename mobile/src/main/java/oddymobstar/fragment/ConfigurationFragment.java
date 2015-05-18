package oddymobstar.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import oddymobstar.adapter.ConfigurationAdapter;
import oddymobstar.crazycourier.R;

/**
 * Created by root on 24/04/15.
 */
public class ConfigurationFragment extends Fragment {


    private Cursor baseConfigs;
    private Cursor userConfigs;
    private Cursor systemConfigs;


    public ConfigurationFragment() {

    }

    public void init(Cursor baseConfigs, Cursor userConfigs, Cursor systemConfigs) {
        this.baseConfigs = baseConfigs;
        this.userConfigs = userConfigs;
        this.systemConfigs = systemConfigs;

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.configuration_fragment, container, false);


        final ExpandableListView lv = (ExpandableListView) view
                .findViewById(R.id.expandableListView1);

        lv.setAdapter(new ConfigurationAdapter(getActivity(), baseConfigs, userConfigs, systemConfigs));


        return view;
    }


}
