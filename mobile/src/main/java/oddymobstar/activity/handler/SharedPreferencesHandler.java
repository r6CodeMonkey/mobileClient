package oddymobstar.activity.handler;

import android.content.SharedPreferences;

import oddymobstar.activity.controller.DemoActivityController;

/**
 * Created by timmytime on 06/12/15.
 */
public class SharedPreferencesHandler {

    public static void handle(SharedPreferences sharedPreferences, DemoActivityController controller) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("latitude", String.valueOf(controller.locationListener.getCurrentLocation().getLatitude()));
        editor.putString("longitude", String.valueOf(controller.locationListener.getCurrentLocation().getLongitude()));
        editor.putFloat("zoom", controller.mapHelper.getMap().getCameraPosition().zoom);
        editor.putFloat("bearing", controller.mapHelper.getMap().getCameraPosition().bearing);
        editor.putFloat("tilt", controller.mapHelper.getMap().getCameraPosition().tilt);
        editor.putString("provider", controller.locationListener.getCurrentLocation().getProvider());

        editor.commit();
    }
}
