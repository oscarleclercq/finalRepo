package ie.ucd.smartride;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

public class CooperativeCompetitiveControl extends AppCompatActivity {
    private static final String tag = "debugging";
    public static final int SEND_TO_BIKE = 1;
    public static final int PLOTTING_DATA = 2;
    private final String TAG = "CCControl";
    private DataExchange dataExchange;
    private float lastRequestToMotor;
    private float motorPowerTarget;
    IntentFilter bikeDataFilter;
    IntentFilter commandSentFilter;

    // UI
    // Plotting
    LineChart cooperationCharacteristicChart;
    LineChart mValueChart;
    TextView humanOutputPowerTextView;
    TextView motorReferencePowerTextView;
    TextView motorActualPowerTextView;
    boolean plottingEnabled = true;

    ToggleButton toggleCoopPlotEnable;
    ToggleButton toggleMValuePlotEnable;
    boolean plotCoopEnable;
    boolean plotMValEnable;

    List motorPowerList = Collections.synchronizedList(new LinkedList());
    List humanPowerList = Collections.synchronizedList(new LinkedList());

    // Changing m functionality
//    final float INITIAL_M_TEST_VALUE = (float)0.3;
//    final float FINAL_M_TEST_VALUE = (float)0.6;
//    final float TRANSITION_TIME = 5; // seconds

    Switch changeMSwitch;
    boolean changeMEnabled=true;
    ToggleButton toggleMStarButton;
    boolean mStarToggleButtonEnabled;
    boolean toggleValueUpper;
//    float mStar = INITIAL_M_TEST_VALUE;
    float mStar = 0.75f;

    private LineDataSet mTargetDataSet;
    private LineDataSet mActualDataSet;

    // Control algorithm
    private int mainSamplingPeriod = 2500; // milliseconds - every 10 seconds, the algorithm should calculate a new setpoint for motor power
    private int secondarySamplingPeriod = 1000; //millieseconds - every 1 second, the requested value sent to the motor should try to converge to the last setpoint
    private Timer mainControlTimer;
    private Timer secondaryControlTimer;

    // BluetoothService
    BluetoothService bluetoothService;
    private boolean isBound;

    PathsenseLocationProviderApi api;
    PathsenseGeofenceDemoGeofenceEventReceiver geofenceEventReceiver;

    // Database
    MyDBHandler dbHandler;
    DatabaseService databaseService;

    //simulated data
    private boolean USE_SIMULATED_DATA=false;
    private int simulated_count = 0;
    private int main_iteration_count=0;
//    int[] simulatedHumanPowerData = new int[]{100, 100, 100, 100, 100, 100, 100, 100, 200, 200, 200, 200, 200, 200, 200, 200};
//    int[] simulatedHumanPowerData = new int[]{50, 50, 50, 70, 70, 70, 90, 90, 90, 110, 110, 130, 130, 140, 140, 150, 155, 175, 175, 175, 185, 195, 195, 215, 215, 225, 225, 235, 235, 235, 235, 255, 255, 270, 290, 300, 310, 320, 300, 280, 260, 245, 230, 230, 230, 270, 290, 310, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 10000000, 10000000, 10000000};
    int[] simulatedHumanPowerData = new int[]{50, 50, 50, 70, 70, 70, 90, 90, 90, 110, 110, 130, 130, 140, 140, 150, 155,  290, 300, 310, 320, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000,10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000, 10000000};
   //1 min 30 secs at high level: float[] changeMArray = new float[]{0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.725f,0.7f,0.675f,0.65f,0.625f,0.6f,0.575f,0.55f,0.525f,0.5f,0.475f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f};
   float[] changeMArray = new float[]{0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.75f,0.725f,0.7f,0.675f,0.65f,0.625f,0.6f,0.575f,0.55f,0.525f,0.5f,0.475f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f};
    // float[] changeMArray = new float[]{0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f,0.45f};




//    int[] range = IntStream.rangeClosed(1, 10).toArray();
//    int[] simulatedHumanPowerData = int[] range = IntStream.iterate(1, n -> n + 1).limit(10).toArray();;


  //  int[] simulatedHumanPowerData = new int[]{140, 140, 140, 140, 140, 140, 160, 160, 160, 160, 160, 180, 180, 180, 180, 200, 200};
  //  int[] simulatedHumanPowerData = new int[]{140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140, 140};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooperative_competitive_control);

        // Start service for bluetooth connection to use BluetoothService methods
        Intent i = new Intent(this, BluetoothService.class);
        bindService(i, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);

        dbHandler = new MyDBHandler(this, null, null, 1);

        // Start thread to start syncing data from bike
        Intent j = new Intent(this, DatabaseService.class);
        bindService(j, databaseServiceConnection, Context.BIND_AUTO_CREATE);

        //api = PathsenseLocationProviderApi.getInstance(this);
        //api.addGeofence("MYGEOFENCE", 53.291377, -6.179869, 100, PathsenseGeofenceDemoGeofenceEventReceiver.class);

//        Log.i(TAG, "Before control loops");
        // start primary control loop - this calculates a new target motor power setpoint once every mainSamplingPeriod
        mainControlTimer = new Timer();
        mainControlTimer.schedule(new CooperativeCompetitiveControlTask(mainSamplingPeriod), 0, mainSamplingPeriod);

//        Log.i(TAG, "Middle of control loops");

        // start secondary control loop - this tries to converge the actual motor power to the target motor power by updating the request to the motor once every secondarySamplingPeriod
       //commented out March 2021 so primary control loop can be debugged
        secondaryControlTimer = new Timer();
        secondaryControlTimer.schedule(new ConvergeToTargetMotorPowerControlTask(), 2000, secondarySamplingPeriod);

//        Log.i(TAG, "After control loops");


        // for exchanging data between sensors and control loops to avoid a high number of calls to database to retrieve data
        dataExchange = new DataExchange();
        //provide initial vallues in case last simulation was bad / generate initial conditions
        //
        if(USE_SIMULATED_DATA == true){
            dataExchange.setMotorPowerTarget(100);
            dataExchange.setRecentMotorPower(100);
        }
        dataExchange.setPreviousMotorPowerTarget(100);
        registerReceivers();

        // Initialise the charts in the activity
//        cooperationCharacteristicChart = findViewById(R.id.cooperationCharacteristicChart);
//
//        configureButtons();
//        configureCharacteristicGraph();
//        configureMValueGraph();
    }

    @Override
    protected void onPause() {
        plottingEnabled = false;

        mainControlTimer.cancel();

        super.onPause();
    }

    @Override
    protected void onResume() {
        plottingEnabled = false;

       // mainControlTimer.schedule(new CooperativeCompetitiveControlTask(mainSamplingPeriod), 0, mainSamplingPeriod);

        super.onResume();
    }

    /**
     * Method to configure the layout of the cooperation characteristic graph
     */
    private void configureCharacteristicGraph() {
        cooperationCharacteristicChart = findViewById(R.id.cooperationCharacteristicChart);
        cooperationCharacteristicChart.setPinchZoom(true);
        cooperationCharacteristicChart.setBackgroundColor(Color.LTGRAY);
        cooperationCharacteristicChart.getAxisRight().setEnabled(false);
        cooperationCharacteristicChart.setDescription(null);

        YAxis yAxisLeft = cooperationCharacteristicChart.getAxisLeft();
        yAxisLeft.setAxisMaximum(400f);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setLabelCount(10);
        yAxisLeft.setDrawAxisLine(true);

        XAxis xAxis = cooperationCharacteristicChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(400f);
        xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(8);
        xAxis.setDrawLabels(true);

        Legend mValueLegend = cooperationCharacteristicChart.getLegend();
        mValueLegend.setWordWrapEnabled(true);
        mValueLegend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        mValueLegend.setForm(Legend.LegendForm.SQUARE);
    }

    /**
     * Method to configure the layout of the m-value graphs
     */
    private void configureMValueGraph() {
        humanOutputPowerTextView = findViewById(R.id.humanOutputPowerTextView);
        humanOutputPowerTextView.setText("Human output: none");
        humanOutputPowerTextView.setTextColor(Color.BLACK);

        motorReferencePowerTextView = findViewById(R.id.motorReferenceOutput);
        motorReferencePowerTextView.setText("Motor reference: none");
        motorReferencePowerTextView.setTextColor(Color.BLACK);

        motorActualPowerTextView = findViewById(R.id.motorActualOutput);
        motorActualPowerTextView.setText("Motor actual: none");
        motorActualPowerTextView.setTextColor(Color.BLACK);

        mValueChart = findViewById(R.id.mValueChart);
        mValueChart.setPinchZoom(true);
        mValueChart.getAxisRight().setEnabled(false);
        mValueChart.setBackgroundColor(Color.LTGRAY);
        mValueChart.setDescription(null);

        YAxis yAxisLeft = mValueChart.getAxisLeft();
        yAxisLeft.setAxisMaximum(1f);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setLabelCount(5);
        yAxisLeft.setDrawAxisLine(true);

        XAxis xAxis = mValueChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(20f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLabels(true);
        xAxis.setLabelCount(5);

        Legend mValueLegend = mValueChart.getLegend();
        mValueLegend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        mValueLegend.setForm(Legend.LegendForm.SQUARE);

        mTargetDataSet = new LineDataSet(null, "mTarget");
        mTargetDataSet.setColor(Color.CYAN);
        mTargetDataSet.setCircleColor(Color.CYAN);
        mTargetDataSet.setCircleRadius(1f);
        mTargetDataSet.setValueTextColor(Color.TRANSPARENT);

        mActualDataSet = new LineDataSet(null, "mActual");
        mActualDataSet.setColor(Color.BLUE);
        mActualDataSet.setCircleColor(Color.BLUE);
        mActualDataSet.setCircleRadius(1f);
        mActualDataSet.setValueTextColor(Color.TRANSPARENT);
    }

    /**
     * Method to wire up buttons
     */
    private void configureButtons() {
        toggleCoopPlotEnable = findViewById(R.id.coopPlotToggle);
        plotCoopEnable = toggleCoopPlotEnable.isChecked();
        toggleCoopPlotEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    plotCoopEnable = true;
                } else {
                    plotCoopEnable = false;
                }
            }
        });

        toggleMValuePlotEnable = findViewById(R.id.mPlotToggle);
        plotMValEnable = toggleMValuePlotEnable.isChecked();
        toggleMValuePlotEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                plotMValEnable = isChecked;
            }
        });

        changeMSwitch = findViewById(R.id.changeMSwitch);
        changeMEnabled = changeMSwitch.isChecked();
        changeMSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeMEnabled = isChecked;
                toggleMStarButton.setEnabled(isChecked);
            }
        });

//        toggleMStarButton = findViewById(R.id.mStarValueToggle);
//        toggleMStarButton.setEnabled(false);
//        toggleMStarButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                toggleValueUpper = isChecked;
//
////                if(!isChecked){
////                    mStar = INITIAL_M_TEST_VALUE;
////                }
//            }
//        });
    }

    /**
     * Method which takes in data to plot, and appends the new
     * data to the graph, pushing out older values as necessary
     *
     * @param plottingData Class containing the relevant data values that
     *                     are desired to be displayed
     */
    private void updateUIPlots(PlottingData plottingData) {
        if (plotMValEnable) {
            humanOutputPowerTextView.setText("Human output: " + plottingData.getRecentHumanPowerAveraged());
            motorReferencePowerTextView.setText("Motor reference: " + plottingData.getMotorReferencePower());
            motorActualPowerTextView.setText("Motor actual: " + plottingData.getMotorActualPower());

            if (mActualDataSet.getEntryCount() < 20) {
                mActualDataSet.addEntry(new Entry(19, plottingData.getRecentMActual().getMValue()));
                mTargetDataSet.addEntry(new Entry(19, plottingData.getRecentMTarget()));

            } else {
                mActualDataSet.addEntry(new Entry(19, plottingData.getRecentMActual().getMValue()));
                mActualDataSet.removeFirst();

                mTargetDataSet.addEntry(new Entry(19, plottingData.getRecentMTarget()));
                mTargetDataSet.removeFirst();
            }

            // Update the rest
            for (int i = mActualDataSet.getEntryCount() - 1, j = 1; i > 0; i--) {
                mActualDataSet.getEntryForIndex(i - 1).setX(19f - j);
                mTargetDataSet.getEntryForIndex(i - 1).setX(19f - j);
                j++;
            }

            LineData data = new LineData(mActualDataSet, mTargetDataSet);

            mValueChart.setData(data);
            mValueChart.notifyDataSetChanged();
            mValueChart.invalidate();
        }

        // Characteristic plot
        if (plotCoopEnable) {
            List<Entry> characteristicEntries = new ArrayList<>();

            int sizeOfPlottingData = plottingData.getOutputOfCharacteristic().size();

            for (int i = 0; i < sizeOfPlottingData; i++) {
                characteristicEntries.add(new Entry(plottingData.getInputToCharacteristic().get(i), plottingData.getOutputOfCharacteristic().get(i)));
            }

            LineDataSet characteristicDataSet = new LineDataSet(characteristicEntries, "");
            characteristicDataSet.setColor(Color.BLUE);
            characteristicDataSet.setValueTextColor(Color.TRANSPARENT);
            characteristicDataSet.setCircleRadius(1f);
            characteristicDataSet.setCircleColor(Color.BLUE);
            characteristicDataSet.setCubicIntensity(1f);
            characteristicDataSet.setDrawFilled(true);

            LimitLine humanPowerLimitLine = new LimitLine(plottingData.getRecentHumanPowerAveraged(), "");
            humanPowerLimitLine.setLineColor(Color.RED);
            humanPowerLimitLine.setLineWidth(1f);

            cooperationCharacteristicChart.getXAxis().removeAllLimitLines();
            cooperationCharacteristicChart.getXAxis().addLimitLine(humanPowerLimitLine);

            LineData characteristicLineData = new LineData(characteristicDataSet);
            cooperationCharacteristicChart.setData(characteristicLineData);
            cooperationCharacteristicChart.invalidate();
        }
    }

    Handler handler = new Handler(new IncomingHandlerCallback());

    /**
     * Class which is used to take messages from the message queue of this activity???
     */
    class IncomingHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                //CALORIES_DATA case is for when an updated assistance level should be sent to the bike
                //based on feedback
                case SEND_TO_BIKE:
                    Integer requestToBikeInt = (Integer) msg.obj;
                    String requestToSendToBike = requestToBikeInt + "!";
                    //Log.i(TAG, "Request to send to bike is: " + requestToSendToBike); // Useful for debugging
                    write(requestToSendToBike);
                    break;
                case PLOTTING_DATA:
                    if (plottingEnabled) {
                        PlottingData plottingData = (PlottingData) msg.obj;
                        updateUIPlots(plottingData);
                    }
                    break;
                default:
                    Log.i(TAG, "PPC: default case");
                    break;
            }
            return true;
        }
    }

    /**
     * Used to send message to BluetoothService for it to be sent by Bluetooth
     */
    private void write(String message) {
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            bluetoothService.write(send);
        }
    }

    /**
     * Scheduled task used to control bike
     */
    private class CooperativeCompetitiveControlTask extends TimerTask {
        private float mainSamplingPeriod;
        private float motorEfficiency = 0.8f;
        private float torqueBias = 44.5f;
        private float cranksetEfficiency = 0.9f;
        private float humanPowerFactor = 1.5f;
        private int windowSize = 1;
//        private float pollutionLevel = 1.0f;
        private final int numEntriesRequired = 20; // Take the most recent 20 plotting table rows to draw on graph
        private final int maxPlottedWatts = 400;
//        private ArrayList<Float> pollutionLevelArray;
//        private final float LOW_POLLUTION_LEVEL = (float)0.2;
//        private final float MID_POLLUTION_LEVEL = (float)0.4;
//        private final float HIGH_POLLUTION_LEVEL = (float)0.6;
//        private float mStar = 0.8f;
        private int timestep;
        private float cooperationThreshold = 200; // Watts
        private int high_m_duration=90;
//        float recentMotorOutputPowerAveraged = 200.0f;


//        float beta = 1.4f; // Scaling parameter for the slope of the cooperative characteristic

        public CooperativeCompetitiveControlTask(float mainSamplingPeriod) {
            this.mainSamplingPeriod = mainSamplingPeriod;



//            Log.i(tag, "mStar value: "+recentHumanOutputPowerAveraged+ ". Simulated count: "+simulated_count);


//            if(main_iteration_count==high_m_duration/mainSamplingPeriod){
//                //update mStar
//
//            }

//            pollutionLevelArray = new ArrayList<>();
//
//            // Add the constant values to the start of the array
//            // Code for simple sigmoid from high -> mid -> low pollution level and back
//            for(int i = 0;i<40;i++){
//                pollutionLevelArray.add(HIGH_POLLUTION_LEVEL);
//            }
//
//            float valueToAdd;
//
//            for(int i = 0;i<50;i++){
//                valueToAdd = (float)(HIGH_POLLUTION_LEVEL - (HIGH_POLLUTION_LEVEL - MID_POLLUTION_LEVEL)*(1/(1+Math.exp(-(-9+(18*i)/50.0)))));
//                pollutionLevelArray.add(valueToAdd);
//            }
//
//            for(int i = 0;i<25;i++){
//                pollutionLevelArray.add(MID_POLLUTION_LEVEL);
//            }
//
//            for(int i = 0;i<50;i++){
//                valueToAdd = (float)(MID_POLLUTION_LEVEL - (MID_POLLUTION_LEVEL - LOW_POLLUTION_LEVEL)*(1/(1+Math.exp(-(-9+(18*i)/50.0)))));
//                pollutionLevelArray.add(valueToAdd);
//            }
//
//            for(int i = 0;i<15;i++){
//                pollutionLevelArray.add(LOW_POLLUTION_LEVEL);
//            }
//
//            ArrayList<Float> reversedArray = new ArrayList<>(pollutionLevelArray);
//            Collections.reverse(reversedArray);
//
//            pollutionLevelArray.addAll(reversedArray);
//
//            timestep = 0;


        }

//        public void run() {
//
//        }

        public void run() {


            mStar=changeMArray[main_iteration_count];
            Log.i(tag, "mStar: "+ mStar+", count: "+main_iteration_count);
            main_iteration_count+=1;


            //can't update this as in a different thread

//            motorReferencePowerTextView = findViewById(R.id.motorReferenceOutput);
//            TextView tv = findViewById(R.id.mStarValueToggle);
//            tv.setText("Blah: " + main_iteration_count);
//            view.invalidate();  // for refreshment


            // Query the database for the required values
//            List<float[]> bikeDataList = dbHandler.getRecentBikeData(motorEfficiency, torqueBias, cranksetEfficiency, humanPowerFactor, windowSize);

//            if (bikeDataList != null) {
//                float[] motorOutputPowerArray = bikeDataList.get(0);
//                float[] humanOutputPowerArray = bikeDataList.get(1);
//
//                float recentMotorOutputPowerAveraged = calculateArrayAverage(motorOutputPowerArray, motorOutputPowerArray.length);
//                float recentHumanOutputPowerAveraged = calculateArrayAverage(humanOutputPowerArray, humanOutputPowerArray.length);

                //   SS 10/10/2020:what is this for and why is it defined in terms of the sampling period?
//                float usingSamplingPeriod = (float) (mainSamplingPeriod / 1000.0);

                // sampling period is defined in milliseconds, so to have the deltaT = 0.1*sampling period for runge kutta method, we need to divide by 10,000
                float deltaT = (float) (mainSamplingPeriod / 1000.0);

//            Log.i(tag, "yo might be null let's see");
                if(dataExchange != null) {
//                    Log.i(tag, "it's not null man");

                    float recentHumanOutputPowerAveraged;
                    if(USE_SIMULATED_DATA == true) {
                        Log.i(tag, "simulated count: "+simulated_count+" simulated data length: "+simulatedHumanPowerData.length);
                        if (simulated_count == simulatedHumanPowerData.length){
//                            Log.i(tag, "Seem to be in here now");
                            //exit simulation
                            mainControlTimer.cancel();
//                            secondaryControlTimer.cancel();
//                            handler.obtainMessage(SEND_TO_BIKE, 0).sendToTarget();
                            Log.i(tag, "End of simulation :-)");
                        }
                        recentHumanOutputPowerAveraged = simulatedHumanPowerData[simulated_count];
                        simulated_count +=1;
                        Log.i(tag, "Simulated human power: "+recentHumanOutputPowerAveraged+ ". Simulated count: "+simulated_count);
                    }
                    else{
                        recentHumanOutputPowerAveraged = dataExchange.getRecentHumanPower();
                    }


                    float recentMotorOutputPowerAveraged;
                    if(USE_SIMULATED_DATA == true) {
                        //March 2021: for testing purposes - change this such that actual = target
                        recentMotorOutputPowerAveraged = dataExchange.getMotorPowerTarget();
                    }else{
                        //We can't use the actual measured values of motor power in the control loop as they won't have converged to the target
                        //As such, we are going to use the previous motor power target value
                        //recentMotorOutputPowerAveraged = dataExchange.getRecentMotorPower();
                        recentMotorOutputPowerAveraged = dataExchange.getPreviousMotorPowerTarget();
//                        Log.i(tag, "recentMotorOutputPowerAveraged: "+ recentMotorOutputPowerAveraged);
                    }
//                    float recentMotorOutputPowerAveraged = 200.0f;


//                    if (recentHumanOutputPowerAveraged > 0 && recentMotorOutputPowerAveraged == 0) {
//                        // Generate dummy motor value to prevent the dead scenario
//                        recentMotorOutputPowerAveraged = 10;
//                    }

                    float motorOutputPowerReference;
                    float mTarget = 0;

//                    if (changeMEnabled) {
//                        // Pre-calculate mStar based on the pollution level, use simple mapping in this case
//                        mStar = 1 - pollutionLevelArray.get(timestep);
//
//                        motorOutputPowerReference = computeNextOutputMotorPowerReference(recentHumanOutputPowerAveraged,
//                                recentMotorOutputPowerAveraged,
//                                pollutionLevelArray.get(timestep),
//                                deltaT);
//
//                        // Threshold is a constant value for the moment
//                        if (recentHumanOutputPowerAveraged <= cooperationThreshold) {
//                            mTarget = mStar;
//                        } else {
//                            mTarget = recentHumanOutputPowerAveraged / (recentHumanOutputPowerAveraged + motorOutputPowerReference);
//                        }
//
//                        if (timestep < pollutionLevelArray.size() - 2) {
//                            timestep++;
//                        }
//                    } else {

                  //  Log.i(tag, "humanPower: "+recentHumanOutputPowerAveraged+" motorPower: "+recentMotorOutputPowerAveraged);
                    motorOutputPowerReference = computeNextOutputMotorPowerReference(recentHumanOutputPowerAveraged,
                            recentMotorOutputPowerAveraged,
                            mStar,
                            deltaT);

//                        Log.i(tag, "motorpower: "+motorOutputPowerReference);

                    if (recentHumanOutputPowerAveraged + motorOutputPowerReference != 0) {
                        mTarget = recentHumanOutputPowerAveraged / (recentHumanOutputPowerAveraged + motorOutputPowerReference);
                    }
//                    }

//                    Log.i(tag, "Human power: " + recentHumanOutputPowerAveraged + ". Target motor power: "+motorOutputPowerReference + ". Actual motor power: "+recentMotorOutputPowerAveraged);

                    //publish motor power target so it can be used in second control loop
                    dataExchange.setMotorPowerTarget(motorOutputPowerReference);
                    dataExchange.setPreviousMotorPowerTarget(motorOutputPowerReference);

                    // Adjust the output speed of the motor based on the error
//                int requestToMotor = computeNextRequestToMotor(recentHumanOutputPowerAveraged,
//                        motorOutputPowerReference,
//                        recentMotorOutputPowerAveraged);

                    float mActualAveraged = 0;

                    if (recentHumanOutputPowerAveraged + recentMotorOutputPowerAveraged != 0) {
                        mActualAveraged = recentHumanOutputPowerAveraged / (recentHumanOutputPowerAveraged + recentMotorOutputPowerAveraged);
                    }

                    float error = 0;

//                    if (changeMEnabled) {
//                        error = pollutionLevelArray.get(timestep);
//                    }

                    // Store data for plotting
                    // Update - October 2020, saving data is now moved to the secondary control loop (convergeToTargetMotorPowerControlTask) so it is saved at a higher frequency
//                BreathingControlData cooperativeBreathingControlData = new BreathingControlData(Integer.toString(requestToMotor),
//                        0,
//                        recentHumanOutputPowerAveraged,
//                        recentMotorOutputPowerAveraged,
//                        mTarget,
//                        mActualAveraged,
//                        error,
//                        mainSamplingPeriod);
//
//                dbHandler.addBreathingControlData(cooperativeBreathingControlData);

                    // Plot values - append to current data for each chart
                    // We are plotting the last 20 values for each chart


                    //-------------COMMENTED OUT PLOTTING CODE OCTOBER 2020
//                MValuePlottingData mValueActualData = dbHandler.getLatestMActual();
//
//                // Need to get the characteristic of the feedback for multiple input values
//                ArrayList<Float> inputToCharacteristicArray = new ArrayList<>();
//                ArrayList<Float> characteristicArray = new ArrayList<>();
//                float inputVal = maxPlottedWatts / numEntriesRequired;
//
//                if(changeMEnabled){
//                    for (int i = 0; i < numEntriesRequired; i++) {
//                        inputToCharacteristicArray.add(inputVal * i);
//                        characteristicArray.add((float) Math.sqrt(varyingCompeteCooperateCharacteristic(inputVal * i, pollutionLevelArray.get(timestep), mStar)));
//                    }
//                }
//                else{
//                    for (int i = 0; i < numEntriesRequired; i++) {
//                        inputToCharacteristicArray.add(inputVal * i);
//                        characteristicArray.add((float) Math.sqrt(competeCooperateCharacteristic(inputVal * i, pollutionLevel)));
//                    }
//                }
//
//                // Object for sending across data to plot
//                PlottingData dataToPlot = new PlottingData(mTarget,
//                        mValueActualData,
//                        inputToCharacteristicArray,
//                        characteristicArray,
//                        recentHumanOutputPowerAveraged,
//                        motorOutputPowerReference,
//                        recentMotorOutputPowerAveraged);
//
//                // Update UI graphs
//                handler.obtainMessage(PLOTTING_DATA, dataToPlot).sendToTarget();

                    //-------------


                    //the request to the motor is now calculated in the secondary control loop, but let's continue to save it in the main control loop table also for convenience
//                float requestToMotor = dbHandler.getLastRequestToBike();
                    lastRequestToMotor = dataExchange.getLastRequestToBike();

                    //should this be float or int?
//              float requestToMotorInt = Float.parseFloat(dataExchange.getLastRequestToBike());

                    //save values to database for debugging
                    CoopCompeteV2Data coopCompeteV2Data = new CoopCompeteV2Data(recentHumanOutputPowerAveraged, recentMotorOutputPowerAveraged, motorOutputPowerReference,
                            mStar, lastRequestToMotor, mTarget, mActualAveraged, mainSamplingPeriod);
                    dbHandler.addNewCoopCompeteV2(coopCompeteV2Data);

                }
        }

        /**
         * Method which uses a 4th order Runge-Kutta method to compute the value of the
         * reference output motor power for the next time instance.
         *
         * @param humanOutputPower average of the 5 most recent database entries for the human output power
         * @param motorOutputPower most recent database entry for the motor output power
         * @param mStar   value between 0 and 1 representing the level of pollution, 0 being low, 1 being high
         * @param dt               timestep used in calculation
         * @return the reference output motor power for the next time instance
         */
        private float computeNextOutputMotorPowerReference(float humanOutputPower, float motorOutputPower, float mStar, float dt) {
//            dt = 0.1f; // shaun added this line October 2020

            float[] f1vals = pitchforkBifurcation(humanOutputPower, motorOutputPower, mStar);
            float f1 = dt*f1vals[0];
            float[] f2vals = pitchforkBifurcation(humanOutputPower + dt / (float) 2.0, motorOutputPower + f1 / (float) 2.0, mStar);
            float f2 = dt*f2vals[0];
            float[] f3vals = pitchforkBifurcation(humanOutputPower + dt / (float) 2.0, motorOutputPower + f2 / (float) 2.0, mStar);
            float f3 = dt*f3vals[0];
            float[] f4vals = pitchforkBifurcation(humanOutputPower + dt, motorOutputPower + f3, mStar);
            float f4 = dt*f4vals[0];

            float nextOutputReferencePower = motorOutputPower + (1 / (float) 6.0) * (f1 + 2 * f2 + 2 * f3 + f4);

            // what do we have to save -
            // float[] testValues = {outputMotorPowerReference, retardingFactor, competeCooperateCharacteristic(humanOutputPower, pollutionLevel), competeCooperateCharacteristic(humanOutputPower, pollutionLevel) * motorOutputPower, (float) Math.pow((double) motorOutputPower, 3), (float) retardingFactor * (competeCooperateCharacteristic(humanOutputPower, pollutionLevel) * motorOutputPower - (float) Math.pow((double) motorOutputPower, 3))};


            //competeCooperateCharacteristic(humanOutputPower, pollutionLevel), competeCooperateCharacteristic(humanOutputPower, pollutionLevel) * motorOutputPower, (float) Math.pow((double) motorOutputPower, 3), (float) retardingFactor * (competeCooperateCharacteristic(humanOutputPower, pollutionLevel) * motorOutputPower - (float) Math.pow((double) motorOutputPower, 3))};
            // float[] testValues = {retardingFactor, competeCooperateCharacteristic(humanOutputPower, pollutionLevel), competeCooperateCharacteristic(humanOutputPower, pollutionLevel) * motorOutputPower, (float) Math.pow((double) motorOutputPower, 3)};


            // retardingFactor, pollutionLevel, cooperationThreshold, humanpower, motor power,
            // characteristicvalue, firstterm, secondterm
            // retardingFactor, pollutionLevel, cooperationThreshold, recentHumanPower, recentMotorPower,

            // WHAT WE NEED TO SAVE:
            // retardingFactor, pollutionLevel, cooperationThreshold, recentHumanPower, recentMotorPower, f1,f2,f3,f4,nextOutputReferencePower, f1_characteristicvalue, f1_firstterm, f1_secondterm, f2_characteristicvalue, f2_firstterm, f2_secondterm, f3_characteristicvalue, f3_firstterm, f3_secondterm, f4_characteristicvalue, f4_firstterm, f4_secondterm
            //f1vals[1], f1vals[2], f1vals[3]

            //save values in database
            BifurcationData bifurcationData = new BifurcationData(f1vals[1], mStar, cooperationThreshold, humanOutputPower, motorOutputPower, f1,f2,f3,f4,nextOutputReferencePower, f1vals[1], f1vals[2], f1vals[3], f2vals[1], f2vals[2], f2vals[3], f3vals[1], f3vals[2], f3vals[3], f4vals[1], f4vals[2], f4vals[3]);
            dbHandler.addBifurcationDataInternalWorkings(bifurcationData);


//            Log.i(tag, "TRUE: f1: "+f1+", f2: "+f2+", f3: "+f3+", f4: "+f4+", nextPowerRef: "+nextOutputReferencePower);
            return nextOutputReferencePower;


            //backup
//            float f1 = dt * pitchforkBifurcation(humanOutputPower, motorOutputPower, pollutionLevel)[0];
//            float f2 = dt * pitchforkBifurcation(humanOutputPower + dt / (float) 2.0, motorOutputPower + f1 / (float) 2.0, pollutionLevel)[0];
//            float f3 = dt * pitchforkBifurcation(humanOutputPower + dt / (float) 2.0, motorOutputPower + f2 / (float) 2.0, pollutionLevel)[0];
//            float f4 = dt * pitchforkBifurcation(humanOutputPower + dt, motorOutputPower + f3, pollutionLevel)[0];
//            float nextOutputReferencePower = motorOutputPower + (1 / (float) 6.0) * (f1 + 2 * f2 + 2 * f3 + f4);
//
//            Log.i(tag, "TRUE: f1: "+f1+", f2: "+f2+", f3: "+f3+", f4: "+f4+", nextPowerRef: "+nextOutputReferencePower);
//            return nextOutputReferencePower;
        }

        /**
         * Compute reference output motor power for the given moving averaged human input power
         *
         * @param humanPower Average of the 5 most recent database entries for the human output power
         * @param motorPower Average of the 5 most recent database entries for the motor output power?
         * @param mStar   value between 0 and 1 representing the level of pollution, 0 being low, 1 being high
         * @return the output of the pitchfork bifurcation
         */
        private float[] pitchforkBifurcation(float humanPower, float motorPower, float mStar) {
//            float retardingFactor = 0.00005f; //
            //float retardingFactor = 0.0005f; //
            float retardingFactor = 0.00001f; // CORRECT VALUE for ts = 2.5s

            float outputMotorPowerReference = retardingFactor * (competeCooperateCharacteristic(humanPower, mStar) * motorPower - (float) Math.pow((double) motorPower, 3));
//            Log.i(tag, "humanPowerinput: "+humanPower);
//            Log.i(tag, "motorpower: "+motorPower);
//            Log.i(tag, "humanPowGraphValue: "+competeCooperateCharacteristic(humanPower, pollutionLevel, mStar)+ " first term: " + (competeCooperateCharacteristic(humanPower, pollutionLevel, mStar) * motorPower) + " second term: " + (Math.pow((double) motorPower, 3)) + " outputMotorPowerReference: "+outputMotorPowerReference);

//            Log.i(tag, "Target motor power: "+outputMotorPowerReference);
            //should be returned for saving to database to enable debugging -
            float[] testValues = {outputMotorPowerReference, retardingFactor, competeCooperateCharacteristic(humanPower, mStar), competeCooperateCharacteristic(humanPower, mStar) * motorPower, (float) Math.pow((double) motorPower, 3)};

            return testValues;
        }

//        private float pitchforkBifurcation(float humanOutputPower, float motorOutputPower, float pollutionLevel, float mStar) {
//            //float retardingFactor = (float)0.0001;//0.000085;//0.00005; //TODO:
//            float kappa = (float)0.0125;
//
//            if(mStar >= 0.4 && mStar < 1){
//                kappa *= 2.5*mStar;
//            }
//
//            float retardingFactor = kappa/((2*(1-mStar)/mStar)*humanOutputPower);
//
//            float outputMotorPowerReference;
//
//            outputMotorPowerReference = retardingFactor * (competeCooperateCharacteristic(humanOutputPower, pollutionLevel, mStar) * motorOutputPower - (float) Math.pow((double) motorOutputPower, 3));
//
//            return outputMotorPowerReference;
//        }

        /**
         *
         * @param humanOutputPower
         * @param motorOutputPower
         * @param pollutionLevel
         * @param dt
         * @param mStar the target value of m in the cooperation region
         * @return
         */
//        private float variableComputeNextOutputMotorPowerReference(float humanOutputPower, float motorOutputPower, float pollutionLevel, float dt, float mStar) {
////            dt = 0.1f; // shaun added this line October 2020
//            float f1 = dt * pitchforkBifurcation(humanOutputPower, motorOutputPower, pollutionLevel, mStar);
//            float f2 = dt * pitchforkBifurcation(humanOutputPower + dt / (float) 2.0, motorOutputPower + f1 / (float) 2.0, pollutionLevel, mStar);
//            float f3 = dt * pitchforkBifurcation(humanOutputPower + dt / (float) 2.0, motorOutputPower + f2 / (float) 2.0, pollutionLevel, mStar);
//            float f4 = dt * pitchforkBifurcation(humanOutputPower + dt, motorOutputPower + f3, pollutionLevel, mStar);
//            float nextOutputReferencePower = motorOutputPower + (1 / (float) 6.0) * (f1 + 2 * f2 + 2 * f3 + f4);
//
//            Log.i(tag, "f1: "+f1+"f2: "+f2+"f3: "+f3+"f4: "+f4+"nextPowerRef: "+nextOutputReferencePower);
//            return nextOutputReferencePower;
//        }

        /**
         * @param humanOutputPower Average of the 5 most recent database entries for the human output power
         * @param mStar   value between 0 and 1 representing the level of pollution, 0 being low, 1 being high
         * @return the output of the cooperate-competitive characteristic
         */
        private float competeCooperateCharacteristic(float humanOutputPower, float mStar) {
            // Need some way to compute a threshold
            // The larger the velocity, the larger the threshold should be

            //float expDecayRate=1/40.0f; //standard value
  //          float expDecayRate=2.5f; // May 2021 value to try get motor to turn off faster
            float expDecayRate=100.0f;
            float outputOfCharacteristic;
            float beta = (1 - mStar)/mStar;

            //Andrew version
//            double beta = 1.4;
//            float pollutionLevel = 0.6f;
            //pollutionLevel variable removed

            //May 2021: hardcoding some values for the purposes of doing a specific experiment
            if(mStar == 0.45f){
//                Log.i(tag, "Cooperation threshold value updated");
                cooperationThreshold = 100;
            }


//            Log.i(tag, "humanPower: "+humanOutputPower);
            if (humanOutputPower < cooperationThreshold) {
//                Log.i(tag, "Cooperation");
                //outputOfCharacteristic = (float) Math.pow(beta * humanOutputPower, 2);
                outputOfCharacteristic = (float) Math.pow(beta*humanOutputPower, 2);
            } else {
//                Log.i(tag, "Competition");
//                Log.i(tag, "human power is greater than threshold");
                //Andrew version
              //  outputOfCharacteristic = (float) Math.pow((beta * pollutionLevel * cooperationThreshold) * Math.exp(-(humanOutputPower - cooperationThreshold) / 20.0), 2);

                //equation updated to match paper January 2021
//                outputOfCharacteristic = beta*(float) (Math.pow(cooperationThreshold,2)*(1+2*(expDecayRate+1/cooperationThreshold)*(humanOutputPower-cooperationThreshold))* (Math.exp( -2*expDecayRate*(humanOutputPower-cooperationThreshold))));
                outputOfCharacteristic = (float) (Math.pow(beta*cooperationThreshold,2)*(1+2*(expDecayRate+1/cooperationThreshold)*(humanOutputPower-cooperationThreshold))* (Math.exp( -2*expDecayRate*(humanOutputPower-cooperationThreshold))));
//                outputOfCharacteristic=0.0f;
//               (beta * pollutionLevel * cooperationThreshold) * Math.exp(-(humanOutputPower - cooperationThreshold) / 20.0), 2);
            }

            return outputOfCharacteristic;
        }

//        private float varyingCompeteCooperateCharacteristic(float humanOutputPower, float pollutionLevel, float mStar) {
//            // Need some way to compute a threshold
//            // The larger the velocity, the larger the threshold should be
//            mStar = 0.5f;
//            float cooperationThreshold = 150; //300 - 200*pollutionLevel; // Watts
//            float outputOfCharacteristic;
//
//            if(mStar < 0.01){
//                mStar = (float)0.01; // Prevent division by very small number
//            }
//
//            float beta = (1 - mStar)/mStar; // Scaling parameter for the slope of the cooperative characteristic
//
//            if (humanOutputPower < cooperationThreshold) {
//                outputOfCharacteristic = (float) Math.pow(beta * humanOutputPower, 2);
//            } else {
//                outputOfCharacteristic = (float) Math.pow((beta * cooperationThreshold) * Math.exp(-(humanOutputPower - cooperationThreshold) / 20.0), 2);
//            }
//
//            return outputOfCharacteristic;
//        }

//        /**
//         * @param motorOutputPowerReference      the value to which we desire the actual motor power to converge to at this timestep
//         * @param recentMotorOutputPowerAveraged running average of the most recent values of the actual output motor power
//         * @return a value between 90 and 175 representing the next control value that should be sent to the motor over the Bluetooth connection
//         */
//        private int computeNextRequestToMotor(float recentHumanOutputPower, float motorOutputPowerReference, float recentMotorOutputPowerAveraged) {
//
//            // set up a new control loop to
//
//            return 0;
//
//        }

        public float calculateArrayAverage(float[] floatArray, int numAverages) {
            float sum = 0;

            for (int i = 0; i < numAverages; i++) {
                sum = sum + floatArray[i];
            }

            // Calculate average value
            float average = sum / (float) numAverages;
            
            return average;
        }
    }


    // code below added in October 2020 to try to get actual motor power to converge to target motor power
    private class ConvergeToTargetMotorPowerControlTask extends TimerTask {
//        float motorPowerTarget;
//        float lastRequestToBike;
        int nextRequestToBikeInt;
//        boolean startingIteration = true;
        private int maxRequestToBike = 220;
        private int minRequestToBike = 90;
        private float motorEfficiency = 0.5f;
        private float torqueBias = 44.5f;
        private float cranksetEfficiency = 0.9f;
        private float humanPowerFactor = 1.5f;
        private int windowSize = 25;
        private int numMotorAverages = 5;
        private float latestMotorPowerAvg;
        private float latestHumanPowerAvg;
        float[] motorPowerArray;
        float[] humanPowerArray;

        public ConvergeToTargetMotorPowerControlTask() {

        }

        @Override
        public void run() {


//            List<float[]> bikeDataList;

            //get recent values of motor power
//            bikeDataList = dbHandler.getRecentBikeData(motorEfficiency, torqueBias, cranksetEfficiency, humanPowerFactor, windowSize);
//            motorPowerArray = bikeDataList.get(0);
//            humanPowerArray = bikeDataList.get(1);
//
//            latestMotorPowerAvg = calculateFloatArrayAverage(motorPowerArray, numMotorAverages);
//            latestHumanPowerAvg = calculateHumanFloatArrayAverage(humanPowerArray, humanPowerArray.length);


//            Log.i(tag, "second control loop running bish");


            float latestHumanPowerAvg;
            if(USE_SIMULATED_DATA == true) {
                latestHumanPowerAvg = simulatedHumanPowerData[simulated_count];
//                simulated_count +=1;
//                Log.i(tag, "Simulated human power: "+latestHumanPowerAvg+ ". Simulated count: "+simulated_count);
            }
            else{
                latestHumanPowerAvg = dataExchange.getRecentHumanPower();
            }

            latestMotorPowerAvg = 200.0f;
            latestMotorPowerAvg = dataExchange.getRecentMotorPower();
//            latestHumanPowerAvg = dataExchange.getRecentHumanPower();
            motorPowerTarget = dataExchange.getMotorPowerTarget();

            //Log.i(tag, "getmotorpowertarget: "+motorPowerTarget);

            //lastRequestToBike = dbHandler.getLastRequestToBike();
//            if(dataExchange.getLastRequestToBike() !=0.0f){
          //  float lastRequestToMotor = Float.parseFloat(dataExchange.getLastRequestToBike());

            float lastRequestToMotor = dataExchange.getLastRequestToBike();

//            }

            //get most recent value of motor power target
//            motorPowerTarget= dbHandler.getMotorPowerTarget();

            float motorPowerError = motorPowerTarget - latestMotorPowerAvg;
//            Log.i(tag, "motorPowerTarget: "+ motorPowerTarget+" motorPowerActual: "+latestMotorPowerAvg);
            float gamma = 0.075f;



            //update request to send to bike
            if (latestHumanPowerAvg < 5) {
                nextRequestToBikeInt = 0;}
            else {
                nextRequestToBikeInt = Math.round(lastRequestToMotor + gamma * motorPowerError);

//                Log.i(tag, "Next request to bike before change: "+ nextRequestToBikeInt);
                if (nextRequestToBikeInt > maxRequestToBike) {
                    nextRequestToBikeInt = maxRequestToBike;
                } else if (nextRequestToBikeInt < minRequestToBike) {
                    Log.i(tag, "next request to bike before change: " + nextRequestToBikeInt);
                    nextRequestToBikeInt = minRequestToBike;
                }
//                Log.i(tag, "Next request to bike after change: "+ nextRequestToBikeInt);
            }


            //send to bike - this count variable enables us to send the request value more or less frequently - may or may not be required depenidng on application
//           int count;
//            count = dbHandler.getLatestMotorCount();
//            if(count == 100) {
//                count=0;
//
//
//                dbHandler.addMotorFilterRow(count);
//            }
//            //update the value of count in the database
//            dbHandler.addMotorFilterRow(count+1);


            lastRequestToMotor = nextRequestToBikeInt;
            // send value to bike
            handler.obtainMessage(SEND_TO_BIKE, nextRequestToBikeInt).sendToTarget();


            //value to send to bike is saved as a string in database
            String bikeRequestString = ""+nextRequestToBikeInt;

            //save data from secondary control loop in database
            MotorPowerControlData motorPowerControlData = new MotorPowerControlData(bikeRequestString, gamma,
                    latestHumanPowerAvg, latestMotorPowerAvg, motorPowerTarget, motorPowerError, secondarySamplingPeriod);
            dbHandler.addMotorPowerControlData(motorPowerControlData);


        }

    }

    public float calculateFloatArrayAverage(float[] floatArray, int numAverages){
        float average;
        float sum=0;

        for(int i=0; i < numAverages; i++){
            sum = sum + floatArray[i];
            // Log.i(tag, "motorPower:" + floatArray[i]);
        }

        //calculate average value
        average = sum / numAverages;
        return average;
    }

    //calculate power average
    public float calculateHumanFloatArrayAverage(float[] floatArray, int numAverages){
        float average;
        float sum=0;

        for(int i=0; i < numAverages; i++){
            sum = sum + floatArray[i];
            //Log.i(tag, "humanPower:" + floatArray[i]);
        }

        //calculate average value
        average = sum / numAverages;
        return average;
    }

    /**
     * bluetoothServiceConnection is needed to use BluetoothService methods
     */
    private ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.BluetoothMyLocalBinder binder = (BluetoothService.BluetoothMyLocalBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    /**
     * databaseServiceConnection is needed to use DatabaseService methods
     */
    private ServiceConnection databaseServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DatabaseService.DatabaseMyLocalBinder binder = (DatabaseService.DatabaseMyLocalBinder) service;
            databaseService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void registerReceivers() {
        // check if I can use this filter or if needs to be different to DatabaseService filter?
        bikeDataFilter = new IntentFilter("ie.ucd.smartride.bikeData");
        registerReceiver(bikeDataReceiver, bikeDataFilter);


        commandSentFilter = new IntentFilter("ie.ucd.smartride.commandSent");
        registerReceiver(commandSentReceiver, commandSentFilter);

    }

    private final BroadcastReceiver commandSentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String lastRequestToBike = intent.getStringExtra("commandSent");

            //Log.i(tag, "am here man");
            if(lastRequestToBike != null){
                float lastRequestToBikeFloat = Float.parseFloat(lastRequestToBike);
                //Log.i(tag, "set lastRequestToBikeFloat: "+lastRequestToBikeFloat);
                dataExchange.setLastRequestToBike(lastRequestToBikeFloat);
            }
        }
    };


    private float calculateAverage(List<Float> power) {
        float sum = 0;
        if(!power.isEmpty()) {
            for (float mark : power) {
                sum += mark;
//                Log.i(tag, "mark: "+mark+" power: "+power);
            }
            return sum / power.size();
        }
        return sum;
    }


    private final BroadcastReceiver bikeDataReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            float pi = (float) Math.PI;
            float gamma1 = 0.1640f;
            float gamma2 = -12.8473f;
            float motorEfficiency = 0.8f;
            float cranksetEfficiency = 0.9f;
            float humanPowerFactor = 0.8f;
//            float lastRequestToBike;
            float humanPower, averageHumanPower;
            float motorPower, averageMotorPower;
            float voltage, current, torque, rpm, caHumanPower; //a value of human power is calculated by the CA directly


           // Log.i(tag, "in bikedata broadcast receiver");

//            String bikeDataReceived = intent.getStringExtra("database");
//            String bikeDataReceived = intent.getStringExtra("bikeData");

            Bundle bikeDataExtras = intent.getExtras();
            float[] bikeDataFloatArray = bikeDataExtras.getFloatArray("bikeData");


            //Log.i(tag, "am here lads: ");

            // use values received to update the value of DataExchange Object
            // need to go back and look at which methods need to be replaced - detailed in evernote
            if(bikeDataFloatArray != null) {

                //data required
                voltage = bikeDataFloatArray[0];
                current = bikeDataFloatArray[1];
                torque = bikeDataFloatArray[2];;
                rpm = bikeDataFloatArray[3];
                caHumanPower = bikeDataFloatArray[4];

//                Log.i(tag, "voltage: "+voltage);


//                bikeDataReceived.get_voltage();
//                current = bikeDataReceived.get_current();
//                torque = bikeDataReceived.get_torque();
//                rpm = bikeDataReceived.get_RPM();

                //get most recent command sent from object
                lastRequestToMotor = dataExchange.getLastRequestToBike();
                //Log.i(tag, "get lastRequestToBikeFloat: "+lastRequestToMotor);

                //calculate motor power and human power to avoid calls to database to later retrieve these
                motorPower = Math.max(voltage * current * motorEfficiency - (lastRequestToMotor * gamma1 + gamma2), 0);
               // humanPower = Math.max(humanPowerFactor * cranksetEfficiency * (torque - 0) * rpm * 2 * pi / 60, 0);
                humanPower = caHumanPower; //let's try the CA value to see if it makes a difference

//                Log.i(tag, "motorPower: "+motorPower);
//                Log.i(tag, "humanPower: " +humanPower);


                // now need to store them all in an array
                int human_average_size=1;
                int motor_average_size=1;
//                Log.i(tag, "motor power size 1: "+motorPowerList.size());
                if(humanPowerList.size() >= human_average_size) {
                    humanPowerList.remove(0);
                }
                if(motorPowerList.size() >= motor_average_size) {
                    motorPowerList.remove(0);
                }
                motorPowerList.add(motorPower);
                humanPowerList.add(humanPower);

//                Log.i(tag, "motor power size 2: "+motorPowerList.size());

                //calculate new average of array list
//                averageHumanPower = calculateArrayAverage(humanPowerList, humanPowerList.size());

                averageHumanPower = calculateAverage(humanPowerList);
                averageMotorPower = calculateAverage(motorPowerList);

//                Log.i(tag, "average human power: "+ averageHumanPower);
//                Log.i(tag, "average human power: "+ averageMotorPower);


//                Log.i(tag, "average human power: "+ averageHumanPower);
//                Log.i(tag, "average motor power: "+ averageMotorPower);

                //set the values
                dataExchange.setRecentHumanPower(averageHumanPower);
                dataExchange.setRecentMotorPower(averageMotorPower);


            }


            //comment this log line for now but it is very useful for checking data is being received
//            Log.i(tag, "DS: Bike data: " + bikeDataReceived);

            // Must determine which table the data is intended for
//            String[] splitBikeDataReceived = bikeDataReceived.split(" ");

            //If it is desired to save the frequency with which data is saved from cycle analyst this can be
            //implemented here with a count variable as is done in mHeartRateEventListener below
            //DatabaseService.ProcessBikeDataThread processBikeDataThread = new DatabaseService.ProcessBikeDataThread(bikeDataReceived);
            //processBikeDataThread.start();
        }
    };

//    public float calculateArrayAverage(float[] floatArray, int numAverages) {
//        float sum = 0;
//
//        for (int i = 0; i < numAverages; i++) {
//            sum = sum + floatArray[i];
//        }
//
//        // Calculate average value
//        float average = sum / (float) numAverages;
//
//        return average;
//    }
}
