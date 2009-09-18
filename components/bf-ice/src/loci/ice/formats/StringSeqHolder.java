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

public final class StringSeqHolder
{
    public
    StringSeqHolder()
    {
    }

    public
    StringSeqHolder(String[] value)
    {
        this.value = value;
    }

    public String[] value;
}
