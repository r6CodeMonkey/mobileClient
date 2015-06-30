package oddymobstar.connect.bluetooth.socket;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import oddymobstar.activity.DemoActivity;
import oddymobstar.message.in.InAllianceMessage;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.message.out.Acknowledge;
import oddymobstar.message.out.OutCoreMessage;

/**
 * Created by root on 26/04/15.
 */
public class BluetoothSocketThread extends Thread {

    private DemoActivity.DeviceDiscovery deviceDiscovery;
    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;


    public BluetoothSocketThread(DemoActivity.DeviceDiscovery deviceDiscovery, BluetoothSocket socket) {
        this.socket = socket;
        this.deviceDiscovery = deviceDiscovery;


        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();

        } catch (IOException ioe) {

        }

    }


    public void run() {

        byte[] buffer = new byte[1024];
        int bytes;

        boolean reading = true;

        while (reading) {

            try {
                bytes = in.read(buffer);

                String message = new String(buffer).substring(0, bytes);
                Log.d("json socket read", "message is " + message);
                try {
                    JSONObject jsonObject = new JSONObject(message);

                    if (!jsonObject.isNull(InCoreMessage.CORE)) {
                        //we need to send back our ack...to allow unpairing..we could match on the ack id...rather than name.  true.
                        Acknowledge acknowledge = new Acknowledge(jsonObject.getJSONObject(InCoreMessage.CORE).getString(OutCoreMessage.ACK_ID), deviceDiscovery.getBluetoothManager().getDeviceName());
                        write(acknowledge.getAcknowledge().toString().getBytes());
                        deviceDiscovery.getBluetoothManager().handleMessage(new InAllianceMessage(jsonObject.getJSONObject(InCoreMessage.CORE).getJSONObject(InCoreMessage.ALLIANCE)));
                    } else {
                        deviceDiscovery.getBluetoothManager().unpair(jsonObject.getString(OutCoreMessage.NAME));
                    }

                } catch (JSONException jse) {
                    Log.d("json socket read", "error is " + jse.toString());

                }


            } catch (IOException ioe) {
                cancel();
                reading = false;
            }

        }

    }

    /*
      we use this to basically write the invite to the user.
      we will expect a confirmation back.
     */
    public void write(byte[] bytes) {

        try {
            out.write(bytes);

        } catch (IOException ioe) {

        }

    }

    public void cancel() {
        try {
            in.close();
            out.close();
            socket.close();

        } catch (IOException ioe) {

        }

    }
}
