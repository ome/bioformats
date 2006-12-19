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
 * Failed tests are written to a logfile, for easier processing.
 */
public class ReaderTest extends TestCase {

  // -- Constants --

  /** Debugging flag. */
  private static final boolean DEBUG = false;

  // -- Fields --

  private static Vector badFiles;
  private static String currentFile;
  private static FileStitcher reader;
  private static FileWriter logFile;

  // -- Constructor --
 
  public ReaderTest(String s) {
    super(s);
  }

  // -- Static ReaderTest API methods --

  /** Makes our results available to a TestRunner. */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new ReaderTest("testBufferedImageDimensions"));
    suite.addTest(new ReaderTest("testByteArrayDimensions"));
    suite.addTest(new ReaderTest("testImageCount"));
    suite.addTest(new ReaderTest("testOMEXML"));
    return suite;
  }

  /** Sets the file to process. */
  public static void setFile(String file) {
    currentFile = file;
  }

  /**
   * Determines if the given filename is a "bad" file.
   * Bad files are skipped rather than tested.
   */
  public static boolean isBadFile(String file) {
    if (badFiles == null) {
      try {
        badFiles = new Vector();
        BufferedReader in = new BufferedReader(new FileReader("bad-files.txt"));
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
    if (badFiles.contains(file)) return true;
    for (int i=0; i<badFiles.size(); i++) {
      if (file.endsWith((String) badFiles.get(i))) return true;
    }
    return false; 
  }

  /** Resets the format reader. */
  public static void resetReader() {
    if (reader != null) {
      try {
        reader.close();
      }
      catch (Exception e) {
        if (DEBUG) e.printStackTrace();
      }
    }
    reader = new FileStitcher();
    OMEXMLMetadataStore store = new OMEXMLMetadataStore();
    store.createRoot();
    reader.setMetadataStore(store);
  }

  /** Recursively generates a list of files to test. */
  public static void getFiles(String root, Vector files) {
    resetReader();
    File f = new File(root);
    String[] subs = f.list();
    if (subs == null) {
      System.out.println("Invalid directory: " + root); 
      return;
    }
    for (int i=0; i<subs.length; i++) {
      if (DEBUG) debug("Checking file " + subs[i]);
      if (isBadFile(subs[i])) {
        if (DEBUG) debug(subs[i] + " is a bad file");
        continue;
      }
      subs[i] = root + File.separator + subs[i]; 
      if (new File(subs[i]).isDirectory()) getFiles(subs[i], files);
      else {
        if (reader.isThisType(subs[i])) {
          if (DEBUG) debug("Adding " + subs[i]);
          files.add(subs[i]);
        }
        else if (DEBUG) debug(subs[i] + " has invalid type");
      }
    }
  }

  /** Writes the given message to the log file. */
  public static void writeLog(String s) {
    if (logFile == null) {
      Date date = new Date();
      try {
        logFile = new FileWriter("bioformats-test-" + date.toString() + ".log");
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

  // -- ReaderTest API methods - tests --

  /** 
   * Checks the SizeX and SizeY dimensions against the actual dimensions of
   * the BufferedImages.
   */
  public void testBufferedImageDimensions() {
    boolean success = true;
    try {
      for (int i=0; i<reader.getSeriesCount(currentFile); i++) {
        reader.setSeries(currentFile, i);
        int imageCount = reader.getImageCount(currentFile);
        int sizeX = reader.getSizeX(currentFile);
        int sizeY = reader.getSizeY(currentFile);
        for (int j=0; j<imageCount; j++) {
          BufferedImage b = reader.openImage(currentFile, j);
          boolean failW = b.getWidth() != sizeX;
          boolean failH = b.getHeight() != sizeY;
          if (failW) writeLog(currentFile + " failed width test");
          if (failH) writeLog(currentFile + " failed height test");
          if (failW || failH) {
            success = false;
            j = imageCount;
            i = reader.getSeriesCount(currentFile);
            break;
          }
        }
      }
    }
    catch (Exception e) {
      if (DEBUG) e.printStackTrace();
      success = false; 
    }
    if (!success) writeLog(currentFile + " failed BufferedImage test");
    assertTrue(success);
  }

  /**
   * Checks the SizeX and SizeY dimensions against the actual dimensions of
   * the byte array returned by openBytes.
   */
  public void testByteArrayDimensions() {
    boolean success = true;
    try {
      for (int i=0; i<reader.getSeriesCount(currentFile); i++) {
        reader.setSeries(currentFile, i);
        int imageCount = reader.getImageCount(currentFile);
        int sizeX = reader.getSizeX(currentFile);
        int sizeY = reader.getSizeY(currentFile);
        int bytesPerPixel = 
          FormatReader.getBytesPerPixel(reader.getPixelType(currentFile));
        int sizeC = reader.getSizeC(currentFile);
        boolean rgb = reader.isRGB(currentFile);

        int expectedBytes = sizeX * sizeY * bytesPerPixel * (rgb ? sizeC : 1);

        for (int j=0; j<imageCount; j++) {
          byte[] b = reader.openBytes(currentFile, j);
          if (b.length != expectedBytes) {
            success = false; 
            j = imageCount;
            i = reader.getSeriesCount(currentFile);
            break;
          }
        }
      }
    }
    catch (Exception e) { 
      if (DEBUG) e.printStackTrace();
      success = false; 
    }
    if (!success) writeLog(currentFile + " failed byte array test");
    assertTrue(success);
  }

  /**
   * Checks the SizeZ, SizeC, and SizeT dimensions against the 
   * total image count.
   */
  public void testImageCount() {
    boolean success = true;
    try {
      for (int i=0; i<reader.getSeriesCount(currentFile); i++) {
        reader.setSeries(currentFile, i);
        int imageCount = reader.getImageCount(currentFile);
        int sizeZ = reader.getSizeZ(currentFile);
        int sizeC = reader.getEffectiveSizeC(currentFile);
        int sizeT = reader.getSizeT(currentFile);
        success = imageCount == sizeZ * sizeC * sizeT;
        if (!success) {
          writeLog(currentFile + " failed image count test");
          assertTrue(false);
        }
      }
      assertTrue(success);
    }
    catch (Exception e) { 
      if (DEBUG) e.printStackTrace();
      writeLog(currentFile + " failed image count test");
      assertTrue(false); 
    }
  }

  /**
   * Checks that the OME-XML attribute values match the values of the core
   * metadata (Size*, DimensionOrder, etc.).
   */
  public void testOMEXML() {
    try {
      OMEXMLMetadataStore store = 
        (OMEXMLMetadataStore) reader.getMetadataStore(currentFile);
 
      boolean success = true;
      for (int i=0; i<reader.getSeries(currentFile); i++) {
        reader.setSeries(currentFile, i);
        int sizeX = reader.getSizeX(currentFile);
        int sizeY = reader.getSizeY(currentFile);
        int sizeZ = reader.getSizeZ(currentFile);
        int sizeC = reader.getSizeC(currentFile);
        int sizeT = reader.getSizeT(currentFile);
        boolean bigEndian = !reader.isLittleEndian(currentFile);
        String type = 
          FormatReader.getPixelTypeString(reader.getPixelType(currentFile));
        String dimensionOrder = reader.getDimensionOrder(currentFile);

        Integer ii = new Integer(i);
        boolean failX = sizeX != store.getSizeX(ii).intValue();
        boolean failY = sizeY != store.getSizeY(ii).intValue();
        boolean failZ = sizeZ != store.getSizeZ(ii).intValue();
        boolean failC = sizeC != store.getSizeC(ii).intValue();
        boolean failT = sizeT != store.getSizeT(ii).intValue();
        boolean failBE = bigEndian != store.getBigEndian(ii).booleanValue();
        boolean failType = !type.equalsIgnoreCase(store.getPixelType(ii));
        boolean failDE = !dimensionOrder.equals(store.getDimensionOrder(ii));
        if (failX) writeLog(currentFile + " failed OME-XML SizeX test");
        if (failY) writeLog(currentFile + " failed OME-XML SizeY test");
        if (failZ) writeLog(currentFile + " failed OME-XML SizeZ test");
        if (failC) writeLog(currentFile + " failed OME-XML SizeC test");
        if (failT) writeLog(currentFile + " failed OME-XML SizeT test");
        if (failBE) writeLog(currentFile + " failed OME-XML BigEndian test");
        if (failType) writeLog(currentFile + " failed OME-XML PixelType test");
        if (failDE) {
          writeLog(currentFile + " failed OME-XML DimensionOrder test");
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
      writeLog(currentFile + " failed OME-XML sanity test");
      assertTrue(false); 
    }
  }

  // -- TestCase API methods --

  /** Releases resources after tests have completed. */
  protected void tearDown() {
    try {
      reader.close();
      badFiles = null;
      if (logFile != null) {
        logFile.close();
        logFile = null;
      }
    }
    catch (FormatException fe) {
      if (DEBUG) fe.printStackTrace();
    }
    catch (IOException io) {
      if (DEBUG) io.printStackTrace();
    }
  }

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
      String file = (String) files.get(0);
      try {
        FilePattern fp = reader.getFilePattern(file);
        System.out.println("Testing " + fp.getPattern());
      }
      catch (FormatException exc) { exc.printStackTrace(); }
      catch (IOException exc) { exc.printStackTrace(); }
      setFile(file);
      TestResult result = new TestResult();
      suite().run(result);
      int total = result.runCount();
      int failed = result.failureCount();
      float failPercent = (float) (100 * ((double) failed / (double) total));
      System.out.println(file + " - " + failed + " failures in " +
        total + " tests (" + failPercent + "% failed)");

      // remove relevant files from the list
      try {
        String[] used = reader.getUsedFiles(file);
        for (int i=0; i<used.length; i++) {
          if (DEBUG) System.out.println("Removing " + used[i]);
          files.removeElement(used[i]);
        }
      }
      catch (FormatException exc) { exc.printStackTrace(); }
      catch (IOException exc) { exc.printStackTrace(); }

      resetReader();
    }
  }

}
