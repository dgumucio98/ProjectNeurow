package com.ti.neurow;

import com.ti.neurow.db.DatabaseHelper;

public class VariableChanges {

    // Define variables to be listened for
    private String message; // the message that may change
    private double time; // the time that may change

    // Define Listeners
    private MessageListener messageListener; // listener for message changes
    private TimeListener timeListener; // listener for time changes


    // Variable Setters
    public void setMessage(String newMessage) {
        message = newMessage;
        // Check if a listener is set and notify it of the new message
        if (messageListener != null) {
            messageListener.onMessageChanged(newMessage);
        }
    }
    public void setTime(double newTime) {
        time = newTime;
        // Check if a listener is set and notify it of the new time
        if (timeListener != null) {
            timeListener.onTimeChanged(newTime);
        }
    }

    // Variable Listener Setters
    public void setMessageListener(MessageListener newListener) {
        messageListener = newListener;
    }
    public void setTimeListener(TimeListener newListener) {
        timeListener = newListener;
    }


    // Interfaces
    public interface MessageListener {
        void onMessageChanged(String newMessage);
    }
    public interface TimeListener {
        void onTimeChanged(double newTime);
    }
}
