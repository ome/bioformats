/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package mdbtools.dbengine;

import mdbtools.dbengine.functions.Aggregate;
import mdbtools.dbengine.functions.Function;
import mdbtools.dbengine.sql.Condition;
import mdbtools.dbengine.sql.Equation;
import mdbtools.dbengine.sql.FQColumn;
import mdbtools.dbengine.sql.FunctionDef;
import mdbtools.dbengine.sql.Join;
import mdbtools.dbengine.sql.OrderBy;
import mdbtools.dbengine.sql.Select;
import mdbtools.dbengine.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.SQLException;

/**
 * SelectEngine is able to execute a select query
 * TODO replace all runtime exceptions with sql exceptions
 */
public class SelectEngine
{
  private Select sql;  // the select statement to execute
  private int[] tableMap;  // maps tables / columns to the combined dataset
  private ArrayList tasks;  // tasks to run

  public SelectEngine(Select select)
  {
    this.sql = select;
    tasks = new ArrayList();
  }

  public Data execute()
   throws SQLException
  {
    Task task;

    // deal with the from clause
    task = fromClause();

    // deal with the where clause
    task = whereClause(task);

    // deal with the query itself
    if (isAggregate())
    {
      // sort by group by
      int groupByCount = sql.getGroupByCount();
      OrderBy[] sortBy;
      if (groupByCount != 0)
      {
        sortBy = new OrderBy[groupByCount];
        for (int i = 0; i < groupByCount; i++)
        {
          sortBy[i] = new OrderBy();
          sortBy[i].setSort((FQColumn)sql.getGroupBy(i));
          sortBy[i].setAscending(true);
        }
        task = new SimpleSort(task,sortBy,tableMap);
        tasks.add(task);
      }
      // run the aggregates
      task = new AggregateQuery(task,sql,tableMap);
      tasks.add(task);
      // do the order by
      int orderByCount = sql.getOrderByCount();
      if (orderByCount != 0)
      {
        sortBy = new OrderBy[orderByCount];
        for (int i = 0; i < orderByCount; i++)
        {
          OrderBy ob = sql.getOrderBy(i);
          // ensure only integer order by's
          if (ob.getSort() instanceof Integer == false)
            throw new SQLException("group by's can only sort on an index");
          sortBy[i] = new OrderBy();
          sortBy[i] = ob;
        }
        task = new SimpleSort(task,sortBy,tableMap);
        tasks.add(task);
      }
    }
    else
    {
      int num = sql.getOrderByCount();
      if (num == 0)
      {
        task = new NonAggregateQuery(task,sql,tableMap);
        tasks.add(task);
      }
      else
      {
        int numFQs = 0;
        int numColumns = 0;

        OrderBy[] fqs;
        OrderBy[] columns;

        // count how many of each
        for (int i = 0; i < num; i++)
        {
          Object o = sql.getOrderBy(i).getSort();
          if (o instanceof FQColumn)
            numFQs++;
          else if (o instanceof Integer)
            numColumns++;
          else
            throw new SQLException("can't sort this");
        }

        int index = 0;
        fqs = new OrderBy[numFQs];
        for (int i = 0; i < num; i++)
        {
          OrderBy o = sql.getOrderBy(i);
          if (o.getSort() instanceof FQColumn)
            fqs[index++] = o;
        }

        index = 0;
        columns = new OrderBy[numColumns];
        for (int i = 0; i < num; i++)
        {
          OrderBy o = sql.getOrderBy(i);
          if (o.getSort() instanceof Integer)
            columns[index++] = o;
        }

        // order by fq's
        if (numFQs != 0)
        {
          task = new SimpleSort(task,fqs,tableMap);
          tasks.add(task);
        }

        // run the query
        task = new NonAggregateQuery(task,sql,tableMap);
        tasks.add(task);

        // order by column
        if (numColumns != 0)
        {
          task = new SimpleSort(task,columns,tableMap);
          tasks.add(task);
        }
      }
    }

    // execute the query
    for (int i = 0; i < tasks.size(); i++)
      ((Task)tasks.get(i)).run();

    // return the result
    return (Data)task.getResult();

/*
    // convert the from clause into data
    Data data = getData();

    // execute the where clause
    data = where((MemoryData)data,sql.getWhere());

    // if aggregate exectute aggregate code
    Data result;
    if (isAggregate())
      result = aggregateQuery(data);
    else
      result = nonAggregateQuery(data); // not aggregate return all data

    // deal with order by
    result = orderBy(result);

//    ((MemoryData)result).dump();

    return result;
*/
  }

  private Task fromClause()
  {
    tableMap = new int[1];
    tableMap[0] = 0;

    Task current = null;
    // walk the from clause and load data for each table listed
    int numTables = sql.getTableCount();
    for (int i = 0; i < numTables; i++)
    {
      Table table = (Table)sql.getTable(i);
      LoadData ld = new LoadData(table);
      tasks.add(ld);
      if (current != null)
      {
        // merge with the previous result
        throw new RuntimeException("implement me");
      }
      else
        current = ld;
    }
    return current;
  }

  private Task whereClause(Task task)
  {
    Task current;

    Object where = sql.getWhere();
    if (where != null)
    {
      // get the data set from the from clause
      Data data = (Data)task.getResult();
      Task fd = new FilterData(task,where,tableMap);
      tasks.add(fd);
      current = fd;
    }
    else
      current = task;

    return current;
  }

  /**
   * An aggregate query is true if any function is an aggregate function
   * @return booolean
   */
  private boolean isAggregate()
  {
    int numColumns = sql.getColumnCount();
    for (int i = 0; i < numColumns; i++)
    {
      Object o = sql.getColumn(i);
      if (o instanceof FunctionDef && ((FunctionDef)o).getFunction() instanceof Aggregate)
        return true;
    }
    return false;
  }

  /**
   * execute an aggregate query
   * @return data
   */
  private Data aggregateQuery(Data data)
    throws SQLException
  {
    MemoryData result = new MemoryData();
    int numColumns = sql.getColumnCount();
    Object dummy = new Object();
    int groupByCount = sql.getGroupByCount();
    while (data.next())
    {
      if (groupByCount != 0)
      {
        // check if on next set
      }
      for (int i = 0; i < numColumns; i++)
      {
        resolveColumn(data,sql.getColumn(i));
      }
    }
    Object[] row = new Object[numColumns];
    for (int i = 0; i < numColumns; i++)
      row[i] = ((Aggregate)((FunctionDef)sql.getColumn(i)).getFunction()).getResult();
    result.addRow(row);
    return result;
  }

  /**
   * Resolve the from clause to a single set of data
   * @return the data
   */
  private Data getData()
    throws SQLException
  {
    // right now only one table is supported
    if (sql.getTableCount() == 0)
      throw new RuntimeException("must specify a table to select from");

    Data result;
    if (sql.getTableCount() == 1 && sql.getTable(0) instanceof Table)
    {
      // a single table - take the easy road
      Table table = (Table)sql.getTable(0);
      // map the table / columns to the combined dataset
      tableMap = new int[1];
      tableMap[0] = 0;

      // merge all the tables into a single data set
      result = new MemoryData(table.getData(),table.getColumnCount());
    }
    else
    {
      // multiple tables, merge them into a single dataset

      // count the number of tables involved
      int numTables = 0;
      for (int i = 0; i < sql.getTableCount(); i++)
      {
        Object table = sql.getTable(i);
        if (table instanceof Table)
          numTables++;
        else if (table instanceof Join)
        {
          Join join = (Join)table;

          // count the number of tables involved
          numTables++;  // at least one
          Object j = join;
          while (j instanceof Join)
          {
            j = ((Join)j).getRight();
            numTables++;
          }
        }
        else
          throw new RuntimeException("unknown table type");
      }

      // create an array of all the tables
      Table[] tables = new Table[numTables];
      int tableIndex = 0;
      for (int i = 0; i < sql.getTableCount(); i++)
      {
        Object table = sql.getTable(i);
        if (table instanceof Table)
          tables[tableIndex++] = (Table)table;
        else if (table instanceof Join)
        {
          Join join = (Join)table;

          // first left is always a table
          tables[tableIndex++] = (Table)join.getLeft();
          Object j = join;
          while (j instanceof Join)
          {
            j = ((Join)j).getRight();
            if (j instanceof Table)
              tables[tableIndex++] = (Table)j;
          }
        }
        else
          throw new RuntimeException("unknown table type");
      }

      // map the table / columns to the combined dataset
      tableMap = new int[numTables];
      int numColumns = 0;
      for (int i = 0; i < numTables; i++)
      {
        tableMap[i] = numColumns;
        numColumns += tables[i].getColumnCount();
      }

      //
      // merge all the tables into a single data set
      // merge each item down to data, then merge all data's
      // when a join is merged it's on clause will be executed as well
      //

      Data[] datas = new Data[sql.getTableCount()];
      int[] mdColumnCount = new int[datas.length];
      for (int i = 0; i < datas.length; i++)

      {
        Object table = sql.getTable(i);
        if (table instanceof Table)
        {
          // a table is already merged
          Table t = (Table)table;
          mdColumnCount[i] = t.getColumnCount();
          datas[i] = t.getData();
        }
        else if (table instanceof Join)
        {
          // merge a join down to a single data
          Join join = (Join)table;
          if (join.getLeft() instanceof Table && join.getRight() instanceof Table)
          {
            Table leftTable = (Table)join.getLeft();
            Table rightTable = (Table)join.getRight();
            int[] rwdcc = new int[2];
            rwdcc[0] = leftTable.getColumnCount();
            rwdcc[1] = rightTable.getColumnCount();
            RewindableData[] rwd = new RewindableData[2];
            rwd[0] = new RewindableData(leftTable.getData(),rwdcc[0]);
            rwd[1] = new RewindableData(rightTable.getData(),rwdcc[1]);
            rwd[0].dump();
            rwd[1].dump();
            result = new MemoryData();
            int cc = rwdcc[0] + rwdcc[1];
            mergeData((MemoryData)result,new Object[cc],0,rwd,rwdcc,0);
            ((MemoryData)result).dump();
            // apply the on clause of the join
            Equation eq = join.getEquation();
            // must massage the tableMap to account for a limited number of tables
            int leftTableIndex = findTableIndex(leftTable,tables);
            int rightTableIndex = findTableIndex(rightTable,tables);
            int[] tempTableMap = tableMap;
            tableMap = new int[tempTableMap.length];
            tableMap[leftTableIndex] = 0;
            tableMap[rightTableIndex] = rwdcc[0];
            result = where((MemoryData)result,eq);
            ((MemoryData)result).dump();
            tableMap = tempTableMap;
            datas[i] = result;
            mdColumnCount[i] = cc;
          }
          else
            throw new RuntimeException("not done");
        }
        else
          throw new RuntimeException("unknown table type");
      }

      // now simply merge the data's together
      if (datas.length == 1)
        result = datas[0];
      else
      {
        result = new MemoryData();
        RewindableData[] rd = new RewindableData[datas.length];
        for (int i = 0; i < datas.length; i++)
          rd[i] = new RewindableData(datas[i],mdColumnCount[i]);
        mergeData((MemoryData)result,new Object[numColumns],0,rd,mdColumnCount,0);
      }
      ((MemoryData)result).dump();
    }

    return result;
  }

  private int findTableIndex(Table t,Table[] tables)
  {
    for (int i = 0; i < tables.length; i++)
      if (t == tables[i])
        return i;
    throw new RuntimeException("table not found");
  }

  /**
   * merge the data from several datasets to a single dataset
   */
  private void mergeData(MemoryData memData,
                         Object[] _row,int columnIndex,
                         RewindableData[] datas,int[] datasColumnCount,
                         int dataIndex)
    throws SQLException
  {
    RewindableData data = datas[dataIndex];
    data.rewind();  // ensure always start at the begining
    int dataColumnCount = datasColumnCount[dataIndex];
    Object[] row = _row;
    while (data.next())
    {
      if (dataIndex + 1 == datas.length)
      {
        row = new Object[row.length];
        System.arraycopy(_row,0,row,0,columnIndex+1);
      }
      for (int i = 0; i < dataColumnCount; i++)
        row[columnIndex+i] = data.get(i);
      if (dataIndex + 1 < datas.length)
        mergeData(memData,
                  row,columnIndex+dataColumnCount,
                  datas,datasColumnCount,dataIndex+1);
      else
      {
        memData.addRow(row);
      }
    }
  }

  /**
   * non aggregate so simply return the column's wanted
   * running any function requested
   *
   * @return Data
   */
  private Data nonAggregateQuery(Data data)
    throws SQLException
  {
    MemoryData result = new MemoryData();
    int numColumns = sql.getColumnCount();
    while (data.next())
    {
      Object[] row = new Object[numColumns];
      for (int i = 0; i < numColumns; i++)
      {
        row[i] = resolveColumn(data,sql.getColumn(i));
      }
      result.addRow(row);
    }
    return result;
  }

  /**
   * resolve a column down to it's final value
   * @param data the value from the data set
   * @param column the object from the sql query
   * @return Object
   */
  private Object resolveColumn(Data data,Object column)
    throws SQLException
  {
    if (column instanceof FQColumn)
    {
      FQColumn c = (FQColumn)column;
      return data.get(tableMap[c.getTable()]+c.getColumn());
    }
    else if (column instanceof FunctionDef)
    {
      FunctionDef fdef = (FunctionDef)column;
      Function f = (Function)fdef.getFunction();
      Object argument = fdef.getArgument();
      return f.execute(resolveColumn(data,argument));
    }
    else
      return column;  // static value
  }

  /**
   * sort the data
   * todo: allow for sorting non-memory data
   * @param data
   * @return Data
   */
  private Data orderBy(Data data)
  {
    int orderByCount = sql.getOrderByCount();

    if (orderByCount == 0)
      return data;

//    ((MemoryData)data).sort(sql);
    return data;
  }

  /**
   * execute the where clause
   */
  private Data where(MemoryData data,Object where)
    throws SQLException
  {
    if (where == null)
      return data;

    MemoryData result = new MemoryData();
    for (int i = 0; data.next(); i++)
    {
      if (whereCheckRow(data,where))
        result.addRow(data.getRow(i));
    }
    return result;
  }

  /**
   * Check the where constraint against a single row
   * @param data
   * @param where
   * @return boolean
   */
  private boolean whereCheckRow(Data data,Object where)
    throws SQLException
  {
    if (where instanceof Equation)
      return whereCheckRowEquation(data,(Equation)where);
    if (where instanceof Condition)
      return whereCheckRowConditon(data,(Condition)where);
    else
      throw new RuntimeException("uknown where type: " + where.getClass().getName());
  }

  /**
   * Check an equation portion of the where contraints against a single row
   * @param data
   * @param eq
   * @return boolean
   */
  private boolean whereCheckRowEquation(Data data,Equation eq)
    throws SQLException
  {
    Object left = resolveColumn(data,eq.getLeft());
    Object right = resolveColumn(data,eq.getRight());
    switch(eq.getOperator())
    {
      case Equation.EQUALS:
        return left.equals(right);
      case Equation.NOT_EQUALS:
        return left.equals(right) == false;
      case Equation.LESS_THAN:
//        return ColumnComparator.compare(left,right) < 0;
      case Equation.GREATER_THAN:
//        return ColumnComparator.compare(left,right) > 0;
      default:
        throw new RuntimeException("unknown equation operator: " + eq.getOperator());
    }
  }

  /**
   * Check a condtion portion of the where constraint against a single row
   * left and right of conditon is either another condition or an equation
   * @param data
   * @param condition
   * @return boolean
   */
  private boolean whereCheckRowConditon(Data data,Condition condition)
    throws SQLException
  {
    Object left = condition.getLeft();
    Object right = condition.getRight();
    switch(condition.getOperator())
    {
      case Condition.AND:
        return whereCheckRow(data,left) && whereCheckRow(data,right);
      case Condition.OR:
        return whereCheckRow(data,left) || whereCheckRow(data,right);
      default:
        throw new RuntimeException("unknown condition operator: " + condition.getOperator());
    }
  }
}

