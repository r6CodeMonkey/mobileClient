package oddymobstar.connect.handler;

import android.content.Intent;

import oddymobstar.connect.ConnectivityInterface;

/**
 * Created by root on 25/04/15.
 */
public class NFC implements ConnectivityInterface {

    private byte[] message;


    public NFC() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void setMessage(byte[] message) {
        this.message = message;
    }

    @Override
    public void handle(int requestCode, int resultCode, Intent data) {

    }
}
