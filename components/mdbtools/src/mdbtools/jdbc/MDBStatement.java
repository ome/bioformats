package mdbtools.jdbc;

import mdbtools.libmdb.*;

import java.io.IOException;
import java.sql.*;

public class MDBStatement implements Statement
{
  private Engine engine;  // this is the sql engine
  private ResultSet savedResultSet;   // supports execute

  MDBStatement(MdbHandle mdb,Engine engine)
  {
    this.engine = engine;
  }

  public ResultSet executeQuery(String sql)
      throws SQLException
  {
    try
    {
      return engine.execute(sql);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new SQLException(e.getMessage());
    }
  }

  public int executeUpdate(String sql)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.executeUpdate not implemented");
  }

  public void close()
      throws SQLException
  {
    /** @todo don't allow the statement to be used after a close */
//    throw new RuntimeException("MDBStatement.close not implemented");
  }

  public int getMaxFieldSize()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getMaxFieldSize not implemented");
  }

  public void setMaxFieldSize(int max)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setMaxFieldSize not implemented");
  }

  public int getMaxRows()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getMaxRows not implemented");
  }

  public void setMaxRows(int max)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setMaxRows not implemented");
  }

  public void setEscapeProcessing(boolean enable)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setEscapeProcessing not implemented");
  }

  public int getQueryTimeout()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getQueryTimeout not implemented");
  }

  public void setQueryTimeout(int seconds)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setQueryTimeout not implemented");
  }

  public void cancel()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.cancel not implemented");
  }

  public SQLWarning getWarnings()
      throws SQLException
  {
//    throw new RuntimeException("MDBStatement.getWarnings not implemented");
    return null; //no warnings to return
  }

  public void clearWarnings()
      throws SQLException
  {
    //no problem "clearing" what we don't have!
    //throw new RuntimeException("MDBStatement.clearWarnings not implemented");
  }

  public void setCursorName(String name)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setCursorName not implemented");
  }

  public boolean execute(String sql)
      throws SQLException
  {
//    throw new RuntimeException("MDBStatement.execute not implemented");
    savedResultSet = executeQuery(sql);
    return true;
  }

  public ResultSet getResultSet()
      throws SQLException
  {
//    throw new RuntimeException("MDBStatement.getResultSet not implemented");
    return savedResultSet;
  }

  public int getUpdateCount()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getUpdateCount not implemented");
  }

  public boolean getMoreResults()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getMoreResults not implemented");
  }

  public void setFetchDirection(int direction)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setFetchDirection not implemented");
  }

  public int getFetchDirection()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getFetchDirection not implemented");
  }

  public void setFetchSize(int rows)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setFetchSize not implemented");
  }

  public int getFetchSize()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getFetchSize not implemented");
  }

  public int getResultSetConcurrency()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getResultSetConcurrency not implemented");
  }

  public int getResultSetType()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getResultSetType not implemented");
  }

  public void addBatch(String sql)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.addBatch not implemented");
  }

  public void clearBatch()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.clearBatch not implemented");
  }

  public int[] executeBatch()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.executeBatch not implemented");
  }

  public Connection getConnection()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getConnection not implemented");
  }

  public boolean getMoreResults(int current)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getMoreResults not implemented");
  }

  public ResultSet getGeneratedKeys()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getGeneratedKeys not implemented");
  }

  public int executeUpdate(String sql, int autoGeneratedKeys)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.executeUpdate not implemented");
  }

  public int executeUpdate(String sql, int columnIndexes[])
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.executeUpdate not implemented");
  }

  public int executeUpdate(String sql, String columnNames[])
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.executeUpdate not implemented");
  }

  public boolean execute(String sql, int autoGeneratedKeys)
      throws SQLException
  {
//    throw new RuntimeException("MDBStatement.execute not implemented");
    throw new RuntimeException("MDBStatement.execute:String,int not implemented");
  }

  public boolean execute(String sql, int columnIndexes[])
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.execute:String,int[] not implemented");
  }

  public boolean execute(String sql, String columnNames[])
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.execute:String,String[] not implemented");
  }

  public int getResultSetHoldability()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getResultSetHoldability not implemented");
  }

  public boolean isPoolable() {
    throw new RuntimeException("not implemented");
  }

  public void setPoolable(boolean poolable) {
    throw new RuntimeException("not implemented");
  }

  public boolean isClosed() {
    throw new RuntimeException("not implemented");
  }

  public boolean isWrapperFor(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public Object unwrap(Class iface) {
    throw new RuntimeException("not implemented");
  }
}
