/*
 * ome.xml.model.enums.MicroscopeType
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

public enum MicroscopeType implements Enumeration
{
  UPRIGHT("Upright"), INVERTED("Inverted"), DISSECTION("Dissection"), ELECTROPHYSIOLOGY("Electrophysiology"), OTHER("Other");
  
  private MicroscopeType(String value)
  {
    this.value = value;
  }

  public static MicroscopeType fromString(String value)
    throws EnumerationException
  {
    if ("Upright".equals(value))
    {
      return UPRIGHT;
    }
    if ("Inverted".equals(value))
    {
      return INVERTED;
    }
    if ("Dissection".equals(value))
    {
      return DISSECTION;
    }
    if ("Electrophysiology".equals(value))
    {
      return ELECTROPHYSIOLOGY;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("%s not a supported value of %s",
                             value, MicroscopeType.class);
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
