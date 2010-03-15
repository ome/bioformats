//
// SingularityTest.java
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

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.ImageReader;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for testing the accuracy of
 * {@link loci.formats.IFormatReader#isSingleFile(String)}.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/test-suite/src/loci/tests/SingularityTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/test-suite/src/loci/tests/SingularityTest.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class SingularityTest {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(SingularityTest.class);

  private static ImageReader reader = new ImageReader();

  public static void main(String[] args) throws FormatException, IOException {
    org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
    root.setLevel(Level.INFO);
    root.addAppender(new ConsoleAppender(new PatternLayout("%m%n")));

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
