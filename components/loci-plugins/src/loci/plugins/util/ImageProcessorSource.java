/*
 * #%L
 * LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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

import ij.process.ImageProcessor;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.cache.CacheException;
import loci.formats.cache.ICacheSource;

/**
 * Retrieves ImageJ image processors from an image reader.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/loci-plugins/src/loci/plugins/util/ImageProcessorSource.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/loci-plugins/src/loci/plugins/util/ImageProcessorSource.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ImageProcessorSource implements ICacheSource {

  // -- Fields --

  /** Image reader from which to draw ImageProcessors. */
  protected ImageProcessorReader reader;

  // -- Constructors --

  public ImageProcessorSource(IFormatReader reader) {
    if (reader instanceof ImageProcessorReader) {
      this.reader = (ImageProcessorReader) reader;
    }
    else {
      this.reader = new ImageProcessorReader(reader);
    }
  }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObject(int) */
  public int getObjectCount() { return reader.getImageCount(); }

  /* @see loci.formats.cache.ICacheSource#getObject(int) */
  public Object getObject(int index) throws CacheException {
    // assumes that channels are separated
    ImageProcessor ip = null;
    try {
      ip = reader.openProcessors(index)[0];
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
