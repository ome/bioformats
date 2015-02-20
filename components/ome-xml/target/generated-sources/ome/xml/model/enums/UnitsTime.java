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

import ome.xml.model.enums.handlers.UnitsTimeEnumHandler;
import ome.xml.model.primitives.*;

public enum UnitsTime implements Enumeration
{
  YOTTAS("Ys"), ZETTAS("Zs"), EXAS("Es"), PETAS("Ps"), TERAS("Ts"), GIGAS("Gs"), MEGAS("Ms"), KS("ks"), HS("hs"), DAS("das"), S("s"), DS("ds"), CS("cs"), MS("ms"), MICROS("µs"), NS("ns"), PS("ps"), FS("fs"), AS("as"), ZS("zs"), YS("ys"), MIN("min"), H("h"), D("d");

  private UnitsTime(String value)
  {
    this.value = value;
  }

  public static UnitsTime fromString(String value)
    throws EnumerationException
  {
    if ("Ys".equals(value))
    {
      return YOTTAS;
    }
    if ("Zs".equals(value))
    {
      return ZETTAS;
    }
    if ("Es".equals(value))
    {
      return EXAS;
    }
    if ("Ps".equals(value))
    {
      return PETAS;
    }
    if ("Ts".equals(value))
    {
      return TERAS;
    }
    if ("Gs".equals(value))
    {
      return GIGAS;
    }
    if ("Ms".equals(value))
    {
      return MEGAS;
    }
    if ("ks".equals(value))
    {
      return KS;
    }
    if ("hs".equals(value))
    {
      return HS;
    }
    if ("das".equals(value))
    {
      return DAS;
    }
    if ("s".equals(value))
    {
      return S;
    }
    if ("ds".equals(value))
    {
      return DS;
    }
    if ("cs".equals(value))
    {
      return CS;
    }
    if ("ms".equals(value))
    {
      return MS;
    }
    if ("µs".equals(value))
    {
      return MICROS;
    }
    if ("ns".equals(value))
    {
      return NS;
    }
    if ("ps".equals(value))
    {
      return PS;
    }
    if ("fs".equals(value))
    {
      return FS;
    }
    if ("as".equals(value))
    {
      return AS;
    }
    if ("zs".equals(value))
    {
      return ZS;
    }
    if ("ys".equals(value))
    {
      return YS;
    }
    if ("min".equals(value))
    {
      return MIN;
    }
    if ("h".equals(value))
    {
      return H;
    }
    if ("d".equals(value))
    {
      return D;
    }
    String s = String.format("'%s' not a supported value of '%s'",
                             value, UnitsTime.class);
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

  public static <T extends PrimitiveNumber> Time create(T newValue, UnitsTime newUnit)
  {
    Time theQuantity = null;
    
    try
    {
      theQuantity = UnitsTimeEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  public static <T extends Number> Time create(T newValue, UnitsTime newUnit)
  {
    Time theQuantity = null;
    
    try
    {
      theQuantity = UnitsTimeEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  private final String value;
}
