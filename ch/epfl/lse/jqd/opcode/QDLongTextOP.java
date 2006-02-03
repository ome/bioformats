package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import java.awt.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcodes draws text at an absolute position 
 *  @version 1.0
 *  @author Matthias Wiesmann
 */ 

public class QDLongTextOP implements QDOpCode {
    protected Point 	position ;
    protected String	text ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	position = theStream.readPoint();
	text = theStream.readString();
	return(5+text.length());
    } // read

    public void	execute(QDPort thePort) throws QDException {
	thePort.txLoc=position ;
	thePort.stdText(text);
    } // execute 
    
    public String toString() {
	return "Long Text"+position+"\t"+text ;
    } // toString
} // QDLongTextOP
