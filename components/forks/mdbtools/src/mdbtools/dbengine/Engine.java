package mdbtools.dbengine;

import mdbtools.dbengine.sql.Select;
import mdbtools.dbengine.sql.SQL;

import java.sql.SQLException;

/**
 * Engine is the entry point into a java based database backend
 * The datastore for the data is un-important since the engine communiates
 * with the data via a set of defined interfaces.
 * A sql parser is not included here instead the caller must parse the sql and
 * pass in a parse tree.
 *
 */
public class Engine
{
  public Engine()
  {
  }

  /**
   * execute is used to tell the engine to do something
   */
  public Data execute(SQL sql)
    throws SQLException
  {
    if (sql instanceof Select)
      return execute((Select)sql);
    return null;  // unknown query type
  }

  /**
   * Execute a sql select query
   * @param sql
   */
  private Data execute(Select select)
    throws SQLException
  {
    SelectEngine se = new SelectEngine(select);
    return se.execute();
  }
}
