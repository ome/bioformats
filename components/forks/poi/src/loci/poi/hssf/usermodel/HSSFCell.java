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
 * Cell.java
 *
 * Created on September 30, 2001, 3:46 PM
 */
package loci.poi.hssf.usermodel;

import loci.poi.hssf.model.FormulaParser;
import loci.poi.hssf.model.Sheet;
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.*;
import loci.poi.hssf.record.aggregates.FormulaRecordAggregate;
import loci.poi.hssf.record.formula.Ptg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * High level representation of a cell in a row of a spreadsheet.
 * Cells can be numeric, formula-based or string-based (text).  The cell type
 * specifies this.  String cells cannot conatin numbers and numeric cells cannot
 * contain strings (at least according to our model).  Client apps should do the
 * conversions themselves.  Formula cells have the formula string, as well as 
 * the formula result, which can be numeric or string. 
 * <p>
 * Cells should have their number (0 based) before being added to a row.  Only
 * cells that have values should be added.
 * <p>
 *
 * @author  Andrew C. Oliver (acoliver at apache dot org)
 * @author  Dan Sherman (dsherman at isisph.com)
 * @author  Brian Sanders (kestrel at burdell dot org) Active Cell support
 * @author  Yegor Kozlov cell comments support
 * @version 1.0-pre
 */

public class HSSFCell
{

    /**
     * Numeric Cell type (0)
     * @see #setCellType(int)
     * @see #getCellType()
     */

    public final static int          CELL_TYPE_NUMERIC           = 0;

    /**
     * String Cell type (1)
     * @see #setCellType(int)
     * @see #getCellType()
     */

    public final static int          CELL_TYPE_STRING            = 1;

    /**
     * Formula Cell type (2)
     * @see #setCellType(int)
     * @see #getCellType()
     */

    public final static int          CELL_TYPE_FORMULA           = 2;

    /**
     * Blank Cell type (3)
     * @see #setCellType(int)
     * @see #getCellType()
     */

    public final static int          CELL_TYPE_BLANK             = 3;

    /**
     * Boolean Cell type (4)
     * @see #setCellType(int)
     * @see #getCellType()
     */

    public final static int          CELL_TYPE_BOOLEAN           = 4;

    /**
     * Error Cell type (5)
     * @see #setCellType(int)
     * @see #getCellType()
     */

    public final static int          CELL_TYPE_ERROR             = 5;
    public final static short        ENCODING_UNCHANGED          = -1;
    public final static short        ENCODING_COMPRESSED_UNICODE = 0;
    public final static short        ENCODING_UTF_16             = 1;
    private int                      cellType;
    private HSSFRichTextString       stringValue;
    private short                    encoding = ENCODING_UNCHANGED;
    private Workbook                 book;
    private Sheet                    sheet;
    private CellValueRecordInterface record;
    private HSSFComment              comment;

    /**
     * Creates new Cell - Should only be called by HSSFRow.  This creates a cell
     * from scratch.
     * <p>
     * When the cell is initially created it is set to CELL_TYPE_BLANK. Cell types
     * can be changed/overwritten by calling setCellValue with the appropriate
     * type as a parameter although conversions from one type to another may be
     * prohibited.
     *
     * @param book - Workbook record of the workbook containing this cell
     * @param sheet - Sheet record of the sheet containing this cell
     * @param row   - the row of this cell
     * @param col   - the column for this cell
     *
     * @see loci.poi.hssf.usermodel.HSSFRow#createCell(short)
     */

    //protected HSSFCell(Workbook book, Sheet sheet, short row, short col)
    protected HSSFCell(Workbook book, Sheet sheet, int row, short col)
    {
        checkBounds(col);
        stringValue  = null;
        this.book    = book;
        this.sheet   = sheet;

        // Relying on the fact that by default the cellType is set to 0 which
        // is different to CELL_TYPE_BLANK hence the following method call correctly
        // creates a new blank cell.
        short xfindex = sheet.getXFIndexForColAt(col);
        setCellType(CELL_TYPE_BLANK, false, row, col,xfindex);
    }

    /**
     * Creates new Cell - Should only be called by HSSFRow.  This creates a cell
     * from scratch.
     *
     * @param book - Workbook record of the workbook containing this cell
     * @param sheet - Sheet record of the sheet containing this cell
     * @param row   - the row of this cell
     * @param col   - the column for this cell
     * @param type  - CELL_TYPE_NUMERIC, CELL_TYPE_STRING, CELL_TYPE_FORMULA, CELL_TYPE_BLANK,
     *                CELL_TYPE_BOOLEAN, CELL_TYPE_ERROR
     *                Type of cell
     * @see loci.poi.hssf.usermodel.HSSFRow#createCell(short,int)
     */

    //protected HSSFCell(Workbook book, Sheet sheet, short row, short col,
    protected HSSFCell(Workbook book, Sheet sheet, int row, short col,
                       int type)
    {
        checkBounds(col);
        cellType     = -1; // Force 'setCellType' to create a first Record
        stringValue  = null;
        this.book    = book;
        this.sheet   = sheet;
        
        short xfindex = sheet.getXFIndexForColAt(col);
        setCellType(type,false,row,col,xfindex);
    }

    /**
     * Creates an HSSFCell from a CellValueRecordInterface.  HSSFSheet uses this when
     * reading in cells from an existing sheet.
     *
     * @param book - Workbook record of the workbook containing this cell
     * @param sheet - Sheet record of the sheet containing this cell
     * @param cval - the Cell Value Record we wish to represent
     */

    //protected HSSFCell(Workbook book, Sheet sheet, short row,
    protected HSSFCell(Workbook book, Sheet sheet, int row,
                       CellValueRecordInterface cval)
    {
        record      = cval;
        cellType    = determineType(cval);
        stringValue = null;
        this.book   = book;
        this.sheet  = sheet;
        switch (cellType)
        {
            case CELL_TYPE_STRING :
                stringValue = new HSSFRichTextString(book, (LabelSSTRecord ) cval);
                break;

            case CELL_TYPE_BLANK :
                break;

            case CELL_TYPE_FORMULA :
                stringValue=new HSSFRichTextString(((FormulaRecordAggregate) cval).getStringValue());
                break;
        }
        ExtendedFormatRecord xf = book.getExFormatAt(cval.getXFIndex());

        setCellStyle(new HSSFCellStyle(( short ) cval.getXFIndex(), xf));
    }

    /**
     * private constructor to prevent blank construction
     */
    private HSSFCell()
    {
    }

    /**
     * used internally -- given a cell value record, figure out its type
     */
    private int determineType(CellValueRecordInterface cval)
    {
        Record record = ( Record ) cval;
        int    sid    = record.getSid();
        int    retval = 0;

        switch (sid)
        {

            case NumberRecord.sid :
                retval = HSSFCell.CELL_TYPE_NUMERIC;
                break;

            case BlankRecord.sid :
                retval = HSSFCell.CELL_TYPE_BLANK;
                break;

            case LabelSSTRecord.sid :
                retval = HSSFCell.CELL_TYPE_STRING;
                break;

            case FormulaRecordAggregate.sid :
                retval = HSSFCell.CELL_TYPE_FORMULA;
                break;

            case BoolErrRecord.sid :
                BoolErrRecord boolErrRecord = ( BoolErrRecord ) record;

                retval = (boolErrRecord.isBoolean())
                         ? HSSFCell.CELL_TYPE_BOOLEAN
                         : HSSFCell.CELL_TYPE_ERROR;
                break;
        }
        return retval;
    }
    
    /**
     * Returns the Workbook that this Cell is bound to
     * @return
     */
    protected Workbook getBoundWorkbook() {
    	return book;
    }

    /**
     * set the cell's number within the row (0 based)
     * @param num  short the cell number
     */

    public void setCellNum(short num)
    {
        record.setColumn(num);
    }

    /**
     *  get the cell's number within the row
     * @return short reperesenting the column number (logical!)
     */

    public short getCellNum()
    {
        return record.getColumn();
    }

    /**
     * set the cells type (numeric, formula or string)
     * @see #CELL_TYPE_NUMERIC
     * @see #CELL_TYPE_STRING
     * @see #CELL_TYPE_FORMULA
     * @see #CELL_TYPE_BLANK
     * @see #CELL_TYPE_BOOLEAN
     * @see #CELL_TYPE_ERROR
     */

    public void setCellType(int cellType)
    {
        int row=record.getRow();
        short col=record.getColumn();
        short styleIndex=record.getXFIndex();
        setCellType(cellType, true, row, col, styleIndex);
    }

    /**
     * sets the cell type. The setValue flag indicates whether to bother about
     *  trying to preserve the current value in the new record if one is created.
     *  <p>
     *  The @see #setCellValue method will call this method with false in setValue
     *  since it will overwrite the cell value later
     *
     */

    private void setCellType(int cellType, boolean setValue, int row,short col, short styleIndex)
    {

        // if (cellType == CELL_TYPE_FORMULA)
        // {
        // throw new RuntimeException(
        // "Formulas have not been implemented in this release");
        // }
        if (cellType > CELL_TYPE_ERROR)
        {
            throw new RuntimeException("I have no idea what type that is!");
        }
        switch (cellType)
        {

            case CELL_TYPE_FORMULA :
                FormulaRecordAggregate frec = null;

                if (cellType != this.cellType)
                {
                    frec = new FormulaRecordAggregate(new FormulaRecord(),null);
                }
                else
                {
                    frec = ( FormulaRecordAggregate ) record;
                }
                frec.setColumn(col);
                if (setValue)
                {
                    frec.getFormulaRecord().setValue(getNumericCellValue());
                }
                frec.setXFIndex(styleIndex);
                frec.setRow(row);
                record = frec;
                break;

            case CELL_TYPE_NUMERIC :
                NumberRecord nrec = null;

                if (cellType != this.cellType)
                {
                    nrec = new NumberRecord();
                }
                else
                {
                    nrec = ( NumberRecord ) record;
                }
                nrec.setColumn(col);
                if (setValue)
                {
                    nrec.setValue(getNumericCellValue());
                }
                nrec.setXFIndex(styleIndex);
                nrec.setRow(row);
                record = nrec;
                break;

            case CELL_TYPE_STRING :
                LabelSSTRecord lrec = null;

                if (cellType != this.cellType)
                {
                    lrec = new LabelSSTRecord();
                }
                else
                {
                    lrec = ( LabelSSTRecord ) record;
                }
                lrec.setColumn(col);
                lrec.setRow(row);
                lrec.setXFIndex(styleIndex);
                if (setValue)
                {
                    if ((getStringCellValue() != null)
                            && (!getStringCellValue().equals("")))
                    {
                        int sst = 0;

                        UnicodeString str = getRichStringCellValue().getUnicodeString();
//jmh                        if (encoding == ENCODING_COMPRESSED_UNICODE)
//jmh                        {
//                      jmh                            str.setCompressedUnicode();
//                      jmh                        } else if (encoding == ENCODING_UTF_16)
//                      jmh                        {
//                      jmh                            str.setUncompressedUnicode();
//                      jmh                        }
                        sst = book.addSSTString(str);
                        lrec.setSSTIndex(sst);
                        getRichStringCellValue().setUnicodeString(book.getSSTString(sst));
                    }
                }
                record = lrec;
                break;

            case CELL_TYPE_BLANK :
                BlankRecord brec = null;

                if (cellType != this.cellType)
                {
                    brec = new BlankRecord();
                }
                else
                {
                    brec = ( BlankRecord ) record;
                }
                brec.setColumn(col);

                // During construction the cellStyle may be null for a Blank cell.
                brec.setXFIndex(styleIndex);
                brec.setRow(row);
                record = brec;
                break;

            case CELL_TYPE_BOOLEAN :
                BoolErrRecord boolRec = null;

                if (cellType != this.cellType)
                {
                    boolRec = new BoolErrRecord();
                }
                else
                {
                    boolRec = ( BoolErrRecord ) record;
                }
                boolRec.setColumn(col);
                if (setValue)
                {
                    boolRec.setValue(getBooleanCellValue());
                }
                boolRec.setXFIndex(styleIndex);
                boolRec.setRow(row);
                record = boolRec;
                break;

            case CELL_TYPE_ERROR :
                BoolErrRecord errRec = null;

                if (cellType != this.cellType)
                {
                    errRec = new BoolErrRecord();
                }
                else
                {
                    errRec = ( BoolErrRecord ) record;
                }
                errRec.setColumn(col);
                if (setValue)
                {
                    errRec.setValue(getErrorCellValue());
                }
                errRec.setXFIndex(styleIndex);
                errRec.setRow(row);
                record = errRec;
                break;
        }
        if (cellType != this.cellType && 
            this.cellType!=-1 )  // Special Value to indicate an uninitialized Cell
        {
            int loc = sheet.getLoc();

            sheet.replaceValueRecord(record);
            sheet.setLoc(loc);
        }
        this.cellType = cellType;
    }

    /**
     * get the cells type (numeric, formula or string)
     * @see #CELL_TYPE_STRING
     * @see #CELL_TYPE_NUMERIC
     * @see #CELL_TYPE_FORMULA
     * @see #CELL_TYPE_BOOLEAN
     * @see #CELL_TYPE_ERROR
     */

    public int getCellType()
    {
        return cellType;
    }

    /**
     * set a numeric value for the cell
     *
     * @param value  the numeric value to set this cell to.  For formulas we'll set the
     *        precalculated value, for numerics we'll set its value. For other types we
     *        will change the cell to a numeric cell and set its value.
     */
    public void setCellValue(double value)
    {
        int row=record.getRow();
        short col=record.getColumn();
        short styleIndex=record.getXFIndex();
        if ((cellType != CELL_TYPE_NUMERIC) && (cellType != CELL_TYPE_FORMULA))
        {
            setCellType(CELL_TYPE_NUMERIC, false, row, col, styleIndex);
        }
        (( NumberRecord ) record).setValue(value);
    }

    /**
     * set a date value for the cell. Excel treats dates as numeric so you will need to format the cell as
     * a date.
     *
     * @param value  the date value to set this cell to.  For formulas we'll set the
     *        precalculated value, for numerics we'll set its value. For other types we
     *        will change the cell to a numeric cell and set its value.
     */
    public void setCellValue(Date value)
    {
        setCellValue(HSSFDateUtil.getExcelDate(value));
    }

    /**
     * set a date value for the cell. Excel treats dates as numeric so you will need to format the cell as
     * a date.
     *
     * @param value  the date value to set this cell to.  For formulas we'll set the
     *        precalculated value, for numerics we'll set its value. For othertypes we
     *        will change the cell to a numeric cell and set its value.
     */
    public void setCellValue(Calendar value)
    {
        setCellValue(value.getTime());
    }

    /**
     * set a string value for the cell. Please note that if you are using
     * full 16 bit unicode you should call <code>setEncoding()</code> first.
     *
     * @param value  value to set the cell to.  For formulas we'll set the formula
     * string, for String cells we'll set its value.  For other types we will
     * change the cell to a string cell and set its value.
     * If value is null then we will change the cell to a Blank cell.
     * @deprecated Use setCellValue(HSSFRichTextString) instead.
     */

    public void setCellValue(String value)
    {
      HSSFRichTextString str = new HSSFRichTextString(value);
      setCellValue(str);
    }

    /**
     * set a string value for the cell. Please note that if you are using
     * full 16 bit unicode you should call <code>setEncoding()</code> first.
     *
     * @param value  value to set the cell to.  For formulas we'll set the formula
     * string, for String cells we'll set its value.  For other types we will
     * change the cell to a string cell and set its value.
     * If value is null then we will change the cell to a Blank cell.
     */

    public void setCellValue(HSSFRichTextString value)
    {
        int row=record.getRow();
        short col=record.getColumn();
        short styleIndex=record.getXFIndex();
        if (value == null)
        {
            setCellType(CELL_TYPE_BLANK, false, row, col, styleIndex);
        }
        else
        {
            if ((cellType != CELL_TYPE_STRING ) && ( cellType != CELL_TYPE_FORMULA))
            {
                setCellType(CELL_TYPE_STRING, false, row, col, styleIndex);
            }
            int index = 0;

            UnicodeString str = value.getUnicodeString();            
//          jmh            if (encoding == ENCODING_COMPRESSED_UNICODE)
//          jmh            {
//          jmh                str.setCompressedUnicode();
//          jmh            } else if (encoding == ENCODING_UTF_16)
//          jmh            {
//          jmh                str.setUncompressedUnicode();
//          jmh            }
            index = book.addSSTString(str);            
            (( LabelSSTRecord ) record).setSSTIndex(index);
            stringValue = value;
            stringValue.setWorkbookReferences(book, (( LabelSSTRecord ) record));
            stringValue.setUnicodeString(book.getSSTString(index));            
        }
    }

    public void setCellFormula(String formula) {
        int row=record.getRow();
        short col=record.getColumn();
        short styleIndex=record.getXFIndex();
        //Workbook.currentBook=book;
        if (formula==null) {
            setCellType(CELL_TYPE_BLANK,false,row,col,styleIndex);
        } else {
            setCellType(CELL_TYPE_FORMULA,false,row,col,styleIndex);
            FormulaRecordAggregate rec = (FormulaRecordAggregate) record;
            FormulaRecord frec = rec.getFormulaRecord();
            frec.setOptions(( short ) 2);
            frec.setValue(0);
            
            //only set to default if there is no extended format index already set
            if (rec.getXFIndex() == (short)0) rec.setXFIndex(( short ) 0x0f);
            FormulaParser fp = new FormulaParser(formula+";",book);
            fp.parse();
            Ptg[] ptg  = fp.getRPNPtg();
            int   size = 0;

            // clear the Ptg Stack
            for (int i=0, iSize=frec.getNumberOfExpressionTokens(); i<iSize; i++) {
                frec.popExpressionToken();
            }

            // fill the Ptg Stack with Ptgs of new formula
            for (int k = 0; k < ptg.length; k++) {
                size += ptg[ k ].getSize();
                frec.pushExpressionToken(ptg[ k ]);
            }
            rec.getFormulaRecord().setExpressionLength(( short ) size);
            //Workbook.currentBook = null;
        }
    }

    public String getCellFormula() {
        //Workbook.currentBook=book;
        String retval = FormulaParser.toFormulaString(book, ((FormulaRecordAggregate)record).getFormulaRecord().getParsedExpression());
        //Workbook.currentBook=null;
        return retval;
    }


    /**
     * get the value of the cell as a number.  For strings we throw an exception.
     * For blank cells we return a 0.
     */

    public double getNumericCellValue()
    {
        if (cellType == CELL_TYPE_BLANK)
        {
            return 0;
        }
        if (cellType == CELL_TYPE_STRING)
        {
            throw new NumberFormatException(
                "You cannot get a numeric value from a String based cell");
        }
        if (cellType == CELL_TYPE_BOOLEAN)
        {
            throw new NumberFormatException(
                "You cannot get a numeric value from a boolean cell");
        }
        if (cellType == CELL_TYPE_ERROR)
        {
            throw new NumberFormatException(
                "You cannot get a numeric value from an error cell");
        }
        if(cellType == CELL_TYPE_NUMERIC)
        {
          return ((NumberRecord)record).getValue();
        }
        if(cellType == CELL_TYPE_FORMULA)
        {
          return ((FormulaRecordAggregate)record).getFormulaRecord().getValue();
        }
        throw new NumberFormatException("Unknown Record Type in Cell:"+cellType);
    }

    /**
     * get the value of the cell as a date.  For strings we throw an exception.
     * For blank cells we return a null.
     */
    public Date getDateCellValue()
    {
        if (cellType == CELL_TYPE_BLANK)
        {
            return null;
        }
        if (cellType == CELL_TYPE_STRING)
        {
            throw new NumberFormatException(
                "You cannot get a date value from a String based cell");
        }
        if (cellType == CELL_TYPE_BOOLEAN)
        {
            throw new NumberFormatException(
                "You cannot get a date value from a boolean cell");
        }
        if (cellType == CELL_TYPE_ERROR)
        {
            throw new NumberFormatException(
                "You cannot get a date value from an error cell");
        }
        double value=this.getNumericCellValue();
        if (book.isUsing1904DateWindowing()) {
            return HSSFDateUtil.getJavaDate(value,true);
        }
        else {
            return HSSFDateUtil.getJavaDate(value,false);
        }
    }

    /**
     * get the value of the cell as a string - for numeric cells we throw an exception.
     * For blank cells we return an empty string.
     * For formulaCells that are not string Formulas, we return empty String
     * @deprecated Use the HSSFRichTextString return
     */

    public String getStringCellValue()
    {
      HSSFRichTextString str = getRichStringCellValue();
      return str.getString();
    }

    /**
     * get the value of the cell as a string - for numeric cells we throw an exception.
     * For blank cells we return an empty string.
     * For formulaCells that are not string Formulas, we return empty String
     */

    public HSSFRichTextString getRichStringCellValue()
    {
        if (cellType == CELL_TYPE_BLANK)
        {
            return new HSSFRichTextString("");
        }
        if (cellType == CELL_TYPE_NUMERIC)
        {
            throw new NumberFormatException(
                "You cannot get a string value from a numeric cell");
        }
        if (cellType == CELL_TYPE_BOOLEAN)
        {
            throw new NumberFormatException(
                "You cannot get a string value from a boolean cell");
        }
        if (cellType == CELL_TYPE_ERROR)
        {
            throw new NumberFormatException(
                "You cannot get a string value from an error cell");
        }
        if (cellType == CELL_TYPE_FORMULA) 
        {
            if (stringValue==null) return new HSSFRichTextString("");
        }
        return stringValue;
    }

    /**
     * set a boolean value for the cell
     *
     * @param value the boolean value to set this cell to.  For formulas we'll set the
     *        precalculated value, for booleans we'll set its value. For other types we
     *        will change the cell to a boolean cell and set its value.
     */

    public void setCellValue(boolean value)
    {
        int row=record.getRow();
        short col=record.getColumn();
        short styleIndex=record.getXFIndex();
        if ((cellType != CELL_TYPE_BOOLEAN ) && ( cellType != CELL_TYPE_FORMULA))
        {
            setCellType(CELL_TYPE_BOOLEAN, false, row, col, styleIndex);
        }
        (( BoolErrRecord ) record).setValue(value);
    }

    /**
     * set a error value for the cell
     *
     * @param value the error value to set this cell to.  For formulas we'll set the
     *        precalculated value ??? IS THIS RIGHT??? , for errors we'll set
     *        its value. For other types we will change the cell to an error
     *        cell and set its value.
     */

    public void setCellErrorValue(byte value)
    {
        int row=record.getRow();
        short col=record.getColumn();
        short styleIndex=record.getXFIndex();
        if ((cellType != CELL_TYPE_ERROR) && (cellType != CELL_TYPE_FORMULA))
        {
            setCellType(CELL_TYPE_ERROR, false, row, col, styleIndex);
        }
        (( BoolErrRecord ) record).setValue(value);
    }

    /**
     * get the value of the cell as a boolean.  For strings, numbers, and errors, we throw an exception.
     * For blank cells we return a false.
     */

    public boolean getBooleanCellValue()
    {
        if (cellType == CELL_TYPE_BOOLEAN)
        {
            return (( BoolErrRecord ) record).getBooleanValue();
        }
        if (cellType == CELL_TYPE_BLANK)
        {
            return false;
        }
        throw new NumberFormatException(
            "You cannot get a boolean value from a non-boolean cell");
    }

    /**
     * get the value of the cell as an error code.  For strings, numbers, and booleans, we throw an exception.
     * For blank cells we return a 0.
     */

    public byte getErrorCellValue()
    {
        if (cellType == CELL_TYPE_ERROR)
        {
            return (( BoolErrRecord ) record).getErrorValue();
        }
        if (cellType == CELL_TYPE_BLANK)
        {
            return ( byte ) 0;
        }
        throw new NumberFormatException(
            "You cannot get an error value from a non-error cell");
    }

    /**
     * set the style for the cell.  The style should be an HSSFCellStyle created/retreived from
     * the HSSFWorkbook.
     *
     * @param style  reference contained in the workbook
     * @see loci.poi.hssf.usermodel.HSSFWorkbook#createCellStyle()
     * @see loci.poi.hssf.usermodel.HSSFWorkbook#getCellStyleAt(short)
     */

    public void setCellStyle(HSSFCellStyle style)
    {
        record.setXFIndex(style.getIndex());
    }

    /**
     * get the style for the cell.  This is a reference to a cell style contained in the workbook
     * object.
     * @see loci.poi.hssf.usermodel.HSSFWorkbook#getCellStyleAt(short)
     */

    public HSSFCellStyle getCellStyle()
    {
      short styleIndex=record.getXFIndex();
      ExtendedFormatRecord xf = book.getExFormatAt(styleIndex);
      return new HSSFCellStyle(styleIndex, xf);
    }

    /**
     * used for internationalization, currently -1 for unchanged, 0 for compressed unicode or 1 for 16-bit
     *
     * @see #ENCODING_UNCHANGED
     * @see #ENCODING_COMPRESSED_UNICODE
     * @see #ENCODING_UTF_16
     *
     * @return -1, 1 or 0 for unchanged, compressed or uncompressed (used only with String type)
     * 
     * @deprecated As of 3-Jan-06 POI now automatically handles Unicode without forcing the encoding.
     */
    public short getEncoding()
    {
        return encoding;
    }

    /**
     * set the encoding to either 8 or 16 bit. (US/UK use 8-bit, rest of the western world use 16bit)
     *
     * @see #ENCODING_UNCHANGED
     * @see #ENCODING_COMPRESSED_UNICODE
     * @see #ENCODING_UTF_16
     *
     * @param encoding either ENCODING_COMPRESSED_UNICODE (0) or ENCODING_UTF_16 (1)
     * @deprecated As of 3-Jan-06 POI now automatically handles Unicode without forcing the encoding.
     */

    public void setEncoding(short encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Should only be used by HSSFSheet and friends.  Returns the low level CellValueRecordInterface record
     *
     * @return CellValueRecordInterface representing the cell via the low level api.
     */

    protected CellValueRecordInterface getCellValueRecord()
    {
        return record;
    }

    /**
     * @throws RuntimeException if the bounds are exceeded.
     */
    private void checkBounds(int cellNum) {
      if (cellNum > 255) {
          throw new RuntimeException("You cannot have more than 255 columns "+
                    "in a given row (IV).  Because Excel can't handle it");
      }
      else if (cellNum < 0) {
          throw new RuntimeException("You cannot reference columns with an index of less then 0.");
      }
    }
    
    /**
     * Sets this cell as the active cell for the worksheet
     */
    public void setAsActiveCell()
    {
        int row=record.getRow();
        short col=record.getColumn();
        this.sheet.setActiveCellRow(row);
        this.sheet.setActiveCellCol(col);
    }
    
    /**
     * Returns a string representation of the cell
     * 
     * This method returns a simple representation, 
     * anthing more complex should be in user code, with
     * knowledge of the semantics of the sheet being processed. 
     * 
     * Formula cells return the formula string, 
     * rather than the formula result. 
     * Dates are displayed in dd-MMM-yyyy format
     * Errors are displayed as #ERR&lt;errIdx&gt;
     */
    public String toString() {
    	switch     (getCellType()) {
    		case CELL_TYPE_BLANK:
    			return "";
    		case CELL_TYPE_BOOLEAN:
    			return getBooleanCellValue()?"TRUE":"FALSE";
    		case CELL_TYPE_ERROR:
    			return "#ERR"+getErrorCellValue();
    		case CELL_TYPE_FORMULA:
    			return getCellFormula();
    		case CELL_TYPE_NUMERIC:
    			//TODO apply the dataformat for this cell
    			if (HSSFDateUtil.isCellDateFormatted(this)) {
    				DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    				return sdf.format(getDateCellValue());
    			}else {
    				return  getNumericCellValue() + "";
    			}
    		case CELL_TYPE_STRING:
    			return getStringCellValue();
    		default:
    			return "Unknown Cell Type: " + getCellType();
    	}
    }

    /**
     * Assign a comment to this cell
     *
     * @param comment comment associated with this cell
     */
    public void setCellComment(HSSFComment comment){
        comment.setRow((short)record.getRow());
        comment.setColumn(record.getColumn());
        this.comment = comment;
    }

    /**
     * Returns comment associated with this cell
     *
     * @return comment associated with this cell
     */
     public HSSFComment getCellComment(){
        if (comment == null) {
            comment = findCellComment(sheet, record.getRow(), record.getColumn());
        }
        return comment;
    }

    /**
     * Cell comment finder.
     * Returns cell comment for the specified sheet, row and column.
     *
     * @return cell comment or <code>null</code> if not found
     */
    protected static HSSFComment findCellComment(Sheet sheet, int row, int column){
        HSSFComment comment = null;
        HashMap txshapes = new HashMap(); //map shapeId and TextObjectRecord
        for (Iterator it = sheet.getRecords().iterator(); it.hasNext(); ) {
           Record rec = ( Record ) it.next();
           if (rec instanceof NoteRecord){
               NoteRecord note = (NoteRecord)rec;
               if (note.getRow() == row && note.getColumn() == column){
                   TextObjectRecord txo = (TextObjectRecord)txshapes.get(new Integer(note.getShapeId()));
                   comment = new HSSFComment(note, txo);
                   comment.setRow(note.getRow());
                   comment.setColumn(note.getColumn());
                   comment.setAuthor(note.getAuthor());
                   comment.setVisible(note.getFlags() == NoteRecord.NOTE_VISIBLE);
                   comment.setString(txo.getStr());
                   break;
               }
           } else if (rec instanceof ObjRecord){
               ObjRecord obj = (ObjRecord)rec;
               SubRecord sub = (SubRecord)obj.getSubRecords().get(0);
               if (sub instanceof CommonObjectDataSubRecord){
                   CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord)sub;
                   if (cmo.getObjectType() == CommonObjectDataSubRecord.OBJECT_TYPE_COMMENT){
                       //find the nearest TextObjectRecord which holds comment's text and map it to its shapeId
                       while(it.hasNext()) {
                           rec = ( Record ) it.next();
                           if (rec instanceof TextObjectRecord) {
                               txshapes.put(new Integer(cmo.getObjectId()), rec);
                               break;
                           }
                       }

                   }
               }
           }
        }
        return comment;
   }
}
