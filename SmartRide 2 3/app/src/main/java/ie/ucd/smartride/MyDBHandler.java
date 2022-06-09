/*
 * Class Name: MyDBHandler.java
 * Corresponding layout: No
 * Author: Shaun Sweeney - shaun.sweeney@ucdconnect.ie // shaunsweeney12@gmail.com
 * Date: March 2017
 * Description: MyDBHandler extends SQLiteOpenHelper and is used to set up a database on the android
 * phone. It is used below to create different SQL tables. The structure of the tables (table names,
 * column names, data types for each column etc.) are all specified here. Methods for writing to the
 * database or pulling data from the database are all also implemented here.
 * N.B. If it is desired to add a new table / modify an existing table / clear all saved data in the
 * database this can be achieved by changing the variable "private static final int DATABASE_VERSION",
 * it should be incremented by 1, then the next time the database is bound to (onBind), the tables
 * will be recreated.
 * */

package ie.ucd.smartride;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bikemessaging.SimpleLocation;
import bikemessaging.TrafficLightMessage;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final String tag = "debugging";
    private static final int DATABASE_VERSION = 126;
    private static final String DATABASE_NAME = "data.db";
    public static final String TABLE_BIKEDATA = "bikeData";
    public static final String TABLE_CALORIESBURNED = "mBandCaloriesBurned";
    public static final String TABLE_CALORIESCONTROL = "caloriesControl";
    public static final String TABLE_COMMANDSENT = "commandSent";
    public static final String TABLE_HEARTRATE = "heartRate";
    public static final String TABLE_BREATHINGCONTROL = "breathingControl";
    public static final String TABLE_MTARGET = "mTarget";
    public static final String TABLE_COOPERATIVEBREATHINGCONTROL = "cooperativeBreathingControl";
    public static final String TABLE_MOTORFILTER = "motorFilter";
    public static final String TABLE_TRAFFIC_LIGHT = "trafficLight";
    public static final String TABLE_BIKE_LOCATION = "bikeLocation";
    public static final String TABLE_COOPERATIVE_COMPETITIVE_CONTROL_V2 = "coopCompeteV2";
    public static final String TABLE_MOTOR_POWER_CONTROL = "motorPowerControl";
    public static final String TABLE_COOPERATIVE_INTERNAL_CALCS = "coopInternalCalcs";

    //Common column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";

    //Column names for bike data table
    private static final String COLUMN_BATTERYENERGY = "batteryEnergy";
    private static final String COLUMN_VOLTAGE = "voltage";
    private static final String COLUMN_CURRENT = "current";
    private static final String COLUMN_SPEED = "speed";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_TEMPERATURE = "temperature";
    private static final String COLUMN_RPM = "RPM";
    private static final String COLUMN_HUMANPOWER = "humanPower";
    private static final String COLUMN_TORQUE = "torque";
    private static final String COLUMN_THROTTLEIN = "throttleIn";
    private static final String COLUMN_THROTTLEOUT = "throttleOut";
    private static final String COLUMN_ACCELERATION = "acceleration";
    private static final String COLUMN_FLAG = "flag";

    //Column names for Microsoft band data table
    private static final String COLUMN_HEARTRATE = "heartRate";
    private static final String COLUMN_CALORIESBURNED = "caloriesBurned";

    //Column names for command sent table
    private static final String COLUMN_COMMANDSENT = "commandSent";
    private static final String COLUMN_ACTUALCALORIESBURNEDRATE = "actualCaloriesBurnedRate";
    private static final String COLUMN_TARGETCALORIESRATE = "targetCaloriesBurnedRate";
    private static final String COLUMN_ERRORCALORIES = "errorCalories";
    private static final String COLUMN_GAINPARAMETER = "gainParameter";

    //Column names for breathing control table
    private static final String COLUMN_BREATHING_GAINPARAMETER = "gainParameter";
    private static final String COLUMN_BREATHING_HUMANPOWER_AVG = "humanPowerAvg";
    private static final String COLUMN_BREATHING_MOTORPOWER_AVG = "motorPowerAvg";
    private static final String COLUMN_BREATHING_MTARGET = "mTarget";
    private static final String COLUMN_BREATHING_MACTUAL_AVG = "mActualAvg";
    private static final String COLUMN_BREATHING_ERROR = "error";
    private static final String COLUMN_BREATHING_SAMPLINGPERIOD = "samplingPeriod";

    //extra column names for cooperative breathing control table
    private static final String COLUMN_BREATHING_FM = "fm";

    //column names for cooperative competitive breathing control table v2
    private static final String COLUMN_RECENT_HUMAN_POWER_AVG = "recentHumanPowerAveraged";
    private static final String COLUMN_RECENT_MOTOR_POWER_AVG = "recentMotorPowerAveraged";
    private static final String COLUMN_MOTOR_OUTPUT_REFERENCE = "motorOutputPowerReference";
    private static final String COLUMN_POLLUTION_LEVEL = "pollutionLevel";
    private static final String COLUMN_REQUEST_TO_MOTOR = "requestToMotor";
    private static final String COLUMN_M_TARGET = "mTarget";
    private static final String COLUMN_M_ACTUAL = "mActual";
    private static final String COLUMN_MAIN_SAMPLING_PERIOD = "mainSamplingPeriod";

    // column names for motor power control table
    private static final String COLUMN_BREATHING_MOTORPOWER_TARGET = "motorPowerTarget";
    private static final String COLUMN_BREATHING_MOTORPOWER_ERROR = "motorPowerError";
    private static final String COLUMN_BREATHING_SECONDARY_SAMPLINGPERIOD = "secondarySamplingPeriod";

    //columns for saving internal workings of bifurcation calculation
    private static final String COLUMN_BREATHING_RETARDING_FACTOR = "retardingFactor";
    private static final String COLUMN_BREATHING_POLLUTION_LEVEL = "pollutionLevel";
    private static final String COLUMN_BREATHING_COOPERATION_THRESHOLD = "cooperationThreshold";
    private static final String COLUMN_BREATHING_RECENT_HUMAN_POWER = "recentHumanPower";
    private static final String COLUMN_BREATHING_RECENT_MOTOR_POWER = "recentMotorPower";
    private static final String COLUMN_BREATHING_F1 = "f1";
    private static final String COLUMN_BREATHING_F2 = "f2";
    private static final String COLUMN_BREATHING_F3 = "f3";
    private static final String COLUMN_BREATHING_F4 = "f4";
    private static final String COLUMN_BREATHING_NEXT_POWER_REFERENCE = "nextOutputReferencePower";
    private static final String COLUMN_BREATHING_F1_CHARACTERISTIC_VALUE = "f1_characteristicvalue";
    private static final String COLUMN_BREATHING_F1_FIRST_TERM = "f1_firstterm";
    private static final String COLUMN_BREATHING_F1_SECONDTERM = "f1_secondterm";
    private static final String COLUMN_BREATHING_F2_CHARACTERISTIC_VALUE = "f2_characteristicvalue";
    private static final String COLUMN_BREATHING_F2_FIRST_TERM = "f2_firstterm";
    private static final String COLUMN_BREATHING_F2_SECONDTERM = "f2_secondterm";
    private static final String COLUMN_BREATHING_F3_CHARACTERISTIC_VALUE = "f3_characteristicvalue";
    private static final String COLUMN_BREATHING_F3_FIRST_TERM = "f3_firstterm";
    private static final String COLUMN_BREATHING_F3_SECONDTERM = "f3_secondterm";
    private static final String COLUMN_BREATHING_F4_CHARACTERISTIC_VALUE = "f4_characteristicvalue";
    private static final String COLUMN_BREATHING_F4_FIRST_TERM = "f4_firstterm";
    private static final String COLUMN_BREATHING_F4_SECONDTERM = "f4_secondterm";





    //column names for storing updated values of mTarget
    private static final String COLUMN_MTARGET = "mTarget";

    //column names for motor filter table
    private static final String COLUMN_COUNT = "count";

    // Column names for TrafficLightData table
    private static final String COLUMN_TRAFFIC_LIGHT_SIGNAL = "signal";
    private static final String COLUMN_TRAFFIC_LIGHT_LATITUDE = "latitude";
    private static final String COLUMN_TRAFFIC_LIGHT_LONGITUDE = "longitude";

    // Column names for Bike location data
    private static final String COLUMN_BIKE_LOCATION_LATITUDE = "latitude";
    private static final String COLUMN_BIKE_LOCATION_LONGITUDE = "longitude";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //first time table is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        //String with query to create table for cycle analyst data (invoked at end of this method)
        String bikeDataQuery = "CREATE TABLE " + TABLE_BIKEDATA + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_BATTERYENERGY + " FLOAT, " +
                COLUMN_VOLTAGE + " FLOAT, " +
                COLUMN_CURRENT + " FLOAT, " +
                COLUMN_SPEED + " FLOAT, " +
                COLUMN_DISTANCE + " FLOAT, " +
                COLUMN_TEMPERATURE + " FLOAT, " +
                COLUMN_RPM + " FLOAT, " +
                COLUMN_HUMANPOWER + " FLOAT, " +
                COLUMN_TORQUE + " FLOAT, " +
                COLUMN_THROTTLEIN + " FLOAT, " +
                COLUMN_THROTTLEOUT + " FLOAT, " +
                COLUMN_ACCELERATION + " FLOAT, " +
                COLUMN_FLAG + " STRING " +
                ");";
        Log.i(tag, bikeDataQuery);

        //String with query to create table for calories burned from Microsoft Band
        String caloriesBurnedQuery = "CREATE TABLE " + TABLE_CALORIESBURNED + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_CALORIESBURNED + " FLOAT " +
                ");";
        Log.i(tag, caloriesBurnedQuery);

        //String with query to create table for heart rate from Microsoft Band
        String heartRateQuery = "CREATE TABLE " + TABLE_HEARTRATE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_HEARTRATE + " FLOAT " +
                ");";
        Log.i(tag, heartRateQuery);


        //String with query to create table for calories burned from Microsoft Band control
        String caloriesControlQuery = "CREATE TABLE " + TABLE_CALORIESCONTROL + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_COMMANDSENT + " STRING, " +
                COLUMN_ACTUALCALORIESBURNEDRATE + " FLOAT, " +
                COLUMN_TARGETCALORIESRATE + " FLOAT, " +
                COLUMN_ERRORCALORIES + " FLOAT, " +
                COLUMN_GAINPARAMETER + " FLOAT " +
                ");";
        Log.i(tag, caloriesControlQuery);

        //String with query to create table to save all commands sent to the bike
        String commandSentQuery = "CREATE TABLE " + TABLE_COMMANDSENT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_COMMANDSENT + " STRING " +
                ");";
        Log.i(tag, commandSentQuery);


        //String with query to create table to save all data from breathing control data
        String breathingControlQuery = "CREATE TABLE " + TABLE_BREATHINGCONTROL + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_COMMANDSENT + " STRING, " +
                COLUMN_BREATHING_GAINPARAMETER + " FLOAT, " +
                COLUMN_BREATHING_HUMANPOWER_AVG + " FLOAT, " +
                COLUMN_BREATHING_MOTORPOWER_AVG + " FLOAT, " +
                COLUMN_BREATHING_MTARGET + " FLOAT, " +
                COLUMN_BREATHING_MACTUAL_AVG + " FLOAT, " +
                COLUMN_BREATHING_ERROR + " FLOAT, " +
                COLUMN_BREATHING_SAMPLINGPERIOD + " FLOAT " +
                ");";
        Log.i(tag, breathingControlQuery);


        //String with query to create table to save all data from motor power control data
        String motorPowerControlQuery = "CREATE TABLE " + TABLE_MOTOR_POWER_CONTROL + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_COMMANDSENT + " STRING, " +
                COLUMN_BREATHING_GAINPARAMETER + " FLOAT, " +
                COLUMN_BREATHING_HUMANPOWER_AVG + " FLOAT, " +
                COLUMN_BREATHING_MOTORPOWER_AVG + " FLOAT, " +
                COLUMN_BREATHING_MOTORPOWER_TARGET + " FLOAT, " +
                COLUMN_BREATHING_MOTORPOWER_ERROR + " FLOAT, " +
                COLUMN_BREATHING_SAMPLINGPERIOD + " FLOAT " +
                ");";
        Log.i(tag, motorPowerControlQuery);



        //String with query to create table to store updated values of mTarget
        String mTargetQuery = "CREATE TABLE " + TABLE_MTARGET + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_MTARGET + " FLOAT " +
                ");";
        Log.i(tag, mTargetQuery);


        //String with query to create table to save all data from breathing cooperative control data
        String breathingCooperativeControlQuery = "CREATE TABLE " + TABLE_COOPERATIVEBREATHINGCONTROL + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_COMMANDSENT + " STRING, " +
                COLUMN_BREATHING_MTARGET + " FLOAT, " +
                COLUMN_BREATHING_FM + " FLOAT, " +
                COLUMN_BREATHING_SAMPLINGPERIOD + " FLOAT " +
                ");";
        Log.i(tag, breathingCooperativeControlQuery);

        //String with query to create table to store updated values of mTarget
        String motorFilterQuery = "CREATE TABLE " + TABLE_MOTORFILTER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_COUNT + " INT " +
                ");";
        Log.i(tag, motorFilterQuery);

        String trafficLightQuery = "CREATE TABLE " + TABLE_TRAFFIC_LIGHT + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_TRAFFIC_LIGHT_SIGNAL + " STRING, " +
                COLUMN_TRAFFIC_LIGHT_LATITUDE + " FLOAT, " +
                COLUMN_TRAFFIC_LIGHT_LONGITUDE + " FLOAT " +
                ");";
        Log.i(tag, trafficLightQuery);

        String bikeLocationQuery = "CREATE TABLE " + TABLE_BIKE_LOCATION + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BIKE_LOCATION_LATITUDE + " FLOAT, " +
                COLUMN_BIKE_LOCATION_LONGITUDE + " FLOAT " +
                ");";
        Log.i(tag, bikeLocationQuery);




        //create cooperative competitive control v2 table
        String cooperateCompeteV2Query = "CREATE TABLE " + TABLE_COOPERATIVE_COMPETITIVE_CONTROL_V2 + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_RECENT_HUMAN_POWER_AVG + " FLOAT, " +
                COLUMN_RECENT_MOTOR_POWER_AVG + " FLOAT, " +
                COLUMN_MOTOR_OUTPUT_REFERENCE + " FLOAT, " +
                COLUMN_POLLUTION_LEVEL + " FLOAT, " +
                COLUMN_REQUEST_TO_MOTOR + " FLOAT, " +
                COLUMN_M_TARGET + " FLOAT, " +
                COLUMN_M_ACTUAL + " FLOAT, " +
                COLUMN_MAIN_SAMPLING_PERIOD + " FLOAT " +
                ");";
        Log.i(tag, cooperateCompeteV2Query);




        //create cooperative competitive control v2 table
        String coopInternalWorkingsQuery = "CREATE TABLE " + TABLE_COOPERATIVE_INTERNAL_CALCS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_BREATHING_RETARDING_FACTOR + " FLOAT, " +
                COLUMN_BREATHING_POLLUTION_LEVEL + " FLOAT, " +
                COLUMN_BREATHING_COOPERATION_THRESHOLD + " FLOAT, " +
                COLUMN_BREATHING_RECENT_HUMAN_POWER + " FLOAT, " +
                COLUMN_BREATHING_RECENT_MOTOR_POWER + " FLOAT, " +
                COLUMN_BREATHING_F1 + " FLOAT, " +
                COLUMN_BREATHING_F2 + " FLOAT, " +
                COLUMN_BREATHING_F3 + " FLOAT, " +
                COLUMN_BREATHING_F4 + " FLOAT, " +
                COLUMN_BREATHING_NEXT_POWER_REFERENCE + " FLOAT, " +
                COLUMN_BREATHING_F1_CHARACTERISTIC_VALUE + " FLOAT, " +
                COLUMN_BREATHING_F1_FIRST_TERM + " FLOAT, " +
                COLUMN_BREATHING_F1_SECONDTERM + " FLOAT, " +
                COLUMN_BREATHING_F2_CHARACTERISTIC_VALUE + " FLOAT, " +
                COLUMN_BREATHING_F2_FIRST_TERM + " FLOAT, " +
                COLUMN_BREATHING_F2_SECONDTERM + " FLOAT, " +
                COLUMN_BREATHING_F3_CHARACTERISTIC_VALUE + " FLOAT, " +
                COLUMN_BREATHING_F3_FIRST_TERM + " FLOAT, " +
                COLUMN_BREATHING_F3_SECONDTERM + " FLOAT, " +
                COLUMN_BREATHING_F4_CHARACTERISTIC_VALUE + " FLOAT, " +
                COLUMN_BREATHING_F4_FIRST_TERM + " FLOAT, " +
                COLUMN_BREATHING_F4_SECONDTERM + " FLOAT " +
                ");";
        Log.i(tag, coopInternalWorkingsQuery);




        //how to execute SQL queries on android
        db.execSQL(bikeDataQuery);
        Log.i(tag, "bikeData table should be created");
        db.execSQL(caloriesBurnedQuery);
        Log.i(tag, "caloriesBurned table should be created");
        db.execSQL(heartRateQuery);
        Log.i(tag, "heartRate table should be created");
        db.execSQL(caloriesControlQuery);
        Log.i(tag, "calories control table should be created");
        db.execSQL(commandSentQuery);
        Log.i(tag, "commandSent table should be created");
        db.execSQL(breathingControlQuery);
        Log.i(tag, "breathingControl table should be created");
        db.execSQL(mTargetQuery);
        Log.i(tag, "mTarget table should be created");
        db.execSQL(breathingCooperativeControlQuery);
        Log.i(tag, "mTarget table should be created");
        db.execSQL(motorFilterQuery);
        Log.i(tag, "motor filter table should be created");
        db.execSQL(trafficLightQuery);
        Log.i(tag, "traffic light table should be created");
        db.execSQL(bikeLocationQuery);
        Log.i(tag, "bike location table should be created");
        db.execSQL(cooperateCompeteV2Query);
        Log.i(tag, "coooperate compete v2 table should be created");
        db.execSQL(motorPowerControlQuery);
        Log.i(tag, "motor power control table should be created");
        db.execSQL(coopInternalWorkingsQuery);
        Log.i(tag, "coop internal workings table table should be created");

    }

    //this is the method that is called if the DATABASE_VERSION variable gets incremented
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String bikeDataQuery = "DROP TABLE IF EXISTS " + TABLE_BIKEDATA + ";";
        db.execSQL(bikeDataQuery);

        String mBandQuery = "DROP TABLE IF EXISTS " + TABLE_CALORIESBURNED + ";";
        db.execSQL(mBandQuery);

        String heartRateQuery = "DROP TABLE IF EXISTS " + TABLE_HEARTRATE + ";";
        db.execSQL(heartRateQuery);

        String caloriesControlQuery = "DROP TABLE IF EXISTS " + TABLE_CALORIESCONTROL + ";";
        db.execSQL(caloriesControlQuery);

        String commandSentQuery = "DROP TABLE IF EXISTS " + TABLE_COMMANDSENT + ";";
        db.execSQL(commandSentQuery);

        String breathingControlQuery = "DROP TABLE IF EXISTS " + TABLE_BREATHINGCONTROL + ";";
        db.execSQL(breathingControlQuery);

        String mTargetQuery = "DROP TABLE IF EXISTS " + TABLE_MTARGET + ";";
        db.execSQL(mTargetQuery);

        String cooperativeBreathingControlQuery = "DROP TABLE IF EXISTS " + TABLE_COOPERATIVEBREATHINGCONTROL + ";";
        db.execSQL(cooperativeBreathingControlQuery);

        String motorFilterQuery = "DROP TABLE IF EXISTS " + TABLE_MOTORFILTER + ";";
        db.execSQL(motorFilterQuery);

        String trafficLightQuery = "DROP TABLE IF EXISTS " + TABLE_TRAFFIC_LIGHT + ";";
        db.execSQL(trafficLightQuery);

        String bikeLocationQuery = "DROP TABLE IF EXISTS " + TABLE_BIKE_LOCATION + ";";
        db.execSQL(bikeLocationQuery);

        String coopeateCompeteV2Query = "DROP TABLE IF EXISTS " + TABLE_COOPERATIVE_COMPETITIVE_CONTROL_V2 + ";";
        db.execSQL(coopeateCompeteV2Query);

        String motorPowerControlQuery = "DROP TABLE IF EXISTS " + TABLE_MOTOR_POWER_CONTROL + ";";
        db.execSQL(motorPowerControlQuery);

        String coopInternalWorkingsQuery = "DROP TABLE IF EXISTS " + TABLE_COOPERATIVE_INTERNAL_CALCS + ";";
        db.execSQL(coopInternalWorkingsQuery);


        onCreate(db);
    }

    private String getDateTime() {
        //There is a problem with using Android SimpleDateFormat - android wants latest API which seems to be a bug
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    //Add a new row to the bikedata table
    public void addBifurcationDataInternalWorkings(BifurcationData bifurcationData) {
        //Use ContentValues for Bikedata table
        ContentValues bifurcationDataValues = new ContentValues();
        bifurcationDataValues.put(COLUMN_TIME, getDateTime());
        bifurcationDataValues.put(COLUMN_BREATHING_RETARDING_FACTOR, bifurcationData.get_retardingFactor());
        bifurcationDataValues.put(COLUMN_BREATHING_POLLUTION_LEVEL, bifurcationData.get_pollutionLevel());
        bifurcationDataValues.put(COLUMN_BREATHING_COOPERATION_THRESHOLD, bifurcationData.get_cooperationThreshold());
        bifurcationDataValues.put(COLUMN_BREATHING_RECENT_HUMAN_POWER, bifurcationData.get_recentHumanPower());
        bifurcationDataValues.put(COLUMN_BREATHING_RECENT_MOTOR_POWER, bifurcationData.get_recentMotorPower());
        bifurcationDataValues.put(COLUMN_BREATHING_F1, bifurcationData.get_f1());
        bifurcationDataValues.put(COLUMN_BREATHING_F2, bifurcationData.get_f2());
        bifurcationDataValues.put(COLUMN_BREATHING_F3, bifurcationData.get_f3());
        bifurcationDataValues.put(COLUMN_BREATHING_F4, bifurcationData.get_f4());
        bifurcationDataValues.put(COLUMN_BREATHING_NEXT_POWER_REFERENCE, bifurcationData.get_nextOutputReferencePower());
        bifurcationDataValues.put(COLUMN_BREATHING_F1_CHARACTERISTIC_VALUE, bifurcationData.get_f1_characteristicvalue());
        bifurcationDataValues.put(COLUMN_BREATHING_F1_FIRST_TERM, bifurcationData.get_f1_firstterm());
        bifurcationDataValues.put(COLUMN_BREATHING_F1_SECONDTERM, bifurcationData.get_f1_secondterm());
        bifurcationDataValues.put(COLUMN_BREATHING_F2_CHARACTERISTIC_VALUE, bifurcationData.get_f2_characteristicvalue());
        bifurcationDataValues.put(COLUMN_BREATHING_F2_FIRST_TERM, bifurcationData.get_f2_firstterm());
        bifurcationDataValues.put(COLUMN_BREATHING_F2_SECONDTERM, bifurcationData.get_f2_secondterm());
        bifurcationDataValues.put(COLUMN_BREATHING_F3_CHARACTERISTIC_VALUE, bifurcationData.get_f3_characteristicvalue());
        bifurcationDataValues.put(COLUMN_BREATHING_F3_FIRST_TERM, bifurcationData.get_f3_firstterm());
        bifurcationDataValues.put(COLUMN_BREATHING_F3_SECONDTERM, bifurcationData.get_f3_secondterm());
        bifurcationDataValues.put(COLUMN_BREATHING_F4_CHARACTERISTIC_VALUE, bifurcationData.get_f4_characteristicvalue());
        bifurcationDataValues.put(COLUMN_BREATHING_F4_FIRST_TERM, bifurcationData.get_f4_firstterm());
        bifurcationDataValues.put(COLUMN_BREATHING_F4_SECONDTERM, bifurcationData.get_f4_secondterm());



        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        long errorCheck = 0;
        db.insert(TABLE_COOPERATIVE_INTERNAL_CALCS, null, bifurcationDataValues);

        // Use this to keep the DB table size quite small
        //db.delete(TABLE_BIKEDATA, "WHERE ? IN (SELECT ? FROM ? ORDER BY ? ASC LIMIT -1 OFFSET 100)", new String[]{"id", "id", TABLE_BIKEDATA, "id"});
        //db.execSQL("DELETE FROM " + TABLE_BIKEDATA + " WHERE _id IN (SELECT _id FROM " + TABLE_BIKEDATA + " ORDER BY _id DESC LIMIT -1 OFFSET 100)"); //TODO:
        //close database
//        db.close();
    }


    //Add a new row to the coop internal workings table
    public void addBikeDataRow(BikeData bikeData) {
        //Use ContentValues for Bikedata table
        ContentValues bikeDataValues = new ContentValues();
        bikeDataValues.put(COLUMN_TIME, getDateTime());
        bikeDataValues.put(COLUMN_BATTERYENERGY, bikeData.get_batteryEnergy());
        bikeDataValues.put(COLUMN_VOLTAGE, bikeData.get_voltage());
        bikeDataValues.put(COLUMN_CURRENT, bikeData.get_current());
        bikeDataValues.put(COLUMN_SPEED, bikeData.get_speed());
        bikeDataValues.put(COLUMN_DISTANCE, bikeData.get_distance());
        bikeDataValues.put(COLUMN_TEMPERATURE, bikeData.get_temperature());
        bikeDataValues.put(COLUMN_RPM, bikeData.get_RPM());
        bikeDataValues.put(COLUMN_HUMANPOWER, bikeData.get_humanPower());
        bikeDataValues.put(COLUMN_TORQUE, bikeData.get_torque());
        bikeDataValues.put(COLUMN_THROTTLEIN, bikeData.get_throttleIn());
        bikeDataValues.put(COLUMN_THROTTLEOUT, bikeData.get_throttleOut());
        bikeDataValues.put(COLUMN_ACCELERATION, bikeData.get_acceleration());
        bikeDataValues.put(COLUMN_FLAG, bikeData.get_flag());


        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        long errorCheck = 0;
        db.insert(TABLE_BIKEDATA, null, bikeDataValues);

        // Use this to keep the DB table size quite small
        //db.delete(TABLE_BIKEDATA, "WHERE ? IN (SELECT ? FROM ? ORDER BY ? ASC LIMIT -1 OFFSET 100)", new String[]{"id", "id", TABLE_BIKEDATA, "id"});
        //db.execSQL("DELETE FROM " + TABLE_BIKEDATA + " WHERE _id IN (SELECT _id FROM " + TABLE_BIKEDATA + " ORDER BY _id DESC LIMIT -1 OFFSET 100)"); //TODO:
        //close database
//        db.close();
    }


    //Add a new row to the bikedata table
    public void addNewCoopCompeteV2(CoopCompeteV2Data coopCompeteV2Data) {
        //Use ContentValues for Bikedata table
        ContentValues coopCompeteV2DataValues = new ContentValues();
        coopCompeteV2DataValues.put(COLUMN_TIME, getDateTime());
        coopCompeteV2DataValues.put(COLUMN_RECENT_HUMAN_POWER_AVG, coopCompeteV2Data.get_recentHumanPowerAvg());
        coopCompeteV2DataValues.put(COLUMN_RECENT_MOTOR_POWER_AVG, coopCompeteV2Data.get_recentMotorPowerAvg());
        coopCompeteV2DataValues.put(COLUMN_MOTOR_OUTPUT_REFERENCE, coopCompeteV2Data.get_motorOutputPowerReference());
        coopCompeteV2DataValues.put(COLUMN_POLLUTION_LEVEL, coopCompeteV2Data.get_pollutionLevel());
        coopCompeteV2DataValues.put(COLUMN_REQUEST_TO_MOTOR, coopCompeteV2Data.get_requestToMotor());
        coopCompeteV2DataValues.put(COLUMN_M_TARGET, coopCompeteV2Data.get_mTarget());
        coopCompeteV2DataValues.put(COLUMN_M_ACTUAL, coopCompeteV2Data.get_mActual());
        coopCompeteV2DataValues.put(COLUMN_MAIN_SAMPLING_PERIOD, coopCompeteV2Data.get_mainSamplindPeriod());



        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        long errorCheck = 0;
        db.insert(TABLE_COOPERATIVE_COMPETITIVE_CONTROL_V2, null, coopCompeteV2DataValues);

        // Use this to keep the DB table size quite small
        //db.delete(TABLE_BIKEDATA, "WHERE ? IN (SELECT ? FROM ? ORDER BY ? ASC LIMIT -1 OFFSET 100)", new String[]{"id", "id", TABLE_BIKEDATA, "id"});
        //db.execSQL("DELETE FROM " + TABLE_BIKEDATA + " WHERE _id IN (SELECT _id FROM " + TABLE_BIKEDATA + " ORDER BY _id DESC LIMIT -1 OFFSET 100)"); //TODO:
        //close database
//        db.close();
    }






    public void addTrafficLightDataRow(TrafficLightData trafficLightData) {
        ContentValues trafficLightDataValues = new ContentValues();
        trafficLightDataValues.put(COLUMN_TIME, getDateTime());
        trafficLightDataValues.put(COLUMN_TRAFFIC_LIGHT_SIGNAL, trafficLightData.getTrafficLightStatus().toString());
        trafficLightDataValues.put(COLUMN_TRAFFIC_LIGHT_LATITUDE, trafficLightData.getLatitude());
        trafficLightDataValues.put(COLUMN_TRAFFIC_LIGHT_LONGITUDE, trafficLightData.getLongitude());

        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_TRAFFIC_LIGHT, null, trafficLightDataValues);

        // Close database
//        db.close();
    }

    public void addBikeLocation(float latitude, float longitude) {
        ContentValues bikeLocationDataValues = new ContentValues();
        bikeLocationDataValues.put(COLUMN_BIKE_LOCATION_LATITUDE, latitude);
        bikeLocationDataValues.put(COLUMN_TRAFFIC_LIGHT_LONGITUDE, longitude);

        SQLiteDatabase db = getWritableDatabase();

        db.insert(TABLE_BIKE_LOCATION, null, bikeLocationDataValues);

//        db.close();
    }

    //Add a new row to the database
    public void addMBandRow(MicrosoftBandData mBandData) {
        ContentValues mBandValues = new ContentValues();
        mBandValues.put(COLUMN_TIME, getDateTime());

        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        switch (mBandData.get_category()) {
            case "heartRate":
                Log.i(tag, "MyDBH: heart rate case");
                mBandValues.put(COLUMN_HEARTRATE, mBandData.get_heartRate());
                //insert new row database
                db.insert(TABLE_HEARTRATE, null, mBandValues);
                break;
            case "calories":
                Log.i(tag, "MyDBH: calories case");
                mBandValues.put(COLUMN_CALORIESBURNED, mBandData.get_caloriesBurned());
                //insert new row database
                db.insert(TABLE_CALORIESBURNED, null, mBandValues);
                break;
            default:
                Log.i(tag, "default case");
                break;
        }

        //close database
//        db.close();


    }

    //Add a new row to the database which stores variables from closed loop feedback control - targets, errors etc.
    public void addCaloriesControlData(CaloriesControlData caloriesControlData) {
        ContentValues caloriesControlValue = new ContentValues();
        caloriesControlValue.put(COLUMN_TIME, getDateTime());
        caloriesControlValue.put(COLUMN_COMMANDSENT, caloriesControlData.get_commandSent());
        caloriesControlValue.put(COLUMN_ACTUALCALORIESBURNEDRATE, caloriesControlData.get_actualCaloriesBurnedRate());
        caloriesControlValue.put(COLUMN_TARGETCALORIESRATE, caloriesControlData.get_targetCaloriesRate());
        caloriesControlValue.put(COLUMN_ERRORCALORIES, caloriesControlData.get_errorCalories());
        caloriesControlValue.put(COLUMN_GAINPARAMETER, caloriesControlData.get_gainParameter());


        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        db.insert(TABLE_CALORIESCONTROL, null, caloriesControlValue);

        //close database
//        db.close();
    }

    public void addCommandSentRow(CommandSentData commandSentData) {
        ContentValues commandSentValues = new ContentValues();
        commandSentValues.put(COLUMN_TIME, getDateTime());
        commandSentValues.put(COLUMN_COMMANDSENT, commandSentData.get_commandSent());

        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        db.insert(TABLE_COMMANDSENT, null, commandSentValues);

        //close database
//        db.close();
    }

    public void addMTargetRow(Float mTarget) {
        ContentValues mTargetValues = new ContentValues();
        mTargetValues.put(COLUMN_TIME, getDateTime());
        mTargetValues.put(COLUMN_MTARGET, mTarget);

        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        db.insert(TABLE_MTARGET, null, mTargetValues);

        //close database
//        db.close();
    }

    public void addMotorFilterRow(int count) {
        ContentValues motorCountValues = new ContentValues();
        motorCountValues.put(COLUMN_TIME, getDateTime());
        motorCountValues.put(COLUMN_COUNT, count);

        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        db.insert(TABLE_MOTORFILTER, null, motorCountValues);

        //close database
//        db.close();
    }

    //Add a new row to the database which stores variables from closed loop feedback control - targets, errors etc.
    public void addBreathingControlData(BreathingControlData breathingControlData) {
        ContentValues breathingControlValue = new ContentValues();
        breathingControlValue.put(COLUMN_TIME, getDateTime());
        breathingControlValue.put(COLUMN_COMMANDSENT, breathingControlData.get_commandSent());
        breathingControlValue.put(COLUMN_BREATHING_GAINPARAMETER, breathingControlData.get_gainParameter());
        breathingControlValue.put(COLUMN_BREATHING_HUMANPOWER_AVG, breathingControlData.get_humanPowerAvg());
        breathingControlValue.put(COLUMN_BREATHING_MOTORPOWER_AVG, breathingControlData.get_motorPowerAvg());
        breathingControlValue.put(COLUMN_BREATHING_MTARGET, breathingControlData.get_mTarget());
        breathingControlValue.put(COLUMN_BREATHING_MACTUAL_AVG, breathingControlData.get_mActualAvg());
        breathingControlValue.put(COLUMN_BREATHING_ERROR, breathingControlData.get_error());
        breathingControlValue.put(COLUMN_BREATHING_SAMPLINGPERIOD, breathingControlData.get_samplingPeriod());


        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        db.insert(TABLE_BREATHINGCONTROL, null, breathingControlValue);

        // Use this to keep the DB table size quite small
        //db.delete(TABLE_BREATHINGCONTROL, "WHERE ROWID IN (SELECT ROWID FROM " + TABLE_BREATHINGCONTROL + " ORDER BY ROWID ASC LIMIT -1 OFFSET 100)", null);
        //db.execSQL("DELETE FROM " + TABLE_BREATHINGCONTROL + " WHERE _id IN (SELECT _id FROM " + TABLE_BREATHINGCONTROL + " ORDER BY _id DESC LIMIT -1 OFFSET 100)"); //TODO:

        // Close database
//        db.close();
    }


    //Add a new row for motor power control data
    public void addMotorPowerControlData(MotorPowerControlData motorPowerControlData) {
        ContentValues motorPowerControlValue = new ContentValues();
        motorPowerControlValue.put(COLUMN_TIME, getDateTime());
        motorPowerControlValue.put(COLUMN_COMMANDSENT, motorPowerControlData.get_commandSent());
        motorPowerControlValue.put(COLUMN_BREATHING_GAINPARAMETER, motorPowerControlData.get_gainParameter());
        motorPowerControlValue.put(COLUMN_BREATHING_HUMANPOWER_AVG, motorPowerControlData.get_humanPowerAvg());
        motorPowerControlValue.put(COLUMN_BREATHING_MOTORPOWER_AVG, motorPowerControlData.get_motorPowerAvg());
        motorPowerControlValue.put(COLUMN_BREATHING_MOTORPOWER_TARGET, motorPowerControlData.get_motorPowerTarget());
        motorPowerControlValue.put(COLUMN_BREATHING_MOTORPOWER_ERROR, motorPowerControlData.get_motorPowerError());
        motorPowerControlValue.put(COLUMN_BREATHING_SAMPLINGPERIOD, motorPowerControlData.get_samplingPeriod());


        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        db.insert(TABLE_MOTOR_POWER_CONTROL, null, motorPowerControlValue);

        // Use this to keep the DB table size quite small
        //db.delete(TABLE_BREATHINGCONTROL, "WHERE ROWID IN (SELECT ROWID FROM " + TABLE_BREATHINGCONTROL + " ORDER BY ROWID ASC LIMIT -1 OFFSET 100)", null);
        //db.execSQL("DELETE FROM " + TABLE_BREATHINGCONTROL + " WHERE _id IN (SELECT _id FROM " + TABLE_BREATHINGCONTROL + " ORDER BY _id DESC LIMIT -1 OFFSET 100)"); //TODO:

        // Close database
//        db.close();
    }

    public void addCooperativeControlData(CooperativeBreathingControlData cooperativeBreathingControlData) {
        ContentValues cooperativeControlValue = new ContentValues();
        cooperativeControlValue.put(COLUMN_TIME, getDateTime());
        cooperativeControlValue.put(COLUMN_COMMANDSENT, cooperativeBreathingControlData.get_commandSent());
        cooperativeControlValue.put(COLUMN_BREATHING_MTARGET, cooperativeBreathingControlData.get_mTarget());
        cooperativeControlValue.put(COLUMN_BREATHING_SAMPLINGPERIOD, cooperativeBreathingControlData.get_samplingPeriod());
        cooperativeControlValue.put(COLUMN_BREATHING_FM, cooperativeBreathingControlData.get_fm());

        //db equals database we are going to write to
        SQLiteDatabase db = getWritableDatabase();

        //insert new row database
        db.insert(TABLE_COOPERATIVEBREATHINGCONTROL, null, cooperativeControlValue);

        //close database
//        db.close();
    }

    //    public void addCoopCompeteControlData(CoopCompeteControlData coopCompeteControlData){
//        ContentValues coopCompeteValue = new ContentValues();
//        coopCompeteValue.put(COLUMN_TIME, getDateTime());
//        coopCompeteValue.put(COLUMN_COMMANDSENT, coopCompeteControlData.getCommandSent());
//        coopCompeteValue.put(COLUMN_MTARGET, coopCompeteControlData.getMTarget());
//        coopCompeteValue.put(COLUMN_MACTUAL, coopCompeteControlData.getMActual());
//        coopCompeteValue.put(COLUMN_MOTOR_POWER_REF, coopCompeteControlData.getMotorPowerReference());
//        coopCompeteValue.put(COLUMN_MOTOR_POWER_AVG, coopCompeteControlData.getMotorPowerAvg());
//
//        SQLiteDatabase db = getWritableDatabase();
//
//        db.insert(TABLE_COOP_COMPETE_CONTROL, null, coopCompeteValue);
//
//        db.close();
//    }

    //Delete row from database - method is not in use but this is how it would be done
//    public void deleteRow(String bikeDataName){
//        SQLiteDatabase db = getWritableDatabase();
//        db.execSQL("DELETE FROM "+ TABLE_BIKEDATA + " WHERE " + COLUMN_PRODUCTNAME + " = \"" + bikeDataName + "\";");
//    }

    //Print out certain columns of bike data table as string
    public String getBikeData() {
        SQLiteDatabase db = getWritableDatabase();
        String dbString = "";
        String query = "SELECT * FROM " + TABLE_BIKEDATA + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Move to first row in results
        c.moveToFirst();
        //Log.i(tag, "Bikedata cursor position is " + c.getPosition());
        //Log.i(tag, "Bikedata cursor count is " + c.getCount());

        //make sure there are still some results to go
        while (!c.isAfterLast()) {
            //if(c.getString(c.getColumnIndex("productname"))!=null){
            if (c.getString(c.getColumnIndex("time")) != null) {
                dbString += "Time: " + c.getString(c.getColumnIndex("time"));
                dbString += " \n";
                Log.i(tag, dbString);
            }
            c.moveToNext();
        }
        //Log.i(tag, "Cursor position is " + c.getPosition());
//        db.close();

        return dbString;
    }

    //Print out microsoft band calories table as string
    public String getMBandData() {
        int startCursorPosition = 0;
        SQLiteDatabase db = getWritableDatabase();
        String dbString = "";
        String query = "SELECT * FROM " + TABLE_CALORIESBURNED + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Move to last row (most recently saved) in results
        c.moveToLast();
        startCursorPosition = c.getCount();
        Log.i(tag, "Cursor position is " + c.getPosition());
        Log.i(tag, "Cursor count is " + c.getCount());

        //make sure there are still some results to go
        while (!c.isBeforeFirst()) {
            if (c.getString(c.getColumnIndex("time")) != null) {
                dbString += "Time: " + c.getString(c.getColumnIndex("time"));
                dbString += " \n";
            }
            if (c.getString(c.getColumnIndex("caloriesBurned")) != null) {
                dbString += "Calories: " + c.getString(c.getColumnIndex("caloriesBurned"));
                dbString += " \n";
                Log.i(tag, c.getString(c.getColumnIndex("caloriesBurned")));
            }
            c.moveToPrevious();
        }
//        db.close();

        return dbString;
    }

    //this method returns a treemap containing recent values of data saved to database - the reason why
    //it is formatted as a treemap is in case the control method needs several recent values of data e.g.
    //the 5 most recent values of heart rate to be used in the control loop, so it must be returned in
    //a structured format
    public Map getRecentCaloriesData() {
        int count = 0;
        int numIterations = 1;
        float caloriesBurned;
        String dateString;

        //Create TreeMap to store data retrieved from database
        Map<Date, Float> m = new TreeMap<Date, Float>();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        //Check if calories data has been synced recently - to be implemented


        //Get most recent data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CALORIESBURNED + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Get most recent results
        c.moveToLast();

        //set numIterations above to choose how many results are needed
        while (count < numIterations) {
            if (c.getString(c.getColumnIndex("caloriesBurned")) != null) {
                caloriesBurned = Float.parseFloat(c.getString(c.getColumnIndex("caloriesBurned")));
                Log.i(tag, "MyDB: Calories burned is: " + caloriesBurned);

                //format string to datetime
                Date date = new Date();
                dateString = c.getString(c.getColumnIndex("time"));
                try {
                    date = dateFormat.parse(dateString);
                    m.put(date, caloriesBurned);
                } catch (ParseException e) {
                    Log.i(tag, "catch error");
                    e.printStackTrace();
                }
            }
            c.moveToPrevious();
            count += 1;
        }

//        db.close();

        //return the HashMap object
        return m;
    }

    //this method returns a treemap containing recent values of data saved to database - the reason why
    //it is formatted as a treemap is in case the control method needs several recent values of data e.g.
    //the 5 most recent values of heart rate to be used in the control loop, so it must be returned in
    //a structured format
    public List<float[]> getRecentBikeData(float motorEfficiency, float torqueBias, float cranksetEfficiency, float humanPowerFactor, int windowSize) {
        int count = 0;
        int numIterations;
        float voltage; //Volts
        float current; //Amps
        float humanPower; //Watts
        float torque;
        float rpm;
        float lastRequestToBike;
        float[] motorPowerArray;
        float[] humanPowerArray;
        float pi = (float) Math.PI;
        float gamma1 = 0.1640f;
        float gamma2 = -12.8473f;
        List<float[]> bikeDataList = null;
        String dateString;

        //Create TreeMap to store data retrieved from database
        // Map<Date, Float[]> m = new TreeMap<Date, Float[]>();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Check if data has been synced recently - to be implemented

        //Get most recent data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BIKEDATA + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Get most recent results
        boolean moveToLastSucceeded = c.moveToLast();

        // Set numIterations above to choose how many results are needed
        numIterations = windowSize;
        motorPowerArray = new float[numIterations];
        humanPowerArray = new float[numIterations];

        //need most recent command sent to bike also
        lastRequestToBike = getLastRequestToBike();

        // Check recent data has synced / we are not analysing old data (based on datetime)
        for (int i = 0; i < numIterations; i++) {
            if (c.getString(c.getColumnIndex("voltage")) != null) {
                // Calculate motor power
                voltage = Float.parseFloat(c.getString(c.getColumnIndex("voltage")));
                current = Float.parseFloat(c.getString(c.getColumnIndex("current")));
                float motorPower = Math.max(voltage * current * motorEfficiency - (lastRequestToBike * gamma1 + gamma2), 0);

                // Calculate human power
                torque = Float.parseFloat(c.getString(c.getColumnIndex("torque")));
                rpm = Float.parseFloat(c.getString(c.getColumnIndex("RPM")));
                //Log.i(tag, "humanCalculated is " + humanPowerFactor * cranksetEfficiency * (torque - torqueBias) * rpm * 2 * pi / 60);
                //humanPower = Math.max(humanPowerFactor * cranksetEfficiency * (torque - torqueBias) * rpm * 2 * pi / 60, 0);
                humanPower = Math.max(humanPowerFactor * cranksetEfficiency * (torque - 0) * rpm * 2 * pi / 60, 0);

                motorPowerArray[i] = motorPower;
                humanPowerArray[i] = humanPower;

                //format string to datetime
//                Date date = new Date();
//                dateString = c.getString(c.getColumnIndex("time"));
//                try {
//                    date = dateFormat.parse(dateString);
//                    m.put(date, new Float[] {motorPower, humanPower});
//                } catch (ParseException e) {
//                    Log.i(tag, "catch error");
//                    e.printStackTrace();
//                }
//                Log.i(tag, "end of if");
            }
            c.moveToPrevious();
            //count +=1;

        }
        //   Log.i(tag, "outside for");

        bikeDataList = new ArrayList<>();
        bikeDataList.add(motorPowerArray);
        bikeDataList.add(humanPowerArray);

//        db.close();

        //return the HashMap object
        return bikeDataList;
    }

    //method to get most recent speed from database - not used for anything currently but could be used as speedometer
    public float getLatestSpeed() {
        float latestSpeed = 0;

        //Get most recent speed data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BIKEDATA + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Get most recent result
        c.moveToLast();

        //set numIterations above to choose how many results are needed
        if (c.getString(c.getColumnIndex("speed")) != null) {
            latestSpeed = Float.parseFloat(c.getString(c.getColumnIndex("speed")));
        }

//        db.close();

        //return the latest speed
        return latestSpeed;
    }

    //get most recent motor power target
    public float getMotorPowerTarget() {
//        Log.i(tag, "broooo");
        float latestMotorPowerTarget = 0;

        //Get most recent speed data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MOTOR_POWER_CONTROL + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Get most recent result

       // Log.i(tag, "what is this: "+ c.getString(c.getColumnIndex("motorPowerTarget")));

        //set numIterations above to choose how many results are needed
        if (c == null || c.getCount() == 0 ) {
            return 0;

        }
        else{
//            Log.i(tag, "BROOOO:");
//            Log.i(tag, "Count: "+ c.getCount());
            c.moveToLast();
            latestMotorPowerTarget = Float.parseFloat(c.getString(c.getColumnIndex("motorPowerTarget")));
//            db.close();
            //return the latest speed
            return latestMotorPowerTarget;

        }



    }

    public TrafficLightData getLatestTrafficLightData() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TRAFFIC_LIGHT + " WHERE 1;";

        // Cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        c.moveToLast();

        TrafficLightStatus mostRecentTrafficLightStatus = TrafficLightStatus.Red;
        float mostRecentLatitude;
        float mostRecentLongitude;

        if (c.getString(c.getColumnIndex(COLUMN_TRAFFIC_LIGHT_SIGNAL)) != null) {
            String trafficLightSignalString = c.getString(c.getColumnIndex(COLUMN_TRAFFIC_LIGHT_SIGNAL));

            switch (trafficLightSignalString) {
                case "Red":
                    mostRecentTrafficLightStatus = TrafficLightStatus.Red;
                    break;
                case "Amber":
                    mostRecentTrafficLightStatus = TrafficLightStatus.Amber;
                    break;
                case "Green":
                    mostRecentTrafficLightStatus = TrafficLightStatus.Green;
                    break;
                default:
                    Log.i(tag, "Invalid signal colour was written to the database, " + trafficLightSignalString);
                    return null;
            }
        } else {
            Log.i(tag, "Signal colour is null");
            return null;
        }

        if (c.getString(c.getColumnIndex(COLUMN_TRAFFIC_LIGHT_LATITUDE)) != null) {
            mostRecentLatitude = Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_TRAFFIC_LIGHT_LATITUDE)));
        } else {
            Log.i(tag, "Latitude is null");
            return null;
        }

        if (c.getString(c.getColumnIndex(COLUMN_TRAFFIC_LIGHT_LONGITUDE)) != null) {
            mostRecentLongitude = Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_TRAFFIC_LIGHT_LONGITUDE)));
        } else {
            Log.i(tag, "Longitude is null");
            return null;
        }

        TrafficLightData mostRecentTrafficLightData = new TrafficLightData(mostRecentTrafficLightStatus, mostRecentLatitude, mostRecentLongitude);

        return mostRecentTrafficLightData;
    }

    public SimpleLocation getLatestBikeLocation() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BIKE_LOCATION + " WHERE 1;";
        float latitude;
        float longitude;
        // Cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        c.moveToLast();

        if (c.getString(c.getColumnIndex(COLUMN_BIKE_LOCATION_LATITUDE)) != null) {
            latitude = Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_BIKE_LOCATION_LATITUDE)));
        } else {
            return null;
        }

        if (c.getString(c.getColumnIndex(COLUMN_BIKE_LOCATION_LONGITUDE)) != null) {
            longitude = Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_BIKE_LOCATION_LONGITUDE)));
        } else {
            return null;
        }

        SimpleLocation currentLocation = new SimpleLocation(latitude, longitude);

        return currentLocation;
    }

    //method to get most recent mTarget value
    public float getLatestMTarget() {
        float mTarget = 0;
        //Log.i(tag, "start of getLatestMTarget");

        //Get most recent speed data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MTARGET + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Get most recent result
        c.moveToLast();

        //set numIterations above to choose how many results are needed
        if (c.getString(c.getColumnIndex("mTarget")) != null) {
            mTarget = Float.parseFloat(c.getString(c.getColumnIndex("mTarget")));
        }

//        db.close();
        //Log.i(tag, "end of getLatestMTarget:"+mTarget);
        //return the latest speed
        return mTarget;

    }

    //method to get most recent motor count value
    public int getLatestMotorCount() {
        int count = 0;

        //Get most recent speed data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MOTORFILTER + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);


        //set numIterations above to choose how many results are needed
        if (c == null || c.getCount() == 0 ) {
            return 0;
        } else{

            //Get most recent result
            c.moveToLast();

            count = Integer.parseInt(c.getString(c.getColumnIndex("count")));
//            db.close();
            return count;
        }

    }

    public MValuePlottingData getLatestMActual() {
        float mActual = 0;
        String timeString = "";
        java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = null;

        //Get most recent speed data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BREATHINGCONTROL + " WHERE 1;";

        // cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Get most recent result
        c.moveToLast();

        //set numIterations above to choose how many results are needed
        if (c.getString(c.getColumnIndex(COLUMN_BREATHING_MACTUAL_AVG)) != null) {
            mActual = Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_BREATHING_MACTUAL_AVG)));
        }

        if(c.getString(c.getColumnIndex(COLUMN_TIME)) != null){
            timeString = c.getString(c.getColumnIndex(COLUMN_TIME));
        }

        try{
            date = parser.parse(timeString);
        }
        catch(ParseException e){
            Log.e(tag, "Unable to parse the date." + e.toString());
        }

//        db.close();

        return new MValuePlottingData(date, mActual);
    }

    public PlottingData getRecentPlottingData(int numRowsToProvide) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_BREATHINGCONTROL + " WHERE 1;";

        // Cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        // Get most recent results
        boolean moveToLastSucceeded = c.moveToLast();
        ArrayList<Float> recentMActual = new ArrayList<>();
        ArrayList<Float> recentMTarget= new ArrayList<>();

        for(int i = 0;i<numRowsToProvide;i++){
            if(c.getString(c.getColumnIndex(COLUMN_BREATHING_MACTUAL_AVG)) != null){
                float mActualAverage = Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_BREATHING_MACTUAL_AVG)));
                recentMActual.add(mActualAverage);
                c.moveToPrevious();
            }
            else{
                // No data / no more data remaining
                break;
            }
        }

        moveToLastSucceeded = c.moveToLast();

        for(int i = 0;i<numRowsToProvide;i++){
            if(c.getString(c.getColumnIndex(COLUMN_BREATHING_MTARGET)) != null){
                float mTargetAverage = Float.parseFloat(c.getString(c.getColumnIndex(COLUMN_BREATHING_MTARGET)));
                recentMTarget.add(mTargetAverage);
                c.moveToPrevious();
            }
            else{
                // No data / no more data remaining
                break;
            }
        }

        //PlottingData plottingData = new PlottingData(recentMTarget, recentMActual);
//        db.close();
        return null;
    }

    //get last request sent to bike for control implementation
    public float getLastRequestToBike() {
        float lastRequestToBike = 0;
        int count = 0;

        //Get most recent data
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_COMMANDSENT + " WHERE 1;";

        //change query to pull back only required data when it is working
        //String query = "SELECT COLUMN_COMMANDSENT FROM " + TABLE_COMMANDSENT + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Get most recent results
        boolean moveToLastSucceeded = c.moveToLast();


        if (moveToLastSucceeded) {
            //Log.i(tag, "Move to last succeeded");
            while (count < 1) {
                if (c.getString(c.getColumnIndex("commandSent")) != null) {
                    lastRequestToBike = Float.parseFloat(c.getString(c.getColumnIndex("commandSent")));
                }
                count = 1;
            }
        }

//        db.close();

        return lastRequestToBike;
    }

    //get values saved in database relating to closed loop calories control
    public String getCaloriesControlData() {
        String dbString = "";
        int count = 0;
        int numIterations = 10;

        int startCursorPosition = 0;
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_CALORIESCONTROL + " WHERE 1;";

        //cursor points to a location in results
        Cursor c = db.rawQuery(query, null);

        //Move to first row in results
        c.moveToLast();

        //column names for the retrieved data which will be formatted in while loop below - has the effect of printing as a table
        dbString += "Time" + "\t" + "commandSent" + "\t" + "actualCaloriesBurnedRate" + "\t" + "targetCaloriesBurnedRate" + "\t" +
                "errorCalories" + "\t" + "gainParameter" + "\n\n";

        while (count < numIterations) {
            if (c.getString(c.getColumnIndex("time")) != null) {
                dbString += c.getString(c.getColumnIndex("time"));
                dbString += " \n";
            }
            if (c.getString(c.getColumnIndex("commandSent")) != null) {
                dbString += c.getString(c.getColumnIndex("commandSent"));
                dbString += " \t";
            }

            if (c.getString(c.getColumnIndex("actualCaloriesBurnedRate")) != null) {
                dbString += c.getString(c.getColumnIndex("actualCaloriesBurnedRate"));
                dbString += " \t";
            }

            if (c.getString(c.getColumnIndex("targetCaloriesBurnedRate")) != null) {
                dbString += c.getString(c.getColumnIndex("targetCaloriesBurnedRate"));
                dbString += " \t";
            }

            if (c.getString(c.getColumnIndex("errorCalories")) != null) {
                dbString += c.getString(c.getColumnIndex("errorCalories"));
                dbString += " \t";
            }

            if (c.getString(c.getColumnIndex("gainParameter")) != null) {
                dbString += c.getString(c.getColumnIndex("gainParameter"));
                dbString += " \n";
            }

            c.moveToPrevious();
            count += 1;

        }
//        db.close();

        return dbString;
    }
}
