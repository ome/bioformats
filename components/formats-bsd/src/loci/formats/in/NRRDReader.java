/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.ClassList;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * File format reader for NRRD files; see http://teem.sourceforge.net/nrrd.
 */
public class NRRDReader extends FormatReader {

  // -- Constants --

  public static final String NRRD_MAGIC_STRING = "NRRD";

  // -- Fields --

  /** Helper reader. */
  private ImageReader helper;

  /** Name of data file, if the current extension is 'nhdr'. */
  private String dataFile;

  /** Data encoding. */
  private String encoding;

  /** Offset to pixel data. */
  private long offset;

  private String[] pixelSizes;

  private boolean lookForCompanion = true;
  private boolean initializeHelper = false;

  // -- Constructor --

  /** Constructs a new NRRD reader. */
  public NRRDReader() {
    super("NRRD", new String[] {"nrrd", "nhdr"});
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "A single .nrrd file or one .nhdr file and one " +
      "other file containing the pixels";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return checkSuffix(id, "nrrd");
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (super.isThisType(name, open)) return true;
    if (!open) return false;

    // look for a matching .nhdr file
    Location header = new Location(name + ".nhdr");
    if (header.exists()) {
      return true;
    }

    if (name.indexOf(".") >= 0) {
      name = name.substring(0, name.lastIndexOf("."));
    }

    header = new Location(name + ".nhdr");
    return header.exists();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = NRRD_MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).startsWith(NRRD_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      if (dataFile == null) return null;
      return new String[] {currentId};
    }
    if (dataFile == null) return new String[] {currentId};
    return new String[] {currentId, dataFile};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (initializeHelper && dataFile != null && helper.getCurrentFile() == null) {
      helper.setId(dataFile);
    }

    // TODO : add support for additional encoding types
    if (dataFile == null) {
      if (encoding.equals("raw")) {
        long planeSize = FormatTools.getPlaneSize(this);
        in.seek(offset + no * planeSize);

        readPlane(in, x, y, w, h, buf);
        return buf;
      }
      else {
        throw new UnsupportedCompressionException(
          "Unsupported encoding: " + encoding);
      }
    }
    else if (encoding.equals("raw")) {
      RandomAccessInputStream s = new RandomAccessInputStream(dataFile);
      s.seek(offset + no * FormatTools.getPlaneSize(this));
      readPlane(s, x, y, w, h, buf);
      s.close();
      return buf;
    }

    return helper.openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (helper != null) helper.close(fileOnly);
    if (!fileOnly) {
      helper = null;
      dataFile = encoding = null;
      offset = 0;
      pixelSizes = null;
      initializeHelper = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // make sure we actually have the .nrrd/.nhdr file
    if (!checkSuffix(id, "nhdr") && !checkSuffix(id, "nrrd")) {
      id += ".nhdr";

      if (!new Location(id).exists()) {
        id = id.substring(0, id.lastIndexOf("."));
        id = id.substring(0, id.lastIndexOf("."));
        id += ".nhdr";
      }
      id = new Location(id).getAbsolutePath();
    }

    super.initFile(id);

    in = new RandomAccessInputStream(id);

    ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
    Class<? extends IFormatReader>[] classArray = classes.getClasses();
    ClassList<IFormatReader> newClasses =
      new ClassList<IFormatReader>(IFormatReader.class);
    for (Class<? extends IFormatReader> c : classArray) {
      if (!c.equals(NRRDReader.class)) {
        newClasses.addClass(c);
      }
    }
    helper = new ImageReader(newClasses);
    helper.setMetadataOptions(
      new DefaultMetadataOptions(MetadataLevel.MINIMUM));

    String key, v;

    int numDimensions = 0;

    CoreMetadata m = core.get(0);

    m.sizeX = 1;
    m.sizeY = 1;
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.dimensionOrder = "XYCZT";

    String line = in.readLine();
    while (line != null && line.length() > 0) {
      if (!line.startsWith("#") && !line.startsWith("NRRD")) {
        // parse key/value pair
        key = line.substring(0, line.indexOf(":")).trim();
        v = line.substring(line.indexOf(":") + 1).trim();
        addGlobalMeta(key, v);

        if (key.equals("type")) {
          if (v.indexOf("char") != -1 || v.indexOf("8") != -1) {
            m.pixelType = FormatTools.UINT8;
          }
          else if (v.indexOf("short") != -1 || v.indexOf("16") != -1) {
            m.pixelType = FormatTools.UINT16;
          }
          else if (v.equals("int") || v.equals("signed int") ||
            v.equals("int32") || v.equals("int32_t") || v.equals("uint") ||
            v.equals("unsigned int") || v.equals("uint32") ||
            v.equals("uint32_t"))
          {
            m.pixelType = FormatTools.UINT32;
          }
          else if (v.equals("float")) m.pixelType = FormatTools.FLOAT;
          else if (v.equals("double")) m.pixelType = FormatTools.DOUBLE;
          else throw new FormatException("Unsupported data type: " + v);
        }
        else if (key.equals("dimension")) {
          numDimensions = Integer.parseInt(v);
        }
        else if (key.equals("sizes")) {
          String[] tokens = v.split(" ");
          for (int i=0; i<numDimensions; i++) {
            int size = Integer.parseInt(tokens[i]);

            if (numDimensions >= 3 && i == 0 && size > 1 && size <= 16) {
              m.sizeC = size;
            }
            else if (i == 0 || (getSizeC() > 1 && i == 1)) {
              m.sizeX = size;
            }
            else if (i == 1 || (getSizeC() > 1 && i == 2)) {
              m.sizeY = size;
            }
            else if (i == 2 || (getSizeC() > 1 && i == 3)) {
              m.sizeZ = size;
            }
            else if (i == 3 || (getSizeC() > 1 && i == 4)) {
              m.sizeT = size;
            }
          }
        }
        else if (key.equals("data file") || key.equals("datafile")) {
          dataFile = v;
        }
        else if (key.equals("encoding")) encoding = v;
        else if (key.equals("endian")) {
          m.littleEndian = v.equals("little");
        }
        else if (key.equals("spacings")) {
          pixelSizes = v.split(" ");
        }
        else if (key.equals("byte skip")) {
          offset = Long.parseLong(v);
        }
      }

      line = in.readLine();
      if (line != null) line = line.trim();
    }

    // nrrd files store pixel data in addition to metadata
    // nhdr files don't store pixel data, but instead provide a path to the
    //   pixels file (this can be any format)

    if (dataFile == null) offset = in.getFilePointer();
    else {
      Location f = new Location(currentId).getAbsoluteFile();
      Location parent = f.getParentFile();
      if (f.exists() && parent != null) {
        dataFile = dataFile.substring(dataFile.indexOf(File.separator) + 1);
        dataFile = new Location(parent, dataFile).getAbsolutePath();
      }
      initializeHelper = !encoding.equals("raw");
    }

    m.rgb = getSizeC() > 1;
    m.interleaved = true;
    m.imageCount = getSizeZ() * getSizeT();
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (pixelSizes != null) {
        for (int i=0; i<pixelSizes.length; i++) {
          if (pixelSizes[i] == null) continue;
          try {
            Double d = new Double(pixelSizes[i].trim());
            if (i == 0) {
              Length x = FormatTools.getPhysicalSizeX(d);
              if (x != null) {
                store.setPixelsPhysicalSizeX(x, 0);
              }
            }
            else if (i == 1) {
              Length y = FormatTools.getPhysicalSizeY(d);
              if (y != null) {
                store.setPixelsPhysicalSizeY(y, 0);
              }
            }
            else if (i == 2) {
              Length z = FormatTools.getPhysicalSizeZ(d);
              if (z != null) {
                store.setPixelsPhysicalSizeZ(z, 0);
              }
            }
          }
          catch (NumberFormatException e) { }
        }
      }
    }
  }

}
