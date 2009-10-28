/*
 * loci.formats.enums.handler.LaserMediumHandler
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
 * Created by callan via xsd-fu on 2009-10-28 16:52:37+0000
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.enums.handler;

import java.util.Hashtable;
import java.util.List;

import loci.formats.enums.Enumeration;
import loci.formats.enums.EnumerationException;
import loci.formats.enums.LaserMedium;

/**
 * Enumeration handler for LaserMedium.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/enums/handler/LaserMediumHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/enums/handler/LaserMediumHandler.java">SVN</a></dd></dl>
 */
public class LaserMediumEnumHandler implements IEnumerationHandler {

  // -- Fields --

  /** Every LaserMedium value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    p.put("^\\s*Cu", "Cu");
    p.put("^\\s*Ag", "Ag");
    p.put("^\\s*ArFl", "ArFl");
    p.put("^\\s*ArCl", "ArCl");
    p.put("^\\s*KrFl", "KrFl");
    p.put("^\\s*KrCl", "KrCl");
    p.put("^\\s*XeFl", "XeFl");
    p.put("^\\s*XeCl", "XeCl");
    p.put("^\\s*XeBr", "XeBr");
    p.put("^\\s*N", "N");
    p.put("^\\s*Ar", "Ar");
    p.put("^\\s*Kr", "Kr");
    p.put("^\\s*Xe", "Xe");
    p.put("^\\s*HeNe", "HeNe");
    p.put("^\\s*HeCd", "HeCd");
    p.put("^\\s*CO", "CO");
    p.put("^\\s*CO2", "CO2");
    p.put("^\\s*H2O", "H2O");
    p.put("^\\s*HFl", "HFl");
    p.put("^\\s*NdGlass", "NdGlass");
    p.put("^\\s*NdYAG", "NdYAG");
    p.put("^\\s*ErGlass", "ErGlass");
    p.put("^\\s*ErYAG", "ErYAG");
    p.put("^\\s*HoYLF", "HoYLF");
    p.put("^\\s*HoYAG", "HoYAG");
    p.put("^\\s*Ruby", "Ruby");
    p.put("^\\s*TiSapphire", "TiSapphire");
    p.put("^\\s*Alexandrite", "Alexandrite");
    p.put("^\\s*Rhodamine6G", "Rhodamine6G");
    p.put("^\\s*CoumarinC30", "CoumarinC30");
    p.put("^\\s*GaAs", "GaAs");
    p.put("^\\s*GaAlAs", "GaAlAs");
    p.put("^\\s*EMinus", "EMinus");
    p.put("^\\s*Other", "Other");
    return p;
  }

  // -- IEnumerationHandler API methods --

  /* @see IEnumerationHandler#getEnumeration(String) */
  public Enumeration getEnumeration(String value)
    throws EnumerationException
  {
    for (String pattern : patterns.keySet()) {
      if (value.matches(pattern)) {
        String v = patterns.get(pattern);
        return LaserMedium.fromString(v);
      }
    }
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return LaserMedium.class;
  }

}
