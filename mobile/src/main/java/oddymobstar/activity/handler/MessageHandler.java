package oddymobstar.activity.handler;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.crazycourier.R;
import oddymobstar.model.AllianceMember;
import oddymobstar.model.Message;
import oddymobstar.util.Configuration;
import oddymobstar.util.SubUTM;
import oddymobstar.util.UTM;
import oddymobstar.util.UTMGridCreator;

/**
 * Created by timmytime on 06/12/15.
 */
public class MessageHandler extends Handler {

    private DemoActivityController controller;
    private AppCompatActivity main;

    public MessageHandler(AppCompatActivity main, DemoActivityController controller) {
        this.main = main;
        this.controller = controller;
    }


    public void handleList() {
        if (controller.fragmentHandler.gridFrag != null) {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    controller.fragmentHandler.gridFrag.refreshAdapter();
                }
            });

        }
    }


    public void handleUTMChange(String utm) {

        controller.materialsHandler.setNavConfigValues();


        final PolygonOptions options = UTMGridCreator.getUTMGrid(new UTM(utm)).strokeColor(main.getResources().getColor(android.R.color.holo_purple));
        main.runOnUiThread(new Runnable() {


            @Override
            public void run() {

                if (controller.mapHelper.getMyUTM() != null) {
                    controller.mapHelper.getMyUTM().remove();
                }

                controller.mapHelper.setMyUTM(controller.mapHelper.getMap().addPolygon(options));

            }
        });
    }

    public void handleSubUTMChange(String subUtm) {

        controller.materialsHandler.setNavConfigValues();

        controller.configuration = new Configuration(controller.dbHelper.getConfigs());

        //timing can cause this to fail...its no biggy its not likely required in end model.
        UTM utm = null;
        SubUTM subUTM = null;

        try {
            utm = new UTM(controller.configuration.getConfig(Configuration.CURRENT_UTM).getValue());
            //seem to get problems with this for some reason...ie integer = "".  could be data has not updated etc.
            subUTM = new SubUTM(subUtm);
        } catch (Exception e) {
            Log.d("error on utm", "error " + e.getMessage());
        }

        if (utm != null && subUTM != null) {
            PolygonOptions utmOption = UTMGridCreator.getUTMGrid(utm);
            final PolygonOptions options = UTMGridCreator.getSubUTMGrid(subUTM, utmOption).strokeColor(main.getResources().getColor(android.R.color.holo_orange_dark));
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (controller.mapHelper.getMySubUTM() != null) {
                        controller.mapHelper.getMySubUTM().remove();
                    }
                    controller.mapHelper.setMySubUTM(controller.mapHelper.getMap().addPolygon(options));
                }
            });
        }

    }

    public void handleChat(final String type) {
        if (controller.fragmentHandler.chatFrag != null && controller.fragmentHandler.chatFrag.isVisible()) {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    controller.fragmentHandler.chatFrag.refreshAdapter(controller.dbHelper.getMessages(type, controller.fragmentHandler.chatFrag.getKey()));
                }
            });
        }

    }

    public void handleInvite(final String key, final String title) {

        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();


                controller.fragmentHandler.chatFrag.setCursor(controller.dbHelper.getMessages(Message.ALLIANCE_MESSAGE, key), key, title);

                transaction.replace(R.id.chat_fragment, controller.fragmentHandler.chatFrag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    public void handleAllianceMember(final AllianceMember allianceMember, final boolean zoomTo) {


        Log.d("adding marker", "marker " + allianceMember.getKey() + " lat long is " + allianceMember.getLatLng().toString());


        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (controller.mapHelper.getMarkerMap().containsKey(allianceMember.getKey())) {
                    controller.mapHelper.getMarkerMap().get(allianceMember.getKey()).remove();
                    Log.d("adding marker", "removing marker ");
                }

                Marker marker = controller.mapHelper.getMap().addMarker(new MarkerOptions().position(allianceMember.getLatLng()).title(allianceMember.getKey()));

                if (zoomTo) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(allianceMember.getLatLng())
                            .tilt(controller.mapHelper.getMap().getCameraPosition().tilt)
                            .bearing(controller.mapHelper.getMap().getCameraPosition().bearing)
                            .zoom(controller.mapHelper.getMap().getCameraPosition().zoom)
                            .build();

                    controller.mapHelper.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

                if (marker != null) {
                    controller.mapHelper.getMarkerMap().put(allianceMember.getKey(), marker);
                }

            }

        });


    }
}
