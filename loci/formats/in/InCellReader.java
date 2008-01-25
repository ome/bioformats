//
// InCellReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

package loci.formats.in;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * InCellReader is the file format reader for InCell 1000 datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/InCellReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/InCellReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class InCellReader extends FormatReader {

  // -- Constants --

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Fields --

  private Vector tiffs;
  private TiffReader[] tiffReaders;

  // -- Constructor --

  /** Constructs a new InCell 1000 reader. */
  public InCellReader() { super("InCell 1000", "xdce"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[0].get8BitLookupTable();
  }

  /* @see loci.formats.IForamtReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[0].get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);
    return tiffReaders[no].openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    Vector v = new Vector();
    v.add(currentId);
    for (int i=0; i<tiffs.size(); i++) {
      v.add(tiffs.get(i));
    }
    return (String[]) v.toArray(new String[0]);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    tiffs = null;
    if (tiffReaders != null) {
      for (int i=0; i<tiffReaders.length; i++) {
        tiffReaders[i].close();
      }
      tiffReaders = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("InCellReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    tiffs = new Vector();

    InCellHandler handler = new InCellHandler();
    byte[] b = new byte[(int) in.length()];
    in.read(b);

    try {
      SAXParser parser = SAX_FACTORY.newSAXParser();
      parser.parse(new ByteArrayInputStream(b), handler);
    }
    catch (ParserConfigurationException exc) {
      throw new FormatException(exc);
    }
    catch (SAXException exc) {
      throw new FormatException(exc);
    }

    tiffReaders = new TiffReader[tiffs.size()];
    for (int i=0; i<tiffs.size(); i++) {
      tiffReaders[i] = new TiffReader();
      tiffReaders[i].setId((String) tiffs.get(i));
    }

    core.sizeX[0] = tiffReaders[0].getSizeX();
    core.sizeY[0] = tiffReaders[0].getSizeY();
    core.imageCount[0] = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
    core.interleaved[0] = tiffReaders[0].isInterleaved();
    core.indexed[0] = tiffReaders[0].isIndexed();
    core.rgb[0] = tiffReaders[0].isRGB();
    core.currentOrder[0] = "XYZCT";
    core.pixelType[0] = tiffReaders[0].getPixelType();
    core.littleEndian[0] = tiffReaders[0].isLittleEndian();

    MetadataStore store = getMetadataStore();
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    MetadataTools.populatePixels(store, this);
    // CTR CHECK
//    for (int c=0; c<core.sizeC[0]; c++) {
//      store.setLogicalChannel(c, null, null, null, null, null, null, null, null,
//        null, null, null, null, null, null, null, null, null, null, null, null,
//        null, null, null, null);
//    }
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class InCellHandler extends DefaultHandler {
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      for (int i=0; i<attributes.getLength(); i++) {
        addMeta(qName + " - " + attributes.getQName(i), attributes.getValue(i));
      }

      if (qName.equals("Image")) {
        String file = attributes.getValue("filename");
        Location parent =
          new Location(currentId).getAbsoluteFile().getParentFile();
        tiffs.add(new Location(parent, file).getAbsolutePath());
      }
      else if (qName.equals("Identifier")) {
        core.sizeZ[0] = Integer.parseInt(attributes.getValue("z_index")) + 1;
        core.sizeC[0] = Integer.parseInt(attributes.getValue("wave_index")) + 1;
        core.sizeT[0] = Integer.parseInt(attributes.getValue("time_index")) + 1;
      }
    }
  }

}
