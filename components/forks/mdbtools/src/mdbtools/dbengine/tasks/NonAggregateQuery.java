package mdbtools.dbengine.tasks;

import mdbtools.dbengine.Data;
import mdbtools.dbengine.MemoryData;
import mdbtools.dbengine.functions.Function;
import mdbtools.dbengine.sql.FQColumn;
import mdbtools.dbengine.sql.FunctionDef;
import mdbtools.dbengine.sql.Select;

import java.sql.SQLException;

/**
 * resolve the columns, num rows stays same
 */
public class NonAggregateQuery implements Task
{
  private MemoryData result;
  private Task task;
  private Select sql;
  private int[] tableMap;

  public NonAggregateQuery(Task task, Select sql, int[] tableMap)
  {
    this.task = task;
    this.sql = sql;
    this.tableMap = tableMap;
  }

  public void run()
    throws SQLException
  {
    result = new MemoryData();
    Data data = (Data)task.getResult();
    int numColumns = sql.getColumnCount();
    while (data.next())
    {
      Object[] row = new Object[numColumns];
      for (int i = 0; i < numColumns; i++)
      {
        row[i] = resolveColumn(data,sql.getColumn(i));
      }
      result.addRow(row);
    }
  }

  public Object getResult()
  {
    return result;
  }

  private Object resolveColumn(Data data,Object column)
    throws SQLException
  {
    if (column instanceof FQColumn)
    {
      FQColumn c = (FQColumn)column;
      return data.get(tableMap[c.getTable()]+c.getColumn());
    }
    else if (column instanceof FunctionDef)
    {
      FunctionDef fdef = (FunctionDef)column;
      Function f = (Function)fdef.getFunction();
      Object argument = fdef.getArgument();
      return f.execute(resolveColumn(data,argument));
    }
    else if (column.getClass().isArray())
    {
      // resolve each element
      Object[] oa = (Object[])column;
      Object[] result = new Object[oa.length];
      for (int i = 0; i < result.length;i++)
      {
        result[i] = resolveColumn(data,oa[i]);
      }
      return result;
    }
    else
      return column;  // static value
  }
}
