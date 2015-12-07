package oddymobstar.activity.handler;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.crazycourier.R;
import oddymobstar.fragment.ChatFragment;
import oddymobstar.fragment.ConfigurationFragment;
import oddymobstar.fragment.DeviceFragment;
import oddymobstar.fragment.GridFragment;
import oddymobstar.fragment.GridViewFragment;

/**
 * Created by timmytime on 06/12/15.
 */
public class FragmentHandler {

    public ChatFragment chatFrag = new ChatFragment();
    public GridFragment gridFrag = new GridFragment();
    public DeviceFragment deviceFragment = new DeviceFragment();
    public ConfigurationFragment confFrag = new ConfigurationFragment();
    public GridViewFragment gridViewFragment = new GridViewFragment();
    private AppCompatActivity main;
    private DemoActivityController controller;


    public FragmentHandler(AppCompatActivity main, DemoActivityController controller) {
        this.main = main;
        this.controller = controller;
    }

    public void removeFragments(boolean backPressed) {

        android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();

        try {
            chatFrag.getHiddenChatPost().setVisibility(View.GONE);
        } catch (Exception e) {

        }


        try {
            transaction.remove(chatFrag);
        } catch (Exception e) {

        }


        try {
            gridFrag.getHiddenCreateView().setVisibility(View.GONE);
        } catch (Exception e) {

        }

        try {
            gridFrag.clearAdapter();
            transaction.remove(gridFrag);
        } catch (Exception e) {

        }


        try {
            transaction.remove(confFrag);
        } catch (Exception e) {

        }
        if (!backPressed) {
            try {
              if(controller.mapHandler.CURRENT_GRID_FAB_STATE != MapHandler.UTM_FAB_STATE ||
                controller.mapHandler.CURRENT_GRID_FAB_STATE   !=  MapHandler.SUBUTM_FAB_STATE) {
                  transaction.hide(gridViewFragment);
              }
            } catch (Exception e) {

            }
        } else {
            try {
                    transaction.show(gridViewFragment);
            } catch (Exception e) {
                Log.d("show opengl", "fialed "+e.toString());
            }
        }

        //what is this for?
        try {
            controller.materialsHandler.handleFABChange(-1, R.drawable.ic_search_white_24dp, View.INVISIBLE);
            controller.materialsHelper.navToolbar.setTitle(R.string.app_name);
            controller.materialsHelper.navToolbar.setBackgroundColor(main.getResources().getColor(android.R.color.holo_red_dark));
        } catch (Exception e) {

        }

        transaction.commit();

    }

}
