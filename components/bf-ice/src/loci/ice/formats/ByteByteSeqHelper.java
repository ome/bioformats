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

public final class ByteByteSeqHelper
{
    public static void
    write(IceInternal.BasicStream __os, byte[][] __v)
    {
        if(__v == null)
        {
            __os.writeSize(0);
        }
        else
        {
            __os.writeSize(__v.length);
            for(int __i0 = 0; __i0 < __v.length; __i0++)
            {
                ByteSeqHelper.write(__os, __v[__i0]);
            }
        }
    }

    public static byte[][]
    read(IceInternal.BasicStream __is)
    {
        byte[][] __v;
        final int __len0 = __is.readSize();
        __is.startSeq(__len0, 1);
        __v = new byte[__len0][];
        for(int __i0 = 0; __i0 < __len0; __i0++)
        {
            __v[__i0] = ByteSeqHelper.read(__is);
            __is.endElement();
        }
        __is.endSeq(__len0);
        return __v;
    }
}
