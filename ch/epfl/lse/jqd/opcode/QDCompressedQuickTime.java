//                              -*- Mode: Java -*- 
// QDCompressedQuickTime.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:15:26 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:13:03 2000
// Update Count    : 4
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.*;

import ch.epfl.lse.jqd.qt.CompressedImage;

import java.io.IOException;

/** This opcode represents compressed QuickTime Data.<br>
 *  <strong>Warning</strong> this class does nothing at the moment.
 *  @version 1.0 revised
 *  @author Matthias Wiesmann
 */ 

public class QDCompressedQuickTime implements QDOpCode {
    /** Length of QuickTime data */
    protected int length ;
    /** Compressed image header */
    protected CompressedImage image ;
    /** number of images ? */
    protected int number ;

    protected static final int SKIP_HEAD = 4 * 16 ; 

    public int read(QDInputStream theStream) 
	throws IOException, QDException {
	length=theStream.readInt();
	number=theStream.readInt();
	theStream.skipBytes(SKIP_HEAD);
	image = new CompressedImage();
	final int intern = image.read(theStream);
	final int rest = length - intern - SKIP_HEAD - 4;
	theStream.skipBytes(rest);
	return 4+length ;
    } // read
	
    public void	execute(QDPort thePort) {
    } // execute 
    
    public String toString() {
	return "QuickTime Compressed"+
	    " ["+image+"]" ;
    } // toString

} // QDCompressedQuickTime
