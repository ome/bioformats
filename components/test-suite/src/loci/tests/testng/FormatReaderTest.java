//
// FormatReaderTest.java
//

/*
LOCI software automated test suite for TestNG. Copyright (C) 2007-@year@
Melissa Linkert and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.tests.testng;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import loci.common.ByteArrayHandle;
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
import loci.formats.ReaderWrapper;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.in.AnalyzeReader;
import loci.formats.in.APLReader;
import loci.formats.in.BioRadReader;
import loci.formats.in.GelReader;
import loci.formats.in.JPEGReader;
import loci.formats.in.JPEG2000Reader;
import loci.formats.in.L2DReader;
import loci.formats.in.MetamorphReader;
import loci.formats.in.MetamorphTiffReader;
import loci.formats.in.ND2Reader;
import loci.formats.in.NiftiReader;
import loci.formats.in.NRRDReader;
import loci.formats.in.OMETiffReader;
import loci.formats.in.PGMReader;
import loci.formats.in.PrairieReader;
import loci.formats.in.SISReader;
import loci.formats.in.TiffDelegateReader;
import loci.formats.in.TrestleReader;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.SkipException;
import org.testng.annotations.AfterClass;

/**
 * TestNG tester for Bio-Formats file format readers.
 * Details on failed tests are written to a log file, for easier processing.
 *
 * NB: {@link loci.formats.ome} and ome-xml.jar
 * are required for some of the tests.
 *
 * To run tests:
 * ant -Dtestng.directory="/path" -Dtestng.multiplier="1.0" test-all
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/FormatReaderTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/FormatReaderTest.java;hb=HEAD">Gitweb</a></dd></dl>
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

  /** Global shared reader for use in all tests. */
  private static BufferedImageReader reader;

  // -- Fields --

  private String id;
  private boolean skip = false;
  private Configuration config;

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

  // -- Setup/teardown methods --

  @AfterClass
  public void close() throws IOException {
    reader.close();
    HashMap<String, Object> idMap = Location.getIdMap();
    idMap.clear();
    Location.setIdMap(idMap);
  }

  // -- Tests --

  /**
   * @testng.test groups = "all pixels"
   */
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

        if (c > 4 || plane < 0 || !TestTools.canFitInMemory(plane)) {
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
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  /**
   * @testng.test groups = "all pixels"
   */
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

        int expected = x * y * c * bytes;

        if (!TestTools.canFitInMemory(expected) || expected < 0) {
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
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  /**
   * @testng.test groups = "all pixels"
   */
  public void testThumbnailImageDimensions() {
    String testName = "testThumbnailImageDimensions";
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

        int fx = reader.getSizeX();
        int fy = reader.getSizeY();

        if (c > 4 || type == FormatTools.FLOAT || type == FormatTools.DOUBLE ||
          !TestTools.canFitInMemory(fx * fy * c * bytes))
        {
          continue;
        }

        BufferedImage b = reader.openThumbImage(0);

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

  /**
   * @testng.test groups = "all pixels"
   */
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
          !TestTools.canFitInMemory(fx * fy * c * bytes))
        {
          continue;
        }

        byte[] b = reader.openThumbBytes(0);
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

  /**
   * @testng.test groups = "all fast"
   */
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

  /**
   * @testng.test groups = "all xml fast"
   */
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
          if (reader.isLittleEndian() ==
            retrieve.getPixelsBinDataBigEndian(i, 0).booleanValue())
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

  /**
   * @testng.test groups = "all fast"
   */
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

  /**
   * @testng.test groups = "all xml"
   */
  public void testSaneOMEXML() {
    String testName = "testSaneOMEXML";
    if (!initFile()) result(testName, false, "initFile");
    String msg = null;
    try {
      String format = config.getReader();
      if (format.equals("OMETiffReader") || format.equals("OMEXMLReader")) {
        result(testName, true);
        return;
      }

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

        Double deltaT = null;
        Double exposure = null;
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
        String date = retrieve.getImageAcquiredDate(i);
        String configDate = config.getDate();
        if (date != null && !date.equals(configDate)) {
          date = date.trim();
          long acquiredDate = DateTools.getTime(date, DateTools.ISO8601_FORMAT);
          long saneDate =
            DateTools.getTime("1990-01-01T00:00:00", DateTools.ISO8601_FORMAT);
          long fileDate = new Location(
            reader.getCurrentFile()).getAbsoluteFile().lastModified();
          if (acquiredDate < saneDate && fileDate >= saneDate) {
            msg = "CreationDate";
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

  /**
   * @testng.test groups = "all fast"
   */
  public void testSizeX() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeX";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeX() != config.getSizeX()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testSizeY() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeY";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeY() != config.getSizeY()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testSizeZ() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeZ";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeZ() != config.getSizeZ()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testSizeC() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeC";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeC() != config.getSizeC()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testSizeT() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "SizeT";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getSizeT() != config.getSizeT()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
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

  /**
   * @testng.test groups = "all fast"
   */
  public void testIsInterleaved() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "Interleaved";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isInterleaved() != config.isInterleaved()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testIndexed() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "Indexed";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isIndexed() != config.isIndexed()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testFalseColor() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "FalseColor";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isFalseColor() != config.isFalseColor()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testRGB() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "RGB";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isRGB() != config.isRGB()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testThumbSizeX() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ThumbSizeX";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getThumbSizeX() != config.getThumbSizeX()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testThumbSizeY() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ThumbSizeY";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.getThumbSizeY() != config.getThumbSizeY()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
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
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testLittleEndian() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "LittleEndian";
    if (!initFile()) result(testName, false, "initFile");

    for (int i=0; i<reader.getSeriesCount(); i++) {
      reader.setSeries(i);
      config.setSeries(i);

      if (reader.isLittleEndian() != config.isLittleEndian()) {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testPhysicalSizeX() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PhysicalSizeX";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      Double expectedSize = config.getPhysicalSizeX();
      if (expectedSize == null || expectedSize == 0d) {
        expectedSize = null;
      }
      PositiveFloat realSize = retrieve.getPixelsPhysicalSizeX(i);
      Double size = realSize == null ? null : realSize.getValue();

      if (!(expectedSize == null && realSize == null) &&
        !expectedSize.equals(size))
      {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testPhysicalSizeY() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PhysicalSizeY";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);
      Double expectedSize = config.getPhysicalSizeY();
      if (expectedSize == null || expectedSize == 0d) {
        expectedSize = null;
      }
      PositiveFloat realSize = retrieve.getPixelsPhysicalSizeY(i);
      Double size = realSize == null ? null : realSize.getValue();

      if (!(expectedSize == null && realSize == null) &&
        !expectedSize.equals(size))
      {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testPhysicalSizeZ() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "PhysicalSizeZ";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      Double expectedSize = config.getPhysicalSizeZ();
      if (expectedSize == null || expectedSize == 0d) {
        expectedSize = null;
      }
      PositiveFloat realSize = retrieve.getPixelsPhysicalSizeZ(i);
      Double size = realSize == null ? null : realSize.getValue();

      if (!(expectedSize == null && realSize == null) &&
        !expectedSize.equals(size))
      {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testTimeIncrement() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "TimeIncrement";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      Double expectedIncrement = config.getTimeIncrement();
      Double realIncrement = retrieve.getPixelsTimeIncrement(i);

      if (!(expectedIncrement == null && realIncrement == null) &&
        !expectedIncrement.equals(realIncrement))
      {
        result(testName, false, "Series " + i);
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
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
          result(testName, false, "Series " + i + " channel " + c);
        }
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
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

  /**
   * @testng.test groups = "all fast"
   */
  public void testEmissionWavelengths() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "EmissionWavelengths";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int c=0; c<config.getChannelCount(); c++) {
        PositiveInteger realWavelength =
          retrieve.getChannelEmissionWavelength(i, c);
        Integer expectedWavelength = config.getEmissionWavelength(c);

        if (realWavelength == null && expectedWavelength == null) {
          continue;
        }

        if (realWavelength == null || expectedWavelength == null ||
          !expectedWavelength.equals(realWavelength.getValue()))
        {
          result(testName, false, "Series " + i + " channel " + c);
        }
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
  public void testExcitationWavelengths() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "ExcitationWavelengths";
    if (!initFile()) result(testName, false, "initFile");
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      config.setSeries(i);

      for (int c=0; c<config.getChannelCount(); c++) {
        PositiveInteger realWavelength =
          retrieve.getChannelExcitationWavelength(i, c);
        Integer expectedWavelength = config.getExcitationWavelength(c);

        if (realWavelength == null && expectedWavelength == null) {
          continue;
        }

        if (realWavelength == null || expectedWavelength == null ||
          !expectedWavelength.equals(realWavelength.getValue()))
        {
          result(testName, false, "Series " + i + " channel " + c);
        }
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
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
            result(testName, false, "Series " + i + " channel " + c);
          }
        }
      }
    }
    result(testName, true);
  }

  /**
   * @testng.test groups = "all fast"
   */
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

  /**
   * @testng.test groups = "all"
   */
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
          }
        }
        long t2 = System.currentTimeMillis();
        System.gc();
        long m2 = r.totalMemory() - r.freeMemory();
        double actualTime = (double) (t2 - t1) / totalPlanes;
        int actualMem = (int) ((m2 - m1) >> 20);

        // check time elapsed
        if (actualTime - timeMultiplier * properTime > 20.0) {
          success = false;
          msg = "got " + actualTime + " ms, expected " + properTime + " ms";
        }

        // check memory used
        else if (actualMem > properMem) {
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

  /**
   * @testng.test groups = "all type"
   */
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

        for (int i=0; i<base.length && success; i++) {
          // .xlog files in InCell 1000/2000 files may belong to more
          // than one dataset
          if (file.toLowerCase().endsWith(".xdce") &&
            base[i].toLowerCase().endsWith(".xlog"))
          {
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
            continue;
          }

          for (int j=0; j<comp.length && success; j++) {
            if (!comp[j].equals(base[j])) {
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

  /**
   * @testng.test groups = "all xml fast"
   */
  public void testValidXML() {
    if (config == null) throw new SkipException("No config tree");
    String testName = "testValidXML";
    if (!initFile()) result(testName, false, "initFile");
    String format = config.getReader();
    if (format.equals("OMETiffReader") || format.equals("OMEXMLReader")) {
      result(testName, true);
      return;
    }
    boolean success = true;
    try {
      MetadataStore store = reader.getMetadataStore();
      MetadataRetrieve retrieve = omexmlService.asRetrieve(store);
      String xml = omexmlService.getOMEXML(retrieve);
      success = xml != null && omexmlService.validateOMEXML(xml);
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success);
  }

  /**
   * @testng.test groups = "all pixels"
   */
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

        int planeSize = FormatTools.getPlaneSize(reader);
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
          msg = "series " + i;
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  /**
   * @testng.test groups = "all pixels"
   */
  /*
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

  /**
   * @testng.test groups = "all pixels"
   */
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

        String md5 = TestTools.md5(reader.openBytes(0, 0, 0, w, h));
        String expected1 = config.getTileMD5();
        String expected2 = config.getTileAlternateMD5();

        if (!md5.equals(expected1) && !md5.equals(expected2) &&
          (expected1 != null || expected2 != null))
        {
          success = false;
          msg = "series " + i;
        }
      }
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  /**
   * @testng.test groups = "all fast"
   */
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

  /**
   * @testng.test groups = "all fast"
   */
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
              ((r instanceof L2DReader) && (readers[j] instanceof GelReader)))
            {
              continue;
            }

            // ND2Reader is allowed to accept JPEG-2000 files
            if (result && r instanceof JPEG2000Reader &&
              readers[j] instanceof ND2Reader)
            {
              continue;
            }

            if (result && r instanceof APLReader &&
              readers[j] instanceof SISReader)
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

            if (result && r instanceof TrestleReader &&
              (readers[j] instanceof JPEGReader ||
              readers[j] instanceof PGMReader ||
              readers[j] instanceof TiffDelegateReader))
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

  /**
   * @testng.test groups = "config"
   */
  public void writeConfigFile() {
    reader = new BufferedImageReader(new FileStitcher());
    setupReader();
    if (!initFile(false)) return;
    String file = reader.getCurrentFile();
    LOGGER.info("Generating configuration: {}", file);
    try {
      File f = new File(new Location(file).getParent(), ".bioformats");
      Configuration newConfig = new Configuration(reader, f.getAbsolutePath());
      newConfig.saveToFile();
      reader.close();
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      assert false;
    }
  }

  // -- Helper methods --

  /** Sets up the current IFormatReader. */
  private void setupReader() {
    reader.setNormalized(true);
    reader.setOriginalMetadataPopulated(true);
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
        config = configTree.get(id);
      }
      catch (IOException e) { }
    }

    if (reader == null) {
      /*
      if (config.noStitching()) {
        reader = new BufferedImageReader();
      }
      else {
      */
        reader = new BufferedImageReader(new FileStitcher());
      //}
      reader.setNormalized(true);
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

    if (id.equals(reader.getCurrentFile())) return true; // already initialized

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
      if (inMemory) {
        HashMap<String, Object> idMap = Location.getIdMap();
        idMap.clear();
        Location.setIdMap(idMap);

        reallyInMemory = mapFile(id);
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
          mapFile(used[i]);
        }
      }
      boolean single = used.length == 1;
      if (single && base) LOGGER.info("OK");
      else LOGGER.info("{} {}", used.length, single ? "file" : "files");
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
    LOGGER.info("\t{}: {} ({})", new Object[] {testName,
      success ? "PASSED" : "FAILED", msg == null ? "" : msg});
    if (msg == null) assert success;
    else assert success : msg;
  }

  private static boolean mapFile(String id) throws IOException {
    RandomAccessInputStream stream = new RandomAccessInputStream(id);
    Runtime rt = Runtime.getRuntime();
    long maxMem = rt.freeMemory();
    long length = stream.length();
    if (length < Integer.MAX_VALUE && length < maxMem) {
      stream.close();
      FileInputStream fis = new FileInputStream(id);
      FileChannel channel = fis.getChannel();
      ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);
      ByteArrayHandle handle = new ByteArrayHandle(buf);
      Location.mapFile(id, handle);
      fis.close();
      return true;
    }
    stream.close();
    return false;
  }

}
