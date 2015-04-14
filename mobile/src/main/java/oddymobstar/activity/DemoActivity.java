package oddymobstar.activity;


import android.app.ActivityManager;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Map;

import oddymobstar.core.Alliance;
import oddymobstar.core.Message;
import oddymobstar.core.Topic;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.fragment.ChatFragment;
import oddymobstar.fragment.ListFragment;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.message.out.OutTopicMessage;
import oddymobstar.service.CheService;
import oddymobstar.util.Configuration;
import oddymobstar.util.UUIDGenerator;

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

    private ChatFragment chatFrag = new ChatFragment();
    private ListFragment listFrag = new ListFragment();

    public class CreateHandler extends Handler{

        public void handleMessage(){
            if(listFrag != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listFrag.refreshAdapter();
                    }
                });

            }
        }

    }



    private class ListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> listView, View v, int position,
                                long id) {

            /*
            basically if they select an item we launch chat frag with an ID...
             */
            Cursor cursor = (Cursor)listFrag.getListAdapter().getItem(position);
            removeFragments();

            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


            switch(listFrag.getType()){
                case ListFragment.MY_TOPICS:

                    chatFrag.setCursor(dbHelper.getMessages(Message.TOPIC_MESSAGE, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TOPIC_KEY))));

                    transaction.replace(R.id.chat_fragment, chatFrag);
                    transaction.addToBackStack(null);
                    break;
                case ListFragment.MY_ALLIANCES:
                    chatFrag.setCursor(dbHelper.getMessages(Message.ALLIANCE_MESSAGE, cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALLIANCE_KEY))));

                    transaction.replace(R.id.chat_fragment, chatFrag);
                    transaction.addToBackStack(null);
                    break;
                case ListFragment.GLOBAL_TOPICS:
                    //we need to launch something to offer a register.  like the fucking dialogs i just ditched!
                    //well maybe a global / utm / sub utm selector fragment would be better...
                    break;
            }

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
/*

    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> listView, View v, int position, long id){
             //need to launch dialog for item selected, need to know if its global or alliance

        }
    };

    private DialogInterface.OnClickListener ll = new DialogInterface.OnClickListener(){

        public void onClick(DialogInterface dialog, int value) {
            show
        }

    };

*/

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

        font = Typeface.createFromAsset(
                this.getAssets(), "fontawesome-webfont.ttf");

        //get messages in.
        configuration = new Configuration(dbHelper.getConfigs());
        uuidGenerator = new UUIDGenerator(configuration.getConfig(Configuration.UUID_ALGORITHM).getValue());


        intent = new Intent(this, CheService.class);
        serviceIntent = new Intent(this, CheService.class);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(MESSAGE_INTENT));

        startService(serviceIntent);


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                cheService = ((CheService.CheServiceBinder) service).getCheServiceInstance();
                cheService.setCreateHandler(new CreateHandler());
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

        transaction.commit();
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


                chatFrag.setCursor(dbHelper.getConfigs());


                transaction.replace(R.id.chat_fragment, chatFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                break;


            case R.id.globalTopics:
                /*
                 we present user with a selectable filter that lists from server (most popular topics)
                 and then a search filter to query server for more topics
                 */
                try {
                    Topic topic = new Topic();
                    OutTopicMessage topicMessage = new OutTopicMessage(currentLatLng, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                    topicMessage.setTopic(topic, OutTopicMessage.GLOBAL, OutTopicMessage.TOPIC_GET, "");
                    //this needs lots of thought but for present we can live with it.....its defo not ideal.
                    //in fact its plain wrong.  we really dont want to do it like this.....traffic will be too high.
                    //to review.
                    cheService.writeToSocket(topicMessage);

                } catch (JSONException jse) {
                    Log.d("json", "json " + jse.toString());
                } catch (NoSuchAlgorithmException nsae) {
                    Log.d("security", "security " + nsae.toString());
                }

                listFrag.init(ListFragment.GLOBAL_TOPICS, listClickListener);
                transaction.replace(R.id.chat_fragment, listFrag);
                transaction.addToBackStack(null);
                transaction.commit();


                break;
            case R.id.alliances:

                listFrag.init(ListFragment.MY_ALLIANCES, listClickListener);
                transaction.replace(R.id.chat_fragment, listFrag);
                transaction.addToBackStack(null);
                transaction.commit();


                break;
            case R.id.subscribedTopics:


                listFrag.init(ListFragment.MY_TOPICS, listClickListener);
                transaction.replace(R.id.chat_fragment, listFrag);
                transaction.addToBackStack(null);
                transaction.commit();


                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

     /*   if(service == null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startService(serviceIntent);
                }
            });

            service.start();
        } */

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

    public void sendPost(View view) {

        /*
          now focus on this.  we need to send the chat message to service....
         */
        ///what is our chat type?  we find out we instantiate the message then it should run....
        //probably need to spend time making service write messages to db

        cheService.writeToSocket(null);

        Toast.makeText(this, chatFrag.getPost(), Toast.LENGTH_SHORT).show();
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
            case ListFragment.MY_TOPICS:

                if (!createText.trim().isEmpty()) {
                    Topic topic = new Topic();
                    topic.setName(createText);
                    try {
                        OutTopicMessage topicMessage = new OutTopicMessage(currentLatLng, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        topicMessage.setTopic(topic, OutTopicMessage.CREATE, OutTopicMessage.GLOBAL, "");
                        //we now need to send it to server
                        cheService.writeToSocket(topicMessage);

                    } catch (JSONException jse) {
                        Log.d("json", "json " + jse.toString());
                    } catch (NoSuchAlgorithmException nsae) {
                        Log.d("security", "security " + nsae.toString());
                    }
                }

                break;
        }

        listFrag.clear();



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

            //bind again if its down.  see if this imrpves things.  long term can send a message back to it rather than this.
        /*    if (cheService == null) {
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            }

            //lets send our location
            if (cheService != null) {
                try {
                    CoreMessage coreMessage = new CoreMessage(currentLatLng, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey(), CoreMessage.PLAYER);

                    cheService.writeToSocket(coreMessage);
                } catch (JSONException jse) {

                } catch (NoSuchAlgorithmException nsae) {

                }
            } */

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

