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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.FieldPosition;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for use with TestNG tests.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/TestTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/TestTools.java;hb=HEAD">Gitweb</a></dd></dl>
*/
public class TestTools {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(TestTools.class);

  public static final String DIVIDER =
    "----------------------------------------";

  /** Calculate the SHA-1 of a byte array. */
  public static String sha1(byte[] b, int offset, int len) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.reset();
      md.update(b, offset, len);
      byte[] digest = md.digest();
      return DataTools.bytesToHex(digest);
    }
    catch (NoSuchAlgorithmException e) { }
    return null;
  }

  /** Calculate the SHA-1 of a byte array. */
  public static String sha1(byte[] b) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.reset();
      md.update(b);
      byte[] digest = md.digest();
      return DataTools.bytesToHex(digest);
    }
    catch (NoSuchAlgorithmException e) { }
    return null;
  }

  /** Calculate the MD5 of a byte array. */
  public static String md5(byte[] b, int sizeX, int sizeY, int posX, int posY,
                           int width, int height, int bpp) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.reset();
      int offset = 0;
      for (int i = 0; i < height; i++) {
        offset = (((posY + i) * sizeX) + posX) * bpp;
        md.update(b, offset, width * bpp);
      }
      byte[] digest = md.digest();
      return DataTools.bytesToHex(digest);
    }
    catch (NoSuchAlgorithmException e) { }
    return null;
  }

  /** Calculate the MD5 of a byte array. */
  public static String md5(byte[] b, int offset, int len) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.reset();
      md.update(b, offset, len);
      byte[] digest = md.digest();
      return DataTools.bytesToHex(digest);
    }
    catch (NoSuchAlgorithmException e) { }
    return null;
  }

  /** Calculate the MD5 of a byte array. */
  public static String md5(byte[] b) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.reset();
      md.update(b);
      byte[] digest = md.digest();
      return DataTools.bytesToHex(digest);
    }
    catch (NoSuchAlgorithmException e) { }
    return null;
  }

  /** Returns true if a byte buffer of the given size will fit in memory. */
  public static boolean canFitInMemory(long bufferSize) {
    Runtime r = Runtime.getRuntime();
    long mem = r.freeMemory() / 2;
    int threadCount = 1;
    try {
      threadCount = Integer.parseInt(System.getProperty("testng.threadCount"));
    }
    catch (NumberFormatException e) { }
    mem /= threadCount;
    return bufferSize < mem && bufferSize <= Integer.MAX_VALUE;
  }

  /** Gets the quantity of used memory, in MB. */
  public static long getUsedMemory() {
    Runtime r = Runtime.getRuntime();
    long mem = r.totalMemory() - r.freeMemory();
    return mem >> 20;
  }

  /** Gets the class name sans package for the given object. */
  public static String shortClassName(Object o) {
    String name = o.getClass().getName();
    int dot = name.lastIndexOf(".");
    return dot < 0 ? name : name.substring(dot + 1);
  }

  /** Creates a new log file. */
  @Deprecated
  public static void createLogFile() {
    LOGGER.info("Start test suite");

    // close log file on exit
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        LOGGER.info(DIVIDER);
        LOGGER.info("Test suite complete.");
      }
    });
  }

  /** Recursively generate a list of files to test. */
  public static void getFiles(String root, List files,
    final ConfigurationTree config, String toplevelConfig)
  {
    getFiles(root, files, config, toplevelConfig, null);
  }

  /** Recursively generate a list of files to test. */
  public static void getFiles(String root, List files,
    final ConfigurationTree config, String toplevelConfig, String[] subdirs)
  {
    getFiles(root, files, config, toplevelConfig, subdirs, "");
  }


  /** Recursively generate a list of files to test. */
  public static void getFiles(String root, List files,
    final ConfigurationTree config, String toplevelConfig, String[] subdirs,
    String configFileSuffix)
  {
    String configName = ".bioformats";
    String baseConfigName = configName;
    if (configFileSuffix.length() > 0) {
      configName += ".";
      configName += configFileSuffix;
    }

    Location f = new Location(root);
    String[] subs = f.list();
    if (subs == null) subs = new String[0];
    if (subdirs != null) {
      subs = subdirs;
    }

    boolean isToplevel =
     toplevelConfig != null && new File(toplevelConfig).exists();

    Arrays.sort(subs);

    // make sure that if a config file exists, it is first on the list
    for (int i=0; i<subs.length; i++) {
      Location file = new Location(root, subs[i]);
      subs[i] = file.getAbsolutePath();

      String filename = file.getName();

      if ((!isToplevel && (filename.equals(configName) ||
        filename.equals(baseConfigName))) ||
        (isToplevel && subs[i].equals(toplevelConfig)))
      {
        String tmp = subs[0];
        subs[0] = subs[i];
        subs[i] = tmp;
      }
    }

    // special config file for the test suite
    LOGGER.debug("\tconfig file");
    try {
      config.parseConfigFile(subs[0]);
    }
    catch (IOException exc) {
      LOGGER.debug("", exc);
    }
    catch (Exception e) { }

    Arrays.sort(subs, new Comparator() {
      public int compare(Object o1, Object o2) {
        String s1 = o1.toString();
        String s2 = o2.toString();

        Configuration c1 = null;
        Configuration c2 = null;

        try {
          c1 = config.get(s1);
        }
        catch (IOException e) { }
        try {
          c2 = config.get(s2);
        }
        catch (IOException e) { }

        if (c1 == null && c2 != null) {
          return 1;
        }
        else if (c1 != null && c2 == null) {
          return -1;
        }

        return s1.compareTo(s2);
      }
    });

    ImageReader typeTester = new ImageReader();

    for (int i=0; i<subs.length; i++) {
      Location file = new Location(subs[i]);
      LOGGER.debug("Checking {}:", subs[i]);

      String filename = file.getName();

      if (filename.equals(configName) || filename.equals(baseConfigName)) {
        continue;
      }
      else if (isIgnoredFile(subs[i], config)) {
        LOGGER.debug("\tignored");
        continue;
      }
      else if (file.isDirectory()) {
        LOGGER.debug("\tdirectory");
        getFiles(subs[i], files, config, null, null, configFileSuffix);
      }
      else if (!subs[i].endsWith("readme.txt")) {
        if (typeTester.isThisType(subs[i])) {
          LOGGER.debug("\tOK");
          files.add(file.getAbsolutePath());
        }
        else LOGGER.debug("\tunknown type");
      }
      file = null;
    }
  }

  /** Determines if the given file should be ignored by the test suite. */
  public static boolean isIgnoredFile(String file, ConfigurationTree config) {
    if (file.indexOf(File.separator + ".") >= 0) return true; // hidden file

    try {
      Configuration c = config.get(file);
      if (c == null) return false;
      if (!c.doTest()) return true;
    }
    catch (IOException e) { }
    catch (Exception e) { }

    // HACK - heuristics to speed things up
    if (file.endsWith(".oif.files")) return true; // ignore .oif folders

    return false;
  }

  /**
   * Iterates over every tile in a given pixel buffer based on the over arching
   * dimensions and a requested maximum tile width and height.
   * @param iteration Invoker to call for each tile.
   * @param sizeX Width of the entire image.
   * @param sizeY Height of the entire image.
   * @param sizeZ Number of optical sections the image contains.
   * @param sizeC Number of channels the image contains.
   * @param sizeT Number of timepoints the image contains.
   * @param tileWidth <b>Maximum</b> width of the tile requested. The tile
   * request itself will be smaller than the original tile width requested if
   * <code>x + tileWidth > sizeX</code>.
   * @param tileHeight <b>Maximum</b> height of the tile requested. The tile
   * request itself will be smaller if <code>y + tileHeight > sizeY</code>.
   * @return The total number of tiles iterated over.
   */
  public static int forEachTile(TileLoopIteration iteration,
      int sizeX, int sizeY, int sizeZ, int sizeC,
      int sizeT, int tileWidth, int tileHeight)

  {
    int tileCount = 0;
    int x, y, w, h;
    for (int t = 0; t < sizeT; t++) {
      for (int c = 0; c < sizeC; c++) {
        for (int z = 0; z < sizeZ; z++) {
          for (int tileOffsetY = 0; 
               tileOffsetY < (sizeY + tileHeight - 1) / tileHeight;
               tileOffsetY++) {
            for (int tileOffsetX = 0;
                 tileOffsetX < (sizeX + tileWidth - 1) / tileWidth;
                 tileOffsetX++) {
              x = tileOffsetX * tileWidth;
              y = tileOffsetY * tileHeight;
              w = tileWidth;
              if (w + x > sizeX) {
                w = sizeX - x;
              }
              h = tileHeight;
              if (h + y > sizeY) {
                h = sizeY - y;
              }
              iteration.run(z, c, t, x, y, w, h, tileCount);
              tileCount++;
            }
          }
        }
      }
    }
    return tileCount;
  }

  /**
   * A single iteration of a tile for each loop.
   * @author Chris Allan <callan at blackcat dot ca>
   * @since OMERO Beta-4.3.0
   */
  public interface TileLoopIteration {
    /**
     * Invoke a single loop iteration.
     * @param z Z section counter of the loop.
     * @param c Channel counter of the loop.
     * @param t Timepoint counter of the loop.
     * @param x X offset within the plane specified by the section, channel and
     * timepoint counters.
     * @param y Y offset within the plane specified by the section, channel and
     * timepoint counters.
     * @param tileWidth Width of the tile requested. The tile request
     * itself may be smaller than the original tile width requested if
     * <code>x + tileWidth > sizeX</code>.
     * @param tileHeight Height of the tile requested. The tile request
     * itself may be smaller if <code>y + tileHeight > sizeY</code>.
     * @param tileCount Counter of the tile since the beginning of the loop.
     */
    void run(int z, int c, int t, int x, int y, int tileWidth,
             int tileHeight, int tileCount);
  }

  /**
   * Map the given file into memory.
   *
   * @return true if the mapping was successful.
   */
  public static boolean mapFile(String id) throws IOException {
    RandomAccessInputStream stream = new RandomAccessInputStream(id);
    Runtime rt = Runtime.getRuntime();
    long maxMem = rt.freeMemory();
    long length = stream.length();
    if (length < Integer.MAX_VALUE && length < maxMem) {
      stream.close();
      FileInputStream fis = new FileInputStream(id);
      FileChannel channel = fis.getChannel();
      ByteBuffer buf =
        channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
      ByteArrayHandle handle = new ByteArrayHandle(buf);
      Location.mapFile(id, handle);
      fis.close();
      return true;
    }
    stream.close();
    return false;
  }


}
