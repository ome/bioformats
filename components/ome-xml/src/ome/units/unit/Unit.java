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

package ome.units.unit;

import ome.units.quantity.Quantity;
import java.math.BigInteger;

/**
 * The for the Unit class.
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 * @version 1.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 5.1
 */
public class Unit<Q extends Quantity>
{
  private final String measurementSystem;
  private String symbol;
  private Double scaleFactor;
  private Double offset;

  private Unit(String uniqueSystemName, String uniqueSymbol)
  {
    measurementSystem = uniqueSystemName;
    symbol = uniqueSymbol;
    scaleFactor = 1.0;
    offset = 0.0;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public Double getScaleFactor()
  {
    return scaleFactor;
  }

  public Double getOffset()
  {
    return offset;
  }

  public Boolean isConvertible(Unit<Q> inUnit)
  {
    return (measurementSystem.equals(inUnit.measurementSystem));
  }

  public Double convertValue(Number inValue, Unit<Q> inUnit)
  {
    if (!isConvertible(inUnit))
    {
      throw new ArithmeticException(
        "Incompatible units are not convertible [" +
        measurementSystem +
        "]->[" +
        inUnit.measurementSystem +
        "]");
    }
    Double theResult = (((inValue.doubleValue()*scaleFactor)+offset)-inUnit.offset)/inUnit.scaleFactor;
    return theResult;
  }

  // Begin "protected" functions
  // These functions should only ever need called from
  // within the ome.units.UNITS class. I would have made them
  // protected and ome.units.UNITS a friend if that were possible
  // in java.
  public Unit<Q> multiply(Integer scalar)
  {
    scaleFactor = scaleFactor * scalar;
    offset = offset * scalar;
    return this;
  }
  public Unit<Q> multiply(Double scalar)
  {
    scaleFactor = scaleFactor * scalar;
    offset = offset * scalar;
    return this;
  }
  public Unit<Q> divide(Integer scalar)
  {
    scaleFactor = scaleFactor / scalar;
    offset = offset / scalar;
    return this;
  }
  public Unit<Q> divide(Double scalar)
  {
    scaleFactor = scaleFactor / scalar;
    offset = offset / scalar;
    return this;
  }
  public Unit<Q> add(Integer scalar)
  {
    offset = offset + scalar;
    return this;
  }
  public Unit<Q> add(Double scalar)
  {
    offset = offset + scalar;
    return this;
  }
  public Unit<Q> setSymbol(String abbreviation)
  {
    symbol = abbreviation;
    return this;
  }

  public Unit<Q> prefixSymbol(String prefix)
  {
    symbol = prefix + symbol;
    return this;
  }

  public static <Q extends Quantity> Unit<Q> CreateBaseUnit(String uniqueName, String baseSymbol)
  {
    return new Unit<Q>(uniqueName, baseSymbol);
  }
  // End "protected" functions

}