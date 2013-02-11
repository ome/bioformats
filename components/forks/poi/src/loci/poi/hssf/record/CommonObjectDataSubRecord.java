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
 * The common object data record is used to store all common preferences for an excel object.
 * NOTE: This source is automatically generated please do not modify this file.  Either subclass or
 *       remove the record in src/records/definitions.

 * @author Glen Stampoultzis (glens at apache.org)
 */
public class CommonObjectDataSubRecord
    extends SubRecord
{
    public final static short      sid                             = 0x15;
    private  short      field_1_objectType;
    public final static short       OBJECT_TYPE_GROUP              = 0;
    public final static short       OBJECT_TYPE_LINE               = 1;
    public final static short       OBJECT_TYPE_RECTANGLE          = 2;
    public final static short       OBJECT_TYPE_OVAL               = 3;
    public final static short       OBJECT_TYPE_ARC                = 4;
    public final static short       OBJECT_TYPE_CHART              = 5;
    public final static short       OBJECT_TYPE_TEXT               = 6;
    public final static short       OBJECT_TYPE_BUTTON             = 7;
    public final static short       OBJECT_TYPE_PICTURE            = 8;
    public final static short       OBJECT_TYPE_POLYGON            = 9;
    public final static short       OBJECT_TYPE_RESERVED1          = 10;
    public final static short       OBJECT_TYPE_CHECKBOX           = 11;
    public final static short       OBJECT_TYPE_OPTION_BUTTON      = 12;
    public final static short       OBJECT_TYPE_EDIT_BOX           = 13;
    public final static short       OBJECT_TYPE_LABEL              = 14;
    public final static short       OBJECT_TYPE_DIALOG_BOX         = 15;
    public final static short       OBJECT_TYPE_SPINNER            = 16;
    public final static short       OBJECT_TYPE_SCROLL_BAR         = 17;
    public final static short       OBJECT_TYPE_LIST_BOX           = 18;
    public final static short       OBJECT_TYPE_GROUP_BOX          = 19;
    public final static short       OBJECT_TYPE_COMBO_BOX          = 20;
    public final static short       OBJECT_TYPE_RESERVED2          = 21;
    public final static short       OBJECT_TYPE_RESERVED3          = 22;
    public final static short       OBJECT_TYPE_RESERVED4          = 23;
    public final static short       OBJECT_TYPE_RESERVED5          = 24;
    public final static short       OBJECT_TYPE_COMMENT            = 25;
    public final static short       OBJECT_TYPE_RESERVED6          = 26;
    public final static short       OBJECT_TYPE_RESERVED7          = 27;
    public final static short       OBJECT_TYPE_RESERVED8          = 28;
    public final static short       OBJECT_TYPE_RESERVED9          = 29;
    public final static short       OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    private  short      field_2_objectId;
    private  short      field_3_option;
    private  BitField   locked                                      = BitFieldFactory.getInstance(0x1);
    private  BitField   printable                                   = BitFieldFactory.getInstance(0x10);
    private  BitField   autofill                                    = BitFieldFactory.getInstance(0x2000);
    private  BitField   autoline                                    = BitFieldFactory.getInstance(0x4000);
    private  int        field_4_reserved1;
    private  int        field_5_reserved2;
    private  int        field_6_reserved3;


    public CommonObjectDataSubRecord()
    {

    }

    /**
     * Constructs a CommonObjectData record and sets its fields appropriately.
     *
     * @param in the RecordInputstream to read the record from
     */

    public CommonObjectDataSubRecord(RecordInputStream in)
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
            throw new RecordFormatException("Not a CommonObjectData record");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_objectType             = in.readShort();
        field_2_objectId               = in.readShort();
        field_3_option                 = in.readShort();
        field_4_reserved1              = in.readInt();
        field_5_reserved2              = in.readInt();
        field_6_reserved3              = in.readInt();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[ftCmo]\n");
        buffer.append("    .objectType           = ")
            .append("0x").append(HexDump.toHex(  getObjectType ()))
            .append(" (").append( getObjectType() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .objectId             = ")
            .append("0x").append(HexDump.toHex(  getObjectId ()))
            .append(" (").append( getObjectId() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .option               = ")
            .append("0x").append(HexDump.toHex(  getOption ()))
            .append(" (").append( getOption() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("         .locked                   = ").append(isLocked()).append('\n'); 
        buffer.append("         .printable                = ").append(isPrintable()).append('\n'); 
        buffer.append("         .autofill                 = ").append(isAutofill()).append('\n'); 
        buffer.append("         .autoline                 = ").append(isAutoline()).append('\n'); 
        buffer.append("    .reserved1            = ")
            .append("0x").append(HexDump.toHex(  getReserved1 ()))
            .append(" (").append( getReserved1() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .reserved2            = ")
            .append("0x").append(HexDump.toHex(  getReserved2 ()))
            .append(" (").append( getReserved2() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 
        buffer.append("    .reserved3            = ")
            .append("0x").append(HexDump.toHex(  getReserved3 ()))
            .append(" (").append( getReserved3() ).append(" )");
        buffer.append(System.getProperty("line.separator")); 

        buffer.append("[/ftCmo]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte[] data)
    {
        int pos = 0;

        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, (short)(getRecordSize() - 4));

        LittleEndian.putShort(data, 4 + offset + pos, field_1_objectType);
        LittleEndian.putShort(data, 6 + offset + pos, field_2_objectId);
        LittleEndian.putShort(data, 8 + offset + pos, field_3_option);
        LittleEndian.putInt(data, 10 + offset + pos, field_4_reserved1);
        LittleEndian.putInt(data, 14 + offset + pos, field_5_reserved2);
        LittleEndian.putInt(data, 18 + offset + pos, field_6_reserved3);

        return getRecordSize();
    }

    /**
     * Size of record (exluding 4 byte header)
     */
    public int getRecordSize()
    {
        return 4  + 2 + 2 + 2 + 4 + 4 + 4;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
        CommonObjectDataSubRecord rec = new CommonObjectDataSubRecord();
    
        rec.field_1_objectType = field_1_objectType;
        rec.field_2_objectId = field_2_objectId;
        rec.field_3_option = field_3_option;
        rec.field_4_reserved1 = field_4_reserved1;
        rec.field_5_reserved2 = field_5_reserved2;
        rec.field_6_reserved3 = field_6_reserved3;
        return rec;
    }


    /**
     * Get the object type field for the CommonObjectData record.
     *
     * @return  One of 
     *        OBJECT_TYPE_GROUP
     *        OBJECT_TYPE_LINE
     *        OBJECT_TYPE_RECTANGLE
     *        OBJECT_TYPE_OVAL
     *        OBJECT_TYPE_ARC
     *        OBJECT_TYPE_CHART
     *        OBJECT_TYPE_TEXT
     *        OBJECT_TYPE_BUTTON
     *        OBJECT_TYPE_PICTURE
     *        OBJECT_TYPE_POLYGON
     *        OBJECT_TYPE_RESERVED1
     *        OBJECT_TYPE_CHECKBOX
     *        OBJECT_TYPE_OPTION_BUTTON
     *        OBJECT_TYPE_EDIT_BOX
     *        OBJECT_TYPE_LABEL
     *        OBJECT_TYPE_DIALOG_BOX
     *        OBJECT_TYPE_SPINNER
     *        OBJECT_TYPE_SCROLL_BAR
     *        OBJECT_TYPE_LIST_BOX
     *        OBJECT_TYPE_GROUP_BOX
     *        OBJECT_TYPE_COMBO_BOX
     *        OBJECT_TYPE_RESERVED2
     *        OBJECT_TYPE_RESERVED3
     *        OBJECT_TYPE_RESERVED4
     *        OBJECT_TYPE_RESERVED5
     *        OBJECT_TYPE_COMMENT
     *        OBJECT_TYPE_RESERVED6
     *        OBJECT_TYPE_RESERVED7
     *        OBJECT_TYPE_RESERVED8
     *        OBJECT_TYPE_RESERVED9
     *        OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING
     */
    public short getObjectType()
    {
        return field_1_objectType;
    }

    /**
     * Set the object type field for the CommonObjectData record.
     *
     * @param field_1_objectType
     *        One of 
     *        OBJECT_TYPE_GROUP
     *        OBJECT_TYPE_LINE
     *        OBJECT_TYPE_RECTANGLE
     *        OBJECT_TYPE_OVAL
     *        OBJECT_TYPE_ARC
     *        OBJECT_TYPE_CHART
     *        OBJECT_TYPE_TEXT
     *        OBJECT_TYPE_BUTTON
     *        OBJECT_TYPE_PICTURE
     *        OBJECT_TYPE_POLYGON
     *        OBJECT_TYPE_RESERVED1
     *        OBJECT_TYPE_CHECKBOX
     *        OBJECT_TYPE_OPTION_BUTTON
     *        OBJECT_TYPE_EDIT_BOX
     *        OBJECT_TYPE_LABEL
     *        OBJECT_TYPE_DIALOG_BOX
     *        OBJECT_TYPE_SPINNER
     *        OBJECT_TYPE_SCROLL_BAR
     *        OBJECT_TYPE_LIST_BOX
     *        OBJECT_TYPE_GROUP_BOX
     *        OBJECT_TYPE_COMBO_BOX
     *        OBJECT_TYPE_RESERVED2
     *        OBJECT_TYPE_RESERVED3
     *        OBJECT_TYPE_RESERVED4
     *        OBJECT_TYPE_RESERVED5
     *        OBJECT_TYPE_COMMENT
     *        OBJECT_TYPE_RESERVED6
     *        OBJECT_TYPE_RESERVED7
     *        OBJECT_TYPE_RESERVED8
     *        OBJECT_TYPE_RESERVED9
     *        OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING
     */
    public void setObjectType(short field_1_objectType)
    {
        this.field_1_objectType = field_1_objectType;
    }

    /**
     * Get the object id field for the CommonObjectData record.
     */
    public short getObjectId()
    {
        return field_2_objectId;
    }

    /**
     * Set the object id field for the CommonObjectData record.
     */
    public void setObjectId(short field_2_objectId)
    {
        this.field_2_objectId = field_2_objectId;
    }

    /**
     * Get the option field for the CommonObjectData record.
     */
    public short getOption()
    {
        return field_3_option;
    }

    /**
     * Set the option field for the CommonObjectData record.
     */
    public void setOption(short field_3_option)
    {
        this.field_3_option = field_3_option;
    }

    /**
     * Get the reserved1 field for the CommonObjectData record.
     */
    public int getReserved1()
    {
        return field_4_reserved1;
    }

    /**
     * Set the reserved1 field for the CommonObjectData record.
     */
    public void setReserved1(int field_4_reserved1)
    {
        this.field_4_reserved1 = field_4_reserved1;
    }

    /**
     * Get the reserved2 field for the CommonObjectData record.
     */
    public int getReserved2()
    {
        return field_5_reserved2;
    }

    /**
     * Set the reserved2 field for the CommonObjectData record.
     */
    public void setReserved2(int field_5_reserved2)
    {
        this.field_5_reserved2 = field_5_reserved2;
    }

    /**
     * Get the reserved3 field for the CommonObjectData record.
     */
    public int getReserved3()
    {
        return field_6_reserved3;
    }

    /**
     * Set the reserved3 field for the CommonObjectData record.
     */
    public void setReserved3(int field_6_reserved3)
    {
        this.field_6_reserved3 = field_6_reserved3;
    }

    /**
     * Sets the locked field value.
     * true if object is locked when sheet has been protected
     */
    public void setLocked(boolean value)
    {
        field_3_option = locked.setShortBoolean(field_3_option, value);
    }

    /**
     * true if object is locked when sheet has been protected
     * @return  the locked field value.
     */
    public boolean isLocked()
    {
        return locked.isSet(field_3_option);
    }

    /**
     * Sets the printable field value.
     * object appears when printed
     */
    public void setPrintable(boolean value)
    {
        field_3_option = printable.setShortBoolean(field_3_option, value);
    }

    /**
     * object appears when printed
     * @return  the printable field value.
     */
    public boolean isPrintable()
    {
        return printable.isSet(field_3_option);
    }

    /**
     * Sets the autofill field value.
     * whether object uses an automatic fill style
     */
    public void setAutofill(boolean value)
    {
        field_3_option = autofill.setShortBoolean(field_3_option, value);
    }

    /**
     * whether object uses an automatic fill style
     * @return  the autofill field value.
     */
    public boolean isAutofill()
    {
        return autofill.isSet(field_3_option);
    }

    /**
     * Sets the autoline field value.
     * whether object uses an automatic line style
     */
    public void setAutoline(boolean value)
    {
        field_3_option = autoline.setShortBoolean(field_3_option, value);
    }

    /**
     * whether object uses an automatic line style
     * @return  the autoline field value.
     */
    public boolean isAutoline()
    {
        return autoline.isSet(field_3_option);
    }


}  // END OF CLASS


