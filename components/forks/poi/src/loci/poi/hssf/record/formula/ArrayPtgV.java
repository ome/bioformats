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
import loci.poi.util.BitFieldFactory;
import loci.poi.util.StringUtil;

import loci.poi.hssf.util.CellReference;
import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordFormatException;
import loci.poi.hssf.record.RecordInputStream;
import loci.poi.hssf.record.SSTRecord;
import loci.poi.hssf.record.UnicodeString;

/**
 * ArrayPtg - handles arrays
 * 
 * The ArrayPtg is a little wierd, the size of the Ptg when parsing initially only
 * includes the Ptg sid and the reserved bytes. The next Ptg in the expression then follows.
 * It is only after the "size" of all the Ptgs is met, that the ArrayPtg data is actually
 * held after this. So Ptg.createParsedExpression keeps track of the number of 
 * ArrayPtg elements and need to parse the data upto the FORMULA record size.
 *  
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class ArrayPtgV extends ArrayPtg
{
    public final static byte sid  = 0x40;

    protected ArrayPtgV() {
      //Required for clone methods
    }

    public ArrayPtgV(RecordInputStream in)
    {
    	super(in);
    }
    
    public Object clone() {
      ArrayPtgV ptg = new ArrayPtgV();
      ptg.field_1_reserved = field_1_reserved;
      ptg.field_2_reserved = field_2_reserved;
      ptg.field_3_reserved = field_3_reserved;
      ptg.field_4_reserved = field_4_reserved;
      ptg.field_5_reserved = field_5_reserved;
      ptg.field_6_reserved = field_6_reserved;
      ptg.field_7_reserved = field_7_reserved;
      
      ptg.token_1_columns = token_1_columns;
      ptg.token_2_rows = token_2_rows;
      ptg.token_3_arrayValues = new Object[getColumnCount()][getRowCount()];
      for (int x=0;x<getColumnCount();x++) {
      	for (int y=0;y<getRowCount();y++) {
      		ptg.token_3_arrayValues[x][y] = token_3_arrayValues[x][y];
      	}
      }      
      ptg.setClass(ptgClass);
      return ptg;
    }
}
