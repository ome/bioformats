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

public interface _IFormatWriterOperations
{
    void setId(String id, Ice.Current __current);

    void setStoreAsRetrieve(MetadataStorePrx store, Ice.Current __current);

    void close(Ice.Current __current);

    void saveBytes1(byte[] bytes, boolean last, Ice.Current __current);

    void saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last, Ice.Current __current);

    boolean canDoStacks(Ice.Current __current);

    void setMetadataRetrieve(MetadataRetrievePrx r, Ice.Current __current);

    MetadataRetrieve getMetadataRetrieve(Ice.Current __current);

    void setFramesPerSecond(int rate, Ice.Current __current);

    int getFramesPerSecond(Ice.Current __current);

    String[] getCompressionTypes(Ice.Current __current);

    int[] getPixelTypes(Ice.Current __current);

    boolean isSupportedType(int type, Ice.Current __current);

    void setCompression(String compress, Ice.Current __current);

    boolean isThisType(String name, Ice.Current __current);

    String getFormat(Ice.Current __current);

    String[] getSuffixes(Ice.Current __current);
}
