/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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
import java.util.ArrayList;
import java.util.Arrays;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * MicroCTReader is the file format reader for VFF files from a GE MicroCT scanner.
 *
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MicroCTReader extends FormatReader {

  // -- Constants --

  private static final String VFF_MAGIC = "ncaa";
  private static final String DATE_FORMAT = "EEE, MMM dd, yyyy HH:mm:ss a";

  // -- Fields --

  private String[] vffs;
  private long[] headerSize;
  private ArrayList<String> metadataFiles = new ArrayList<String>();
  private String date, time;
  private String imageDescription;
  private Double exposureTime;

  // -- Constructor --

  /** Constructs a new MicroCT reader. */
  public MicroCTReader() {
    super("MicroCT", "vff");
    domains = new String[] {FormatTools.MEDICAL_DOMAIN};
    datasetDescription =
      "Directory with XML file and one .tif/.tiff file per plane";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return 1;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    FilePattern pattern = new FilePattern(FilePattern.findPattern(id));
    return pattern.getFiles().length == 1;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return stream.readString(4).equals(VFF_MAGIC);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    if (!noPixels) {
      for (String file : vffs) {
        files.add(file);
      }
    }
    files.addAll(metadataFiles);

    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      vffs = null;
      headerSize = null;
      metadataFiles.clear();
      date = null;
      time = null;
      imageDescription = null;
      exposureTime = null;
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int vffIndex = no % vffs.length;
    try (RandomAccessInputStream vff = new RandomAccessInputStream(vffs[vffIndex])) {
      if (headerSize[vffIndex] == 0) {
        skipHeader(vff);
        headerSize[vffIndex] = vff.getFilePointer();
      }

      long planeSize = (long) FormatTools.getPlaneSize(this);
      vff.seek(headerSize[vffIndex] + planeSize * (no / vffs.length));
      readPlane(vff, x, y, w, h, buf);

      // reverse the rows in the buffer
      // images are stored with the origin in the lower-left corner

      byte[] row = new byte[(int) (w * (planeSize / (getSizeX() * getSizeY())))];
      for (int yy=0; yy<h/2; yy++) {
        int topOffset = buf.length - (yy + 1) * row.length;
        int bottomOffset = yy * row.length;
        System.arraycopy(buf, bottomOffset, row, 0, row.length);
        System.arraycopy(buf, topOffset, buf, bottomOffset, row.length);
        System.arraycopy(row, 0, buf, topOffset, row.length);
      }

      return buf;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // use a FilePattern to find any other .vff files in the same dataset

    FilePattern pattern = new FilePattern(FilePattern.findPattern(id));
    vffs = pattern.getFiles();

    headerSize = new long[vffs.length];

    // find all metadata files in the same directory

    Location parent = new Location(id).getAbsoluteFile().getParentFile();
    String[] files = parent.list(true);
    for (String file : files) {
      if (!checkSuffix(file, "vff")) {
        Location metadata = new Location(parent, file);
        if (!metadata.isDirectory()) {
          metadataFiles.add(metadata.getAbsolutePath());
        }
      }
    }

    CoreMetadata ms = core.get(0);
    ms.sizeZ = vffs.length;
    PositiveFloat physicalSize = null;
    in = new RandomAccessInputStream(id);

    try {
      String line = in.readLine().trim();
      int dimCount = 0;
      while (line.length() > 0) {
        int eq = line.indexOf("=");
        if (eq >= 0) {
          String key = line.substring(0, eq);
          String value = line.substring(eq + 1, line.length() - 1);

          processKey(key, value);

          if (key.equals("rank")) {
            // determines the number of dimensions stored within the file
            // a Z stack can either be stored internally, or across multiple
            // files as separate slices
            dimCount = Integer.parseInt(value);
          }
          else if (key.equals("size")) {
            String[] dims = value.split(" ");
            if (dimCount > 0) {
              ms.sizeX = Integer.parseInt(dims[0]);
            }
            if (dimCount > 1) {
              ms.sizeY = Integer.parseInt(dims[1]);
            }
            if (dimCount > 2) {
              ms.sizeZ *= Integer.parseInt(dims[2]);
            }
          }
          else if (key.equals("bits")) {
            int bits = Integer.parseInt(value);
            ms.pixelType = FormatTools.pixelTypeFromBytes(bits / 8, true, false);
          }
          else if (key.equals("elementsize")) {
            Double size = new Double(value);
            // physical size is stored in mm, not um
            physicalSize = new PositiveFloat(size * 1000);
          }
        }
        line = in.readLine().trim();
      }
    }
    finally {
      in.close();
    }

    ms.sizeT = 1;
    ms.sizeC = 1;
    ms.imageCount = getSizeZ() * getSizeT() * getSizeC();
    ms.dimensionOrder = "XYZCT";

    // parse extra values from metadata files

    for (String file : metadataFiles) {
      String name = new Location(file).getName();
      String data = DataTools.readFile(file).trim();
      if (checkSuffix(file, "protocol") || checkSuffix(file, "log") ||
        name.equals("Parameters.txt"))
      {
        // key/value pairs separated by = or :

        String separator = name.equals("Parameters.txt") ? ":" : "=";

        String[] pairs = data.split("\r\n");
        for (String pair : pairs) {
          int sep = pair.indexOf(separator);
          if (sep < 0) {
            continue;
          }
          processKey(
            pair.substring(0, sep).trim(), pair.substring(sep + 1).trim());
        }
      }
      else {
        // assume a single value; the file name is the key
        processKey(name, data);
      }
    }

    // populate the MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, exposureTime != null);

    if (imageDescription != null) {
      store.setImageDescription(imageDescription, 0);
    }

    if (date != null && time != null) {
      String timestamp = DateTools.formatDate(date + " " + time, DATE_FORMAT);
      store.setImageAcquisitionDate(new Timestamp(timestamp), 0);
    }

    if (physicalSize != null) {
      Length size = FormatTools.createLength(physicalSize, UNITS.MICROM);
      store.setPixelsPhysicalSizeX(size, 0);
      store.setPixelsPhysicalSizeY(size, 0);
      store.setPixelsPhysicalSizeZ(size, 0);
    }

    if (exposureTime != null) {
      Time exposureSeconds = new Time(exposureTime, UNITS.S);
      for (int i=0; i<getImageCount(); i++) {
        store.setPlaneExposureTime(exposureSeconds, 0, i);
      }
    }

  }

  /**
   * Skip the VFF header, which consists of a variable number of LF-terminated
   * lines.  The final line in the header is expected to be 0x0c0a.
   */
  private void skipHeader(RandomAccessInputStream s) throws IOException {
    while (s.readLine().trim().length() > 0);
  }

  /**
   * Takes a key/value pair from disparate metadata files, and attempts
   * to stores it in the original metadata table and/or MetadataStore
   * as appropriate.
   */
  private void processKey(String key, String value) {
    addGlobalMeta(key, value);

    if (key.equals("Exposure Time (ms)")) {
      exposureTime = new Double(value);
      exposureTime /= 1000.0;
    }
    else if (key.equals("Description.txt")) {
      imageDescription = value;
    }
    else if (key.equals("Date")) {
      date = value;
    }
    else if (key.equals("Time")) {
      time = value;
    }
  }

}
