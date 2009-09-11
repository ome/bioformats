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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getArcType(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getChannelComponentCount(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
    getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        return getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex, null, false);
    }

    public String
    getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
    {
        return getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getChannelComponentPixels");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
    getCircleCx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleCx(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getCircleCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getCircleCx(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getCircleCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getCircleCx");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getCircleCx(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getCircleCy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleCy(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getCircleCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getCircleCy(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getCircleCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getCircleCy");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getCircleCy(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getCircleID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getCircleID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getCircleID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getCircleID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getCircleID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getCircleID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getCircleR(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleR(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getCircleR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getCircleR(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getCircleR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getCircleR");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getCircleR(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getCircleTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getCircleTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getCircleTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getCircleTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getCircleTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getCircleTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getCircleTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getContactExperimenter(int groupIndex)
    {
        return getContactExperimenter(groupIndex, null, false);
    }

    public String
    getContactExperimenter(int groupIndex, java.util.Map<String, String> __ctx)
    {
        return getContactExperimenter(groupIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getContactExperimenter(int groupIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getContactExperimenter");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getContactExperimenter(groupIndex, __ctx);
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
    getDatasetCount()
    {
        return getDatasetCount(null, false);
    }

    public int
    getDatasetCount(java.util.Map<String, String> __ctx)
    {
        return getDatasetCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDatasetCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetCount(__ctx);
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
    getDatasetDescription(int datasetIndex)
    {
        return getDatasetDescription(datasetIndex, null, false);
    }

    public String
    getDatasetDescription(int datasetIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetDescription(datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDatasetDescription(int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetDescription");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetDescription(datasetIndex, __ctx);
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
    getDatasetExperimenterRef(int datasetIndex)
    {
        return getDatasetExperimenterRef(datasetIndex, null, false);
    }

    public String
    getDatasetExperimenterRef(int datasetIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetExperimenterRef(datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDatasetExperimenterRef(int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetExperimenterRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetExperimenterRef(datasetIndex, __ctx);
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
    getDatasetGroupRef(int datasetIndex)
    {
        return getDatasetGroupRef(datasetIndex, null, false);
    }

    public String
    getDatasetGroupRef(int datasetIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetGroupRef(datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDatasetGroupRef(int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetGroupRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetGroupRef(datasetIndex, __ctx);
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
    getDatasetID(int datasetIndex)
    {
        return getDatasetID(datasetIndex, null, false);
    }

    public String
    getDatasetID(int datasetIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetID(datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDatasetID(int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetID(datasetIndex, __ctx);
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
    getDatasetLocked(int datasetIndex)
    {
        return getDatasetLocked(datasetIndex, null, false);
    }

    public boolean
    getDatasetLocked(int datasetIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetLocked(datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getDatasetLocked(int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetLocked");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetLocked(datasetIndex, __ctx);
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
    getDatasetName(int datasetIndex)
    {
        return getDatasetName(datasetIndex, null, false);
    }

    public String
    getDatasetName(int datasetIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetName(datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDatasetName(int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetName");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetName(datasetIndex, __ctx);
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
    getDatasetRefCount(int imageIndex)
    {
        return getDatasetRefCount(imageIndex, null, false);
    }

    public int
    getDatasetRefCount(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetRefCount(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDatasetRefCount(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetRefCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetRefCount(imageIndex, __ctx);
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
    getDatasetRefID(int imageIndex, int datasetRefIndex)
    {
        return getDatasetRefID(imageIndex, datasetRefIndex, null, false);
    }

    public String
    getDatasetRefID(int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx)
    {
        return getDatasetRefID(imageIndex, datasetRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDatasetRefID(int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDatasetRefID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDatasetRefID(imageIndex, datasetRefIndex, __ctx);
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

    public float
    getDetectorAmplificationGain(int instrumentIndex, int detectorIndex)
    {
        return getDetectorAmplificationGain(instrumentIndex, detectorIndex, null, false);
    }

    public float
    getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorAmplificationGain(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorAmplificationGain(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorAmplificationGain");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorAmplificationGain(instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorCount(instrumentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorGain(instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorID(instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorManufacturer(instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorModel(instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorOffset(instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSerialNumber(instrumentIndex, detectorIndex, __ctx);
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
    getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsBinning(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorSettingsBinning(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorSettingsBinning");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsBinning(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsDetector(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsGain(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsOffset(imageIndex, logicalChannelIndex, __ctx);
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

    public float
    getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex, null, false);
    }

    public float
    getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorSettingsReadOutRate");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex, __ctx);
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

    public float
    getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex)
    {
        return getDetectorSettingsVoltage(imageIndex, logicalChannelIndex, null, false);
    }

    public float
    getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorSettingsVoltage(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorSettingsVoltage");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorSettingsVoltage(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorType(instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorVoltage(instrumentIndex, detectorIndex, __ctx);
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

    public float
    getDetectorZoom(int instrumentIndex, int detectorIndex)
    {
        return getDetectorZoom(instrumentIndex, detectorIndex, null, false);
    }

    public float
    getDetectorZoom(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        return getDetectorZoom(instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getDetectorZoom(int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDetectorZoom");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDetectorZoom(instrumentIndex, detectorIndex, __ctx);
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
    getDichroicCount(int instrumentIndex)
    {
        return getDichroicCount(instrumentIndex, null, false);
    }

    public int
    getDichroicCount(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getDichroicCount(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getDichroicCount(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDichroicCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDichroicCount(instrumentIndex, __ctx);
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
    getDichroicID(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicID(instrumentIndex, dichroicIndex, null, false);
    }

    public String
    getDichroicID(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        return getDichroicID(instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDichroicID(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDichroicID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDichroicID(instrumentIndex, dichroicIndex, __ctx);
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
    getDichroicLotNumber(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicLotNumber(instrumentIndex, dichroicIndex, null, false);
    }

    public String
    getDichroicLotNumber(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        return getDichroicLotNumber(instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDichroicLotNumber(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDichroicLotNumber");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDichroicLotNumber(instrumentIndex, dichroicIndex, __ctx);
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
    getDichroicManufacturer(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicManufacturer(instrumentIndex, dichroicIndex, null, false);
    }

    public String
    getDichroicManufacturer(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        return getDichroicManufacturer(instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDichroicManufacturer(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDichroicManufacturer");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDichroicManufacturer(instrumentIndex, dichroicIndex, __ctx);
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
    getDichroicModel(int instrumentIndex, int dichroicIndex)
    {
        return getDichroicModel(instrumentIndex, dichroicIndex, null, false);
    }

    public String
    getDichroicModel(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        return getDichroicModel(instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDichroicModel(int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDichroicModel");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDichroicModel(instrumentIndex, dichroicIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsTimeIncrement(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsWaveIncrement(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDimensionsWaveStart(imageIndex, pixelsIndex, __ctx);
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
    getDisplayOptionsDisplay(int imageIndex)
    {
        return getDisplayOptionsDisplay(imageIndex, null, false);
    }

    public String
    getDisplayOptionsDisplay(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getDisplayOptionsDisplay(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getDisplayOptionsDisplay(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getDisplayOptionsDisplay");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsDisplay(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsID(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getDisplayOptionsZoom(imageIndex, __ctx);
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
    getEllipseCx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseCx(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getEllipseCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getEllipseCx(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEllipseCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEllipseCx");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEllipseCx(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getEllipseCy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseCy(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getEllipseCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getEllipseCy(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEllipseCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEllipseCy");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEllipseCy(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getEllipseID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getEllipseID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getEllipseID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEllipseID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEllipseID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEllipseID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getEllipseRx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseRx(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getEllipseRx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getEllipseRx(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEllipseRx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEllipseRx");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEllipseRx(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getEllipseRy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseRy(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getEllipseRy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getEllipseRy(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEllipseRy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEllipseRy");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEllipseRy(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getEllipseTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getEllipseTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEllipseTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEllipseTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getEmFilterLotNumber(int instrumentIndex, int filterIndex)
    {
        return getEmFilterLotNumber(instrumentIndex, filterIndex, null, false);
    }

    public String
    getEmFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getEmFilterLotNumber(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEmFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEmFilterLotNumber");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEmFilterLotNumber(instrumentIndex, filterIndex, __ctx);
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
    getEmFilterManufacturer(int instrumentIndex, int filterIndex)
    {
        return getEmFilterManufacturer(instrumentIndex, filterIndex, null, false);
    }

    public String
    getEmFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getEmFilterManufacturer(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEmFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEmFilterManufacturer");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEmFilterManufacturer(instrumentIndex, filterIndex, __ctx);
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
    getEmFilterModel(int instrumentIndex, int filterIndex)
    {
        return getEmFilterModel(instrumentIndex, filterIndex, null, false);
    }

    public String
    getEmFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getEmFilterModel(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEmFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEmFilterModel");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEmFilterModel(instrumentIndex, filterIndex, __ctx);
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
    getEmFilterType(int instrumentIndex, int filterIndex)
    {
        return getEmFilterType(instrumentIndex, filterIndex, null, false);
    }

    public String
    getEmFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getEmFilterType(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getEmFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getEmFilterType");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getEmFilterType(instrumentIndex, filterIndex, __ctx);
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
    getExFilterLotNumber(int instrumentIndex, int filterIndex)
    {
        return getExFilterLotNumber(instrumentIndex, filterIndex, null, false);
    }

    public String
    getExFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getExFilterLotNumber(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExFilterLotNumber");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExFilterLotNumber(instrumentIndex, filterIndex, __ctx);
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
    getExFilterManufacturer(int instrumentIndex, int filterIndex)
    {
        return getExFilterManufacturer(instrumentIndex, filterIndex, null, false);
    }

    public String
    getExFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getExFilterManufacturer(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExFilterManufacturer");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExFilterManufacturer(instrumentIndex, filterIndex, __ctx);
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
    getExFilterModel(int instrumentIndex, int filterIndex)
    {
        return getExFilterModel(instrumentIndex, filterIndex, null, false);
    }

    public String
    getExFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getExFilterModel(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExFilterModel");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExFilterModel(instrumentIndex, filterIndex, __ctx);
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
    getExFilterType(int instrumentIndex, int filterIndex)
    {
        return getExFilterType(instrumentIndex, filterIndex, null, false);
    }

    public String
    getExFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getExFilterType(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExFilterType");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExFilterType(instrumentIndex, filterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentCount(__ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentDescription(experimentIndex, __ctx);
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
    getExperimentExperimenterRef(int experimentIndex)
    {
        return getExperimentExperimenterRef(experimentIndex, null, false);
    }

    public String
    getExperimentExperimenterRef(int experimentIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimentExperimenterRef(experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimentExperimenterRef(int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimentExperimenterRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentExperimenterRef(experimentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentID(experimentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimentType(experimentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterCount(__ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterEmail(experimenterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterFirstName(experimenterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterID(experimenterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterInstitution(experimenterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterLastName(experimenterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterMembershipCount(experimenterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex, __ctx);
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
    getExperimenterOMEName(int experimenterIndex)
    {
        return getExperimenterOMEName(experimenterIndex, null, false);
    }

    public String
    getExperimenterOMEName(int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        return getExperimenterOMEName(experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getExperimenterOMEName(int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getExperimenterOMEName");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getExperimenterOMEName(experimenterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilamentType(instrumentIndex, lightSourceIndex, __ctx);
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
    getFilterCount(int instrumentIndex)
    {
        return getFilterCount(instrumentIndex, null, false);
    }

    public int
    getFilterCount(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterCount(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getFilterCount(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterCount(instrumentIndex, __ctx);
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
    getFilterFilterWheel(int instrumentIndex, int filterIndex)
    {
        return getFilterFilterWheel(instrumentIndex, filterIndex, null, false);
    }

    public String
    getFilterFilterWheel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterFilterWheel(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterFilterWheel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterFilterWheel");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterFilterWheel(instrumentIndex, filterIndex, __ctx);
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
    getFilterID(int instrumentIndex, int filterIndex)
    {
        return getFilterID(instrumentIndex, filterIndex, null, false);
    }

    public String
    getFilterID(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterID(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterID(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterID(instrumentIndex, filterIndex, __ctx);
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
    getFilterLotNumber(int instrumentIndex, int filterIndex)
    {
        return getFilterLotNumber(instrumentIndex, filterIndex, null, false);
    }

    public String
    getFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterLotNumber(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterLotNumber(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterLotNumber");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterLotNumber(instrumentIndex, filterIndex, __ctx);
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
    getFilterManufacturer(int instrumentIndex, int filterIndex)
    {
        return getFilterManufacturer(instrumentIndex, filterIndex, null, false);
    }

    public String
    getFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterManufacturer(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterManufacturer(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterManufacturer");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterManufacturer(instrumentIndex, filterIndex, __ctx);
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
    getFilterModel(int instrumentIndex, int filterIndex)
    {
        return getFilterModel(instrumentIndex, filterIndex, null, false);
    }

    public String
    getFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterModel(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterModel(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterModel");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterModel(instrumentIndex, filterIndex, __ctx);
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
    getFilterSetCount(int instrumentIndex)
    {
        return getFilterSetCount(instrumentIndex, null, false);
    }

    public int
    getFilterSetCount(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetCount(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getFilterSetCount(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetCount(instrumentIndex, __ctx);
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
    getFilterSetDichroic(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetDichroic(instrumentIndex, filterSetIndex, null, false);
    }

    public String
    getFilterSetDichroic(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetDichroic(instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterSetDichroic(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetDichroic");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetDichroic(instrumentIndex, filterSetIndex, __ctx);
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
    getFilterSetEmFilter(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetEmFilter(instrumentIndex, filterSetIndex, null, false);
    }

    public String
    getFilterSetEmFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetEmFilter(instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterSetEmFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetEmFilter");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetEmFilter(instrumentIndex, filterSetIndex, __ctx);
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
    getFilterSetExFilter(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetExFilter(instrumentIndex, filterSetIndex, null, false);
    }

    public String
    getFilterSetExFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetExFilter(instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterSetExFilter(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetExFilter");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetExFilter(instrumentIndex, filterSetIndex, __ctx);
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
    getFilterSetID(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetID(instrumentIndex, filterSetIndex, null, false);
    }

    public String
    getFilterSetID(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetID(instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterSetID(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetID(instrumentIndex, filterSetIndex, __ctx);
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
    getFilterSetLotNumber(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetLotNumber(instrumentIndex, filterSetIndex, null, false);
    }

    public String
    getFilterSetLotNumber(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetLotNumber(instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterSetLotNumber(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetLotNumber");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetLotNumber(instrumentIndex, filterSetIndex, __ctx);
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
    getFilterSetManufacturer(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetManufacturer(instrumentIndex, filterSetIndex, null, false);
    }

    public String
    getFilterSetManufacturer(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetManufacturer(instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterSetManufacturer(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetManufacturer");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetManufacturer(instrumentIndex, filterSetIndex, __ctx);
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
    getFilterSetModel(int instrumentIndex, int filterSetIndex)
    {
        return getFilterSetModel(instrumentIndex, filterSetIndex, null, false);
    }

    public String
    getFilterSetModel(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterSetModel(instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterSetModel(int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterSetModel");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterSetModel(instrumentIndex, filterSetIndex, __ctx);
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
    getFilterType(int instrumentIndex, int filterIndex)
    {
        return getFilterType(instrumentIndex, filterIndex, null, false);
    }

    public String
    getFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getFilterType(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getFilterType(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getFilterType");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getFilterType(instrumentIndex, filterIndex, __ctx);
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
    getGroupCount()
    {
        return getGroupCount(null, false);
    }

    public int
    getGroupCount(java.util.Map<String, String> __ctx)
    {
        return getGroupCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getGroupCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getGroupCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getGroupCount(__ctx);
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
    getGroupID(int groupIndex)
    {
        return getGroupID(groupIndex, null, false);
    }

    public String
    getGroupID(int groupIndex, java.util.Map<String, String> __ctx)
    {
        return getGroupID(groupIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getGroupID(int groupIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getGroupID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getGroupID(groupIndex, __ctx);
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
    getGroupName(int groupIndex)
    {
        return getGroupName(groupIndex, null, false);
    }

    public String
    getGroupName(int groupIndex, java.util.Map<String, String> __ctx)
    {
        return getGroupName(groupIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getGroupName(int groupIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getGroupName");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getGroupName(groupIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getGroupRefCount(experimenterIndex, __ctx);
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
    getImageAcquiredPixels(int imageIndex)
    {
        return getImageAcquiredPixels(imageIndex, null, false);
    }

    public String
    getImageAcquiredPixels(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageAcquiredPixels(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageAcquiredPixels(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageAcquiredPixels");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageAcquiredPixels(imageIndex, __ctx);
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
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageCreationDate(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageDefaultPixels(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageDescription(imageIndex, __ctx);
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
    getImageExperimentRef(int imageIndex)
    {
        return getImageExperimentRef(imageIndex, null, false);
    }

    public String
    getImageExperimentRef(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageExperimentRef(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageExperimentRef(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageExperimentRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageExperimentRef(imageIndex, __ctx);
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
    getImageExperimenterRef(int imageIndex)
    {
        return getImageExperimenterRef(imageIndex, null, false);
    }

    public String
    getImageExperimenterRef(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageExperimenterRef(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageExperimenterRef(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageExperimenterRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageExperimenterRef(imageIndex, __ctx);
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
    getImageGroupRef(int imageIndex)
    {
        return getImageGroupRef(imageIndex, null, false);
    }

    public String
    getImageGroupRef(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getImageGroupRef(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getImageGroupRef(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getImageGroupRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageGroupRef(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageID(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageInstrumentRef(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImageName(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentAirPressure(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentCO2Percent(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentHumidity(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getImagingEnvironmentTemperature(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getInstrumentCount(__ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getInstrumentID(instrumentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserLaserMedium(instrumentIndex, lightSourceIndex, __ctx);
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
    getLaserPockelCell(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserPockelCell(instrumentIndex, lightSourceIndex, null, false);
    }

    public boolean
    getLaserPockelCell(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserPockelCell(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getLaserPockelCell(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserPockelCell");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserPockelCell(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserPulse(instrumentIndex, lightSourceIndex, __ctx);
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
    getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
    {
        return getLaserRepetitionRate(instrumentIndex, lightSourceIndex, null, false);
    }

    public boolean
    getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getLaserRepetitionRate(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLaserRepetitionRate");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserRepetitionRate(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserTuneable(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserType(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLaserWavelength(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceCount(instrumentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceID(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceManufacturer(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceModel(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourcePower(instrumentIndex, lightSourceIndex, __ctx);
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

    public float
    getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        return getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null, false);
    }

    public float
    getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceRefAttenuation");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx);
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
    getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex)
    {
        return getLightSourceRefCount(imageIndex, microbeamManipulationIndex, null, false);
    }

    public int
    getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceRefCount(imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceRefCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceRefCount(imageIndex, microbeamManipulationIndex, __ctx);
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
    getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        return getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null, false);
    }

    public String
    getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceRefLightSource");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx);
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
    getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        return getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null, false);
    }

    public int
    getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
    {
        return getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLightSourceRefWavelength");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex, __ctx);
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
    getLineID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getLineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getLineID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLineID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLineID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getLineTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getLineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getLineTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLineTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLineTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getLineX1(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineX1(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getLineX1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getLineX1(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLineX1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLineX1");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLineX1(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getLineX2(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineX2(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getLineX2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getLineX2(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLineX2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLineX2");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLineX2(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getLineY1(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineY1(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getLineY1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getLineY1(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLineY1(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLineY1");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLineY1(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getLineY2(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getLineY2(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getLineY2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getLineY2(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLineY2(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLineY2");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLineY2(imageIndex, roiIndex, shapeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelCount(imageIndex, __ctx);
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
    getLogicalChannelDetector(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelDetector(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelDetector(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelDetector(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelDetector");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelDetector(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelEmWave(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelExWave(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelFilterSet(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelFilterSet(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelFilterSet");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelFilterSet(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelFluor(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelID(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelLightSource(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelLightSource(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelLightSource");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelLightSource(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelMode(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelName(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelOTF(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelSecondaryEmissionFilter");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex, __ctx);
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
    getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex)
    {
        return getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex, null, false);
    }

    public String
    getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        return getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getLogicalChannelSecondaryExcitationFilter");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex, __ctx);
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
    getMaskHeight(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskHeight(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskHeight(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskHeight");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskHeight(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public boolean
    getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskPixelsBigEndian");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskPixelsBinData");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskPixelsExtendedPixelType");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskPixelsID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskPixelsID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskPixelsID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskPixelsSizeX");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskPixelsSizeY");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskWidth(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskWidth(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskWidth(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskWidth");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskWidth(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskX(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskX(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskX(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskX");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskX(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMaskY(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getMaskY(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getMaskY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getMaskY(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMaskY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMaskY");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMaskY(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getMicrobeamManipulationCount(int imageIndex)
    {
        return getMicrobeamManipulationCount(imageIndex, null, false);
    }

    public int
    getMicrobeamManipulationCount(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getMicrobeamManipulationCount(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getMicrobeamManipulationCount(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicrobeamManipulationCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicrobeamManipulationCount(imageIndex, __ctx);
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
    getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex)
    {
        return getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex, null, false);
    }

    public String
    getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        return getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicrobeamManipulationExperimenterRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex, __ctx);
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
    getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex)
    {
        return getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex, null, false);
    }

    public String
    getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        return getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicrobeamManipulationID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex, __ctx);
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
    getMicrobeamManipulationRefCount(int experimentIndex)
    {
        return getMicrobeamManipulationRefCount(experimentIndex, null, false);
    }

    public int
    getMicrobeamManipulationRefCount(int experimentIndex, java.util.Map<String, String> __ctx)
    {
        return getMicrobeamManipulationRefCount(experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getMicrobeamManipulationRefCount(int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicrobeamManipulationRefCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicrobeamManipulationRefCount(experimentIndex, __ctx);
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
    getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex)
    {
        return getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex, null, false);
    }

    public String
    getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx)
    {
        return getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicrobeamManipulationRefID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex, __ctx);
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
    getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex)
    {
        return getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex, null, false);
    }

    public String
    getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        return getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicrobeamManipulationType");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex, __ctx);
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
    getMicroscopeID(int instrumentIndex)
    {
        return getMicroscopeID(instrumentIndex, null, false);
    }

    public String
    getMicroscopeID(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getMicroscopeID(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicroscopeID(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicroscopeID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicroscopeID(instrumentIndex, __ctx);
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
    getMicroscopeManufacturer(int instrumentIndex)
    {
        return getMicroscopeManufacturer(instrumentIndex, null, false);
    }

    public String
    getMicroscopeManufacturer(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getMicroscopeManufacturer(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicroscopeManufacturer(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicroscopeManufacturer");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicroscopeManufacturer(instrumentIndex, __ctx);
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
    getMicroscopeModel(int instrumentIndex)
    {
        return getMicroscopeModel(instrumentIndex, null, false);
    }

    public String
    getMicroscopeModel(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getMicroscopeModel(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicroscopeModel(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicroscopeModel");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicroscopeModel(instrumentIndex, __ctx);
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
    getMicroscopeSerialNumber(int instrumentIndex)
    {
        return getMicroscopeSerialNumber(instrumentIndex, null, false);
    }

    public String
    getMicroscopeSerialNumber(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getMicroscopeSerialNumber(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicroscopeSerialNumber(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicroscopeSerialNumber");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicroscopeSerialNumber(instrumentIndex, __ctx);
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
    getMicroscopeType(int instrumentIndex)
    {
        return getMicroscopeType(instrumentIndex, null, false);
    }

    public String
    getMicroscopeType(int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        return getMicroscopeType(instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getMicroscopeType(int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getMicroscopeType");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getMicroscopeType(instrumentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOMEXML(__ctx);
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
    getOTFBinaryFile(int instrumentIndex, int otfIndex)
    {
        return getOTFBinaryFile(instrumentIndex, otfIndex, null, false);
    }

    public String
    getOTFBinaryFile(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        return getOTFBinaryFile(instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getOTFBinaryFile(int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getOTFBinaryFile");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFBinaryFile(instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFCount(instrumentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFID(instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFObjective(instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFPixelType(instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFSizeX(instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getOTFSizeY(instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveCorrection(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveCount(instrumentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveID(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveImmersion(instrumentIndex, objectiveIndex, __ctx);
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
    getObjectiveIris(int instrumentIndex, int objectiveIndex)
    {
        return getObjectiveIris(instrumentIndex, objectiveIndex, null, false);
    }

    public boolean
    getObjectiveIris(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveIris(instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getObjectiveIris(int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveIris");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveIris(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveLensNA(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveManufacturer(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveModel(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveSerialNumber(instrumentIndex, objectiveIndex, __ctx);
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

    public float
    getObjectiveSettingsCorrectionCollar(int imageIndex)
    {
        return getObjectiveSettingsCorrectionCollar(imageIndex, null, false);
    }

    public float
    getObjectiveSettingsCorrectionCollar(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveSettingsCorrectionCollar(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getObjectiveSettingsCorrectionCollar(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveSettingsCorrectionCollar");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveSettingsCorrectionCollar(imageIndex, __ctx);
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
    getObjectiveSettingsMedium(int imageIndex)
    {
        return getObjectiveSettingsMedium(imageIndex, null, false);
    }

    public String
    getObjectiveSettingsMedium(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveSettingsMedium(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveSettingsMedium(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveSettingsMedium");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveSettingsMedium(imageIndex, __ctx);
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
    getObjectiveSettingsObjective(int imageIndex)
    {
        return getObjectiveSettingsObjective(imageIndex, null, false);
    }

    public String
    getObjectiveSettingsObjective(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveSettingsObjective(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getObjectiveSettingsObjective(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveSettingsObjective");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveSettingsObjective(imageIndex, __ctx);
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

    public float
    getObjectiveSettingsRefractiveIndex(int imageIndex)
    {
        return getObjectiveSettingsRefractiveIndex(imageIndex, null, false);
    }

    public float
    getObjectiveSettingsRefractiveIndex(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getObjectiveSettingsRefractiveIndex(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getObjectiveSettingsRefractiveIndex(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getObjectiveSettingsRefractiveIndex");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveSettingsRefractiveIndex(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex, __ctx);
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
    getPathD(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPathD(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPathD(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPathD(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPathD(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPathD");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPathD(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPathID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPathID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPathID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPathID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPathID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPathID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPathID(imageIndex, roiIndex, shapeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsBigEndian(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsCount(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsDimensionOrder(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsID(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsPixelType(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeC(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeT(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeX(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeY(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPixelsSizeZ(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneCount(imageIndex, pixelsIndex, __ctx);
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
    getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public String
    getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneHashSHA1");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex, __ctx);
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
    getPlaneID(int imageIndex, int pixelsIndex, int planeIndex)
    {
        return getPlaneID(imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public String
    getPlaneID(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        return getPlaneID(imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlaneID(int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlaneID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneID(imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTheC(imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTheT(imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex, __ctx);
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
    getPlateColumnNamingConvention(int plateIndex)
    {
        return getPlateColumnNamingConvention(plateIndex, null, false);
    }

    public String
    getPlateColumnNamingConvention(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateColumnNamingConvention(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateColumnNamingConvention(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateColumnNamingConvention");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateColumnNamingConvention(plateIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateCount(__ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateDescription(plateIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateExternalIdentifier(plateIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateID(plateIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateName(plateIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateRefCount(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateRefID(screenIndex, plateRefIndex, __ctx);
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
    getPlateRefSample(int screenIndex, int plateRefIndex)
    {
        return getPlateRefSample(screenIndex, plateRefIndex, null, false);
    }

    public int
    getPlateRefSample(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateRefSample(screenIndex, plateRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getPlateRefSample(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateRefSample");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateRefSample(screenIndex, plateRefIndex, __ctx);
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
    getPlateRefWell(int screenIndex, int plateRefIndex)
    {
        return getPlateRefWell(screenIndex, plateRefIndex, null, false);
    }

    public String
    getPlateRefWell(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateRefWell(screenIndex, plateRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateRefWell(int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateRefWell");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateRefWell(screenIndex, plateRefIndex, __ctx);
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
    getPlateRowNamingConvention(int plateIndex)
    {
        return getPlateRowNamingConvention(plateIndex, null, false);
    }

    public String
    getPlateRowNamingConvention(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateRowNamingConvention(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPlateRowNamingConvention(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateRowNamingConvention");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateRowNamingConvention(plateIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateStatus(plateIndex, __ctx);
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

    public double
    getPlateWellOriginX(int plateIndex)
    {
        return getPlateWellOriginX(plateIndex, null, false);
    }

    public double
    getPlateWellOriginX(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateWellOriginX(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private double
    getPlateWellOriginX(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateWellOriginX");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateWellOriginX(plateIndex, __ctx);
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

    public double
    getPlateWellOriginY(int plateIndex)
    {
        return getPlateWellOriginY(plateIndex, null, false);
    }

    public double
    getPlateWellOriginY(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getPlateWellOriginY(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private double
    getPlateWellOriginY(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPlateWellOriginY");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPlateWellOriginY(plateIndex, __ctx);
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
    getPointCx(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointCx(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPointCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPointCx(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPointCx(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPointCx");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPointCx(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPointCy(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointCy(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPointCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPointCy(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPointCy(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPointCy");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPointCy(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPointID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPointID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPointID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPointID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPointID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPointID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPointR(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointR(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPointR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPointR(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPointR(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPointR");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPointR(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPointTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPointTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPointTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPointTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPointTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPointTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPointTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPolygonID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolygonID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPolygonID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPolygonID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPolygonID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPolygonID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPolygonID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolygonPoints(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPolygonPoints(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPolygonPoints");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPolygonPoints(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolygonTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPolygonTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPolygonTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPolygonTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPolylineID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolylineID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPolylineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPolylineID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPolylineID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPolylineID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPolylineID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolylinePoints(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPolylinePoints(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPolylinePoints");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPolylinePoints(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getPolylineTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getPolylineTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPolylineTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPolylineTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getProjectCount()
    {
        return getProjectCount(null, false);
    }

    public int
    getProjectCount(java.util.Map<String, String> __ctx)
    {
        return getProjectCount(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getProjectCount(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectCount(__ctx);
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
    getProjectDescription(int projectIndex)
    {
        return getProjectDescription(projectIndex, null, false);
    }

    public String
    getProjectDescription(int projectIndex, java.util.Map<String, String> __ctx)
    {
        return getProjectDescription(projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getProjectDescription(int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectDescription");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectDescription(projectIndex, __ctx);
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
    getProjectExperimenterRef(int projectIndex)
    {
        return getProjectExperimenterRef(projectIndex, null, false);
    }

    public String
    getProjectExperimenterRef(int projectIndex, java.util.Map<String, String> __ctx)
    {
        return getProjectExperimenterRef(projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getProjectExperimenterRef(int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectExperimenterRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectExperimenterRef(projectIndex, __ctx);
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
    getProjectGroupRef(int projectIndex)
    {
        return getProjectGroupRef(projectIndex, null, false);
    }

    public String
    getProjectGroupRef(int projectIndex, java.util.Map<String, String> __ctx)
    {
        return getProjectGroupRef(projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getProjectGroupRef(int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectGroupRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectGroupRef(projectIndex, __ctx);
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
    getProjectID(int projectIndex)
    {
        return getProjectID(projectIndex, null, false);
    }

    public String
    getProjectID(int projectIndex, java.util.Map<String, String> __ctx)
    {
        return getProjectID(projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getProjectID(int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectID(projectIndex, __ctx);
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
    getProjectName(int projectIndex)
    {
        return getProjectName(projectIndex, null, false);
    }

    public String
    getProjectName(int projectIndex, java.util.Map<String, String> __ctx)
    {
        return getProjectName(projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getProjectName(int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectName");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectName(projectIndex, __ctx);
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
    getProjectRefCount(int datasetIndex)
    {
        return getProjectRefCount(datasetIndex, null, false);
    }

    public int
    getProjectRefCount(int datasetIndex, java.util.Map<String, String> __ctx)
    {
        return getProjectRefCount(datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getProjectRefCount(int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectRefCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectRefCount(datasetIndex, __ctx);
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
    getProjectRefID(int datasetIndex, int projectRefIndex)
    {
        return getProjectRefID(datasetIndex, projectRefIndex, null, false);
    }

    public String
    getProjectRefID(int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx)
    {
        return getProjectRefID(datasetIndex, projectRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getProjectRefID(int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getProjectRefID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getProjectRefID(datasetIndex, projectRefIndex, __ctx);
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
    getPumpLightSource(int instrumentIndex, int lightSourceIndex)
    {
        return getPumpLightSource(instrumentIndex, lightSourceIndex, null, false);
    }

    public String
    getPumpLightSource(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        return getPumpLightSource(instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getPumpLightSource(int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getPumpLightSource");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getPumpLightSource(instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROICount(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIID(imageIndex, roiIndex, __ctx);
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
    getROIRefCount(int imageIndex, int microbeamManipulationIndex)
    {
        return getROIRefCount(imageIndex, microbeamManipulationIndex, null, false);
    }

    public int
    getROIRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        return getROIRefCount(imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getROIRefCount(int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIRefCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIRefCount(imageIndex, microbeamManipulationIndex, __ctx);
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
    getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex)
    {
        return getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex, null, false);
    }

    public String
    getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx)
    {
        return getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getROIRefID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIT0(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIT1(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIX0(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIX1(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIY0(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIY1(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIZ0(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getROIZ1(imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentCount(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentDescription(screenIndex, reagentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentID(screenIndex, reagentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentName(screenIndex, reagentIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getReagentReagentIdentifier(screenIndex, reagentIndex, __ctx);
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
    getRectHeight(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectHeight(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getRectHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getRectHeight(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRectHeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRectHeight");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRectHeight(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getRectID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getRectID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getRectID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRectID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRectID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRectID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getRectTransform(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectTransform(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getRectTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getRectTransform(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRectTransform(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRectTransform");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRectTransform(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getRectWidth(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectWidth(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getRectWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getRectWidth(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRectWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRectWidth");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRectWidth(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getRectX(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectX(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getRectX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getRectX(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRectX(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRectX");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRectX(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getRectY(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getRectY(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getRectY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getRectY(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRectY(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRectY");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRectY(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getRegionCount(int imageIndex)
    {
        return getRegionCount(imageIndex, null, false);
    }

    public int
    getRegionCount(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getRegionCount(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getRegionCount(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRegionCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRegionCount(imageIndex, __ctx);
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
    getRegionID(int imageIndex, int regionIndex)
    {
        return getRegionID(imageIndex, regionIndex, null, false);
    }

    public String
    getRegionID(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
    {
        return getRegionID(imageIndex, regionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRegionID(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRegionID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRegionID(imageIndex, regionIndex, __ctx);
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
    getRegionName(int imageIndex, int regionIndex)
    {
        return getRegionName(imageIndex, regionIndex, null, false);
    }

    public String
    getRegionName(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
    {
        return getRegionName(imageIndex, regionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRegionName(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRegionName");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRegionName(imageIndex, regionIndex, __ctx);
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
    getRegionTag(int imageIndex, int regionIndex)
    {
        return getRegionTag(imageIndex, regionIndex, null, false);
    }

    public String
    getRegionTag(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
    {
        return getRegionTag(imageIndex, regionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRegionTag(int imageIndex, int regionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRegionTag");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRegionTag(imageIndex, regionIndex, __ctx);
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
    getRoiLinkCount(int imageIndex, int roiIndex)
    {
        return getRoiLinkCount(imageIndex, roiIndex, null, false);
    }

    public int
    getRoiLinkCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getRoiLinkCount(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getRoiLinkCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRoiLinkCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRoiLinkCount(imageIndex, roiIndex, __ctx);
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
    getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex)
    {
        return getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex, null, false);
    }

    public String
    getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
    {
        return getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRoiLinkDirection");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex, __ctx);
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
    getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex)
    {
        return getRoiLinkName(imageIndex, roiIndex, roiLinkIndex, null, false);
    }

    public String
    getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
    {
        return getRoiLinkName(imageIndex, roiIndex, roiLinkIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRoiLinkName");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRoiLinkName(imageIndex, roiIndex, roiLinkIndex, __ctx);
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
    getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex)
    {
        return getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex, null, false);
    }

    public String
    getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
    {
        return getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getRoiLinkRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionCount(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenCount(__ctx);
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
    getScreenDescription(int screenIndex)
    {
        return getScreenDescription(screenIndex, null, false);
    }

    public String
    getScreenDescription(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenDescription(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenDescription(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenDescription");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenDescription(screenIndex, __ctx);
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
    getScreenExtern(int screenIndex)
    {
        return getScreenExtern(screenIndex, null, false);
    }

    public String
    getScreenExtern(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenExtern(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenExtern(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenExtern");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenExtern(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenID(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenName(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenProtocolDescription(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenProtocolIdentifier(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenReagentSetDescription(screenIndex, __ctx);
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
    getScreenReagentSetIdentifier(int screenIndex)
    {
        return getScreenReagentSetIdentifier(screenIndex, null, false);
    }

    public String
    getScreenReagentSetIdentifier(int screenIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenReagentSetIdentifier(screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenReagentSetIdentifier(int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenReagentSetIdentifier");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenReagentSetIdentifier(screenIndex, __ctx);
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
    getScreenRefCount(int plateIndex)
    {
        return getScreenRefCount(plateIndex, null, false);
    }

    public int
    getScreenRefCount(int plateIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenRefCount(plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getScreenRefCount(int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenRefCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenRefCount(plateIndex, __ctx);
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
    getScreenRefID(int plateIndex, int screenRefIndex)
    {
        return getScreenRefID(plateIndex, screenRefIndex, null, false);
    }

    public String
    getScreenRefID(int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx)
    {
        return getScreenRefID(plateIndex, screenRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getScreenRefID(int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getScreenRefID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenRefID(plateIndex, screenRefIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getScreenType(screenIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getServant(__ctx);
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
    getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeBaselineShift(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeBaselineShift(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeBaselineShift");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeBaselineShift(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeCount(int imageIndex, int roiIndex)
    {
        return getShapeCount(imageIndex, roiIndex, null, false);
    }

    public int
    getShapeCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeCount(imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getShapeCount(int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeCount(imageIndex, roiIndex, __ctx);
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
    getShapeDirection(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeDirection(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeDirection(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeDirection(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeDirection(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeDirection");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeDirection(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFillColor(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFillColor(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFillColor");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFillColor(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFillOpacity(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFillOpacity(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFillOpacity");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFillOpacity(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFillRule(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFillRule(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFillRule");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFillRule(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontFamily(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFontFamily(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFontFamily");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFontFamily(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontSize(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFontSize(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFontSize");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFontSize(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontStretch(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFontStretch(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFontStretch");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFontStretch(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontStyle(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFontStyle(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFontStyle");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFontStyle(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontVariant(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFontVariant(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFontVariant");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFontVariant(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeFontWeight(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeFontWeight(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeFontWeight");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeFontWeight(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeG(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeG(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeG(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeG(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeG(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeG");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeG(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeGlyphOrientationVertical");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeID(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeID(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeID(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeID(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeID(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeLocked(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeLocked(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public boolean
    getShapeLocked(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeLocked(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getShapeLocked(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeLocked");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeLocked(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeAttribute");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeColor(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeColor(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeColor");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeColor(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeDashArray");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeLineCap");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeLineJoin");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeMiterLimit");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex, __ctx);
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

    public float
    getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public float
    getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private float
    getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeOpacity");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeStrokeWidth");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeText(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeText(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeText(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeText(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeText(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeText");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeText(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextAnchor(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeTextAnchor(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeTextAnchor");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeTextAnchor(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextDecoration(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeTextDecoration(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeTextDecoration");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeTextDecoration(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextFill(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeTextFill(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeTextFill");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeTextFill(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTextStroke(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeTextStroke(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeTextStroke");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeTextStroke(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeTheT(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTheT(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getShapeTheT(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeTheT(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getShapeTheT(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeTheT");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeTheT(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeTheZ(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public int
    getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeTheZ(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeTheZ");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeTheZ(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeVectorEffect(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeVectorEffect(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeVectorEffect");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeVectorEffect(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeVisibility(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public boolean
    getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeVisibility(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeVisibility");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeVisibility(imageIndex, roiIndex, shapeIndex, __ctx);
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
    getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex)
    {
        return getShapeWritingMode(imageIndex, roiIndex, shapeIndex, null, false);
    }

    public String
    getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        return getShapeWritingMode(imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getShapeWritingMode");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getShapeWritingMode(imageIndex, roiIndex, shapeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelName(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelX(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelY(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStageLabelZ(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex, __ctx);
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
    getThumbnailHref(int imageIndex)
    {
        return getThumbnailHref(imageIndex, null, false);
    }

    public String
    getThumbnailHref(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getThumbnailHref(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getThumbnailHref(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getThumbnailHref");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getThumbnailHref(imageIndex, __ctx);
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
    getThumbnailID(int imageIndex)
    {
        return getThumbnailID(imageIndex, null, false);
    }

    public String
    getThumbnailID(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getThumbnailID(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getThumbnailID(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getThumbnailID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getThumbnailID(imageIndex, __ctx);
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
    getThumbnailMIMEtype(int imageIndex)
    {
        return getThumbnailMIMEtype(imageIndex, null, false);
    }

    public String
    getThumbnailMIMEtype(int imageIndex, java.util.Map<String, String> __ctx)
    {
        return getThumbnailMIMEtype(imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getThumbnailMIMEtype(int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getThumbnailMIMEtype");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getThumbnailMIMEtype(imageIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataCount(imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutIn(instrumentIndex, filterIndex, null, false);
    }

    public int
    getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getTransmittanceRangeCutIn(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTransmittanceRangeCutIn");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTransmittanceRangeCutIn(instrumentIndex, filterIndex, __ctx);
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
    getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex, null, false);
    }

    public int
    getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTransmittanceRangeCutInTolerance");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex, __ctx);
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
    getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutOut(instrumentIndex, filterIndex, null, false);
    }

    public int
    getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getTransmittanceRangeCutOut(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTransmittanceRangeCutOut");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTransmittanceRangeCutOut(instrumentIndex, filterIndex, __ctx);
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
    getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex, null, false);
    }

    public int
    getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTransmittanceRangeCutOutTolerance");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex, __ctx);
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
    getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex)
    {
        return getTransmittanceRangeTransmittance(instrumentIndex, filterIndex, null, false);
    }

    public int
    getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        return getTransmittanceRangeTransmittance(instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getTransmittanceRangeTransmittance");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getUUID(__ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellColumn(plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellCount(plateIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellExternalDescription(plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellExternalIdentifier(plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellID(plateIndex, wellIndex, __ctx);
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
    getWellReagent(int plateIndex, int wellIndex)
    {
        return getWellReagent(plateIndex, wellIndex, null, false);
    }

    public String
    getWellReagent(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        return getWellReagent(plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellReagent(int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellReagent");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellReagent(plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellRow(plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleCount(plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleID(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex)
    {
        return getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public String
    getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSampleImageRef");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex)
    {
        return getWellSampleRefCount(screenIndex, screenAcquisitionIndex, null, false);
    }

    public int
    getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSampleRefCount(screenIndex, screenAcquisitionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private int
    getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSampleRefCount");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleRefCount(screenIndex, screenAcquisitionIndex, __ctx);
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
    getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex)
    {
        return getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex, null, false);
    }

    public String
    getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx)
    {
        return getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private String
    getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getWellSampleRefID");
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate(false);
                _MetadataRetrieveDel __del = (_MetadataRetrieveDel)__delBase;
                return __del.getWellType(plateIndex, wellIndex, __ctx);
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
            try
            {
                __d = (MetadataRetrievePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                MetadataRetrievePrxHelper __h = new MetadataRetrievePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
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
