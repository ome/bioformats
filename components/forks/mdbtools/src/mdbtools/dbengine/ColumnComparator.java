package mdbtools.dbengine;

/**
 * Compare the values from a column in two rows
 */
public class ColumnComparator
{
/*
  public static int compare(Object c1,Object c2)
  {
    if (c1 instanceof Comparable == false || c2 )
      throw new SQLException("argument to max must implement comparable");
    if (c1 instanceof String)
    {
      String s1 = (String)c1;
      String s2 = (String)c2;
      return s1.compareTo(s2);
    }
    else if (c1 instanceof Integer)
    {
      Integer i1 = (Integer)c1;
      Integer i2 = (Integer)c2;
      if (i1.intValue() == i2.intValue())
        return 0;
      else if (i1.intValue() < i2.intValue())
        return -1;
      else
        return 1;  // i1 > i2
    }
    else
      throw new RuntimeException("unknown column type: " + c1.getClass().getName());
  }
*/
}
