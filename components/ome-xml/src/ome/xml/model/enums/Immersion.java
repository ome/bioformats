/*
 * ome.xml.model.enums.Immersion
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

public enum Immersion implements Enumeration
{
  OIL("Oil"), WATER("Water"), WATERDIPPING("WaterDipping"), AIR("Air"), MULTI("Multi"), GLYCEROL("Glycerol"), OTHER("Other");
  
  private Immersion(String value)
  {
    this.value = value;
  }

  public static Immersion fromString(String value)
    throws EnumerationException
  {
    if ("Oil".equals(value))
    {
      return OIL;
    }
    if ("Water".equals(value))
    {
      return WATER;
    }
    if ("WaterDipping".equals(value))
    {
      return WATERDIPPING;
    }
    if ("Air".equals(value))
    {
      return AIR;
    }
    if ("Multi".equals(value))
    {
      return MULTI;
    }
    if ("Glycerol".equals(value))
    {
      return GLYCEROL;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("%s not a supported value of %s",
                             value, Immersion.class);
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
