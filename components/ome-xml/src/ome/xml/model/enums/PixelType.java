/*
 * ome.xml.model.enums.PixelType
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

public enum PixelType implements Enumeration
{
  INT8("int8"), INT16("int16"), INT32("int32"), UINT8("uint8"), UINT16("uint16"), UINT32("uint32"), FLOAT("float"), BIT("bit"), DOUBLE("double"), COMPLEX("complex"), DOUBLECOMPLEX("double-complex");
  
  private PixelType(String value)
  {
    this.value = value;
  }

  public static PixelType fromString(String value)
    throws EnumerationException
  {
    if ("int8".equals(value))
    {
      return INT8;
    }
    if ("int16".equals(value))
    {
      return INT16;
    }
    if ("int32".equals(value))
    {
      return INT32;
    }
    if ("uint8".equals(value))
    {
      return UINT8;
    }
    if ("uint16".equals(value))
    {
      return UINT16;
    }
    if ("uint32".equals(value))
    {
      return UINT32;
    }
    if ("float".equals(value))
    {
      return FLOAT;
    }
    if ("bit".equals(value))
    {
      return BIT;
    }
    if ("double".equals(value))
    {
      return DOUBLE;
    }
    if ("complex".equals(value))
    {
      return COMPLEX;
    }
    if ("double-complex".equals(value))
    {
      return DOUBLECOMPLEX;
    }
    String s = String.format("%s not a supported value of %s",
                             value, PixelType.class);
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
