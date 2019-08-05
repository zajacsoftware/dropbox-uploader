
package com.gpprogrammer.prototypes.dbx;

/**
 *
 * @author Tomasz Zajac
 */
import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import java.io.*;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropboxHandler {
    
    // Test app auth valuse. Set up for testing only, uploaded files get automaticly deleted sortly after upload.
    // Check https://www.dropbox.com/developers/reference for more details
   private final String APP_KEY = "ap1bymgtzym3pj9";  
   private final String APP_SECRET = "lnd51yve8baq733";  
   private final String ACCESS_TOKEN  = "s-ey2GvTYRUAAAAAAAScMZkz0nCI2xNV_Yz3Y99r8YG0MB9ynOqCEg8CDplETHXs";

    private final Pattern extractReason = Pattern.compile("(.*)\"(reason)\"\\s*?:\\s*?\"(?<value>.*?)\"(.*$)");
    private final Pattern extractTag = Pattern.compile("(.*)\"(\\.tag)\"\\s*?:\\s*?\"(?<value>.*)\"(.*$)");
    
    private DbxClientV2 client; 
    
    public DropboxHandler()
    {
    }
    
    private void initDbx()
    {
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        DbxRequestConfig config = new DbxRequestConfig("Apps/BDIN");
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }
    public UploadStatus bulkPush(ArrayList<Path> list, Path syncRoot, String destination)
    {
          return new UploadStatus("Upload Complete", "bulk ", "ppst");
    }
    /**
     * Initiates upload of a single file with DBXv2Uploader @see com.gpprogrammer.prototypes.dbx.DBXv2Uploader#chunkedUploadFile()
     *
     * @param filePath local filepath
     * @param syncRoot root directory of Dropbox application
     * @param destination file destination relative to syncRoot
     * @return UploadStatus @see com.gpprogrammer.prototypes.dbx.UploadStatus
     */
    public UploadStatus pushToDropbox(String filePath, String syncRoot, String destination)
    {
        if(client == null)
        {
            initDbx();
        }
        
        File inputFile = new File(filePath);
        if(inputFile.exists() == false ) return new UploadStatus(null, null, null);
        Path p = inputFile.toPath();

        String ppst;
        if(syncRoot.equals(p.toString()))
        { 
            ppst  = destination + "/"+p.getFileName().toString().replace("\\", "/");
        }
        else
        {
            ppst  = destination + (p.toString().substring(syncRoot.length(), p.toString().length())).replace("\\", "/");
        }
     
        String apiMessage = DBXv2Uploader.chunkedUploadFile(client, inputFile, ppst);    
         
        UploadStatus status = getUploadStatus(p.toString(), ppst, apiMessage);
        
        return status;
 
    }
    
 
    private UploadStatus getUploadStatus(String inputPath, String targetPath, String apiMessage){
        
             UploadStatus status;
             String inp = apiMessage; 

              Matcher matcher = extractReason.matcher(inp);
            
              String reason = null;
              String tag = null;
              
                while (matcher.find()) 
                {
                    reason = matcher.group("value");
                }
          
                if(reason == null)
                {
                     Matcher matcher0 = extractTag.matcher(inp); 
                     while ( matcher0.find()) 
                     {
                         tag = matcher0.group("value");
                      //   System.out.println(tag);
                     }
                }
        
            if(reason != null)
            {
                status = new UploadStatus(UploadStatus.UPLOAD_ERROR, inputPath, targetPath, reason);
            }else if(tag != null)
            {
                status = new UploadStatus(UploadStatus.API_ERROR, inputPath, targetPath, tag);
            }else
            {
                status = new UploadStatus(UploadStatus.UPLOAD_COMPLETE, inputPath, targetPath, apiMessage);
            }
          return status;
    }
}