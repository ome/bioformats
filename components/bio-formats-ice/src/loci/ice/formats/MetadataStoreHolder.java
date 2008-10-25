// **********************************************************************
//
// Copyright (c) 2003-2007 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.2.1

package loci.ice.formats;

public final class MetadataStoreHolder
{
    public
    MetadataStoreHolder()
    {
    }

    public
    MetadataStoreHolder(MetadataStore value)
    {
        this.value = value;
    }

    public class Patcher implements IceInternal.Patcher
    {
        public void
        patch(Ice.Object v)
        {
            try
            {
                value = (MetadataStore)v;
            }
            catch(ClassCastException ex)
            {
                Ice.UnexpectedObjectException _e = new Ice.UnexpectedObjectException();
                _e.type = v.ice_id();
                _e.expectedType = type();
                throw _e;
            }
        }

        public String
        type()
        {
            return "::formats::MetadataStore";
        }
    }

    public Patcher
    getPatcher()
    {
        return new Patcher();
    }

    public MetadataStore value;
}
