package com.ti.neurow;

public class VariableChanges {

    // Step 1: Add variables here:
    //EXAMPLE: private [variable type] [variable name];
    private String message; // the message that may change

    // Step 2: Establish listeners here:
    //EXAMPLE: private [listener name] listener;
    private MessageListener listener; // listener that for changes

    
    // Step 3: Write method to update variable
    public void setMessage(String newMessage) {
        message = newMessage;
        
        // Check if a listener is set and notify it of the new message
        if (listener != null) {
            listener.onMessageChanged(newMessage);
        }
    }
    
    // Step 4: Sets a new listener to receive message change updates.
    // EXAMPLE: private [listener name] listener;
    public void setMessageListener(MessageListener newListener) {
        listener = newListener;
    }

    // An interface representing the message change listener. Any class implementing this interface can receive updates when a message changes.
    public interface MessageListener {
        void onMessageChanged(String newMessage); // called when messages are made
    }
}