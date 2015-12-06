package oddymobstar.activity.handler;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import oddymobstar.activity.DemoActivity;
import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.activity.listener.MaterialsListener;
import oddymobstar.connect.bluetooth.handler.Bluetooth;
import oddymobstar.message.out.OutImageMessage;
import oddymobstar.model.UserImage;
import oddymobstar.util.Configuration;
import oddymobstar.util.widget.ConnectivityDialog;

/**
 * Created by timmytime on 04/12/15.
 */
public class ActivityResultHandler {

    private DemoActivity main;
    private DemoActivityController controller;

    public ActivityResultHandler(DemoActivity main, DemoActivityController controller) {
        this.main = main;
        this.controller = controller;
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case MaterialsListener.USER_IMAGE_RESULT_CODE:

                if (resultCode == main.RESULT_OK) {
                    try {
                        // We need to recyle unused bitmaps

                        InputStream stream = main.getContentResolver().openInputStream(
                                data.getData());
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        stream.close();
                        bitmap = Bitmap.createScaledBitmap(bitmap, 236, 354, false);
                        Log.d("bitmap size is", "size " + bitmap.getRowBytes() * bitmap.getHeight());
                        controller.materialsHelper.userImageView.setImageBitmap(bitmap);
                        byte[] imageArray;
                        if (controller.materialsHelper.userImage == null) {
                            controller.materialsHelper.userImage = new UserImage();
                            controller.materialsHelper.userImage.setUserImageKey(controller.configuration.getConfig(Configuration.PLAYER_KEY).getValue());
                            controller.materialsHelper.userImage.setUserImage(bitmap);
                            imageArray = controller.dbHelper.addUserImage(controller.materialsHelper.userImage);
                        } else {
                            controller.materialsHelper.userImage.setUserImage(bitmap);
                            imageArray = controller.dbHelper.updateUserImage(controller.materialsHelper.userImage);
                        }

                        try {
                            final OutImageMessage outImageMessage = new OutImageMessage(controller.locationListener.getCurrentLocation(), controller.configuration.getConfig(Configuration.PLAYER_KEY).getValue(), controller.uuidGenerator.generateAcknowledgeKey());
                            outImageMessage.setImage(Base64.encodeToString(imageArray, Base64.DEFAULT));

                            new Thread((new Runnable() {
                                @Override
                                public void run() {
                                    controller.cheService.writeToSocket(outImageMessage);
                                }
                            })).start();


                        } catch (JSONException jse) {

                        } catch (NoSuchAlgorithmException nsae) {

                        }

                        //need to review this too.! bitmap.recycle();


                    } catch (FileNotFoundException fe) {
                    } catch (IOException e) {

                    }
                }


                return;

            case Bluetooth.REQUEST_ENABLE_BT:


                switch (controller.connectivityHandler.getMode()) {
                    case ConnectivityDialog.BLUETOOTH:

                        controller.bluetoothReceiver = controller.connectivityHandler.getBluetooth().getReceiver();
                        main.registerReceiver(controller.bluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

                        controller.connectivityHandler.getBluetooth().handle(requestCode, resultCode, data);
                        if (resultCode == controller.connectivityHandler.getBluetooth().DISCOVERABLE_SECONDS) {
                            controller.deviceDiscoveryHandler.init();
                            controller.connectivityHandler.getBluetooth().getProgress(controller.deviceDiscoveryHandler).show();
                        }
                        break;

                }

                return;
        }
    }

}
