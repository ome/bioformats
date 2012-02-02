//
// IvisionReader.java
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

import java.io.IOException;

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * IvisionReader is the file format reader for IVision (.IPM) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/IvisionReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/IvisionReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class IvisionReader extends FormatReader {

  // -- Constants --

  public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  // -- Fields --

  private boolean color16;
  private boolean squareRoot;
  private byte[] lut;
  private long imageOffset;

  private String binX, binY;
  private String creationDate;
  private String exposureTime;
  private String gain, offset;
  private String deltaT;
  private String magnification, lensNA, refractiveIndex;
  private String wavelength;

  private boolean hasPaddingByte = false;

  // -- Constructor --

  /** Constructs a new Ivision reader. */
  public IvisionReader() {
    super("IVision", "ipm");
    suffixSufficient = false;
    suffixNecessary = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockCheckLen = 4;
    if (!FormatTools.validStream(stream, blockCheckLen, true)) return false;
    String version = stream.readString(4);
    return version.equals("3.9a");
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int planeSize = getSizeX() * getSizeY() * getSizeC();
    if (color16) planeSize = 2 * (planeSize / 3);
    else if (squareRoot) planeSize *= 2;
    else if (hasPaddingByte) {
      planeSize += getSizeX() * getSizeY();
    }
    else planeSize *= FormatTools.getBytesPerPixel(getPixelType());

    in.seek(imageOffset + planeSize * no);

    if (color16) {
      // TODO
      throw new FormatException("16-bit color iVision files are not supported");
    }
    else if (squareRoot) {
      // TODO
      throw new FormatException("Square-root iVision files are not supported");
    }
    else if (hasPaddingByte) {
      int next = 0;
      in.skipBytes(y * getSizeX() * getSizeC());
      for (int row=0; row<h; row++) {
        in.skipBytes(x * getSizeC());
        for (int col=0; col<w; col++) {
          in.skipBytes(1);
          in.read(buf, next, getSizeC());
          next += getSizeC();
        }
        in.skipBytes(getSizeC() * (getSizeX() - w - x));
      }
    }
    else readPlane(in, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      color16 = false;
      squareRoot = false;
      lut = null;
      imageOffset = 0;
      binX = binY = null;
      creationDate = null;
      exposureTime = null;
      gain = offset = null;
      deltaT = null;
      magnification = lensNA = refractiveIndex = null;
      wavelength = null;
      hasPaddingByte = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("IvisionReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    status("Populating metadata");

    String version = in.readString(4);
    int fileFormat = in.read();
    int dataType = in.read();

    core[0].sizeC = 1;
    switch (dataType) {
      case 0:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 1:
        core[0].pixelType = FormatTools.INT16;
        break;
      case 2:
        core[0].pixelType = FormatTools.INT32;
        break;
      case 3:
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case 4:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC = 3;
        color16 = true;
        break;
      case 5:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC = 3;
        hasPaddingByte = true;
        break;
      case 6:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 7:
        core[0].pixelType = FormatTools.FLOAT;
        squareRoot = true;
        break;
      case 8:
        core[0].pixelType = FormatTools.UINT16;
        core[0].sizeC = 3;
        break;
    }

    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    in.skipBytes(6);

    core[0].sizeZ = in.readShort();
    in.skipBytes(50);

    core[0].sizeT = 1;

    if (getSizeX() > 1 && getSizeY() > 1) {
      lut = new byte[2048];
      in.read(lut);
    }

    imageOffset = in.getFilePointer();

    in.skipBytes(getSizeZ() * getSizeC() * getSizeT() * getSizeX() *
      getSizeY() * FormatTools.getBytesPerPixel(getPixelType()));

    // look for block of XML data
    status("Looking for XML metadata");
    boolean xmlFound = false;
    while (!xmlFound && in.getFilePointer() < in.length() - 6) {
      int len = (int) Math.min(8192, in.length() - in.getFilePointer());
      String check = in.readString(len);
      int xmlIndex = check.indexOf("<?xml");
      xmlFound = xmlIndex != -1;
      if (xmlFound) {
        in.seek(in.getFilePointer() - check.length() + xmlIndex);
      }
      else in.seek(in.getFilePointer() - 6);
    }

    if (xmlFound) {
      String xml = in.readString((int) (in.length() - in.getFilePointer()));
      xml = xml.substring(xml.indexOf("<"), xml.lastIndexOf(">") + 1);
      IvisionHandler handler = new IvisionHandler();
      XMLTools.parseXML(xml, handler);
    }

    status("Populating core metadata");
    core[0].rgb = getSizeC() > 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].littleEndian = false;
    core[0].interleaved = true;
    core[0].indexed = false;
    core[0].imageCount = getSizeZ() * getSizeT();

    status("Populating MetadataStore");
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);

    if (creationDate != null) {
      String date = DateTools.formatDate(creationDate, DATE_FORMAT);
      store.setImageCreationDate(date, 0);
    }
    else MetadataTools.setDefaultCreationDate(store, currentId, 0);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);

    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    if (deltaT != null) {
      store.setDimensionsTimeIncrement(new Float(deltaT), 0, 0);
    }

    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);

    store.setObjectiveCorrection("Unknown", 0, 0);
    store.setObjectiveImmersion("Unknown", 0, 0);

    if (lensNA != null) store.setObjectiveLensNA(new Float(lensNA), 0, 0);
    if (magnification != null) {
      store.setObjectiveNominalMagnification(new Integer(magnification), 0, 0);
    }
    if (refractiveIndex != null) {
      store.setObjectiveSettingsRefractiveIndex(new Float(refractiveIndex), 0);
    }

    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorSettingsDetector(detectorID, 0, 0);

    store.setDetectorType("Unknown", 0, 0);

    store.setDetectorSettingsBinning(binX + "x" + binY, 0, 0);
    if (gain != null) store.setDetectorSettingsGain(new Float(gain), 0, 0);
  }

  // -- Helper class --

  class IvisionHandler extends DefaultHandler {

    // -- Fields --

    private String key, value;
    private String currentElement;

    // -- DefaultHandler API methods --

    public void endElement(String uri, String localName, String qName) {
      addGlobalMeta(key, value);
      if ("iplab:Bin_X".equals(key)) binX = value;
      else if ("iplab:Bin_Y".equals(key)) binY = value;
      else if ("iplab:Capture_Date".equals(key)) creationDate = value;
      else if ("iplab:Exposure".equals(key)) exposureTime = value;
      else if ("iplab:Gain".equals(key)) gain = value;
      else if ("iplab:Offset".equals(key)) offset = value;
      else if ("iplab:Interval_T".equals(key)) deltaT = value;
      else if ("iplab:Objective_Mag".equals(key)) magnification = value;
      else if ("iplab:Objective_NA".equals(key)) lensNA = value;
      else if ("iplab:Objective_RI".equals(key)) refractiveIndex = value;
      else if ("iplab:Wavelength".equals(key)) wavelength = value;
    }

    public void characters(char[] ch, int start, int length) {
      String v = new String(ch, start, length);
      if ("key".equals(currentElement)) {
        key = v;
      }
      else value = v;
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentElement = qName;
    }

  }

}
