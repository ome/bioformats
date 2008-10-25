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

public interface _IFormatReaderDel extends Ice._ObjectDel
{
    void setId(String id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setRetrieveAsStore(MetadataRetrievePrx retrieve, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isThisType(String name, boolean open, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getImageCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isRGB(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getSizeX(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getSizeY(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getSizeZ(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getSizeC(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getSizeT(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getPixelType(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getEffectiveSizeC(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getRGBChannelCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isIndexed(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isFalseColor(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    byte[][] get8BitLookupTable(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    short[][] get16BitLookupTable(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int[] getChannelDimLengths(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String[] getChannelDimTypes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getThumbSizeX(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getThumbSizeY(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isLittleEndian(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getDimensionOrder(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isOrderCertain(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isInterleaved(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isInterleavedSubC(int subC, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    byte[] openBytes(int no, int x, int y, int width, int height, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    byte[] openThumbBytes(int no, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void closeFile(boolean fileOnly, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getFormat(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String[] getSuffixes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void close(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getSeriesCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setSeries(int no, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getSeries(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setNormalized(boolean normalize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isNormalized(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMetadataCollected(boolean collect, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isMetadataCollected(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setOriginalMetadataPopulated(boolean populate, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isOriginalMetadataPopulated(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setGroupFiles(boolean group, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isGroupFiles(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isMetadataComplete(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int fileGroupOption(String id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String[] getUsedFiles(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    String getCurrentFile(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int getIndex(int z, int c, int t, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    int[] getZCTCoords(int index, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMetadataFiltered(boolean filter, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    boolean isMetadataFiltered(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;

    void setMetadataStore(MetadataStorePrx store, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper;
}
