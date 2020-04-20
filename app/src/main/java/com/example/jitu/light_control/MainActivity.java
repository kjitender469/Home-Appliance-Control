package com.example.jitu.light_control;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SERVER_PORT = 12345;
    public MySocketClient mySocketClient=null;
    String serverIP = "192.168.4.1";

    String serverURI = "";

    Button bt_connect,bt_light_1,bt_light_2,bt_light_3;
    TextView tv_connection_status;

    WifiManager wifiManager;
    String ssidConnectedTo="";
    private int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_connect = (Button)findViewById(R.id.button_connect);
        bt_light_1 = (Button)findViewById(R.id.button_light_1);
        bt_light_2 = (Button)findViewById(R.id.button_light_2);
        bt_light_3 = (Button)findViewById(R.id.button_Light_3);

        /*wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
        wifiManager.startScan();*/

        //Log.wtf("Light_Control WiFi","WiFi debug onCreate() : "+wifiManager.startScan());

        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        //Log.wtf("WiFi Status","WiFi Status E Light Point status initial SSID is : "+wifiManager.);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                        Log.wtf("WiFi Status","WiFi Status E Light Point status initial SSID is : "+wifiInfo.getSSID());
                        Log.wtf("WiFi Status","WiFi Status E Light Point status initial BSSID is : "+wifiInfo.getBSSID());
                        Log.wtf("WiFi Status","WiFi Status E Light Point status initial networkID is : "+wifiInfo.getNetworkId());

                        if(!wifiManager.isWifiEnabled())
                        {
                            Log.wtf("WiFi state","Wifi is disabled");
                            wifiManager.setWifiEnabled(true);
                        }

                        statusCheck();

                        Log.wtf("WiFi state","Wifi is disabled--------------- : "+wifiManager.isWifiEnabled());

                        if(wifiManager.isWifiEnabled())
                        {
                            Log.wtf("WiFi state","Wifi is alrady enabled");
                            /*
                             * This block of code is to turn on the GPS.
                             * From android 8.0 onwards we wont be getting SSID of the connected
                             * network unless GPS is turned on.
                             * */
                            String provider = Settings.Secure.getString(getContentResolver(), Settings.ACTION_LOCALE_SETTINGS);
                            if(!provider.contains("gps"))
                            { //if gps is disabled
                                final Intent poke = new Intent();
                                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                                poke.setData(Uri.parse("3"));
                                sendBroadcast(poke);
                            }



//                            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                                    != PackageManager.PERMISSION_GRANTED) {
//                                // Permission is not granted
//                            }
//
//                            List locnProviders = null;
//                            try {
//                                LocationManager lm =(LocationManager)getApplicationContext().getSystemService(Activity.LOCATION_SERVICE);
//                                locnProviders = lm.getProviders(true);
//
//                                //return (locnProviders.size() != 0);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                if (BuildConfig.DEBUG) {
//                                    if ((locnProviders == null) || (locnProviders.isEmpty()))
//                                        Log.wtf("WiFi state", "Location services disabled");
//                                    else
//                                        Log.wtf("WiFi state", "locnProviders: " + locnProviders.toString());
//                                }
//                            }
//                            //return(false);



                            ssidConnectedTo = wifiInfo.getSSID();

                            Log.wtf("WiFi state","Name of ssid connected to is : "+ssidConnectedTo+" networkId is : "+wifiInfo.getNetworkId()+" BSSID is : "+wifiInfo.getBSSID());
                        }

                        String networkSSID = "Jitu";
                        String networkPass = "123456789";

                        WifiConfiguration conf = new WifiConfiguration();
                        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
                        conf.preSharedKey = "\""+ networkPass +"\"";

                        wifiManager.addNetwork(conf);
                        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                        for( WifiConfiguration i : list )
                        {
                            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\""))
                            {
                                wifiManager.disconnect();
                                wifiManager.enableNetwork(i.networkId, true);
                                wifiManager.reconnect();

                                break;
                            }
                        }

                        try
                        {
                            Log.wtf("WiFi state","Process is sleep for 1 sec");
                            Thread.sleep(2000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }


                        //WifiManager wifiManager_2 = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiInfo = wifiManager.getConnectionInfo();

                        Log.wtf("WiFi Status","WiFi Status E Light Point status final SSID is : "+wifiInfo.getSSID());
                        Log.wtf("WiFi Status","WiFi Status E Light Point status final BSSID is : "+wifiInfo.getBSSID());
                        Log.wtf("WiFi Status","WiFi Status E Light Point status final networkID is : "+wifiInfo.getNetworkId());

                        int id = wifiInfo.getNetworkId();

                        Log.wtf("WiFi state","Name of ssid connected to is : "+ssidConnectedTo+" networkId is : "+wifiInfo.getNetworkId()+" BSSID is : "+wifiInfo.getBSSID());

                        Log.wtf("WiFi state","Light Point Network Wifi network id is : "+id);

                        ssidConnectedTo = "";
                        ssidConnectedTo = wifiInfo.getSSID();
                        Log.wtf("WiFi state","Jitu Light Point Network Wifi network id is : "+ssidConnectedTo);
                        String newSSID_connectedTo = ssidConnectedTo.substring(1,5);
                        Log.wtf("WiFi state","Light Point Network newSSID_connectedTo Wifi is connected to : "+newSSID_connectedTo);

                        if(newSSID_connectedTo.equals(networkSSID)) {

                            Log.wtf("WiFi state","Jitu Light Point Network Wifi is connected to : "+newSSID_connectedTo+" successfully");
                            //connectButton.setBackgroundResource(R.drawable.ic_connect_button_blue);

                            ssidConnectedTo="";

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(getApplicationContext(), "WiFi Connected", Toast.LENGTH_SHORT).show();
                                }
                            });

                            try
                            {
                                Log.wtf("WiFi state","Process is sleep for 1 sec");
                                Thread.sleep(600);
                            } catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }

                            connectToServer();

                            //startActivity(new Intent(MainActivity.this, RemoteControl.class));
                        }
                        else
                        {

                            Log.wtf("WiFi state","Light Point Network Wifi is connected to after : "+ssidConnectedTo);
                            Log.wtf("WiFi state","Light Point Network M to else me hu abhi ");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(getApplicationContext(), "WiFi not Connected", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                //connectToWiFi();
                //connectToServer();

            }
        });

        bt_light_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySocketClient.send("L1_On");
            }
        });

        bt_light_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySocketClient.send("L1_Off");
            }
        });

        bt_light_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySocketClient.send("L3");
            }
        });
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void connectToWiFi() {
//        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);



        if (!wifiManager.isWifiEnabled()) {
            // Turn on WiFi
            wifiManager.setWifiEnabled(true);
        }

        //


    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // Do something with granted permission
//            //.getScanningResults();
//        }
//    }

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

/*    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
//            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
//            if (success) {
//                scanSuccess();
//            } else {
//                // scan failure handling
//                scanFailure();
//            }

            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                // Write your logic to show in the list
                Log.wtf("Light_Control WiFi","WiFi debug I am Here_1 inside successScan() --- result is : "+scanResults.toString());
            }

        }
    };*/



    /*boolean success = wifiManager.startScan();
        if (!success) {
        // scan failure handling
        scanFailure();
    }*/

    private void scanSuccess() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//            List<ScanResult> results = wifiManager.getScanResults();
//            Log.wtf("Light_Control WiFi","WiFi debug I am Here_1 inside successScan() --- result is : "+results.toString());
//        }else{
//            //getScannp;.ingResults();
//            //do something, permission was previously granted; or legacy device
//        }
        List<ScanResult> results = wifiManager.getScanResults();
        Log.wtf("Light_Control WiFi","WiFi debug I am Here_1 inside successScan() --- result is : "+results.toString());
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        Log.wtf("Light_Control WiFi","WiFi debug I am Here_1 inside scanFailure() --- old result is : "+results);

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
