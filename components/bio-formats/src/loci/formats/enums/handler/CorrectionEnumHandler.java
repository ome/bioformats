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

import java.util.Hashtable;
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
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    p.put("^\\s*Pl.*Apo.*$", "PlanApo");
    p.put("^\\s*Pl.*Flu.*$", "PlanFluor");
    p.put("^\\s*Sup.*Flu.*$", "SuperFluor");
    p.put("^\\s*Vio.*Corr.*$", "VioletCorrected");
    p.put("^\\s*Achr.*Flu.*$", "Achromat");
    p.put("^\\s*Neo.*flu.*$", "Neofluar");
    p.put("^\\s*Apo.*$", "Apo");
    p.put("^\\s*UV.*$", "UV");
    p.put("^\\s*Fluar.*$", "Fluar");
    p.put("^\\s*Neo.*fluar.*$", "Neofluar");
    p.put("^\\s*Pl.*Neo.*flu.*$", "PlanNeofluar");
    p.put("[Oo]ther", "Other");
    return p;
  }

  // -- IEnumerationHandler API methods --

  /* @see IEnumerationHandler#getEnumeration(String) */
  public <T extends Enumeration> T getEnumeration(String value)
    throws EnumerationException
  {
    for (String pattern : patterns.keySet()) {
      if (value.matches(pattern)) {
        String v = patterns.get(pattern);
        // TODO : uncomment this once enum classes are committed
        //Correction c = Correction.fromString(v);
        //return c;
      }
    }
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }

  /* @see IEnumerationHandler#getEntity() */
  public String getEntity() {
    return "Correction";
  }

}
