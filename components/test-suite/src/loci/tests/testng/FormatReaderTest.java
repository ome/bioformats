/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package loci.tests.testng;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.Memoizer;
import loci.formats.ReaderWrapper;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.in.*;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Quantity;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * TestNG tester for Bio-Formats file format readers.
 * Details on failed tests are written to a log file, for easier processing.
 *
 * NB: {@link loci.formats.ome} and ome-xml.jar
 * are required for some of the tests.
 *
 * To run tests:
 * ant -Dtestng.directory="/path" -Dtestng.multiplier="1.0" test-all
 */
public class FormatReaderTest {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(FormatReaderTest.class);

  /** Message to give for why a test was skipped. */
  private static final String SKIP_MESSAGE = "Dataset already tested.";

  // -- Static fields --

  /** Configuration tree structure containing dataset metadata. */
  public static ConfigurationTree configTree;

  /** List of files to skip. */
  private static List<String> skipFiles = new LinkedList<String>();

  /** Global shared jeader for use in all tests. */
  private BufferedImageReader reader;

  // -- Fields --

  private String id;
  private boolean skip = false;
  private Configuration config;
  private String omexmlDir = System.getProperty("testng.omexmlDirectory");
  private String cacheDir  = System.getProperty("testng.cacheDirectory");
  private String fileList = System.getProperty("testng.file-list");

  /**
   * Multiplier for use adjusting timing values. Slower machines take longer to
   * complete the timing test, and thus need to set a higher (&gt;1) multiplier
   * to avoid triggering false timing test failures. Conversely, faster
   * machines should set a lower (&lt;1) multipler to ensure things finish as
   * quickly as expected.
   */
  private float timeMultiplier = 1;

  private boolean inMemory = false;

  private OMEXMLService omexmlService = null;

  // -- Constructor --

  public FormatReaderTest(String filename, float multiplier, boolean inMemory) {
    id = filename;
    timeMultiplier = multiplier;
    this.inMemory = inMemory;
    try {
      ServiceFactory factory = new ServiceFactory();
      omexmlService = factory.getInstance(OMEXMLService.class);
    }
    catch (DependencyException e) {
      LOGGER.warn("OMEXMLService not available", e);
    }
  }

  public String getID() {
    return id;
  }

  // -- Setup/teardown methods --

  @BeforeClass
  public void setup() throws IOException {
    initFile();
  }

  @AfterClass
  public void close() throws IOException {
    reader.close();
    HashMap<String, Object> idMap = Location.getIdMap();
    idMap.clear();
    Location.setIdMap(idMap);
  }

  // -- Tests --

  @Test(groups = {"all", "pixels", "automated"})
  public void testBufferedImageDimensions() {
    String testName = "testBufferedImageDimensions";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      BufferedImage b = null;
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);

        int x = reader.getSizeX();
        int y = reader.getSizeY();
        int c = reader.getRGBChannelCount();
        int type = reader.getPixelType();
        int bytes = FormatTools.getBytesPerPixel(type);

        int plane = x * y * c * bytes;
        long checkPlane = (long) x * y * c * bytes;

        // account for the fact that most histology (big image) files
        // require more memory for decoding/re-encoding BufferedImages
        if (DataTools.indexOf(reader.getDomains(),
          FormatTools.HISTOLOGY_DOMAIN) >= 0)
        {
          plane *= 2;
          checkPlane *= 2;
        }

        if (c > 4 || plane < 0 || plane != checkPlane ||
          !TestTools.canFitInMemory(plane * 3))
        {
          continue;
        }

        int num = reader.getImageCount();
        if (num > 3) num = 3; // test first three image planes only, for speed
        for (int j=0; j<num && success; j++) {
          b = reader.openImage(j);

          int actualX = b.getWidth();
          boolean passX = x == actualX;
          if (!passX) msg = "X: was " + actualX + ", expected " + x;

          int actualY = b.getHeight();
          boolean passY = y == actualY;
          if (!passY) msg = "Y: was " + actualY + ", expected " + y;

          int actualC = b.getRaster().getNumBands();
          boolean passC = c == actualC;
          if (!passC) msg = "C: was " + actualC + ", expected " + c;

          int actualType = AWTImageTools.getPixelType(b);
          boolean passType = type == actualType;
          if (!passType && actualType == FormatTools.UINT16 &&
            type == FormatTools.INT16)
          {
            passType = true;
          }

          if (!passType) msg = "type: was " + actualType + ", expected " + type;

          success = passX && passY && passC && passType;
        }
      }
    }
    catch (Throwable t) {
      if (TestTools.isOutOfMemory(t)) {
        result(testName, true, "Image too large");
        return;
      }
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "pixels", "automated"})
  public void testByteArrayDimensions() {
    String testName = "testByteArrayDimensions";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      byte[] b = null;
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        int x = reader.getSizeX();
        int y = reader.getSizeY();
        int c = reader.getRGBChannelCount();
        int bytes = FormatTools.getBytesPerPixel(reader.getPixelType());

        int expected = -1;
        try {
          expected = DataTools.safeMultiply32(x, y, c, bytes);
        }
        catch (IllegalArgumentException e) {
          continue;
        }

        if (!TestTools.canFitInMemory(expected * 3) || expected < 0) {
          continue;
        }

        int num = reader.getImageCount();
        if (num > 3) num = 3; // test first three planes only, for speed
        for (int j=0; j<num && success; j++) {
          b = reader.openBytes(j);
          success = b.length == expected;
          if (!success) {
            msg = "series #" + i + ", image #" + j +
              ": was " + b.length + ", expected " + expected;
          }
        }
      }
    }
    catch (Throwable t) {
      if (TestTools.isOutOfMemory(t)) {
        result(testName, true, "Image too large");
        return;
      }
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "pixels", "automated"})
  public void testThumbnailImageDimensions() {
    String testName = "testThumbnailImageDimensions";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      int seriesCount = reader.getSeriesCount();
      if (DataTools.indexOf(reader.getDomains(), FormatTools.HCS_DOMAIN) >= 0) {
        seriesCount = 1;
      }
      for (int i=0; i<seriesCount && success; i++) {
        reader.setSeries(i);

        int x = reader.getThumbSizeX();
        int y = reader.getThumbSizeY();
        int c = reader.getRGBChannelCount();
        int type = reader.getPixelType();
        int bytes = FormatTools.getBytesPerPixel(type);

        int fx = reader.getSizeX();
        int fy = reader.getSizeY();

        if (c > 4 || type == FormatTools.FLOAT || type == FormatTools.DOUBLE ||
          !TestTools.canFitInMemory((long) fx * fy * c * bytes))
        {
          continue;
        }

        BufferedImage b = null;
        try {
          b = reader.openThumbImage(0);
        }
        catch (Throwable e) {
          if (TestTools.isOutOfMemory(e)) {
            result(testName, true, "Image too large");
            return;
          }
          throw e;
        }

        int actualX = b.getWidth();
        boolean passX = x == actualX;
        if (!passX) {
          msg = "series #" + i + ": X: was " + actualX + ", expected " + x;
        }

        int actualY = b.getHeight();
        boolean passY = y == actualY;
        if (!passY) {
          msg = "series #" + i + ": Y: was " + actualY + ", expected " + y;
        }

        int actualC = b.getRaster().getNumBands();
        boolean passC = c == actualC;
        if (!passC) {
          msg = "series #" + i + ": C: was " + actualC + ", expected < " + c;
        }

        int actualType = AWTImageTools.getPixelType(b);
        boolean passType = type == actualType;
        if (!passType && actualType == FormatTools.UINT16 &&
          type == FormatTools.INT16)
        {
          passType = true;
        }

        if (!passType) {
          msg = "series #" + i + ": type: was " +
            actualType + ", expected " + type;
        }

        success = passX && passY && passC && passType;
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "pixels", "automated"})
  public void testThumbnailByteArrayDimensions() {
    String testName = "testThumbnailByteArrayDimensions";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        int x = reader.getThumbSizeX();
        int y = reader.getThumbSizeY();
        int c = reader.getRGBChannelCount();
        int type = reader.getPixelType();
        int bytes = FormatTools.getBytesPerPixel(type);
        int expected = x * y * c * bytes;

        int fx = reader.getSizeX();
        int fy = reader.getSizeY();

        if (c > 4 || type == FormatTools.FLOAT || type == FormatTools.DOUBLE ||
          !TestTools.canFitInMemory((long) fx * fy * c * bytes * 20))
        {
          continue;
        }

        byte[] b = null;
        try {
          b = reader.openThumbBytes(0);
        }
        catch (Throwable e) {
          if (TestTools.isOutOfMemory(e)) {
            result(testName, true, "Image too large");
            return;
          }
          throw e;
        }
        success = b.length == expected;
        if (!success) {
          msg = "series #" + i + ": was " + b.length + ", expected " + expected;
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testImageCount() {
    String testName = "testImageCount";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        int imageCount = reader.getImageCount();
        int z = reader.getSizeZ();
        int c = reader.getEffectiveSizeC();
        int t = reader.getSizeT();
        success = imageCount == z * c * t;
        msg = "series #" + i + ": imageCount=" + imageCount +
          ", z=" + z + ", c=" + c + ", t=" + t;
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testTileWidth() {
    String testName = "testTileWidth";
    if (!initFile()) result(testName, false, "initFile");

    boolean success = true;
    String msg = null;
    try {
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        int width = reader.getOptimalTileWidth();
        success = width > 0;
        msg = "series #" + i + ": tile width = " + width;
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testTileHeight() {
    String testName = "testTileHeight";
    if (!initFile()) result(testName, false, "initFile");

    boolean success = true;
    String msg = null;
    try {
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        int height = reader.getOptimalTileHeight();
        success = height > 0;
        msg = "series #" + i + ": tile height = " + height;
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "xml", "fast", "automated"})
  public void testOMEXML() {
    String testName = "testOMEXML";
    if (!initFile()) result(testName, false, "initFile");
    String msg = null;
    try {
      MetadataRetrieve retrieve = (MetadataRetrieve) reader.getMetadataStore();
      boolean success = omexmlService.isOMEXMLMetadata(retrieve);
      if (!success) msg = TestTools.shortClassName(retrieve);

      for (int i=0; i<reader.getSeriesCount() && msg == null; i++) {
        reader.setSeries(i);

        String type = FormatTools.getPixelTypeString(reader.getPixelType());

        if (reader.getSizeX() !=
          retrieve.getPixelsSizeX(i).getValue().intValue())
        {
          msg = "SizeX";
        }
        if (reader.getSizeY() !=
          retrieve.getPixelsSizeY(i).getValue().intValue())
        {
          msg = "SizeY";
        }
        if (reader.getSizeZ() !=
          retrieve.getPixelsSizeZ(i).getValue().intValue())
        {
          msg = "SizeZ";
        }
        if (reader.getSizeC() !=
          retrieve.getPixelsSizeC(i).getValue().intValue())
        {
          msg = "SizeC";
        }
        if (reader.getSizeT() !=
          retrieve.getPixelsSizeT(i).getValue().intValue())
        {
          msg = "SizeT";
        }

        // NB: OME-TIFF files do not have a BinData element under Pixels
        IFormatReader r = reader.unwrap();
        if (r instanceof FileStitcher) r = ((FileStitcher) r).getReader();
        if (r instanceof ReaderWrapper) r = ((ReaderWrapper) r).unwrap();
        if (!(r instanceof OMETiffReader)) {
          boolean littleEndian = false;
          if (retrieve.getPixelsBigEndian(i) != null)
          {
            littleEndian = !retrieve.getPixelsBigEndian(i).booleanValue();
          }
          else if (retrieve.getPixelsBinDataCount(i) == 0) {
            littleEndian = !retrieve.getPixelsBinDataBigEndian(i, 0).booleanValue();
          }
          if (reader.isLittleEndian() != littleEndian)
          {
            msg = "BigEndian";
          }
        }
        if (!reader.getDimensionOrder().equals(
          retrieve.getPixelsDimensionOrder(i).toString()))
        {
          msg = "DimensionOrder";
        }
        if (!type.equalsIgnoreCase(retrieve.getPixelsType(i).toString())) {
          msg = "PixelType";
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      msg = t.getMessage();
    }
    result(testName, msg == null, msg);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testConsistentReader() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testConsistentReader";
    if (!initFile()) result(testName, false, "initFile");

    String format = config.getReader();

    IFormatReader r = reader;
    if (r instanceof ImageReader) {
      r = ((ImageReader) r).getReader();
    }
    else if (r instanceof ReaderWrapper) {
      try {
        r = ((ReaderWrapper) r).unwrap();
      }
      catch (FormatException e) { }
      catch (IOException e) { }
    }

    String realFormat = TestTools.shortClassName(r);

    result(testName, realFormat.equals(format), realFormat);
  }

  @Test(groups = {"all", "xml", "automated"})
  public void testSaneOMEXML() {
    String testName = "testSaneOMEXML";
    if (!initFile()) result(testName, false, "initFile");
    if (!config.hasValidXML()) {
      LOGGER.debug("Skipping valid XML test");
      result(testName, true);
      return;
    }
    String msg = null;
    try {

      MetadataRetrieve retrieve = (MetadataRetrieve) reader.getMetadataStore();
      boolean success = omexmlService.isOMEXMLMetadata(retrieve);
      if (!success) msg = TestTools.shortClassName(retrieve);

      for (int i=0; i<reader.getSeriesCount() && msg == null; i++) {
        // total number of ChannelComponents should match SizeC
        int sizeC = retrieve.getPixelsSizeC(i).getValue().intValue();
        int nChannelComponents = retrieve.getChannelCount(i);
        int samplesPerPixel =
          retrieve.getChannelSamplesPerPixel(i, 0).getValue();

        if (sizeC != nChannelComponents * samplesPerPixel) {
          msg = "ChannelComponent";
        }

        // Z, C and T indices should be populated if PlaneTiming is present

        Time deltaT = null;
        Time exposure = null;
        Integer z = null, c = null, t = null;

        if (retrieve.getPlaneCount(i) > 0) {
          deltaT = retrieve.getPlaneDeltaT(i, 0);
          exposure = retrieve.getPlaneExposureTime(i, 0);
          z = retrieve.getPlaneTheZ(i, 0).getValue();
          c = retrieve.getPlaneTheC(i, 0).getValue();
          t = retrieve.getPlaneTheT(i, 0).getValue();
        }

        if ((deltaT != null || exposure != null) &&
          (z == null || c == null || t == null))
        {
          msg = "PlaneTiming";
        }

        // if CreationDate is before 1990, it's probably invalid
        String date = null;
        if (retrieve.getImageAcquisitionDate(i) != null) {
          date = retrieve.getImageAcquisitionDate(i).getValue();
        }
        String configDate = config.getDate();
        if (date != null && !date.equals(configDate)) {
          date = date.trim();
          long acquiredDate = new Timestamp(date).asInstant().getMillis();
          long saneDate = new Timestamp("1990-01-01T00:00:00").asInstant().getMillis();
          long fileDate = new Location(
            reader.getCurrentFile()).getAbsoluteFile().lastModified();
          if (acquiredDate < saneDate && fileDate >= saneDate) {
            msg = "CreationDate (date=" + date + " acquiredDate=" + acquiredDate + " fileDate=" + fileDate + " saneDate=" + saneDate + ")";
          }
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      msg = t.getMessage();
    }
    result(testName, msg == null, msg);
  }

  // -- Consistency tests --

  @Test(groups = {"all", "fast", "automated"})
  public void testSeriesCount() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SeriesCount";
    if (!initFile()) result(testName, false, "initFile");

    result(testName, reader.getSeriesCount() == config.getSeriesCount());
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testSizeX() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeX";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeX() != config.getSizeX()) {
        result(testName, false, "Series " + i + " (expected " + config.getSizeX() + ", actual " + reader.getSizeX() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testSizeY() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeY";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeY() != config.getSizeY()) {
        result(testName, false, "Series " + i + " (expected " + config.getSizeY() + ", actual " + reader.getSizeY() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testSizeZ() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeZ";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeZ() != config.getSizeZ()) {
        result(testName, false, "Series " + i + " (expected " + config.getSizeZ() + ", actual " + reader.getSizeZ() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testSizeC() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeC";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeC() != config.getSizeC()) {
        result(testName, false, "Series " + i + " (expected " + config.getSizeC() + ", actual " + reader.getSizeC() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testSizeT() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeT";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeT() != config.getSizeT()) {
        result(testName, false, "Series " + i + " (expected " + config.getSizeT() + ", actual " + reader.getSizeT() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testDimensionOrder() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "DimensionOrder";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      String realOrder = reader.getDimensionOrder();
      String expectedOrder = config.getDimensionOrder();

      if (!realOrder.equals(expectedOrder)) {
        result(testName, false, "Series " + i + " (got " + realOrder +
          ", expected " + expectedOrder + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testIsInterleaved() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "Interleaved";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isInterleaved() != config.isInterleaved()) {
        result(testName, false, "Series " + i + " (expected " + config.isInterleaved() + ", actual " + reader.isInterleaved() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testIndexed() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "Indexed";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isIndexed() != config.isIndexed()) {
        result(testName, false, "Series " + i + " (expected " + config.isIndexed() + ", actual " + reader.isIndexed() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testFalseColor() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "FalseColor";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isFalseColor() != config.isFalseColor()) {
        result(testName, false, "Series " + i + " (expected " + config.isFalseColor() + ", actual " + reader.isFalseColor() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testRGB() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "RGB";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isRGB() != config.isRGB()) {
        result(testName, false, "Series " + i + " (expected " + config.isRGB() + ", actual " + reader.isRGB() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testThumbSizeX() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ThumbSizeX";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getThumbSizeX() != config.getThumbSizeX()) {
        result(testName, false, "Series " + i + " (expected " + config.getThumbSizeX() + ", actual " + reader.getThumbSizeX() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testThumbSizeY() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ThumbSizeY";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getThumbSizeY() != config.getThumbSizeY()) {
        result(testName, false, "Series " + i + " (expected " + config.getThumbSizeY() + ", actual " + reader.getThumbSizeY() + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testPixelType() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PixelType";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getPixelType() !=
        FormatTools.pixelTypeFromString(config.getPixelType()))
      {
        result(testName, false, "Series " + i + " (expected " +
               config.getPixelType() + ", actual " +
               FormatTools.getPixelTypeString(reader.getPixelType()) + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testLittleEndian() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "LittleEndian";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isLittleEndian() != config.isLittleEndian()) {
        result(testName, false, "Series " + i + " (expected " + config.isLittleEndian() + ", actual " + reader.isLittleEndian() + ")");
      }
    }
    result(testName, true);
  }

  private boolean isAlmostEqual(Quantity q1, Quantity q2) {

    if (q1 == null && q2 == null) {
      return true;
    } else if (q1 == null || q2 == null) {
      return false;
    } else if (q1.unit() != q2.unit()) {
      return false;
    } else if (Math.abs(q1.value().doubleValue() - q2.value().doubleValue()) > Constants.EPSILON) {

      return false;
    } else {
      return true;
    }
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testPhysicalSizeX() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PhysicalSizeX";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      Length expectedSize = config.getPhysicalSizeX();
      Length realSize = retrieve.getPixelsPhysicalSizeX(i);
      
      if (!isAlmostEqual(realSize,expectedSize))
      {
        result(testName, false, "Series " + i + " (expected " + expectedSize + ", actual " + realSize + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testPhysicalSizeY() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PhysicalSizeY";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);
      Length expectedSize = config.getPhysicalSizeY();
      Length realSize = retrieve.getPixelsPhysicalSizeY(i);
      
      if (!isAlmostEqual(realSize,expectedSize))
      {
        result(testName, false, "Series " + i + " (expected " + expectedSize + ", actual " + realSize + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testPhysicalSizeZ() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PhysicalSizeZ";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      Length expectedSize = config.getPhysicalSizeZ();
      Length realSize = retrieve.getPixelsPhysicalSizeZ(i);
      if (!isAlmostEqual(realSize,expectedSize))
      {
        result(testName, false, "Series " + i + " (expected " + expectedSize + ", actual " + realSize + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testTimeIncrement() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "TimeIncrement";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      Time expectedIncrement = config.getTimeIncrement();
      Time realIncrement = retrieve.getPixelsTimeIncrement(i);

      if (!isAlmostEqual(expectedIncrement,realIncrement))
      {
        result(testName, false, "Series " + i + " (expected " + expectedIncrement + ", actual " + realIncrement + ")");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testLightSources() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "LightSources";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int c=0; c<config.getChannelCount(); c++) {
        String expectedLightSource = config.getLightSource(c);
        String realLightSource = null;
        try {
          realLightSource = retrieve.getChannelLightSourceSettingsID(i, c);
        }
        catch (NullPointerException e) { }

        if (!(expectedLightSource == null && realLightSource == null) &&
          !expectedLightSource.equals(realLightSource))
        {
          result(testName, false, "Series " + i + " channel " + c + " (expected " + expectedLightSource + ", actual " + realLightSource + ")");
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testChannelNames() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ChannelNames";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int c=0; c<config.getChannelCount(); c++) {
        String realName = retrieve.getChannelName(i, c);
        String expectedName = config.getChannelName(c);

        if (!expectedName.equals(realName) &&
          (realName == null && !expectedName.equals("null")))
        {
          result(testName, false, "Series " + i + " channel " + c +
            " (got '" + realName + "', expected '" + expectedName + "')");
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testExposureTimes() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ExposureTimes";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getImageCount() != retrieve.getPlaneCount(i)) {
        continue;
      }

      for (int c=0; c<config.getChannelCount(); c++) {
        if (config.hasExposureTime(c)) {
          Time exposureTime = config.getExposureTime(c);

          for (int p=0; p<reader.getImageCount(); p++) {
            int[] zct = reader.getZCTCoords(p);
            if (zct[1] == c && p < retrieve.getPlaneCount(i)) {
              Time planeExposureTime = retrieve.getPlaneExposureTime(i, p);

              if (exposureTime == null && planeExposureTime == null) {
                continue;
              }

              if (exposureTime == null || planeExposureTime == null ||
                !exposureTime.equals(planeExposureTime))
              {
                result(testName, false, "Series " + i + " plane " + p +
                  " channel " + c + " (got " + planeExposureTime +
                  ", expected " + exposureTime + ")");
              }
            }
          }
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testDeltaT() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "DeltaT";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int p=0; p<reader.getImageCount(); p++) {
        Time deltaT = null;
        try {
          deltaT = retrieve.getPlaneDeltaT(i, p);
        }
        catch (IndexOutOfBoundsException e) { }
        Double expectedDeltaT = config.getDeltaT(p);

        if (deltaT == null && expectedDeltaT == null) {
          continue;
        }

        if (deltaT == null) {
          result(testName, false, "missing series " + i + ", plane " + p);
          return;
        }
        if (expectedDeltaT != null) {
          Double seconds = deltaT.value(UNITS.SECOND).doubleValue();
          if (Math.abs(seconds - expectedDeltaT) > Constants.EPSILON) {
            result(testName, false, "series " + i + ", plane " + p +
              " (expected " + expectedDeltaT + ", actual " + seconds + ")");
            return;
          }
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testPlanePositions() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PlanePositions";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int p=0; p<reader.getImageCount(); p++) {
        Length posX = null;
        Length posY = null;
        Length posZ = null;
        try {
          posX = retrieve.getPlanePositionX(i, p);
        }
        catch (IndexOutOfBoundsException e) { }
        try {
          posY = retrieve.getPlanePositionY(i, p);
        }
        catch (IndexOutOfBoundsException e) { }
        try {
          posZ = retrieve.getPlanePositionZ(i, p);
        }
        catch (IndexOutOfBoundsException e) { }

        Double expectedX = config.getPositionX(p);
        Double expectedY = config.getPositionY(p);
        Double expectedZ = config.getPositionZ(p);
        String expectedXUnit = config.getPositionXUnit(p);
        String expectedYUnit = config.getPositionYUnit(p);
        String expectedZUnit = config.getPositionZUnit(p);
        
        if (posX == null && expectedX == null) {
        }
        else if (posX == null) {
          result(testName, false, "missing X position for series " + i + ", plane " + p);
          return;
        }
        else if (expectedX != null && expectedXUnit != null) {
          Double x = posX.value().doubleValue();
          if (!expectedXUnit.equals(posX.unit().getSymbol())) {
            result(testName, false, "X position unit series " + i + ", plane " + p +
              " (expected " + expectedXUnit + ", actual " + posX.unit().getSymbol() + ")");
            return;
          }
          if (Math.abs(x - expectedX) > Constants.EPSILON) {
            result(testName, false, "X position series " + i + ", plane " + p +
              " (expected " + expectedX + ", actual " + x + ")");
            return;
          }
        }

        if (posY == null && expectedY == null) {
        }
        else if (posY == null) {
          result(testName, false, "missing Y position for series " + i + ", plane " + p);
          return;
        }
        else if (expectedY != null && expectedYUnit != null) {
          Double y = posY.value().doubleValue();
          if (!expectedYUnit.equals(posY.unit().getSymbol())) {
            result(testName, false, "Y position unit series " + i + ", plane " + p +
              " (expected " + expectedYUnit + ", actual " + posY.unit().getSymbol() + ")");
            return;
          }
          if (Math.abs(y - expectedY) > Constants.EPSILON) {
            result(testName, false, "Y position series " + i + ", plane " + p +
              " (expected " + expectedY + ", actual " + y + ")");
            return;
          }
        }
        if (posZ == null && expectedZ == null) {
        }
        else if (posZ == null) {
          result(testName, false, "missing Z position for series " + i + ", plane " + p);
          return;
        }
        else if (expectedZ != null && expectedZUnit != null) {
          Double z = posZ.value().doubleValue();
          if (!expectedZUnit.equals(posZ.unit().getSymbol())) {
            result(testName, false, "Z position unit series " + i + ", plane " + p +
              " (expected " + expectedZUnit + ", actual " + posZ.unit().getSymbol() + ")");
            return;
          }
          if (Math.abs(z - expectedZ) > Constants.EPSILON) {
            result(testName, false, "Z position series " + i + ", plane " + p +
              " (expected " + expectedZ + ", actual " + z + ")");
            return;
          }
        }
      }
    }
    result(testName, true);
  }



  @Test(groups = {"all", "fast", "automated"})
  public void testEmissionWavelengths() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "EmissionWavelengths";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int c=0; c<config.getChannelCount(); c++) {
        Length realWavelength = retrieve.getChannelEmissionWavelength(i, c);
        Length expectedWavelength = config.getEmissionWavelength(c);

        if (realWavelength == null && expectedWavelength == null) {
          continue;
        }

        if (!isAlmostEqual(expectedWavelength,realWavelength))
        {
          result(testName, false, "Series " + i + " channel " + c + " (expected " + expectedWavelength + ", actual " + realWavelength + ")");
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testExcitationWavelengths() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ExcitationWavelengths";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int c=0; c<config.getChannelCount(); c++) {
        Length realWavelength = retrieve.getChannelExcitationWavelength(i, c);
        Length expectedWavelength = config.getExcitationWavelength(c);

        if (!isAlmostEqual(expectedWavelength,realWavelength))
        {
          result(testName, false, "Series " + i + " channel " + c + " (expected " + expectedWavelength + ", actual " + realWavelength + ")");
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testDetectors() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "Detectors";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int c=0; c<config.getChannelCount(); c++) {
        String expectedDetector = config.getDetector(c);
        String realDetector = null;

        try {
          realDetector = retrieve.getDetectorSettingsID(i, c);
        }
        catch (NullPointerException e) { }

        if (!(expectedDetector == null && realDetector == null)) {
          if ((expectedDetector == null ||
            !expectedDetector.equals(realDetector)) && (realDetector == null ||
            !realDetector.equals(expectedDetector)))
          {
            result(testName, false, "Series " + i + " channel " + c + " (expected " + expectedDetector + ", actual " + realDetector + ")");
          }
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testImageNames() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ImageNames";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      String realName = retrieve.getImageName(i);
      String expectedName = config.getImageName();

      if (!expectedName.equals(realName) &&
        !(realName == null && expectedName.equals("null")))
      {
        result(testName, false, "Series " + i + " (got '" + realName +
          "', expected '" + expectedName + "')");
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testImageDescriptions() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ImageDescriptions";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      String realDescription = retrieve.getImageDescription(i);
      if (realDescription != null) {
        realDescription = realDescription.trim();
      }
      if (config.hasImageDescription()) {
        String expectedDescription = config.getImageDescription();
        if (expectedDescription != null) {
          expectedDescription = expectedDescription.trim();
        }

        if (!expectedDescription.equals(realDescription) &&
          !(realDescription == null && expectedDescription.equals("null")))
        {
          result(testName, false, "Series " + i + " (got '" + realDescription +
            "', expected '" + expectedDescription + "')");
        }
      }
    }
    result(testName, true);
  }

  @Test(groups = {"all", "xml", "automated"})
  public void testEqualOMEXML() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testEqualOMEXML";
    if (!initFile()) result(testName, false, "initFile");

    boolean success = true;
    String msg = null;
    try {
      MetadataStore store = reader.getMetadataStore();
      success = omexmlService.isOMEXMLMetadata(store);
      if (!success) msg = TestTools.shortClassName(store);

      String file = reader.getCurrentFile() + ".ome.xml";
      if (success) {
        if (!new File(file).exists() && omexmlDir != null &&
          new File(omexmlDir).exists())
        {
          String dir = System.getProperty("testng.directory");
          if (dir != null) {
            file = reader.getCurrentFile().replace(dir, omexmlDir) + ".ome.xml";

            if (!new File(file).exists()) {
              file = reader.getCurrentFile().replace(dir, omexmlDir);
              file = file.substring(0, file.lastIndexOf(".")) + ".ome.xml";
            }
          }
        }
        if (new File(file).exists()) {
          String xml = DataTools.readFile(file);
          OMEXMLMetadata base = omexmlService.createOMEXMLMetadata(xml);

          success = omexmlService.isEqual(base, (OMEXMLMetadata) store);
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      msg = t.getMessage();
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all"})
  public void testPerformance() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testPerformance";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      int properMem = config.getMemory();
      double properTime = config.getAccessTimeMillis();
      if (properMem <= 0 || properTime <= 0) {
        success = true;
        msg = "no configuration";
      }
      else {
        Runtime r = Runtime.getRuntime();
        System.gc(); // clean memory before we start
        Thread.sleep(1000);
        System.gc(); // clean memory before we start
        long m1 = r.totalMemory() - r.freeMemory();
        long t1 = System.currentTimeMillis();
        int totalPlanes = 0;
        int seriesCount = reader.getSeriesCount();
        for (int i=0; i<seriesCount; i++) {
          reader.setSeries(i);
          int imageCount = reader.getImageCount();
          totalPlanes += imageCount;
          int planeSize = FormatTools.getPlaneSize(reader);
          if (planeSize < 0) {
            continue;
          }
          byte[] buf = new byte[planeSize];
          for (int j=0; j<imageCount; j++) {
            try {
              reader.openBytes(j, buf);
            }
            catch (FormatException e) {
              LOGGER.info("", e);
            }
            catch (IOException e) {
              LOGGER.info("", e);
            }
            catch (Throwable e) {
              if (TestTools.isOutOfMemory(e)) {
                result(testName, true, "Image too large");
                return;
              }
              throw e;
            }
          }
        }
        long t2 = System.currentTimeMillis();
        System.gc();
        Thread.sleep(1000);
        System.gc();
        long m2 = r.totalMemory() - r.freeMemory();
        double actualTime = (double) (t2 - t1) / totalPlanes;
        int actualMem = (int) ((m2 - m1) >> 20);

        // check time elapsed
        if (actualTime - timeMultiplier * properTime > 250.0) {
          success = false;
          msg = "got " + actualTime + " ms, expected " + properTime + " ms";
        }

        // check memory used
        else if (actualMem > properMem + 20) {
          success = false;
          msg =  "used " + actualMem + " MB; expected <= " + properMem + " MB";
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "type", "automated"})
  public void testRequiredDirectories() {
    if (!initFile()) return;
    if (reader.getFormat().equals("Woolz") ||
      reader.getFormat().startsWith("CellH5"))
    {
      throw new SkipException(SKIP_MESSAGE);
    }
    String testName = "testRequiredDirectories";
    String file = reader.getCurrentFile();
    LOGGER.debug("testRequiredDirectories({})", file);
    int directories = -1;

    try {
      directories = reader.getRequiredDirectories(reader.getUsedFiles());
    }
    catch (Exception e) {
      LOGGER.warn("Could not retrieve directory count", e);
    }

    LOGGER.debug("directories = {}", directories);

    if (directories < 0) {
      result(testName, false, "Invalid directory count (" + directories + ")");
    }
    else {
      // make sure the directory count is not too small
      // we can't reliably test for the directory count being too large,
      // since a different fileset in the same format may need more directories

      String[] usedFiles = reader.getUsedFiles();
      String[] newFiles = new String[usedFiles.length];

      // find the common parent

      String commonParent = new Location(usedFiles[0]).getAbsoluteFile().getParent();
      for (int i=1; i<usedFiles.length; i++) {
        while (!usedFiles[i].startsWith(commonParent)) {
          commonParent = commonParent.substring(0, commonParent.lastIndexOf(File.separator));
        }
      }

      LOGGER.debug("commonParent = {}", commonParent);

      // remove extra directories

      String split = File.separatorChar == '\\' ? "\\\\" : File.separator;
      LOGGER.debug("split = {}", split);
      String[] f = commonParent.split(split);
      StringBuilder toRemove = new StringBuilder();
      for (int i=0; i<f.length - directories - 1; i++) {
        toRemove.append(f[i]);
        if (i < f.length - directories - 2) {
          toRemove.append(split);
        }
      }

      // map new file names and verify that setId still works

      String newFile = null;
      for (int i=0; i<usedFiles.length; i++) {
        newFiles[i] = usedFiles[i].replaceAll(toRemove.toString(), "");
        LOGGER.debug("mapping {} to {}", newFiles[i], usedFiles[i]);
        Location.mapId(newFiles[i], usedFiles[i]);

        if (usedFiles[i].equals(file)) {
          newFile = newFiles[i];
        }
      }
      if (newFile == null) {
        newFile = newFiles[0];
      }

      LOGGER.debug("newFile = {}", newFile);

      IFormatReader check = new FileStitcher();
      try {
        check.setId(newFile);
        int nFiles = check.getUsedFiles().length;
        result(testName, nFiles == usedFiles.length,
          "Found " + nFiles + "; expected " + usedFiles.length);
      }
      catch (Exception e) {
        LOGGER.info("Initialization failed", e);
        result(testName, false, e.getMessage());
      }
      finally {
        try {
          check.close();
        }
        catch (IOException e) {
          LOGGER.warn("Could not close reader", e);
        }

        for (int i=0; i<newFiles.length; i++) {
          Location.mapId(newFiles[i], null);
        }
      }
    }
  }

  @Test(groups = {"all", "type", "automated"})
  public void testSaneUsedFiles() {
    if (!initFile()) return;
    String file = reader.getCurrentFile();
    String testName = "testSaneUsedFiles";
    boolean success = true;
    String msg = null;
    try {
      String[] base = reader.getUsedFiles();
      if (base.length == 1) {
        if (!base[0].equals(file)) success = false;
      }
      else {
        Arrays.sort(base);
        IFormatReader r =
          /*config.noStitching() ? new ImageReader() :*/ new FileStitcher();

        int maxFiles = (int) Math.min(base.length, 100);

        if (DataTools.indexOf(
	  reader.getDomains(), FormatTools.HCS_DOMAIN) >= 0 ||
	  file.toLowerCase().endsWith(".czi"))
	{
          maxFiles = (int) Math.min(maxFiles, 10);
        }

        for (int i=0; i<maxFiles && success; i++) {
          // .xlog files in InCell 1000/2000 files may belong to more
          // than one dataset
          if (reader.getFormat().equals("InCell 1000/2000")) {
            if (!base[i].toLowerCase().endsWith(".xdce") &&
              !base[i].toLowerCase().endsWith(".xml"))
            {
              continue;
            }
          }

          // Volocity datasets can only be detected with the .mvd2 file
          if (file.toLowerCase().endsWith(".mvd2") &&
            !base[i].toLowerCase().endsWith(".mvd2"))
          {
            continue;
          }

          // Bruker datasets can only be detected with the
          // 'fid' and 'acqp' files
          if ((file.toLowerCase().endsWith("fid") ||
            file.toLowerCase().endsWith("acqp")) &&
            !base[i].toLowerCase().endsWith("fid") &&
            !base[i].toLowerCase().endsWith("acqp") &&
            reader.getFormat().equals("Bruker"))
          {
            continue;
          }

          // CellR datasets cannot be detected with a TIFF file
          if (reader.getFormat().equals("Olympus APL") &&
            base[i].toLowerCase().endsWith("tif"))
          {
            continue;
          }

          // Micromanager datasets cannot be detected with an OME-TIFF file
          if (reader.getFormat().equals("Micro-Manager") &&
            (base[i].toLowerCase().endsWith(".ome.tiff") ||
            base[i].toLowerCase().endsWith(".ome.tif")))
          {
            continue;
          }

          // DICOM companion files may not be detected
          if (reader.getFormat().equals("DICOM") && !base[i].equals(file)) {
            continue;
          }

          // QuickTime resource forks are not detected
          if (reader.getFormat().equals("QuickTime") && !base[i].equals(file)) {
            continue;
          }

          // SVS files in AFI datasets are detected as SVS
          if (reader.getFormat().equals("Aperio AFI") &&
            base[i].toLowerCase().endsWith(".svs"))
          {
            continue;
          }

          if (reader.getFormat().equals("BD Pathway") &&
            (base[i].endsWith(".adf") || base[i].endsWith(".txt")) ||
            base[i].endsWith(".roi"))
          {
            continue;
          }

          // Hamamatsu VMS datasets cannot be detected with non-.vms files
          if (reader.getFormat().equals("Hamamatsu VMS") &&
            !base[i].toLowerCase().endsWith(".vms"))
          {
            continue;
          }

          // pattern datasets can only be detected with the pattern file
          if (reader.getFormat().equals("File pattern")) {
            continue;
          }

          r.setId(base[i]);

          String[] comp = r.getUsedFiles();

          // If an .mdb file was initialized, then .lsm files are grouped.
          // If one of the .lsm files is initialized, though, then files
          // are not grouped.  This is expected behavior; see ticket #3701.
          if (base[i].toLowerCase().endsWith(".lsm") && comp.length == 1) {
            r.close();
            continue;
          }

          // Deltavision datasets are allowed to have different
          // used file counts.  In some cases, a log file is associated
          // with multiple .dv files, so initializing the log file
          // will give different results.
          if (file.toLowerCase().endsWith(".dv") &&
            base[i].toLowerCase().endsWith(".log"))
          {
            r.close();
            continue;
          }

          // Hitachi datasets consist of one text file and one pixels file
          // in a common format (e.g. BMP, JPEG, TIF).
          // It is acceptable for the pixels file to have a different
          // used file count from the text file.
          if (reader.getFormat().equals("Hitachi")) {
            r.close();
            continue;
          }

          // JPEG files that are part of a Trestle dataset can be detected
          // separately
          if (reader.getFormat().equals("Trestle")) {
            r.close();
            continue;
          }

          // TIFF files in CellR datasets are detected separately
          if (reader.getFormat().equals("Olympus APL") &&
            base[i].toLowerCase().endsWith(".tif"))
          {
            r.close();
            continue;
          }

          // TIFF files in Li-Cor datasets are detected separately
          if (reader.getFormat().equals("Li-Cor L2D") &&
            !base[i].toLowerCase().endsWith("l2d"))
          {
            r.close();
            continue;
          }

          // TIFF files in Prairie datasets may be detected as OME-TIFF
          if (reader.getFormat().equals("Prairie TIFF") &&
            base[i].toLowerCase().endsWith(".tif") &&
            r.getFormat().equals("OME-TIFF"))
          {
            r.close();
            continue;
          }

          if (reader.getFormat().equals("Hamamatsu NDPIS") &&
            r.getFormat().equals("Hamamatsu NDPI"))
          {
            r.close();
            continue;
          }

          if (base[i].endsWith(".bmp") && reader.getFormat().equals("BD Pathway"))
          {
            r.close();
            continue;
          }

          if (comp.length != base.length) {
            success = false;
            msg = base[i] + " (file list length was " + comp.length +
              "; expected " + base.length + ")";
          }
          if (success) Arrays.sort(comp);

          // NRRD datasets are allowed to have differing used files.
          // One raw file can have multiple header files associated with
          // it, in which case selecting the raw file will always produce
          // a test failure (which we can do nothing about).
          if (file.toLowerCase().endsWith(".nhdr") ||
            base[i].toLowerCase().endsWith(".nhdr"))
          {
            r.close();
            continue;
          }

          for (int j=0; j<comp.length && success; j++) {
            if (!comp[j].equals(base[j])) {
              if (base[j].equals(new Location(comp[j]).getCanonicalPath())) {
                continue;
              }
              success = false;
              msg = base[i] + "(file @ " + j + " was '" + comp[j] +
                "', expected '" + base[j] + "')";
            }
          }
          r.close();
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "xml", "fast", "automated"})
  public void testValidXML() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testValidXML";
    if (!initFile()) result(testName, false, "initFile");
    if (!config.hasValidXML()) {
      LOGGER.debug("Skipping valid XML test");
      result(testName, true);
      return;
    }
    String format = config.getReader();
    boolean success = true;
    try {
      MetadataStore store = reader.getMetadataStore();
      MetadataRetrieve retrieve = omexmlService.asRetrieve(store);
      String xml = omexmlService.getOMEXML(retrieve);
      // prevent issues due to thread-unsafeness of
      // javax.xml.validation.Validator as used during XML validation
      synchronized (configTree) {
        success = xml != null && omexmlService.validateOMEXML(xml);
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success);
    try {
      close();
    }
    catch (IOException e) {
      LOGGER.info("", e);
    }
  }

  @Test(groups = {"all", "pixels", "automated"})
  public void testUnflattenedPixelsHashes() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testUnflattenedPixelsHashes";
    if (!initFile()) result(testName, false, "initFile");

    boolean success = true;
    String msg = null;
    try {
      IFormatReader resolutionReader =
        new BufferedImageReader(new FileStitcher());
      resolutionReader.setFlattenedResolutions(false);
      resolutionReader.setNormalized(true);
      resolutionReader.setOriginalMetadataPopulated(false);
      resolutionReader.setMetadataFiltered(true);
      resolutionReader.setId(id);

      // check the MD5 of the first plane in each resolution
      for (int i=0; i<resolutionReader.getSeriesCount() && success; i++) {
        resolutionReader.setSeries(i);

        for (int r=0; r<resolutionReader.getResolutionCount() && success; r++) {
          resolutionReader.setResolution(r);
          config.setSeries(resolutionReader.getCoreIndex());

          long planeSize = -1;
          try {
            planeSize = DataTools.safeMultiply32(resolutionReader.getSizeX(),
              resolutionReader.getSizeY(),
              resolutionReader.getRGBChannelCount(),
              FormatTools.getBytesPerPixel(resolutionReader.getPixelType()));
          }
          catch (IllegalArgumentException e) {
            continue;
          }

          if (planeSize < 0 || !TestTools.canFitInMemory(planeSize)) {
            continue;
          }

          String md5 = TestTools.md5(resolutionReader.openBytes(0));
          String expected1 = config.getMD5();
          String expected2 = config.getAlternateMD5();

          if (expected1 == null && expected2 == null) {
            continue;
          }
          if (!md5.equals(expected1) && !md5.equals(expected2)) {
            success = false;
            msg = "series " + i + ", resolution " + r;
          }
        }
      }
      resolutionReader.close();
    }
    catch (Throwable t) {
      if (TestTools.isOutOfMemory(t)) {
        result(testName, true, "Image too large");
        return;
      }
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "pixels", "automated"})
  public void testPixelsHashes() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testPixelsHashes";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      // check the MD5 of the first plane in each series
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        config.setSeries(i);

        long planeSize = -1;
        try {
          planeSize = DataTools.safeMultiply32(reader.getSizeX(),
            reader.getSizeY(), reader.getRGBChannelCount(),
            FormatTools.getBytesPerPixel(reader.getPixelType()));
        }
        catch (IllegalArgumentException e) {
          continue;
        }

        if (planeSize < 0 || !TestTools.canFitInMemory(planeSize)) {
          continue;
        }

        String md5 = TestTools.md5(reader.openBytes(0));
        String expected1 = config.getMD5();
        String expected2 = config.getAlternateMD5();

        if (expected1 == null && expected2 == null) {
          continue;
        }
        if (!md5.equals(expected1) && !md5.equals(expected2)) {
          success = false;
          msg = "series " + i + " (" + md5 + ")";
        }
      }
    }
    catch (Throwable t) {
      if (TestTools.isOutOfMemory(t)) {
        result(testName, true, "Image too large");
        return;
      }
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  /*
  @Test(groups = {"all", "pixels"})
  public void testReorderedPixelsHashes() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testReorderedPixelsHashes";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        config.setSeries(i);

        for (int j=0; j<3; j++) {
          int index = (int) (Math.random() * reader.getImageCount());
          reader.openBytes(index);
        }

        String md5 = TestTools.md5(reader.openBytes(0));
        String expected1 = config.getMD5();
        String expected2 = config.getAlternateMD5();

        if (!md5.equals(expected1) && !md5.equals(expected2)) {
          success = false;
          msg = expected1 == null && expected2 == null ? "no configuration" :
            "series " + i;
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }
  */

  @Test(groups = {"all", "pixels", "automated"})
  public void testUnflattenedSubimagePixelsHashes() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testUnflattenedSubimagePixelsHashes";
    if (!initFile()) result(testName, false, "initFile");

    boolean success = true;
    String msg = null;
    try {
      IFormatReader resolutionReader =
        new BufferedImageReader(new FileStitcher());
      resolutionReader.setFlattenedResolutions(false);
      resolutionReader.setNormalized(true);
      resolutionReader.setOriginalMetadataPopulated(false);
      resolutionReader.setMetadataFiltered(true);
      resolutionReader.setId(id);

      // check the MD5 of the first plane in each resolution
      for (int i=0; i<resolutionReader.getSeriesCount() && success; i++) {
        resolutionReader.setSeries(i);

        for (int r=0; r<resolutionReader.getResolutionCount() && success; r++) {
          resolutionReader.setResolution(r);
          config.setSeries(resolutionReader.getCoreIndex());

          int w = (int) Math.min(Configuration.TILE_SIZE,
            resolutionReader.getSizeX());
          int h = (int) Math.min(Configuration.TILE_SIZE,
            resolutionReader.getSizeY());

          String expected1 = config.getTileMD5();
          String expected2 = config.getTileAlternateMD5();

          String md5 = null;

          try {
            md5 = TestTools.md5(resolutionReader.openBytes(0, 0, 0, w, h));
          }
          catch (Throwable e) {
            if (TestTools.isOutOfMemory(e)) {
              result(testName, true, "Image too large");
              return;
            }
            LOGGER.warn("", e);
          }

          if (md5 == null && expected1 == null && expected2 == null) {
            success = true;
          }
          else if (!md5.equals(expected1) && !md5.equals(expected2) &&
            (expected1 != null || expected2 != null))
          {
            success = false;
            msg = "series " + i + ", resolution " + r;
          }
        }
      }
      resolutionReader.close();
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "pixels", "automated"})
  public void testSubimagePixelsHashes() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testSubimagePixelsHashes";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      // check the MD5 of the first 512x512 tile of
      // the first plane in each series
      for (int i=0; i<reader.getSeriesCount() && success; i++) {
        reader.setSeries(i);
        config.setSeries(i);

        int w = (int) Math.min(Configuration.TILE_SIZE, reader.getSizeX());
        int h = (int) Math.min(Configuration.TILE_SIZE, reader.getSizeY());

        String expected1 = config.getTileMD5();
        String expected2 = config.getTileAlternateMD5();

        String md5 = null;

        try {
          md5 = TestTools.md5(reader.openBytes(0, 0, 0, w, h));
        }
        catch (Throwable e) {
          if (TestTools.isOutOfMemory(e)) {
            result(testName, true, "Image too large");
            return;
          }
          throw e;
        }

        if (md5 == null && expected1 == null && expected2 == null) {
          success = true;
        }
        else if (!md5.equals(expected1) && !md5.equals(expected2) &&
          (expected1 != null || expected2 != null))
        {
          success = false;
          msg = "series " + i + " (" + md5 + ")";
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testIsThisTypeConsistent() {
    String testName = "testIsThisTypeConsistent";
    if (!initFile()) result(testName, false, "initFile");

    String file = reader.getCurrentFile();
    boolean isThisTypeOpen = reader.isThisType(file, true);
    boolean isThisTypeNotOpen = reader.isThisType(file, false);
    result(testName, (isThisTypeOpen == isThisTypeNotOpen) ||
      (isThisTypeOpen && !isThisTypeNotOpen),
      "open = " + isThisTypeOpen + ", !open = " + isThisTypeNotOpen);
  }

  @Test(groups = {"all", "fast", "automated"})
  public void testIsThisType() {
    String testName = "testIsThisType";
    if (!initFile()) result(testName, false, "initFile");
    boolean success = true;
    String msg = null;
    try {
      IFormatReader r = reader;
      // unwrap reader
      while (true) {
        if (r instanceof ReaderWrapper) {
          r = ((ReaderWrapper) r).getReader();
        }
        else if (r instanceof FileStitcher) {
          r = ((FileStitcher) r).getReader();
        }
        else break;
      }
      if (r instanceof ImageReader) {
        ImageReader ir = (ImageReader) r;
        r = ir.getReader();
        IFormatReader[] readers = ir.getReaders();
        String[] used = reader.getUsedFiles();
        for (int i=0; i<used.length && success; i++) {
          // for each used file, make sure that one reader,
          // and only one reader, identifies the dataset as its own
          for (int j=0; j<readers.length; j++) {
            boolean result = readers[j].isThisType(used[i]);

            // TIFF reader is allowed to redundantly green-light files
            if (result && readers[j] instanceof TiffDelegateReader) continue;

            // Bio-Rad reader is allowed to redundantly
            // green-light PIC files from NRRD datasets
            if (result && r instanceof NRRDReader &&
              readers[j] instanceof BioRadReader)
            {
              String low = used[i].toLowerCase();
              boolean isPic = low.endsWith(".pic") || low.endsWith(".pic.gz");
              if (isPic) continue;
            }

            // Analyze reader is allowed to redundantly accept NIfTI files
            if (result && r instanceof NiftiReader &&
              readers[j] instanceof AnalyzeReader)
            {
              continue;
            }

            if (result && r instanceof MetamorphReader &&
              readers[j] instanceof MetamorphTiffReader)
            {
              continue;
            }

            if (result && (readers[j] instanceof L2DReader) ||
              ((r instanceof L2DReader) && (readers[j] instanceof GelReader) ||
              readers[j] instanceof L2DReader))
            {
              continue;
            }

            // ND2Reader is allowed to accept JPEG-2000 files
            if (result && r instanceof JPEG2000Reader &&
              readers[j] instanceof ND2Reader)
            {
              continue;
            }

            if ((result && r instanceof APLReader &&
              readers[j] instanceof SISReader) || (!result &&
              r instanceof APLReader && readers[j] instanceof APLReader))
            {
              continue;
            }

            // Prairie datasets can consist of OME-TIFF files with
            // extra metadata files, so it is acceptable for the OME-TIFF
            // reader to pick up TIFFs from a Prairie dataset
            if (result && r instanceof PrairieReader &&
              readers[j] instanceof OMETiffReader)
            {
              continue;
            }

            // Micromanager datasets can consist of OME-TIFF files
            // with an extra metadata file
            if (result && r instanceof MicromanagerReader &&
              readers[j] instanceof OMETiffReader)
            {
              continue;
            }
            if (!result && r instanceof MicromanagerReader &&
              readers[j] instanceof MicromanagerReader &&
              (used[i].toLowerCase().endsWith(".ome.tif") ||
              used[i].toLowerCase().endsWith(".ome.tiff")))
            {
              continue;
            }

            if (result && r instanceof TrestleReader &&
              (readers[j] instanceof JPEGReader ||
              readers[j] instanceof PGMReader ||
              readers[j] instanceof TiffDelegateReader))
            {
              continue;
            }

            if (result && ((r instanceof HitachiReader) ||
              (readers[j] instanceof HitachiReader &&
              (r instanceof TiffDelegateReader || r instanceof JPEGReader ||
              r instanceof BMPReader))))
            {
              continue;
            }

            if (result && r instanceof BDReader &&
              readers[j] instanceof BMPReader)
            {
              continue;
            }

            if (!result && readers[j] instanceof BDReader &&
              (used[i].endsWith(".bmp") || used[i].endsWith(".adf") ||
              used[i].endsWith(".txt") || used[i].endsWith(".roi")))
            {
              continue;
            }

            if (!result && r instanceof VolocityReader &&
              readers[j] instanceof VolocityReader)
            {
              continue;
            }

            if (!result && r instanceof InCellReader &&
              readers[j] instanceof InCellReader &&
              !used[i].toLowerCase().endsWith(".xdce"))
            {
              continue;
            }

            if (!result && r instanceof BrukerReader &&
              readers[j] instanceof BrukerReader &&
              !used[i].toLowerCase().equals("acqp") &&
              !used[i].toLowerCase().equals("fid"))
            {
              continue;
            }

            // Volocity reader is allowed to accept files of other formats
            if (result && r instanceof VolocityReader) {
              continue;
            }

            // DNG files can be picked up by both the Nikon reader and the
            // DNG reader

            if (result && r instanceof NikonReader &&
              readers[j] instanceof DNGReader)
            {
              continue;
            }

            // DICOM reader is not expected to pick up companion files
            if (!result && r instanceof DicomReader &&
              readers[j] instanceof DicomReader)
            {
              continue;
            }

            // AFI reader is not expected to pick up .svs files
            if (r instanceof AFIReader && (readers[j] instanceof AFIReader ||
              readers[j] instanceof SVSReader))
            {
              continue;
            }

            if (!result && readers[j] instanceof MIASReader) {
              continue;
            }

            if ((readers[j] instanceof NDPISReader ||
              r instanceof NDPISReader) &&
              used[i].toLowerCase().endsWith(".ndpi"))
            {
              continue;
            }

            // the JPEG reader can pick up JPEG files associated with a
            // Hamamatsu VMS dataset
            if (readers[j] instanceof JPEGReader &&
              r instanceof HamamatsuVMSReader &&
              used[i].toLowerCase().endsWith(".jpg"))
            {
              continue;
            }

            // the Hamamatsu VMS reader only picks up its .vms file
            if (!result && !used[i].toLowerCase().endsWith(".vms") &&
              r instanceof HamamatsuVMSReader)
            {
              continue;
            }

            // QuickTime reader doesn't pick up resource forks
            if (!result && i > 0 && r instanceof QTReader) {
              continue;
            }

            // the pattern reader only picks up pattern files
            if (!result && !used[i].toLowerCase().endsWith(".pattern") &&
              r instanceof FilePatternReader)
            {
              continue;
            }

            boolean expected = r == readers[j];
            if (result != expected) {
              success = false;
              if (result) {
                msg = TestTools.shortClassName(readers[j]) + " flagged \"" +
                  used[i] + "\" but so did " + TestTools.shortClassName(r);
              }
              else {
                msg = TestTools.shortClassName(readers[j]) +
                  " skipped \"" + used[i] + "\"";
              }
              break;
            }
          }
        }
      }
      else {
        success = false;
        msg = "Reader " + r.getClass().getName() + " is not an ImageReader";
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  @Test(groups = {"all",  "automated", "memoizer"})
  public void testMemoFileUsage() {
    String testName = "testMemoFileUsage";
    if (!initFile()) result(testName, false, "initFile");
    File memoFile = null;
    File memoDir = null;
    try {
      // this should prevent conflicts when running multiple tests
      // on the same system and/or in multiple threads
      String tmpdir = System.getProperty("java.io.tmpdir");
      memoDir = new File(tmpdir, System.currentTimeMillis() + ".memo");
      memoDir.mkdir();
      Memoizer memo = new Memoizer(0, memoDir);
      memo.setId(reader.getCurrentFile());
      memo.close();
      memoFile = memo.getMemoFile(reader.getCurrentFile());
      if (!memo.isSavedToMemo()) {
        result(testName, false, "Memo file not saved");
      }

      // first test memo file generated with current build

      memo.setId(reader.getCurrentFile());
      if (!memo.isLoadedFromMemo()) {
        result(testName, false, "Memo file could not be loaded");
      }
      memo.openBytes(0, 0, 0, 1, 1);
      memo.close();

      // now test pre-generated memo file in the cache directory

      String cacheDir = configTree.getCacheDirectory();
      if (cacheDir != null) {
        LOGGER.debug("Loading memo from populated cache");
        File dir = new File(cacheDir);

        if (!dir.exists() || !dir.isDirectory() || !dir.canRead()) {
          result(testName, false, "Cached memo directory does not exist");
        }

        File currentFile = new File(reader.getCurrentFile());
        String relativeName = "." + currentFile.getName() + ".bfmemo";
        File expectedMemo = new File(cacheDir, currentFile.getParent());
        expectedMemo = new File(expectedMemo, relativeName);

        if (expectedMemo.exists()) {
          memo = new Memoizer(0, dir);
          // do not allow an existing memo file to be overwritten
          memo.setReadOnly(true);
          memo.setId(reader.getCurrentFile());
          if (!memo.isLoadedFromMemo()) {
            result(testName, false, "Existing memo file could not be loaded");
          }
          memo.openBytes(0, 0, 0, 1, 1);
          memo.close();
        }
        else {
          LOGGER.warn("Missing memo file {}; passing test anyway", expectedMemo);
        }
      }

      result(testName, true);
    }
    catch (Throwable t) {
      if (TestTools.isOutOfMemory(t)) {
        result(testName, true, "Image too large");
        return;
      }
      LOGGER.warn("", t);
      result(testName, false, t.getMessage());
    }
    finally {
      if (memoFile != null) {
        // log the memo file's size
        try {
          RandomAccessInputStream s = new RandomAccessInputStream(memoFile.getAbsolutePath());
          LOGGER.debug("memo file size for {} = {} bytes",
                      new Location(reader.getCurrentFile()).getAbsolutePath(),
                      s.length());
          s.close();
        }
        catch (IOException e) {
          LOGGER.warn("memo file size not available");
        }

        memoFile.delete();
        // recursively delete, as the original file's path is replicated
        // within the memo directory
        while (!memoFile.getParentFile().equals(memoDir)) {
          memoFile = memoFile.getParentFile();
          memoFile.delete();
        }
      }
      if (memoDir != null) {
        memoDir.delete();
      }
    }
  }

  @Test(groups = {"config"})
  public void writeConfigFile() throws IOException {
    setupReader();
    if (!initFile(false)) return;
    String file = reader.getCurrentFile();
    try {
      String parent = new Location(file).getParent();
      String configDir = configTree.getConfigDirectory();
      String rootDir = configTree.getRootDirectory();
      if (configDir != null) {
        parent = parent.replaceAll(rootDir, configDir);
        File parentDir = new File(parent);
        if (!parentDir.exists()) {
          parentDir.mkdirs();
        }
      }
      File f = new File(parent, ".bioformats");
      LOGGER.info("Generating configuration: {}", f);
      Configuration newConfig = new Configuration(reader, f.getAbsolutePath());
      newConfig.saveToFile();
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      assert false;
    } finally {
      reader.close();
    }
  }

  @Test(groups = {"cache"})
  public void writeCacheFile() throws IOException {
    setupReader();
    if (!initFile(false)) return;
    String cacheDir = configTree.getCacheDirectory();
    if (cacheDir == null) {
      LOGGER.info("No cache directory specified");
      return;
    }
    try {
      Memoizer memo = new Memoizer(0, new File(cacheDir));
      assert memo.generateMemo(reader.getCurrentFile());
      File memoFile = memo.getMemoFile(reader.getCurrentFile());
      LOGGER.info("Saved memo file to {}", memoFile);
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      assert false;
    } finally {
      reader.close();
    }
  }

  @Test(groups = {"config-xml"})
  public void writeXML() {
    setupReader();
    if (!initFile(false)) return;
    String file = reader.getCurrentFile();
    LOGGER.info("Generating XML: {}", file);
    try {
      Location l = new Location(file);
      File f = new File(l.getParent(), l.getName() + ".ome.xml");
      OutputStreamWriter writer =
        new OutputStreamWriter(new FileOutputStream(f), Constants.ENCODING);
      MetadataStore store = reader.getMetadataStore();
      MetadataRetrieve retrieve = omexmlService.asRetrieve(store);
      String xml = omexmlService.getOMEXML(retrieve);
      writer.write(xml);
      writer.close();
      reader.close();
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      assert false;
    }
  }

  @Test(groups = {"file-list"})
  public void saveFileScanList() {
    try {
      File f = new File(fileList);
      OutputStreamWriter writer =
        new OutputStreamWriter(new FileOutputStream(f, true),
        Constants.ENCODING);
      if (f.length() == 0) {
        // make sure the first line is the base directory
        writer.write(System.getProperty("testng.directory"));
        writer.write("\n");
      }
      writer.write(id);
      writer.write("\n");
      writer.close();
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      assert false;
    }
  }

  // -- Helper methods --

  /** Sets up the current IFormatReader. */
  private void setupReader() {
    ImageReader ir = new ImageReader();
    reader = new BufferedImageReader(new FileStitcher(new Memoizer(ir, Memoizer.DEFAULT_MINIMUM_ELAPSED, new File(""))));
    reader.setMetadataOptions(new DefaultMetadataOptions(MetadataLevel.NO_OVERLAYS));
    reader.setNormalized(true);
    reader.setOriginalMetadataPopulated(false);
    reader.setMetadataFiltered(true);
    MetadataStore store = null;
    try {
      store = omexmlService.createOMEXMLMetadata();
    }
    catch (ServiceException e) {
      LOGGER.warn("Could not parse OME-XML", e);
    }
    reader.setMetadataStore(store);
  }

  /** Initializes the reader and configuration tree. */
  private boolean initFile() {
    return initFile(true);
  }

  private boolean initFile(boolean removeDuplicateFiles) {
    if (skip) throw new SkipException(SKIP_MESSAGE);

    // initialize configuration tree
    if (config == null) {
      try {
        synchronized (configTree) {
          config = configTree.get(id);
        }
      }
      catch (IOException e) { }
    }

    if (reader == null) {
      setupReader();
    }

    String absPath = new Location(id).getAbsolutePath();
    if (reader.getCurrentFile() != null &&
      (absPath.equals(
      new Location(reader.getCurrentFile()).getAbsolutePath()) ||
      DataTools.indexOf(reader.getUsedFiles(), absPath) >= 0))
    {
      return true;  // already initialized
    }

    // skip files that were already tested as part of another file's dataset
    int ndx = skipFiles.indexOf(id);
    if (ndx >= 0 && removeDuplicateFiles) {
      LOGGER.info("Skipping {}", id);
      skipFiles.remove(ndx);
      skip = true;
      throw new SkipException(SKIP_MESSAGE);
    }

    // only test for missing configuration *after* we have removed duplicates
    // this prevents failures for missing configuration of files that are on
    // the used files list for a different file (e.g. TIFFs in a Leica LEI
    // dataset)
    if (config == null && removeDuplicateFiles) {
      throw new RuntimeException(id + " not configured.");
    }

    LOGGER.info("Initializing {}: ", id);
    try {
      boolean reallyInMemory = false;
      if (inMemory && reader.isSingleFile(id)) {
        HashMap<String, Object> idMap = Location.getIdMap();
        idMap.clear();
        Location.setIdMap(idMap);

        reallyInMemory = TestTools.mapFile(id);
      }
      reader.setId(id);
      // remove used files
      String[] used = reader.getUsedFiles();
      boolean base = false;
      for (int i=0; i<used.length; i++) {
        if (id.equals(used[i])) {
          base = true;
          continue;
        }
        skipFiles.add(used[i]);
        if (reallyInMemory) {
          TestTools.mapFile(used[i]);
        }
      }
      boolean single = used.length == 1;
      if (single && base) LOGGER.debug("OK");
      else LOGGER.debug("{} {}", used.length, single ? "file" : "files");
      if (!base) {
        LOGGER.error("Used files list does not include base file");
      }
    }
    catch (Throwable t) {
      LOGGER.error("", t);
      return false;
    }

    return true;
  }

  /** Outputs test result and generates appropriate assertion. */
  private static void result(String testName, boolean success) {
    result(testName, success, null);
  }

  /**
   * Outputs test result with optional extra message
   * and generates appropriate assertion.
   */
  private static void result(String testName, boolean success, String msg) {
    if (success) {
      LOGGER.debug("\t{}: PASSED ({})", new Object[] {testName,
        msg == null ? "" : msg});
    }
    else {
      LOGGER.error("\t{}: FAILED ({})", new Object[] {testName,
        msg == null ? "" : msg});
    }

    if (msg == null) assert success;
    else assert success : msg;
  }

}
