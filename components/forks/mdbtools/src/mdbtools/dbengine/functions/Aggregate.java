package mdbtools.dbengine.functions;

import java.sql.SQLException;

public interface Aggregate
{
  public void execute(Object column)
    throws SQLException;

  public Object getResult();
}
