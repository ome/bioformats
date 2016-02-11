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

public class Equation
{
  public static final int EQUALS = 0;
  public static final int LESS_THAN = 1;
  public static final int GREATER_THAN = 2;
  public static final int NOT_EQUALS = 3;
  public static final int GREATER_THAN_OR_EQUALS = 4;
  public static final int LESS_THAN_OR_EQUALS = 5;

  private static final String[] operators = new String[]
    {
      "=",
      "<",
      ">",
      "<>",
      ">=",
      "<="
    };

  Object left;
  int operator;
  Object right;

  public boolean equals(Object o)
  {
    if (o instanceof Equation)
    {
      Equation equation = (Equation)o;
      return left.equals(equation.left) &&
             operator == equation.operator &&
             right.equals(equation.right);
    }
    else
      return false;
  }

  public String toString()
  {
    return left.toString() + " " + operators[operator] + " " + right.toString();
  }

  public String toString(Select sql)
  {
    return Util.toString(sql,left) + " " + operators[operator] + " " + Util.toString(sql,right);
  }

  public Object getLeft()
  {
    return left;
  }

  public int getOperator()
  {
    return operator;
  }

  public Object getRight()
  {
    return right;
  }

  public void setLeft(Object left)
  {
    this.left = left;
  }

  public void setOperator(int operator)
  {
    this.operator = operator;
  }

  public void setRight(Object right)
  {
    this.right = right;
  }
}
