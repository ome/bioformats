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
 * Number
 * Stores a floating point value in a formula
 * value stored in a 8 byte field using IEEE notation
 * @author  Avik Sengupta
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class NumberPtg
    extends Ptg
{
    public final static int  SIZE = 9;
    public final static byte sid  = 0x1f;
    private double            field_1_value;

    private NumberPtg() {
      //Required for clone methods
    }
        
    /** Create a NumberPtg from a byte array read from disk */
    public NumberPtg(RecordInputStream in)
    {
        setValue(in.readDouble());
    }
    
    /** Create a NumberPtg from a string representation of  the number
     *  Number format is not checked, it is expected to be validated in the parser
     *   that calls this method. 
     *  @param value : String representation of a floating point number
     */
    public NumberPtg(String value) {
        setValue(Double.parseDouble(value));
    }
    
    
    public void setValue(double value)
    {
        field_1_value = value;
    }
    
    
    public double getValue()
    {
        return field_1_value;
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[ offset + 0 ] = sid;
        LittleEndian.putDouble(array, offset + 1, getValue());
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(Workbook book)
    {
        return "" + getValue();
    }
       public byte getDefaultOperandClass() {return Ptg.CLASS_VALUE;}

    public Object clone() {
      NumberPtg ptg = new NumberPtg();
      ptg.field_1_value = field_1_value;
      return ptg;
    }
}
