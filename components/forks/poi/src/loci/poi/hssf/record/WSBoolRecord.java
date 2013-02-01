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

import loci.poi.util.BitField;
import loci.poi.util.BitFieldFactory;
import loci.poi.util.LittleEndian;

/**
 * Title:        WSBool Record.<p>
 * Description:  stores workbook settings  (aka its a big "everything we didn't
 *               put somewhere else")<P>
 * REFERENCE:  PG 425 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Glen Stampoultzis (gstamp@iprimus.com.au)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @version 2.0-pre
 */

public class WSBoolRecord
    extends Record
{
    public final static short     sid = 0x81;
    private byte                  field_1_wsbool;         // crappy names are because this is really one big short field (2byte)
    private byte                  field_2_wsbool;         // but the docs inconsistantly use it as 2 seperate bytes

    // I decided to be consistant in this way.
    static final private BitField autobreaks          =
        BitFieldFactory.getInstance(0x01);                               // are automatic page breaks visible

    // bits 1 to 3 unused
    static final private BitField dialog              =
        BitFieldFactory.getInstance(0x10);                               // is sheet dialog sheet
    static final private BitField applystyles         =
        BitFieldFactory.getInstance(0x20);                               // whether to apply automatic styles to outlines
    static final private BitField rowsumsbelow        = BitFieldFactory.getInstance(
        0x40);                                            // whether summary rows will appear below detail in outlines
    static final private BitField rowsumsright        = BitFieldFactory.getInstance(
        0x80);                                            // whether summary rows will appear right of the detail in outlines
    static final private BitField fittopage           =
        BitFieldFactory.getInstance(0x01);                               // whether to fit stuff to the page

    // bit 2 reserved
    static final private BitField displayguts         = BitFieldFactory.getInstance(
        0x06);                                            // whether to display outline symbols (in the gutters)

    // bits 4-5 reserved
    static final private BitField alternateexpression =   // whether to use alternate expression eval
        BitFieldFactory.getInstance(0x40);
    static final private BitField alternateformula    =   // whether to use alternate formula entry
        BitFieldFactory.getInstance(0x80);

    public WSBoolRecord()
    {
    }

    /**
     * Constructs a WSBool record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public WSBoolRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A WSBoolRECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
       byte data[] = in.readRemainder();
        field_1_wsbool =
            data[ 1 ];   // backwards because theoretically this is one short field
        field_2_wsbool =
            data[ 0 ];   // but it was easier to implement it this way to avoid confusion
    }                             // because the dev kit shows the masks for it as 2 byte fields

    // why?  Why ask why?  But don't drink bud dry as its a really
    // crappy beer, try the czech "Budvar" beer (which is the real
    // budweiser though its ironically good...its sold in the USs
    // as czechvar  --- odd that they had the name first but can't
    // use it)...

    /**
     * set first byte (see bit setters)
     */

    public void setWSBool1(byte bool1)
    {
        field_1_wsbool = bool1;
    }

    // bool1 bitfields

    /**
     * show automatic page breaks or not
     * @param ab  whether to show auto page breaks
     */

    public void setAutobreaks(boolean ab)
    {
        field_1_wsbool = autobreaks.setByteBoolean(field_1_wsbool, ab);
    }

    /**
     * set whether sheet is a dialog sheet or not
     * @param isDialog or not
     */

    public void setDialog(boolean isDialog)
    {
        field_1_wsbool = dialog.setByteBoolean(field_1_wsbool, isDialog);
    }

    /**
     * set if row summaries appear below detail in the outline
     * @param below or not
     */

    public void setRowSumsBelow(boolean below)
    {
        field_1_wsbool = rowsumsbelow.setByteBoolean(field_1_wsbool, below);
    }

    /**
     * set if col summaries appear right of the detail in the outline
     * @param right or not
     */

    public void setRowSumsRight(boolean right)
    {
        field_1_wsbool = rowsumsright.setByteBoolean(field_1_wsbool, right);
    }

    // end bitfields

    /**
     * set the second byte (see bit setters)
     */

    public void setWSBool2(byte bool2)
    {
        field_2_wsbool = bool2;
    }

    // bool2 bitfields

    /**
     * fit to page option is on
     * @param fit2page  fit or not
     */

    public void setFitToPage(boolean fit2page)
    {
        field_2_wsbool = fittopage.setByteBoolean(field_2_wsbool, fit2page);
    }

    /**
     * set whether to display the guts or not
     *
     * @param guts or no guts (or glory)
     */

    public void setDisplayGuts(boolean guts)
    {
        field_2_wsbool = displayguts.setByteBoolean(field_2_wsbool, guts);
    }

    /**
     * whether alternate expression evaluation is on
     * @param altexp  alternative expression evaluation or not
     */

    public void setAlternateExpression(boolean altexp)
    {
        field_2_wsbool = alternateexpression.setByteBoolean(field_2_wsbool,
                altexp);
    }

    /**
     * whether alternative formula entry is on
     * @param formula  alternative formulas or not
     */

    public void setAlternateFormula(boolean formula)
    {
        field_2_wsbool = alternateformula.setByteBoolean(field_2_wsbool,
                formula);
    }

    // end bitfields

    /**
     * get first byte (see bit getters)
     */

    public byte getWSBool1()
    {
        return field_1_wsbool;
    }

    // bool1 bitfields

    /**
     * show automatic page breaks or not
     * @return whether to show auto page breaks
     */

    public boolean getAutobreaks()
    {
        return autobreaks.isSet(field_1_wsbool);
    }

    /**
     * get whether sheet is a dialog sheet or not
     * @return isDialog or not
     */

    public boolean getDialog()
    {
        return dialog.isSet(field_1_wsbool);
    }

    /**
     * get if row summaries appear below detail in the outline
     * @return below or not
     */

    public boolean getRowSumsBelow()
    {
        return rowsumsbelow.isSet(field_1_wsbool);
    }

    /**
     * get if col summaries appear right of the detail in the outline
     * @return right or not
     */

    public boolean getRowSumsRight()
    {
        return rowsumsright.isSet(field_1_wsbool);
    }

    // end bitfields

    /**
     * get the second byte (see bit getters)
     */

    public byte getWSBool2()
    {
        return field_2_wsbool;
    }

    // bool2 bitfields

    /**
     * fit to page option is on
     * @return fit or not
     */

    public boolean getFitToPage()
    {
        return fittopage.isSet(field_2_wsbool);
    }

    /**
     * get whether to display the guts or not
     *
     * @return guts or no guts (or glory)
     */

    public boolean getDisplayGuts()
    {
        return displayguts.isSet(field_2_wsbool);
    }

    /**
     * whether alternate expression evaluation is on
     * @return alternative expression evaluation or not
     */

    public boolean getAlternateExpression()
    {
        return alternateexpression.isSet(field_2_wsbool);
    }

    /**
     * whether alternative formula entry is on
     * @return alternative formulas or not
     */

    public boolean getAlternateFormula()
    {
        return alternateformula.isSet(field_2_wsbool);
    }

    // end bitfields
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[WSBOOL]\n");
        buffer.append("    .wsbool1        = ")
            .append(Integer.toHexString(getWSBool1())).append("\n");
        buffer.append("        .autobreaks = ").append(getAutobreaks())
            .append("\n");
        buffer.append("        .dialog     = ").append(getDialog())
            .append("\n");
        buffer.append("        .rowsumsbelw= ").append(getRowSumsBelow())
            .append("\n");
        buffer.append("        .rowsumsrigt= ").append(getRowSumsRight())
            .append("\n");
        buffer.append("    .wsbool2        = ")
            .append(Integer.toHexString(getWSBool2())).append("\n");
        buffer.append("        .fittopage  = ").append(getFitToPage())
            .append("\n");
        buffer.append("        .displayguts= ").append(getDisplayGuts())
            .append("\n");
        buffer.append("        .alternateex= ")
            .append(getAlternateExpression()).append("\n");
        buffer.append("        .alternatefo= ").append(getAlternateFormula())
            .append("\n");
        buffer.append("[/WSBOOL]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 0x2);
        data[ 5 + offset ] = getWSBool1();
        data[ 4 + offset ] = getWSBool2();
        return getRecordSize();
    }

    public int getRecordSize()
    {
        return 6;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
      WSBoolRecord rec = new WSBoolRecord();
      rec.field_1_wsbool = field_1_wsbool;
      rec.field_2_wsbool = field_2_wsbool;
      return rec;
    }
}
