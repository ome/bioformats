//
// ReaderTest.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats.test;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import junit.framework.*;
import loci.formats.*;

/**
 * JUnit tester for Bio-Formats file format readers.
 * Details on failed tests are written to a log file, for easier processing.
 *
 * To test the framework, run this class from the command line with a command
 * line argument indicating the root path of data files to be tested. The
 * path will be scanned, and a list of files to test will be built (files
 * matching those enumerated in bad-files.txt are excluded).
 *
 * Unfortunately, it is not practical to construct one large JUnit test suite
 * with a static suite() method, because many datasets are spread across
 * multiple files. For example, a collection of 100 TIFF files numbered
 * tiff001.tif through tiff100.tif should only be tested once, rather than 100
 * times. To solve this problem, the list of files to test is whittled down
 * dynamically -- after each file is tested, the Bio-Formats library reports
 * all files that are part of that same dataset (i.e., essentially, a list of
 * all files just tested). These files are all removed from the list, ensuring
 * each dataset is only tested once.
 *
 * As such, this test case is not well suited for use with most JUnit tools,
 * such as junit.awtui.TestRunner, junit.swingui.TestRunner, or
 * junit.textui.TestRunner. If you are interested enough in unit testing to
 * have read this explanation, and have any thoughts or suggestions for
 * improvement, your thoughts would be most welcome.
 */
public class ReaderTest extends TestCase {

  // -- Constants --

  /** Debugging flag. */
  private static final boolean DEBUG = true;

  // -- Static fields --

  private static Vector badFiles;
  private static FileWriter logFile;
  private String[] used;

  // -- Fields --

  private String id;
  private FileStitcher reader;

  // -- Constructor --
 
  public ReaderTest(String s) {
    super(s);
    throw new RuntimeException("Sorry, ReaderTest must be constructed with " +
      "a filename to read for performing the tests. See the class javadoc " +
      "for ReaderTest for more details.");
  }

  public ReaderTest(String s, String id) {
    super(s);
    this.id = id;
  }

  // -- ReaderTest API methods --

  /** Gets all constituent files in the tested dataset. */
  public String[] getUsedFiles() { return used; }

  // -- ReaderTest API methods - tests --

  /** 
   * Checks the SizeX and SizeY dimensions against the actual dimensions of
   * the BufferedImages.
   */
  public void testBufferedImageDimensions() {
    boolean success = true;
    try {
      for (int i=0; i<reader.getSeriesCount(id); i++) {
        reader.setSeries(id, i);
        int imageCount = reader.getImageCount(id);
        int sizeX = reader.getSizeX(id);
        int sizeY = reader.getSizeY(id);
        for (int j=0; j<imageCount; j++) {
          BufferedImage b = reader.openImage(id, j);
          boolean failW = b.getWidth() != sizeX;
          boolean failH = b.getHeight() != sizeY;
          if (failW) writeLog(id + " failed width test");
          if (failH) writeLog(id + " failed height test");
          if (failW || failH) {
            success = false;
            j = imageCount;
            i = reader.getSeriesCount(id);
            break;
          }
        }
      }
    }
    catch (Exception e) {
      if (DEBUG) e.printStackTrace();
      success = false; 
    }
    if (!success) writeLog(id + " failed BufferedImage test");
    assertTrue(success);
  }

  /**
   * Checks the SizeX and SizeY dimensions against the actual dimensions of
   * the byte array returned by openBytes.
   */
  public void testByteArrayDimensions() {
    boolean success = true;
    try {
      for (int i=0; i<reader.getSeriesCount(id); i++) {
        reader.setSeries(id, i);
        int imageCount = reader.getImageCount(id);
        int sizeX = reader.getSizeX(id);
        int sizeY = reader.getSizeY(id);
        int bytesPerPixel = 
          FormatReader.getBytesPerPixel(reader.getPixelType(id));
        int sizeC = reader.getSizeC(id);
        boolean rgb = reader.isRGB(id);

        int expectedBytes = sizeX * sizeY * bytesPerPixel * (rgb ? sizeC : 1);

        for (int j=0; j<imageCount; j++) {
          byte[] b = reader.openBytes(id, j);
          if (b.length != expectedBytes) {
            success = false; 
            j = imageCount;
            i = reader.getSeriesCount(id);
            break;
          }
        }
      }
    }
    catch (Exception e) { 
      if (DEBUG) e.printStackTrace();
      success = false; 
    }
    if (!success) writeLog(id + " failed byte array test");
    assertTrue(success);
  }

  /**
   * Checks the SizeZ, SizeC, and SizeT dimensions against the 
   * total image count.
   */
  public void testImageCount() {
    boolean success = true;
    try {
      for (int i=0; i<reader.getSeriesCount(id); i++) {
        reader.setSeries(id, i);
        int imageCount = reader.getImageCount(id);
        int sizeZ = reader.getSizeZ(id);
        int sizeC = reader.getEffectiveSizeC(id);
        int sizeT = reader.getSizeT(id);
        success = imageCount == sizeZ * sizeC * sizeT;
        if (!success) {
          writeLog(id + " failed image count test");
          assertTrue(false);
        }
      }
      assertTrue(success);
    }
    catch (Exception e) { 
      if (DEBUG) e.printStackTrace();
      writeLog(id + " failed image count test");
      assertTrue(false); 
    }
  }

  /**
   * Checks that the OME-XML attribute values match the values of the core
   * metadata (Size*, DimensionOrder, etc.).
   */
  public void testOMEXML() {
    try {
      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      store.createRoot();
      reader.setMetadataStore(store);

      boolean success = true;
      for (int i=0; i<reader.getSeries(id); i++) {
        reader.setSeries(id, i);
        int sizeX = reader.getSizeX(id);
        int sizeY = reader.getSizeY(id);
        int sizeZ = reader.getSizeZ(id);
        int sizeC = reader.getSizeC(id);
        int sizeT = reader.getSizeT(id);
        boolean bigEndian = !reader.isLittleEndian(id);
        String type = 
          FormatReader.getPixelTypeString(reader.getPixelType(id));
        String dimensionOrder = reader.getDimensionOrder(id);

        Integer ii = new Integer(i);
        boolean failX = sizeX != store.getSizeX(ii).intValue();
        boolean failY = sizeY != store.getSizeY(ii).intValue();
        boolean failZ = sizeZ != store.getSizeZ(ii).intValue();
        boolean failC = sizeC != store.getSizeC(ii).intValue();
        boolean failT = sizeT != store.getSizeT(ii).intValue();
        boolean failBE = bigEndian != store.getBigEndian(ii).booleanValue();
        boolean failType = !type.equalsIgnoreCase(store.getPixelType(ii));
        boolean failDE = !dimensionOrder.equals(store.getDimensionOrder(ii));
        if (failX) writeLog(id + " failed OME-XML SizeX test");
        if (failY) writeLog(id + " failed OME-XML SizeY test");
        if (failZ) writeLog(id + " failed OME-XML SizeZ test");
        if (failC) writeLog(id + " failed OME-XML SizeC test");
        if (failT) writeLog(id + " failed OME-XML SizeT test");
        if (failBE) writeLog(id + " failed OME-XML BigEndian test");
        if (failType) writeLog(id + " failed OME-XML PixelType test");
        if (failDE) {
          writeLog(id + " failed OME-XML DimensionOrder test");
        }
        if (failX || failY || failZ || failC || failT ||
          failBE || failType || failDE)
        {
          assertTrue(false);
        }
      }
      assertTrue(success); 
    }
    catch (Exception e) { 
      if (DEBUG) e.printStackTrace();
      writeLog(id + " failed OME-XML sanity test");
      assertTrue(false); 
    }
  }

  // -- TestCase API methods --

  /** Sets up the fixture. */
  protected void setUp() {
    reader = new FileStitcher();
  }

  /** Releases resources after tests have completed. */
  protected void tearDown() {
    try {
      used = reader.getUsedFiles(id);
      reader.close();
    }
    catch (FormatException fe) {
      if (DEBUG) fe.printStackTrace();
    }
    catch (IOException io) {
      if (DEBUG) io.printStackTrace();
    }
  }

  // -- Static ReaderTest API methods --

  /**
   * Creates a test suite for all ReaderTest tests, on the given file.
   * This method is patterned after the suite() method for use with a
   * TestRunner, but is distinct in that ReaderTest tests must be executed
   * on a particular input file.
   */
  public static TestSuite suite(String id) {
    TestSuite suite = new TestSuite();
    suite.addTest(new ReaderTest("testBufferedImageDimensions", id));
    suite.addTest(new ReaderTest("testByteArrayDimensions", id));
    suite.addTest(new ReaderTest("testImageCount", id));
    suite.addTest(new ReaderTest("testOMEXML", id));
    return suite;
  }

  /**
   * Determines if the given filename is a "bad" file.
   * Bad files are skipped rather than tested.
   */
  public static boolean isBadFile(String file) {
    if (badFiles == null) {
      try {
        badFiles = new Vector();
        BufferedReader in = new BufferedReader(new InputStreamReader(
          ReaderTest.class.getResourceAsStream("bad-files.txt")));
        while (true) {
          String line = in.readLine();
          if (line == null) break;
          badFiles.add(line);
        }
        in.close();
      }
      catch (IOException io) {
        if (DEBUG) io.printStackTrace();
      }
    }
    String absFile = new File(file).getAbsolutePath();
    for (int i=0; i<badFiles.size(); i++) {
      String bad = (String) badFiles.elementAt(i);
      if (absFile.endsWith(bad)) return true;
    }
    return false; 
  }

  /** Recursively generates a list of files to test. */
  public static void getFiles(String root, Vector files) {
    File f = new File(root);
    String[] subs = f.list();
    if (subs == null) {
      System.out.println("Invalid directory: " + root); 
      return;
    }
    ImageReader ir = new ImageReader();
    for (int i=0; i<subs.length; i++) {
      if (DEBUG) debug("Checking file " + subs[i]);
      subs[i] = root + File.separator + subs[i]; 
      if (isBadFile(subs[i])) {
        if (DEBUG) debug(subs[i] + " is a bad file");
        continue;
      }
      File file = new File(subs[i]);
      if (file.isDirectory()) getFiles(subs[i], files);
      else {
        if (ir.isThisType(subs[i])) {
          if (DEBUG) debug("Adding " + subs[i]);
          files.add(file.getAbsolutePath());
        }
        else if (DEBUG) debug(subs[i] + " has invalid type");
      }
    }
  }

  /** Writes the given message to the log file. */
  public static void writeLog(String s) {
    if (logFile == null) {
      try {
        logFile = new FileWriter(
          "bio-formats-test-" + new Date().toString() + ".log");
        logFile.flush();
      }
      catch (IOException io) {
        if (DEBUG) io.printStackTrace();
      }
    }
    try {
      logFile.write(s + "\n");
      logFile.flush();
    }
    catch (IOException exc) {
      if (DEBUG) exc.printStackTrace();
    }
  }

  public static void debug(String s) { System.out.println(s); }

  // -- Main method --

  public static void main(String[] args) {
    if (DEBUG) FormatReader.debug = true;
    Vector files = new Vector();  
    if (args == null || args.length == 0) {
      System.out.println(
        "Please specify root folder to search for data files.");
      System.exit(1);
    }
    System.out.print("Building file list...");
    if (DEBUG) System.out.println();
    getFiles(args[0], files);
    System.out.println(files.size() + " found.");
    while (files.size() > 0) {
      String id = (String) files.get(0);
      String pattern = FilePattern.findPattern(new File(id));
      System.out.println("Testing " + pattern);
      TestResult result = new TestResult();
      TestSuite suite = suite(id);
      suite.run(result);
      int total = result.runCount();
      int failed = result.failureCount();
      float failPercent = (float) (100 * ((double) failed / (double) total));
      System.out.println(id + " - " + failed + " failures in " +
        total + " tests (" + failPercent + "% failed)");

      // remove files part of the just-tested dataset from the list
      ReaderTest test = (ReaderTest) suite.testAt(0);
      String[] used = test.getUsedFiles();
      for (int i=0; i<used.length; i++) {
        if (DEBUG) System.out.println("Removing " + used[i]);
        files.removeElement(used[i]);
      }
    }
  }

}
