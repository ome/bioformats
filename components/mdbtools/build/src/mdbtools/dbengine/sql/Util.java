package mdbtools.dbengine.sql;

/**
 * Helper methods
 */
public class Util
{
  /**
   * easy way to call toString(Select) (if needed) on any object
   * also quotes strings and expands out arrays
   * @param sql
   * @param o
   * @return
   */
  public static String toString(Select sql,Object o)
  {
    String s;
    if (o instanceof Equation)
      s = ((Equation)o).toString(sql);
    else if (o instanceof Condition)
      s = ((Condition)o).toString(sql);
    else if (o instanceof String)
      s = "'" + (String)o + '"';
    else if (o instanceof Join)
      s = ((Join)o).toString(sql);
    else if (o instanceof FQColumn)
      s = ((FQColumn)o).toString(sql);
    else if (o instanceof FunctionDef)
      s = ((FunctionDef)o).toString(sql);
    else if (o instanceof OrderBy)
      s = ((OrderBy)o).toString(sql);
    else if (o.getClass().isArray())
    {
      Object[] oa = (Object[])o;
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < oa.length; i++)
      {
        if (i != 0)
          sb.append(',');
        sb.append(toString(sql,oa[i]));
      }
      s = sb.toString();
    }
    else
      s = o.toString();
    return s;
  }
}
