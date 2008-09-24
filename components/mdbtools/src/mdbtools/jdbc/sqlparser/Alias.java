package mdbtools.jdbc.sqlparser;

public class Alias
{
  private Object subject;  // what is being aliased
  private String alias; // the name of the alias

  public Alias()
  {
  }

  public boolean equals(Object o)
  {
    if (o instanceof Alias)
    {
      Alias alias = (Alias)o;
      return subject.equals(alias.subject) && this.alias.equals(alias.alias);
    }
    else
      return false;
  }

  public String toString()
  {
    return subject.toString() + " " + alias;
  }

  public String getAlias()
  {
    return alias;
  }

  void setAlias(String alias)
  {
    this.alias = alias;
  }

  public Object getSubject()
  {
    return subject;
  }

  void setSubject(Object name)
  {
    this.subject = name;
  }
}
