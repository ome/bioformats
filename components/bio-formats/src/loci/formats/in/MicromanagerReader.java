//
// MicromanagerReader.java
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * MicromanagerReader is the file format reader for Micro-Manager files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MicromanagerReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MicromanagerReader.java">SVN</a></dd></dl>
 */
public class MicromanagerReader extends FormatReader {

  // -- Constants --

  /** File containing extra metadata. */
  private static final String METADATA = "metadata.txt";

  // -- Fields --

  /** Helper reader for TIFF files. */
  private MinimalTiffReader tiffReader;

  /** List of TIFF files to open. */
  private Vector tiffs;

  private String metadataFile;

  private String[] channels;

  private String comment, time;
  private Float exposureTime, sliceThickness, pixelSize;
  private Float[] timestamps;

  private int gain;
  private String binning, detectorID, detectorModel, detectorManufacturer;
  private float temperature;
  private Vector voltage;
  private String cameraRef;
  private String cameraMode;

  // -- Constructor --

  /** Constructs a new Micromanager reader. */
  public MicromanagerReader() {
    super("Micro-Manager", new String[] {"tif", "tiff", "txt"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (name.equals(METADATA) || name.endsWith(File.separator + METADATA)) {
      try {
        RandomAccessInputStream stream = new RandomAccessInputStream(name);
        long length = stream.length();
        stream.close();
        return length > 0;
      }
      catch (IOException e) {
        return false;
      }
    }
    if (!open) return false; // not allowed to touch the file system
    try {
      Location parent = new Location(name).getAbsoluteFile().getParentFile();
      Location metaFile = new Location(parent, METADATA);
      return metaFile.exists() && metaFile.length() > 0;
    }
    catch (NullPointerException e) { }
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return tiffReader.isThisType(stream);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    String[] s = new String[tiffs.size() + 1];
    tiffs.copyInto(s);
    s[tiffs.size()] = metadataFile;
    return s;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    return noPixels ? new String[] {metadataFile} : getUsedFiles();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    int[] coords = getZCTCoords(no);
    tiffReader.setId((String) tiffs.get(no));
    return tiffReader.openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (tiffReader != null) tiffReader.close(fileOnly);
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (tiffReader != null) tiffReader.close();
    tiffReader = null;
    tiffs = null;
    comment = time = null;
    exposureTime = sliceThickness = pixelSize = null;
    timestamps = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    tiffReader = new MinimalTiffReader();

    status("Reading metadata file");

    // find metadata.txt

    Location file = new Location(currentId).getAbsoluteFile();
    metadataFile = file.exists() ? new Location(file.getParentFile(),
      METADATA).getAbsolutePath() : METADATA;
    in = new RandomAccessInputStream(metadataFile);
    String parent = file.exists() ?
      file.getParentFile().getAbsolutePath() + File.separator : "";

    // usually a small file, so we can afford to read it into memory

    byte[] meta = new byte[(int) in.length()];
    in.read(meta);
    String s = new String(meta);
    meta = null;

    status("Finding image file names");

    // find the name of a TIFF file
    String baseTiff = null;
    tiffs = new Vector();
    int pos = 0;
    while (true) {
      pos = s.indexOf("FileName", pos);
      if (pos == -1 || pos >= in.length()) break;
      String name = s.substring(s.indexOf(":", pos), s.indexOf(",", pos));
      baseTiff = parent + name.substring(3, name.length() - 1);
      pos++;
    }

    // now parse the rest of the metadata

    // metadata.txt looks something like this:
    //
    // {
    //   "Section Name": {
    //      "Key": "Value",
    //      "Array key": [
    //        first array value, second array value
    //      ]
    //   }
    //
    // }

    status("Populating metadata");

    Vector stamps = new Vector();
    Vector voltage = new Vector();

    StringTokenizer st = new StringTokenizer(s, "\n");
    int[] slice = new int[3];
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim();
      boolean open = token.indexOf("[") != -1;
      boolean closed = token.indexOf("]") != -1;
      if (open || (!open && !closed && !token.equals("{") &&
        !token.startsWith("}")))
      {
        int quote = token.indexOf("\"") + 1;
        String key = token.substring(quote, token.indexOf("\"", quote));
        String value = null;

        if (!open && !closed) {
          value = token.substring(token.indexOf(":") + 1);
        }
        else if (!closed){
          StringBuffer valueBuffer = new StringBuffer();
          while (!closed) {
            token = st.nextToken();
            closed = token.indexOf("]") != -1;
            valueBuffer.append(token);
          }
          value = valueBuffer.toString();
          value = value.replaceAll("\n", "");
        }

        int startIndex = value.indexOf("[");
        int endIndex = value.indexOf("]");
        if (endIndex == -1) endIndex = value.length();

        value = value.substring(startIndex + 1, endIndex).trim();
        value = value.substring(0, value.length() - 1);
        value = value.replaceAll("\"", "");
        if (value.endsWith(",")) value = value.substring(0, value.length() - 1);
        addMeta(key, value);
        if (key.equals("Channels")) core[0].sizeC = Integer.parseInt(value);
        else if (key.equals("ChNames")) {
          StringTokenizer t = new StringTokenizer(value, ",");
          int nTokens = t.countTokens();
          channels = new String[nTokens];
          for (int q=0; q<nTokens; q++) {
            channels[q] = t.nextToken().replaceAll("\"", "").trim();
          }
        }
        else if (key.equals("Frames")) {
          core[0].sizeT = Integer.parseInt(value);
        }
        else if (key.equals("Slices")) {
          core[0].sizeZ = Integer.parseInt(value);
        }
        else if (key.equals("PixelSize_um")) {
          pixelSize = new Float(value);
        }
        else if (key.equals("z-step_um")) {
          sliceThickness = new Float(value);
        }
        else if (key.equals("Time")) time = value;
        else if (key.equals("Comment")) comment = value;
      }

      if (token.startsWith("\"FrameKey")) {
        int dash = token.indexOf("-") + 1;
        int nextDash = token.indexOf("-", dash);
        slice[2] = Integer.parseInt(token.substring(dash, nextDash));
        dash = nextDash + 1;
        nextDash = token.indexOf("-", dash);
        slice[1] = Integer.parseInt(token.substring(dash, nextDash));
        dash = nextDash + 1;
        slice[0] = Integer.parseInt(token.substring(dash,
          token.indexOf("\"", dash)));

        token = st.nextToken().trim();
        String key = "", value = "";
        while (!token.startsWith("}")) {
          int colon = token.indexOf(":");
          key = token.substring(1, colon).trim();
          value = token.substring(colon + 1, token.length() - 1).trim();

          key = key.replaceAll("\"", "");
          value = value.replaceAll("\"", "");

          addMeta(key, value);

          if (key.equals("Exposure-ms")) {
            float t = Float.parseFloat(value);
            exposureTime = new Float(t / 1000);
          }
          else if (key.equals("ElapsedTime-ms")) {
            float t = Float.parseFloat(value);
            stamps.add(new Float(t / 1000));
          }
          else if (key.equals("Core-Camera")) cameraRef = value;
          else if (key.equals(cameraRef + "-Binning")) {
            binning = value;
          }
          else if (key.equals(cameraRef + "-CameraID")) detectorID = value;
          else if (key.equals(cameraRef + "-CameraName")) detectorModel = value;
          else if (key.equals(cameraRef + "-Gain")) {
            gain = Integer.parseInt(value);
          }
          else if (key.equals(cameraRef + "-Name")) {
            detectorManufacturer = value;
          }
          else if (key.equals(cameraRef + "-Temperature")) {
            temperature = Float.parseFloat(value);
          }
          else if (key.equals(cameraRef + "-CCDMode")) {
            cameraMode = value;
          }
          else if (key.startsWith("DAC-") && key.endsWith("-Volts")) {
            voltage.add(new Float(value));
          }

          token = st.nextToken().trim();
        }
      }
    }

    timestamps = (Float[]) stamps.toArray(new Float[0]);
    Arrays.sort(timestamps);

    // build list of TIFF files

    String prefix = "";
    if (baseTiff.indexOf(File.separator) != -1) {
      prefix = baseTiff.substring(0, baseTiff.lastIndexOf(File.separator) + 1);
      baseTiff = baseTiff.substring(baseTiff.lastIndexOf(File.separator) + 1);
    }

    String[] blocks = baseTiff.split("_");
    StringBuffer filename = new StringBuffer();
    for (int t=0; t<getSizeT(); t++) {
      for (int c=0; c<getSizeC(); c++) {
        for (int z=0; z<getSizeZ(); z++) {
          // file names are of format:
          // img_<T>_<channel name>_<T>.tif
          filename.append(prefix);
          filename.append(blocks[0]);
          filename.append("_");

          int zeros = blocks[1].length() - String.valueOf(t).length();
          for (int q=0; q<zeros; q++) {
            filename.append("0");
          }
          filename.append(t);
          filename.append("_");

          filename.append(channels[c]);
          filename.append("_");

          zeros = blocks[3].length() - String.valueOf(z).length() - 4;
          for (int q=0; q<zeros; q++) {
            filename.append("0");
          }
          filename.append(z);
          filename.append(".tif");

          tiffs.add(filename.toString());
          filename.delete(0, filename.length());
        }
      }
    }

    tiffReader.setId((String) tiffs.get(0));

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = tiffs.size() / getSizeC();

    core[0].sizeX = tiffReader.getSizeX();
    core[0].sizeY = tiffReader.getSizeY();
    core[0].dimensionOrder = "XYZCT";
    core[0].pixelType = tiffReader.getPixelType();
    core[0].rgb = tiffReader.isRGB();
    core[0].interleaved = false;
    core[0].littleEndian = tiffReader.isLittleEndian();
    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);
    store.setImageName("", 0);
    store.setImageDescription(comment, 0);
    if (time != null) {
      SimpleDateFormat parser =
        new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
      try {
        long stamp = parser.parse(time).getTime();
        store.setImageCreationDate(
          DataTools.convertDate(stamp, DataTools.UNIX), 0);
      }
      catch (ParseException e) {
        MetadataTools.setDefaultCreationDate(store, id, 0);
      }
    }
    else MetadataTools.setDefaultCreationDate(store, id, 0);

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", 0);
    store.setImageInstrumentRef("Instrument:0", 0);

    for (int i=0; i<channels.length; i++) {
      store.setLogicalChannelName(channels[i], 0, i);
    }

    store.setDimensionsPhysicalSizeX(pixelSize, 0, 0);
    store.setDimensionsPhysicalSizeY(pixelSize, 0, 0);
    store.setDimensionsPhysicalSizeZ(sliceThickness, 0, 0);

    for (int i=0; i<getImageCount(); i++) {
      store.setPlaneTimingExposureTime(exposureTime, 0, 0, i);
      if (i < timestamps.length) {
        store.setPlaneTimingDeltaT(timestamps[i], 0, 0, i);
      }
    }

    for (int i=0; i<channels.length; i++) {
      store.setDetectorSettingsBinning(binning, 0, i);
      store.setDetectorSettingsGain(new Float(gain), 0, i);
      if (i < voltage.size()) {
        store.setDetectorSettingsVoltage((Float) voltage.get(i), 0, i);
      }
      store.setDetectorSettingsDetector(detectorID, 0, i);
    }

    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorModel(detectorModel, 0, 0);
    store.setDetectorManufacturer(detectorManufacturer, 0, 0);
    store.setDetectorType(cameraMode, 0, 0);

    store.setImagingEnvironmentTemperature(new Float(temperature), 0);
  }

}
