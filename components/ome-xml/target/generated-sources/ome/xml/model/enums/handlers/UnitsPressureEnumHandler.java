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
import ome.xml.model.enums.UnitsPressure;

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
 * Enumeration handler for UnitsPressure.
 */
public class UnitsPressureEnumHandler implements IEnumerationHandler {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(UnitsPressureEnumHandler.class);

  // -- Fields --

  /** Every UnitsPressure value must match one of these patterns. */
  private static final Hashtable<String, String> patterns = makePatterns();

  private static Hashtable<String, String> makePatterns() {
    Hashtable<String, String> p = new Hashtable<String, String>();
    // BEGIN Schema enumeration mappings
    p.put("^\\s*YPa\\s*", "YPa");
    p.put("^\\s*ZPa\\s*", "ZPa");
    p.put("^\\s*EPa\\s*", "EPa");
    p.put("^\\s*PPa\\s*", "PPa");
    p.put("^\\s*TPa\\s*", "TPa");
    p.put("^\\s*GPa\\s*", "GPa");
    p.put("^\\s*MPa\\s*", "MPa");
    p.put("^\\s*kPa\\s*", "kPa");
    p.put("^\\s*hPa\\s*", "hPa");
    p.put("^\\s*daPa\\s*", "daPa");
    p.put("^\\s*Pa\\s*", "Pa");
    p.put("^\\s*dPa\\s*", "dPa");
    p.put("^\\s*cPa\\s*", "cPa");
    p.put("^\\s*mPa\\s*", "mPa");
    p.put("^\\s*µPa\\s*", "µPa");
    p.put("^\\s*nPa\\s*", "nPa");
    p.put("^\\s*pPa\\s*", "pPa");
    p.put("^\\s*fPa\\s*", "fPa");
    p.put("^\\s*aPa\\s*", "aPa");
    p.put("^\\s*zPa\\s*", "zPa");
    p.put("^\\s*yPa\\s*", "yPa");
    p.put("^\\s*Mbar\\s*", "Mbar");
    p.put("^\\s*kbar\\s*", "kbar");
    p.put("^\\s*dbar\\s*", "dbar");
    p.put("^\\s*cbar\\s*", "cbar");
    p.put("^\\s*mbar\\s*", "mbar");
    p.put("^\\s*atm\\s*", "atm");
    p.put("^\\s*psi\\s*", "psi");
    p.put("^\\s*Torr\\s*", "Torr");
    p.put("^\\s*mTorr\\s*", "mTorr");
    p.put("^\\s*mm Hg\\s*", "mm Hg");
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
          return UnitsPressure.fromString(v);
        }
      }
    }
    LOGGER.warn("Unknown {} value '{}' will be stored as \"Other\"",
      "UnitsPressure", value);
    throw new EnumerationException(this.getClass().getName() +
     " could not find enumeration for " + value);
  }


  public Enumeration getEnumeration(Pressure inUnit)
    throws EnumerationException {
    return getEnumeration(inUnit.unit().getSymbol());
  }

  public static Unit<Pressure> getBaseUnit(UnitsPressure inModelUnit)
  {
    Unit<Pressure> theResult;
    // begin: should be lookup in template
    theResult = UNITS.PASCAL;
    if (UnitsPressure.YOTTAPA.equals(inModelUnit))
    {
      theResult = UNITS.YOTTAPA;
    }
    if (UnitsPressure.ZETTAPA.equals(inModelUnit))
    {
      theResult = UNITS.ZETTAPA;
    }
    if (UnitsPressure.EXAPA.equals(inModelUnit))
    {
      theResult = UNITS.EXAPA;
    }
    if (UnitsPressure.PETAPA.equals(inModelUnit))
    {
      theResult = UNITS.PETAPA;
    }
    if (UnitsPressure.TERAPA.equals(inModelUnit))
    {
      theResult = UNITS.TERAPA;
    }
    if (UnitsPressure.GIGAPA.equals(inModelUnit))
    {
      theResult = UNITS.GIGAPA;
    }
    if (UnitsPressure.MEGAPA.equals(inModelUnit))
    {
      theResult = UNITS.MEGAPA;
    }
    if (UnitsPressure.KPA.equals(inModelUnit))
    {
      theResult = UNITS.KPA;
    }
    if (UnitsPressure.HPA.equals(inModelUnit))
    {
      theResult = UNITS.HPA;
    }
    if (UnitsPressure.DAPA.equals(inModelUnit))
    {
      theResult = UNITS.DAPA;
    }
    if (UnitsPressure.PA.equals(inModelUnit))
    {
      theResult = UNITS.PA;
    }
    if (UnitsPressure.DPA.equals(inModelUnit))
    {
      theResult = UNITS.DPA;
    }
    if (UnitsPressure.CPA.equals(inModelUnit))
    {
      theResult = UNITS.CPA;
    }
    if (UnitsPressure.MPA.equals(inModelUnit))
    {
      theResult = UNITS.MPA;
    }
    if (UnitsPressure.MICROPA.equals(inModelUnit))
    {
      theResult = UNITS.MICROPA;
    }
    if (UnitsPressure.NPA.equals(inModelUnit))
    {
      theResult = UNITS.NPA;
    }
    if (UnitsPressure.PPA.equals(inModelUnit))
    {
      theResult = UNITS.PPA;
    }
    if (UnitsPressure.FPA.equals(inModelUnit))
    {
      theResult = UNITS.FPA;
    }
    if (UnitsPressure.APA.equals(inModelUnit))
    {
      theResult = UNITS.APA;
    }
    if (UnitsPressure.ZPA.equals(inModelUnit))
    {
      theResult = UNITS.ZPA;
    }
    if (UnitsPressure.YPA.equals(inModelUnit))
    {
      theResult = UNITS.YPA;
    }
    if (UnitsPressure.MEGABAR.equals(inModelUnit))
    {
      theResult = UNITS.MEGABAR;
    }
    if (UnitsPressure.KBAR.equals(inModelUnit))
    {
      theResult = UNITS.KBAR;
    }
    if (UnitsPressure.DBAR.equals(inModelUnit))
    {
      theResult = UNITS.DBAR;
    }
    if (UnitsPressure.CBAR.equals(inModelUnit))
    {
      theResult = UNITS.CBAR;
    }
    if (UnitsPressure.MBAR.equals(inModelUnit))
    {
      theResult = UNITS.MBAR;
    }
    if (UnitsPressure.ATM.equals(inModelUnit))
    {
      theResult = UNITS.ATM;
    }
    if (UnitsPressure.PSI.equals(inModelUnit))
    {
      theResult = UNITS.PSI;
    }
    if (UnitsPressure.TORR.equals(inModelUnit))
    {
      theResult = UNITS.TORR;
    }
    if (UnitsPressure.MTORR.equals(inModelUnit))
    {
      theResult = UNITS.MTORR;
    }
    if (UnitsPressure.MMHG.equals(inModelUnit))
    {
      theResult = UNITS.MMHG;
    }
    // end: should be lookup in template
    return theResult;
  }

  public static <T extends PrimitiveNumber> Pressure getQuantity(T inValue, UnitsPressure inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof NonNegativeFloat) {
        NonNegativeFloat typedValue = (NonNegativeFloat) inValue;
        return new ome.units.quantity.Pressure(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeInteger) {
        NonNegativeInteger typedValue = (NonNegativeInteger) inValue;
        return new ome.units.quantity.Pressure(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof NonNegativeLong) {
        NonNegativeLong typedValue = (NonNegativeLong) inValue;
        return new ome.units.quantity.Pressure(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PercentFraction) {
        PercentFraction typedValue = (PercentFraction) inValue;
        return new ome.units.quantity.Pressure(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveFloat) {
        PositiveFloat typedValue = (PositiveFloat) inValue;
        return new ome.units.quantity.Pressure(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveInteger) {
        PositiveInteger typedValue = (PositiveInteger) inValue;
        return new ome.units.quantity.Pressure(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    if (inValue instanceof PositiveLong) {
        PositiveLong typedValue = (PositiveLong) inValue;
        return new ome.units.quantity.Pressure(typedValue.getValue(), getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Pressure' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsPressureEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  public static <T extends Number> Pressure getQuantity(T inValue, UnitsPressure inModelUnit)
    throws EnumerationException
  {
    if (inValue instanceof Double) {
        Double doubleValue = (Double) inValue;
        return new ome.units.quantity.Pressure(doubleValue, getBaseUnit(inModelUnit));
    }
    if (inValue instanceof Integer) {
        Integer intValue = (Integer) inValue;
        return new ome.units.quantity.Pressure(intValue, getBaseUnit(inModelUnit));
    }
    LOGGER.warn("Unknown type '{}' cannot be used to create a 'Pressure' quantity",
      inValue.getClass().getName());
    throw new EnumerationException("UnitsPressureEnumHandler: type '"
      + inValue.getClass().getName() + "' cannot be used to create a quantity");
  }

  /* @see IEnumerationHandler#getEntity() */
  public Class<? extends Enumeration> getEntity() {
    return UnitsPressure.class;
  }

}
