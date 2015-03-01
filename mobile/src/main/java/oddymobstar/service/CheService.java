package oddymobstar.service;

import android.app.IntentService;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import oddymobstar.core.Config;
import oddymobstar.core.Grid;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.message.in.Acknowledge;
import oddymobstar.message.in.AllianceMessage;
import oddymobstar.message.in.GridMessage;
import oddymobstar.message.in.TopicMessage;
import oddymobstar.message.out.CoreMessage;
import oddymobstar.message.out.PackageMessage;
import oddymobstar.util.Configuration;
import oddymobstar.util.UUIDGenerator;

/**
 * Created by root on 23/02/15.
 * <p/>
 * tjw decide if we want service or intent service.  technically its not threaded.
 * <p/>
 * but we do need to receive events but again these are on 1 socket.
 */
public class CheService extends IntentService {

    public static final int BUFFER_SIZE = 2048;
    /*
    we have 1 socket.  possibly configure to have more, but 1 is fine for testing
     */
    private Socket socket;
    private DataOutputStream dOut = null;
    private DataInputStream dIn = null;

    private Map<String, CoreMessage> sentAcks = new HashMap<String, CoreMessage>();

    public class CheServiceBinder extends Binder {
        public CheService getCheServiceInstance() {
            return CheService.this;
        }
    }

    private CheServiceBinder cheServiceBinder = new CheServiceBinder();


    private Configuration configuration;

    /*
    we defnitely need an instance of Database as well
     */
    private DBHelper dbHelper = new DBHelper(this);

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


    public CheService() {
        super("CheService");
    }


    @Override
    public void onCreate() {

        //this works fine for what i want (need to ensure it stays up.
        //also need to have timeout / reconnect etc.
        configuration = new Configuration(dbHelper.getConfigs());


        //st up our socket.
        new Thread(new Runnable() {
            public void run() {
                connectSocket();
            }
        }).start();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return cheServiceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (dIn != null) {
            try {
                dIn.close();
            } catch (Exception e) {
                Log.d("dIn error", e.toString());
            }
        }

        if (dOut != null) {
            try {
                dOut.close();
            } catch (Exception e) {
                Log.d("dOut error", e.toString());
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.d("socket error", e.toString());
            }
        }


        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void socketListen() {


        try {

            while (socket.isConnected()) {

                byte[] buffer = new byte[BUFFER_SIZE];

                int charsRead = 0;

                while ((charsRead = dIn.read(buffer)) != -1) {
                    try {
                        //each message we receive should be a JSON.  We need to work out the type.
                        oddymobstar.message.in.CoreMessage coreMessage = new oddymobstar.message.in.CoreMessage(new String(buffer).substring(0, charsRead));

                        //what are we?
                        if (!coreMessage.getJsonObject().isNull(Acknowledge.ACKNOWLEDGE)) {

                            Acknowledge ack = new Acknowledge(coreMessage.getJsonObject().getJSONObject(Acknowledge.ACKNOWLEDGE));
                            ack.create();

                            /*
                            acknowledges either tell us of a fail (we can log it etc) or tell us of a success and generally a UUID.
                           */
                            if (ack.getState().equals(Acknowledge.ERROR)) {
                                Log.d("ack error", "error information is " + ack.getInfo());
                            } else {
                                //match to our acknowledge sent list...and this will hold the relevant action required.
                                CoreMessage ackSent = sentAcks.get(ack.getAckId());

                                if (ackSent != null) {
                                    //is it a UUID message
                                    if (ack.getInfo().equals(Acknowledge.UUID)) {
                                        if (ackSent instanceof oddymobstar.message.out.TopicMessage) {

                                        } else if (ackSent instanceof oddymobstar.message.out.AllianceMessage) {

                                        } else if (ackSent instanceof PackageMessage) {

                                        } else {
                                            //its core.  therefore player.
                                            Config config = configuration.getConfig(Configuration.PLAYER_KEY);
                                            config.setValue(ack.getUid());
                                            dbHelper.updateConfig(config);
                                        }
                                    }

                                  /*
                                   also we have the current UTM / SUBUTM so we need to update this too.

                                   */
                                    if (!(ackSent instanceof oddymobstar.message.out.AllianceMessage
                                            || ackSent instanceof oddymobstar.message.out.TopicMessage
                                            || ackSent instanceof PackageMessage)) {
                                        Config config = configuration.getConfig(Configuration.CURRENT_UTM);
                                        config.setValue(ack.getUtm());
                                        dbHelper.updateConfig(config);

                                        config = configuration.getConfig(Configuration.CURRENT_SUBUTM);
                                        config.setValue(ack.getSubUtm());
                                        dbHelper.updateConfig(config);
                                    }

                                    sentAcks.remove(ack.getAckId());
                                }

                            }

                        } else if (!coreMessage.getJsonObject().isNull(GridMessage.GRID)) {

                            GridMessage gridMessage = new GridMessage(coreMessage.getJsonObject().getJSONObject(GridMessage.GRID));
                            gridMessage.create();

                        } else if (!coreMessage.getJsonObject().isNull(AllianceMessage.ALLIANCE)) {

                            AllianceMessage allianceMessage = new AllianceMessage(coreMessage.getJsonObject().getJSONObject(AllianceMessage.ALLIANCE));
                            allianceMessage.create();


                        } else if (!coreMessage.getJsonObject().isNull(TopicMessage.TOPIC)) {

                            TopicMessage topicMessage = new TopicMessage(coreMessage.getJsonObject().getJSONObject(TopicMessage.TOPIC));
                            topicMessage.create();

                        }

                        Log.d("socket listen", "we have read " + new String(buffer).substring(0, charsRead));

                    } catch (JSONException jse) {
                        Log.d("json exception", "json exception " + jse.toString());
                    }

                }


            }


        } catch (Exception e) {

            Log.d("socket listen", "socket listen error " + e.toString());

        }


    }

    private void reConnect() {

        /*
        id we go down we need to be able to re connect.  not issue at moment as we are running service from app
        Long term, we want service running and managing itself outside of app (android system permissions to review).

        cant be accessed publicly tho, so we need to manage it all here.

        Likely need a watcher thread on connection, and to try to reconnect it if it goes down.

        Stupid test here as i am wired into local network,

         */

    }


    public void writeToSocket(CoreMessage coreMessage, String ackId) {


        try {
            //to tidy this all up its just a test
            //   String ackId = uuidGenerator.generateAcknowledgeKey();
            //   coreMessage = new CoreMessage(new LatLng(1, 1), configuration.getConfig(Configuration.PLAYER_KEY).getValue(), ackId, CoreMessage.PLAYER);

            sentAcks.put(ackId, coreMessage);


            dOut.writeUTF(coreMessage.getMessage().toString());


        } catch (Exception e) {
            Log.d("socket exception", "socket " + e.toString());
        }

    }

    /*
      this will also need to be used to reconnect the socket if it goes down or is shut
      (due to network signal etc).
     */
    private void connectSocket() {


        try {
            socket = new Socket(configuration.getConfig(Configuration.URL).getValue(), Integer.parseInt(configuration.getConfig(Configuration.PORT).getValue()));
            socket.setKeepAlive(true);

            //set up the two stream readers
            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());


            //now start listening to the socket this is ongoing.
            new Thread((new Runnable() {
                @Override
                public void run() {
                    socketListen();
                }
            })).start();


        } catch (Exception e) {
            //crashed.
            Log.d("socket error", "socket " + e.toString());

        }
    }

    @Override
    public ComponentName startService(Intent intent) {
        return super.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //to review this.
        return START_NOT_STICKY;
        //  return super.onStartCommand(intent,flags,startId);
    }


}
