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



import loci.poi.util.*;

/**
 * The area format record is used to define the colours and patterns for an area.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class AreaFormatRecord
    extends Record
{
    public final static short      sid                             = 0x100a;
    private  int        field_1_foregroundColor;
    private  int        field_2_backgroundColor;
    private  short      field_3_pattern;
    private  short      field_4_formatFlags;
    private  BitField   automatic                                   = BitFieldFactory.getInstance(0x1);
    private  BitField   invert                                      = BitFieldFactory.getInstance(0x2);
    private  short      field_5_forecolorIndex;
    private  short      field_6_backcolorIndex;


    public AreaFormatRecord()
    {

    }

    /**
     * Constructs a AreaFormat record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public AreaFormatRecord(RecordInputStream in)
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
            throw new RecordFormatException("Not a AreaFormat record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_foregroundColor        = in.readInt();
        field_2_backgroundColor        = in.readInt();
        field_3_pattern                = in.readShort();
        field_4_formatFlags            = in.readShort();
        field_5_forecolorIndex         = in.readShort();
        field_6_backcolorIndex         = in.readShort();

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[AREAFORMAT]\n");
        buffer.append("    .foregroundColor      = ")
            .append("0x").append(HexDump.toHex(  getForegroundColor ()))
            .append(" (").append( getForegroundColor() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .backgroundColor      = ")
            .append("0x").append(HexDump.toHex(  getBackgroundColor ()))
            .append(" (").append( getBackgroundColor() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .pattern              = ")
            .append("0x").append(HexDump.toHex(  getPattern ()))
            .append(" (").append( getPattern() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .formatFlags          = ")
            .append("0x").append(HexDump.toHex(  getFormatFlags ()))
            .append(" (").append( getFormatFlags() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("         .automatic                = ").append(isAutomatic()).append('\n'); 
        buffer.append("         .invert                   = ").append(isInvert()).append('\n'); 
        buffer.append("    .forecolorIndex       = ")
            .append("0x").append(HexDump.toHex(  getForecolorIndex ()))
            .append(" (").append( getForecolorIndex() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .backcolorIndex       = ")
            .append("0x").append(HexDump.toHex(  getBackcolorIndex ()))
            .append(" (").append( getBackcolorIndex() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/AREAFORMAT]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putInt(data, 4 + offset + pos, field_1_foregroundColor);
        LittleEndian.putInt(data, 8 + offset + pos, field_2_backgroundColor);
        LittleEndian.putShort(data, 12 + offset + pos, field_3_pattern);
        LittleEndian.putShort(data, 14 + offset + pos, field_4_formatFlags);
        LittleEndian.putShort(data, 16 + offset + pos, field_5_forecolorIndex);
        LittleEndian.putShort(data, 18 + offset + pos, field_6_backcolorIndex);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 4 + 4 + 2 + 2 + 2 + 2;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        AreaFormatRecord rec = new AreaFormatRecord();
    
        rec.field_1_foregroundColor = field_1_foregroundColor;
        rec.field_2_backgroundColor = field_2_backgroundColor;
        rec.field_3_pattern = field_3_pattern;
        rec.field_4_formatFlags = field_4_formatFlags;
        rec.field_5_forecolorIndex = field_5_forecolorIndex;
        rec.field_6_backcolorIndex = field_6_backcolorIndex;
        return rec;
    }




    /**
     * Get the foreground color field for the AreaFormat record.
     */
    public int getForegroundColor()
    {
        return field_1_foregroundColor;
    }

    /**
     * Set the foreground color field for the AreaFormat record.
     */
    public void setForegroundColor(int field_1_foregroundColor)
    {
        this.field_1_foregroundColor = field_1_foregroundColor;
    }

    /**
     * Get the background color field for the AreaFormat record.
     */
    public int getBackgroundColor()
    {
        return field_2_backgroundColor;
    }

    /**
     * Set the background color field for the AreaFormat record.
     */
    public void setBackgroundColor(int field_2_backgroundColor)
    {
        this.field_2_backgroundColor = field_2_backgroundColor;
    }

    /**
     * Get the pattern field for the AreaFormat record.
     */
    public short getPattern()
    {
        return field_3_pattern;
    }

    /**
     * Set the pattern field for the AreaFormat record.
     */
    public void setPattern(short field_3_pattern)
    {
        this.field_3_pattern = field_3_pattern;
    }

    /**
     * Get the format flags field for the AreaFormat record.
     */
    public short getFormatFlags()
    {
        return field_4_formatFlags;
    }

    /**
     * Set the format flags field for the AreaFormat record.
     */
    public void setFormatFlags(short field_4_formatFlags)
    {
        this.field_4_formatFlags = field_4_formatFlags;
    }

    /**
     * Get the forecolor index field for the AreaFormat record.
     */
    public short getForecolorIndex()
    {
        return field_5_forecolorIndex;
    }

    /**
     * Set the forecolor index field for the AreaFormat record.
     */
    public void setForecolorIndex(short field_5_forecolorIndex)
    {
        this.field_5_forecolorIndex = field_5_forecolorIndex;
    }

    /**
     * Get the backcolor index field for the AreaFormat record.
     */
    public short getBackcolorIndex()
    {
        return field_6_backcolorIndex;
    }

    /**
     * Set the backcolor index field for the AreaFormat record.
     */
    public void setBackcolorIndex(short field_6_backcolorIndex)
    {
        this.field_6_backcolorIndex = field_6_backcolorIndex;
    }

    /**
     * Sets the automatic field value.
     * automatic formatting
     */
    public void setAutomatic(boolean value)
    {
        field_4_formatFlags = automatic.setShortBoolean(field_4_formatFlags, value);
    }

    /**
     * automatic formatting
     * @return  the automatic field value.
     */
    public boolean isAutomatic()
    {
        return automatic.isSet(field_4_formatFlags);
    }

    /**
     * Sets the invert field value.
     * swap foreground and background colours when data is negative
     */
    public void setInvert(boolean value)
    {
        field_4_formatFlags = invert.setShortBoolean(field_4_formatFlags, value);
    }

    /**
     * swap foreground and background colours when data is negative
     * @return  the invert field value.
     */
    public boolean isInvert()
    {
        return invert.isSet(field_4_formatFlags);
    }


}  // END OF CLASS




