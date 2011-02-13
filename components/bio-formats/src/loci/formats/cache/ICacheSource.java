//
// ICacheSource.java
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

/**
 * Interface for cache sources. A cache source returns objects
 * in a defined order, from 0 to getObjectCount() - 1.
 * The actual source of the objects is implementation-dependent;
 * Bio-Formats uses {@link loci.formats.IFormatReader} to obtain image planes,
 * but any ordered collection of objects can conceivably be cached.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/cache/ICacheSource.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/cache/ICacheSource.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public interface ICacheSource {

  /** Get the total number of objects that this source can retrieve. */
  int getObjectCount() throws CacheException;

  /** Get the object corresponding to the given index. */
  Object getObject(int index) throws CacheException;

}
