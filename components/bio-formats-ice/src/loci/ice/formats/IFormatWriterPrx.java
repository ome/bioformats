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

public interface IFormatWriterPrx extends Ice.ObjectPrx
{
    public void setId(String id);
    public void setId(String id, java.util.Map<String, String> __ctx);

    public void setStoreAsRetrieve(MetadataStorePrx store);
    public void setStoreAsRetrieve(MetadataStorePrx store, java.util.Map<String, String> __ctx);

    public void close();
    public void close(java.util.Map<String, String> __ctx);

    public void saveBytes1(byte[] bytes, boolean last);
    public void saveBytes1(byte[] bytes, boolean last, java.util.Map<String, String> __ctx);

    public void saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last);
    public void saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last, java.util.Map<String, String> __ctx);

    public boolean canDoStacks();
    public boolean canDoStacks(java.util.Map<String, String> __ctx);

    public void setMetadataRetrieve(MetadataRetrievePrx r);
    public void setMetadataRetrieve(MetadataRetrievePrx r, java.util.Map<String, String> __ctx);

    public MetadataRetrieve getMetadataRetrieve();
    public MetadataRetrieve getMetadataRetrieve(java.util.Map<String, String> __ctx);

    public void setFramesPerSecond(int rate);
    public void setFramesPerSecond(int rate, java.util.Map<String, String> __ctx);

    public int getFramesPerSecond();
    public int getFramesPerSecond(java.util.Map<String, String> __ctx);

    public String[] getCompressionTypes();
    public String[] getCompressionTypes(java.util.Map<String, String> __ctx);

    public int[] getPixelTypes();
    public int[] getPixelTypes(java.util.Map<String, String> __ctx);

    public boolean isSupportedType(int type);
    public boolean isSupportedType(int type, java.util.Map<String, String> __ctx);

    public void setCompression(String compress);
    public void setCompression(String compress, java.util.Map<String, String> __ctx);

    public boolean isThisType(String name);
    public boolean isThisType(String name, java.util.Map<String, String> __ctx);

    public String getFormat();
    public String getFormat(java.util.Map<String, String> __ctx);

    public String[] getSuffixes();
    public String[] getSuffixes(java.util.Map<String, String> __ctx);
}
