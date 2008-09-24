package mdbtools.jdbc2;

import mdbtools.dbengine.Data;
import mdbtools.dbengine.Table;

import mdbtools.libmdb.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A table that grabs the data from an access .mdb file
 */
public class MDBTable implements Table
{
  private String name;
  private MdbHandle mdb;
  private ArrayList columns;

  MDBTable(String name, MdbHandle mdb,MDBSchema mdbSchema)
    throws SQLException
  {
    this.name = name;
    this.mdb = mdb;
    this.columns = mdbSchema.getColumns(name);
    if (this.columns == null)
      throw new SQLException("unknown table name: " +  name);
  }

  public String getName()
  {
    return name;
  }

  public int getColumnCount()
  {
    return columns.size();
  }

  public String getColumnName(int index)
  {
    return (String)columns.get(index);
  }

  public Data getData()
    throws SQLException
  {
    return new MDBData(mdb,name);
  }
}
