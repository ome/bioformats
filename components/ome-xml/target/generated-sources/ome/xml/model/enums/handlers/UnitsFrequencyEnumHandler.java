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
import ome.xml.model.enums.UnitsFrequency;

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
 * Enumeration handler for UnitsFrequency.
 */
public class UnitsFrequencyEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(UnitsFrequencyEnumHandler.class);

  // -- Fields --

  /** Every UnitsFrequency value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*YHz\\s*", "YHz");
    p.put("^\\s*ZHz\\s*", "ZHz");
    p.put("^\\s*EHz\\s*", "EHz");
    p.put("^\\s*PHz\\s*", "PHz");
    p.put("^\\s*THz\\s*", "THz");
    p.put("^\\s*GHz\\s*", "GHz");
    p.put("^\\s*MHz\\s*", "MHz");
    p.put("^\\s*kHz\\s*", "kHz");
    p.put("^\\s*hHz\\s*", "hHz");
    p.put("^\\s*daHz\\s*", "daHz");
    p.put("^\\s*Hz\\s*", "Hz");
    p.put("^\\s*dHz\\s*", "dHz");
    p.put("^\\s*cHz\\s*", "cHz");
    p.put("^\\s*mHz\\s*", "mHz");
    p.put("^\\s*µHz\\s*", "µHz");
    p.put("^\\s*nHz\\s*", "nHz");
    p.put("^\\s*pHz\\s*", "pHz");
    p.put("^\\s*fHz\\s*", "fHz");
    p.put("^\\s*aHz\\s*", "aHz");
    p.put("^\\s*zHz\\s*", "zHz");
    p.put("^\\s*yHz\\s*", "yHz");
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
          return UnitsFrequency.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "UnitsFrequency", value);
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }


  public Enumeration getEnumeration(Frequency inUnit)
    throws EnumerationException {
    return getEnumeration(inUnit.unit().getSymbol());
  }

  public static Unit<Frequency> getBaseUnit(UnitsFrequency inModelUnit)
  {
    Unit<Frequency> theResult;
    // begin: should be lookup in template
    theResult = UNITS.HERTZ;
    if (UnitsFrequency.YOTTAHZ.equals(inModelUnit))
    {
      theResult = UNITS.YOTTAHZ;
    }
    if (UnitsFrequency.ZETTAHZ.equals(inModelUnit))
    {
      theResult = UNITS.ZETTAHZ;
    }
    if (UnitsFrequency.EXAHZ.equals(inModelUnit))
    {
      theResult = UNITS.EXAHZ;
    }
    if (UnitsFrequency.PETAHZ.equals(inModelUnit))
    {
      theResult = UNITS.PETAHZ;
    }
    if (UnitsFrequency.TERAHZ.equals(inModelUnit))
    {
      theResult = UNITS.TERAHZ;
    }
    if (UnitsFrequency.GIGAHZ.equals(inModelUnit))
    {
      theResult = UNITS.GIGAHZ;
    }
    if (UnitsFrequency.MEGAHZ.equals(inModelUnit))
    {
      theResult = UNITS.MEGAHZ;
    }
    if (UnitsFrequency.KHZ.equals(inModelUnit))
    {
      theResult = UNITS.KHZ;
    }
    if (UnitsFrequency.HHZ.equals(inModelUnit))
    {
      theResult = UNITS.HHZ;
    }
    if (UnitsFrequency.DAHZ.equals(inModelUnit))
    {
      theResult = UNITS.DAHZ;
    }
    if (UnitsFrequency.HZ.equals(inModelUnit))
    {
      theResult = UNITS.HZ;
    }
    if (UnitsFrequency.DHZ.equals(inModelUnit))
    {
      theResult = UNITS.DHZ;
    }
    if (UnitsFrequency.CHZ.equals(inModelUnit))
    {
      theResult = UNITS.CHZ;
    }
    if (UnitsFrequency.MHZ.equals(inModelUnit))
    {
      theResult = UNITS.MHZ;
    }
    if (UnitsFrequency.MICROHZ.equals(inModelUnit))
    {
      theResult = UNITS.MICROHZ;
    }
    if (UnitsFrequency.NHZ.equals(inModelUnit))
    {
      theResult = UNITS.NHZ;
    }
    if (UnitsFrequency.PHZ.equals(inModelUnit))
    {
      theResult = UNITS.PHZ;
    }
    if (UnitsFrequency.FHZ.equals(inModelUnit))
    {
      theResult = UNITS.FHZ;
    }
    if (UnitsFrequency.AHZ.equals(inModelUnit))
    {
      theResult = UNITS.AHZ;
    }
    if (UnitsFrequency.ZHZ.equals(inModelUnit))
    {
      theResult = UNITS.ZHZ;
    }
    if (UnitsFrequency.YHZ.equals(inModelUnit))
    {
      theResult = UNITS.YHZ;
    }
    // end: should be lookup in template
    return theResult;
  }

  public static <T extends PrimitiveNumber> Frequency getQuantity(T inValue, UnitsFrequency inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof NonNegativeFloat) {
        NonNegativeFloat typedValue = (NonNegativeFloat) inValue;
        return new ome.units.quantity.Frequency(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeInteger) {
        NonNegativeInteger typedValue = (NonNegativeInteger) inValue;
        return new ome.units.quantity.Frequency(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeLong) {
        NonNegativeLong typedValue = (NonNegativeLong) inValue;
        return new ome.units.quantity.Frequency(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PercentFraction) {
        PercentFraction typedValue = (PercentFraction) inValue;
        return new ome.units.quantity.Frequency(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveFloat) {
        PositiveFloat typedValue = (PositiveFloat) inValue;
        return new ome.units.quantity.Frequency(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveInteger) {
        PositiveInteger typedValue = (PositiveInteger) inValue;
        return new ome.units.quantity.Frequency(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveLong) {
        PositiveLong typedValue = (PositiveLong) inValue;
        return new ome.units.quantity.Frequency(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Frequency' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsFrequencyEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  public static <T extends Number> Frequency getQuantity(T inValue, UnitsFrequency inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof Double) {
        Double doubleValue = (Double) inValue;
        return new ome.units.quantity.Frequency(doubleValue, getBaseUnit(inModelUnit));
    }
    if (inValue instanceof Integer) {
        Integer intValue = (Integer) inValue;
        return new ome.units.quantity.Frequency(intValue, getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Frequency' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsFrequencyEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return UnitsFrequency.class;
  }

}
