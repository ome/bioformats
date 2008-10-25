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

public abstract class _IFormatReaderDisp extends Ice.ObjectImpl implements IFormatReader
{
    protected void
    ice_copyStateFrom(Ice.Object __obj)
        throws java.lang.CloneNotSupportedException
    {
        throw new java.lang.CloneNotSupportedException();
    }

    public static final String[] __ids =
    {
        "::Ice::Object",
        "::formats::IFormatReader"
    };

    public boolean
    ice_isA(String s)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public boolean
    ice_isA(String s, Ice.Current __current)
    {
        return java.util.Arrays.binarySearch(__ids, s) >= 0;
    }

    public String[]
    ice_ids()
    {
        return __ids;
    }

    public String[]
    ice_ids(Ice.Current __current)
    {
        return __ids;
    }

    public String
    ice_id()
    {
        return __ids[1];
    }

    public String
    ice_id(Ice.Current __current)
    {
        return __ids[1];
    }

    public static String
    ice_staticId()
    {
        return __ids[1];
    }

    public final void
    close()
    {
        close(null);
    }

    public final void
    closeFile(boolean fileOnly)
    {
        closeFile(fileOnly, null);
    }

    public final int
    fileGroupOption(String id)
    {
        return fileGroupOption(id, null);
    }

    public final short[][]
    get16BitLookupTable()
    {
        return get16BitLookupTable(null);
    }

    public final byte[][]
    get8BitLookupTable()
    {
        return get8BitLookupTable(null);
    }

    public final int[]
    getChannelDimLengths()
    {
        return getChannelDimLengths(null);
    }

    public final String[]
    getChannelDimTypes()
    {
        return getChannelDimTypes(null);
    }

    public final String
    getCurrentFile()
    {
        return getCurrentFile(null);
    }

    public final String
    getDimensionOrder()
    {
        return getDimensionOrder(null);
    }

    public final int
    getEffectiveSizeC()
    {
        return getEffectiveSizeC(null);
    }

    public final String
    getFormat()
    {
        return getFormat(null);
    }

    public final int
    getImageCount()
    {
        return getImageCount(null);
    }

    public final int
    getIndex(int z, int c, int t)
    {
        return getIndex(z, c, t, null);
    }

    public final int
    getPixelType()
    {
        return getPixelType(null);
    }

    public final int
    getRGBChannelCount()
    {
        return getRGBChannelCount(null);
    }

    public final int
    getSeries()
    {
        return getSeries(null);
    }

    public final int
    getSeriesCount()
    {
        return getSeriesCount(null);
    }

    public final int
    getSizeC()
    {
        return getSizeC(null);
    }

    public final int
    getSizeT()
    {
        return getSizeT(null);
    }

    public final int
    getSizeX()
    {
        return getSizeX(null);
    }

    public final int
    getSizeY()
    {
        return getSizeY(null);
    }

    public final int
    getSizeZ()
    {
        return getSizeZ(null);
    }

    public final String[]
    getSuffixes()
    {
        return getSuffixes(null);
    }

    public final int
    getThumbSizeX()
    {
        return getThumbSizeX(null);
    }

    public final int
    getThumbSizeY()
    {
        return getThumbSizeY(null);
    }

    public final String[]
    getUsedFiles()
    {
        return getUsedFiles(null);
    }

    public final int[]
    getZCTCoords(int index)
    {
        return getZCTCoords(index, null);
    }

    public final boolean
    isFalseColor()
    {
        return isFalseColor(null);
    }

    public final boolean
    isGroupFiles()
    {
        return isGroupFiles(null);
    }

    public final boolean
    isIndexed()
    {
        return isIndexed(null);
    }

    public final boolean
    isInterleaved()
    {
        return isInterleaved(null);
    }

    public final boolean
    isInterleavedSubC(int subC)
    {
        return isInterleavedSubC(subC, null);
    }

    public final boolean
    isLittleEndian()
    {
        return isLittleEndian(null);
    }

    public final boolean
    isMetadataCollected()
    {
        return isMetadataCollected(null);
    }

    public final boolean
    isMetadataComplete()
    {
        return isMetadataComplete(null);
    }

    public final boolean
    isMetadataFiltered()
    {
        return isMetadataFiltered(null);
    }

    public final boolean
    isNormalized()
    {
        return isNormalized(null);
    }

    public final boolean
    isOrderCertain()
    {
        return isOrderCertain(null);
    }

    public final boolean
    isOriginalMetadataPopulated()
    {
        return isOriginalMetadataPopulated(null);
    }

    public final boolean
    isRGB()
    {
        return isRGB(null);
    }

    public final boolean
    isThisType(String name, boolean open)
    {
        return isThisType(name, open, null);
    }

    public final byte[]
    openBytes(int no, int x, int y, int width, int height)
    {
        return openBytes(no, x, y, width, height, null);
    }

    public final byte[]
    openThumbBytes(int no)
    {
        return openThumbBytes(no, null);
    }

    public final void
    setGroupFiles(boolean group)
    {
        setGroupFiles(group, null);
    }

    public final void
    setId(String id)
    {
        setId(id, null);
    }

    public final void
    setMetadataCollected(boolean collect)
    {
        setMetadataCollected(collect, null);
    }

    public final void
    setMetadataFiltered(boolean filter)
    {
        setMetadataFiltered(filter, null);
    }

    public final void
    setMetadataStore(MetadataStorePrx store)
    {
        setMetadataStore(store, null);
    }

    public final void
    setNormalized(boolean normalize)
    {
        setNormalized(normalize, null);
    }

    public final void
    setOriginalMetadataPopulated(boolean populate)
    {
        setOriginalMetadataPopulated(populate, null);
    }

    public final void
    setRetrieveAsStore(MetadataRetrievePrx retrieve)
    {
        setRetrieveAsStore(retrieve, null);
    }

    public final void
    setSeries(int no)
    {
        setSeries(no, null);
    }

    public static IceInternal.DispatchStatus
    ___setId(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        String id;
        id = __is.readString();
        __obj.setId(id, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setRetrieveAsStore(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        MetadataRetrievePrx retrieve;
        retrieve = MetadataRetrievePrxHelper.__read(__is);
        __obj.setRetrieveAsStore(retrieve, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isThisType(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        String name;
        name = __is.readString();
        boolean open;
        open = __is.readBool();
        boolean __ret = __obj.isThisType(name, open, __current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getImageCount(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getImageCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isRGB(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isRGB(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSizeX(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getSizeX(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSizeY(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getSizeY(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSizeZ(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getSizeZ(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSizeC(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getSizeC(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSizeT(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getSizeT(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getPixelType(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getPixelType(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getEffectiveSizeC(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getEffectiveSizeC(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getRGBChannelCount(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getRGBChannelCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isIndexed(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isIndexed(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isFalseColor(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isFalseColor(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___get8BitLookupTable(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        byte[][] __ret = __obj.get8BitLookupTable(__current);
        ByteByteSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___get16BitLookupTable(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        short[][] __ret = __obj.get16BitLookupTable(__current);
        ShortShortSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getChannelDimLengths(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int[] __ret = __obj.getChannelDimLengths(__current);
        IntSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getChannelDimTypes(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String[] __ret = __obj.getChannelDimTypes(__current);
        StringSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getThumbSizeX(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getThumbSizeX(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getThumbSizeY(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getThumbSizeY(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isLittleEndian(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isLittleEndian(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getDimensionOrder(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getDimensionOrder(__current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isOrderCertain(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isOrderCertain(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isInterleaved(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isInterleaved(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isInterleavedSubC(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int subC;
        subC = __is.readInt();
        boolean __ret = __obj.isInterleavedSubC(subC, __current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___openBytes(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int no;
        no = __is.readInt();
        int x;
        x = __is.readInt();
        int y;
        y = __is.readInt();
        int width;
        width = __is.readInt();
        int height;
        height = __is.readInt();
        byte[] __ret = __obj.openBytes(no, x, y, width, height, __current);
        ByteSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___openThumbBytes(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int no;
        no = __is.readInt();
        byte[] __ret = __obj.openThumbBytes(no, __current);
        ByteSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___closeFile(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean fileOnly;
        fileOnly = __is.readBool();
        __obj.closeFile(fileOnly, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getFormat(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFormat(__current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSuffixes(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String[] __ret = __obj.getSuffixes(__current);
        StringSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___close(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __obj.close(__current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSeriesCount(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getSeriesCount(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setSeries(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        int no;
        no = __is.readInt();
        __obj.setSeries(no, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getSeries(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getSeries(__current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setNormalized(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean normalize;
        normalize = __is.readBool();
        __obj.setNormalized(normalize, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isNormalized(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isNormalized(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setMetadataCollected(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean collect;
        collect = __is.readBool();
        __obj.setMetadataCollected(collect, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isMetadataCollected(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isMetadataCollected(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setOriginalMetadataPopulated(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean populate;
        populate = __is.readBool();
        __obj.setOriginalMetadataPopulated(populate, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isOriginalMetadataPopulated(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isOriginalMetadataPopulated(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setGroupFiles(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean group;
        group = __is.readBool();
        __obj.setGroupFiles(group, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isGroupFiles(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isGroupFiles(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isMetadataComplete(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isMetadataComplete(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___fileGroupOption(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        String id;
        id = __is.readString();
        int __ret = __obj.fileGroupOption(id, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getUsedFiles(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String[] __ret = __obj.getUsedFiles(__current);
        StringSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getCurrentFile(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getCurrentFile(__current);
        __os.writeString(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getIndex(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int z;
        z = __is.readInt();
        int c;
        c = __is.readInt();
        int t;
        t = __is.readInt();
        int __ret = __obj.getIndex(z, c, t, __current);
        __os.writeInt(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___getZCTCoords(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        IceInternal.BasicStream __os = __inS.os();
        int index;
        index = __is.readInt();
        int[] __ret = __obj.getZCTCoords(index, __current);
        IntSeqHelper.write(__os, __ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setMetadataFiltered(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        boolean filter;
        filter = __is.readBool();
        __obj.setMetadataFiltered(filter, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___isMetadataFiltered(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isMetadataFiltered(__current);
        __os.writeBool(__ret);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    public static IceInternal.DispatchStatus
    ___setMetadataStore(IFormatReader __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        MetadataStorePrx store;
        store = MetadataStorePrxHelper.__read(__is);
        __obj.setMetadataStore(store, __current);
        return IceInternal.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "close",
        "closeFile",
        "fileGroupOption",
        "get16BitLookupTable",
        "get8BitLookupTable",
        "getChannelDimLengths",
        "getChannelDimTypes",
        "getCurrentFile",
        "getDimensionOrder",
        "getEffectiveSizeC",
        "getFormat",
        "getImageCount",
        "getIndex",
        "getPixelType",
        "getRGBChannelCount",
        "getSeries",
        "getSeriesCount",
        "getSizeC",
        "getSizeT",
        "getSizeX",
        "getSizeY",
        "getSizeZ",
        "getSuffixes",
        "getThumbSizeX",
        "getThumbSizeY",
        "getUsedFiles",
        "getZCTCoords",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "isFalseColor",
        "isGroupFiles",
        "isIndexed",
        "isInterleaved",
        "isInterleavedSubC",
        "isLittleEndian",
        "isMetadataCollected",
        "isMetadataComplete",
        "isMetadataFiltered",
        "isNormalized",
        "isOrderCertain",
        "isOriginalMetadataPopulated",
        "isRGB",
        "isThisType",
        "openBytes",
        "openThumbBytes",
        "setGroupFiles",
        "setId",
        "setMetadataCollected",
        "setMetadataFiltered",
        "setMetadataStore",
        "setNormalized",
        "setOriginalMetadataPopulated",
        "setRetrieveAsStore",
        "setSeries"
    };

    public IceInternal.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            return IceInternal.DispatchStatus.DispatchOperationNotExist;
        }

        switch(pos)
        {
            case 0:
            {
                return ___close(this, in, __current);
            }
            case 1:
            {
                return ___closeFile(this, in, __current);
            }
            case 2:
            {
                return ___fileGroupOption(this, in, __current);
            }
            case 3:
            {
                return ___get16BitLookupTable(this, in, __current);
            }
            case 4:
            {
                return ___get8BitLookupTable(this, in, __current);
            }
            case 5:
            {
                return ___getChannelDimLengths(this, in, __current);
            }
            case 6:
            {
                return ___getChannelDimTypes(this, in, __current);
            }
            case 7:
            {
                return ___getCurrentFile(this, in, __current);
            }
            case 8:
            {
                return ___getDimensionOrder(this, in, __current);
            }
            case 9:
            {
                return ___getEffectiveSizeC(this, in, __current);
            }
            case 10:
            {
                return ___getFormat(this, in, __current);
            }
            case 11:
            {
                return ___getImageCount(this, in, __current);
            }
            case 12:
            {
                return ___getIndex(this, in, __current);
            }
            case 13:
            {
                return ___getPixelType(this, in, __current);
            }
            case 14:
            {
                return ___getRGBChannelCount(this, in, __current);
            }
            case 15:
            {
                return ___getSeries(this, in, __current);
            }
            case 16:
            {
                return ___getSeriesCount(this, in, __current);
            }
            case 17:
            {
                return ___getSizeC(this, in, __current);
            }
            case 18:
            {
                return ___getSizeT(this, in, __current);
            }
            case 19:
            {
                return ___getSizeX(this, in, __current);
            }
            case 20:
            {
                return ___getSizeY(this, in, __current);
            }
            case 21:
            {
                return ___getSizeZ(this, in, __current);
            }
            case 22:
            {
                return ___getSuffixes(this, in, __current);
            }
            case 23:
            {
                return ___getThumbSizeX(this, in, __current);
            }
            case 24:
            {
                return ___getThumbSizeY(this, in, __current);
            }
            case 25:
            {
                return ___getUsedFiles(this, in, __current);
            }
            case 26:
            {
                return ___getZCTCoords(this, in, __current);
            }
            case 27:
            {
                return ___ice_id(this, in, __current);
            }
            case 28:
            {
                return ___ice_ids(this, in, __current);
            }
            case 29:
            {
                return ___ice_isA(this, in, __current);
            }
            case 30:
            {
                return ___ice_ping(this, in, __current);
            }
            case 31:
            {
                return ___isFalseColor(this, in, __current);
            }
            case 32:
            {
                return ___isGroupFiles(this, in, __current);
            }
            case 33:
            {
                return ___isIndexed(this, in, __current);
            }
            case 34:
            {
                return ___isInterleaved(this, in, __current);
            }
            case 35:
            {
                return ___isInterleavedSubC(this, in, __current);
            }
            case 36:
            {
                return ___isLittleEndian(this, in, __current);
            }
            case 37:
            {
                return ___isMetadataCollected(this, in, __current);
            }
            case 38:
            {
                return ___isMetadataComplete(this, in, __current);
            }
            case 39:
            {
                return ___isMetadataFiltered(this, in, __current);
            }
            case 40:
            {
                return ___isNormalized(this, in, __current);
            }
            case 41:
            {
                return ___isOrderCertain(this, in, __current);
            }
            case 42:
            {
                return ___isOriginalMetadataPopulated(this, in, __current);
            }
            case 43:
            {
                return ___isRGB(this, in, __current);
            }
            case 44:
            {
                return ___isThisType(this, in, __current);
            }
            case 45:
            {
                return ___openBytes(this, in, __current);
            }
            case 46:
            {
                return ___openThumbBytes(this, in, __current);
            }
            case 47:
            {
                return ___setGroupFiles(this, in, __current);
            }
            case 48:
            {
                return ___setId(this, in, __current);
            }
            case 49:
            {
                return ___setMetadataCollected(this, in, __current);
            }
            case 50:
            {
                return ___setMetadataFiltered(this, in, __current);
            }
            case 51:
            {
                return ___setMetadataStore(this, in, __current);
            }
            case 52:
            {
                return ___setNormalized(this, in, __current);
            }
            case 53:
            {
                return ___setOriginalMetadataPopulated(this, in, __current);
            }
            case 54:
            {
                return ___setRetrieveAsStore(this, in, __current);
            }
            case 55:
            {
                return ___setSeries(this, in, __current);
            }
        }

        assert(false);
        return IceInternal.DispatchStatus.DispatchOperationNotExist;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeTypeId(ice_staticId());
        __os.startWriteSlice();
        __os.endWriteSlice();
        super.__write(__os);
    }

    public void
    __read(IceInternal.BasicStream __is, boolean __rid)
    {
        if(__rid)
        {
            __is.readTypeId();
        }
        __is.startReadSlice();
        __is.endReadSlice();
        super.__read(__is, true);
    }

    public void
    __write(Ice.OutputStream __outS)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::IFormatReader was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::IFormatReader was not generated with stream support";
        throw ex;
    }
}
