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

  private final String basename;
  private String symbol;
  private Unit(String uniqueName, String uniqueSymbol)
  {
    basename = uniqueName;
    symbol = uniqueSymbol;
  }

  public Unit<Q> multiply(Integer scalar)
  {
    return this;
  }
  public Unit<Q> multiply(Double scalar)
  {
    return this;
  }
  public Unit<Q> divide(Integer scalar)
  {
    return this;
  }
  public Unit<Q> divide(Double scalar)
  {
    return this;
  }
  public Unit<Q> add(Integer scalar)
  {
    return this;
  }
  public Unit<Q> add(Double scalar)
  {
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

  public String getSymbol()
  {
    return symbol;
  }

  public static <Q extends Quantity> Unit<Q> CreateBaseUnit(String uniqueName, String baseSymbol)
  {
    return new Unit<Q>(uniqueName, baseSymbol);
  }

}