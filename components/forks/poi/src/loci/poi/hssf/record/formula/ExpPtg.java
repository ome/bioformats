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

import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordFormatException;
import loci.poi.hssf.record.RecordInputStream;

import loci.poi.util.LittleEndian;

/**
 *
 * @author  andy
 * @author Jason Height (jheight at chariot dot net dot au)
 * @author dmui (save existing implementation)
 */

public class ExpPtg
    extends Ptg
{
    private final static int  SIZE = 5;
    public final static short sid  = 0x1;
    private short            field_1_first_row;
    private short            field_2_first_col;

    /** Creates new ExpPtg */

    public ExpPtg()
    {
    }

    /** Creates new ExpPtg */

    public ExpPtg(RecordInputStream in)
    {
      field_1_first_row = in.readShort();
      field_2_first_col = in.readShort();
    }

    public void writeBytes(byte [] array, int offset)
    {
      array[offset+0]= (byte) (sid);
      LittleEndian.putShort(array,offset+1,field_1_first_row);
      LittleEndian.putShort(array,offset+3,field_2_first_col);
    }

    public int getSize()
    {
        return SIZE;
    }
    
    public short getRow() {
      return field_1_first_row;
    }

    public short getColumn() {
      return field_2_first_col;
    }    

    public String toFormulaString(Workbook book)
    {
        throw new RecordFormatException("Coding Error: Expected ExpPtg to be converted from Shared to Non-Shared Formula");
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer("[Array Formula or Shared Formula]\n");
        buffer.append("row = ").append(getRow()).append("\n");
        buffer.append("col = ").append(getColumn()).append("\n");
        return buffer.toString();
    }    
    
    public byte getDefaultOperandClass() {return Ptg.CLASS_VALUE;}
    
    public Object clone() {
	ExpPtg result = new ExpPtg();
        result.field_1_first_row = field_1_first_row;
        result.field_2_first_col = field_2_first_col;        
        return result;
    }

}
