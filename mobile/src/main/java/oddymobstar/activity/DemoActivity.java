package oddymobstar.activity;


import android.app.ActionBar;
import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oddymobstar.connect.ConnectivityHandler;
import oddymobstar.connect.manager.BluetoothManager;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.fragment.ChatFragment;
import oddymobstar.fragment.ConfigurationFragment;
import oddymobstar.fragment.DeviceFragment;
import oddymobstar.fragment.ListFragment;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.model.Alliance;
import oddymobstar.model.Message;
import oddymobstar.service.handler.CheService;
import oddymobstar.util.Configuration;
import oddymobstar.util.UUIDGenerator;
import oddymobstar.util.widget.ConnectivityDialog;

public class DemoActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private android.os.Handler handler = new android.os.Handler();

    private Configuration configuration;
    private UUIDGenerator uuidGenerator;

    private CheService cheService;
    private ServiceConnection serviceConnection;

    public static final Long TWO_MINUTES = 120000l;

    public static final String MESSAGE_INTENT = "MESSAGE_INTENT";

    private static Typeface font = null;

    private Intent intent;
    private Intent serviceIntent;

    private View actionBar;

    private static final String BLUETOOTH_UUID = "39159dac-ead1-47ad-9975-ec8390df6f7d";

    private boolean isClient = false;

    private BroadcastReceiver bluetoothReceiver;


    private ChatFragment chatFrag = new ChatFragment();
    private ListFragment listFrag = new ListFragment();
    private DeviceFragment deviceFragment = new DeviceFragment();
    private ConfigurationFragment confFrag = new ConfigurationFragment();

    private ConnectivityHandler connectivityHandler;

    public class MessageHandler extends Handler {

        public void handleList() {
            if (listFrag != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listFrag.refreshAdapter();
                    }
                });

            }
        }

        public void handleChat(final String type) {
            if (chatFrag != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatFrag.refreshAdapter(dbHelper.getMessages(type, chatFrag.getKey()));
                    }
                });
            }

        }

        public void handleInvite(final String key, final String title){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


            chatFrag.setCursor(dbHelper.getMessages(Message.ALLIANCE_MESSAGE, key), key, title);

            transaction.replace(R.id.chat_fragment, chatFrag);
            transaction.addToBackStack(null);
            transaction.commit();
                }
            });

            }


    }

    public class DeviceDiscovery {

        private BluetoothManager bluetoothManager;


        public DeviceDiscovery(Context context) {

            bluetoothManager = new BluetoothManager(context, connectivityHandler, dbHelper, uuidGenerator, cheService, configuration, chatFrag.getKey(), currentLatLng);

        }

        public void addDevice(BluetoothDevice device) {
            bluetoothManager.addDevice(device);
            deviceFragment.refreshAdapter(device.getName());
        }

        public BluetoothManager getBluetoothManager() {
            return bluetoothManager;
        }


        public void onDiscover() {
            bluetoothManager.setIsRunning(true);
            //we launch..
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            List<String> deviceKeys = new ArrayList<>();
            for (BluetoothDevice device : bluetoothManager.getDevices()) {
                deviceKeys.add(device.getName());
            }

            deviceFragment = bluetoothManager.onDiscover(isClient);

            deviceFragment.show(transaction, "dialog");

            isClient = false;
        }


    }


    private class ListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> listView, View v, int position,
                                long id) {

            /*
            basically if they select an item we launch chat frag with an ID...
             */
            Cursor cursor = (Cursor) listFrag.getListAdapter().getItem(position);
            removeFragments();

            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            String key = "";
            switch (listFrag.getType()) {
                case ListFragment.MY_ALLIANCES:
                    key = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALLIANCE_KEY));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALLIANCE_NAME));
                    chatFrag.setCursor(dbHelper.getMessages(Message.ALLIANCE_MESSAGE, key), key, title);

                    transaction.replace(R.id.chat_fragment, chatFrag);
                    transaction.addToBackStack(null);
                    break;

            }

            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getActionBar().setCustomView(actionBar);
            getActionBar().show();

            transaction.commit();


        }
    }

    private ListClickListener listClickListener = new ListClickListener();


    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            messageHandler(message);
            Log.d("message received is ", "message " + message);

        }
    };


    private Thread locationUpdates;
    // private Thread service;

    private LatLng currentLatLng = new LatLng(0, 0);  //it does use saved prefs now

    private Map<String, Marker> markerMap = new HashMap<>();

    //db helper can test this out.  and fix up the map to work.  is a start.
    //also need to set up base configs.
    private DBHelper dbHelper = new DBHelper(this);


    private void messageHandler(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static Typeface getFont() {
        return font;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setUpMapIfNeeded();

        if (!dbHelper.hasPreLoad()) {
            dbHelper.addBaseConfiguration();
        }
        //we need one too...no shit.
        dbHelper.setMessageHandler(new MessageHandler());

        font = Typeface.createFromAsset(
                this.getAssets(), "fontawesome-webfont.ttf");

        actionBar = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.alliance_action_bar, null);

        Button b = (Button) actionBar.findViewById(R.id.message_delete);
        b.setTypeface(font);

        b = (Button) actionBar.findViewById(R.id.message_coverage);
        b.setTypeface(font);

        b = (Button) actionBar.findViewById(R.id.alliance_invite);
        b.setTypeface(font);

        //get messages in.
        configuration = new Configuration(dbHelper.getConfigs());
        uuidGenerator = new UUIDGenerator(configuration.getConfig(Configuration.UUID_ALGORITHM).getValue());


        intent = new Intent(this, CheService.class);
        serviceIntent = new Intent(this, CheService.class);
        connectivityHandler = new ConnectivityHandler(this, BLUETOOTH_UUID);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(MESSAGE_INTENT));

        startService(serviceIntent);


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                cheService = ((CheService.CheServiceBinder) service).getCheServiceInstance();
                cheService.setMessageHandler(new MessageHandler());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                cheService = null;
            }
        };

        //and we need to bind to it.
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);


        return true;
    }

    @Override
    public void onBackPressed() {

        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(true);

        super.onBackPressed();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*
        to confirm they all use this....quite likely really as we have to make a request to turn shit on...
        also need to ensure other things dont also call it..
         */
        switch (connectivityHandler.getMode()) {
            case ConnectivityDialog.BLUETOOTH:

                bluetoothReceiver = connectivityHandler.getBluetooth().getReceiver();
                registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

                connectivityHandler.getBluetooth().handle(requestCode, resultCode, data);
                if (resultCode == connectivityHandler.getBluetooth().DISCOVERABLE_SECONDS) {
                    connectivityHandler.getBluetooth().getProgress(new DeviceDiscovery(this)).show();
                }
                break;
            case ConnectivityDialog.WIFI:
                connectivityHandler.getWifi().handle(requestCode, resultCode, data);
                break;
            case ConnectivityDialog.NFC:
                connectivityHandler.getNfc().handle(requestCode, resultCode, data);
                break;
        }

    }


    private void removeFragments() {

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


        try {
            transaction.remove(chatFrag);
        } catch (Exception e) {

        }

        try {
            listFrag.clearAdapter();
            transaction.remove(listFrag);
        } catch (Exception e) {

        }

        try {
            transaction.remove(confFrag);
        } catch (Exception e) {

        }


        transaction.commit();

        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(true);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        removeFragments();


        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        configuration = new Configuration(dbHelper.getConfigs());

        //bind again if its down.
        if (cheService == null) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

        switch (item.getItemId()) {

            case R.id.configuration:


                confFrag.init(dbHelper.getConfigs());


                transaction.replace(R.id.chat_fragment, confFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                break;


            case R.id.alliances:

                listFrag.init(ListFragment.MY_ALLIANCES, listClickListener);
                transaction.replace(R.id.chat_fragment, listFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                break;

            case R.id.connect:
                //same mechanism for discovery.
                isClient = true;
                allianceInvite(null);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        if (locationUpdates == null) {
            locationUpdates = new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.parseLong(configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, demoLocationListener);
                        }
                    });

                }
            });
            locationUpdates.start();
        }
        //and we need to bind to it.
        if (cheService == null) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }


    }

    /*
      button handlers.
     */

    public void deleteMessages(View view) {
        //grab the chat frag id...
        dbHelper.deleteMessages(chatFrag.getKey());
        removeFragments();
    }

    public void messageCoverage(View view) {
        /*
          need a dialog with GLOBAL / UTM / SUBUTM...
         */
    }

    public void allianceInvite(View view) {

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        ConnectivityDialog connectivityDialog = ConnectivityDialog.newInstance(connectivityHandler, isClient);

        connectivityDialog.show(transaction, "dialog");

    }


    public void sendPost(View view) {

        /*
          now focus on this.  we need to send the chat message to service....
         */
        ///what is our chat type?  we find out we instantiate the message then it should run....
        //probably need to spend time making service write messages to db
        try {

            OutCoreMessage coreMessage = null;

            switch (listFrag.getType()) {
                case ListFragment.MY_ALLIANCES:
                    //create a message for the alliance....
                    coreMessage = new OutAllianceMessage(currentLatLng, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                    ((OutAllianceMessage) coreMessage).setAlliance(dbHelper.getAlliance(chatFrag.getKey()), OutCoreMessage.PUBLISH, OutCoreMessage.GLOBAL, chatFrag.getPost());
                    break;

            }

            cheService.writeToSocket(coreMessage);

        } catch (NoSuchAlgorithmException nse) {

        } catch (JSONException jse) {

        }

        cancelPost(null);


    }

    public void cancelPost(View view) {
        //make it clear message
        chatFrag.cancelPost();
    }

    public void createButton(View view) {

        String createText = listFrag.getCreateText();

        switch (listFrag.getType()) {

            case ListFragment.MY_ALLIANCES:

                if (!createText.trim().isEmpty()) {

                    try {
                        //LatLng latLng, String uid, String ackId, String type
                        Alliance alliance = new Alliance();
                        alliance.setName(createText);


                        OutAllianceMessage allianceMessage = new OutAllianceMessage(currentLatLng, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        allianceMessage.setAlliance(alliance, OutAllianceMessage.CREATE, OutAllianceMessage.GLOBAL, "");

                        cheService.writeToSocket(allianceMessage);

                    } catch (NoSuchAlgorithmException nsae) {
                        Log.d("security", "security " + nsae.toString());
                    } catch (JSONException jse) {
                        Log.d("json", "json " + jse.toString());
                    }
                }

                break;

        }

        listFrag.clear();


    }

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

        locationUpdates = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.parseLong(configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, demoLocationListener);
                    }
                });

            }
        });

        locationUpdates.start();


        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);


        float zoom = sharedPreferences.getFloat("zoom", 10.0f);
        float tilt = sharedPreferences.getFloat("tilt", 0.0f);
        float bearing = sharedPreferences.getFloat("bearing", 0.0f);
        currentLatLng = new LatLng(Double.parseDouble(sharedPreferences.getString("latitude", "0.0")),
                Double.parseDouble(sharedPreferences.getString("longitude", "0.0")));


        //need to manage map markers too.  as per old code ie remove and re add.  do this now....joy

        /*
        really need to then move towards a jira project to manage it as got loads of shit todo.

        bsically its
         */


        markerMap.put("Me", mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Me")));


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .tilt(tilt)
                .bearing(bearing)
                .zoom(zoom)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }


    public void onPause() {
        super.onPause();

        if (bluetoothReceiver != null) {
            try {
                unregisterReceiver(bluetoothReceiver);
            } catch (Exception e) {
                //its probably not registered..
            }
        }

       /* if (cheService != null) {
            unbindService(serviceConnection);
        }*/

    }

    public void onDestroy() {
        super.onDestroy();

        //service = null;
        locationUpdates = null;
        unbindService(serviceConnection);

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("latitude", String.valueOf(currentLatLng.latitude));
        editor.putString("longitude", String.valueOf(currentLatLng.longitude));
        editor.putFloat("zoom", mMap.getCameraPosition().zoom);
        editor.putFloat("bearing", mMap.getCameraPosition().bearing);
        editor.putFloat("tilt", mMap.getCameraPosition().tilt);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);

        editor.commit();

        if (bluetoothReceiver != null) {
            try {
                unregisterReceiver(bluetoothReceiver);
            } catch (Exception e) {
                //probably no longer registerd..
            }
        }


        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }


    }


    public class DemoLocationListener implements LocationListener {

        /*
        this really needs to be moved to the service not the app......to review and test nearer time.  use away cambridge as test
         */

        private LocationManager locationManager;

        public DemoLocationListener(LocationManager locationManager) {
            this.locationManager = locationManager;
        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            //  callBack.setLocationUpdated(location);

            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (markerMap.containsKey("Me")) {
                markerMap.get("Me").remove();
                markerMap.put("Me", mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Me")));
            }


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)
                    .tilt(mMap.getCameraPosition().tilt)
                    .bearing(mMap.getCameraPosition().bearing)
                    .zoom(mMap.getCameraPosition().zoom)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //we need to execute the method we are interested in.  note if we time out then
            locationManager.removeUpdates(this);


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

    private DemoLocationListener demoLocationListener;


}

