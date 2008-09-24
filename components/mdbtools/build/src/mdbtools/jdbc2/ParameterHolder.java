package mdbtools.jdbc2;

public class ParameterHolder
{
  private Object value;
  private boolean valueSet = false;

  public void setValue(Object value)
  {
    valueSet = true;
    this.value = value;
  }

  public Object getValue()
  {
    return value;
  }

  public boolean isSet()
  {
    return valueSet;
  }
}


