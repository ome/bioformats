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

import java.util.List;

import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

/**
 * While formula tokens are stored in RPN order and thus do not need parenthesis for 
 * precedence reasons, Parenthesis tokens ARE written to ensure that user entered
 * parenthesis are displayed as-is on reading back
 *
 * Avik Sengupta &lt;lists@aviksengupta.com&gt;
 * Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 */
public class ParenthesisPtg
    extends OperationPtg
{
   
    private final static int SIZE = 1;
    public final static byte sid  = 0x15;
   
    public ParenthesisPtg()
    {
    }

    public ParenthesisPtg(RecordInputStream in)
    {

        // doesn't need anything
    }
    
  
    
    public void writeBytes(byte [] array, int offset)
    {
        array[ offset + 0 ] = sid;
    }

    public int getSize()
    {
        return SIZE;
    }

    public int getType()
    {
        return TYPE_BINARY;
    }

    public int getNumberOfOperands()
    {
        return 1;
    }

    public String toFormulaString(Workbook book)
    {
        return "()";
    }

          
    public String toFormulaString(String[] operands) {
        return "("+operands[0]+")";
    }  
    
    public byte getDefaultOperandClass() {return Ptg.CLASS_VALUE;}
        
    public Object clone() {
      return new ParenthesisPtg();
    }

}
