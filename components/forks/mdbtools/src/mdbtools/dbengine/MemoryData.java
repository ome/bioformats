/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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

import java.util.ArrayList;

import java.sql.SQLException;

/**
 * An implementation of Data that stores the complete result in memory
 */
public class MemoryData implements Data
{
  protected int currentRow = -1;
  private ArrayList rows = new ArrayList();

  public MemoryData()
  {
  }

  public MemoryData(Data data,int numColumns)
    throws SQLException
  {
    while (data.next())
    {
      Object[] row = new Object[numColumns];
      for (int i = 0; i < numColumns; i++)
        row[i] = data.get(i);
      rows.add(row);
    }
  }

  /**
   * goto the next (or first) row
   * @return true if row exits, false if not
   */
  public boolean next()
    throws SQLException
  {
    if (currentRow+1 < rows.size())
    {
      currentRow++;
      return true;
    }
    return false;
  }

  /**
   * get the data at a certain column
   * @param index the column to get
   */
  public Object get(int index)
  {
    Object[] row = (Object[])rows.get(currentRow);
    return row[index];
  }

  public Object[] getRow(int index)
  {
    return (Object[])rows.get(index);
  }

  /**
   * Add a row of data
   */
  public void addRow(Object[] row)
  {
    if (currentRow != -1)
      throw new RuntimeException("can't add a row after the data has been read");
    rows.add(row);
  }

  /**
   * sort the data set by the given columns
   * each column is sorted seperate
   */
  public void sort(int[] sortBy,boolean[] ascending)
    throws SQLException
  {
    if (rows.size() == 0)
      return;  // an empty list is sorted

    int numColumns = ((Object[])rows.get(0)).length;
    for (int i = 0; i < sortBy.length; i++)
      if (sortBy[i] >= numColumns)
        throw new SQLException("can't sort by a column that does not exist");

    int columnToSort;
    boolean asc;
    int lastColumnSorted;

    columnToSort = sortBy[0];
    asc = ascending[0];
    sort(columnToSort,0,rows.size()-1,asc);
//dump();
    lastColumnSorted = columnToSort;
    for (int i = 1;i < sortBy.length; i++)
    {
      columnToSort = sortBy[i];
      asc = ascending[i];
      int start = 0;
      int end = findDifferent(lastColumnSorted,start)-1;
      while (end != -2)
      {
        sort(columnToSort,start,end,asc);
//dump();
        start = end+1;
        end = findDifferent(lastColumnSorted,start)-1;
      }
      sort(columnToSort,start,rows.size()-1,asc);
//dump();
    }
  }

  // find the index of the row that containes a different value
  private int findDifferent(int column,int start)
  {
    Object value = ((Object[])rows.get(start))[column];
    for (int i = start+1;i < rows.size(); i++)
    {
      Object o = ((Object[])rows.get(i))[column];
      if (value.equals(o) == false)
        return i;
    }
    return -1;  // not found
  }

  /**
   * sort the data by the given column using only the given rows
   * insertion sort is used for the sort
   * @todo replace with a better sorting algorithm
   * @param columnToSort the column to sort
   * @param startAt the row to start sorting at
   * @param endAt the row to end sorting at
   * @param ascending true for ascending order, false for descending order
   */
  private void sort(int columnToSort,int startAt,int endAt,boolean ascending)
  {
    if (startAt == endAt)
      return; // a single row is sorted

    int size = endAt - startAt + 1;
    // loop invariant applies here
    for (int i = 1, j = startAt+1; i < size; j++,i++)
    {
      Object[] row = (Object[])rows.get(j);
      int a = j - 1;
      while ( (a > (startAt-1)) && (
        (ascending == true &&
          ((Comparable)row[columnToSort]).compareTo(
          ((Comparable)((Object[])rows.get(a))[columnToSort])) < 0) ||
        (ascending == false &&
          ((Comparable)row[columnToSort]).compareTo(
          ((Comparable)((Object[])rows.get(a))[columnToSort])) > 0) ))
      {
        rows.set(a+1,rows.get(a));
        a = a - 1;
      }
      rows.set(a+1,row);
    }
  }

  // Dump the data
  public void dump()
  {
    System.out.println("+++++++++++");
    for (int i = 0; i < rows.size();i++)
    {
      Object[] row = (Object[])rows.get(i);
      for (int j = 0;j < row.length; j++)
      {
        if (j != 0)
          System.out.print(',');
        System.out.print(row[j].toString());
      }
      System.out.println("");
    }
  }
}
