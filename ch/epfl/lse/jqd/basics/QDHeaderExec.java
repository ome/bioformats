package ch.epfl.lse.jqd.basics;

/** This interface is implmented by ocopde that need to be executed during parsing
 *  @author Matthias Wiesmann
 *  @version 1.0
 *  @see jqd.opcode.OpCode
 */

public interface QDHeaderExec 
{
    /** This method is called during the parsing of the picture */
    void headerExecute(QDPicture thePicture);
} // QDHeaderExec


