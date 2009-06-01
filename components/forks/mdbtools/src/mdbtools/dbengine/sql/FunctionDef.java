package mdbtools.dbengine.sql;

import mdbtools.dbengine.functions.Aggregate;
import mdbtools.dbengine.functions.Function;

/**
 * A defination of a function,  used in the parse tree
 */
public class FunctionDef
{
  private Object function;
  private Object argument;

  public Object getArgument()
  {
    return argument;
  }

  public void setArgument(Object argument)
  {
    this.argument = argument;
  }

  public Object getFunction()
  {
    return function;
  }

  public void setFunction(Function function)
  {
    this.function = function;
  }

  public void setFunction(Aggregate function)
  {
    this.function = function;
  }

  public String toString()
  {
    return function.getClass().getName() + '(' + argument.toString() + ')';
  }

  public String toString(Select sql)
  {
    return function.getClass().getName() + '(' + Util.toString(sql,argument) + ')';
  }
}

