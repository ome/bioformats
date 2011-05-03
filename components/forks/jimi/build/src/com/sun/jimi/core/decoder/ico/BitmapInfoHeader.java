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

public class BitmapInfoHeader
{
    /**
      *
      *
     **/
    private long biSize_;

    private long biWidth_;

    private long biHeight_;

    private int biPlanes_;

    private int biBitCount_;

    private long biCompression_;

    private long biSizeImage_;

    private long biXPelsPerMeter_;

    private long biYPelsPerMeter_;

    private long biClrUsed_;

    private long biClrImportant_;


    public BitmapInfoHeader(LEDataInputStream dis) throws IOException
    {
        biSize_ = dis.readInt();

        biWidth_ = dis.readInt();

        biHeight_ = dis.readInt();

        biPlanes_ = dis.readShort();

        biBitCount_ = dis.readShort();

        biCompression_ = dis.readInt();

        biSizeImage_ = dis.readInt();

        biXPelsPerMeter_ = dis.readInt();

        biYPelsPerMeter_ = dis.readInt();

        biClrUsed_ = dis.readInt();

        biClrImportant_ = dis.readInt();
    }

    public int getBitCount()
    {
        return biBitCount_;
    }

    public long getWidth()
    {
        return biWidth_;
    }

    public long getHeight()
    {
        return biHeight_;
    }


    public String toString()
    {
        return "Width:"+ biWidth_ +" Height: "+ biHeight_;
    }

}
