package oddymobstar.activity.listener;

import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import oddymobstar.activity.helper.MapHelper;
import oddymobstar.activity.helper.MaterialsHelper;
import oddymobstar.util.Configuration;
import oddymobstar.util.graphics.RoundedImageView;

/**
 * Created by timmytime on 03/12/15.
 */
public class LocationListener implements android.location.LocationListener {


    private Location currentLocation;


    private LocationManager locationManager;
    private MapHelper mapHelper;
    private Configuration configuration;
    private MaterialsHelper materialsHelper;

        public LocationListener(LocationManager locationManager, MapHelper mapHelper, Configuration configuration, MaterialsHelper materialsHelper) {
            this.locationManager = locationManager;
            this.mapHelper = mapHelper;
            this.configuration = configuration;
            this.materialsHelper = materialsHelper;
        }


    public Location getCurrentLocation(){return currentLocation;}

    public void setCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
    }

    public void updateConfiguration(Configuration configuration){
        this.configuration = configuration;
    }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            //  callBack.setLocationUpdated(location);

            currentLocation = location;

            Log.d("location changed", "location changed");
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            if (mapHelper.getMarkerMap().containsKey("Me")) {
                mapHelper.getMarkerMap().get("Me").remove();
            }

            if (materialsHelper.userImage != null) {
                if (materialsHelper.userImage.getUserImage() != null) {


                    Bitmap bitmap = materialsHelper.userImage.getUserImage().copy(Bitmap.Config.ARGB_8888, true);

                    int w = bitmap.getWidth();


                    Bitmap roundBitmap = RoundedImageView.getCroppedBitmap(bitmap, w);

                    //236 - 354
                    mapHelper.getMarkerMap().put("Me", mapHelper.getMap().addMarker(new MarkerOptions().position(currentLatLng).title("Me").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(roundBitmap, 354, 354, false))).flat(false)));

                }
            } else {
                mapHelper.getMarkerMap().put("Me", mapHelper.getMap().addMarker(new MarkerOptions().position(currentLatLng).title("Me")));
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)
                    .tilt(mapHelper.getMap().getCameraPosition().tilt)
                    .bearing(mapHelper.getMap().getCameraPosition().bearing)
                    .zoom(mapHelper.getMap().getCameraPosition().zoom)
                    .build();

            mapHelper.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            locationManager.removeUpdates(this);

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            locationManager.requestLocationUpdates(provider, Long.parseLong(configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, this);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }


