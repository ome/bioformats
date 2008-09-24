package mdbtools.libmdb;

import java.util.HashMap;

public class backend
{
  public static HashMap mdb_backends;
  private static int did_first;

  public static void mdb_init_backends()
  {
    MdbBackend backend;

    mdb_backends = new HashMap();

    MdbBackend a_backend = new MdbBackend();
    a_backend.types_table = mdb_access_types;
    mdb_register_backend(a_backend, "access");

//    MdbBackend s_backend = new MdbBackend();
//    s_backend.types_table = mdb_sybase_types;
//    mdb_register_backend(s_backend, "sybase");
/*
if (((MdbBackend)mdb_backends.get("access")) == a_backend)
  System.out.println("true");
else
  System.out.println("false");

if (((MdbBackend)mdb_backends.get("access")).types_table == a_backend.types_table)
  System.out.println("true");
else
  System.out.println("false");

if (((MdbBackend)mdb_backends.get("access")).types_table == mdb_access_types)
  System.out.println("true");
else
  System.out.println("false");
*/
//    backend = new MdbBackend();
//    backend.types_table = mdb_oracle_types;
//    mdb_register_backend(backend, "oracle");
//
//    backend = new MdbBackend();
//    backend.types_table = mdb_postgres_types;
//    mdb_register_backend(backend, "postgres");
  }

  private static void mdb_register_backend(MdbBackend backend, String backend_name)
  {
    mdb_backends.put(backend_name,backend);
  }

  public static int mdb_set_default_backend(MdbHandle mdb, String backend_name)
  {
    MdbBackend backend;

    backend = (MdbBackend)mdb_backends.get(backend_name);
    if (backend == null)
    {
      mdb.default_backend = backend;
      mdb.backend_name = backend_name;
      did_first = 0;
      return 1;
    }
    else
    {
      return 0;
    }
  }

  public static String mdb_get_coltype_string(MdbBackend backend, int col_type)
  {
    if (col_type > 0x10)
    {
      return "type " + col_type;
    }
    else
    {
      return backend.types_table[col_type];
    }
  }

   /*  Access data types */
  public static final String[] mdb_access_types = new String[]
	{"Unknown 0x00",
         "Boolean",
         "Byte",
         "Integer",
         "Long Integer",
         "Currency",
         "Single",
         "Double",
         "DateTime (Short)",
         "Unknown 0x09",
         "Text",
         "OLE",
         "Memo/Hyperlink",
         "Unknown 0x0d",
         "Unknown 0x0e",
         "Replication ID",
	 "Decimal"};

    /*  Oracle data types */
    public static final String[] mdb_oracle_types = new String[]
        {"Oracle_Unknown 0x00",
         "NUMBER",
         "NUMBER",
         "NUMBER",
         "NUMBER",
         "NUMBER",
         "FLOAT",
         "FLOAT",
         "DATE",
         "Oracle_Unknown 0x09",
         "VARCHAR2",
         "BLOB",
         "CLOB",
         "Oracle_Unknown 0x0d",
         "Oracle_Unknown 0x0e",
         "NUMBER",
         "NUMBER"};

      /*  Sybase/MSSQL data types */
      public static final String[] mdb_sybase_types = new String[]
        {"Sybase_Unknown 0x00",
         "bit",
         "char",
         "smallint",
         "int",
         "money",
         "real",
         "float",
         "smalldatetime",
         "Sybase_Unknown 0x09",
         "varchar",
         "varbinary",
         "text",
         "Sybase_Unknown 0x0d",
         "Sybase_Unknown 0x0e",
  	"Sybase_Replication ID",
	"numeric"};

    /*  Postgres data types */
    public static final String[] mdb_postgres_types = new String[]
     {"Postgres_Unknown 0x00",
         "Bool",
         "Int2",
         "Int4",
         "Int8",
         "Money",
         "Float4",
         "Float8",
         "Timestamp",
         "Postgres_Unknown 0x09",
         "Char",
         "Postgres_Unknown 0x0b",
         "Postgres_Unknown 0x0c",
         "Postgres_Unknown 0x0d",
         "Postgres_Unknown 0x0e",
         "Serial",
	 "Postgres_Unknown 0x10"};
}
