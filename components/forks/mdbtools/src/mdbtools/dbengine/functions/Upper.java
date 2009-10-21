package mdbtools.dbengine.functions;

public class Upper implements Function
{
  /** convert column to uppercase */
  public Object execute(Object column)
  {
    if (column == null)
      return null;
    if ((column instanceof String) == false)
      throw new RuntimeException("upper can only operate on strings");
    return ((String)column).toUpperCase();
  }
}
