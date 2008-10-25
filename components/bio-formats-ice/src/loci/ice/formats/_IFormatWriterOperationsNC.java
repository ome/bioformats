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

public interface _IFormatWriterOperationsNC
{
    void setId(String id);

    void setStoreAsRetrieve(MetadataStorePrx store);

    void close();

    void saveBytes1(byte[] bytes, boolean last);

    void saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last);

    boolean canDoStacks();

    void setMetadataRetrieve(MetadataRetrievePrx r);

    MetadataRetrieve getMetadataRetrieve();

    void setFramesPerSecond(int rate);

    int getFramesPerSecond();

    String[] getCompressionTypes();

    int[] getPixelTypes();

    boolean isSupportedType(int type);

    void setCompression(String compress);

    boolean isThisType(String name);

    String getFormat();

    String[] getSuffixes();
}
