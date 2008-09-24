package mdbtools.jdbc.functions;

import mdbtools.jdbc.sqlparser.Function;
import mdbtools.jdbc.FunctionExecutor;
import mdbtools.libmdb.Holder;
import mdbtools.libmdb.MdbTableDef;

import java.sql.SQLException;

public class Count implements FunctionExecutor
{
  int count;

  public void init(MdbTableDef table,Function function,Holder[] bound_columns)
    throws SQLException
  {
    if (function.getArgumentCount() != 1 ||
        ((String)function.getArgumentAt(0)) .equals("*") == false)
      throw new SQLException("only count(*) is supported");
  }

  public void executeRow()
  {
    count++;
  }

  public Object toResult()
  {
    return new Integer(count);
  }
}
