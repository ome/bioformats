//
// CacheSource.java
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

package loci.formats.cache;

import java.io.IOException;

import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.IFormatReader;

/**
 * Superclass of cache sources that retrieve image planes
 * from a data source (e.g., a file) using Bio-Formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/cache/CacheSource.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/cache/CacheSource.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public abstract class CacheSource implements ICacheSource {

  // -- Fields --

  /** Reader from which to draw image planes. */
  protected IFormatReader reader;

  // -- Constructors --

  /** Constructs a cache source from the given Bio-Formats reader. */
  public CacheSource(IFormatReader r) { reader = r; }

  /** Constructs a cache source that draws from the given file. */
  public CacheSource(String id) throws CacheException {
    this(new FileStitcher());
    try { reader.setId(id); }
    catch (FormatException exc) { throw new CacheException(exc); }
    catch (IOException exc) { throw new CacheException(exc); }
  }

  // -- ICacheSource API methods --

  /* @see loci.formats.cache.ICacheSource#getObjectCount() */
  public int getObjectCount() { return reader.getImageCount(); }

  /* @see ICacheSource#getObject(int) */
  public abstract Object getObject(int index) throws CacheException;

}
