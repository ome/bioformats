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

  private Vector tempIfdMap, tempFileMap, tempIfdCount;
  private int currentFile, currentSeries, seriesCount;
  private int[] numIFDs;
  private int[][] ifdMap, fileMap;
  private boolean lsids, isWiscScan;
  private Hashtable[][] usedIFDs;

  // -- Constructor --

  public OMETiffReader() {
    super("OME-TIFF", new String[] {"tif", "tiff"});
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    OMETiffHandler handler = new OMETiffHandler();
    String comment = (String) getMeta("Comment");

    currentSeries = -1;
    seriesCount = 0;
    imageIDs = null;
    pixelsIDs = null;
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
      s = (String) TiffTools.getIFDValue(ifds[1], TiffTools.IMAGE_DESCRIPTION);
    }
    isWiscScan = s != null && s.indexOf("ome.xsd") != -1;

    // look for additional files in the dataset
    Vector files = new Vector();
    Location l = new Location(currentId);
    l = l.getAbsoluteFile().getParentFile();
    String[] fileList = l.list();

    if (!lsids) {
      fileList = new String[] {currentId};
      LogTools.println("Not searching for other files - " +
        "Image LSID not present");
    }

    for (int i=0; i<fileList.length; i++) {
      String check = fileList[i].toLowerCase();
      if (check.endsWith(".tif") || check.endsWith(".tiff")) {
        status("Checking " + fileList[i]);
        String iid = l.getAbsolutePath() + File.separator + fileList[i];
        String icomment = TiffTools.getComment(iid);
        boolean addToList = true;
        if (imageIDs != null) {
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
        if (addToList) files.add(iid);
      }
    }

    // parse grouped files

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
      for (int j=0; j<v.size(); j++) {
        ifdMap[i][j] = ((Integer) v.get(j)).intValue();
      }
      numIFDs[i] = v.size();
    }

    for (int i=0; i<tempFileMap.size(); i++) {
      Vector v = (Vector) tempFileMap.get(i);
      for (int j=0; j<v.size(); j++) {
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

      usedIFDs[i] = TiffTools.getIFDs(new RandomAccessStream(usedFiles[i]));
      String c = (String)
        TiffTools.getIFDValue(usedIFDs[i][0], TiffTools.IMAGE_DESCRIPTION);
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
      else if (core.imageCount[i] > ifds.length) {
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
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() {
    String comment = (String) getMeta("Comment");
    metadata.remove("Comment");
    MetadataStore store = getMetadataStore();
    MetadataTools.convertMetadata(comment, store);
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return usedFiles;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int ifd = ifdMap[series][no];
    int fileIndex = fileMap[series][no];

    in = new RandomAccessStream(usedFiles[fileIndex]);
    TiffTools.getSamples(usedIFDs[fileIndex][ifd], in, buf);
    in.close();
    return swapIfRequired(buf);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension
    if (!open) return true; // not allowed to check the file contents

    // just checking the filename isn't enough to differentiate between
    // OME-TIFF and regular TIFF; open the file and check more thoroughly
    try {
      RandomAccessStream ras = new RandomAccessStream(name);
      Hashtable ifd = TiffTools.getFirstIFD(ras);
      ras.close();
      if (ifd == null) return false;

      String comment = (String)
        ifd.get(new Integer(TiffTools.IMAGE_DESCRIPTION));
      if (comment == null) return false;
      return comment.indexOf("ome.xsd") >= 0;
    }
    catch (IOException e) { return false; }
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  private class OMETiffHandler extends DefaultHandler {
    private String order;
    private int sizeZ, sizeC, sizeT;

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("Image")) {
        String id = attributes.getValue("ID");
        if (id.startsWith("urn:lsid:")) {
          if (imageIDs == null) imageIDs = new Vector();
          imageIDs.add(id);
        }
        else lsids = false;
      }
      else if (qName.equals("Pixels")) {
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
        if (core.rgb[currentSeries] && !core.indexed[currentSeries]) sc /= 3;
        if (core.indexed[currentSeries]) core.sizeC[currentSeries] *= 3;
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
        String ifd = attributes.getValue("IFD");
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
            v.setElementAt(new Integer(Integer.parseInt(ifd) + i), idx + i);
            y.setElementAt(new Integer(0), idx + i);
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
            ifdMap[currentSeries][idx + i] = ifdMap[currentSeries][idx] + i;
            fileMap[currentSeries][idx + i] = currentFile;
          }
        }
      }
    }
  }

}
