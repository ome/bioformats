//
// FileHandleTest.java
//

/*
LOCI software automated test suite for TestNG. Copyright (C) 2007-@year@
Melissa Linkert. All rights reserved.

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

package loci.tests.testng;

import static org.testng.AssertJUnit.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks that no file handles are open after closing a reader.
 * This will not work on Windows, as it depends upon the 'lsof' command.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/FileHandleTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/FileHandleTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class FileHandleTest {
  private static final Logger LOGGER =
    LoggerFactory.getLogger(FileHandleTest.class);

  private String id;

  private IFormatReader reader;

  @Parameters({"id"})
  @BeforeClass
  public void init(String id) {
    this.id = id;
  }

  @Test
  public void testHandleCount() throws FormatException, IOException {
    int initialHandleCount = getHandleCount();
    reader = new ImageReader();
    reader.setId(id);

    // subtract 1 for libnio.so, which only shows up after setId is called
    int intermediateHandleCount = getHandleCount() - 1;
    reader.close();
    int finalHandleCount = getHandleCount() - 1;

    assertEquals(finalHandleCount, initialHandleCount);
    assertTrue(intermediateHandleCount >= initialHandleCount);
    assertTrue(intermediateHandleCount < 1024);
  }

  private int getHandleCount() throws IOException {
    String pid = ManagementFactory.getRuntimeMXBean().getName();
    pid = pid.substring(0, pid.indexOf("@"));

    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec("lsof -Ft -p " + pid);
    BufferedReader s = new BufferedReader(
      new InputStreamReader(p.getInputStream(), FormatTools.ENCODING));
    int handleCount = 0;
    String line = s.readLine();
    while (true) {
      try {
        p.exitValue();
        if (line == null) {
          break;
        }
      }
      catch (Exception e) {
        LOGGER.warn("", e);
      }
      if (line != null && line.endsWith("REG")) {
        handleCount++;
      }
      line = s.readLine();
    }
    s.close();
    p.getInputStream().close();
    p.getOutputStream().close();
    p.getErrorStream().close();
    return handleCount;
  }

}
