/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.Location;
import loci.formats.ResourceNamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates fake image structures. Methods defensively check
 * caller-supplied arguments and throw relevant exceptions where needed.
 *
 * @author Blazej Pindelski, bpindelski at dundee.ac.uk
 * @since 5.0
 */
public class FakeImage {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(FakeImage.class);

  private Location directoryRoot;

  private ResourceNamer resourceNamer;

  public FakeImage(Location directoryRoot) {
    this.directoryRoot = directoryRoot;
  }

  public static void isValidRange(int arg, int min, int max) {
    if (arg < min || arg > max) {
      throw new IllegalArgumentException("Method argument value outside "
          + "valid range.");
    }
  }

  /**
   * Creates a fake SPW file/directory structure. Maximum supported size is a
   * 384-well plate with multiple runs and fields. All arguments indicating
   * plate or well element count must be at least <code>1</code> and cannot be
   * <code>null</code>. The structure appears on the file system as: <br/>
   *
   * <pre>
   * foo.fake/
   * └── Plate000/
   *     └── Run000/
   *         └── WellA000/
   *             └── Field000.fake
   *                 ...
   *             ...
   *             WellA254/
   *             WellB000/
   *             ...
   *             WellAA000/
   *             ...
   *         Run001/
   *         ...
   *     Plate001/
   *     ...
   * </pre>
   *
   * @param plates
   *          Number of plates in a screen (max 255).
   * @param plateAcquisitions
   *          Number of plate acquisitions (runs) in a plate (max 255).
   * @param rows
   *          Number of rows in a plate (max 255).
   * @param columns
   *          Number of columns in a plate (max 255).
   * @param fields
   *          Number of fields for a plate acquisition (max 255).
   * @throws IllegalArgumentException
   *           when any of the arguments fail validation.
   * @throws NullPointerException
   *           when null specified as argument value.
   * @return {@link Location} Instance representing the top-level directory
   *           of the SPW structure.
   */
  public Location generateScreen(int plates, int plateAcquisitions, int rows,
      int columns, int fields) {
    isValidRange(plates, 1, 255);
    isValidRange(plateAcquisitions, 1, 255);
    isValidRange(rows, 1, 255);
    isValidRange(columns, 1, 255);
    isValidRange(fields, 1, 255);

    List<Location> paths = new ArrayList<Location>();
    this.resourceNamer = new ResourceNamer(rows);

    long start = System.currentTimeMillis();
    for (int i = 0; i < plates; ++i) {
      Location plateLocation = resourceNamer.getLocationFromResourceName(
          directoryRoot, ResourceNamer.PLATE, i, File.separator);
      for (int j = 0; j < plateAcquisitions; ++j) {
        Location plateAcquisitionLocation = resourceNamer.
            getLocationFromResourceName(plateLocation, ResourceNamer.RUN, j,
                File.separator);
        resourceNamer.restartAlphabet();
        for (int k = 0; k < rows; ++k) {
          for (int l = 0; l < columns; ++l) {
            Location wellLocation = resourceNamer.getLocationFromResourceName(
                plateAcquisitionLocation, ResourceNamer.WELL +
                resourceNamer.getLetter(), l, File.separator);
            paths.add(wellLocation);
          }
          resourceNamer.nextLetter();
        }
      }
    }

    for (Location path : paths) {
      if (path.mkdirs()) {
        for (int i = 0; i < fields; ++i) {
          Location fieldLocation = resourceNamer.
              getLocationFromResourceName(path, ResourceNamer.FIELD, i,
                  ResourceNamer.FAKE_EXT);
          try {
            fieldLocation.createNewFile();
            LOGGER.debug("Created: " + fieldLocation.getCanonicalPath());
          } catch (IOException ioe) {
            throw new RuntimeException(ioe);
          }
        }
      }
    }

    long end = System.currentTimeMillis();
    LOGGER.debug(String.format("Fake SPW structure generation took %s ms.", end
        - start));

    return directoryRoot;
  }

}
