package oddymobstar.activity.helper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import oddymobstar.activity.DemoActivity;
import oddymobstar.crazycourier.R;
import oddymobstar.model.UserImage;
import oddymobstar.util.graphics.RoundedImageView;

/**
 * Created by timmytime on 03/12/15.
 */
public class MaterialsHelper {

    public static final int UTM_COLOR = 1;
    public static final int SUB_UTM_COLOR = 2;
    public static final int ALLIANCE_COLOR = 3;
    public static final int CHAT_COLOR = 4;
    public DrawerLayout navDrawer;
    public ActionBarDrawerToggle navToggle;
    public NavigationView navigationView;
    public Toolbar navToolbar;
    public Toolbar hiddenToolbar;
    public FloatingActionButton floatingActionButton;
    public RoundedImageView userImageView;
    public UserImage userImage;
    private Context context;
    private DemoActivity main;
    private ColorStateList subUtmColorList;
    private ColorStateList utmColorList;
    private ColorStateList allianceColorList;
    private ColorStateList chatColorList;


    public MaterialsHelper(DemoActivity main) {
        this.context = main.getApplicationContext();
        this.main = main;

        subUtmColorList = createColorStateList(android.R.color.holo_orange_dark);
        utmColorList = createColorStateList(android.R.color.holo_purple);
        allianceColorList = createColorStateList(android.R.color.holo_red_dark);
        chatColorList = createColorStateList(android.R.color.holo_green_dark);

    }

    public ColorStateList getColorStateList(int which) {
        switch (which) {

            case UTM_COLOR:
                return utmColorList;
            case SUB_UTM_COLOR:
                return subUtmColorList;
            case ALLIANCE_COLOR:
                return allianceColorList;
            case CHAT_COLOR:
                return chatColorList;
            default:
                return utmColorList;

        }
    }


    private ColorStateList createColorStateList(int color) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        context.getResources().getColor(color), //1
                        context.getResources().getColor(color), //2
                        context.getResources().getColor(color) //3
                });
    }


    public void setUpMaterials(View.OnClickListener fabListener, View.OnTouchListener imageListener) {
        navDrawer = (DrawerLayout) main.findViewById(R.id.drawer_layout);
        navDrawer.setElevation(16.0f);

        navToolbar = (Toolbar) main.findViewById(R.id.toolbar);


        navToggle = new ActionBarDrawerToggle(
                main,
                navDrawer,
                navToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                main.invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                main.invalidateOptionsMenu();
            }
        };


        navDrawer.setDrawerListener(navToggle);


        //  toolbar.setLogo(R.drawable.ic_drawer);
        main.setSupportActionBar(navToolbar);
        main.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        main.getSupportActionBar().setHomeButtonEnabled(true);
        main.getSupportActionBar().setElevation(12.0f);


        navigationView = (NavigationView) main.findViewById(R.id.left_drawer);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                main.onOptionsItemSelected(menuItem);
                return true;
            }
        });

        floatingActionButton = (FloatingActionButton) main.findViewById(R.id.fab);

        floatingActionButton.setImageDrawable(context.getDrawable(R.drawable.ic_search_white_24dp));

        floatingActionButton.setBackgroundTintList(getColorStateList(SUB_UTM_COLOR));
        floatingActionButton.setVisibility(View.INVISIBLE);

        floatingActionButton.setOnClickListener(fabListener);

        hiddenToolbar = (Toolbar) main.findViewById(R.id.hidden_toolbar);
        hiddenToolbar.setVisibility(View.INVISIBLE);

        hiddenToolbar.setNavigationIcon(context.getDrawable(R.drawable.ic_search_white_24dp));

        userImageView = (RoundedImageView) navigationView.findViewById(R.id.user_image);
        userImageView.setOnTouchListener(imageListener);

    }


}
