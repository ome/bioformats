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

public abstract class _IFormatWriterDisp extends Ice.ObjectImpl implements IFormatWriter
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
        "::formats::IFormatWriter"
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

    public final boolean
    canDoStacks()
    {
        return canDoStacks(null);
    }

    public final void
    close()
    {
        close(null);
    }

    public final String[]
    getCompressionTypes()
    {
        return getCompressionTypes(null);
    }

    public final String
    getFormat()
    {
        return getFormat(null);
    }

    public final int
    getFramesPerSecond()
    {
        return getFramesPerSecond(null);
    }

    public final IMetadata
    getMetadataRetrieve()
    {
        return getMetadataRetrieve(null);
    }

    public final int[]
    getPixelTypes()
    {
        return getPixelTypes(null);
    }

    public final String[]
    getSuffixes()
    {
        return getSuffixes(null);
    }

    public final boolean
    isSupportedType(int type)
    {
        return isSupportedType(type, null);
    }

    public final boolean
    isThisType(String name)
    {
        return isThisType(name, null);
    }

    public final void
    saveBytes1(byte[] bytes, boolean last)
    {
        saveBytes1(bytes, last, null);
    }

    public final void
    saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last)
    {
        saveBytes2(bytes, series, lastInSeries, last, null);
    }

    public final void
    setCompression(String compress)
    {
        setCompression(compress, null);
    }

    public final void
    setFramesPerSecond(int rate)
    {
        setFramesPerSecond(rate, null);
    }

    public final void
    setId(String id)
    {
        setId(id, null);
    }

    public final void
    setMetadataRetrieve(IMetadataPrx r)
    {
        setMetadataRetrieve(r, null);
    }

    public static Ice.DispatchStatus
    ___setId(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String id;
        id = __is.readString();
        __is.endReadEncaps();
        __obj.setId(id, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___close(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        __obj.close(__current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___saveBytes1(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] bytes;
        bytes = ByteSeqHelper.read(__is);
        boolean last;
        last = __is.readBool();
        __is.endReadEncaps();
        __obj.saveBytes1(bytes, last, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___saveBytes2(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        byte[] bytes;
        bytes = ByteSeqHelper.read(__is);
        int series;
        series = __is.readInt();
        boolean lastInSeries;
        lastInSeries = __is.readBool();
        boolean last;
        last = __is.readBool();
        __is.endReadEncaps();
        __obj.saveBytes2(bytes, series, lastInSeries, last, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___canDoStacks(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.canDoStacks(__current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setMetadataRetrieve(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        IMetadataPrx r;
        r = IMetadataPrxHelper.__read(__is);
        __is.endReadEncaps();
        __obj.setMetadataRetrieve(r, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getMetadataRetrieve(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        IMetadata __ret = __obj.getMetadataRetrieve(__current);
        __os.writeObject(__ret);
        __os.writePendingObjects();
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setFramesPerSecond(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int rate;
        rate = __is.readInt();
        __is.endReadEncaps();
        __obj.setFramesPerSecond(rate, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFramesPerSecond(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int __ret = __obj.getFramesPerSecond(__current);
        __os.writeInt(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getCompressionTypes(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String[] __ret = __obj.getCompressionTypes(__current);
        StringSeqHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getPixelTypes(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        int[] __ret = __obj.getPixelTypes(__current);
        IntSeqHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___isSupportedType(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        int type;
        type = __is.readInt();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isSupportedType(type, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___setCompression(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String compress;
        compress = __is.readString();
        __is.endReadEncaps();
        __obj.setCompression(compress, __current);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___isThisType(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        IceInternal.BasicStream __is = __inS.is();
        __is.startReadEncaps();
        String name;
        name = __is.readString();
        __is.endReadEncaps();
        IceInternal.BasicStream __os = __inS.os();
        boolean __ret = __obj.isThisType(name, __current);
        __os.writeBool(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getFormat(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String __ret = __obj.getFormat(__current);
        __os.writeString(__ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    public static Ice.DispatchStatus
    ___getSuffixes(IFormatWriter __obj, IceInternal.Incoming __inS, Ice.Current __current)
    {
        __checkMode(Ice.OperationMode.Normal, __current.mode);
        __inS.is().skipEmptyEncaps();
        IceInternal.BasicStream __os = __inS.os();
        String[] __ret = __obj.getSuffixes(__current);
        StringSeqHelper.write(__os, __ret);
        return Ice.DispatchStatus.DispatchOK;
    }

    private final static String[] __all =
    {
        "canDoStacks",
        "close",
        "getCompressionTypes",
        "getFormat",
        "getFramesPerSecond",
        "getMetadataRetrieve",
        "getPixelTypes",
        "getSuffixes",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "isSupportedType",
        "isThisType",
        "saveBytes1",
        "saveBytes2",
        "setCompression",
        "setFramesPerSecond",
        "setId",
        "setMetadataRetrieve"
    };

    public Ice.DispatchStatus
    __dispatch(IceInternal.Incoming in, Ice.Current __current)
    {
        int pos = java.util.Arrays.binarySearch(__all, __current.operation);
        if(pos < 0)
        {
            throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return ___canDoStacks(this, in, __current);
            }
            case 1:
            {
                return ___close(this, in, __current);
            }
            case 2:
            {
                return ___getCompressionTypes(this, in, __current);
            }
            case 3:
            {
                return ___getFormat(this, in, __current);
            }
            case 4:
            {
                return ___getFramesPerSecond(this, in, __current);
            }
            case 5:
            {
                return ___getMetadataRetrieve(this, in, __current);
            }
            case 6:
            {
                return ___getPixelTypes(this, in, __current);
            }
            case 7:
            {
                return ___getSuffixes(this, in, __current);
            }
            case 8:
            {
                return ___ice_id(this, in, __current);
            }
            case 9:
            {
                return ___ice_ids(this, in, __current);
            }
            case 10:
            {
                return ___ice_isA(this, in, __current);
            }
            case 11:
            {
                return ___ice_ping(this, in, __current);
            }
            case 12:
            {
                return ___isSupportedType(this, in, __current);
            }
            case 13:
            {
                return ___isThisType(this, in, __current);
            }
            case 14:
            {
                return ___saveBytes1(this, in, __current);
            }
            case 15:
            {
                return ___saveBytes2(this, in, __current);
            }
            case 16:
            {
                return ___setCompression(this, in, __current);
            }
            case 17:
            {
                return ___setFramesPerSecond(this, in, __current);
            }
            case 18:
            {
                return ___setId(this, in, __current);
            }
            case 19:
            {
                return ___setMetadataRetrieve(this, in, __current);
            }
        }

        assert(false);
        throw new Ice.OperationNotExistException(__current.id, __current.facet, __current.operation);
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
        ex.reason = "type formats::IFormatWriter was not generated with stream support";
        throw ex;
    }

    public void
    __read(Ice.InputStream __inS, boolean __rid)
    {
        Ice.MarshalException ex = new Ice.MarshalException();
        ex.reason = "type formats::IFormatWriter was not generated with stream support";
        throw ex;
    }
}
