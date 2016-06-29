/*
 * #%L
 * Fork of Apache Jakarta POI.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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
 * AreaPtg.java
 *
 * Created on November 17, 2001, 9:30 PM
 */
package loci.poi.hssf.record.formula;

import loci.poi.util.LittleEndian;
import loci.poi.util.BitField;

import loci.poi.hssf.record.RecordInputStream;
import loci.poi.hssf.util.AreaReference;
import loci.poi.hssf.util.CellReference;
import loci.poi.hssf.model.Workbook;

/**
 * Specifies a rectangular area of cells A1:A4 for instance.
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class AreaAPtg
    extends AreaPtg
{
    public final static short sid  = 0x65;

    protected AreaAPtg() {
      //Required for clone methods
    }

    public AreaAPtg(short firstRow, short lastRow, short firstColumn, short lastColumn, boolean firstRowRelative, boolean lastRowRelative, boolean firstColRelative, boolean lastColRelative) {
      super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
    }

    public AreaAPtg(RecordInputStream in)
    {
      super(in);
    }

    public String getAreaPtgName() {
      return "AreaAPtg";
    }

    public Object clone() {
      AreaAPtg ptg = new AreaAPtg();
      ptg.setFirstRow(getFirstRow());
      ptg.setLastRow(getLastRow());
      ptg.setFirstColumnRaw(getFirstColumnRaw());
      ptg.setLastColumnRaw(getLastColumnRaw());
      ptg.setClass(ptgClass);
      return ptg;
    }
}
