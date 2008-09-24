package mdbtools.dbengine.sql;

public class OrderBy
{
  Object sort;
  boolean ascending;

  public String toString(Select select)
  {
    return Util.toString(select,sort) + (ascending?" asc":" desc");
  }

  public boolean isAscending() {
    return ascending;
  }
  public Object getSort() {
    return sort;
  }
  public void setAscending(boolean ascending) {
    this.ascending = ascending;
  }
  public void setSort(Object sort) {
    this.sort = sort;
  }
}
