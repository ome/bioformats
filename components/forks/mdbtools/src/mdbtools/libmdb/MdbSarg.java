package mdbtools.libmdb;

public class MdbSarg implements Cloneable
{
  int op;
  MdbAny value;

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch(CloneNotSupportedException e)
    {
      // should never happen
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }
}
