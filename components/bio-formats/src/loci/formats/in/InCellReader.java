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

import java.awt.Point;
import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * InCellReader is the file format reader for InCell 1000 datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/InCellReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/InCellReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class InCellReader extends FormatReader {

  // -- Fields --

  private Vector tiffs;
  private MinimalTiffReader tiffReader;
  private int seriesCount;
  private Vector emWaves, exWaves;
  private Vector timings;
  private int totalImages;
  private String creationDate;
  private int startRow, startCol;
  private int fieldCount, zCount;

  private int wellRows, wellCols;
  private Vector wellCoordinates;

  // -- Constructor --

  /** Constructs a new InCell 1000 reader. */
  public InCellReader() { super("InCell 1000", "xdce"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader.get8BitLookupTable();
  }

  /* @see loci.formats.IForamtReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader.get16BitLookupTable();
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
    tiffReader.setId((String) tiffs.get(series * getImageCount() + no));
    return tiffReader.openBytes(0, buf, x, y, w, h);
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
    if (tiffReader != null) tiffReader.close();
    tiffReader = null;
    seriesCount = 0;
    totalImages = 0;

    emWaves = exWaves = null;
    wellCoordinates = null;
    timings = null;
    creationDate = null;
    wellRows = wellCols = 0;
    startRow = startCol = 0;
    fieldCount = zCount = 0;
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

    wellCoordinates = new Vector();

    byte[] b = new byte[(int) in.length()];
    in.read(b);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    DefaultHandler handler = new InCellHandler(store);
    DataTools.parseXML(b, handler);

    core[0].sizeZ = fieldCount * zCount;

    seriesCount = totalImages / (getSizeZ() * getSizeC() * getSizeT());

    int z = getSizeZ();
    int c = getSizeC();
    int t = getSizeT();

    core = new CoreMetadata[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      core[i] = new CoreMetadata();
      core[i].sizeZ = z;
      core[i].sizeC = c;
      core[i].sizeT = t;
      core[i].imageCount = z * c * t;
      core[i].dimensionOrder = "XYZCT";
    }

    tiffReader = new MinimalTiffReader();

    int nextTiming = 0;
    for (int i=0; i<seriesCount; i++) {
      tiffReader.setId((String) tiffs.get(i * (tiffs.size() / seriesCount)));
      core[i].sizeX = tiffReader.getSizeX();
      core[i].sizeY = tiffReader.getSizeY();
      core[i].interleaved = tiffReader.isInterleaved();
      core[i].indexed = tiffReader.isIndexed();
      core[i].rgb = tiffReader.isRGB();
      core[i].pixelType = tiffReader.getPixelType();
      core[i].littleEndian = tiffReader.isLittleEndian();
      store.setImageName("", i);
      store.setImageCreationDate(creationDate, i);
      for (int q=0; q<core[i].imageCount; q++) {
        store.setPlaneTimingDeltaT((Float) timings.get(nextTiming++), i, 0, q);
        int[] coords = FormatTools.getZCTCoords("XYZCT", z, c, t, z * c * t, q);
        store.setPlaneTheZ(new Integer(coords[0]), i, 0, q);
        store.setPlaneTheC(new Integer(coords[1]), i, 0, q);
        store.setPlaneTheT(new Integer(coords[2]), i, 0, q);
        store.setPlaneTimingExposureTime(new Float(0), i, 0, q);
      }

      int row = (int) ((Point) wellCoordinates.get(i)).x - startRow;
      int col = (int) ((Point) wellCoordinates.get(i)).y - startCol;
      store.setWellSampleIndex(new Integer(i), 0, row*wellCols + col, 0);
    }

    MetadataTools.populatePixels(store, this);

    for (int i=0; i<seriesCount; i++) {
      for (int q=0; q<emWaves.size(); q++) {
        store.setLogicalChannelEmWave((Integer) emWaves.get(q), i, q);
        store.setLogicalChannelExWave((Integer) exWaves.get(q), i, q);
      }
    }
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class InCellHandler extends DefaultHandler {
    private String currentQName;
    private int nextEmWave = 0;
    private int nextExWave = 0;
    private MetadataStore store;
    private int nextPlate = 0;
    private int currentRow = -1, currentCol = -1;

    public InCellHandler(MetadataStore store) {
      this.store = store;
    }

    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      if (currentQName.equals("UserComment")) {
        store.setImageDescription(value, 0);
      }
    }

    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("Image")) {
        Point p = new Point(currentRow, currentCol);
        if (!wellCoordinates.contains(p)) wellCoordinates.add(p);
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
        int field = Integer.parseInt(attributes.getValue("field_index")) + 1;
        int z = Integer.parseInt(attributes.getValue("z_index")) + 1;
        int c = Integer.parseInt(attributes.getValue("wave_index")) + 1;
        int t = Integer.parseInt(attributes.getValue("time_index")) + 1;
        fieldCount = (int) Math.max(fieldCount, field);
        zCount = (int) Math.max(zCount, z);
        core[0].sizeC = (int) Math.max(getSizeC(), c);
        core[0].sizeT = (int) Math.max(getSizeT(), t);
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
        store.setPlateName(new Location(getCurrentFile()).getName(), nextPlate);
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
        currentRow = Integer.parseInt(attributes.getValue("number"));
      }
      else if (qName.equals("Column")) {
        currentCol = Integer.parseInt(attributes.getValue("number"));
      }
      else if (qName.equals("NamingRows")) {
        String row = attributes.getValue("begin");
        try {
          startRow = Integer.parseInt(row);
        }
        catch (NumberFormatException e) {
          startRow = row.charAt(0) - 'A' + 1;
        }
      }
      else if (qName.equals("NamingColumns")) {
        String col = attributes.getValue("begin");
        try {
          startCol = Integer.parseInt(col);
        }
        catch (NumberFormatException e) {
          startCol = col.charAt(0) - 'A' + 1;
        }
      }
    }
  }

}
