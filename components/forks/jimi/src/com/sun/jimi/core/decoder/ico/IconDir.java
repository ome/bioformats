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

public class IconDir
{

    /**
      * Reserved; must be zero.
      *
     **/
    private int idReserved_;

    /**
      * Specifies the resource type. This member is set to 1.
      *
     **/
    private int idType_;

    /**
      * Specifies the number of entries in the directory.
      *
     **/
    private int idCount_;


    private IconDirEntry[] iconDirEntries_;


    public IconDir(LEDataInputStream dis) throws IOException
    {
        idReserved_ = dis.readShort();

        idType_ = dis.readShort();

        idCount_ = dis.readShort();

        iconDirEntries_ = new IconDirEntry[idCount_];

        for (int i = 0;i < idCount_; i++)
        {
            iconDirEntries_[i] = new IconDirEntry(dis);
        }

    }

    public int getCount()
    {
        return idCount_;
    }

    public IconDirEntry getEntry(int index)
    {
        return iconDirEntries_[index];
    }

    public String toString()
    {
        return "idReserved: " + idReserved_ + " idType: " + idType_ + " idCount: " + idCount_;
    }
}
