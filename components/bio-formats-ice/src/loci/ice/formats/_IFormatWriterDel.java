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

public interface _IFormatWriterDel extends Ice._ObjectDel
{
    void setId(String id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setStoreAsRetrieve(MetadataStorePrx store, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void close(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void saveBytes1(byte[] bytes, boolean last, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean canDoStacks(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMetadataRetrieve(MetadataRetrievePrx r, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    MetadataRetrieve getMetadataRetrieve(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setFramesPerSecond(int rate, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getFramesPerSecond(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String[] getCompressionTypes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int[] getPixelTypes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isSupportedType(int type, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setCompression(String compress, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isThisType(String name, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFormat(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String[] getSuffixes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
