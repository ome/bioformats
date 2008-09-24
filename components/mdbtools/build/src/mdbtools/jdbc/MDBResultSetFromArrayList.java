package mdbtools.jdbc;

import java.sql.*;
import java.util.ArrayList;

public class MDBResultSetFromArrayList extends MDBResultSet
{
  private ArrayList rows;
  private String[] names;
  private int current = -1;

  MDBResultSetFromArrayList(String[] names,ArrayList rows)
  {
    this.rows = rows;
    this.names = names;
  }

  public boolean next()
    throws SQLException
  {
    current++;
//System.out.println("row: " + current);
    if (current < rows.size())
      return true;
    return false;
  }

  public int findColumn(String columnName)
    throws SQLException
  {
    for (int i = 0; i < names.length; i++)
    {
      if (names[i].equalsIgnoreCase(columnName))
        return i+1;
    }
    throw new SQLException("unknown column name: " + columnName);
  }

  public String getString(String columnName)
    throws SQLException
  {
    return getString(findColumn(columnName));
  }

  public String getString(int columnIndex)
      throws SQLException
  {
//System.out.print("column: " + columnIndex);
    if (columnIndex > names.length)
      throw new RuntimeException("invalid column index");
    Object o = ((Object[])rows.get(current))[columnIndex-1];
    String result;
    if (o == null)
      return null;
    else if (o instanceof String)
      result = (String)o;
    else  // catch all
      result = o.toString();
//System.out.println(" value: " + s );
    return result;
  }

  public int getInt(String columnName)
    throws SQLException
  {
    return getInt(findColumn(columnName));
  }

  public int getInt(int columnIndex)
    throws SQLException
  {
    Integer o = (Integer)((Object[])rows.get(current))[columnIndex-1];
    return o.intValue();
//    System.out.println("getInt: " + columnIndex +
//                       " name: " + names[columnIndex-1] +
//                       " type: " + o.getClass().getName() +
//                       " value: " + o.toString());
//    System.exit(1);
//    return 0;
  }

  public void close()
    throws SQLException
  {
    /** @todo don't allow the result set to be used after a close */
  }

  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    return new MDBResultSetMetaData(names);
  }

  public boolean wasNull()
    throws SQLException
  {
    // nothing is ever null
    return false;
  }
}

