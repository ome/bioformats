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
 * The series record describes the overall data for a series.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class SeriesRecord
    extends Record
{
    public final static short      sid                             = 0x1003;
    private  short      field_1_categoryDataType;
    public final static short       CATEGORY_DATA_TYPE_DATES       = 0;
    public final static short       CATEGORY_DATA_TYPE_NUMERIC     = 1;
    public final static short       CATEGORY_DATA_TYPE_SEQUENCE    = 2;
    public final static short       CATEGORY_DATA_TYPE_TEXT        = 3;
    private  short      field_2_valuesDataType;
    public final static short       VALUES_DATA_TYPE_DATES         = 0;
    public final static short       VALUES_DATA_TYPE_NUMERIC       = 1;
    public final static short       VALUES_DATA_TYPE_SEQUENCE      = 2;
    public final static short       VALUES_DATA_TYPE_TEXT          = 3;
    private  short      field_3_numCategories;
    private  short      field_4_numValues;
    private  short      field_5_bubbleSeriesType;
    public final static short       BUBBLE_SERIES_TYPE_DATES       = 0;
    public final static short       BUBBLE_SERIES_TYPE_NUMERIC     = 1;
    public final static short       BUBBLE_SERIES_TYPE_SEQUENCE    = 2;
    public final static short       BUBBLE_SERIES_TYPE_TEXT        = 3;
    private  short      field_6_numBubbleValues;


    public SeriesRecord()
    {

    }

    /**
     * Constructs a Series record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public SeriesRecord(RecordInputStream in)
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
            throw new RecordFormatException("Not a Series record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_categoryDataType       = in.readShort();
        field_2_valuesDataType         = in.readShort();
        field_3_numCategories          = in.readShort();
        field_4_numValues              = in.readShort();
        field_5_bubbleSeriesType       = in.readShort();
        field_6_numBubbleValues        = in.readShort();

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SERIES]\n");
        buffer.append("    .categoryDataType     = ")
            .append("0x").append(HexDump.toHex(  getCategoryDataType ()))
            .append(" (").append( getCategoryDataType() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .valuesDataType       = ")
            .append("0x").append(HexDump.toHex(  getValuesDataType ()))
            .append(" (").append( getValuesDataType() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .numCategories        = ")
            .append("0x").append(HexDump.toHex(  getNumCategories ()))
            .append(" (").append( getNumCategories() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .numValues            = ")
            .append("0x").append(HexDump.toHex(  getNumValues ()))
            .append(" (").append( getNumValues() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .bubbleSeriesType     = ")
            .append("0x").append(HexDump.toHex(  getBubbleSeriesType ()))
            .append(" (").append( getBubbleSeriesType() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .numBubbleValues      = ")
            .append("0x").append(HexDump.toHex(  getNumBubbleValues ()))
            .append(" (").append( getNumBubbleValues() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/SERIES]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShort(data, 4 + offset + pos, field_1_categoryDataType);
        LittleEndian.putShort(data, 6 + offset + pos, field_2_valuesDataType);
        LittleEndian.putShort(data, 8 + offset + pos, field_3_numCategories);
        LittleEndian.putShort(data, 10 + offset + pos, field_4_numValues);
        LittleEndian.putShort(data, 12 + offset + pos, field_5_bubbleSeriesType);
        LittleEndian.putShort(data, 14 + offset + pos, field_6_numBubbleValues);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 2 + 2 + 2 + 2 + 2 + 2;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        SeriesRecord rec = new SeriesRecord();
    
        rec.field_1_categoryDataType = field_1_categoryDataType;
        rec.field_2_valuesDataType = field_2_valuesDataType;
        rec.field_3_numCategories = field_3_numCategories;
        rec.field_4_numValues = field_4_numValues;
        rec.field_5_bubbleSeriesType = field_5_bubbleSeriesType;
        rec.field_6_numBubbleValues = field_6_numBubbleValues;
        return rec;
    }




    /**
     * Get the category data type field for the Series record.
     *
     * @return  One of 
     *        CATEGORY_DATA_TYPE_DATES
     *        CATEGORY_DATA_TYPE_NUMERIC
     *        CATEGORY_DATA_TYPE_SEQUENCE
     *        CATEGORY_DATA_TYPE_TEXT
     */
    public short getCategoryDataType()
    {
        return field_1_categoryDataType;
    }

    /**
     * Set the category data type field for the Series record.
     *
     * @param field_1_categoryDataType
     *        One of 
     *        CATEGORY_DATA_TYPE_DATES
     *        CATEGORY_DATA_TYPE_NUMERIC
     *        CATEGORY_DATA_TYPE_SEQUENCE
     *        CATEGORY_DATA_TYPE_TEXT
     */
    public void setCategoryDataType(short field_1_categoryDataType)
    {
        this.field_1_categoryDataType = field_1_categoryDataType;
    }

    /**
     * Get the values data type field for the Series record.
     *
     * @return  One of 
     *        VALUES_DATA_TYPE_DATES
     *        VALUES_DATA_TYPE_NUMERIC
     *        VALUES_DATA_TYPE_SEQUENCE
     *        VALUES_DATA_TYPE_TEXT
     */
    public short getValuesDataType()
    {
        return field_2_valuesDataType;
    }

    /**
     * Set the values data type field for the Series record.
     *
     * @param field_2_valuesDataType
     *        One of 
     *        VALUES_DATA_TYPE_DATES
     *        VALUES_DATA_TYPE_NUMERIC
     *        VALUES_DATA_TYPE_SEQUENCE
     *        VALUES_DATA_TYPE_TEXT
     */
    public void setValuesDataType(short field_2_valuesDataType)
    {
        this.field_2_valuesDataType = field_2_valuesDataType;
    }

    /**
     * Get the num categories field for the Series record.
     */
    public short getNumCategories()
    {
        return field_3_numCategories;
    }

    /**
     * Set the num categories field for the Series record.
     */
    public void setNumCategories(short field_3_numCategories)
    {
        this.field_3_numCategories = field_3_numCategories;
    }

    /**
     * Get the num values field for the Series record.
     */
    public short getNumValues()
    {
        return field_4_numValues;
    }

    /**
     * Set the num values field for the Series record.
     */
    public void setNumValues(short field_4_numValues)
    {
        this.field_4_numValues = field_4_numValues;
    }

    /**
     * Get the bubble series type field for the Series record.
     *
     * @return  One of 
     *        BUBBLE_SERIES_TYPE_DATES
     *        BUBBLE_SERIES_TYPE_NUMERIC
     *        BUBBLE_SERIES_TYPE_SEQUENCE
     *        BUBBLE_SERIES_TYPE_TEXT
     */
    public short getBubbleSeriesType()
    {
        return field_5_bubbleSeriesType;
    }

    /**
     * Set the bubble series type field for the Series record.
     *
     * @param field_5_bubbleSeriesType
     *        One of 
     *        BUBBLE_SERIES_TYPE_DATES
     *        BUBBLE_SERIES_TYPE_NUMERIC
     *        BUBBLE_SERIES_TYPE_SEQUENCE
     *        BUBBLE_SERIES_TYPE_TEXT
     */
    public void setBubbleSeriesType(short field_5_bubbleSeriesType)
    {
        this.field_5_bubbleSeriesType = field_5_bubbleSeriesType;
    }

    /**
     * Get the num bubble values field for the Series record.
     */
    public short getNumBubbleValues()
    {
        return field_6_numBubbleValues;
    }

    /**
     * Set the num bubble values field for the Series record.
     */
    public void setNumBubbleValues(short field_6_numBubbleValues)
    {
        this.field_6_numBubbleValues = field_6_numBubbleValues;
    }


}  // END OF CLASS




