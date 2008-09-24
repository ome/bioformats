package mdbtools.jdbc.sqlparser;

public class Equation
{
  public static final int EQUALS = 1;
  public static final int LESS_THAN = 2;
  public static final int GREATER_THAN = 3;
  public static final int NOT_EQUALS = 4;
  public static final int GREATER_THAN_OR_EQUALS = 5;
  public static final int LESS_THAN_OR_EQUALS = 6;

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
