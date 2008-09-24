package mdbtools.jdbc2;

import java.sql.*;

// abstract class that returns the metadata.  the metadata
// must be created prior to crating an instance of this class
public class ResultSetMetaData  implements java.sql.ResultSetMetaData
{
  protected String[] names;

  public ResultSetMetaData(String[] names)
  {
    this.names = names;
  }

  public Object unwrap(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public boolean isWrapperFor(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public int getColumnCount()
    throws SQLException
  {
    return names.length;
  }

  public boolean isAutoIncrement(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.isAutoIncrement() not yet implemented.");
  }

  public boolean isCaseSensitive(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.isCaseSensitive() not yet implemented.");
  }

  public boolean isSearchable(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.isSearchable() not yet implemented.");
  }

  public boolean isCurrency(int column) throws SQLException
  {
    return false;
  }

  public int isNullable(int column) throws SQLException
  {
    return columnNullableUnknown; // how the fuck should I know
  }

  public boolean isSigned(int column)
    throws SQLException
  {
    return false;
  }

  public int getColumnDisplaySize(int column)
    throws SQLException
  {
    return 20;
  }

  public String getColumnLabel(int column)
    throws SQLException
  {
    return names[column-1];
  }

  public String getColumnName(int column)
    throws SQLException
  {
    return names[column-1];
  }

  public String getSchemaName(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.getSchemaName() not yet implemented.");
  }

  public int getPrecision(int column)
    throws SQLException
  {
    return 10;  // just a guess
  }

  public int getScale(int column)
    throws SQLException
  {
    return 10;
  }

  public String getTableName(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.getTableName() not yet implemented.");
  }

  public String getCatalogName(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.getCatalogName() not yet implemented.");
  }

  public int getColumnType(int column)
    throws SQLException
  {
    return Types.VARCHAR;
  }

  public String getColumnTypeName(int column)
    throws SQLException
  {
    return "TEXT";
  }

  public boolean isReadOnly(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.isReadOnly() not yet implemented.");
  }

  public boolean isWritable(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.isWritable() not yet implemented.");
  }

  public boolean isDefinitelyWritable(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.isDefinitelyWritable() not yet implemented.");
  }

  public String getColumnClassName(int column) throws SQLException {
    /**@todo Implement this java.sql.ResultSetMetaData method*/
    throw new java.lang.UnsupportedOperationException(
        "Method MDBResultSetMetaData.getColumnClassName() not yet implemented.");
  }

}
