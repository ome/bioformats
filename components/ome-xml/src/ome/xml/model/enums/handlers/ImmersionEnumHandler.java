/*
 * ome.xml.model.enums.handlers.ImmersionHandler
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
import ome.xml.model.enums.Immersion;

/**
 * Enumeration handler for Immersion.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/enums/handler/ImmersionHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/enums/handler/ImmersionHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ImmersionEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(ImmersionEnumHandler.class);

  // -- Fields --

  /** Every Immersion value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*Oil\\s*", "Oil");
    p.put("^\\s*Water\\s*", "Water");
    p.put("^\\s*WaterDipping\\s*", "WaterDipping");
    p.put("^\\s*Air\\s*", "Air");
    p.put("^\\s*Multi\\s*", "Multi");
    p.put("^\\s*Glycerol\\s*", "Glycerol");
    p.put("^\\s*Other\\s*", "Other");
    // BEGIN custom enumeration mappings
    p.put("^\\s*Dry\\s*", "Air");
    p.put("^\\s*OI\\s*", "Oil");
    p.put(".*Oil.*", "Oil");
    p.put(".*Oel.*", "Oil");
    p.put(".*Wasser.*", "Water");
    p.put(".*Gly.*", "Glycerol");
    p.put("^\\s*Wl\\s*", "Water");
    p.put("^\\s*W\\s*", "Water");
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
          return Immersion.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "Immersion", value);
    return Immersion.OTHER;
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return Immersion.class;
  }

}
