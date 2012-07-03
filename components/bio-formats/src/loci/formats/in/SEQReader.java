/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * SEQReader is the file format reader for Image-Pro Sequence files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/SEQReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/SEQReader.java;hb=HEAD">Gitweb</a></dd></dl>
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

  // -- Constructor --

  /** Constructs a new Image-Pro SEQ reader. */
  public SEQReader() {
    super("Image-Pro Sequence", "seq");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    parser.setDoCaching(false);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    Object tag = ifd.get(IMAGE_PRO_TAG_1);
    return tag != null && (tag instanceof short[]);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core[0].sizeZ = 0;
    core[0].sizeT = 0;

    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    for (IFD ifd : ifds) {
      if (level != MetadataLevel.MINIMUM) {
        short[] tag1 = (short[]) ifd.getIFDValue(IMAGE_PRO_TAG_1);

        if (tag1 != null) {
          String seqId = "";
          for (int i=0; i<tag1.length; i++) seqId = seqId + tag1[i];
          addGlobalMeta("Image-Pro SEQ ID", seqId);
        }
      }

      int tag2 = ifds.get(0).getIFDIntValue(IMAGE_PRO_TAG_2);

      if (tag2 != -1) {
        // should be one of these for every image plane
        core[0].sizeZ++;
        addGlobalMeta("Frame Rate", tag2);
      }

      addGlobalMeta("Number of images", getSizeZ());
    }

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    if (getSizeZ() == 1 && getSizeT() == 1) {
      core[0].sizeZ = ifds.size();
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
        int eq = token.indexOf("=");
        if (eq == -1) eq = token.indexOf(":");
        if (eq != -1) {
          String label = token.substring(0, eq);
          String data = token.substring(eq + 1);
          addGlobalMeta(label, data);
          if (label.equals("channels")) core[0].sizeC = Integer.parseInt(data);
          else if (label.equals("frames")) {
            core[0].sizeT = Integer.parseInt(data);
          }
          else if (label.equals("slices")) {
            core[0].sizeZ = Integer.parseInt(data);
          }
        }
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
