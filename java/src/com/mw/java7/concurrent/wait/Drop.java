package com.mw.java7.concurrent.wait;

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
    	try{
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
		        System.out.println("<------ take: " + message);
		        //throw new Exception("an exception");
		        return message;
	    	}
    	} catch(Exception e) {
    		try {
				Thread.sleep(100000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
        System.out.println("------> put : " + message);
        // Notify consumer that status
        // has changed.
        notifyAll();
    }
}
