package mdbtools.dbengine.functions;

import java.sql.SQLException;

public class Sum implements Aggregate
{
  private int sum = -1;

  public void execute(Object column)
    throws SQLException
  {
    if (column != null)
    {
      if (column instanceof Integer == false)
        throw new RuntimeException("sum can only operate on integers");
      int value = ((Integer)column).intValue();
      if (sum == -1)
        sum = value;
      else
        sum += value;
    }
  }

  public Object getResult()
  {
    int result = sum;
    sum = -1;
    if (result == -1)
      return null;
    return new Integer(result);
  }
}
