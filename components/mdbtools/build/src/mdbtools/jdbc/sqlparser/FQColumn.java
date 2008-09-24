package mdbtools.jdbc.sqlparser;

public class FQColumn
{
  private String tableName;
  private String columnName;

  public String toString()
  {
    return tableName + '.' + columnName;
  }

  public boolean equals(Object o)
  {
    if (o instanceof FQColumn)
    {
      FQColumn fq = (FQColumn)o;
      return tableName.equals(fq.tableName) &&
             columnName.equals(fq.columnName);
    }
    else
      return false;
  }

  public String getColumnName()
  {
    return columnName;
  }

  public String getTableName()
  {
    return tableName;
  }

  public void setColumnName(String columnName)
  {
    this.columnName = columnName;
  }

  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }
}
