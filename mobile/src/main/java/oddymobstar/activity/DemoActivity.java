package oddymobstar.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import oddymobstar.crazycourier.R;


import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.util.Configuration;
import oddymobstar.util.CoreDialog;

public class DemoActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Configuration configuration;
    private CoreDialog core;

    //db helper can test this out.  and fix up the map to work.  is a start.
    //also need to set up base configs.
    private DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setUpMapIfNeeded();

        if (!dbHelper.hasPreLoad()) {
            dbHelper.addBaseConfiguration();
        }
        configuration = new Configuration(dbHelper.getConfigs());

        /*

        next up...and i dont want to program all night have big hockey game tomoz

        (plus i need to clean house and r6 before match and do some washing)

        menus launch list driven dialogs (its fine for this long term needs custom list fragments etc)

        we also need sub menu actions to them

        ie
        my alliances: message (plus zone filters), leave
        my topics: message, amend zone filters, leave
        global topics: register (plus zone filters)


        Key thing we need before all this is register and ongoing grid / core updating.

        This must be driven by the service and is really first test before making stupid lists (which is dead easy anyway).


        Finally: create (topic | alliance) is basically a popup with a text field to name it and then send it to server.


        And advanced: is add members to alliances.  currently this will be locked to bluetooth | nfc so is some work.

        Well nfc isnt. and neither is bluetooth either.

        Once we have done all this we then need to be able to allow display of the items on the map using specific markers
        per type.

         */


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {

            case R.id.configuration:
                core = new CoreDialog().newInstance(dbHelper, null, CoreDialog.CONFIG);
                core.show(transaction, "core_dialog");

                break;

            case R.id.createAlliance:
                /*
                 we simply name an alliance and send to server.  a test to manage
                 */
                core = new CoreDialog().newInstance(dbHelper,null, CoreDialog.CREATE_ALLIANCE);
                core.show(transaction, "core_dialog");

                break;
            case R.id.createTopic:
                /*
                we name a topic and send to server.  its global
                 */
                core = new CoreDialog().newInstance(dbHelper,null, CoreDialog.CREATE_TOPIC);
                core.show(transaction, "core_dialog");

                break;
            case R.id.globalTopics:
                /*
                 we present user with a selectable filter that lists from server (most popular topics)
                 and then a search filter to query server for more topics
                 */
                core = new CoreDialog().newInstance(dbHelper, null, CoreDialog.GLOBAL_TOPICS);
                core.show(transaction, "core_dialog");

                break;
            case R.id.myAlliances:

   /*
                 shows my alliances for selection
                 */
                core = new CoreDialog().newInstance(dbHelper, null, CoreDialog.MY_ALLIANCES);
                core.show(transaction, "core_dialog");

                break;
            case R.id.subscribedTopics:

                 /*
                shows my subscribed topics for selection.
                 */
                core = new CoreDialog().newInstance(dbHelper, null, CoreDialog.MY_TOPICS);
                core.show(transaction, "core_dialog");

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }
}

