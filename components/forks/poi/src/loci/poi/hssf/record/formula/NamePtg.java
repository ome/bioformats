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
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.NameRecord;
import loci.poi.hssf.record.RecordInputStream;

/**
 *
 * @author  andy
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class NamePtg
    extends Ptg
{
    public final static short sid  = 0x23;
    private final static int  SIZE = 5;
    private short             field_1_label_index;
    private short             field_2_zero;   // reserved must be 0
    boolean xtra=false;


    private NamePtg() {
      //Required for clone methods
    }

    /** Creates new NamePtg */

    public NamePtg(String name, Workbook book)
    {
        final short n = (short) (book.getNumNames() + 1);
        NameRecord rec;
        for (short i = 1; i < n; i++) {
            rec = book.getNameRecord(i - 1);
            if (name.equals(rec.getNameText())) {
                field_1_label_index = i;
                return;
            }
        }
        rec = new NameRecord();
        rec.setNameText(name);
        rec.setNameTextLength((byte) name.length());
        book.addName(rec);
        field_1_label_index = n;
    }

    /** Creates new NamePtg */

    public NamePtg(RecordInputStream in)
    {
        //field_1_ixti        = LittleEndian.getShort(data, offset);
        field_1_label_index = in.readShort();
        field_2_zero        = in.readShort();
        //if (data[offset+6]==0) xtra=true;
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[offset+0]= (byte) (sid + ptgClass);
        LittleEndian.putShort(array,offset+1,field_1_label_index);
        LittleEndian.putShort(array,offset+3, field_2_zero);
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        NameRecord rec = book.getNameRecord(field_1_label_index - 1);
        return rec.getNameText();
    }
    
    public byte getDefaultOperandClass() {return Ptg.CLASS_REF;}

    public Object clone() {
      NamePtg ptg = new NamePtg();
      ptg.field_1_label_index = field_1_label_index;
      ptg.field_2_zero = field_2_zero;
      return ptg;
    }
}
