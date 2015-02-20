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

import ome.xml.model.enums.handlers.UnitsFrequencyEnumHandler;
import ome.xml.model.primitives.*;

public enum UnitsFrequency implements Enumeration
{
  YOTTAHZ("YHz"), ZETTAHZ("ZHz"), EXAHZ("EHz"), PETAHZ("PHz"), TERAHZ("THz"), GIGAHZ("GHz"), MEGAHZ("MHz"), KHZ("kHz"), HHZ("hHz"), DAHZ("daHz"), HZ("Hz"), DHZ("dHz"), CHZ("cHz"), MHZ("mHz"), MICROHZ("µHz"), NHZ("nHz"), PHZ("pHz"), FHZ("fHz"), AHZ("aHz"), ZHZ("zHz"), YHZ("yHz");

  private UnitsFrequency(String value)
  {
    this.value = value;
  }

  public static UnitsFrequency fromString(String value)
    throws EnumerationException
  {
    if ("YHz".equals(value))
    {
      return YOTTAHZ;
    }
    if ("ZHz".equals(value))
    {
      return ZETTAHZ;
    }
    if ("EHz".equals(value))
    {
      return EXAHZ;
    }
    if ("PHz".equals(value))
    {
      return PETAHZ;
    }
    if ("THz".equals(value))
    {
      return TERAHZ;
    }
    if ("GHz".equals(value))
    {
      return GIGAHZ;
    }
    if ("MHz".equals(value))
    {
      return MEGAHZ;
    }
    if ("kHz".equals(value))
    {
      return KHZ;
    }
    if ("hHz".equals(value))
    {
      return HHZ;
    }
    if ("daHz".equals(value))
    {
      return DAHZ;
    }
    if ("Hz".equals(value))
    {
      return HZ;
    }
    if ("dHz".equals(value))
    {
      return DHZ;
    }
    if ("cHz".equals(value))
    {
      return CHZ;
    }
    if ("mHz".equals(value))
    {
      return MHZ;
    }
    if ("µHz".equals(value))
    {
      return MICROHZ;
    }
    if ("nHz".equals(value))
    {
      return NHZ;
    }
    if ("pHz".equals(value))
    {
      return PHZ;
    }
    if ("fHz".equals(value))
    {
      return FHZ;
    }
    if ("aHz".equals(value))
    {
      return AHZ;
    }
    if ("zHz".equals(value))
    {
      return ZHZ;
    }
    if ("yHz".equals(value))
    {
      return YHZ;
    }
    String s = String.format("'%s' not a supported value of '%s'",
                             value, UnitsFrequency.class);
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

  public static <T extends PrimitiveNumber> Frequency create(T newValue, UnitsFrequency newUnit)
  {
    Frequency theQuantity = null;
    
    try
    {
      theQuantity = UnitsFrequencyEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  public static <T extends Number> Frequency create(T newValue, UnitsFrequency newUnit)
  {
    Frequency theQuantity = null;
    
    try
    {
      theQuantity = UnitsFrequencyEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  private final String value;
}
