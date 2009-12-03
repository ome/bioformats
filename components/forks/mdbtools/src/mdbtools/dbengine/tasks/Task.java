package mdbtools.dbengine.tasks;

import java.sql.SQLException;

/**
 * Task is one step on the way to producing a result
 */
public interface Task
{
  public void run()
    throws SQLException;

  public Object getResult();
}
