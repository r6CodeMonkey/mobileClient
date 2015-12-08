package oddymobstar.activity.handler;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.HashMap;
import java.util.Map;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.model.AllianceMember;
import oddymobstar.util.Configuration;
import oddymobstar.util.SubUTM;
import oddymobstar.util.UTM;
import oddymobstar.util.UTMGridCreator;
import oddymobstar.util.graphics.RoundedImageView;

/**
 * Created by timmytime on 04/12/15.
 */
public class MapHandler {


    public static final int UTM_FAB_STATE = 0;
    public static final int SUBUTM_FAB_STATE = 1;
    public static final int UTM_REGION_FAB_STATE = 2;
    public static final int BASE_STATE = 3;
    public static final int OTHER_STATE = 4;

    public static float UTM_REGION_ZOOM = 3;
    public static float UTM_ZOOM = 5;
    public static float SUB_UTM_ZOOM = 12;
    public static int CURRENT_GRID_FAB_STATE = BASE_STATE;


    public static String UTM_REGION = "";
    public boolean CLEAR_GRIDS = false;
    public PolygonOptions lastUTMOptions;
    public Map<String, Polygon> lastLocateUTMs = new HashMap<>();
    public Polygon lastLocateSubUTM;

    private Map<String, Marker> markerMap = new HashMap<>();
    private String selectedGrid;

    private DemoActivityController controller;
    private AppCompatActivity main;

    public MapHandler(AppCompatActivity main, DemoActivityController controller) {
        this.main = main;
        this.controller = controller;
    }

    public Map<String, Marker> getMarkerMap() {
        return markerMap;
    }


    public void animateToGrid(Polygon polygon, float zoom) {
        //make map zoom to the UTM and search function now allows UTM search
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target((CURRENT_GRID_FAB_STATE == UTM_FAB_STATE || CURRENT_GRID_FAB_STATE == UTM_REGION_FAB_STATE) ? UTMGridCreator.getCentreUTM(polygon.getPoints()) : UTMGridCreator.getCentreSubUTM(polygon.getPoints()))
                .tilt(controller.mapHelper.tilt)
                .bearing(controller.mapHelper.bearing)
                .zoom(zoom)
                .build();

        controller.mapHelper.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void handleLocateDialog(String grid) {

        if (CLEAR_GRIDS) {
            //try twice lol.
            for (final Polygon polygon : lastLocateUTMs.values()) {
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        polygon.remove();
                    }
                });
            }
            lastLocateUTMs.clear();
            CLEAR_GRIDS = false;
        }

        //only show 1.
        if (CURRENT_GRID_FAB_STATE == UTM_FAB_STATE && !lastLocateUTMs.isEmpty()) {

            for (final Polygon polygon : lastLocateUTMs.values()) {
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        polygon.remove();
                    }
                });
            }
            CLEAR_GRIDS = true;


        } else if (CURRENT_GRID_FAB_STATE != UTM_FAB_STATE && lastLocateSubUTM != null) {
            lastLocateSubUTM.remove();
        }

        if (CURRENT_GRID_FAB_STATE == UTM_FAB_STATE) {
            //are we a region?
            if (UTM.isUTMRegion(grid)) {
                CURRENT_GRID_FAB_STATE = UTM_REGION_FAB_STATE;
                UTM_REGION = grid;

                Polygon regionCentre = null;

                for (String utm : UTM.getUtmRegion(grid)) {
                    lastUTMOptions = UTMGridCreator.getUTMGrid(new UTM(utm)).strokeColor(main.getResources().getColor(android.R.color.holo_purple));
                    Polygon polygon = controller.mapHelper.getMap().addPolygon(lastUTMOptions);

                    if (utm.equals(UTM.getRegionCentre(grid))) {
                        regionCentre = polygon;
                    }
                    lastLocateUTMs.put(utm, polygon);

                }

                animateToGrid(regionCentre, UTM_REGION_ZOOM);

            } else {
                lastUTMOptions = UTMGridCreator.getUTMGrid(new UTM(grid)).strokeColor(main.getResources().getColor(android.R.color.holo_purple));
                lastLocateUTMs.put(grid, controller.mapHelper.getMap().addPolygon(lastUTMOptions));
                animateToGrid(lastLocateUTMs.get(grid), UTM_ZOOM);
            }

        } else if (CURRENT_GRID_FAB_STATE == UTM_REGION_FAB_STATE) {
            animateToGrid(lastLocateUTMs.get(grid), UTM_ZOOM);

        } else {
            PolygonOptions subUtmOptions = UTMGridCreator.getSubUTMGrid(new SubUTM(grid), controller.mapHelper.getUtmOptions()).strokeColor(main.getResources().getColor(android.R.color.holo_orange_dark));
            lastLocateSubUTM = controller.mapHelper.getMap().addPolygon(subUtmOptions);
            animateToGrid(lastLocateSubUTM, SUB_UTM_ZOOM);
        }

        controller.materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);

    }

    public void addUser(LatLng latLng) {
        if (controller.materialsHelper.userImage != null) {
            if (controller.materialsHelper.userImage.getUserImage() != null) {
                Bitmap bitmap = controller.materialsHelper.userImage.getUserImage().copy(Bitmap.Config.ARGB_8888, true);

                int w = bitmap.getWidth();
                Bitmap roundBitmap = RoundedImageView.getCroppedBitmap(bitmap, w);

                markerMap.put("Me", controller.mapHelper.getMap().addMarker(new MarkerOptions().position(latLng).title("Me").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(roundBitmap, 354, 354, false))).flat(false)));
            }
        } else {
            markerMap.put("Me", controller.mapHelper.getMap().addMarker(new MarkerOptions().position(latLng).title("Me")));
        }
    }

    public void addOthers() {
        //we now need to add any of our alliance members in...
        Cursor allianceMembers = controller.dbHelper.getAllianceMembers();

        while (allianceMembers.moveToNext()) {
            AllianceMember allianceMember = new AllianceMember(allianceMembers);

            if (markerMap.containsKey(allianceMember.getKey())) {
                markerMap.get(allianceMember.getKey()).remove();
            }
            markerMap.put(allianceMember.getKey(), controller.mapHelper.getMap().addMarker(new MarkerOptions().position(new LatLng(allianceMember.getLatitude(), allianceMember.getLongitude())).title(allianceMember.getKey())));

        }

        allianceMembers.close();
    }

    public void handleCamera(LatLng currentLatLng, float tilt, float bearing, float zoom) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .tilt(tilt)
                .bearing(bearing)
                .zoom(zoom)
                .build();

        controller.mapHelper.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void setSelectedGrid() {
        selectedGrid = MapHandler.CURRENT_GRID_FAB_STATE == MapHandler.SUBUTM_FAB_STATE ?
                controller.configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue() :
                MapHandler.CURRENT_GRID_FAB_STATE == MapHandler.UTM_FAB_STATE ? UTM.getUTMRegion(controller.configuration.getConfig(Configuration.CURRENT_UTM).getValue()) : "";

    }

    public String getSelectedGrid() {
        return selectedGrid;
    }


}
