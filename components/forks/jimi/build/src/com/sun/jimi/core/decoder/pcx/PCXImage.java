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
import com.sun.jimi.core.util.lzw.BitInput;

import com.sun.jimi.tools.Debug;
import com.sun.jimi.core.util.LEDataInputStream;

public class PCXImage
{
    /**
      *
      *
     **/
    private PCXHeader header_;

    private byte[] imageData_;

    private int bytesPerLine_, bitCount_, planes_;

    public PCXImage(LEDataInputStream dis, PCXHeader header) throws IOException
    {
        header_ = header;

        planes_ = header.getPlanes();

        // RLE Decode, read first byte

        bytesPerLine_ = header.getBytesPerLine();

        bitCount_ = getBitCount();

        imageData_ = new byte[getHeight()*getWidth()];

        int col=0;

        switch (bitCount_) {
            case 1:
                if (planes_ >= 1 && planes_ <= 4)
                {
                    get16ColorPCX(dis);
                }
                break;
            case 2:
            case 4:
                if (planes_ == 1)
                {
                    get16ColorPCX(dis);
                }

            case 8:
                switch (planes_) {
                    case 1:
                      get256ColorPCX(dis);
                      break;
                    case 3:
                    case 4:
                      getTrueColorPCX(dis);
                      break;
                }
        }
    }


    public void get16ColorPCX(LEDataInputStream dis) throws IOException
    {
        int cols = getWidth();
        int rows = getHeight();

        //  BytesPerLine should be >= BitsPerPixel * cols / 8
        int rawcols = bytesPerLine_ * 8 / bitCount_;

        if( cols > rawcols ) {

            // error, correct...
            cols = rawcols;
        }

        byte[] rawrow = new byte[rawcols];
        byte[] pcxrow = new byte[planes_ * bytesPerLine_];

        for( int row = 0; row < rows; row++ ) {

            getPCXRow(dis, pcxrow, planes_ * bytesPerLine_);

            if (planes_ == 1)
                pcxUnpackPixels(rawrow, pcxrow);
            else
                pcxPlanesToPixels(rawrow, pcxrow);

            for( int col = 0; col < cols; col++ )
            {
                imageData_[row*getWidth()+col] = rawrow[col];

            }
        }

    }

    private int count = 0;
    private int c = 0;

    public void getPCXRow(LEDataInputStream dis, byte[] pcxRow, int bytesPerLineTotal)// throws IOException
    {
        try
        {
            count = 0;

            int i = 0, j=0;

            while( i < bytesPerLineTotal)
            {
                if(count>0)
                {
                    pcxRow[i++] = (byte) c;
                    --count;
                }
                else
                {
                    c = dis.readByte();
                    if( (c & 0xc0) != 0xc0 )
                    {
                        pcxRow[i++] = (byte) c;
                    }
                    else
                    {
                        count = c & 0x3f;
                        c = dis.readByte();
                    }
                }
            }


        }
        catch (Exception e)
        {
        }

    }

    public void pcxUnpackPixels(byte[] unpacked, byte[] source)
    {
        int        bits;
        int bytesPerLine = bytesPerLine_;

        // source and unpacked counters
        int u_i = 0;
        int s_i = 0;

        if (bitCount_ == 8)
        {
            while (--bytesPerLine >= 0)
                unpacked[u_i++] = source[s_i++];
        }
        else
        if (bitCount_ == 4)
        {
            while (--bytesPerLine >= 0)
            {
                bits        = source[s_i++];
                unpacked[u_i++]   = (byte) ((bits >> 4) & 0x0f);
                unpacked[u_i++]   = (byte) ((bits     ) & 0x0f);
            }
        }
        else if (bitCount_ == 2)
        {
            while (--bytesPerLine >= 0)
            {
                bits        = source[s_i++];
                unpacked[u_i++]   = (byte) ((bits >> 6) & 0x03);
                unpacked[u_i++]   = (byte) ((bits >> 4) & 0x03);
                unpacked[u_i++]   = (byte) ((bits >> 2) & 0x03);
                unpacked[u_i++]   = (byte) ((bits     ) & 0x03);
            }
        }
        else if (bitCount_ == 1)
        {
            while (--bytesPerLine >= 0)
            {
                bits        = source[s_i++];
                unpacked[u_i++]   = (byte) ((bits >> 7) & 0x01);
                unpacked[u_i++]   = (byte) ((bits >> 6) & 0x01);
                unpacked[u_i++]   = (byte) ((bits >> 5) & 0x01);
                unpacked[u_i++]   = (byte) ((bits >> 4) & 0x01);
                unpacked[u_i++]   = (byte) ((bits >> 3) & 0x01);
                unpacked[u_i++]   = (byte) ((bits >> 2) & 0x01);
                unpacked[u_i++]   = (byte) ((bits >> 1) & 0x01);
                unpacked[u_i++]   = (byte) ((bits     ) & 0x01);
            }
        }

    }

    public void pcxPlanesToPixels(byte[] pixels, byte[] bitplanes)
    {
        int  i, j;
        int  npixels;

        int p_i=0;
        int b_i=0;

         for (i = 0; i < planes_; i++)
         {
             int pixbit, bits, mask;

             p_i = 0;

             pixbit    = (1 << i);
             for (j = 0; j < bytesPerLine_; j++)
             {
                 bits = bitplanes[b_i++];
                 for (mask = 0x80; mask != 0; mask >>= 1, p_i++)
                 {
                     if ((bits & mask)>0)
                     {
                         pixels[p_i] |= pixbit;
                     }
                 }
             }
         }
    }

    public void get256ColorPCX(LEDataInputStream dis)
    {
        int col;

        try
        {

            for (int row=0;row<getHeight();row++)
            {
                col = 0;

                while (col<bytesPerLine_)
                {
                    int control = dis.readByte();
                    int rlelength = 0;

                    if ((control & 0xC0) == 0xC0)
                    {
                        int data = dis.readByte();

                        rlelength = control & 0x3F;

                        for (int i=0;i<rlelength;i++)
                        {
                            imageData_[col+row*bytesPerLine_] = (byte) data;
                            col++;
                        }
                    }
                    else
                    {
                        imageData_[col+row*bytesPerLine_] = (byte) control;
                        col++;
                    }
                }
            }
        }
        catch (IOException e)
        {
        }

    }

    public void getTrueColorPCX(LEDataInputStream dis)
    {
    }

    public int getBytesPerLine()
    {
        return bytesPerLine_;
    }

    public byte[] getImageData()
    {
        return imageData_;
    }

    public int getBitCount()
    {
        return header_.getDepth();
    }

    public int getWidth()
    {
        return (int) header_.getWidth();
    }

    public int getHeight()
    {
        return (int) header_.getHeight();
    }

}
