package mdbtools.dbengine;

import java.sql.SQLException;

/**
 * Data represents a set of rows containing data
 */
public interface Data
{
  /**
   * goto the next (or first) row
   * @return true if row exits, false if not
   */
  public boolean next()
    throws SQLException;

  /**
   * get the data at a certain column
   * @param index the column to get
   */
  public Object get(int index)
    throws SQLException;
}
