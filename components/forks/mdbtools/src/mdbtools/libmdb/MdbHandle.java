package mdbtools.libmdb;

import java.util.ArrayList;

public class MdbHandle
{
  public static final int MDB_PGSIZE = 4096;

  public MdbFile f;
  public short cur_pg;
  public int row_num;
  public long  cur_pos;
  public byte[] pg_buf = new byte[MDB_PGSIZE];
  public byte[] alt_pg_buf = new byte[MDB_PGSIZE];
  public int num_catalog;
  public ArrayList catalog;
  public MdbBackend default_backend;
  public String backend_name;
  public MdbFormatConstants fmt;
  public MdbStatistics stats;

  public void close() {
    f.close();
  }
}
