package oddymobstar.activity.listener;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.util.Configuration;
import oddymobstar.util.graphics.RoundedImageView;

/**
 * Created by timmytime on 03/12/15.
 */
public class LocationListener implements android.location.LocationListener {


    private DemoActivityController controller;
    private Location currentLocation;

    public LocationListener(DemoActivityController controller) {
        this.controller = controller;
    }


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        //  callBack.setLocationUpdated(location);

        currentLocation = location;

        Log.d("location changed", "location changed");
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (controller.mapHelper.getMarkerMap().containsKey("Me")) {
            controller.mapHelper.getMarkerMap().get("Me").remove();
        }

        if (controller.materialsHelper.userImage != null) {
            if (controller.materialsHelper.userImage.getUserImage() != null) {


                Bitmap bitmap = controller.materialsHelper.userImage.getUserImage().copy(Bitmap.Config.ARGB_8888, true);

                int w = bitmap.getWidth();


                Bitmap roundBitmap = RoundedImageView.getCroppedBitmap(bitmap, w);

                //236 - 354
                controller.mapHelper.getMarkerMap().put("Me", controller.mapHelper.getMap().addMarker(new MarkerOptions().position(currentLatLng).title("Me").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(roundBitmap, 354, 354, false))).flat(false)));

            }
        } else {
            controller.mapHelper.getMarkerMap().put("Me", controller.mapHelper.getMap().addMarker(new MarkerOptions().position(currentLatLng).title("Me")));
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .tilt(controller.mapHelper.getMap().getCameraPosition().tilt)
                .bearing(controller.mapHelper.getMap().getCameraPosition().bearing)
                .zoom(controller.mapHelper.getMap().getCameraPosition().zoom)
                .build();

        controller.mapHelper.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        controller.locationManager.removeUpdates(this);

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        controller.locationManager.requestLocationUpdates(provider, Long.parseLong(controller.configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, this);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}


