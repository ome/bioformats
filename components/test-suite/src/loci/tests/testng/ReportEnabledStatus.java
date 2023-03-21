/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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
import java.util.Set;
import java.util.LinkedHashSet;

import loci.common.DataTools;
import loci.common.DebugTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Print a list of files that are explicitly set to not be tested,
 * and which do not have a justifying comment in the config file.
 * Comments should be of the form "test = false # this is why".
 */
public class ReportEnabledStatus {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ReportEnabledStatus.class);

  /**
   * Print list of files.
   *
   * @param enabled true if the list of files is all configured and tested files,
   *                false if the list of files is all configured but neither tested nor commented
   */
  public void logConfiguredFiles(String dataDir, String configDir, boolean enabled) throws IOException {
    List<String> files = new ArrayList<String>();

    String[] validSubdirs = null;

    // Return early if no base directory is supplied
    if (dataDir == null) {
      LOGGER.error("No data directory specified.");
      return;
    }

    // Test base directory validity
    File baseDirFile = new File(dataDir);
    if (!baseDirFile.isDirectory()) {
      LOGGER.info("Directory: {}", dataDir);
      LOGGER.info("  exists?: {}", baseDirFile.exists());
      LOGGER.info("  readable?: {}", baseDirFile.canRead());
      LOGGER.info("  is a directory?: {}", baseDirFile.isDirectory());
      return;
    }

    ConfigurationTree configTree = new ConfigurationTree(dataDir, configDir, null);

    // scan for files
    System.out.println("Scanning for files...");
    long start = System.currentTimeMillis();
    try {
      TestTools.getFiles(dataDir, files, configTree, configDir, validSubdirs, "", false);
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

    // remove duplicates
    Set<String> fileSet = new LinkedHashSet<String>();
    for (String s: files) {
      String canonicalPath;
      try {
        canonicalPath = (new File(s)).getCanonicalPath();
      } catch (IOException e) {
        LOGGER.warn("Could not get canonical path for {}", s);
        canonicalPath = s;
      }
      if (!fileSet.contains(canonicalPath)) {
        Configuration conf = configTree.get(canonicalPath);
        if (conf != null && conf.doTest() == enabled) {
          if (enabled || conf.getTestComment() == null) {
            System.out.println(canonicalPath);
          }
        }
        fileSet.add(canonicalPath);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0 || args[0].equals("--help")) {
      System.out.println("unconfigured data_dir configuration_dir [--enabled]");
      System.out.println();
      System.out.println("  --enabled: print a list of files that are configured and tested");
    }

    DebugTools.enableLogging("INFO");
    ReportEnabledStatus status = new ReportEnabledStatus();
    String data = null;
    String config = null;
    boolean enabled = false;

    String msg = null;
    for (String arg : args) {
      if (arg.equals("--enabled")) {
        enabled = true;
      }
      else if (data == null) {
        data = arg;
      }
      else if (config == null) {
        config = arg;
      }
      else {
        msg = "Unexpected argument: " + arg;
        break;
      }
    }
    if (data == null) {
      msg = "Data directory not specified";
    }
    else if (config == null) {
      msg = "Configuration directory not specified";
    }
    if (msg != null) {
      throw new IllegalArgumentException(msg);
    }

    status.logConfiguredFiles(data, config, enabled);
  }

}
