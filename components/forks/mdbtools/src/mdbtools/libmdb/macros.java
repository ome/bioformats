package mdbtools.libmdb;

// these are the macros that were originally defines
public class macros
{
  private static final int MDB_VER_JET3 = 0;
  private static final int MDB_VER_JET4 = 1;

  public static boolean IS_JET4(MdbHandle mdb)
  {
    return (mdb.f.jet_version == MDB_VER_JET4);
  }

  public static boolean IS_JET3(MdbHandle mdb)
  {
    return (mdb.f.jet_version == MDB_VER_JET3);
  }
}
