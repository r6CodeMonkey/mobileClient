package oddymobstar.activity.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.LocationManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.HashMap;
import java.util.Map;

import oddymobstar.activity.DemoActivity;
import oddymobstar.activity.listener.LocationListener;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.model.AllianceMember;
import oddymobstar.util.Configuration;
import oddymobstar.util.SubUTM;
import oddymobstar.util.UTM;
import oddymobstar.util.UTMGridCreator;
import oddymobstar.util.graphics.RoundedImageView;

/**
 * Created by timmytime on 03/12/15.
 */
public class MapHelper {

    private LocationListener locationListener;
    private LocationHelper locationHelper;
    private LocationManager locationManager;
    private Configuration configuration;
    private MaterialsHelper materialsHelper;


    private DemoActivity main;
    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private Map<String, Marker> markerMap = new HashMap<>();

    public float bearing, tilt, zoom = 0.0f;

    private Polygon myUTM;
    private Polygon mySubUTM;

    private PolygonOptions utmOptions;



    public MapHelper(DemoActivity main, Configuration configuration, MaterialsHelper materialsHelper){
        this.main = main;
        this.configuration = configuration;
        this.materialsHelper = materialsHelper;
        this.locationHelper = new LocationHelper(configuration);
    }


    public void setUpMapIfNeeded(DBHelper dbHelper) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) main.getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap(dbHelper);
            }
        }
    }


    private void setUpMap(DBHelper dbHelper) {
        map.getUiSettings().setZoomControlsEnabled(false);
        //now dd our last known location.
        locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener(locationManager, this, configuration, materialsHelper);

        locationHelper.initLocationUpdates(locationManager, locationListener);


        SharedPreferences sharedPreferences = main.getPreferences(Context.MODE_PRIVATE);


        zoom = sharedPreferences.getFloat("zoom", 10.0f);
        tilt = sharedPreferences.getFloat("tilt", 0.0f);
        bearing = sharedPreferences.getFloat("bearing", 0.0f);

        LatLng currentLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("latitude", "0.0")),
                Double.parseDouble(sharedPreferences.getString("longitude", "0.0")));

        //need to manage map markers too.  as per old code ie remove and re add.  do this now....joy

        /*
        really need to then move towards a jira project to manage it as got loads of shit todo.

        bsically its
         */

        if (materialsHelper.userImage != null) {
            if (materialsHelper.userImage.getUserImage() != null) {
                Bitmap bitmap = materialsHelper.userImage.getUserImage().copy(Bitmap.Config.ARGB_8888, true);

                int w = bitmap.getWidth();


                Bitmap roundBitmap = RoundedImageView.getCroppedBitmap(bitmap, w);


                markerMap.put("Me", map.addMarker(new MarkerOptions().position(currentLatLng).title("Me").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(roundBitmap, 354, 354, false))).flat(false)));
            }
        } else {
            markerMap.put("Me", map.addMarker(new MarkerOptions().position(currentLatLng).title("Me")));
        }


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .tilt(tilt)
                .bearing(bearing)
                .zoom(zoom)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        if (myUTM != null) {
            myUTM.remove();
        }

        if (mySubUTM != null) {
            mySubUTM.remove();
        }

        if (!configuration.getConfig(Configuration.CURRENT_UTM).getValue().trim().isEmpty()) {
            utmOptions = UTMGridCreator.getUTMGrid(new UTM(configuration.getConfig(Configuration.CURRENT_UTM).getValue())).strokeColor(main.getResources().getColor(android.R.color.holo_purple));
            myUTM = map.addPolygon(utmOptions);

            PolygonOptions subUtmOptions = UTMGridCreator.getSubUTMGrid(new SubUTM(configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue()), utmOptions).strokeColor(main.getResources().getColor(android.R.color.holo_orange_dark));
            mySubUTM = map.addPolygon(subUtmOptions);

        }


        //we now need to add any of our alliance members in...
        Cursor allianceMembers = dbHelper.getAllianceMembers();

        while (allianceMembers.moveToNext()) {
            AllianceMember allianceMember = new AllianceMember(allianceMembers);

            if (markerMap.containsKey(allianceMember.getKey())) {
                markerMap.get(allianceMember.getKey()).remove();
            }
            markerMap.put(allianceMember.getKey(), map.addMarker(new MarkerOptions().position(new LatLng(allianceMember.getLatitude(), allianceMember.getLongitude())).title(allianceMember.getKey())));

        }

        allianceMembers.close();


    }



    public GoogleMap getMap(){return map;}
    public Map<String, Marker> getMarkerMap(){return markerMap;}
    public void initLocationUpdates(){ locationHelper.initLocationUpdates(locationManager, locationListener);}
    public LocationHelper getLocationHelper(){return  locationHelper;}
    public LocationListener getLocationListener(){return locationListener;}
}
