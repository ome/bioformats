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

import java.util.Vector;
import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.sun.jimi.core.JimiDecoder;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataInputStream;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.P;

public class DCXHeader
{
    private static final int MAGIC = 987654321;

    private boolean multi_;

    private int     numberOfImages_;

    private int[]   offsets_;
    private int[]   lengths_;


    public int getNumberOfImages()
    {
        return numberOfImages_;
    }

    public int getLength(int i)
    {
        if (i >= lengths_.length) return -1;
        return lengths_[i];
    }

    public int getOffset(int i)
    {
        if (i >= offsets_.length) return -1;
        return offsets_[i];
    }

    public DCXHeader(InputStream in) throws JimiException
    {
        try
        {
            LEDataInputStream led = new LEDataInputStream(in);

            int magic = led.readInt();

            if (magic!=MAGIC) throw new JimiException();

            int offset = led.readInt();
            Vector offsets = new Vector();


            while (offset!=0)
            {
                offsets.addElement(new Integer(offset));
                offset = led.readInt();
            }

            numberOfImages_ = offsets.size();

            offsets_ = new int[offsets.size()];
            lengths_ = new int[offsets.size()];

            for (int i=0;i<offsets.size();i++)
            {
                offsets_[i] = ((Integer) offsets.elementAt(i)).intValue();
            }

            for (int i=0;i<offsets.size()-1;i++)
            {
                lengths_[i] = offsets_[i+1]-offsets_[i];
            }

            lengths_[offsets.size()-1] = -1;

        }
        catch (IOException e)
        {
            throw new JimiException();
        }

    }
}
