package mdbtools.jdbc;

import mdbtools.libmdb.*;
import mdbtools.jdbc.sqlparser.Alias;
import mdbtools.jdbc.sqlparser.Equation;
import mdbtools.jdbc.sqlparser.FQColumn;
import mdbtools.jdbc.sqlparser.Function;
import mdbtools.jdbc.sqlparser.Parser;
import mdbtools.jdbc.sqlparser.Select;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

// this is the sql execution engine
class Engine
{
  private MdbHandle mdb;

  Engine(MdbHandle mdb)
  {
    this.mdb = mdb;
    // cache common info
  }

  public MDBResultSet execute(String sql)
    throws IOException,IllegalAccessException, InstantiationException,
           SQLException
  {
    Parser parser = new Parser(sql);
    Select select = (Select)parser.parse();
    // figure out what to do with this
    MDBResultSet result;
    if ((result = aggregate(select)) != null)
      return result;
    // catch all
    return executeSelect(select);
  }

  private MDBResultSet executeSelect(Select select)
    throws IOException, SQLException
  {
    if (select.getTableCount() != 1)
      throw new RuntimeException("only one table allowed");
    Object sqlTable = select.getTableAt(0);
    String tablename;
    if (sqlTable instanceof String)
      tablename = (String)sqlTable;
    else if (sqlTable instanceof Alias)
      tablename = (String)((Alias)sqlTable).getSubject();
    else
      throw new RuntimeException("can't find a table");
    int numColumns = select.getColumnCount();
    // open up the requested table for reading
    MdbTableDef table;
    MdbCatalogEntry entry = null;
    boolean found = false;
    for (int i = 0;i < mdb.num_catalog && found == false;i++)
    {
      entry = (MdbCatalogEntry)mdb.catalog.get(i);
      if (entry.object_type == Constants.MDB_TABLE && entry.object_name.equals(tablename))
        found = true;
    }
    if (found == false)
      throw new RuntimeException("table does not exist in the database");

    table = Table.mdb_read_table(entry);
    Table.mdb_read_columns(table);
    Data.mdb_rewind_table(table);

    Holder[] bound_columns = new Holder[table.num_cols];
    for (int i = 0; i < table.num_cols;i++)
    {
      bound_columns[i] = new Holder();
      Data.mdb_bind_column(table, i + 1, bound_columns[i]);
    }

    boolean allColumns = false;
    if (select.getColumnCount() == 1)
    {
      Object o = select.getColumnAt(0);
      allColumns =
          ((o instanceof FQColumn) && ((FQColumn)o).getColumnName().equals("*")) ||
          ((o instanceof String) && ((String)o).equals("*"));
    }

    ArrayList rows = new ArrayList();
    while(Data.mdb_fetch_row(table))
      if (executeWhere(select.getWhere(),bound_columns,table))
    {
      String[] row;
      if (allColumns)
        row = new String[table.num_cols];
      else
        row = new String[numColumns];
      rows.add(row);
      for (int i = 0; i < row.length; i++)
      {
        if (allColumns == false)
        {
          Object cn = select.getColumnAt(i);
          String columnName;
          if (cn instanceof String)
            columnName = (String)cn;
          else
            columnName = ((FQColumn)cn).getColumnName();
          for (int j = 0;j < table.num_cols;j++)
          {
            MdbColumn col = (MdbColumn)table.columns.get(j);
            if (columnName.equals(col.name))
            {
              row[i] = bound_columns[j].s;
            }
          }
        }
        else
        {
          row[i] = bound_columns[i].s;
        }
      }
    }
    String[] columnNames;
    if (allColumns == false)
    {
      columnNames = new String[numColumns];
      for (int i = 0; i < numColumns; i++)
      {
        columnNames[i] = select.getColumnAt(i).toString();
      }
    }
    else
    {
      columnNames = new String[table.num_cols];
      for (int i = 0; i < table.num_cols; i++)
      {
        columnNames[i] = ((MdbColumn)table.columns.get(i)).name;
      }
    }
    // return the result set
    return new MDBResultSetFromArrayList(columnNames,rows);
  }

  // handle select count(*) from table
  // handle select min(id) from table
  private MDBResultSet aggregate(Select select)
    throws IOException,IllegalAccessException, InstantiationException,
           SQLException
  {
    int function;
    if (select.getTableCount() == 1 &&
        select.getColumnCount() == 1 &&
        select.getColumnAt(0) instanceof Function &&
        ( function = getAggregate(((Function)select.getColumnAt(0)).getName()))
                     != AGGREGATE_UNKNOWN)
    {
      // get the table name
      Object sqlTable = select.getTableAt(0);
      String tablename;
      if (sqlTable instanceof String)
        tablename = (String)sqlTable;
      else if (sqlTable instanceof Alias)
        tablename = (String)((Alias)sqlTable).getSubject();
      else
        throw new RuntimeException("can't find a table");
      // result, the single value to return as the result
      MdbTableDef table;
      MdbCatalogEntry entry = null;
      boolean found = false;
      for (int i = 0;i < mdb.num_catalog && found == false;i++)
      {
        entry = (MdbCatalogEntry)mdb.catalog.get(i);
        if (entry.object_type == Constants.MDB_TABLE && entry.object_name.equals(tablename))
          found = true;
      }
      if (found == false)
        throw new RuntimeException("table does not exist in the database");

      table = Table.mdb_read_table(entry);
      Table.mdb_read_columns(table);
      Data.mdb_rewind_table(table);

      Holder[] bound_columns = new Holder[table.num_cols];
      for (int i = 0; i < table.num_cols;i++)
      {
        bound_columns[i] = new Holder();
        Data.mdb_bind_column(table, i + 1, bound_columns[i]);
      }
      FunctionExecutor executor = (FunctionExecutor)executors[function].newInstance();
      executor.init(table,(Function)select.getColumnAt(0),bound_columns);
      while(Data.mdb_fetch_row(table))
        if (executeWhere(select.getWhere(),bound_columns,table))
          executor.executeRow();
      ArrayList rows = new ArrayList();
      Object[] row = new Object[1];
      row[0] = executor.toResult();
      rows.add(row);
      // meta data info
      Function countFunction = (Function)select.getColumnAt(0);
      String[] columnNames;
      columnNames = new String[1];
      columnNames[0] = countFunction.toString();
      // return the result set
      return new MDBResultSetFromArrayList(columnNames,rows);
    }
    else
      return null;  // not a count statement
  }

  // look at the function name and return which aggregate function it it

  private int getAggregate(String name)
  {
    for (int i = 0; i < aggregateNames.length; i++)
      if (name.equalsIgnoreCase(aggregateNames[i]))
        return i;
    return AGGREGATE_UNKNOWN;
  }

  // list of all aggregates
  private static final String[] aggregateNames =
      {
        "count",
        "min",
        "max"
      };

  // the executors for the aggregates
  private static final Class[] executors =
    {
      mdbtools.jdbc.functions.Count.class,
      mdbtools.jdbc.functions.Min.class,
      mdbtools.jdbc.functions.Max.class
    };
  private static final int AGGREGATE_UNKNOWN = -1;

  private boolean executeWhere(Object where,Holder[] bound_columns,
                                      MdbTableDef table)
    throws SQLException
  {
    if (where == null)
      return true; // no "where" clause means all rows match

    if ((where instanceof Equation) == false)
      throw new SQLException("only equation is implemented");

    Equation eq = (Equation)where;
    String columnName = (String)eq.getLeft();
    int fieldNum = -1;
    for (int i = 0;i < table.num_cols;i++)
    {
      MdbColumn col = (MdbColumn)table.columns.get(i);
      if (columnName.equalsIgnoreCase(col.name))
      {
        fieldNum = i;
        break;
      }
    }
    String value = bound_columns[fieldNum].s;
    if (value == null)
      return false;
    boolean result = ((Integer)eq.getRight()).intValue() == Integer.parseInt(value);
    return result;
  }
}
