package oddymobstar.connect;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.nfc.NfcManager;
import android.support.v4.app.FragmentActivity;

import oddymobstar.connect.handler.Bluetooth;
import oddymobstar.connect.handler.NFC;
import oddymobstar.connect.handler.Wifi;
import oddymobstar.util.widget.ConnectivityDialog;

/**
 * Created by root on 25/04/15.
 */
public class ConnectivityHandler {

    private FragmentActivity activity;

    private Bluetooth bluetooth;
    private NFC nfc;
    private Wifi wifi;

    private String mode;

    private String uuid;
    private byte[] message;

    public ConnectivityHandler(FragmentActivity activity, String uuid) {
        this.activity = activity;
        this.uuid = uuid;

    }

    public void handle(String mode) {

        switch (mode) {
            case ConnectivityDialog.BLUETOOTH:
                bluetooth = new Bluetooth(activity, uuid);
                bluetooth.enable();
                break;
            case ConnectivityDialog.NFC:
                break;
            case ConnectivityDialog.WIFI:
                break;
        }

        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public NFC getNfc() {
        return nfc;
    }

    public Wifi getWifi() {
        return wifi;
    }

    public Bluetooth getBluetooth() {
        return bluetooth;
    }


    public boolean hasBluetooth() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null;
    }

    public boolean hasNFC() {

        NfcManager nfcManager = (NfcManager) activity.getSystemService(Context.NFC_SERVICE);
        return nfcManager != null;
    }

    public boolean hasWifiP2P() {
        //is slightly different.  leave for time being.
        return true;
    }


}
