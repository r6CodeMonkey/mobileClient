package oddymobstar.activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.crazycourier.R;
import oddymobstar.util.SubUTM;
import oddymobstar.util.UTM;

public class DemoActivity extends AppCompatActivity {


    public static final Long TWO_MINUTES = 120000l;
    private static Typeface font = null;
    private DemoActivityController controller = new DemoActivityController(this);

    public static Typeface getFont() {
        return font;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller.onCreate();
        setContentView(R.layout.activity_game);

        font = Typeface.createFromAsset(
                this.getAssets(), "fontawesome-webfont.ttf");

        //useful makes it a bit easier to work with.
        UTM.createUTMRegions();
        SubUTM.createSubUtms();

        controller.onCreate();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        controller.onPostCreate();
        // Sync the toggle state after onRestoreInstanceState has occurred.
        controller.onPostCreate();
    }


    @Override
    public void onBackPressed() {
        controller.onBackPressed();
        controller.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        controller.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        controller.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        controller.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onResume();
    }

    public void onPause() {
        super.onPause();
        controller.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        controller.onDestroy();
    }

    /*
      View Callbacks / action performed
     */

    public void deleteMessages(View view) {
        controller.viewHandler.deleteMessages();
    }

    public void messageCoverage(View view) {
        controller.viewHandler.messageCoverage();
    }


    public void allianceInvite(View view) {
        controller.viewHandler.allianceInvite(false);
    }

    public void sendPost(View view) {
        controller.viewHandler.sendPost();
    }

    public void cancelPost(View view) {
        controller.viewHandler.cancelPost();
    }

    public void createButton(View view) {
        controller.viewHandler.createButton();
    }


}

