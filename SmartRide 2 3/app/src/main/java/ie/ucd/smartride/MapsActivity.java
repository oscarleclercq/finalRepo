/*
 * Class Name: MapsActivity.java
 * Corresponding layout: No
 * Author: Oscar Leclercq
 * Description: MapsActivity is the central activity for the geofencing system. It sets up the view
 * of the map and shows the geofence and user's location visually. It also connects to the geofencing
 * service and sets the parameters of the geofence. Finally, it sets the motor speed to default on
 * launch (if not yet in the geofence), which will then be reduced when entering the geofence.
 * */

package ie.ucd.smartride;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ie.ucd.smartride.databinding.ActivityMapsBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //Initialise tags for log messages for debugging
    public static final String TAG = "debugging";

    //initialise the google maps provider
    public GoogleMap mMap;
    public ActivityMapsBinding binding;

    //initialise connection to the geofencing system
    public GeofencingClient geofencingClient;
    public GeofenceHelper geofenceHelper;

    //Set the radius of the geofence (because circle shape is being used)
    public float GEOFENCE_RADIUS = 210;
    //Give this geofence a name
    public String GEOFENCE_ID = "Imperial College Geofence";

    //This permits google maps to access fine location using the device's hardware
    public int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;

    //onCreate is run when the map is launched
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debugging", "launched");
        super.onCreate(savedInstanceState);

        //Set the layout of the maps activity to a standard google maps view
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialise geofencing system with google maps services
        geofencingClient = LocationServices.getGeofencingClient(this);
        //initialise the GeofenceHelper activity
        geofenceHelper = new GeofenceHelper(this);
    }

    /*
     * onMapReady manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we add the marker and circle around it for the geofence, and move the camera to it.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //target is the target spot to make a circular geofence around
        LatLng target = new LatLng(51.49913, -0.17428); //This is the latitude and longitude of Imperial College
        //add a marker here
        addMarker(target);
        //add a circle with predefined radius around this marker
        addCircle(target, GEOFENCE_RADIUS);

        //Move the camera to this spot and set a zoom level of 16 on launch
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 16));

        //make a geofence with the same shape, size, and location as the visual one displayed for the user, full details of method shown below
        addGeofence(target, GEOFENCE_RADIUS);

        //Call for user's location (this will prompt the user to accept location tracking in this app if this has not yet been done
        enableUserLocation();
    }

    //enableUserLocation method is set here
    public void enableUserLocation() {
        //check if permission has already been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .ACCESS_FINE_LOCATION)) {
                //show dialog to ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    //if location has now been requested, onRequestPermissionResult checks it is now granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We don't have permission, the user's location will not be displayed on the map view, and geofencing will not work.
            }
        }
    }

    //This method initialises the geofence and sets it up to trigger
    public void addGeofence(LatLng latLng, float radius) {
        //This is where the types of transition we want access to is established (enter, exit, dwell)
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        //if location has not been granted, this will not run
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //geofenceingClient adds the geofence and makes a listener, which will trigger when a transition happens
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
                    //if this works
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                        //a toast confirms the geofence
                        Toast.makeText(getApplicationContext(), "Geofence added...", Toast.LENGTH_SHORT).show();
                        //The motor is turned on. As with geofence transitions explained in GeofenceBroadcastReceiver, the OnData method from the MainActivity class is called here as the geofence is successfully made, to set the motor on as default
                        MainActivity.OnData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    //If the geofence could not be set up for whatever reason
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                        //This is reported and the geofence will not work
                    }
                });
    }

    //addMarker and addCircle are the methods called when the map is alunched to visually display
    //the geofence in the map view
    public void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }
    public void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
}