//
// ImageProcessorSource.java
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

import ij.process.ImageProcessor;
import java.io.IOException;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.cache.CacheException;
import loci.formats.cache.CacheSource;

/**
 * Retrieves ImageJ image processors from a data source
 * (e.g., a file) using Bio-Formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/ImageProcessorSource.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/ImageProcessorSource.java">SVN</a></dd></dl>
 */
public class ImageProcessorSource extends CacheSource {

  // -- Constructors --

  /** Constructs an ImageProcessor source from the given Bio-Formats reader. */
  public ImageProcessorSource(IFormatReader r) { super(r); }

  /** Constructs an ImageProcessor source that draws from the given file. */
  public ImageProcessorSource(String id) throws CacheException { super(id); }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObject(int) */
  public Object getObject(int index) throws CacheException {
    ImageProcessor ip = null;
    try {
      ip = Util.openProcessor(reader, index);
    }
    catch (FormatException exc) {
      throw new CacheException(exc);
    }
    catch (IOException exc) {
      throw new CacheException(exc);
    }
    return ip;
  }

}
