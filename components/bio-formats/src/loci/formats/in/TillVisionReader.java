//
// TillVisionReader.java
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.POITools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * TillVisionReader is the file format reader for TillVision files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/TillVisionReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/TillVisionReader.java">SVN</a></dd></dl>
 */
public class TillVisionReader extends FormatReader {

  // -- Constants --

  private static final byte[] MARKER_0 = new byte[] {0x25, (byte) 0x80, 3, 0};
  private static final byte[] MARKER_1 =
    new byte[] {(byte) 0x96, (byte) 0x81, 3, 0};

  // -- Fields --

  private RandomAccessInputStream[] pixelsStream;
  private Hashtable exposureTimes;
  private boolean embeddedImages;
  private int embeddedOffset;

  // -- Constructor --

  /** Constructs a new TillVision reader. */
  public TillVisionReader() {
    super("TillVision", "vws");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int plane = FormatTools.getPlaneSize(this);
    if (embeddedImages) {
      in.seek(embeddedOffset + no * plane);
      readPlane(in, x, y, w, h, buf);
    }
    else {
      pixelsStream[series].seek(no * plane);
      readPlane(pixelsStream[series], x, y, w, h, buf);
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (pixelsStream != null) {
      for (int i=0; i<pixelsStream.length; i++) {
        if (pixelsStream[i] != null) {
          pixelsStream[i].close();
        }
      }
    }
    pixelsStream = null;
    embeddedOffset = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("TillVisionReader.initFile(" + id + ")");
    super.initFile(id);

    exposureTimes = new Hashtable();

    POITools poi = new POITools(id);
    Vector documents = poi.getDocumentList();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    Vector imageNames = new Vector();
    Vector waves = new Vector();
    Vector types = new Vector();
    Vector dates = new Vector();
    int nImages = 0;

    Hashtable tmpSeriesMetadata = new Hashtable();

    for (int i=0; i<documents.size(); i++) {
      String name = (String) documents.get(i);
      if (name.equals("Root Entry/Contents")) {
        RandomAccessInputStream s = poi.getDocumentStream(name);

        byte[] b = new byte[(int) s.length()];
        s.read(b);
        int pos = 0;
        int nFound = 0;

        embeddedImages = b[0] == 1;

        if (embeddedImages) {
          int len = DataTools.bytesToShort(b, 13, 2, true);
          String type = new String(b, 15, len);
          if (!type.equals("CImage")) {
            embeddedImages = false;
            continue;
          }

          int offset = 27;
          len = b[offset++] & 0xff;
          while (len != 0) {
            offset += len;
            len = b[offset++] & 0xff;
          }

          offset += 1243;

          core[0].sizeX = DataTools.bytesToInt(b, offset, 4, true);
          offset += 4;
          core[0].sizeY = DataTools.bytesToInt(b, offset, 4, true);
          offset += 4;
          core[0].sizeZ = DataTools.bytesToInt(b, offset, 4, true);
          offset += 4;
          core[0].sizeC = DataTools.bytesToInt(b, offset, 4, true);
          offset += 4;
          core[0].sizeT = DataTools.bytesToInt(b, offset, 4, true);
          offset += 4;
          core[0].pixelType =
            convertPixelType(DataTools.bytesToInt(b, offset, 4, true));
          embeddedOffset = offset + 32;
          in = poi.getDocumentStream(name);
        }

        byte[] marker = getMarker(b);
        byte[] check = new byte[] {4, 0, 0, 4};
        s.seek(0);

        while (s.getFilePointer() < s.length() - 2) {
          s.order(false);
          s.seek(findNextOffset(b, marker, (int) s.getFilePointer()));
          if (s.getFilePointer() < 0) break;
          s.skipBytes(3);
          int len = s.readShort();
          if (len <= 0) continue;
          imageNames.add(s.readString(len));
          s.skipBytes(6);
          s.order(true);
          len = s.readShort();
          if (len < 0 || len > 0x1000) continue;
          String description = s.readString(len);

          // parse key/value pairs from description

          String dateTime = "";

          String[] lines = description.split("\n");
          for (int q=0; q<lines.length; q++) {
            lines[q] = lines[q].trim();
            if (lines[q].indexOf(":") != -1 && !lines[q].startsWith(";")) {
              String key = lines[q].substring(0, lines[q].indexOf(":")).trim();
              String value =
                lines[q].substring(lines[q].indexOf(":") + 1).trim();
              String metaKey = "Series " + nImages + " " + key;
              addMeta(metaKey, value, tmpSeriesMetadata);

              if (key.equals("Start time of experiment")) {
                // HH:mm:ss aa OR HH:mm:ss.sss aa
                dateTime += " " + value;
              }
              else if (key.equals("Date")) {
                // mm/dd/yy ?
                dateTime = value + " " + dateTime;
              }
              else if (key.equals("Exposure time [ms]")) {
                float exp = Float.parseFloat(value) / 1000;
                exposureTimes.put(new Integer(nImages), new Float(exp));
              }
              else if (key.equals("Image type")) {
                types.add(value);
              }
              else if (key.equals("Monochromator wavelength [nm]")) {

              }
              else if (key.equals("Monochromator wavelength increment[nm]")) {
                waves.add(value);
              }
            }
          }

          dateTime = dateTime.trim();
          if (!dateTime.equals("")) {
            String[] formats = new String[] {"mm/dd/yy HH:mm:ss aa",
              "mm/dd/yy HH:mm:ss.SSS aa", "mm/dd/yy", "HH:mm:ss aa",
              "HH:mm:ss.SSS aa"};
            boolean success = false;
            for (int q=0; q<formats.length; q++) {
              try {
                dateTime = DateTools.formatDate(dateTime, formats[q]);
                success = true;
              }
              catch (NullPointerException e) { }
            }
            dates.add(success ? dateTime : "");
          }
          nImages++;
        }
      }
    }

    String directory = new Location(currentId).getAbsoluteFile().getParent();

    String[] pixelsFile = new String[nImages];

    if (!embeddedImages) {
      if (nImages == 0) {
        throw new FormatException("No images found.");
      }
      core = new CoreMetadata[nImages];

      // look for appropriate pixels files

      Location dir = new Location(directory);
      String[] files = dir.list();

      int nextFile = 0;

      for (int i=0; i<files.length; i++) {
        if (files[i].endsWith(".pst")) {
          Location pst = new Location(directory + File.separator + files[i]);
          if (pst.isDirectory()) {
            String[] subfiles = pst.list();
            for (int q=0; q<subfiles.length; q++) {
              if (subfiles[q].endsWith(".pst") && nextFile < nImages) {
                pixelsFile[nextFile++] =
                  files[i] + File.separator + subfiles[q];
              }
            }
          }
          else if (nextFile < nImages) {
            pixelsFile[nextFile++] = files[i];
          }
        }
      }
      if (nextFile == 0) {
        throw new FormatException("No image files found.");
      }
    }

    Arrays.sort(pixelsFile);

    pixelsStream = new RandomAccessInputStream[getSeriesCount()];

    Object[] metadataKeys = tmpSeriesMetadata.keySet().toArray();

    for (int i=0; i<getSeriesCount(); i++) {
      if (!embeddedImages) {
        core[i] = new CoreMetadata();

        // make sure that pixels file exists

        String file = pixelsFile[i];

        file = file.replaceAll("/", File.separator);
        file = file.replace('\\', File.separatorChar);
        String oldFile = file;

        file = directory + File.separator + oldFile;

        if (!new Location(file).exists()) {
          oldFile = oldFile.substring(oldFile.lastIndexOf(File.separator) + 1);
          file = directory + File.separator + oldFile;
          if (!new Location(file).exists()) {
            throw new FormatException("Could not find pixels file '" + file);
          }
        }

        pixelsStream[i] = new RandomAccessInputStream(file);

        // read key/value pairs from .inf files

        int dot = file.lastIndexOf(".");
        String infFile = file.substring(0, dot) + ".inf";
        in = new RandomAccessInputStream(infFile);

        String data = in.readString((int) in.length());
        StringTokenizer lines = new StringTokenizer(data);

        while (lines.hasMoreTokens()) {
          String line = lines.nextToken().trim();
          if (line.startsWith("[") || line.indexOf("=") == -1) {
            continue;
          }

          int equal = line.indexOf("=");
          String key = line.substring(0, equal).trim();
          String value = line.substring(equal + 1).trim();

          addGlobalMeta(key, value);

          if (key.equals("Width")) core[i].sizeX = Integer.parseInt(value);
          else if (key.equals("Height")) {
            core[i].sizeY = Integer.parseInt(value);
          }
          else if (key.equals("Bands")) core[i].sizeC = Integer.parseInt(value);
          else if (key.equals("Slices")) {
            core[i].sizeZ = Integer.parseInt(value);
          }
          else if (key.equals("Frames")) {
            core[i].sizeT = Integer.parseInt(value);
          }
          else if (key.equals("Datatype")) {
            core[i].pixelType = convertPixelType(Integer.parseInt(value));
          }
        }
        in.close();
      }

      core[i].imageCount = core[i].sizeZ * core[i].sizeC * core[i].sizeT;
      core[i].rgb = false;
      core[i].littleEndian = true;
      core[i].dimensionOrder = "XYCZT";

      core[i].seriesMetadata = new Hashtable();
      for (Object key : metadataKeys) {
        String keyName = key.toString();
        if (keyName.startsWith("Series " + i + " ")) {
          keyName = keyName.replaceAll("Series " + i + " ", "");
          core[i].seriesMetadata.put(keyName, tmpSeriesMetadata.get(key));
        }
      }
    }
    tmpSeriesMetadata = null;

    MetadataTools.populatePixels(store, this, true);

    for (int i=0; i<getSeriesCount(); i++) {
      // populate Image data
      if (i < imageNames.size()) {
        store.setImageName((String) imageNames.get(i), i);
      }
      String date = i < dates.size() ? (String) dates.get(i) : "";
      if (date != null && !date.equals("")) {
        store.setImageCreationDate(date, i);
      }
      else MetadataTools.setDefaultCreationDate(store, id, i);

      // populate PlaneTiming data

      for (int q=0; q<core[i].imageCount; q++) {
        store.setPlaneTimingExposureTime(
          (Float) exposureTimes.get(new Integer(i)), i, 0, q);
      }

      // populate Dimensions data

      if (i < waves.size()) {
        int waveIncrement = Integer.parseInt((String) waves.get(i));
        if (waveIncrement > 0) {
          store.setDimensionsWaveIncrement(new Integer(waveIncrement), i, 0);
        }
      }

      // populate Experiment data

      if (i < types.size()) {
        store.setExperimentType((String) types.get(i), i);
      }
    }
  }

  // -- Helper methods --

  private int convertPixelType(int type) throws FormatException {
    switch (type) {
      case 1:
        return FormatTools.INT8;
      case 2:
        return FormatTools.UINT8;
      case 3:
        return FormatTools.INT16;
      case 4:
        return FormatTools.UINT16;
      default:
        throw new FormatException("Unsupported data type: " + type);
    }
  }

  private byte[] getMarker(byte[] s) throws IOException {
    int offset = findNextOffset(s, MARKER_0, 0);
    if (offset != -1) return MARKER_0;
    return MARKER_1;
  }

  private int findNextOffset(byte[] s, byte[] marker, int pos)
    throws IOException
  {
    for (int i=pos; i<s.length-marker.length; i++) {
      boolean found = true;
      for (int q=0; q<marker.length; q++) {
        if (marker[q] != s[i + q]) {
          found = false;
          break;
        }
      }
      if (found) return i + marker.length;
    }
    return -1;
  }

}
