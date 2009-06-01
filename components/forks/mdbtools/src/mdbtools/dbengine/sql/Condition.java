package mdbtools.dbengine.sql;

public class Condition
{
  public static final int AND = 0;
  public static final int OR = 1;

  private static final String[] operators = new String[]
    {
      "AND",
      "OR"
    };

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
