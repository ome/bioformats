//
// IOTester.java
//

/*
LOCI software manual test suite. Copyright (C) 2007-@year@
Curtis Rueden and Melissa Linkert. All rights reserved.

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

package loci.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import loci.common.RandomAccessInputStream;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for testing {@link loci.common.RandomAccessInputStream}'s
 * efficiency in various cases.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/test-suite/src/loci/tests/IOTester.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/test-suite/src/loci/tests/IOTester.java">SVN</a></dd></dl>
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

  private ConsoleAppender appender;
  private PatternLayout originalLayout;

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

    appender.setLayout(new PatternLayout("%m"));
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
    appender.setLayout(originalLayout);
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
    org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
    root.setLevel(Level.INFO);
    originalLayout = new PatternLayout("%m%n");
    appender = new ConsoleAppender(originalLayout);
    root.addAppender(appender);

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
