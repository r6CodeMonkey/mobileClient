package oddymobstar.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URI;

import oddymobstar.crazycourier.R;

/**
 * Created by root on 28/12/14.
 */
public class EchoActivity extends FragmentActivity{


    private Socket socket;


    public EchoActivity(){

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.echo_layout);

        Thread t = new Thread(new Runnable(){
           public void run(){
               connectSocket();
           }

       });

        t.start();

      //  connectWebSocket();

      /*  EditText et = (EditText)this.findViewById(R.id.echo_text);

        et.setO
        */
    }

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





}
