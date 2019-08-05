
package com.gpprogrammer.prototypes.dbx;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import java.util.Date;

/**
 *
 * @author Zajac Family
 */
public class UploadResult extends FileMetadata{

    public UploadResult(String name, String id, Date clientModified, Date serverModified, String rev, long size) {
        super(name, id, clientModified, serverModified, rev, size);
    }
}
