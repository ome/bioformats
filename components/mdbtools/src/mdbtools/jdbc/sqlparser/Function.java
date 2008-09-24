package mdbtools.jdbc.sqlparser;

import java.util.ArrayList;

public class Function
{
  private String name;
  ArrayList arguments = new ArrayList();

  Function(String name)
  {
    this.name = name;
  }

  public boolean equals(Object o)
  {
    if (o instanceof Function)
    {
      Function function = (Function)o;
      return name.equals(function.name) && arguments.equals(function.arguments);
    }
    else
      return false;
  }

  public String getName()
  {
    return name;
  }

  public int getArgumentCount()
  {
    return arguments.size();
  }

  public Object getArgumentAt(int index)
  {
    return arguments.get(index);
  }
}
