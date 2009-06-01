package mdbtools.dbengine;

/**
 * DataSource represents the data store.  The obtains all
 * data from the data source.
 */
public interface DataSource
{
  public int getTableCount();

  public Table getTable(int index);
}
