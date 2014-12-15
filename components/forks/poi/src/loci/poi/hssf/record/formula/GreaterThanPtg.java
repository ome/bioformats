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


/*
 * GreaterThanPtg.java
 *
 * Created on January 23, 2003, 9:47 AM
 */
package loci.poi.hssf.record.formula;

import java.util.List;

import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

/**
 * Greater than operator PTG ">"
 * @author  Cameron Riley (criley at ekmail.com)
 */
public class GreaterThanPtg
    extends OperationPtg
{
    public final static int  SIZE = 1;
    public final static byte sid  = 0x0D;    
    private final static String GREATERTHAN = ">";

    /** 
     * Constructor. Creates new GreaterThanPtg 
     */
    public GreaterThanPtg()
    {
        //deliberately empty
    }

    /**
     * Constructor. Create a new GreaterThanPtg.
     * @param in the RecordInputstream to read the record from
     */
    public GreaterThanPtg(RecordInputStream in)
    {
        //deliberately empty
    }
    
    /**
     * Write the sid to an array
     * @param array the array of bytes to write the sid to
     * @param offset the offset to add the sid to
     */
    public void writeBytes(byte [] array, int offset)
    {
        array[ offset + 0 ] = sid;
    }

    /**
     * Get the size of the sid
     * @return int the size of the sid in terms of byte additions to an array
     */
    public int getSize()
    {
        return SIZE;
    }

    /**
     * Get the type of PTG for Greater Than
     * @return int the identifier for the type
     */
    public int getType()
    {
        return TYPE_BINARY;
    }

    /**
     * Get the number of operands for the Less than operator
     * @return int the number of operands
     */
    public int getNumberOfOperands()
    {
        return 2;
    }
    
    /** 
     * Implementation of method from Ptg 
     * @param book the Sheet References
     */
    public String toFormulaString(Workbook book)
    {
        return this.GREATERTHAN;
    }
      
    /** 
     * Implementation of method from OperationsPtg
     * @param operands a String array of operands
     * @return String the Formula as a String
     */  
    public String toFormulaString(String[] operands) 
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(operands[ 0 ]);
        buffer.append(this.GREATERTHAN);
        buffer.append(operands[ 1 ]);
        return buffer.toString();
    }
    
    /**
     * Get the default operands class value
     * @return byte the Ptg Class Value as a byte from the Ptg Parent object
     */
    public byte getDefaultOperandClass() 
    {
        return Ptg.CLASS_VALUE;
    }
           
    /**
     * Implementation of clone method from Object
     * @return Object a clone of this class as an Object
     */ 
    public Object clone() 
    {
        return new GreaterThanPtg();
    }
}
