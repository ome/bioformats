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
        
package loci.poi.util;

import loci.common.*;

import loci.poi.hssf.usermodel.HSSFSheet;
import loci.poi.hssf.usermodel.HSSFWorkbook;
import loci.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;

/**
 * Dump out the aggregated escher records
 */
public class DrawingDump
{
    public static void main( String[] args ) throws IOException
    {
        POIFSFileSystem fs      =
                new POIFSFileSystem(new RandomAccessInputStream(args[0]), 512);
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        System.out.println( "Drawing group:" );
        wb.dumpDrawingGroupRecords(true);

        for (int sheetNum = 1; sheetNum <= wb.getNumberOfSheets(); sheetNum++)
        {
            System.out.println( "Sheet " + sheetNum + ":" );
            HSSFSheet sheet = wb.getSheetAt(sheetNum - 1);
            sheet.dumpDrawingRecords(true);
        }

    }
}
