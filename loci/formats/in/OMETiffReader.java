//
// OMETiffReader.java
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
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * OMETiffReader is the file format reader for
 * <a href="http://www.loci.wisc.edu/ome/ome-tiff-spec.html">OME-TIFF</a>
 * files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/OMETiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/OMETiffReader.java">SVN</a></dd></dl>
 */
public class OMETiffReader extends BaseTiffReader {

  // -- Constants --

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Fields --

  /** List of used files. */
  private String[] usedFiles;

  /** List of Image IDs. */
  private Vector imageIDs;

  /** List of Pixels IDs. */
  private Vector pixelsIDs;

  /** List of TiffData UUIDs. */
  private Vector planeUUIDs;

  /** List of file UUIDs. */
  private Vector fileUUIDs;

  private Vector tempIfdMap, tempFileMap, tempIfdCount;
  private int currentFile, currentSeries, seriesCount;
  private int[] numIFDs;
  private int[][] ifdMap, fileMap;
  private boolean uuids, lsids, isWiscScan;
  private Hashtable[][] usedIFDs;
  private Hashtable keyMetadata, temporaryKeyMetadata;

  // -- Constructor --

  public OMETiffReader() {
    super("OME-TIFF", new String[] {"tif", "tiff"});
    blockCheckLen = 1048576;
    suffixSufficient = false;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    keyMetadata = new Hashtable();
    temporaryKeyMetadata = new Hashtable();

    OMETiffHandler handler = new OMETiffHandler(false);
    String comment = TiffTools.getComment(ifds[0]);

    // remove invalid characters from comment

    for (int i=0; i<comment.length(); i++) {
      char c = comment.charAt(i);
      if (Character.isISOControl(c) || !Character.isDefined(c)) {
        comment = comment.replace(c, ' ');
      }
    }

    currentSeries = -1;
    seriesCount = 0;
    imageIDs = null;
    pixelsIDs = null;
    planeUUIDs = null;
    fileUUIDs = null;
    lsids = true;

    tempIfdMap = new Vector();
    tempFileMap = new Vector();
    tempIfdCount = new Vector();

    try {
      SAXParser parser = SAX_FACTORY.newSAXParser();
      parser.parse(new ByteArrayInputStream(comment.getBytes()), handler);
    }
    catch (ParserConfigurationException exc) {
      throw new FormatException(exc);
    }
    catch (SAXException exc) {
      throw new FormatException(exc);
    }

    // MAJOR HACK : check for OME-XML in the comment of the second IFD
    // There is a version of WiscScan which writes OME-XML to every IFD,
    // but with SizeZ and SizeT equal to 1.

    String s = null;
    if (ifds.length > 1) {
      s = TiffTools.getComment(ifds[1]);
    }
    isWiscScan = s != null && s.indexOf("ome.xsd") != -1;

    // look for additional files in the dataset
    Vector files = new Vector();
    Location l = new Location(currentId).getAbsoluteFile().getParentFile();
    String[] fileList = new File(currentId).exists() ? l.list() :
      (String[]) Location.getIdMap().keySet().toArray(new String[0]);

    int oldSeriesCount = seriesCount;

    for (int i=0; i<fileList.length; i++) {
      String check = fileList[i].toLowerCase();
      if (checkSuffix(fileList[i], TiffReader.TIFF_SUFFIXES)) {
        status("Checking " + fileList[i]);
        Location file = new Location(fileList[i]);
        if (!file.exists()) file = new Location(l, fileList[i]);
        String iid = file.getAbsolutePath();
        String icomment = TiffTools.getComment(iid);
        if (icomment == null || icomment.trim().length() == 0) continue;
        boolean addToList = true;
        if (uuids) {
          currentSeries = -1;
          seriesCount = 0;
          handler = new OMETiffHandler(true);
          try {
            SAXParser parser = SAX_FACTORY.newSAXParser();
            parser.parse(
              new ByteArrayInputStream(icomment.getBytes()), handler);
          }
          catch (ParserConfigurationException exc) {
            throw new FormatException(exc);
          }
          catch (SAXException exc) {
            throw new FormatException(exc);
          }
          addToList = planeUUIDs.contains(fileUUIDs.get(fileUUIDs.size() - 1));
        }
        // the rest of this logic is a hack for obsolete OME-TIFF files
        else if (lsids && imageIDs != null) {
          for (int k=0; k<imageIDs.size(); k++) {
            if (icomment.indexOf((String) imageIDs.get(k)) == -1) {
              addToList = false;
              break;
            }
          }
          if (addToList) {
            for (int k=0; k<pixelsIDs.size(); k++) {
              if (icomment.indexOf((String) pixelsIDs.get(k)) == -1) {
                addToList = false;
                break;
              }
            }
          }
        }
        else {
          // check key pieces of metadata for consistency
          currentSeries = -1;
          seriesCount = 0;
          handler = new OMETiffHandler(true);
          try {
            SAXParser parser = SAX_FACTORY.newSAXParser();
            parser.parse(
              new ByteArrayInputStream(icomment.getBytes()), handler);
          }
          catch (ParserConfigurationException exc) {
            throw new FormatException(exc);
          }
          catch (SAXException exc) {
            throw new FormatException(exc);
          }

          Enumeration keys = keyMetadata.keys();
          while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            if (!keyMetadata.get(key).equals(temporaryKeyMetadata.get(key))) {
              addToList = false;
              break;
            }
          }
          temporaryKeyMetadata.clear();
        }

        if (addToList || iid.endsWith(File.separator + currentId)) {
          files.add(iid);
        }
      }
    }

    if (!files.contains(currentId)) files.add(currentId);

    // parse grouped files

    seriesCount = oldSeriesCount;

    ifdMap = new int[seriesCount][];
    fileMap = new int[seriesCount][];
    numIFDs = new int[seriesCount];

    for (int i=0; i<seriesCount; i++) {
      int ii = ((Integer) tempIfdCount.get(i)).intValue();
      ifdMap[i] = new int[ii];
      fileMap[i] = new int[ii];
    }

    // copy temp IFD/file maps

    for (int i=0; i<tempIfdMap.size(); i++) {
      Vector v = (Vector) tempIfdMap.get(i);
      int ifdCount = ((Integer) tempIfdCount.get(i)).intValue();
      for (int j=0; j<ifdCount; j++) {
        ifdMap[i][j] = ((Integer) v.get(j)).intValue();
      }
      numIFDs[i] = ifdCount;
    }

    for (int i=0; i<tempFileMap.size(); i++) {
      Vector v = (Vector) tempFileMap.get(i);
      int ifdCount = ((Integer) tempIfdCount.get(i)).intValue();
      for (int j=0; j<ifdCount; j++) {
        fileMap[i][j] = ((Integer) v.get(j)).intValue();
      }
    }

    usedFiles = (String[]) files.toArray(new String[0]);
    usedIFDs = new Hashtable[usedFiles.length][];

    for (int i=0; i<usedFiles.length; i++) {
      if (usedFiles[i].endsWith(currentId)) {
        usedIFDs[i] = ifds;
        continue;
      }
      status("Parsing " + usedFiles[i]);
      currentSeries = -1;
      tempIfdMap = null;
      tempFileMap = null;
      tempIfdCount = null;
      currentFile = i;

      RandomAccessStream ras = new RandomAccessStream(usedFiles[i]);
      usedIFDs[i] = TiffTools.getIFDs(ras);
      ras.close();
      String c = TiffTools.getComment(usedIFDs[i][0]);
      handler = new OMETiffHandler(false);
      try {
        SAXParser parser = SAX_FACTORY.newSAXParser();
        parser.parse(new ByteArrayInputStream(c.getBytes()), handler);
      }
      catch (ParserConfigurationException exc) {
        throw new FormatException(exc);
      }
      catch (SAXException exc) {
        throw new FormatException(exc);
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      if (numIFDs != null && lsids) {
        if (numIFDs[i] < core.imageCount[i]) {
          LogTools.println("Too few IFDs; got " + numIFDs[i] +
            ", expected " + core.imageCount[i]);
        }
        else if (numIFDs[i] > core.imageCount[i]) {
          LogTools.println("Too many IFDs; got " + numIFDs[i] +
            ", expected " + core.imageCount[i]);
        }
      }
      else if (core.imageCount[i] > ifdMap[i].length) {
        core.imageCount[i] = ifds.length;
        if (core.sizeZ[i] > ifds.length) {
          core.sizeZ[i] = ifds.length / (core.rgb[i] ? core.sizeC[i] : 1);
          core.sizeT[i] = 1;
          if (!core.rgb[i]) core.sizeC[i] = 1;
        }
        else if (core.sizeT[i] > ifds.length) {
          core.sizeT[i] = ifds.length / (core.rgb[i] ? core.sizeC[i] : 1);
          core.sizeZ[i] = 1;
          if (!core.rgb[i]) core.sizeC[i] = 1;
        }
      }
      else if (core.imageCount[i] !=
        core.sizeZ[i] * core.sizeC[i] * core.sizeT[i])
      {
        if (!core.rgb[i]) {
          core.imageCount[i] = core.sizeZ[i] * core.sizeC[i] * core.sizeT[i];
        }
        else core.imageCount[i] = core.sizeZ[i] * core.sizeT[i];
      }
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() {
    String comment = TiffTools.getComment(ifds[0]);
    metadata.remove("Comment");
    MetadataStore store = getMetadataStore();
    MetadataTools.convertMetadata(comment, store);
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open && !checkSuffix(name, suffixes)) return false;
    try {
      RandomAccessStream s = new RandomAccessStream(name);
      byte[] buf = new byte[blockCheckLen];
      s.seek(0);
      s.read(buf);
      boolean passFirst = isThisType(buf);
      s.seek(s.length() - blockCheckLen);
      s.read(buf);
      boolean passSecond = isThisType(buf);
      s.close();
      return passFirst || passSecond;
    }
    catch (IOException e) {
      if (debug) LogTools.trace(e);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    try {
      RandomAccessStream ras = new RandomAccessStream(block);
      Hashtable ifd = TiffTools.getFirstIFD(ras);
      ras.close();
      if (ifd == null) throw new IOException();

      String comment = TiffTools.getComment(ifd);
      return comment != null && comment.indexOf("ome.xsd") >= 0;
    }
    catch (IOException e) {
      if (debug) LogTools.trace(e);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      if (debug) LogTools.trace(e);
    }
    return new String(block).indexOf("ome.xsd") != -1;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return usedFiles;
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

    int ifd = ifdMap[series][no];
    int fileIndex = fileMap[series][no];

    in = new RandomAccessStream(usedFiles[fileIndex]);
    TiffTools.getSamples(usedIFDs[fileIndex][ifd], in, buf, x, y, w, h);
    in.close();
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    usedFiles = null;
    usedIFDs = null;
    imageIDs = pixelsIDs = planeUUIDs = fileUUIDs = null;
    tempIfdMap = tempFileMap = tempIfdCount = null;
    numIFDs = null;
    ifdMap = fileMap = null;
    lsids = isWiscScan = false;
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  private class OMETiffHandler extends DefaultHandler {
    private String order;
    private int sizeZ, sizeC, sizeT;
    private int imageCount = 0;
    private boolean foundDescription = false, foundDate = false;
    private boolean minimal;

    public OMETiffHandler(boolean minimal) {
      // flag is true if we just want to scan for metadata, without populating
      // anything
      this.minimal = minimal;
    }

    public void characters(char[] ch, int start, int length) {
      String key = null;
      if (foundDescription) {
        key = "Description " + (imageCount - 1);
        foundDescription = false;
      }
      if (foundDate) {
        key = "CreationDate " + (imageCount - 1);
        foundDate = false;
      }
      if (key != null) {
        if (!keyMetadata.containsKey(key)) {
          keyMetadata.put(key, new String(ch, start, length));
        }
        else temporaryKeyMetadata.put(key, new String(ch, start, length));
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("OME")) {
        String uuid = attributes.getValue("UUID");
        if (fileUUIDs == null) fileUUIDs = new Vector();
        fileUUIDs.add(uuid);
      }
      else if (qName.equals("Image")) {
        String id = attributes.getValue("ID");
        if (!minimal) {
          if (id.startsWith("urn:lsid:")) {
            if (imageIDs == null) imageIDs = new Vector();
            imageIDs.add(id);
          }
          else lsids = false;
        }

        imageCount++;
      }
      else if (qName.equals("CreationDate")) {
        foundDate = true;
      }
      else if (qName.equals("Description")) {
        foundDescription = true;
      }
      else if (qName.equals("Pixels")) {
        if (minimal) return;
        currentSeries++;
        String id = attributes.getValue("ID");
        if (id.startsWith("urn:lsid:")) {
          if (pixelsIDs == null) pixelsIDs = new Vector();
          pixelsIDs.add(id);
        }
        else lsids = false;

        order = attributes.getValue("DimensionOrder");
        sizeZ = Integer.parseInt(attributes.getValue("SizeZ"));
        sizeC = Integer.parseInt(attributes.getValue("SizeC"));
        sizeT = Integer.parseInt(attributes.getValue("SizeT"));
        if (tempIfdCount != null) {
          tempIfdCount.add(new Integer(sizeZ * sizeC * sizeT));
        }

        if (sizeZ < 1) sizeZ = 1;
        if (sizeC < 1) sizeC = 1;
        if (sizeT < 1) sizeT = 1;

        if (core.sizeZ.length <= currentSeries) {
          CoreMetadata tempCore = new CoreMetadata(currentSeries + 1);
          int ss = core.sizeX.length;
          System.arraycopy(core.sizeX, 0, tempCore.sizeX, 0, ss);
          System.arraycopy(core.sizeY, 0, tempCore.sizeY, 0, ss);
          System.arraycopy(core.sizeZ, 0, tempCore.sizeZ, 0, ss);
          System.arraycopy(core.sizeC, 0, tempCore.sizeC, 0, ss);
          System.arraycopy(core.sizeT, 0, tempCore.sizeT, 0, ss);
          System.arraycopy(core.thumbSizeX, 0, tempCore.thumbSizeX, 0, ss);
          System.arraycopy(core.thumbSizeY, 0, tempCore.thumbSizeY, 0, ss);
          System.arraycopy(core.pixelType, 0, tempCore.pixelType, 0, ss);
          System.arraycopy(core.imageCount, 0, tempCore.imageCount, 0, ss);
          System.arraycopy(core.currentOrder, 0, tempCore.currentOrder, 0, ss);
          System.arraycopy(core.orderCertain, 0, tempCore.orderCertain, 0, ss);
          System.arraycopy(core.rgb, 0, tempCore.rgb, 0, ss);
          System.arraycopy(core.littleEndian, 0, tempCore.littleEndian, 0, ss);
          System.arraycopy(core.interleaved, 0, tempCore.interleaved, 0, ss);
          System.arraycopy(core.indexed, 0, tempCore.indexed, 0, ss);
          System.arraycopy(core.falseColor, 0, tempCore.falseColor, 0, ss);
          System.arraycopy(core.metadataComplete, 0,
            tempCore.metadataComplete, 0, ss);
          core = tempCore;
        }

        core.sizeX[currentSeries] =
          Integer.parseInt(attributes.getValue("SizeX"));
        core.sizeY[currentSeries] =
          Integer.parseInt(attributes.getValue("SizeY"));
        core.sizeZ[currentSeries] = sizeZ;
        core.sizeC[currentSeries] = sizeC;
        core.sizeT[currentSeries] = sizeT;
        core.currentOrder[currentSeries] = order;
        core.rgb[currentSeries] = isRGB();
        core.indexed[currentSeries] = isIndexed();
        core.falseColor[currentSeries] = isFalseColor();

        int sc = core.sizeC[currentSeries];
        if (core.rgb[currentSeries] && !core.indexed[currentSeries]) {
          if (sc <= 3) sc = 1;
          else sc /= 3;
        }
        core.imageCount[currentSeries] =
          core.sizeZ[currentSeries] * sc * core.sizeT[currentSeries];
        core.pixelType[currentSeries] =
          FormatTools.pixelTypeFromString(attributes.getValue("PixelType"));
        if (core.pixelType[currentSeries] == FormatTools.INT8 ||
          core.pixelType[currentSeries] == FormatTools.INT16 ||
          core.pixelType[currentSeries] == FormatTools.INT32)
        {
          core.pixelType[currentSeries]++;
        }

        if (isWiscScan) core.sizeT[currentSeries] = core.imageCount[0];

        core.orderCertain[currentSeries] = true;

        seriesCount++;
      }
      else if (qName.equals("TiffData")) {
        if (minimal) return;
        String uuid = attributes.getValue("UUID");
        String ifd = attributes.getValue("IFD");
        if (uuid != null) {
          uuids = true;
          if (planeUUIDs == null) planeUUIDs = new Vector();
          int ndx = Integer.parseInt(ifd);
          if (ndx < planeUUIDs.size()) planeUUIDs.setElementAt(uuid, ndx);
          else {
            int diff = ndx - planeUUIDs.size();
            for (int i=0; i<diff; i++) planeUUIDs.add("");
            planeUUIDs.add(uuid);
          }
        }
        String numPlanes = attributes.getValue("NumPlanes");
        String z = attributes.getValue("FirstZ");
        String c = attributes.getValue("FirstC");
        String t = attributes.getValue("FirstT");
        if (ifd == null || ifd.equals("")) ifd = "0";
        if (numPlanes == null || numPlanes.equals("")) {
          if (usedIFDs != null) numPlanes = "" + usedIFDs[currentSeries].length;
          else numPlanes = "" + ifds.length;
        }
        if (z == null || z.equals("")) z = "0";
        if (c == null || c.equals("")) c = "0";
        if (t == null || t.equals("")) t = "0";

        try {
          if (usedIFDs != null && usedIFDs[currentFile] != null) {
            int f = Integer.parseInt(ifd);
            int x = (int) TiffTools.getImageWidth(usedIFDs[currentFile][f]);
            int y = (int) TiffTools.getImageLength(usedIFDs[currentFile][f]);
            if (x != core.sizeX[currentSeries]) {
              LogTools.println("Mismatched width: got " +
                core.sizeX[currentSeries] + ", expected " + x);
              core.sizeX[currentSeries] = x;
            }
            if (y != core.sizeY[currentSeries]) {
              LogTools.println("Mismatched height: got " +
                core.sizeY[currentSeries] + ", expected " + y);
              core.sizeY[currentSeries] = y;
            }
          }
        }
        catch (FormatException e) { }

        int idx = FormatTools.getIndex(order, sizeZ, sizeC, sizeT,
          sizeZ * sizeC * sizeT, Integer.parseInt(z), Integer.parseInt(c),
          Integer.parseInt(t));

        if (tempIfdMap != null) {
          Vector v = new Vector(sizeZ * sizeC * sizeT);
          Vector y = new Vector(sizeZ * sizeC * sizeT);
          if (tempIfdMap.size() >= seriesCount && tempIfdMap.size() > 0) {
            v = (Vector) tempIfdMap.get(seriesCount - 1);
            y = (Vector) tempFileMap.get(seriesCount - 1);
          }
          else {
            for (int i=0; i<sizeZ*sizeC*sizeT; i++) {
              v.add(new Integer(-1));
              y.add(new Integer(-1));
            }
          }

          v.setElementAt(new Integer(ifd), idx);
          y.setElementAt(new Integer(0), idx);

          for (int i=1; i<Integer.parseInt(numPlanes); i++) {
            if (idx + i < v.size()) {
              v.setElementAt(new Integer(Integer.parseInt(ifd) + i), idx + i);
              y.setElementAt(new Integer(0), idx + i);
            }
            else {
              int diff = idx + i - v.size();
              for (int q=0; q<diff; q++) {
                v.add(new Integer(-1));
                y.add(new Integer(-1));
              }
              v.add(new Integer(Integer.parseInt(ifd) + i));
              y.add(new Integer(0));
            }
          }

          if (tempIfdMap.size() >= seriesCount) {
            tempIfdMap.setElementAt(v, seriesCount - 1);
            tempFileMap.setElementAt(y, seriesCount - 1);
          }
          else {
            tempIfdMap.add(v);
            tempFileMap.add(y);
          }
        }
        else {
          ifdMap[currentSeries][idx] = Integer.parseInt(ifd);
          fileMap[currentSeries][idx] = currentFile;
          for (int i=1; i<Integer.parseInt(numPlanes); i++) {
            if (idx + i < ifdMap[currentSeries].length) {
              ifdMap[currentSeries][idx + i] = ifdMap[currentSeries][idx] + i;
              fileMap[currentSeries][idx + i] = currentFile;
            }
          }
        }
      }
      else if (qName.equals("OriginalMetadata")) {
        addMeta(attributes.getValue("name"), attributes.getValue("value"));
      }
    }
  }

}
