package mdbtools.jdbc.sqlparser;

public class Condition
{
  public static final int AND = 1;
  public static final int OR = 2;

  private Object left;
  private int operator;
  private Object right;

  public boolean equals(Object o)
  {
    if (o instanceof Condition)
    {
      Condition condition = (Condition)o;
      return left.equals(condition.left) &&
             operator == condition.operator &&
             right.equals(condition.right);
    }
    else
      return false;
  }

  public Object getLeft()
  {
    return left;
  }

  public void setLeft(Object left)
  {
    this.left = left;
  }

  public void setOperator(int operator)
  {
    this.operator = operator;
  }

  public int getOperator()
  {
    return operator;
  }

  public Object getRight()
  {
    return right;
  }

  public void setRight(Object right)
  {
    this.right = right;
  }

}
