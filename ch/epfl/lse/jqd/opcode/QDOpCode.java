//                              -*- Mode: Java -*- 
// QDOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 14:20:54 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:15:36 2000
// Update Count    : 9
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.*;
import java.io.IOException;


/** This interface describes an QuickDraw Opcode
 *  @version 1.1
 *  @author Matthias Wiesmann
 */

public interface QDOpCode {
    int NO_OP = 0x000 ;
    int CLIP_REGION_OP = 0x0001 ;
    int BACK_PAT_OP = 0x0002 ;
    int TX_FONT_OP =  0x0003 ;
    int TX_FACE_OP =  0x0004 ;
    int TX_MODE_OP =  0x0005 ;
    int PEN_SIZE_OP = 0x0007 ;
    int PEN_MODE_OP = 0x0008 ;
    int PEN_PAT_OP = 0x0009 ;
    int FILL_PAT_OP = 0x000A ;
    int OVAL_SIZE_OP = 0x000B ;
    int SET_ORIGIN_OP = 0x000C ;
    int TX_SIZE_OP = 0x000D ;
    int FORE_COLOR_OP = 0x000E ;
    int BACK_COLOR_OP = 0x000F ;
    int TX_RATIO_OP = 0x0010 ;
    int VERSION_OP = 0x0011 ;
    int BK_PIX_PAT = 0x0012 ;    
    int PN_PIX_PAT = 0x0013 ;    
    int FILL_PIX_PAT = 0x0014 ;  
    int PN_LOC_H_FRAC = 0x0015 ;  
    int CH_EXTRA = 0x0016;      // ! not implemented
    int RGB_FORE_COLOR_OP = 0x001A ;
    int RGB_BACK_COLOR_OP = 0x001B ;
    int HILITE_MODE = 0x001C;  
    int HILITE_COLOR = 0x001D; 
    int DEF_HILITE = 0x001E;   
    int OP_COLOR = 0x001F;      // ! not implemented
    int LINE_OP = 0x0020 ; 
    int LINE_FROM_OP = 0x0021 ; 
    int SHORT_LINE_OP = 0x0022 ;
    int SHORT_LINE_FROM_OP = 0x0023 ;
    int LONG_TEXT_OP = 0x0028 ;
    int DH_TEXT_OP = 0x0029 ;
    int DV_TEXT_OP = 0x002A ;
	
    int DHDV_TEXT_OP = 0x002B ;
	
    int FONT_NAME_OP = 0x002C ;
    int GLYPH_STATE_OP = 0x002E ;
    int FRAME_RECT_OP = 0x0030 ;
    int PAINT_RECT_OP = 0x0031 ;
    int ERASE_RECT_OP = 0x0032 ;
    int INVERT_RECT_OP = 0x0033 ;
    int FILL_RECT_OP  = 0x0034 ;
    int FRAME_SAME_RECT_OP = 0x0038 ;
    int PAINT_SAME_RECT_OP = 0x0039 ;
    int FRAME_ROUND_RECT_OP = 0x0040 ;
    int PAINT_ROUND_RECT_OP = 0x0041 ;
    int FRAME_SAME_ROUND_RECT_OP = 0x0048 ;
    int PAINT_SAME_ROUND_RECT_OP = 0x0049 ;
    int FRAME_OVAL_OP = 0x0050 ;
    int PAINT_OVAL_OP = 0x0051 ;
    int FILL_OVAL_OP = 0x0054 ;
    int FRAME_SAME_OVAL_OP = 0x0058 ;
    int PAINT_SAME_OVAL_OP = 0x0059 ;
    int FRAME_ARC_OP = 0x0060 ;
    int PAINT_ARC_OP = 0x0061 ;
    int FRAME_SAME_ARC_OP = 0x0068 ;
    int PAINT_SAME_ARC_OP = 0x0069 ;
    int FRAME_POLY_OP = 0x0070 ;
    int PAINT_POLY_OP = 0x0071 ;
    int FRAME_REGION_OP = 0x0080 ;
    int PAINT_REGION_OP = 0x0081 ;
    int ERASE_REGION_OP = 0x0082 ;
    int INVERT_REGION_OP = 0x0083 ;
    int FILL_REGION_OP =  0x0084 ;
    int BIT_RECT_OP =  0x0090 ;
    int DIRECT_BITS_RECT_OP = 0x009A ;
    int PACK_BIT_RECT_OP =  0x0098 ;
    int SHORT_COMMENT_OP = 0x00A0 ;
    int LONG_COMMENT_OP = 0x00A1 ;
    int DEF_HILITE_OP = 0x001E ;
	
    int HEADER_OP = 0x0C00 ;

    int COMPRESSED_QUICK_TIME = 0x8200 ;

    /** Reads the opcode data from a stream
     *  @param theStream the stream containing the data
     *  @return the number of bytes read
     *  @exception IOException problem with the stream
     *  @exception QDException QuickDraw parsing error 
     */ 
    int read(QDInputStream theStream) 
	throws IOException, QDException ;

    /** Executes the opcode into a QuickDraw Port
     *  @param thePort the port to execute into
     *  @exception QDException QuickDraw display error 
     */ 
    void execute(QDPort  thePort) 
	throws QDException;
} // OpCode
	
