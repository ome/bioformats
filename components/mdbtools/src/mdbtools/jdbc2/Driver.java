package mdbtools.jdbc2;

import mdbtools.libmdb.*;
import mdbtools.publicapi.RandomAccess;

import java.io.IOException;
import java.sql.*;

public class Driver implements java.sql.Driver
{
  public Driver()
  {
  }

  static
  {
    try
    {
      mem.mdb_init();
      DriverManager.registerDriver(new Driver());
    }
    catch(SQLException e)
    {
      throw new RuntimeException(e.getMessage());
    }
  }

  public Connection connect(String url, java.util.Properties info)
    throws SQLException
  {
    if(url.startsWith("jdbc:mdbtools:") == false)
      return null;  // not the right driver

    try
    {
      RandomAccess ra;
      Object o = info.get("RandomAccess");
      if (o == null)
      {
        String filename = url.substring(14);
        ra = new File(filename);
      }
      else
        ra = (RandomAccess)o;
      return new MDBConnection(ra);
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
}
