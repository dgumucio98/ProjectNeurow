package com.ti.neurow;

import java.util.ArrayList;

public class GlobalVariables {
    public static String loggedInUsername; // keeps track of what user is "logged in"
    public static ArrayList<Double> finalListTimePower; // populated once a workout activity concludes
    public static Double elapsedTime; // populated once a workout activity concludes
    public static Integer intervalCount; // populated once a workout activity concludes
    public static Integer averagePower; // populated once a workout activity concludes
    public static Integer totalCalories; // populated once a workout activity concludes
    public static Double splitIntAvgPace; // populated once a workout activity concludes
    public static Integer splitIntAvgPwr; // populated once a workout activity concludes
    public static Integer splitIntAvgCal; // populated once a workout activity concludes
    public static Double lastSplitTime; // populated once a workout activity concludes
    public static Integer lastSplitDist; // populated once a workout activity concludes
    public static VariableChanges globalTimeInstance;
}
