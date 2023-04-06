package com.ti.neurow;

import com.ti.neurow.db.DatabaseHelper;

public class VariableChanges {

    // Step 1: Add variables here:
    private String message; // the message that may change
    private double time; // the time that may change

    // Step 2: Establish listeners here:
    private MessageListener messageListener; // listener for message changes
    private TimeListener timeListener; // listener for time changes

    // Step 3: Write method to update message variable
    public void setMessage(String newMessage) {
        message = newMessage;

        // Check if a listener is set and notify it of the new message
        if (messageListener != null) {
            messageListener.onMessageChanged(newMessage);
        }
    }

    // Step 4: Write method to update time variable
    public void setTime(double newTime) {
        time = newTime;

        // Check if a listener is set and notify it of the new time
        if (timeListener != null) {
            timeListener.onTimeChanged(newTime);
        }
    }

    // Step 5: Sets a new listener to receive message change updates.
    public void setMessageListener(MessageListener newListener) {
        messageListener = newListener;
    }

    // Step 6: Sets a new listener to receive time change updates.
    public void setTimeListener(TimeListener newListener) {
        timeListener = newListener;
    }


    // An interface representing the message change listener. Any class implementing this interface can receive updates when a message changes.
    public interface MessageListener {
        void onMessageChanged(String newMessage);
    }

    // An interface representing the time change listener. Any class implementing this interface can receive updates when the time changes.
    public interface TimeListener {
        void onTimeChanged(double newTime);
    }
}
