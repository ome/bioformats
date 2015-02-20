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

import ome.xml.model.enums.handlers.UnitsPowerEnumHandler;
import ome.xml.model.primitives.*;

public enum UnitsPower implements Enumeration
{
  YOTTAW("YW"), ZETTAW("ZW"), EXAW("EW"), PETAW("PW"), TERAW("TW"), GIGAW("GW"), MEGAW("MW"), KW("kW"), HW("hW"), DAW("daW"), W("W"), DW("dW"), CW("cW"), MW("mW"), MICROW("µW"), NW("nW"), PW("pW"), FW("fW"), AW("aW"), ZW("zW"), YW("yW");

  private UnitsPower(String value)
  {
    this.value = value;
  }

  public static UnitsPower fromString(String value)
    throws EnumerationException
  {
    if ("YW".equals(value))
    {
      return YOTTAW;
    }
    if ("ZW".equals(value))
    {
      return ZETTAW;
    }
    if ("EW".equals(value))
    {
      return EXAW;
    }
    if ("PW".equals(value))
    {
      return PETAW;
    }
    if ("TW".equals(value))
    {
      return TERAW;
    }
    if ("GW".equals(value))
    {
      return GIGAW;
    }
    if ("MW".equals(value))
    {
      return MEGAW;
    }
    if ("kW".equals(value))
    {
      return KW;
    }
    if ("hW".equals(value))
    {
      return HW;
    }
    if ("daW".equals(value))
    {
      return DAW;
    }
    if ("W".equals(value))
    {
      return W;
    }
    if ("dW".equals(value))
    {
      return DW;
    }
    if ("cW".equals(value))
    {
      return CW;
    }
    if ("mW".equals(value))
    {
      return MW;
    }
    if ("µW".equals(value))
    {
      return MICROW;
    }
    if ("nW".equals(value))
    {
      return NW;
    }
    if ("pW".equals(value))
    {
      return PW;
    }
    if ("fW".equals(value))
    {
      return FW;
    }
    if ("aW".equals(value))
    {
      return AW;
    }
    if ("zW".equals(value))
    {
      return ZW;
    }
    if ("yW".equals(value))
    {
      return YW;
    }
    String s = String.format("'%s' not a supported value of '%s'",
                             value, UnitsPower.class);
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

  public static <T extends PrimitiveNumber> Power create(T newValue, UnitsPower newUnit)
  {
    Power theQuantity = null;
    
    try
    {
      theQuantity = UnitsPowerEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  public static <T extends Number> Power create(T newValue, UnitsPower newUnit)
  {
    Power theQuantity = null;
    
    try
    {
      theQuantity = UnitsPowerEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  private final String value;
}
