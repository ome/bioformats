package mdbtools.dbengine.functions;

public class Count implements Aggregate
{
  private int numRows;

  public void execute(Object column)
  {
    if (column != null)
      numRows++;
  }

  public Object getResult()
  {
    Object result = new Integer(numRows);
    numRows = 0;
    return result;
  }
}
