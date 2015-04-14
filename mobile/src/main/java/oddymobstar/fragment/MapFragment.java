package oddymobstar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import oddymobstar.crazycourier.R;

/**
 * Created by root on 05/04/15.
 */
public class MapFragment extends Fragment {

    private GoogleMap map;

    public MapFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_fragment, container, false);
        map = ((SupportMapFragment) this.getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();


        return view;

    }
}
