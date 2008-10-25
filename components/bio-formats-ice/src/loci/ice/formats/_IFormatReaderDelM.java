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

public final class _IFormatReaderDelM extends Ice._ObjectDelM implements _IFormatReaderDel
{
    public void
    close(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "close", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    closeFile(boolean fileOnly, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "closeFile", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeBool(fileOnly);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    fileGroupOption(String id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "fileGroupOption", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeString(id);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public short[][]
    get16BitLookupTable(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "get16BitLookupTable", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                short[][] __ret;
                __ret = ShortShortSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public byte[][]
    get8BitLookupTable(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "get8BitLookupTable", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                byte[][] __ret;
                __ret = ByteByteSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int[]
    getChannelDimLengths(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getChannelDimLengths", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int[] __ret;
                __ret = IntSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public String[]
    getChannelDimTypes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getChannelDimTypes", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                String[] __ret;
                __ret = StringSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public String
    getCurrentFile(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getCurrentFile", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                String __ret;
                __ret = __is.readString();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public String
    getDimensionOrder(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getDimensionOrder", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                String __ret;
                __ret = __is.readString();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getEffectiveSizeC(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getEffectiveSizeC", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public String
    getFormat(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getFormat", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                String __ret;
                __ret = __is.readString();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getImageCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getImageCount", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getIndex(int z, int c, int t, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getIndex", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeInt(z);
                __os.writeInt(c);
                __os.writeInt(t);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getPixelType(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getPixelType", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getRGBChannelCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getRGBChannelCount", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getSeries(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSeries", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getSeriesCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSeriesCount", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getSizeC(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSizeC", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getSizeT(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSizeT", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getSizeX(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSizeX", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getSizeY(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSizeY", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getSizeZ(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSizeZ", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public String[]
    getSuffixes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getSuffixes", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                String[] __ret;
                __ret = StringSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getThumbSizeX(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getThumbSizeX", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int
    getThumbSizeY(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getThumbSizeY", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int __ret;
                __ret = __is.readInt();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public String[]
    getUsedFiles(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getUsedFiles", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                String[] __ret;
                __ret = StringSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public int[]
    getZCTCoords(int index, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "getZCTCoords", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeInt(index);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                int[] __ret;
                __ret = IntSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isFalseColor(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isFalseColor", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isGroupFiles(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isGroupFiles", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isIndexed(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isIndexed", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isInterleaved(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isInterleaved", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isInterleavedSubC(int subC, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isInterleavedSubC", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeInt(subC);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isLittleEndian(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isLittleEndian", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isMetadataCollected(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isMetadataCollected", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isMetadataComplete(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isMetadataComplete", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isMetadataFiltered(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isMetadataFiltered", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isNormalized(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isNormalized", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isOrderCertain(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isOrderCertain", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isOriginalMetadataPopulated(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isOriginalMetadataPopulated", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isRGB(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isRGB", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public boolean
    isThisType(String name, boolean open, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "isThisType", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeString(name);
                __os.writeBool(open);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                boolean __ret;
                __ret = __is.readBool();
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public byte[]
    openBytes(int no, int x, int y, int width, int height, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "openBytes", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeInt(no);
                __os.writeInt(x);
                __os.writeInt(y);
                __os.writeInt(width);
                __os.writeInt(height);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                byte[] __ret;
                __ret = ByteSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public byte[]
    openThumbBytes(int no, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "openThumbBytes", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeInt(no);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
                byte[] __ret;
                __ret = ByteSeqHelper.read(__is);
                return __ret;
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setGroupFiles(boolean group, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setGroupFiles", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeBool(group);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setId(String id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setId", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeString(id);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setMetadataCollected(boolean collect, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setMetadataCollected", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeBool(collect);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setMetadataFiltered(boolean filter, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setMetadataFiltered", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeBool(filter);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setMetadataStore(MetadataStorePrx store, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setMetadataStore", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                MetadataStorePrxHelper.__write(__os, store);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setNormalized(boolean normalize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setNormalized", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeBool(normalize);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setOriginalMetadataPopulated(boolean populate, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setOriginalMetadataPopulated", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeBool(populate);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setRetrieveAsStore(MetadataRetrievePrx retrieve, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setRetrieveAsStore", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                MetadataRetrievePrxHelper.__write(__os, retrieve);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }

    public void
    setSeries(int no, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        IceInternal.Outgoing __og = __connection.getOutgoing(__reference, "setSeries", Ice.OperationMode.Normal, __ctx, __compress);
        try
        {
            try
            {
                IceInternal.BasicStream __os = __og.os();
                __os.writeInt(no);
            }
            catch(Ice.LocalException __ex)
            {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            try
            {
                IceInternal.BasicStream __is = __og.is();
                if(!__ok)
                {
                    try
                    {
                        __is.throwException();
                    }
                    catch(Ice.UserException __ex)
                    {
                        throw new Ice.UnknownUserException(__ex.ice_name());
                    }
                }
            }
            catch(Ice.LocalException __ex)
            {
                throw new IceInternal.LocalExceptionWrapper(__ex, false);
            }
        }
        finally
        {
            __connection.reclaimOutgoing(__og);
        }
    }
}
