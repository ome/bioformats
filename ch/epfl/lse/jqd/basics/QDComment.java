//                              -*- Mode: Java -*- 
// QDComment.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 15:08:17 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Dec  5 17:13:16 2000
// Update Count    : 16
// Status          : OK
// 

package ch.epfl.lse.jqd.basics;

/** This class represents a QuickDraw Comment.
 *  Comment are not executed has OpCodes, but serves as hints.
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public abstract class QDComment {

    /** begin QuickDraw Grouping */
    public static final int GROUP_BEGIN = 0 ;
    /** end QuickDraw Grouping */ 
    public static final int GROUP_END = 1   ;
    
    /** PROPRIETARY comment */ 
    public static final int PROPRIETARY = 100;
    
    public static final int MAC_DRAW_BEGIN = 130 ;
    public static final int MAC_DRAW_END = 131 ;

    /** Marks the begining of a group in the picture */
    public static final int GROUPED_BEGIN = 140 ;
    /** Marks the end of a group in the picture */ 
    public static final int GROUPED_END = 141 ;

    public static final int BITMAP_BEGIN = 142 ;
    public static final int BITMAP_END = 143 ;

    /** marks the beginning of a chunk of text */
    public static final int TEXT_BEGIN 	= 150;
    /** marks the end of a chunk of text */
    public static final int TEXT_END 	= 151;
    /** marks the beginning of a string of text */
    public static final int STRING_BEGIN = 152 ;
    /** marks the end of a string of text */
    public static final int STRING_END 	= 153;
    /** marks the center of the text */ 
    public static final int TEXT_CENTER  = 154;
    public static final int LINE_LAYOUT_OFF = 155;
    public static final int LINE_LAYOUT_ON = 156;
    public static final int LINE_LAYOUT_CLIENT = 157;
    /** begining of a polygon */
    public static final int POLY_BEGIN 	= 160;
    /** end of a polygon */ 
    public static final int POLY_END 	= 161;
    public static final int POLY_CURVE   = 162;
    public static final int POLY_IGNORE  = 163;
    public static final int POLY_SMOOTH  = 164;
    public static final int POLY_CLOSE   = 165;
    public static final int ARROW1 = 170 ;
    public static final int ARROW2 = 171 ;
    public static final int ARROW3 = 172 ;
    public static final int ARROW_END = 173;
    public static final int ROTATE_BEGIN = 200;
    public static final int ROTATE_END   = 201;
    public static final int CREATOR = 498 ;
    /** marks the begining of a section that should be 
     *  ignored if postscript data is used 
     */ 
    public static final int POSTSCRIPT_BEGIN = 190 ;
    /** marks the end of a section that should be 
     *  ignored if postscript data is used
     */ 
    public static final int POSTSCRIPT_END = 191 ; 
    /** reference to a postscript handle in memory
     *  @deprecated This comment type has been deprecated by apple
     *  Should only appear in spool files
     */ 
    public static final int POSTSCRIPT_HANDLE = 192 ; 
    /** reference to a postscript file 
     *   @deprecated This comment type has been deprecated by apple
     *  Should only appear in spool files
     */ 
    public static final int POSTSCRIPT_FILE = 193 ; 

    public static final int TEST_IS_POSTSCRIPT = 194 ; 


    /** the kind of the comment */
    protected int kind ;
    
    /** Gets the kind of the comment 
     *  @return the kind 
     */ 
    public int getKind() {
	return kind;
    } // getKind

    /** Gets the text version of the comment 
     * @param type the comment code 
     * @return the name of the comment
     */ 

    public static String toString(int type) {
	switch(type) {
        case CREATOR:                   return("Picture Creator");
        case GROUPED_BEGIN:
	case GROUP_BEGIN:                return("Begin Group");
        case GROUPED_END:
	case GROUP_END:                  return("End Group");
        case BITMAP_BEGIN:               return("Bitmap begin");
	case BITMAP_END:                 return("Bitmap end");
	case ARROW1:                    return("Arrow at end");
        case ARROW2:                    return("Arrow at start");
	case ARROW3:                    return("Arrow at both");
        case ARROW_END:                  return("End of arrow");
	case PROPRIETARY:               return("Proprietary");
	case TEXT_BEGIN:                 return("Text Begin");
	case TEXT_END: 	                return("Text End");
	case STRING_BEGIN: 	        return("String Begin");
	case STRING_END: 	        return("String End");
	case TEXT_CENTER:	        return("Text Center");
	case LINE_LAYOUT_OFF: 	        return("Line Layout Off");
	case LINE_LAYOUT_ON: 	        return("Line Layout On");
	case LINE_LAYOUT_CLIENT: 	        return("Client Line Layout");
	case POLY_BEGIN: 	        return("Poly Begin");
	case POLY_END: 		        return("Poly End");

	case POLY_IGNORE: 	       	return("Poly Ignore");
	case POLY_SMOOTH: 		return("Poly Smooth");
	case POLY_CLOSE: 		return("Poly Close");
	case ROTATE_BEGIN: 		return("Rotate Begin");
	case ROTATE_END: 		return("Rotate End");
	case 180: 			return("Dashed Line");
	case 181: 			return("Dashed End");
	case 182: 			return("Set Line Width");
	case POSTSCRIPT_BEGIN: 		return("PostScript Begin");
	case POSTSCRIPT_END: 		return("PostScript End");
	case POSTSCRIPT_HANDLE: 		return("PostScript Handle");
	case POSTSCRIPT_FILE: 		return("PostScript File");
	case TEST_IS_POSTSCRIPT: 		return("Text is PostScript");
	case 195: 			return("Resource PS");
	case 196: 			return("PS Begin No Save");
	case 197: 			return("Set Gray");
	case 210: 			return("Forms Printing");
	case 211: 			return("End Forms Printing");
	case 220: 			return("CM Begin Profile");
	case 221: 			return("CM End Profile");
	case 222: 			return("CM Enable Color Matching");
	case 223: 			return("CM Disable Color Matching");
	} // switch
	return(Integer.toString(type));
    } // toString
   
} // QDComment


