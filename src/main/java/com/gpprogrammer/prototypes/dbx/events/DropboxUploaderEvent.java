
package com.gpprogrammer.prototypes.dbx.events;

import java.util.EventObject;
import com.gpprogrammer.prototypes.dbx.UploadExecutor;
/**
 *
 * @author Zajac Family
 */
public class DropboxUploaderEvent extends EventObject {
    public static final String FILES_LISTING_PROGRESS = "DropboxSyncerEvent_files_listing_progress";
    public static final String FILES_LISTING_FINISED = "DropboxSyncerEvent_files_listing_finished";
    public static final String FILES_UPLOAD_PROGRESS = "DropboxSyncerEvent_files_upload_progress";
    public static final String FILES_UPLOAD_FINISED = "DropboxSyncerEvent_files_upload_finished";
    
    public final Object data;
    public final String eventId;
    public DropboxUploaderEvent(UploadExecutor source, String eventId) 
    {
        super(source);
        this.eventId = eventId;
        this.data = null;
    }
    
     public DropboxUploaderEvent(UploadExecutor source, String eventId, Object data) 
    {
        super(source);
        this.eventId = eventId;
        this.data = data;
    }
    
    public UploadExecutor getExecutor()
    {
        return ((UploadExecutor)this.source);
    }
}
