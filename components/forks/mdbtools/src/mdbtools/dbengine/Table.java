package mdbtools.dbengine;

import java.sql.SQLException;

public interface Table
{
  public String getName();
  public int getColumnCount();
  public String getColumnName(int index);

  public Data getData()
    throws SQLException;
}

