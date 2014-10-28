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
 * A wrapper for the Temperature class from the units implimintation.
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 * @version 1.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 5.1
 */
public class Temperature extends Quantity
{
  public Temperature(Number value, 
    Unit<ome.units.quantity.Temperature> unit)
  {
  }
    public Number value()
    {
      return 1;
    }
    
    public Unit<ome.units.quantity.Temperature> unit()
    {
      return UNITS.KELVIN;
    }
}