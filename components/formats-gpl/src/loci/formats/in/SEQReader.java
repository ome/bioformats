/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * SEQReader is the file format reader for Image-Pro Sequence files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
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

  private static final int IMAGE_PRO_TAG_3 = 40100;

  // -- Constructor --

  /** Constructs a new Image-Pro SEQ reader. */
  public SEQReader() {
    super("Image-Pro Sequence", "seq");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    parser.setDoCaching(false);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    parser.fillInIFD(ifd);
    Object tag1 = ifd.get(IMAGE_PRO_TAG_1);
    Object tag3 = ifd.get(IMAGE_PRO_TAG_3);
    return (tag1 != null && (tag1 instanceof short[])) || (tag3 != null &&
      (tag3 instanceof int[]));
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    CoreMetadata m = core.get(0);

    m.sizeZ = 0;
    m.sizeT = 0;

    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    for (IFD ifd : ifds) {
      if (level != MetadataLevel.MINIMUM) {
        short[] tag1 = (short[]) ifd.getIFDValue(IMAGE_PRO_TAG_1);

        if (tag1 != null) {
          StringBuffer seqId = new StringBuffer();
          for (int i=0; i<tag1.length; i++) {
            seqId.append(tag1[i]);
          }
          addGlobalMeta("Image-Pro SEQ ID", seqId.toString());
        }
      }

      int tag2 = ifds.get(0).getIFDIntValue(IMAGE_PRO_TAG_2);

      if (tag2 != -1) {
        // should be one of these for every image plane
        m.sizeZ++;
        addGlobalMeta("Frame Rate", tag2);
      }

      addGlobalMeta("Number of images", getSizeZ());
    }

    if (getSizeZ() == 0) m.sizeZ = 1;
    if (getSizeT() == 0) m.sizeT = 1;

    if (getSizeZ() == 1 && getSizeT() == 1) {
      m.sizeZ = ifds.size();
    }

    // default values
    addGlobalMeta("frames", getSizeZ());
    addGlobalMeta("channels", super.getSizeC());
    addGlobalMeta("slices", getSizeT());

    // parse the description to get channels, slices and times where applicable
    String descr = ifds.get(0).getComment();
    metadata.remove("Comment");
    if (descr != null) {
      String[] lines = descr.split("\n");
      for (String token : lines) {
        token = token.trim();
        int eq = token.indexOf('=');
        if (eq == -1) eq = token.indexOf(':');
        if (eq != -1) {
          String label = token.substring(0, eq);
          String data = token.substring(eq + 1);
          addGlobalMeta(label, data);
          if (label.equals("channels")) m.sizeC = Integer.parseInt(data);
          else if (label.equals("frames")) {
            m.sizeT = Integer.parseInt(data);
          }
          else if (label.equals("slices")) {
            m.sizeZ = Integer.parseInt(data);
          }
        }
      }
    }

    if (isRGB() && getSizeC() != 3) m.sizeC *= 3;

    m.dimensionOrder = "XY";

    int maxNdx = 0, max = 0;
    int[] dims = {getSizeZ(), getSizeC(), getSizeT()};
    String[] axes = {"Z", "C", "T"};

    for (int i=0; i<dims.length; i++) {
      if (dims[i] > max) {
        max = dims[i];
        maxNdx = i;
      }
    }

    m.dimensionOrder += axes[maxNdx];

    if (maxNdx != 1) {
      if (getSizeC() > 1) {
        m.dimensionOrder += 'C';
        m.dimensionOrder += (maxNdx == 0 ? axes[2] : axes[0]);
      }
      else m.dimensionOrder += (maxNdx == 0 ? axes[2] : axes[0]) + "C";
    }
    else {
      if (getSizeZ() > getSizeT()) m.dimensionOrder += "ZT";
      else m.dimensionOrder += "TZ";
    }
  }

}
