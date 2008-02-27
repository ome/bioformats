package loci.tests.testng;

import java.io.*;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.*;
import loci.formats.*;

public class FormatReaderTestFactory {

  // -- Static fields --

  /** List of configuration files. */
  private static Vector configFiles = new Vector();

  // -- TestNG factory methods --

  /**
   * @testng.factory
   */
  public Object[] createInstances() {
    // parse base directory
    final String baseDirProp = "testng.directory";
    String baseDir = System.getProperty(baseDirProp);
    if (!new File(baseDir).isDirectory()) {
      if (baseDir == null || baseDir.equals("${" + baseDirProp + "}")) {
        LogTools.println("Error: no base directory specified.");
      }
      else LogTools.println("Error: invalid base directory: " + baseDir);
      LogTools.println("Please specify a directory containing files to test:");
      LogTools.println("   ant -D" + baseDirProp +
        "=\"/path/to/data\" test-all");
      return new Object[0];
    }

    // create log file
    createLogFile();
    LogTools.println("testng.directory = " + baseDir);

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

    // scan for files
    Vector files = new Vector();
    System.out.println("Scanning for files...");
    getFiles(baseDir, files);
    LogTools.println("--------------------");
    LogTools.println("Total files: " + files.size());
    LogTools.println("--------------------");

    // create test class instances
    System.out.println("Building list of tests...");
    Object[] tests = new Object[files.size()];
    for (int i=0; i<tests.length; i++) {
      String id = (String) files.get(i);
      tests[i] = new FormatReaderTest(id, multiplier);
    }
    System.out.println("Ready to test " + tests.length + " files");

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
      if (isIgnoredFile(subs[i])) {
        LogTools.println("ignored");
        continue;
      }

      if (file.isDirectory()) {
        LogTools.println("directory");
        getFiles(subs[i], files);
      }
      else if (file.getName().equals(".bioformats")) {
        // special config file for the test suite
        LogTools.println("config file");
        configFiles.add(file.getAbsolutePath());
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
    for (int i=0; i<configFiles.size(); i++) {
      try {
        String s = (String) configFiles.get(i);
        if (!FormatReaderTest.config.isParsed(s)) {
          FormatReaderTest.config.addFile(s);
        }
      }
      catch (IOException exc) {
        LogTools.trace(exc);
      }
    }
    return !FormatReaderTest.config.testFile(file) &&
      !file.endsWith(".bioformats");
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
        LogTools.println("--------------------");
        LogTools.println("Test suite complete.");
        LogTools.getLog().getStream().close();
      }
    });
  }

}
