//
// JimiServiceImpl.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.services;

import com.sun.jimi.core.Jimi;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.services.DependencyException;
import loci.common.services.Service;
import loci.common.services.ServiceException;
import loci.formats.FormatException;
import loci.formats.codec.ByteVector;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/services/JimiServiceImpl.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/services/JimiServiceImpl.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 */
public class JimiServiceImpl implements JimiService {

  // -- Constants --

  protected static final Logger LOGGER =
    LoggerFactory.getLogger(JimiServiceImpl.class);

  // -- Fields --

  private JimiConsumer consumer;
  private TileCache tiles;
  private RandomAccessInputStream in;

  // -- JimiService API methods --

  /* @see loci.formats.services.JimiService#initialize(String) */
  public void initialize(String id) {
    try {
      initialize(new RandomAccessInputStream(id));
    }
    catch (IOException e) {
      LOGGER.debug("", e);
    }
  }

  /**
   * @see loci.formats.services.JimiService#initialize(RandomAccessInputStream)
   */
  public void initialize(RandomAccessInputStream in) {
    initialize(in, 0, 0);
  }

  /**
   * @see loci.formats.services.JimiService#initialize(RandomAccessInputStream)
   */
  public void initialize(RandomAccessInputStream in, int y, int h) {
    this.in = in;
    tiles = new TileCache();

    ImageProducer producer = Jimi.getImageProducer(this.in);
    consumer = new JimiConsumer(producer, y, h);
    producer.startProduction(consumer);
    while (producer.isConsumer(consumer));
  }

  /* @see loci.formats.services.JimiServices#getScanline(int) */
  public byte[] getScanline(int y) {
    try {
      return tiles.get(0, y, consumer.getWidth(), 1);
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    catch (IOException e) {
      LOGGER.debug("", e);
    }
    return null;
  }

  /* @see loci.formats.services.JimiService#getWidth() */
  public int getWidth() {
    return consumer.getWidth();
  }

  /* @see loci.formats.services.JimiService#getHeight() */
  public int getHeight() {
    return consumer.getHeight();
  }

  /* @see loci.formats.services.JimiService#close() */
  public void close() {
    try {
      if (in != null) {
        in.close();
      }
    }
    catch (IOException e) {
      LOGGER.debug("", e);
    }
    tiles = null;
    consumer = null;
  }

  // -- Helper classes --

  class JimiConsumer implements ImageConsumer {
    private int width, height;
    private ImageProducer producer;
    private int yy = 0, hh = 0;

    public JimiConsumer(ImageProducer producer) {
      this.producer = producer;
    }

    public JimiConsumer(ImageProducer producer, int y, int h) {
      this(producer);
      this.yy = y;
      this.hh = h;
    }

    // -- JimiConsumer API methods --

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    // -- ImageConsumer API methods --

    public void imageComplete(int status) {
      producer.removeConsumer(this);
    }

    public void setDimensions(int width, int height) {
      this.width = width;
      this.height = height;
      if (hh <= 0) hh = height;
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model,
      byte[] pixels, int off, int scanSize)
    {
      LOGGER.debug("Storing row {} of {} ({}%)", new Object[] {y, height,
        ((double) y / height) * 100.0});
      if (y >= (yy + hh)) {
        imageComplete(0);
        return;
      }
      else if (y < yy) return;
      try {
        tiles.add(pixels, x, y, w, h);
      }
      catch (FormatException e) {
        LOGGER.debug("", e);
      }
      catch (IOException e) {
        LOGGER.debug("", e);
      }
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model,
      int[] pixels, int off, int scanSize)
    {
      LOGGER.debug("Storing row {} of {} ({}%)", new Object[] {y, (yy + hh),
        ((double) y / (yy + hh)) * 100.0});
      if (y >= (yy + hh)) {
        imageComplete(0);
        return;
      }
      else if (y < yy) return;
      try {
        tiles.add(pixels, x, y, w, h);
      }
      catch (FormatException e) {
        LOGGER.debug("", e);
      }
      catch (IOException e) {
        LOGGER.debug("", e);
      }
    }

    public void setProperties(Hashtable props) { }
    public void setColorModel(ColorModel model) { }
    public void setHints(int hintFlags) { }
  }

  class TileCache {
    private static final int ROW_COUNT = 256;

    private Hashtable<Region, byte[]> compressedTiles =
      new Hashtable<Region, byte[]>();
    private JPEGCodec codec = new JPEGCodec();
    private CodecOptions options = new CodecOptions();

    private ByteVector toCompress = new ByteVector();
    private int row = 0;

    private Region lastRegion = null;
    private byte[] lastTile = null;

    public TileCache() {
      options.interleaved = true;
      options.littleEndian = false;
    }

    public void add(byte[] pixels, int x, int y, int w, int h)
      throws FormatException, IOException
    {
      toCompress.add(pixels);
      row++;

      if ((y % ROW_COUNT) == ROW_COUNT - 1 || y == getHeight() - 1) {
        Region r = new Region(x, y - row + 1, w, row);
        options.width = w;
        options.height = row;
        options.channels = 1;
        options.bitsPerSample = 8;
        options.signed = false;

        byte[] compressed = codec.compress(toCompress.toByteArray(), options);
        compressedTiles.put(r, compressed);
        toCompress.clear();
      }
    }

    public void add(int[] pixels, int x, int y, int w, int h)
      throws FormatException, IOException
    {
      byte[] buf = new byte[pixels.length * 3];
      for (int i=0; i<pixels.length; i++) {
        buf[i * 3] = (byte) ((pixels[i] & 0xff0000) >> 16);
        buf[i * 3 + 1] = (byte) ((pixels[i] & 0xff00) >> 8);
        buf[i * 3 + 2] = (byte) (pixels[i] & 0xff);
      }

      toCompress.add(buf);
      row++;

      if ((y % ROW_COUNT) == ROW_COUNT - 1 || y == getHeight() - 1) {
        Region r = new Region(x, y - row + 1, w, row);
        options.width = w;
        options.height = row;
        options.channels = 3;
        options.bitsPerSample = 8;
        options.signed = false;

        byte[] compressed = codec.compress(toCompress.toByteArray(), options);
        compressedTiles.put(r, compressed);
        toCompress.clear();
        row = 0;
      }
    }

    public byte[] get(int x, int y, int w, int h)
      throws FormatException, IOException
    {
      Region[] keys = compressedTiles.keySet().toArray(new Region[0]);
      Region r = new Region(x, y, w, h);
      for (Region key : keys) {
        if (key.intersects(r)) {
          r = key;
        }
      }
      if (!r.equals(lastRegion)) {
        lastRegion = r;
        byte[] compressed = null;
        compressed = compressedTiles.get(r);
        if (compressed == null) return null;
        lastTile = codec.decompress(compressed, options);
      }

      int pixel = options.channels * (options.bitsPerSample / 8);
      byte[] buf = new byte[w * h * pixel];

      for (int i=0; i<h; i++) {
        System.arraycopy(lastTile, r.width * pixel * (i + y - r.y) + (x - r.x),
          buf, i * w * pixel, pixel * w);
      }

      return buf;
    }
  }

}
