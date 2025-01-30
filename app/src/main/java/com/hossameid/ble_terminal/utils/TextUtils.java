package com.hossameid.ble_terminal.utils;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class TextUtils {

    public static void appendTextSafely(TextView textView, String newText, Context context) {
        int maxSizeInBytes = 2 * 1024 * 1024; // 2MB in bytes

        // Get the current text in the TextView
        String existingText = textView.getText().toString();

        // Convert the existing text and the new text to byte arrays
        byte[] existingTextBytes = existingText.getBytes(StandardCharsets.UTF_8);
        byte[] newTextBytes = newText.getBytes(StandardCharsets.UTF_8);

        // Calculate the combined size of both existing and new text
        int totalSizeInBytes = existingTextBytes.length + newTextBytes.length;

        // Check if the total size exceeds the 2MB limit
        if (totalSizeInBytes <= maxSizeInBytes) {
            // Append the new text to the existing text if it's within the limit
            textView.append(TimeUtils.getCurrentTimestamp() + " > " + newText + "\n");
        } else {
            // Handle the case where the combined text exceeds 2MB
            Toast.makeText(context, "Maximum text size of 2MB reached!", Toast.LENGTH_LONG).show();
        }
    }
}
