//
// SEQReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * SEQReader is the file format reader for Image-Pro Sequence files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class SEQReader extends BaseTiffReader {

  // -- Constants --

  /**
   * An array of shorts (length 12) with identical values in all of our
   * samples; assuming this is some sort of format identifier.
   */
  private static final int IMAGE_PRO_TAG_1 = 50288;

  /** Frame rate. */
  private static final int IMAGE_PRO_TAG_2 = 40105;

  /** Guessing this is thumbnail pixel data. */
  private static final int IMAGE_PRO_TAG_3 = 40106;


  // -- Constructor --

  /** Constructs a new Image-Pro SEQ reader. */
  public SEQReader() { super("Image-Pro Sequence", "seq"); }


  // -- Internal BaseTiffReader API methods --

  /** Overridden to include the three SEQ-specific tags. */
  protected void initStandardMetadata() {
    super.initStandardMetadata();

    int imageCount = 0;
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
        imageCount++;
        metadata.put("Frame Rate", new Integer(tag2));
      }
      else {
        imageCount = 1;
      }
      metadata.put("Number of images", new Integer(imageCount));
    }

    String description =
      (String) TiffTools.getIFDValue(ifds[0], TiffTools.IMAGE_DESCRIPTION);

    // default values
    metadata.put("slices", new Integer(1));
    metadata.put("channels", new Integer(1));
    metadata.put("frames", new Integer(imageCount));

    // parse the description to get channels, slices and times where applicable
    if (description != null) {
      StringTokenizer tokenizer = new StringTokenizer(description, "\n");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        String label = token.substring(0, token.indexOf("="));
        String data = token.substring(token.indexOf("=") + 1);
        metadata.put(label, data);
      }
    }
  }

  /** Overridden to include the three SEQ-specific tags. */
  protected void initOMEMetadata() {
    super.initOMEMetadata();

    if ((ome != null) &&
      ((Integer) metadata.get("Number of images")).intValue() != 1)
    {
      Integer sizeZ;
      Integer sizeT;

      if (metadata.get("slices") instanceof Integer) {
        sizeZ = (Integer) metadata.get("slices");
      }
      else sizeZ = new Integer("" + metadata.get("slices"));

      if (metadata.get("frames") instanceof Integer) {
        sizeT = (Integer) metadata.get("frames");
      }
      else sizeT = new Integer("" + metadata.get("frames"));

      OMETools.setPixels(ome, null, null,
        sizeZ, null, sizeT, null, null, null);
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new SEQReader().testRead(args);
  }

}
