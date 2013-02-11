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
        

package loci.poi.hssf.record;

import loci.poi.util.LittleEndian;

import java.util.ArrayList;

/**
 * Title:        Extern Sheet <P>
 * Description:  A List of Inndexes to SupBook <P>
 * REFERENCE:  <P>
 * @author Libin Roman (Vista Portal LDT. Developer)
 * @version 1.0-pre
 */

public class ExternSheetRecord extends Record {
    public final static short sid = 0x17;
    private short             field_1_number_of_REF_sturcutres;
    private ArrayList         field_2_REF_structures;
    
    public ExternSheetRecord() {
        field_2_REF_structures = new ArrayList();
    }
    
    /**
     * Constructs a Extern Sheet record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */
    
    public ExternSheetRecord(RecordInputStream in) {
        super(in);
    }
    
    /**
     * called by constructor, should throw runtime exception in the event of a
     * record passed with a differing ID.
     *
     * @param id alleged id for this record
     */
    protected void validateSid(short id) {
        if (id != sid) {
            throw new RecordFormatException("NOT An ExternSheet RECORD");
        }
    }
    
    /**
     * called by the constructor, should set class level fields.  Should throw
     * runtime exception for bad/icomplete data.
     *
     * @param in the RecordInputstream to read the record from
     */
    protected void fillFields(RecordInputStream in) {
        field_2_REF_structures           = new ArrayList();
        
        field_1_number_of_REF_sturcutres = in.readShort();
        
        for (int i = 0 ; i < field_1_number_of_REF_sturcutres ; ++i) {
            ExternSheetSubRecord rec = new ExternSheetSubRecord(in);
            
            field_2_REF_structures.add( rec);
        }
    }
    
    /** 
     * sets the number of the REF structors , that is in Excel file
     * @param numStruct number of REF structs
     */
    public void setNumOfREFStructures(short numStruct) {
        field_1_number_of_REF_sturcutres = numStruct;
    }
    
    /**  
     * return the number of the REF structors , that is in Excel file
     * @return number of REF structs
     */
    public short getNumOfREFStructures() {
        return field_1_number_of_REF_sturcutres;
    }
    
    /** 
     * adds REF struct (ExternSheetSubRecord)
     * @param rec REF struct
     */
    public void addREFRecord(ExternSheetSubRecord rec) {
        field_2_REF_structures.add(rec);
    }
    
    /** returns the number of REF Records, which is in model
     * @return number of REF records
     */
    public int getNumOfREFRecords() {
        return field_2_REF_structures.size();
    }
    
    /** returns the REF record (ExternSheetSubRecord)
     * @param elem index to place
     * @return REF record
     */
    public ExternSheetSubRecord getREFRecordAt(int elem) {
        ExternSheetSubRecord result = ( ExternSheetSubRecord ) field_2_REF_structures.get(elem);
        
        return result;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("[EXTERNSHEET]\n");
        buffer.append("   numOfRefs     = ").append(getNumOfREFStructures()).append("\n");
        for (int k=0; k < this.getNumOfREFRecords(); k++) {
            buffer.append("refrec         #").append(k).append('\n');
            buffer.append(getREFRecordAt(k).toString());
            buffer.append("----refrec     #").append(k).append('\n');
        }
        buffer.append("[/EXTERNSHEET]\n");
        
        
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
    public int serialize(int offset, byte [] data) {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,(short)(2 + (getNumOfREFRecords() *6)));
        
        LittleEndian.putShort(data, 4 + offset, getNumOfREFStructures());
        
        int pos = 6 ;
        
        for (int k = 0; k < getNumOfREFRecords(); k++) {
            ExternSheetSubRecord record = getREFRecordAt(k);
            System.arraycopy(record.serialize(), 0, data, pos + offset, 6);
            
            pos +=6;
        }
        return getRecordSize();
    }
    
    public int getRecordSize() {
        return 4 + 2 + getNumOfREFRecords() * 6;
    }
    
    /**
     * return the non static version of the id for this record.
     */
    public short getSid() {
        return sid;
    }
}
