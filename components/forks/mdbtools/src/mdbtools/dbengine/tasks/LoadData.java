package mdbtools.dbengine.tasks;

import mdbtools.dbengine.MemoryData;
import mdbtools.dbengine.Table;

import java.sql.SQLException;

/**
 * Load any data from a table into data
 */
public class LoadData implements Task
{
  private Table table;
  private MemoryData result;

  public LoadData(Table table)
  {
    this.table = table;
  }

  public void run()
    throws SQLException
  {
    result = new MemoryData(table.getData(),table.getColumnCount());
  }

  public Object getResult()
  {
    return result;
  }
}
