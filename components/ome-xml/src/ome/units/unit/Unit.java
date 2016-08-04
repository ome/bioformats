/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2014 - 2015 Open Microscopy Environment:
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
  private final String symbol;
  private final Double scaleFactor;
  private final Double offset;

  private Unit(String inSystem, String inSymbol, Double inScaleFactor, Double inOffset)
  {
    measurementSystem = inSystem;
    symbol = inSymbol;
    scaleFactor = inScaleFactor;
    offset = inOffset;
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
    Double newScaleFactor = scaleFactor * scalar;
    Double newOffset = offset * scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  public Unit<Q> multiply(Double scalar)
  {
    Double newScaleFactor = scaleFactor * scalar;
    Double newOffset = offset * scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  public Unit<Q> divide(Integer scalar)
  {
    Double newScaleFactor = scaleFactor / scalar;
    Double newOffset = offset / scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  public Unit<Q> divide(Double scalar)
  {
    Double newScaleFactor = scaleFactor / scalar;
    Double newOffset = offset / scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  public Unit<Q> add(Integer scalar)
  {
    Double newOffset = offset + scalar;
    return new Unit<Q>(measurementSystem, symbol, scaleFactor, newOffset);
  }
  public Unit<Q> add(Double scalar)
  {
    Double newOffset = offset + scalar;
    return new Unit<Q>(measurementSystem, symbol, scaleFactor, newOffset);
  }
  public Unit<Q> setSymbol(String inSymbol)
  {
    return new Unit<Q>(measurementSystem, inSymbol, scaleFactor, offset);
  }

  public Unit<Q> prefixSymbol(String prefix)
  {
    String newSymbol = prefix + symbol;
    return new Unit<Q>(measurementSystem, newSymbol, scaleFactor, offset);
  }

  public static <Q extends Quantity> Unit<Q> CreateBaseUnit(String inMeasurementSystem, String inSymbol)
  {
    return new Unit<Q>(inMeasurementSystem, inSymbol, 1.0, 0.0);
  }
  // End "protected" functions

}
