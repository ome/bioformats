package mdbtools.dbengine;

import java.util.ArrayList;

import java.sql.SQLException;

/**
 * Allow the data to be restarted
 */
public class RewindableData extends MemoryData
{
  RewindableData(Data data,int numColumns)
    throws SQLException
  {
    super(data,numColumns);
  }

  public void rewind()
  {
    currentRow = -1;
  }
}
