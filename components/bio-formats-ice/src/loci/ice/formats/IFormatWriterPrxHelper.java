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

public final class IFormatWriterPrxHelper extends Ice.ObjectPrxHelperBase implements IFormatWriterPrx
{
    public boolean
    canDoStacks()
    {
        return canDoStacks(null, false);
    }

    public boolean
    canDoStacks(java.util.Map<String, String> __ctx)
    {
        return canDoStacks(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    canDoStacks(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("canDoStacks");
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.canDoStacks(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.close(__ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public String[]
    getCompressionTypes()
    {
        return getCompressionTypes(null, false);
    }

    public String[]
    getCompressionTypes(java.util.Map<String, String> __ctx)
    {
        return getCompressionTypes(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String[]
    getCompressionTypes(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getCompressionTypes");
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.getCompressionTypes(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.getFormat(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public int
    getFramesPerSecond()
    {
        return getFramesPerSecond(null, false);
    }

    public int
    getFramesPerSecond(java.util.Map<String, String> __ctx)
    {
        return getFramesPerSecond(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getFramesPerSecond(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFramesPerSecond");
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.getFramesPerSecond(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public MetadataRetrieve
    getMetadataRetrieve()
    {
        return getMetadataRetrieve(null, false);
    }

    public MetadataRetrieve
    getMetadataRetrieve(java.util.Map<String, String> __ctx)
    {
        return getMetadataRetrieve(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private MetadataRetrieve
    getMetadataRetrieve(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMetadataRetrieve");
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.getMetadataRetrieve(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public int[]
    getPixelTypes()
    {
        return getPixelTypes(null, false);
    }

    public int[]
    getPixelTypes(java.util.Map<String, String> __ctx)
    {
        return getPixelTypes(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int[]
    getPixelTypes(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelTypes");
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.getPixelTypes(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.getSuffixes(__ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public boolean
    isSupportedType(int type)
    {
        return isSupportedType(type, null, false);
    }

    public boolean
    isSupportedType(int type, java.util.Map<String, String> __ctx)
    {
        return isSupportedType(type, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isSupportedType(int type, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("isSupportedType");
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.isSupportedType(type, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public boolean
    isThisType(String name)
    {
        return isThisType(name, null, false);
    }

    public boolean
    isThisType(String name, java.util.Map<String, String> __ctx)
    {
        return isThisType(name, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    isThisType(String name, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                return __del.isThisType(name, __ctx);
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public void
    saveBytes1(byte[] bytes, boolean last)
    {
        saveBytes1(bytes, last, null, false);
    }

    public void
    saveBytes1(byte[] bytes, boolean last, java.util.Map<String, String> __ctx)
    {
        saveBytes1(bytes, last, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    saveBytes1(byte[] bytes, boolean last, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.saveBytes1(bytes, last, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public void
    saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last)
    {
        saveBytes2(bytes, series, lastInSeries, last, null, false);
    }

    public void
    saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last, java.util.Map<String, String> __ctx)
    {
        saveBytes2(bytes, series, lastInSeries, last, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    saveBytes2(byte[] bytes, int series, boolean lastInSeries, boolean last, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.saveBytes2(bytes, series, lastInSeries, last, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public void
    setCompression(String compress)
    {
        setCompression(compress, null, false);
    }

    public void
    setCompression(String compress, java.util.Map<String, String> __ctx)
    {
        setCompression(compress, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setCompression(String compress, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.setCompression(compress, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public void
    setFramesPerSecond(int rate)
    {
        setFramesPerSecond(rate, null, false);
    }

    public void
    setFramesPerSecond(int rate, java.util.Map<String, String> __ctx)
    {
        setFramesPerSecond(rate, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFramesPerSecond(int rate, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.setFramesPerSecond(rate, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.setId(id, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public void
    setMetadataRetrieve(MetadataRetrievePrx r)
    {
        setMetadataRetrieve(r, null, false);
    }

    public void
    setMetadataRetrieve(MetadataRetrievePrx r, java.util.Map<String, String> __ctx)
    {
        setMetadataRetrieve(r, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMetadataRetrieve(MetadataRetrievePrx r, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.setMetadataRetrieve(r, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public void
    setStoreAsRetrieve(MetadataStorePrx store)
    {
        setStoreAsRetrieve(store, null, false);
    }

    public void
    setStoreAsRetrieve(MetadataStorePrx store, java.util.Map<String, String> __ctx)
    {
        setStoreAsRetrieve(store, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStoreAsRetrieve(MetadataStorePrx store, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __delBase = __getDelegate();
                _IFormatWriterDel __del = (_IFormatWriterDel)__delBase;
                __del.setStoreAsRetrieve(store, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, __cnt);
            }
        }
    }

    public static IFormatWriterPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        IFormatWriterPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IFormatWriterPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::IFormatWriter"))
                {
                    IFormatWriterPrxHelper __h = new IFormatWriterPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static IFormatWriterPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        IFormatWriterPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (IFormatWriterPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::IFormatWriter", __ctx))
                {
                    IFormatWriterPrxHelper __h = new IFormatWriterPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static IFormatWriterPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        IFormatWriterPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::IFormatWriter"))
                {
                    IFormatWriterPrxHelper __h = new IFormatWriterPrxHelper();
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

    public static IFormatWriterPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        IFormatWriterPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::IFormatWriter", __ctx))
                {
                    IFormatWriterPrxHelper __h = new IFormatWriterPrxHelper();
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

    public static IFormatWriterPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        IFormatWriterPrx __d = null;
        if(__obj != null)
        {
            IFormatWriterPrxHelper __h = new IFormatWriterPrxHelper();
            __h.__copyFrom(__obj);
            __d = __h;
        }
        return __d;
    }

    public static IFormatWriterPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        IFormatWriterPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            IFormatWriterPrxHelper __h = new IFormatWriterPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _IFormatWriterDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _IFormatWriterDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, IFormatWriterPrx v)
    {
        __os.writeProxy(v);
    }

    public static IFormatWriterPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            IFormatWriterPrxHelper result = new IFormatWriterPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
