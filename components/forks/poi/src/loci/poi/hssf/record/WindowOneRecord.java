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
 * Title:        Window1 Record<P>
 * Description:  Stores the attributes of the workbook window.  This is basically
 *               so the gui knows how big to make the window holding the spreadsheet
 *               document.<P>
 * REFERENCE:  PG 421 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @version 2.0-pre
 */

public class WindowOneRecord
    extends Record
{
    public final static short     sid = 0x3d;

    // our variable names stolen from old TV sets.
    private short                 field_1_h_hold;                  // horizontal position
    private short                 field_2_v_hold;                  // vertical position
    private short                 field_3_width;
    private short                 field_4_height;
    private short                 field_5_options;
    static final private BitField hidden   =
        BitFieldFactory.getInstance(0x01);                                        // is this window is hidden
    static final private BitField iconic   =
        BitFieldFactory.getInstance(0x02);                                        // is this window is an icon
    static final private BitField reserved = BitFieldFactory.getInstance(0x04);   // reserved
    static final private BitField hscroll  =
        BitFieldFactory.getInstance(0x08);                                        // display horizontal scrollbar
    static final private BitField vscroll  =
        BitFieldFactory.getInstance(0x10);                                        // display vertical scrollbar
    static final private BitField tabs     =
        BitFieldFactory.getInstance(0x20);                                        // display tabs at the bottom

    // all the rest are "reserved"
    private short                 field_6_selected_tab;
    private short                 field_7_displayed_tab;
    private short                 field_8_num_selected_tabs;
    private short                 field_9_tab_width_ratio;

    public WindowOneRecord()
    {
    }

    /**
     * Constructs a WindowOne record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public WindowOneRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A WINDOW1 RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_h_hold            = in.readShort();
        field_2_v_hold            = in.readShort();
        field_3_width             = in.readShort();
        field_4_height            = in.readShort();
        field_5_options           = in.readShort();
        field_6_selected_tab      = in.readShort();
        field_7_displayed_tab     = in.readShort();
        field_8_num_selected_tabs = in.readShort();
        field_9_tab_width_ratio   = in.readShort();
    }

    /**
     * set the horizontal position of the window (in 1/20ths of a point)
     * @param h - horizontal location
     */

    public void setHorizontalHold(short h)
    {
        field_1_h_hold = h;
    }

    /**
     * set the vertical position of the window (in 1/20ths of a point)
     * @param v - vertical location
     */

    public void setVerticalHold(short v)
    {
        field_2_v_hold = v;
    }

    /**
     * set the width of the window
     * @param w  width
     */

    public void setWidth(short w)
    {
        field_3_width = w;
    }

    /**
     * set teh height of the window
     * @param h  height
     */

    public void setHeight(short h)
    {
        field_4_height = h;
    }

    /**
     * set the options bitmask (see bit setters)
     *
     * @param o - the bitmask
     */

    public void setOptions(short o)
    {
        field_5_options = o;
    }

    // bitfields for options

    /**
     * set whether the window is hidden or not
     * @param ishidden or not
     */

    public void setHidden(boolean ishidden)
    {
        field_5_options = hidden.setShortBoolean(field_5_options, ishidden);
    }

    /**
     * set whether the window has been iconized or not
     * @param isiconic  iconize  or not
     */

    public void setIconic(boolean isiconic)
    {
        field_5_options = iconic.setShortBoolean(field_5_options, isiconic);
    }

    /**
     * set whether to display the horizontal scrollbar or not
     * @param scroll display or not
     */

    public void setDisplayHorizonalScrollbar(boolean scroll)
    {
        field_5_options = hscroll.setShortBoolean(field_5_options, scroll);
    }

    /**
     * set whether to display the vertical scrollbar or not
     * @param scroll  display or not
     */

    public void setDisplayVerticalScrollbar(boolean scroll)
    {
        field_5_options = vscroll.setShortBoolean(field_5_options, scroll);
    }

    /**
     * set whether to display the tabs or not
     * @param disptabs  display or not
     */

    public void setDisplayTabs(boolean disptabs)
    {
        field_5_options = tabs.setShortBoolean(field_5_options, disptabs);
    }

    // end bitfields

    /**
     * set the selected tab number
     * @param s  tab number
     */

    public void setSelectedTab(short s)
    {
        field_6_selected_tab = s;
    }

    /**
     * set the displayed tab number
     * @param t  tab number
     */

    public void setDisplayedTab(short t)
    {
        field_7_displayed_tab = t;
    }

    /**
     * set the number of selected tabs
     * @param n  number of tabs
     */

    public void setNumSelectedTabs(short n)
    {
        field_8_num_selected_tabs = n;
    }

    /**
     * ratio of the width of the tabs to the horizontal scrollbar
     * @param r  ratio
     */

    public void setTabWidthRatio(short r)
    {
        field_9_tab_width_ratio = r;
    }

    /**
     * get the horizontal position of the window (in 1/20ths of a point)
     * @return h - horizontal location
     */

    public short getHorizontalHold()
    {
        return field_1_h_hold;
    }

    /**
     * get the vertical position of the window (in 1/20ths of a point)
     * @return v - vertical location
     */

    public short getVerticalHold()
    {
        return field_2_v_hold;
    }

    /**
     * get the width of the window
     * @return width
     */

    public short getWidth()
    {
        return field_3_width;
    }

    /**
     * get the height of the window
     * @return height
     */

    public short getHeight()
    {
        return field_4_height;
    }

    /**
     * get the options bitmask (see bit setters)
     *
     * @return o - the bitmask
     */

    public short getOptions()
    {
        return field_5_options;
    }

    // bitfields for options

    /**
     * get whether the window is hidden or not
     * @return ishidden or not
     */

    public boolean getHidden()
    {
        return hidden.isSet(field_5_options);
    }

    /**
     * get whether the window has been iconized or not
     * @return iconize  or not
     */

    public boolean getIconic()
    {
        return iconic.isSet(field_5_options);
    }

    /**
     * get whether to display the horizontal scrollbar or not
     * @return display or not
     */

    public boolean getDisplayHorizontalScrollbar()
    {
        return hscroll.isSet(field_5_options);
    }

    /**
     * get whether to display the vertical scrollbar or not
     * @return display or not
     */

    public boolean getDisplayVerticalScrollbar()
    {
        return vscroll.isSet(field_5_options);
    }

    /**
     * get whether to display the tabs or not
     * @return display or not
     */

    public boolean getDisplayTabs()
    {
        return tabs.isSet(field_5_options);
    }

    // end options bitfields

    /**
     * get the selected tab number
     * @return Tab number
     */

    public short getSelectedTab()
    {
        return field_6_selected_tab;
    }

    /**
     * get the displayed tab number
     * @return Tab number
     */

    public short getDisplayedTab()
    {
        return field_7_displayed_tab;
    }

    /**
     * get the number of selected tabs
     * @return number of tabs
     */

    public short getNumSelectedTabs()
    {
        return field_8_num_selected_tabs;
    }

    /**
     * ratio of the width of the tabs to the horizontal scrollbar
     * @return ratio
     */

    public short getTabWidthRatio()
    {
        return field_9_tab_width_ratio;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[WINDOW1]\n");
        buffer.append("    .h_hold          = ")
            .append(Integer.toHexString(getHorizontalHold())).append("\n");
        buffer.append("    .v_hold          = ")
            .append(Integer.toHexString(getVerticalHold())).append("\n");
        buffer.append("    .width           = ")
            .append(Integer.toHexString(getWidth())).append("\n");
        buffer.append("    .height          = ")
            .append(Integer.toHexString(getHeight())).append("\n");
        buffer.append("    .options         = ")
            .append(Integer.toHexString(getOptions())).append("\n");
        buffer.append("        .hidden      = ").append(getHidden())
            .append("\n");
        buffer.append("        .iconic      = ").append(getIconic())
            .append("\n");
        buffer.append("        .hscroll     = ")
            .append(getDisplayHorizontalScrollbar()).append("\n");
        buffer.append("        .vscroll     = ")
            .append(getDisplayVerticalScrollbar()).append("\n");
        buffer.append("        .tabs        = ").append(getDisplayTabs())
            .append("\n");
        buffer.append("    .selectedtab     = ")
            .append(Integer.toHexString(getSelectedTab())).append("\n");
        buffer.append("    .displayedtab    = ")
            .append(Integer.toHexString(getDisplayedTab())).append("\n");
        buffer.append("    .numselectedtabs = ")
            .append(Integer.toHexString(getNumSelectedTabs())).append("\n");
        buffer.append("    .tabwidthratio   = ")
            .append(Integer.toHexString(getTabWidthRatio())).append("\n");
        buffer.append("[/WINDOW1]\n");
        return buffer.toString();
    }

    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset,
                              (( short ) 0x12));   // 18 bytes (22 total)
        LittleEndian.putShort(data, 4 + offset, getHorizontalHold());
        LittleEndian.putShort(data, 6 + offset, getVerticalHold());
        LittleEndian.putShort(data, 8 + offset, getWidth());
        LittleEndian.putShort(data, 10 + offset, getHeight());
        LittleEndian.putShort(data, 12 + offset, getOptions());
        LittleEndian.putShort(data, 14 + offset, getSelectedTab());
        LittleEndian.putShort(data, 16 + offset, getDisplayedTab());
        LittleEndian.putShort(data, 18 + offset, getNumSelectedTabs());
        LittleEndian.putShort(data, 20 + offset, getTabWidthRatio());
        return getRecordSize();
    }

    public int getRecordSize()
    {
        return 22;
    }

    public short getSid()
    {
        return sid;
    }
}
