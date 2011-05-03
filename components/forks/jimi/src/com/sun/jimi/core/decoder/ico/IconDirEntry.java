/*
 * Copyright 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.decoder.ico;

import java.io.IOException;

import com.sun.jimi.core.util.LEDataInputStream;

public class IconDirEntry
{
    /**
      * Specifies the width of the icon, in pixels. Acceptable values are 16, 32, and 64.
      *
     **/
    private short bWidth_;

    /**
      * Specifies the height of the icon, in pixels. Acceptable values are 16, 32, and 64.
      *
     **/
    private short bHeight_;

    /**
      * Specifies the number of colors in the icon. Acceptable values are 2, 8, and 16.
      *
     **/
    private short bColorCount_;

    /**
      * Reserved; must be zero.
      *
     **/
    private short bReserved_;


    /**
      * Specifies the number of color planes in the icon bitmap.
      *
     **/
    private int wPlanes_;

    /**
      * Specifies the number of bits in the icon bitmap.
      *
     **/
    private int wBitCount_;


    /**
      * Specifies the size of the resource, in bytes.
      *
     **/
    private long dwBytesInRes_;

    /**
      * Specifies the offset, in bytes, from the beginning of the file to the icon image.
      *
     **/
    private long dwImageOffset_;

    public IconDirEntry(LEDataInputStream dis) throws IOException
    {
        bWidth_         = (short) dis.readUnsignedByte();
        bHeight_        = (short) dis.readUnsignedByte();
        bColorCount_    = (short) dis.readUnsignedByte();
        bReserved_      = (short) dis.readUnsignedByte();

        wPlanes_        = dis.readShort();
        wBitCount_      = dis.readShort();

        dwBytesInRes_   = dis.readInt();
        dwImageOffset_  = dis.readInt();
    }

    public int getWidth()
    {
        return bWidth_;
    }

    public int getHeight()
    {
        return bHeight_;
    }

    public int getColorCount()
    {
        return bColorCount_;
    }

    public long getImageOffset()
    {
        return dwImageOffset_;
    }


    public String toString()
    {
        return "Width:"+ getWidth()
        +" Height: "+ getHeight()
        + " ColorCount: " + getColorCount()
        + " Offset: " + getImageOffset();
    }

}
