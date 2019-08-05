
package com.gpprogrammer.prototypes.dbx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Tomasz Zajac
 */
public class ListFilesThread extends ThreadProcessor {
    public static final String COMPLETE = "complete";
    private final File target;
    public final PrintFiles fvs; 
    
    public ListFilesThread(File target)
    {
       super();
       this.target = target;
       this.fvs = new PrintFiles();  
    }

    private void listFilesInDir(File target)
    {
        Path p = target.toPath();
        
        try 
        {
          Files.walkFileTree(p, fvs);
        } catch (IOException e) 
        {
          // do nothing; 
        }
    }  
 
    @Override
    protected void action() throws InterruptedException {
        listFilesInDir(target);
        this.notify(ListFilesThread.COMPLETE);
    }
}
