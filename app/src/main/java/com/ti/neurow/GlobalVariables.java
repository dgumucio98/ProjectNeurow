package com.ti.neurow;

import java.util.ArrayList;

public class GlobalVariables {
    public static String loggedInUsername; // keeps track of what user is "logged in"
    public static ArrayList<Double> finalListTimePower; // populated once a workout activity concludes

    // Dataframe 33 stuff
    public static Double elapsedTime33 = 0.0; // Metric displayed in UI
    public static Integer intervalCount33 = 0;
    public static Integer averagePower33 = 0; // Metric displayed in UI
    public static Integer totalCalories33 = 0; // Metric displayed in UI
    public static Double splitIntAvgPace33 = 0.0;
    public static Integer splitIntAvgPwr33 = 0;
    public static Double splitIntAvgCal33 = 0.0;
    public static Double lastSplitTime33 = 0.0;
    public static Double lastSplitDist33; // Metric displayed in UI
    public static VariableChanges globalTimeInstance33;

    // Dataframe 35 stuff
    public static Double elapsedTime35 = 0.0; // Metric displayed in UI
    public static Double distance35 = 0.0; // Metric displayed in UI
    public static Double driveLength35 = 0.0; // Metric displayed in UI
    public static Double driveTime35 = 0.0; // Metric displayed in UI
    public static Double strokeRecTime35 = 0.0;
    public static Double strokeDistance35 = 0.0;
    public static Double peakDriveForce35 = 0.0;
    public static Double averageDriveForce35 = 0.0;
    public static Double workPerStroke35 = 0.0;
    public static Integer strokeCount35 = 0; // Metric displayed in UI
    public static VariableChanges globalTimeInstance35;

    // Dataframe 3D stuff
    public static Integer pol3D;
    public static String message3D;
    public static VariableChanges globalTimeInstance3D;

    // Others
    public static Integer failCount;
    public static Integer ftp = 45; // default FTP value

}
