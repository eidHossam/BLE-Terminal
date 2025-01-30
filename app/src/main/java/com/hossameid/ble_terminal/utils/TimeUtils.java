package com.hossameid.ble_terminal.utils;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtils {
    public static String getCurrentTimestamp(){
        LocalTime currentTime = LocalTime.now(); // Get the current time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS"); // Define the format
        return currentTime.format(formatter);
    }

    public static String getCurrentDateTime(){
        // Create a new SimpleDateFormat object with the desired pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy_MM_dd_HH_mm");

        // Get the current date and time
        Date date = new Date();

        // Format the date and time as a string
        return dateFormat.format(date);
    }

    public static String getCurrentDateTime(String format){
        // Create a new SimpleDateFormat object with the desired pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        // Get the current date and time
        Date date = new Date();

        // Format the date and time as a string
        return dateFormat.format(date);
    }
}
