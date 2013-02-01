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


package loci.poi.hssf.model;

import loci.poi.ddf.*;
import loci.poi.hssf.record.*;
import loci.poi.hssf.util.HSSFColor;
import loci.poi.hssf.util.SheetReferences;
import loci.poi.util.POILogFactory;
import loci.poi.util.POILogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Low level model implementation of a Workbook.  Provides creational methods
 * for settings and objects contained in the workbook object.
 * <P>
 * This file contains the low level binary records starting at the workbook's BOF and
 * ending with the workbook's EOF.  Use HSSFWorkbook for a high level representation.
 * <P>
 * The structures of the highlevel API use references to this to perform most of their
 * operations.  Its probably unwise to use these low level structures directly unless you
 * really know what you're doing.  I recommend you read the Microsoft Excel 97 Developer's
 * Kit (Microsoft Press) and the documentation at http://sc.openoffice.org/excelfileformat.pdf
 * before even attempting to use this.
 *
 *
 * @author  Luc Girardin (luc dot girardin at macrofocus dot com)
 * @author  Sergei Kozello (sergeikozello at mail.ru)
 * @author  Shawn Laubach (slaubach at apache dot org) (Data Formats)
 * @author  Andrew C. Oliver (acoliver at apache dot org)
 * @author  Brian Sanders (bsanders at risklabs dot com) - custom palette
 * @author  Dan Sherman (dsherman at isisph.com)
 * @author  Glen Stampoultzis (glens at apache.org)
 * @see loci.poi.hssf.usermodel.HSSFWorkbook
 * @version 1.0-pre
 */

public class Workbook implements Model
{
    private static final int   DEBUG       = POILogger.DEBUG;

//    public static Workbook currentBook = null;

    /**
     * constant used to set the "codepage" wherever "codepage" is set in records
     * (which is duplciated in more than one record)
     */

    private final static short CODEPAGE    = ( short ) 0x4b0;

    /**
     * this contains the Worksheet record objects
     */
    protected WorkbookRecordList        records     = new WorkbookRecordList();

    /**
     * this contains a reference to the SSTRecord so that new stings can be added
     * to it.
     */
    protected SSTRecord        sst         = null;

    /**
     * Holds the Extern Sheet with references to bound sheets
     */
    protected ExternSheetRecord externSheet= null;

    /**
     * holds the "boundsheet" records (aka bundlesheet) so that they can have their
     * reference to their "BOF" marker
     */
    protected ArrayList        boundsheets = new ArrayList();

    protected ArrayList        formats = new ArrayList();

    protected ArrayList        names = new ArrayList();

    protected int              numxfs      = 0;   // hold the number of extended format records
    protected int              numfonts    = 0;   // hold the number of font records
    private short              maxformatid  = -1;  // holds the max format id
    private boolean            uses1904datewindowing  = false;  // whether 1904 date windowing is being used
    private DrawingManager2    drawingManager;
    private List               escherBSERecords = new ArrayList();  // EscherBSERecord
    private WindowOneRecord windowOne;
    private FileSharingRecord fileShare;
    private WriteAccessRecord writeAccess;
    private WriteProtectRecord writeProtect;

    private static POILogger   log = POILogFactory.getLogger(Workbook.class);

    /**
     * Creates new Workbook with no intitialization --useless right now
     * @see #createWorkbook(List)
     */
    public Workbook() {
    }

    /**
     * read support  for low level
     * API.  Pass in an array of Record objects, A Workbook
     * object is constructed and passed back with all of its initialization set
     * to the passed in records and references to those records held. Unlike Sheet
     * workbook does not use an offset (its assumed to be 0) since its first in a file.
     * If you need an offset then construct a new array with a 0 offset or write your
     * own ;-p.
     *
     * @param recs an array of Record objects
     * @return Workbook object
     */
    public static Workbook createWorkbook(List recs) {
        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "Workbook (readfile) created with reclen=",
                    new Integer(recs.size()));
        Workbook  retval  = new Workbook();
        ArrayList records = new ArrayList(recs.size() / 3);

        for (int k = 0; k < recs.size(); k++) {
            Record rec = ( Record ) recs.get(k);

            if (rec.getSid() == EOFRecord.sid) {
                records.add(rec);
                if (log.check( POILogger.DEBUG ))
                    log.log(DEBUG, "found workbook eof record at " + k);
                break;
            }
            switch (rec.getSid()) {

                case BoundSheetRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found boundsheet record at " + k);
                    retval.boundsheets.add(rec);
                    retval.records.setBspos( k );
                    break;

                case SSTRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found sst record at " + k);
                    retval.sst = ( SSTRecord ) rec;
                    break;

                case FontRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found font record at " + k);
                    retval.records.setFontpos( k );
                    retval.numfonts++;
                    break;

                case ExtendedFormatRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found XF record at " + k);
                    retval.records.setXfpos( k );
                    retval.numxfs++;
                    break;

                case TabIdRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found tabid record at " + k);
                    retval.records.setTabpos( k );
                    break;

                case ProtectRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found protect record at " + k);
                    retval.records.setProtpos( k );
                    break;

                case BackupRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found backup record at " + k);
                    retval.records.setBackuppos( k );
                    break;
                case ExternSheetRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found extern sheet record at " + k);
                    retval.externSheet = ( ExternSheetRecord ) rec;
                    break;
                case NameRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found name record at " + k);
                    retval.names.add(rec);
                    //                    retval.records.namepos = k;
                    break;
                case SupBookRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found SupBook record at " + k);
                    //                    retval.records.supbookpos = k;
                    break;
                case FormatRecord.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found format record at " + k);
                    retval.formats.add(rec);
                    retval.maxformatid = retval.maxformatid >= ((FormatRecord)rec).getIndexCode() ? retval.maxformatid : ((FormatRecord)rec).getIndexCode();
                    break;
                case DateWindow1904Record.sid :
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found datewindow1904 record at " + k);
                    retval.uses1904datewindowing = ((DateWindow1904Record)rec).getWindowing() == 1;
                    break;
                case PaletteRecord.sid:
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found palette record at " + k);
                    retval.records.setPalettepos( k );
                    break;
                case WindowOneRecord.sid:
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found WindowOneRecord at " + k);
                    retval.windowOne = (WindowOneRecord) rec; 
                    break;
                case WriteAccessRecord.sid: 
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found WriteAccess at " + k);
                    retval.writeAccess = (WriteAccessRecord) rec;
                    break;
                case WriteProtectRecord.sid: 
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found WriteProtect at " + k);
                    retval.writeProtect = (WriteProtectRecord) rec;
                    break;
                case FileSharingRecord.sid: 
                    if (log.check( POILogger.DEBUG ))
                        log.log(DEBUG, "found FileSharing at " + k);
                    retval.fileShare = (FileSharingRecord) rec;
                default :
            }
            records.add(rec);
        }
        //What if we dont have any ranges and supbooks
        //        if (retval.records.supbookpos == 0) {
        //            retval.records.supbookpos = retval.records.bspos + 1;
        //            retval.records.namepos    = retval.records.supbookpos + 1;
        //        }

        retval.records.setRecords(records);
        
        if (retval.windowOne == null) {
            retval.windowOne = (WindowOneRecord) retval.createWindowOne();
        }
        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "exit create workbook from existing file function");
        return retval;
    }

    /**
     * Creates an empty workbook object with three blank sheets and all the empty
     * fields.  Use this to create a workbook from scratch.
     */
    public static Workbook createWorkbook()
    {
        if (log.check( POILogger.DEBUG ))
            log.log( DEBUG, "creating new workbook from scratch" );
        Workbook retval = new Workbook();
        ArrayList records = new ArrayList( 30 );
        ArrayList formats = new ArrayList( 8 );

        records.add( retval.createBOF() );
        records.add( retval.createInterfaceHdr() );
        records.add( retval.createMMS() );
        records.add( retval.createInterfaceEnd() );
        records.add( retval.createWriteAccess() );
        records.add( retval.createCodepage() );
        records.add( retval.createDSF() );
        records.add( retval.createTabId() );
        retval.records.setTabpos( records.size() - 1 );
        records.add( retval.createFnGroupCount() );
        records.add( retval.createWindowProtect() );
        records.add( retval.createProtect() );
        retval.records.setProtpos( records.size() - 1 );
        records.add( retval.createPassword() );
        records.add( retval.createProtectionRev4() );
        records.add( retval.createPasswordRev4() );
        retval.windowOne = (WindowOneRecord) retval.createWindowOne();
        records.add( retval.windowOne );
        records.add( retval.createBackup() );
        retval.records.setBackuppos( records.size() - 1 );
        records.add( retval.createHideObj() );
        records.add( retval.createDateWindow1904() );
        records.add( retval.createPrecision() );
        records.add( retval.createRefreshAll() );
        records.add( retval.createBookBool() );
        records.add( retval.createFont() );
        records.add( retval.createFont() );
        records.add( retval.createFont() );
        records.add( retval.createFont() );
        retval.records.setFontpos( records.size() - 1 );   // last font record postion
        retval.numfonts = 4;

        // set up format records
        for ( int i = 0; i <= 7; i++ )
        {
            Record rec;
            rec = retval.createFormat( i );
            retval.maxformatid = retval.maxformatid >= ( (FormatRecord) rec ).getIndexCode() ? retval.maxformatid : ( (FormatRecord) rec ).getIndexCode();
            formats.add( rec );
            records.add( rec );
        }
        retval.formats = formats;

        for ( int k = 0; k < 21; k++ )
        {
            records.add( retval.createExtendedFormat( k ) );
            retval.numxfs++;
        }
        retval.records.setXfpos( records.size() - 1 );
        for ( int k = 0; k < 6; k++ )
        {
            records.add( retval.createStyle( k ) );
        }
        records.add( retval.createUseSelFS() );
        for ( int k = 0; k < 1; k++ )
        {   // now just do 1
            BoundSheetRecord bsr =
                    (BoundSheetRecord) retval.createBoundSheet( k );

            records.add( bsr );
            retval.boundsheets.add( bsr );
            retval.records.setBspos( records.size() - 1 );
        }
//        retval.records.supbookpos = retval.records.bspos + 1;
//        retval.records.namepos = retval.records.supbookpos + 2;
        records.add( retval.createCountry() );
        retval.sst = (SSTRecord) retval.createSST();
        records.add( retval.sst );
        records.add( retval.createExtendedSST() );

        records.add( retval.createEOF() );
        retval.records.setRecords(records);
        if (log.check( POILogger.DEBUG ))
            log.log( DEBUG, "exit create new workbook from scratch" );
        return retval;
    }


	/**Retrieves the Builtin NameRecord that matches the name and index
	 * There shouldn't be too many names to make the sequential search too slow
	 * @param name byte representation of the builtin name to match
	 * @param sheetIndex Index to match
	 * @return null if no builtin NameRecord matches
	 */
	public NameRecord getSpecificBuiltinRecord(byte name, int sheetIndex)
	{
	    Iterator iterator = names.iterator();
	    while (iterator.hasNext()) {
	        NameRecord record = ( NameRecord ) iterator.next();
	
	        //print areas are one based
	        if (record.getBuiltInName() == name && record.getIndexToSheet() == sheetIndex) {
	            return record;
	        }
	    }
	    
	    return null;
	    
	}

	/**
	 * Removes the specified Builtin NameRecord that matches the name and index
	 * @param name byte representation of the builtin to match
	 * @param sheetIndex zero-based sheet reference
	 */
	public void removeBuiltinRecord(byte name, int sheetIndex) {
		//the name array is smaller so searching through it should be faster than
		//using the findFirstXXXX methods
		NameRecord record = getSpecificBuiltinRecord(name, sheetIndex);
		if (record != null) {
			names.remove(record);
		}
		
	}

    public int getNumRecords() {
        return records.size();
    }

    /**
     * gets the font record at the given index in the font table.  Remember
     * "There is No Four" (someone at M$ must have gone to Rocky Horror one too
     * many times)
     *
     * @param idx the index to look at (0 or greater but NOT 4)
     * @return FontRecord located at the given index
     */

    public FontRecord getFontRecordAt(int idx) {
        int index = idx;

        if (index > 4) {
            index -= 1;   // adjust for "There is no 4"
        }
        if (index > (numfonts - 1)) {
            throw new ArrayIndexOutOfBoundsException(
            "There are only " + numfonts
            + " font records, you asked for " + idx);
        }
        FontRecord retval =
        ( FontRecord ) records.get((records.getFontpos() - (numfonts - 1)) + index);

        return retval;
    }

    /**
     * creates a new font record and adds it to the "font table".  This causes the
     * boundsheets to move down one, extended formats to move down (so this function moves
     * those pointers as well)
     *
     * @return FontRecord that was just created
     */

    public FontRecord createNewFont() {
        FontRecord rec = ( FontRecord ) createFont();

        records.add(records.getFontpos()+1, rec);
        records.setFontpos( records.getFontpos() + 1 );
        numfonts++;
        return rec;
    }

    /**
     * gets the number of font records
     *
     * @return   number of font records in the "font table"
     */

    public int getNumberOfFontRecords() {
        return numfonts;
    }

    /**
     * Sets the BOF for a given sheet
     *
     * @param sheetnum the number of the sheet to set the positing of the bof for
     * @param pos the actual bof position
     */

    public void setSheetBof(int sheetnum, int pos) {
        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "setting bof for sheetnum =", new Integer(sheetnum),
                " at pos=", new Integer(pos));
        checkSheets(sheetnum);
        (( BoundSheetRecord ) boundsheets.get(sheetnum))
        .setPositionOfBof(pos);
    }

    /**
     * Returns the position of the backup record.
     */

    public BackupRecord getBackupRecord() {
        return ( BackupRecord ) records.get(records.getBackuppos());
    }


    /**
     * sets the name for a given sheet.  If the boundsheet record doesn't exist and
     * its only one more than we have, go ahead and create it.  If its > 1 more than
     * we have, except
     *
     * @param sheetnum the sheet number (0 based)
     * @param sheetname the name for the sheet
     */
    public void setSheetName(int sheetnum, String sheetname ) {
        checkSheets(sheetnum);
        BoundSheetRecord sheet = (BoundSheetRecord)boundsheets.get( sheetnum );
        sheet.setSheetname(sheetname);
        sheet.setSheetnameLength( (byte)sheetname.length() );
    }

    /**
     * Determines whether a workbook contains the privided sheet name.
     *
     * @param name the name to test
     * @param excludeSheetIdx the sheet to exclude from the check or -1 to include all sheets in the check.
     * @return true if the sheet contains the name, false otherwise.
     */
    public boolean doesContainsSheetName( String name, int excludeSheetIdx )
    {
        for ( int i = 0; i < boundsheets.size(); i++ )
        {
            BoundSheetRecord boundSheetRecord = (BoundSheetRecord) boundsheets.get( i );
            if (excludeSheetIdx != i && name.equals(boundSheetRecord.getSheetname()))
                return true;
        }
        return false;
    }

    /**
     * sets the name for a given sheet forcing the encoding. This is STILL A BAD IDEA.
     * Poi now automatically detects unicode
     *
     *@deprecated 3-Jan-06 Simply use setSheetNam e(int sheetnum, String sheetname)
     * @param sheetnum the sheet number (0 based)
     * @param sheetname the name for the sheet
     */    
    public void setSheetName(int sheetnum, String sheetname, short encoding ) {
        checkSheets(sheetnum);
        BoundSheetRecord sheet = (BoundSheetRecord)boundsheets.get( sheetnum );
        sheet.setSheetname(sheetname);
        sheet.setSheetnameLength( (byte)sheetname.length() );
		sheet.setCompressedUnicodeFlag( (byte)encoding );
    }
    
    /**
	 * sets the order of appearance for a given sheet.
	 *
	 * @param sheetname the name of the sheet to reorder
	 * @param pos the position that we want to insert the sheet into (0 based)
	 */
    
    public void setSheetOrder(String sheetname, int pos ) {
	int sheetNumber = getSheetIndex(sheetname);
	//remove the sheet that needs to be reordered and place it in the spot we want
	boundsheets.add(pos, boundsheets.remove(sheetNumber));	
    }

    /**
     * gets the name for a given sheet.
     *
     * @param sheetnum the sheet number (0 based)
     * @return sheetname the name for the sheet
     */

    public String getSheetName(int sheetnum) {
        return (( BoundSheetRecord ) boundsheets.get(sheetnum))
        .getSheetname();
    }

    /**
     * get the sheet's index
     * @param name  sheet name
     * @return sheet index or -1 if it was not found.
     */

    public int getSheetIndex(String name) {
        int retval = -1;

        for (int k = 0; k < boundsheets.size(); k++) {
            String sheet = getSheetName(k);

            if (sheet.equalsIgnoreCase(name)) {
                retval = k;
                break;
            }
        }
        return retval;
    }

    /**
     * if we're trying to address one more sheet than we have, go ahead and add it!  if we're
     * trying to address >1 more than we have throw an exception!
     */

    private void checkSheets(int sheetnum) {
        if ((boundsheets.size()) <= sheetnum) {   // if we're short one add another..
            if ((boundsheets.size() + 1) <= sheetnum) {
                throw new RuntimeException("Sheet number out of bounds!");
            }
            BoundSheetRecord bsr = (BoundSheetRecord ) createBoundSheet(sheetnum);

            records.add(records.getBspos()+1, bsr);
            records.setBspos( records.getBspos() + 1 );
            boundsheets.add(bsr);
            fixTabIdRecord();
        }
    }

    public void removeSheet(int sheetnum) {
        if (boundsheets.size() > sheetnum) {
            records.remove(records.getBspos() - (boundsheets.size() - 1) + sheetnum);
//            records.bspos--;
            boundsheets.remove(sheetnum);
            fixTabIdRecord();
        }
    }

    /**
     * make the tabid record look like the current situation.
     *
     */
    private void fixTabIdRecord() {
        TabIdRecord tir = ( TabIdRecord ) records.get(records.getTabpos());
        short[]     tia = new short[ boundsheets.size() ];

        for (short k = 0; k < tia.length; k++) {
            tia[ k ] = k;
        }
        tir.setTabIdArray(tia);
    }

    /**
     * returns the number of boundsheet objects contained in this workbook.
     *
     * @return number of BoundSheet records
     */

    public int getNumSheets() {
        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "getNumSheets=", new Integer(boundsheets.size()));
        return boundsheets.size();
    }

    /**
     * get the number of ExtendedFormat records contained in this workbook.
     *
     * @return int count of ExtendedFormat records
     */

    public int getNumExFormats() {
        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "getXF=", new Integer(numxfs));
        return numxfs;
    }

    /**
     * gets the ExtendedFormatRecord at the given 0-based index
     *
     * @param index of the Extended format record (0-based)
     * @return ExtendedFormatRecord at the given index
     */

    public ExtendedFormatRecord getExFormatAt(int index) {
        int xfptr = records.getXfpos() - (numxfs - 1);

        xfptr += index;
        ExtendedFormatRecord retval =
        ( ExtendedFormatRecord ) records.get(xfptr);

        return retval;
    }

    /**
     * creates a new Cell-type Extneded Format Record and adds it to the end of
     *  ExtendedFormatRecords collection
     *
     * @return ExtendedFormatRecord that was created
     */

    public ExtendedFormatRecord createCellXF() {
        ExtendedFormatRecord xf = createExtendedFormat();

        records.add(records.getXfpos()+1, xf);
        records.setXfpos( records.getXfpos() + 1 );
        numxfs++;
        return xf;
    }

    /**
     * Adds a string to the SST table and returns its index (if its a duplicate
     * just returns its index and update the counts) ASSUMES compressed unicode
     * (meaning 8bit)
     *
     * @param string the string to be added to the SSTRecord
     *
     * @return index of the string within the SSTRecord
     */

    public int addSSTString(UnicodeString string) {
        if (log.check( POILogger.DEBUG ))
          log.log(DEBUG, "insert to sst string='", string);
        if (sst == null) {
            insertSST();
        }
      return sst.addString(string);
    }

    /**
     * given an index into the SST table, this function returns the corresponding String value
     * @return String containing the SST String
     */

    public UnicodeString getSSTString(int str) {
        if (sst == null) {
            insertSST();
        }
        UnicodeString retval = sst.getString(str);

        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "Returning SST for index=", new Integer(str),
                " String= ", retval);
        return retval;
    }

    /**
     * use this function to add a Shared String Table to an existing sheet (say
     * generated by a different java api) without an sst....
     * @see #createSST()
     * @see loci.poi.hssf.record.SSTRecord
     */

    public void insertSST() {
        if (log.check( POILogger.DEBUG ))
            log.log(DEBUG, "creating new SST via insertSST!");
        sst = ( SSTRecord ) createSST();
        records.add(records.size() - 1, createExtendedSST());
        records.add(records.size() - 2, sst);
    }

    /**
     * Serializes all records int the worksheet section into a big byte array. Use
     * this to write the Workbook out.
     *
     * @return byte array containing the HSSF-only portions of the POIFS file.
     */
     // GJS: Not used so why keep it.
//    public byte [] serialize() {
//        log.log(DEBUG, "Serializing Workbook!");
//        byte[] retval    = null;
//
////         ArrayList bytes     = new ArrayList(records.size());
//        int    arraysize = getSize();
//        int    pos       = 0;
//
//        retval = new byte[ arraysize ];
//        for (int k = 0; k < records.size(); k++) {
//
//            Record record = records.get(k);
////             Let's skip RECALCID records, as they are only use for optimization
//	    if(record.getSid() != RecalcIdRecord.sid || ((RecalcIdRecord)record).isNeeded()) {
//                pos += record.serialize(pos, retval);   // rec.length;
//	    }
//        }
//        log.log(DEBUG, "Exiting serialize workbook");
//        return retval;
//    }

    /**
     * Serializes all records int the worksheet section into a big byte array. Use
     * this to write the Workbook out.
     * @param offset of the data to be written
     * @param data array of bytes to write this to
     */

    public int serialize( int offset, byte[] data )
    {
        if (log.check( POILogger.DEBUG ))
            log.log( DEBUG, "Serializing Workbook with offsets" );

        int pos = 0;

        SSTRecord sst = null;
        int sstPos = 0;
        boolean wroteBoundSheets = false;
        for ( int k = 0; k < records.size(); k++ )
        {

            Record record = records.get( k );
            // Let's skip RECALCID records, as they are only use for optimization
            if ( record.getSid() != RecalcIdRecord.sid || ( (RecalcIdRecord) record ).isNeeded() )
            {
                int len = 0; 
                if (record instanceof SSTRecord)
                {
                    sst = (SSTRecord)record;
                    sstPos = pos;
                }
                if (record.getSid() == ExtSSTRecord.sid && sst != null)
                {
                    record = sst.createExtSSTRecord(sstPos + offset);
                }
                if (record instanceof BoundSheetRecord) {
                     if(!wroteBoundSheets) {
                        for (int i = 0; i < boundsheets.size(); i++) {
                            len+= ((BoundSheetRecord)boundsheets.get(i))
                                             .serialize(pos+offset+len, data);
                        }
                        wroteBoundSheets = true;
                     }
                } else {
                   len = record.serialize( pos + offset, data );
                }
                /////  DEBUG BEGIN /////
//                if (len != record.getRecordSize())
//                    throw new IllegalStateException("Record size does not match serialized bytes.  Serialized size = " + len + " but getRecordSize() returns " + record.getRecordSize());
                /////  DEBUG END /////
                pos += len;   // rec.length;
            }
        }
        if (log.check( POILogger.DEBUG ))
            log.log( DEBUG, "Exiting serialize workbook" );
        return pos;
    }

    public int getSize()
    {
        int retval = 0;

        SSTRecord sst = null;
        for ( int k = 0; k < records.size(); k++ )
        {
            Record record = records.get( k );
            // Let's skip RECALCID records, as they are only use for optimization
            if ( record.getSid() != RecalcIdRecord.sid || ( (RecalcIdRecord) record ).isNeeded() )
            {
                if (record instanceof SSTRecord)
                    sst = (SSTRecord)record;
                if (record.getSid() == ExtSSTRecord.sid && sst != null)
                    retval += sst.calcExtSSTRecordSize();
                else
                    retval += record.getRecordSize();
            }
        }
        return retval;
    }

    /**
     * creates the BOF record
     * @see loci.poi.hssf.record.BOFRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a BOFRecord
     */

    protected Record createBOF() {
        BOFRecord retval = new BOFRecord();

        retval.setVersion(( short ) 0x600);
        retval.setType(( short ) 5);
        retval.setBuild(( short ) 0x10d3);

        //        retval.setBuild((short)0x0dbb);
        retval.setBuildYear(( short ) 1996);
        retval.setHistoryBitMask(0x41);   // was c1 before verify
        retval.setRequiredVersion(0x6);
        return retval;
    }

    /**
     * creates the InterfaceHdr record
     * @see loci.poi.hssf.record.InterfaceHdrRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a InterfaceHdrRecord
     */

    protected Record createInterfaceHdr() {
        InterfaceHdrRecord retval = new InterfaceHdrRecord();

        retval.setCodepage(CODEPAGE);
        return retval;
    }

    /**
     * creates an MMS record
     * @see loci.poi.hssf.record.MMSRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a MMSRecord
     */

    protected Record createMMS() {
        MMSRecord retval = new MMSRecord();

        retval.setAddMenuCount(( byte ) 0);
        retval.setDelMenuCount(( byte ) 0);
        return retval;
    }

    /**
     * creates the InterfaceEnd record
     * @see loci.poi.hssf.record.InterfaceEndRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a InterfaceEndRecord
     */

    protected Record createInterfaceEnd() {
        return new InterfaceEndRecord();
    }

    /**
     * creates the WriteAccess record containing the logged in user's name
     * @see loci.poi.hssf.record.WriteAccessRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a WriteAccessRecord
     */

    protected Record createWriteAccess() {
        WriteAccessRecord retval = new WriteAccessRecord();

        try
        {
            retval.setUsername(System.getProperty("user.name"));
        }
        catch (java.security.AccessControlException e)
        {
                // AccessControlException can occur in a restricted context
                // (client applet/jws application or restricted security server)
                retval.setUsername("POI");
        }
        return retval;
    }

    /**
     * creates the Codepage record containing the constant stored in CODEPAGE
     * @see loci.poi.hssf.record.CodepageRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a CodepageRecord
     */

    protected Record createCodepage() {
        CodepageRecord retval = new CodepageRecord();

        retval.setCodepage(CODEPAGE);
        return retval;
    }

    /**
     * creates the DSF record containing a 0 since HSSF can't even create Dual Stream Files
     * @see loci.poi.hssf.record.DSFRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a DSFRecord
     */

    protected Record createDSF() {
        DSFRecord retval = new DSFRecord();

        retval.setDsf(
        ( short ) 0);   // we don't even support double stream files
        return retval;
    }

    /**
     * creates the TabId record containing an array of 0,1,2.  This release of HSSF
     * always has the default three sheets, no less, no more.
     * @see loci.poi.hssf.record.TabIdRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a TabIdRecord
     */

    protected Record createTabId() {
        TabIdRecord retval     = new TabIdRecord();
        short[]     tabidarray = {
            0
        };

        retval.setTabIdArray(tabidarray);
        return retval;
    }

    /**
     * creates the FnGroupCount record containing the Magic number constant of 14.
     * @see loci.poi.hssf.record.FnGroupCountRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a FnGroupCountRecord
     */

    protected Record createFnGroupCount() {
        FnGroupCountRecord retval = new FnGroupCountRecord();

        retval.setCount(( short ) 14);
        return retval;
    }

    /**
     * creates the WindowProtect record with protect set to false.
     * @see loci.poi.hssf.record.WindowProtectRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a WindowProtectRecord
     */

    protected Record createWindowProtect() {
        WindowProtectRecord retval = new WindowProtectRecord();

        retval.setProtect(
        false);   // by default even when we support it we won't
        return retval;   // want it to be protected
    }

    /**
     * creates the Protect record with protect set to false.
     * @see loci.poi.hssf.record.ProtectRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a ProtectRecord
     */

    protected Record createProtect() {
        ProtectRecord retval = new ProtectRecord();

        retval.setProtect(
        false);   // by default even when we support it we won't
        return retval;   // want it to be protected
    }

    /**
     * creates the Password record with password set to 0.
     * @see loci.poi.hssf.record.PasswordRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a PasswordRecord
     */

    protected Record createPassword() {
        PasswordRecord retval = new PasswordRecord();

        retval.setPassword(( short ) 0);   // no password by default!
        return retval;
    }

    /**
     * creates the ProtectionRev4 record with protect set to false.
     * @see loci.poi.hssf.record.ProtectionRev4Record
     * @see loci.poi.hssf.record.Record
     * @return record containing a ProtectionRev4Record
     */

    protected Record createProtectionRev4() {
        ProtectionRev4Record retval = new ProtectionRev4Record();

        retval.setProtect(false);
        return retval;
    }

    /**
     * creates the PasswordRev4 record with password set to 0.
     * @see loci.poi.hssf.record.PasswordRev4Record
     * @see loci.poi.hssf.record.Record
     * @return record containing a PasswordRev4Record
     */

    protected Record createPasswordRev4() {
        PasswordRev4Record retval = new PasswordRev4Record();

        retval.setPassword(( short ) 0);   // no password by default!
        return retval;
    }

    /**
     * creates the WindowOne record with the following magic values: <P>
     * horizontal hold - 0x168 <P>
     * vertical hold   - 0x10e <P>
     * width           - 0x3a5c <P>
     * height          - 0x23be <P>
     * options         - 0x38 <P>
     * selected tab    - 0 <P>
     * displayed tab   - 0 <P>
     * num selected tab- 0 <P>
     * tab width ratio - 0x258 <P>
     * @see loci.poi.hssf.record.WindowOneRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a WindowOneRecord
     */

    protected Record createWindowOne() {
        WindowOneRecord retval = new WindowOneRecord();

        retval.setHorizontalHold(( short ) 0x168);
        retval.setVerticalHold(( short ) 0x10e);
        retval.setWidth(( short ) 0x3a5c);
        retval.setHeight(( short ) 0x23be);
        retval.setOptions(( short ) 0x38);
        retval.setSelectedTab(( short ) 0x0);
        retval.setDisplayedTab(( short ) 0x0);
        retval.setNumSelectedTabs(( short ) 1);
        retval.setTabWidthRatio(( short ) 0x258);
        return retval;
    }

    /**
     * creates the Backup record with backup set to 0. (loose the data, who cares)
     * @see loci.poi.hssf.record.BackupRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a BackupRecord
     */

    protected Record createBackup() {
        BackupRecord retval = new BackupRecord();

        retval.setBackup(
        ( short ) 0);   // by default DONT save backups of files...just loose data
        return retval;
    }

    /**
     * creates the HideObj record with hide object set to 0. (don't hide)
     * @see loci.poi.hssf.record.HideObjRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a HideObjRecord
     */

    protected Record createHideObj() {
        HideObjRecord retval = new HideObjRecord();

        retval.setHideObj(( short ) 0);   // by default set hide object off
        return retval;
    }

    /**
     * creates the DateWindow1904 record with windowing set to 0. (don't window)
     * @see loci.poi.hssf.record.DateWindow1904Record
     * @see loci.poi.hssf.record.Record
     * @return record containing a DateWindow1904Record
     */

    protected Record createDateWindow1904() {
        DateWindow1904Record retval = new DateWindow1904Record();

        retval.setWindowing(
        ( short ) 0);   // don't EVER use 1904 date windowing...tick tock..
        return retval;
    }

    /**
     * creates the Precision record with precision set to true. (full precision)
     * @see loci.poi.hssf.record.PrecisionRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a PrecisionRecord
     */

    protected Record createPrecision() {
        PrecisionRecord retval = new PrecisionRecord();

        retval.setFullPrecision(
        true);   // always use real numbers in calculations!
        return retval;
    }

    /**
     * creates the RefreshAll record with refreshAll set to true. (refresh all calcs)
     * @see loci.poi.hssf.record.RefreshAllRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a RefreshAllRecord
     */

    protected Record createRefreshAll() {
        RefreshAllRecord retval = new RefreshAllRecord();

        retval.setRefreshAll(false);
        return retval;
    }

    /**
     * creates the BookBool record with saveLinkValues set to 0. (don't save link values)
     * @see loci.poi.hssf.record.BookBoolRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a BookBoolRecord
     */

    protected Record createBookBool() {
        BookBoolRecord retval = new BookBoolRecord();

        retval.setSaveLinkValues(( short ) 0);
        return retval;
    }

    /**
     * creates a Font record with the following magic values: <P>
     * fontheight           = 0xc8<P>
     * attributes           = 0x0<P>
     * color palette index  = 0x7fff<P>
     * bold weight          = 0x190<P>
     * Font Name Length     = 5 <P>
     * Font Name            = Arial <P>
     *
     * @see loci.poi.hssf.record.FontRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a FontRecord
     */

    protected Record createFont() {
        FontRecord retval = new FontRecord();

        retval.setFontHeight(( short ) 0xc8);
        retval.setAttributes(( short ) 0x0);
        retval.setColorPaletteIndex(( short ) 0x7fff);
        retval.setBoldWeight(( short ) 0x190);
        retval.setFontNameLength(( byte ) 5);
        retval.setFontName("Arial");
        return retval;
    }

    /**
     * Creates a FormatRecord object
     * @param id    the number of the format record to create (meaning its position in
     *        a file as M$ Excel would create it.)
     * @return record containing a FormatRecord
     * @see loci.poi.hssf.record.FormatRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createFormat(int id) {   // we'll need multiple editions for
        FormatRecord retval = new FormatRecord();   // the differnt formats

        switch (id) {

            case 0 :
                retval.setIndexCode(( short ) 5);
                retval.setFormatStringLength(( byte ) 0x17);
                retval.setFormatString("\"$\"#,##0_);\\(\"$\"#,##0\\)");
                break;

            case 1 :
                retval.setIndexCode(( short ) 6);
                retval.setFormatStringLength(( byte ) 0x1c);
                retval.setFormatString("\"$\"#,##0_);[Red]\\(\"$\"#,##0\\)");
                break;

            case 2 :
                retval.setIndexCode(( short ) 7);
                retval.setFormatStringLength(( byte ) 0x1d);
                retval.setFormatString("\"$\"#,##0.00_);\\(\"$\"#,##0.00\\)");
                break;

            case 3 :
                retval.setIndexCode(( short ) 8);
                retval.setFormatStringLength(( byte ) 0x22);
                retval.setFormatString(
                "\"$\"#,##0.00_);[Red]\\(\"$\"#,##0.00\\)");
                break;

            case 4 :
                retval.setIndexCode(( short ) 0x2a);
                retval.setFormatStringLength(( byte ) 0x32);
                retval.setFormatString(
                "_(\"$\"* #,##0_);_(\"$\"* \\(#,##0\\);_(\"$\"* \"-\"_);_(@_)");
                break;

            case 5 :
                retval.setIndexCode(( short ) 0x29);
                retval.setFormatStringLength(( byte ) 0x29);
                retval.setFormatString(
                "_(* #,##0_);_(* \\(#,##0\\);_(* \"-\"_);_(@_)");
                break;

            case 6 :
                retval.setIndexCode(( short ) 0x2c);
                retval.setFormatStringLength(( byte ) 0x3a);
                retval.setFormatString(
                "_(\"$\"* #,##0.00_);_(\"$\"* \\(#,##0.00\\);_(\"$\"* \"-\"??_);_(@_)");
                break;

            case 7 :
                retval.setIndexCode(( short ) 0x2b);
                retval.setFormatStringLength(( byte ) 0x31);
                retval.setFormatString(
                "_(* #,##0.00_);_(* \\(#,##0.00\\);_(* \"-\"??_);_(@_)");
                break;
        }
        return retval;
    }

    /**
     * Creates an ExtendedFormatRecord object
     * @param id    the number of the extended format record to create (meaning its position in
     *        a file as MS Excel would create it.)
     *
     * @return record containing an ExtendedFormatRecord
     * @see loci.poi.hssf.record.ExtendedFormatRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createExtendedFormat(int id) {   // we'll need multiple editions
        ExtendedFormatRecord retval = new ExtendedFormatRecord();

        switch (id) {

            case 0 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 1 :
                retval.setFontIndex(( short ) 1);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 2 :
                retval.setFontIndex(( short ) 1);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 3 :
                retval.setFontIndex(( short ) 2);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 4 :
                retval.setFontIndex(( short ) 2);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 5 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 6 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 7 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 8 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 9 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 10 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 11 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 12 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 13 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 14 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff400);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

                // cell records
            case 15 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0);
                retval.setCellOptions(( short ) 0x1);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0x0);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

                // style
            case 16 :
                retval.setFontIndex(( short ) 1);
                retval.setFormatIndex(( short ) 0x2b);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff800);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 17 :
                retval.setFontIndex(( short ) 1);
                retval.setFormatIndex(( short ) 0x29);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff800);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 18 :
                retval.setFontIndex(( short ) 1);
                retval.setFormatIndex(( short ) 0x2c);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff800);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 19 :
                retval.setFontIndex(( short ) 1);
                retval.setFormatIndex(( short ) 0x2a);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff800);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 20 :
                retval.setFontIndex(( short ) 1);
                retval.setFormatIndex(( short ) 0x9);
                retval.setCellOptions(( short ) 0xfffffff5);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0xfffff800);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

                // unused from this point down
            case 21 :
                retval.setFontIndex(( short ) 5);
                retval.setFormatIndex(( short ) 0x0);
                retval.setCellOptions(( short ) 0x1);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0x800);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 22 :
                retval.setFontIndex(( short ) 6);
                retval.setFormatIndex(( short ) 0x0);
                retval.setCellOptions(( short ) 0x1);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0x5c00);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 23 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0x31);
                retval.setCellOptions(( short ) 0x1);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0x5c00);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 24 :
                retval.setFontIndex(( short ) 0);
                retval.setFormatIndex(( short ) 0x8);
                retval.setCellOptions(( short ) 0x1);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0x5c00);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;

            case 25 :
                retval.setFontIndex(( short ) 6);
                retval.setFormatIndex(( short ) 0x8);
                retval.setCellOptions(( short ) 0x1);
                retval.setAlignmentOptions(( short ) 0x20);
                retval.setIndentionOptions(( short ) 0x5c00);
                retval.setBorderOptions(( short ) 0);
                retval.setPaletteOptions(( short ) 0);
                retval.setAdtlPaletteOptions(( short ) 0);
                retval.setFillPaletteOptions(( short ) 0x20c0);
                break;
        }
        return retval;
    }

    /**
     * creates an default cell type ExtendedFormatRecord object.
     * @return ExtendedFormatRecord with intial defaults (cell-type)
     */

    protected ExtendedFormatRecord createExtendedFormat() {
        ExtendedFormatRecord retval = new ExtendedFormatRecord();

        retval.setFontIndex(( short ) 0);
        retval.setFormatIndex(( short ) 0x0);
        retval.setCellOptions(( short ) 0x1);
        retval.setAlignmentOptions(( short ) 0x20);
        retval.setIndentionOptions(( short ) 0);
        retval.setBorderOptions(( short ) 0);
        retval.setPaletteOptions(( short ) 0);
        retval.setAdtlPaletteOptions(( short ) 0);
        retval.setFillPaletteOptions(( short ) 0x20c0);
        retval.setTopBorderPaletteIdx(HSSFColor.BLACK.index);
        retval.setBottomBorderPaletteIdx(HSSFColor.BLACK.index);
        retval.setLeftBorderPaletteIdx(HSSFColor.BLACK.index);
        retval.setRightBorderPaletteIdx(HSSFColor.BLACK.index);
        return retval;
    }

    /**
     * Creates a StyleRecord object
     * @param id        the number of the style record to create (meaning its position in
     *                  a file as MS Excel would create it.
     * @return record containing a StyleRecord
     * @see loci.poi.hssf.record.StyleRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createStyle(int id) {   // we'll need multiple editions
        StyleRecord retval = new StyleRecord();

        switch (id) {

            case 0 :
                retval.setIndex(( short ) 0xffff8010);
                retval.setBuiltin(( byte ) 3);
                retval.setOutlineStyleLevel(( byte ) 0xffffffff);
                break;

            case 1 :
                retval.setIndex(( short ) 0xffff8011);
                retval.setBuiltin(( byte ) 6);
                retval.setOutlineStyleLevel(( byte ) 0xffffffff);
                break;

            case 2 :
                retval.setIndex(( short ) 0xffff8012);
                retval.setBuiltin(( byte ) 4);
                retval.setOutlineStyleLevel(( byte ) 0xffffffff);
                break;

            case 3 :
                retval.setIndex(( short ) 0xffff8013);
                retval.setBuiltin(( byte ) 7);
                retval.setOutlineStyleLevel(( byte ) 0xffffffff);
                break;

            case 4 :
                retval.setIndex(( short ) 0xffff8000);
                retval.setBuiltin(( byte ) 0);
                retval.setOutlineStyleLevel(( byte ) 0xffffffff);
                break;

            case 5 :
                retval.setIndex(( short ) 0xffff8014);
                retval.setBuiltin(( byte ) 5);
                retval.setOutlineStyleLevel(( byte ) 0xffffffff);
                break;
        }
        return retval;
    }

    /**
     * Creates a palette record initialized to the default palette
     * @return a PaletteRecord instance populated with the default colors
     * @see loci.poi.hssf.record.PaletteRecord
     */
    protected PaletteRecord createPalette()
    {
        return new PaletteRecord();
    }
    
    /**
     * Creates the UseSelFS object with the use natural language flag set to 0 (false)
     * @return record containing a UseSelFSRecord
     * @see loci.poi.hssf.record.UseSelFSRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createUseSelFS() {
        UseSelFSRecord retval = new UseSelFSRecord();

        retval.setFlag(( short ) 0);
        return retval;
    }

    /**
     * create a "bound sheet" or "bundlesheet" (depending who you ask) record
     * Always sets the sheet's bof to 0.  You'll need to set that yourself.
     * @param id either sheet 0,1 or 2.
     * @return record containing a BoundSheetRecord
     * @see loci.poi.hssf.record.BoundSheetRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createBoundSheet(int id) {   // 1,2,3 sheets
        BoundSheetRecord retval = new BoundSheetRecord();

        switch (id) {

            case 0 :
                retval.setPositionOfBof(0x0);   // should be set later
                retval.setOptionFlags(( short ) 0);
                retval.setSheetnameLength(( byte ) 0x6);
                retval.setCompressedUnicodeFlag(( byte ) 0);
                retval.setSheetname("Sheet1");
                break;

            case 1 :
                retval.setPositionOfBof(0x0);   // should be set later
                retval.setOptionFlags(( short ) 0);
                retval.setSheetnameLength(( byte ) 0x6);
                retval.setCompressedUnicodeFlag(( byte ) 0);
                retval.setSheetname("Sheet2");
                break;

            case 2 :
                retval.setPositionOfBof(0x0);   // should be set later
                retval.setOptionFlags(( short ) 0);
                retval.setSheetnameLength(( byte ) 0x6);
                retval.setCompressedUnicodeFlag(( byte ) 0);
                retval.setSheetname("Sheet3");
                break;
        }
        return retval;
    }

    /**
     * Creates the Country record with the default country set to 1
     * and current country set to 7 in case of russian locale ("ru_RU") and 1 otherwise
     * @return record containing a CountryRecord
     * @see loci.poi.hssf.record.CountryRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createCountry() {   // what a novel idea, create your own!
        CountryRecord retval = new CountryRecord();

        retval.setDefaultCountry(( short ) 1);

        // from Russia with love ;)
        if ( Locale.getDefault().toString().equals( "ru_RU" ) ) {
	        retval.setCurrentCountry(( short ) 7);
        }
        else {
	        retval.setCurrentCountry(( short ) 1);
        }

        return retval;
    }

    /**
     * Creates the SST record with no strings and the unique/num string set to 0
     * @return record containing a SSTRecord
     * @see loci.poi.hssf.record.SSTRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createSST() {
        return new SSTRecord();
    }

    /**
     * Creates the ExtendedSST record with numstrings per bucket set to 0x8.  HSSF
     * doesn't yet know what to do with this thing, but we create it with nothing in
     * it hardly just to make Excel happy and our sheets look like Excel's
     *
     * @return record containing an ExtSSTRecord
     * @see loci.poi.hssf.record.ExtSSTRecord
     * @see loci.poi.hssf.record.Record
     */

    protected Record createExtendedSST() {
        ExtSSTRecord retval = new ExtSSTRecord();

        retval.setNumStringsPerBucket(( short ) 0x8);
        return retval;
    }

    /**
     * creates the EOF record
     * @see loci.poi.hssf.record.EOFRecord
     * @see loci.poi.hssf.record.Record
     * @return record containing a EOFRecord
     */

    protected Record createEOF() {
        return new EOFRecord();
    }

    public SheetReferences getSheetReferences() {
        SheetReferences refs = new SheetReferences();
        
        if (externSheet != null) {
            for (int k = 0; k < externSheet.getNumOfREFStructures(); k++) {
                
                String sheetName = findSheetNameFromExternSheet((short)k);
                refs.addSheetReference(sheetName, k);
                
            }
        }
        return refs;
    }

    /** finds the sheet name by his extern sheet index
     * @param num extern sheet index
     * @return sheet name
     */
    public String findSheetNameFromExternSheet(short num){
        String result="";

        short indexToSheet = externSheet.getREFRecordAt(num).getIndexToFirstSupBook();
        if (indexToSheet>-1) { //error check, bail out gracefully!
            result = getSheetName(indexToSheet);
        }

        return result;
    }

    /**
     * Finds the sheet index for a particular external sheet number.
     * @param externSheetNumber     The external sheet number to convert
     * @return  The index to the sheet found.
     */
    public int getSheetIndexFromExternSheetIndex(int externSheetNumber)
    {
        if (externSheetNumber >= externSheet.getNumOfREFStructures())
            return -1;
        else
            return externSheet.getREFRecordAt(externSheetNumber).getIndexToFirstSupBook();
    }

    /** returns the extern sheet number for specific sheet number ,
     *  if this sheet doesn't exist in extern sheet , add it
     * @param sheetNumber sheet number
     * @return index to extern sheet
     */
    public short checkExternSheet(int sheetNumber){

        int i = 0;
        boolean flag = false;
        short result = 0;

        if (externSheet == null) {
            externSheet = createExternSheet();
        }

        //Trying to find reference to this sheet
        while (i < externSheet.getNumOfREFStructures() && !flag){
            ExternSheetSubRecord record = externSheet.getREFRecordAt(i);

            if (record.getIndexToFirstSupBook() ==  sheetNumber &&
            record.getIndexToLastSupBook() == sheetNumber){
                flag = true;
                result = (short) i;
            }

            ++i;
        }

        //We Havent found reference to this sheet
        if (!flag) {
            result = addSheetIndexToExternSheet((short) sheetNumber);
        }

        return result;
    }

    private short addSheetIndexToExternSheet(short sheetNumber){
        short result;

        ExternSheetSubRecord record = new ExternSheetSubRecord();
        record.setIndexToFirstSupBook(sheetNumber);
        record.setIndexToLastSupBook(sheetNumber);
        externSheet.addREFRecord(record);
        externSheet.setNumOfREFStructures((short)(externSheet.getNumOfREFStructures() + 1));
        result = (short)(externSheet.getNumOfREFStructures() - 1);

        return result;
    }



    /** gets the total number of names
     * @return number of names
     */
    public int getNumNames(){
        int result = names.size();

        return result;
    }

    /** gets the name record
     * @param index name index
     * @return name record
     */
    public NameRecord getNameRecord(int index){
        NameRecord result = (NameRecord) names.get(index);

        return result;

    }

    /** creates new name
     * @return new name record
     */
    public NameRecord createName(){

        NameRecord name = new NameRecord();

        // Not the most efficient way but the other way was causing too many bugs
        int idx = findFirstRecordLocBySid(ExternSheetRecord.sid);
        if (idx == -1) idx = findFirstRecordLocBySid(SupBookRecord.sid);
        if (idx == -1) idx = findFirstRecordLocBySid(CountryRecord.sid);

        records.add(idx+names.size()+1, name);
        names.add(name);

        return name;
    }


    /** creates new name
     * @return new name record
     */
    public NameRecord addName(NameRecord name)
    {
        // Not the most efficient way but the other way was causing too many bugs
        int idx = findFirstRecordLocBySid(ExternSheetRecord.sid);
        if (idx == -1) idx = findFirstRecordLocBySid(SupBookRecord.sid);
        if (idx == -1) idx = findFirstRecordLocBySid(CountryRecord.sid);
        records.add(idx+names.size()+1, name);
        names.add(name);

        return name;
    }

	/**Generates a NameRecord to represent a built-in region
	 * @return a new NameRecord unless the index is invalid
	 */
	public NameRecord createBuiltInName(byte builtInName, int index)
	{
		if (index == -1 || index+1 > (int)Short.MAX_VALUE) 
			throw new IllegalArgumentException("Index is not valid ["+index+"]");
	    
		NameRecord name = new NameRecord(builtInName, (short)(index));
	            
		addName(name);
	    
		return name;
	}


    /** removes the name
     * @param namenum name index
     */
    public void removeName(int namenum){
        if (names.size() > namenum) {
            int idx = findFirstRecordLocBySid(NameRecord.sid);
            records.remove(idx + namenum);
            names.remove(namenum);
        }

    }

    /** creates a new extern sheet record
     * @return the new extern sheet record
     */
    protected ExternSheetRecord createExternSheet(){
        ExternSheetRecord externSheet = new ExternSheetRecord();

        int idx = findFirstRecordLocBySid(CountryRecord.sid);

        records.add(idx+1, externSheet);
//        records.add(records.supbookpos + 1 , rec);

        //We also adds the supBook for internal reference
        SupBookRecord supbook = new SupBookRecord();

        supbook.setNumberOfSheets((short)getNumSheets());
        //supbook.setFlag();

        records.add(idx+1, supbook);
//        records.add(records.supbookpos + 1 , supbook);

        return externSheet;
    }

    /**
     * Returns a format index that matches the passed in format.  It does not tie into HSSFDataFormat.
     * @param format the format string
     * @param createIfNotFound creates a new format if format not found
     * @return the format id of a format that matches or -1 if none found and createIfNotFound
     */
    public short getFormat(String format, boolean createIfNotFound) {
	Iterator iterator;
	for (iterator = formats.iterator(); iterator.hasNext();) {
	    FormatRecord r = (FormatRecord)iterator.next();
	    if (r.getFormatString().equals(format)) {
		return r.getIndexCode();
	    }
	}

	if (createIfNotFound) {
	    return createFormat(format);
	}

	return -1;
    }

    /**
     * Returns the list of FormatRecords in the workbook.
     * @return ArrayList of FormatRecords in the notebook
     */
    public ArrayList getFormats() {
	return formats;
    }

    /**
     * Creates a FormatRecord, inserts it, and returns the index code.
     * @param format the format string
     * @return the index code of the format record.
     * @see loci.poi.hssf.record.FormatRecord
     * @see loci.poi.hssf.record.Record
     */
    public short createFormat( String format )
    {
//        ++xfpos;	//These are to ensure that positions are updated properly
//        ++palettepos;
//        ++bspos;
        FormatRecord rec = new FormatRecord();
        maxformatid = maxformatid >= (short) 0xa4 ? (short) ( maxformatid + 1 ) : (short) 0xa4; //Starting value from M$ empiracle study.
        rec.setIndexCode( maxformatid );
        rec.setFormatStringLength( (byte) format.length() );
        rec.setFormatString( format );

        int pos = 0;
        while ( pos < records.size() && records.get( pos ).getSid() != FormatRecord.sid )
            pos++;
        pos += formats.size();
        formats.add( rec );
        records.add( pos, rec );
        return maxformatid;
    }

  

    /**
     * Returns the first occurance of a record matching a particular sid.
     */
    public Record findFirstRecordBySid(short sid) {
        for (Iterator iterator = records.iterator(); iterator.hasNext(); ) {
            Record record = ( Record ) iterator.next();
            
            if (record.getSid() == sid) {
                return record;
            }
        }
        return null;
    }

    /**
     * Returns the index of a record matching a particular sid.
     * @param sid   The sid of the record to match
     * @return      The index of -1 if no match made.
     */
    public int findFirstRecordLocBySid(short sid) {
        int index = 0;
        for (Iterator iterator = records.iterator(); iterator.hasNext(); ) {
            Record record = ( Record ) iterator.next();

            if (record.getSid() == sid) {
                return index;
            }
            index ++;
        }
        return -1;
    }

    /**
     * Returns the next occurance of a record matching a particular sid.
     */
    public Record findNextRecordBySid(short sid, int pos) {
        int matches = 0;
        for (Iterator iterator = records.iterator(); iterator.hasNext(); ) {
            Record record = ( Record ) iterator.next();

            if (record.getSid() == sid) {
                if (matches++ == pos)
                    return record;
            }
        }
        return null;
    }

    public List getRecords()
    {
        return records.getRecords();
    }

//    public void insertChartRecords( List chartRecords )
//    {
//        backuppos += chartRecords.size();
//        fontpos += chartRecords.size();
//        palettepos += chartRecords.size();
//        bspos += chartRecords.size();
//        xfpos += chartRecords.size();
//
//        records.addAll(protpos, chartRecords);
//    }

    /**
    * Whether date windowing is based on 1/2/1904 or 1/1/1900.
    * Some versions of Excel (Mac) can save workbooks using 1904 date windowing.
    *
    * @return true if using 1904 date windowing
    */
    public boolean isUsing1904DateWindowing() {
        return uses1904datewindowing;
    }
    
    /**
     * Returns the custom palette in use for this workbook; if a custom palette record
     * does not exist, then it is created.
     */
    public PaletteRecord getCustomPalette()
    {
      PaletteRecord palette;
      int palettePos = records.getPalettepos();
      if (palettePos != -1) {
        Record rec = records.get(palettePos);
        if (rec instanceof PaletteRecord) {
          palette = (PaletteRecord) rec;
        } else throw new RuntimeException("InternalError: Expected PaletteRecord but got a '"+rec+"'");
      }
      else
      {
          palette = createPalette();
          //Add the palette record after the bof which is always the first record
          records.add(1, palette);
          records.setPalettepos(1);
      }
      return palette;
    }

    /**
     * Creates a drawing group record.  If it already exists then it's modified.
     */
    public void createDrawingGroup()
    {

        if (drawingManager == null)
        {
            EscherContainerRecord dggContainer = new EscherContainerRecord();
            EscherDggRecord dgg = new EscherDggRecord();
            EscherOptRecord opt = new EscherOptRecord();
            EscherSplitMenuColorsRecord splitMenuColors = new EscherSplitMenuColorsRecord();

            dggContainer.setRecordId((short) 0xF000);
            dggContainer.setOptions((short) 0x000F);
            dgg.setRecordId(EscherDggRecord.RECORD_ID);
            dgg.setOptions((short)0x0000);
            dgg.setShapeIdMax(1024);
            dgg.setNumShapesSaved(0);
            dgg.setDrawingsSaved(0);
            dgg.setFileIdClusters(new EscherDggRecord.FileIdCluster[] {} );
            drawingManager = new DrawingManager2(dgg);
            EscherContainerRecord bstoreContainer = null;
            if (escherBSERecords.size() > 0)
            {
                bstoreContainer = new EscherContainerRecord();
                bstoreContainer.setRecordId( EscherContainerRecord.BSTORE_CONTAINER );
                bstoreContainer.setOptions( (short) ( (escherBSERecords.size() << 4) | 0xF ) );
                for ( Iterator iterator = escherBSERecords.iterator(); iterator.hasNext(); )
                {
                    EscherRecord escherRecord = (EscherRecord) iterator.next();
                    bstoreContainer.addChildRecord( escherRecord );
                }
            }
            opt.setRecordId((short) 0xF00B);
            opt.setOptions((short) 0x0033);
            opt.addEscherProperty( new EscherBoolProperty(EscherProperties.TEXT__SIZE_TEXT_TO_FIT_SHAPE, 524296) );
            opt.addEscherProperty( new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, 0x08000041) );
            opt.addEscherProperty( new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, 134217792) );
            splitMenuColors.setRecordId((short) 0xF11E);
            splitMenuColors.setOptions((short) 0x0040);
            splitMenuColors.setColor1(0x0800000D);
            splitMenuColors.setColor2(0x0800000C);
            splitMenuColors.setColor3(0x08000017);
            splitMenuColors.setColor4(0x100000F7);

            dggContainer.addChildRecord(dgg);
            if (bstoreContainer != null)
                dggContainer.addChildRecord( bstoreContainer );
            dggContainer.addChildRecord(opt);
            dggContainer.addChildRecord(splitMenuColors);

            int dgLoc = findFirstRecordLocBySid(DrawingGroupRecord.sid);
            if (dgLoc == -1)
            {
                DrawingGroupRecord drawingGroup = new DrawingGroupRecord();
                drawingGroup.addEscherRecord(dggContainer);
                int loc = findFirstRecordLocBySid(CountryRecord.sid);

                getRecords().add(loc+1, drawingGroup);
            }
            else
            {
                DrawingGroupRecord drawingGroup = new DrawingGroupRecord();
                drawingGroup.addEscherRecord(dggContainer);
                getRecords().set(dgLoc, drawingGroup);
            }

        }

    }
    
    public WindowOneRecord getWindowOne() {
        return windowOne;
    }

    public EscherBSERecord getBSERecord(int pictureIndex)
    {
        return (EscherBSERecord)escherBSERecords.get(pictureIndex-1);
    }

    public int addBSERecord(EscherBSERecord e)
    {
        createDrawingGroup();

        // maybe we don't need that as an instance variable anymore
        escherBSERecords.add( e );

        int dgLoc = findFirstRecordLocBySid(DrawingGroupRecord.sid);
        DrawingGroupRecord drawingGroup = (DrawingGroupRecord) getRecords().get( dgLoc );

        EscherContainerRecord dggContainer = (EscherContainerRecord) drawingGroup.getEscherRecord( 0 );
        EscherContainerRecord bstoreContainer;
        if (dggContainer.getChild( 1 ).getRecordId() == EscherContainerRecord.BSTORE_CONTAINER )
        {
            bstoreContainer = (EscherContainerRecord) dggContainer.getChild( 1 );
        }
        else
        {
            bstoreContainer = new EscherContainerRecord();
            bstoreContainer.setRecordId( EscherContainerRecord.BSTORE_CONTAINER );
            dggContainer.getChildRecords().add( 1, bstoreContainer );
        }
        bstoreContainer.setOptions( (short) ( (escherBSERecords.size() << 4) | 0xF ) );

        bstoreContainer.addChildRecord( e );

        return escherBSERecords.size();
    }

    public DrawingManager2 getDrawingManager()
    {
        return drawingManager;
    }

    public WriteProtectRecord getWriteProtect() {
        if (this.writeProtect == null) {
           this.writeProtect = new WriteProtectRecord();
           int i = 0;
           for (i = 0; 
                i < records.size() && !(records.get(i) instanceof BOFRecord); 
                i++) {
           }
           records.add(i+1,this.writeProtect);
        }
        return this.writeProtect;
    }

    public WriteAccessRecord getWriteAccess() {
        if (this.writeAccess == null) {
           this.writeAccess = (WriteAccessRecord)createWriteAccess();
           int i = 0;
           for (i = 0; 
                i < records.size() && !(records.get(i) instanceof InterfaceEndRecord); 
                i++) {
           }
           records.add(i+1,this.writeAccess);
        }
        return this.writeAccess;
    }

    public FileSharingRecord getFileSharing() {
        if (this.fileShare == null) {
           this.fileShare = new FileSharingRecord();
           int i = 0;
           for (i = 0; 
                i < records.size() && !(records.get(i) instanceof WriteAccessRecord); 
                i++) {
           }
           records.add(i+1,this.fileShare);
        }
        return this.fileShare;
    }

    /**
     * protect a workbook with a password (not encypted, just sets writeprotect
     * flags and the password.
     * @param password to set
     */
    public void writeProtectWorkbook( String password, String username ) {
        int protIdx = -1;
        FileSharingRecord frec = getFileSharing();
        WriteAccessRecord waccess = getWriteAccess();
        WriteProtectRecord wprotect = getWriteProtect();
        frec.setReadOnly((short)1);
        frec.setPassword(FileSharingRecord.hashPassword(password));
        frec.setUsername(username);
        waccess.setUsername(username);
    }

    /**
     * removes the write protect flag
     */
    public void unwriteProtectWorkbook() {
        records.remove(fileShare);
        records.remove(writeProtect);
        fileShare = null;
        writeProtect = null;
    }

}

