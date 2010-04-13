//
// PCIReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.services.POIService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * PCIReader is the file format reader for SimplePCI (Compix) .cxd files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/PCIReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/PCIReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class PCIReader extends FormatReader {

  // -- Constants --

  public static final int PCI_MAGIC_BYTES = 0xd0cf11e0;

  // -- Fields --

  private HashMap<Integer, String> imageFiles;
  private POIService poi;
  private HashMap<Integer, Double> timestamps;
  private String creationDate;
  private int binning;

  // -- Constructor --

  /** Constructs a new SimplePCI reader. */
  public PCIReader() {
    super("Compix Simple-PCI", "cxd");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == PCI_MAGIC_BYTES;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    RandomAccessInputStream s =
      poi.getDocumentStream(imageFiles.get(new Integer(no)));
    TiffParser tp = new TiffParser(s);

    // can be raw pixel data or an embedded TIFF file

    if (tp.isValidHeader()) {
      IFD ifd = tp.getFirstIFD();
      tp.getSamples(ifd, buf);
    }
    else {
      s.seek(0);
      int planeSize = FormatTools.getPlaneSize(this);
      s.skipBytes((int) (s.length() - planeSize));
      readPlane(s, x, y, w, h, buf);
    }
    s.close();

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      imageFiles = null;
      timestamps = null;
      if (poi != null) poi.close();
      poi = null;
      binning = 0;
      creationDate = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    imageFiles = new HashMap<Integer, String>();
    timestamps = new HashMap<Integer, Double>();

    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }

    poi.initialize(Location.getMappedId(currentId));

    double scaleFactor = 1;

    Vector<String> allFiles = poi.getDocumentList();
    if (allFiles.size() == 0) {
      throw new FormatException(
        "No files were found - the .cxd may be corrupt.");
    }

    for (String name : allFiles) {
      int separator = name.lastIndexOf(File.separator);
      String parent = name.substring(0, separator);
      String relativePath = name.substring(separator + 1);

      if (relativePath.equals("Field Count")) {
        byte[] b = poi.getDocumentBytes(name, 4);
        core[0].imageCount = DataTools.bytesToInt(b, true);
      }
      else if (relativePath.equals("File Has Image")) {
        byte[] b = poi.getDocumentBytes(name, 2);
        if (DataTools.bytesToInt(b, true) == 0) {
          throw new FormatException("This file does not contain image data.");
        }
      }
      else if (relativePath.equals("Comments")) {
        String comments = new String(poi.getDocumentBytes(name));
        String[] lines = comments.split("\n");
        for (String line : lines) {
          int eq = line.indexOf("=");
          if (eq != -1) {
            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();
            addGlobalMeta(key, value);

            if (key.equals("factor")) {
              if (value.indexOf(";") != -1) {
                value = value.substring(0, value.indexOf(";"));
              }
              scaleFactor = Double.parseDouble(value.trim());
            }
          }
        }
      }
      else if (relativePath.startsWith("Bitmap") || relativePath.equals("Data"))
      {
        int space = parent.lastIndexOf(" ") + 1;
        if (space >= parent.length()) continue;
        int num = Integer.parseInt(parent.substring(space,
          parent.indexOf(File.separator, space))) - 1;
        imageFiles.put(new Integer(num), name);

        if (getSizeX() != 0 && getSizeY() != 0) {
          int bpp = FormatTools.getBytesPerPixel(getPixelType());
          int plane = getSizeX() * getSizeY() * bpp;
          core[0].sizeC = poi.getFileSize(name) / plane;
          if (getSizeC() == 0) {
            core[0].sizeX /= 16;
            core[0].sizeY /= 16;
            core[0].sizeC = poi.getFileSize(name) / plane;
          }
        }
      }
      else if (relativePath.indexOf("Image_Depth") != -1) {
        byte[] b = poi.getDocumentBytes(name, 8);
        int bits = (int) DataTools.bytesToDouble(b, true);
        while (bits % 8 != 0 || bits == 0) bits++;
        if (bits % 3 == 0) {
          core[0].sizeC = 3;
          bits /= 3;
        }
        bits /= 8;
        core[0].pixelType = FormatTools.pixelTypeFromBytes(bits, false, false);
      }
      else if (relativePath.indexOf("Image_Height") != -1 && getSizeY() == 0) {
        byte[] b = poi.getDocumentBytes(name, 8);
        core[0].sizeY = (int) DataTools.bytesToDouble(b, true);
      }
      else if (relativePath.indexOf("Image_Width") != -1 && getSizeX() == 0) {
        byte[] b = poi.getDocumentBytes(name, 8);
        core[0].sizeX = (int) DataTools.bytesToDouble(b, true);
      }
      else if (relativePath.indexOf("Time_From_Start") != -1) {
        byte[] b = poi.getDocumentBytes(name, 8);
        Double v = new Double(DataTools.bytesToDouble(b, true));

        int space = parent.lastIndexOf(" ") + 1;
        if (space >= parent.length()) continue;
        int num = Integer.parseInt(parent.substring(space,
          parent.indexOf(File.separator, space))) - 1;
        timestamps.put(new Integer(num), v);
      }
      else if (relativePath.equals("First Field Date & Time")) {
        byte[] b = poi.getDocumentBytes(name);
        long date = (long) DataTools.bytesToDouble(b, true) * 1000;
        creationDate = DateTools.convertDate(date, DateTools.COBOL);
      }
      else if (relativePath.equals("First Field Start Clock")) {
        byte[] b = poi.getDocumentBytes(name);
        double v = DataTools.bytesToDouble(b, true);
      }
      else if (relativePath.equals("Binning")) {
        byte[] b = poi.getDocumentBytes(name, 8);
        binning = (int) DataTools.bytesToDouble(b, true);
      }
    }

    if (getSizeC() == 0) core[0].sizeC = 1;

    if (timestamps.size() > 0) {
      core[0].sizeZ = getImageCount() / timestamps.size();
      core[0].sizeT = timestamps.size();
    }
    if (timestamps.size() == 0 || getSizeZ() * getSizeT() != getImageCount()) {
      core[0].sizeZ = getImageCount();
      core[0].sizeT = 1;
    }
    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = false;
    core[0].dimensionOrder = "XYCTZ";
    core[0].littleEndian = true;
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);

    if (creationDate != null) {
      store.setImageCreationDate(creationDate, 0);
    }
    else MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setDimensionsPhysicalSizeX(scaleFactor, 0, 0);
    store.setDimensionsPhysicalSizeY(scaleFactor, 0, 0);

    for (int i=0; i<timestamps.size(); i++) {
      Double timestamp = new Double(
        timestamps.get(new Integer(i)).doubleValue());
      store.setPlaneTimingDeltaT(timestamp, 0, 0, i);
      if (i == 2) {
        double first = timestamps.get(new Integer(1)).doubleValue();
        Double increment = new Double(timestamp.doubleValue() - first);
        store.setDimensionsTimeIncrement(increment, 0, 0);
      }
    }

    if (binning > 0) {
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      String detectorID = MetadataTools.createLSID("Detector", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setDetectorID(detectorID, 0, 0);
      store.setDetectorType("Unknown", 0, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      for (int c=0; c<getEffectiveSizeC(); c++) {
        store.setDetectorSettingsDetector(detectorID, 0, c);
        store.setDetectorSettingsBinning(binning + "x" + binning, 0, c);
      }
    }
  }

}
