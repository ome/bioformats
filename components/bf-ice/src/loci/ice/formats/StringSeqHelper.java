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

public final class StringSeqHelper
{
    public static void
    write(IceInternal.BasicStream __os, String[] __v)
    {
        __os.writeStringSeq(__v);
    }

    public static String[]
    read(IceInternal.BasicStream __is)
    {
        String[] __v;
        __v = __is.readStringSeq();
        return __v;
    }
}
