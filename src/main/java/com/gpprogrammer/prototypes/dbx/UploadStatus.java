
package com.gpprogrammer.prototypes.dbx;

/**
 * Carries information about the upload status.
 * 
 * @author Tomasz Zajac
 */

public class UploadStatus
{
   public static final String UPLOAD_STARTED = "upload_started";
   public static final String UPLOAD_COMPLETE = "upload_complete";
   public static final String UPLOAD_ERROR = "upload_error";
   public static final String API_ERROR = "api_error";
   
   private final String status;
   private final String source;
   private final String destination;
   private final String apiMessage;
   
   /**
    * Constructor for class UploadStatus
    * 
    * @param status upload status
    * @param source file local path
    * @param destination Dropbox destination
    * @param apiMessage additional information from DropboxAPI
   */
    public UploadStatus(String status, String source, String destination, String apiMessage)
   {
       this.status = status;
       this.source = source;
       this.destination = destination;
       this.apiMessage = apiMessage;
   }
    
    /**
     *  Constructor for class UploadStatus without apiMessage
     *  @see com.gpprogrammer.prototypes.dbx.UploadStatus#UploadStatus( status, source, destination, apiMessage)
     */
   public UploadStatus(String status, String source, String destination)
   {
       this.status = status;
       this.source = source;
       this.destination = destination;
       this.apiMessage = null;
   }
    

   public String getStatus()
   {
       return status;
   }
   
   public String getSource()
   {
      return source;
   }
      
   public String getDestination()
   {
      return destination;
   }
   
   public String getApiMessage()
   {
       return apiMessage;
   }

}