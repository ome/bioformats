//                              -*- Mode: Java -*- 
// QDVerbs.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:23:46 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:24:04 1999
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.basics;

/** This class cannot be instanciated 
 *  it contains all constant and methods for the different quickDraw Verbs
 *  @author Matthias Wiesmann
 *  @version 1.1 revised
 */

public final class QDVerbs 
{
    protected QDVerbs() {} ;
    
    /** draw the outline of the object */
    public static final short FRAME_VERB = 0 ;
    /** fill the object with the pattern */
    public static final short PAINT_VERB = 1 ;
    /** fill the object with the background color */
    public static final short ERASE_VERB = 2 ;
    /** invert the object - not well supported */
    public static final short INVERT_VERB = 3 ;
    /** fill the object with the fill color */
    public static final short FILL_VERB = 4 ;
    /** not an actual QD Verb, used to signal text drawing */ 
    public static final short TXT_VERB = 255 ; 
    /** not an actual QD Verb, used to signal highlight mode */
    public static final short HIGH_VERB = 256 ;


    /** Gets the text version of the QuickDraw Verb
     *  @param theVerb the verb to transalte
     *  @return a text version of the verb 
     */ 

    public static final String toString(short theVerb) {
	switch(theVerb){
	case FRAME_VERB: return("frame");
	case PAINT_VERB: return("paint");
	case ERASE_VERB: return("erase");
	case INVERT_VERB: return("invert");
	case FILL_VERB: return("fill");
	}
	return("unknown verb");
    } // toString 
    
    public static final String toString(int verb) {
	return toString((short) verb);
    } // toString
} // QDVerbs


