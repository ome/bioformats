/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2014 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
/*
 * $Id$
 *
 *   Copyright 2014 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.units.quantity;

import ome.units.quantity.Quantity;
import ome.units.unit.Unit;
import ome.units.UNITS;

/**
 * A wrapper for the Pressure class from the units implementation.
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 * @version 1.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 5.1
 */
public class Pressure extends Quantity implements Comparable<Pressure>
{
  private static final int SEED1 = 67;
  private static final int SEED2 = 78;
  Number value;
  Unit<ome.units.quantity.Pressure> unit;
  private int hashCodeValue;

  public Pressure(Number inValue,
    Unit<ome.units.quantity.Pressure> inUnit)
  {
    if (inValue == null)
    {
      throw new NullPointerException("Pressure: Pressure cannot be constructed with a null value.");
    }
    value = inValue;
    unit = inUnit;
    hashCodeValue = SEED1;
    hashCodeValue = SEED2 * hashCodeValue + Float.floatToIntBits(value.floatValue());
    hashCodeValue = SEED2 * hashCodeValue + unit.getSymbol().hashCode();
  }

  public Number value()
  {
    return value;
  }
  
  public Number value(Unit<ome.units.quantity.Pressure> inUnit)
  {
    if (unit.equals(inUnit))
    {
      return value;
    }
    if (unit.isConvertible(inUnit))
    {
      return unit.convertValue(value, inUnit);
    }
    return null;
  }

  public boolean equals(Object other)
  {
    if (other == null)
    {
      return false;
    }
    if (this.getClass() != other.getClass())
    {
      return false;
    }
    Pressure otherPressure = (Pressure)other;
    if (unit.equals(otherPressure.unit))
    {
      // Pressures use same unit so compare value
      return value.equals(otherPressure.value);
    } else {
      if (unit.isConvertible(otherPressure.unit))
      {
        // Pressures use different compatible units so convert value then compare
        return (unit.convertValue(value, otherPressure.unit)).equals(otherPressure.value);
      }
    }
    return false;
  }

  @Override
  public int compareTo(Pressure other)
  {
    if (this == other) {
      return 0;
    }
    return Double.compare(value.doubleValue(), other.value(unit).doubleValue());
  }

  @Override
  public int hashCode()
  {
    return hashCodeValue;
  }
  @Override
  public String toString()
  {
    StringBuilder result = new StringBuilder();
    result.append(this.getClass().getName());
    result.append(": ");
    result.append("value[");
    result.append(value);
    result.append("], unit[");
    result.append(unit.getSymbol());
    result.append("] stored as ");
    result.append(value.getClass().getName());
    return result.toString();
  }

  public Unit<ome.units.quantity.Pressure> unit()
  {
    return unit;
  }
}
