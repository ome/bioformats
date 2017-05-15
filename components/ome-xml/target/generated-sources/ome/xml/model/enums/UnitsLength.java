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

import ome.xml.model.enums.handlers.UnitsLengthEnumHandler;
import ome.xml.model.primitives.*;

public enum UnitsLength implements Enumeration
{
  YOTTAM("Ym"), ZETTAM("Zm"), EXAM("Em"), PETAM("Pm"), TERAM("Tm"), GIGAM("Gm"), MEGAM("Mm"), KM("km"), HM("hm"), DAM("dam"), M("m"), DM("dm"), CM("cm"), MM("mm"), MICROM("µm"), NM("nm"), PM("pm"), FM("fm"), AM("am"), ZM("zm"), YM("ym"), ANGSTROM("Å"), THOU("thou"), LI("li"), IN("in"), FT("ft"), YD("yd"), MI("mi"), UA("ua"), LY("ly"), PC("pc"), PT("pt"), PIXEL("pixel"), REFERENCEFRAME("reference frame");

  private UnitsLength(String value)
  {
    this.value = value;
  }

  public static UnitsLength fromString(String value)
    throws EnumerationException
  {
    if ("Ym".equals(value))
    {
      return YOTTAM;
    }
    if ("Zm".equals(value))
    {
      return ZETTAM;
    }
    if ("Em".equals(value))
    {
      return EXAM;
    }
    if ("Pm".equals(value))
    {
      return PETAM;
    }
    if ("Tm".equals(value))
    {
      return TERAM;
    }
    if ("Gm".equals(value))
    {
      return GIGAM;
    }
    if ("Mm".equals(value))
    {
      return MEGAM;
    }
    if ("km".equals(value))
    {
      return KM;
    }
    if ("hm".equals(value))
    {
      return HM;
    }
    if ("dam".equals(value))
    {
      return DAM;
    }
    if ("m".equals(value))
    {
      return M;
    }
    if ("dm".equals(value))
    {
      return DM;
    }
    if ("cm".equals(value))
    {
      return CM;
    }
    if ("mm".equals(value))
    {
      return MM;
    }
    if ("µm".equals(value))
    {
      return MICROM;
    }
    if ("nm".equals(value))
    {
      return NM;
    }
    if ("pm".equals(value))
    {
      return PM;
    }
    if ("fm".equals(value))
    {
      return FM;
    }
    if ("am".equals(value))
    {
      return AM;
    }
    if ("zm".equals(value))
    {
      return ZM;
    }
    if ("ym".equals(value))
    {
      return YM;
    }
    if ("Å".equals(value))
    {
      return ANGSTROM;
    }
    if ("thou".equals(value))
    {
      return THOU;
    }
    if ("li".equals(value))
    {
      return LI;
    }
    if ("in".equals(value))
    {
      return IN;
    }
    if ("ft".equals(value))
    {
      return FT;
    }
    if ("yd".equals(value))
    {
      return YD;
    }
    if ("mi".equals(value))
    {
      return MI;
    }
    if ("ua".equals(value))
    {
      return UA;
    }
    if ("ly".equals(value))
    {
      return LY;
    }
    if ("pc".equals(value))
    {
      return PC;
    }
    if ("pt".equals(value))
    {
      return PT;
    }
    if ("pixel".equals(value))
    {
      return PIXEL;
    }
    if ("reference frame".equals(value))
    {
      return REFERENCEFRAME;
    }
    String s = String.format("'%s' not a supported value of '%s'",
                             value, UnitsLength.class);
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

  public static <T extends PrimitiveNumber> Length create(T newValue, UnitsLength newUnit)
  {
    Length theQuantity = null;
    
    try
    {
      theQuantity = UnitsLengthEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  public static <T extends Number> Length create(T newValue, UnitsLength newUnit)
  {
    Length theQuantity = null;
    
    try
    {
      theQuantity = UnitsLengthEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  private final String value;
}
