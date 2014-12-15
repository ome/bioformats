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
        

package loci.poi.hssf.record;



import loci.poi.util.*;

/**
 * The series list record defines the series displayed as an overlay to the main chart record.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class SeriesListRecord
    extends Record
{
    public final static short      sid                             = 0x1016;
    private  short[]    field_1_seriesNumbers;


    public SeriesListRecord()
    {

    }

    /**
     * Constructs a SeriesList record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public SeriesListRecord(RecordInputStream in)
    {
        super(in);
    
    }

    /**
     * Checks the sid matches the expected side for this record
     *
     * @param id   the expected sid.
     */
    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("Not a SeriesList record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_seriesNumbers          = in.readShortArray();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SERIESLIST]\n");
        buffer.append("    .seriesNumbers        = ")
            .append(" (").append( getSeriesNumbers() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/SERIESLIST]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShortArray(data, 4 + offset + pos, field_1_seriesNumbers);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + field_1_seriesNumbers.length * 2 + 2;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        SeriesListRecord rec = new SeriesListRecord();
    
        rec.field_1_seriesNumbers = field_1_seriesNumbers;
        return rec;
    }




    /**
     * Get the series numbers field for the SeriesList record.
     */
    public short[] getSeriesNumbers()
    {
        return field_1_seriesNumbers;
    }

    /**
     * Set the series numbers field for the SeriesList record.
     */
    public void setSeriesNumbers(short[] field_1_seriesNumbers)
    {
        this.field_1_seriesNumbers = field_1_seriesNumbers;
    }


}  // END OF CLASS




