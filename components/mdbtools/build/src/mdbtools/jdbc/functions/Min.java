package mdbtools.jdbc.functions;

import mdbtools.jdbc.FunctionExecutor;
import mdbtools.jdbc.sqlparser.Function;
import mdbtools.libmdb.Constants;
import mdbtools.libmdb.Holder;
import mdbtools.libmdb.MdbColumn;
import mdbtools.libmdb.MdbTableDef;

import java.sql.SQLException;

public class Min implements FunctionExecutor
{
  // min is for either an int or a string
  private MdbTableDef table;
  private Holder[] bound_columns;
  private int columnNum;
  private boolean isInteger = true;
  private int i = 0;
  private String s = null;
  private boolean firstRow = true;

  public void init(MdbTableDef table,Function function,Holder[] bound_columns)
    throws SQLException
  {
    if (function.getArgumentCount() != 1)
      throw new SQLException("min only works on a single column");
    this.bound_columns = bound_columns;
    String columnName = (String)function.getArgumentAt(0);
    this.table = table;
    // find the column to min on
    columnNum = -1;
    for (int i = 0;i < table.num_cols;i++)
    {
      MdbColumn col = (MdbColumn)table.columns.get(i);
      if (columnName.equalsIgnoreCase(col.name))
      {
        columnNum = i;
        if (col.col_type == Constants.MDB_INT)
          isInteger = true;
        else if (col.col_type == Constants.MDB_TEXT)
          isInteger = false;
        else
          throw new SQLException("min only works on integers and strings");

        break;
      }
    }
    if (columnNum == -1)
      throw new SQLException("column not found");
  }

  public void executeRow()
    throws SQLException
  {
    if (isInteger)
    {
      int current = Integer.parseInt(bound_columns[columnNum].s);
      if (firstRow)
      {
        i = current;
        firstRow = false;
      }
      else if (current < i)
        i = current;
    }
    else
      throw new SQLException("only min(integer) is implemented");
  }

  public Object toResult()
  {
    if (isInteger)
      return new Integer(i);
    else
      return s;
  }
}
