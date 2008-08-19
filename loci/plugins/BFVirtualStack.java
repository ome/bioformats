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
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.*;
import ij.process.ImageProcessor;
import java.io.IOException;
import java.util.Vector;
import loci.formats.*;
import loci.formats.cache.*;

/**
 * Subclass of VirtualStack that uses Bio-Formats to read planes on demand.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/BFVirtualStack.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/BFVirtualStack.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class BFVirtualStack extends VirtualStack {

  // -- Fields --

  protected IFormatReader reader;
  protected String id;
  protected Cache cache;
//  protected String stackOrder;
//  protected int merge;

  private Vector[] methodStacks;
  private int currentSlice = -1;
  private RecordedImageProcessor currentProcessor;

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

//  public BFVirtualStack(int w, int h, ColorModel cm, String path,
//    IFormatReader r, String stackOrder, int merge)
  public BFVirtualStack(String path, IFormatReader r)
    throws FormatException, IOException, CacheException
  {
    super(getWidth(r, path), getHeight(r, path), null, path);
    reader = r;
    id = path;
//    this.stackOrder = stackOrder;
//    this.merge = merge;

    // set up cache
    int[] len = new int[] {r.getEffectiveSizeC(), r.getSizeZ(), r.getSizeT()};
    CacheStrategy strategy = new CrosshairStrategy(len);

    // set default ranges

    strategy.setRange((int) Math.min(r.getSizeZ(), 1), 0);
    strategy.setRange((int) Math.min(r.getSizeC(), 1), 1);
    strategy.setRange((int) Math.min(r.getSizeT(), 1), 2);
    cache = new Cache(strategy, new ImageProcessorSource(r), true);

    methodStacks = new Vector[r.getImageCount()];
    for (int i=0; i<methodStacks.length; i++) {
      methodStacks[i] = new Vector();
    }
  }

  // -- BFVirtualStack API methods --

  public String getPath() { return id; }

  public IFormatReader getReader() { return reader; }

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
    // check cache first
    if (currentSlice >= 0 && currentProcessor != null) {
      Vector currentStack = currentProcessor.getMethodStack();
      if (currentStack.size() > 1) {
        methodStacks[currentSlice].addAll(currentStack);
      }
    }
    int[] pos = reader.getZCTCoords(n - 1);
    int z = pos[0];
    pos[0] = pos[1];
    pos[1] = z;
    ImageProcessor ip = null;

    try {
      ip = (ImageProcessor) cache.getObject(pos);
      cache.setCurrentPos(pos);
    }
    catch (CacheException exc) {
      exc.printStackTrace();
    }

    // cache missed
    try {
      if (ip == null) {
        ip = Util.openProcessors(reader, n - 1)[0];
      }
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }

    if (ip != null) {
      currentSlice = n - 1;
      currentProcessor = new RecordedImageProcessor(ip, currentSlice);
      return currentProcessor;
    }

    return null;
  }

  public int getWidth() {
    return reader.getSizeX();
  }

  public int getHeight() {
    return reader.getSizeY();
  }

  public int getSize() {
//    return reader.getImageCount() / merge;
    return reader.getImageCount();
  }

}
