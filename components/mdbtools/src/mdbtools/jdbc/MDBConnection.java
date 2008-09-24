package mdbtools.jdbc;

import mdbtools.libmdb.*;

import java.util.Properties;
import java.io.IOException;
import java.sql.*;

public class MDBConnection implements Connection
{
  MdbHandle mdb;
  private Engine engine;  // this is the sql engine

  MDBConnection(String filename)
    throws IOException
  {
    mdb = file.mdb_open(new mdbtools.jdbc2.File(filename));
    Catalog.mdb_read_catalog(mdb, Constants.MDB_TABLE);
    this.mdb = mdb;
    engine = new Engine(mdb);
  }

  public Statement createStatement()
      throws SQLException
  {
    return new MDBStatement(mdb,engine);
  }

  public PreparedStatement prepareStatement(String sql)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
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
    /** @todo need a real implementation */
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
    throw new RuntimeException("MDBConnection.isClosed not implemented");
  }

  public DatabaseMetaData getMetaData()
      throws SQLException
  {
    return new MDBDatabaseMetaData(this);
  }

  public void setReadOnly(boolean readOnly)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.setReadOnly not implemented");
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
    /** @todo need a real implementation */
    return null;
  }

  public void clearWarnings()
      throws SQLException
  {
    // no problem "clearing" what we don't have!
    /** @todo need a real implementation */
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency)
      throws SQLException
  {
//    throw new RuntimeException("MDBConnection.createStatement not implemented");
    /** @todo need a real implementation */
    return createStatement();
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType,
                                     int resultSetConcurrency)
      throws SQLException
  {
    throw new RuntimeException("MDBConnection.prepareStatement:sql,int,int not implemented");
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
    throw new RuntimeException("MDBConnection.createStatement:int:int:int not implemented");
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType,
                                     int resultSetConcurrency, int resultSetHoldability)
      throws SQLException
  {
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
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public PreparedStatement prepareStatement(String sql, int columnIndexes[])
    throws SQLException
  {
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public PreparedStatement prepareStatement(String sql, String columnNames[])
    throws SQLException
  {
    throw new RuntimeException("MDBConnection.prepareStatement not implemented");
  }

  public Struct createStruct(String type, Object[] attributes) {
    throw new RuntimeException("not implemented");
  }

  public Array createArrayOf(String type, Object[] elements) {
    throw new RuntimeException("not implemented");
  }

  public Properties getClientInfo() {
    throw new RuntimeException("not implemented");
  }

  public String getClientInfo(String name) {
    throw new RuntimeException("not implemented");
  }

  public void setClientInfo(Properties properties) {
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

  public Blob createBlob() {
    throw new RuntimeException("not implemented");
  }

  public Clob createClob() {
    throw new RuntimeException("not implemented");
  }

  public NClob createNClob() {
    throw new RuntimeException("not implemented");
  }

  public boolean isWrapperFor(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public Object unwrap(Class iface) {
    throw new RuntimeException("not implemented");
  }

}
