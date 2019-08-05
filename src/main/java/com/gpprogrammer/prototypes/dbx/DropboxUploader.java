
package com.gpprogrammer.prototypes.dbx;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Observer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.gpprogrammer.prototypes.dbx.events.DropboxUploaderEvent;
import com.gpprogrammer.prototypes.dbx.events.DropboxUploaderEventListener;

/**
 *
 * @author Tomasz Zajac
 */
public class DropboxUploader {

    private static final Pattern PROCESS_EXTRACTOR = Pattern.compile("(?<prs>^.*\\.exe)(.*$)");
    private DropboxSyncerGUI gui;

    private DropboxHandler dbh;
    private UploadExecutor executor;

    private Observer fileListObserver;
    private FileSearchResult fsr;

    public static void main(String[] args) 
    {
        new DropboxUploader();
    }
  
    public DropboxUploader()
    {
      initGUI();
      initExecutor();
    }
    
    private void initGUI()
    {
       gui = new DropboxSyncerGUI(); 
       gui.setVisible(true); 
       gui.addReloadListener(this);
       gui.setUploadBtnEnabled(false);
    }

    private void initExecutor()
    {
        executor = new UploadExecutor();
        
        // files list job
        executor.addEventListener(DropboxUploaderEvent.FILES_LISTING_PROGRESS, (DropboxUploaderEvent e) -> {
            gui.updateLastLog("working... directories: "+ ((FileSearchResult)e.data).getDirCount()+"; files: "+ ((FileSearchResult)e.data).getFileCount()+"; symlinks: "+ ((FileSearchResult)e.data).getSymlinkCount());
          });
       
       executor.addEventListener(DropboxUploaderEvent.FILES_LISTING_FINISED, (DropboxUploaderEvent e) -> {
           fsr = ((FileSearchResult)e.data);
           gui.rewriteLog("Finished! found: directories: "+ fsr.getDirCount()+"; files: "+ fsr.getFileCount()+"; symlinks: "+ fsr.getSymlinkCount());
           gui.setUploadBtnEnabled(true);

       });
       
        // upload job
       executor.addEventListener(DropboxUploaderEvent.FILES_UPLOAD_FINISED, (DropboxUploaderEvent e) -> {
            String msg = "Finised! Uploaded " + e.data + " of " + fsr.getOutput().size();
            gui.updateLastLog(msg);
       });
        executor.addEventListener(DropboxUploaderEvent.FILES_UPLOAD_PROGRESS, new DropboxUploaderEventListener() {
            @Override
            public void handleEvent(DropboxUploaderEvent e) {
                String msg = "working... uploaded " + e.data + " of " + fsr.getOutput().size();
                ArrayList<String> list = executor.getCurrentUploading();
                HashMap<String, Integer> fileErrors = executor.getFileErrors();
                 Iterator it = fileErrors.entrySet().iterator();
                 if(it.hasNext())
                 {
                    msg += "\nUpload errors caused by restricted or inaccessible files: ";
                    while (it.hasNext()) 
                    {
                        Map.Entry pair = (Map.Entry)it.next();
                        msg += "\n"+ pair.getKey() +": "+pair.getValue();
                    }
                 }
                msg += "\nconcurrently uploading:";
                for(int i = 0; i < list.size(); i++)
                {
                    msg += "\n"+list.get(i);
                } 
                gui.updateLastLog(msg, "working..." );
            }
        });
    }
    
    
    public Socket connection;
    private Boolean abcx = false;
    
    private Path syncRoot; 
    
    public void onSrcRootSelected(File path)
    {
       gui.setSelectBtnEnabled(false);
       syncRoot = path.toPath();
       gui.addLog("Serching directory. May take a moment.");
       gui.addLog("working ...");
       executor.listFiles(path);
    }
    
    public void beginUpload()
    {
        gui.setUploadBtnEnabled(false);
        gui.updateLastLog("working...");
        executor.pushBunch(fsr.getOutput(), this.syncRoot, gui.getDestPath());
    }
}
