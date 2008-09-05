//
// FormatReaderTestFactory.java
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

import java.io.*;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.*;
import loci.formats.*;

/**
 * Factory for scanning a directory structure and generating instances of
 * {@link FormatReaderTest} based on the image files found.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/tests/testng/FormatReaderTestFactory.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/tests/testng/FormatReaderTestFactory.java">SVN</a></dd></dl>
 */
public class FormatReaderTestFactory {

  // -- Constants --

  private static final String DIVIDER =
    "----------------------------------------";

  // -- TestNG factory methods --

  /**
   * @testng.factory
   */
  public Object[] createInstances() {
    Vector files = new Vector();

    // parse explicit filename, if any
    final String nameProp = "testng.filename";
    String filename = System.getProperty(nameProp);
    if (filename != null && filename.equals("${" + nameProp + "}")) {
      filename = null;
    }
    if (filename != null && !new File(filename).exists()) {
      LogTools.println("Error: invalid filename: " + filename);
      return new Object[0];
    }

    String baseDir = null;
    if (filename == null) {
      // parse base directory
      final String baseDirProp = "testng.directory";
      baseDir = System.getProperty(baseDirProp);
      if (!new File(baseDir).isDirectory()) {
        if (baseDir == null || baseDir.equals("${" + baseDirProp + "}")) {
          LogTools.println("Error: no base directory specified.");
        }
        else LogTools.println("Error: invalid base directory: " + baseDir);
        LogTools.println(
          "Please specify a directory containing files to test:");
        LogTools.println("   ant -D" + baseDirProp +
          "=\"/path/to/data\" test-all");
        return new Object[0];
      }
      FormatReaderTest.config = new ConfigurationTree(baseDir);

      // create log file
      createLogFile();
      LogTools.println("testng.directory = " + baseDir);
    }

    // parse multiplier
    final String multProp = "testng.multiplier";
    String mult = System.getProperty(multProp);
    float multiplier = 1;
    if (mult != null && !mult.equals("${" + multProp + "}")) {
      try {
        multiplier = Float.parseFloat(mult);
      }
      catch (NumberFormatException exc) {
        LogTools.println("Warning: invalid multiplier: " + mult);
      }
    }
    LogTools.println("testng.multiplier = " + multiplier);

    // detect maximum heap size
    long maxMemory = Runtime.getRuntime().maxMemory() >> 20;
    LogTools.println("Maximum heap size = " + maxMemory + " MB");

    if (filename == null) {
      // scan for files
      System.out.println("Scanning for files...");
      long start = System.currentTimeMillis();
      getFiles(baseDir, files);
      long end = System.currentTimeMillis();
      double time = (end - start) / 1000.0;
      LogTools.println(DIVIDER);
      LogTools.println("Total files: " + files.size());
      LogTools.print("Scan time: " + time + " s");
      if (files.size() > 0) {
        long avg = (end - start) / files.size();
        LogTools.println(" (" + avg + " ms/file)");
      }
      else LogTools.println();
      LogTools.println(DIVIDER);
    }
    else {
      files.add(filename);
    }

    // create test class instances
    System.out.println("Building list of tests...");
    Object[] tests = new Object[files.size()];
    for (int i=0; i<tests.length; i++) {
      String id = (String) files.get(i);
      tests[i] = new FormatReaderTest(id, multiplier);
    }
    if (tests.length == 1) System.out.println("Ready to test " + files.get(0));
    else System.out.println("Ready to test " + tests.length + " files");

    return tests;
  }

  // -- Helper methods --

  /** Recursively generate a list of files to test. */
  private static void getFiles(String root, List files) {
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
      LogTools.print("Checking " + subs[i] + ": ");

      if (file.getName().equals(".bioformats")) {
        // special config file for the test suite
        LogTools.println("config file");
        try {
          FormatReaderTest.config.parseConfigFile(subs[i]);
        }
        catch (IOException exc) {
          LogTools.trace(exc);
        }
      }
      else if (isIgnoredFile(subs[i])) {
        LogTools.println("ignored");
        continue;
      }
      else if (file.isDirectory()) {
        LogTools.println("directory");
        getFiles(subs[i], files);
      }
      else {
        if (typeTester.isThisType(subs[i])) {
          LogTools.println("OK");
          files.add(file.getAbsolutePath());
        }
        else LogTools.println("unknown type");
      }
      file = null;
    }
  }

  /** Determines if the given file should be ignored by the test suite. */
  private static boolean isIgnoredFile(String file) {
    if (file.indexOf(File.separator + ".") >= 0) return true; // hidden file

    FormatReaderTest.config.setId(file);
    if (!FormatReaderTest.config.isTestable()) return true;

    // HACK - heuristics to speed things up
    if (file.endsWith(".oif.files")) return true; // ignore .oif folders

    return false;
  }

  /** Creates a new log file. */
  private static void createLogFile() {
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
        LogTools.println(DIVIDER);
        LogTools.println("Test suite complete.");
        LogTools.getLog().getStream().close();
      }
    });
  }

}
