package mdbtools.jdbc2;

import mdbtools.libmdb.MdbHandle;
import mdbtools.dbengine.Engine;

import java.sql.RowId;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Ref;
import java.sql.Blob;
import java.sql.NClob;
import java.sql.SQLXML;
import java.sql.Clob;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.net.URL;
import java.sql.ParameterMetaData;
import java.sql.SQLWarning;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @todo implement a real prepared statement
 * cache the executable statement, instead of parsing it each time
 */
public class MDBPreparedStatement implements PreparedStatement
{
  private Engine sqlEngine;  // this is the sql engine
  private String[] selectedColumnNames;
  private MDBSchema mdbSchema;
  private MdbHandle mdb;
  private mdbtools.dbengine.sql.Select esql;
  private ArrayList paramters = new ArrayList();

  MDBPreparedStatement(Engine sqlEngine,String sql,MDBSchema mdbSchema,MdbHandle mdb)
    throws SQLException
  {
System.out.println("psql: " + sql);
    this.sqlEngine = sqlEngine;
    this.mdbSchema = mdbSchema;
    this.mdb = mdb;

    // parse the sql into a parse tree
    mdbtools.jdbc.sqlparser.Parser parser = new mdbtools.jdbc.sqlparser.Parser(sql);
    mdbtools.jdbc.sqlparser.Select parsedSql =
          (mdbtools.jdbc.sqlparser.Select)parser.parse();

    // convert the parse tree into an exectuable sql tree
    esql = ConvertSQLParseTree.convert(parsedSql,mdbSchema,mdb,paramters);

    // keep track of where the paramters are
//    esql.

    // give every column a name
    int numColumns = esql.getColumnCount();
    selectedColumnNames = new String[numColumns];
    for (int j = 0; j < numColumns; j++)
    {
      Object o = esql.getColumn(j);
      if (o instanceof mdbtools.dbengine.sql.FQColumn)
      {
        mdbtools.dbengine.sql.FQColumn realColumn = (mdbtools.dbengine.sql.FQColumn)o;
        selectedColumnNames[j] = // This might not work when joins are enabled.
            ((mdbtools.dbengine.Table)esql.getTable(realColumn.getTable())).
                getColumnName(realColumn.getColumn());
      }
      else
        selectedColumnNames[j] = Integer.toString(j+1);  // not a column (perhaps a function?) assign a name
    }
  }

  public ResultSet executeQuery()
    throws SQLException
  {
    mdbtools.dbengine.sql.Select esql2;

    // replace all parmaters (if any) with their value
    if (paramters.size() == 0)
      esql2 = esql;
    else
      esql2 = convert(esql);

    // run the sql
    mdbtools.dbengine.Data result = sqlEngine.execute(esql2);

    // return the results
    return new DataResultSet(result,selectedColumnNames,
        new ResultSetMetaData(selectedColumnNames),0);
  }

  public void setNClob(int paramIndex, NClob nclob) {
    throw new RuntimeException("not implemented");
  }

  public void setNClob(int paramIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void setNClob(int paramIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void setBlob(int paramIndex, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void setBlob(int paramIndex, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void setClob(int paramIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void setClob(int paramIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void setNCharacterStream(int paramIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void setNCharacterStream(int paramIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void setCharacterStream(int paramIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void setBinaryStream(int paramIndex, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void setBinaryStream(int paramIndex, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void setAsciiStream(int paramIndex, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void setAsciiStream(int paramIndex, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void setCharacterStream(int paramIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void setSQLXML(int paramIndex, SQLXML xml) {
    throw new RuntimeException("not implemented");
  }

  public void setNString(int paramIndex, String v) {
    throw new RuntimeException("not implemented");
  }

  public void setRowId(int paramIndex, RowId x) {
    throw new RuntimeException("not implemented");
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

  public int executeUpdate()
    throws SQLException
  {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.executeUpdate() not yet implemented.");
  }

  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setNull() not yet implemented.");
  }
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setBoolean() not yet implemented.");
  }
  public void setByte(int parameterIndex, byte x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setByte() not yet implemented.");
  }
  public void setShort(int parameterIndex, short x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setShort() not yet implemented.");
  }

  public void setInt(int index, int x)
    throws SQLException
  {
    // replace the given paramter with the given object
    ((ParameterHolder)paramters.get(index-1)).setValue(new Integer(x));
  }

  public void setLong(int parameterIndex, long x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setLong() not yet implemented.");
  }
  public void setFloat(int parameterIndex, float x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setFloat() not yet implemented.");
  }
  public void setDouble(int parameterIndex, double x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setDouble() not yet implemented.");
  }
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setBigDecimal() not yet implemented.");
  }

  public void setString(int index, String x)
    throws SQLException
  {
    // replace the given paramter with the given object
    ((ParameterHolder)paramters.get(index-1)).setValue(x);
  }

  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setBytes() not yet implemented.");
  }
  public void setDate(int parameterIndex, Date x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setDate() not yet implemented.");
  }
  public void setTime(int parameterIndex, Time x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setTime() not yet implemented.");
  }
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setTimestamp() not yet implemented.");
  }
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setAsciiStream() not yet implemented.");
  }
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setUnicodeStream() not yet implemented.");
  }
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setBinaryStream() not yet implemented.");
  }
  public void clearParameters() throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.clearParameters() not yet implemented.");
  }
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setObject() not yet implemented.");
  }
  public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setObject() not yet implemented.");
  }
  public void setObject(int parameterIndex, Object x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setObject() not yet implemented.");
  }
  public boolean execute() throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.execute() not yet implemented.");
  }
  public void addBatch() throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.addBatch() not yet implemented.");
  }
  public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setCharacterStream() not yet implemented.");
  }
  public void setRef(int i, Ref x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setRef() not yet implemented.");
  }
  public void setBlob(int i, Blob x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setBlob() not yet implemented.");
  }
  public void setClob(int i, Clob x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setClob() not yet implemented.");
  }
  public void setArray(int i, Array x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setArray() not yet implemented.");
  }
  public java.sql.ResultSetMetaData getMetaData() throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getMetaData() not yet implemented.");
  }
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setDate() not yet implemented.");
  }
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setTime() not yet implemented.");
  }
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setTimestamp() not yet implemented.");
  }
  public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setNull() not yet implemented.");
  }
  public void setURL(int parameterIndex, URL x) throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setURL() not yet implemented.");
  }
  public ParameterMetaData getParameterMetaData() throws SQLException {
    /**@todo Implement this java.sql.PreparedStatement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getParameterMetaData() not yet implemented.");
  }
  public ResultSet executeQuery(String sql) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.executeQuery() not yet implemented.");
  }
  public int executeUpdate(String sql) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.executeUpdate() not yet implemented.");
  }

  public void close()
    throws SQLException
  {
    /** @todo implement a proper close method */
//    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.close() not yet implemented.");
  }

  public int getMaxFieldSize() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getMaxFieldSize() not yet implemented.");
  }
  public void setMaxFieldSize(int max) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setMaxFieldSize() not yet implemented.");
  }
  public int getMaxRows() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getMaxRows() not yet implemented.");
  }
  public void setMaxRows(int max) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setMaxRows() not yet implemented.");
  }
  public void setEscapeProcessing(boolean enable) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setEscapeProcessing() not yet implemented.");
  }
  public int getQueryTimeout() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getQueryTimeout() not yet implemented.");
  }
  public void setQueryTimeout(int seconds) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setQueryTimeout() not yet implemented.");
  }
  public void cancel() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.cancel() not yet implemented.");
  }
  public SQLWarning getWarnings() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getWarnings() not yet implemented.");
  }
  public void clearWarnings() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.clearWarnings() not yet implemented.");
  }
  public void setCursorName(String name) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setCursorName() not yet implemented.");
  }
  public boolean execute(String sql) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.execute() not yet implemented.");
  }
  public ResultSet getResultSet() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getResultSet() not yet implemented.");
  }
  public int getUpdateCount() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getUpdateCount() not yet implemented.");
  }
  public boolean getMoreResults() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getMoreResults() not yet implemented.");
  }
  public void setFetchDirection(int direction) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setFetchDirection() not yet implemented.");
  }
  public int getFetchDirection() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getFetchDirection() not yet implemented.");
  }
  public void setFetchSize(int rows) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.setFetchSize() not yet implemented.");
  }
  public int getFetchSize() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getFetchSize() not yet implemented.");
  }
  public int getResultSetConcurrency() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getResultSetConcurrency() not yet implemented.");
  }
  public int getResultSetType() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getResultSetType() not yet implemented.");
  }
  public void addBatch(String sql) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.addBatch() not yet implemented.");
  }
  public void clearBatch() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.clearBatch() not yet implemented.");
  }
  public int[] executeBatch() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.executeBatch() not yet implemented.");
  }
  public Connection getConnection() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getConnection() not yet implemented.");
  }
  public boolean getMoreResults(int current) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getMoreResults() not yet implemented.");
  }
  public ResultSet getGeneratedKeys() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getGeneratedKeys() not yet implemented.");
  }
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.executeUpdate() not yet implemented.");
  }
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.executeUpdate() not yet implemented.");
  }
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.executeUpdate() not yet implemented.");
  }
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.execute() not yet implemented.");
  }
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.execute() not yet implemented.");
  }

  public boolean execute(String sql, String[] columnNames) throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.execute() not yet implemented.");
  }

  public int getResultSetHoldability() throws SQLException {
    /**@todo Implement this java.sql.Statement method*/
    throw new java.lang.UnsupportedOperationException("Method MDBPreparedStatement.getResultSetHoldability() not yet implemented.");
  }

  // replace paramters with their value
  private int numReplacedParameters;
  private mdbtools.dbengine.sql.Select convert(mdbtools.dbengine.sql.Select original)
    throws SQLException
  {
    numReplacedParameters = 0;
    mdbtools.dbengine.sql.Select converted = new mdbtools.dbengine.sql.Select();
    // columns
    for (int i = 0; i < original.getColumnCount(); i++)
      converted.addColumn(convert(original.getColumn(i)));
    // tables
    for (int i = 0; i < original.getTableCount(); i++)
      converted.addTable(original.getTable(i));
    // where
    Object where = original.getWhere();
    if (where != null)
      converted.setWhere(convert(original.getWhere()));
    if (numReplacedParameters != paramters.size())
      throw new SQLException("not all parameters were found: " + numReplacedParameters);
    return converted;
  }

  private Object convert(Object o)
    throws SQLException
  {
    if (o instanceof ParameterHolder)
    {
      numReplacedParameters++;
      ParameterHolder ph = (ParameterHolder)o;
      if (ph.isSet() == false)
        throw new SQLException("parmeter: " + numReplacedParameters + " not set");
      return ph.getValue();
    }
    else if (o instanceof mdbtools.dbengine.sql.Equation)
      return convert((mdbtools.dbengine.sql.Equation)o);
    else if (o instanceof mdbtools.dbengine.sql.Condition)
      return convert((mdbtools.dbengine.sql.Condition)o);
    else
      return o;
  }

  private Object convert(mdbtools.dbengine.sql.Equation orig)
    throws SQLException
  {
    mdbtools.dbengine.sql.Equation converted = new mdbtools.dbengine.sql.Equation();
    converted.setLeft(convert(orig.getLeft()));
    converted.setOperator(orig.getOperator());
    converted.setRight(convert(orig.getRight()));
    return converted;
  }

  private Object convert(mdbtools.dbengine.sql.Condition orig)
    throws SQLException
  {
    mdbtools.dbengine.sql.Condition converted = new mdbtools.dbengine.sql.Condition();
    converted.setLeft(convert(orig.getLeft()));
    converted.setOperator(orig.getOperator());
    converted.setRight(convert(orig.getRight()));
    return converted;
  }
}
