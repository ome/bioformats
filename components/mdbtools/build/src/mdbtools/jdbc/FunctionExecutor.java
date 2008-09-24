package mdbtools.jdbc;

import mdbtools.jdbc.sqlparser.Function;
import mdbtools.libmdb.Holder;
import mdbtools.libmdb.MdbTableDef;

import java.sql.SQLException;

public interface FunctionExecutor
{
  public void init(MdbTableDef table,Function function,Holder[] bound_columns)
    throws SQLException;
  public void executeRow()
    throws SQLException;
  public Object toResult();
}