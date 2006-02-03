//                              -*- Mode: Java -*- 
// QDParser.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 11:32:41 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:27:15 2000
// Update Count    : 19
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;

/** This class converts opcodes numbers into their class equivalent.
 *  It acts as an Opcode factory.
 *  <br><strong>To Do:</strong> implement it using hashtable and reflections.
 *
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public final class QDParser {
    /** OpCode factory.
     * Reserved opcodes are produced by the 
     * <code>factory</code> method in class 
     * <code>QDReservedOP</code>.
     * @psee QDReservedOP#factory. 
     * @param theCode the number of the opcode
     * @return an instance of the correct opcode. 
     *  <code>null</code> if none is available
     *
     */
    public static final QDOpCode getOp(int theCode) {
	switch (theCode) {
	case QDOpCode.NO_OP:        return new QDNoOp() ; 
	case QDOpCode.VERSION_OP:   return new QDVersionOP() ;
	case QDOpCode.PN_LOC_H_FRAC:  return new QDPnLocHFrac(); 
	case QDOpCode.HEADER_OP:    return new QDHeaderOP() ;
	case QDOpCode.TX_FONT_OP:    return new QDTxFontOP() ;
	case QDOpCode.DEF_HILITE_OP: return new QDDefHiliteOP() ;
	case QDOpCode.HILITE_MODE:  return new QDHiliteOP();
	case QDOpCode.TX_SIZE_OP:    return new QDTxSizeOP() ;
	case QDOpCode.TX_FACE_OP:    return new QDTxFaceOP() ;
	case QDOpCode.TX_RATIO_OP:   return new QDTxRatioOP() ;
	case QDOpCode.GLYPH_STATE_OP:   return new QDGlyphStateOP() ;
	case QDOpCode.SHORT_COMMENT_OP: return new QDShortCommentOP() ;
	case QDOpCode.LONG_COMMENT_OP:  return new QDLongCommentOP() ;
	case QDOpCode.PEN_MODE_OP:      return new QDPnMode() ;
	case QDOpCode.CLIP_REGION_OP:   return new QDClipRgnOP();
	case QDOpCode.PEN_SIZE_OP:      return new QDPnSizeOP();
	case QDOpCode.OVAL_SIZE_OP:     return new QDOvSizeOP();
	case QDOpCode.PEN_PAT_OP:       return new QDPatOP(QDPort.PEN_PAT);
	case QDOpCode.FILL_PAT_OP:      return new QDPatOP(QDPort.FILL_PAT);
	case QDOpCode.BACK_PAT_OP:      return new QDPatOP(QDPort.BACK_PAT);
	case QDOpCode.PN_PIX_PAT:       return new QDPixPatOp(QDPort.PEN_PAT);
	case QDOpCode.BK_PIX_PAT:       return new QDPixPatOp(QDPort.BACK_PAT);
	case QDOpCode.FILL_PIX_PAT:       return new QDPixPatOp(QDPort.FILL_PAT);  
	case QDOpCode.FONT_NAME_OP:     return new QDFontNameOP();
	case QDOpCode.SET_ORIGIN_OP:    return new QDOriginOP();
	case QDOpCode.TX_MODE_OP:       return new QDTextModeOP() ;
	case QDOpCode.DHDV_TEXT_OP:     return new QDDHDVTextOP();
	case QDOpCode.DV_TEXT_OP:       return new QDDVTextOP();
	case QDOpCode.DH_TEXT_OP:       return new QDDHTextOP();
	case QDOpCode.LONG_TEXT_OP:     return new QDLongTextOP();
	case QDOpCode.FORE_COLOR_OP:    return new QDOldColor(QDColorOP.FRONT_COLOR_VERB);
	case QDOpCode.BACK_COLOR_OP:    return new QDOldColor(QDColorOP.BACK_COLOR_VERB);
	case QDOpCode.RGB_FORE_COLOR_OP: return new QDColorOP(QDColorOP.FRONT_COLOR_VERB);
	case QDOpCode.RGB_BACK_COLOR_OP: return new QDColorOP(QDColorOP.BACK_COLOR_VERB);
	
	case QDOpCode.SHORT_LINE_OP:    return new QDShortLineOP();
	case QDOpCode.SHORT_LINE_FROM_OP: return new QDShortLineFromOP();
	case QDOpCode.LINE_FROM_OP:      return new QDLineFromOP();
	case QDOpCode.LINE_OP:          return new QDLineOP();
	case QDOpCode.FRAME_POLY_OP:     return new QDPolyOpCode(QDVerbs.FRAME_VERB); 
	case QDOpCode.PAINT_POLY_OP:     return new QDPolyOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.PAINT_RECT_OP:     return new QDRectOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_RECT_OP:     return new QDRectOpCode(QDVerbs.FRAME_VERB); 
	case QDOpCode.FILL_RECT_OP:      return new QDRectOpCode(QDVerbs.FILL_VERB);
	case QDOpCode.ERASE_RECT_OP:     return new QDRectOpCode(QDVerbs.ERASE_VERB); 
	case QDOpCode.INVERT_RECT_OP:     return new QDRectOpCode(QDVerbs.INVERT_VERB);
	case QDOpCode.PAINT_OVAL_OP:     return new QDOvalOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_OVAL_OP:     return new QDOvalOpCode(QDVerbs.FRAME_VERB); 
	case QDOpCode.FILL_OVAL_OP:      return new QDOvalOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.PAINT_ARC_OP:      return new QDArcOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_ARC_OP:      return new QDArcOpCode(QDVerbs.FRAME_VERB); 
	case QDOpCode.PAINT_ROUND_RECT_OP: return new QDRoundRectOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_ROUND_RECT_OP: return new QDRoundRectOpCode(QDVerbs.FRAME_VERB); 
	case QDOpCode.PAINT_SAME_RECT_OP:  return new QDSameRectOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_SAME_RECT_OP:  return new QDSameRectOpCode(QDVerbs.FRAME_VERB); 
	case QDOpCode.PAINT_SAME_ROUND_RECT_OP: return new QDSameRoundRectOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_SAME_ROUND_RECT_OP: return new QDSameRoundRectOpCode(QDVerbs.FRAME_VERB); 
	case QDOpCode.PAINT_SAME_OVAL_OP: return new QDSameOvalOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_SAME_OVAL_OP: return new QDSameOvalOpCode(QDVerbs.FRAME_VERB);
	case QDOpCode.PAINT_SAME_ARC_OP:  return new QDSameArcOpCode(QDVerbs.PAINT_VERB); 
	case QDOpCode.FRAME_SAME_ARC_OP:  return new QDSameArcOpCode(QDVerbs.FRAME_VERB);

	case QDOpCode.FRAME_REGION_OP:     return new QDRegionOP(QDVerbs.FRAME_VERB);
	case QDOpCode.PAINT_REGION_OP:     return new QDRegionOP(QDVerbs.PAINT_VERB);
	case QDOpCode.ERASE_REGION_OP:     return new QDRegionOP(QDVerbs.ERASE_VERB);
	case QDOpCode.INVERT_REGION_OP:     return new QDRegionOP(QDVerbs.FILL_VERB);   
	case QDOpCode.FILL_REGION_OP:     return new QDRegionOP(QDVerbs.FILL_VERB);   
	case QDOpCode.BIT_RECT_OP:       return new QDBitsRectOP(); 
	case QDOpCode.PACK_BIT_RECT_OP:    return new QDPackedBitsRectOP(); 
	case QDOpCode.DIRECT_BITS_RECT_OP: return new QDDirectBitsRectOP(); 
	case QDOpCode.COMPRESSED_QUICK_TIME: return new QDCompressedQuickTime();
	} // switch
	return QDReservedOP.factory(theCode);
    } // getOp

    /** checks if an opcode can be discarded.
     *  The check is done by verifying if the opcode 
     *  is an instance of the <code>QDDiscard</code>
     *  interface, if this is the case the <code>discard</code>
     *  method is checked. 
     *  Here are a few examples of discarded opcodes:
     *  <ul><li>NoOps
     *  <li>Unused Comments
     *  <li>Undefined opcodes (apple reserved)
     *  <li>Unimplemented opcodes (glyph state)
     *  </ul>
     *  @param op the opcode to check
     *  @return <code>true</code> if the opcode can be discarded 
     *  @see QDDiscard#discard
     */

    public static final boolean discard(QDOpCode op) {
	if (op instanceof QDDiscard) return ((QDDiscard) op).discard();
	return false ;
    } // discard

    
} // QDParser
