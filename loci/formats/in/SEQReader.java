//
// SEQReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/SEQReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/SEQReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
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

  // -- Constructor --

  /** Constructs a new Image-Pro SEQ reader. */
  public SEQReader() { super("Image-Pro Sequence", "seq"); }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core[0].sizeZ = 0;
    core[0].sizeT = 0;

    for (int j=0; j<ifds.length; j++) {
      short[] tag1 = (short[]) TiffTools.getIFDValue(ifds[j], IMAGE_PRO_TAG_1);

      if (tag1 != null) {
        String seqId = "";
        for (int i=0; i<tag1.length; i++) seqId = seqId + tag1[i];
        addMeta("Image-Pro SEQ ID", seqId);
      }

      int tag2 = TiffTools.getIFDIntValue(ifds[0], IMAGE_PRO_TAG_2);

      if (tag2 != -1) {
        // should be one of these for every image plane
        core[0].sizeZ++;
        addMeta("Frame Rate", tag2);
      }

      addMeta("Number of images", getSizeZ());
    }

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    if (getSizeZ() == 1 && getSizeT() == 1) {
      core[0].sizeZ = ifds.length;
    }

    // default values
    addMeta("frames", getSizeZ());
    addMeta("channels", super.getSizeC());
    addMeta("slices", getSizeT());

    // parse the description to get channels, slices and times where applicable
    String descr = TiffTools.getComment(ifds[0]);
    metadata.remove("Comment");
    if (descr != null) {
      StringTokenizer tokenizer = new StringTokenizer(descr, "\n");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        String label = token.substring(0, token.indexOf("="));
        String data = token.substring(token.indexOf("=") + 1);
        addMeta(label, data);
        if (label.equals("channels")) core[0].sizeC = Integer.parseInt(data);
        else if (label.equals("frames")) core[0].sizeZ = Integer.parseInt(data);
        else if (label.equals("slices")) core[0].sizeT = Integer.parseInt(data);
      }
    }

    if (isRGB() && getSizeC() != 3) core[0].sizeC *= 3;

    core[0].dimensionOrder = "XY";

    int maxNdx = 0, max = 0;
    int[] dims = {getSizeZ(), getSizeC(), getSizeT()};
    String[] axes = {"Z", "C", "T"};

    for (int i=0; i<dims.length; i++) {
      if (dims[i] > max) {
        max = dims[i];
        maxNdx = i;
      }
    }

    core[0].dimensionOrder += axes[maxNdx];

    if (maxNdx != 1) {
      if (getSizeC() > 1) {
        core[0].dimensionOrder += "C";
        core[0].dimensionOrder += (maxNdx == 0 ? axes[2] : axes[0]);
      }
      else core[0].dimensionOrder += (maxNdx == 0 ? axes[2] : axes[0]) + "C";
    }
    else {
      if (getSizeZ() > getSizeT()) core[0].dimensionOrder += "ZT";
      else core[0].dimensionOrder += "TZ";
    }
  }

}
