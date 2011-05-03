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

public class IconImage
{
    /**
      *
      *
     **/

    private static final boolean DEBUG = false;

    private BitmapInfoHeader header_;

    private RGBQuad[] colors_;

    private byte[] xorMap_;

    private byte[] andMap_;


    public IconImage(LEDataInputStream dis) throws IOException
    {
        try
        {
            header_ = new BitmapInfoHeader(dis);

            int colorcount = (int) (Math.pow(2,getBitCount()));

            colors_ = new RGBQuad[colorcount];

            for (int i=0;i<colors_.length;i++)
            {
                colors_[i] = new RGBQuad(dis);
            }

            int length = (int) ((header_.getWidth()*header_.getHeight())/2);

            xorMap_ = new byte[length];

            byte[] tempMap = new byte[length/(8/getBitCount())];

            for (int i=0;i<tempMap.length;i++)
            {
                byte xor = (byte) dis.readUnsignedByte();
                tempMap[i] = xor;
            }

            int pixels_per_byte = getBitCount();

            // Depending on the bitcount, a different numberof pixels is stored in a byte,
            // extract the pixels from the bytes.

            switch (pixels_per_byte)
            {
                case 1:
                    for (int i=0,j=0;i<tempMap.length;i++,j+=8)
                    {
                        xorMap_[j]  =(byte) ((tempMap[i] & 0x80)>>>7);
                        xorMap_[j+1]=(byte) ((tempMap[i] & 0x40)>>>6);
                        xorMap_[j+2]=(byte) ((tempMap[i] & 0x20)>>>5);
                        xorMap_[j+3]=(byte) ((tempMap[i] & 0x10)>>>4);
                        xorMap_[j+4]=(byte) ((tempMap[i] & 0x08)>>>3);
                        xorMap_[j+5]=(byte) ((tempMap[i] & 0x04)>>>2);
                        xorMap_[j+6]=(byte) ((tempMap[i] & 0x02)>>>1);
                        xorMap_[j+7]=(byte) ((tempMap[i] & 0x01));
                    }
                    break;
                case 3:
                    for (int i=0,j=0;i<tempMap.length;i++,j+=4)
                    {
                        xorMap_[j]  =(byte) ((tempMap[i] & 0xc0)>>>6);
                        xorMap_[j+1]=(byte) ((tempMap[i] & 0x30)>>>4);
                        xorMap_[j+2]=(byte) ((tempMap[i] & 0x0c)>>>2);
                        xorMap_[j+3]=(byte) ((tempMap[i] & 0x03));
                    }
                    break;

                case 4:
                    for (int i=0,j=0;i<tempMap.length;i++,j+=2)
                    {
                        xorMap_[j]=(byte) ((tempMap[i] & 0xF0)>>>4);
                        xorMap_[j+1]=(byte) (tempMap[i] & 0x0F);
                    }
                    break;

                case 8:
                    xorMap_ = tempMap;
                    break;
            }


            andMap_ = new byte[length/8];


            for (int i=0;i<andMap_.length;i++)
            {
                byte and = (byte) dis.readUnsignedByte();
                andMap_[i] = and;
            }
        }
        catch (Exception e)
        {
        }
    }

    public int getBitCount()
    {
        return header_.getBitCount();
    }

    public int getWidth()
    {
        return (int) header_.getWidth();
    }

    public int getHeight()
    {
        return (int) header_.getHeight()/2;
    }

    public RGBQuad[] getColors()
    {
        return colors_;
    }

    public byte[] getXORMap()
    {
        return xorMap_;
    }

    public byte[] getANDMap()
    {
        return andMap_;
    }
}
