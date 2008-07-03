//
// InCellReader.java
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

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
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
  private TiffReader[][] tiffReaders;
  private int seriesCount;
  private Vector emWaves, exWaves;
  private Vector timings;
  private int totalImages;
  private String creationDate;

  private int wellRows, wellCols;
  private Vector rows, cols;

  // -- Constructor --

  /** Constructs a new InCell 1000 reader. */
  public InCellReader() { super("InCell 1000", "xdce"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[series][0].get8BitLookupTable();
  }

  /* @see loci.formats.IForamtReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReaders[series][0].get16BitLookupTable();
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
    tiffReaders[series][no].setId(
      (String) tiffs.get(series * tiffReaders[0].length + no));
    return tiffReaders[series][no].openBytes(0, buf, x, y, w, h);
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
        for (int j=0; j<tiffReaders[i].length; j++) {
          tiffReaders[i][j].close();
        }
      }
      tiffReaders = null;
    }
    seriesCount = 0;
    totalImages = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("InCellReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    tiffs = new Vector();
    emWaves = new Vector();
    exWaves = new Vector();
    timings = new Vector();

    rows = new Vector();
    cols = new Vector();

    byte[] b = new byte[(int) in.length()];
    in.read(b);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    InCellHandler handler = new InCellHandler(store);

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

    seriesCount = totalImages / (core.sizeZ[0] * core.sizeC[0] * core.sizeT[0]);
    tiffReaders = new TiffReader[seriesCount][tiffs.size() / seriesCount];
    for (int i=0; i<tiffReaders.length; i++) {
      for (int j=0; j<tiffReaders[0].length; j++) {
        tiffReaders[i][j] = new TiffReader();
      }
      tiffReaders[i][0].setId((String) tiffs.get(i * tiffReaders[0].length));
    }

    int z = core.sizeZ[0];
    int c = core.sizeC[0];
    int t = core.sizeT[0];

    core = new CoreMetadata(seriesCount);

    Arrays.fill(core.sizeZ, z);
    Arrays.fill(core.sizeC, c);
    Arrays.fill(core.sizeT, t);
    Arrays.fill(core.imageCount, z * c * t);
    Arrays.fill(core.currentOrder, "XYZCT");

    int nextTiming = 0;
    for (int i=0; i<seriesCount; i++) {
      core.sizeX[i] = tiffReaders[i][0].getSizeX();
      core.sizeY[i] = tiffReaders[i][0].getSizeY();
      core.interleaved[i] = tiffReaders[i][0].isInterleaved();
      core.indexed[i] = tiffReaders[i][0].isIndexed();
      core.rgb[i] = tiffReaders[i][0].isRGB();
      core.pixelType[i] = tiffReaders[i][0].getPixelType();
      core.littleEndian[i] = tiffReaders[i][0].isLittleEndian();
      store.setImageName("", i);
      store.setImageCreationDate(creationDate, i);
      for (int q=0; q<emWaves.size(); q++) {
        store.setLogicalChannelEmWave((Integer) emWaves.get(q), i, q);
        store.setLogicalChannelExWave((Integer) exWaves.get(q), i, q);
      }
      for (int q=0; q<core.imageCount[i]; q++) {
        store.setPlaneTimingDeltaT((Float) timings.get(nextTiming++), i, 0, q);
        int[] coords = FormatTools.getZCTCoords("XYZCT", z, c, t, z * c * t, q);
        store.setPlaneTheZ(new Integer(coords[0]), i, 0, q);
        store.setPlaneTheC(new Integer(coords[1]), i, 0, q);
        store.setPlaneTheT(new Integer(coords[2]), i, 0, q);
        store.setPlaneTimingExposureTime(new Float(0), i, 0, q);
      }
      store.setWellSampleIndex(new Integer(i), 0, i, 0);
    }

    MetadataTools.populatePixels(store, this);
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class InCellHandler extends DefaultHandler {
    private String currentQName;
    private int nextEmWave = 0;
    private int nextExWave = 0;
    private MetadataStore store;
    private int nextPlate = 0;

    public InCellHandler(MetadataStore store) {
      this.store = store;
    }

    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      if (currentQName.equals("UserComment")) {
        store.setImageDescription(value, 0);
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentQName = qName;
      for (int i=0; i<attributes.getLength(); i++) {
        addMeta(qName + " - " + attributes.getQName(i), attributes.getValue(i));
      }

      if (qName.equals("Images")) {
        totalImages = Integer.parseInt(attributes.getValue("number"));
      }
      else if (qName.equals("Image")) {
        String file = attributes.getValue("filename");
        String thumb = attributes.getValue("thumbnail");
        Location current = new Location(currentId).getAbsoluteFile();

        if (new Location(current.getParentFile(), file).exists()) {
          tiffs.add(
            new Location(current.getParentFile(), file).getAbsolutePath());
        }
        else tiffs.add(file);
        timings.add(new Float(attributes.getValue("acquisition_time_ms")));
      }
      else if (qName.equals("Identifier")) {
        int z = Integer.parseInt(attributes.getValue("z_index")) + 1;
        int c = Integer.parseInt(attributes.getValue("wave_index")) + 1;
        int t = Integer.parseInt(attributes.getValue("time_index")) + 1;
        core.sizeZ[0] = (int) Math.max(core.sizeZ[0], z);
        core.sizeC[0] = (int) Math.max(core.sizeC[0], c);
        core.sizeT[0] = (int) Math.max(core.sizeT[0], t);
      }
      else if (qName.equals("Creation")) {
        String date = attributes.getValue("date"); // yyyy-mm-dd
        String time = attributes.getValue("time"); // hh:mm:ss
        creationDate = date + "T" + time;
      }
      else if (qName.equals("ObjectiveCalibration")) {
        store.setObjectiveCalibratedMagnification(
          new Float(attributes.getValue("magnification")), 0, 0);
        store.setObjectiveModel(attributes.getValue("objective_name"), 0, 0);
        store.setObjectiveLensNA(new Float(
          attributes.getValue("numerical_aperture")), 0, 0);
      }
      else if (qName.equals("ExcitationFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) exWaves.add(new Integer(wave));
      }
      else if (qName.equals("EmissionFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) emWaves.add(new Integer(wave));
      }
      else if (qName.equals("Plate")) {
        store.setPlateName(attributes.getValue("name"), nextPlate);
        wellRows = Integer.parseInt(attributes.getValue("rows"));
        wellCols = Integer.parseInt(attributes.getValue("columns"));

        for (int r=0; r<wellRows; r++) {
          for (int c=0; c<wellCols; c++) {
            store.setWellRow(new Integer(r), nextPlate, r*wellCols + c);
            store.setWellColumn(new Integer(c), nextPlate, r*wellCols + c);
          }
        }
        nextPlate++;
      }
      else if (qName.equals("Row")) {
        rows.add(new Integer(attributes.getValue("number")));
      }
      else if (qName.equals("Column")) {
        cols.add(new Integer(attributes.getValue("number")));
      }
    }
  }

}
