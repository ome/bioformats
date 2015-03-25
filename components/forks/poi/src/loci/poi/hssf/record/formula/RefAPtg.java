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

/*
 * ValueReferencePtg.java
 *
 * Created on November 21, 2001, 5:27 PM
 */
package loci.poi.hssf.record.formula;

import loci.poi.util.LittleEndian;
import loci.poi.util.BitField;

import loci.poi.hssf.record.RecordInputStream;
import loci.poi.hssf.util.CellReference;
import loci.poi.hssf.model.Workbook;

/**
 * RefNAPtg
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class RefAPtg extends ReferencePtg
{
    public final static byte sid  = 0x64;

    protected RefAPtg() {
      super();
    }

    public RefAPtg(short row, short column, boolean isRowRelative, boolean isColumnRelative) {
      super(row, column, isRowRelative, isColumnRelative);
    }

    public RefAPtg(RecordInputStream in)
    {
      super(in);
    }


    public String getRefPtgName() {
      return "RefAPtg";
    }

    public Object clone() {
      RefAPtg ptg = new RefAPtg();
      ptg.setRow(getRow());
      ptg.setColumnRaw(getColumnRaw());
      ptg.setClass(ptgClass);
      return ptg;
    }
}
