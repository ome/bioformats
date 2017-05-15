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

import ome.xml.model.enums.handlers.UnitsElectricPotentialEnumHandler;
import ome.xml.model.primitives.*;

public enum UnitsElectricPotential implements Enumeration
{
  YOTTAV("YV"), ZETTAV("ZV"), EXAV("EV"), PETAV("PV"), TERAV("TV"), GIGAV("GV"), MEGAV("MV"), KV("kV"), HV("hV"), DAV("daV"), V("V"), DV("dV"), CV("cV"), MV("mV"), MICROV("µV"), NV("nV"), PV("pV"), FV("fV"), AV("aV"), ZV("zV"), YV("yV");

  private UnitsElectricPotential(String value)
  {
    this.value = value;
  }

  public static UnitsElectricPotential fromString(String value)
    throws EnumerationException
  {
    if ("YV".equals(value))
    {
      return YOTTAV;
    }
    if ("ZV".equals(value))
    {
      return ZETTAV;
    }
    if ("EV".equals(value))
    {
      return EXAV;
    }
    if ("PV".equals(value))
    {
      return PETAV;
    }
    if ("TV".equals(value))
    {
      return TERAV;
    }
    if ("GV".equals(value))
    {
      return GIGAV;
    }
    if ("MV".equals(value))
    {
      return MEGAV;
    }
    if ("kV".equals(value))
    {
      return KV;
    }
    if ("hV".equals(value))
    {
      return HV;
    }
    if ("daV".equals(value))
    {
      return DAV;
    }
    if ("V".equals(value))
    {
      return V;
    }
    if ("dV".equals(value))
    {
      return DV;
    }
    if ("cV".equals(value))
    {
      return CV;
    }
    if ("mV".equals(value))
    {
      return MV;
    }
    if ("µV".equals(value))
    {
      return MICROV;
    }
    if ("nV".equals(value))
    {
      return NV;
    }
    if ("pV".equals(value))
    {
      return PV;
    }
    if ("fV".equals(value))
    {
      return FV;
    }
    if ("aV".equals(value))
    {
      return AV;
    }
    if ("zV".equals(value))
    {
      return ZV;
    }
    if ("yV".equals(value))
    {
      return YV;
    }
    String s = String.format("'%s' not a supported value of '%s'",
                             value, UnitsElectricPotential.class);
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

  public static <T extends PrimitiveNumber> ElectricPotential create(T newValue, UnitsElectricPotential newUnit)
  {
    ElectricPotential theQuantity = null;
    
    try
    {
      theQuantity = UnitsElectricPotentialEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  public static <T extends Number> ElectricPotential create(T newValue, UnitsElectricPotential newUnit)
  {
    ElectricPotential theQuantity = null;
    
    try
    {
      theQuantity = UnitsElectricPotentialEnumHandler.getQuantity(newValue, newUnit);
    }
    finally
    {
      return theQuantity;
    }
  }

  private final String value;
}
