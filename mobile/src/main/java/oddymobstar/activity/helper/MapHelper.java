package oddymobstar.activity.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;

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
import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.activity.handler.SharedPreferencesHandler;
import oddymobstar.activity.listener.LocationListener;
import oddymobstar.crazycourier.R;
import oddymobstar.model.AllianceMember;
import oddymobstar.util.Configuration;
import oddymobstar.util.SubUTM;
import oddymobstar.util.UTM;
import oddymobstar.util.UTMGridCreator;
import oddymobstar.util.graphics.RoundedImageView;
import oddymobstar.util.widget.GridDialog;

/**
 * Created by timmytime on 03/12/15.
 */
public class MapHelper {

    public float bearing, tilt, zoom = 0.0f;


    private DemoActivity main;
    private DemoActivityController controller;

    private GoogleMap map; // Might be null if Google Play services APK is not available.

    private Map<String, Marker> markerMap = new HashMap<>();
    private Polygon myUTM;
    private Polygon mySubUTM;

    private PolygonOptions utmOptions;


    public MapHelper(DemoActivity main, DemoActivityController controller) {
        this.main = main;
        this.controller = controller;

    }


    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) main.getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        map.getUiSettings().setZoomControlsEnabled(false);
        //now dd our last known location.
        controller.locationListener = new LocationListener(controller);
        controller.locationHelper.initLocationUpdates();


        SharedPreferences sharedPreferences = main.getPreferences(Context.MODE_PRIVATE);


        zoom = sharedPreferences.getFloat(SharedPreferencesHandler.ZOOM, 10.0f);
        tilt = sharedPreferences.getFloat(SharedPreferencesHandler.TILT, 0.0f);
        bearing = sharedPreferences.getFloat(SharedPreferencesHandler.BEARING, 0.0f);

        LatLng currentLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString(SharedPreferencesHandler.LATITUTE, "0.0")),
                Double.parseDouble(sharedPreferences.getString(SharedPreferencesHandler.LONGITUDE, "0.0")));

        //need to manage map markers too.  as per old code ie remove and re add.  do this now....joy


        if (controller.materialsHelper.userImage != null) {
            if (controller.materialsHelper.userImage.getUserImage() != null) {
                Bitmap bitmap = controller.materialsHelper.userImage.getUserImage().copy(Bitmap.Config.ARGB_8888, true);

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

        if (!controller.configuration.getConfig(Configuration.CURRENT_UTM).getValue().trim().isEmpty()) {
            utmOptions = UTMGridCreator.getUTMGrid(new UTM(controller.configuration.getConfig(Configuration.CURRENT_UTM).getValue())).strokeColor(main.getResources().getColor(android.R.color.holo_purple));
            myUTM = map.addPolygon(utmOptions);

            PolygonOptions subUtmOptions = UTMGridCreator.getSubUTMGrid(new SubUTM(controller.configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue()), utmOptions).strokeColor(main.getResources().getColor(android.R.color.holo_orange_dark));
            mySubUTM = map.addPolygon(subUtmOptions);

        }


        //we now need to add any of our alliance members in...
        Cursor allianceMembers = controller.dbHelper.getAllianceMembers();

        while (allianceMembers.moveToNext()) {
            AllianceMember allianceMember = new AllianceMember(allianceMembers);

            if (markerMap.containsKey(allianceMember.getKey())) {
                markerMap.get(allianceMember.getKey()).remove();
            }
            markerMap.put(allianceMember.getKey(), map.addMarker(new MarkerOptions().position(new LatLng(allianceMember.getLatitude(), allianceMember.getLongitude())).title(allianceMember.getKey())));

        }

        allianceMembers.close();


    }


    public GoogleMap getMap() {
        return map;
    }

    public Map<String, Marker> getMarkerMap() {
        return markerMap;
    }

    public void initLocationUpdates() {
        controller.locationHelper.initLocationUpdates();
    }


    public PolygonOptions getUtmOptions() {
        return utmOptions;
    }

    public Polygon getMyUTM() {
        return myUTM;
    }

    public void setMyUTM(Polygon myUTM) {
        this.myUTM = myUTM;
    }

    public Polygon getMySubUTM() {
        return mySubUTM;
    }

    public void setMySubUTM(Polygon mySubUTM) {
        this.mySubUTM = mySubUTM;
    }

    public GridDialog createGridDialog(String selectedGrid) {
        controller.gridDialog =
         GridDialog.newInstance(selectedGrid, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                //the magic happens...but we cant deselect our selected item its not the pattern..
                dialog.dismiss();
                controller.mapHandler.handleLocateDialog(controller.gridDialog.getGrid(which));

            }
        }, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                controller.materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

        return controller.gridDialog;

    }
}
