/*
 * Class Name: GeofenceBroadcastReceiver.java
 * Corresponding layout: No
 * Author: Oscar Leclercq
 * Description: GeofenceBroadcastReceiver receives a set of data when the geofence is triggered.
 * It extracts information from that data to establish what kind of geofence transition it was.
 * It uses that data to send toasts and notifications, play the sound, and call methods relating to
 * running the bike's motor at a given speed according to the type of transition.
 * */

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
    //initialise log tag for debugging
    public static final String TAG = "debugging";

    //OnReceive is called when the BroadcastReceiver is receiving an intent broadcast
    @Override
    public void onReceive (Context context, Intent intent) {
        //Toasts are displayed onscreen in the map activity, this one confirms the geofence has been triggered
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        //Initialise the notification helper which will enable sending notifications of what sort of geofence trigger this was
        NotificationHelper notificationHelper = new NotificationHelper(context);

        //This line asks for details of the geofencing event that was triggered from the broadcastreceiver
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        //Initialise the mediaplayer, like in mainactivity, to play sounds when entering or exiting the geofence
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.videoplayback);

        //error situation stops the geofencing trigger
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        //This list is initialised then called to inform which geofence has been triggered in the log
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        //The transition type (enter, exit, dwell) is extracted from the geofencingEvent data
        int transitionType = geofencingEvent.getGeofenceTransition();

        //the switch runs a different set of code depending on which transition type was received
        switch (transitionType) {
            //If the transition type is enter
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "Enter log");

                //play the sound
                mp.start();

                //write a toadt
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();

                //Trigger the OffData method straight from MainActivity (made possible because it is static, so accessible directly from other activities)
                MainActivity.OffData();

                //Send a notification using notificationHelper
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "90!", MapsActivity.class);
                break;

            //The Dwell case can be used to trigger something when the device is in the geofence for more than 5 seconds.
            //For this app and the features developed, this was not needed
            /*case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
                break;*/

            //The Exit case runs the same as Enter, it also plays the sound, but triggers the OnData method from MainActivity
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "Exit  log");
                mp.start();
                MainActivity.OnData();
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                break;
        }
    }
}



