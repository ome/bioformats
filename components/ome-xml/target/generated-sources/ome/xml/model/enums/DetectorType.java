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

import ome.xml.model.enums.handlers.DetectorTypeEnumHandler;
import ome.xml.model.primitives.*;

public enum DetectorType implements Enumeration
{
  CCD("CCD"), INTENSIFIEDCCD("IntensifiedCCD"), ANALOGVIDEO("AnalogVideo"), PMT("PMT"), PHOTODIODE("Photodiode"), SPECTROSCOPY("Spectroscopy"), LIFETIMEIMAGING("LifetimeImaging"), CORRELATIONSPECTROSCOPY("CorrelationSpectroscopy"), FTIR("FTIR"), EMCCD("EMCCD"), APD("APD"), CMOS("CMOS"), EBCCD("EBCCD"), OTHER("Other");

  private DetectorType(String value)
  {
    this.value = value;
  }

  public static DetectorType fromString(String value)
    throws EnumerationException
  {
    if ("CCD".equals(value))
    {
      return CCD;
    }
    if ("IntensifiedCCD".equals(value))
    {
      return INTENSIFIEDCCD;
    }
    if ("AnalogVideo".equals(value))
    {
      return ANALOGVIDEO;
    }
    if ("PMT".equals(value))
    {
      return PMT;
    }
    if ("Photodiode".equals(value))
    {
      return PHOTODIODE;
    }
    if ("Spectroscopy".equals(value))
    {
      return SPECTROSCOPY;
    }
    if ("LifetimeImaging".equals(value))
    {
      return LIFETIMEIMAGING;
    }
    if ("CorrelationSpectroscopy".equals(value))
    {
      return CORRELATIONSPECTROSCOPY;
    }
    if ("FTIR".equals(value))
    {
      return FTIR;
    }
    if ("EMCCD".equals(value))
    {
      return EMCCD;
    }
    if ("APD".equals(value))
    {
      return APD;
    }
    if ("CMOS".equals(value))
    {
      return CMOS;
    }
    if ("EBCCD".equals(value))
    {
      return EBCCD;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("'%s' not a supported value of '%s'",
                             value, DetectorType.class);
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

  private final String value;
}
