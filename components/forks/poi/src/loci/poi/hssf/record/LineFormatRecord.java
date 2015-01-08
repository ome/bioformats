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
 * Describes a line format record.  The line format record controls how a line on a chart appears.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class LineFormatRecord
    extends Record
{
    public final static short      sid                             = 0x1007;
    private  int        field_1_lineColor;
    private  short      field_2_linePattern;
    public final static short       LINE_PATTERN_SOLID             = 0;
    public final static short       LINE_PATTERN_DASH              = 1;
    public final static short       LINE_PATTERN_DOT               = 2;
    public final static short       LINE_PATTERN_DASH_DOT          = 3;
    public final static short       LINE_PATTERN_DASH_DOT_DOT      = 4;
    public final static short       LINE_PATTERN_NONE              = 5;
    public final static short       LINE_PATTERN_DARK_GRAY_PATTERN = 6;
    public final static short       LINE_PATTERN_MEDIUM_GRAY_PATTERN = 7;
    public final static short       LINE_PATTERN_LIGHT_GRAY_PATTERN = 8;
    private  short      field_3_weight;
    public final static short       WEIGHT_HAIRLINE                = -1;
    public final static short       WEIGHT_NARROW                  = 0;
    public final static short       WEIGHT_MEDIUM                  = 1;
    public final static short       WEIGHT_WIDE                    = 2;
    private  short      field_4_format;
    private  BitField   auto                                        = BitFieldFactory.getInstance(0x1);
    private  BitField   drawTicks                                   = BitFieldFactory.getInstance(0x4);
    private  BitField   unknown                                     = BitFieldFactory.getInstance(0x4);
    private  short      field_5_colourPaletteIndex;


    public LineFormatRecord()
    {

    }

    /**
     * Constructs a LineFormat record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public LineFormatRecord(RecordInputStream in)
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
            throw new RecordFormatException("Not a LineFormat record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_lineColor              = in.readInt();
        field_2_linePattern            = in.readShort();
        field_3_weight                 = in.readShort();
        field_4_format                 = in.readShort();
        field_5_colourPaletteIndex     = in.readShort();

    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[LINEFORMAT]\n");
        buffer.append("    .lineColor            = ")
            .append("0x").append(HexDump.toHex(  getLineColor ()))
            .append(" (").append( getLineColor() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .linePattern          = ")
            .append("0x").append(HexDump.toHex(  getLinePattern ()))
            .append(" (").append( getLinePattern() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .weight               = ")
            .append("0x").append(HexDump.toHex(  getWeight ()))
            .append(" (").append( getWeight() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .format               = ")
            .append("0x").append(HexDump.toHex(  getFormat ()))
            .append(" (").append( getFormat() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("         .auto                     = ").append(isAuto()).append('\n'); 
        buffer.append("         .drawTicks                = ").append(isDrawTicks()).append('\n'); 
        buffer.append("         .unknown                  = ").append(isUnknown()).append('\n'); 
        buffer.append("    .colourPaletteIndex   = ")
            .append("0x").append(HexDump.toHex(  getColourPaletteIndex ()))
            .append(" (").append( getColourPaletteIndex() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/LINEFORMAT]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putInt(data, 4 + offset + pos, field_1_lineColor);
        LittleEndian.putShort(data, 8 + offset + pos, field_2_linePattern);
        LittleEndian.putShort(data, 10 + offset + pos, field_3_weight);
        LittleEndian.putShort(data, 12 + offset + pos, field_4_format);
        LittleEndian.putShort(data, 14 + offset + pos, field_5_colourPaletteIndex);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 4 + 2 + 2 + 2 + 2;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        LineFormatRecord rec = new LineFormatRecord();
    
        rec.field_1_lineColor = field_1_lineColor;
        rec.field_2_linePattern = field_2_linePattern;
        rec.field_3_weight = field_3_weight;
        rec.field_4_format = field_4_format;
        rec.field_5_colourPaletteIndex = field_5_colourPaletteIndex;
        return rec;
    }




    /**
     * Get the line color field for the LineFormat record.
     */
    public int getLineColor()
    {
        return field_1_lineColor;
    }

    /**
     * Set the line color field for the LineFormat record.
     */
    public void setLineColor(int field_1_lineColor)
    {
        this.field_1_lineColor = field_1_lineColor;
    }

    /**
     * Get the line pattern field for the LineFormat record.
     *
     * @return  One of 
     *        LINE_PATTERN_SOLID
     *        LINE_PATTERN_DASH
     *        LINE_PATTERN_DOT
     *        LINE_PATTERN_DASH_DOT
     *        LINE_PATTERN_DASH_DOT_DOT
     *        LINE_PATTERN_NONE
     *        LINE_PATTERN_DARK_GRAY_PATTERN
     *        LINE_PATTERN_MEDIUM_GRAY_PATTERN
     *        LINE_PATTERN_LIGHT_GRAY_PATTERN
     */
    public short getLinePattern()
    {
        return field_2_linePattern;
    }

    /**
     * Set the line pattern field for the LineFormat record.
     *
     * @param field_2_linePattern
     *        One of 
     *        LINE_PATTERN_SOLID
     *        LINE_PATTERN_DASH
     *        LINE_PATTERN_DOT
     *        LINE_PATTERN_DASH_DOT
     *        LINE_PATTERN_DASH_DOT_DOT
     *        LINE_PATTERN_NONE
     *        LINE_PATTERN_DARK_GRAY_PATTERN
     *        LINE_PATTERN_MEDIUM_GRAY_PATTERN
     *        LINE_PATTERN_LIGHT_GRAY_PATTERN
     */
    public void setLinePattern(short field_2_linePattern)
    {
        this.field_2_linePattern = field_2_linePattern;
    }

    /**
     * Get the weight field for the LineFormat record.
     *
     * @return  One of 
     *        WEIGHT_HAIRLINE
     *        WEIGHT_NARROW
     *        WEIGHT_MEDIUM
     *        WEIGHT_WIDE
     */
    public short getWeight()
    {
        return field_3_weight;
    }

    /**
     * Set the weight field for the LineFormat record.
     *
     * @param field_3_weight
     *        One of 
     *        WEIGHT_HAIRLINE
     *        WEIGHT_NARROW
     *        WEIGHT_MEDIUM
     *        WEIGHT_WIDE
     */
    public void setWeight(short field_3_weight)
    {
        this.field_3_weight = field_3_weight;
    }

    /**
     * Get the format field for the LineFormat record.
     */
    public short getFormat()
    {
        return field_4_format;
    }

    /**
     * Set the format field for the LineFormat record.
     */
    public void setFormat(short field_4_format)
    {
        this.field_4_format = field_4_format;
    }

    /**
     * Get the colour palette index field for the LineFormat record.
     */
    public short getColourPaletteIndex()
    {
        return field_5_colourPaletteIndex;
    }

    /**
     * Set the colour palette index field for the LineFormat record.
     */
    public void setColourPaletteIndex(short field_5_colourPaletteIndex)
    {
        this.field_5_colourPaletteIndex = field_5_colourPaletteIndex;
    }

    /**
     * Sets the auto field value.
     * automatic format
     */
    public void setAuto(boolean value)
    {
        field_4_format = auto.setShortBoolean(field_4_format, value);
    }

    /**
     * automatic format
     * @return  the auto field value.
     */
    public boolean isAuto()
    {
        return auto.isSet(field_4_format);
    }

    /**
     * Sets the draw ticks field value.
     * draw tick marks
     */
    public void setDrawTicks(boolean value)
    {
        field_4_format = drawTicks.setShortBoolean(field_4_format, value);
    }

    /**
     * draw tick marks
     * @return  the draw ticks field value.
     */
    public boolean isDrawTicks()
    {
        return drawTicks.isSet(field_4_format);
    }

    /**
     * Sets the unknown field value.
     * book marks this as reserved = 0 but it seems to do something
     */
    public void setUnknown(boolean value)
    {
        field_4_format = unknown.setShortBoolean(field_4_format, value);
    }

    /**
     * book marks this as reserved = 0 but it seems to do something
     * @return  the unknown field value.
     */
    public boolean isUnknown()
    {
        return unknown.isSet(field_4_format);
    }


}  // END OF CLASS




