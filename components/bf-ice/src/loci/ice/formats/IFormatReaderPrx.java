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

public interface IFormatReaderPrx extends Ice.ObjectPrx
{
    public void setId(String id);
    public void setId(String id, java.util.Map<String, String> __ctx);

    public boolean isThisType(String name, boolean open);
    public boolean isThisType(String name, boolean open, java.util.Map<String, String> __ctx);

    public int getImageCount();
    public int getImageCount(java.util.Map<String, String> __ctx);

    public boolean isRGB();
    public boolean isRGB(java.util.Map<String, String> __ctx);

    public int getSizeX();
    public int getSizeX(java.util.Map<String, String> __ctx);

    public int getSizeY();
    public int getSizeY(java.util.Map<String, String> __ctx);

    public int getSizeZ();
    public int getSizeZ(java.util.Map<String, String> __ctx);

    public int getSizeC();
    public int getSizeC(java.util.Map<String, String> __ctx);

    public int getSizeT();
    public int getSizeT(java.util.Map<String, String> __ctx);

    public int getPixelType();
    public int getPixelType(java.util.Map<String, String> __ctx);

    public int getEffectiveSizeC();
    public int getEffectiveSizeC(java.util.Map<String, String> __ctx);

    public int getRGBChannelCount();
    public int getRGBChannelCount(java.util.Map<String, String> __ctx);

    public boolean isIndexed();
    public boolean isIndexed(java.util.Map<String, String> __ctx);

    public boolean isFalseColor();
    public boolean isFalseColor(java.util.Map<String, String> __ctx);

    public byte[][] get8BitLookupTable();
    public byte[][] get8BitLookupTable(java.util.Map<String, String> __ctx);

    public short[][] get16BitLookupTable();
    public short[][] get16BitLookupTable(java.util.Map<String, String> __ctx);

    public int[] getChannelDimLengths();
    public int[] getChannelDimLengths(java.util.Map<String, String> __ctx);

    public String[] getChannelDimTypes();
    public String[] getChannelDimTypes(java.util.Map<String, String> __ctx);

    public int getThumbSizeX();
    public int getThumbSizeX(java.util.Map<String, String> __ctx);

    public int getThumbSizeY();
    public int getThumbSizeY(java.util.Map<String, String> __ctx);

    public boolean isLittleEndian();
    public boolean isLittleEndian(java.util.Map<String, String> __ctx);

    public String getDimensionOrder();
    public String getDimensionOrder(java.util.Map<String, String> __ctx);

    public boolean isOrderCertain();
    public boolean isOrderCertain(java.util.Map<String, String> __ctx);

    public boolean isInterleaved();
    public boolean isInterleaved(java.util.Map<String, String> __ctx);

    public boolean isInterleavedSubC(int subC);
    public boolean isInterleavedSubC(int subC, java.util.Map<String, String> __ctx);

    public byte[] openBytes(int no, int x, int y, int width, int height);
    public byte[] openBytes(int no, int x, int y, int width, int height, java.util.Map<String, String> __ctx);

    public byte[] openThumbBytes(int no);
    public byte[] openThumbBytes(int no, java.util.Map<String, String> __ctx);

    public void closeFile(boolean fileOnly);
    public void closeFile(boolean fileOnly, java.util.Map<String, String> __ctx);

    public String getFormat();
    public String getFormat(java.util.Map<String, String> __ctx);

    public String[] getSuffixes();
    public String[] getSuffixes(java.util.Map<String, String> __ctx);

    public void close();
    public void close(java.util.Map<String, String> __ctx);

    public int getSeriesCount();
    public int getSeriesCount(java.util.Map<String, String> __ctx);

    public void setSeries(int no);
    public void setSeries(int no, java.util.Map<String, String> __ctx);

    public int getSeries();
    public int getSeries(java.util.Map<String, String> __ctx);

    public void setNormalized(boolean normalize);
    public void setNormalized(boolean normalize, java.util.Map<String, String> __ctx);

    public boolean isNormalized();
    public boolean isNormalized(java.util.Map<String, String> __ctx);

    public void setMetadataCollected(boolean collect);
    public void setMetadataCollected(boolean collect, java.util.Map<String, String> __ctx);

    public boolean isMetadataCollected();
    public boolean isMetadataCollected(java.util.Map<String, String> __ctx);

    public void setOriginalMetadataPopulated(boolean populate);
    public void setOriginalMetadataPopulated(boolean populate, java.util.Map<String, String> __ctx);

    public boolean isOriginalMetadataPopulated();
    public boolean isOriginalMetadataPopulated(java.util.Map<String, String> __ctx);

    public void setGroupFiles(boolean group);
    public void setGroupFiles(boolean group, java.util.Map<String, String> __ctx);

    public boolean isGroupFiles();
    public boolean isGroupFiles(java.util.Map<String, String> __ctx);

    public boolean isMetadataComplete();
    public boolean isMetadataComplete(java.util.Map<String, String> __ctx);

    public int fileGroupOption(String id);
    public int fileGroupOption(String id, java.util.Map<String, String> __ctx);

    public String[] getUsedFiles();
    public String[] getUsedFiles(java.util.Map<String, String> __ctx);

    public String getCurrentFile();
    public String getCurrentFile(java.util.Map<String, String> __ctx);

    public int getIndex(int z, int c, int t);
    public int getIndex(int z, int c, int t, java.util.Map<String, String> __ctx);

    public int[] getZCTCoords(int index);
    public int[] getZCTCoords(int index, java.util.Map<String, String> __ctx);

    public void setMetadataFiltered(boolean filter);
    public void setMetadataFiltered(boolean filter, java.util.Map<String, String> __ctx);

    public boolean isMetadataFiltered();
    public boolean isMetadataFiltered(java.util.Map<String, String> __ctx);

    public void setMetadataStore(IMetadataPrx store);
    public void setMetadataStore(IMetadataPrx store, java.util.Map<String, String> __ctx);
}
