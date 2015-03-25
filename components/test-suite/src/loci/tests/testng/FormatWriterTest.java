/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import loci.common.DataTools;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.gui.BufferedImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.out.JPEG2000Writer;
import loci.formats.out.JPEGWriter;
import loci.formats.services.OMEXMLService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestNG tester for Bio-Formats file format writers.
 * Details on failed tests are written to a log file, for easier processing.
 *
 * NB: {@link loci.formats.ome} and ome-xml.jar
 * are required for some of the tests.
 *
 * To run tests:
 * ant -Dtestng.directory="/path" -Dtestng.multiplier="1.0" test-all
 */
public class FormatWriterTest {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(FormatWriterTest.class);

  /** Message to give for why a test was skipped. */
  private static final String SKIP_MESSAGE = "Dataset already tested.";

  // -- Static fields --

  /** Configuration tree structure containing dataset metadata. */
  public static ConfigurationTree configTree;

  /** List of files to skip. */
  public static List skipFiles = new LinkedList();

  /** Reader for input file. */
  private static BufferedImageReader reader = new BufferedImageReader();

  /** Reader for converted files. */
  private static BufferedImageReader convertedReader =
    new BufferedImageReader();

  private static Configuration config;

  // -- Fields --

  private String id;
  private boolean skip = false;

  // -- Constructor --

  public FormatWriterTest(String filename) {
    id = filename;
    try {
      reader.setId(id);
    }
    catch (FormatException e) { LOGGER.info("", e); }
    catch (IOException e) { LOGGER.info("", e); }
  }

  // -- Data provider --

  @DataProvider(name = "getWriterList")
  public Object[][] getWriterList() {
    IFormatWriter[] writers = new ImageWriter().getWriters();
    List tmp = new ArrayList();
    for (int i=0; i<writers.length; i++) {
      String[] compressionTypes = writers[i].getCompressionTypes();
      if (compressionTypes == null) {
        try {
          IFormatWriter w = (IFormatWriter) writers[i].getClass().newInstance();
          tmp.add(w);
        }
        catch (InstantiationException ie) { }
        catch (IllegalAccessException iae) { }
        continue;
      }
      for (int q=0; q<compressionTypes.length; q++) {
        try {
          IFormatWriter w = (IFormatWriter) writers[i].getClass().newInstance();
          if (DataTools.containsValue(w.getPixelTypes(compressionTypes[q]),
            reader.getPixelType()))
          {
            w.setCompression(compressionTypes[q]);
            tmp.add(w);
          }
        }
        catch (FormatException fe) { }
        catch (InstantiationException ie) { }
        catch (IllegalAccessException iae) { }
      }
    }
    IFormatWriter[][] writersToUse = new IFormatWriter[tmp.size()][1];
    for (int i=0; i<tmp.size(); i++) {
      writersToUse[i][0] = (IFormatWriter) tmp.get(i);
    }
    return writersToUse;
  }

  // -- Tests --

  @Test(groups = {"all"}, dataProvider = "getWriterList")
  public void testWriterConsistency(IFormatWriter writer) {
    String testName = TestTools.shortClassName(writer) + " " +
      writer.getCompression() + " testWriterConsistency";
    boolean success = true;
    String msg = null;
    try {
      reader.close();

      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      reader.setMetadataStore(service.createOMEXMLMetadata());
      reader.setId(id);

      int type = reader.getPixelType();
      if (!writer.isSupportedType(type)) {
        success = true;
        result(testName, success, msg);
        return;
      }

      config = configTree.get(id);

      String prefix = id.substring(id.lastIndexOf(File.separator) + 1,
        id.lastIndexOf("."));
      // prefix must be at least 3 chars, or File.createTempFile(String, String)
      // will throw an exception
      while (prefix.length() < 3) prefix = "x" + prefix;
      String suffix = "." + writer.getSuffixes()[0];
      File tmpFile = File.createTempFile(prefix, suffix);
      tmpFile.deleteOnExit();
      String convertedFile = tmpFile.getAbsolutePath();

      IMetadata meta = (IMetadata) reader.getMetadataStore();
      writer.close();
      writer.setMetadataRetrieve((MetadataRetrieve) meta);

      // convert the input file
      writer.setId(convertedFile);

      int seriesCount = writer.canDoStacks() ? reader.getSeriesCount() : 1;

      for (int series=0; series<seriesCount; series++) {
        reader.setSeries(series);
        writer.setSeries(series);
        int imageCount = writer.canDoStacks() ? reader.getImageCount() : 1;
        for (int image=0; image<imageCount; image++) {
          writer.saveBytes(image, reader.openBytes(image));
        }
      }
      writer.close();

      // verify that the dimensions are accurate

      convertedReader.setId(convertedFile);

      boolean seriesMatch =
        convertedReader.getSeriesCount() == config.getSeriesCount();

      boolean expectRGB = config.isRGB();
      int expectedCount = config.getSizeZ() * config.getSizeT() *
        (expectRGB ? 1 : config.getSizeC());
      boolean imageMatch = convertedReader.getImageCount() == expectedCount;

      if (!seriesMatch && writer.canDoStacks()) {
        int totalImages = 0;
        for (int i=0; i<reader.getSeriesCount(); i++) {
          reader.setSeries(i);
          totalImages += reader.getImageCount();
        }
        reader.setSeries(0);
        if (convertedReader.getImageCount() != totalImages) {
          success = false;
          msg = "Series counts do not match (found " +
            convertedReader.getSeriesCount() + ", expected " +
            config.getSeriesCount() + ")";
        }
        else imageMatch = true;
      }
      if (success) {
        for (int series=0; series<seriesCount; series++) {
          if (series >= convertedReader.getSeriesCount()) {
            break;
          }
          convertedReader.setSeries(series);
          config.setSeries(series);

          int expectedX = config.getSizeX();
          int expectedY = config.getSizeY();
          expectRGB = config.isRGB();
          if (TestTools.shortClassName(writer).equals("OMEXMLWriter")) {
            expectRGB = false;
          }
          else if (TestTools.shortClassName(writer).equals("JPEGWriter")) {
            expectRGB = expectRGB || config.isIndexed();
          }

          int expectedPixelType =
            FormatTools.pixelTypeFromString(config.getPixelType());
          expectedCount = config.getSizeZ() * config.getSizeT() *
            (expectRGB ? 1 : config.getSizeC());

          String expectedMD5 = config.getMD5();

          int x = convertedReader.getSizeX();
          int y = convertedReader.getSizeY();
          int count = convertedReader.getImageCount();
          boolean rgb = convertedReader.isRGB();
          int pixelType = convertedReader.getPixelType();

          boolean isQuicktime =
            TestTools.shortClassName(writer).equals("QTWriter");

          String md5 = TestTools.md5(convertedReader.openBytes(0));

          if (msg == null) msg = checkMismatch(x, expectedX, series, "X");
          if (msg == null) msg = checkMismatch(y, expectedY, series, "Y");
          if (msg == null && writer.canDoStacks() && !imageMatch) {
            msg = checkMismatch(count, expectedCount, series, "Image count");
          }
          if (msg == null && !isQuicktime) {
            msg = checkMismatch(rgb, expectRGB, series, "RGB");
          }
          if (msg == null && !isQuicktime) {
            msg =
              checkMismatch(pixelType, expectedPixelType, series, "Pixel type");
          }
          if (msg == null && isLosslessWriter(writer) &&
            config.isRGB() == expectRGB)
          {
            msg = checkMismatch(md5, expectedMD5, series, "Pixels hash");
          }

          success = msg == null;
          if (!success) break;
        }
      }
      convertedReader.close();
    }
    catch (Throwable t) {
      LOGGER.info("", t);
      success = false;
    }
    result(testName, success, msg);
  }

  // -- Helper methods --

  private static String checkMismatch(boolean i1, boolean i2, int series,
    String label)
  {
    return checkMismatch(String.valueOf(i1), String.valueOf(i2), series, label);
  }

  private static String checkMismatch(int i1, int i2, int series, String label)
  {
    return checkMismatch(String.valueOf(i1), String.valueOf(i2), series, label);
  }

  private static String checkMismatch(String s1, String s2, int series,
    String label)
  {
    if (s1.equals(s2)) return null;
    return label + " mismatch [got " + s1 + ", expected " + s2 +
      "] in series " + series;
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

  private static boolean isLosslessWriter(IFormatWriter writer) {
    if ((writer instanceof JPEGWriter) || (writer instanceof JPEG2000Writer)) {
      return false;
    }
    String compression = writer.getCompression();
    if (compression != null) compression = compression.toLowerCase();
    if (compression == null || compression.equals("lzw") ||
      compression.equals("zlib") || compression.equals("uncompressed"))
    {
      return true;
    }
    return false;
  }

}
