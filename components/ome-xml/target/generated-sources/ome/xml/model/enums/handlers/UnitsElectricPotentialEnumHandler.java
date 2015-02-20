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
import ome.xml.model.enums.UnitsElectricPotential;

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
 * Enumeration handler for UnitsElectricPotential.
 */
public class UnitsElectricPotentialEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(UnitsElectricPotentialEnumHandler.class);

  // -- Fields --

  /** Every UnitsElectricPotential value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*YV\\s*", "YV");
    p.put("^\\s*ZV\\s*", "ZV");
    p.put("^\\s*EV\\s*", "EV");
    p.put("^\\s*PV\\s*", "PV");
    p.put("^\\s*TV\\s*", "TV");
    p.put("^\\s*GV\\s*", "GV");
    p.put("^\\s*MV\\s*", "MV");
    p.put("^\\s*kV\\s*", "kV");
    p.put("^\\s*hV\\s*", "hV");
    p.put("^\\s*daV\\s*", "daV");
    p.put("^\\s*V\\s*", "V");
    p.put("^\\s*dV\\s*", "dV");
    p.put("^\\s*cV\\s*", "cV");
    p.put("^\\s*mV\\s*", "mV");
    p.put("^\\s*µV\\s*", "µV");
    p.put("^\\s*nV\\s*", "nV");
    p.put("^\\s*pV\\s*", "pV");
    p.put("^\\s*fV\\s*", "fV");
    p.put("^\\s*aV\\s*", "aV");
    p.put("^\\s*zV\\s*", "zV");
    p.put("^\\s*yV\\s*", "yV");
    return p;
  }

  // -- IEnumerationHandler API methods --

  /* @see IEnumerationHandler#getEnumeration(String) */
  public Enumeration getEnumeration(String value)
    throws EnumerationException
  {
    if (value != null) {
      for (String pattern : patterns.keySet()) {
        // case sensitive compare
        if (value.matches(pattern)) {
          String v = patterns.get(pattern);
          return UnitsElectricPotential.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "UnitsElectricPotential", value);
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }


  public Enumeration getEnumeration(ElectricPotential inUnit)
    throws EnumerationException {
    return getEnumeration(inUnit.unit().getSymbol());
  }

  public static Unit<ElectricPotential> getBaseUnit(UnitsElectricPotential inModelUnit)
  {
    Unit<ElectricPotential> theResult;
    // begin: should be lookup in template
    theResult = UNITS.VOLT;
    if (UnitsElectricPotential.YOTTAV.equals(inModelUnit))
    {
      theResult = UNITS.YOTTAV;
    }
    if (UnitsElectricPotential.ZETTAV.equals(inModelUnit))
    {
      theResult = UNITS.ZETTAV;
    }
    if (UnitsElectricPotential.EXAV.equals(inModelUnit))
    {
      theResult = UNITS.EXAV;
    }
    if (UnitsElectricPotential.PETAV.equals(inModelUnit))
    {
      theResult = UNITS.PETAV;
    }
    if (UnitsElectricPotential.TERAV.equals(inModelUnit))
    {
      theResult = UNITS.TERAV;
    }
    if (UnitsElectricPotential.GIGAV.equals(inModelUnit))
    {
      theResult = UNITS.GIGAV;
    }
    if (UnitsElectricPotential.MEGAV.equals(inModelUnit))
    {
      theResult = UNITS.MEGAV;
    }
    if (UnitsElectricPotential.KV.equals(inModelUnit))
    {
      theResult = UNITS.KV;
    }
    if (UnitsElectricPotential.HV.equals(inModelUnit))
    {
      theResult = UNITS.HV;
    }
    if (UnitsElectricPotential.DAV.equals(inModelUnit))
    {
      theResult = UNITS.DAV;
    }
    if (UnitsElectricPotential.V.equals(inModelUnit))
    {
      theResult = UNITS.V;
    }
    if (UnitsElectricPotential.DV.equals(inModelUnit))
    {
      theResult = UNITS.DV;
    }
    if (UnitsElectricPotential.CV.equals(inModelUnit))
    {
      theResult = UNITS.CV;
    }
    if (UnitsElectricPotential.MV.equals(inModelUnit))
    {
      theResult = UNITS.MV;
    }
    if (UnitsElectricPotential.MICROV.equals(inModelUnit))
    {
      theResult = UNITS.MICROV;
    }
    if (UnitsElectricPotential.NV.equals(inModelUnit))
    {
      theResult = UNITS.NV;
    }
    if (UnitsElectricPotential.PV.equals(inModelUnit))
    {
      theResult = UNITS.PV;
    }
    if (UnitsElectricPotential.FV.equals(inModelUnit))
    {
      theResult = UNITS.FV;
    }
    if (UnitsElectricPotential.AV.equals(inModelUnit))
    {
      theResult = UNITS.AV;
    }
    if (UnitsElectricPotential.ZV.equals(inModelUnit))
    {
      theResult = UNITS.ZV;
    }
    if (UnitsElectricPotential.YV.equals(inModelUnit))
    {
      theResult = UNITS.YV;
    }
    // end: should be lookup in template
    return theResult;
  }

  public static <T extends PrimitiveNumber> ElectricPotential getQuantity(T inValue, UnitsElectricPotential inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof NonNegativeFloat) {
        NonNegativeFloat typedValue = (NonNegativeFloat) inValue;
        return new ome.units.quantity.ElectricPotential(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeInteger) {
        NonNegativeInteger typedValue = (NonNegativeInteger) inValue;
        return new ome.units.quantity.ElectricPotential(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeLong) {
        NonNegativeLong typedValue = (NonNegativeLong) inValue;
        return new ome.units.quantity.ElectricPotential(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PercentFraction) {
        PercentFraction typedValue = (PercentFraction) inValue;
        return new ome.units.quantity.ElectricPotential(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveFloat) {
        PositiveFloat typedValue = (PositiveFloat) inValue;
        return new ome.units.quantity.ElectricPotential(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveInteger) {
        PositiveInteger typedValue = (PositiveInteger) inValue;
        return new ome.units.quantity.ElectricPotential(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveLong) {
        PositiveLong typedValue = (PositiveLong) inValue;
        return new ome.units.quantity.ElectricPotential(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'ElectricPotential' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsElectricPotentialEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  public static <T extends Number> ElectricPotential getQuantity(T inValue, UnitsElectricPotential inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof Double) {
        Double doubleValue = (Double) inValue;
        return new ome.units.quantity.ElectricPotential(doubleValue, getBaseUnit(inModelUnit));
    }
    if (inValue instanceof Integer) {
        Integer intValue = (Integer) inValue;
        return new ome.units.quantity.ElectricPotential(intValue, getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'ElectricPotential' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsElectricPotentialEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return UnitsElectricPotential.class;
  }

}
