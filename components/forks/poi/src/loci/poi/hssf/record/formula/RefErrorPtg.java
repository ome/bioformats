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

import loci.poi.hssf.util.CellReference;
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

/**
 * RefError - handles deleted cell reference
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class RefErrorPtg extends Ptg
{
    private final static int SIZE = 5;
    public final static byte sid  = 0x2a;
    private int              field_1_reserved;

    private RefErrorPtg() {
      //Required for clone methods
    }
    
    public RefErrorPtg(RecordInputStream in)
    {
        field_1_reserved = in.readInt();

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("[RefError]\n");

        buffer.append("reserved = ").append(getReserved()).append("\n");
        return buffer.toString();
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[offset] = (byte) (sid + ptgClass);
        LittleEndian.putInt(array,offset+1,field_1_reserved);
    }

    public void setReserved(int reserved)
    {
        field_1_reserved = reserved;
    }

    public int getReserved()
    {
        return field_1_reserved;
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        //TODO -- should we store a cellreference instance in this ptg?? but .. memory is an issue, i believe!
        return "#REF!";
    }
    
    public byte getDefaultOperandClass() {
        return Ptg.CLASS_REF;
    }
    
    public Object clone() {
      RefErrorPtg ptg = new RefErrorPtg();
      ptg.field_1_reserved = field_1_reserved;
      ptg.setClass(ptgClass);
      return ptg;
    }
}
