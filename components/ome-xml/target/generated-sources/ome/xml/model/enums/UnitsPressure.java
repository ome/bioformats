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

package ome.xml.model.enums;


import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.handlers.UnitsPressureEnumHandler;
import ome.xml.model.primitives.*;

public enum UnitsPressure implements Enumeration
{
  YOTTAPA("YPa"), ZETTAPA("ZPa"), EXAPA("EPa"), PETAPA("PPa"), TERAPA("TPa"), GIGAPA("GPa"), MEGAPA("MPa"), KPA("kPa"), HPA("hPa"), DAPA("daPa"), PA("Pa"), DPA("dPa"), CPA("cPa"), MPA("mPa"), MICROPA("µPa"), NPA("nPa"), PPA("pPa"), FPA("fPa"), APA("aPa"), ZPA("zPa"), YPA("yPa"), MEGABAR("Mbar"), KBAR("kbar"), DBAR("dbar"), CBAR("cbar"), MBAR("mbar"), ATM("atm"), PSI("psi"), TORR("Torr"), MTORR("mTorr"), MMHG("mm Hg");

  private UnitsPressure(String value)
  {
    this.value = value;
  }

  public static UnitsPressure fromString(String value)
    throws EnumerationException
  {
    if ("YPa".equals(value))
    {
      return YOTTAPA;
    }
    if ("ZPa".equals(value))
    {
      return ZETTAPA;
    }
    if ("EPa".equals(value))
    {
      return EXAPA;
    }
    if ("PPa".equals(value))
    {
      return PETAPA;
    }
    if ("TPa".equals(value))
    {
      return TERAPA;
    }
    if ("GPa".equals(value))
    {
      return GIGAPA;
    }
    if ("MPa".equals(value))
    {
      return MEGAPA;
    }
    if ("kPa".equals(value))
    {
      return KPA;
    }
    if ("hPa".equals(value))
    {
      return HPA;
    }
    if ("daPa".equals(value))
    {
      return DAPA;
    }
    if ("Pa".equals(value))
    {
      return PA;
    }
    if ("dPa".equals(value))
    {
      return DPA;
    }
    if ("cPa".equals(value))
    {
      return CPA;
    }
    if ("mPa".equals(value))
    {
      return MPA;
    }
    if ("µPa".equals(value))
    {
      return MICROPA;
    }
    if ("nPa".equals(value))
    {
      return NPA;
    }
    if ("pPa".equals(value))
    {
      return PPA;
    }
    if ("fPa".equals(value))
    {
      return FPA;
    }
    if ("aPa".equals(value))
    {
      return APA;
    }
    if ("zPa".equals(value))
    {
      return ZPA;
    }
    if ("yPa".equals(value))
    {
      return YPA;
    }
    if ("Mbar".equals(value))
    {
      return MEGABAR;
    }
    if ("kbar".equals(value))
    {
      return KBAR;
    }
    if ("dbar".equals(value))
    {
      return DBAR;
    }
    if ("cbar".equals(value))
    {
      return CBAR;
    }
    if ("mbar".equals(value))
    {
      return MBAR;
    }
    if ("atm".equals(value))
    {
      return ATM;
    }
    if ("psi".equals(value))
    {
      return PSI;
    }
    if ("Torr".equals(value))
    {
      return TORR;
    }
    if ("mTorr".equals(value))
    {
      return MTORR;
    }
    if ("mm Hg".equals(value))
    {
      return MMHG;
    }
    String s = String.format("'%s' not a supported value of '%s'",
                             value, UnitsPressure.class);
    throw new EnumerationException(s);
  }

  public String getValue()
  {
    return value;
  }

  @Override
  public String toString()
  {
    return value;
  }

  public static <T extends PrimitiveNumber> Pressure create(T newValue, UnitsPressure newUnit)
  {
    Pressure theQuantity = null;
    
    try
    {
      theQuantity = UnitsPressureEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  public static <T extends Number> Pressure create(T newValue, UnitsPressure newUnit)
  {
    Pressure theQuantity = null;
    
    try
    {
      theQuantity = UnitsPressureEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  private final String value;
}
