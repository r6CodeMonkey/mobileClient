package oddymobstar.activity;


import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import oddymobstar.activity.handler.MaterialsHandler;
import oddymobstar.activity.helper.MapHelper;
import oddymobstar.activity.helper.MaterialsHelper;
import oddymobstar.activity.listener.MaterialsListener;
import oddymobstar.connect.ConnectivityHandler;
import oddymobstar.connect.bluetooth.handler.Bluetooth;
import oddymobstar.connect.bluetooth.manager.BluetoothManager;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.fragment.ChatFragment;
import oddymobstar.fragment.ConfigurationFragment;
import oddymobstar.fragment.DeviceFragment;
import oddymobstar.fragment.GridFragment;
import oddymobstar.fragment.GridViewFragment;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.message.out.OutImageMessage;
import oddymobstar.model.Alliance;
import oddymobstar.model.AllianceMember;
import oddymobstar.model.Config;
import oddymobstar.model.Message;
import oddymobstar.model.UserImage;
import oddymobstar.service.handler.CheService;
import oddymobstar.util.Configuration;
import oddymobstar.util.SubUTM;
import oddymobstar.util.UTM;
import oddymobstar.util.UTMGridCreator;
import oddymobstar.util.UUIDGenerator;
import oddymobstar.util.widget.ConnectivityDialog;
import oddymobstar.util.widget.GridDialog;

public class DemoActivity extends AppCompatActivity {


    private static String SELECTED_GRID = "";


    public static final Long TWO_MINUTES = 120000l;
    public static final String MESSAGE_INTENT = "MESSAGE_INTENT";
    private static final String BLUETOOTH_UUID = "39159dac-ead1-47ad-9975-ec8390df6f7d";
    private static Typeface font = null;
    private static float UTM_REGION_ZOOM = 3;
    private static float UTM_ZOOM = 5;
    private static float SUB_UTM_ZOOM = 12;


    private Configuration configuration;

    private UUIDGenerator uuidGenerator;
    private CheService cheService;
    private ServiceConnection serviceConnection;
    private Intent intent;
    private Intent serviceIntent;
    private View actionBar;
    private boolean isClient = false;
    private BroadcastReceiver bluetoothReceiver;
    private Map<String, Polygon> lastLocateUTMs = new HashMap<>();
    private Polygon lastLocateSubUTM;
    //map settings
    private ChatFragment chatFrag = new ChatFragment();
    private GridFragment gridFrag = new GridFragment();
    private DeviceFragment deviceFragment = new DeviceFragment();
    private ConfigurationFragment confFrag = new ConfigurationFragment();
    private GridViewFragment gridViewFragment = new GridViewFragment();
    private GridDialog gridDialog;
    private ConnectivityHandler connectivityHandler;
    /*
      materials
     */

    private MaterialsHelper materialsHelper;
    private MaterialsHandler materialsHandler;
    private MaterialsListener materialsListener;


    public static final int UTM_FAB_STATE = 0;
    public static final int SUBUTM_FAB_STATE = 1;
    public static final int UTM_REGION_FAB_STATE = 2;

    /*
    result codes
     */


    private boolean CLEAR_GRIDS = false;


    public static int CURRENT_GRID_FAB_STATE = UTM_FAB_STATE;

    private PolygonOptions lastUTMOptions;

    public static String UTM_REGION = "";


    // private Thread service;


    //db helper can test this out.  and fix up the map to work.  is a start.
    //also need to set up base configs.
    private DBHelper dbHelper = new DBHelper(this);
    private ListClickListener listClickListener = new ListClickListener();
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            messageHandler(message);
            Log.d("message received is ", "message " + message);

        }
    };
    private MapHelper mapHelper;

    public static Typeface getFont() {
        return font;
    }

    private void messageHandler(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /*
     getters to our fragments for listeners
     */
    public ChatFragment getChatFrag() {
        return chatFrag;
    }

    ;

    public GridFragment getGridFrag() {
        return gridFrag;
    }

    public GridDialog getGridDialog() {
        return gridDialog;
    }

    public void createGridDialog() {
        gridDialog = GridDialog.newInstance(SELECTED_GRID, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                //the magic happens...but we cant deselect our selected item its not the pattern..
                dialog.dismiss();
                handleLocateDialog(gridDialog.getGrid(which));

            }
        }, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (!dbHelper.hasPreLoad()) {
            dbHelper.addBaseConfiguration();
        }
        // dbHelper.test();
        //we need one too...no shit.
        dbHelper.setMessageHandler(new MessageHandler());

        font = Typeface.createFromAsset(
                this.getAssets(), "fontawesome-webfont.ttf");

      materialsHelper = new MaterialsHelper(this);
        materialsHandler = new MaterialsHandler(this, materialsHelper);
      materialsListener = new MaterialsListener(this, materialsHandler);



        materialsHelper.setUpMaterials(
                materialsListener.getFABListener(),
                materialsListener.getImageListener());

        //useful makes it a bit easier to work with.
        UTM.createUTMRegions();
        SubUTM.createSubUtms();


        configuration = new Configuration(dbHelper.getConfigs());
        materialsHelper.userImage = dbHelper.getUserImage(configuration.getConfig(Configuration.PLAYER_KEY).getValue());
        materialsHelper.setNavConfigValues(configuration);

        mapHelper = new MapHelper(this, configuration, materialsHelper);


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

        mapHelper.setUpMapIfNeeded(dbHelper);

        getSupportFragmentManager().beginTransaction().add(R.id.grid_view_fragment, gridViewFragment).addToBackStack(null).commit();

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        materialsHelper.navToggle.syncState();
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        removeFragments(true);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case MaterialsListener.USER_IMAGE_RESULT_CODE:

                try {
                    // We need to recyle unused bitmaps

                    InputStream stream = getContentResolver().openInputStream(
                            data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 236, 354, false);
                    Log.d("bitmap size is", "size " + bitmap.getRowBytes() * bitmap.getHeight());
                    materialsHelper.userImageView.setImageBitmap(bitmap);
                    byte[] imageArray;
                    if (materialsHelper.userImage == null) {
                        materialsHelper.userImage = new UserImage();
                        materialsHelper.userImage.setUserImageKey(configuration.getConfig(Configuration.PLAYER_KEY).getValue());
                        materialsHelper.userImage.setUserImage(bitmap);
                        imageArray = dbHelper.addUserImage(materialsHelper.userImage);
                    } else {
                        materialsHelper.userImage.setUserImage(bitmap);
                        imageArray = dbHelper.updateUserImage(materialsHelper.userImage);
                    }

                    try {
                        final OutImageMessage outImageMessage = new OutImageMessage(mapHelper.getLocationListener().getCurrentLocation(), configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        outImageMessage.setImage(Base64.encodeToString(imageArray, Base64.DEFAULT));

                        new Thread((new Runnable() {
                            @Override
                            public void run() {
                                cheService.writeToSocket(outImageMessage);
                            }
                        })).start();


                    } catch (JSONException jse) {

                    } catch (NoSuchAlgorithmException nsae) {

                    }


                } catch (FileNotFoundException fe) {
                } catch (IOException e) {

                }

                return;

            case Bluetooth.REQUEST_ENABLE_BT:

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
         /*   case ConnectivityDialog.WIFI:
           //     connectivityHandler.getWifi().handle(requestCode, resultCode, data);
                break;
            case ConnectivityDialog.NFC:
                if(!isClient) {
                    OutAllianceMessage allianceMessage = null;
                    try {
                        allianceMessage = new OutAllianceMessage(currentLocation, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        allianceMessage.setAlliance(dbHelper.getAlliance(chatFrag.getKey()), OutCoreMessage.INVITE, OutCoreMessage.GLOBAL, "Invitation to Join");
                        connectivityHandler.getNfc().setMessage(allianceMessage.getMessage().toString().getBytes());
                    } catch (JSONException jse) {
                    } catch (NoSuchAlgorithmException nsae) {

                    }
                }
                break; */
                }

                return;
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (materialsHelper.navToggle.onOptionsItemSelected(item)) {
            return true;
        }

        removeFragments(false);


        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        configuration = new Configuration(dbHelper.getConfigs());


        //bind again if its down.
        if (cheService == null) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }


        switch (item.getItemId()) {

            case android.R.id.home:
                materialsHelper.navDrawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.settings:

                materialsHelper.navDrawer.closeDrawer(materialsHelper.navigationView);
                materialsHelper.navToolbar.setTitle(R.string.menu_settings);

                confFrag.init(new ConfigurationHandler(), dbHelper.getConfigs(Config.USER), dbHelper.getConfigs(Config.SYSTEM));


                transaction.replace(R.id.chat_fragment, confFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                break;


            case R.id.alliances:


                materialsHelper.navDrawer.closeDrawer(materialsHelper.navigationView);

                materialsHelper.floatingActionButton.setBackgroundTintList(materialsHelper.getColorStateList(MaterialsHelper.ALLIANCE_COLOR));
                materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
                materialsHelper.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_add_circle_white_24dp));

                MaterialsListener.FAB_MODE = MaterialsListener.ALLIANCE_FAB;
                materialsHelper.navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                materialsHelper.navToolbar.setTitle(R.string.menu_alliances);

                gridFrag.init(GridFragment.MY_ALLIANCES, listClickListener);
                transaction.replace(R.id.chat_fragment, gridFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                break;

            case R.id.bluetooth_receive:
                //same mechanism for discovery.
                allianceInvite(null, true);

                break;
            case R.id.bluetooth_send:
                //same mechanism for discovery.
                allianceInvite(null, false);

                break;

            case R.id.utm:
                CURRENT_GRID_FAB_STATE = UTM_FAB_STATE;
                if (!lastLocateUTMs.isEmpty()) {

                    for (final Polygon polygon : lastLocateUTMs.values()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                polygon.remove();
                            }
                        });
                    }
                    CLEAR_GRIDS = true;


                }

                materialsHelper.navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                materialsHelper.navToolbar.setTitle(R.string.menu_utm);
                materialsHelper.floatingActionButton.setBackgroundTintList(materialsHelper.getColorStateList(MaterialsHelper.UTM_COLOR));
                materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
                MaterialsListener.FAB_MODE = MaterialsListener.GRID_FAB;
                SELECTED_GRID = CURRENT_GRID_FAB_STATE == SUBUTM_FAB_STATE ?
                        configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue() :
                        CURRENT_GRID_FAB_STATE == UTM_FAB_STATE ? UTM.getUTMRegion(configuration.getConfig(Configuration.CURRENT_UTM).getValue()) : "";


                animateToGrid(mapHelper.getMyUTM(), UTM_ZOOM);
                materialsHelper.navDrawer.closeDrawer(materialsHelper.navigationView);

                try {
                    transaction.add(R.id.grid_view_fragment, gridViewFragment);
                } catch (Exception e) {

                }

                transaction.commit();
                break;

            case R.id.sub_utm:
                CURRENT_GRID_FAB_STATE = SUBUTM_FAB_STATE;
                if (lastLocateSubUTM != null) {
                    lastLocateSubUTM.remove();
                }
                lastLocateSubUTM = null;
                materialsHelper.navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                materialsHelper.navToolbar.setTitle(R.string.menu_subutm);
                materialsHelper.floatingActionButton.setBackgroundTintList(materialsHelper.getColorStateList(MaterialsHelper.SUB_UTM_COLOR));
                materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
                MaterialsListener.FAB_MODE = MaterialsListener.GRID_FAB;
                SELECTED_GRID = CURRENT_GRID_FAB_STATE == SUBUTM_FAB_STATE ?
                        configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue() :
                        CURRENT_GRID_FAB_STATE == UTM_FAB_STATE ? UTM.getUTMRegion(configuration.getConfig(Configuration.CURRENT_UTM).getValue()) : "";


                materialsHelper.navDrawer.closeDrawer(materialsHelper.navigationView);
                animateToGrid(mapHelper.getMySubUTM(), SUB_UTM_ZOOM);

                try {
                    transaction.replace(R.id.grid_view_fragment, gridViewFragment);
                } catch (Exception e) {

                }

                transaction.commit();

                break;

            case R.id.encrypt:
                break;

            case R.id.invite:
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        materialsHelper.navToggle.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onResume() {
        super.onResume();


        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        if (mapHelper.getLocationListener().getCurrentLocation() == null) {
            mapHelper.getLocationListener().setCurrentLocation(new Location(sharedPreferences.getString("provider", "")));
        }

        mapHelper.getLocationListener().getCurrentLocation().setLatitude(Double.parseDouble(sharedPreferences.getString("latitude", "0.0")));
        mapHelper.getLocationListener().getCurrentLocation().setLongitude(Double.parseDouble(sharedPreferences.getString("longitude", "0.0")));


        mapHelper.setUpMapIfNeeded(dbHelper);

        if (mapHelper.getLocationHelper().getLocationUpdates() == null) {
            mapHelper.initLocationUpdates();
        }
        //and we need to bind to it.
        if (cheService == null) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

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

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("latitude", String.valueOf(mapHelper.getLocationListener().getCurrentLocation().getLatitude()));
        editor.putString("longitude", String.valueOf(mapHelper.getLocationListener().getCurrentLocation().getLongitude()));
        editor.putFloat("zoom", mapHelper.getMap().getCameraPosition().zoom);
        editor.putFloat("bearing", mapHelper.getMap().getCameraPosition().bearing);
        editor.putFloat("tilt", mapHelper.getMap().getCameraPosition().tilt);
        editor.putString("provider", mapHelper.getLocationListener().getCurrentLocation().getProvider());


        editor.commit();


    }

    public void onDestroy() {
        super.onDestroy();

        //service = null;
        mapHelper.getLocationHelper().killLocationUpdates();

        unbindService(serviceConnection);

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("latitude", String.valueOf(mapHelper.getLocationListener().getCurrentLocation().getLatitude()));
        editor.putString("longitude", String.valueOf(mapHelper.getLocationListener().getCurrentLocation().getLongitude()));
        editor.putFloat("zoom", mapHelper.getMap().getCameraPosition().zoom);
        editor.putFloat("bearing", mapHelper.getMap().getCameraPosition().bearing);
        editor.putFloat("tilt", mapHelper.getMap().getCameraPosition().tilt);
        editor.putString("provider", mapHelper.getLocationListener().getCurrentLocation().getProvider());

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




    /*
     *  Helpers
      *
      * */


    private void animateToGrid(Polygon polygon, float zoom) {
        //make map zoom to the UTM and search function now allows UTM search
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target((CURRENT_GRID_FAB_STATE == UTM_FAB_STATE || CURRENT_GRID_FAB_STATE == UTM_REGION_FAB_STATE) ? UTMGridCreator.getCentreUTM(polygon.getPoints()) : UTMGridCreator.getCentreSubUTM(polygon.getPoints()))
                .tilt(mapHelper.tilt)
                .bearing(mapHelper.bearing)
                .zoom(zoom)
                .build();

        //so need the optimal zoom to display the utm...tilt is tilt, and lat /long is centre of the utm (to calc based on utm shit)
        //and bearing = bearing
        mapHelper.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


    private void handleLocateDialog(String grid) {

        if (CLEAR_GRIDS) {
            //try twice lol.
            for (final Polygon polygon : lastLocateUTMs.values()) {
                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
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
                    lastUTMOptions = UTMGridCreator.getUTMGrid(new UTM(utm)).strokeColor(getResources().getColor(android.R.color.holo_purple));
                    Polygon polygon = mapHelper.getMap().addPolygon(lastUTMOptions);

                    if (utm.equals(UTM.getRegionCentre(grid))) {
                        regionCentre = polygon;
                    }
                    lastLocateUTMs.put(utm, polygon);

                }
                //we actually need the central one for this...god damn it.
                animateToGrid(regionCentre, UTM_REGION_ZOOM);

            } else {
                lastUTMOptions = UTMGridCreator.getUTMGrid(new UTM(grid)).strokeColor(getResources().getColor(android.R.color.holo_purple));
                lastLocateUTMs.put(grid, mapHelper.getMap().addPolygon(lastUTMOptions));
                animateToGrid(lastLocateUTMs.get(grid), UTM_ZOOM);
            }

        } else if (CURRENT_GRID_FAB_STATE == UTM_REGION_FAB_STATE) {
            //  PolygonOptions subUtmOptions = UTMGridCreator.getUTMGrid(new UTM(grid)).strokeColor(getResources().getColor(android.R.color.holo_purple));
            //  lastLocateUTMs.add(map.addPolygon(subUtmOptions);
            animateToGrid(lastLocateUTMs.get(grid), UTM_ZOOM);

        } else {
            PolygonOptions subUtmOptions = UTMGridCreator.getSubUTMGrid(new SubUTM(grid), mapHelper.getUtmOptions()).strokeColor(getResources().getColor(android.R.color.holo_orange_dark));
            lastLocateSubUTM = mapHelper.getMap().addPolygon(subUtmOptions);
            animateToGrid(lastLocateSubUTM, SUB_UTM_ZOOM);
        }

        materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);

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


    private void removeFragments(boolean backPressed) {

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

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
            materialsHelper.floatingActionButton.setVisibility(View.INVISIBLE);
            materialsHelper.navToolbar.setTitle(R.string.app_name);
            materialsHelper.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_search_white_24dp));
            materialsHelper.navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } catch (Exception e) {

        }


        transaction.commit();


    }


    /*
     Button callbacks
     */


    public void deleteMessages(View view) {
        //grab the chat frag id...
        dbHelper.deleteMessages(chatFrag.getKey());
        removeFragments(false);
    }

    public void messageCoverage(View view) {
        /*
          need a dialog with GLOBAL / UTM / SUBUTM...
         */
    }


    private void showChat(String title) {


        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


        //change the
        materialsHelper.floatingActionButton.setBackgroundTintList(materialsHelper.getColorStateList(MaterialsHelper.CHAT_COLOR));
        materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
        materialsHelper.floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_chat_bubble_outline_white_24dp));
        materialsHelper.navToolbar.setTitle(title);

        MaterialsListener.FAB_MODE = MaterialsListener.CHAT_FAB;
        materialsHelper.navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        materialsHelper.navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));


        chatFrag.setCursor(dbHelper.getMessages(Message.ALLIANCE_MESSAGE, chatFrag.getKey()), chatFrag.getKey(), chatFrag.getTitle());
        transaction.replace(R.id.chat_fragment, chatFrag);
        transaction.addToBackStack(null);


        transaction.commit();

    }


    public void allianceInvite(View view, boolean isClient) {

        this.isClient = isClient;

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        ConnectivityDialog connectivityDialog = ConnectivityDialog.newInstance(connectivityHandler, isClient);

        connectivityDialog.show(transaction, "dialog");

    }

    public void allianceInvite(View view) {

        allianceInvite(view, false);
    }

    public void sendPost(View view) {


        /*
          now focus on this.  we need to send the chat message to service....
         */
        ///what is our chat type?  we find out we instantiate the message then it should run....
        //probably need to spend time making service write messages to db
        if (chatFrag.getHiddenChatPost().isPostValid()) {
            try {

                OutCoreMessage coreMessage = null;

                switch (gridFrag.getType()) {


                    case GridFragment.MY_ALLIANCES:

                        //create a message for the alliance....
                        coreMessage = new OutAllianceMessage(mapHelper.getLocationListener().getCurrentLocation(), configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        ((OutAllianceMessage) coreMessage).setAlliance(dbHelper.getAlliance(chatFrag.getKey()), OutCoreMessage.PUBLISH, OutCoreMessage.GLOBAL, chatFrag.getHiddenChatPost().getPost());
                        break;

                }

                //need to animate...but
                materialsHandler.handleChatFAB(chatFrag, false);


                cheService.writeToSocket(coreMessage);

            } catch (NoSuchAlgorithmException nse) {

            } catch (JSONException jse) {
            }

            cancelPost(null);
        } else {
            //find out if this every works! it was not cause of my bug
            removeFragments(false);
        }

    }

    public void cancelPost(View view) {
        //make it clear message
        chatFrag.getHiddenChatPost().cancelPost();
    }

    public void createButton(View view) {

        String createText = gridFrag.getHiddenCreateView().getCreateText();

        switch (gridFrag.getType()) {

            case GridFragment.MY_ALLIANCES:

                if (!createText.trim().isEmpty()) {

                    try {
                        //LatLng latLng, String uid, String ackId, String type
                        Alliance alliance = new Alliance();
                        alliance.setName(createText);


                        OutAllianceMessage allianceMessage = new OutAllianceMessage(mapHelper.getLocationListener().getCurrentLocation(), configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        allianceMessage.setAlliance(alliance, OutAllianceMessage.CREATE, OutAllianceMessage.GLOBAL, "");

                        cheService.writeToSocket(allianceMessage);


                        //need to animate...but
                        materialsHandler.handleAllianceFAB(gridFrag, false);


                    } catch (NoSuchAlgorithmException nsae) {
                        Log.d("security", "security " + nsae.toString());
                    } catch (JSONException jse) {
                        Log.d("json", "json " + jse.toString());
                    }
                }

                break;

        }

        gridFrag.getHiddenCreateView().clear();


    }




    /*
     MAP

     */


    /**
     * this is demo code.  we want to zoom in better and use last known location etc.  but for testing its fine
     * as i can see other code working
     */


    public class ConfigurationHandler extends Handler {

        public void handleClearBacklog() {
            //calls the service to handle.  add service method
            cheService.clearBacklog();

        }


        public void handleResetConnection() {
            cheService.resetConnection();
        }

        public void handleGPSInterval(int progress) {
            //
            Config config = configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL);
            config.setValue(String.valueOf(progress * 1000));

            dbHelper.updateConfig(config);

            configuration = new Configuration(dbHelper.getConfigs());

            if (mapHelper.getLocationHelper().getLocationUpdates().isAlive()) {
                mapHelper.getLocationHelper().getLocationUpdates().interrupt();
                mapHelper.getLocationHelper().killLocationUpdates();
            }

            mapHelper.initLocationUpdates();
            cheService.resetLocationUpdates();
        }

        public void handleHideUser(boolean hide) {
            //updates config.
            Config config = configuration.getConfig(Configuration.SERVER_LOCATION_HIDE);
            config.setValue(hide ? "Y" : "N");

            dbHelper.updateConfig(config);

            configuration = new Configuration(dbHelper.getConfigs());
        }

    }

    public class MessageHandler extends Handler {


        public void handleList() {
            if (gridFrag != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridFrag.refreshAdapter();
                    }
                });

            }
        }


        public void handleUTMChange(String utm) {

            materialsHelper.setNavConfigValues(configuration);


            final PolygonOptions options = UTMGridCreator.getUTMGrid(new UTM(utm)).strokeColor(getResources().getColor(android.R.color.holo_purple));
            runOnUiThread(new Runnable() {


                @Override
                public void run() {

                    if (mapHelper.getMyUTM() != null) {
                        mapHelper.getMyUTM().remove();
                    }

                    mapHelper.setMyUTM(mapHelper.getMap().addPolygon(options));

                }
            });
        }

        public void handleSubUTMChange(String subUtm) {

            materialsHelper.setNavConfigValues(configuration);

            configuration = new Configuration(dbHelper.getConfigs());

            //timing can cause this to fail...its no biggy its not likely required in end model.
            UTM utm = null;
            SubUTM subUTM = null;

            try {
                utm = new UTM(configuration.getConfig(Configuration.CURRENT_UTM).getValue());
                //seem to get problems with this for some reason...ie integer = "".  could be data has not updated etc.
                subUTM = new SubUTM(subUtm);
            } catch (Exception e) {
                Log.d("error on utm", "error " + e.getMessage());
            }

            if (utm != null && subUTM != null) {
                PolygonOptions utmOption = UTMGridCreator.getUTMGrid(utm);
                final PolygonOptions options = UTMGridCreator.getSubUTMGrid(subUTM, utmOption).strokeColor(getResources().getColor(android.R.color.holo_orange_dark));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mapHelper.getMySubUTM() != null) {
                            mapHelper.getMySubUTM().remove();
                        }
                        mapHelper.setMySubUTM(mapHelper.getMap().addPolygon(options));
                    }
                });
            }

        }

        public void handleChat(final String type) {
            if (chatFrag != null && chatFrag.isVisible()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatFrag.refreshAdapter(dbHelper.getMessages(type, chatFrag.getKey()));
                    }
                });
            }

        }

        public void handleInvite(final String key, final String title) {

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

        public void handleAllianceMember(final AllianceMember allianceMember, final boolean zoomTo) {


            Log.d("adding marker", "marker " + allianceMember.getKey() + " lat long is " + allianceMember.getLatLng().toString());


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mapHelper.getMarkerMap().containsKey(allianceMember.getKey())) {
                        mapHelper.getMarkerMap().get(allianceMember.getKey()).remove();
                        Log.d("adding marker", "removing marker ");
                    }

                    Marker marker = mapHelper.getMap().addMarker(new MarkerOptions().position(allianceMember.getLatLng()).title(allianceMember.getKey()));

                    if (zoomTo) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(allianceMember.getLatLng())
                                .tilt(mapHelper.getMap().getCameraPosition().tilt)
                                .bearing(mapHelper.getMap().getCameraPosition().bearing)
                                .zoom(mapHelper.getMap().getCameraPosition().zoom)
                                .build();

                        mapHelper.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    if (marker != null) {
                        mapHelper.getMarkerMap().put(allianceMember.getKey(), marker);
                    }

                }

            });


        }
    }



    /*
     Inner helper classes for callbacks

     */


    public class DeviceDiscovery {

        private BluetoothManager bluetoothManager;


        public DeviceDiscovery(Context context) {

            bluetoothManager = new BluetoothManager(context, connectivityHandler, dbHelper, uuidGenerator, cheService, configuration, chatFrag.getKey(), mapHelper.getLocationListener().getCurrentLocation());

        }

        public void addDevice(BluetoothDevice device) {

            Log.d("adding device", "adding a device");
            if (bluetoothManager.addDevice(device)) {
                deviceFragment.refreshAdapter(device.getName());
            }
        }

        public BluetoothManager getBluetoothManager() {
            return bluetoothManager;
        }


        public void onDiscover() {
            bluetoothManager.setIsRunning(true);
            //we launch..
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            deviceFragment = bluetoothManager.onDiscover(isClient);

            deviceFragment.show(transaction, "dialog");

        }


    }


    private class ListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> listView, View v, int position,
                                long id) {

            /*
            basically if they select an item we launch chat frag with an ID...
             */
            Cursor cursor = (Cursor) gridFrag.getListAdapter().getItem(position);
            removeFragments(false);

            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            String key = "";
            switch (gridFrag.getType()) {
                case GridFragment.MY_ALLIANCES:
                    key = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALLIANCE_KEY));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALLIANCE_NAME));
                    chatFrag.setCursor(dbHelper.getMessages(Message.ALLIANCE_MESSAGE, key), key, title);

                    //and show
                    showChat(title);

                    break;

            }

            //     getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            //     getActionBar().setCustomView(actionBar);
            //     getActionBar().show();

            transaction.commit();


        }
    }


}

