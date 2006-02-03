//                              -*- Mode: Java -*- 
// CompressedImage.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:49:54 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:50:19 1999
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.qt;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;

/** This class represents a QuickTime image header 
 *  <br>The actual data is not handled. 
 *  @version 1.0
 *  @author Matthias Wiesmann
 */ 

public class CompressedImage {
    protected int size ;
    protected String compressorCreator ;
    protected String compressorDeveloper ;
    protected short version ;
    protected short revision ;
    protected int   temporalQuality ;
    protected int   spatialQuality ;
    protected Dimension dimension ;
    protected double hRes ;
    protected double vRes ;
    protected int dataSize ;
    protected short frameNumber ;
    protected String name ;
    protected short depth ;
    protected short clutId ; 
    
    public int read(DataInputStream input)
	throws IOException, QDException {
	size = input.readInt();
	compressorCreator = QDLoader.readCreator(input);
	input.skipBytes(8); 
	version = input.readShort();
	revision = input.readShort(); 
	compressorDeveloper = QDLoader.readCreator(input); 
	temporalQuality = input.readInt(); 
	spatialQuality = input.readInt();  
	dimension = QDLoader.readDimension(input); 
	hRes = QDLoader.readFixed(input); 
	vRes = QDLoader.readFixed(input); 
	dataSize = input.readInt(); 
	frameNumber = input.readShort(); 
	name = QDLoader.readStr31(input); 
	depth = input.readShort(); 
	clutId = input.readShort(); 
	return size ;
    } // load
    
    public String toString() { 
	return 
	    name+
	    ", compressor="+compressorCreator+"-"+compressorDeveloper+
	    ", resolution="+(int) hRes+"/"+(int)vRes +
	    ", dimension="+ dimension +
	    ", depth="+depth ;
    }// toString
    
} // CompressedImage
