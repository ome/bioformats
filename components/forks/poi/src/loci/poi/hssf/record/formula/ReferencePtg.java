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

package loci.poi.hssf.record.formula;

import loci.poi.util.LittleEndian;
import loci.poi.util.BitField;
import loci.poi.util.BitFieldFactory;

import loci.poi.hssf.util.CellReference;
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

/**
 * ReferencePtg - handles references (such as A1, A2, IA4)
 * @author  Andrew C. Oliver (acoliver@apache.org)
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class ReferencePtg extends Ptg
{
    private final static int SIZE = 5;
    public final static byte sid  = 0x24;
    private final static int MAX_ROW_NUMBER = 65536;             
    //public final static byte sid = 0x44;

   /** 
     * The row number, between 0 and 65535, but stored as a signed
     *  short between -32767 and 32768.
     * Take care about which version you fetch back!
     */
    private short            field_1_row;
    /**
     * The column number, between 0 and ??
     */
    private short            field_2_col;
    private BitField         rowRelative = BitFieldFactory.getInstance(0x8000);
    private BitField         colRelative = BitFieldFactory.getInstance(0x4000);
    private BitField         column      = BitFieldFactory.getInstance(0x3FFF);

    protected ReferencePtg() {
      //Required for clone methods
    }
    
    /**
     * Takes in a String represnetation of a cell reference and fills out the 
     * numeric fields.
     */
    public ReferencePtg(String cellref) {
        CellReference c= new CellReference(cellref);
        setRow((short) c.getRow());
        setColumn((short) c.getCol());
        setColRelative(!c.isColAbsolute());
        setRowRelative(!c.isRowAbsolute());
    }
    
    public ReferencePtg(short row, short column, boolean isRowRelative, boolean isColumnRelative) {
      setRow(row);
      setColumn(column);
      setRowRelative(isRowRelative);
      setColRelative(isColumnRelative);
    }    

    /** Creates new ValueReferencePtg */

    public ReferencePtg(RecordInputStream in)
    {
        field_1_row = in.readShort();
        field_2_col = in.readShort();
    }
    
    public String getRefPtgName() {
      return "ReferencePtg";
    }    

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("[");
        buffer.append(getRefPtgName());
        buffer.append("]\n");

        buffer.append("row = ").append(getRow()).append("\n");
        buffer.append("col = ").append(getColumn()).append("\n");
        buffer.append("rowrelative = ").append(isRowRelative()).append("\n");
        buffer.append("colrelative = ").append(isColRelative()).append("\n");
        return buffer.toString();
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[offset] = (byte) (sid + ptgClass);

        LittleEndian.putShort(array,offset+1,field_1_row);
        LittleEndian.putShort(array,offset+3,field_2_col);
    }

    public void setRow(short row)
    {
        field_1_row = row;
    }
    public void setRow(int row)
    {
        if(row < 0 || row >= MAX_ROW_NUMBER) {
           throw new IllegalArgumentException("The row number, when specified as an integer, must be between 0 and " + MAX_ROW_NUMBER);
        }
        
        // Save, wrapping as needed
        if(row > Short.MAX_VALUE) {
        	field_1_row = (short)(row - MAX_ROW_NUMBER);
        } else {
        	field_1_row = (short)row;
        }
    }

    /**
     * Returns the row number as a short, which will be
     *  wrapped (negative) for values between 32769 and 65535
     */
    public short getRow()
    {
        return field_1_row;
    }
    /**
     * Returns the row number as an int, between 0 and 65535
     */
    public int getRowAsInt()
    {
    	if(field_1_row < 0) {
    		return field_1_row + MAX_ROW_NUMBER;
    	}
        return field_1_row;
    }

    public boolean isRowRelative()
    {
        return rowRelative.isSet(field_2_col);
    }
    
    public void setRowRelative(boolean rel) {
        field_2_col=rowRelative.setShortBoolean(field_2_col,rel);
    }
    
    public boolean isColRelative()
    {
        return colRelative.isSet(field_2_col);
    }
    
    public void setColRelative(boolean rel) {
        field_2_col=colRelative.setShortBoolean(field_2_col,rel);
    }

    public void setColumnRaw(short col)
    {
        field_2_col = col;
    }

    public short getColumnRaw()
    {
        return field_2_col;
    }

    public void setColumn(short col)
    {
    	field_2_col = column.setShortValue(field_2_col, col);
    }

    public short getColumn()
    {
    	return column.getShortValue(field_2_col);
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        //TODO -- should we store a cellreference instance in this ptg?? but .. memory is an issue, i believe!
        return (new CellReference(getRowAsInt(),getColumn(),!isRowRelative(),!isColRelative())).toString();
    }
    
    public byte getDefaultOperandClass() {
        return Ptg.CLASS_REF;
    }
    
    public Object clone() {
      ReferencePtg ptg = new ReferencePtg();
      ptg.field_1_row = field_1_row;
      ptg.field_2_col = field_2_col;
      ptg.setClass(ptgClass);
      return ptg;
    }
}
