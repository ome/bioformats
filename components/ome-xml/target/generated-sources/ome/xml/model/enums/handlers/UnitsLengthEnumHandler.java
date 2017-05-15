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
import ome.xml.model.enums.UnitsLength;

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
 * Enumeration handler for UnitsLength.
 */
public class UnitsLengthEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(UnitsLengthEnumHandler.class);

  // -- Fields --

  /** Every UnitsLength value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*Ym\\s*", "Ym");
    p.put("^\\s*Zm\\s*", "Zm");
    p.put("^\\s*Em\\s*", "Em");
    p.put("^\\s*Pm\\s*", "Pm");
    p.put("^\\s*Tm\\s*", "Tm");
    p.put("^\\s*Gm\\s*", "Gm");
    p.put("^\\s*Mm\\s*", "Mm");
    p.put("^\\s*km\\s*", "km");
    p.put("^\\s*hm\\s*", "hm");
    p.put("^\\s*dam\\s*", "dam");
    p.put("^\\s*m\\s*", "m");
    p.put("^\\s*dm\\s*", "dm");
    p.put("^\\s*cm\\s*", "cm");
    p.put("^\\s*mm\\s*", "mm");
    p.put("^\\s*µm\\s*", "µm");
    p.put("^\\s*nm\\s*", "nm");
    p.put("^\\s*pm\\s*", "pm");
    p.put("^\\s*fm\\s*", "fm");
    p.put("^\\s*am\\s*", "am");
    p.put("^\\s*zm\\s*", "zm");
    p.put("^\\s*ym\\s*", "ym");
    p.put("^\\s*Å\\s*", "Å");
    p.put("^\\s*thou\\s*", "thou");
    p.put("^\\s*li\\s*", "li");
    p.put("^\\s*in\\s*", "in");
    p.put("^\\s*ft\\s*", "ft");
    p.put("^\\s*yd\\s*", "yd");
    p.put("^\\s*mi\\s*", "mi");
    p.put("^\\s*ua\\s*", "ua");
    p.put("^\\s*ly\\s*", "ly");
    p.put("^\\s*pc\\s*", "pc");
    p.put("^\\s*pt\\s*", "pt");
    p.put("^\\s*pixel\\s*", "pixel");
    p.put("^\\s*reference frame\\s*", "reference frame");
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
          return UnitsLength.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "UnitsLength", value);
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }


  public Enumeration getEnumeration(Length inUnit)
    throws EnumerationException {
    return getEnumeration(inUnit.unit().getSymbol());
  }

  public static Unit<Length> getBaseUnit(UnitsLength inModelUnit)
  {
    Unit<Length> theResult;
    // begin: should be lookup in template
    theResult = UNITS.METRE;
    if (UnitsLength.YOTTAM.equals(inModelUnit))
    {
      theResult = UNITS.YOTTAM;
    }
    if (UnitsLength.ZETTAM.equals(inModelUnit))
    {
      theResult = UNITS.ZETTAM;
    }
    if (UnitsLength.EXAM.equals(inModelUnit))
    {
      theResult = UNITS.EXAM;
    }
    if (UnitsLength.PETAM.equals(inModelUnit))
    {
      theResult = UNITS.PETAM;
    }
    if (UnitsLength.TERAM.equals(inModelUnit))
    {
      theResult = UNITS.TERAM;
    }
    if (UnitsLength.GIGAM.equals(inModelUnit))
    {
      theResult = UNITS.GIGAM;
    }
    if (UnitsLength.MEGAM.equals(inModelUnit))
    {
      theResult = UNITS.MEGAM;
    }
    if (UnitsLength.KM.equals(inModelUnit))
    {
      theResult = UNITS.KM;
    }
    if (UnitsLength.HM.equals(inModelUnit))
    {
      theResult = UNITS.HM;
    }
    if (UnitsLength.DAM.equals(inModelUnit))
    {
      theResult = UNITS.DAM;
    }
    if (UnitsLength.M.equals(inModelUnit))
    {
      theResult = UNITS.M;
    }
    if (UnitsLength.DM.equals(inModelUnit))
    {
      theResult = UNITS.DM;
    }
    if (UnitsLength.CM.equals(inModelUnit))
    {
      theResult = UNITS.CM;
    }
    if (UnitsLength.MM.equals(inModelUnit))
    {
      theResult = UNITS.MM;
    }
    if (UnitsLength.MICROM.equals(inModelUnit))
    {
      theResult = UNITS.MICROM;
    }
    if (UnitsLength.NM.equals(inModelUnit))
    {
      theResult = UNITS.NM;
    }
    if (UnitsLength.PM.equals(inModelUnit))
    {
      theResult = UNITS.PM;
    }
    if (UnitsLength.FM.equals(inModelUnit))
    {
      theResult = UNITS.FM;
    }
    if (UnitsLength.AM.equals(inModelUnit))
    {
      theResult = UNITS.AM;
    }
    if (UnitsLength.ZM.equals(inModelUnit))
    {
      theResult = UNITS.ZM;
    }
    if (UnitsLength.YM.equals(inModelUnit))
    {
      theResult = UNITS.YM;
    }
    if (UnitsLength.ANGSTROM.equals(inModelUnit))
    {
      theResult = UNITS.ANGSTROM;
    }
    if (UnitsLength.THOU.equals(inModelUnit))
    {
      theResult = UNITS.THOU;
    }
    if (UnitsLength.LI.equals(inModelUnit))
    {
      theResult = UNITS.LI;
    }
    if (UnitsLength.IN.equals(inModelUnit))
    {
      theResult = UNITS.IN;
    }
    if (UnitsLength.FT.equals(inModelUnit))
    {
      theResult = UNITS.FT;
    }
    if (UnitsLength.YD.equals(inModelUnit))
    {
      theResult = UNITS.YD;
    }
    if (UnitsLength.MI.equals(inModelUnit))
    {
      theResult = UNITS.MI;
    }
    if (UnitsLength.UA.equals(inModelUnit))
    {
      theResult = UNITS.UA;
    }
    if (UnitsLength.LY.equals(inModelUnit))
    {
      theResult = UNITS.LY;
    }
    if (UnitsLength.PC.equals(inModelUnit))
    {
      theResult = UNITS.PC;
    }
    if (UnitsLength.PT.equals(inModelUnit))
    {
      theResult = UNITS.PT;
    }
    if (UnitsLength.PIXEL.equals(inModelUnit))
    {
      theResult = UNITS.PIXEL;
    }
    if (UnitsLength.REFERENCEFRAME.equals(inModelUnit))
    {
      theResult = UNITS.REFERENCEFRAME;
    }
    // end: should be lookup in template
    return theResult;
  }

  public static <T extends PrimitiveNumber> Length getQuantity(T inValue, UnitsLength inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof NonNegativeFloat) {
        NonNegativeFloat typedValue = (NonNegativeFloat) inValue;
        return new ome.units.quantity.Length(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeInteger) {
        NonNegativeInteger typedValue = (NonNegativeInteger) inValue;
        return new ome.units.quantity.Length(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeLong) {
        NonNegativeLong typedValue = (NonNegativeLong) inValue;
        return new ome.units.quantity.Length(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PercentFraction) {
        PercentFraction typedValue = (PercentFraction) inValue;
        return new ome.units.quantity.Length(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveFloat) {
        PositiveFloat typedValue = (PositiveFloat) inValue;
        return new ome.units.quantity.Length(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveInteger) {
        PositiveInteger typedValue = (PositiveInteger) inValue;
        return new ome.units.quantity.Length(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveLong) {
        PositiveLong typedValue = (PositiveLong) inValue;
        return new ome.units.quantity.Length(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Length' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsLengthEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  public static <T extends Number> Length getQuantity(T inValue, UnitsLength inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof Double) {
        Double doubleValue = (Double) inValue;
        return new ome.units.quantity.Length(doubleValue, getBaseUnit(inModelUnit));
    }
    if (inValue instanceof Integer) {
        Integer intValue = (Integer) inValue;
        return new ome.units.quantity.Length(intValue, getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Length' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsLengthEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return UnitsLength.class;
  }

}
