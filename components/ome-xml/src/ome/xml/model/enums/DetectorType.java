/*
 * ome.xml.model.enums.DetectorType
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via xsd-fu on 2012-09-10 13:40:23-0400
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model.enums;

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
    String s = String.format("%s not a supported value of %s",
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
