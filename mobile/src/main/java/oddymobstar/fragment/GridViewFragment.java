package oddymobstar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oddymobstar.graphics.GridGLSurfaceView;

/**
 * Created by timmytime on 08/08/15.
 */
public class GridViewFragment extends Fragment {

    private GridGLSurfaceView surfaceView;

    public GridViewFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        surfaceView = new GridGLSurfaceView(this.getActivity());

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        surfaceView.init(this.getActivity(), displayMetrics.density);

        return surfaceView;
    }
}
