/*
 * ome.xml.model.enums.Correction
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

public enum Correction implements Enumeration
{
  UV("UV"), PLANAPO("PlanApo"), PLANFLUOR("PlanFluor"), SUPERFLUOR("SuperFluor"), VIOLETCORRECTED("VioletCorrected"), ACHRO("Achro"), ACHROMAT("Achromat"), FLUOR("Fluor"), FL("Fl"), FLUAR("Fluar"), NEOFLUAR("Neofluar"), FLUOTAR("Fluotar"), APO("Apo"), PLANNEOFLUAR("PlanNeofluar"), OTHER("Other");
  
  private Correction(String value)
  {
    this.value = value;
  }

  public static Correction fromString(String value)
    throws EnumerationException
  {
    if ("UV".equals(value))
    {
      return UV;
    }
    if ("PlanApo".equals(value))
    {
      return PLANAPO;
    }
    if ("PlanFluor".equals(value))
    {
      return PLANFLUOR;
    }
    if ("SuperFluor".equals(value))
    {
      return SUPERFLUOR;
    }
    if ("VioletCorrected".equals(value))
    {
      return VIOLETCORRECTED;
    }
    if ("Achro".equals(value))
    {
      return ACHRO;
    }
    if ("Achromat".equals(value))
    {
      return ACHROMAT;
    }
    if ("Fluor".equals(value))
    {
      return FLUOR;
    }
    if ("Fl".equals(value))
    {
      return FL;
    }
    if ("Fluar".equals(value))
    {
      return FLUAR;
    }
    if ("Neofluar".equals(value))
    {
      return NEOFLUAR;
    }
    if ("Fluotar".equals(value))
    {
      return FLUOTAR;
    }
    if ("Apo".equals(value))
    {
      return APO;
    }
    if ("PlanNeofluar".equals(value))
    {
      return PLANNEOFLUAR;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("%s not a supported value of %s",
                             value, Correction.class);
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
