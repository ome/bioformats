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

import static org.testng.AssertJUnit.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

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
    ArrayList<String> initialHandles = TestTools.getHandles();
    reader = new ImageReader();
    reader.setId(id);

    ArrayList<String> intermediateHandles = TestTools.getHandles();
    reader.close();
    ArrayList<String> finalHandles = TestTools.getHandles();

    int intermediateHandleCount = intermediateHandles.size();

    for (int i=0; i<initialHandles.size(); i++) {
      String s = initialHandles.get(i);
      initialHandles.remove(s);
      finalHandles.remove(s);
      intermediateHandles.remove(s);
      i--;
    }

    for (int i=0; i<finalHandles.size(); i++) {
      String s = finalHandles.get(i);
      if (s.endsWith("libnio.so") || s.endsWith("resources.jar") ||
        s.startsWith("/usr/lib") || s.startsWith("/opt/") ||
        s.startsWith("/usr/share/locale") || s.startsWith("/lib") ||
        s.indexOf("turbojpeg") > 0 || s.indexOf("/jre/") > 0 ||
        s.indexOf("nativedata") > 0 || s.indexOf("jhdf") > 0)
      {
        finalHandles.remove(s);
        i--;
      }
      else {
        LOGGER.warn(s);
      }
    }

    assertEquals(finalHandles.size(), initialHandles.size());
    assertTrue(intermediateHandles.size() >= initialHandles.size());
    assertTrue(intermediateHandleCount < 1024);
  }

}
