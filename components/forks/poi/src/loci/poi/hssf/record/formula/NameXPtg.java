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
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

/**
 *
 * @author  aviks
 */

public class NameXPtg extends Ptg
{
    public final static short sid  = 0x39;
    private final static int  SIZE = 7;
    private short             field_1_ixals;   // index to externsheet record
    private short             field_2_ilbl;    //index to name or externname table(1 based)
    private short            field_3_reserved;   // reserved must be 0


    private NameXPtg() {
      //Required for clone methods
    }

    /** Creates new NamePtg */

    public NameXPtg(String name)
    {
        //TODO
    }

    /** Creates new NamePtg */

    public NameXPtg(RecordInputStream in)
    {
        field_1_ixals        = in.readShort();
        field_2_ilbl        = in.readShort();
        field_3_reserved = in.readShort();
        
        //field_2_reserved = LittleEndian.getByteArray(data, offset + 12,12);
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[ offset + 0 ] = (byte)(sid + ptgClass);
        LittleEndian.putShort(array, offset + 1, field_1_ixals);
        LittleEndian.putShort(array,offset+3, field_2_ilbl);
        LittleEndian.putShort(array, offset + 5, field_3_reserved);
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        return "NO IDEA - NAME";
    }
    
    public byte getDefaultOperandClass() {return Ptg.CLASS_VALUE;}

    public Object clone() {
      NameXPtg ptg = new NameXPtg();
      ptg.field_1_ixals = field_1_ixals;
      ptg.field_3_reserved = field_3_reserved;
      ptg.field_2_ilbl = field_2_ilbl;
      ptg.setClass(ptgClass);
      return ptg;
    }
}
