//
// FlexReader.java
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
import java.util.Arrays;
import java.util.Vector;
import loci.formats.*;
import loci.formats.meta.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * FlexReader is a file format reader for Evotec Flex files.
 * To use it, the LuraWave decoder library, lwf_jsdk2.6.jar, must be available,
 * and a LuraWave license key must be specified in the lurawave.license system
 * property (e.g., <code>-Dlurawave.license=XXXX</code> on the command line).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FlexReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FlexReader.java">SVN</a></dd></dl>
 */
public class FlexReader extends BaseTiffReader {

  // -- Constants --

  /** Custom IFD entry for Flex XML. */
  protected static final int FLEX = 65200;

  // -- Fields --

  /** Scale factor for each image. */
  protected double[] factors;

  /** Camera binning values. */
  private int binX, binY;

  // -- Constructor --

  /** Constructs a new Flex reader. */
  public FlexReader() { super("Evotec Flex", "flex"); }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);

    int nBytes = TiffTools.getBitsPerSample(ifds[no])[0] / 8;

    // expand pixel values with multiplication by factor[no]
    byte[] bytes = super.openBytes(no, buf, x, y, w, h);

    if (binX > 1) {
      // actually 8 bit data
      int num = bytes.length / binX;
      byte[] t = new byte[bytes.length];
      System.arraycopy(bytes, 0, t, 0, t.length);
      Arrays.fill(bytes, (byte) 0);
      int bpp = FormatTools.getBytesPerPixel(getPixelType());
      for (int i=num-1; i>=0; i--) {
        int q = (int) ((t[i] & 0xff) * factors[no]);
        DataTools.unpackBytes(q, bytes, i * bpp, bpp, isLittleEndian());
      }
    }
    else {
      if (getPixelType() == FormatTools.UINT8) {
        int num = bytes.length;
        for (int i=num-1; i>=0; i--) {
          int q = (int) ((bytes[i] & 0xff) * factors[no]);
          bytes[i] = (byte) (q & 0xff);
        }
      }
      else if (getPixelType() == FormatTools.UINT16) {
        int num = bytes.length / 2;
        for (int i=num-1; i>=0; i--) {
          int q = nBytes == 1 ? (int) ((bytes[i] & 0xff) * factors[no]) :
            (int) (DataTools.bytesToInt(bytes, i*2, 2, isLittleEndian()) *
            factors[no]);
          DataTools.unpackBytes(q, bytes, i * 2, 2, isLittleEndian());
        }
      }
      else if (getPixelType() == FormatTools.UINT32) {
        int num = bytes.length / 4;
        for (int i=num-1; i>=0; i--) {
          int q = nBytes == 1 ? (int) ((bytes[i] & 0xff) * factors[no]) :
            (int) (DataTools.bytesToInt(bytes, i*4, nBytes,
            isLittleEndian()) * factors[no]);
          DataTools.unpackBytes(q, bytes, i * 4, 4, isLittleEndian());
        }
      }
    }
    System.arraycopy(bytes, 0, buf, 0, bytes.length);
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    factors = null;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core[0].orderCertain = false;

    // parse factors from XML
    String xml = (String) TiffTools.getIFDValue(ifds[0],
      FLEX, true, String.class);

    // HACK - workaround for Windows and Mac OS X bug where
    // SAX parser fails due to improperly handled mu (181) characters.
    byte[] c = xml.getBytes();
    for (int i=0; i<c.length; i++) {
      if (c[i] > '~' || (c[i] != '\t' && c[i] < ' ')) c[i] = ' ';
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    Vector n = new Vector();
    Vector f = new Vector();
    DefaultHandler handler = new FlexHandler(n, f, store);
    DataTools.parseXML(c, handler);

    // verify factor count
    int nsize = n.size();
    int fsize = f.size();
    if (debug && (nsize != fsize || nsize != getImageCount())) {
      LogTools.println("Warning: mismatch between image count, " +
        "names and factors (count=" + getImageCount() +
        ", names=" + nsize + ", factors=" + fsize + ")");
    }
    for (int i=0; i<nsize; i++) addMeta("Name " + i, n.get(i));
    for (int i=0; i<fsize; i++) addMeta("Factor " + i, f.get(i));

    // parse factor values
    factors = new double[getImageCount()];
    int max = 0;
    for (int i=0; i<fsize; i++) {
      String factor = (String) f.get(i);
      double q = 1;
      try {
        q = Double.parseDouble(factor);
      }
      catch (NumberFormatException exc) {
        if (debug) {
          LogTools.println("Warning: invalid factor #" + i + ": " + factor);
        }
      }
      factors[i] = q;
      if (q > factors[max]) max = i;
    }
    Arrays.fill(factors, fsize, factors.length, 1);

    // determine pixel type
    if (factors[max] > 256) core[0].pixelType = FormatTools.UINT32;
    else if (factors[max] > 1) core[0].pixelType = FormatTools.UINT16;
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  public class FlexHandler extends DefaultHandler {
    private Vector names, factors;
    private MetadataStore store;

    private int nextLightSource = 0;
    private int nextLaser = -1;

    private int nextArrayImage = 0;
    private int nextSlider = 0;
    private int nextFilter = 0;
    private int nextCamera = 0;
    private int nextObjective = -1;
    private int nextSublayout = 0;
    private int nextField = 0;
    private int nextStack = 0;
    private int nextPlane = 0;
    private int nextKinetic = 0;
    private int nextDispensing = 0;
    private int nextImage = 0;
    private int nextLightSourceCombination = 0;
    private int nextLightSourceRef = 0;
    private int nextPlate = 0;
    private int nextWell = 0;
    private int nextSliderRef = 0;
    private int nextFilterCombination = 0;

    private String parentQName;
    private String currentQName;

    public FlexHandler(Vector names, Vector factors, MetadataStore store) {
      this.names = names;
      this.factors = factors;
      this.store = store;
    }

    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      if (currentQName.equals("PlateName")) {
        store.setPlateName(value, nextPlate - 1);
        addMeta("Plate " + (nextPlate - 1) + " Name", value);
      }
      else if (parentQName.equals("Plate")) {
        addMeta("Plate " + (nextPlate - 1) + " " + currentQName, value);
      }
      else if (parentQName.equals("WellShape")) {
        addMeta("Plate " + (nextPlate - 1) + " WellShape " + currentQName,
          value);
      }
      else if (currentQName.equals("Wavelength")) {
        store.setLaserWavelength(new Integer(value), 0, nextLaser);
        addMeta("Laser " + nextLaser + " Wavelength", value);
      }
      else if (currentQName.equals("Magnification")) {
        store.setObjectiveCalibratedMagnification(new Float(value), 0,
          nextObjective);
      }
      else if (currentQName.equals("NumAperture")) {
        store.setObjectiveLensNA(new Float(value), 0, nextObjective);
      }
      else if (currentQName.equals("Immersion")) {
        store.setObjectiveImmersion(value, 0, nextObjective);
      }
      else if (currentQName.equals("OffsetX") || currentQName.equals("OffsetY"))
      {
        addMeta("Sublayout " + (nextSublayout - 1) + " Field " +
          (nextField - 1) + " " + currentQName, value);
      }
      else if (currentQName.equals("OffsetZ")) {
        addMeta("Stack " + (nextStack - 1) + " Plane " + (nextPlane - 1) +
          " OffsetZ", value);
      }
      else if (currentQName.equals("Power")) {
        addMeta("LightSourceCombination " + (nextLightSourceCombination - 1) +
          " LightSourceRef " + (nextLightSourceRef - 1) + " Power", value);
      }
      else if (parentQName.equals("Image")) {
        addMeta("Image " + (nextImage - 1) + " " + currentQName, value);

        if (currentQName.equals("DateTime")) {
          store.setImageCreationDate(value, nextImage - 1);
        }
        else if (currentQName.equals("ImageResolutionX")) {
          store.setDimensionsPhysicalSizeX(new Float(value), nextImage - 1, 0);
        }
        else if (currentQName.equals("ImageResolutionY")) {
          store.setDimensionsPhysicalSizeY(new Float(value), nextImage - 1, 0);
        }
        else if (currentQName.equals("CameraBinningX")) {
          binX = Integer.parseInt(value);
        }
        else if (currentQName.equals("CameraBinningY")) {
          binY = Integer.parseInt(value);
        }
      }
      else if (parentQName.equals("Well")) {
        addMeta("Well " + (nextWell - 1) + " " + currentQName, value);
      }
    }

    public void startElement(String uri,
      String localName, String qName, Attributes attributes)
    {
      currentQName = qName;
      if (qName.equals("Array")) {
        int len = attributes.getLength();
        for (int i=0; i<len; i++) {
          String name = attributes.getQName(i);
          if (name.equals("Name")) {
            names.add(attributes.getValue(i));
            store.setImageName(attributes.getValue(i), nextArrayImage);
          }
          else if (name.equals("Factor")) factors.add(attributes.getValue(i));
          else if (name.equals("Description")) {
            store.setImageDescription(attributes.getValue(i), nextArrayImage++);
          }
        }
      }
      else if (qName.equals("LightSource")) {
        parentQName = qName;
        String id = attributes.getValue("ID");
        String type = attributes.getValue("LightSourceType");
        addMeta("LightSource " + nextLightSource + " ID", id);
        addMeta("LightSource " + nextLightSource + " Type", type);

        if (type.equals("Laser")) nextLaser++;
        nextLightSource++;
      }
      else if (qName.equals("Slider")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Slider " + nextSlider + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextSlider++;
      }
      else if (qName.equals("Filter")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Filter " + nextFilter + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextFilter++;
      }
      else if (qName.equals("Camera")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Camera " + nextCamera + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextCamera++;
      }
      else if (qName.startsWith("PixelSize") && parentQName.equals("Camera"))
      {
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Camera " + (nextCamera - 1) + " " + qName + " " +
            attributes.getQName(i), attributes.getValue(i));
        }
      }
      else if (qName.equals("Objective")) {
        parentQName = qName;
        nextObjective++;
      }
      else if (qName.equals("Sublayout")) {
        parentQName = qName;
        nextField = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Sublayout " + nextSublayout + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextSublayout++;
      }
      else if (qName.equals("Field")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Sublayout " + (nextSublayout - 1) + " Field " + nextField +
            " " + attributes.getQName(i), attributes.getValue(i));
        }
        nextField++;
      }
      else if (qName.equals("Stack")) {
        nextPlane = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Stack " + nextStack + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextStack++;
      }
      else if (qName.equals("Plane")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Stack " + (nextStack - 1) + " Plane " + nextPlane +
            " " + attributes.getQName(i), attributes.getValue(i));
        }
        nextPlane++;
      }
      else if (qName.equals("Kinetic")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Kinetic " + nextKinetic + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextKinetic++;
      }
      else if (qName.equals("Dispensing")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Dispensing " + nextDispensing + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextDispensing++;
      }
      else if (qName.equals("LightSourceCombination")) {
        nextLightSourceRef = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("LightSourceCombination " + nextLightSourceCombination +
            " " + attributes.getQName(i), attributes.getValue(i));
        }
        nextLightSourceCombination++;
      }
      else if (qName.equals("LightSourceRef")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("LightSourceCombination " + (nextLightSourceCombination - 1) +
            " LightSourceRef " + nextLightSourceRef + " " +
            attributes.getQName(i), attributes.getValue(i));
        }
        nextLightSourceRef++;
      }
      else if (qName.equals("FilterCombination")) {
        parentQName = qName;
        nextSliderRef = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("FilterCombination " + nextFilterCombination + " " +
            attributes.getQName(i), attributes.getValue(i));
        }
        nextFilterCombination++;
      }
      else if (qName.equals("SliderRef")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("FilterCombination " + (nextFilterCombination - 1) +
            " SliderRef " + nextSliderRef + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextSliderRef++;
      }
      else if (qName.equals("Image")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addMeta("Image " + nextImage + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextImage++;

        String x = attributes.getValue("CameraBinningX");
        String y = attributes.getValue("CameraBinningY");
        if (x != null) binX = Integer.parseInt(x);
        if (y != null) binY = Integer.parseInt(y);
      }
      else if (qName.equals("Plate") || qName.equals("WellShape") ||
        qName.equals("Well"))
      {
        parentQName = qName;
        if (qName.equals("Plate")) nextPlate++;
        else if (qName.equals("Well")) nextWell++;
      }
      else if (qName.equals("WellCoordinate")) {
        int ndx = nextWell - 1;
        addMeta("Well" + ndx + " Row", attributes.getValue("Row"));
        addMeta("Well" + ndx + " Col", attributes.getValue("Col"));
        store.setWellRow(new Integer(attributes.getValue("Row")), 0,ndx);
        store.setWellColumn(new Integer(attributes.getValue("Col")), 0, ndx);
      }
      else if (qName.equals("Status")) {
        addMeta("Status", attributes.getValue("StatusString"));
      }
    }
  }

}
