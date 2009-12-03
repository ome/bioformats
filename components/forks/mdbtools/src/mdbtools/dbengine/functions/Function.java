package mdbtools.dbengine.functions;

import java.sql.SQLException;

/**
 * All functions must implement this
 */
public interface Function
{
  /** given the data for a column execute the function */
  public Object execute(Object column)
    throws SQLException;
}
