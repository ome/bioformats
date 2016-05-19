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

import mdbtools.dbengine.Table;

public class Join
{
  private Object left;  // either a table or a join
  private int type;
  private Table right; // a table
  private Equation equation;

  public static final int INNER = 0;

  private static final String[] types = new String[]
    {
      "INNER JOIN"
    };

  public Join()
  {
  }

  public boolean equals(Object o)
  {
    if (o instanceof Join)
    {
      Join join = (Join)o;
      return left.equals(join.left) &&
             type == join.type &&
             right.equals(join.right) &&
             equation.equals(join.equation);
    }
    else
      return false;
  }

  public String toString()
  {
    return left.toString() + " " + types[type] + " " + right.toString() +
      " ON " + equation.toString();
  }

  public String toString(Select sql)
  {
    return left.toString() + " " + types[type] + " " + right.toString() +
      " ON " + equation.toString(sql);
  }

  public Object getLeft()
  {
    return left;
  }

  public void setLeft(Object left)
  {
    this.left = left;
  }

  public void setRight(Table right)
  {
    this.right = right;
  }

  public Table getRight()
  {
    return right;
  }

  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

  public Equation getEquation()
  {
    return equation;
  }

  public void setEquation(Equation equation)
  {
    this.equation = equation;
  }
}
