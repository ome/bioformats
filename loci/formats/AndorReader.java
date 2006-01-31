//
// AndorReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.*;

/**
 * Reader is the file format reader for Andor TIFF files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class AndorReader extends BaseTiffReader {

  // -- Constants --
  
  /** Andor TIFF private IFD tags. */
  private static final int MMHEADER = 317;
  private static final int MMSTAMP = 34362;
	

  // -- Constructor --

  /** Constructs a new Andor reader. */
  public AndorReader() { super("Andor", new String[] {"tif", "tiff"}); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Metamorph file. */
  public boolean isThisType(byte[] block) {
    // adapted from MetamorphReader.isThisType(byte[])

    if (block.length < 3) return false;
    if (block.length < 8) return true; // we have no way of verifying further
    
    int ifdlocation = DataTools.bytesToInt(block, 4 , true);
    if (ifdlocation + 1 > block.length) { return true; }
    else {  
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) return true;
        else {
          int ifdtag = DataTools.bytesToInt(block, 
  	    ifdlocation + 2 + (i*12), 2, true);
 	  if (ifdtag == MMHEADER || ifdtag == MMSTAMP) return true;
        } 	    
      } 	  
      return false;
    }  
  }	  
  
  // -- Internal BaseTiffReader API methods --

  /** Populate the metadata hashtable. */
  protected void initMetadata() {
    super.initMetadata();

    metadata.put("Andor TIFF identifier", "" + TiffTools.getIFDIntValue(ifds[0], MMHEADER));
    short[] stamp = (short[]) TiffTools.getIFDValue(ifds[0], MMSTAMP);
    String dataStamp = "";
    for(int i=0; i<stamp.length; i++) {
      dataStamp += stamp[i];
      if (i < stamp.length - 1) {
        dataStamp += ", ";
      }	      
    }
    
    metadata.put("Data Stamp for the first plane", dataStamp);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new AndorReader().testRead(args);
  }

}
