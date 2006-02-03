//                              -*- Mode: Java -*- 
// QDPicture.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 11:54:14 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:54:47 2000
// Update Count    : 13
// Status          : OK
// 

package ch.epfl.lse.jqd.basics;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

import java.io.File;
import java.io.FileInputStream;

import java.awt.Rectangle;

import java.util.Vector;

import java.net.URL;
import java.net.URLConnection; 

import ch.epfl.lse.jqd.io.QDInputStream;
import ch.epfl.lse.jqd.opcode.QDOpCode;
import ch.epfl.lse.jqd.opcode.QDParser;
import ch.epfl.lse.jqd.utils.QDLoader;
import ch.epfl.lse.jqd.utils.QDGeneralUtils;


/** This class defines a QuickDraw picture.
 *  The picture is composed by a sequence of opcodes. 
 *  Those opcodes can be executed inside a QuickDraw Port. 
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public class QDPicture
{
    /** size of the header (ignored bytes) */
    protected static final short HEADER_SIZE = 512 ;
    protected boolean 		verbose ;
    
    /** old size unreliable if &gt; 32 k */
    protected short			picSize ;
    /** actual size of the picture file */
    protected long 			picLen ;
    /** the picture frame at 72 DPI */
    protected  Rectangle picFrame ;
    /** version - values 1 and 2 supported 
     *  should not be changed, access should be done 
     *  with <code>getVersion</code>
     *  @see #getVersion
     */
    public int version=1 ;
    /** number of opcodes */ 
    protected int totalLine = 0 ; 

    /** the array containing all opcodes */
    protected QDOpCode[]	opCodes ;

 
    /** builds an empty picture (non loaded)
     * the verbose mode is on if the system 
     * property <code>QDVerbose</code>
     * is defined.
     * In case of <code>SecurityException</code>
     * i.e. we are in an applet
     * the verbose is set to false.
     * @see java.lang.SecurityException
     */

    protected QDPicture() {
	try {
	    final String param = System.getProperty("QDVerbose");
	    if (param!= null) this.verbose = true ;
	    else this.verbose = false ; 
	} catch (SecurityException e) {
	    this.verbose = false ;
	} // try / catch
    } // QDPicture
    
    /** builds a picture from a file.  
     * @param file the <code>Pict</code> file
     * @exception IOException file error
     * @exception QDException QuickDraw Error
     */ 

    public QDPicture(File file)
	throws QDException, IOException {
	this();
	read(file);
    } //  QDPicture
    
    /** builds a picture from an URL
     * @param url the URL of the <code>Pict</code> file
     * @exception IOException file or network error
     * @exception QDException QuickDraw Error
     */ 

    public QDPicture(URL url)
	throws QDException, IOException {
	this();
	read(url);
    } // QDPicture
    
    /** dumps info about the picture 
     * @return info string 
     */ 

    public String toString() {
	return("QDPicture size= "+picLen+"("+picSize+")\t frame "+picFrame);
    } // toString
    
    /** Method from Shape 
     *  @return the bounds of the picture at 72 dpi 
     */ 

    public Rectangle getBounds() { return picFrame ; } 

    /** Gets the version number 
     *  @return the quickdraw version number
     */ 
    public int getVersion() { return version ; }

    /** Reads the next code on the stream. 
     * The acutal reading depends on the picture version. 
     * @param stream the stream to read on
     * @return the value (code) of the next Opcode
     * @exception IOException I/O Problem
     * @exception QDException wrong QuickDraw Version
     */ 

    protected int readNextCode(QDInputStream stream)
	throws IOException, QDException {
	switch(version) {
	case 1: return stream.readUnsignedByte();
	case 2: return stream.readUnsignedShort();
	default: throw  new QDUnknownPictVersion(version);
	} // swithc
    } // readNextCode
    
    /** Aligns the stream on word boundaries.<br>
     * Word size: 
     * <ol><li>8 bits (version 1)<li>16 bits (version 2)</ol>
     * @param stream the stream to align
     * @param size the size of the last opcode
     * @exception IOException I/O Problem
     * @exception QDException wrong QuickDraw Version
     */
	
    protected void alignCode(QDInputStream stream, int size)
	throws IOException, QDException {
	    switch(version) {
	    case 1: totalLine+=(size+1); return ;
	    case 2: 
		totalLine+=(size+2); 
		if (size%2==1) 
		    { stream.skipBytes(1); totalLine++ ; }
		return ;
	    default: throw  new QDUnknownPictVersion(version);
	    } // switch
    } // alignCode
    
    /** Checks if the opcode is a header opcode
     *  if it is, executes it.
     *  (Header opcode have to executed during parsing). 
     *  @param opcode the opcode to check/execute
     *  @exception QDException QuickDraw Problem
     *  @see QDHeaderExec
     */

    protected void executeHeaderOp(QDOpCode opcode)
	throws QDException {
	if ((opcode instanceof QDHeaderExec)== false) return ;
	final QDHeaderExec exec = (QDHeaderExec) opcode ;
	exec.headerExecute(this);
    } // executeHeaderOp
    
    /** Reads the next opcode.<br>
     * This is the core method that does all the data reading
     * @param 	theStream 	the stream where the data is read 
     * @exception IOException I/O related problem
     * @exception QDException QuickDraw Related problem 
     */
    protected QDOpCode readNextOP(QDInputStream theStream) 
	throws IOException, QDException {
	final int theCode = readNextCode(theStream);
	final QDOpCode theOp = QDParser.getOp(theCode) ;		
	if (theOp==null) {
	    System.out.println(Integer.toString(totalLine,16)+"\t"+
			       Integer.toString(theCode,16));
	    return theOp ;
	} // if (theOp==null)      
	int size=theOp.read(theStream);
	if (verbose) {
	    System.out.println(Integer.toString(totalLine,16)+
			       "\t"+
			       Integer.toString(theCode,16)+
			       "\t"+
			       theOp.toString());
	} // if verbose
	if (size<0) throw new QDException("illegal size "+size);
	alignCode(theStream,size);
	executeHeaderOp(theOp);
	return theOp ;
    } //  readNextOP
		
    
    /** reads the picture data from a stream.<br>
     * When the reading is done, the stream is closed. 
     * @param theStream the stream where the data is read.
     * @exception IOException I/O problem
     * @exception QDException QuickDraw parsing problem
     */ 
    protected synchronized void read(QDInputStream theStream) 
	throws IOException, QDException {
	theStream.skipBytes(HEADER_SIZE);
	picSize=theStream.readShort();
	picFrame=QDLoader.readRect(theStream);
	if (verbose) System.out.println(this);
	Vector v = new Vector(); 
	while (true) {
	    QDOpCode nextCode = readNextOP(theStream);
	    if (nextCode== null) break ;
	    if (! QDParser.discard(nextCode)) v.addElement(nextCode);
	    else if (verbose) System.out.println("discarding "+nextCode);
	} // parseLoop
	opCodes = QDGeneralUtils.buildArray(v);
	theStream.close();
    } // load
    
    /** reads the picture data from a stream.<br>
     * When the reading is done, the stream is closed. 
     * @param dataStream the stream where the data is read 
     * @exception IOException I/O problem
     * @exception QDException QuickDraw parsing problem
     */ 

    protected void read(InputStream dataStream)
	throws IOException, QDException {
	DataInputStream textStream = new DataInputStream(dataStream) ;
	QDInputStream qdStream = new QDInputStream(textStream);
	read(qdStream);
	dataStream.close();
    } // read
    
     /** reads the picture data from an URL 
      * @param srcURL the url form where the data is read 
      * @exception IOException I/O problem
      * @exception QDException QuickDraw Parsing Problem
      */	
    
    protected void read(URL  srcURL)
	throws IOException, QDException {
	final  URLConnection connect = srcURL.openConnection() ;
	final InputStream dataStream = connect.getInputStream();
	picLen = connect.getContentLength()-HEADER_SIZE;
	read(dataStream);
    } // read
    
    /** reads the picture data from a file
     * @param file the file to read
     * @exception IOException I/O problem
     * @exception QDException QuickDraw Parsing Problem
     */

    protected void read(File file)
	throws IOException, QDException {
	final FileInputStream stream = new FileInputStream(file);
	picLen = file.length()-HEADER_SIZE;
	read(stream);
    } // read
    
    /** Executes the QuickDraw sequence of the Port 
     *  @param thePort the port to draw on 
     *  @exception QDException QuickDraw display error
     */

    public void execute(QDPort thePort) throws QDException {
	if (verbose) System.err.println("executing into "+thePort);
	thePort.setPortRect(picFrame);
	thePort.penNormal(); 
	thePort.txtNormal();
	for (int i=0;i<opCodes.length;i++)
	    opCodes[i].execute(thePort);
    } // execute
} // QDPicture







