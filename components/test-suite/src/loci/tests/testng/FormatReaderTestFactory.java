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
import java.util.List;

import loci.common.DataTools;
import loci.formats.FileStitcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Factory;

/**
 * Factory for scanning a directory structure and generating instances of
 * {@link FormatReaderTest} based on the image files found.
 * <p>
 * Note that this approach works due to an implementation detail of TestNG.
 * Ideally we would use the lazy iterator approach instead, but it does not
 * execute methods in the correct order. For details, see <a
 * href="http://code.google.com/p/testng/issues/detail?id=19#c6">this
 * issue</a>.
 * </p>
 */
public class FormatReaderTestFactory {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(FormatReaderTestFactory.class);

  // -- TestNG factory methods --

  /**
   * Safely return a system property by key excluding default Ant values
   */
  public String getProperty(String key) {
    String value = System.getProperty(key);
    if (value == null || value.equals("${" + key + "}")) {
      return null;
    }
    return value;
  }

  @Factory
  public Object[] createInstances() {
    List files = new ArrayList();

    // parse explicit filename, if any
    final String nameProp = "testng.filename";
    String filename = getProperty(nameProp);

    if (filename != null && !new File(filename).exists()) {
      LOGGER.error("Invalid filename: {}", filename);
      return new Object[0];
    }

    String baseDir = null;
    String[] validSubdirs = null;
    if (filename == null) {
      // parse base directory
      final String baseDirProp = "testng.directory";
      baseDir = getProperty(baseDirProp);

      if (baseDir == null) {
        baseDir = getProperty("testng.directory-prefix");
        String dirList = System.getProperty("testng.directory-list");
        try {
          validSubdirs = DataTools.readFile(dirList).split("\n");
        }
        catch (IOException e) {
          LOGGER.debug("", e);
        }
      }

      // Return early if no base directory is supplied
      if (baseDir == null) {
        LOGGER.error("No base directory specified.");
        LOGGER.error("Please specify a directory containing files to test:");
        LOGGER.error("   ant -D{}=\"/path/to/data\" test-all", baseDirProp);
        return new Object[0];
      }

      // Test base directory validity
      File baseDirFile = new File(baseDir);
      if (!baseDirFile.isDirectory()) {
        LOGGER.info("Directory: {}", baseDir);
        LOGGER.info("  exists?: {}", baseDirFile.exists());
        LOGGER.info("  readable?: {}", baseDirFile.canRead());
        LOGGER.info("  is a directory?: {}", baseDirFile.isDirectory());
        LOGGER.error("Please specify a directory containing files to test:");
        LOGGER.error("   ant -D{}=\"/path/to/data\" test-all", baseDirProp);
        return new Object[0];
      }

      // check for an alternate configuration directory
      final String configDirProperty = "testng.configDirectory";
      String configDir = getProperty(configDirProperty);
      LOGGER.info("testng.directory = {}", baseDir);
      if (configDir != null) {
        LOGGER.info("testng.configDirectory = {}", configDir);
      }

      FormatReaderTest.configTree = new ConfigurationTree(baseDir, configDir);
    }

    // parse multiplier
    final String multProp = "testng.multiplier";
    String mult = getProperty(multProp);
    float multiplier = 1;
    if (mult != null) {
      try {
        multiplier = Float.parseFloat(mult);
      }
      catch (NumberFormatException exc) {
        LOGGER.warn("Invalid multiplier: {}", mult);
      }
    }
    LOGGER.info("testng.multiplier = {}", multiplier);

    // detect whether or not the map the files into memory
    final String inMemoryProp = "testng.in-memory";
    String inMemoryValue = getProperty(inMemoryProp);
    boolean inMemory = Boolean.parseBoolean(inMemoryValue);
    LOGGER.info("testng.in-memory = {}", inMemory);

    // check for an alternate top level configuration file

    final String toplevelConfig = "testng.toplevel-config";
    String configFile = getProperty(toplevelConfig);
    if (configFile != null) {
      LOGGER.info("testng.toplevel-config = {}", configFile);
    }

    // check for a configuration file suffix

    final String configSuffixProperty = "testng.configSuffix";
    String configSuffix = getProperty(configSuffixProperty);
    if (configSuffix == null) {
      configSuffix = "";
    }

    // display local information
    LOGGER.info("user.language = {}", System.getProperty("user.language"));
    LOGGER.info("user.country = {}", System.getProperty("user.country"));

    // detect maximum heap size
    long maxMemory = Runtime.getRuntime().maxMemory() >> 20;
    LOGGER.info("Maximum heap size = {} MB", maxMemory);

    if (filename == null) {
      // scan for files
      System.out.println("Scanning for files...");
      long start = System.currentTimeMillis();
      try {
        TestTools.getFiles(baseDir, files, FormatReaderTest.configTree,
          configFile, validSubdirs, configSuffix);
      }
      catch (Exception e) {
        LOGGER.info("Failed to retrieve complete list of files", e);
      }
      long end = System.currentTimeMillis();
      double time = (end - start) / 1000.0;
      LOGGER.info(TestTools.DIVIDER);
      LOGGER.info("Total files: {}", files.size());
      long avg = (end - start);
      if (files.size() > 0) avg /= files.size();
      LOGGER.info("Scan time: {} s ({} ms/file)", time, avg);
      LOGGER.info(TestTools.DIVIDER);
    }
    else {
      files.add(filename);
    }

    // remove duplicates
    int index = 0;
    FileStitcher reader = new FileStitcher();
    while (index < files.size()) {
      String file = (String) files.get(index);
      try {
        reader.setId(file);
        String[] usedFiles = reader.getUsedFiles();
        for (int q=0; q<usedFiles.length; q++) {
          if (files.indexOf(usedFiles[q]) > index) {
            files.remove(usedFiles[q]);
          }
        }
      }
      catch (Exception e) { }
      finally {
        try {
          reader.close();
        }
        catch (IOException e) { }
      }

      index++;
    }

    // create test class instances
    System.out.println("Building list of tests...");
    Object[] tests = new Object[files.size()];
    for (int i=0; i<tests.length; i++) {
      String id = (String) files.get(i);
      try {
        if (FormatReaderTest.configTree.get(id) == null) {
          LOGGER.error("{} not configured.", id);
        }
      }
      catch (Exception e) {
        LOGGER.warn("", e);
      }
      tests[i] = new FormatReaderTest(id, multiplier, inMemory);
    }
    if (tests.length == 1) System.out.println("Ready to test " + files.get(0));
    else System.out.println("Ready to test " + tests.length + " files");

    return tests;
  }

}
