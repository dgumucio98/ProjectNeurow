package com.ti.neurow;

public class VariableChanges {

    private String message; // the message that may change
    private MessageListener listener; // listener that for changes

    // 1. Sets a new message and notifies the listener if one has been set.
    public void setMessage(String newMessage) {
        message = newMessage;

        // Check if a listener is set and notify it of the new message
        if (listener != null) {
            listener.onMessageChanged(newMessage);
        }
    }

    // 2. Sets a new listener to receive message change updates.
    public void setMessageListener(MessageListener newListener) {
        listener = newListener;
    }

    // An interface representing the message change listener. Any class implementing this interface can receive updates when a message changes.
    public interface MessageListener {
        void onMessageChanged(String newMessage); // called when messages are made
    }
}