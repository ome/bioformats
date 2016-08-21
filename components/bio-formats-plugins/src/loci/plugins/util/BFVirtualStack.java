/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

package loci.plugins.util;

import ij.VirtualStack;
import ij.process.ImageProcessor;

import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.formats.ChannelMerger;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.cache.Cache;
import loci.formats.cache.CacheException;
import loci.formats.cache.CacheStrategy;
import loci.formats.cache.CrosshairStrategy;
import loci.plugins.util.RecordedImageProcessor.MethodEntry;

/**
 * Subclass of VirtualStack that uses Bio-Formats to read planes on demand.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/util/BFVirtualStack.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/util/BFVirtualStack.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class BFVirtualStack extends VirtualStack {

  // -- Fields --

  protected ImageProcessorReader reader;
  protected String id;
  protected Cache cache;

  private List<List<MethodEntry>> methodStacks;
  private int currentSlice = -1;
  private RecordedImageProcessor currentProcessor;

  private boolean colorize;
  private boolean merge;
  private boolean record;

  private int series;

  private int[] len;

  private int[] planeIndexes;

  private float[] calibrationTable;

  // -- Static utility methods --

  protected static int getWidth(IFormatReader r, String path, int series)
    throws FormatException, IOException
  {
    r.setSeries(series);
    return r.getSizeX();
  }

  protected static int getHeight(IFormatReader r, String path, int series)
    throws FormatException, IOException
  {
    r.setSeries(series);
    return r.getSizeY();
  }

  // -- Constructor --

  public BFVirtualStack(String path, IFormatReader r, boolean colorize,
    boolean merge, boolean record)
    throws FormatException, IOException, CacheException
  {
    super(getWidth(r, path, r.getSeries()), getHeight(r, path, r.getSeries()),
      null, path);
    reader = new ImageProcessorReader(r);
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

    methodStacks = new ArrayList<List<MethodEntry>>();
    for (int i=0; i<r.getImageCount(); i++) {
      methodStacks.add(new ArrayList<MethodEntry>());
    }
  }

  // -- BFVirtualStack API methods --

  public String getPath() { return id; }

  public ImageProcessorReader getReader() { return reader; }

  public Cache getCache() { return cache; }

  public RecordedImageProcessor getRecordedProcessor() {
    return currentProcessor;
  }

  public List<MethodEntry> getMethodStack() {
    if (currentSlice >= 0) return methodStacks.get(currentSlice);
    return null;
  }

  public void setPlaneIndexes(int[] planeIndexes) {
    this.planeIndexes = planeIndexes;
  }

  // -- VirtualStack API methods --

  public synchronized ImageProcessor getProcessor(int n) {
    reader.setSeries(series);

    // check cache first
    if (currentSlice >= 0 && currentProcessor != null) {
      List<MethodEntry> currentStack = currentProcessor.getMethodStack();
      if (currentStack.size() > 1) {
        methodStacks.get(currentSlice).addAll(currentStack);
      }
    }
    int sliceIndex = planeIndexes == null ? n - 1 : planeIndexes[n - 1];
    int[] pos = reader.getZCTCoords(sliceIndex);
    if (merge) pos = new ChannelMerger(reader).getZCTCoords(sliceIndex);
    int[] cachePos = FormatTools.rasterToPosition(len, sliceIndex);
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
      currentProcessor = new RecordedImageProcessor(ip, pos[1], otherChannels);
      currentProcessor.setDoRecording(record);
      if (calibrationTable == null) {
        calibrationTable = currentProcessor.getChild().getCalibrationTable();
      }
      else {
        currentProcessor.setCalibrationTable(calibrationTable);
      }
      return currentProcessor.getChild();
    }

    if (ip != null) {
      currentSlice = n - 1;
      currentProcessor = new RecordedImageProcessor(ip);
      currentProcessor.setDoRecording(record);
      if (calibrationTable == null) {
        calibrationTable = currentProcessor.getChild().getCalibrationTable();
      }
      else {
        currentProcessor.setCalibrationTable(calibrationTable);
      }
      return currentProcessor.getChild();
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
    return planeIndexes == null ? reader.getImageCount() : planeIndexes.length;
  }

}
