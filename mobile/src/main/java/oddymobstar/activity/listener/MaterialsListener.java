package oddymobstar.activity.listener;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import oddymobstar.activity.DemoActivity;
import oddymobstar.activity.handler.MaterialsHandler;
import oddymobstar.fragment.ChatFragment;
import oddymobstar.fragment.GridFragment;

/**
 * Created by timmytime on 03/12/15.
 */
public class MaterialsListener {


    public static final int ALLIANCE_FAB = 0;
    public static final int CHAT_FAB = 1;
    public static final int GRID_FAB = 2;

    public static int FAB_MODE = ALLIANCE_FAB;

    public static final int USER_IMAGE_RESULT_CODE = 1001;


    private DemoActivity main;
    private MaterialsHandler materialsHandler;

    public MaterialsListener(DemoActivity main, MaterialsHandler materialsHandler){
        this.main = main;
        this.materialsHandler = materialsHandler;
    }

    public View.OnClickListener getFABListener(){
        return   new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (FAB_MODE) {
                    case ALLIANCE_FAB:
                        materialsHandler.handleAllianceFAB(main.getGridFrag(), true);
                        break;
                    case CHAT_FAB:
                        materialsHandler.handleChatFAB(main.getChatFrag(), true);
                        break;
                    case GRID_FAB:
                        materialsHandler.handleSearchFab();
                        break;
                }

            }
        };

        /*
        CURRENT_GRID_FAB_STATE == SUBUTM_FAB_STATE ?
                                configuration.getConfig(Configuration.CURRENT_SUBUTM).getValue() :
                                CURRENT_GRID_FAB_STATE == UTM_FAB_STATE ? UTM.getUTMRegion(configuration.getConfig(Configuration.CURRENT_UTM).getValue()) : "");
         */

    }

    public View.OnTouchListener getImageListener(){
        return   new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //we need to launch to access gallery store
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                main.startActivityForResult(intent, USER_IMAGE_RESULT_CODE);

                return false;
            }
        };
    }


}
