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

public class RGBQuad
{
    /**
      *
      *
     **/
    private short blue_;

    private short green_;

    private short red_;

    private short reserved_;

    public RGBQuad(LEDataInputStream dis) throws IOException
    {
        blue_       = (short) dis.readUnsignedByte();
        green_      = (short) dis.readUnsignedByte();
        red_        = (short) dis.readUnsignedByte();
        reserved_   = (short) dis.readUnsignedByte();
    }

    public short getRed()
    {
        return red_;
    }

    public short getGreen()
    {
        return green_;
    }

    public short getBlue()
    {
        return blue_;
    }
}
