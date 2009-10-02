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

public final class IFormatReaderPrxHelper extends Ice.ObjectPrxHelperBase implements IFormatReaderPrx
{
    public void
    close()
    {
        close(null, false);
    }

    public void
    close(java.util.Map<String, String> __ctx)
    {
        close(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    close(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.close(__ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    closeFile(boolean fileOnly)
    {
        closeFile(fileOnly, null, false);
    }

    public void
    closeFile(boolean fileOnly, java.util.Map<String, String> __ctx)
    {
        closeFile(fileOnly, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    closeFile(boolean fileOnly, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.closeFile(fileOnly, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    fileGroupOption(String id)
    {
        return fileGroupOption(id, null, false);
    }

    public int
    fileGroupOption(String id, java.util.Map<String, String> __ctx)
    {
        return fileGroupOption(id, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    fileGroupOption(String id, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("fileGroupOption");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.fileGroupOption(id, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public short[][]
    get16BitLookupTable()
    {
        return get16BitLookupTable(null, false);
    }

    public short[][]
    get16BitLookupTable(java.util.Map<String, String> __ctx)
    {
        return get16BitLookupTable(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private short[][]
    get16BitLookupTable(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("get16BitLookupTable");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.get16BitLookupTable(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public byte[][]
    get8BitLookupTable()
    {
        return get8BitLookupTable(null, false);
    }

    public byte[][]
    get8BitLookupTable(java.util.Map<String, String> __ctx)
    {
        return get8BitLookupTable(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private byte[][]
    get8BitLookupTable(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("get8BitLookupTable");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.get8BitLookupTable(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int[]
    getChannelDimLengths()
    {
        return getChannelDimLengths(null, false);
    }

    public int[]
    getChannelDimLengths(java.util.Map<String, String> __ctx)
    {
        return getChannelDimLengths(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int[]
    getChannelDimLengths(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getChannelDimLengths");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getChannelDimLengths(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String[]
    getChannelDimTypes()
    {
        return getChannelDimTypes(null, false);
    }

    public String[]
    getChannelDimTypes(java.util.Map<String, String> __ctx)
    {
        return getChannelDimTypes(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String[]
    getChannelDimTypes(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getChannelDimTypes");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getChannelDimTypes(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String
    getCurrentFile()
    {
        return getCurrentFile(null, false);
    }

    public String
    getCurrentFile(java.util.Map<String, String> __ctx)
    {
        return getCurrentFile(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getCurrentFile(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getCurrentFile");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getCurrentFile(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String
    getDimensionOrder()
    {
        return getDimensionOrder(null, false);
    }

    public String
    getDimensionOrder(java.util.Map<String, String> __ctx)
    {
        return getDimensionOrder(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDimensionOrder(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getDimensionOrder");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getDimensionOrder(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getEffectiveSizeC()
    {
        return getEffectiveSizeC(null, false);
    }

    public int
    getEffectiveSizeC(java.util.Map<String, String> __ctx)
    {
        return getEffectiveSizeC(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getEffectiveSizeC(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getEffectiveSizeC");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getEffectiveSizeC(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String
    getFormat()
    {
        return getFormat(null, false);
    }

    public String
    getFormat(java.util.Map<String, String> __ctx)
    {
        return getFormat(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFormat(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getFormat");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getFormat(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getImageCount()
    {
        return getImageCount(null, false);
    }

    public int
    getImageCount(java.util.Map<String, String> __ctx)
    {
        return getImageCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getImageCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getImageCount");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getImageCount(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getIndex(int z, int c, int t)
    {
        return getIndex(z, c, t, null, false);
    }

    public int
    getIndex(int z, int c, int t, java.util.Map<String, String> __ctx)
    {
        return getIndex(z, c, t, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getIndex(int z, int c, int t, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getIndex");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getIndex(z, c, t, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getPixelType()
    {
        return getPixelType(null, false);
    }

    public int
    getPixelType(java.util.Map<String, String> __ctx)
    {
        return getPixelType(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPixelType(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getPixelType");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getPixelType(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getRGBChannelCount()
    {
        return getRGBChannelCount(null, false);
    }

    public int
    getRGBChannelCount(java.util.Map<String, String> __ctx)
    {
        return getRGBChannelCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getRGBChannelCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getRGBChannelCount");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getRGBChannelCount(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getSeries()
    {
        return getSeries(null, false);
    }

    public int
    getSeries(java.util.Map<String, String> __ctx)
    {
        return getSeries(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getSeries(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSeries");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSeries(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getSeriesCount()
    {
        return getSeriesCount(null, false);
    }

    public int
    getSeriesCount(java.util.Map<String, String> __ctx)
    {
        return getSeriesCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getSeriesCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSeriesCount");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSeriesCount(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getSizeC()
    {
        return getSizeC(null, false);
    }

    public int
    getSizeC(java.util.Map<String, String> __ctx)
    {
        return getSizeC(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getSizeC(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSizeC");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSizeC(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getSizeT()
    {
        return getSizeT(null, false);
    }

    public int
    getSizeT(java.util.Map<String, String> __ctx)
    {
        return getSizeT(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getSizeT(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSizeT");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSizeT(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getSizeX()
    {
        return getSizeX(null, false);
    }

    public int
    getSizeX(java.util.Map<String, String> __ctx)
    {
        return getSizeX(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getSizeX(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSizeX");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSizeX(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getSizeY()
    {
        return getSizeY(null, false);
    }

    public int
    getSizeY(java.util.Map<String, String> __ctx)
    {
        return getSizeY(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getSizeY(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSizeY");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSizeY(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getSizeZ()
    {
        return getSizeZ(null, false);
    }

    public int
    getSizeZ(java.util.Map<String, String> __ctx)
    {
        return getSizeZ(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getSizeZ(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSizeZ");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSizeZ(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String[]
    getSuffixes()
    {
        return getSuffixes(null, false);
    }

    public String[]
    getSuffixes(java.util.Map<String, String> __ctx)
    {
        return getSuffixes(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String[]
    getSuffixes(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getSuffixes");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getSuffixes(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getThumbSizeX()
    {
        return getThumbSizeX(null, false);
    }

    public int
    getThumbSizeX(java.util.Map<String, String> __ctx)
    {
        return getThumbSizeX(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getThumbSizeX(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getThumbSizeX");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getThumbSizeX(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int
    getThumbSizeY()
    {
        return getThumbSizeY(null, false);
    }

    public int
    getThumbSizeY(java.util.Map<String, String> __ctx)
    {
        return getThumbSizeY(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getThumbSizeY(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getThumbSizeY");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getThumbSizeY(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public String[]
    getUsedFiles()
    {
        return getUsedFiles(null, false);
    }

    public String[]
    getUsedFiles(java.util.Map<String, String> __ctx)
    {
        return getUsedFiles(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String[]
    getUsedFiles(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getUsedFiles");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getUsedFiles(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public int[]
    getZCTCoords(int index)
    {
        return getZCTCoords(index, null, false);
    }

    public int[]
    getZCTCoords(int index, java.util.Map<String, String> __ctx)
    {
        return getZCTCoords(index, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int[]
    getZCTCoords(int index, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("getZCTCoords");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.getZCTCoords(index, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isFalseColor()
    {
        return isFalseColor(null, false);
    }

    public boolean
    isFalseColor(java.util.Map<String, String> __ctx)
    {
        return isFalseColor(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isFalseColor(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isFalseColor");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isFalseColor(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isGroupFiles()
    {
        return isGroupFiles(null, false);
    }

    public boolean
    isGroupFiles(java.util.Map<String, String> __ctx)
    {
        return isGroupFiles(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isGroupFiles(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isGroupFiles");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isGroupFiles(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isIndexed()
    {
        return isIndexed(null, false);
    }

    public boolean
    isIndexed(java.util.Map<String, String> __ctx)
    {
        return isIndexed(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isIndexed(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isIndexed");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isIndexed(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isInterleaved()
    {
        return isInterleaved(null, false);
    }

    public boolean
    isInterleaved(java.util.Map<String, String> __ctx)
    {
        return isInterleaved(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isInterleaved(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isInterleaved");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isInterleaved(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isInterleavedSubC(int subC)
    {
        return isInterleavedSubC(subC, null, false);
    }

    public boolean
    isInterleavedSubC(int subC, java.util.Map<String, String> __ctx)
    {
        return isInterleavedSubC(subC, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isInterleavedSubC(int subC, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isInterleavedSubC");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isInterleavedSubC(subC, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isLittleEndian()
    {
        return isLittleEndian(null, false);
    }

    public boolean
    isLittleEndian(java.util.Map<String, String> __ctx)
    {
        return isLittleEndian(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isLittleEndian(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isLittleEndian");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isLittleEndian(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isMetadataCollected()
    {
        return isMetadataCollected(null, false);
    }

    public boolean
    isMetadataCollected(java.util.Map<String, String> __ctx)
    {
        return isMetadataCollected(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isMetadataCollected(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isMetadataCollected");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isMetadataCollected(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isMetadataComplete()
    {
        return isMetadataComplete(null, false);
    }

    public boolean
    isMetadataComplete(java.util.Map<String, String> __ctx)
    {
        return isMetadataComplete(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isMetadataComplete(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isMetadataComplete");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isMetadataComplete(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isMetadataFiltered()
    {
        return isMetadataFiltered(null, false);
    }

    public boolean
    isMetadataFiltered(java.util.Map<String, String> __ctx)
    {
        return isMetadataFiltered(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isMetadataFiltered(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isMetadataFiltered");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isMetadataFiltered(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isNormalized()
    {
        return isNormalized(null, false);
    }

    public boolean
    isNormalized(java.util.Map<String, String> __ctx)
    {
        return isNormalized(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isNormalized(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isNormalized");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isNormalized(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isOrderCertain()
    {
        return isOrderCertain(null, false);
    }

    public boolean
    isOrderCertain(java.util.Map<String, String> __ctx)
    {
        return isOrderCertain(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isOrderCertain(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isOrderCertain");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isOrderCertain(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isOriginalMetadataPopulated()
    {
        return isOriginalMetadataPopulated(null, false);
    }

    public boolean
    isOriginalMetadataPopulated(java.util.Map<String, String> __ctx)
    {
        return isOriginalMetadataPopulated(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isOriginalMetadataPopulated(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isOriginalMetadataPopulated");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isOriginalMetadataPopulated(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isRGB()
    {
        return isRGB(null, false);
    }

    public boolean
    isRGB(java.util.Map<String, String> __ctx)
    {
        return isRGB(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isRGB(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isRGB");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isRGB(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public boolean
    isThisType(String name, boolean open)
    {
        return isThisType(name, open, null, false);
    }

    public boolean
    isThisType(String name, boolean open, java.util.Map<String, String> __ctx)
    {
        return isThisType(name, open, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isThisType(String name, boolean open, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("isThisType");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.isThisType(name, open, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public byte[]
    openBytes(int no, int x, int y, int width, int height)
    {
        return openBytes(no, x, y, width, height, null, false);
    }

    public byte[]
    openBytes(int no, int x, int y, int width, int height, java.util.Map<String, String> __ctx)
    {
        return openBytes(no, x, y, width, height, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private byte[]
    openBytes(int no, int x, int y, int width, int height, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("openBytes");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.openBytes(no, x, y, width, height, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public byte[]
    openThumbBytes(int no)
    {
        return openThumbBytes(no, null, false);
    }

    public byte[]
    openThumbBytes(int no, java.util.Map<String, String> __ctx)
    {
        return openThumbBytes(no, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private byte[]
    openThumbBytes(int no, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __checkTwowayOnly("openThumbBytes");
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                return __del.openThumbBytes(no, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setGroupFiles(boolean group)
    {
        setGroupFiles(group, null, false);
    }

    public void
    setGroupFiles(boolean group, java.util.Map<String, String> __ctx)
    {
        setGroupFiles(group, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setGroupFiles(boolean group, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setGroupFiles(group, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setId(String id)
    {
        setId(id, null, false);
    }

    public void
    setId(String id, java.util.Map<String, String> __ctx)
    {
        setId(id, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setId(String id, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setId(id, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setMetadataCollected(boolean collect)
    {
        setMetadataCollected(collect, null, false);
    }

    public void
    setMetadataCollected(boolean collect, java.util.Map<String, String> __ctx)
    {
        setMetadataCollected(collect, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMetadataCollected(boolean collect, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setMetadataCollected(collect, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setMetadataFiltered(boolean filter)
    {
        setMetadataFiltered(filter, null, false);
    }

    public void
    setMetadataFiltered(boolean filter, java.util.Map<String, String> __ctx)
    {
        setMetadataFiltered(filter, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMetadataFiltered(boolean filter, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setMetadataFiltered(filter, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setMetadataStore(IMetadataPrx store)
    {
        setMetadataStore(store, null, false);
    }

    public void
    setMetadataStore(IMetadataPrx store, java.util.Map<String, String> __ctx)
    {
        setMetadataStore(store, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMetadataStore(IMetadataPrx store, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setMetadataStore(store, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setNormalized(boolean normalize)
    {
        setNormalized(normalize, null, false);
    }

    public void
    setNormalized(boolean normalize, java.util.Map<String, String> __ctx)
    {
        setNormalized(normalize, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setNormalized(boolean normalize, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setNormalized(normalize, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setOriginalMetadataPopulated(boolean populate)
    {
        setOriginalMetadataPopulated(populate, null, false);
    }

    public void
    setOriginalMetadataPopulated(boolean populate, java.util.Map<String, String> __ctx)
    {
        setOriginalMetadataPopulated(populate, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOriginalMetadataPopulated(boolean populate, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setOriginalMetadataPopulated(populate, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public void
    setSeries(int no)
    {
        setSeries(no, null, false);
    }

    public void
    setSeries(int no, java.util.Map<String, String> __ctx)
    {
        setSeries(no, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setSeries(int no, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _IFormatReaderDel __del = (_IFormatReaderDel)__delBase;
                __del.setSeries(no, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public static IFormatReaderPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        IFormatReaderPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IFormatReaderPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::IFormatReader"))
                {
                    IFormatReaderPrxHelper __h = new IFormatReaderPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static IFormatReaderPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        IFormatReaderPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IFormatReaderPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::IFormatReader", __ctx))
                {
                    IFormatReaderPrxHelper __h = new IFormatReaderPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static IFormatReaderPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        IFormatReaderPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::IFormatReader"))
                {
                    IFormatReaderPrxHelper __h = new IFormatReaderPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static IFormatReaderPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        IFormatReaderPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::IFormatReader", __ctx))
                {
                    IFormatReaderPrxHelper __h = new IFormatReaderPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static IFormatReaderPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        IFormatReaderPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IFormatReaderPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                IFormatReaderPrxHelper __h = new IFormatReaderPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static IFormatReaderPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        IFormatReaderPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            IFormatReaderPrxHelper __h = new IFormatReaderPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _IFormatReaderDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _IFormatReaderDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, IFormatReaderPrx v)
    {
        __os.writeProxy(v);
    }

    public static IFormatReaderPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            IFormatReaderPrxHelper result = new IFormatReaderPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
