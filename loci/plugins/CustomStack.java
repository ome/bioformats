//
// Importer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.*;
import ij.process.ImageProcessor;
import java.awt.image.ColorModel;
import java.io.IOException;
import loci.formats.*;

/**
 * Subclass of VirtualStack that uses Bio-Formats to read planes on demand.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/CustomStack.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/CustomStack.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class CustomStack extends VirtualStack {

  // -- Fields --

  private IFormatReader reader;
  private String file;
  private String stackOrder;
  private int merge;

  // -- Constructor --

  public CustomStack(int w, int h, ColorModel cm, String path, IFormatReader r,
    String stackOrder, int merge)
  {
    super(w, h, cm, path);
    reader = r;
    file = path;
    this.stackOrder = stackOrder;
    this.merge = merge;
    try {
      reader.setId(path);
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
  }

  // -- VirtualStack API methods --

  public synchronized ImageProcessor getProcessor(int n) {
    try {
      int index = FormatTools.getReorderedIndex(reader, stackOrder, n - 1);
      if (merge <= 1) return Util.openProcessor(reader, index);
      else {
        ImageProcessor[] p = new ImageProcessor[merge];
        int[] zct = FormatTools.getZCTCoords(reader, index * merge);
        for (int q=0; q<merge; q++) {
          p[q] = Util.openProcessor(reader, FormatTools.getIndex(reader,
            zct[0], zct[1] + q, zct[2]));
        }
        return Util.makeRGB(p).getProcessor();
      }
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
    catch (IOException exc) {
      exc.printStackTrace();
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
    return reader.getImageCount() / merge;
  }

  // -- CustomStack API methods --

  public String getPath() { return file; }

  public IFormatReader getReader() { return reader; }

}
