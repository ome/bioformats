/*
 * #%L
 * Classes implementing Bio-Formats command line tools for reading and converting files
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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

package loci.formats.tools;

import loci.common.DebugTools;
import loci.common.Location;
import loci.formats.FormatHandler;
import loci.formats.ResourceNamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageFaker is a wrapper class for invoking methods in {@link FakeImage}.
 *
 * @author Blazej Pindelski, bpindelski at dundee.ac.uk
 * @since 5.0
 */
public class ImageFaker {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ImageFaker.class);

  private String targetDirectoryPath;

  private int plates = 1;

  private int plateAcquisitions = 1;

  private int rows = 1;

  private int columns = 1;

  private int fields = 1;

  public boolean parseArgs(String[] args) {
    if (args == null || args.length == 0) {
      return false;
    }
    for (int i = 0; i < args.length; i++) {
      if (args[i].charAt(0) == '-') {
        if (args[i].equals("-plates")) {
          plates = Integer.parseInt(args[++i]);
        } else if (args[i].equals("-runs")) {
          plateAcquisitions = Integer.parseInt(args[++i]);
        } else if (args[i].equals("-rows")) {
          rows = Integer.parseInt(args[++i]);
        } else if (args[i].equals("-columns")) {
          columns = Integer.parseInt(args[++i]);
        } else if (args[i].equals("-fields")) {
          fields = Integer.parseInt(args[++i]);
        } else if (args[i].equals("-debug")) {
          DebugTools.enableLogging("DEBUG");
        }
      } else {
        if (targetDirectoryPath == null) {
          targetDirectoryPath = args[i];
        } else {
          LOGGER.error("Found unknown argument: {}; exiting.", args[i]);
          return false;
        }
      }
    }
    return true;
  }

  public void printUsage() {
    String[] s = { "To generate a fake file / dir structure, run:",
        "  mkfake path [-plates] [-runs] [-rows] [-columns] ",
        "    [-fields] [-debug]", "",
        "        path: the top-level directory for the SPW structure",
        "     -plates: number of plates (default: 1)",
        "       -runs: number of plate runs (acquisitions) (default: 1)",
        "       -rows: number of rows in a plate (default: 1)",
        "    -columns: number of columns in a plate (default: 1)",
        "     -fields: number of fields in a plate (default: 1)",
        "      -debug: turn on debugging output", "" };
    for (int i = 0; i < s.length; i++) {
      LOGGER.info(s[i]);
    }
  }

  public boolean fakeScreen(String[] args) {
    DebugTools.enableLogging("INFO");

    boolean validArgs = parseArgs(args);

    if (!validArgs || targetDirectoryPath == null) {
      printUsage();
      return false;
    }

    Location directoryRoot;
    if (!FormatHandler.checkSuffix(targetDirectoryPath,
            ResourceNamer.FAKE_EXT)) {
      directoryRoot = new Location(targetDirectoryPath + ResourceNamer.DOT
        + ResourceNamer.FAKE_EXT);
    } else {
      directoryRoot = new Location(targetDirectoryPath);
    }

    FakeImage fake = new FakeImage(directoryRoot);
    fake.generateScreen(plates, plateAcquisitions, rows, columns, fields);

    return true;
  }

  public static void main(String[] args) throws Exception {
    if (!new ImageFaker().fakeScreen(args)) {
      System.exit(1);
    }
  }

}
