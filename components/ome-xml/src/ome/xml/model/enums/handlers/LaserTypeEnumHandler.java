/*
 * ome.xml.model.enums.handlers.LaserTypeHandler
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
 * Created by callan via xsd-fu on 2010-06-11 17:18:07+0100
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
import ome.xml.model.enums.LaserType;

/**
 * Enumeration handler for LaserType.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/handler/LaserTypeHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/handler/LaserTypeHandler.java">SVN</a></dd></dl>
 */
public class LaserTypeEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(LaserTypeEnumHandler.class);

  // -- Fields --

  /** Every LaserType value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*Excimer\\s*", "Excimer");
    p.put("^\\s*Gas\\s*", "Gas");
    p.put("^\\s*MetalVapor\\s*", "MetalVapor");
    p.put("^\\s*SolidState\\s*", "SolidState");
    p.put("^\\s*Dye\\s*", "Dye");
    p.put("^\\s*Semiconductor\\s*", "Semiconductor");
    p.put("^\\s*FreeElectron\\s*", "FreeElectron");
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
          return LaserType.fromString(v);
        }
      }
    }
    LOGGER.warn("Could not find enumeration for '{}'({})",
                value, value.length());
    return LaserType.OTHER;
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return LaserType.class;
  }

}
