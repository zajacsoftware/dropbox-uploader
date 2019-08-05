
package com.gpprogrammer.prototypes.dbx;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author Tomasz Zajac
 */
public interface FileSearchResult {
    public Boolean isFinised();  
    public int getFileCount();
    public int getSymlinkCount();
    public int getOtherCount();
    public int getDirCount();
    public ArrayList<String> getIgnored();
    public ArrayList<Path> getOutput();
}
