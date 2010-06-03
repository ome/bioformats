//
// IEnumerationHandler.java
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

package ome.xml.model.enums.handlers;

import ome.xml.model.enums.Enumeration;
import ome.xml.model.enums.EnumerationException;

/**
 * An enumeration provider for making OME data model enumerations available.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/handler/IEnumerationHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/handler/IEnumerationHandler.java">SVN</a></dd></dl>
 */
public interface IEnumerationHandler {

  /**
   * Attempt to find an enumeration from a list of enumeration values.
   * @param value Value for which to find an enumeration.
   */
  Enumeration getEnumeration(String value) throws EnumerationException;

  /**
   * Retrieve the entity corresponding to this handler.
   */
  Class<? extends Enumeration> getEntity();

}
