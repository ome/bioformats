package ch.epfl.lse.jqd.basics;

/** This Exception is throw when a bad QuickDraw Version 
 *  is encountered. Recognised version are 1 and 2
 * @author Matthias Wiesmann
 * @version 1.0
 */

public class QDUnknownPictVersion extends QDException
{
    protected int version ;

    public QDUnknownPictVersion(int vers) {
	this.version=vers ;
    } // QDUnknownPictVersion
    
    public String toString() {
	    return("Unknown pict version "+version);
	} // toString
} // QDUnknownPictVersion
