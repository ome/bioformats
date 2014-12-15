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

import java.util.*;

import loci.poi.util.LittleEndian;

/**
 * Title:        Selection Record<P>
 * Description:  shows the user's selection on the sheet
 *               for write set num refs to 0<P>
 *
 * TODO :  Fully implement reference subrecords.
 * REFERENCE:  PG 291 Microsoft Excel 97 Developer's Kit (ISBN: 1-57231-498-2)<P>
 * @author Andrew C. Oliver (acoliver at apache dot org)
 * @author Jason Height (jheight at chariot dot net dot au)
 * @author Glen Stampoultzis (glens at apache.org)
 */

public class SelectionRecord
    extends Record
{
    public final static short sid = 0x1d;
    private byte              field_1_pane;
    //private short             field_2_row_active_cell;
    private int             field_2_row_active_cell;
    private short             field_3_col_active_cell;
    private short             field_4_ref_active_cell;
    private short             field_5_num_refs;
    private ArrayList         field_6_refs;     // not used yet

    public class Reference {
      private short field_1_first_row;
      private short field_2_last_row;
      private byte field_3_first_column;
      private byte field_4_last_column;
      
      Reference(RecordInputStream in) {
        field_1_first_row = in.readShort();
        field_2_last_row = in.readShort();
        field_3_first_column = in.readByte();
        field_4_last_column = in.readByte();
      }
      
      public short getFirstRow() {
    	  return field_1_first_row;
      }
      
      public short getLastRow() {
    	  return field_2_last_row;
      }
      
      public byte getFirstColumn() {
    	  return field_3_first_column;
      }
      
      public byte getLastColumn() {
    	  return field_4_last_column;
      }
    }

    public SelectionRecord()
    {
    }

    /**
     * Constructs a Selection record and sets its fields appropriately.
     * @param in the RecordInputstream to read the record from
     */

    public SelectionRecord(RecordInputStream in)
    {
        super(in);
    }

    protected void validateSid(short id)
    {
        if (id != sid)
        {
            throw new RecordFormatException("NOT A valid Selection RECORD");
        }
    }

    protected void fillFields(RecordInputStream in)
    {
        field_1_pane            = in.readByte();
        //field_2_row_active_cell = LittleEndian.getShort(data, 1 + offset);
        field_2_row_active_cell = in.readUShort();
        field_3_col_active_cell = in.readShort();
        field_4_ref_active_cell = in.readShort();
        field_5_num_refs        = in.readShort();
        
        field_6_refs = new ArrayList(field_5_num_refs);
        for (int i=0; i<field_5_num_refs; i++) {
          field_6_refs.add(new Reference(in));
        }
    }

    /**
     * set which window pane this is for
     * @param pane
     */

    public void setPane(byte pane)
    {
        field_1_pane = pane;
    }

    /**
     * set the active cell's row
     * @param row number of active cell
     */

    //public void setActiveCellRow(short row)
    public void setActiveCellRow(int row)
    {
        field_2_row_active_cell = row;
    }

    /**
     * set the active cell's col
     * @param col number of active cell
     */

    public void setActiveCellCol(short col)
    {
        field_3_col_active_cell = col;
    }

    /**
     * set the active cell's reference number
     * @param ref number of active cell
     */

    public void setActiveCellRef(short ref)
    {
        field_4_ref_active_cell = ref;
    }

    /**
     * set the number of cell refs (we don't support selection so set to 0
     * @param refs - number of references
     */

    public void setNumRefs(short refs)
    {
        field_5_num_refs = refs;
    }

    /**
     * get which window pane this is for
     * @return pane
     */

    public byte getPane()
    {
        return field_1_pane;
    }

    /**
     * get the active cell's row
     * @return row number of active cell
     */

    //public short getActiveCellRow()
    public int getActiveCellRow()
    {
        return field_2_row_active_cell;
    }

    /**
     * get the active cell's col
     * @return col number of active cell
     */

    public short getActiveCellCol()
    {
        return field_3_col_active_cell;
    }

    /**
     * get the active cell's reference number
     * @return ref number of active cell
     */

    public short getActiveCellRef()
    {
        return field_4_ref_active_cell;
    }

    /**
     * get the number of cell refs (we don't support selection so set to 0
     * @return refs - number of references
     */

    public short getNumRefs()
    {
        return field_5_num_refs;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SELECTION]\n");
        buffer.append("    .pane            = ")
            .append(Integer.toHexString(getPane())).append("\n");
        buffer.append("    .activecellrow   = ")
            .append(Integer.toHexString(getActiveCellRow())).append("\n");
        buffer.append("    .activecellcol   = ")
            .append(Integer.toHexString(getActiveCellCol())).append("\n");
        buffer.append("    .activecellref   = ")
            .append(Integer.toHexString(getActiveCellRef())).append("\n");
        buffer.append("    .numrefs         = ")
            .append(Integer.toHexString(getNumRefs())).append("\n");
        buffer.append("[/SELECTION]\n");
        return buffer.toString();
    }

//hacked to provide one cell reference to 0,0 - 0,0
    public int serialize(int offset, byte [] data)
    {
        LittleEndian.putShort(data, 0 + offset, sid);
        LittleEndian.putShort(data, 2 + offset, ( short ) 15);
        data[ 4 + offset ] = getPane();
        //LittleEndian.putShort(data, 5 + offset, getActiveCellRow());
        LittleEndian.putShort(data, 5 + offset, ( short ) getActiveCellRow());
        LittleEndian.putShort(data, 7 + offset, getActiveCellCol());
        LittleEndian.putShort(data, 9 + offset, getActiveCellRef());
        LittleEndian.putShort(data, 11 + offset, ( short ) 1);
        LittleEndian.putShort(data, 13 + offset, ( short ) getActiveCellRow());
        LittleEndian.putShort(data, 15 + offset, ( short ) getActiveCellRow());
        data[ 17 + offset ] = (byte)getActiveCellCol();
        data[ 18 + offset ] = (byte)getActiveCellCol();
        return getRecordSize();
    }

    public int getRecordSize()
    {
        return 19;
    }

    public short getSid()
    {
        return sid;
    }

    public Object clone() {
      SelectionRecord rec = new SelectionRecord();
      rec.field_1_pane = field_1_pane;
      rec.field_2_row_active_cell = field_2_row_active_cell;
      rec.field_3_col_active_cell = field_3_col_active_cell;
      rec.field_4_ref_active_cell = field_4_ref_active_cell;
      rec.field_5_num_refs = field_5_num_refs;
      rec.field_6_refs = field_6_refs;
      return rec;
    }
}
