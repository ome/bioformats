//
// CorrectionEnumHandler.java
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

package loci.formats.enums.handler;

import java.util.List;

import loci.formats.enums.Enumeration;
import loci.formats.enums.EnumerationException;

/**
 * Enumeration handler for Objective Correction.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/handler/CorrectionEnumHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/handler/CorrectionEnumHandler.java">SVN</a></dd></dl>
 */
public class CorrectionEnumHandler implements IEnumerationHandler {

  // -- Fields --

  /** Every Correction value must match one of these patterns. */
  private static final String[] patterns = new String[] {
    "^\\s*Pl.*Apo.*$",
    "^\\s*Pl.*Flu.*$",
    "^\\s*Sup.*Flu.*$",
    "^\\s*Vio.*Corr.*$",
    "^\\s*Achr.*Flu.*$",
    "^\\s*Neo.*flu.*$",
    "^\\s*Apo.*$"
  };

  // -- IEnumerationHandler API methods --

  /* @see IEnumerationHandler#getEnumeration(List<T>, String) */
  public <T extends Enumeration> T getEnumeration(List<T> enumerations,
    String value) throws EnumerationException
  {
    for (String pattern : patterns) {
      if (value.matches(pattern)) {
        for (T e : enumerations) {
          if (e.toString().matches(pattern)) {
            return e;
          }
        }
        throw new EnumerationException(this.getClass().getName() +
          " could not find enumeration for " + value);
      }
    }
    return null;
  }

  /* @see IEnumerationHandler#getEntity() */
  public String getEntity() {
    return "Correction";
  }

}
