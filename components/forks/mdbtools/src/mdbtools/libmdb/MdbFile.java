package mdbtools.libmdb;

import mdbtools.publicapi.RandomAccess;

import java.io.IOException;

public class MdbFile
{
  public RandomAccess fd;
  public boolean writable;
  public String filename;
  int jet_version;
  int db_key;
  public char[] db_passwd = new char[14];
  public MdbBackend default_backend;
  public String backend_name;
//  MdbStatistics	stats;
  /* free map */
  public int  map_sz;
//  unsigned char *free_map;
  /* reference count */
  public int refs;

  public void close() {
    try {
      fd.close();
    }
    catch (IOException e) { }
  }
}
