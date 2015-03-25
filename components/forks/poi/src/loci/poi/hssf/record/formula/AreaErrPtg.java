/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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

import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

/**
 * AreaErr - handles deleted cell area references.
 *
 * @author Daniel Noll (daniel at nuix dot com dot au)
 */
public class AreaErrPtg extends AreaPtg
{
    public final static byte sid  = 0x2b;

    private AreaErrPtg()
    {
        //Required for clone methods
        super();
    }
    
    public AreaErrPtg(RecordInputStream in)
    {
        super(in);
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("AreaErrPtg\n");
        buffer.append("firstRow = " + getFirstRow()).append("\n");
        buffer.append("lastRow  = " + getLastRow()).append("\n");
        buffer.append("firstCol = " + getFirstColumn()).append("\n");
        buffer.append("lastCol  = " + getLastColumn()).append("\n");
        buffer.append("firstColRowRel= "
                      + isFirstRowRelative()).append("\n");
        buffer.append("lastColRowRel = "
                      + isLastRowRelative()).append("\n");
        buffer.append("firstColRel   = " + isFirstColRelative()).append("\n");
        buffer.append("lastColRel    = " + isLastColRelative()).append("\n");
        return buffer.toString();
    }

    public void writeBytes(byte [] array, int offset) {
        super.writeBytes(array, offset);
        array[offset] = (byte) (sid + ptgClass);
    }

    public String toFormulaString(Workbook book)
    {
        return "#REF!";
    }
    
    public Object clone()
    {
        AreaErrPtg ptg = new AreaErrPtg();
        ptg.setFirstRow(getFirstRow());
        ptg.setFirstColumn(getFirstColumn());
        ptg.setLastRow(getLastRow());
        ptg.setLastColumn(getLastColumn());
        ptg.setFirstColRelative(isFirstColRelative());
        ptg.setLastColRelative(isLastColRelative());
        ptg.setFirstRowRelative(isFirstRowRelative());
        ptg.setLastRowRelative(isLastRowRelative());
        ptg.setClass(ptgClass);
        return ptg;
    }
}

