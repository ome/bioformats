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

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;

/**
 * JPKReader is the file format reader for JPK Instruments files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JPKReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JPKReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class JPKReader extends BaseTiffReader {

  // -- Constructor --

  /** Constructs a new JPK reader. */
  public JPKReader() {
    super("JPK Instruments", "jpk");
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    return checkSuffix(name, "jpk");
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (getSeriesCount() == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    int ifd = series == 0 ? 0 : no + 1;
    tiffParser.fillInIFD(ifds.get(ifd));
    tiffParser.getSamples(ifds.get(ifd), buf, x, y, w, h);
    return buf;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    ifds = tiffParser.getIFDs();

    core = new CoreMetadata[ifds.size() > 1 ? 2 : 1];

    // repopulate core metadata

    for (int s=0; s<core.length; s++) {
      core[s] = new CoreMetadata();
      IFD ifd = ifds.get(s);
      tiffParser.fillInIFD(ifd);
      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      core[s].rgb = samples > 1 || p == PhotoInterp.RGB;

      core[s].sizeX = (int) ifd.getImageWidth();
      core[s].sizeY = (int) ifd.getImageLength();
      core[s].sizeZ = 1;
      core[s].sizeT = s == 0 ? 1 : ifds.size() - 1;
      core[s].sizeC = core[s].rgb ? samples : 1;
      core[s].littleEndian = ifd.isLittleEndian();
      core[s].indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      core[s].imageCount = s == 0 ? 1 : ifds.size() - 1;
      core[s].pixelType = ifd.getPixelType();
      core[s].metadataComplete = true;
      core[s].interleaved = false;
      core[s].falseColor = false;
      core[s].dimensionOrder = "XYCZT";

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        setSeries(s);
        for (Integer key : ifds.get(s).keySet()) {
          if (key >= 32848) {
            addSeriesMeta("Tag " + key, ifds.get(s).get(key));
          }
        }
      }
    }
    setSeries(0);
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName("Series " + (i + 1), i);
    }
  }

}
