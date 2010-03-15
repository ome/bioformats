//
// IEnumerationProvider.java
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

import java.util.List;

/**
 * Interface for retrieving enumerations for an entity.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/IEnumerationProvider.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/IEnumerationProvider.java">SVN</a></dd></dl>
 */
public interface IEnumerationProvider {

  /**
   * Retrieve an enumeration for the specified entity and value.
   * @return The enumeration corresponding to the given value, according to
   * the latest OME-XML schema version.  If no enumeration corresponds to the
   * given value, null is returned.
   *
   * @param enumerations List of enumerations for this type.
   */
  <T extends Enumeration> T getEnumeration(Class<T> type, String value)
    throws EnumerationException;

  /**
   * Retrieve an enumeration for the specified entity and value.
   * @return The enumeration corresponding to the given value, according to
   * the given OME-XML schema version.  If no enumeration corresponds to the
   * given value, null is returned.
   */
  <T extends Enumeration> T getEnumeration(Class<T> type, String value,
    String schemaVersion) throws EnumerationException;

  /**
   * Retrieves all of the enumerations for the specified entity.
   * @return The enumerations for the given entity, according to the latest
   * OME-XML schema version.
   */
  <T extends Enumeration> List<T> getEnumerations(Class<T> type)
    throws EnumerationException;

  /**
   * Retrieves all of the enumerations for the specified entity.
   * @return The enumerations for the given entity, according to the given
   * OME-XML schema version.
   */
  <T extends Enumeration> List<T> getEnumerations(Class<T> type,
    String schemaVersion) throws EnumerationException;

}
