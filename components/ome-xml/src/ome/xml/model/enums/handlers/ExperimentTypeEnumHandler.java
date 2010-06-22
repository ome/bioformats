/*
 * ome.xml.model.enums.handlers.ExperimentTypeHandler
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
 * Created by callan via xsd-fu on 2010-06-22 13:36:18+0100
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
import ome.xml.model.enums.ExperimentType;

/**
 * Enumeration handler for ExperimentType.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/handler/ExperimentTypeHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/handler/ExperimentTypeHandler.java">SVN</a></dd></dl>
 */
public class ExperimentTypeEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(ExperimentTypeEnumHandler.class);

  // -- Fields --

  /** Every ExperimentType value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*FP\\s*", "FP");
    p.put("^\\s*FRET\\s*", "FRET");
    p.put("^\\s*TimeLapse\\s*", "TimeLapse");
    p.put("^\\s*FourDPlus\\s*", "FourDPlus");
    p.put("^\\s*Screen\\s*", "Screen");
    p.put("^\\s*Immunocytochemistry\\s*", "Immunocytochemistry");
    p.put("^\\s*Immunofluorescence\\s*", "Immunofluorescence");
    p.put("^\\s*FISH\\s*", "FISH");
    p.put("^\\s*Electrophysiology\\s*", "Electrophysiology");
    p.put("^\\s*IonImaging\\s*", "IonImaging");
    p.put("^\\s*Colocalization\\s*", "Colocalization");
    p.put("^\\s*PGIDocumentation\\s*", "PGIDocumentation");
    p.put("^\\s*FluorescenceLifetime\\s*", "FluorescenceLifetime");
    p.put("^\\s*SpectralImaging\\s*", "SpectralImaging");
    p.put("^\\s*Photobleaching\\s*", "Photobleaching");
    p.put("^\\s*Other\\s*", "Other");
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
          return ExperimentType.fromString(v);
        }
      }
    }
    LOGGER.warn("Could not find enumeration for '{}'({})",
                value, value == null? -1 : value.length());
    return ExperimentType.OTHER;
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return ExperimentType.class;
  }

}
