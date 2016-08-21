/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.utests;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.Location;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.Memoizer;
import loci.formats.MinMaxCalculator;
import loci.formats.in.FakeReader;
import loci.formats.meta.IMinMaxStore;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 */
public class MemoizerTest {

  private static final String TEST_FILE =
    "test&pixelType=int8&sizeX=20&sizeY=20&sizeC=1&sizeZ=1&sizeT=1.fake";

  private FakeReader reader;

  private Memoizer memoizer;

  private static int fullPlaneCallIndex;

  private int sizeX;

  private int sizeY;

  private int bpp;

  private int planeSize;

  @BeforeMethod
  public void setUp() throws Exception {
    fullPlaneCallIndex = 1;
    // No mapping.
    // Location.mapId(TEST_FILE, TEST_FILE);
    reader = new FakeReader();
    try {
      reader.setId(TEST_FILE);
      sizeX = reader.getSizeX();
      sizeY = reader.getSizeY();
      bpp = FormatTools.getBytesPerPixel(reader.getPixelType());
      planeSize = sizeY * sizeY * bpp;
    } finally {
      reader.close();
    }
    reader = new FakeReader(); // No setId !
  }

  @AfterMethod
  public void tearDown() throws Exception {
    memoizer.close();
    reader.close();
  }

  @Test
  public void testSimple() throws Exception {
      memoizer = new Memoizer(reader);
      File f = memoizer.getMemoFile(TEST_FILE);
      if (f.exists()) {
        f.delete();
      }
      // At this point we're sure that there's no memo file.
      reader.setId(TEST_FILE);
      reader.close();
      memoizer.setId(TEST_FILE);
      memoizer.close();
      memoizer.setId(TEST_FILE);
      memoizer.close();
  }

  public static void main(String[] args) throws Exception {
      MemoizerTest t = new MemoizerTest();
      t.setUp();
      try {
        t.testSimple();
      } finally {
        t.tearDown();
      }
  }
}
