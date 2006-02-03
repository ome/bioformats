//                              -*- Mode: Java -*- 
// QDPixMapNotSupported.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:23:32 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:24:19 2000
// Update Count    : 1
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.*;

public class QDPixMapNotSupported extends QDException {
    final int pixSize ;
    final int pixType ;
    final int packType ;
    public QDPixMapNotSupported(int pixSize, int pixType, int packType){
	this.pixSize = pixSize ;
	this.pixType = pixType ;
	this.packType = packType ;
    } // QDPixMapNotSupported
    
    public String toString() {
	return 
	    "PixMap not supported [pixel size="+
	    pixSize+", pixel type="+
	    pixType+ "packing type "+
	    packType+"]";
    } // toString
} // QDPixMapNotSupported
