package mdbtools.jdbc2;

import mdbtools.dbengine.Data;

import java.sql.SQLException;
import java.util.ArrayList;

public class ArrayListData implements Data
{
  private ArrayList rows;
  private int index = 0;

  public ArrayListData(ArrayList rows)
  {
    this.rows = rows;
  }

  public boolean next()
    throws SQLException
  {
    if (index == rows.size())
      return false;
    index++;
    return true;
  }

  /**
   * get the data at a certain column
   * @param index the column to get
   */
  public Object get(int columnIndex)
    throws SQLException
  {
    return ((Object[])rows.get(index-1))[columnIndex];
  }
}
