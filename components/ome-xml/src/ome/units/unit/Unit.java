/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2014 - 2016 Open Microscopy Environment:
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

package ome.units.unit;

import ome.units.quantity.Quantity;
import java.math.BigInteger;

/**
 * Unit of measurement.
 *
 * The representation of a unit of measurement.  This generic type is
 * specialised by the dimension type for the unit; for example Length.
 *
 * A unit of measurement belongs to a measurement system with a
 * corresponding symbol.  Additionally, a scaling factor and offset
 * provide the means to scale the unit to the base unit for the
 * measuring system, and hence perform unit conversions.
 *
 * There is always one measurement system per dimension type, but
 * additional systems may be added, and these may or may not be
 * interconvertible.  For example, an abstract length representation
 * such as pixel size would not be convertible to the SI length units
 * without an explicit conversion with a conversion factor.
 *
 * @since 5.1.0
 */
public class Unit<Q extends Quantity>
{
  /** The measurement system for the dimension type */
  private final String measurementSystem;
  /** The symbol for this unit. */
  private final String symbol;
  /** The conversion scaling factor for this unit. */
  private final Double scaleFactor;
  /** The conversion offset for this unit. */
  private final Double offset;

  /**
   * Create a unit.
   *
   * @param inSystem the measurement system.
   * @param inSymbol the unit symbol.
   * @param inScaleFactor the conversion scaling factor (1.0 for base unit).
   * @param inOffset the conversion offset (0.0 for base unit).
   */
  private Unit(String inSystem, String inSymbol, Double inScaleFactor, Double inOffset)
  {
    measurementSystem = inSystem;
    symbol = inSymbol;
    scaleFactor = inScaleFactor;
    offset = inOffset;
  }

  /**
   * Get the symbol for this unit.
   *
   * @return the symbol.
   */
  public String getSymbol()
  {
    return symbol;
  }

  /**
   * Get the conversion scaling factor for this unit.
   *
   * @return the scaling factor.
   */
  public Double getScaleFactor()
  {
    return scaleFactor;
  }

  /**
   * Get the conversion offset for this unit.
   *
   * @return the offset.
   */
  public Double getOffset()
  {
    return offset;
  }

  /**
   * Check if this unit is convertible to another unit.
   *
   * @param inUnit the unit to which to convert.
   * @return true if convertible or false if not convertible.
   */
  public Boolean isConvertible(Unit<Q> inUnit)
  {
    return (measurementSystem.equals(inUnit.measurementSystem));
  }

  /**
   * Convert a value in this unit to the specified unit.
   *
   * @param inValue the value to convert.
   * @param inUnit the unit to which to convert.
   * @return the converted value
   * @throws ArithmeticException if the conversion is not possible.
   */
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
  /**
   * Multiply the scaling conversion factor in this unit by a scalar.
   *
   * @param scalar the multiplication factor.
   * @return a new Unit containing the result of the multiplication.
   */
  public Unit<Q> multiply(Integer scalar)
  {
    Double newScaleFactor = scaleFactor * scalar;
    Double newOffset = offset * scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  /**
   * Multiply the scaling conversion factor in this unit by a scalar.
   *
   * @param scalar the multiplication factor.
   * @return a new Unit with the result of the multiplication.
   */
  public Unit<Q> multiply(Double scalar)
  {
    Double newScaleFactor = scaleFactor * scalar;
    Double newOffset = offset * scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  /**
   * Divide the scaling conversion factor in this unit by a scalar.
   *
   * @param scalar the division factor.
   * @return a new Unit with the result of the division.
   */
  public Unit<Q> divide(Integer scalar)
  {
    Double newScaleFactor = scaleFactor / scalar;
    Double newOffset = offset / scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  /**
   * Divide the scaling conversion factor in this unit by a scalar.
   *
   * @param scalar the division factor.
   * @return a new Unit with the result of the division.
   */
  public Unit<Q> divide(Double scalar)
  {
    Double newScaleFactor = scaleFactor / scalar;
    Double newOffset = offset / scalar;
    return new Unit<Q>(measurementSystem, symbol, newScaleFactor, newOffset);
  }
  /**
   * Increase the scaling offset in this unit by a scalar.
   *
   * @param scalar the value to add.
   * @return a new Unit with the result of the addition.
   */
  public Unit<Q> add(Integer scalar)
  {
    Double newOffset = offset + scalar;
    return new Unit<Q>(measurementSystem, symbol, scaleFactor, newOffset);
  }
  /**
   * Increase the scaling offset in this unit by a scalar.
   *
   * @param scalar the value to add.
   * @return a new Unit with the result of the addition.
   */
  public Unit<Q> add(Double scalar)
  {
    Double newOffset = offset + scalar;
    return new Unit<Q>(measurementSystem, symbol, scaleFactor, newOffset);
  }
  /**
   * Change the unit symbol in this unit.
   *
   * @param inSymbol the new unit symbol.
   * @return a new Unit with the result of the unit symbol change.
   */
  public Unit<Q> setSymbol(String inSymbol)
  {
    return new Unit<Q>(measurementSystem, inSymbol, scaleFactor, offset);
  }

  /**
   * Add a prefix to the unit symbol in this unit.
   *
   * @param prefix the unit symbol prefix.
   * @return a new Unit with the result of the unit symbol prefix addition.
   */
  public Unit<Q> prefixSymbol(String prefix)
  {
    String newSymbol = prefix + symbol;
    return new Unit<Q>(measurementSystem, newSymbol, scaleFactor, offset);
  }

  /**
   * Create the "base unit" for a given measurement system.
   *
   * This is the default or reference unit representing unity for the
   * measurement system.  For example, for a unit system representing
   * length, the measurement system and symbol could be "SI.METRE" and
   * "m", respectively.
   *
   * @param inMeasurementSystem the name of the measurement system.
   * @param inSymbol the symbol of the base unit.
   */
  public static <Q extends Quantity> Unit<Q> CreateBaseUnit(String inMeasurementSystem, String inSymbol)
  {
    return new Unit<Q>(inMeasurementSystem, inSymbol, 1.0, 0.0);
  }
  // End "protected" functions

}
