//                              -*- Mode: Java -*- 
// QDRegionOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 17:06:08 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:36:13 2000
// Update Count    : 8
// Status          : No fully implemented
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.QDRegion;
import ch.epfl.lse.jqd.basics.QDPort;
import ch.epfl.lse.jqd.basics.QDException;
import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** This class implements Region Opcodes.
 *  The actual region handling is done 
 *  in the port <code>stdRgn</code> bottleneck 
 *  procedure.
 *  @see jqd.basics.QDRegion
 *  @see jqd.basics.QDPort#stdRgn
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public class QDRegionOP extends QDShapeOpCode implements QDOpCode 
{
    /** the region handled by the opcode */
    protected QDRegion region ;

    public QDRegionOP(short verb) {
	super(verb);
    } // QDRegionOP
    
    /** reads the region opcode
     *  @param stream the input stream
     *  @exception IOException I/O problem
     *  @return number of bytes read 
     */

    public int read(QDInputStream stream) 
	throws IOException {
	region = new QDRegion(stream);
	bounds = region.getBounds();
	return region.rgnSize ;
    } // read
    
    /** executes the opcode 
     *  all work is done by the <code>stdRgn</code> method
     *  @see QDPort#stdRgn
     *  @param port the QuickDraw port to draw in
     */ 

    public void	execute(QDPort port)  throws QDException {
	port.stdRgn(verb,region);
    } // execute
 
    public String toString() {
	return super.toString() + region ; 
    } // toString

} // QDRegionOP
