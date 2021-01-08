/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2019 Open Microscopy Environment:
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

package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.*;

import java.io.File;
import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.out.TiffWriter;
import loci.formats.tiff.IFD;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 */
public class TiffTileReadingTest {

  private static final int TILE_SIZE = 2;

  private File file;
  private ImageReader reader;

  @BeforeClass
  public void setUp() throws Exception {
    file = File.createTempFile("tileTest", ".tiff");
    writeFile(TILE_SIZE * 2, TILE_SIZE * 2);
    reader = new ImageReader();
    reader.setId(file.getAbsolutePath());
  }

  @AfterClass
  public void tearDown() throws Exception {
    reader.close();
    file.delete();
  }

  @Test
  public void testPixelByPixel() throws FormatException, IOException {
    for (int y=0; y<reader.getSizeY(); y++) {
      for (int x=0; x<reader.getSizeX(); x++) {
        byte[] pixel = reader.openBytes(0, x, y, 1, 1);

        int xp = x % TILE_SIZE;
        int yp = y % TILE_SIZE;
        assertEquals(pixel[0], getValue(x - xp, (yp * TILE_SIZE) + xp));
      }
    }
  }

  @Test
  public void testOnTileBoundaries() throws FormatException, IOException {
    for (int y=0; y<reader.getSizeY(); y+=TILE_SIZE) {
      for (int x=0; x<reader.getSizeX(); x+=TILE_SIZE) {
        byte[] tile = reader.openBytes(0, x, y, TILE_SIZE, TILE_SIZE);
        for (int q=0; q<tile.length; q++) {
          assertEquals(tile[q], getValue(x, q));
        }
      }
    }
  }

  @Test
  public void testOffTileBoundaries() throws FormatException, IOException {
    for (int y=0; y<=reader.getSizeY() - TILE_SIZE; y++) {
      for (int x=0; x<=reader.getSizeX() - TILE_SIZE; x++) {
        byte[] tile = reader.openBytes(0, x, y, TILE_SIZE, TILE_SIZE);

        for (int q=0; q<tile.length; q++) {
          assertEquals(tile[q], getValue(x, q));
        }
      }
    }
  }

  private void writeFile(int width, int height) throws FormatException, IOException {
    IMetadata meta = MetadataTools.createOMEXMLMetadata();
    populateImage(meta, 0, width, height, 1, false);

    TiffWriter writer = new TiffWriter();
    writer.setWriteSequentially(true);
    writer.setMetadataRetrieve(meta);
    writer.setId(file.getAbsolutePath());

    IFD ifd = new IFD();
    ifd.put(IFD.TILE_WIDTH, TILE_SIZE);
    ifd.put(IFD.TILE_LENGTH, TILE_SIZE);

    byte[] tile = new byte[TILE_SIZE * TILE_SIZE];
    for (int yy=0; yy<height; yy+=TILE_SIZE) {
      for (int xx=0; xx<width; xx+=TILE_SIZE) {
        for (int q=0; q<tile.length; q++) {
          tile[q] = getValue(xx, q);
        }
        writer.saveBytes(0, tile, ifd, xx, yy, TILE_SIZE, TILE_SIZE);
      }
    }
    writer.close();
  }

  private byte getValue(int x, int tilePos) {
    int v = x + (tilePos % TILE_SIZE);
    return (byte) (v & 0xff);
  }

  /**
   * Set metadata for writing a single Image/series.  Does not set subresolution data.
   */
  private void populateImage(IMetadata meta, int p, int width, int height, int planes, boolean bigEndian) {
    meta.setImageID("Image:" + p, p);
    meta.setPixelsID("Pixels:" + p, p);
    meta.setPixelsDimensionOrder(DimensionOrder.XYZCT, p);
    meta.setPixelsSizeX(new PositiveInteger(width), p);
    meta.setPixelsSizeY(new PositiveInteger(height), p);
    meta.setPixelsSizeZ(new PositiveInteger(planes), p);
    meta.setPixelsSizeC(new PositiveInteger(1), p);
    meta.setPixelsSizeT(new PositiveInteger(1), p);
    meta.setPixelsType(PixelType.UINT8, p);
    meta.setPixelsBigEndian(bigEndian, p);
    meta.setChannelID("Channel:" + p + ":0", p, 0);
    meta.setChannelSamplesPerPixel(new PositiveInteger(1), p, 0);
  }

}
