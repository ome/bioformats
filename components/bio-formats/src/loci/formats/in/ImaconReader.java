//
// ImaconReader.java
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
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ImaconReader is the file format reader for Imacon .fff (TIFF) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ImaconReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ImaconReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ImaconReader extends BaseTiffReader {

  // -- Constants --

  private static final int CREATOR_TAG = 34377;
  private static final int XML_TAG = 50457;

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

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      experimenterName = null;
      creationDate = null;
      imageName = null;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    ifds = tiffParser.getIFDs(false);

    core = new CoreMetadata[ifds.size()];
    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();
      core[i].imageCount = 1;
      IFD ifd = ifds.get(i);

      int photo = ifd.getPhotometricInterpretation();
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
      core[i].interleaved = ifd.getPlanarConfiguration() == 2;
    }

    IFD firstIFD = ifds.get(0);
    String xml = firstIFD.getIFDTextValue(XML_TAG).trim();
    xml = xml.substring(xml.indexOf("<"));

    XMLTools.parseXML(xml, new ImaconHandler());

    String[] creationInfo = firstIFD.getIFDTextValue(CREATOR_TAG).split("\n");

    int lineNumber = 0;
    for (String s : creationInfo) {
      if (s.trim().length() == 0) continue;
      switch (lineNumber) {
        case 4:
          // creator's name
          experimenterName = s.trim();
          break;
        case 6:
          // image name
          imageName = s.trim();
          break;
        case 8:
          // creation date
          creationDate = s.trim();
          break;
        case 10:
          // creation time
          creationDate += " " + s.trim();
          break;
      }

      lineNumber++;
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

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

    if (creationDate != null) {
      creationDate = DateTools.formatDate(creationDate, "yyyyMMdd HHmmSSZ");
    }

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageExperimenterRef(experimenter, i);
      store.setImageName(imageName + " #" + (i + 1), i);
      if (creationDate != null) {
        store.setImageCreationDate(creationDate, i);
      }
      else MetadataTools.setDefaultCreationDate(store, currentId, i);
    }
  }

  // -- Helper class --

  class ImaconHandler extends DefaultHandler {
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
