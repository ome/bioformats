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

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;

import loci.common.Location;
import loci.formats.ChannelFiller;
import loci.formats.ChannelMerger;
import loci.formats.ChannelSeparator;
import loci.formats.CoreMetadata;
import loci.formats.DimensionSwapper;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.Memoizer;
import loci.formats.MinMaxCalculator;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/test/loci/formats/utests/ReaderEqualityTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/test/loci/formats/utests/ReaderEqualityTest.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ReaderEqualityTest {

  private static final String TEST_FILE_A =
    "A&pixelType=uint8&sizeX=128&sizeY=64&sizeC=2&sizeZ=4&sizeT=5&series=3.fake";
  private static final String TEST_FILE_B =
    "B&pixelType=uint8&sizeX=128&sizeY=64&sizeC=2&sizeZ=4&sizeT=5&series=2.fake";

  @DataProvider(name = "equalWrappers")
  public Object[][] createEqualWrappers() {
    Location.mapId(TEST_FILE_A, TEST_FILE_A);
    Location.mapId(TEST_FILE_B, TEST_FILE_B);

    Object[][] wrappers = new Object[][] {
      {new ImageReader(), new ImageReader()},
      {new ImageReader(), new ImageReader()},
      {new MinMaxCalculator(new ChannelSeparator(new ChannelFiller())),
       new MinMaxCalculator(new ChannelSeparator(new ChannelFiller()))},
      {new DimensionSwapper(new ChannelMerger()),
       new DimensionSwapper(new ChannelMerger())}
    };
    for (int i=1; i<wrappers.length; i++) {
      IFormatReader readerA = (IFormatReader) wrappers[i][0];
      IFormatReader readerB = (IFormatReader) wrappers[i][1];
      try {
        readerA.setId(TEST_FILE_A);
        readerB.setId(TEST_FILE_B);
      }
      catch (FormatException e) { e.printStackTrace(); }
      catch (IOException e) { e.printStackTrace(); }
    }
    return wrappers;
  }

  @DataProvider(name = "unequalWrappers")
  public Object[][] createUnequalWrappers() {
    Object[][] wrappers = new Object[][] {
      {new ImageReader(), new ImageReader()},
      {new ImageReader(), new ImageReader()},
      {new MinMaxCalculator(new ChannelSeparator(new ChannelFiller())),
       new MinMaxCalculator(new ChannelSeparator())},
      {new DimensionSwapper(new ChannelMerger()),
       new DimensionSwapper(new ChannelMerger())}
    };

    for (int i=0; i<wrappers.length; i++) {
      IFormatReader readerA = (IFormatReader) wrappers[i][0];
      IFormatReader readerB = (IFormatReader) wrappers[i][1];

      try {
        if (i != 1) {
          readerA.setId(TEST_FILE_A);
        }
        else {
          readerA.setId(TEST_FILE_B);
        }
        if (i > 1) {
          readerB.setId(TEST_FILE_B);
        }
        else if (i == 1) {
          readerB.setId(TEST_FILE_A);
        }
      }
      catch (FormatException e) { e.printStackTrace(); }
      catch (IOException e) { e.printStackTrace(); }
    }
    return wrappers;
  }

  @Test(dataProvider = "equalWrappers")
  public void testEquality(IFormatReader[] readers) {
    if (readers.length == 2) {
      assertTrue(FormatTools.equalReaders(readers[0], readers[1]));
    }
  }

  @Test(dataProvider = "unequalWrappers")
  public void testInequality(IFormatReader[] readers) {
    if (readers.length == 2) {
      assertFalse(FormatTools.equalReaders(readers[0], readers[1]));
    }
  }
}
