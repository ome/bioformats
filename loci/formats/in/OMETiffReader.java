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
import org.w3c.dom.*;
import org.xml.sax.SAXException;

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

  // -- Fields --

  /** List of used files. */
  private String[] used;

  private Hashtable[] coordinateMap;

  // -- Constructor --

  public OMETiffReader() {
    super("OME-TIFF", new String[] {"tif", "tiff"});
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

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return used;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    if (used.length == 1) {
      TiffTools.getSamples(ifds[no], in, buf);
      return swapIfRequired(buf);
    }

    int[] zct = getZCTCoords(no);
    int fileIndex = -1;
    int[] savedCoords = new int[] {-1, -1, -1};
    Integer[] keys =
      (Integer[]) coordinateMap[series].keySet().toArray(new Integer[0]);

    for (int i=0; i<keys.length; i++) {
      int[] firstZCT = (int[]) coordinateMap[series].get(keys[i]);
      if (firstZCT[0] <= zct[0] && firstZCT[1] <= zct[1] &&
        firstZCT[2] <= zct[2] && firstZCT[0] >= savedCoords[0] &&
        firstZCT[1] >= savedCoords[1] && firstZCT[2] >= savedCoords[2])
      {
        savedCoords = firstZCT;
        fileIndex = keys[i].intValue();
      }
    }
    in = new RandomAccessStream(used[fileIndex]);
    ifds = TiffTools.getIFDs(in);
    int firstIFD = ((int[]) coordinateMap[series].get(keys[fileIndex]))[3];
    TiffTools.getSamples(ifds[firstIFD + (no % ifds.length)], in, buf);
    in.close();
    return swapIfRequired(buf);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    String comment = (String) getMeta("Comment");
    boolean lsids = true;

    // find list of Image IDs
    Vector v = new Vector();
    String check = "<Image ";
    int ndx = comment.indexOf(check);
    while (ndx >= 0) {
      int ii = comment.indexOf("ID=\"", ndx);
      v.add(comment.substring(ii + 4, comment.indexOf("\"", ii + 5)));
      ndx = comment.indexOf(check, ndx + 1);
    }
    String[] imageIds = (String[]) v.toArray(new String[0]);
    for (int i=0; i<imageIds.length; i++) {
      if (!imageIds[i].toLowerCase().startsWith("urn:lsid")) {
        lsids = false;
        break;
      }
    }

    // find list of Pixels IDs
    v.clear();
    check = "<Pixels ";
    ndx = comment.indexOf(check);
    while (ndx >= 0) {
      int ii = comment.indexOf("ID=\"", ndx);
      v.add(comment.substring(ii + 4, comment.indexOf("\"", ii + 5)));
      ndx = comment.indexOf(check, ndx + 1);
    }
    String[] pixelsIds = (String[]) v.toArray(new String[0]);
    for (int i=0; i<pixelsIds.length; i++) {
      if (!pixelsIds[i].toLowerCase().startsWith("urn:lsid")) {
        lsids = false;
        break;
      }
    }

    int numSeries = pixelsIds.length;

    int[] numIFDs = new int[numSeries];

    // only look for files in the same dataset if LSIDs are present
    if (lsids) {
      Vector files = new Vector();
      Location l = new Location(currentId);
      l = l.getAbsoluteFile().getParentFile();
      String[] fileList = l.list();
      coordinateMap = new Hashtable[numSeries];
      for (int i=0; i<numSeries; i++) {
        coordinateMap[i] = new Hashtable();
      }

      for (int i=0; i<fileList.length; i++) {
        check = fileList[i].toLowerCase();
        if (check.endsWith(".tif") || check.endsWith(".tiff")) {
          Hashtable ifd = TiffTools.getFirstIFD(new RandomAccessStream(
            l.getAbsolutePath() + File.separator + fileList[i]));
          if (ifd == null) continue;
          comment =
            (String) TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION);
          boolean addToList = true;
          for (int s=0; s<imageIds.length; s++) {
            if (comment.indexOf(imageIds[s]) == -1) {
              addToList = false;
              break;
            }
          }
          if (addToList) {
            for (int s=0; s<pixelsIds.length; s++) {
              if (comment.indexOf(pixelsIds[s]) == -1) {
                addToList = false;
              }
            }
          }

          if (addToList) {
            files.add(l.getAbsolutePath() + File.separator + fileList[i]);

            ByteArrayInputStream is =
              new ByteArrayInputStream(comment.getBytes());
            DocumentBuilderFactory factory =
              DocumentBuilderFactory.newInstance();
            Document doc = null;
            try {
              DocumentBuilder builder = factory.newDocumentBuilder();
              doc = builder.parse(is);
            }
            catch (ParserConfigurationException exc) { }
            catch (SAXException exc) { }
            catch (IOException exc) { }

            if (doc != null) {
              NodeList pixelsList = doc.getElementsByTagName("Pixels");
              for (int j=0; j<numSeries; j++) {
                NodeList list = ((Element) pixelsList.item(j)).getChildNodes();
                // if there are multiple TiffData elements, find the one with
                // the smallest ZCT coordinates

                v = new Vector();
                for (int q=0; q<list.getLength(); q++) {
                  if (((Node) list.item(q)).getNodeName().equals("TiffData")) {
                    v.add(list.item(q));
                  }
                }
                Element[] tiffData = (Element[]) v.toArray(new Element[0]);
                int[] smallestCoords = new int[4];
                Arrays.fill(smallestCoords, Integer.MAX_VALUE);
                for (int q=0; q<tiffData.length; q++) {
                  String firstZ = tiffData[q].getAttribute("FirstZ");
                  String firstC = tiffData[q].getAttribute("FirstC");
                  String firstT = tiffData[q].getAttribute("FirstT");
                  String firstIFD = tiffData[q].getAttribute("IFD");
                  if (firstZ == null || firstZ.equals("")) firstZ = "0";
                  if (firstC == null || firstC.equals("")) firstC = "0";
                  if (firstT == null || firstT.equals("")) firstT = "0";
                  if (firstIFD == null || firstIFD.equals("")) firstIFD = "0";
                  int z = Integer.parseInt(firstZ);
                  int c = Integer.parseInt(firstC);
                  int t = Integer.parseInt(firstT);
                  if (z <= smallestCoords[0] && c <= smallestCoords[1] &&
                    t <= smallestCoords[2])
                  {
                    smallestCoords[0] = z;
                    smallestCoords[1] = c;
                    smallestCoords[2] = t;
                    smallestCoords[3] = Integer.parseInt(firstIFD);
                  }
                }
                coordinateMap[j].put(new Integer(files.size() - 1),
                  smallestCoords);
                numIFDs[j] += TiffTools.getIFDs(new RandomAccessStream(
                  (String) files.get(files.size() - 1))).length;
              }
            }
          }
        }
      }

      used = (String[]) files.toArray(new String[0]);
    }
    else {
      used = new String[] {currentId};
      LogTools.println("Not searching for other files - " +
        "Image LSID not present.");
      numIFDs[0] = ifds.length;
    }

    int oldX = core.sizeX[0];
    int oldY = core.sizeY[0];
    comment = (String) getMeta("Comment");

    // convert string to DOM
    ByteArrayInputStream is = new ByteArrayInputStream(comment.getBytes());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    Document doc = null;
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      doc = builder.parse(is);
    }
    catch (ParserConfigurationException exc) { }
    catch (SAXException exc) { }
    catch (IOException exc) { }

    // extract TiffData elements from XML
    Element[] pixels = null;
    Element[][] tiffData = null;
    if (doc != null) {
      NodeList pixelsList = doc.getElementsByTagName("Pixels");
      int nSeries = pixelsList.getLength();
      v = new Vector();
      pixels = new Element[nSeries];
      tiffData = new Element[nSeries][];
      for (int i=0; i<nSeries; i++) {
        pixels[i] = (Element) pixelsList.item(i);
        NodeList list = pixels[i].getChildNodes();
        int size = list.getLength();
        v.clear();
        for (int j=0; j<size; j++) {
          Node node = list.item(j);
          if (!(node instanceof Element)) continue;
          if ("TiffData".equals(node.getNodeName())) v.add(node);
        }
        tiffData[i] = new Element[v.size()];
        v.copyInto(tiffData[i]);
      }
    }

    // MAJOR HACK : check for OME-XML in the comment of the second IFD
    // There is a version of WiscScan which writes OME-XML to every IFD,
    // but with SizeZ and SizeT equal to 1.

    String s = null;
    if (ifds.length > 1) {
      s = (String) TiffTools.getIFDValue(ifds[1], TiffTools.IMAGE_DESCRIPTION);
    }
    boolean isWiscScan = s != null && s.indexOf("ome.xsd") != -1;

    // extract SizeZ, SizeC and SizeT from XML block
    if (tiffData != null) {
      boolean rgb = isRGB();
      boolean indexed = isIndexed();
      boolean falseColor = isFalseColor();
      core = new CoreMetadata(tiffData.length);
      Arrays.fill(core.orderCertain, true);
      Arrays.fill(core.indexed, indexed);
      Arrays.fill(core.falseColor, falseColor);

      for (int i=0; i<tiffData.length; i++) {
        core.sizeX[i] = Integer.parseInt(pixels[i].getAttribute("SizeX"));
        if (core.sizeX[i] != oldX) {
          LogTools.println("Mismatched width: OME-XML reports SizeX=" +
            core.sizeX[i] + "; expecting " + oldX);
          core.sizeX[i] = oldX;
        }
        core.sizeY[i] = Integer.parseInt(pixels[i].getAttribute("SizeY"));
        if (core.sizeY[i] != oldY) {
          LogTools.println("Mismatched height: OME-XML reports SizeY=" +
            core.sizeY[i] + "; expecting " + oldY);
          core.sizeY[i] = oldY;
        }
        core.sizeZ[i] = Integer.parseInt(pixels[i].getAttribute("SizeZ"));
        core.sizeC[i] = Integer.parseInt(pixels[i].getAttribute("SizeC"));
        core.sizeT[i] = Integer.parseInt(pixels[i].getAttribute("SizeT"));

        // HACK: correct for erroneous negative size values
        if (core.sizeZ[i] < 1) core.sizeZ[i] = 1;
        if (core.sizeC[i] < 1) core.sizeC[i] = 1;
        if (core.sizeT[i] < 1) core.sizeT[i] = 1;

        if (rgb && indexed && core.sizeC[i] == 3) {
          rgb = false;
          core.indexed[i] = false;
          core.falseColor[i] = false;
        }

        int sc = core.sizeC[i];
        if (rgb && sc > 1) sc /= 3;
        core.imageCount[i] = core.sizeZ[i] * sc * core.sizeT[i];
        core.rgb[i] = rgb;
        core.pixelType[i] = FormatTools.pixelTypeFromString(
          pixels[i].getAttribute("PixelType"));
        if (core.pixelType[i] == FormatTools.INT8 ||
          core.pixelType[i] == FormatTools.INT16 ||
          core.pixelType[i] == FormatTools.INT32)
        {
          core.pixelType[i]++;
        }

        // MAJOR HACK: adjust SizeT to match the number of IFDs, if this file
        // was written by a buggy version of WiscScan
        if (isWiscScan) core.sizeT[i] = core.imageCount[0];

        core.currentOrder[i] = pixels[i].getAttribute("DimensionOrder");
        core.orderCertain[i] = true;

        if (numIFDs != null && lsids) {
          if (numIFDs[i] < core.imageCount[i]) {
            LogTools.println("Too few IFDs; got " + numIFDs[i] + ", expected " +
              core.imageCount[i]);
          }
          else if (numIFDs[i] > core.imageCount[i]) {
            LogTools.println("Too many IFDs; got " + numIFDs[i] +
              ", expected " + core.imageCount[i]);
          }
        }
        else if (core.imageCount[i] > ifds.length) {
          core.imageCount[i] = ifds.length;
          if (core.sizeZ[i] > ifds.length) {
            core.sizeZ[i] = ifds.length / (rgb ? core.sizeC[i] : 1);
            core.sizeT[i] = 1;
            if (!rgb) core.sizeC[i] = 1;
          }
          else if (core.sizeT[i] > ifds.length) {
            core.sizeT[i] = ifds.length / (rgb ? core.sizeC[i] : 1);
            core.sizeZ[i] = 1;
            if (!rgb) core.sizeC[i] = 1;
          }
        }

        boolean[][][] zct = new boolean[core.sizeZ[i]][sc][core.sizeT[i]];

        for (int j=0; j<tiffData[i].length; j++) {
          String aIfd = tiffData[i][j].getAttribute("IFD");
          String aFirstZ = tiffData[i][j].getAttribute("FirstZ");
          String aFirstT = tiffData[i][j].getAttribute("FirstT");
          String aFirstC = tiffData[i][j].getAttribute("FirstC");
          String aNumPlanes = tiffData[i][j].getAttribute("NumPlanes");
          boolean nullIfd = aIfd == null || aIfd.equals("");
          boolean nullFirstZ = aFirstZ == null || aFirstZ.equals("");
          boolean nullFirstC = aFirstC == null || aFirstC.equals("");
          boolean nullFirstT = aFirstT == null || aFirstT.equals("");
          boolean nullNumPlanes = aNumPlanes == null || aNumPlanes.equals("");

          int firstZ = nullFirstZ ? 0 : Integer.parseInt(aFirstZ);
          int firstC = nullFirstC ? 0 : Integer.parseInt(aFirstC);
          int firstT = nullFirstT ? 0 : Integer.parseInt(aFirstT);
          int numPlanes = nullNumPlanes ? (nullIfd ? core.imageCount[0] : 1) :
            Integer.parseInt(aNumPlanes);

          // HACK: adjust first values, if this file
          // was written by a buggy version of WiscScan
          if (firstZ >= core.sizeZ[0]) firstZ = core.sizeZ[0] - 1;
          if (firstC >= core.sizeC[0]) firstC = core.sizeC[0] - 1;
          if (firstT >= core.sizeT[0]) firstT = core.sizeT[0] - 1;

          // populate ZCT matrix
          char d1st = core.currentOrder[i].charAt(2);
          char d2nd = core.currentOrder[i].charAt(3);
          int z = firstZ, t = firstT, c = firstC;

          for (int k=0; k<numPlanes; k++) {
            zct[z][c][t] = true;
            switch (d1st) {
              case 'Z':
                z++;
                if (z >= core.sizeZ[i]) {
                  z = 0;
                  switch (d2nd) {
                    case 'T':
                      t++;
                      if (t >= core.sizeT[i]) {
                        t = 0;
                        c++;
                      }
                      break;
                    case 'C':
                      c++;
                      if (c >= sc) {
                        c = 0;
                        t++;
                      }
                      break;
                  }
                }
                break;
              case 'T':
                t++;
                if (t >= core.sizeT[i]) {
                  t = 0;
                  switch (d2nd) {
                    case 'Z':
                      z++;
                      if (z >= core.sizeZ[i]) {
                        z = 0;
                        c++;
                      }
                      break;
                    case 'C':
                      c++;
                      if (c >= sc) {
                        c = 0;
                        z++;
                      }
                      break;
                  }
                }
                break;
              case 'C':
                c++;
                if (c >= sc) {
                  c = 0;
                  switch (d2nd) {
                    case 'Z':
                      z++;
                      if (z >= core.sizeZ[i]) {
                        z = 0;
                        t++;
                      }
                      break;
                    case 'T':
                      t++;
                      if (t >= core.sizeT[i]) {
                        t = 0;
                        z++;
                      }
                      break;
                  }
                }
                break;
            }
          }
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

}
