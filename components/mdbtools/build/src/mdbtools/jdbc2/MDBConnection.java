package mdbtools.jdbc2;

import mdbtools.dbengine.*;
import mdbtools.libmdb.*;
import mdbtools.publicapi.RandomAccess;

import java.util.Properties;
import java.io.IOException;
import java.sql.*;

public class MDBConnection implements Connection
{
  private MdbHandle mdb;
  private Engine sqlEngine;  // this is the sql engine
  private MDBSchema mdbSchema;

  MDBConnection(RandomAccess f)
    throws IOException
  {
    mdb = file.mdb_open(f);
    boolean success = false;
    try
    {
      Catalog.mdb_read_catalog(mdb, Constants.MDB_TABLE);
      this.mdb = mdb;
      sqlEngine = new Engine();
      mdbSchema = new MDBSchema(mdb);
      success = true;
    }
    finally
    {
      // close the file on any errors
      if (success == false)
        mem.mdb_free_handle(mdb);
    }
  }

  public Statement createStatement()
      throws SQLException
  {
    return new MDBStatement(mdb,sqlEngine,mdbSchema);
  }

  public PreparedStatement prepareStatement(String sql)
      throws SQLException
  {
    return new MDBPreparedStatement(sqlEngine,sql,mdbSchema,mdb);
  }

  public CallableStatement prepareCall(String sql)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.prepareCall not implemented");
  }

  public String nativeSQL(String sql)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.nativeSQL not implemented");
  }

  public void setAutoCommit(boolean autoCommit)
      throws SQLException
  {
    // till write support is added this is a noop
//    throw new RuntimeException("MDBConnection.setAutoCommit not implemented");
  }

  public boolean getAutoCommit()
      throws SQLException
  {
    return true;
  }

  public void commit()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.commit not implemented");
  }

  public void rollback()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.rollback not implemented");
  }

  public void close()
      throws SQLException
  {
    /** @todo don't allow the connection to be used after a close */
    try
    {
      mem.mdb_free_handle(mdb);
    }
    catch(IOException e)
    {
      throw new SQLException(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  public boolean isClosed()
      throws SQLException
  {
    /** @todo do a real implementation */
    return false;
//    throw new RuntimeException("MDBConnection.isClosed not implemented");
  }

  public java.sql.DatabaseMetaData getMetaData()
      throws SQLException
  {
    return new DatabaseMetaData(mdbSchema);
  }

  public void setReadOnly(boolean readOnly)
      throws SQLException
  {
    // till write support is added this is a noop
//    throw new RuntimeException("MDBConnection.setReadOnly not implemented");
  }

  public boolean isReadOnly()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.isReadOnly not implemented");
  }

  public void setCatalog(String catalog)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.setCatalog not implemented");
  }

  public String getCatalog()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.getCatalog not implemented");
  }

  public void setTransactionIsolation(int level)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.setTransactionIsolation not implemented");
  }

  public int getTransactionIsolation()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.getTransactionIsolation not implemented");
  }

  public SQLWarning getWarnings()
      throws SQLException
  {
    return null;

    /** @todo do a proper implementation */
//    throw new RuntimeException("MDBConnection.getWarnings not implemented");
  }

  public void clearWarnings()
      throws SQLException
  {
    /** @todo do a proper implementation */
//    throw new RuntimeException("MDBConnection.clearWarnings not implemented");
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency)
      throws SQLException
  {
    /** @todo do a proper implementation */
    return createStatement();
//    throw new RuntimeException("MDBConnection.createStatement not implemented");
  }

  public PreparedStatement prepareStatement(String sql,
                                            int resultSetType,
                                            int resultSetConcurrency)
      throws SQLException
  {
    /** @todo implement the passed parameters */
//    System.out.println("2 - sql: " + sql);
    return prepareStatement(sql);
//    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public CallableStatement prepareCall(String sql, int resultSetType,
                                int resultSetConcurrency)
    throws SQLException
  {
    throw new RuntimeException("MDBConnection.prepareCall not implemented");
  }

  public java.util.Map getTypeMap()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.getTypeMap not implemented");
  }

  public void setTypeMap(java.util.Map map)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.setTypeMap not implemented");
  }

  public void setHoldability(int holdability)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.setHoldability not implemented");
  }

  public int getHoldability()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.getHoldability not implemented");
  }

  public Savepoint setSavepoint()
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.setSavepoint not implemented");
  }

  public Savepoint setSavepoint(String name)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.setSavepoint not implemented");
  }

  public void rollback(Savepoint savepoint)
      throws SQLException
 {
    throw new RuntimeException("MDBConnection.rollback not implemented");
  }

  public void releaseSavepoint(Savepoint savepoint)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.releaseSavepoint not implemented");
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency,
                            int resultSetHoldability) throws SQLException
  {
    /** @todo do a proper implementation */
    return createStatement();
//    throw new RuntimeException("MDBConnection.createStatement not implemented");
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType,
                                     int resultSetConcurrency, int resultSetHoldability)
      throws SQLException
  {
    System.out.println("3 - sql: " + sql);
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public CallableStatement prepareCall(String sql, int resultSetType,
                                int resultSetConcurrency,
                                int resultSetHoldability)
    throws SQLException
  {
    throw new RuntimeException("MDBConnection.prepareCall not implemented");
  }

  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
      throws SQLException
  {
    System.out.println("4 - sql: " + sql);
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public PreparedStatement prepareStatement(String sql, int columnIndexes[])
    throws SQLException
  {
    System.out.println("5 - sql: " + sql);
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public PreparedStatement prepareStatement(String sql, String columnNames[])
    throws SQLException
  {
    System.out.println("6 - sql: " + sql);
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public Struct createStruct(String type, Object[] attributes) {
    throw new RuntimeException("not implemented");
  }

  public Array createArrayOf(String type, Object[] attributes) {
    throw new RuntimeException("not implemented");
  }

  public Properties getClientInfo() {
    throw new RuntimeException("not implemented");
  }

  public String getClientInfo(String name) {
    throw new RuntimeException("not implemented");
  }

  public void setClientInfo(Properties props) {
    throw new RuntimeException("not implemented");
  }

  public void setClientInfo(String name, String value) {
    throw new RuntimeException("not implemented");
  }

  public boolean isValid(int timeout) {
    throw new RuntimeException("not implemented");
  }

  public SQLXML createSQLXML() {
    throw new RuntimeException("not implemented");
  }

  public NClob createNClob() {
    throw new RuntimeException("not implemented");
  }

  public Blob createBlob() {
    throw new RuntimeException("not implemented");
  }

  public Clob createClob() {
    throw new RuntimeException("not implemented");
  }

  public boolean isWrapperFor(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public Object unwrap(Class iface) {
    throw new RuntimeException("not implemented");
  }

}
