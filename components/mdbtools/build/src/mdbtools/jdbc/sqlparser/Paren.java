package mdbtools.jdbc.sqlparser;

public class Paren
{
  private Object o;

  public Paren()
  {
  }

  public boolean equals(Object o)
  {
    if (o instanceof Paren)
    {
      Paren p = (Paren)o;
      return this.o.equals(p.o);
    }
    else
      return false;
  }

  public void setSubject(Object o)
  {
    this.o = o;
  }

  public Object getSubject()
  {
    return o;
  }
}
