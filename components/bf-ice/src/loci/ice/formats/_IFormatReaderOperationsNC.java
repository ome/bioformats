// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package loci.ice.formats;

public interface _IFormatReaderOperationsNC
{
    void setId(String id);

    boolean isThisType(String name, boolean open);

    int getImageCount();

    boolean isRGB();

    int getSizeX();

    int getSizeY();

    int getSizeZ();

    int getSizeC();

    int getSizeT();

    int getPixelType();

    int getEffectiveSizeC();

    int getRGBChannelCount();

    boolean isIndexed();

    boolean isFalseColor();

    byte[][] get8BitLookupTable();

    short[][] get16BitLookupTable();

    int[] getChannelDimLengths();

    String[] getChannelDimTypes();

    int getThumbSizeX();

    int getThumbSizeY();

    boolean isLittleEndian();

    String getDimensionOrder();

    boolean isOrderCertain();

    boolean isInterleaved();

    boolean isInterleavedSubC(int subC);

    byte[] openBytes(int no, int x, int y, int width, int height);

    byte[] openThumbBytes(int no);

    void closeFile(boolean fileOnly);

    String getFormat();

    String[] getSuffixes();

    void close();

    int getSeriesCount();

    void setSeries(int no);

    int getSeries();

    void setNormalized(boolean normalize);

    boolean isNormalized();

    void setMetadataCollected(boolean collect);

    boolean isMetadataCollected();

    void setOriginalMetadataPopulated(boolean populate);

    boolean isOriginalMetadataPopulated();

    void setGroupFiles(boolean group);

    boolean isGroupFiles();

    boolean isMetadataComplete();

    int fileGroupOption(String id);

    String[] getUsedFiles();

    String getCurrentFile();

    int getIndex(int z, int c, int t);

    int[] getZCTCoords(int index);

    void setMetadataFiltered(boolean filter);

    boolean isMetadataFiltered();

    void setMetadataStore(IMetadataPrx store);
}
