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
