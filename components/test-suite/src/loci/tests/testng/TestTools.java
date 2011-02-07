//
// TestTools.java
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
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.formats.ImageReader;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for use with TestNG tests.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/test-suite/src/loci/tests/testng/TestTools.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/test-suite/src/loci/tests/testng/TestTools.java">SVN</a></dd></dl>
*/
public class TestTools {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(TestTools.class);

  public static final String DIVIDER =
    "----------------------------------------";

  /** Gets a timestamp for the current moment. */
  public static String timestamp() {
    return DateTools.convertDate(System.currentTimeMillis(), DateTools.UNIX);
  }

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
  public static void createLogFile() {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    StringBuffer dateBuf = new StringBuffer();
    fmt.format(new Date(), dateBuf, new FieldPosition(0));
    String logFile = "loci-software-test-" + dateBuf + ".log";
    LOGGER.info("Output logged to {}", logFile);
    try {
      org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
      root.setLevel(Level.INFO);
      root.addAppender(new WriterAppender(
        new PatternLayout("%p [%d{dd-MM-yyyy HH:mm:ss.SSS}] %m%n"),
        new PrintWriter(new FileOutputStream(logFile))));
    }
    catch (IOException e) { LOGGER.info("", e); }

    // close log file on exit
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        LOGGER.info(DIVIDER);
        LOGGER.info("Test suite complete.");
      }
    });
  }

  /** Recursively generate a list of files to test. */
  public static void getFiles(String root, List files, ConfigurationTree config)
  {
    Location f = new Location(root);
    String[] subs = f.list();
    if (subs == null) subs = new String[0];
    Arrays.sort(subs);

    // make sure that if a config file exists, it is first on the list
    for (int i=0; i<subs.length; i++) {
      if (subs[i].endsWith(".bioformats")) {
        String tmp = subs[0];
        subs[0] = subs[i];
        subs[i] = tmp;
        break;
      }
    }

    ImageReader typeTester = new ImageReader();

    for (int i=0; i<subs.length; i++) {
      Location file = new Location(root, subs[i]);
      subs[i] = file.getAbsolutePath();
      LOGGER.info("Checking {}:", subs[i]);

      if (file.getName().equals(".bioformats")) {
        // special config file for the test suite
        LOGGER.info("\tconfig file");
        try {
          config.parseConfigFile(subs[i]);
        }
        catch (IOException exc) {
          LOGGER.info("", exc);
        }
      }
      else if (isIgnoredFile(subs[i], config)) {
        LOGGER.info("\tignored");
        continue;
      }
      else if (file.isDirectory()) {
        LOGGER.info("\tdirectory");
        getFiles(subs[i], files, config);
      }
      else if (!subs[i].endsWith("readme.txt")) {
        if (typeTester.isThisType(subs[i])) {
          LOGGER.info("\tOK");
          files.add(file.getAbsolutePath());
        }
        else LOGGER.info("\tunknown type");
      }
      file = null;
    }
  }

  /** Determines if the given file should be ignored by the test suite. */
  public static boolean isIgnoredFile(String file, ConfigurationTree config) {
    if (file.indexOf(File.separator + ".") >= 0) return true; // hidden file

    try {
      Configuration c = config.get(file);
      if (!c.doTest()) return true;
    }
    catch (IOException e) { }

    // HACK - heuristics to speed things up
    if (file.endsWith(".oif.files")) return true; // ignore .oif folders

    return false;
  }

}
