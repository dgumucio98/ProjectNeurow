package com.ti.neurow;

import java.util.ArrayList;

public class GlobalVariables {
    public static String loggedInUsername; // keeps track of what user is "logged in"
    public static ArrayList<Double> finalListTimePower; // populated once a workout activity concludes


    // Dataframe 33 stuff
    public static Double elapsedTime33 = 0.0;
    public static Integer intervalCount33 = 0;
    public static Integer averagePower33 = 0;
    public static Integer totalCalories33 = 0;
    public static Double splitIntAvgPace33 = 0.0;
    public static Integer splitIntAvgPwr33 = 0;
    public static Double splitIntAvgCal33 = 0.0; //TODO discrepancy between int and double? between ble and db
    public static Double lastSplitTime33 = 0.0;
    public static Double lastSplitDist33 = 0.0; //TODO discrepancy between int and double? between ble and db
    public static VariableChanges globalTimeInstance33;

    // Dataframe 35 stuff
    public static Double elapsedTime35 = 0.0;
    public static Double distance35 = 0.0;
    public static Double driveLength35 = 0.0;
    public static Double driveTime35 = 0.0;
    public static Double strokeRecTime35 = 0.0;
    public static Double strokeDistance35 = 0.0;
    public static Double peakDriveForce35 = 0.0;
    public static Double averageDriveForce35 = 0.0;
    public static Double workPerStroke35 = 0.0;
    public static Integer strokeCount35 = 0;
    public static VariableChanges globalTimeInstance35;

    // Dataframe 3D stuff
    public static Integer pol3D;
    public static String message3D;
    public static VariableChanges globalTimeInstance3D;

    // Others
    public static Integer failCount;
    public static Integer ftp = 45; // default FTP value
//TODO change pz vals
    public static Integer pz_1 = 0; // default FTP value
    public static Integer pz_2 = 25; // default FTP value
    public static Integer pz_3 = 50; // default FTP value 34
    public static Integer pz_4 = 100; // default FTP value 40
    public static Integer pz_5 = 47; // default FTP value
    public static Integer pz_6 = 54; // default FTP value
    public static Integer pz_7 = 67; // default FTP value

}
