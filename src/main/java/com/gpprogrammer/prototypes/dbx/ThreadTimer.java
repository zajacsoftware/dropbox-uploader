
package com.gpprogrammer.prototypes.dbx;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zajac Family
 */
public class ThreadTimer extends ThreadProcessor  {

    public static final String TICK = "tick";
    public static final String TERMINATED = "terminated";
   
    private final int interval;
    public ThreadTimer(int interval)
    {
        super();
        this.interval = interval;  
    }
   
    @Override
    protected void action() throws InterruptedException {
         
        while(this.active)
        {
            synchronized (this) 
            {
               this.wait(interval);
               if(this.active){
                  this.notify(ThreadTimer.TICK);
               }
            }
        }
    }
    
    public void terminate()
    {
       this.active = false; 
       this.notify(ThreadTimer.TERMINATED);
    }
}
