
package com.gpprogrammer.prototypes.dbx;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import  com.gpprogrammer.prototypes.dbx.events.DropboxUploaderEvent;
import  com.gpprogrammer.prototypes.dbx.events.DropboxUploaderEventListener;
    
/**
 * Manages upload threads
 * 
 * @author Tomasz Zajac 
 */

public class UploadExecutor {

   //  TODO: add note regarding threads number and Dropbox api limits
    private final int maxThreds = 10;
    private final ExecutorService executor;
    
    private final List list = Collections.synchronizedList(new ArrayList());
    
    private ArrayList<PathQueueElem> pathsQueue;
    private ArrayList<PathQueueElem> failedPathsQueue;
    private ArrayList<String> currentUploading;
    private HashMap<String, Integer> fileErrors;
    
    private int counter = 0;
    private int counterErrors = 0;
    
    public UploadExecutor()
    {
       executor = Executors.newFixedThreadPool(maxThreds);
    }
     /**
    * 
    * @param fileList list of local files to upload
    * @param root file local path
    * @param destination Dropbox destination
   */
    public void pushBunch(ArrayList<Path> fileList, Path root, String destination  )
    { 
        ThreadTimer threadTimer = new ThreadTimer(500);
        threadTimer.addObserver((Observable obj, Object arg) -> {
            if(ThreadTimer.TICK == arg )
            {
                dispatch(DropboxUploaderEvent.FILES_UPLOAD_PROGRESS, counter);
                if(pathsQueue.isEmpty())
                {
                   dispatch(DropboxUploaderEvent.FILES_UPLOAD_FINISED, counter);
                }
            }
        });   
       
        executor.execute(threadTimer);
          
        pathsQueue = new ArrayList<>();
        failedPathsQueue = new ArrayList<>();
        currentUploading = new ArrayList<>();
        fileErrors = new HashMap();
        
        fileList.stream().forEach((fileList1) -> {
            pathsQueue.add(new PathQueueElem(fileList1));
        });
       
        ArrayList<UploadThread> workers = new ArrayList<>();
        
        for (int i = 0; i <= maxThreds; i++) 
        {
           if(i < pathsQueue.size())
            {
               uploadFile(pathsQueue.get(i), root.toString(), destination);   
            }
        }
    }
    
    public synchronized void uploadFile(PathQueueElem pathElement, String root, String destination)
    {
        if(pathElement == null)
        {
            return;
        }
        
        pathElement.state = 1;
        UploadThread worker = new UploadThread(pathElement, root, destination); 
        worker.addObserver((Observable obj, Object arg) -> {
            if( arg instanceof UploadStatus )
            {
                String stId = ((UploadStatus) arg).getStatus();
                switch (stId)
                {
                    case  UploadStatus.UPLOAD_COMPLETE:
                        synchronized(UploadExecutor.this)
                        {
                            counter++;
                            getPathQueue().remove(((UploadThread) obj).getpathQueueElem());
                            getCurrentUploading().remove(((UploadStatus) arg).getSource());
                        }
                        
                        uploadFile(getNextAvailable(), root, destination);
                        // System.out.println(" UPLOAD_COMPLETE ");
                        break;
                    case  UploadStatus.UPLOAD_ERROR:
                        getPathQueue().remove(((UploadThread) obj).getpathQueueElem());
                        synchronized(UploadExecutor.this)
                        {
                            getPathQueue().remove(((UploadThread) obj).getpathQueueElem());
                            getCurrentUploading().remove(((UploadStatus) arg).getSource());   
                            
                            if(fileErrors.containsKey(((UploadStatus) arg).getApiMessage()))
                            {
                                fileErrors.put(((UploadStatus) arg).getApiMessage(), fileErrors.get(((UploadStatus) arg).getApiMessage())+1);
                            }else
                            {
                                fileErrors.put(((UploadStatus) arg).getApiMessage(), 1);
                            }
                        }
                        // TODO: add note why retry here;
                        uploadFile(getNextAvailable(), root, destination);
                        break;
                        
                    case  UploadStatus.API_ERROR:
                        // System.out.println(" API_ERROR "+((UploadStatus) arg).getApiMessage());
                        ((UploadThread) obj).getpathQueueElem().state = 0;
                        synchronized(UploadExecutor.this)
                        {
                            getCurrentUploading().remove(((UploadStatus) arg).getSource());
                        }
                          // TODO: add note why retry here;
                        uploadFile(getNextAvailable(), root, destination);
                        break;
                    case  UploadStatus.UPLOAD_STARTED:
                        synchronized(UploadExecutor.this)
                        {
                            getCurrentUploading().add(((UploadStatus) arg).getSource());
                        }
                        break;
                    default:
                        System.out.println("Invalid UploadStatus: "+stId);
                }
            }
        }); 
        executor.execute(worker);      
    }
   
    public synchronized ArrayList<PathQueueElem> getPathQueue()
    {
        return pathsQueue;
    } 
    
    public synchronized ArrayList<PathQueueElem> getFailedPathsQueue()
    {
        return failedPathsQueue;
    } 
    
    public synchronized HashMap<String, Integer> getFileErrors()
    {
        return fileErrors;
    } 
    
    public synchronized PathQueueElem getNextAvailable()
    {
         for(int i = 0; i < getPathQueue().size(); i++)
         {
             if(getPathQueue().get(i).state == 0)
             {
                 return getPathQueue().get(i);
             }
         }
         return null;
    } 
    
    public synchronized ArrayList<String> getCurrentUploading()
    {
        return currentUploading;
    } 
    
    public void listFiles(File target)
    {
        ThreadTimer threadTimer = new ThreadTimer(500);
        ListFilesThread worker = new ListFilesThread(target);

        threadTimer.addObserver((Observable obj, Object arg) -> {
            System.out.println(arg);
            if(ThreadTimer.TERMINATED != arg )
            {
                dispatch(DropboxUploaderEvent.FILES_LISTING_PROGRESS, worker.fvs.getResult() );
            }
            else
            {
                 dispatch(DropboxUploaderEvent.FILES_LISTING_FINISED, worker.fvs.getResult() );
            }
        });    
        
         worker.addObserver((Observable obj, Object arg) -> {
            if(ListFilesThread.COMPLETE == arg )
            {
                threadTimer.terminate();
            }
        });   
            
        executor.execute(threadTimer);
        executor.execute(worker);
    }
    
    private Map<String, ArrayList<DropboxUploaderEventListener>> allEvents;
    public void addEventListener(String eventId, DropboxUploaderEventListener e)
    {
        if(allEvents == null)
        {
            allEvents = new HashMap<>();
        }
        
        if(allEvents.containsKey(eventId) == false)
        {
            allEvents.put(eventId, new ArrayList<>());
        }
        
        allEvents.get(eventId).add(e);
    }
    
    public void removeEventListener(String eventId, DropboxUploaderEventListener e)
    {
        if(allEvents == null)
        {
          return;
        }
        
        if(allEvents.containsKey(eventId))
        {
          ArrayList<DropboxUploaderEventListener> el = allEvents.get(eventId);
          el.remove(e);
          if(el.isEmpty())
          {
              allEvents.remove(eventId);
          }
       }
    }
    
    public void removeEventListener(String eventId)
    {
        if(allEvents == null)
        {
          return;
        }
      
        if(allEvents.containsKey(eventId)){
             ArrayList<DropboxUploaderEventListener> el = allEvents.get(eventId);
             allEvents.remove(eventId, el);
             el.clear();
        }
    }
    
    private void dispatch(String eventId, Object data)
    {
        if(allEvents == null)
        {
          return;
        }
        
        if(allEvents.containsKey(eventId))
        {
            ArrayList<DropboxUploaderEventListener> el = allEvents.get(eventId);
        
            DropboxUploaderEvent event =  new DropboxUploaderEvent(this, eventId, data);
            Iterator iterator = el.iterator();
            while(iterator.hasNext())
            {
               ((DropboxUploaderEventListener)iterator.next()).handleEvent(event);
            }
        }
    }
    
    public boolean terminate()
    {
        return executor.shutdownNow().isEmpty();
    }
}

