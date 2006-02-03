//                              -*- Mode: Java -*- 
// QDModes.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:26:00 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:26:11 1999
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.basics;

/** This abstract class contains the Quickdraw Modes Definitions
 *  @author Matthias Wiesmann
 *  @version 1.1
 */	
public abstract class QDModes
{
    public static final short COPY = 0 ;
    public static final short OR = 1 ;
    public static final short XOR = 2 ;
    public static final short BIC = 3 ;
    public static final short NOT_COPY = 4 ;
    public static final short NOT_OR = 5 ;
    public static final short NOT_XOR = 6 ;
    public static final short NOT_BIC = 7 ;
    /** pattern bit mask */ 
    public static final short PAT = 8 ;
    /** dither bit mask */
    public static final short DITHER_COPY = 64 ;
	
    /** is the mode a pattern */
    public static boolean isPat(short mode) {
	return  ((mode & PAT) !=0) ;
    } // isPat

    /** is the mode a dithered mode */
    public static boolean isDither(short mode) {
	return (mode > DITHER_COPY) ;
    } // isDither

    /** is the mode a xor mode */
    public static boolean isXor(short mode) {
	if ((mode % PAT)==XOR) return true ;
	return false ;
    } // isXor
    
    /** builds the base string of the mode */
    protected static String baseString(short mode) {
	switch (mode % 8) {
	case COPY : return("copy");
	case OR : return("or");
	case XOR : return("xor");
	case BIC : return("bic");
	case NOT_COPY : return("not copy");
	case NOT_OR : return("not or");
	case NOT_XOR : return("not xor");
	case NOT_BIC : return("not bic");
	} // mode
	return("");
    } // baseString
    
    /** builds the text version of the mode 
     *  @param mode the mode to convert
     *  @return the text version
     *  @see #baseString
     */ 
    public static String toString(short mode) {
	String type  ;
	String dither ;
	if (isPat(mode)) type="pattern" ; else type="source" ;
	if (isDither(mode)) dither="dither" ; else dither="" ;
	return(type+" "+baseString(mode)+" "+dither);
    } // toString
	
} // QDModes
