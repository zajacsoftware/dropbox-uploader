
package com.gpprogrammer.prototypes.dbx;

import java.nio.file.Path;
import java.util.Observable;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Tomasz Zajac
 */
public class UploadThread extends ThreadProcessor {

    public static final String COMPLETE = "complete";
    private String fileLocation;
    private String command;
    private final PathQueueElem pathElem;
    private final String root;
    private final String destination;
    public UploadStatus status;
    public UploadThread(PathQueueElem pathElem, String root, String destination)
    {
        this.pathElem = pathElem;
        this.root = root;
        this.destination = destination;
    }
    
    public PathQueueElem getpathQueueElem()
    {
        return pathElem;
    }
    
    @Override
    protected void action() throws InterruptedException {
        
        DropboxHandler dbh = new DropboxHandler();
     
        this.notify(new UploadStatus(UploadStatus.UPLOAD_STARTED, pathElem.getPath().toString(), null));
        
        status = dbh.pushToDropbox(pathElem.getPath().toString(), root, destination);
        this.notify(status);
    }
    
    @Override
    public String toString(){
        return this.command;
    }
}