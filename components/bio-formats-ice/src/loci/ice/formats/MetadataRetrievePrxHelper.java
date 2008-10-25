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

public final class MetadataRetrievePrxHelper extends Ice.ObjectPrxHelperBase implements MetadataRetrievePrx
{
    public String
    getArcType(int instrumentIndex, int lightSourceIndex)
    {
        return getArcType(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getArcType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getArcType(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getArcType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getArcType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getArcType(instrumentIndex, lightSourceIndex, __ctx);
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
    getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        return getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex, null, false);
    }

    public String
    getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
    {
        return getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getChannelComponentColorDomain");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
    getChannelComponentCount(int imageIndex, int logicalChannelIndex)
    {
        return getChannelComponentCount(imageIndex, logicalChannelIndex, null, false);
    }

    public int
    getChannelComponentCount(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getChannelComponentCount(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getChannelComponentCount(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getChannelComponentCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getChannelComponentCount(imageIndex, logicalChannelIndex, __ctx);
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
    getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        return getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex, null, false);
    }

    public int
    getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
    {
        return getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getChannelComponentIndex");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
    getDetectorCount(int instrumentIndex)
    {
        return getDetectorCount(instrumentIndex, null, false);
    }

    public int
    getDetectorCount(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorCount(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDetectorCount(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorCount(instrumentIndex, __ctx);
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

    public float
    getDetectorGain(int instrumentIndex, int detectorIndex)
    {
        return getDetectorGain(instrumentIndex, detectorIndex, null, false);
    }

    public float
    getDetectorGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorGain(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorGain");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorGain(instrumentIndex, detectorIndex, __ctx);
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
    getDetectorID(int instrumentIndex, int detectorIndex)
    {
        return getDetectorID(instrumentIndex, detectorIndex, null, false);
    }

    public String
    getDetectorID(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorID(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDetectorID(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorID(instrumentIndex, detectorIndex, __ctx);
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
    getDetectorManufacturer(int instrumentIndex, int detectorIndex)
    {
        return getDetectorManufacturer(instrumentIndex, detectorIndex, null, false);
    }

    public String
    getDetectorManufacturer(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorManufacturer(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDetectorManufacturer(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorManufacturer");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorManufacturer(instrumentIndex, detectorIndex, __ctx);
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
    getDetectorModel(int instrumentIndex, int detectorIndex)
    {
        return getDetectorModel(instrumentIndex, detectorIndex, null, false);
    }

    public String
    getDetectorModel(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorModel(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDetectorModel(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorModel");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorModel(instrumentIndex, detectorIndex, __ctx);
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

    public float
    getDetectorOffset(int instrumentIndex, int detectorIndex)
    {
        return getDetectorOffset(instrumentIndex, detectorIndex, null, false);
    }

    public float
    getDetectorOffset(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorOffset(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorOffset(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorOffset");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorOffset(instrumentIndex, detectorIndex, __ctx);
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
    getDetectorSerialNumber(int instrumentIndex, int detectorIndex)
    {
        return getDetectorSerialNumber(instrumentIndex, detectorIndex, null, false);
    }

    public String
    getDetectorSerialNumber(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorSerialNumber(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDetectorSerialNumber(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorSerialNumber");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSerialNumber(instrumentIndex, detectorIndex, __ctx);
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
    getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsDetector(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorSettingsDetector(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorSettingsDetector");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsDetector(imageIndex, logicalChannelIndex, __ctx);
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

    public float
    getDetectorSettingsGain(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsGain(imageIndex, logicalChannelIndex, null, false);
    }

    public float
    getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorSettingsGain(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorSettingsGain(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorSettingsGain");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsGain(imageIndex, logicalChannelIndex, __ctx);
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

    public float
    getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsOffset(imageIndex, logicalChannelIndex, null, false);
    }

    public float
    getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorSettingsOffset(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorSettingsOffset");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsOffset(imageIndex, logicalChannelIndex, __ctx);
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
    getDetectorType(int instrumentIndex, int detectorIndex)
    {
        return getDetectorType(instrumentIndex, detectorIndex, null, false);
    }

    public String
    getDetectorType(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorType(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDetectorType(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorType(instrumentIndex, detectorIndex, __ctx);
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

    public float
    getDetectorVoltage(int instrumentIndex, int detectorIndex)
    {
        return getDetectorVoltage(instrumentIndex, detectorIndex, null, false);
    }

    public float
    getDetectorVoltage(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorVoltage(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorVoltage(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorVoltage");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorVoltage(instrumentIndex, detectorIndex, __ctx);
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

    public float
    getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex)
    {
        return getDimensionsPhysicalSizeX(imageIndex, pixelsIndex, null, false);
    }

    public float
    getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getDimensionsPhysicalSizeX(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDimensionsPhysicalSizeX");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex, __ctx);
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

    public float
    getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex)
    {
        return getDimensionsPhysicalSizeY(imageIndex, pixelsIndex, null, false);
    }

    public float
    getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getDimensionsPhysicalSizeY(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDimensionsPhysicalSizeY");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex, __ctx);
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

    public float
    getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex)
    {
        return getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex, null, false);
    }

    public float
    getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDimensionsPhysicalSizeZ");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex, __ctx);
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

    public float
    getDimensionsTimeIncrement(int imageIndex, int pixelsIndex)
    {
        return getDimensionsTimeIncrement(imageIndex, pixelsIndex, null, false);
    }

    public float
    getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getDimensionsTimeIncrement(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDimensionsTimeIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDimensionsTimeIncrement");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsTimeIncrement(imageIndex, pixelsIndex, __ctx);
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
    getDimensionsWaveIncrement(int imageIndex, int pixelsIndex)
    {
        return getDimensionsWaveIncrement(imageIndex, pixelsIndex, null, false);
    }

    public int
    getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getDimensionsWaveIncrement(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDimensionsWaveIncrement(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDimensionsWaveIncrement");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsWaveIncrement(imageIndex, pixelsIndex, __ctx);
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
    getDimensionsWaveStart(int imageIndex, int pixelsIndex)
    {
        return getDimensionsWaveStart(imageIndex, pixelsIndex, null, false);
    }

    public int
    getDimensionsWaveStart(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getDimensionsWaveStart(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDimensionsWaveStart(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDimensionsWaveStart");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsWaveStart(imageIndex, pixelsIndex, __ctx);
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
    getDisplayOptionsID(int imageIndex)
    {
        return getDisplayOptionsID(imageIndex, null, false);
    }

    public String
    getDisplayOptionsID(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDisplayOptionsID(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDisplayOptionsID(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDisplayOptionsID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsID(imageIndex, __ctx);
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
    getDisplayOptionsProjectionZStart(int imageIndex)
    {
        return getDisplayOptionsProjectionZStart(imageIndex, null, false);
    }

    public int
    getDisplayOptionsProjectionZStart(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDisplayOptionsProjectionZStart(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDisplayOptionsProjectionZStart(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDisplayOptionsProjectionZStart");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsProjectionZStart(imageIndex, __ctx);
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
    getDisplayOptionsProjectionZStop(int imageIndex)
    {
        return getDisplayOptionsProjectionZStop(imageIndex, null, false);
    }

    public int
    getDisplayOptionsProjectionZStop(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDisplayOptionsProjectionZStop(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDisplayOptionsProjectionZStop(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDisplayOptionsProjectionZStop");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsProjectionZStop(imageIndex, __ctx);
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
    getDisplayOptionsTimeTStart(int imageIndex)
    {
        return getDisplayOptionsTimeTStart(imageIndex, null, false);
    }

    public int
    getDisplayOptionsTimeTStart(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDisplayOptionsTimeTStart(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDisplayOptionsTimeTStart(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDisplayOptionsTimeTStart");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsTimeTStart(imageIndex, __ctx);
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
    getDisplayOptionsTimeTStop(int imageIndex)
    {
        return getDisplayOptionsTimeTStop(imageIndex, null, false);
    }

    public int
    getDisplayOptionsTimeTStop(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDisplayOptionsTimeTStop(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDisplayOptionsTimeTStop(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDisplayOptionsTimeTStop");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsTimeTStop(imageIndex, __ctx);
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

    public float
    getDisplayOptionsZoom(int imageIndex)
    {
        return getDisplayOptionsZoom(imageIndex, null, false);
    }

    public float
    getDisplayOptionsZoom(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDisplayOptionsZoom(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDisplayOptionsZoom(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDisplayOptionsZoom");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsZoom(imageIndex, __ctx);
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
    getExperimentCount()
    {
        return getExperimentCount(null, false);
    }

    public int
    getExperimentCount(java.util.Map<String, String> __ctx)
    {
        return getExperimentCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getExperimentCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimentCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentCount(__ctx);
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
    getExperimentDescription(int experimentIndex)
    {
        return getExperimentDescription(experimentIndex, null, false);
    }

    public String
    getExperimentDescription(int experimentIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimentDescription(experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimentDescription(int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimentDescription");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentDescription(experimentIndex, __ctx);
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
    getExperimentID(int experimentIndex)
    {
        return getExperimentID(experimentIndex, null, false);
    }

    public String
    getExperimentID(int experimentIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimentID(experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimentID(int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimentID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentID(experimentIndex, __ctx);
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
    getExperimentType(int experimentIndex)
    {
        return getExperimentType(experimentIndex, null, false);
    }

    public String
    getExperimentType(int experimentIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimentType(experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimentType(int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimentType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentType(experimentIndex, __ctx);
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
    getExperimenterCount()
    {
        return getExperimenterCount(null, false);
    }

    public int
    getExperimenterCount(java.util.Map<String, String> __ctx)
    {
        return getExperimenterCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getExperimenterCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterCount(__ctx);
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
    getExperimenterEmail(int experimenterIndex)
    {
        return getExperimenterEmail(experimenterIndex, null, false);
    }

    public String
    getExperimenterEmail(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterEmail(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimenterEmail(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterEmail");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterEmail(experimenterIndex, __ctx);
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
    getExperimenterFirstName(int experimenterIndex)
    {
        return getExperimenterFirstName(experimenterIndex, null, false);
    }

    public String
    getExperimenterFirstName(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterFirstName(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimenterFirstName(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterFirstName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterFirstName(experimenterIndex, __ctx);
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
    getExperimenterID(int experimenterIndex)
    {
        return getExperimenterID(experimenterIndex, null, false);
    }

    public String
    getExperimenterID(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterID(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimenterID(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterID(experimenterIndex, __ctx);
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
    getExperimenterInstitution(int experimenterIndex)
    {
        return getExperimenterInstitution(experimenterIndex, null, false);
    }

    public String
    getExperimenterInstitution(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterInstitution(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimenterInstitution(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterInstitution");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterInstitution(experimenterIndex, __ctx);
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
    getExperimenterLastName(int experimenterIndex)
    {
        return getExperimenterLastName(experimenterIndex, null, false);
    }

    public String
    getExperimenterLastName(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterLastName(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimenterLastName(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterLastName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterLastName(experimenterIndex, __ctx);
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
    getExperimenterMembershipCount(int experimenterIndex)
    {
        return getExperimenterMembershipCount(experimenterIndex, null, false);
    }

    public int
    getExperimenterMembershipCount(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterMembershipCount(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getExperimenterMembershipCount(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterMembershipCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterMembershipCount(experimenterIndex, __ctx);
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
    getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex)
    {
        return getExperimenterMembershipGroup(experimenterIndex, groupRefIndex, null, false);
    }

    public String
    getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterMembershipGroup(experimenterIndex, groupRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterMembershipGroup");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex, __ctx);
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
    getFilamentType(int instrumentIndex, int lightSourceIndex)
    {
        return getFilamentType(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getFilamentType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getFilamentType(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilamentType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilamentType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilamentType(instrumentIndex, lightSourceIndex, __ctx);
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
    getGroupRefCount(int experimenterIndex)
    {
        return getGroupRefCount(experimenterIndex, null, false);
    }

    public int
    getGroupRefCount(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getGroupRefCount(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getGroupRefCount(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getGroupRefCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getGroupRefCount(experimenterIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageCount(__ctx);
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
    getImageCreationDate(int imageIndex)
    {
        return getImageCreationDate(imageIndex, null, false);
    }

    public String
    getImageCreationDate(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageCreationDate(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageCreationDate(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageCreationDate");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageCreationDate(imageIndex, __ctx);
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
    getImageDefaultPixels(int imageIndex)
    {
        return getImageDefaultPixels(imageIndex, null, false);
    }

    public String
    getImageDefaultPixels(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageDefaultPixels(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageDefaultPixels(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageDefaultPixels");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageDefaultPixels(imageIndex, __ctx);
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
    getImageDescription(int imageIndex)
    {
        return getImageDescription(imageIndex, null, false);
    }

    public String
    getImageDescription(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageDescription(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageDescription(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageDescription");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageDescription(imageIndex, __ctx);
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
    getImageID(int imageIndex)
    {
        return getImageID(imageIndex, null, false);
    }

    public String
    getImageID(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageID(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageID(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageID(imageIndex, __ctx);
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
    getImageInstrumentRef(int imageIndex)
    {
        return getImageInstrumentRef(imageIndex, null, false);
    }

    public String
    getImageInstrumentRef(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageInstrumentRef(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageInstrumentRef(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageInstrumentRef");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageInstrumentRef(imageIndex, __ctx);
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
    getImageName(int imageIndex)
    {
        return getImageName(imageIndex, null, false);
    }

    public String
    getImageName(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageName(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageName(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageName(imageIndex, __ctx);
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

    public float
    getImagingEnvironmentAirPressure(int imageIndex)
    {
        return getImagingEnvironmentAirPressure(imageIndex, null, false);
    }

    public float
    getImagingEnvironmentAirPressure(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImagingEnvironmentAirPressure(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getImagingEnvironmentAirPressure(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImagingEnvironmentAirPressure");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentAirPressure(imageIndex, __ctx);
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

    public float
    getImagingEnvironmentCO2Percent(int imageIndex)
    {
        return getImagingEnvironmentCO2Percent(imageIndex, null, false);
    }

    public float
    getImagingEnvironmentCO2Percent(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImagingEnvironmentCO2Percent(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getImagingEnvironmentCO2Percent(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImagingEnvironmentCO2Percent");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentCO2Percent(imageIndex, __ctx);
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

    public float
    getImagingEnvironmentHumidity(int imageIndex)
    {
        return getImagingEnvironmentHumidity(imageIndex, null, false);
    }

    public float
    getImagingEnvironmentHumidity(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImagingEnvironmentHumidity(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getImagingEnvironmentHumidity(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImagingEnvironmentHumidity");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentHumidity(imageIndex, __ctx);
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

    public float
    getImagingEnvironmentTemperature(int imageIndex)
    {
        return getImagingEnvironmentTemperature(imageIndex, null, false);
    }

    public float
    getImagingEnvironmentTemperature(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImagingEnvironmentTemperature(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getImagingEnvironmentTemperature(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImagingEnvironmentTemperature");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentTemperature(imageIndex, __ctx);
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
    getInstrumentCount()
    {
        return getInstrumentCount(null, false);
    }

    public int
    getInstrumentCount(java.util.Map<String, String> __ctx)
    {
        return getInstrumentCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getInstrumentCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getInstrumentCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getInstrumentCount(__ctx);
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
    getInstrumentID(int instrumentIndex)
    {
        return getInstrumentID(instrumentIndex, null, false);
    }

    public String
    getInstrumentID(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getInstrumentID(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getInstrumentID(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getInstrumentID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getInstrumentID(instrumentIndex, __ctx);
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
    getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex, null, false);
    }

    public int
    getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserFrequencyMultiplication");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex, __ctx);
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
    getLaserLaserMedium(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserLaserMedium(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserLaserMedium(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLaserLaserMedium(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserLaserMedium");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserLaserMedium(instrumentIndex, lightSourceIndex, __ctx);
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
    getLaserPulse(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserPulse(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getLaserPulse(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserPulse(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLaserPulse(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserPulse");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserPulse(instrumentIndex, lightSourceIndex, __ctx);
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
    getLaserTuneable(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserTuneable(instrumentIndex, lightSourceIndex, null, false);
    }

    public boolean
    getLaserTuneable(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserTuneable(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getLaserTuneable(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserTuneable");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserTuneable(instrumentIndex, lightSourceIndex, __ctx);
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
    getLaserType(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserType(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getLaserType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserType(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLaserType(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserType(instrumentIndex, lightSourceIndex, __ctx);
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
    getLaserWavelength(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserWavelength(instrumentIndex, lightSourceIndex, null, false);
    }

    public int
    getLaserWavelength(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserWavelength(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLaserWavelength(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserWavelength");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserWavelength(instrumentIndex, lightSourceIndex, __ctx);
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
    getLightSourceCount(int instrumentIndex)
    {
        return getLightSourceCount(instrumentIndex, null, false);
    }

    public int
    getLightSourceCount(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceCount(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLightSourceCount(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceCount(instrumentIndex, __ctx);
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
    getLightSourceID(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceID(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getLightSourceID(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceID(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLightSourceID(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceID(instrumentIndex, lightSourceIndex, __ctx);
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
    getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceManufacturer(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceManufacturer(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceManufacturer");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceManufacturer(instrumentIndex, lightSourceIndex, __ctx);
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
    getLightSourceModel(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceModel(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getLightSourceModel(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceModel(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLightSourceModel(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceModel");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceModel(instrumentIndex, lightSourceIndex, __ctx);
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

    public float
    getLightSourcePower(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourcePower(instrumentIndex, lightSourceIndex, null, false);
    }

    public float
    getLightSourcePower(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourcePower(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getLightSourcePower(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourcePower");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourcePower(instrumentIndex, lightSourceIndex, __ctx);
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
    getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex)
    {
        return getLightSourceSerialNumber(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceSerialNumber(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceSerialNumber");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex, __ctx);
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

    public float
    getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex)
    {
        return getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex, null, false);
    }

    public float
    getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceSettingsAttenuation");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex, __ctx);
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
    getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex)
    {
        return getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceSettingsLightSource");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex, __ctx);
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
    getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex)
    {
        return getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex, null, false);
    }

    public int
    getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceSettingsWavelength");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelContrastMethod");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelCount(int imageIndex)
    {
        return getLogicalChannelCount(imageIndex, null, false);
    }

    public int
    getLogicalChannelCount(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelCount(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLogicalChannelCount(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelCount(imageIndex, __ctx);
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
    getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelEmWave(imageIndex, logicalChannelIndex, null, false);
    }

    public int
    getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelEmWave(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelEmWave");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelEmWave(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelExWave(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelExWave(imageIndex, logicalChannelIndex, null, false);
    }

    public int
    getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelExWave(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLogicalChannelExWave(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelExWave");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelExWave(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelFluor(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelFluor(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelFluor(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelFluor(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelFluor");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelFluor(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelID(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelID(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelID(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelID(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelID(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelID(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelIlluminationType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelMode(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelMode(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelMode(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelMode(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelMode(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelMode");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelMode(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelName(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelName(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelName(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelName(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelName(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelName(imageIndex, logicalChannelIndex, __ctx);
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

    public float
    getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelNdFilter(imageIndex, logicalChannelIndex, null, false);
    }

    public float
    getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelNdFilter(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelNdFilter");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelOTF(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelOTF(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelOTF(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelOTF(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelOTF");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelOTF(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelPhotometricInterpretation");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex, __ctx);
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

    public float
    getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex, null, false);
    }

    public float
    getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelPinholeSize");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex, null, false);
    }

    public int
    getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelPockelCellSetting");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex, null, false);
    }

    public int
    getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelSamplesPerPixel");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex, __ctx);
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
    getOMEXML()
    {
        return getOMEXML(null, false);
    }

    public String
    getOMEXML(java.util.Map<String, String> __ctx)
    {
        return getOMEXML(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getOMEXML(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOMEXML");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOMEXML(__ctx);
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
    getOTFCount(int instrumentIndex)
    {
        return getOTFCount(instrumentIndex, null, false);
    }

    public int
    getOTFCount(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFCount(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getOTFCount(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFCount(instrumentIndex, __ctx);
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
    getOTFID(int instrumentIndex, int otfIndex)
    {
        return getOTFID(instrumentIndex, otfIndex, null, false);
    }

    public String
    getOTFID(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFID(instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getOTFID(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFID(instrumentIndex, otfIndex, __ctx);
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
    getOTFObjective(int instrumentIndex, int otfIndex)
    {
        return getOTFObjective(instrumentIndex, otfIndex, null, false);
    }

    public String
    getOTFObjective(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFObjective(instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getOTFObjective(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFObjective");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFObjective(instrumentIndex, otfIndex, __ctx);
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
    getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex)
    {
        return getOTFOpticalAxisAveraged(instrumentIndex, otfIndex, null, false);
    }

    public boolean
    getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFOpticalAxisAveraged(instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFOpticalAxisAveraged");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex, __ctx);
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
    getOTFPixelType(int instrumentIndex, int otfIndex)
    {
        return getOTFPixelType(instrumentIndex, otfIndex, null, false);
    }

    public String
    getOTFPixelType(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFPixelType(instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getOTFPixelType(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFPixelType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFPixelType(instrumentIndex, otfIndex, __ctx);
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
    getOTFSizeX(int instrumentIndex, int otfIndex)
    {
        return getOTFSizeX(instrumentIndex, otfIndex, null, false);
    }

    public int
    getOTFSizeX(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFSizeX(instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getOTFSizeX(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFSizeX");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFSizeX(instrumentIndex, otfIndex, __ctx);
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
    getOTFSizeY(int instrumentIndex, int otfIndex)
    {
        return getOTFSizeY(instrumentIndex, otfIndex, null, false);
    }

    public int
    getOTFSizeY(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFSizeY(instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getOTFSizeY(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFSizeY");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFSizeY(instrumentIndex, otfIndex, __ctx);
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

    public float
    getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex, null, false);
    }

    public float
    getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveCalibratedMagnification");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveCorrection(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveCorrection(instrumentIndex, objectiveIndex, null, false);
    }

    public String
    getObjectiveCorrection(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveCorrection(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveCorrection(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveCorrection");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveCorrection(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveCount(int instrumentIndex)
    {
        return getObjectiveCount(instrumentIndex, null, false);
    }

    public int
    getObjectiveCount(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveCount(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getObjectiveCount(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveCount(instrumentIndex, __ctx);
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
    getObjectiveID(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveID(instrumentIndex, objectiveIndex, null, false);
    }

    public String
    getObjectiveID(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveID(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveID(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveID(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveImmersion(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveImmersion(instrumentIndex, objectiveIndex, null, false);
    }

    public String
    getObjectiveImmersion(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveImmersion(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveImmersion(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveImmersion");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveImmersion(instrumentIndex, objectiveIndex, __ctx);
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

    public float
    getObjectiveLensNA(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveLensNA(instrumentIndex, objectiveIndex, null, false);
    }

    public float
    getObjectiveLensNA(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveLensNA(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getObjectiveLensNA(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveLensNA");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveLensNA(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveManufacturer(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveManufacturer(instrumentIndex, objectiveIndex, null, false);
    }

    public String
    getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveManufacturer(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveManufacturer(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveManufacturer");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveManufacturer(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveModel(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveModel(instrumentIndex, objectiveIndex, null, false);
    }

    public String
    getObjectiveModel(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveModel(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveModel(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveModel");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveModel(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveNominalMagnification(instrumentIndex, objectiveIndex, null, false);
    }

    public int
    getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveNominalMagnification(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveNominalMagnification");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveSerialNumber(instrumentIndex, objectiveIndex, null, false);
    }

    public String
    getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveSerialNumber(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveSerialNumber");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveSerialNumber(instrumentIndex, objectiveIndex, __ctx);
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

    public float
    getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, null, false);
    }

    public float
    getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveWorkingDistance");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, __ctx);
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
    getPixelsBigEndian(int imageIndex, int pixelsIndex)
    {
        return getPixelsBigEndian(imageIndex, pixelsIndex, null, false);
    }

    public boolean
    getPixelsBigEndian(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsBigEndian(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getPixelsBigEndian(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsBigEndian");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsBigEndian(imageIndex, pixelsIndex, __ctx);
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
    getPixelsCount(int imageIndex)
    {
        return getPixelsCount(imageIndex, null, false);
    }

    public int
    getPixelsCount(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsCount(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPixelsCount(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsCount(imageIndex, __ctx);
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
    getPixelsDimensionOrder(int imageIndex, int pixelsIndex)
    {
        return getPixelsDimensionOrder(imageIndex, pixelsIndex, null, false);
    }

    public String
    getPixelsDimensionOrder(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsDimensionOrder(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPixelsDimensionOrder(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsDimensionOrder");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsDimensionOrder(imageIndex, pixelsIndex, __ctx);
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
    getPixelsID(int imageIndex, int pixelsIndex)
    {
        return getPixelsID(imageIndex, pixelsIndex, null, false);
    }

    public String
    getPixelsID(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsID(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPixelsID(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsID(imageIndex, pixelsIndex, __ctx);
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
    getPixelsPixelType(int imageIndex, int pixelsIndex)
    {
        return getPixelsPixelType(imageIndex, pixelsIndex, null, false);
    }

    public String
    getPixelsPixelType(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsPixelType(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPixelsPixelType(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsPixelType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsPixelType(imageIndex, pixelsIndex, __ctx);
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
    getPixelsSizeC(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeC(imageIndex, pixelsIndex, null, false);
    }

    public int
    getPixelsSizeC(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsSizeC(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPixelsSizeC(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsSizeC");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeC(imageIndex, pixelsIndex, __ctx);
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
    getPixelsSizeT(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeT(imageIndex, pixelsIndex, null, false);
    }

    public int
    getPixelsSizeT(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsSizeT(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPixelsSizeT(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsSizeT");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeT(imageIndex, pixelsIndex, __ctx);
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
    getPixelsSizeX(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeX(imageIndex, pixelsIndex, null, false);
    }

    public int
    getPixelsSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsSizeX(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPixelsSizeX(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsSizeX");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeX(imageIndex, pixelsIndex, __ctx);
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
    getPixelsSizeY(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeY(imageIndex, pixelsIndex, null, false);
    }

    public int
    getPixelsSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsSizeY(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPixelsSizeY(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsSizeY");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeY(imageIndex, pixelsIndex, __ctx);
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
    getPixelsSizeZ(int imageIndex, int pixelsIndex)
    {
        return getPixelsSizeZ(imageIndex, pixelsIndex, null, false);
    }

    public int
    getPixelsSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPixelsSizeZ(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPixelsSizeZ(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPixelsSizeZ");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeZ(imageIndex, pixelsIndex, __ctx);
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
    getPlaneCount(int imageIndex, int pixelsIndex)
    {
        return getPlaneCount(imageIndex, pixelsIndex, null, false);
    }

    public int
    getPlaneCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneCount(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPlaneCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneCount(imageIndex, pixelsIndex, __ctx);
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
    getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTheC(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public int
    getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneTheC(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneTheC");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTheC(imageIndex, pixelsIndex, planeIndex, __ctx);
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
    getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTheT(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public int
    getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneTheT(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneTheT");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTheT(imageIndex, pixelsIndex, planeIndex, __ctx);
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
    getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTheZ(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public int
    getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneTheZ(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneTheZ");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex, __ctx);
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

    public float
    getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public float
    getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneTimingDeltaT");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex, __ctx);
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

    public float
    getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public float
    getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneTimingExposureTime");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex, __ctx);
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
    getPlateCount()
    {
        return getPlateCount(null, false);
    }

    public int
    getPlateCount(java.util.Map<String, String> __ctx)
    {
        return getPlateCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPlateCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateCount(__ctx);
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
    getPlateDescription(int plateIndex)
    {
        return getPlateDescription(plateIndex, null, false);
    }

    public String
    getPlateDescription(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateDescription(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateDescription(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateDescription");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateDescription(plateIndex, __ctx);
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
    getPlateExternalIdentifier(int plateIndex)
    {
        return getPlateExternalIdentifier(plateIndex, null, false);
    }

    public String
    getPlateExternalIdentifier(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateExternalIdentifier(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateExternalIdentifier(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateExternalIdentifier");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateExternalIdentifier(plateIndex, __ctx);
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
    getPlateID(int plateIndex)
    {
        return getPlateID(plateIndex, null, false);
    }

    public String
    getPlateID(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateID(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateID(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateID(plateIndex, __ctx);
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
    getPlateName(int plateIndex)
    {
        return getPlateName(plateIndex, null, false);
    }

    public String
    getPlateName(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateName(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateName(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateName(plateIndex, __ctx);
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
    getPlateRefCount(int screenIndex)
    {
        return getPlateRefCount(screenIndex, null, false);
    }

    public int
    getPlateRefCount(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateRefCount(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPlateRefCount(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateRefCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateRefCount(screenIndex, __ctx);
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
    getPlateRefID(int screenIndex, int plateRefIndex)
    {
        return getPlateRefID(screenIndex, plateRefIndex, null, false);
    }

    public String
    getPlateRefID(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateRefID(screenIndex, plateRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateRefID(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateRefID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateRefID(screenIndex, plateRefIndex, __ctx);
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
    getPlateStatus(int plateIndex)
    {
        return getPlateStatus(plateIndex, null, false);
    }

    public String
    getPlateStatus(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateStatus(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateStatus(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateStatus");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateStatus(plateIndex, __ctx);
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
    getROICount(int imageIndex)
    {
        return getROICount(imageIndex, null, false);
    }

    public int
    getROICount(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getROICount(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROICount(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROICount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROICount(imageIndex, __ctx);
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
    getROIID(int imageIndex, int roiIndex)
    {
        return getROIID(imageIndex, roiIndex, null, false);
    }

    public String
    getROIID(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIID(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getROIID(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIID(imageIndex, roiIndex, __ctx);
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
    getROIT0(int imageIndex, int roiIndex)
    {
        return getROIT0(imageIndex, roiIndex, null, false);
    }

    public int
    getROIT0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIT0(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIT0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIT0");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIT0(imageIndex, roiIndex, __ctx);
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
    getROIT1(int imageIndex, int roiIndex)
    {
        return getROIT1(imageIndex, roiIndex, null, false);
    }

    public int
    getROIT1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIT1(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIT1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIT1");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIT1(imageIndex, roiIndex, __ctx);
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
    getROIX0(int imageIndex, int roiIndex)
    {
        return getROIX0(imageIndex, roiIndex, null, false);
    }

    public int
    getROIX0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIX0(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIX0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIX0");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIX0(imageIndex, roiIndex, __ctx);
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
    getROIX1(int imageIndex, int roiIndex)
    {
        return getROIX1(imageIndex, roiIndex, null, false);
    }

    public int
    getROIX1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIX1(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIX1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIX1");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIX1(imageIndex, roiIndex, __ctx);
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
    getROIY0(int imageIndex, int roiIndex)
    {
        return getROIY0(imageIndex, roiIndex, null, false);
    }

    public int
    getROIY0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIY0(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIY0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIY0");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIY0(imageIndex, roiIndex, __ctx);
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
    getROIY1(int imageIndex, int roiIndex)
    {
        return getROIY1(imageIndex, roiIndex, null, false);
    }

    public int
    getROIY1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIY1(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIY1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIY1");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIY1(imageIndex, roiIndex, __ctx);
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
    getROIZ0(int imageIndex, int roiIndex)
    {
        return getROIZ0(imageIndex, roiIndex, null, false);
    }

    public int
    getROIZ0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIZ0(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIZ0(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIZ0");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIZ0(imageIndex, roiIndex, __ctx);
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
    getROIZ1(int imageIndex, int roiIndex)
    {
        return getROIZ1(imageIndex, roiIndex, null, false);
    }

    public int
    getROIZ1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getROIZ1(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIZ1(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIZ1");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIZ1(imageIndex, roiIndex, __ctx);
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
    getReagentCount(int screenIndex)
    {
        return getReagentCount(screenIndex, null, false);
    }

    public int
    getReagentCount(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getReagentCount(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getReagentCount(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getReagentCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentCount(screenIndex, __ctx);
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
    getReagentDescription(int screenIndex, int reagentIndex)
    {
        return getReagentDescription(screenIndex, reagentIndex, null, false);
    }

    public String
    getReagentDescription(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        return getReagentDescription(screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getReagentDescription(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getReagentDescription");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentDescription(screenIndex, reagentIndex, __ctx);
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
    getReagentID(int screenIndex, int reagentIndex)
    {
        return getReagentID(screenIndex, reagentIndex, null, false);
    }

    public String
    getReagentID(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        return getReagentID(screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getReagentID(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getReagentID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentID(screenIndex, reagentIndex, __ctx);
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
    getReagentName(int screenIndex, int reagentIndex)
    {
        return getReagentName(screenIndex, reagentIndex, null, false);
    }

    public String
    getReagentName(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        return getReagentName(screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getReagentName(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getReagentName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentName(screenIndex, reagentIndex, __ctx);
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
    getReagentReagentIdentifier(int screenIndex, int reagentIndex)
    {
        return getReagentReagentIdentifier(screenIndex, reagentIndex, null, false);
    }

    public String
    getReagentReagentIdentifier(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        return getReagentReagentIdentifier(screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getReagentReagentIdentifier(int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getReagentReagentIdentifier");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentReagentIdentifier(screenIndex, reagentIndex, __ctx);
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
    getScreenAcquisitionCount(int screenIndex)
    {
        return getScreenAcquisitionCount(screenIndex, null, false);
    }

    public int
    getScreenAcquisitionCount(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenAcquisitionCount(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getScreenAcquisitionCount(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenAcquisitionCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionCount(screenIndex, __ctx);
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
    getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex)
    {
        return getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex, null, false);
    }

    public String
    getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenAcquisitionEndTime");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex, __ctx);
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
    getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex)
    {
        return getScreenAcquisitionID(screenIndex, screenAcquisitionIndex, null, false);
    }

    public String
    getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenAcquisitionID(screenIndex, screenAcquisitionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenAcquisitionID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex, __ctx);
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
    getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex)
    {
        return getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex, null, false);
    }

    public String
    getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenAcquisitionStartTime");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex, __ctx);
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
    getScreenCount()
    {
        return getScreenCount(null, false);
    }

    public int
    getScreenCount(java.util.Map<String, String> __ctx)
    {
        return getScreenCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getScreenCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenCount(__ctx);
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
    getScreenID(int screenIndex)
    {
        return getScreenID(screenIndex, null, false);
    }

    public String
    getScreenID(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenID(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenID(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenID(screenIndex, __ctx);
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
    getScreenName(int screenIndex)
    {
        return getScreenName(screenIndex, null, false);
    }

    public String
    getScreenName(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenName(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenName(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenName(screenIndex, __ctx);
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
    getScreenProtocolDescription(int screenIndex)
    {
        return getScreenProtocolDescription(screenIndex, null, false);
    }

    public String
    getScreenProtocolDescription(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenProtocolDescription(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenProtocolDescription(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenProtocolDescription");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenProtocolDescription(screenIndex, __ctx);
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
    getScreenProtocolIdentifier(int screenIndex)
    {
        return getScreenProtocolIdentifier(screenIndex, null, false);
    }

    public String
    getScreenProtocolIdentifier(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenProtocolIdentifier(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenProtocolIdentifier(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenProtocolIdentifier");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenProtocolIdentifier(screenIndex, __ctx);
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
    getScreenReagentSetDescription(int screenIndex)
    {
        return getScreenReagentSetDescription(screenIndex, null, false);
    }

    public String
    getScreenReagentSetDescription(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenReagentSetDescription(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenReagentSetDescription(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenReagentSetDescription");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenReagentSetDescription(screenIndex, __ctx);
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
    getScreenType(int screenIndex)
    {
        return getScreenType(screenIndex, null, false);
    }

    public String
    getScreenType(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenType(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenType(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenType(screenIndex, __ctx);
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
    getServant()
    {
        return getServant(null, false);
    }

    public MetadataRetrieve
    getServant(java.util.Map<String, String> __ctx)
    {
        return getServant(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private MetadataRetrieve
    getServant(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getServant");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getServant(__ctx);
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
    getStageLabelName(int imageIndex)
    {
        return getStageLabelName(imageIndex, null, false);
    }

    public String
    getStageLabelName(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getStageLabelName(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getStageLabelName(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getStageLabelName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelName(imageIndex, __ctx);
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

    public float
    getStageLabelX(int imageIndex)
    {
        return getStageLabelX(imageIndex, null, false);
    }

    public float
    getStageLabelX(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getStageLabelX(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getStageLabelX(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getStageLabelX");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelX(imageIndex, __ctx);
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

    public float
    getStageLabelY(int imageIndex)
    {
        return getStageLabelY(imageIndex, null, false);
    }

    public float
    getStageLabelY(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getStageLabelY(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getStageLabelY(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getStageLabelY");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelY(imageIndex, __ctx);
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

    public float
    getStageLabelZ(int imageIndex)
    {
        return getStageLabelZ(imageIndex, null, false);
    }

    public float
    getStageLabelZ(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getStageLabelZ(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getStageLabelZ(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getStageLabelZ");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelZ(imageIndex, __ctx);
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

    public float
    getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public float
    getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getStagePositionPositionX");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex, __ctx);
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

    public float
    getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public float
    getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getStagePositionPositionY");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex, __ctx);
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

    public float
    getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public float
    getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getStagePositionPositionZ");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex, __ctx);
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
    getTiffDataCount(int imageIndex, int pixelsIndex)
    {
        return getTiffDataCount(imageIndex, pixelsIndex, null, false);
    }

    public int
    getTiffDataCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataCount(imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTiffDataCount(int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataCount(imageIndex, pixelsIndex, __ctx);
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
    getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public String
    getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataFileName");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public int
    getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataFirstC");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public int
    getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataFirstT");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public int
    getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataFirstZ");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public int
    getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataIFD");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public int
    getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataNumPlanes");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        return getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public String
    getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        return getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTiffDataUUID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getUUID()
    {
        return getUUID(null, false);
    }

    public String
    getUUID(java.util.Map<String, String> __ctx)
    {
        return getUUID(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getUUID(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getUUID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getUUID(__ctx);
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
    getWellColumn(int plateIndex, int wellIndex)
    {
        return getWellColumn(plateIndex, wellIndex, null, false);
    }

    public int
    getWellColumn(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellColumn(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getWellColumn(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellColumn");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellColumn(plateIndex, wellIndex, __ctx);
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
    getWellCount(int plateIndex)
    {
        return getWellCount(plateIndex, null, false);
    }

    public int
    getWellCount(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getWellCount(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getWellCount(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellCount(plateIndex, __ctx);
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
    getWellExternalDescription(int plateIndex, int wellIndex)
    {
        return getWellExternalDescription(plateIndex, wellIndex, null, false);
    }

    public String
    getWellExternalDescription(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellExternalDescription(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellExternalDescription(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellExternalDescription");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellExternalDescription(plateIndex, wellIndex, __ctx);
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
    getWellExternalIdentifier(int plateIndex, int wellIndex)
    {
        return getWellExternalIdentifier(plateIndex, wellIndex, null, false);
    }

    public String
    getWellExternalIdentifier(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellExternalIdentifier(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellExternalIdentifier(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellExternalIdentifier");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellExternalIdentifier(plateIndex, wellIndex, __ctx);
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
    getWellID(int plateIndex, int wellIndex)
    {
        return getWellID(plateIndex, wellIndex, null, false);
    }

    public String
    getWellID(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellID(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellID(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellID(plateIndex, wellIndex, __ctx);
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
    getWellRow(int plateIndex, int wellIndex)
    {
        return getWellRow(plateIndex, wellIndex, null, false);
    }

    public int
    getWellRow(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellRow(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getWellRow(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellRow");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellRow(plateIndex, wellIndex, __ctx);
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
    getWellSampleCount(int plateIndex, int wellIndex)
    {
        return getWellSampleCount(plateIndex, wellIndex, null, false);
    }

    public int
    getWellSampleCount(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSampleCount(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getWellSampleCount(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSampleCount");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleCount(plateIndex, wellIndex, __ctx);
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
    getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleID(plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public String
    getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSampleID(plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSampleID");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleID(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public int
    getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSampleIndex");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex, __ctx);
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

    public float
    getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public float
    getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSamplePosX");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex, __ctx);
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

    public float
    getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public float
    getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSamplePosY");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public int
    getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSampleTimepoint");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    getWellType(int plateIndex, int wellIndex)
    {
        return getWellType(plateIndex, wellIndex, null, false);
    }

    public String
    getWellType(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellType(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellType(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellType");
                __delBase = __getDelegate();
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellType(plateIndex, wellIndex, __ctx);
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

    public static MetadataRetrievePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        MetadataRetrievePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MetadataRetrievePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::MetadataRetrieve"))
                {
                    MetadataRetrievePrxHelper __h = new MetadataRetrievePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MetadataRetrievePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        MetadataRetrievePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MetadataRetrievePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::MetadataRetrieve", __ctx))
                {
                    MetadataRetrievePrxHelper __h = new MetadataRetrievePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MetadataRetrievePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MetadataRetrievePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::MetadataRetrieve"))
                {
                    MetadataRetrievePrxHelper __h = new MetadataRetrievePrxHelper();
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

    public static MetadataRetrievePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        MetadataRetrievePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::MetadataRetrieve", __ctx))
                {
                    MetadataRetrievePrxHelper __h = new MetadataRetrievePrxHelper();
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

    public static MetadataRetrievePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        MetadataRetrievePrx __d = null;
        if(__obj != null)
        {
            MetadataRetrievePrxHelper __h = new MetadataRetrievePrxHelper();
            __h.__copyFrom(__obj);
            __d = __h;
        }
        return __d;
    }

    public static MetadataRetrievePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MetadataRetrievePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            MetadataRetrievePrxHelper __h = new MetadataRetrievePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _MetadataRetrieveDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _MetadataRetrieveDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, MetadataRetrievePrx v)
    {
        __os.writeProxy(v);
    }

    public static MetadataRetrievePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            MetadataRetrievePrxHelper result = new MetadataRetrievePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
