package mdbtools.jdbc;

import mdbtools.libmdb.*;

import java.io.IOException;
import java.sql.*;

/**
 * Driver is the entry point to access a ms access database from jdbc.
 * The name of this class is the only guarentee given for the entire codebase
 */
public class Driver // implements java.sql.Driver
{
  private Driver()
  {
  }

  static
  {
    try
    {
      Class.forName(mdbtools.jdbc2.Driver.class.getName());
    }
    catch(ClassNotFoundException e)
    {
      throw new RuntimeException(e);
    }

    /*
    try
    {
      mem.mdb_init();
      DriverManager.registerDriver(new Driver());
    }
    catch(SQLException e)
    {
      throw new RuntimeException(e.getMessage());
    }
    */
  }
/*
  public Connection connect(String url, java.util.Properties info)
    throws SQLException
  {
    if(url.startsWith("jdbc:mdbtools:") == false)
      return null;  // not the right driver

    try
    {
      String filename = url.substring(14);
      return new MDBConnection(filename);
    }
    catch(IOException e)
    {
      throw new SQLException(e.getClass() + ": " + e.getMessage());
    }
  }

  public boolean acceptsURL(String url)
      throws SQLException
  {
    return url.startsWith("jdbc:mdbtools:");
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info)
    throws SQLException
  {
    throw new RuntimeException("Driver.getPropertyInfo not implemented");
  }

  public int getMajorVersion()
  {
    return 0;
  }

  public int getMinorVersion()
  {
    return 1;
  }

  public boolean jdbcCompliant()
  {
    return false;  // have not passed the certification tests
  }
*/
}
