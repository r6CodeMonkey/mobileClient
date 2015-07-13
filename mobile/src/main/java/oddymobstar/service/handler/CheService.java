package oddymobstar.service.handler;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import oddymobstar.activity.DemoActivity;
import oddymobstar.adapter.SystemConfigurationAdapter;
import oddymobstar.database.DBHelper;
import oddymobstar.message.in.Acknowledge;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.util.Configuration;
import oddymobstar.util.UUIDGenerator;


/**
 * Created by root on 23/02/15.
 */
public class CheService extends IntentService {

    public static final int BUFFER_SIZE = 2048;

    private Socket socket;
    private DataOutputStream dOut = null;
    private DataInputStream dIn = null;

    private DBHelper dbHelper = new DBHelper(this);

    private MessageService messageHandler;


    private Thread write;
    private Thread connect;
    private Thread read;
    private Thread locationUpdates;

    private List<OutCoreMessage> messageBuffer = new ArrayList<>();


    //service manager
    private LocationManager locationManager;
    private android.os.Handler handler = new android.os.Handler();


    public class CheServiceBinder extends Binder {
        public CheService getCheServiceInstance() {
            return CheService.this;
        }
    }

    private CheServiceBinder cheServiceBinder = new CheServiceBinder();


    public class DemoLocationListener implements LocationListener {

        private UUIDGenerator uuidGenerator;

        private LocationManager locationManager;

        public DemoLocationListener(LocationManager locationManager) {
            this.locationManager = locationManager;
        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            //  callBack.setLocationUpdated(location);
            uuidGenerator = new UUIDGenerator(configuration.getConfig(Configuration.UUID_ALGORITHM).getValue());
            try {
                OutCoreMessage coreMessage = new OutCoreMessage(location, configuration.getConfig(Configuration.PLAYER_KEY).getValue(), uuidGenerator.generateAcknowledgeKey(), OutCoreMessage.PLAYER);
                writeToSocket(coreMessage);

            } catch (NoSuchAlgorithmException nsae) {
                Log.d("Security error", nsae.toString());
            } catch (JSONException jse) {
                Log.d("JSON error", jse.toString());
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            locationManager.removeUpdates(this);

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            locationManager.requestLocationUpdates(provider, Long.parseLong(configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, this);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

            if ((!locationManager.isProviderEnabled(provider)) && status == LocationProvider.AVAILABLE) {
                locationManager.requestLocationUpdates(provider, Long.parseLong(configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, this);
            }
        }
    }

    private DemoLocationListener demoLocationListener;

    private Configuration configuration;


    public CheService() {
        super("CheService");
    }

    public void setMessageHandler(DemoActivity.MessageHandler messageHandler) {
        dbHelper.setMessageHandler(messageHandler);
    }


    @Override
    public void onCreate() {

        //this works fine for what i want (need to ensure it stays up.
        //also need to have timeout / reconnect etc.
        configuration = new Configuration(dbHelper.getConfigs());

        messageHandler = new MessageService(dbHelper, configuration);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        demoLocationListener = new DemoLocationListener(locationManager);

        initLocationUpdates();

        if (connect == null) {
            //st up our socket.
            connect = new Thread(new Runnable() {
                public void run() {
                    connectSocket();
                }
            });

            connect.start();
        }


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

        locationUpdates = null;
        write = null;
        read = null;
        connect = null;


        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void socketListen() {

        /*
          this needs to be broken up and call a secondary class to handle the various input
          bar acknowledge.  that way the actions can be overriden.  part of refactor work.
         */

        try {


            byte[] buffer = new byte[BUFFER_SIZE];
            String partialObject = "";
            int partialOpenBracket = 0;
            int partialCloseBracket = 0;


            int charsRead = 0;

            while ((charsRead = dIn.read(buffer)) != -1) {
                try {
                    //we need to grab each core message out.
                    int openBracket = partialOpenBracket > 0 ? partialOpenBracket : 0;
                    int closeBracket = partialCloseBracket > 0 ? partialCloseBracket : 0;
                    partialOpenBracket = 0;
                    partialCloseBracket = 0;

                    //for the given line we need to read all of it
                    char[] lineRead = new String(buffer).substring(0, charsRead).toCharArray();
                    boolean objectsToRead = true;
                    int charPos = 0;

                    while (objectsToRead) {

                        boolean objectFound = false;
                        String object = partialObject.trim().isEmpty() ? "" : partialObject;
                        partialObject = "";


                        for (int i = charPos; i < lineRead.length && !objectFound; i++) {
                            if (lineRead[i] == '{') {
                                openBracket++;
                            }
                            if (lineRead[i] == '}') {
                                closeBracket++;
                            }

                            object = object + lineRead[i];

                            if (openBracket == closeBracket) {
                                objectFound = true;
                                charPos = i + 1;
                            }

                            if (i == lineRead.length - 1) {
                                objectsToRead = false;
                            }
                            //if we are partial we need to carry on.
                            if (i == lineRead.length - 1 && !objectFound) {
                                partialObject = object;
                                partialOpenBracket = openBracket;
                                partialCloseBracket = closeBracket;
                            }

                        }

                        Log.d("the buffer is ", "buffer " + new String(buffer).substring(0, charsRead));
                        //each message we receive should be a JSON.  We need to work out the type.
                        InCoreMessage coreMessage = new InCoreMessage(object);


                        Intent messageIntent = new Intent(DemoActivity.MESSAGE_INTENT);
                        messageIntent.putExtra("message", coreMessage.getJsonObject().toString());
                        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                        Log.d("message received", "message received " + coreMessage.getJsonObject().toString());
                        //at this point we could have more than 1 core message here.


                        Acknowledge ack = null;

                        //what are we?
                        if (!coreMessage.getJsonObject().isNull(InCoreMessage.ACKNOWLEDGE)) {

                            ack = new Acknowledge(coreMessage.getJsonObject().getJSONObject(InCoreMessage.ACKNOWLEDGE));

                            /*
                            acknowledges either tell us of a fail (we can log it etc) or tell us of a success and generally a UUID.
                           */

                            if (ack.getState().equals(InCoreMessage.ERROR)) {
                                Log.d("ack error", "error information is " + ack.getInfo());
                            } else {
                                if (ack.getInfo().equals(InCoreMessage.ACTIVE)) {
                                    //we need to wake up our write thread.
                                    if (write != null) {

                                        Log.d("ack error", "trying to wake up thread");

                                        if (connect != null) {
                                            connect.interrupt();
                                        }

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

                            }

                        }

                        messageHandler.handleMessage(coreMessage, ack);

                    }


                } catch (JSONException jse) {
                    Log.d("json exception", "json exception " + jse.toString());
                }

            }


        } catch (IOException e) {

            Log.d("socket listen", "socket listen error " + e.toString());

        }


    }


    public void clearBacklog(){
        this.messageBuffer.clear();
    }


    public void resetConnection(){
        connect = new Thread(new Runnable() {
            @Override
            public void run() {
                reConnect();
            }
        });

        connect.start();
    }


    public void resetLocationUpdates(){

        configuration = new Configuration(dbHelper.getConfigs());

        if(locationUpdates.isAlive()){
            locationUpdates.interrupt();
            locationUpdates = null;
        }

        initLocationUpdates();
    }

    private void initLocationUpdates(){
        locationUpdates = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.parseLong(configuration.getConfig(Configuration.GPS_UPDATE_INTERVAL).getValue()), 0, demoLocationListener);
                    }
                });

            }
        });

        locationUpdates.start();
    }



    private void reConnect() {

        Log.d("reconnect", "reconnect called");
        Intent messageIntent = new Intent(DemoActivity.MESSAGE_INTENT);
        messageIntent.putExtra("message", "Reconnect called");
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);


        try {
            socket.close();
        } catch (Exception e) {

        }
        if (write != null) {
            write.interrupt();
        }

        if (read != null) {
            read.interrupt();
        }

        socket = null;
        write = null;
        read = null;


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


        synchronized (write) {
            try {
                write.wait();
            } catch (InterruptedException ie) {
                Log.d("wait", "wait " + ie.toString());
            } catch (Exception e) {
                Log.d("wait", "wait " + e.toString());
            }

            for (OutCoreMessage coreMessage : messageBuffer) {
                writeToSocket(coreMessage);
            }

            messageBuffer.clear();
            write.interrupt();

        }

        write.start();


    }




    public void writeToSocket(final OutCoreMessage coreMessage) {


        try {

            messageHandler.getSentAcks().put(coreMessage.getMessage().getJSONObject(OutCoreMessage.CORE_OBJECT).getString(OutCoreMessage.ACK_ID), coreMessage);
            if (coreMessage.isPost()) {
                messageHandler.getSentPosts().put(coreMessage.getMessage().getJSONObject(OutCoreMessage.CORE_OBJECT).getString(OutCoreMessage.ACK_ID), coreMessage);
            }

            dOut.write(coreMessage.getMessage().toString().getBytes("UTF-8"));


        } catch (Exception e) {
            Log.d("socket exception", "socket " + e.toString() + coreMessage.getMessage());
            messageBuffer.add(coreMessage);

            connect = new Thread(new Runnable() {
                @Override
                public void run() {
                    reConnect();
                }
            });

            connect.start();

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

            //probably not required due to message issue. todo (TEST)
            connect.interrupt();
            connect = null;


            //set up the two stream readers
            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());


            //now start listening to the socket this is ongoing.
            read = new Thread((new Runnable() {
                @Override
                public void run() {
                    socketListen();
                }
            }));

            read.start();


        } catch (IOException e) {
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
