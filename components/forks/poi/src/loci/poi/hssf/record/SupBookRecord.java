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

import loci.poi.util.LittleEndian;

/**
 * Title:        Sup Book  <P>
 * Description:  A Extrenal Workbook Deciption (Sup Book)
 *               Its only a dummy record for making new ExternSheet Record <P>
 * REFERENCE:  <P>
 * @author Libin Roman (Vista Portal LDT. Developer)
 * @author Andrew C. Oliver (acoliver@apache.org)
 */
public class SupBookRecord extends Record
{
    public final static short sid = 0x1AE;
    private short             field_1_number_of_sheets;
    private short             field_2_flag;


    public SupBookRecord()
    {
        setFlag((short)0x401);
    }

    /**
     * Constructs a Extern Sheet record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */
    public SupBookRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT An Supbook RECORD");
        }
    }

    /**
     * @param in the RecordInputstream to read the record from
     */
    protected void fillFields(RecordInputStream in)
    {
        //For now We use it only for one case
        //When we need to add an named range when no named ranges was
        //before it
        field_1_number_of_sheets = in.readShort();
        field_2_flag = in.readShort();
    }


    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SUPBOOK]\n");
        buffer.append("numberosheets = ").append(getNumberOfSheets()).append('\n');
        buffer.append("flag          = ").append(getFlag()).append('\n');
        buffer.append("[/SUPBOOK]\n");
        return buffer.toString();
    }

    /**
     * called by the class that is responsible for writing this sucker.
     * Subclasses should implement this so that their data is passed back in a
     * byte array.
     *
     * @param offset to begin writing at
     * @param data byte array containing instance data
     * @return number of bytes written
     */
    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short) 4);
        LittleEndian.putShort(data, 4 + offset, field_1_number_of_sheets);
        LittleEndian.putShort(data, 6 + offset, field_2_flag);

        return getRecordSize();
    }

    public void setNumberOfSheets(short number){
        field_1_number_of_sheets = number;
    }

    public short getNumberOfSheets(){
        return field_1_number_of_sheets;
    }

    public void setFlag(short flag){
        field_2_flag = flag;
    }

    public short getFlag() {
        return field_2_flag;
    }

    public int getRecordSize()
    {
        return 4 + 4;
    }

    public short getSid()
    {
        return sid;
    }
}
