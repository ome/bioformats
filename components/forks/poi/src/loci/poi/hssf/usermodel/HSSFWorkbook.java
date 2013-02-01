/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2013 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */


/*
 * HSSFWorkbook.java
 *
 * Created on September 30, 2001, 3:37 PM
 */
package loci.poi.hssf.usermodel;

import loci.common.*;

import loci.poi.POIDocument;
import loci.poi.ddf.EscherBSERecord;
import loci.poi.ddf.EscherBitmapBlip;
import loci.poi.ddf.EscherRecord;
import loci.poi.ddf.EscherBlipRecord;
import loci.poi.hssf.eventmodel.EventRecordFactory;
import loci.poi.hssf.model.Sheet;
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.*;
import loci.poi.hssf.record.formula.Area3DPtg;
import loci.poi.hssf.record.formula.MemFuncPtg;
import loci.poi.hssf.record.formula.UnionPtg;
import loci.poi.hssf.util.CellReference;
import loci.poi.poifs.filesystem.*;
import loci.poi.util.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * High level representation of a workbook.  This is the first object most users
 * will construct whether they are reading or writing a workbook.  It is also the
 * top level object for creating new sheets/etc.
 *
 * @see loci.poi.hssf.model.Workbook
 * @see loci.poi.hssf.usermodel.HSSFSheet
 * @author  Andrew C. Oliver (acoliver at apache dot org)
 * @author  Glen Stampoultzis (glens at apache.org)
 * @author  Shawn Laubach (slaubach at apache dot org)
 * @version 2.0-pre
 */

public class HSSFWorkbook extends POIDocument
{
    private static final int DEBUG = POILogger.DEBUG;

    /**
     * used for compile-time performance/memory optimization.  This determines the
     * initial capacity for the sheet collection.  Its currently set to 3.
     * Changing it in this release will decrease performance
     * since you're never allowed to have more or less than three sheets!
     */

    public final static int INITIAL_CAPACITY = 3;

    /**
     * this is the reference to the low level Workbook object
     */

    private Workbook workbook;

    /**
     * this holds the HSSFSheet objects attached to this workbook
     */

    protected ArrayList sheets;

    /**
     * this holds the HSSFName objects attached to this workbook
     */

    private ArrayList names;

    /**
     * holds whether or not to preserve other nodes in the POIFS.  Used
     * for macros and embedded objects.
     */
    private boolean   preserveNodes;

    /**
     * Used to keep track of the data formatter so that all
     * createDataFormatter calls return the same one for a given
     * book.  This ensures that updates from one places is visible
     * someplace else.
     */
    private HSSFDataFormat formatter;


    /** Extended windows meta file */
    public static final int PICTURE_TYPE_EMF = 2;
    /** Windows Meta File */
    public static final int PICTURE_TYPE_WMF = 3;
    /** Mac PICT format */
    public static final int PICTURE_TYPE_PICT = 4;
    /** JPEG format */
    public static final int PICTURE_TYPE_JPEG = 5;
    /** PNG format */
    public static final int PICTURE_TYPE_PNG = 6;
    /** Device independant bitmap */
    public static final int PICTURE_TYPE_DIB = 7;


    private static POILogger log = POILogFactory.getLogger(HSSFWorkbook.class);



    /**
     * Creates new HSSFWorkbook from scratch (start here!)
     *
     */
    public HSSFWorkbook()
    {
        this(Workbook.createWorkbook());
    }

    protected HSSFWorkbook( Workbook book )
    {
        workbook = book;
        sheets = new ArrayList( INITIAL_CAPACITY );
        names = new ArrayList( INITIAL_CAPACITY );
    }

    public HSSFWorkbook(POIFSFileSystem fs) throws IOException {
      this(fs,true);
    }

    /**
     * given a POI POIFSFileSystem object, read in its Workbook and populate the high and
     * low level models.  If you're reading in a workbook...start here.
     *
     * @param fs the POI filesystem that contains the Workbook stream.
     * @param preserveNodes whether to preseve other nodes, such as
     *        macros.  This takes more memory, so only say yes if you
     *        need to. If set, will store all of the POIFSFileSystem
     *        in memory
     * @see loci.poi.poifs.filesystem.POIFSFileSystem
     * @exception IOException if the stream cannot be read
     */

    public HSSFWorkbook(POIFSFileSystem fs, boolean preserveNodes)
            throws IOException
    {
        this.preserveNodes = preserveNodes;

        // Read in the HPSF properties
        this.filesystem = fs;
        readProperties();
        
        // If we're not preserving nodes, don't track the
        //  POIFS any more
        if(! preserveNodes) {
           this.filesystem = null;
        }

        sheets = new ArrayList(INITIAL_CAPACITY);
        names  = new ArrayList(INITIAL_CAPACITY);
        
        // Normally, the Workbook will be in a POIFS Stream
        //  called "Workbook". However, some wierd XLS generators
        //  put theirs in one called "WORKBOOK"
        String workbookName = "Workbook";
        try {
        	fs.getRoot().getEntry(workbookName);
        	// Is the default name
        } catch(FileNotFoundException fe) {
        	// Try the upper case form
        	try {
        		workbookName = "WORKBOOK";
        		fs.getRoot().getEntry(workbookName);
        	} catch(FileNotFoundException wfe) {
        		// Doesn't contain it in either form
        		throw new IllegalArgumentException("The supplied POIFSFileSystem contained neither a 'Workbook' entry, nor a 'WORKBOOK' entry. Is it really an excel file?");
        	}
        }

        
        // Grab the data from the workbook stream, however
        //  it happens to be spelt.
        InputStream stream = fs.createDocumentInputStream(workbookName);

        EventRecordFactory factory = new EventRecordFactory();

        List records = RecordFactory.createRecords(stream);

        workbook = Workbook.createWorkbook(records);
        setPropertiesFromWorkbook(workbook);
        int recOffset = workbook.getNumRecords();
        int sheetNum = 0;

        // convert all LabelRecord records to LabelSSTRecord
        convertLabelRecords(records, recOffset);        
        while (recOffset < records.size())
        {
            Sheet sheet = Sheet.createSheet(records, sheetNum++, recOffset );

            recOffset = sheet.getEofLoc()+1;
            if (recOffset == 1)
            {
                break;
            }

            HSSFSheet hsheet = new HSSFSheet(this, sheet);

            sheets.add(hsheet);

            // workbook.setSheetName(sheets.size() -1, "Sheet"+sheets.size());
        }

        for (int i = 0 ; i < workbook.getNumNames() ; ++i){
            HSSFName name = new HSSFName(workbook, workbook.getNameRecord(i));
            names.add(name);
        }
    }

     public HSSFWorkbook(RandomAccessInputStream s) throws IOException {
         this(s,true);
     }

    /**
     * Companion to HSSFWorkbook(POIFSFileSystem), this constructs the POI filesystem around your
     * inputstream.
     *
     * @param s  the POI filesystem that contains the Workbook stream.
     * @param preserveNodes whether to preseve other nodes, such as
     *        macros.  This takes more memory, so only say yes if you
     *        need to.
     * @see loci.poi.poifs.filesystem.POIFSFileSystem
     * @see #HSSFWorkbook(POIFSFileSystem)
     * @exception IOException if the stream cannot be read
     */

    public HSSFWorkbook(RandomAccessInputStream s, boolean preserveNodes)
            throws IOException
    {
        this(new POIFSFileSystem(s, 512), preserveNodes);
    }

    /**
     * used internally to set the workbook properties.
     */

    private void setPropertiesFromWorkbook(Workbook book)
    {
        this.workbook = book;

        // none currently
    }
    
    /**
      * This is basically a kludge to deal with the now obsolete Label records.  If
      * you have to read in a sheet that contains Label records, be aware that the rest
      * of the API doesn't deal with them, the low level structure only provides read-only
      * semi-immutable structures (the sets are there for interface conformance with NO
      * impelmentation).  In short, you need to call this function passing it a reference
      * to the Workbook object.  All labels will be converted to LabelSST records and their
      * contained strings will be written to the Shared String tabel (SSTRecord) within
      * the Workbook.
      *
      * @param wb sheet's matching low level Workbook structure containing the SSTRecord.
      * @see loci.poi.hssf.record.LabelRecord
      * @see loci.poi.hssf.record.LabelSSTRecord
      * @see loci.poi.hssf.record.SSTRecord
      */
 
     private void convertLabelRecords(List records, int offset)
     {
         if (log.check( POILogger.DEBUG ))
             log.log(POILogger.DEBUG, "convertLabelRecords called");
         for (int k = offset; k < records.size(); k++)
         {
             Record rec = ( Record ) records.get(k);

             if (rec.getSid() == LabelRecord.sid)
             {
                 LabelRecord oldrec = ( LabelRecord ) rec;

                 records.remove(k);
                 LabelSSTRecord newrec   = new LabelSSTRecord();
                 int            stringid =
                     workbook.addSSTString(new UnicodeString(oldrec.getValue()));

                 newrec.setRow(oldrec.getRow());
                 newrec.setColumn(oldrec.getColumn());
                 newrec.setXFIndex(oldrec.getXFIndex());
                 newrec.setSSTIndex(stringid);
                       records.add(k, newrec);
             }
         }
         if (log.check( POILogger.DEBUG ))
             log.log(POILogger.DEBUG, "convertLabelRecords exit");
     }
    

    /**
     * sets the order of appearance for a given sheet.
     *
     * @param sheetname the name of the sheet to reorder
     * @param pos the position that we want to insert the sheet into (0 based)
     */

    public void setSheetOrder(String sheetname, int pos ) {
        sheets.add(pos,sheets.remove(getSheetIndex(sheetname)));
        workbook.setSheetOrder(sheetname, pos);
    }
    
    /**
     * sets the tab whose data is actually seen when the sheet is opened.
     * This may be different from the "selected sheet" since excel seems to
     * allow you to show the data of one sheet when another is seen "selected"
     * in the tabs (at the bottom).
     * @see loci.poi.hssf.usermodel.HSSFSheet#setSelected(boolean)
     * @param index
     */
    public void setSelectedTab(short index) {
        workbook.getWindowOne().setSelectedTab(index);
    }
    
    /**
     * gets the tab whose data is actually seen when the sheet is opened.
     * This may be different from the "selected sheet" since excel seems to
     * allow you to show the data of one sheet when another is seen "selected"
     * in the tabs (at the bottom).
     * @see loci.poi.hssf.usermodel.HSSFSheet#setSelected(boolean)
     */
    public short getSelectedTab() {
        return workbook.getWindowOne().getSelectedTab();
    }
    
    /**
     * sets the first tab that is displayed in the list of tabs
     * in excel.
     * @param index
     */
    public void setDisplayedTab(short index) {
        workbook.getWindowOne().setDisplayedTab(index);
    }
    
    /**
     * sets the first tab that is displayed in the list of tabs
     * in excel.
     */
    public short getDisplayedTab() {
        return workbook.getWindowOne().getDisplayedTab();
    }

    /**
     * @deprecated POI will now properly handle unicode strings without
     * forceing an encoding
     */
    public final static byte ENCODING_COMPRESSED_UNICODE = 0;
    /**
     * @deprecated POI will now properly handle unicode strings without
     * forceing an encoding
     */
    public final static byte ENCODING_UTF_16             = 1;


    /**
     * set the sheet name. 
     * Will throw IllegalArgumentException if the name is greater than 31 chars
     * or contains /\?*[]
     * @param sheet number (0 based)
     */
    public void setSheetName(int sheet, String name)
    {
        if (workbook.doesContainsSheetName( name, sheet ))
            throw new IllegalArgumentException( "The workbook already contains a sheet with this name" );

        if (sheet > (sheets.size() - 1))
        {
            throw new RuntimeException("Sheet out of bounds");
        }
        
        workbook.setSheetName( sheet, name);
    }

    
    /**
     * set the sheet name forcing the encoding. Forcing the encoding IS A BAD IDEA!!!
     * @deprecated 3-Jan-2006 POI now automatically detects unicode and sets the encoding
     * appropriately. Simply use setSheetName(int sheet, String encoding) 
     * @throws IllegalArgumentException if the name is greater than 31 chars
     * or contains /\?*[]
     * @param sheet number (0 based)
     */    
    public void setSheetName( int sheet, String name, short encoding )
    {
        if (workbook.doesContainsSheetName( name, sheet ))
            throw new IllegalArgumentException( "The workbook already contains a sheet with this name" );

        if (sheet > (sheets.size() - 1))
        {
            throw new RuntimeException("Sheet out of bounds");
        }

        switch ( encoding ) {
        case ENCODING_COMPRESSED_UNICODE:
        case ENCODING_UTF_16:
            break;

        default:
            // TODO java.io.UnsupportedEncodingException
            throw new RuntimeException( "Unsupported encoding" );
        }

        workbook.setSheetName( sheet, name, encoding );
    }

    /**
     * get the sheet name
     * @param sheet Number
     * @return Sheet name
     */

    public String getSheetName(int sheet)
    {
        if (sheet > (sheets.size() - 1))
        {
            throw new RuntimeException("Sheet out of bounds");
        }
        return workbook.getSheetName(sheet);
    }

    /*
     * get the sheet's index
     * @param name  sheet name
     * @return sheet index or -1 if it was not found.
     */

    /** Returns the index of the sheet by his name
     * @param name the sheet name
     * @return index of the sheet (0 based)
     */
    public int getSheetIndex(String name)
    {
        int retval = workbook.getSheetIndex(name);

        return retval;
    }

    /** Returns the index of the given sheet
     * @param sheet the sheet to look up
     * @return index of the sheet (0 based)
     */
    public int getSheetIndex(HSSFSheet sheet)
    {
    	for(int i=0; i<sheets.size(); i++) {
    		if(sheets.get(i) == sheet) {
    			return i;
    		}
    	}
    	return -1;
    }

    /**
     * create an HSSFSheet for this HSSFWorkbook, adds it to the sheets and returns
     * the high level representation.  Use this to create new sheets.
     *
     * @return HSSFSheet representing the new sheet.
     */

    public HSSFSheet createSheet()
    {

//        if (getNumberOfSheets() == 3)
//            throw new RuntimeException("You cannot have more than three sheets in HSSF 1.0");
        HSSFSheet sheet = new HSSFSheet(this);

        sheets.add(sheet);
        workbook.setSheetName(sheets.size() - 1,
                "Sheet" + (sheets.size() - 1));
        WindowTwoRecord windowTwo = (WindowTwoRecord) sheet.getSheet().findFirstRecordBySid(WindowTwoRecord.sid);
        windowTwo.setSelected(sheets.size() == 1);
        windowTwo.setPaged(sheets.size() == 1);
        return sheet;
    }

    /**
     * create an HSSFSheet from an existing sheet in the HSSFWorkbook.
     *
     * @return HSSFSheet representing the cloned sheet.
     */

    public HSSFSheet cloneSheet(int sheetNum) {
      HSSFSheet srcSheet = (HSSFSheet)sheets.get(sheetNum);
      String srcName = workbook.getSheetName(sheetNum);
      if (srcSheet != null) {
        HSSFSheet clonedSheet = srcSheet.cloneSheet(this);
        WindowTwoRecord windowTwo = (WindowTwoRecord) clonedSheet.getSheet().findFirstRecordBySid(WindowTwoRecord.sid);
        windowTwo.setSelected(sheets.size() == 1);
        windowTwo.setPaged(sheets.size() == 1);

        sheets.add(clonedSheet);
        int i=1;
        while (true) {
        	//Try and find the next sheet name that is unique
        	String name = srcName;
        	String index = Integer.toString(i++);
        	if (name.length()+index.length()+2<31)
        	  name = name + "("+index+")";
        	else name = name.substring(0, 31-index.length()-2)+"("+index+")";
        	
        	//If the sheet name is unique, then set it otherwise move on to the next number.
        	if (workbook.getSheetIndex(name) == -1) {
              workbook.setSheetName(sheets.size()-1, name);
              break;
        	}
        }
        return clonedSheet;
      }
      return null;
    }

    /**
     * create an HSSFSheet for this HSSFWorkbook, adds it to the sheets and returns
     * the high level representation.  Use this to create new sheets.
     *
     * @param sheetname     sheetname to set for the sheet.
     * @return HSSFSheet representing the new sheet.
     */

    public HSSFSheet createSheet(String sheetname)
    {
        if (workbook.doesContainsSheetName( sheetname, sheets.size() ))
            throw new IllegalArgumentException( "The workbook already contains a sheet of this name" );

        HSSFSheet sheet = new HSSFSheet(this);

        sheets.add(sheet);
        workbook.setSheetName(sheets.size() - 1, sheetname);
        WindowTwoRecord windowTwo = (WindowTwoRecord) sheet.getSheet().findFirstRecordBySid(WindowTwoRecord.sid);
        windowTwo.setSelected(sheets.size() == 1);
        windowTwo.setPaged(sheets.size() == 1);
        return sheet;
    }

    /**
     * get the number of spreadsheets in the workbook (this will be three after serialization)
     * @return number of sheets
     */

    public int getNumberOfSheets()
    {
        return sheets.size();
    }

    /**
     * Get the HSSFSheet object at the given index.
     * @param index of the sheet number (0-based physical & logical)
     * @return HSSFSheet at the provided index
     */

    public HSSFSheet getSheetAt(int index)
    {
        return (HSSFSheet) sheets.get(index);
    }

    /**
     * Get sheet with the given name
     * @param name of the sheet
     * @return HSSFSheet with the name provided or null if it does not exist
     */

    public HSSFSheet getSheet(String name)
    {
        HSSFSheet retval = null;

        for (int k = 0; k < sheets.size(); k++)
        {
            String sheetname = workbook.getSheetName(k);

            if (sheetname.equals(name))
            {
                retval = (HSSFSheet) sheets.get(k);
            }
        }
        return retval;
    }

    /**
     * removes sheet at the given index
     * @param index of the sheet  (0-based)
     */

    public void removeSheetAt(int index)
    {
        sheets.remove(index);
        workbook.removeSheet(index);
    }

    /**
     * determine whether the Excel GUI will backup the workbook when saving.
     *
     * @param backupValue   true to indicate a backup will be performed.
     */

    public void setBackupFlag(boolean backupValue)
    {
        BackupRecord backupRecord = workbook.getBackupRecord();

        backupRecord.setBackup(backupValue ? (short) 1
                : (short) 0);
    }

    /**
     * determine whether the Excel GUI will backup the workbook when saving.
     *
     * @return the current setting for backups.
     */

    public boolean getBackupFlag()
    {
        BackupRecord backupRecord = workbook.getBackupRecord();

        return (backupRecord.getBackup() == 0) ? false
                : true;
    }

    /**
     * Sets the repeating rows and columns for a sheet (as found in
     * File->PageSetup->Sheet).  This is function is included in the workbook
     * because it creates/modifies name records which are stored at the
     * workbook level.
     * <p>
     * To set just repeating columns:
     * <pre>
     *  workbook.setRepeatingRowsAndColumns(0,0,1,-1-1);
     * </pre>
     * To set just repeating rows:
     * <pre>
     *  workbook.setRepeatingRowsAndColumns(0,-1,-1,0,4);
     * </pre>
     * To remove all repeating rows and columns for a sheet.
     * <pre>
     *  workbook.setRepeatingRowsAndColumns(0,-1,-1,-1,-1);
     * </pre>
     *
     * @param sheetIndex    0 based index to sheet.
     * @param startColumn   0 based start of repeating columns.
     * @param endColumn     0 based end of repeating columns.
     * @param startRow      0 based start of repeating rows.
     * @param endRow        0 based end of repeating rows.
     */
    public void setRepeatingRowsAndColumns(int sheetIndex,
                                           int startColumn, int endColumn,
                                           int startRow, int endRow)
    {
        // Check arguments
        if (startColumn == -1 && endColumn != -1) throw new IllegalArgumentException("Invalid column range specification");
        if (startRow == -1 && endRow != -1) throw new IllegalArgumentException("Invalid row range specification");
        if (startColumn < -1 || startColumn >= 0xFF) throw new IllegalArgumentException("Invalid column range specification");
        if (endColumn < -1 || endColumn >= 0xFF) throw new IllegalArgumentException("Invalid column range specification");
        if (startRow < -1 || startRow > 65535) throw new IllegalArgumentException("Invalid row range specification");
        if (endRow < -1 || endRow > 65535) throw new IllegalArgumentException("Invalid row range specification");
        if (startColumn > endColumn) throw new IllegalArgumentException("Invalid column range specification");
        if (startRow > endRow) throw new IllegalArgumentException("Invalid row range specification");

        HSSFSheet sheet = getSheetAt(sheetIndex);
        short externSheetIndex = getWorkbook().checkExternSheet(sheetIndex);

        boolean settingRowAndColumn =
                startColumn != -1 && endColumn != -1 && startRow != -1 && endRow != -1;
        boolean removingRange =
                startColumn == -1 && endColumn == -1 && startRow == -1 && endRow == -1;

        boolean isNewRecord = false;
        NameRecord nameRecord;
        nameRecord = findExistingRowColHeaderNameRecord(sheetIndex);
        if (removingRange )
        {
            if (nameRecord != null)
                workbook.removeName(findExistingRowColHeaderNameRecordIdx(sheetIndex+1));
            return;
        }
        if ( nameRecord == null )
        {
            nameRecord = workbook.createBuiltInName(NameRecord.BUILTIN_PRINT_TITLE, sheetIndex+1);
            //does a lot of the house keeping for builtin records, like setting lengths to zero etc
            isNewRecord = true;
        }

        short definitionTextLength = settingRowAndColumn ? (short)0x001a : (short)0x000b;
        nameRecord.setDefinitionTextLength(definitionTextLength);

        Stack ptgs = new Stack();

        if (settingRowAndColumn)
        {
            MemFuncPtg memFuncPtg = new MemFuncPtg();
            memFuncPtg.setLenRefSubexpression(23);
            ptgs.add(memFuncPtg);
        }
        if (startColumn >= 0)
        {
            Area3DPtg area3DPtg1 = new Area3DPtg();
            area3DPtg1.setExternSheetIndex(externSheetIndex);
            area3DPtg1.setFirstColumn((short)startColumn);
            area3DPtg1.setLastColumn((short)endColumn);
            area3DPtg1.setFirstRow((short)0);
            area3DPtg1.setLastRow((short)0xFFFF);
            ptgs.add(area3DPtg1);
        }
        if (startRow >= 0)
        {
            Area3DPtg area3DPtg2 = new Area3DPtg();
            area3DPtg2.setExternSheetIndex(externSheetIndex);
            area3DPtg2.setFirstColumn((short)0);
            area3DPtg2.setLastColumn((short)0x00FF);
            area3DPtg2.setFirstRow((short)startRow);
            area3DPtg2.setLastRow((short)endRow);
            ptgs.add(area3DPtg2);
        }
        if (settingRowAndColumn)
        {
            UnionPtg unionPtg = new UnionPtg();
            ptgs.add(unionPtg);
        }
        nameRecord.setNameDefinition(ptgs);

        if (isNewRecord)
        {
            HSSFName newName = new HSSFName(workbook, nameRecord);
            names.add(newName);
        }

        HSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setValidSettings(false);

        WindowTwoRecord w2 = (WindowTwoRecord) sheet.getSheet().findFirstRecordBySid(WindowTwoRecord.sid);
        w2.setPaged(true);
    }

    private NameRecord findExistingRowColHeaderNameRecord( int sheetIndex )
    {
        int index = findExistingRowColHeaderNameRecordIdx(sheetIndex);
        if (index == -1)
            return null;
        else
            return (NameRecord)workbook.findNextRecordBySid(NameRecord.sid, index);
    }

    private int findExistingRowColHeaderNameRecordIdx( int sheetIndex )
    {
        int index = 0;
        NameRecord r = null;
        while ((r = (NameRecord) workbook.findNextRecordBySid(NameRecord.sid, index)) != null)
        {
            int indexToSheet = r.getEqualsToIndexToSheet() -1;
            if(indexToSheet > -1) { //ignore "GLOBAL" name records
                int nameRecordSheetIndex = workbook.getSheetIndexFromExternSheetIndex(indexToSheet);
                if (isRowColHeaderRecord( r ) && nameRecordSheetIndex == sheetIndex)
                {
                    return index;
                }
            } 
            index++;
        }

        return -1;
    }

    private boolean isRowColHeaderRecord( NameRecord r )
    {
        return r.getOptionFlag() == 0x20 && ("" + ((char)7)).equals(r.getNameText());
    }

    /**
     * create a new Font and add it to the workbook's font table
     * @return new font object
     */

    public HSSFFont createFont()
    {
        FontRecord font = workbook.createNewFont();
        short fontindex = (short) (getNumberOfFonts() - 1);

        if (fontindex > 3)
        {
            fontindex++;   // THERE IS NO FOUR!!
        }
        if(fontindex == Short.MAX_VALUE){
            throw new IllegalArgumentException("Maximum number of fonts was exceeded");
        }
        HSSFFont retval = new HSSFFont(fontindex, font);

        return retval;
    }

    /**
     * Finds a font that matches the one with the supplied attributes
     */
    public HSSFFont findFont(short boldWeight, short color, short fontHeight,
                             String name, boolean italic, boolean strikeout,
                             short typeOffset, byte underline)
    {
//        System.out.println( boldWeight + ", " + color + ", " + fontHeight + ", " + name + ", " + italic + ", " + strikeout + ", " + typeOffset + ", " + underline );
        for (short i = 0; i < workbook.getNumberOfFontRecords(); i++)
        {
            if (i == 4)
                continue;

            FontRecord font = workbook.getFontRecordAt(i);
            HSSFFont hssfFont = new HSSFFont(i, font);
//            System.out.println( hssfFont.getBoldweight() + ", " + hssfFont.getColor() + ", " + hssfFont.getFontHeight() + ", " + hssfFont.getFontName() + ", " + hssfFont.getItalic() + ", " + hssfFont.getStrikeout() + ", " + hssfFont.getTypeOffset() + ", " + hssfFont.getUnderline() );
            if (hssfFont.getBoldweight() == boldWeight
                    && hssfFont.getColor() == color
                    && hssfFont.getFontHeight() == fontHeight
                    && hssfFont.getFontName().equals(name)
                    && hssfFont.getItalic() == italic
                    && hssfFont.getStrikeout() == strikeout
                    && hssfFont.getTypeOffset() == typeOffset
                    && hssfFont.getUnderline() == underline)
            {
//                System.out.println( "Found font" );
                return hssfFont;
            }
        }

//        System.out.println( "No font found" );
        return null;
    }

    /**
     * get the number of fonts in the font table
     * @return number of fonts
     */

    public short getNumberOfFonts()
    {
        return (short) workbook.getNumberOfFontRecords();
    }

    /**
     * get the font at the given index number
     * @param idx  index number
     * @return HSSFFont at the index
     */

    public HSSFFont getFontAt(short idx)
    {
        FontRecord font = workbook.getFontRecordAt(idx);
        HSSFFont retval = new HSSFFont(idx, font);

        return retval;
    }

    /**
     * create a new Cell style and add it to the workbook's style table
     * @return the new Cell Style object
     */

    public HSSFCellStyle createCellStyle()
    {
        ExtendedFormatRecord xfr = workbook.createCellXF();
        short index = (short) (getNumCellStyles() - 1);
        HSSFCellStyle style = new HSSFCellStyle(index, xfr);

        return style;
    }

    /**
     * get the number of styles the workbook contains
     * @return count of cell styles
     */

    public short getNumCellStyles()
    {
        return (short) workbook.getNumExFormats();
    }

    /**
     * get the cell style object at the given index
     * @param idx  index within the set of styles
     * @return HSSFCellStyle object at the index
     */

    public HSSFCellStyle getCellStyleAt(short idx)
    {
        ExtendedFormatRecord xfr = workbook.getExFormatAt(idx);
        HSSFCellStyle style = new HSSFCellStyle(idx, xfr);

        return style;
    }

    /**
     * Method write - write out this workbook to an Outputstream.  Constructs
     * a new POI POIFSFileSystem, passes in the workbook binary representation  and
     * writes it out.
     *
     * @param stream - the java OutputStream you wish to write the XLS to
     *
     * @exception IOException if anything can't be written.
     * @see loci.poi.poifs.filesystem.POIFSFileSystem
     */

    public void write(OutputStream stream)
            throws IOException
    {
        byte[] bytes = getBytes();
        POIFSFileSystem fs = new POIFSFileSystem();

        // For tracking what we've written out, used if we're
        //  going to be preserving nodes
        List excepts = new ArrayList(1);
        
        // Write out the Workbook stream
        fs.createDocument(new RandomAccessInputStream(bytes), "Workbook");
        
        // Write out our HPFS properties, if we have them
        writeProperties(fs, excepts);

        if (preserveNodes) {
			// Don't write out the old Workbook, we'll be doing our new one
            excepts.add("Workbook");
			// If the file had WORKBOOK instead of Workbook, we'll write it
			//  out correctly shortly, so don't include the old one
            excepts.add("WORKBOOK");

			// Copy over all the other nodes to our new poifs
            copyNodes(this.filesystem,fs,excepts);
        }
        fs.writeFilesystem(stream);
        //poifs.writeFilesystem(stream);
    }

    /**
     * Method getBytes - get the bytes of just the HSSF portions of the XLS file.
     * Use this to construct a POI POIFSFileSystem yourself.
     *
     *
     * @return byte[] array containing the binary representation of this workbook and all contained
     *         sheets, rows, cells, etc.
     *
     * @see loci.poi.hssf.model.Workbook
     * @see loci.poi.hssf.model.Sheet
     */

    public byte[] getBytes()
    {
        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "HSSFWorkbook.getBytes()");

        // before getting the workbook size we must tell the sheets that
        // serialization is about to occur.
        for (int k = 0; k < sheets.size(); k++)
            ((HSSFSheet) sheets.get(k)).getSheet().preSerialize();

        int wbsize = workbook.getSize();

        // log.debug("REMOVEME: old sizing method "+workbook.serialize().length);
        // ArrayList sheetbytes = new ArrayList(sheets.size());
        int totalsize = wbsize;

        for (int k = 0; k < sheets.size(); k++)
        {
            workbook.setSheetBof(k, totalsize);
            totalsize += ((HSSFSheet) sheets.get(k)).getSheet().getSize();
        }


/*        if (totalsize < 4096)
        {
            totalsize = 4096;
        }*/
        byte[] retval = new byte[totalsize];
        int pos = workbook.serialize(0, retval);

        // System.arraycopy(wb, 0, retval, 0, wb.length);
        for (int k = 0; k < sheets.size(); k++)
        {

            // byte[] sb = (byte[])sheetbytes.get(k);
            // System.arraycopy(sb, 0, retval, pos, sb.length);
            int len = ((HSSFSheet) sheets.get(k)).getSheet().serialize(pos,
                                retval);
            pos += len;   // sb.length;
        }
/*        for (int k = pos; k < totalsize; k++)
        {
            retval[k] = 0;
        }*/
        return retval;
    }

    /** @deprecated Do not call this method from your applications. Use the methods
     *  available in the HSSFRow to add string HSSFCells
     */
    public int addSSTString(String string)
    {
        return workbook.addSSTString(new UnicodeString(string));
    }

    /** @deprecated Do not call this method from your applications. Use the methods
     *  available in the HSSFRow to get string HSSFCells
     */
    public String getSSTString(int index)
    {
        return workbook.getSSTString(index).getString();
    }

    protected Workbook getWorkbook()
    {
        return workbook;
    }

    /** gets the total number of named ranges in the workboko
     * @return number of named ranges
     */
    public int getNumberOfNames(){
        int result = names.size();
        return result;
    }

    /** gets the Named range
     * @param index position of the named range
     * @return named range high level
     */
    public HSSFName getNameAt(int index){
        HSSFName result = (HSSFName) names.get(index);

        return result;
    }

    /** gets the named range name
     * @param index the named range index (0 based)
     * @return named range name
     */
    public String getNameName(int index){
        String result = getNameAt(index).getNameName();

        return result;
    }

	/**
	 * Sets the printarea for the sheet provided
	 * <p>
	 * i.e. Reference = $A$1:$B$2
	 * @param sheetIndex Zero-based sheet index (0 Represents the first sheet to keep consistent with java)
	 * @param reference Valid name Reference for the Print Area
	 */
	public void setPrintArea(int sheetIndex, String reference)
	{
		NameRecord name = workbook.getSpecificBuiltinRecord(NameRecord.BUILTIN_PRINT_AREA, sheetIndex+1);


		if (name == null)
			name = workbook.createBuiltInName(NameRecord.BUILTIN_PRINT_AREA, sheetIndex+1);
       //adding one here because 0 indicates a global named region; doesnt make sense for print areas

	    short externSheetIndex = getWorkbook().checkExternSheet(sheetIndex);
		name.setExternSheetNumber(externSheetIndex);
		name.setAreaReference(reference);


	}

	/**
	 * For the Convenience of Java Programmers maintaining pointers.
	 * @see #setPrintArea(int, String)
	 * @param sheetIndex Zero-based sheet index (0 = First Sheet)
	 * @param startColumn Column to begin printarea
	 * @param endColumn Column to end the printarea
	 * @param startRow Row to begin the printarea
	 * @param endRow Row to end the printarea
	 */
	public void setPrintArea(int sheetIndex, int startColumn, int endColumn,
							  int startRow, int endRow) {

		//using absolute references because they dont get copied and pasted anyway
		CellReference cell = new CellReference(startRow, startColumn, true, true);
		String reference = cell.toString();

		cell = new CellReference(endRow, endColumn, true, true);
		reference = reference+":"+cell.toString();

		setPrintArea(sheetIndex, reference);
	}


	/**
	 * Retrieves the reference for the printarea of the specified sheet, the sheet name is appended to the reference even if it was not specified.
	 * @param sheetIndex Zero-based sheet index (0 Represents the first sheet to keep consistent with java)
	 * @return String Null if no print area has been defined
	 */
	public String getPrintArea(int sheetIndex)
	{
		NameRecord name = workbook.getSpecificBuiltinRecord(NameRecord.BUILTIN_PRINT_AREA, sheetIndex+1);
		if (name == null) return null;
		//adding one here because 0 indicates a global named region; doesnt make sense for print areas

		return name.getAreaReference(workbook);
	}

    /**
     * Delete the printarea for the sheet specified
     * @param sheetIndex Zero-based sheet index (0 = First Sheet)
     */
    public void removePrintArea(int sheetIndex) {
    	getWorkbook().removeBuiltinRecord(NameRecord.BUILTIN_PRINT_AREA, sheetIndex+1);
    }

    /** creates a new named range and add it to the model
     * @return named range high level
     */
    public HSSFName createName(){
        NameRecord nameRecord = workbook.createName();

        HSSFName newName = new HSSFName(workbook, nameRecord);

        names.add(newName);

        return newName;
    }

    /** gets the named range index by his name
     * <i>Note:</i>Excel named ranges are case-insensitive and
     * this method performs a case-insensitive search.
     * 
     * @param name named range name
     * @return named range index
     */
    public int getNameIndex(String name)
    {
        int retval = -1;

        for (int k = 0; k < names.size(); k++)
        {
            String nameName = getNameName(k);

            if (nameName.equalsIgnoreCase(name))
            {
                retval = k;
                break;
            }
        }
        return retval;
    }


    /** remove the named range by his index
     * @param index named range index (0 based)
     */
    public void removeName(int index){
        names.remove(index);
        workbook.removeName(index);
    }

    /**
     * Returns the instance of HSSFDataFormat for this workbook.
     * @return the HSSFDataFormat object
     * @see loci.poi.hssf.record.FormatRecord
     * @see loci.poi.hssf.record.Record
     */
    public HSSFDataFormat createDataFormat() {
	if (formatter == null)
	    formatter = new HSSFDataFormat(workbook);
	return formatter;
    }

    /** remove the named range by his name
     * @param name named range name
     */
    public void removeName(String name){
        int index = getNameIndex(name);

        removeName(index);

    }

    public HSSFPalette getCustomPalette()
    {
        return new HSSFPalette(workbook.getCustomPalette());
    }

    /** Test only. Do not use */
    public void insertChartRecord()
    {
        int loc = workbook.findFirstRecordLocBySid(SSTRecord.sid);
        byte[] data = {
           (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0xF0, (byte)0x52,
           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
           (byte)0x06, (byte)0xF0, (byte)0x18, (byte)0x00, (byte)0x00,
           (byte)0x00, (byte)0x01, (byte)0x08, (byte)0x00, (byte)0x00,
           (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02,
           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00,
           (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
           (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00,
           (byte)0x33, (byte)0x00, (byte)0x0B, (byte)0xF0, (byte)0x12,
           (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xBF, (byte)0x00,
           (byte)0x08, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x81,
           (byte)0x01, (byte)0x09, (byte)0x00, (byte)0x00, (byte)0x08,
           (byte)0xC0, (byte)0x01, (byte)0x40, (byte)0x00, (byte)0x00,
           (byte)0x08, (byte)0x40, (byte)0x00, (byte)0x1E, (byte)0xF1,
           (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D,
           (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x0C, (byte)0x00,
           (byte)0x00, (byte)0x08, (byte)0x17, (byte)0x00, (byte)0x00,
           (byte)0x08, (byte)0xF7, (byte)0x00, (byte)0x00, (byte)0x10,
        };
        UnknownRecord r = new UnknownRecord((short)0x00EB, data);
        workbook.getRecords().add(loc, r);
    }

    /**
     * Spits out a list of all the drawing records in the workbook.
     */
    public void dumpDrawingGroupRecords(boolean fat)
    {
        DrawingGroupRecord r = (DrawingGroupRecord) workbook.findFirstRecordBySid( DrawingGroupRecord.sid );
        r.decode();
        List escherRecords = r.getEscherRecords();
        PrintWriter w = new PrintWriter(System.out);
        for ( Iterator iterator = escherRecords.iterator(); iterator.hasNext(); )
        {
            EscherRecord escherRecord = (EscherRecord) iterator.next();
            if (fat)
                System.out.println(escherRecord.toString());
            else
                escherRecord.display(w, 0);
        }
        w.flush();
    }

    /**
     * Adds a picture to the workbook.
     *
     * @param pictureData       The bytes of the picture
     * @param format            The format of the picture.  One of <code>PICTURE_TYPE_*</code>
     *
     * @return the index to this picture (1 based).
     */
    public int addPicture(byte[] pictureData, int format)
    {
        byte[] uid = newUID();
        EscherBitmapBlip blipRecord = new EscherBitmapBlip();
        blipRecord.setRecordId( (short) ( EscherBitmapBlip.RECORD_ID_START + format ) );
        switch (format)
        {
            case PICTURE_TYPE_EMF:
                blipRecord.setOptions(HSSFPictureData.MSOBI_EMF);
                break;
            case PICTURE_TYPE_WMF:
                blipRecord.setOptions(HSSFPictureData.MSOBI_WMF);
                break;
            case PICTURE_TYPE_PICT:
                blipRecord.setOptions(HSSFPictureData.MSOBI_PICT);
                break;
            case PICTURE_TYPE_PNG:
                blipRecord.setOptions(HSSFPictureData.MSOBI_PNG);
                break;
            case HSSFWorkbook.PICTURE_TYPE_JPEG:
                blipRecord.setOptions(HSSFPictureData.MSOBI_JPEG);
                break;
            case HSSFWorkbook.PICTURE_TYPE_DIB:
                blipRecord.setOptions(HSSFPictureData.MSOBI_DIB);
                break;
        }

        blipRecord.setUID( uid );
        blipRecord.setMarker( (byte) 0xFF );
        blipRecord.setPictureData( pictureData );

        EscherBSERecord r = new EscherBSERecord();
        r.setRecordId( EscherBSERecord.RECORD_ID );
        r.setOptions( (short) ( 0x0002 | ( format << 4 ) ) );
        r.setBlipTypeMacOS( (byte) format );
        r.setBlipTypeWin32( (byte) format );
        r.setUid( uid );
        r.setTag( (short) 0xFF );
        r.setSize( pictureData.length + 25 );
        r.setRef( 1 );
        r.setOffset( 0 );
        r.setBlipRecord( blipRecord );

        return workbook.addBSERecord( r );
    }

    /**
     * Gets all pictures from the Workbook.
     *
     * @return the list of pictures (a list of {@link HSSFPictureData} objects.)
     */
    public List getAllPictures()
    {
        // The drawing group record always exists at the top level, so we won't need to do this recursively.
        List pictures = new ArrayList();
        Iterator recordIter = workbook.getRecords().iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
            if (obj instanceof AbstractEscherHolderRecord)
            {
                ((AbstractEscherHolderRecord) obj).decode();
                List escherRecords = ((AbstractEscherHolderRecord) obj).getEscherRecords();
                searchForPictures(escherRecords, pictures);
            }
        }
        return pictures;
    }

    /**
     * Performs a recursive search for pictures in the given list of escher records.
     *
     * @param escherRecords the escher records.
     * @param pictures the list to populate with the pictures.
     */
    private void searchForPictures(List escherRecords, List pictures)
    {
        Iterator recordIter = escherRecords.iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
            if (obj instanceof EscherRecord)
            {
                EscherRecord escherRecord = (EscherRecord) obj;

                if (escherRecord instanceof EscherBSERecord)
                {
                    EscherBlipRecord blip = ((EscherBSERecord) escherRecord).getBlipRecord();
                    if (blip != null)
                    {
                        // TODO: Some kind of structure.
                        pictures.add(new HSSFPictureData(blip));
                    }
                }

                // Recursive call.
                searchForPictures(escherRecord.getChildRecords(), pictures);
            }
        }
    }

    /**
     * protect a workbook with a password (not encypted, just sets writeprotect
     * flags and the password.
     * @param password to set
     */
    public void writeProtectWorkbook( String password, String username ) {
       this.workbook.writeProtectWorkbook(password, username);
    }

    /**
     * removes the write protect flag
     */
    public void unwriteProtectWorkbook() {
       this.workbook.unwriteProtectWorkbook();
    }

    /**
     * Gets all embedded OLE2 objects from the Workbook.
     *
     * @return the list of embedded objects (a list of {@link HSSFObjectData} objects.)
     */
    public List getAllEmbeddedObjects()
    {
        List objects = new ArrayList();
        for (int i = 0; i < getNumberOfSheets(); i++)
        {
            getAllEmbeddedObjects(getSheetAt(i).getSheet().getRecords(), objects);
        }
        return objects;
    }

    /**
     * Gets all embedded OLE2 objects from the Workbook.
     *
     * @param records the list of records to search.
     * @param objects the list of embedded objects to populate.
     */
    private void getAllEmbeddedObjects(List records, List objects)
    {
        Iterator recordIter = records.iterator();
        while (recordIter.hasNext())
        {
            Object obj = recordIter.next();
            if (obj instanceof ObjRecord)
            {
                // TODO: More convenient way of determining if there is stored binary.
                // TODO: Link to the data stored in the other stream.
                Iterator subRecordIter = ((ObjRecord) obj).getSubRecords().iterator();
                while (subRecordIter.hasNext())
                {
                    Object sub = subRecordIter.next();
                    if (sub instanceof EmbeddedObjectRefSubRecord)
                    {
                        objects.add(new HSSFObjectData((ObjRecord) obj, filesystem));
                    }
                }
            }
        }
    }

    private byte[] newUID()
    {
        byte[] bytes = new byte[16];
        return bytes;
    }
}
