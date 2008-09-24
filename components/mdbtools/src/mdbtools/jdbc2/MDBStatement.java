package mdbtools.jdbc2;

import mdbtools.dbengine.*;
import mdbtools.dbengine.sql.*;
import mdbtools.dbengine.functions.*;
import mdbtools.jdbc.sqlparser.*;
import mdbtools.libmdb.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * @todo make a new class to do the transform from the parse tree
 * to the executable tree.
 * flow is something like:
 *
 * validate parse tree  (is this needed ?)
 * remove: select *
 *         replace with all columns
 * resolve columns
 * resolve functions
 * convert the where clause, resolving as it goes
 * ...
 */
public class MDBStatement implements Statement
{
  private Engine sqlEngine;  // this is the sql engine
  private MdbHandle mdb;
  private MDBSchema mdbSchema;
  private int maxRows = 0;  // unlimited
  private ResultSet savedResultSet;   // supports execute

  MDBStatement(MdbHandle mdb,Engine engine,MDBSchema mdbSchema)
  {
    this.mdb = mdb;
    this.sqlEngine = engine;
    this.mdbSchema = mdbSchema;
  }

  public ResultSet executeQuery(String sql)
      throws SQLException
  {
System.out.println("sql: " + sql);
    sql = sql.trim();
System.out.println("trimmed sql: " + sql);

    // parse the sql into a parse tree
    Parser parser = new Parser(sql);
    mdbtools.jdbc.sqlparser.Select parsedSql =
        (mdbtools.jdbc.sqlparser.Select)parser.parse();

    // convert the parse tree into an exectuable sql tree
    mdbtools.dbengine.sql.Select esql = ConvertSQLParseTree.convert(parsedSql,mdbSchema,mdb,null);

    // give every column a name
    int numColumns = esql.getColumnCount();
    String[] selectedColumnNames = new String[numColumns];
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

    // run the sql
    mdbtools.dbengine.Data result = sqlEngine.execute(esql);

    // return the results
    return new DataResultSet(result,selectedColumnNames,
        new ResultSetMetaData(selectedColumnNames),maxRows);
  }

  public int executeUpdate(String sql)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.executeUpdate not implemented");
  }

  public boolean isPoolable() {
    throw new RuntimeException("not implemented");
  }

  public void setPoolable(boolean poolable) { }

  public boolean isClosed() {
    throw new RuntimeException("not implemented");
  }

  public boolean isWrapperFor(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public Object unwrap(Class iface) {
    throw new RuntimeException("not implemented");
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
    return this.maxRows;
  }

  public void setMaxRows(int max)
      throws SQLException
  {
    this.maxRows = max;
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
    return null; //no warnings to return
//    throw new RuntimeException("MDBStatement.getWarnings not implemented");
  }

  public void clearWarnings()
      throws SQLException
  {
    //no problem "clearing" what we don't have!
//    throw new RuntimeException("MDBStatement.clearWarnings not implemented");
  }

  public void setCursorName(String name)
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.setCursorName not implemented");
  }

  public boolean execute(String sql)
      throws SQLException
  {
    savedResultSet = executeQuery(sql);
    return true;
//    throw new RuntimeException("MDBStatement.execute not implemented");
  }

  public ResultSet getResultSet()
      throws SQLException
  {
    return savedResultSet;
//    throw new RuntimeException("MDBStatement.getResultSet not implemented");
  }

  public int getUpdateCount()
      throws SQLException
  {
    /** @todo code a real implementation */
    // what should this do?  this has something to do with getMoreResults
    return -1;
//    throw new RuntimeException("MDBStatement.getUpdateCount not implemented");
  }

  public boolean getMoreResults()
      throws SQLException
  {
    /** @todo code a real implementation */
    // not entirely sure what it means to have mulitple results
    return false;
//    throw new RuntimeException("MDBStatement.getMoreResults not implemented");
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
    throw new RuntimeException("MDBStatement.execute not implemented");
  }

  public boolean execute(String sql, int columnIndexes[])
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.execute not implemented");
  }

  public boolean execute(String sql, String columnNames[])
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.execute not implemented");
  }

  public int getResultSetHoldability()
      throws SQLException
  {
    throw new RuntimeException("MDBStatement.getResultSetHoldability not implemented");
  }
}
