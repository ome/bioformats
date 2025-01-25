/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import loci.common.Location;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataStore;
import loci.formats.pyramidio.DeepZoomImageReader;
import ome.units.UNITS;
import ome.units.quantity.Length;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Reader for PreciPoint .vmic WSI files
 *
 * @author Kai Wiechen kai.wiechen at pathologie-worms.de
 */

public class VmicReader extends SubResolutionFormatReader {
  private static final String INNER_CONTAINER = "Image.vmici";
  private static final String EXTENDED_METADATA = "VMCF/config.osc";
  private DeepZoomImageReader reader;
  private FileSystem inner_zipfs;

  // -- Constructor --

  public VmicReader() {
    super("vmic", new String[] {"vmic"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = true;
    suffixSufficient = true;
  }

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (isThisType && open) {
      try (ZipFile outerZipFile = new ZipFile(name)) {
        ZipEntry innerZipEntry = outerZipFile.getEntry(INNER_CONTAINER);
        try (InputStream innerZip = outerZipFile.getInputStream(innerZipEntry)) {
          // check inner zip file magic numbers 0x50 0x4B
          byte[] bytes = new byte[2];
          innerZip.read(bytes, 0, 2);
          if( bytes[0] == 0x50 && bytes[1] == 0x4B) return true;
          else return false;
        }
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        return false;
      }
    }
    return isThisType;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    Rectangle rect = new Rectangle(x, y, w, h);
    BufferedImage image = reader.readRegionOfLevel(rect, reader.getMaxLevel() - resolution);

    byte[] t = AWTImageTools.getBytes(image, false);
    System.arraycopy(t, 0, buf, 0, (int) Math.min(t.length, buf.length));

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (reader != null) {
      inner_zipfs = null;
      reader = null;
    }
  }

  /* @see IFormatReader#getResolutionCount() */
  @Override
  public int getResolutionCount() {
    FormatTools.assertId(currentId, true, 1);
    return core.get(0, 0).resolutionCount;
  }

  /* @see IFormatReader#setResolution(int) */
  @Override
  public void setResolution(int no) {
    if (no < 0 || no >= getResolutionCount()) {
      throw new IllegalArgumentException("Invalid resolution: " + no);
    }

    if (!hasFlattenedResolutions()) {
      resolution = no;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    return reader.getTileSize();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    return reader.getTileSize();
  }

  /* @see loci.formats.FormatReader#getThumbSizeX() */
  @Override
  public int getThumbSizeX() {
    return core.get(0, 0).thumbSizeX;
  }

  /* @see loci.formats.FormatReader#getThumbSizeY() */
  @Override
  public int getThumbSizeY() {
    return core.get(0, 0).thumbSizeY;
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  public void initFile(String id) throws FormatException, IOException {
    setFlattenedResolutions(false);
    super.initFile(id);

    Path outer_zip = Path.of(Location.getMappedId(id));

    try (FileSystem fs = FileSystems.newFileSystem(outer_zip)) {
      Path inner_zip_path = fs.getPath(INNER_CONTAINER);
      inner_zipfs = FileSystems.newFileSystem(inner_zip_path);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    reader = new DeepZoomImageReader(inner_zipfs);

    CoreMetadata m0 = core.get(0, 0);

    m0.interleaved = false;
    m0.littleEndian = false;
    m0.sizeX = reader.getWidth();
    m0.sizeY = reader.getHeight();
    m0.sizeZ = 1;
    m0.sizeT = 1;
    m0.sizeC = 3;
    m0.rgb = getSizeC() > 1;
    m0.imageCount = 1;
    m0.pixelType = FormatTools.UINT8;
    m0.dimensionOrder = "XYCZT";
    m0.metadataComplete = true;
    m0.indexed = false;

    int maxResolutionLevels = reader.getMaxLevel() - 1;

    m0.resolutionCount = 1;

    // deepzoom pyramid subresolutions
    for (int i = maxResolutionLevels; i >= 0; i--) {
      CoreMetadata ms = new CoreMetadata(this, 0);
      core.add(0, ms);
      ms.sizeX = (int) Math.round(reader.getWidth() * reader.getZoomOfLevel(i));
      ms.sizeY = (int) Math.round(reader.getHeight() * reader.getZoomOfLevel(i));
      ms.sizeT = m0.sizeT;
      ms.imageCount = m0.imageCount;
      ms.thumbnail = true;
      ms.resolutionCount = 1;

      m0.resolutionCount += 1;

      List<File> list_of_files = reader.getFilesOfLevel(i);
      if (list_of_files.size() == 1) {
        m0.thumbSizeX = ms.sizeX;;
        m0.thumbSizeY = ms.sizeY;
        break;
      }
    }

    initExtendedMetadata();
  }

  private void initExtendedMetadata() throws IOException {
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    HashMap<String, String> metaDataMap = new HashMap<>();
    metaDataMap.put("ShortName", null);
    metaDataMap.put("Magnification", null);
    metaDataMap.put("PixelPerMicron", null);

    try {
      Path entry = inner_zipfs.getPath(EXTENDED_METADATA);
      InputStream is = Files.newInputStream(entry);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = factory.newDocumentBuilder();
      Document doc = db.parse(is);
      Element imageNode = doc.getDocumentElement();
      if (! imageNode.getNodeName().contains("ObjectScanConfig")) {
        throw new IOException("Unsupported config.osc file.");
      }

      NodeList outerChildNodes = imageNode.getChildNodes();
      int outerLength = outerChildNodes.getLength();

      for (int i = 0; i < outerLength; i++) {
        Node outerNode = outerChildNodes.item(i);
        if (outerNode.getNodeName().contains("Objective") || outerNode.getNodeName().contains("CombinedOpticalConfig")) {
          NodeList innerChildNodes = outerNode.getChildNodes();
          int innerLength = innerChildNodes.getLength();

          for (int j = 0; j < innerLength; j++) {
            Node innerNode = innerChildNodes.item(j);

            String nn = innerNode.getNodeName().replace("ObjectScanConfig:", "");
            if (metaDataMap.containsKey(nn)) {
              metaDataMap.put(nn, innerNode.getTextContent());
            }
          }
        }
      }

      Length pixelsize = FormatTools.createLength(1 / Double.parseDouble(metaDataMap.get("PixelPerMicron")), UNITS.MICROMETER);
      store.setPixelsPhysicalSizeX(pixelsize, 0);
      store.setPixelsPhysicalSizeY(pixelsize, 0);
      // IDs must not contain white spaces ??
      store.setInstrumentID("PreciPoint", 0);
      store.setObjectiveSettingsID(metaDataMap.get("ShortName"), 0);
      store.setObjectiveID(metaDataMap.get("ShortName"), 0, 0);
      store.setObjectiveNominalMagnification(Double.parseDouble(metaDataMap.get("Magnification")), 0, 0);

      is.close();

    } catch (IOException | ParserConfigurationException e) {
        throw new IOException(e);
    } catch (SAXException e) {
        throw new RuntimeException(e);
    }
  }
}
