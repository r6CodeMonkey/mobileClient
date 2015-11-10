package oddymobstar.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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
import oddymobstar.util.RoundedImageView;
import oddymobstar.util.SubUTM;
import oddymobstar.util.UTM;
import oddymobstar.util.UTMGridCreator;
import oddymobstar.util.UUIDGenerator;
import oddymobstar.util.widget.ChatPost;
import oddymobstar.util.widget.ConnectivityDialog;
import oddymobstar.util.widget.CreateView;
import oddymobstar.util.widget.GridDialog;

public class DemoActivity extends AppCompatActivity {

    public static final Long TWO_MINUTES = 120000l;
    public static final String MESSAGE_INTENT = "MESSAGE_INTENT";
    private static final String BLUETOOTH_UUID = "39159dac-ead1-47ad-9975-ec8390df6f7d";
    private static final int ALLIANCE_FAB = 0;
    private static final int CHAT_FAB = 1;
    private static final int GRID_FAB = 2;
    private static Typeface font = null;
    private static float UTM_REGION_ZOOM = 3;
    private static float UTM_ZOOM = 5;
    private static float SUB_UTM_ZOOM = 12;
    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private android.os.Handler handler = new android.os.Handler();
    private Configuration configuration;
    private UserImage userImage;
    private UUIDGenerator uuidGenerator;
    private CheService cheService;
    private ServiceConnection serviceConnection;
    private Intent intent;
    private Intent serviceIntent;
    private View actionBar;
    private boolean isClient = false;
    private BroadcastReceiver bluetoothReceiver;
    private PolygonOptions utmOptions;
    private PolygonOptions lastUTMOptions;
    private Polygon myUTM;
    private Polygon mySubUTM;
    private Map<String, Polygon> lastLocateUTMs = new HashMap<>();
    private Polygon lastLocateSubUTM;
    //map settings
    private float bearing, tilt, zoom = 0.0f;
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
    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle navToggle;
    private NavigationView navigationView;
    private Toolbar navToolbar;
    private Toolbar hiddenToolbar;
    private FloatingActionButton floatingActionButton;
    private RoundedImageView userImageView;
    private ColorStateList subUtmColorList;
    private ColorStateList utmColorList;
    private ColorStateList allianceColorList;
    private ColorStateList chatColorList;
    private int fabMode = ALLIANCE_FAB;

    public static final int UTM_FAB_STATE = 0;
    public static final int SUBUTM_FAB_STATE = 1;
    public static final int UTM_REGION_FAB_STATE = 2;

    /*
    result codes
     */
    private static final int USER_IMAGE_RESULT_CODE = 1001;

    private boolean CLEAR_GRIDS = false;


    public static int CURRENT_GRID_FAB_STATE = UTM_FAB_STATE;

    public static String UTM_REGION = "";


    private Thread locationUpdates;
    // private Thread service;

    private Location currentLocation;

    private Map<String, Marker> markerMap = new HashMap<>();

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
    private DemoLocationListener demoLocationListener;

    public static Typeface getFont() {
        return font;
    }

    private void messageHandler(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //boring shit
    private void setUpColorLists() {
        subUtmColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        getResources().getColor(android.R.color.holo_orange_dark), //1
                        getResources().getColor(android.R.color.holo_orange_dark), //2
                        getResources().getColor(android.R.color.holo_orange_dark) //3
                });

        utmColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        getResources().getColor(android.R.color.holo_purple), //1
                        getResources().getColor(android.R.color.holo_purple), //2
                        getResources().getColor(android.R.color.holo_purple) //3
                }
        );

        allianceColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        getResources().getColor(android.R.color.holo_red_dark), //1
                        getResources().getColor(android.R.color.holo_red_dark), //2
                        getResources().getColor(android.R.color.holo_red_dark) //3
                }
        );

        chatColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        getResources().getColor(android.R.color.holo_green_dark), //1
                        getResources().getColor(android.R.color.holo_green_dark), //2
                        getResources().getColor(android.R.color.holo_green_dark) //3
                }
        );

    }


    private void setUpMaterials() {
        navDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navDrawer.setElevation(16.0f);

        navToolbar = (Toolbar) findViewById(R.id.toolbar);


        navToggle = new ActionBarDrawerToggle(
                this,
                navDrawer,
                navToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };


        navDrawer.setDrawerListener(navToggle);


        //  toolbar.setLogo(R.drawable.ic_drawer);
        setSupportActionBar(navToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(12.0f);


        navigationView = (NavigationView) findViewById(R.id.left_drawer);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                onOptionsItemSelected(menuItem);
                return true;
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_search_white_24dp));

        floatingActionButton.setBackgroundTintList(subUtmColorList);
        floatingActionButton.setVisibility(View.INVISIBLE);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (fabMode) {
                    case ALLIANCE_FAB:
                        handleAllianceFAB(true);
                        break;
                    case CHAT_FAB:
                        handleChatFAB(true);
                        break;
                    case GRID_FAB:
                        handleSearchFab(CURRENT_GRID_FAB_STATE == SUBUTM_FAB_STATE ?
                                configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue() :
                                CURRENT_GRID_FAB_STATE == UTM_FAB_STATE ? UTM.getUTMRegion(configuration.getConfig(Configuration.CURRENT_UTM).getValue()) : "");
                        break;
                }

            }
        });

        hiddenToolbar = (Toolbar) findViewById(R.id.hidden_toolbar);
        hiddenToolbar.setVisibility(View.INVISIBLE);

        hiddenToolbar.setNavigationIcon(getDrawable(R.drawable.ic_search_white_24dp));

        userImageView = (RoundedImageView) navigationView.findViewById(R.id.user_image);
        userImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //we need to launch to access gallery store
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, USER_IMAGE_RESULT_CODE);

                return false;
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

        setUpColorLists();
        setUpMaterials();

        //useful makes it a bit easier to work with.
        UTM.createUTMRegions();
        SubUTM.createSubUtms();


        configuration = new Configuration(dbHelper.getConfigs());
        userImage = dbHelper.getUserImage(configuration.getConfig(Configuration.PLAYER_KEY).getValue());
        setNavConfigValues();


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

        setUpMapIfNeeded();

        getSupportFragmentManager().beginTransaction().add(R.id.grid_view_fragment, gridViewFragment).addToBackStack(null).commit();

    }

    private void handleChatFAB(final boolean hide) {

        final ChatPost hiddenChatPost = chatFrag.getHiddenChatPost();

        int cxIn = (hiddenChatPost.getLeft() + hiddenChatPost.getRight()) / 2;
        int cyIn = (hiddenChatPost.getTop() + hiddenChatPost.getBottom()) / 2;

        int radiusIn = Math.max(hiddenChatPost.getWidth(), hiddenChatPost.getHeight());

        int cxOut = (floatingActionButton.getLeft() + floatingActionButton.getRight()) / 2;
        int cyOut = (floatingActionButton.getTop() + floatingActionButton.getBottom()) / 2;

        int radiusOut = floatingActionButton.getWidth();

        Animator animatorIn, animatorOut = null;

        if (hide) {
            animatorIn = ViewAnimationUtils.createCircularReveal(hiddenChatPost, cxIn, cyIn, 0, radiusIn);
        } else {
            animatorIn = ViewAnimationUtils.createCircularReveal(floatingActionButton, cxOut, cyOut, 0, radiusOut);
        }
        //   animatorIn.setDuration(500);
        animatorIn.setInterpolator(new AccelerateInterpolator());

        if (hide) {
            animatorOut = ViewAnimationUtils.createCircularReveal(floatingActionButton, cxOut, cyOut, radiusOut, 0);
        } else {
            animatorOut = ViewAnimationUtils.createCircularReveal(hiddenChatPost, cxIn, cyIn, radiusIn, 0);
        }
        //   animatorOut.setDuration(300);
        animatorOut.setInterpolator(new AccelerateInterpolator());

        animatorOut.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (hide) {
                    floatingActionButton.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    hiddenChatPost.requestFocus();

                } else {
                    hiddenChatPost.setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(hiddenChatPost.getWindowToken(), 0);


                }

            }
        });

        if (hide) {
            hiddenChatPost.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);
        }


        animatorOut.start();
        animatorIn.start();

    }

    private void handleAllianceFAB(final boolean hide) {

        final CreateView hiddenCreateView = gridFrag.getHiddenCreateView();


        int cxIn = (hiddenCreateView.getLeft() + hiddenCreateView.getRight()) / 2;
        int cyIn = (hiddenCreateView.getTop() + hiddenCreateView.getBottom()) / 2;

        int radiusIn = Math.max(hiddenCreateView.getWidth(), hiddenCreateView.getHeight());

        int cxOut = (floatingActionButton.getLeft() + floatingActionButton.getRight()) / 2;
        int cyOut = (floatingActionButton.getTop() + floatingActionButton.getBottom()) / 2;

        int radiusOut = floatingActionButton.getWidth();

        Animator animatorIn, animatorOut = null;

        if (hide) {
            animatorIn = ViewAnimationUtils.createCircularReveal(hiddenCreateView, cxIn, cyIn, 0, radiusIn);
        } else {
            animatorIn = ViewAnimationUtils.createCircularReveal(floatingActionButton, cxOut, cyOut, 0, radiusOut);
        }

        //   animatorIn.setDuration(500);
        animatorIn.setInterpolator(new AccelerateInterpolator());

        if (hide) {
            animatorOut = ViewAnimationUtils.createCircularReveal(floatingActionButton, cxOut, cyOut, radiusOut, 0);
        } else {
            animatorOut = ViewAnimationUtils.createCircularReveal(hiddenCreateView, cxIn, cyIn, radiusIn, 0);
        }

        // animatorOut.setDuration(300);
        animatorOut.setInterpolator(new AccelerateInterpolator());

        animatorOut.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);


                if (hide) {
                    floatingActionButton.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    hiddenCreateView.requestFocus();

                } else {
                    hiddenCreateView.setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(hiddenCreateView.getWindowToken(), 0);


                }


            }
        });

        if (hide) {
            hiddenCreateView.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);
        }


        animatorOut.start();
        animatorIn.start();


    }

    //this is useful...but not for this.  we want a dialog with the codes. to launch.  give up now.
    private void handleSearchFab(final String selectedGrid) {


        int cxOut = (floatingActionButton.getLeft() + floatingActionButton.getRight()) / 2;
        int cyOut = (floatingActionButton.getTop() + floatingActionButton.getBottom()) / 2;

        int radiusOut = floatingActionButton.getWidth();


        Animator animatorOut = ViewAnimationUtils.createCircularReveal(floatingActionButton, cxOut, cyOut, radiusOut, 0);

        //animatorOut.setDuration(300);
        animatorOut.setInterpolator(new AccelerateInterpolator());

        animatorOut.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                floatingActionButton.setVisibility(View.INVISIBLE);

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();

                gridDialog = GridDialog.newInstance(selectedGrid, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        //the magic happens...but we cant deselect our selected item its not the pattern..
                        dialog.dismiss();
                        handleLocateDialog(gridDialog.getGrid(which));

                    }
                }, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }
                });

                gridDialog.show(transaction, "dialog");


            }
        });

        animatorOut.start();


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
                    Polygon polygon = map.addPolygon(lastUTMOptions);

                    if (utm.equals(UTM.getRegionCentre(grid))) {
                        regionCentre = polygon;
                    }
                    lastLocateUTMs.put(utm, polygon);

                }
                //we actually need the central one for this...god damn it.
                animateToGrid(regionCentre, UTM_REGION_ZOOM);

            } else {
                lastUTMOptions = UTMGridCreator.getUTMGrid(new UTM(grid)).strokeColor(getResources().getColor(android.R.color.holo_purple));
                lastLocateUTMs.put(grid, map.addPolygon(lastUTMOptions));
                animateToGrid(lastLocateUTMs.get(grid), UTM_ZOOM);
            }

        } else if (CURRENT_GRID_FAB_STATE == UTM_REGION_FAB_STATE) {
            //  PolygonOptions subUtmOptions = UTMGridCreator.getUTMGrid(new UTM(grid)).strokeColor(getResources().getColor(android.R.color.holo_purple));
            //  lastLocateUTMs.add(map.addPolygon(subUtmOptions);
            animateToGrid(lastLocateUTMs.get(grid), UTM_ZOOM);

        } else {
            PolygonOptions subUtmOptions = UTMGridCreator.getSubUTMGrid(new SubUTM(grid), utmOptions).strokeColor(getResources().getColor(android.R.color.holo_orange_dark));
            lastLocateSubUTM = map.addPolygon(subUtmOptions);
            animateToGrid(lastLocateSubUTM, SUB_UTM_ZOOM);
        }

        floatingActionButton.setVisibility(View.VISIBLE);

    }

    private void setNavConfigValues() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MenuItem item = navigationView.getMenu().findItem(R.id.utm);
                item.setTitle(getResources().getString(R.string.menu_utm) + " - " + configuration.getConfig(Configuration.CURRENT_UTM).getValue());

                item = navigationView.getMenu().findItem(R.id.sub_utm);
                item.setTitle(getResources().getString(R.string.menu_subutm) + " - " + configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue());

                item = navigationView.getMenu().findItem(R.id.encrypt);
                item.setTitle(getResources().getString(R.string.menu_encryption) + " - " + configuration.getConfig(Configuration.SSL_ALGORITHM).getValue());

                TextView textView = (TextView) navigationView.findViewById(R.id.nav_header);
                textView.setText(configuration.getConfig(Configuration.PLAYER_KEY).getValue());

                if (userImage != null) {
                    if (userImage.getUserImage() != null) {
                        userImageView.setImageBitmap(userImage.getUserImage());
                    }
                }

                gridFrag.refreshAdapter();
            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        navToggle.syncState();
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
    public void onBackPressed() {

        super.onBackPressed();
        removeFragments(true);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case USER_IMAGE_RESULT_CODE:

                try {
                    // We need to recyle unused bitmaps

                    InputStream stream = getContentResolver().openInputStream(
                            data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 236, 354, false);
                    Log.d("bitmap size is", "size " + bitmap.getRowBytes() * bitmap.getHeight());
                    userImageView.setImageBitmap(bitmap);
                    byte[] imageArray;
                    if (userImage == null) {
                        userImage = new UserImage();
                        userImage.setUserImageKey(configuration.getConfig(Configuration.PLAYER_KEY).getValue());
                        userImage.setUserImage(bitmap);
                        imageArray = dbHelper.addUserImage(userImage);
                    } else {
                        userImage.setUserImage(bitmap);
                        imageArray = dbHelper.updateUserImage(userImage);
                    }

                    try {
                        final OutImageMessage outImageMessage = new OutImageMessage(currentLocation, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
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
            floatingActionButton.setVisibility(View.INVISIBLE);
            navToolbar.setTitle(R.string.app_name);
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_search_white_24dp));
            navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } catch (Exception e) {

        }


        transaction.commit();


    }

    private void animateToGrid(Polygon polygon, float zoom) {
        //make map zoom to the UTM and search function now allows UTM search
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target((CURRENT_GRID_FAB_STATE == UTM_FAB_STATE || CURRENT_GRID_FAB_STATE == UTM_REGION_FAB_STATE) ? UTMGridCreator.getCentreUTM(polygon.getPoints()) : UTMGridCreator.getCentreSubUTM(polygon.getPoints()))
                .tilt(tilt)
                .bearing(bearing)
                .zoom(zoom)
                .build();

        //so need the optimal zoom to display the utm...tilt is tilt, and lat /long is centre of the utm (to calc based on utm shit)
        //and bearing = bearing
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (navToggle.onOptionsItemSelected(item)) {
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
                navDrawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.settings:

                navDrawer.closeDrawer(navigationView);
                navToolbar.setTitle(R.string.menu_settings);

                confFrag.init(new ConfigurationHandler(), dbHelper.getConfigs(Config.USER), dbHelper.getConfigs(Config.SYSTEM));


                transaction.replace(R.id.chat_fragment, confFrag);
                transaction.addToBackStack(null);
                transaction.commit();

                break;


            case R.id.alliances:


                navDrawer.closeDrawer(navigationView);

                floatingActionButton.setBackgroundTintList(allianceColorList);
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_add_circle_white_24dp));

                fabMode = ALLIANCE_FAB;
                navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                navToolbar.setTitle(R.string.menu_alliances);

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

                navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                navToolbar.setTitle(R.string.menu_utm);
                floatingActionButton.setBackgroundTintList(utmColorList);
                floatingActionButton.setVisibility(View.VISIBLE);
                fabMode = GRID_FAB;


                animateToGrid(myUTM, UTM_ZOOM);
                navDrawer.closeDrawer(navigationView);

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
                navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                navToolbar.setTitle(R.string.menu_subutm);
                floatingActionButton.setBackgroundTintList(subUtmColorList);
                floatingActionButton.setVisibility(View.VISIBLE);
                fabMode = GRID_FAB;


                navDrawer.closeDrawer(navigationView);
                animateToGrid(mySubUTM, SUB_UTM_ZOOM);

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
        navToggle.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onResume() {
        super.onResume();


        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        if (currentLocation == null) {
            currentLocation = new Location(sharedPreferences.getString("provider", ""));
        }

        currentLocation.setLatitude(Double.parseDouble(sharedPreferences.getString("latitude", "0.0")));
        currentLocation.setLongitude(Double.parseDouble(sharedPreferences.getString("longitude", "0.0")));


        setUpMapIfNeeded();

        if (locationUpdates == null) {
            initLocationUpdates();
        }
        //and we need to bind to it.
        if (cheService == null) {
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

    }

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
        floatingActionButton.setBackgroundTintList(chatColorList);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_chat_bubble_outline_white_24dp));
        navToolbar.setTitle(title);

        fabMode = CHAT_FAB;
        navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        navToolbar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));


        chatFrag.setCursor(dbHelper.getMessages(Message.ALLIANCE_MESSAGE, chatFrag.getKey()), chatFrag.getKey(), chatFrag.getTitle());
        transaction.replace(R.id.chat_fragment, chatFrag);
        transaction.addToBackStack(null);


        transaction.commit();

    }

    /*
      button handlers.
     */

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
                        coreMessage = new OutAllianceMessage(currentLocation, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        ((OutAllianceMessage) coreMessage).setAlliance(dbHelper.getAlliance(chatFrag.getKey()), OutCoreMessage.PUBLISH, OutCoreMessage.GLOBAL, chatFrag.getHiddenChatPost().getPost());
                        break;

                }

                //need to animate...but
                handleChatFAB(false);


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


                        OutAllianceMessage allianceMessage = new OutAllianceMessage(currentLocation, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey());
                        allianceMessage.setAlliance(alliance, OutAllianceMessage.CREATE, OutAllianceMessage.GLOBAL, "");

                        cheService.writeToSocket(allianceMessage);


                        //need to animate...but
                        handleAllianceFAB(false);


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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * this is demo code.  we want to zoom in better and use last known location etc.  but for testing its fine
     * as i can see other code working
     */
    private void setUpMap() {
        map.getUiSettings().setZoomControlsEnabled(false);
        //now dd our last known location.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        demoLocationListener = new DemoLocationListener(locationManager);

        initLocationUpdates();


        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);


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

        if (userImage != null) {
            if (userImage.getUserImage() != null) {
                Bitmap bitmap = userImage.getUserImage().copy(Bitmap.Config.ARGB_8888, true);

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
            utmOptions = UTMGridCreator.getUTMGrid(new UTM(configuration.getConfig(Configuration.CURRENT_UTM).getValue())).strokeColor(getResources().getColor(android.R.color.holo_purple));
            myUTM = map.addPolygon(utmOptions);

            PolygonOptions subUtmOptions = UTMGridCreator.getSubUTMGrid(new SubUTM(configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue()), utmOptions).strokeColor(getResources().getColor(android.R.color.holo_orange_dark));
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

        editor.putString("latitude", String.valueOf(currentLocation.getLatitude()));
        editor.putString("longitude", String.valueOf(currentLocation.getLongitude()));
        editor.putFloat("zoom", map.getCameraPosition().zoom);
        editor.putFloat("bearing", map.getCameraPosition().bearing);
        editor.putFloat("tilt", map.getCameraPosition().tilt);
        editor.putString("provider", currentLocation.getProvider());


        editor.commit();


    }

    public void onDestroy() {
        super.onDestroy();

        //service = null;
        locationUpdates = null;
        locationUpdates = null;
        unbindService(serviceConnection);

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("latitude", String.valueOf(currentLocation.getLatitude()));
        editor.putString("longitude", String.valueOf(currentLocation.getLongitude()));
        editor.putFloat("zoom", map.getCameraPosition().zoom);
        editor.putFloat("bearing", map.getCameraPosition().bearing);
        editor.putFloat("tilt", map.getCameraPosition().tilt);
        editor.putString("provider", currentLocation.getProvider());

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

    private void initLocationUpdates() {
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

            if (locationUpdates.isAlive()) {
                locationUpdates.interrupt();
                locationUpdates = null;
            }

            initLocationUpdates();
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

            setNavConfigValues();


            final PolygonOptions options = UTMGridCreator.getUTMGrid(new UTM(utm)).strokeColor(getResources().getColor(android.R.color.holo_purple));
            runOnUiThread(new Runnable() {


                @Override
                public void run() {

                    if (myUTM != null) {
                        myUTM.remove();
                    }

                    myUTM = map.addPolygon(options);

                }
            });
        }

        public void handleSubUTMChange(String subUtm) {

            setNavConfigValues();

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

                        if (mySubUTM != null) {
                            mySubUTM.remove();
                        }
                        mySubUTM = map.addPolygon(options);
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

                    if (markerMap.containsKey(allianceMember.getKey())) {
                        markerMap.get(allianceMember.getKey()).remove();
                        Log.d("adding marker", "removing marker ");
                    }

                    Marker marker = map.addMarker(new MarkerOptions().position(allianceMember.getLatLng()).title(allianceMember.getKey()));

                    if (zoomTo) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(allianceMember.getLatLng())
                                .tilt(map.getCameraPosition().tilt)
                                .bearing(map.getCameraPosition().bearing)
                                .zoom(map.getCameraPosition().zoom)
                                .build();

                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    if (marker != null) {
                        markerMap.put(allianceMember.getKey(), marker);
                    }

                }

            });


        }
    }

    public class DeviceDiscovery {

        private BluetoothManager bluetoothManager;


        public DeviceDiscovery(Context context) {

            bluetoothManager = new BluetoothManager(context, connectivityHandler, dbHelper, uuidGenerator, cheService, configuration, chatFrag.getKey(), currentLocation);

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

            currentLocation = location;

            Log.d("location changed", "location changed");
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            if (markerMap.containsKey("Me")) {
                markerMap.get("Me").remove();
            }

            if (userImage != null) {
                if (userImage.getUserImage() != null) {


                    Bitmap bitmap = userImage.getUserImage().copy(Bitmap.Config.ARGB_8888, true);

                    int w = bitmap.getWidth();


                    Bitmap roundBitmap = RoundedImageView.getCroppedBitmap(bitmap, w);

                    //236 - 354
                    markerMap.put("Me", map.addMarker(new MarkerOptions().position(currentLatLng).title("Me").icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(roundBitmap, 354, 354, false))).flat(false)));

                }
            } else {
                markerMap.put("Me", map.addMarker(new MarkerOptions().position(currentLatLng).title("Me")));
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)
                    .tilt(map.getCameraPosition().tilt)
                    .bearing(map.getCameraPosition().bearing)
                    .zoom(map.getCameraPosition().zoom)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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


}

