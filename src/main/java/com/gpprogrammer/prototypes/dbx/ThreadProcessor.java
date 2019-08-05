
package com.gpprogrammer.prototypes.dbx;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zajac Family
 */
public class ThreadProcessor extends Observable implements Runnable {

    /**
     * Indicates if an instance of ThreadProcessor is running
     */
    protected Boolean active;
    public ThreadProcessor()
    {
       super();
       active = false;
    }
    
    @Override
    public void run() {
        active = true;
        try 
        {
           action();
        } catch (InterruptedException ex) 
        {
            Logger.getLogger(ThreadTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
        active = false;
    }
    
    protected void action() throws InterruptedException
    {
        throw new UnsupportedOperationException("TheredProcessor::action() not set in a parent class");
    }
    
    protected void notify(String noticeID)
    {
        setChanged();
        notifyObservers(noticeID);
    }
    
    protected void notify(Object data)
    {
        setChanged();
        notifyObservers(data);
    }
}
