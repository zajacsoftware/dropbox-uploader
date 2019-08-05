
package com.gpprogrammer.prototypes.dbx;

import java.nio.file.Path;

/**
 *
 * @author Tomasz Zajac
 */
class PathQueueElem
{
    public int state;
    private Path path;
    public PathQueueElem(Path path)
    {
        this.path = path;
        this.state = 0;
    }
    
    public Path getPath()
    {
        return path;
    }
    
    public void dispose()
    {
        this.path = null;
    }
}