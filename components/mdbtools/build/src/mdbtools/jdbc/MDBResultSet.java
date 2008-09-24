package mdbtools.jdbc;

import mdbtools.libmdb.*;

import java.sql.*;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Calendar;
import java.net.URL;

public class MDBResultSet  implements ResultSet
{
  MDBResultSet()
  {
  }

  public boolean next()
    throws SQLException
  {
    throw new java.lang.UnsupportedOperationException("not yet implemented");
  }

  public void close()
    throws SQLException
  {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method close() not yet implemented.");
  }

  public boolean wasNull() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method wasNull() not yet implemented.");
  }

  public String getString(int columnIndex)
      throws SQLException
  {
    throw new java.lang.UnsupportedOperationException(
        "not yet implemented.");
  }

  public boolean getBoolean(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBoolean() not yet implemented.");
  }

  public byte getByte(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getByte() not yet implemented.");
  }

  public short getShort(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getShort() not yet implemented.");
  }

  public int getInt(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getInt() not yet implemented.");
  }

  public long getLong(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getLong() not yet implemented.");
  }

  public float getFloat(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getFloat() not yet implemented.");
  }

  public double getDouble(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getDouble() not yet implemented.");
  }

  public BigDecimal getBigDecimal(int columnIndex, int scale) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBigDecimal() not yet implemented.");
  }

  public byte[] getBytes(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBytes() not yet implemented.");
  }

  public Date getDate(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getDate() not yet implemented.");
  }

  public Time getTime(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTime() not yet implemented.");
  }

  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTimestamp() not yet implemented.");
  }

  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getAsciiStream() not yet implemented.");
  }

  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getUnicodeStream() not yet implemented.");
  }

  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBinaryStream() not yet implemented.");
  }

  public String getString(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getString() not yet implemented.");
  }

  public boolean getBoolean(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBoolean() not yet implemented.");
  }

  public byte getByte(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getByte() not yet implemented.");
  }

  public short getShort(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getShort() not yet implemented.");
  }

  public int getInt(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getInt() not yet implemented.");
  }

  public long getLong(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getLong() not yet implemented.");
  }

  public float getFloat(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getFloat() not yet implemented.");
  }

  public double getDouble(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getDouble() not yet implemented.");
  }

  public BigDecimal getBigDecimal(String columnName, int scale) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBigDecimal() not yet implemented.");
  }

  public byte[] getBytes(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBytes() not yet implemented.");
  }

  public Date getDate(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getDate() not yet implemented.");
  }

  public Time getTime(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTime() not yet implemented.");
  }

  public Timestamp getTimestamp(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTimestamp() not yet implemented.");
  }

  public InputStream getAsciiStream(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getAsciiStream() not yet implemented.");
  }

  public InputStream getUnicodeStream(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getUnicodeStream() not yet implemented.");
  }

  public InputStream getBinaryStream(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBinaryStream() not yet implemented.");
  }

  public SQLWarning getWarnings() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getWarnings() not yet implemented.");
  }

  public void clearWarnings() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method clearWarnings() not yet implemented.");
  }

  public String getCursorName() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getCursorName() not yet implemented.");
  }

  public ResultSetMetaData getMetaData() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getMetaData() not yet implemented.");
  }

  public Object getObject(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getObject() not yet implemented.");
  }

  public Object getObject(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getObject() not yet implemented.");
  }

  public int findColumn(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method findColumn() not yet implemented.");
  }

  public Reader getCharacterStream(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getCharacterStream() not yet implemented.");
  }

  public Reader getCharacterStream(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getCharacterStream() not yet implemented.");
  }

  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBigDecimal() not yet implemented.");
  }

  public BigDecimal getBigDecimal(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBigDecimal() not yet implemented.");
  }

  public boolean isBeforeFirst() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method isBeforeFirst() not yet implemented.");
  }

  public boolean isAfterLast() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method isAfterLast() not yet implemented.");
  }

  public boolean isFirst() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method isFirst() not yet implemented.");
  }

  public boolean isLast() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method isLast() not yet implemented.");
  }

  public void beforeFirst() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method beforeFirst() not yet implemented.");
  }

  public void afterLast() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method afterLast() not yet implemented.");
  }

  public boolean first() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method first() not yet implemented.");
  }

  public boolean last() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method last() not yet implemented.");
  }

  public int getRow() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getRow() not yet implemented.");
  }

  public boolean absolute(int row) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method absolute() not yet implemented.");
  }

  public boolean relative(int rows) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method relative() not yet implemented.");
  }

  public boolean previous() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method previous() not yet implemented.");
  }

  public void setFetchDirection(int direction) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method setFetchDirection() not yet implemented.");
  }

  public int getFetchDirection() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getFetchDirection() not yet implemented.");
  }

  public void setFetchSize(int rows) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method setFetchSize() not yet implemented.");
  }

  public int getFetchSize() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getFetchSize() not yet implemented.");
  }

  public int getType() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getType() not yet implemented.");
  }

  public int getConcurrency() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getConcurrency() not yet implemented.");
  }

  public boolean rowUpdated() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method rowUpdated() not yet implemented.");
  }

  public boolean rowInserted() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method rowInserted() not yet implemented.");
  }

  public boolean rowDeleted() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method rowDeleted() not yet implemented.");
  }

  public void updateNull(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateNull() not yet implemented.");
  }

  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBoolean() not yet implemented.");
  }

  public void updateByte(int columnIndex, byte x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateByte() not yet implemented.");
  }

  public void updateShort(int columnIndex, short x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateShort() not yet implemented.");
  }

  public void updateInt(int columnIndex, int x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateInt() not yet implemented.");
  }

  public void updateLong(int columnIndex, long x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateLong() not yet implemented.");
  }

  public void updateFloat(int columnIndex, float x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateFloat() not yet implemented.");
  }

  public void updateDouble(int columnIndex, double x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateDouble() not yet implemented.");
  }

  public void updateBigDecimal(int columnIndex, BigDecimal x) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBigDecimal() not yet implemented.");
  }

  public void updateString(int columnIndex, String x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateString() not yet implemented.");
  }

  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBytes() not yet implemented.");
  }

  public void updateDate(int columnIndex, Date x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateDate() not yet implemented.");
  }

  public void updateTime(int columnIndex, Time x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateTime() not yet implemented.");
  }

  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateTimestamp() not yet implemented.");
  }

  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateAsciiStream() not yet implemented.");
  }

  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBinaryStream() not yet implemented.");
  }

  public void updateCharacterStream(int columnIndex, Reader x, int length) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateCharacterStream() not yet implemented.");
  }

  public void updateObject(int columnIndex, Object x, int scale) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateObject() not yet implemented.");
  }

  public void updateObject(int columnIndex, Object x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateObject() not yet implemented.");
  }

  public void updateNull(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateNull() not yet implemented.");
  }

  public void updateBoolean(String columnName, boolean x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBoolean() not yet implemented.");
  }

  public void updateByte(String columnName, byte x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateByte() not yet implemented.");
  }

  public void updateShort(String columnName, short x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateShort() not yet implemented.");
  }

  public void updateInt(String columnName, int x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateInt() not yet implemented.");
  }

  public void updateLong(String columnName, long x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateLong() not yet implemented.");
  }

  public void updateFloat(String columnName, float x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateFloat() not yet implemented.");
  }

  public void updateDouble(String columnName, double x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateDouble() not yet implemented.");
  }

  public void updateBigDecimal(String columnName, BigDecimal x) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBigDecimal() not yet implemented.");
  }

  public void updateString(String columnName, String x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateString() not yet implemented.");
  }

  public void updateBytes(String columnName, byte[] x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBytes() not yet implemented.");
  }

  public void updateDate(String columnName, Date x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateDate() not yet implemented.");
  }

  public void updateTime(String columnName, Time x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateTime() not yet implemented.");
  }

  public void updateTimestamp(String columnName, Timestamp x) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateTimestamp() not yet implemented.");
  }

  public void updateAsciiStream(String columnName, InputStream x, int length) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateAsciiStream() not yet implemented.");
  }

  public void updateBinaryStream(String columnName, InputStream x, int length) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBinaryStream() not yet implemented.");
  }

  public void updateCharacterStream(String columnName, Reader reader,
                                    int length) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateCharacterStream() not yet implemented.");
  }

  public void updateObject(String columnName, Object x, int scale) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateObject() not yet implemented.");
  }

  public void updateObject(String columnName, Object x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateObject() not yet implemented.");
  }

  public void insertRow() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method insertRow() not yet implemented.");
  }

  public void updateRow() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateRow() not yet implemented.");
  }

  public void deleteRow() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method deleteRow() not yet implemented.");
  }

  public void refreshRow() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method refreshRow() not yet implemented.");
  }

  public void cancelRowUpdates() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method cancelRowUpdates() not yet implemented.");
  }

  public void moveToInsertRow() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method moveToInsertRow() not yet implemented.");
  }

  public void moveToCurrentRow() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method moveToCurrentRow() not yet implemented.");
  }

  public Statement getStatement() throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getStatement() not yet implemented.");
  }

  public Object getObject(int i, Map map) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getObject() not yet implemented.");
  }

  public Ref getRef(int i) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getRef() not yet implemented.");
  }

  public Blob getBlob(int i) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBlob() not yet implemented.");
  }

  public Clob getClob(int i) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getClob() not yet implemented.");
  }

  public Array getArray(int i) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getArray() not yet implemented.");
  }

  public Object getObject(String colName, Map map) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getObject() not yet implemented.");
  }

  public Ref getRef(String colName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getRef() not yet implemented.");
  }

  public Blob getBlob(String colName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getBlob() not yet implemented.");
  }

  public Clob getClob(String colName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getClob() not yet implemented.");
  }

  public Array getArray(String colName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getArray() not yet implemented.");
  }

  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getDate() not yet implemented.");
  }

  public Date getDate(String columnName, Calendar cal) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getDate() not yet implemented.");
  }

  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTime() not yet implemented.");
  }

  public Time getTime(String columnName, Calendar cal) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTime() not yet implemented.");
  }

  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTimestamp() not yet implemented.");
  }

  public Timestamp getTimestamp(String columnName, Calendar cal) throws
      SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getTimestamp() not yet implemented.");
  }

  public URL getURL(int columnIndex) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getURL() not yet implemented.");
  }

  public URL getURL(String columnName) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method getURL() not yet implemented.");
  }

  public void updateRef(int columnIndex, Ref x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateRef() not yet implemented.");
  }

  public void updateRef(String columnName, Ref x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateRef() not yet implemented.");
  }

  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBlob() not yet implemented.");
  }

  public void updateBlob(String columnName, Blob x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateBlob() not yet implemented.");
  }

  public void updateClob(int columnIndex, Clob x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateClob() not yet implemented.");
  }

  public void updateClob(String columnName, Clob x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateClob() not yet implemented.");
  }

  public void updateArray(int columnIndex, Array x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateArray() not yet implemented.");
  }

  public void updateArray(String columnName, Array x) throws SQLException {
    /**@todo Implement this java.sql.ResultSet method*/
    throw new java.lang.UnsupportedOperationException(
        "Method updateArray() not yet implemented.");
  }

  public boolean isWrapperFor(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public Object unwrap(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public void updateNClob(String column, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateNClob(String column, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateNClob(int columnIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateNClob(int columnIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateClob(String column, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateClob(String column, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateClob(int columnIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateClob(int columnIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateBlob(int columnIndex, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void updateBlob(int columnIndex, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateBlob(String column, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void updateBlob(String column, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateCharacterStream(String column, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateCharacterStream(int columnIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateCharacterStream(String column, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateCharacterStream(int columnIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateBinaryStream(String column, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void updateBinaryStream(int columnIndex, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void updateBinaryStream(String column, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateBinaryStream(int columnIndex, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateAsciiStream(String column, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void updateAsciiStream(int columnIndex, InputStream is) {
    throw new RuntimeException("not implemented");
  }

  public void updateAsciiStream(String column, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateAsciiStream(int columnIndex, InputStream is, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateNCharacterStream(String column, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateNCharacterStream(int columnIndex, Reader r) {
    throw new RuntimeException("not implemented");
  }

  public void updateNCharacterStream(String column, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public void updateNCharacterStream(int columnIndex, Reader r, long length) {
    throw new RuntimeException("not implemented");
  }

  public Reader getNCharacterStream(int columnIndex) {
    throw new RuntimeException("not implemented");
  }

  public Reader getNCharacterStream(String column) {
    throw new RuntimeException("not implemented");
  }

  public String getNString(int columnIndex) {
    throw new RuntimeException("not implemented");
  }

  public String getNString(String column) {
    throw new RuntimeException("not implemented");
  }

  public void updateSQLXML(int columnIndex, SQLXML xml) {
    throw new RuntimeException("not implemented");
  }

  public void updateSQLXML(String column, SQLXML xml) {
    throw new RuntimeException("not implemented");
  }

  public SQLXML getSQLXML(int columnIndex) {
    throw new RuntimeException("not implemented");
  }

  public SQLXML getSQLXML(String column) {
    throw new RuntimeException("not implemented");
  }

  public NClob getNClob(int columnIndex) {
    throw new RuntimeException("not implemented");
  }

  public NClob getNClob(String column) {
    throw new RuntimeException("not implemented");
  }

  public void updateNClob(String column, NClob nclob) {
    throw new RuntimeException("not implemented");
  }

  public void updateNClob(int columnIndex, NClob nclob) {
    throw new RuntimeException("not implemented");
  }

  public void updateNString(int columnIndex, String value) {
    throw new RuntimeException("not implemented");
  }

  public void updateNString(String column, String value) {
    throw new RuntimeException("not implemented");
  }

  public boolean isClosed() {
    throw new RuntimeException("not implemented");
  }

  public int getHoldability() {
    throw new RuntimeException("not implemented");
  }

  public void updateRowId(String column, RowId x) {
    throw new RuntimeException("not implemented");
  }

  public void updateRowId(int columnIndex, RowId x) {
    throw new RuntimeException("not implemented");
  }

  public RowId getRowId(int columnIndex) {
    throw new RuntimeException("not implemented");
  }

  public RowId getRowId(String column) {
    throw new RuntimeException("not implemented");
  }

}
