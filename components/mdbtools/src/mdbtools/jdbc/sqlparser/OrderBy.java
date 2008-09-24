package mdbtools.jdbc.sqlparser;

import java.util.ArrayList;

public class OrderBy
{
  public static final int NONE = 0;
  public static final int ASC = 1;
  public static final int DESC = 2;

  private ArrayList columns = new ArrayList();
  private int type;

  void add(String name)
  {
    columns.add(name);
  }

  void setType(int type)
  {
    this.type = type;
  }
}
