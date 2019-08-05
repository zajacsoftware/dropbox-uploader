
package com.gpprogrammer.prototypes.dbx;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 *
 * @author Tomasz Zajac
 */
    public class PrintFiles extends SimpleFileVisitor<Path> {

 //   public Boolean complete = false;
    private Boolean finised;    
    public int fileCount = 0;
    public int symlinkCount = 0;
    public int otherCount = 0;
    public int dirCount = 0;
    public ArrayList<String> ignored = new ArrayList<>();
    public ArrayList<Path> output = new ArrayList<>();
    // Print information about
    // each type of file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
     
        if (attr.isSymbolicLink()) {
            symlinkCount++;
        } else if (attr.isRegularFile()) {
            fileCount++;
            output.add(file);
        } else {
            otherCount++;
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {        
        dirCount++;
        if (exc!=null)
        {
          ignored.add( exc.getMessage());
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        if(exc!=null)
        {
           System.out.println(exc.toString());
           ignored.add( exc.getMessage());
        }
        return CONTINUE;
    }
    
    public Boolean isFinised()
    {
        return finised;
    }
    
    public FileSearchResult getResult(){
  
        return new FileSearchResult()
        {
            @Override
            public Boolean isFinised() {
                return finised;
            }

            @Override
            public int getFileCount() {
             return fileCount;
            }

            @Override
            public int getSymlinkCount() {
                return symlinkCount;
            }

            @Override
            public int getOtherCount() {
                return otherCount;
            }

            @Override
            public int getDirCount() {
                return dirCount;
            }

            @Override
            public ArrayList<String> getIgnored() {
               return ignored;
            }

            @Override
            public ArrayList<Path> getOutput() {
                return output;
            }
        };
    } 
}
