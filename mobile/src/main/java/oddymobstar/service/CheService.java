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
import android.widget.Toast;

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

import oddymobstar.core.Alliance;
import oddymobstar.core.Config;
import oddymobstar.core.Grid;
import oddymobstar.core.Topic;
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

    private Thread write;

    //retry counters + max
    private int retryCounter = 0;
    private final static int MAX_RETRY = 10;

    private Map<String, CoreMessage> sentAcks = new HashMap<String, CoreMessage>();

    public class CheServiceBinder extends Binder {
        public CheService getCheServiceInstance() {
            return CheService.this;
        }
    }

    private CheServiceBinder cheServiceBinder = new CheServiceBinder();


    private Configuration configuration;
    private DBHelper dbHelper = new DBHelper(this);


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
                dIn = null;
            }
        }

        if (dOut != null) {
            try {
                dOut.close();
            } catch (Exception e) {
                Log.d("dOut error", e.toString());
                dOut = null;
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.d("socket error", e.toString());
                socket = null;
            }
        }


        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void socketListen() {


        try {


            byte[] buffer = new byte[BUFFER_SIZE];

            int charsRead = 0;

            while ((charsRead = dIn.read(buffer)) != -1) {
                try {
                    //each message we receive should be a JSON.  We need to work out the type.
                    oddymobstar.message.in.CoreMessage coreMessage = new oddymobstar.message.in.CoreMessage(new String(buffer).substring(0, charsRead));

                    Log.d("incoming", "the core message is " + coreMessage.getJsonObject().toString());
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
                            if (ack.getInfo().equals(Acknowledge.ACTIVE)) {
                                //we need to wake up our write thread.
                                if (write != null) {

                                    Log.d("ack error", "trying to wake up thread");

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            synchronized (write) {
                                                write.notify();
                                            }
                                        }
                                    }).start();


                                }
                            }
                            //match to our acknowledge sent list...and this will hold the relevant action required.
                            CoreMessage ackSent = sentAcks.get(ack.getAckId());

                            if (ackSent != null) {
                                //is it a UUID message
                                if (ack.getInfo().equals(Acknowledge.UUID)) {
                                    if (ackSent instanceof oddymobstar.message.out.TopicMessage) {

                                        Topic topic = new Topic();
                                        topic.setKey(ack.getOid());
                                        topic.setName(ackSent.getMessage().getJSONObject(CoreMessage.CORE_OBJECT).getJSONObject(oddymobstar.message.out.TopicMessage.TOPIC).getString(oddymobstar.message.out.TopicMessage.TNAME));
                                        dbHelper.addTopic(topic);

                                    } else if (ackSent instanceof oddymobstar.message.out.AllianceMessage) {

                                        Alliance alliance = new Alliance();
                                        alliance.setKey(ack.getOid());
                                        alliance.setName(ackSent.getMessage().getJSONObject(CoreMessage.CORE_OBJECT).getJSONObject(oddymobstar.message.out.AllianceMessage.ALLIANCE).getString(oddymobstar.message.out.AllianceMessage.ANAME));
                                        dbHelper.addAlliance(alliance);

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

                        if (topicMessage.getFilter().equals(TopicMessage.POST)) {

                            try {
                                Topic topic = new Topic();
                                topic.setKey(topicMessage.getTid());
                                topic.setName(topicMessage.getTitle());
                                Topic checkGlobal = dbHelper.getGlobalTopic(topic.getKey());
                                if(!checkGlobal.getKey().equals(topic.getKey())) {
                                    dbHelper.addGlobalTopic(topic);
                                }
                                //this may not be catching it lol.  it seems not.  otherwise it all works (ie get global topics)
                            } catch (android.database.sqlite.SQLiteConstraintException e) {
                                Log.d("topic error", "sql exception  " + e.toString());
                            }
                        }

                    }

                    Log.d("socket listen", "we have read " + new String(buffer).substring(0, charsRead));

                } catch (JSONException jse) {
                    Log.d("json exception", "json exception " + jse.toString());
                }

            }


        } catch (Exception e) {

            Log.d("socket listen", "socket listen error " + e.toString());

        }


    }

    private void reConnect(final CoreMessage coreMessage) {

        try {
            socket.close();
        } catch (Exception e) {

        }

        socket = null;


        try {
            dIn.close();
        } catch (Exception e) {
        }
        try {
            dOut.close();
        } catch (Exception e) {
        }

        //try again
        connectSocket();


        //we now need to wait for the active connection message again.
        write = new Thread();

        write.start();

        synchronized (write) {
            try {
                this.wait();
            } catch (InterruptedException ie) {
                Log.d("wait", "wait " + ie.toString());
            } catch (Exception e) {
                Log.d("wait", "wait " + e.toString());
            }


            writeToSocket(coreMessage);

        }


    }


    public void writeToSocket(final CoreMessage coreMessage) {


        try {

            sentAcks.put(coreMessage.getMessage().getJSONObject(CoreMessage.CORE_OBJECT).getString(CoreMessage.ACK_ID), coreMessage);
            dOut.writeUTF(coreMessage.getMessage().toString());


        } catch (Exception e) {
            Log.d("socket exception", "socket " + e.toString() + coreMessage.getMessage());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    reConnect(coreMessage);
                }
            }).start();

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
        return START_STICKY;
        //  return super.onStartCommand(intent,flags,startId);
    }


}
