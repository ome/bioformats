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


package loci.poi.hssf.record.formula;

import loci.poi.hssf.model.Workbook;
import loci.poi.hssf.record.RecordInputStream;

import loci.poi.util.LittleEndian;
import loci.poi.util.BitField;
import loci.poi.util.BitFieldFactory;

/**
 * "Special Attributes"
 * This seems to be a Misc Stuff and Junk record.  One function it serves is
 * in SUM functions (i.e. SUM(A1:A3) causes an area PTG then an ATTR with the SUM option set)
 * @author  andy
 * @author Jason Height (jheight at chariot dot net dot au)
 */

public class AttrPtg
    extends OperationPtg
{
    public final static byte sid  = 0x19;
    private final static int  SIZE = 4;
    private byte              field_1_options;
    private short             field_2_data;
    private BitField          semiVolatile = BitFieldFactory.getInstance(0x01);
    private BitField          optiIf       = BitFieldFactory.getInstance(0x02);
    private BitField          optiChoose   = BitFieldFactory.getInstance(0x04);
    private BitField          optGoto      = BitFieldFactory.getInstance(0x08);
    private BitField          sum          = BitFieldFactory.getInstance(0x10);
    private BitField          baxcel       = BitFieldFactory.getInstance(0x20);
    private BitField          space        = BitFieldFactory.getInstance(0x40);

    public AttrPtg() {
    }
    
    public AttrPtg(RecordInputStream in)
    {
        field_1_options = in.readByte();
        field_2_data    = in.readShort();
    }

    public void setOptions(byte options)
    {
        field_1_options = options;
    }

    public byte getOptions()
    {
        return field_1_options;
    }

    public boolean isSemiVolatile()
    {
        return semiVolatile.isSet(getOptions());
    }

    public boolean isOptimizedIf()
    {
        return optiIf.isSet(getOptions());
    }

    public boolean isOptimizedChoose()
    {
        return optiChoose.isSet(getOptions());
    }

    // lets hope no one uses this anymore
    public boolean isGoto()
    {
        return optGoto.isSet(getOptions());
    }

    public boolean isSum()
    {
        return sum.isSet(getOptions());
    }
    
    public void setSum(boolean bsum) {
        field_1_options=sum.setByteBoolean(field_1_options,bsum);
    }

    public void setOptimizedIf(boolean bif) {
        field_1_options=optiIf.setByteBoolean(field_1_options,bif);
    }

	/**
	 * Flags this ptg as a goto/jump 
	 * @param isGoto
	 */
	public void setGoto(boolean isGoto) {
		field_1_options=optGoto.setByteBoolean(field_1_options, isGoto);
	}
	
    // lets hope no one uses this anymore
    public boolean isBaxcel()
    {
        return baxcel.isSet(getOptions());
    }

    // biff3&4 only  shouldn't happen anymore
    public boolean isSpace()
    {
        return space.isSet(getOptions());
    }

    public void setData(short data)
    {
        field_2_data = data;
    }

    public short getData()
    {
        return field_2_data;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("AttrPtg\n");
        buffer.append("options=").append(field_1_options).append("\n");
        buffer.append("data   =").append(field_2_data).append("\n");
        buffer.append("semi   =").append(isSemiVolatile()).append("\n");
        buffer.append("optimif=").append(isOptimizedIf()).append("\n");
        buffer.append("optchos=").append(isOptimizedChoose()).append("\n");
        buffer.append("isGoto =").append(isGoto()).append("\n");
        buffer.append("isSum  =").append(isSum()).append("\n");
        buffer.append("isBaxce=").append(isBaxcel()).append("\n");
        buffer.append("isSpace=").append(isSpace()).append("\n");
        return buffer.toString();
    }

    public void writeBytes(byte [] array, int offset)
    {
        array[offset]=sid;
        array[offset+1]=field_1_options;
        LittleEndian.putShort(array,offset+2,field_2_data);                
    }

    public int getSize()
    {
        return SIZE;
    }

    public String toFormulaString(String[] operands) {
        if(space.isSet(field_1_options)) {
            return operands[ 0 ];
        } else if (optiIf.isSet(field_1_options)) {
            return toFormulaString((Workbook)null) + "(" + operands[ 0 ]             +")";
        } else if (optGoto.isSet(field_1_options)) {
            return toFormulaString((Workbook)null) + operands[0];   //goto isn't a real formula element should not show up
        } else {
            return toFormulaString((Workbook)null) + "(" + operands[ 0 ] + ")";
        }
    }
  

    public int getNumberOfOperands()
    {
        return 1;
    }

    public int getType()
    {
        return -1;
    }
        
   public String toFormulaString(Workbook book) {
      if(semiVolatile.isSet(field_1_options)) {
        return "ATTR(semiVolatile)";
      }
      if(optiIf.isSet(field_1_options)) {
        return "IF";
      }
      if( optiChoose.isSet(field_1_options)) {
        return "CHOOSE";
      }
      if(optGoto.isSet(field_1_options)) {
        return "";
      }
      if(sum.isSet(field_1_options)) {
        return "SUM";
      }
      if(baxcel.isSet(field_1_options)) {
        return "ATTR(baxcel)";
      }
      if(space.isSet(field_1_options)) {
        return "";
      }
      return "UNKNOWN ATTRIBUTE";
     }
    
    
 
    public byte getDefaultOperandClass() {return Ptg.CLASS_VALUE;}

    public Object clone() {
      AttrPtg ptg = new AttrPtg();
      ptg.field_1_options = field_1_options;
      ptg.field_2_data = field_2_data;
      return ptg;
    }
}
