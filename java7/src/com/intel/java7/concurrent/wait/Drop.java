package com.intel.java7.concurrent.wait;

public class Drop {
	 // Message sent from producer
    // to consumer.
    private String message;
    // True if consumer should wait
    // for producer to send message,
    // false if producer should wait for
    // consumer to retrieve message.
    private boolean empty = true;

    public String take() {
        // Wait until message is
        // available.
    	synchronized(this) {
	        while (empty) {
	            try {
	                wait();
	            } catch (InterruptedException e) {}
	        }
	        // Toggle status.
	        empty = true;
	        // Notify producer that
	        // status has changed.
	        notifyAll();
	        return message;
    	}
    }

    public synchronized void put(String message) {
        // Wait until message has
        // been retrieved.
        while (!empty) {
            try { 
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = false;
        // Store message.
        this.message = message;
        // Notify consumer that status
        // has changed.
        notifyAll();
    }
}
