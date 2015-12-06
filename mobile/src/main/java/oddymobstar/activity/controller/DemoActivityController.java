package oddymobstar.activity.controller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import oddymobstar.activity.DemoActivity;
import oddymobstar.activity.handler.ActivityResultHandler;
import oddymobstar.activity.handler.ConfigurationHandler;
import oddymobstar.activity.handler.DeviceDiscoveryHandler;
import oddymobstar.activity.handler.MapHandler;
import oddymobstar.activity.handler.MaterialsHandler;
import oddymobstar.activity.handler.MessageHandler;
import oddymobstar.activity.handler.OptionsItemSelectedHandler;
import oddymobstar.activity.handler.SharedPreferencesHandler;
import oddymobstar.activity.handler.ViewHandler;
import oddymobstar.activity.helper.LocationHelper;
import oddymobstar.activity.helper.MapHelper;
import oddymobstar.activity.helper.MaterialsHelper;
import oddymobstar.activity.listener.LocationListener;
import oddymobstar.activity.listener.MaterialsListener;
import oddymobstar.activity.listener.ViewListener;
import oddymobstar.connect.ConnectivityHandler;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.fragment.ChatFragment;
import oddymobstar.fragment.ConfigurationFragment;
import oddymobstar.fragment.DeviceFragment;
import oddymobstar.fragment.GridFragment;
import oddymobstar.fragment.GridViewFragment;
import oddymobstar.service.handler.CheService;
import oddymobstar.util.Configuration;
import oddymobstar.util.UUIDGenerator;
import oddymobstar.util.widget.GridDialog;

/**
 * Created by timmytime on 06/12/15.
 */
public class DemoActivityController {

    //statics etc
    public static final String BLUETOOTH_UUID = "39159dac-ead1-47ad-9975-ec8390df6f7d";
    public static final String MESSAGE_INTENT = "MESSAGE_INTENT";
    //utils
    public UUIDGenerator uuidGenerator;
    public Intent intent;
    public Intent serviceIntent;
    public Configuration configuration;
    public DBHelper dbHelper;
    public CheService cheService;
    public ServiceConnection serviceConnection;
    //helpers
    public MaterialsHelper materialsHelper;
    public MapHelper mapHelper;
    public LocationHelper locationHelper;
    //handlers
    public MessageHandler messageHandler;
    public ConnectivityHandler connectivityHandler;
    public MaterialsHandler materialsHandler;
    public MapHandler mapHandler;
    public ConfigurationHandler configurationHandler;
    public ViewHandler viewHandler;
    public ActivityResultHandler activityResultHandler;
    public OptionsItemSelectedHandler optionsItemSelectedHandler;
    public DeviceDiscoveryHandler deviceDiscoveryHandler;
    //listeners
    public MaterialsListener materialsListener;
    public LocationListener locationListener;
    public ViewListener viewListener;
    //receivers
    public BroadcastReceiver bluetoothReceiver;
    public BroadcastReceiver messageReceiver;
    //managers
    public LocationManager locationManager;
    //fragments
    public ChatFragment chatFrag = new ChatFragment();
    public GridFragment gridFrag = new GridFragment();
    public DeviceFragment deviceFragment = new DeviceFragment();
    public ConfigurationFragment confFrag = new ConfigurationFragment();
    public GridViewFragment gridViewFragment = new GridViewFragment();
    public GridDialog gridDialog;
    //core
    private DemoActivity main;



    //this will manage the initialisation of the main objects, and can be used to pass these objects to sub routines
    public DemoActivityController(DemoActivity main) {
        this.main = main;
    }

    public void onCreate() {
        dbHelper = new DBHelper(main);

        if (!dbHelper.hasPreLoad()) {
            dbHelper.addBaseConfiguration();
        }

        //really for testing...
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                Toast.makeText(main, message, Toast.LENGTH_SHORT).show();
            }
        };


        configuration = new Configuration(dbHelper.getConfigs());
        messageHandler = new MessageHandler(main, this);
        uuidGenerator = new UUIDGenerator(configuration.getConfig(Configuration.UUID_ALGORITHM).getValue());
        connectivityHandler = new ConnectivityHandler(main, BLUETOOTH_UUID);
        configurationHandler = new ConfigurationHandler(this);
        viewHandler = new ViewHandler(main, this);
        viewListener = new ViewListener(main, this);
        activityResultHandler = new ActivityResultHandler(main, this);
        optionsItemSelectedHandler = new OptionsItemSelectedHandler(main, this);
        deviceDiscoveryHandler = new DeviceDiscoveryHandler(main, this);

        dbHelper.setMessageHandler(messageHandler);

        materialsHelper = new MaterialsHelper(main);
        materialsHandler = new MaterialsHandler(main, this);
        materialsListener = new MaterialsListener(main, this);


        materialsHelper.setUpMaterials(
                materialsListener.getFABListener(),
                materialsListener.getImageListener());

        materialsHelper.userImage = dbHelper.getUserImage(configuration.getConfig(Configuration.PLAYER_KEY).getValue());
        materialsHandler.setNavConfigValues();

        locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
        mapHandler = new MapHandler(main, this);
        mapHelper = new MapHelper(main, this);
        locationHelper = new LocationHelper(this);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                cheService = ((CheService.CheServiceBinder) service).getCheServiceInstance();
                cheService.setMessageHandler(messageHandler);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                cheService = null;
            }
        };

        intent = new Intent(main, CheService.class);
        serviceIntent = new Intent(main, CheService.class);

        LocalBroadcastManager.getInstance(main).registerReceiver(messageReceiver, new IntentFilter(MESSAGE_INTENT));

        main.startService(serviceIntent);
        main.bindService(intent, serviceConnection, main.BIND_AUTO_CREATE);

        mapHelper.setUpMapIfNeeded();

        main.getSupportFragmentManager().beginTransaction().add(R.id.grid_view_fragment, gridViewFragment).addToBackStack(null).commit();


    }

    public void onPostCreate() {
        materialsHelper.navToggle.syncState();
    }

    public void onPause() {
        if (bluetoothReceiver != null) {
            try {
                main.unregisterReceiver(bluetoothReceiver);
            } catch (Exception e) {
                //its probably not registered..
            }
        }

        SharedPreferences sharedPreferences = main.getPreferences(Context.MODE_PRIVATE);
        SharedPreferencesHandler.handle(sharedPreferences, this);
    }

    public void onResume() {
        SharedPreferences sharedPreferences = main.getPreferences(Context.MODE_PRIVATE);

        if (locationListener.getCurrentLocation() == null) {
            locationListener.setCurrentLocation(new Location(sharedPreferences.getString(SharedPreferencesHandler.PROVIDER, "")));
        }

        locationListener.getCurrentLocation().setLatitude(Double.parseDouble(sharedPreferences.getString(SharedPreferencesHandler.LATITUTE, "0.0")));
        locationListener.getCurrentLocation().setLongitude(Double.parseDouble(sharedPreferences.getString(SharedPreferencesHandler.LONGITUDE, "0.0")));

        mapHelper.setUpMapIfNeeded();

        if (locationHelper.getLocationUpdates() == null) {
            mapHelper.initLocationUpdates();
        }
        //and we need to bind to it.
        if (cheService == null) {
            main.bindService(intent, serviceConnection, main.BIND_AUTO_CREATE);
        }
    }

    public void onDestroy() {
        //service = null;
        locationHelper.killLocationUpdates();

        main.unbindService(serviceConnection);

        SharedPreferences sharedPreferences = main.getPreferences(Context.MODE_PRIVATE);
        SharedPreferencesHandler.handle(sharedPreferences, this);

        LocalBroadcastManager.getInstance(main).unregisterReceiver(messageReceiver);


        if (bluetoothReceiver != null) {
            try {
                main.unregisterReceiver(bluetoothReceiver);
            } catch (Exception e) {
                //probably no longer registerd..
            }
        }


        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
    }

    public void onBackPressed() {
        removeFragments(true);
    }


    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        materialsHelper.navToggle.onConfigurationChanged(newConfig);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResultHandler.handleResult(requestCode, resultCode, data);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return optionsItemSelectedHandler.onOptionsItemSelected(item);
    }


    public void removeFragments(boolean backPressed) {

        android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();

        try {
            chatFrag.getHiddenChatPost().setVisibility(View.GONE);
        } catch (Exception e) {

        }


        try {
            transaction.remove(chatFrag);
        } catch (Exception e) {

        }


        try {
            gridFrag.getHiddenCreateView().setVisibility(View.GONE);
        } catch (Exception e) {

        }

        try {
            gridFrag.clearAdapter();
            transaction.remove(gridFrag);
        } catch (Exception e) {

        }


        try {
            transaction.remove(confFrag);
        } catch (Exception e) {

        }
        if (!backPressed) {
            try {
                transaction.remove(gridViewFragment);
            } catch (Exception e) {

            }
        } else {
            try {
                if (!gridViewFragment.isAdded()) {
                    transaction.replace(R.id.grid_view_fragment, gridViewFragment);
                }
            } catch (Exception e) {

            }
        }

        try {
            materialsHandler.handleFABChange(-1, R.drawable.ic_search_white_24dp, View.INVISIBLE);
            materialsHelper.navToolbar.setTitle(R.string.app_name);
            materialsHelper.navToolbar.setBackgroundColor(main.getResources().getColor(android.R.color.holo_red_dark));
        } catch (Exception e) {

        }

        transaction.commit();

    }

}
