/*
 * ome.xml.model.enums.Pulse
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

public enum Pulse implements Enumeration
{
  CW("CW"), SINGLE("Single"), QSWITCHED("QSwitched"), REPETITIVE("Repetitive"), MODELOCKED("ModeLocked"), OTHER("Other");
  
  private Pulse(String value)
  {
    this.value = value;
  }

  public static Pulse fromString(String value)
    throws EnumerationException
  {
    if ("CW".equals(value))
    {
      return CW;
    }
    if ("Single".equals(value))
    {
      return SINGLE;
    }
    if ("QSwitched".equals(value))
    {
      return QSWITCHED;
    }
    if ("Repetitive".equals(value))
    {
      return REPETITIVE;
    }
    if ("ModeLocked".equals(value))
    {
      return MODELOCKED;
    }
    if ("Other".equals(value))
    {
      return OTHER;
    }
    String s = String.format("%s not a supported value of %s",
                             value, Pulse.class);
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
