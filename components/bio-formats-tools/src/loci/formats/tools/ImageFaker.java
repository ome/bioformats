/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
          DebugTools.setRootLevel("DEBUG");
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

    // make sure that we don't end up with just a ".fake" directory
    if (new Location(targetDirectoryPath).exists()) {
      Location p = new Location(targetDirectoryPath, "screen.fake");
      int index = 1;
      while (p.exists()) {
        p = new Location(targetDirectoryPath, "screen" + index + ".fake");
        index++;
      }

      targetDirectoryPath = p.getAbsolutePath();
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
