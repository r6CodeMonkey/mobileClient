package oddymobstar.service;

import android.app.Service;
import android.util.Log;

import java.net.Socket;

import oddymobstar.database.DBHelper;

/**
 * Created by root on 23/02/15.
 */
public class CheService {

    /*
    we have 1 socket.  possibly configure to have more, but 1 is fine for testing
     */
    private Socket socket;
    /*
    we defnitely need an instance of Database as well
     */
    private DBHelper dbHelper;

    /*
    this is the key class. to design.  once i get old source back

     */

    private void connectSocket(){

        /*
        this is only done on start up.....we also need to handle close, and dormant states.
        it is also controlled via config, so we can change credentials.
         */

        try {
            socket = new Socket("192.168.0.4", 8080);

            socket.setKeepAlive(true);
        }catch (Exception e){
            //crashed.
            Log.d("socket error", e.toString());
        }
    }


}
