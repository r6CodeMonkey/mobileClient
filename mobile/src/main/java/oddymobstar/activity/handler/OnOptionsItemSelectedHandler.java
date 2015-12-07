package oddymobstar.activity.handler;

import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.Polygon;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.activity.helper.MaterialsHelper;
import oddymobstar.activity.listener.MaterialsListener;
import oddymobstar.crazycourier.R;
import oddymobstar.fragment.GridFragment;
import oddymobstar.model.Config;
import oddymobstar.util.Configuration;

/**
 * Created by timmytime on 04/12/15.
 */
public class OnOptionsItemSelectedHandler {

    private AppCompatActivity main;
    private DemoActivityController controller;

    public OnOptionsItemSelectedHandler(AppCompatActivity main, DemoActivityController controller) {
        this.main = main;
        this.controller = controller;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        if (controller.materialsHelper.navToggle.onOptionsItemSelected(item)) {
            return true;
        }

        controller.fragmentHandler.removeFragments(false);
        controller.configuration = new Configuration(controller.dbHelper.getConfigs());
        //bind again if its down.
        if (controller.cheService == null) {
            main.bindService(controller.intent, controller.serviceConnection, main.BIND_AUTO_CREATE);
        }

        switch (item.getItemId()) {

            case android.R.id.home:
                controller.materialsHelper.navDrawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                handleSettings();
                break;
            case R.id.alliances:
                handleAlliance();
                break;
            case R.id.bluetooth_receive:
                //same mechanism for discovery.
                controller.viewHandler.allianceInvite(true);
                break;
            case R.id.bluetooth_send:
                //same mechanism for discovery.
                controller.viewHandler.allianceInvite(false);
                break;
            case R.id.utm:
                handleUTM();
                break;
            case R.id.sub_utm:
                handleSubUTM();
                break;
            case R.id.encrypt:
                break;
            case R.id.invite:
                break;
        }

        return true;
    }

    private void handleUTM() {

        android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();

        controller.mapHandler.CURRENT_GRID_FAB_STATE = MapHandler.UTM_FAB_STATE;
        if (!controller.mapHandler.lastLocateUTMs.isEmpty()) {

            for (final Polygon polygon : controller.mapHandler.lastLocateUTMs.values()) {
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        polygon.remove();
                    }
                });
            }
            controller.mapHandler.CLEAR_GRIDS = true;
        }

        controller.materialsHandler.handleNavToolbar(main.getResources().getColor(android.R.color.holo_purple), main.getResources().getString(R.string.menu_utm));
        controller.materialsHandler.handleFABChange(MaterialsHelper.UTM_COLOR, -1, View.VISIBLE);

        MaterialsListener.FAB_MODE = MaterialsListener.GRID_FAB;
        controller.mapHandler.setSelectedGrid();

        controller.mapHandler.animateToGrid(controller.mapHelper.getMyUTM(), MapHandler.UTM_ZOOM);
        controller.materialsHelper.navDrawer.closeDrawer(controller.materialsHelper.navigationView);

        try {
            transaction.add(R.id.grid_view_fragment, controller.fragmentHandler.gridViewFragment);
        } catch (Exception e) {

        }

        transaction.commit();
    }

    private void handleSubUTM() {

        android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();

        controller.mapHandler.CURRENT_GRID_FAB_STATE = MapHandler.SUBUTM_FAB_STATE;
        if (controller.mapHandler.lastLocateSubUTM != null) {
            controller.mapHandler.lastLocateSubUTM.remove();
        }
        controller.mapHandler.lastLocateSubUTM = null;
        controller.materialsHandler.handleNavToolbar(main.getResources().getColor(android.R.color.holo_orange_dark), main.getResources().getString(R.string.menu_subutm));
        controller.materialsHandler.handleFABChange(MaterialsHelper.SUB_UTM_COLOR, -1, View.VISIBLE);

        MaterialsListener.FAB_MODE = MaterialsListener.GRID_FAB;

        controller.mapHandler.setSelectedGrid();

        controller.materialsHelper.navDrawer.closeDrawer(controller.materialsHelper.navigationView);
        controller.mapHandler.animateToGrid(controller.mapHelper.getMySubUTM(), MapHandler.SUB_UTM_ZOOM);

        try {
            transaction.replace(R.id.grid_view_fragment, controller.fragmentHandler.gridViewFragment);
        } catch (Exception e) {

        }

        transaction.commit();

    }

    private void handleAlliance() {

        android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();

        controller.materialsHelper.navDrawer.closeDrawer(controller.materialsHelper.navigationView);

        controller.materialsHandler.handleFABChange(MaterialsHelper.ALLIANCE_COLOR, R.drawable.ic_add_circle_white_24dp, View.VISIBLE);

        controller.materialsListener.FAB_MODE = MaterialsListener.ALLIANCE_FAB;
        controller.materialsHandler.handleNavToolbar(main.getResources().getColor(android.R.color.holo_red_dark), main.getResources().getString(R.string.menu_alliances));

        controller.fragmentHandler.gridFrag.init(GridFragment.MY_ALLIANCES, controller.viewListener.getListClickListener());
        transaction.replace(R.id.chat_fragment, controller.fragmentHandler.gridFrag);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void handleSettings() {

        android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();

        controller.materialsHelper.navDrawer.closeDrawer(controller.materialsHelper.navigationView);
        controller.materialsHelper.navToolbar.setTitle(R.string.menu_settings);

        controller.fragmentHandler.confFrag.init(controller.configurationHandler, controller.dbHelper.getConfigs(Config.USER), controller.dbHelper.getConfigs(Config.SYSTEM));

        transaction.replace(R.id.chat_fragment, controller.fragmentHandler.confFrag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
