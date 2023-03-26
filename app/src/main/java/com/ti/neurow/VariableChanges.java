package com.ti.neurow;

public class VariableChanges {

    private String message;
    private MessageListener listener;

    public void setMessage(String newMessage) {
        message = newMessage;
        if (listener != null) {
            listener.onMessageChanged(newMessage);
        }
    }

    public void setMessageListener(MessageListener newListener) {
        listener = newListener;
    }

    public interface MessageListener {
        void onMessageChanged(String newMessage);
    }
}