/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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


package loci.poi.hssf.record.formula;

import loci.poi.util.LittleEndian;

import loci.poi.hssf.util.RangeAddress;
import loci.poi.hssf.util.CellReference;
import loci.poi.hssf.util.SheetReferences;
import loci.poi.hssf.model.Workbook;
import loci.poi.util.BitField;
import loci.poi.util.BitFieldFactory;
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

/**
 * Title:        Reference 3D Ptg <P>
 * Description:  Defined a cell in extern sheet. <P>
 * REFERENCE:  <P>
 * @author Libin Roman (Vista Portal LDT. Developer)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 1.0-pre
 */

public class Ref3DPtg extends Ptg {
    public final static byte sid  = 0x3a;
    private final static int  SIZE = 7; // 6 + 1 for Ptg
    private short             field_1_index_extern_sheet;
    private short             field_2_row;
    private short             field_3_column;
    private BitField         rowRelative = BitFieldFactory.getInstance(0x8000);
    private BitField         colRelative = BitFieldFactory.getInstance(0x4000);

    /** Creates new AreaPtg */
    public Ref3DPtg() {}

    public Ref3DPtg(RecordInputStream in) {
        field_1_index_extern_sheet = in.readShort();
        field_2_row          = in.readShort();
        field_3_column        = in.readShort();
    }
    
    public Ref3DPtg(String cellref, short externIdx ) {
        CellReference c= new CellReference(cellref);
        setRow((short) c.getRow());
        setColumn((short) c.getCol());
        setColRelative(!c.isColAbsolute());
        setRowRelative(!c.isRowAbsolute());   
        setExternSheetIndex(externIdx);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Ref3dPtg\n");
        buffer.append("Index to Extern Sheet = " + getExternSheetIndex()).append("\n");
        buffer.append("Row = " + getRow()).append("\n");
        buffer.append("Col  = " + getColumn()).append("\n");
        buffer.append("ColRowRel= "
        + isRowRelative()).append("\n");
        buffer.append("ColRel   = " + isColRelative()).append("\n");
        return buffer.toString();
    }

    public void writeBytes(byte [] array, int offset) {
        array[ 0 + offset ] = (byte) (sid + ptgClass);
        LittleEndian.putShort(array, 1 + offset , getExternSheetIndex());
        LittleEndian.putShort(array, 3 + offset , getRow());
        LittleEndian.putShort(array, 5 + offset , getColumnRaw());
    }

    public int getSize() {
        return SIZE;
    }

    public short getExternSheetIndex(){
        return field_1_index_extern_sheet;
    }

    public void setExternSheetIndex(short index){
        field_1_index_extern_sheet = index;
    }

    public short getRow() {
        return field_2_row;
    }

    public void setRow(short row) {
        field_2_row = row;
    }

    public short getColumn() {
        return ( short ) (field_3_column & 0xFF);
    }

    public short getColumnRaw() {
        return field_3_column;
    }

     public boolean isRowRelative()
    {
        return rowRelative.isSet(field_3_column);
    }
    
    public void setRowRelative(boolean rel) {
        field_3_column=rowRelative.setShortBoolean(field_3_column,rel);
    }
    
    public boolean isColRelative()
    {
        return colRelative.isSet(field_3_column);
    }
    
    public void setColRelative(boolean rel) {
        field_3_column=colRelative.setShortBoolean(field_3_column,rel);
    }
    public void setColumn(short column) {
        field_3_column &= 0xFF00;
        field_3_column |= column & 0xFF;
    }

    public void setColumnRaw(short column) {
        field_3_column = column;
    }

   /* public String getArea(){
        RangeAddress ra = new RangeAddress("");

        String result = (ra.numTo26Sys(getColumn()) + (getRow() + 1));

        return result;
    }*/

    public void setArea(String ref){
        RangeAddress ra = new RangeAddress(ref);

        String from = ra.getFromCell();

        setColumn((short) (ra.getXPosition(from) -1));
        setRow((short) (ra.getYPosition(from) -1));

    }

    public String toFormulaString(Workbook book) {
        StringBuffer retval = new StringBuffer();
        SheetReferences refs = book == null ? null : book.getSheetReferences();
        if (refs != null) {
        	String sheetName =refs.getSheetName((int)this.field_1_index_extern_sheet);
        	boolean appendQuotes = sheetName.indexOf(" ") >= 0;
        	if (appendQuotes)
        	  retval.append("'");
            retval.append(sheetName);
        	if (appendQuotes)
          	  retval.append("'");
            retval.append('!');
        }
        retval.append((new CellReference(getRow(),getColumn(),!isRowRelative(),!isColRelative())).toString()); 
        return retval.toString();
    }

   public byte getDefaultOperandClass() {return Ptg.CLASS_REF;}

   public Object clone() {
     Ref3DPtg ptg = new Ref3DPtg();
     ptg.field_1_index_extern_sheet = field_1_index_extern_sheet;
     ptg.field_2_row = field_2_row;
     ptg.field_3_column = field_3_column;
     ptg.setClass(ptgClass);
     return ptg;
   }

}
