package ch.epfl.lse.jqd.basics;

/** Interface implemented by objet that need to be notified 
 *  of the progress of the loading of a QuickDraw picture.
 *  @author Matthias Wiesmann
 *  @version 1.0
 *  @deprecated not supported in new version of package 
 *   will perhaps be reintroduced. 
 */ 

public interface QDObserver {
    /** called with the status of the loading operation 
     *  @param status a percentage of the job done 
     */ 
    void loadStatus(int status);
    
    /** called when loading ecounters an exception 
     *  @param exception the exception that occured
     */ 

    void loadError(Exception exception);
} // QDObserver

