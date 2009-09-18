// **********************************************************************
//
// Copyright (c) 2003-2008 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.0

package loci.ice.formats;

public final class ShortSeqHelper
{
    public static void
    write(IceInternal.BasicStream __os, short[] __v)
    {
        __os.writeShortSeq(__v);
    }

    public static short[]
    read(IceInternal.BasicStream __is)
    {
        short[] __v;
        __v = __is.readShortSeq();
        return __v;
    }
}
