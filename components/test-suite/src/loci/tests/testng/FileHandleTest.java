/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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

import static org.testng.AssertJUnit.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import loci.common.Constants;
import loci.formats.FormatException;
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
      new InputStreamReader(p.getInputStream(), Constants.ENCODING));
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
