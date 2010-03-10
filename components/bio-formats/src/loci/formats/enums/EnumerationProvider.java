//
// EnumerationProvider.java
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

package loci.formats.enums;

import java.util.Arrays;
import java.util.List;

import loci.formats.MetadataTools;
import loci.formats.enums.handler.HandlerFactory;
import loci.formats.enums.handler.IEnumerationHandler;

/**
 * Implementation of {@link loci.formats.enums.IEnumerationProvider} for
 * providing enumerations based on a particular OME-XML schema.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/EnumerationProvider.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/EnumerationProvider.java">SVN</a></dd></dl>
 */
public class EnumerationProvider implements IEnumerationProvider {

  // -- Fields --

  /** OME-XML schema to use when retrieving enumerations, e.g. "2009-09". */
  private String schema;

  // -- Constructor --

  /** Construct a new EnumerationProvider using the latest supported schema. */
  public EnumerationProvider() {
    this(MetadataTools.getLatestVersion());
  }

  /** Construct a new EnumerationProvider using the given schema version. */
  public EnumerationProvider(String schemaVersion) {
    this.schema = schemaVersion;
  }

  // -- IEnumerationProvider API methods --

  /**
   * @see loci.formats.enums.IEnumerationProvider#getEnumeration(Class,
   *   String)
   */
  public <T extends Enumeration> T getEnumeration(Class<T> entity, String value)
    throws EnumerationException
  {
    return getEnumeration(entity, value, schema);
  }

  /**
   * @see loci.formats.enums.IEnumerationProvider#getEnumeration(Class,
   *   String, String)
   */
  public <T extends Enumeration> T getEnumeration(Class<T> entity, String value,
    String schemaVersion) throws EnumerationException
  {
    IEnumerationHandler handler = HandlerFactory.getHandler(entity);
    return (T) handler.getEnumeration(value);
  }

  /* @see loci.formats.enums.IEnumerationProvider#getEnumerations(Class) */
  public <T extends Enumeration> List<T> getEnumerations(Class<T> entity)
    throws EnumerationException
  {
    return getEnumerations(entity, schema);
  }

  /**
   * @see loci.formats.enums.IEnumerationProvider#getEnumerations(Class,
   *   String)
   */
  public <T extends Enumeration> List<T> getEnumerations(Class<T> entity,
    String schemaVersion) throws EnumerationException
  {
    return Arrays.asList(entity.getEnumConstants());
  }

}
