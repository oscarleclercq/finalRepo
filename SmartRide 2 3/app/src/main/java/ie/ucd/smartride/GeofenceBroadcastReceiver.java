package ie.ucd.smartride;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ie.ucd.smartride.BluetoothService.BluetoothMyLocalBinder;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "debugging";
   /* BluetoothService bluetoothService;
    boolean bluetoothIsBound=false;*/
    /*private int counter = 0;*/

    /*TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("TimerTask", String.valueOf(counter));
            counter++;
        }
    };*/



    @Override
    public void onReceive (Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        final MediaPlayer mp = MediaPlayer.create(context, R.raw.videoplayback);

        /*Timer timer = new Timer();
        timer.schedule(timerTask, 0, 1000);*/

        MainActivity mainActivity = new MainActivity();

        /*BluetoothService bluetoothService = new BluetoothService();
        Toast.makeText(context, "Bluetooth connected...", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(context, BluetoothService.class);
        MainActivity mainActivity = new MainActivity();
        mainActivity.bindService(i, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);

        String message_start = "90!";
        byte[] send_start = message_start.getBytes();
        Log.d(TAG, "byte: " + send_start);*/

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "Enter log");

                mp.start();

                /*mainActivity.onItemClick(AdapterView, View, int,
                long);*/

                /*bluetoothService.write(send_start);*/

                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                MainActivity.OffData();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "90!", MapsActivity.class);
                break;
            /*case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
                break;*/
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "Exit  log");

                mp.start();

                MainActivity.OnData();

                //bluetoothService.write(send_exit);*/

                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                break;
        }

    }

    /*public ServiceConnection bluetoothServiceConnection = new ServiceConnection(){
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

    };*/
}



