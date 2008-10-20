package mdbtools.dbengine.functions;

import java.sql.SQLException;

public class Max implements Aggregate
{
  private Object max = null;

  public void execute(Object column)
    throws SQLException
  {
    if (column != null)
    {
      if (max == null)
        max = (Comparable)column;
      else
      {
        if (((Comparable)column).compareTo(max) > 0)
          max = column;
      }
    }
  }

  public Object getResult()
  {
    Object result = max;
    max = null;
    return result;
  }
}
