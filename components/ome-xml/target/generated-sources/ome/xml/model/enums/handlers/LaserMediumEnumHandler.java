/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
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
import ome.xml.model.enums.LaserMedium;

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.primitives.*;

import ome.units.quantity.*;
import ome.units.*;

/**
 * Enumeration handler for LaserMedium.
 */
public class LaserMediumEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(LaserMediumEnumHandler.class);

  // -- Fields --

  /** Every LaserMedium value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*Cu\\s*", "Cu");
    p.put("^\\s*Ag\\s*", "Ag");
    p.put("^\\s*ArFl\\s*", "ArFl");
    p.put("^\\s*ArCl\\s*", "ArCl");
    p.put("^\\s*KrFl\\s*", "KrFl");
    p.put("^\\s*KrCl\\s*", "KrCl");
    p.put("^\\s*XeFl\\s*", "XeFl");
    p.put("^\\s*XeCl\\s*", "XeCl");
    p.put("^\\s*XeBr\\s*", "XeBr");
    p.put("^\\s*N\\s*", "N");
    p.put("^\\s*Ar\\s*", "Ar");
    p.put("^\\s*Kr\\s*", "Kr");
    p.put("^\\s*Xe\\s*", "Xe");
    p.put("^\\s*HeNe\\s*", "HeNe");
    p.put("^\\s*HeCd\\s*", "HeCd");
    p.put("^\\s*CO\\s*", "CO");
    p.put("^\\s*CO2\\s*", "CO2");
    p.put("^\\s*H2O\\s*", "H2O");
    p.put("^\\s*HFl\\s*", "HFl");
    p.put("^\\s*NdGlass\\s*", "NdGlass");
    p.put("^\\s*NdYAG\\s*", "NdYAG");
    p.put("^\\s*ErGlass\\s*", "ErGlass");
    p.put("^\\s*ErYAG\\s*", "ErYAG");
    p.put("^\\s*HoYLF\\s*", "HoYLF");
    p.put("^\\s*HoYAG\\s*", "HoYAG");
    p.put("^\\s*Ruby\\s*", "Ruby");
    p.put("^\\s*TiSapphire\\s*", "TiSapphire");
    p.put("^\\s*Alexandrite\\s*", "Alexandrite");
    p.put("^\\s*Rhodamine6G\\s*", "Rhodamine6G");
    p.put("^\\s*CoumarinC30\\s*", "CoumarinC30");
    p.put("^\\s*GaAs\\s*", "GaAs");
    p.put("^\\s*GaAlAs\\s*", "GaAlAs");
    p.put("^\\s*EMinus\\s*", "EMinus");
    p.put("^\\s*Other\\s*", "Other");
    return p;
  }

  // -- IEnumerationHandler API methods --

  /* @see IEnumerationHandler#getEnumeration(String) */
  public Enumeration getEnumeration(String value)
    throws EnumerationException
  {
    if (value != null) {
      for (String pattern : patterns.keySet()) {
        // case insensitive compare
        if (value.toLowerCase().matches(pattern.toLowerCase())) {
          String v = patterns.get(pattern);
          return LaserMedium.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "LaserMedium", value);
    return LaserMedium.OTHER;
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return LaserMedium.class;
  }

}
