/*
* Class Name: MainActivity.java
* Corresponding layout: activity_main.xml
* Author: Oscar Leclercq
* Description: MainActivity is the launcher activity. It displays a combined list of bluetooth devices
* that are within range and devices that have already been paired with on the home screen. It also
* has a number of buttons which are used to launch the map activity (and thus the geofencing system),
* set the motor speed, and play the sound for user testing.
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

import ie.ucd.smartride.BluetoothService;
import ie.ucd.smartride.BluetoothService.BluetoothMyLocalBinder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnItemClickListener {
    //initiate log filter for debugging
    public static final String tag = "debugging";

    //initiate the bluetooth service
    BluetoothService bluetoothService;
    boolean bluetoothIsBound=false;

    //initiate lists to display paired devices
    ArrayAdapter<String> activityListAdapter;
    ListView listView;
    IntentFilter filter;

    //initiate handler for timing and repeating motor power values
    Handler handler = new Handler();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        //set layout to that defined by activity_main.xml
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
        //onclicklistener triggers connection once the device is clicked
        listView.setOnItemClickListener(this);
        activityListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0);
        listView.setAdapter(activityListAdapter);
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

    //onItemClick manages what happens when user clicks one of the devices that have been found by bluetooth
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {

        //check which devices are paired
        if (activityListAdapter.getItem(arg2).contains("Paired")) {
            //Take the address out of the string
            String deviceAddress = activityListAdapter.getItem(arg2).split("[\\r\\n]")[1];
            Log.i(tag, "Device address clicked is " + deviceAddress);

            //bluetoothService.connectToPairedDevice(deviceAddress);
            bluetoothService.checkifPaired(arg2);
        } else {
            Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
        }
    }

    //Cooperativecompetitive control is one of the functions used in previous projects, the button launching it has been removed for this project
    public void startCooperativeCompetitiveControlActivity(View view){
        Log.i(tag, "Launching CooperativeCompetitiveControl activity");
        Intent n = new Intent(this, CooperativeCompetitiveControl.class);
        startActivity(n);
    }

    //this method runs the motor on fast speed, and sets it up with a handler to be sent every second while running to keep running if brakes are pressed, after they are released
    public Runnable startMotorFast = new Runnable() {
        @Override
        public void run() {
            Log.i(tag, "Motor On");
            //Fast motor speed defined here as 150, the ! is used by the arduino to know when the string of digits has ended (hardcoded in the arduino code)
            String message = "150!";
            //byte converts the message to bytes
            byte[] send = message.getBytes();
            //the bluetooth service is then called to send the message to the connected device
            bluetoothService.write(send);
            Log.d("Handlers", "Called on main thread");
            // Repeat this the same runnable code block again every second
            handler.postDelayed(startMotorFast, 1000);
        }
    };

    public Runnable startMotorSlow = new Runnable() {
        //same as startMotorFast but with the decided slower speed at 130
        @Override
        public void run() {
            Log.i(tag, "Motor On");
            String message = "130!";
            byte[] send = message.getBytes();
            bluetoothService.write(send);
            Log.d("Handlers", "Called on main thread");
            handler.postDelayed(startMotorSlow, 1000);

        }
    };

    //MotorOn is called when the MotorOn button is pressed, to set the motor speed to fast
    public void MotorOn(View view){
        //call startMotorFast handler and stop any other handlers that could interfere (startMotorSlow)
        handler.post(startMotorFast);
        handler.removeCallbacks(startMotorSlow);
    }

    //MotorSlow is the same as MotorOn, but for the slow speed
    public void MotorSlow(View view){
        handler.post(startMotorSlow);
        handler.removeCallbacks(startMotorFast);
    }

    //MotorOff is called by the MotorOff button, it stops the motor and stops any handlers that would restart it
    public void MotorOff(View view){
        Log.i(tag, "Motor Off");
        //set new motor speed string to 0
        String message = "0!";
        byte[] send = message.getBytes();
        //send it to the motor with bluetoothService
        bluetoothService.write(send);
        //stop any handlers still running
        handler.removeCallbacks(startMotorFast);
        handler.removeCallbacks(startMotorSlow);
    }

    //OnData runs similarly to MotorOn, but this has to be a separate method as it must be static to be called from GeofenceBroadcastReceiver
    //This method is called when launching the map or exiting a Geofence, with OffData below being called when entering the geofence
    public static void OnData() {
        Log.i(tag, "Sending Data");
        String message = "150";
        byte[] send = message.getBytes();
        BluetoothService.write(send);
    }

    //OffData is used when entering the geofence, currently set to stop the motor entirely for demonstration purposes, but could also be set to just a lower speed than OnData
    public static void OffData() {
        Log.i(tag, "Sending Data Off");
        String message = "0!";
        byte[] send = message.getBytes();
        BluetoothService.write(send);
    }

    //The Noise method is called when pressing the noise button
    public void Noise(View view){
        //initialise a mediaplayer and connected to the videoplayback file, which is the sound used for warnings
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.videoplayback);
        //start the sound
        mp.start();
    }

    //MapLaunch is triggered when the Map button is pressed on the home page
    public void MapLaunch(View view){
        Log.i(tag, "Launching Map activity");
        //Intents are used when 2 activities will need to continue communicating, which is the case for MapsActivity
        Intent n = new Intent(this, MapsActivity.class);
        //Launch the activity
        startActivity(n);
    }
}