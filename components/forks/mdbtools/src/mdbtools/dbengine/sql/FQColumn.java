package mdbtools.dbengine.sql;

import mdbtools.dbengine.Table;

public class FQColumn
{
  private int table;
  private int column;

  public FQColumn(int table, int column)
  {
    this.table = table;
    this.column = column;
  }

  public boolean equals(Object o)
  {
    if (o instanceof FQColumn)
    {
      FQColumn fq = (FQColumn)o;
      return table == fq.table &&
             column == fq.column;
    }
    else
      return false;
  }

  public String toString()
  {
    return table + "." + column;
  }

  public String toString(Select sql)
  {
    Table t = sql.resolveTable(table);
    return t.getName() + "." + t.getColumnName(column);
  }

  public int getColumn()
  {
    return column;
  }

  public int getTable()
  {
    return table;
  }

  public void setColumn(int column)
  {
    this.column = column;
  }

  public void setTable(int table)
  {
    this.table = table;
  }
}
