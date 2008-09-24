package mdbtools.examples;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;

public class mdb_tables
{
  public static void main(String[] args)
  {
    try
    {
      Class.forName("mdbtools.jdbc.Driver");
      String filename;
      filename = args[0];

      Connection conn = DriverManager.getConnection("jdbc:mdbtools:" + filename);
      ResultSet rset = conn.getMetaData().getTables(null,null,null,null);
      while (rset.next())
        System.out.println(rset.getString("table_name"));
      rset.close();
      conn.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
