package oddymobstar.activity;

import android.app.FragmentTransaction;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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

import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Handler;

import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.message.out.AllianceMessage;
import oddymobstar.message.out.CoreMessage;
import oddymobstar.service.CheService;
import oddymobstar.util.Configuration;
import oddymobstar.util.CoreDialog;
import oddymobstar.util.CreateDialog;
import oddymobstar.util.UUIDGenerator;

public class DemoActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private android.os.Handler handler = new android.os.Handler();

    private Configuration configuration;
    private CoreDialog core;
    private CreateDialog create;
    private UUIDGenerator uuidGenerator;

    private CheService cheService;
    private ServiceConnection serviceConnection;

    private Intent intent;

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
        uuidGenerator = new UUIDGenerator(configuration.getConfig(Configuration.UUID_ALGORITHM).getValue());


        intent = new Intent(this, CheService.class);

        //we need it started.
        startService(intent);

         serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                cheService = ((CheService.CheServiceBinder)service).getCheServiceInstance();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                cheService = null;
            }
        };

        //and we need to bind to it.
        bindService(intent, serviceConnection,BIND_AUTO_CREATE);


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
                create = new CreateDialog().newInstance(new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int value){
                      //grab text.  then send it to service.  service will create it on id response.
                        try {
                            //LatLng latLng, String uid, String ackId, String type
                            String ackId = uuidGenerator.generateAcknowledgeKey();
                            AllianceMessage allianceMessage = new AllianceMessage(null,null,ackId);

                            cheService.writeToSocket(allianceMessage, ackId);
                            //we also need to take the local name.  topics actually send name to server.
                            //fuck this i need a rest.
                        }catch(NoSuchAlgorithmException nsae){
                            Log.d("security", "security "+nsae.toString());
                        }catch(JSONException jse){
                            Log.d("json", "json "+jse.toString());
                        }
                    }

                }, CreateDialog.CREATE_ALLIANCE);
                create.show(transaction, "create_dialog");

                break;
            case R.id.createTopic:
                /*
                we name a topic and send to server.  its global
                 */
                create = new CreateDialog().newInstance(new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int value){
                        //grab text.  then send it to service.  service will create it on id response.
                    }

                }, CreateDialog.CREATE_TOPIC);
                create.show(transaction, "create_dialog");

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
        //and we need to bind to it.
        bindService(intent, serviceConnection,BIND_AUTO_CREATE);


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
     * this is demo code.  we want to zoom in better and use last known location etc.  but for testing its fine
     * as i can see other code working
     */
    private void setUpMap() {
        //now dd our last known location.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        demoLocationListener = new DemoLocationListener(locationManager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, demoLocationListener);
                    }
                });

            }
        }).start();
    }




    public void onPause(){
        super.onPause();

        if(serviceConnection != null){
            unbindService(serviceConnection);
        }

    }

    public void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }


    }

    public class DemoLocationListener implements LocationListener{

        private LocationManager locationManager;

        public DemoLocationListener(LocationManager locationManager){
         this.locationManager = locationManager;
        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
          //  callBack.setLocationUpdated(location);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Me"));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .tilt(mMap.getCameraPosition().tilt)
                    .bearing(mMap.getCameraPosition().bearing)
                    .zoom(mMap.getCameraPosition().zoom)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //we need to execute the method we are interested in.  note if we time out then
            locationManager.removeUpdates(this);

            //lets send our location
            if(cheService != null){
                try {
                    String ackId = uuidGenerator.generateAcknowledgeKey();
                    CoreMessage coreMessage = new CoreMessage(latLng, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), ackId, CoreMessage.PLAYER);

                    cheService.writeToSocket(coreMessage, ackId);
                }catch (JSONException jse){

                }catch (NoSuchAlgorithmException nsae){

                }
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            locationManager.removeUpdates(this);

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            locationManager.requestLocationUpdates(provider, 0, 0, this);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

    private DemoLocationListener demoLocationListener;
}

