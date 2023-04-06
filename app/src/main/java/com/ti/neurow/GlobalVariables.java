package com.ti.neurow;

import java.util.ArrayList;

public class GlobalVariables {
    public static String loggedInUsername; // keeps track of what user is "logged in"
    public static ArrayList<Double> finalListTimePower; // populated once a workout activity concludes
    public static Integer failCount;
    public static Integer ftp;

    public static Double elapsedTime33;
    public static Integer intervalCount33;
    public static Integer averagePower33;
    public static Integer totalCalories33;
    public static Double splitIntAvgPace33;
    public static Integer splitIntAvgPwr33;
    public static Double splitIntAvgCal33; //TODO discrepancy between int and double? between ble and db
    public static Double lastSplitTime33;
    public static Double lastSplitDist33; //TODO discrepancy between int and double? between ble and db
    public static VariableChanges globalTimeInstance33;


    public static Double elapsedTime35;
    public static Double distance35;
    public static Double driveLength35;
    public static Double driveTime35;
    public static Double strokeRecTime35;
    public static Double strokeDistance35;
    public static Double peakDriveForce35;
    public static Double averageDriveForce35;
    public static Double workPerStroke35;
    public static Integer strokeCount35;
    public static VariableChanges globalTimeInstance35;

    // 3D dataframe variables
    public static Integer pol3D;
    public static String message3D;
    public static VariableChanges globalTimeInstance3D;
}
