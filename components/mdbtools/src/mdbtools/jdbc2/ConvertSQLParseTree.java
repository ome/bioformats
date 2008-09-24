package mdbtools.jdbc2;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * convert a parsed sql parse tree to an executable sql tree
 */
public class ConvertSQLParseTree
{
  // parmaters are paramterholders so the value can be changed
  public static mdbtools.dbengine.sql.Select convert(mdbtools.jdbc.sqlparser.Select parsedSql,
                                                     MDBSchema mdbSchema,
                                                     mdbtools.libmdb.MdbHandle mdb,
                                                     ArrayList paramters)
    throws SQLException
  {
    mdbtools.dbengine.sql.Select esql = new mdbtools.dbengine.sql.Select();
    // convert the columns
    int numColumns = parsedSql.getColumnCount();
//    String[] selectedColumnNames = null;
    Object column = parsedSql.getColumnAt(0);
    if (numColumns == 1 &&
       ((column instanceof String && ((String)column).equals("*")) ||
       (column instanceof mdbtools.jdbc.sqlparser.FQColumn &&
       ((mdbtools.jdbc.sqlparser.FQColumn)column).getColumnName().equals("*"))))
    {
      // deal with a select of all columns
      if (parsedSql.getTableCount() != 1)
        throw new SQLException("found multiple tables on a select *");
      String parsedTableName;
      Object _table = parsedSql.getTableAt(0);
      if (_table instanceof mdbtools.jdbc.sqlparser.Alias)
      {
        mdbtools.jdbc.sqlparser.Alias alias = (mdbtools.jdbc.sqlparser.Alias)_table;
        if (column instanceof mdbtools.jdbc.sqlparser.FQColumn &&
           ((mdbtools.jdbc.sqlparser.FQColumn)column).getTableName().equalsIgnoreCase(alias.getAlias()) == false)
          throw new SQLException("table for table.* not specified in where clause");
        _table = alias.getSubject();
        parsedTableName = (String)_table;
      }
      else
      {
        // at this point parsedTableName should be a string
        parsedTableName = (String)_table;
        if (column instanceof mdbtools.jdbc.sqlparser.FQColumn &&
           ((mdbtools.jdbc.sqlparser.FQColumn)column).getTableName().equalsIgnoreCase(parsedTableName) == false)
          throw new SQLException("table for table.* not specified in where clause");
      }
      ArrayList columns = mdbSchema.getColumns(parsedTableName);
//      selectedColumnNames = new String[columns.size()];
      for (int j = 0;j < columns.size(); j++)
      {
        mdbtools.dbengine.sql.FQColumn realColumn =
            new mdbtools.dbengine.sql.FQColumn(0,j);
        esql.addColumn(realColumn);
//        selectedColumnNames[j] = (String)columns.get(j);
      }
    }
    else
    {
      for (int i = 0; i < numColumns; i++)
      {
        column = parsedSql.getColumnAt(i);
        Object realColumn = convertColumn(column,parsedSql,mdbSchema,paramters);
        esql.addColumn(realColumn);
      }
    }

    // convert the tables
    int numTables = parsedSql.getTableCount();
    for (int i = 0; i < numTables; i++)
    {
      esql.addTable(convertTable(parsedSql.getTableAt(i),mdb,mdbSchema));
    }

    // convert the where
    esql.setWhere(convertWhere(parsedSql.getWhere(),parsedSql,mdbSchema,paramters));

    return esql;
  }

  private static Object convertColumn(Object parsedColumn,
                                      mdbtools.jdbc.sqlparser.Select parsedSql,
                                      MDBSchema mdbSchema,
                                      ArrayList paramters)
    throws SQLException
  {
    if (parsedColumn instanceof mdbtools.jdbc.sqlparser.FQColumn)
      return convertColumn_FQColumn((mdbtools.jdbc.sqlparser.FQColumn)parsedColumn,parsedSql,mdbSchema);
    else if (parsedColumn instanceof mdbtools.jdbc.sqlparser.Function)
      return convertColumn_Function((mdbtools.jdbc.sqlparser.Function)parsedColumn);
    else if (parsedColumn instanceof String)
      return convertColumn_String((String)parsedColumn,parsedSql,mdbSchema);
    else if (parsedColumn instanceof mdbtools.jdbc.sqlparser.QuotedString)
      return convertColumn_QuotedString((mdbtools.jdbc.sqlparser.QuotedString)parsedColumn);
    else if (parsedColumn instanceof Integer)
      return parsedColumn;
    else if (parsedColumn instanceof mdbtools.jdbc.sqlparser.Parameter)
    {
      ParameterHolder ph = new ParameterHolder();
      paramters.add(ph);
      return ph;
    }
    else
      throw new SQLException("unknown column type: " + parsedColumn.getClass().getName());
  }

  private static Object convertColumn_QuotedString(mdbtools.jdbc.sqlparser.QuotedString parsedColumn)
  {
    return parsedColumn.getSubject();
  }

  private static Object convertColumn_FQColumn(mdbtools.jdbc.sqlparser.FQColumn parsedColumn,
                                               mdbtools.jdbc.sqlparser.Select parsedSql,
                                               MDBSchema mdbSchema)
    throws SQLException
  {
    // find the table
    String parsedTableName = parsedColumn.getTableName();
    int tableNum = -1;
    for (int j = 0; j < parsedSql.getTableCount() && tableNum == -1; j++)
    {
      String tname = convertTableName(parsedSql.getTableAt(j));
      if (tname.equalsIgnoreCase(parsedTableName))
        tableNum = j;
    }
    if (tableNum == -1)
      throw new SQLException("can't find the table");
    // find the column in the table
    String parsedColumnName = parsedColumn.getColumnName();
    int colNum = -1;
    ArrayList columns = mdbSchema.getColumns(convertTableRealName(parsedSql.getTableAt(tableNum)));
    for (int j = 0;j < columns.size() && colNum == -1; j++)
    {
      String cname = (String)columns.get(j);
      if (parsedColumnName.equalsIgnoreCase(cname))
        colNum = j;
    }
    if (colNum == -1)
      throw new SQLException("invalid column: " + parsedColumn.toString());
    mdbtools.dbengine.sql.FQColumn realColumn =
        new mdbtools.dbengine.sql.FQColumn(tableNum,colNum);
    return realColumn;
  }

  private static Object convertColumn_Function(mdbtools.jdbc.sqlparser.Function parsedColumn)
    throws SQLException
  {
    mdbtools.jdbc.sqlparser.Function f = (mdbtools.jdbc.sqlparser.Function)parsedColumn;
    String functionName = f.getName();
    if (functionName.equalsIgnoreCase("count"))
    {
      mdbtools.dbengine.sql.FunctionDef fd =
          new mdbtools.dbengine.sql.FunctionDef();
      fd.setFunction(new mdbtools.dbengine.functions.Count());
      fd.setArgument(new Integer(1));  // count(*)
      /** @todo implement select count(column) */
      return fd;
    }
    else
      throw new SQLException("unknown function: " + functionName);
  }

  private static Object convertColumn_String(String parsedColumn,
                                             mdbtools.jdbc.sqlparser.Select parsedSql,
                                             MDBSchema mdbSchema)
    throws SQLException
  {
    String s = (String)parsedColumn;
    // find the table with this column name
    int tableNum = -1;
    int colNum = -1;
    for (int j = 0; j < parsedSql.getTableCount(); j++)
    {
      String tname = convertTableName(parsedSql.getTableAt(j));
      ArrayList columns = mdbSchema.getColumns(convertTableRealName(parsedSql.getTableAt(j)));
      for (int k = 0;k < columns.size(); k++)
      {
        String cname = (String)columns.get(k);
        if (cname.equalsIgnoreCase(s))
        {
          if (tableNum != -1)
            throw new SQLException("ambiquis column: " + s);
          // found it
          tableNum = j;
          colNum = k;
        }
      }
    }
    if (colNum == -1)
      throw new SQLException("invalid column: " + s);
    mdbtools.dbengine.sql.FQColumn realColumn =
        new mdbtools.dbengine.sql.FQColumn(tableNum,colNum);
    return realColumn;
  }

  private static Object convertWhere(Object parsedWhere,
                                     mdbtools.jdbc.sqlparser.Select parsedSql,
                                     MDBSchema mdbSchema,
                                     ArrayList paramters)
    throws SQLException
  {
    if (parsedWhere == null)
      return null;

    if (parsedWhere instanceof mdbtools.jdbc.sqlparser.Equation)
      return convertWhereEquation((mdbtools.jdbc.sqlparser.Equation)parsedWhere,
                                  parsedSql,mdbSchema,paramters);
    if (parsedWhere instanceof mdbtools.jdbc.sqlparser.Condition)
      return convertWhereCondition((mdbtools.jdbc.sqlparser.Condition)parsedWhere,
                                  parsedSql,mdbSchema,paramters);
    else
      throw new SQLException("unknown where: " + parsedWhere.getClass().getName());
  }

  private static Object convertWhereCondition(mdbtools.jdbc.sqlparser.Condition parsedCondition,
                                             mdbtools.jdbc.sqlparser.Select parsedSql,
                                             MDBSchema mdbSchema,
                                             ArrayList paramters)
    throws SQLException
  {
    mdbtools.dbengine.sql.Condition real = new mdbtools.dbengine.sql.Condition();
    real.setLeft(convertWhere(parsedCondition.getLeft(),parsedSql,mdbSchema,paramters));
    int operator = parsedCondition.getOperator();
    if (operator == mdbtools.jdbc.sqlparser.Condition.AND)
      real.setOperator(mdbtools.dbengine.sql.Condition.AND);
    else if (operator == mdbtools.jdbc.sqlparser.Condition.OR)
      real.setOperator(mdbtools.dbengine.sql.Condition.OR);
    else
      throw new SQLException("unknown operator");
    real.setRight(convertWhere(parsedCondition.getRight(),parsedSql,mdbSchema,paramters));
    return real;
  }

  private static Object convertWhereEquation(mdbtools.jdbc.sqlparser.Equation parsedEquation,
                                             mdbtools.jdbc.sqlparser.Select parsedSql,
                                             MDBSchema mdbSchema,
                                             ArrayList paramters)
    throws SQLException
  {
    mdbtools.dbengine.sql.Equation eq = new mdbtools.dbengine.sql.Equation();
    eq.setLeft(convertColumn(parsedEquation.getLeft(),parsedSql,mdbSchema,paramters));
    int parsedOperator = parsedEquation.getOperator();
    int eOperator;
    if (parsedOperator == mdbtools.jdbc.sqlparser.Equation.EQUALS)
      eOperator = mdbtools.dbengine.sql.Equation.EQUALS;
    else if (parsedOperator == mdbtools.jdbc.sqlparser.Equation.GREATER_THAN)
      eOperator = mdbtools.dbengine.sql.Equation.GREATER_THAN;
    else if (parsedOperator == mdbtools.jdbc.sqlparser.Equation.GREATER_THAN_OR_EQUALS)
      eOperator = mdbtools.dbengine.sql.Equation.GREATER_THAN_OR_EQUALS;
    else if (parsedOperator == mdbtools.jdbc.sqlparser.Equation.LESS_THAN)
      eOperator = mdbtools.dbengine.sql.Equation.LESS_THAN;
    else if (parsedOperator == mdbtools.jdbc.sqlparser.Equation.LESS_THAN_OR_EQUALS)
      eOperator = mdbtools.dbengine.sql.Equation.LESS_THAN_OR_EQUALS;
    else if (parsedOperator == mdbtools.jdbc.sqlparser.Equation.NOT_EQUALS)
      eOperator = mdbtools.dbengine.sql.Equation.NOT_EQUALS;
    else
      throw new SQLException("unknown operator");
    eq.setOperator(eOperator);
    eq.setRight(convertColumn(parsedEquation.getRight(),parsedSql,mdbSchema,paramters));
    return eq;
  }

  /** returns the actual table data */
  private static MDBTable convertTable(Object o,mdbtools.libmdb.MdbHandle mdb,
                                     MDBSchema mdbSchema)
    throws SQLException
  {
    String tableName = convertTableRealName(o);
    return new MDBTable(tableName,mdb,mdbSchema);
  }

  /** returns the name of the data as known by the rest of the sql */
  private static String convertTableName(Object o)
    throws SQLException
  {
    if (o instanceof String)
      return (String)o;
    else if (o instanceof mdbtools.jdbc.sqlparser.Alias)
      return ((mdbtools.jdbc.sqlparser.Alias)o).getAlias();
    else
      throw new SQLException("unknown table type: " + o.getClass().getName());
  }

  /** returns the name of the actual table */
  private static String convertTableRealName(Object o)
    throws SQLException
  {
    if (o instanceof String)
      return (String)o;
    else if (o instanceof mdbtools.jdbc.sqlparser.Alias)
      return (String)((mdbtools.jdbc.sqlparser.Alias)o).getSubject();
    else
      throw new SQLException("unknown table type: " + o.getClass().getName());
  }
}
