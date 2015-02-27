package oddymobstar.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.util.Configuration;

/**
 * Created by root on 23/02/15.
 *
 * tjw decide if we want service or intent service.  technically its not threaded.
 *
 * but we do need to receive events but again these are on 1 socket.
 *
 */
public class CheService extends IntentService {

    /*
    we have 1 socket.  possibly configure to have more, but 1 is fine for testing
     */
    private Socket socket;

    private Configuration configuration;
    /*
    we defnitely need an instance of Database as well
     */
    private DBHelper dbHelper;

    /*
    this is the key class. to design.  once i get old source back

    basically it runs permanently.
    it can be turned off and controlled via config.  but basically it does
    the dirty work.  it marshalls the acknowledges and updates the database.

    the client can execute on it.  ie, it can bind to service, but client has no
    ability to access server directly.

    Techincally we need a secondary service to maintain the config?

    That would allow us to reconfigure this service.


     */


    public CheService(Configuration configuration){
        super("CheService");
        this.configuration = configuration;

    }

    private void connectSocket(){

        try {
            socket = new Socket(configuration.getConfig(Configuration.URL).getValue(), Integer.parseInt(configuration.getConfig(Configuration.PORT).getValue()));

            socket.setKeepAlive(true);
        }catch (Exception e){
            //crashed.
            Log.d("socket error", e.toString());
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }



/*  the crap from prototype.  basically its shite and reconnects over and over.
    private void connectSocket(){
        try {
            socket = new Socket("192.168.0.4", 8080);

            socket.setKeepAlive(true);
        }catch (Exception e){
            //crashed.
            Log.d("socket error", e.toString());
        }
    }

    public void echo(View view){

        //grab text
        final EditText et = (EditText)this.findViewById(R.id.echo_text);

        if(socket != null){

            Thread t = new Thread(new Runnable(){
                public void run(){


                    DataOutputStream dOut = null;
                    DataInputStream dIn = null;
                    try {
                        dOut = new DataOutputStream(socket.getOutputStream());
                        dOut.writeUTF("Hello socket");




                        // dOut.close();



                        dIn = new DataInputStream(socket.getInputStream());

                        final String text = dIn.readUTF();

                        dIn.close();

                        EchoActivity.this.runOnUiThread(new Runnable(){
                            public void run(){
                                et.setText(text);
                            }
                        });


                    }catch(Exception e) {
                        Log.d("socket error 2", e.toString());
                    }

                    connectSocket();
                }

            });

            t.start();

        }

    }

    public void onDestroy(){
        super.onDestroy();
        if(socket != null){
            try {
                socket.close();
            }catch(Exception e){

            }

        }
    }
     */




}
