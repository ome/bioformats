//
// BFVirtualStack.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.util;

import ij.VirtualStack;
import ij.process.ImageProcessor;

import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Vector;

import loci.formats.ChannelMerger;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.cache.Cache;
import loci.formats.cache.CacheException;
import loci.formats.cache.CacheStrategy;
import loci.formats.cache.CrosshairStrategy;

/**
 * Subclass of VirtualStack that uses Bio-Formats to read planes on demand.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/BFVirtualStack.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/BFVirtualStack.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class BFVirtualStack extends VirtualStack {

  // -- Fields --

  protected ImagePlusReader reader;
  protected String id;
  protected Cache cache;

  private Vector[] methodStacks;
  private int currentSlice = -1;
  private RecordedImageProcessor currentProcessor;

  private boolean colorize;
  private boolean merge;
  private boolean record;

  private int series;

  private int[] len;

  // -- Static utility methods --

  protected static int getWidth(IFormatReader r, String path)
    throws FormatException, IOException
  {
    r.setId(path);
    return r.getSizeX();
  }

  protected static int getHeight(IFormatReader r, String path)
    throws FormatException, IOException
  {
    r.setId(path);
    return r.getSizeY();
  }

  // -- Constructor --

  public BFVirtualStack(String path, IFormatReader r, boolean colorize,
    boolean merge, boolean record)
    throws FormatException, IOException, CacheException
  {
    super(getWidth(r, path), getHeight(r, path), null, path);
    reader = new ImagePlusReader(r);
    id = path;

    this.colorize = colorize;
    this.merge = merge && !r.isIndexed();
    this.record = record;

    this.series = r.getSeries();

    // set up cache
    int[] subC = r.getChannelDimLengths();
    if (merge) subC = new int[] {new ChannelMerger(r).getEffectiveSizeC()};
    len = new int[subC.length + 2];
    System.arraycopy(subC, 0, len, 0, subC.length);
    len[len.length - 2] = r.getSizeZ();
    len[len.length - 1] = r.getSizeT();
    CacheStrategy strategy = new CrosshairStrategy(len);

    cache = new Cache(strategy, new ImageProcessorSource(r), true);

    methodStacks = new Vector[r.getImageCount()];
    for (int i=0; i<methodStacks.length; i++) {
      methodStacks[i] = new Vector();
    }
  }

  // -- BFVirtualStack API methods --

  public String getPath() { return id; }

  public ImagePlusReader getReader() { return reader; }

  public Cache getCache() { return cache; }

  public RecordedImageProcessor getRecordedProcessor() {
    return currentProcessor;
  }

  public Vector getMethodStack() {
    if (currentSlice >= 0) return methodStacks[currentSlice];
    return null;
  }

  // -- VirtualStack API methods --

  public synchronized ImageProcessor getProcessor(int n) {
    reader.setSeries(series);

    // check cache first
    if (currentSlice >= 0 && currentProcessor != null) {
      Vector currentStack = currentProcessor.getMethodStack();
      if (currentStack.size() > 1) {
        methodStacks[currentSlice].addAll(currentStack);
      }
    }
    int[] pos = reader.getZCTCoords(n - 1);
    if (merge) pos = new ChannelMerger(reader).getZCTCoords(n - 1);
    int[] cachePos = FormatTools.rasterToPosition(len, n - 1);
    ImageProcessor ip = null;

    try {
      ip = (ImageProcessor) cache.getObject(cachePos);
      cache.setCurrentPos(cachePos);
    }
    catch (CacheException exc) {
      exc.printStackTrace();
    }

    // cache missed
    try {
      if (ip == null) {
        ip = reader.openProcessors(reader.getIndex(pos[0], pos[1], pos[2]))[0];
      }
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }

    if (colorize) {
      // apply color table, if necessary
      byte[] lut = new byte[256];
      byte[] blank = new byte[256];
      for (int i=0; i<lut.length; i++) {
        lut[i] = (byte) i;
        blank[i] = (byte) 0;
      }

      IndexColorModel model = null;
      if (pos[1] < 3) {
        model = new IndexColorModel(8, 256, pos[1] == 0 ? lut : blank,
          pos[1] == 1 ? lut : blank, pos[1] == 2 ? lut : blank);
      }
      else model = new IndexColorModel(8, 256, lut, lut, lut);

      if (ip != null) ip.setColorModel(model);
    }
    else if (merge) {
      currentSlice = n - 1;
      ImageProcessor[] otherChannels =
        new ImageProcessor[reader.getSizeC() - 1];
      for (int i=0; i<otherChannels.length; i++) {
        int channel = i >= pos[1] ? i + 1 : i;
        try {
          cachePos[0] = channel;
          otherChannels[i] = (ImageProcessor) cache.getObject(cachePos);
        }
        catch (CacheException exc) {
          exc.printStackTrace();
        }
        if (otherChannels[i] == null) {
          try {
            int index = reader.getIndex(pos[0], channel, pos[2]);
            otherChannels[i] = reader.openProcessors(index)[0];
          }
          catch (FormatException exc) {
            exc.printStackTrace();
          }
          catch (IOException exc) {
            exc.printStackTrace();
          }
        }
      }
      currentProcessor = new RecordedImageProcessor(ip, currentSlice, pos[1],
        otherChannels);
      currentProcessor.setDoRecording(record);
      return currentProcessor;
    }

    if (ip != null) {
      currentSlice = n - 1;
      currentProcessor = new RecordedImageProcessor(ip, currentSlice);
      currentProcessor.setDoRecording(record);
      return currentProcessor;
    }

    return null;
  }

  public int getWidth() {
    reader.setSeries(series);
    return reader.getSizeX();
  }

  public int getHeight() {
    reader.setSeries(series);
    return reader.getSizeY();
  }

  public int getSize() {
    if (reader.getCurrentFile() == null) return 0;
    reader.setSeries(series);
    if (merge) return new ChannelMerger(reader).getImageCount();
    return reader.getImageCount();
  }

}
