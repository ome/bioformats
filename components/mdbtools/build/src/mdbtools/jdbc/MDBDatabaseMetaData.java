package mdbtools.jdbc;

import mdbtools.libmdb.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MDBDatabaseMetaData implements DatabaseMetaData
{
  private String url;
  private MDBConnection conn;

  public MDBDatabaseMetaData(MDBConnection conn)
  {
    this.conn = conn;
    this.url = "jdbc:mdbtools:"+conn.mdb.f.filename;
  }

  public boolean allProceduresAreCallable()
    throws SQLException
  {
    return true;
  }

  public boolean allTablesAreSelectable()
      throws SQLException
  {
    return true;
  }

  public String getURL()
      throws SQLException
  {
    return url;
  }

  public String getUserName()
    throws SQLException
  {
    return "";
  }

  public boolean isReadOnly()
      throws SQLException
  {
    return true;
  }

  public boolean nullsAreSortedHigh()
      throws SQLException
  {
    return false;
  }

  public boolean nullsAreSortedLow()
    throws SQLException
  {
    return false;
  }

  public boolean nullsAreSortedAtStart()
      throws SQLException
  {
    return false;
  }

  public boolean nullsAreSortedAtEnd()
    throws SQLException
  {
    return false;
  }

  public String getDatabaseProductName()
    throws SQLException
  {
    return "Access";
  }

  public String getDatabaseProductVersion()
    throws SQLException
  {
    return "";  /** @todo return jet 3 or jet 4 */
  }

  public String getDriverName()
    throws SQLException
  {
    return "mdbtools";
  }

  public String getDriverVersion()
    throws SQLException
  {
    return "0.0";
  }

  public int getDriverMajorVersion()
  {
    return 0;
  }

  public int getDriverMinorVersion()
  {
    return 0;
  }

  public boolean usesLocalFiles()
    throws SQLException
  {
    return false;
  }

  public boolean usesLocalFilePerTable()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMixedCaseIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesUpperCaseIdentifiers()
    throws SQLException
  {
    return true;
  }

  public boolean storesLowerCaseIdentifiers()
      throws SQLException
  {
    return false;
  }

  public boolean storesMixedCaseIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMixedCaseQuotedIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesUpperCaseQuotedIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesLowerCaseQuotedIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesMixedCaseQuotedIdentifiers()
    throws SQLException
  {
    return false;
  }

  public String getIdentifierQuoteString()
    throws SQLException
  {
    return " ";
  }

  public String getSQLKeywords()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSQLKeywords not implemented");
  }

  public String getNumericFunctions()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getNumericFunctions not implemented");
  }

  public String getStringFunctions()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getStringFunctions not implemented");
  }

  public String getSystemFunctions()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSystemFunctions not implemented");
  }

  public String getTimeDateFunctions()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getTimeDateFunctions not implemented");
  }

  public String getSearchStringEscape()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSearchStringEscape not implemented");
  }

  public String getExtraNameCharacters()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getExtraNameCharacters not implemented");
  }

  public boolean supportsAlterTableWithAddColumn()
    throws SQLException
  {
    return false;
  }

  public boolean supportsAlterTableWithDropColumn()
    throws SQLException
  {
    return false;
  }

  public boolean supportsColumnAliasing()
    throws SQLException
  {
    return false;
  }

  public boolean nullPlusNonNullIsNull()
    throws SQLException
  {
    return false;
  }

  public boolean supportsConvert()
    throws SQLException
  {
    return false;
  }

  public boolean supportsConvert(int fromType, int toType)
    throws SQLException
  {
    return false;
  }

  public boolean supportsTableCorrelationNames()
    throws SQLException
  {
    return false;
  }

  public boolean supportsDifferentTableCorrelationNames()
    throws SQLException
  {
    return false;
  }

  public boolean supportsExpressionsInOrderBy()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOrderByUnrelated()
    throws SQLException
  {
    return false;
  }

  public boolean supportsGroupBy()
    throws SQLException
  {
    return false;
  }

  public boolean supportsGroupByUnrelated()
    throws SQLException
  {
    return false;
  }

  public boolean supportsGroupByBeyondSelect()
    throws SQLException
  {
    return false;
  }

  public boolean supportsLikeEscapeClause()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMultipleResultSets()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMultipleTransactions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsNonNullableColumns()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMinimumSQLGrammar()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCoreSQLGrammar()
    throws SQLException
  {
    return false;
  }

  public boolean supportsExtendedSQLGrammar()
    throws SQLException
  {
    return false;
  }

  public boolean supportsANSI92EntryLevelSQL()
    throws SQLException
  {
    return false;
  }

  public boolean supportsANSI92IntermediateSQL()
    throws SQLException
  {
    return false;
  }

  public boolean supportsANSI92FullSQL()
    throws SQLException
  {
    return false;
  }

  public boolean supportsIntegrityEnhancementFacility()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOuterJoins()
    throws SQLException
  {
    return false;
  }

  public boolean supportsFullOuterJoins()
    throws SQLException
  {
    return false;
  }

  public boolean supportsLimitedOuterJoins()
    throws SQLException
  {
    return false;
  }

  public String getSchemaTerm()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSchemaTerm not implemented");
  }

  public String getProcedureTerm()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getProcedureTerm not implemented");
  }

  public String getCatalogTerm()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getCatalogTerm not implemented");
  }

  public boolean isCatalogAtStart()
      throws SQLException
  {
    return true;
  }

  public String getCatalogSeparator()
    throws SQLException
  {
    return ".";
  }

  public boolean supportsSchemasInDataManipulation()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSchemasInProcedureCalls()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSchemasInTableDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSchemasInIndexDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSchemasInPrivilegeDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInDataManipulation()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInProcedureCalls()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInTableDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInIndexDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInPrivilegeDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsPositionedDelete()
    throws SQLException
  {
    return false;
  }

  public boolean supportsPositionedUpdate()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSelectForUpdate()
    throws SQLException
  {
    return false;
  }

  public boolean supportsStoredProcedures()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSubqueriesInComparisons()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSubqueriesInExists()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSubqueriesInIns()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSubqueriesInQuantifieds()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCorrelatedSubqueries()
    throws SQLException
  {
    return false;
  }

  public boolean supportsUnion()
    throws SQLException
  {
    return false;
  }

  public boolean supportsUnionAll()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOpenCursorsAcrossCommit()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOpenCursorsAcrossRollback()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOpenStatementsAcrossCommit()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOpenStatementsAcrossRollback()
    throws SQLException
  {
    return false;
  }

  public int getMaxBinaryLiteralLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxCharLiteralLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInGroupBy()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInIndex()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInOrderBy()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInSelect()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInTable()
    throws SQLException
  {
    return 0;
  }

  public int getMaxConnections()
    throws SQLException
  {
    return 0;
  }

  public int getMaxCursorNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxIndexLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxSchemaNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxProcedureNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxCatalogNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxRowSize()
    throws SQLException
  {
    return 0;
  }

  public boolean doesMaxRowSizeIncludeBlobs()
    throws SQLException
  {
    return false;
  }

  public int getMaxStatementLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxStatements()
    throws SQLException
  {
    return 0;
  }

  public int getMaxTableNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxTablesInSelect()
    throws SQLException
  {
    return 0;
  }

  public int getMaxUserNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getDefaultTransactionIsolation()
    throws SQLException
  {
    return Connection.TRANSACTION_NONE;
  }

  public boolean supportsTransactions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsTransactionIsolationLevel(int level)
    throws SQLException
  {
    if (level == Connection.TRANSACTION_NONE)
      return true;
    return false;
  }

  public boolean supportsDataDefinitionAndDataManipulationTransactions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsDataManipulationTransactionsOnly()
    throws SQLException
  {
    return false;
  }

  public boolean dataDefinitionCausesTransactionCommit()
    throws SQLException
  {
    return false;
  }

  public boolean dataDefinitionIgnoredInTransactions()
    throws SQLException
  {
    return false;
  }

  public ResultSet getProcedures(String catalog, String schemaPattern,
                          String procedureNamePattern)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getProcedures not implemented");
  }

  public ResultSet getProcedureColumns(String catalog,
                                String schemaPattern,
                                String procedureNamePattern,
                                String columnNamePattern)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getProcedureColumns not implemented");
  }

  public ResultSet getTables(String catalog, String schemaPattern,
                      String tableNamePattern, String types[])
    throws SQLException
  {
//System.out.println("----");
//System.out.println(catalog);
//System.out.println(schemaPattern);
//System.out.println(tableNamePattern);
//System.out.println("----");
    if (catalog != null || schemaPattern != null ||
        (tableNamePattern != null && tableNamePattern.equals("%") == false))
      throw new RuntimeException("MDBDatabaseMetaData.getTables filtering is not implemented");
    boolean foundTable=false;
    if (types == null)
      foundTable = true;  // null means all types
    else
    {
      for (int i=0;i<types.length;i++)
      {
        System.out.println("  type: " + types[i]);
        if(types[i].equals("TABLE"))
          foundTable = true;
      }
    }
    if(foundTable == false)
      throw new RuntimeException("MDBDatabaseMetaData.getTables table not found");
    ArrayList tableNames = new ArrayList();
//    System.out.println("building rows");
    /* loop over each entry in the catalog */
    for (int i=0; i < conn.mdb.num_catalog; i++)
    {
//      System.out.println("i: " + i);
      MdbCatalogEntry entry = (MdbCatalogEntry)conn.mdb.catalog.get(i);

      /* if it's a table */
      if (entry.object_type == Constants.MDB_TABLE)
      {
        /* skip the MSys tables */
        if (entry.object_name.startsWith("MSys") == false)
        {
          Object[] row = new Object[10];
          row[2] = entry.object_name;
          row[3] = "TABLE";
//          System.out.println("added: " + row[2]);
          tableNames.add(row);
        }
      }
    }
//    System.out.println("rows built");
    String[] columnNames = new String[]{"TABLE_CAT","TABLE_SCHEM",
      "TABLE_NAME","TABLE_TYPE","REMARKS","TYPE_CAT","TYPE_SCHEM","TYPE_NAME",
      "SELF_REFERENCING_COL_NAME","REF_GENERATION"};
    return new MDBResultSetFromArrayList(columnNames,tableNames);
  }

  public ResultSet getSchemas()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSchemas not implemented");
  }

  public ResultSet getCatalogs()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getCatalogs not implemented");
  }

  public ResultSet getTableTypes()
    throws SQLException
  {
    ArrayList types = new ArrayList();
    Object[] row = new Object[1];
    row[0] = "TABLE";
    String[] names = new String[]{"TYPE"};
    return new MDBResultSetFromArrayList(names,types);
  }

  public ResultSet getColumns(String catalog, String schemaPattern,
                   String tableNamePattern, String columnNamePattern)
    throws SQLException
  {
//System.out.println("++++");
//System.out.println(catalog);
//System.out.println(schemaPattern);
//System.out.println(tableNamePattern);
//System.out.println(columnNamePattern);
//System.out.println("++++");
    if (catalog != null || schemaPattern != null ||
        columnNamePattern.equals("%") == false)
      throw new RuntimeException("MDBDatabaseMetaData.getColumns not fully implemented");
    ArrayList rows = new ArrayList();
    try
    {
      /* loop over each entry in the catalog */
      for (int i=0; i < conn.mdb.num_catalog; i++)
      {
        MdbCatalogEntry entry = (MdbCatalogEntry)conn.mdb.catalog.get(i);

        /* if it's a table */
        if (entry.object_type == Constants.MDB_TABLE)
        {
          /* skip the MSys tables */
          if (entry.object_name.startsWith("MSys") == false)
          {
            if (entry.object_name.equals(tableNamePattern))
            {
              MdbTableDef table = Table.mdb_read_table(entry);

              /* get the columns */
              Table.mdb_read_columns (table);

              /* loop over the columns, dumping the names and types */
              for (int k = 0; k < table.num_cols; k++)
              {
                MdbColumn col = (MdbColumn)table.columns.get(k);

                String type = backend.mdb_get_coltype_string (conn.mdb.default_backend, col.col_type);
                Object[] row = new Object[22];
                row[0] =  null; // "TABLE_CAT",  // table catalog (may be <code>null</code>)
                row[1] =  null; // "TABLE_SCHEM",  // table schema (may be <code>null</code>)
                row[2] =  tableNamePattern; // "TABLE_NAME",  // </B> String => table name
                row[3] =  col.name; // "COLUMN_NAME",   // </B> String => column name
                row[4] =  null; // "DATA_TYPE",  // int => SQL type from java.sql.Types
                row[5] =  type; // "TYPE_NAME",   // </B> String => Data source dependent type name,
                row[6] =  new Integer(col.col_size); // "COLUMN_SIZE",  // </B> int => column size.  For char or date
                row[7] =  null; // "BUFFER_LENGTH",  // </B> is not used.
                row[8] =  null; // "DECIMAL_DIGITS",  // /B> int => the number of fractional digits
                row[9] =  null; // "NUM_PREC_RADIX",  // </B> int => Radix (typically either 10 or 2)
                row[10] =  new Integer(columnNullableUnknown); // "NULLABLE",  // </B> int => is NULL allowed.
                row[11] =  null; // "REMARKS",   // </B> String => comment describing column (may be <code>null</code>)
                row[12] =  null; // "COLUMN_DEF",  // </B> String => default value (may be <code>null</code>)
                row[13] =  null; // "SQL_DATA_TYPE",  // </B> int => unused
                row[14] =  null; // "SQL_DATETIME_SUB",  // </B> int => unused
                row[15] =  null; // "CHAR_OCTET_LENGTH",  // </B> int => for char types the
                row[16] =  new Integer(k+1); // "ORDINAL_POSITION",  // </B> int	=> index of column in table
                row[17] =  ""; // "IS_NULLABLE",  // </B> String => "NO" means column definitely
                row[18] =  null; // "SCOPE_CATLOG",  // </B> String => catalog of table that is the scope
                row[19] =  null; // "SCOPE_SCHEMA",  // </B> String => schema of table that is the scope
                row[20] =  null; // "SCOPE_TABLE",  // </B> String => table name that this the scope
                row[21] =  null; // "SOURCE_DATA_TYPE" // </B> short => source type of a distinct type or user-generated
                rows.add(row);
              }
            }
          }
        }
      }
    }
    catch(IOException e)
    {
      throw new SQLException(e.getClass().getName() + ": " + e.getMessage());
    }
    String[] columnNames = new String[]{
        "TABLE_CAT",  // table catalog (may be <code>null</code>)
        "TABLE_SCHEM",  // table schema (may be <code>null</code>)
        "TABLE_NAME",  // </B> String => table name
        "COLUMN_NAME",   // </B> String => column name
        "DATA_TYPE",  // int => SQL type from java.sql.Types
        "TYPE_NAME",   // </B> String => Data source dependent type name,
                  // for a UDT the type name is fully qualified
        "COLUMN_SIZE",  // </B> int => column size.  For char or date
//     *	    types this is the maximum number of characters, for numeric or
//     *	    decimal types this is precision.
     "BUFFER_LENGTH",  // </B> is not used.
     "DECIMAL_DIGITS",  // /B> int => the number of fractional digits
     "NUM_PREC_RADIX",  // </B> int => Radix (typically either 10 or 2)
     "NULLABLE",  // </B> int => is NULL allowed.
//     *      <UL>
//     *      <LI> columnNoNulls - might not allow <code>NULL</code> values
//     *      <LI> columnNullable - definitely allows <code>NULL</code> values
//     *      <LI> columnNullableUnknown - nullability unknown
//     *      </UL>
     "REMARKS",   // </B> String => comment describing column (may be <code>null</code>)
     "COLUMN_DEF",  // </B> String => default value (may be <code>null</code>)
     "SQL_DATA_TYPE",  // </B> int => unused
     "SQL_DATETIME_SUB",  // </B> int => unused
     "CHAR_OCTET_LENGTH",  // </B> int => for char types the
//     *       maximum number of bytes in the column
     "ORDINAL_POSITION",  // </B> int	=> index of column in table
//     *      (starting at 1)
     "IS_NULLABLE",  // </B> String => "NO" means column definitely
//     *      does not allow NULL values; "YES" means the column might
//     *      allow NULL values.  An empty string means nobody knows.
     "SCOPE_CATLOG",  // </B> String => catalog of table that is the scope
//     *      of a reference attribute (<code>null</code> if DATA_TYPE isn't REF)
     "SCOPE_SCHEMA",  // </B> String => schema of table that is the scope
//     *      of a reference attribute (<code>null</code> if the DATA_TYPE isn't REF)
     "SCOPE_TABLE",  // </B> String => table name that this the scope
//     *      of a reference attribure (<code>null</code> if the DATA_TYPE isn't REF)
     "SOURCE_DATA_TYPE" // </B> short => source type of a distinct type or user-generated
//     *      Ref type, SQL type from java.sql.Types (<code>null</code> if DATA_TYPE
//     *      isn't DISTINCT or user-generated REF)
    };

    return new MDBResultSetFromArrayList(columnNames,rows);
  }

  public ResultSet getColumnPrivileges(String catalog, String schema,
                                String table, String columnNamePattern)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getColumnPrivileges not implemented");
  }

  public ResultSet getTablePrivileges(String catalog, String schemaPattern,
				 String tableNamePattern)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getTablePrivileges not implemented");
  }

  public ResultSet getBestRowIdentifier(String catalog, String schema,
				   String table, int scope, boolean nullable)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getBestRowIdentifier not implemented");
  }

  public ResultSet getVersionColumns(String catalog, String schema,
				String table)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getVersionColumns not implemented");
  }


  public ResultSet getPrimaryKeys(String catalog, String schema,
			     String table)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getPrimaryKeys not implemented");
  }

  public ResultSet getImportedKeys(String catalog, String schema,
                            String table)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getImportedKeys not implemented");
  }

  public ResultSet getExportedKeys(String catalog, String schema,
			      String table)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getExportedKeys not implemented");
  }

  public ResultSet getCrossReference(
                              String primaryCatalog, String primarySchema, String primaryTable,
                              String foreignCatalog, String foreignSchema, String foreignTable
                              )
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getCrossReference not implemented");
  }

  public ResultSet getTypeInfo()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getTypeInfo not implemented");
  }


  public ResultSet getIndexInfo(String catalog, String schema, String table,
                         boolean unique, boolean approximate)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getIndexInfo not implemented");
  }

  public boolean supportsResultSetType(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsResultSetType not implemented");
  }

  public boolean supportsResultSetConcurrency(int type, int concurrency)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsResultSetConcurrency not implemented");
  }

  public boolean ownUpdatesAreVisible(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.ownUpdatesAreVisible not implemented");
  }

  public boolean ownDeletesAreVisible(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.ownDeletesAreVisible not implemented");
  }

  public boolean ownInsertsAreVisible(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.ownInsertsAreVisible not implemented");
  }

  public boolean othersUpdatesAreVisible(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.othersUpdatesAreVisible not implemented");
  }

  public boolean othersDeletesAreVisible(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.othersDeletesAreVisible not implemented");
  }

  public boolean othersInsertsAreVisible(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.othersInsertsAreVisible not implemented");
  }

  public boolean updatesAreDetected(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.updatesAreDetected not implemented");
  }

  public boolean deletesAreDetected(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.deletesAreDetected not implemented");
  }

  public boolean insertsAreDetected(int type)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.insertsAreDetected not implemented");
  }

  public boolean supportsBatchUpdates()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsBatchUpdates not implemented");
  }

  public ResultSet getUDTs(String catalog, String schemaPattern,
		      String typeNamePattern, int[] types)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getUDTs not implemented");
  }

  public Connection getConnection()
    throws SQLException
  {
    return this.conn;
  }

  public boolean supportsSavepoints()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsSavepoints not implemented");
  }

  public boolean supportsNamedParameters()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsNamedParameters not implemented");
  }

  public boolean supportsMultipleOpenResults()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsMultipleOpenResults not implemented");
  }

  public boolean supportsGetGeneratedKeys()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsGetGeneratedKeys not implemented");
  }

  public ResultSet getSuperTypes(String catalog, String schemaPattern,
                          String typeNamePattern)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSuperTypes not implemented");
  }

  public ResultSet getSuperTables(String catalog, String schemaPattern,
			     String tableNamePattern)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSuperTables not implemented");
  }


  public ResultSet getAttributes(String catalog, String schemaPattern,
			    String typeNamePattern, String attributeNamePattern)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getAttributes not implemented");
  }

  public boolean supportsResultSetHoldability(int holdability)
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsResultSetHoldability not implemented");
  }

  public int getResultSetHoldability()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getResultSetHoldability not implemented");
  }

  public int getDatabaseMajorVersion()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getDatabaseMajorVersion not implemented");
  }

  public int getDatabaseMinorVersion()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getDatabaseMinorVersion not implemented");
  }

  public int getJDBCMajorVersion()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getJDBCMajorVersion not implemented");
  }

  public int getJDBCMinorVersion()
      throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getJDBCMinorVersion not implemented");
  }

  public int getSQLStateType()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.getSQLStateType not implemented");
  }

  public boolean locatorsUpdateCopy()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.locatorsUpdateCopy not implemented");
  }

  public boolean supportsStatementPooling()
    throws SQLException
  {
    throw new RuntimeException("MDBDatabaseMetaData.supportsStatementPooling not implemented");
  }

  public ResultSet getFunctionColumns(String catalog, String schema, String fucntionName, String columnName)
  {
    throw new RuntimeException("not implemented");
  }

  public ResultSet getFunctions(String catalog, String schema, String functionName)
  {
    throw new RuntimeException("not implemented");
  }

  public ResultSet getClientInfoProperties() {
    throw new RuntimeException("not implemented");
  }

  public boolean autoCommitFailureClosesAllResultSets() {
    throw new RuntimeException("not implemented");
  }

  public boolean supportsStoredFunctionsUsingCallSyntax() {
    throw new RuntimeException("not implemented");
  }

  public ResultSet getSchemas(String catalog, String schemaPattern) {
    throw new RuntimeException("not implemented");
  }

  public RowIdLifetime getRowIdLifetime() {
    throw new RuntimeException("not implemented");
  }

  public boolean isWrapperFor(Class iface) {
    throw new RuntimeException("not implemented");
  }

  public Object unwrap(Class iface) {
    throw new RuntimeException("not implemented");
  }

}
