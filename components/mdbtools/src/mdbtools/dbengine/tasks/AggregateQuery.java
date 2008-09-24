package mdbtools.dbengine.tasks;

import mdbtools.dbengine.Data;
import mdbtools.dbengine.MemoryData;
import mdbtools.dbengine.functions.Aggregate;
import mdbtools.dbengine.functions.Function;
import mdbtools.dbengine.sql.FunctionDef;
import mdbtools.dbengine.sql.FQColumn;
import mdbtools.dbengine.sql.Select;

import java.sql.SQLException;

/**
 * resolve the columns, num rows not the same
 */
public class AggregateQuery implements Task
{
  private MemoryData result;
  private Task task;
  private Select sql;
  private int[] tableMap;

  public AggregateQuery(Task task, Select sql, int[] tableMap)
  {
    this.task = task;
    this.sql = sql;
    this.tableMap = tableMap;
  }

  public void run()
    throws SQLException
  {
    result = new MemoryData();
    int numColumns = sql.getColumnCount();
    Data data = (Data)task.getResult();
    int groupByCount = sql.getGroupByCount();
    if (groupByCount != 0)
    {
      Object[] prevRowGroupBy = new Object[groupByCount];
      Object[] currentRowGroupBy = new Object[groupByCount];
      boolean first = true;
      while (data.next())
      {
        int currentGroupBy = 0;
        for (int i = 0; i < numColumns; i++)
        {
          Object column = sql.getColumn(i);
          /** @todo could be a function as well */
          if (column instanceof FQColumn)
          {
            FQColumn fq = (FQColumn)column;
            Object ob = sql.getGroupBy(currentGroupBy);
            if (fq.equals(ob))
            {
              currentRowGroupBy[currentGroupBy++] = data.get(i);
            }
          }
        }
        if (first == false && equals(prevRowGroupBy,currentRowGroupBy) == false)
        {
          currentGroupBy = 0;
          // dump the result to the returning set
          Object[] row = new Object[numColumns];
          for (int i = 0; i < numColumns; i++)
          {
            Object column = sql.getColumn(i);
            if (column instanceof FunctionDef)
              column = ((Aggregate)((FunctionDef)column).getFunction()).getResult();
            else
              column = resolveColumn(data,prevRowGroupBy[currentGroupBy]);
            row[i] = column;
          }
          result.addRow(row);
          // prev goes back to null
          for (int i = 0; i < groupByCount; i++)
            prevRowGroupBy[i] = null;
        }
        for (int i = 0; i < numColumns; i++)
          resolveAggregateFunction(data, sql.getColumn(i));
        // swap current and prev
        Object[] tmp = prevRowGroupBy;
        prevRowGroupBy = currentRowGroupBy;
        currentRowGroupBy = tmp;
        if (first)
          first = false;
      }
      // pick up the last row
      int currentGroupBy = 0;
      Object[] row = new Object[numColumns];
      for (int i = 0; i < numColumns; i++)
      {
        Object column = sql.getColumn(i);
        if (column instanceof FunctionDef)
          column = ((Aggregate)((FunctionDef)column).getFunction()).getResult();
        else
          column = resolveColumn(data,prevRowGroupBy[currentGroupBy]);
        row[i] = column;
      }
      result.addRow(row);
    }
    else
    {
      while (data.next())
        for (int i = 0; i < numColumns; i++)
          resolveAggregateFunction(data,sql.getColumn(i));
      Object[] row = new Object[numColumns];
      for (int i = 0; i < numColumns; i++)
      {
        Object column = sql.getColumn(i);
        if (column instanceof FunctionDef)
          column = ((Aggregate)((FunctionDef)column).getFunction()).getResult();
        row[i] = column;
      }
      result.addRow(row);
    }
  }

  public Object getResult()
  {
    return result;
  }

  private boolean equals(Object[] a, Object[] b)
    throws SQLException
  {
    if (a.length != b.length)
      throw new SQLException("a.length != b.length");
    for (int i = 0; i < a.length; i++)
    {
      if (a[i].equals(b[i]) == false)
        return false;
    }
    return true;
  }

  private void resolveAggregateFunction(Data data,Object column)
    throws SQLException
  {
    if (column instanceof FunctionDef)
    {
      FunctionDef fdef = (FunctionDef)column;
      Aggregate f = (Aggregate)fdef.getFunction();
      Object argument = fdef.getArgument();
      f.execute(resolveColumn(data,argument));
    }
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
    else
      return column;  // static value
  }
}
