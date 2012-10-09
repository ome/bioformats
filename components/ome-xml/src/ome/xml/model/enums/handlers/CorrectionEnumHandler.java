/*
 * ome.xml.model.enums.handlers.CorrectionHandler
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2005-@year@ Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via xsd-fu on 2012-09-10 13:40:24-0400
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model.enums.handlers;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ome.xml.model.enums.Enumeration;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.Correction;

/**
 * Enumeration handler for Correction.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/enums/handler/CorrectionHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/enums/handler/CorrectionHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CorrectionEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(CorrectionEnumHandler.class);

  // -- Fields --

  /** Every Correction value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*UV\\s*", "UV");
    p.put("^\\s*PlanApo\\s*", "PlanApo");
    p.put("^\\s*PlanFluor\\s*", "PlanFluor");
    p.put("^\\s*SuperFluor\\s*", "SuperFluor");
    p.put("^\\s*VioletCorrected\\s*", "VioletCorrected");
    p.put("^\\s*Achro\\s*", "Achro");
    p.put("^\\s*Achromat\\s*", "Achromat");
    p.put("^\\s*Fluor\\s*", "Fluor");
    p.put("^\\s*Fl\\s*", "Fl");
    p.put("^\\s*Fluar\\s*", "Fluar");
    p.put("^\\s*Neofluar\\s*", "Neofluar");
    p.put("^\\s*Fluotar\\s*", "Fluotar");
    p.put("^\\s*Apo\\s*", "Apo");
    p.put("^\\s*PlanNeofluar\\s*", "PlanNeofluar");
    p.put("^\\s*Other\\s*", "Other");
    // BEGIN custom enumeration mappings
    p.put(".*Pl.*Apo.*", "PlanApo");
    p.put(".*Pl.*Flu.*", "PlanFluor");
    p.put("^\\s*Vio.*Corr.*", "VioletCorrected");
    p.put(".*S.*Flu.*", "SuperFluor");
    p.put(".*Neo.*flu.*", "Neofluar");
    p.put(".*Flu.*tar.*", "Fluotar");
    p.put(".*Fluo.*", "Fluor");
    p.put(".*Flua.*", "Fluar");
    p.put("^\\s*Apo.*", "Apo");
    return p;
  }

  // -- IEnumerationHandler API methods --

  /* @see IEnumerationHandler#getEnumeration(String) */
  public Enumeration getEnumeration(String value)
    throws EnumerationException {
    if (value != null) {
      for (String pattern : patterns.keySet()) {
        if (value.toLowerCase().matches(pattern.toLowerCase())) {
          String v = patterns.get(pattern);
          return Correction.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "Correction", value);
    return Correction.OTHER;
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return Correction.class;
  }

}
