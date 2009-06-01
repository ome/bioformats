package mdbtools.dbengine.functions;

import java.sql.SQLException;

public class Min implements Aggregate
{
  private Object min = null;

  public void execute(Object column)
    throws SQLException
  {
    if (column != null)
    {
      if (min == null)
        min = (Comparable)column;
      else
      {
        if (((Comparable)column).compareTo(min) < 0)
          min = column;
      }
    }
  }

  public Object getResult()
  {
    Object result = min;
    min = null;
    return result;
  }
}
