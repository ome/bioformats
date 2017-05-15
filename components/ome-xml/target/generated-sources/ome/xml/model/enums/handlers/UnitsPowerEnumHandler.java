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
import ome.xml.model.enums.UnitsPower;

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
 * Enumeration handler for UnitsPower.
 */
public class UnitsPowerEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(UnitsPowerEnumHandler.class);

  // -- Fields --

  /** Every UnitsPower value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*YW\\s*", "YW");
    p.put("^\\s*ZW\\s*", "ZW");
    p.put("^\\s*EW\\s*", "EW");
    p.put("^\\s*PW\\s*", "PW");
    p.put("^\\s*TW\\s*", "TW");
    p.put("^\\s*GW\\s*", "GW");
    p.put("^\\s*MW\\s*", "MW");
    p.put("^\\s*kW\\s*", "kW");
    p.put("^\\s*hW\\s*", "hW");
    p.put("^\\s*daW\\s*", "daW");
    p.put("^\\s*W\\s*", "W");
    p.put("^\\s*dW\\s*", "dW");
    p.put("^\\s*cW\\s*", "cW");
    p.put("^\\s*mW\\s*", "mW");
    p.put("^\\s*µW\\s*", "µW");
    p.put("^\\s*nW\\s*", "nW");
    p.put("^\\s*pW\\s*", "pW");
    p.put("^\\s*fW\\s*", "fW");
    p.put("^\\s*aW\\s*", "aW");
    p.put("^\\s*zW\\s*", "zW");
    p.put("^\\s*yW\\s*", "yW");
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
          return UnitsPower.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "UnitsPower", value);
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }


  public Enumeration getEnumeration(Power inUnit)
    throws EnumerationException {
    return getEnumeration(inUnit.unit().getSymbol());
  }

  public static Unit<Power> getBaseUnit(UnitsPower inModelUnit)
  {
    Unit<Power> theResult;
    // begin: should be lookup in template
    theResult = UNITS.WATT;
    if (UnitsPower.YOTTAW.equals(inModelUnit))
    {
      theResult = UNITS.YOTTAW;
    }
    if (UnitsPower.ZETTAW.equals(inModelUnit))
    {
      theResult = UNITS.ZETTAW;
    }
    if (UnitsPower.EXAW.equals(inModelUnit))
    {
      theResult = UNITS.EXAW;
    }
    if (UnitsPower.PETAW.equals(inModelUnit))
    {
      theResult = UNITS.PETAW;
    }
    if (UnitsPower.TERAW.equals(inModelUnit))
    {
      theResult = UNITS.TERAW;
    }
    if (UnitsPower.GIGAW.equals(inModelUnit))
    {
      theResult = UNITS.GIGAW;
    }
    if (UnitsPower.MEGAW.equals(inModelUnit))
    {
      theResult = UNITS.MEGAW;
    }
    if (UnitsPower.KW.equals(inModelUnit))
    {
      theResult = UNITS.KW;
    }
    if (UnitsPower.HW.equals(inModelUnit))
    {
      theResult = UNITS.HW;
    }
    if (UnitsPower.DAW.equals(inModelUnit))
    {
      theResult = UNITS.DAW;
    }
    if (UnitsPower.W.equals(inModelUnit))
    {
      theResult = UNITS.W;
    }
    if (UnitsPower.DW.equals(inModelUnit))
    {
      theResult = UNITS.DW;
    }
    if (UnitsPower.CW.equals(inModelUnit))
    {
      theResult = UNITS.CW;
    }
    if (UnitsPower.MW.equals(inModelUnit))
    {
      theResult = UNITS.MW;
    }
    if (UnitsPower.MICROW.equals(inModelUnit))
    {
      theResult = UNITS.MICROW;
    }
    if (UnitsPower.NW.equals(inModelUnit))
    {
      theResult = UNITS.NW;
    }
    if (UnitsPower.PW.equals(inModelUnit))
    {
      theResult = UNITS.PW;
    }
    if (UnitsPower.FW.equals(inModelUnit))
    {
      theResult = UNITS.FW;
    }
    if (UnitsPower.AW.equals(inModelUnit))
    {
      theResult = UNITS.AW;
    }
    if (UnitsPower.ZW.equals(inModelUnit))
    {
      theResult = UNITS.ZW;
    }
    if (UnitsPower.YW.equals(inModelUnit))
    {
      theResult = UNITS.YW;
    }
    // end: should be lookup in template
    return theResult;
  }

  public static <T extends PrimitiveNumber> Power getQuantity(T inValue, UnitsPower inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof NonNegativeFloat) {
        NonNegativeFloat typedValue = (NonNegativeFloat) inValue;
        return new ome.units.quantity.Power(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeInteger) {
        NonNegativeInteger typedValue = (NonNegativeInteger) inValue;
        return new ome.units.quantity.Power(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeLong) {
        NonNegativeLong typedValue = (NonNegativeLong) inValue;
        return new ome.units.quantity.Power(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PercentFraction) {
        PercentFraction typedValue = (PercentFraction) inValue;
        return new ome.units.quantity.Power(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveFloat) {
        PositiveFloat typedValue = (PositiveFloat) inValue;
        return new ome.units.quantity.Power(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveInteger) {
        PositiveInteger typedValue = (PositiveInteger) inValue;
        return new ome.units.quantity.Power(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveLong) {
        PositiveLong typedValue = (PositiveLong) inValue;
        return new ome.units.quantity.Power(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Power' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsPowerEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  public static <T extends Number> Power getQuantity(T inValue, UnitsPower inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof Double) {
        Double doubleValue = (Double) inValue;
        return new ome.units.quantity.Power(doubleValue, getBaseUnit(inModelUnit));
    }
    if (inValue instanceof Integer) {
        Integer intValue = (Integer) inValue;
        return new ome.units.quantity.Power(intValue, getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Power' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsPowerEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return UnitsPower.class;
  }

}
