/*
* Class Name: MainActivity.java
* Corresponding layout: activity_main.xml
* Author: Shaun Sweeney - shaun.sweeney@ucdconnect.ie // shaunsweeney12@gmail.com
* Date: March 2017
* Description: MainActivity is the launcher activity. It displays a combined list of bluetooth devices
* that are within range and devices that have already been paired with on the home screen. It also
* has a number of buttons which are used to launch other activities.
* */


package ie.ucd.smartride;

import android.app.Activity;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.os.IBinder;
import android.content.ServiceConnection;
import android.content.ComponentName;

import java.util.Arrays;
import java.util.List;

import ie.ucd.smartride.BluetoothService.BluetoothMyLocalBinder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnItemClickListener {

    public static final String tag = "debugging";
    BluetoothService bluetoothService;
    boolean bluetoothIsBound=false;
    ArrayAdapter<String> activityListAdapter;
    ListView listView;
    IntentFilter filter;

    Handler handler = new Handler();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        //start service for bluetooth connection to retrieve bluetooth devices
        Intent i = new Intent(this, BluetoothService.class);
        bindService(i, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);

        //register broadcast receiver to produce list of all available devices for Bluetooth connection
        registerDeviceReceiver();


    }

    //init initialises variables especially the Adapter to store all devices found by Bluetooth
    public void init() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        activityListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(activityListAdapter);

        /*List<String> rampUp = new ArrayList<String>();
        rampUp.add("90!");
        rampUp.add("100!");
        rampUp.add("110!");
        rampUp.add("120!");
        rampUp.add("130!");*/
    }

    //this method registers the BroadcastReceiver to listen to BluetoothService for the devices
    // that are available/paired so they can be printed to the screen for user selection
    public void registerDeviceReceiver() {
        filter = new IntentFilter("ie.ucd.smartride");
        registerReceiver(MyReceiver, filter);
    }

    //BroadcastReceiver listens for available devices from BluetoothService
    public final BroadcastReceiver MyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceDataReceived = intent.getStringExtra("device");
            activityListAdapter.add(deviceDataReceived);
        }
    };

    //bluetoothServiceConnection is required for Bluetooth to work in the background
    public ServiceConnection bluetoothServiceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            BluetoothMyLocalBinder binder  = (BluetoothMyLocalBinder) service;
            bluetoothService = binder.getService();
            bluetoothIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            bluetoothIsBound = false;
        }

    };

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//
//        //bluetoothService.onDestroy();
//    }

    //method to manage what happens when user clicks one of the devices that have been found by bluetooth
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {

        if (activityListAdapter.getItem(arg2).contains("Paired")) {
            //Log.i(tag, "Checking if device " + activityListAdapter.getItem(arg2) + " is paired.");
            // Take the address out of the string
            String deviceAddress = activityListAdapter.getItem(arg2).split("[\\r\\n]")[1];
            Log.i(tag, "Device address clicked is " + deviceAddress);

            //bluetoothService.connectToPairedDevice(deviceAddress);
            bluetoothService.checkifPaired(arg2);
        } else {
            Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
        }
    }

    /* The following methods launch different activities depending on the user selection*/

    /*// Method to launch activity to send manual command to bike
    public void goToSendCommand(View view){
        Log.i(tag, "About to launch SendCommand");
        Intent i = new Intent(this, SendCommand.class);
        startActivity(i);
    }

    // Method to launch activity to view data saved in database
    public void goToDb(View view){
        Log.i(tag, "Launching database activity");
        Intent j = new Intent(this, ViewData.class);
        startActivity(j);
    }

    //method to launch activity to start closed loop feedback control for target calories burned
    public void startCaloriesActivity(View view){
        Log.i(tag, "Launching calories activity");
        Intent k = new Intent(this, MicrosoftBand.class);
        startActivity(k);
    }

    //method to launch activity to view data saved in the calories control feedback
    public void ViewCaloriesControlActivity(View view){
        Log.i(tag, "Launching view cals control activity");
        Intent m = new Intent(this, ViewCaloriesControlData.class);
        startActivity(m);
    }

    //method to launch activity to minimise cyclist inhalation of pollutants
    public void StartPollutionControlActivity(View view){
        Log.i(tag, "Launching proactive pollution control activity");
        Intent m = new Intent(this, ProactivePollutionControl.class);
        startActivity(m);
    }*/

//    public void startTrafficLightNudgingControlActivity(View view){
//        Log.i(tag, "Launching TrafficLightNudgingControl activity");
//        Intent n = new Intent(this, TrafficLightNudgingControl.class);
//        startActivity(n);
//    }

    public void startCooperativeCompetitiveControlActivity(View view){
        Log.i(tag, "Launching CooperativeCompetitiveControl activity");
        Intent n = new Intent(this, CooperativeCompetitiveControl.class);
        startActivity(n);
    }

    /*List<String> rampUp1 = Arrays.asList("90!", "100!", "110!", "120!", "130!");*/


    /*for (int a = 1; a<=all.length ;a++) {
        handler1.postDelayed(new Runnable() {

            @Override
            public void run() {
                ImageButton btn5 = all[random.nextInt(all.length)];
                btn5.setBackgroundColor(Color.RED);
            }
        }, 1000 * a);
    }
}*/
    public Runnable startMotorFast = new Runnable() {
        /*for (int i = 1; i<=rampUp.size(); i ++){

        }*/
        @Override
        public void run() {
            // Do something here on the main thread
            Log.i(tag, "Motor On");
            String message = "150!";
            byte[] send = message.getBytes();
            bluetoothService.write(send);
            Log.d("Handlers", "Called on main thread");
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(startMotorFast, 1000);

        }
    };

    public Runnable startMotorSlow = new Runnable() {
        /*for (int i = 1; i<=rampUp.size(); i ++){

        }*/
        @Override
        public void run() {
            // Do something here on the main thread
            Log.i(tag, "Motor On");
            String message = "130!";
            byte[] send = message.getBytes();
            bluetoothService.write(send);
            Log.d("Handlers", "Called on main thread");
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(startMotorSlow, 1000);

        }
    };

    public void MotorOn(View view){
        /*Log.i(tag, "Motor On");
        String message = "155!";
        byte[] send = message.getBytes();
        bluetoothService.write(send);*/
        handler.post(startMotorFast);
        handler.removeCallbacks(startMotorSlow);
    }

    public void MotorSlow(View view){
        /*Log.i(tag, "Motor On");
        String message = "155!";
        byte[] send = message.getBytes();
        bluetoothService.write(send);*/
        handler.post(startMotorSlow);
        handler.removeCallbacks(startMotorFast);
    }

    public void MotorOff(View view){
        Log.i(tag, "Motor Off");
        String message = "0!";
        byte[] send = message.getBytes();
        bluetoothService.write(send);
        handler.removeCallbacks(startMotorFast);
        handler.removeCallbacks(startMotorSlow);
    }

    public void MapLaunch(View view){
        Log.i(tag, "Launching Map activity");
        Intent n = new Intent(this, MapsActivity.class);
        startActivity(n);
    }

    public static void OnData() {
        Log.i(tag, "Sending Data");
        String message = "90!";
        byte[] send = message.getBytes();
        BluetoothService.write(send);
    }

    public static void OffData() {
        Log.i(tag, "Sending Data Off");
        String message = "0!";
        byte[] send = message.getBytes();
        BluetoothService.write(send);
    }

    public void Noise(View view){
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videoplayback);
        mp.start();
    }

    /*@Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        if(i.getStringExtra("methodName").equals("myMethod")){
            MotorOn();
        }
    }*/
}