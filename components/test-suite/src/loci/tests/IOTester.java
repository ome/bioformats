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

package loci.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import loci.common.RandomAccessInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for testing {@link loci.common.RandomAccessInputStream}'s
 * efficiency in various cases.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/IOTester.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/IOTester.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IOTester {

  private static final Logger LOGGER = LoggerFactory.getLogger(IOTester.class);

  private static final String TAG = "<END>";
  private static final int SIZE = 50 * 1024 * 1024; // in bytes
  private static final long NUM_DOTS = 80;

  private static final String ALPHANUM =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

  public byte[] createData() {
    // create random test data
    byte[] data = new byte[SIZE];
    int middle = (int) ((data.length - TAG.length() - 1) * Math.random());
    int left = data.length - middle - TAG.length();
    int rest = middle + TAG.length();

    long middlePercent = 100L * middle / SIZE;
    long leftPercent = 100L * left / SIZE;

    LOGGER.info("Generating data: {} ({}%) alphanumeric + {} ({}%) binary",
      new Object[] {middle, middlePercent, left, leftPercent});

    // TODO: This should use a specific instance like
    // LOGGER2 = LoggerFactory.getLogger("IOTester.ALPHANUM")
    long progress = 0;
    for (int i=0; i<data.length; i++) {
      // print dots to indicate progress
      long p = NUM_DOTS * i / data.length;
      if (p > progress) {
        LOGGER.info(".");
        progress = p;
      }
      if (i < middle) {
        // write random alphanumeric
        int rnd = (int) (ALPHANUM.length() * Math.random());
        data[i] = (byte) ALPHANUM.charAt(rnd);
      }
      else if (i >= rest) {
        // write random binary
        int rnd = (int) (256 * Math.random());
        data[i] = (byte) rnd;
      }
      else { // middle <= i < rest;
        // write divider tag
        data[i] = (byte) TAG.charAt(i - middle);
      }
    }
    // END USE OF NEW LOGGER2
    LOGGER.info("");

    return data;
  }

  public void saveData(String filename, byte[] data) throws IOException {
    LOGGER.info("Saving {}...", filename);
    FileOutputStream out = new FileOutputStream(filename);
    out.write(data);
    out.close();
  }

  /** Searches for the divider tag using repeated readChar() calls. */
  public long testSequential(String filename) throws IOException {
    LOGGER.info("Searching for divider tag sequentially...");
    long start = System.currentTimeMillis();

    RandomAccessInputStream in = new RandomAccessInputStream(filename);
    int matchIndex = 0;
    char matchChar = TAG.charAt(0);
    long inputLen = in.length();
    for (long i=0; i<inputLen; i++) {
      char c = in.readChar();
      if (c == matchChar) {
        matchIndex++;
        if (matchIndex == TAG.length()) {
          break;
        }
        else {
          matchChar = TAG.charAt(matchIndex);
        }
      }
      else {
        matchIndex = 0;
        matchChar = TAG.charAt(0);
      }
    }
    long offset = in.getFilePointer();
    in.close();

    long end = System.currentTimeMillis();
    LOGGER.info("Search result: {} -- in {} ms", offset, end - start);
    return offset;
  }

  /** Searches for the divider tag in blocks of the given size. */
  public long testBlock(String filename, int blockSize) throws IOException {
    LOGGER.info("Searching for divider in blocks of {}...", blockSize);
    long start = System.currentTimeMillis();

    RandomAccessInputStream in = new RandomAccessInputStream(filename);
    long offset = in.findString(blockSize, TAG).length();
    in.close();

    long end = System.currentTimeMillis();
    LOGGER.info("Search result: {} -- in {} ms", offset, end - start);
    return offset;
  }

  public void deleteData(String filename) {
    LOGGER.info("Deleting {}", filename);
    File f = new File(filename);
    f.delete();
  }

  public void testIO() throws IOException {
    String prefix = "IOTester";
    byte[] data = createData();

    String file1 = prefix + "1.tmp";
    String file2 = prefix + "2.tmp";
    String file3 = prefix + "3.tmp";
    String file4 = prefix + "4.tmp";
    String file5 = prefix + "5.tmp";

    saveData(file1, data);
    saveData(file2, data);
    saveData(file3, data);
    saveData(file4, data);
    saveData(file5, data);

    testBlock(file1, 65536);
    testBlock(file2, 262144);
    testBlock(file3, 524288);
    testBlock(file4, 1048576);
    testSequential(file5);

    deleteData(file1);
    deleteData(file2);
    deleteData(file3);
    deleteData(file4);
    deleteData(file5);
  }

  public static void main(String[] args) throws IOException {
    new IOTester().testIO();
  }

}
