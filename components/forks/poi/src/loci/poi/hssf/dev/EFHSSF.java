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


package loci.poi.hssf.dev;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import loci.poi.poifs.filesystem.POIFSFileSystem;
import loci.poi.hssf.record.*;
import loci.poi.hssf.eventmodel.*;
import loci.poi.hssf.eventusermodel.*;
import loci.poi.hssf.usermodel.*;

import loci.poi.hssf.eventusermodel.HSSFRequest;
import loci.poi.hssf.eventusermodel.HSSFListener;
import loci.poi.hssf.eventusermodel.HSSFEventFactory;

/**
 * Event Factory version of HSSF test class.
 * @author  andy
 */
//JMH
//public class EFHSSF
//{
//    String       infile;
//    String       outfile;
//    HSSFWorkbook workbook = null;
//    HSSFSheet    cursheet = null;
//
//    /** Creates a new instance of EFHSSF */
//
//    public EFHSSF()
//    {
//    }
//
//    public void setInputFile(String infile)
//    {
//        this.infile = infile;
//    }
//
//    public void setOutputFile(String outfile)
//    {
//        this.outfile = outfile;
//    }
//
//    public void run()
//        throws IOException
//    {
//        FileInputStream fin   = new FileInputStream(infile);
//        POIFSFileSystem poifs = new POIFSFileSystem(fin);
//        InputStream     din   = poifs.createDocumentInputStream("Workbook");
//        HSSFRequest     req   = new HSSFRequest();
//
//        req.addListenerForAllRecords(new EFHSSFListener(this));
//        HSSFEventFactory factory = new HSSFEventFactory();
//
//        factory.processEvents(req, din);
//        fin.close();
//        din.close();
//        FileOutputStream fout = new FileOutputStream(outfile);
//
//        workbook.write(fout);
//        fout.close();
//        System.out.println("done.");
//    }
//
//    public void recordHandler(Record record)
//    {
//        HSSFRow  row      = null;
//        HSSFCell cell     = null;
//        int      sheetnum = -1;
//
//        switch (record.getSid())
//        {
//
//            case BOFRecord.sid :
//                BOFRecord bof = ( BOFRecord ) record;
//
//                if (bof.getType() == bof.TYPE_WORKBOOK)
//                {
//                    workbook = new HSSFWorkbook();
//                }
//                else if (bof.getType() == bof.TYPE_WORKSHEET)
//                {
//                    sheetnum++;
//                    cursheet = workbook.getSheetAt(sheetnum);
//                }
//                break;
//
//            case BoundSheetRecord.sid :
//                BoundSheetRecord bsr = ( BoundSheetRecord ) record;
//
//                workbook.createSheet(bsr.getSheetname());
//                break;
//
//            case RowRecord.sid :
//                RowRecord rowrec = ( RowRecord ) record;
//
//                cursheet.createRow(rowrec.getRowNumber());
//                break;
//
//            case NumberRecord.sid :
//                NumberRecord numrec = ( NumberRecord ) record;
//
//                row  = cursheet.getRow(numrec.getRow());
//                cell = row.createCell(numrec.getColumn(),
//                                      HSSFCell.CELL_TYPE_NUMERIC);
//                cell.setCellValue(numrec.getValue());
//                break;
//
//            case SSTRecord.sid :
//                SSTRecord sstrec = ( SSTRecord ) record;
//
//                for (int k = 0; k < sstrec.getNumUniqueStrings(); k++)
//                {
//                    workbook.addSSTString(new UnicodeString(sstrec.getString(k)));
//                }
//                break;
//
//            case LabelSSTRecord.sid :
//                LabelSSTRecord lrec = ( LabelSSTRecord ) record;
//
//                row  = cursheet.getRow(lrec.getRow());
//                cell = row.createCell(lrec.getColumn(),
//                                      HSSFCell.CELL_TYPE_STRING);
//                cell.setCellValue(workbook.getSSTString(lrec.getSSTIndex()));
//                break;
//        }
//    }
//
//    public static void main(String [] args)
//    {
//        if ((args.length < 2) || !args[ 0 ].equals("--help"))
//        {
//            try
//            {
//                EFHSSF viewer = new EFHSSF();
//
//                viewer.setInputFile(args[ 0 ]);
//                viewer.setOutputFile(args[ 1 ]);
//                viewer.run();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
//            System.out.println("EFHSSF");
//            System.out.println(
//                "General testbed for HSSFEventFactory based testing and "
//                + "Code examples");
//            System.out.println("Usage: java loci.poi.hssf.dev.EFHSSF "
//                               + "file1 file2");
//            System.out.println(
//                "   --will rewrite the file reading with the event api");
//            System.out.println("and writing with the standard API");
//        }
//    }
//}
//
//class EFHSSFListener
//    implements HSSFListener
//{
//    EFHSSF efhssf;
//
//    public EFHSSFListener(EFHSSF efhssf)
//    {
//        this.efhssf = efhssf;
//    }
//
//    public void processRecord(Record record)
//    {
//        efhssf.recordHandler(record);
//    }
//}
