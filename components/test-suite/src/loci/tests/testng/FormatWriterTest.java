//
// FormatWriterTest.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import loci.common.LogTools;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.JPEG2000Writer;
import loci.formats.out.JPEGWriter;

/**
 * TestNG tester for Bio-Formats file format writers.
 * Details on failed tests are written to a log file, for easier processing.
 *
 * NB: {@link loci.formats.ome} and ome-xml.jar
 * are required for some of the tests.
 *
 * To run tests:
 * ant -Dtestng.directory="/path" -Dtestng.multiplier="1.0" test-all
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/test-suite/src/loci/tests/testng/FormatWriterTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/test-suite/src/loci/tests/testng/FormatWriterTest.java">SVN</a></dd></dl>
 */
public class FormatWriterTest {

  /** Message to give for why a test was skipped. */
  private static final String SKIP_MESSAGE = "Dataset already tested.";

  // -- Static fields --

  /** Configuration tree structure containing dataset metadata. */
  public static ConfigurationTree config;

  /** List of files to skip. */
  public static List skipFiles = new LinkedList();

  /** Global shared reader for use in all tests. */
  private static IFormatReader reader;

  // -- Fields --

  private String id;
  private boolean skip = false;

  // -- Data provider --

  /**
   * @testng.data-provider name="getWriterList"
   */
  public Object[][] getWriterList() {
    createLogFile();
    IFormatWriter[] writers = new ImageWriter().getWriters();
    Vector tmp = new Vector();
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
          w.setCompression(compressionTypes[q]);
          tmp.add(w);
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

  /**
   * @testng.test groups = "all"
   *              dataProvider = "getWriterList"
   */
  public void testWriterConsistency(IFormatWriter writer) {
    String testName =
      TestTools.shortClassName(writer) + " testWriterConsistency";
    if (!initFile()) {
      result(testName, false, null);
      return;
    }
    String file = reader.getCurrentFile();
    boolean success = true;
    String msg = null;
    try {
      int type = reader.getPixelType();
      if (!writer.isSupportedType(type)) {
        success = true;
        result(testName, success, msg);
        return;
      }

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
      writer.setMetadataRetrieve((MetadataRetrieve) meta);

      // convert the input file
      writer.setId(convertedFile);

      int seriesCount = writer.canDoStacks() ? reader.getSeriesCount() : 1;

      for (int series=0; series<seriesCount; series++) {
        reader.setSeries(series);
        boolean lastSeries = series == seriesCount - 1;
        int imageCount = writer.canDoStacks() ? reader.getImageCount() : 1;
        for (int image=0; image<imageCount; image++) {
          boolean lastImage = image == imageCount - 1;
          writer.saveImage(reader.openImage(image), series,
            lastImage, lastImage && lastSeries);
        }
      }
      writer.close();

      // verify that the dimensions are accurate

      reader.setId(convertedFile);

      boolean seriesMatch = reader.getSeriesCount() == config.getNumSeries();
      if (!seriesMatch && writer.canDoStacks()) {
        success = false;
        msg = "Series counts do not match (found " + reader.getSeriesCount() +
          ", expected " + config.getNumSeries() + ")";
      }
      if (success) {
        for (int series=0; series<seriesCount; series++) {
          reader.setSeries(series);
          config.setSeries(series);

          int expectedX = config.getX();
          int expectedY = config.getY();
          boolean expectRGB = config.isRGB();
          int expectedPixelType = config.getPixelType();
          int expectedCount =
            config.getZ() * config.getT() * (expectRGB ? 1 : config.getC());
          if (TestTools.shortClassName(writer).equals("OMEXMLWriter")) {
            expectedCount *= config.getC();
            expectRGB = false;
          }

          String expectedMD5 = config.getMD5();

          int x = reader.getSizeX();
          int y = reader.getSizeY();
          int count = reader.getImageCount();
          boolean rgb = reader.isRGB();
          int pixelType = reader.getPixelType();

          String md5 = TestTools.md5(reader.openBytes(0));

          if (msg == null) msg = checkMismatch(x, expectedX, series, "X");
          if (msg == null) msg = checkMismatch(y, expectedY, series, "Y");
          if (msg == null && writer.canDoStacks()) {
            msg = checkMismatch(count, expectedCount, series, "Image count");
          }
          if (msg == null) msg = checkMismatch(rgb, expectRGB, series, "RGB");
          if (msg == null) {
            msg =
              checkMismatch(pixelType, expectedPixelType, series, "Pixel type");
          }
          if (msg == null && isLosslessWriter(writer)) {
            msg = checkMismatch(md5, expectedMD5, series, "Pixels hash");
          }

          success = msg == null;
          if (!success) break;
        }
      }
      reader.close();
    }
    catch (Throwable t) {
      LogTools.trace(t);
      success = false;
    }
    result(testName, success, msg);
  }

  // -- Helper methods --

  /** Initializes the reader and configuration tree. */
  private boolean initFile() {
    if (id == null) {
      id = System.getProperty("testng.filename");
      if (id == null) return false;
    }
    try {
      if (reader == null) reader = new ImageReader();
      reader.close();
      MetadataStore store = MetadataTools.createOMEXMLMetadata();
      reader.setMetadataStore(store);
      reader.setId(id);

      File f = new File(id);
      String parent = f.getParentFile().getAbsolutePath();
      config = new ConfigurationTree(parent);
      String configFile = parent + File.separator + ".bioformats";
      if (new File(configFile).exists()) {
        config.parseConfigFile(configFile);
      }
      config.setId(id);
    }
    catch (Throwable t) {
      LogTools.trace(t);
      return false;
    }
    return true;
  }

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

  /** Creates a new log file. */
  public static void createLogFile() {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    StringBuffer dateBuf = new StringBuffer();
    fmt.format(new Date(), dateBuf, new FieldPosition(0));
    String logFile = "loci-software-test-" + dateBuf + ".log";
    LogTools.println("Output logged to " + logFile);
    try {
      LogTools.getLog().setStream(
        new PrintStream(new FileOutputStream(logFile)));
    }
    catch (IOException e) { LogTools.println(e); }

    // close log file on exit
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        LogTools.println(TestTools.DIVIDER);
        LogTools.println("Test suite complete.");
        LogTools.getLog().getStream().close();
      }
    });
  }

  /**
   * Outputs test result with optional extra message
   * and generates appropriate assertion.
   */
  private static void result(String testName, boolean success, String msg) {
    LogTools.println("\t" + TestTools.timestamp() + ": " + testName + ": " +
      (success ? "PASSED" : "FAILED") + (msg == null ? "" : " (" + msg + ")"));
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
