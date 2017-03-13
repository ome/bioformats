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

package loci.tests;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.ImageReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for testing the accuracy of
 * {@link loci.formats.IFormatReader#isSingleFile(String)}.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class SingularityTest {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(SingularityTest.class);

  private static ImageReader reader = new ImageReader();

  public static void main(String[] args) throws FormatException, IOException {

    if (args.length < 1) {
      LOGGER.info("Usage: java.loci.tests.SingularityTest /path/to/input-file");
      System.exit(1);
    }

    LOGGER.info("Testing {}", args[0]);

    ImageReader reader = new ImageReader();
    boolean isSingleFile = reader.isSingleFile(args[0]);

    reader.setId(args[0]);
    String[] usedFiles = reader.getUsedFiles();

    if (isSingleFile && usedFiles.length > 1) {
      LOGGER.info("  Used files list contains more than one file, " +
        "but isSingleFile(String) returned true.");
      LOGGER.info("FAILURE");
    }
    else if (!isSingleFile && usedFiles.length == 1) {
      LOGGER.info("  Used files list only contains one file, " +
        "but isSingleFile(String) returned false.");
      LOGGER.info("FAILURE");
    }
    else LOGGER.info("SUCCESS");
  }

}
