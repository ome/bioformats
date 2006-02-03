package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.managers.QDFontManager;

import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** This OpCode sets a font name 
 * @author Matthias Wiesmann
 * @version 1.0 revised
 */

public class QDFontNameOP implements QDOpCode {
    /** The length of the data */
    protected short  dataLength ;
    /** The id of the font */
    protected short  oldFontID ;
    /** The name of the font */ 
    protected String fontName ;
	

    /** Reads the opcode from a stream
     * @param theStream the stream to read from
     * @return the number of bytes read
     * @exception IOException I/O Problem
     * @exception QDException Font Name inconsistency
     * Happens if the length of the font name
     * (Pascal String) does not match the length of the
     * OpCode. Generally implies the file is corrupt. 
     */

    public int 	read(QDInputStream theStream)
	throws IOException, QDException {
	dataLength=theStream.readShort();
	oldFontID=theStream.readShort();
	fontName=theStream.readString();
	if (fontName.length()+3!=dataLength) 
	    throw new QDFontNameError(this);
	return(dataLength);
    } // load
    
    public String toString()  {
	return ("Font Name "+fontName+" id="+oldFontID) ;
    }// toString
    
    public void	execute(QDPort thePort) {
	final QDFontManager fManager = thePort.getFontManager();
	fManager.addFont(fontName,oldFontID) ;
	thePort.txFont = oldFontID ;
    } // execute 
} // QDVersionOP

/** Font Name Inconsistency */

class QDFontNameError extends QDException 
{
    private QDFontNameOP theOP ;
    public QDFontNameError(QDFontNameOP theOP) {
	this.theOP=theOP ;
    } // QDFontNameError
    public String toString() {
	return "Font Name Error "+theOP ;
    } // toString
} // QDFontNameError
