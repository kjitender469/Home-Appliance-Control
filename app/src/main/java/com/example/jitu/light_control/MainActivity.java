package com.example.jitu.light_control;

import android.content.Intent;
import android.os.Parcelable;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private static final int SERVER_PORT = 12345;
    public MySocketClient mySocketClient=null;
    String serverIP = "192.168.43.1";

    String serverURI = "";

    Button bt_connect,bt_light_1,bt_light_2,bt_light_3;
    TextView tv_connection_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_connect = (Button)findViewById(R.id.button_connect);
        bt_light_1 = (Button)findViewById(R.id.button_light_1);
        bt_light_2 = (Button)findViewById(R.id.button_light_2);
        bt_light_3 = (Button)findViewById(R.id.button_Light_3);

        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connectToServer();

            }
        });

        bt_light_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySocketClient.send("L1");
            }
        });

        bt_light_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySocketClient.send("L2");
            }
        });

        bt_light_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySocketClient.send("L3");
            }
        });
    }

    public void connectToServer()
    {
        URI uri = null;
        try {
            uri = new URI("ws://192.168.4.1:8080/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mySocketClient = new MySocketClient(uri,new Draft_17());
        mySocketClient.connect();
    }

    public class MySocketClient extends WebSocketClient
    {
        MainActivity mainActivity=new MainActivity();
        public MySocketClient(URI serverURI, Draft_17 draft_17) {
            super(serverURI,draft_17);
            Log.wtf("Light_Control","I am Here_1");
        }


        //Log.wtf("Light_Control","I am Here_1");

        @Override
        public void onOpen(ServerHandshake handshakedata) {

            Log.wtf("Light_Control","I am Here_2");
            tv_connection_status.setText("Connected");
            tv_connection_status.setTextColor(0xFF087124);
        }

        @Override
        public void onMessage(String message) {
            Log.wtf("Light_Control","I am Here_3"+mySocketClient);

        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.wtf("Light_Control","I am Here_4 code is : "+code);
            Log.wtf("Light_Control","I am Here_4 reason is : "+reason);
            Log.wtf("Light_Control","I am Here_4 remote is : "+remote);
        }

        @Override
        public void onError(Exception ex) {
            Log.wtf("Light_Control","I am Here_5");
            Log.wtf("Light_Control","Error is : "+ex);
        }
    }
}
