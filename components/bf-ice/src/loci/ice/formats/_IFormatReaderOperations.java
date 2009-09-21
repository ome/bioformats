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

public interface _IFormatReaderOperations
{
    void setId(String id, Ice.Current __current);

    boolean isThisType(String name, boolean open, Ice.Current __current);

    int getImageCount(Ice.Current __current);

    boolean isRGB(Ice.Current __current);

    int getSizeX(Ice.Current __current);

    int getSizeY(Ice.Current __current);

    int getSizeZ(Ice.Current __current);

    int getSizeC(Ice.Current __current);

    int getSizeT(Ice.Current __current);

    int getPixelType(Ice.Current __current);

    int getEffectiveSizeC(Ice.Current __current);

    int getRGBChannelCount(Ice.Current __current);

    boolean isIndexed(Ice.Current __current);

    boolean isFalseColor(Ice.Current __current);

    byte[][] get8BitLookupTable(Ice.Current __current);

    short[][] get16BitLookupTable(Ice.Current __current);

    int[] getChannelDimLengths(Ice.Current __current);

    String[] getChannelDimTypes(Ice.Current __current);

    int getThumbSizeX(Ice.Current __current);

    int getThumbSizeY(Ice.Current __current);

    boolean isLittleEndian(Ice.Current __current);

    String getDimensionOrder(Ice.Current __current);

    boolean isOrderCertain(Ice.Current __current);

    boolean isInterleaved(Ice.Current __current);

    boolean isInterleavedSubC(int subC, Ice.Current __current);

    byte[] openBytes(int no, int x, int y, int width, int height, Ice.Current __current);

    byte[] openThumbBytes(int no, Ice.Current __current);

    void closeFile(boolean fileOnly, Ice.Current __current);

    String getFormat(Ice.Current __current);

    String[] getSuffixes(Ice.Current __current);

    void close(Ice.Current __current);

    int getSeriesCount(Ice.Current __current);

    void setSeries(int no, Ice.Current __current);

    int getSeries(Ice.Current __current);

    void setNormalized(boolean normalize, Ice.Current __current);

    boolean isNormalized(Ice.Current __current);

    void setMetadataCollected(boolean collect, Ice.Current __current);

    boolean isMetadataCollected(Ice.Current __current);

    void setOriginalMetadataPopulated(boolean populate, Ice.Current __current);

    boolean isOriginalMetadataPopulated(Ice.Current __current);

    void setGroupFiles(boolean group, Ice.Current __current);

    boolean isGroupFiles(Ice.Current __current);

    boolean isMetadataComplete(Ice.Current __current);

    int fileGroupOption(String id, Ice.Current __current);

    String[] getUsedFiles(Ice.Current __current);

    String getCurrentFile(Ice.Current __current);

    int getIndex(int z, int c, int t, Ice.Current __current);

    int[] getZCTCoords(int index, Ice.Current __current);

    void setMetadataFiltered(boolean filter, Ice.Current __current);

    boolean isMetadataFiltered(Ice.Current __current);

    void setMetadataStore(IMetadataPrx store, Ice.Current __current);
}
