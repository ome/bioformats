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
import ome.xml.model.enums.UnitsTime;

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
 * Enumeration handler for UnitsTime.
 */
public class UnitsTimeEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(UnitsTimeEnumHandler.class);

  // -- Fields --

  /** Every UnitsTime value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*Ys\\s*", "Ys");
    p.put("^\\s*Zs\\s*", "Zs");
    p.put("^\\s*Es\\s*", "Es");
    p.put("^\\s*Ps\\s*", "Ps");
    p.put("^\\s*Ts\\s*", "Ts");
    p.put("^\\s*Gs\\s*", "Gs");
    p.put("^\\s*Ms\\s*", "Ms");
    p.put("^\\s*ks\\s*", "ks");
    p.put("^\\s*hs\\s*", "hs");
    p.put("^\\s*das\\s*", "das");
    p.put("^\\s*s\\s*", "s");
    p.put("^\\s*ds\\s*", "ds");
    p.put("^\\s*cs\\s*", "cs");
    p.put("^\\s*ms\\s*", "ms");
    p.put("^\\s*µs\\s*", "µs");
    p.put("^\\s*ns\\s*", "ns");
    p.put("^\\s*ps\\s*", "ps");
    p.put("^\\s*fs\\s*", "fs");
    p.put("^\\s*as\\s*", "as");
    p.put("^\\s*zs\\s*", "zs");
    p.put("^\\s*ys\\s*", "ys");
    p.put("^\\s*min\\s*", "min");
    p.put("^\\s*h\\s*", "h");
    p.put("^\\s*d\\s*", "d");
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
          return UnitsTime.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "UnitsTime", value);
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }


  public Enumeration getEnumeration(Time inUnit)
    throws EnumerationException {
    return getEnumeration(inUnit.unit().getSymbol());
  }

  public static Unit<Time> getBaseUnit(UnitsTime inModelUnit)
  {
    Unit<Time> theResult;
    // begin: should be lookup in template
    theResult = UNITS.SECOND;
    if (UnitsTime.YOTTAS.equals(inModelUnit))
    {
      theResult = UNITS.YOTTAS;
    }
    if (UnitsTime.ZETTAS.equals(inModelUnit))
    {
      theResult = UNITS.ZETTAS;
    }
    if (UnitsTime.EXAS.equals(inModelUnit))
    {
      theResult = UNITS.EXAS;
    }
    if (UnitsTime.PETAS.equals(inModelUnit))
    {
      theResult = UNITS.PETAS;
    }
    if (UnitsTime.TERAS.equals(inModelUnit))
    {
      theResult = UNITS.TERAS;
    }
    if (UnitsTime.GIGAS.equals(inModelUnit))
    {
      theResult = UNITS.GIGAS;
    }
    if (UnitsTime.MEGAS.equals(inModelUnit))
    {
      theResult = UNITS.MEGAS;
    }
    if (UnitsTime.KS.equals(inModelUnit))
    {
      theResult = UNITS.KS;
    }
    if (UnitsTime.HS.equals(inModelUnit))
    {
      theResult = UNITS.HS;
    }
    if (UnitsTime.DAS.equals(inModelUnit))
    {
      theResult = UNITS.DAS;
    }
    if (UnitsTime.S.equals(inModelUnit))
    {
      theResult = UNITS.S;
    }
    if (UnitsTime.DS.equals(inModelUnit))
    {
      theResult = UNITS.DS;
    }
    if (UnitsTime.CS.equals(inModelUnit))
    {
      theResult = UNITS.CS;
    }
    if (UnitsTime.MS.equals(inModelUnit))
    {
      theResult = UNITS.MS;
    }
    if (UnitsTime.MICROS.equals(inModelUnit))
    {
      theResult = UNITS.MICROS;
    }
    if (UnitsTime.NS.equals(inModelUnit))
    {
      theResult = UNITS.NS;
    }
    if (UnitsTime.PS.equals(inModelUnit))
    {
      theResult = UNITS.PS;
    }
    if (UnitsTime.FS.equals(inModelUnit))
    {
      theResult = UNITS.FS;
    }
    if (UnitsTime.AS.equals(inModelUnit))
    {
      theResult = UNITS.AS;
    }
    if (UnitsTime.ZS.equals(inModelUnit))
    {
      theResult = UNITS.ZS;
    }
    if (UnitsTime.YS.equals(inModelUnit))
    {
      theResult = UNITS.YS;
    }
    if (UnitsTime.MIN.equals(inModelUnit))
    {
      theResult = UNITS.MIN;
    }
    if (UnitsTime.H.equals(inModelUnit))
    {
      theResult = UNITS.H;
    }
    if (UnitsTime.D.equals(inModelUnit))
    {
      theResult = UNITS.D;
    }
    // end: should be lookup in template
    return theResult;
  }

  public static <T extends PrimitiveNumber> Time getQuantity(T inValue, UnitsTime inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof NonNegativeFloat) {
        NonNegativeFloat typedValue = (NonNegativeFloat) inValue;
        return new ome.units.quantity.Time(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeInteger) {
        NonNegativeInteger typedValue = (NonNegativeInteger) inValue;
        return new ome.units.quantity.Time(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeLong) {
        NonNegativeLong typedValue = (NonNegativeLong) inValue;
        return new ome.units.quantity.Time(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PercentFraction) {
        PercentFraction typedValue = (PercentFraction) inValue;
        return new ome.units.quantity.Time(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveFloat) {
        PositiveFloat typedValue = (PositiveFloat) inValue;
        return new ome.units.quantity.Time(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveInteger) {
        PositiveInteger typedValue = (PositiveInteger) inValue;
        return new ome.units.quantity.Time(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveLong) {
        PositiveLong typedValue = (PositiveLong) inValue;
        return new ome.units.quantity.Time(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Time' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsTimeEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  public static <T extends Number> Time getQuantity(T inValue, UnitsTime inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof Double) {
        Double doubleValue = (Double) inValue;
        return new ome.units.quantity.Time(doubleValue, getBaseUnit(inModelUnit));
    }
    if (inValue instanceof Integer) {
        Integer intValue = (Integer) inValue;
        return new ome.units.quantity.Time(intValue, getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Time' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsTimeEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return UnitsTime.class;
  }

}
