package mdbtools.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class Test
{
  public static void main(String[] args)
  {
    try
    {
      Class.forName("mdbtools.jdbc.Driver");
      String filename = args[0];
      Connection conn = DriverManager.getConnection("jdbc:mdbtools:" + filename);
      Statement stmt = conn.createStatement();
      String query = args[1];
      ResultSet rset = stmt.executeQuery(query);
      ResultSetMetaData rmd = rset.getMetaData();
      int numCol = rmd.getColumnCount();

      System.out.print("# ");
      for (int n=1; n <= numCol; n++)
      {
        System.out.print(rmd.getColumnLabel(n) + "|");
      }
      System.out.println();

      for (int i = 1; rset.next(); i++)
      {
        System.out.print((i) + " ");
        for (int n=1; n <= numCol; n++)
        {
          System.out.print(rset.getString(n) + "|");
        }
        System.out.println();
      }
      rset.close();
      stmt.close();
      conn.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
