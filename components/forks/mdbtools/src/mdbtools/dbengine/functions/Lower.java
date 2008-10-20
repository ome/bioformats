package mdbtools.dbengine.functions;

public class Lower implements Function
{
  /** convert column to lowercase */
  public Object execute(Object column)
  {
    if (column == null)
      return null;
    if ((column instanceof String) == false)
      throw new RuntimeException("lower can only operate on strings");
    return ((String)column).toLowerCase();
  }
}
