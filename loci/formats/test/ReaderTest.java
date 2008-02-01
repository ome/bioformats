//
// ReaderTest.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

/**
 * TestNG tester for Bio-Formats file format readers.
 * Details on failed tests are written to a log file, for easier processing.
 *
 * NB: {@link loci.formats.ome} and ome-java.jar
 * are required for some of the tests.
 *
 * To run tests:
 * java -ea -mx512m -Dtestng.directory="/path/" -Dtestng.multiplier="1.0" \
 *   org.testng.TestNG -sourcedir . testng.xml
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/test/ReaderTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/test/ReaderTest.java">SVN</a></dd></dl>
 */
public class ReaderTest {

  // -- Fields --

  /** Whether or not this is the first time calling the data provider. */
  private boolean isFirstTime = true;

  /** List of files to test. */
  private String[][] toTest;

  /** List of configuration files. */
  private static Vector configFiles = new Vector();

  /** Configuration file reader. */
  private static ConfigurationFiles config = ConfigurationFiles.newInstance();

  /** Current log file. */
  private static FileWriter logFile;

  // -- Data provider --

  /**
   * @testng.data-provider name = "provider"
   */
  public Object[][] createData() {
    if (isFirstTime) {
      Vector v = new Vector();
      getFiles(System.getProperty("testng.directory"), v);
      v = removeDuplicates(v);
      isFirstTime = false;
      String[] o = (String[]) v.toArray(new String[0]);
      toTest = new String[o.length][1];
      for (int i=0; i<o.length; i++) {
        toTest[i][0] = o[i];
      }
    }
    return toTest;
  }

  // -- Tests --

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all pixels"
   */
  public void testBufferedImageDimensions(String file) {
    try {
      FileStitcher reader = new FileStitcher();
      reader.setId(file);

      boolean success = true;
      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);

        int x = reader.getSizeX();
        int y = reader.getSizeY();
        int c = reader.getRGBChannelCount();
        int type = reader.getPixelType();

        for (int j=0; j<reader.getImageCount(); j++) {
          BufferedImage b = reader.openImage(j);
          boolean failX = b.getWidth() != x;
          boolean failY = b.getHeight() != y;
          boolean failC = b.getRaster().getNumBands() <= c;
          boolean failType = ImageTools.getPixelType(b) != type;

          success = failX || failY || failC || failType;
          if (!success) {
            writeLog(file + " failed BufferedImage test");
            break;
          }
        }
      }
      assert success;
    }
    catch (FormatException exc) {
      writeLog(file + " failed BufferedImage test");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed BufferedImage test");
      writeLog(exc);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all pixels"
   */
  public void testByteArrayDimensions(String file) {
    try {
      boolean success = true;
      FileStitcher reader = new FileStitcher();
      reader.setId(file);
      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);
        int x = reader.getSizeX();
        int y = reader.getSizeY();
        int c = reader.isIndexed() ? 1 : reader.getRGBChannelCount();
        int bytes = FormatTools.getBytesPerPixel(reader.getPixelType());

        int expected = x * y * c * bytes;

        for (int j=0; j<reader.getImageCount(); j++) {
          byte[] b = reader.openBytes(j);
          if (b.length < expected) {
            success = false;
            break;
          }
        }
      }
      if (!success) writeLog(file + " failed byte array test");
      assert success;
    }
    catch (FormatException exc) {
      writeLog(file + " failed byte array test");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed byte array test");
      writeLog(exc);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all pixels"
   */
  public void testThumbnailImageDimensions(String file) {
    try {
      FileStitcher reader = new FileStitcher();
      reader.setNormalized(true);
      reader.setId(file);

      boolean success = true;
      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);

        int x = reader.getThumbSizeX();
        int y = reader.getThumbSizeY();
        int c = reader.getRGBChannelCount();
        int type = reader.getPixelType();

        for (int j=0; j<reader.getImageCount(); j++) {
          BufferedImage b = reader.openThumbImage(j);
          boolean failX = b.getWidth() != x;
          boolean failY = b.getHeight() != y;
          boolean failC = b.getRaster().getNumBands() <= c;
          boolean failType = ImageTools.getPixelType(b) != type;

          success = failX || failY || failC || failType;
          if (!success) {
            writeLog(file + " failed thumbnail BufferedImage test");
            break;
          }
        }
      }
      assert success;
    }
    catch (FormatException exc) {
      writeLog(file + " failed thumbnail BufferedImage test");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed thumbnail BufferedImage test");
      writeLog(exc);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all pixels"
   */
  public void testThumbnailByteArrayDimensions(String file) {
    try {
      boolean success = true;
      FileStitcher reader = new FileStitcher();
      reader.setNormalized(true);
      reader.setId(file);
      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);
        int x = reader.getThumbSizeX();
        int y = reader.getThumbSizeY();
        int c = reader.isIndexed() ? 1 : reader.getRGBChannelCount();
        int bytes = FormatTools.getBytesPerPixel(reader.getPixelType());

        int expected = x * y * c * bytes;

        for (int j=0; j<reader.getImageCount(); j++) {
          byte[] b = reader.openThumbBytes(j);
          if (b.length < expected) {
            success = false;
            break;
          }
        }
      }
      if (!success) writeLog(file + " failed thumbnail byte array test");
      assert success;
    }
    catch (FormatException exc) {
      writeLog(file + " failed thumbnail byte array test");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed thumbnail byte array test");
      writeLog(exc);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all fast"
   */
  public void testImageCount(String file) {
    try {
      FileStitcher reader = new FileStitcher();
      reader.setId(file);
      boolean success = true;
      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);
        int imageCount = reader.getImageCount();
        int z = reader.getSizeZ();
        int c = reader.getEffectiveSizeC();
        int t = reader.getSizeT();
        if (imageCount != z * c * t) {
          success = false;
          break;
        }
      }
      reader.close();
      if (!success) writeLog(file + " failed image count test");
      assert success;
    }
    catch (FormatException exc) {
      writeLog(file + " failed image count test");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed image count test");
      writeLog(exc);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all xml fast"
   */
  public void testOMEXML(String file) {
    try {
      MetadataStore omexmlMeta = MetadataTools.createOMEXMLMetadata();
      FileStitcher reader = new FileStitcher();
      reader.setMetadataStore(omexmlMeta);
      reader.setId(file);

      MetadataRetrieve retrieve = (MetadataRetrieve) reader.getMetadataStore();

      boolean success = true;
      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);

        String type = FormatTools.getPixelTypeString(reader.getPixelType());

        boolean failX = reader.getSizeX() !=
          retrieve.getPixelsSizeX(i, 0).intValue();
        boolean failY = reader.getSizeY() !=
          retrieve.getPixelsSizeY(i, 0).intValue();
        boolean failZ = reader.getSizeZ() !=
          retrieve.getPixelsSizeZ(i, 0).intValue();
        boolean failC = reader.getSizeC() !=
          retrieve.getPixelsSizeC(i, 0).intValue();
        boolean failT = reader.getSizeT() !=
          retrieve.getPixelsSizeT(i, 0).intValue();
        boolean failBE = reader.isLittleEndian() ==
          retrieve.getPixelsBigEndian(i, 0).booleanValue();
        boolean failDE = !reader.getDimensionOrder().equals(
          retrieve.getPixelsDimensionOrder(i, 0));
        boolean failType = !type.equalsIgnoreCase(
          retrieve.getPixelsPixelType(i, 0));

        if (success) {
          success = !(failX || failY || failZ || failC || failT || failBE ||
            failDE || failType);
        }
        if (!success) break;
      }
      if (!success) writeLog(file + " failed OME-XML sanity test");
      assert success;
    }
    catch (FormatException exc) {
      writeLog(file + " failed OME-XML sanity test");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed OME-XML sanity test");
      writeLog(exc);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all fast"
   */
  public void testConsistent(String file) {
    try {
      FileStitcher r = new FileStitcher();
      r.setId(file);

      boolean success = true;
      boolean failSeries = r.getSeriesCount() != config.getNumSeries(file);

      if (failSeries) {
        writeLog(file + " failed consistent metadata (wrong series count)");
        assert false;
        return;
      }

      for (int i=0; i<r.getSeriesCount(); i++) {
        r.setSeries(i);
        config.setSeries(file, i);

        boolean failX = config.getWidth(file) != r.getSizeX();
        boolean failY = config.getHeight(file) != r.getSizeY();
        boolean failZ = config.getZ(file) != r.getSizeZ();
        boolean failC = config.getC(file) != r.getSizeC();
        boolean failT = config.getT(file) != r.getSizeT();
        boolean failDim =
          !config.getDimOrder(file).equals(r.getDimensionOrder());
        boolean failInt = config.isInterleaved(file) != r.isInterleaved();
        boolean failRGB = config.isRGB(file) != r.isRGB();
        boolean failTX = config.getThumbX(file) != r.getThumbSizeX();
        boolean failTY = config.getThumbY(file) != r.getThumbSizeY();
        boolean failType = config.getPixelType(file) != r.getPixelType();
        boolean failEndian = config.isLittleEndian(file) != r.isLittleEndian();

        success = !(failX || failY || failZ || failC || failT || failDim ||
          failInt || failRGB || failTX || failTY || failType || failEndian);

        if (!success) {
          writeLog(file + " failed consistent metadata test");
          assert false;
          return;
        }
      }
      assert true;
    }
    catch (FormatException exc) {
      writeLog(file + " failed consistent metadata test");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed consistent metadata test");
      writeLog(exc);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all"
   */
  public void testMemoryUsage(String file) {
    try {
      Runtime r = Runtime.getRuntime();
      int maxMemory = (int) ((r.totalMemory() - r.freeMemory()) >> 20);
      int initialMemory = maxMemory;

      FileStitcher reader = new FileStitcher();
      reader.setId(file);
      int mem = (int) ((r.totalMemory() - r.freeMemory()) >> 20);
      if (mem > maxMemory) maxMemory = mem;

      for (int i=0; i<reader.getImageCount(); i++) {
        BufferedImage b = reader.openImage(i);
        mem = (int) ((r.totalMemory() - r.freeMemory()) >> 20);
        if (mem > maxMemory) maxMemory = mem;
      }
      int finalMemory = (int) ((r.totalMemory() - r.freeMemory()) >> 20);

      boolean success = true;

      // max memory usage should be no more than twice the file size
      if (maxMemory - initialMemory > 2*(config.getFileSize(file) + 1)) {
        success = false;
        writeLog(file + " failed memory test (used " +
          (maxMemory - initialMemory) + "MB; expected <= " +
          (2*config.getFileSize(file) + 1) + "MB)");
      }

      // check that the reader doesn't have any significant memory leaks
      if (finalMemory - initialMemory > 10) {
        success = false;
        writeLog(file + " failed memory leak test (" +
          (finalMemory - initialMemory) + "MB leaked)");
      }
      assert success;
    }
    catch (FormatException e) {
      writeLog(file + " failed memory test");
      writeLog(e);
      assert false;
    }
    catch (IOException e) {
      writeLog(file + " failed memory test");
      writeLog(e);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all"
   */
  public void testAccessTime(String file) {
    try {
      float timeMultiplier =
        Float.parseFloat(System.getProperty("testng.multiplier"));

      FileStitcher reader = new FileStitcher();
      reader.setId(file);

      long l1 = System.currentTimeMillis();
      for (int i=0; i<reader.getImageCount(); i++) {
        reader.openImage(i);
      }
      long l2 = System.currentTimeMillis();
      if (((l2 - l1) / reader.getImageCount()) -
        timeMultiplier*config.getTimePerPlane(file) > 20.0)
      {
        writeLog(file + " failed consistent access time test (got " +
          ((l2 - l1) / reader.getImageCount()) + " ms, expected " +
          config.getTimePerPlane(file) + " ms)");
        assert false;
        return;
      }
      assert true;
    }
    catch (FormatException e) {
      writeLog(file + " failed consistent access time test");
      writeLog(e);
      assert false;
    }
    catch (IOException e) {
      writeLog(file + " failed consistent access time test");
      writeLog(e);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all"
   */
  public void testSaneUsedFiles(String file) {
    try {
      FileStitcher reader = new FileStitcher();
      reader.setId(file);

      String[] base = reader.getUsedFiles();
      Arrays.sort(base);

      for (int i=0; i<base.length; i++) {
        reader.setId(base[i]);
        String[] comp = reader.getUsedFiles();
        Arrays.sort(comp);
        for (int j=0; j<comp.length; j++) {
          if (!comp[j].equals(base[j])) {
            writeLog(file + " failed sane used files test (" + base[i] + ")");
            assert false;
            return;
          }
        }
      }
      reader.close();
      assert true;
    }
    catch (FormatException e) {
      writeLog(file + " failed sane used files test");
      writeLog(e);
      assert false;
    }
    catch (IOException e) {
      writeLog(file + " failed sane used files test");
      writeLog(e);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all xml fast"
   */
  public void testValidXML(String file) {
    try {
      MetadataStore omexmlMeta = MetadataTools.createOMEXMLMetadata();
      FileStitcher reader = new FileStitcher();
      reader.setMetadataStore(omexmlMeta);
      reader.setId(file);

      MetadataRetrieve retrieve =
        (MetadataRetrieve) reader.getMetadataStore();

      String xml = MetadataTools.getOMEXML(retrieve);
      if (xml == null) writeLog(file + " failed OME-XML validation");
      reader.close();
      assert xml != null;
    }
    catch (FormatException e) {
      writeLog(file + " failed OME-XML validation");
      writeLog(e);
      assert false;
    }
    catch (IOException e) {
      writeLog(file + " failed OME-XML validation");
      writeLog(e);
      assert false;
    }
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "all pixels"
   */
  public void testPixelsHashes(String file) {
    boolean success = true;
    try {
      // check the MD5 of the first plane in each series

      FileStitcher reader = new FileStitcher();
      reader.setId(file);

      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);
        config.setSeries(file, i);
        String md5 = md5(reader.openBytes(0));
        if (!md5.equals(config.getMD5(file))) {
          writeLog(file + " failed pixels consistency (series " + i + ")");
          success = false;
          assert success;
          return;
        }
      }
    }
    catch (FormatException exc) {
      writeLog(file + " failed pixels consistency");
      writeLog(exc);
      assert false;
    }
    catch (IOException exc) {
      writeLog(file + " failed pixels consistency");
      writeLog(exc);
      assert false;
    }
    assert success;
  }

  /**
   * @testng.test dataProvider = "provider"
   *              groups = "config"
   */
  public void writeConfigFiles(String file) {
    Exception exc = null;
    try {
      FileStitcher reader = new FileStitcher();
      reader.setId(file);

      StringBuffer line = new StringBuffer();
      line.append("\"");
      line.append(new Location(file).getName());
      line.append("\" total_series=");
      line.append(reader.getSeriesCount());
      long start = System.currentTimeMillis();
      int total = 0;
      for (int i=0; i<reader.getSeriesCount(); i++) {
        reader.setSeries(i);
        line.append(" [series=");
        line.append(i);
        line.append(" x=" + reader.getSizeX());
        line.append(" y=" + reader.getSizeY());
        line.append(" z=" + reader.getSizeZ());
        line.append(" c=" + reader.getSizeC());
        line.append(" t=" + reader.getSizeT());
        line.append(" order=" + reader.getDimensionOrder());
        line.append(" interleave=" + reader.isInterleaved());
        line.append(" rgb=" + reader.isRGB());
        line.append(" thumbx=" + reader.getThumbSizeX());
        line.append(" thumby=" + reader.getThumbSizeY());
        line.append(" type=" +
          FormatTools.getPixelTypeString(reader.getPixelType()));
        line.append(" little=" + reader.isLittleEndian());
        line.append(" md5=" + md5(reader.openBytes(0)));
        line.append("]");

        total += reader.getImageCount();
        for (int j=1; j<reader.getImageCount(); j++) reader.openImage(j);
      }
      long end = System.currentTimeMillis();

      line.append(" access=");
      line.append((end - start) / total);
      line.append(" mem=");
      line.append(new RandomAccessStream(file).length());
      line.append(" test=true\n");

      File f = new File(new Location(file).getParent(), ".bioformats");
      BufferedWriter w = new BufferedWriter(new FileWriter(f, true));
      w.write(line.toString());
      w.close();
      assert true;
    }
    catch (FormatException e) { exc = e; }
    catch (IOException e) { exc = e; }
    if (exc != null) {
      try {
        File f = new File(new Location(file).getParent(), ".bioformats");
        BufferedWriter w = new BufferedWriter(new FileWriter(f, true));
        w.write("\"" + file + "\" test=false\n");
        w.close();
        assert true;
      }
      catch (IOException e) { }
    }
  }

  // -- Helper methods --

  /** Calculate the MD5 of a byte array. */
  private static String md5(byte[] b) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.reset();
      md.update(b);
      byte[] digest = md.digest();
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<digest.length; i++) {
        String a = Integer.toHexString(0xff & digest[i]);
        if (a.length() == 1) a = "0" + a;
        sb.append(a);
      }
      return sb.toString();
    }
    catch (NoSuchAlgorithmException e) { writeLog(e); }
    return null;
  }

  /** Writes the given message to the log file. */
  private static void writeLog(String s) {
    if (logFile == null) createLogFile();
    LogTools.println(s);
    LogTools.flush();
  }

  /** Writes the given exception's stack trace to the log file. */
  private static void writeLog(Exception e) {
    if (logFile == null) createLogFile();
    LogTools.trace(e);
    LogTools.flush();
  }

  /** Creates a new log file. */
  private static void createLogFile() {
    try {
      String date = new Date().toString().replaceAll(":", "-");
      logFile = new FileWriter("bio-formats-test-" + date + ".log");
      TestLogger log = new TestLogger(logFile);
      LogTools.setLog(log);
    }
    catch (IOException e) { }
  }

  /** Recursively generate a list of files to test. */
  private static void getFiles(String root, Vector files) {
    Location f = new Location(root);
    String[] subs = f.list();
    if (subs == null) subs = new String[0];
    Arrays.sort(subs);

    // make sure that if a config file exists, it is first on the list
    for (int i=0; i<subs.length; i++) {
      if (subs[i].endsWith(".bioformats") && i != 0) {
        String tmp = subs[0];
        subs[0] = subs[i];
        subs[i] = tmp;
        break;
      }
    }

    if (subs == null) {
      LogTools.println("Invalid directory: " + root);
      return;
    }

    ImageReader ir = new ImageReader();
    Vector similarFiles = new Vector();

    for (int i=0; i<subs.length; i++) {
      LogTools.println("Checking file " + subs[i]);
      subs[i] = new Location(root, subs[i]).getAbsolutePath();
      if (isBadFile(subs[i]) || similarFiles.contains(subs[i])) {
        LogTools.println(subs[i] + " is a bad file");
        String[] matching = new FilePattern(subs[i]).getFiles();
        for (int j=0; j<matching.length; j++) {
          similarFiles.add(new Location(root, matching[j]).getAbsolutePath());
        }
        continue;
      }
      Location file = new Location(subs[i]);

      if (file.isDirectory()) getFiles(subs[i], files);
      else if (file.getName().equals(".bioformats")) {
        // special config file for the test suite
        configFiles.add(file.getAbsolutePath());
      }
      else {
        if (ir.isThisType(subs[i])) {
          LogTools.println("Adding " + subs[i]);
          files.add(file.getAbsolutePath());
        }
        else LogTools.println(subs[i] + " has invalid type");
      }
      file = null;
    }
  }

  /** Determines if the given file is "bad" (bad files are not tested). */
  private static boolean isBadFile(String file) {
    for (int i=0; i<configFiles.size(); i++) {
      try {
        String s = (String) configFiles.get(i);
        if (!config.isParsed(s)) {
          config.addFile(s);
        }
      }
      catch (IOException exc) {
        LogTools.trace(exc);
      }
    }
    return !config.testFile(file) && !file.endsWith(".bioformats");
  }

  /** Remove duplicates from a file list. */
  private static Vector removeDuplicates(Vector files) {
    Vector rtn = new Vector();
    try {
      for (int i=0; i<files.size(); i++) {
        if (files.get(i) == null) continue;
        FileStitcher f = new FileStitcher();
        f.setId((String) files.get(i));
        String[] used = f.getUsedFiles();
        rtn.add(used[0]);
        for (int q=1; q<used.length; q++) {
          int ndx = files.indexOf(used[q]);
          if (ndx > -1) files.setElementAt(null, ndx);
          if (used[q].indexOf(File.separator) != -1) {
            String s =
              used[q].substring(used[q].lastIndexOf(File.separator) + 1);
            ndx = files.indexOf(s);
            if (ndx > -1) files.setElementAt(null, ndx);
          }
        }
        f.close();
      }
    }
    catch (FormatException e) { e.printStackTrace(); }
    catch (IOException e) { e.printStackTrace(); }
    files = rtn;
    return rtn;
  }

  // -- Helper class --

  public static class TestLogger extends Log {
    private FileWriter writer;

    public TestLogger(FileWriter writer) {
      this.writer = writer;
    }

    public void print(String x) {
      try {
        if (writer != null) writer.write(x);
      }
      catch (IOException exc) { }
    }

    public void flush() {
      try { writer.flush(); }
      catch (IOException exc) { }
    }
  }

}
