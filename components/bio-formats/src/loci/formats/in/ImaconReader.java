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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.Timestamp;

import org.xml.sax.Attributes;

/**
 * ImaconReader is the file format reader for Imacon .fff (TIFF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ImaconReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ImaconReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ImaconReader extends BaseTiffReader {

  // -- Constants --

  private static final int CREATOR_TAG = 34377;
  private static final int XML_TAG = 50457;
  private static final int PIXELS_TAG = 46275;

  // -- Fields --

  private String experimenterName;
  private String creationDate;
  private String imageName;

  // -- Constructor --

  /** Constructs a new Imacon reader. */
  public ImaconReader() {
    super("Imacon", new String[] {"fff"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    parser.setDoCaching(false);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(XML_TAG);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      experimenterName = null;
      creationDate = null;
      imageName = null;
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    tiffParser.getSamples(ifds.get(getSeries()), buf, x, y, w, h);
    return buf;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    ifds = tiffParser.getIFDs();

    core = new CoreMetadata[ifds.size()];
    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();
      core[i].imageCount = 1;
      IFD ifd = ifds.get(i);
      ifd.remove(PIXELS_TAG);
      tiffParser.fillInIFD(ifd);

      PhotoInterp photo = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      core[i].rgb = samples > 1 || photo == PhotoInterp.RGB ||
        photo == PhotoInterp.CFA_ARRAY;
      if (photo == PhotoInterp.CFA_ARRAY) samples = 3;

      core[i].sizeX = (int) ifd.getImageWidth();
      core[i].sizeY = (int) ifd.getImageLength();
      core[i].sizeZ = 1;
      core[i].sizeC = isRGB() ? samples : 1;
      core[i].sizeT = 1;
      core[i].pixelType = ifd.getPixelType();
      core[i].indexed = photo == PhotoInterp.RGB_PALETTE;
      core[i].dimensionOrder = "XYCZT";
      core[i].interleaved = false;
    }

    IFD firstIFD = ifds.get(0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      String xml = firstIFD.getIFDTextValue(XML_TAG).trim();
      xml = xml.substring(xml.indexOf("<"));
      XMLTools.parseXML(xml, new ImaconHandler());
    }

    String[] creationInfo = firstIFD.getIFDTextValue(CREATOR_TAG).split("\n");

    if (creationInfo.length > 4) {
      experimenterName = creationInfo[4].trim();
    }
    if (creationInfo.length > 6) {
      imageName = creationInfo[6].trim();
    }
    if (creationInfo.length > 8) {
      creationDate = creationInfo[8].trim();
    }
    if (creationInfo.length > 10) {
      creationDate += " " + creationInfo[10].trim();
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    if (creationDate != null) {
      creationDate = DateTools.formatDate(creationDate, "yyyyMMdd HHmmSSZ");
    }

    for (int i=0; i<getSeriesCount(); i++) {
      String name = imageName;
      if (imageName.length() == 0) {
        name = "#" + (i + 1);
      }
      else {
        name += " #" + (i + 1);
      }
      store.setImageName(name, i);
      if (creationDate != null) {
        store.setImageAcquisitionDate(new Timestamp(creationDate), i);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (experimenterName == null) experimenterName = "";

      int nameSpace = experimenterName.indexOf(" ");
      String firstName =
        nameSpace == -1 ? "" : experimenterName.substring(0, nameSpace);
      String lastName = nameSpace == -1 ? experimenterName :
        experimenterName.substring(nameSpace + 1);

      String experimenter = MetadataTools.createLSID("Experimenter", 0);

      store.setExperimenterID(experimenter, 0);
      store.setExperimenterFirstName(firstName, 0);
      store.setExperimenterLastName(lastName, 0);

      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageExperimenterRef(experimenter, i);
      }
    }
  }

  // -- Helper class --

  class ImaconHandler extends BaseHandler {
    private String key, value;
    private String qName;

    // -- DefaultHandler API methods --

    public void characters(char[] data, int start, int len) {
      if (qName.equals("key")) {
        key = new String(data, start, len);
        value = null;
      }
      else value = new String(data, start, len);

      if (key != null && value != null) {
        addGlobalMeta(key, value);
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      this.qName = qName;
    }
  }

}
