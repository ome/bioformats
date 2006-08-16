//
// SEQReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.IOException;
import java.util.StringTokenizer;
import loci.formats.*;

/**
 * SEQReader is the file format reader for Image-Pro Sequence files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class SEQReader extends BaseTiffReader {

  /** Number of optical sections in the file */
  private int sizeZ = 0;

  /** Number of timepoints in the file */
  private int sizeT = 1;

  // -- Constants --

  /**
   * An array of shorts (length 12) with identical values in all of our
   * samples; assuming this is some sort of format identifier.
   */
  private static final int IMAGE_PRO_TAG_1 = 50288;

  /** Frame rate. */
  private static final int IMAGE_PRO_TAG_2 = 40105;


  // -- Constructor --

  /** Constructs a new Image-Pro SEQ reader. */
  public SEQReader() { super("Image-Pro Sequence", "seq"); }

  // -- FormatReader API methods --

  /**
   * Allows the client to specify whether or not to separate channels.
   * By default, channels are left unseparated; thus if we encounter an RGB
   * image plane, it will be left as RGB and not split into 3 separate planes.
   */
  public void setSeparated(boolean separate) {
    separated = separate;
    super.setSeparated(separate);
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return Integer.parseInt((String) metadata.get("frames"));
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return Integer.parseInt((String) metadata.get("slices"));
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return Integer.parseInt((String) metadata.get("channels"));
  }

  // -- Internal BaseTiffReader API methods --

  /** Overridden to include the three SEQ-specific tags. */
  protected void initStandardMetadata() {
    super.initStandardMetadata();

    for (int j=0; j<ifds.length; j++) {
      short[] tag1 = (short[]) TiffTools.getIFDValue(ifds[j], IMAGE_PRO_TAG_1);

      if (tag1 != null) {
        String seqId = "";
        for (int i=0; i<tag1.length; i++) seqId = seqId + tag1[i];
        metadata.put("Image-Pro SEQ ID", seqId);
      }

      int tag2 = TiffTools.getIFDIntValue(ifds[0], IMAGE_PRO_TAG_2);

      if (tag2 != -1) {
        // should be one of these for every image plane
        sizeZ++;
        metadata.put("Frame Rate", new Integer(tag2));
      }

      metadata.put("Number of images", new Integer(sizeZ));
    }

    if (sizeZ == 0) sizeZ++;

    if (sizeZ == 1 && sizeT == 1) {
      sizeZ = ifds.length;
    }

    // default values
    metadata.put("frames", "" + sizeZ);
    metadata.put("channels", metadata.get("NumberOfChannels").toString());
    metadata.put("slices", "" + sizeT);

    // parse the description to get channels, slices and times where applicable
    String descr = (String) metadata.get("Comment");
    metadata.remove("Comment");
    if (descr != null) {
      StringTokenizer tokenizer = new StringTokenizer(descr, "\n");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        String label = token.substring(0, token.indexOf("="));
        String data = token.substring(token.indexOf("=") + 1);
        metadata.put(label, data);
      }
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new SEQReader().testRead(args);
  }

}
