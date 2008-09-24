package mdbtools.jdbc.sqlparser;

import java.util.ArrayList;

// a parsed out select statement
public class Select
{
  ArrayList columns = new ArrayList();
  ArrayList tables = new ArrayList();
  Object where;
  OrderBy orderBy;

  public Select()
  {
  }

  public boolean equals(Object o)
  {
    if (o instanceof Select)
    {
      Select select = (Select)o;
      return columns.equals(select.columns) &&
             tables.equals(select.tables) &&
             ((where == null && select.where == null) ||
               where.equals(select.where)) &&
             ((orderBy == null && select.orderBy == null) ||
               orderBy.equals(orderBy));
    }
    else
      return false;
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    for (int i = 0; i < columns.size(); i++)
    {
      if (i != 0)
        sb.append(',');
      Object o = columns.get(i);
      sb.append((String)o);
    }
    sb.append(" FROM ");
    for (int i = 0; i < tables.size(); i++)
    {
      if (i != 0)
        sb.append(',');
      Object o = tables.get(i);
      sb.append(o.toString());
    }
    return sb.toString();
  }

  public int getColumnCount()
  {
    return columns.size();
  }

  public Object getColumnAt(int i)
  {
    return columns.get(i);
  }

  public int getTableCount()
  {
    return tables.size();
  }

  public Object getTableAt(int i)
  {
    return tables.get(i);
  }

  void addTable(Object s)
  {
    tables.add(s);
  }

  public void setWhere(Object where)
  {
    this.where = where;
  }

  public Object getWhere()
  {
    return where;
  }
}
