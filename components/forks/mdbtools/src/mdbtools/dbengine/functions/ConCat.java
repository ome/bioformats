package mdbtools.dbengine.functions;

public class ConCat implements Function
{
  /** take two strings stick them together and return the result */
  public Object execute(Object column)
  {
    Object[] strings = (Object[])column;
    return (String)strings[0] + (String)strings[1];
  }
}
