package oddymobstar.activity.handler;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.security.NoSuchAlgorithmException;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.activity.helper.MaterialsHelper;
import oddymobstar.activity.listener.MaterialsListener;
import oddymobstar.crazycourier.R;
import oddymobstar.fragment.GridFragment;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.model.Alliance;
import oddymobstar.model.Message;
import oddymobstar.util.Configuration;
import oddymobstar.util.widget.ConnectivityDialog;

/**
 * Created by timmytime on 06/12/15.
 */
public class ViewHandler {

    private AppCompatActivity main;
    private DemoActivityController controller;

    public ViewHandler(AppCompatActivity main, DemoActivityController controller) {
        this.main = main;
        this.controller = controller;
    }

    public void deleteMessages() {
        //grab the chat frag id...
        controller.dbHelper.deleteMessages(controller.fragmentHandler.chatFrag.getKey());
        controller.fragmentHandler.removeFragments(false);

    }

    public void messageCoverage() {

    }

    public void allianceInvite(boolean isClient) {

        FragmentTransaction transaction = main.getSupportFragmentManager()
                .beginTransaction();

        ConnectivityDialog connectivityDialog = ConnectivityDialog.newInstance(controller.connectivityHandler, isClient);

        connectivityDialog.show(transaction, "dialog");
    }


    public void sendPost() {
        if (controller.fragmentHandler.chatFrag.getHiddenChatPost().isPostValid()) {
            try {

                OutCoreMessage coreMessage = null;

                switch (controller.fragmentHandler.gridFrag.getType()) {


                    case GridFragment.MY_ALLIANCES:

                        //create a message for the alliance....
                        coreMessage = new OutAllianceMessage(controller.locationListener.getCurrentLocation(), controller.configuration.getConfig(Configuration.PLAYER_KEY).getValue(), controller.uuidGenerator.generateAcknowledgeKey());
                        ((OutAllianceMessage) coreMessage).setAlliance(controller.dbHelper.getAlliance(controller.fragmentHandler.chatFrag.getKey()), OutCoreMessage.PUBLISH, OutCoreMessage.GLOBAL, controller.fragmentHandler.chatFrag.getHiddenChatPost().getPost());
                        break;

                }

                //need to animate...but
                controller.materialsHandler.handleChatFAB(controller.fragmentHandler.chatFrag, false);


                controller.cheService.writeToSocket(coreMessage);

            } catch (NoSuchAlgorithmException nse) {

            } catch (JSONException jse) {
            }

            cancelPost();
        } else {
            //find out if this every works! it was not cause of my bug
            controller.fragmentHandler.removeFragments(false);
        }
    }

    public void cancelPost() {
        controller.fragmentHandler.chatFrag.getHiddenChatPost().cancelPost();
    }

    public void createButton() {
        String createText = controller.fragmentHandler.gridFrag.getHiddenCreateView().getCreateText();

        switch (controller.fragmentHandler.gridFrag.getType()) {

            case GridFragment.MY_ALLIANCES:

                if (!createText.trim().isEmpty()) {

                    try {
                        //LatLng latLng, String uid, String ackId, String type
                        Alliance alliance = new Alliance();
                        alliance.setName(createText);


                        OutAllianceMessage allianceMessage = new OutAllianceMessage(controller.locationListener.getCurrentLocation(), controller.configuration.getConfig(Configuration.PLAYER_KEY).getValue(), controller.uuidGenerator.generateAcknowledgeKey());
                        allianceMessage.setAlliance(alliance, OutAllianceMessage.CREATE, OutAllianceMessage.GLOBAL, "");

                        controller.cheService.writeToSocket(allianceMessage);


                        //need to animate...but
                        controller.materialsHandler.handleAllianceFAB(controller.fragmentHandler.gridFrag, false);


                    } catch (NoSuchAlgorithmException nsae) {
                        Log.d("security", "security " + nsae.toString());
                    } catch (JSONException jse) {
                        Log.d("json", "json " + jse.toString());
                    }
                }

                break;

        }

        controller.fragmentHandler.gridFrag.getHiddenCreateView().clear();

    }

    public void showChat(String title) {
        android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();


        //change the
        controller.materialsHandler.handleFABChange(MaterialsHelper.CHAT_COLOR, R.drawable.ic_chat_bubble_outline_white_24dp, View.VISIBLE);
        controller.materialsHelper.navToolbar.setTitle(title);

        MaterialsListener.FAB_MODE = MaterialsListener.CHAT_FAB;
        controller.materialsHelper.navToolbar.setBackgroundColor(main.getResources().getColor(android.R.color.holo_green_dark));
        controller.materialsHelper.navToolbar.setBackgroundColor(main.getResources().getColor(android.R.color.holo_green_dark));


        controller.fragmentHandler.chatFrag.setCursor(controller.dbHelper.getMessages(Message.ALLIANCE_MESSAGE, controller.fragmentHandler.chatFrag.getKey()), controller.fragmentHandler.chatFrag.getKey(), controller.fragmentHandler.chatFrag.getTitle());
        transaction.replace(R.id.chat_fragment, controller.fragmentHandler.chatFrag);
        transaction.addToBackStack(null);


        transaction.commit();

    }


}
