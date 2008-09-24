package mdbtools.jdbc.sqlparser;

public class Join
{
  private Object left;
  private int type;
  private Object right;
  private Equation equation;

  public static final int INNER = 1;

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

  public Object getLeft()
  {
    return left;
  }

  public void setLeft(Object left)
  {
    this.left = left;
  }

  public void setRight(Object right)
  {
    this.right = right;
  }

  public Object getRight()
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
