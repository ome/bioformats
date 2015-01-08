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


package loci.poi.hssf.dev;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import loci.common.RandomAccessInputStream;

import loci.poi.poifs.filesystem.POIFSFileSystem;
import loci.poi.hssf.usermodel.*;
import loci.poi.hssf.util.*;

/**
 * File for HSSF testing/examples
 *
 * THIS IS NOT THE MAIN HSSF FILE!!  This is a util for testing functionality.
 * It does contain sample API usage that may be educational to regular API users.
 *
 * @see #main
 * @author Andrew Oliver (acoliver at apache dot org)
 */

public class HSSF
{
    private String         filename     = null;

    protected HSSFWorkbook hssfworkbook = null;

    /**
     * Constructor HSSF - creates an HSSFStream from an InputStream.  The HSSFStream
     * reads in the records allowing modification.
     *
     *
     * @param filename
     *
     * @exception IOException
     *
     */

    public HSSF(String filename)
        throws IOException
    {
        this.filename = filename;
        POIFSFileSystem fs =
            new POIFSFileSystem(new RandomAccessInputStream(filename), 512);

        hssfworkbook = new HSSFWorkbook(fs);

        // records = RecordFactory.createRecords(stream);
    }

    /**
     * Constructor HSSF - given a filename this outputs a sample sheet with just
     * a set of rows/cells.
     *
     *
     * @param filename
     * @param write
     *
     * @exception IOException
     *
     */

    public HSSF(String filename, boolean write)
        throws IOException
    {
        short            rownum = 0;
        FileOutputStream out    = new FileOutputStream(filename);
        HSSFWorkbook     wb     = new HSSFWorkbook();
        HSSFSheet        s      = wb.createSheet();
        HSSFRow          r      = null;
        HSSFCell         c      = null;
        HSSFCellStyle    cs     = wb.createCellStyle();
        HSSFCellStyle    cs2    = wb.createCellStyle();
        HSSFCellStyle    cs3    = wb.createCellStyle();
        HSSFFont         f      = wb.createFont();
        HSSFFont         f2     = wb.createFont();

        f.setFontHeightInPoints(( short ) 12);
        f.setColor(( short ) 0xA);
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        f2.setFontHeightInPoints(( short ) 10);
        f2.setColor(( short ) 0xf);
        f2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cs.setFont(f);
        cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
        cs2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cs2.setFillPattern(( short ) 1);   // fill w fg
        cs2.setFillForegroundColor(( short ) 0xA);
        cs2.setFont(f2);
        wb.setSheetName(0, "HSSF Test");
        for (rownum = ( short ) 0; rownum < 300; rownum++)
        {
            r = s.createRow(rownum);
            if ((rownum % 2) == 0)
            {
                r.setHeight(( short ) 0x249);
            }

            // r.setRowNum(( short ) rownum);
            for (short cellnum = ( short ) 0; cellnum < 50; cellnum += 2)
            {
                c = r.createCell(cellnum, HSSFCell.CELL_TYPE_NUMERIC);
                c.setCellValue(rownum * 10000 + cellnum
                               + ((( double ) rownum / 1000)
                                  + (( double ) cellnum / 10000)));
                if ((rownum % 2) == 0)
                {
                    c.setCellStyle(cs);
                }
                c = r.createCell(( short ) (cellnum + 1),
                                 HSSFCell.CELL_TYPE_STRING);
                c.setCellValue("TEST");
                s.setColumnWidth(( short ) (cellnum + 1),
                                 ( short ) ((50 * 8) / (( double ) 1 / 20)));
                if ((rownum % 2) == 0)
                {
                    c.setCellStyle(cs2);
                }
            }   // 50 characters divided by 1/20th of a point
        }

        // draw a thick black border on the row at the bottom using BLANKS
        rownum++;
        rownum++;
        r = s.createRow(rownum);
        cs3.setBorderBottom(HSSFCellStyle.BORDER_THICK);
        for (short cellnum = ( short ) 0; cellnum < 50; cellnum++)
        {
            c = r.createCell(cellnum, HSSFCell.CELL_TYPE_BLANK);

            // c.setCellValue(0);
            c.setCellStyle(cs3);
        }
        s.addMergedRegion(new Region(( short ) 0, ( short ) 0, ( short ) 3,
                                     ( short ) 3));
        s.addMergedRegion(new Region(( short ) 100, ( short ) 100,
                                     ( short ) 110, ( short ) 110));

        // end draw thick black border
        // create a sheet, set its title then delete it
        s = wb.createSheet();
        wb.setSheetName(1, "DeletedSheet");
        wb.removeSheetAt(1);

        // end deleted sheet
        wb.write(out);
        out.close();
    }

    /**
     * Constructor HSSF - takes in file - attempts to read it then reconstruct it
     *
     *
     * @param infile
     * @param outfile
     * @param write
     *
     * @exception IOException
     *
     */

    public HSSF(String infile, String outfile, boolean write)
        throws IOException
    {
        this.filename = infile;
        POIFSFileSystem fs =
            new POIFSFileSystem(new RandomAccessInputStream(filename), 512);

        hssfworkbook = new HSSFWorkbook(fs);

        // HSSFWorkbook book = hssfstream.getWorkbook();
    }

    /**
     * Method main
     *
     * Given 1 argument takes that as the filename, inputs it and dumps the
     * cell values/types out to sys.out
     *
     * given 2 arguments where the second argument is the word "write" and the
     * first is the filename - writes out a sample (test) spreadsheet (see
     * public HSSF(String filename, boolean write)).
     *
     * given 2 arguments where the first is an input filename and the second
     * an output filename (not write), attempts to fully read in the
     * spreadsheet and fully write it out.
     *
     * given 3 arguments where the first is an input filename and the second an
     * output filename (not write) and the third is "modify1", attempts to read in the
     * spreadsheet, deletes rows 0-24, 74-99.  Changes cell at row 39, col 3 to
     * "MODIFIED CELL" then writes it out.  Hence this is "modify test 1".  If you
     * take the output from the write test, you'll have a valid scenario.
     *
     * @param args
     *
     */

    public static void main(String [] args)
    {
        if (args.length < 2)
        {

/*            try
            {
                HSSF hssf = new HSSF(args[ 0 ]);

                System.out.println("Data dump:\n");
                HSSFWorkbook wb = hssf.hssfworkbook;

                for (int k = 0; k < wb.getNumberOfSheets(); k++)
                {
                    System.out.println("Sheet " + k);
                    HSSFSheet sheet = wb.getSheetAt(k);
                    int       rows  = sheet.getPhysicalNumberOfRows();

                    for (int r = 0; r < rows; r++)
                    {
                        HSSFRow row   = sheet.getPhysicalRowAt(r);
                        int     cells = row.getPhysicalNumberOfCells();

                        System.out.println("ROW " + row.getRowNum());
                        for (int c = 0; c < cells; c++)
                        {
                            HSSFCell cell  = row.getPhysicalCellAt(c);
                            String   value = null;

                            switch (cell.getCellType())
                            {

                                case HSSFCell.CELL_TYPE_FORMULA :
                                    value = "FORMULA ";
                                    break;

                                case HSSFCell.CELL_TYPE_NUMERIC :
                                    value = "NUMERIC value="
                                            + cell.getNumericCellValue();
                                    break;

                                case HSSFCell.CELL_TYPE_STRING :
                                    value = "STRING value="
                                            + cell.getStringCellValue();
                                    break;

                                default :
                            }
                            System.out.println("CELL col="
                                               + cell.getCellNum()
                                               + " VALUE=" + value);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }*/
        }
        else if (args.length == 2)
        {
            if (args[ 1 ].toLowerCase().equals("write"))
            {
                System.out.println("Write mode");
                try
                {
                    long time = System.currentTimeMillis();
                    HSSF hssf = new HSSF(args[ 0 ], true);

                    System.out
                        .println("" + (System.currentTimeMillis() - time)
                                 + " ms generation time");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("readwrite test");
                try
                {
                    HSSF             hssf   = new HSSF(args[ 0 ]);

                    // HSSFStream       hssfstream = hssf.hssfstream;
                    HSSFWorkbook     wb     = hssf.hssfworkbook;
                    FileOutputStream stream = new FileOutputStream(args[ 1 ]);

                    // HSSFCell cell = new HSSFCell();
                    // cell.setCellNum((short)3);
                    // cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    // cell.setCellValue(-8009.999);
                    // hssfstream.modifyCell(cell,0,(short)6);
                    wb.write(stream);
                    stream.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if ((args.length == 3)
                 && args[ 2 ].toLowerCase().equals("modify1"))
        {
            try   // delete row 0-24, row 74 - 99 && change cell 3 on row 39 to string "MODIFIED CELL!!"
            {
                HSSF             hssf   = new HSSF(args[ 0 ]);

                // HSSFStream       hssfstream = hssf.hssfstream;
                HSSFWorkbook     wb     = hssf.hssfworkbook;
                FileOutputStream stream = new FileOutputStream(args[ 1 ]);
                HSSFSheet        sheet  = wb.getSheetAt(0);

                for (int k = 0; k < 25; k++)
                {
                    HSSFRow row = sheet.getRow(k);

                    sheet.removeRow(row);
                }
                for (int k = 74; k < 100; k++)
                {
                    HSSFRow row = sheet.getRow(k);

                    sheet.removeRow(row);
                }
                HSSFRow  row  = sheet.getRow(39);
                HSSFCell cell = row.getCell(( short ) 3);

                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue("MODIFIED CELL!!!!!");

                // HSSFCell cell = new HSSFCell();
                // cell.setCellNum((short)3);
                // cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                // cell.setCellValue(-8009.999);
                // hssfstream.modifyCell(cell,0,(short)6);
                wb.write(stream);
                stream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
