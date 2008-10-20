package mdbtools.dbengine.functions;

/**
 * Length is the sql length function
 * When passed a string it returns the length of the string
 */
public class Length implements Function
{
  /** execute the length function of a string */
  public Object execute(Object column)
  {
    if (column == null)
      return null;
    if ((column instanceof String) == false)
      throw new RuntimeException("length can only operate on strings");
    return new Integer(((String)column).length());
  }
}
