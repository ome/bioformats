package mdbtools.dbengine.tasks;

import mdbtools.dbengine.MemoryData;
import mdbtools.dbengine.sql.FQColumn;
import mdbtools.dbengine.sql.OrderBy;

import java.sql.SQLException;

/**
 * Sort the given data by the given columns
 */
public class SimpleSort implements Task
{
  private MemoryData result;
  private Task task;
  private OrderBy[] sortBy;
  private int[] tableMap;

  public SimpleSort(Task task, OrderBy[] sortBy, int[] tableMap)
    throws SQLException
  {
    this.task = task;
    this.sortBy = sortBy;
    this.tableMap = tableMap;
  }

  public void run()
    throws SQLException
  {
    MemoryData data = (MemoryData)task.getResult();
//((MemoryData)data).dump();

    int[] sb = new int[sortBy.length];
    boolean[] ascending = new boolean[sb.length];
    for (int i = 0; i < sortBy.length; i++)
    {
      sb[i] = getColumnToSort(sortBy[i].getSort());
      ascending[i] = sortBy[i].isAscending();
    }

    ((MemoryData)data).sort(sb,ascending);
//((MemoryData)data).dump();
    result = data;
  }

  public Object getResult()
  {
    return result;
  }

  private int getColumnToSort(Object column)
    throws SQLException
  {
    if (column instanceof Integer)
      return ((Integer)column).intValue() - 1;
    else if (column instanceof FQColumn)
    {
      FQColumn c = (FQColumn)column;
      return tableMap[c.getTable()]+c.getColumn();
    }
    else
      throw new SQLException("I don't know how to sort this");
  }
}
