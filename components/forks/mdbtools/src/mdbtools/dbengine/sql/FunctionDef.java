/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package mdbtools.dbengine.sql;

import mdbtools.dbengine.functions.Aggregate;
import mdbtools.dbengine.functions.Function;

/**
 * A defination of a function,  used in the parse tree
 */
public class FunctionDef
{
  private Object function;
  private Object argument;

  public Object getArgument()
  {
    return argument;
  }

  public void setArgument(Object argument)
  {
    this.argument = argument;
  }

  public Object getFunction()
  {
    return function;
  }

  public void setFunction(Function function)
  {
    this.function = function;
  }

  public void setFunction(Aggregate function)
  {
    this.function = function;
  }

  public String toString()
  {
    return function.getClass().getName() + '(' + argument.toString() + ')';
  }

  public String toString(Select sql)
  {
    return function.getClass().getName() + '(' + Util.toString(sql,argument) + ')';
  }
}

