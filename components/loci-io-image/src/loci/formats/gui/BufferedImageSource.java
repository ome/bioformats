//
// BufferedImageSource.java
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

package loci.formats.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.cache.CacheException;
import loci.formats.cache.ICacheSource;


/**
 * Retrieves BufferedImages from a data source using Bio-Formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/BufferedImageSource.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/BufferedImageSource.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class BufferedImageSource implements ICacheSource {

  // -- Fields --

  /** Image reader from which to draw BufferedImages. */
  protected BufferedImageReader reader;

  // -- Constructors --

  public BufferedImageSource(IFormatReader reader) throws CacheException {
    if (reader instanceof BufferedImageReader) {
      this.reader = (BufferedImageReader) reader;
    }
    else {
      this.reader = new BufferedImageReader(reader);
    }
  }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObject(int) */
  public int getObjectCount() { return reader.getImageCount(); }

  /* @see loci.formats.cache.ICacheSource#getObject(int) */
  public Object getObject(int index) throws CacheException {
    BufferedImage bi = null;
    try {
      bi = reader.openImage(index);
    }
    catch (FormatException exc) {
      throw new CacheException(exc);
    }
    catch (IOException exc) {
      throw new CacheException(exc);
    }
    return bi;
  }

}
