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

package com.sun.jimi.core.decoder.pcx;

import java.io.IOException;
import java.awt.image.*;

import com.sun.jimi.core.util.P;
import com.sun.jimi.tools.Debug;
import com.sun.jimi.core.util.LEDataInputStream;

public class PCXHeader
{
    /**
      *
      *
     **/

    public static byte PC_PAINTBRUSH   = 10;

    public static byte V2_5            = 0;
    public static byte V2_8p           = 2;
    public static byte V2_8            = 3;
    public static byte V3_0p           = 5;

    public static byte RLE_ENCODING    = 1;

    private byte manufacturer_;

    private byte version_;

    private byte encoding_;

    private byte depth_;

    private int width_;

    private int height_;

    private int xmin_, ymin_, xmax_, ymax_;

    private int hres_, vres_;

    private int nPlanes, bytesPerLine, paletteInfo;

    private PCXColorMap pcxColorMap;

    public PCXHeader(LEDataInputStream dis, int fileLength) throws IOException
    {
        manufacturer_ = (byte) dis.readUnsignedByte();

        version_      = (byte) dis.readUnsignedByte();

        encoding_     = (byte) dis.readUnsignedByte();

        depth_        = (byte) dis.readUnsignedByte();

        xmin_ = dis.readShort();
        ymin_ = dis.readShort();
        xmax_ = dis.readShort();
        ymax_ = dis.readShort();

        hres_ = dis.readShort();
        vres_ = dis.readShort();

        width_ = (xmax_-xmin_+1);
        height_ = (ymax_-ymin_+1);

        pcxColorMap = new PCXColorMap(dis, fileLength, version_);


        // reserved
        dis.skip(1);

        nPlanes = dis.readByte();

        bytesPerLine = dis.readShort();

        paletteInfo = dis.readShort();

        // skip Filler (blank to fill out 128 byte header)
        dis.skip(58);


    }

    public ColorModel getColorModel()
    {
        return pcxColorMap.getColorModel();
    }

    public int getDepth()
    {
        return depth_;
    }

    public int getPlanes()
    {
        return nPlanes;
    }

    public int getWidth()
    {
        if (width_ % 2 == 0)
        {
            return width_;
        }
        return width_+1;
    }

    public int getHeight()
    {
        return height_;
    }

    public int getBytesPerLine()
    {
        return bytesPerLine;
    }

}
