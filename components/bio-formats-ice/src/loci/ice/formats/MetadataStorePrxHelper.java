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

public final class MetadataStorePrxHelper extends Ice.ObjectPrxHelperBase implements MetadataStorePrx
{
    public void
    createRoot()
    {
        createRoot(null, false);
    }

    public void
    createRoot(java.util.Map<String, String> __ctx)
    {
        createRoot(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    createRoot(java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.createRoot(__ctx);
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
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

    public MetadataStore
    getServant()
    {
        return getServant(null, false);
    }

    public MetadataStore
    getServant(java.util.Map<String, String> __ctx)
    {
        return getServant(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private MetadataStore
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
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

    public void
    setArcType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setArcType(type, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setArcType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setArcType(type, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setArcType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setArcType(type, instrumentIndex, lightSourceIndex, __ctx);
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
    setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, null, false);
    }

    public void
    setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
    {
        setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
    setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, null, false);
    }

    public void
    setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
    {
        setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setChannelComponentIndex(int index, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
    setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex)
    {
        setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex, null, false);
    }

    public void
    setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx)
    {
        setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
    setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleCx(cx, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setCircleCx(cx, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setCircleCx(cx, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleCy(cy, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setCircleCy(cy, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setCircleCy(cy, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setCircleID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setCircleID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleR(r, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setCircleR(r, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setCircleR(r, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setCircleTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setCircleTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setCircleTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setContactExperimenter(String experimenter, int groupIndex)
    {
        setContactExperimenter(experimenter, groupIndex, null, false);
    }

    public void
    setContactExperimenter(String experimenter, int groupIndex, java.util.Map<String, String> __ctx)
    {
        setContactExperimenter(experimenter, groupIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setContactExperimenter(String experimenter, int groupIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setContactExperimenter(experimenter, groupIndex, __ctx);
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
    setDatasetDescription(String description, int datasetIndex)
    {
        setDatasetDescription(description, datasetIndex, null, false);
    }

    public void
    setDatasetDescription(String description, int datasetIndex, java.util.Map<String, String> __ctx)
    {
        setDatasetDescription(description, datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDatasetDescription(String description, int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDatasetDescription(description, datasetIndex, __ctx);
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
    setDatasetExperimenterRef(String experimenterRef, int datasetIndex)
    {
        setDatasetExperimenterRef(experimenterRef, datasetIndex, null, false);
    }

    public void
    setDatasetExperimenterRef(String experimenterRef, int datasetIndex, java.util.Map<String, String> __ctx)
    {
        setDatasetExperimenterRef(experimenterRef, datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDatasetExperimenterRef(String experimenterRef, int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDatasetExperimenterRef(experimenterRef, datasetIndex, __ctx);
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
    setDatasetGroupRef(String groupRef, int datasetIndex)
    {
        setDatasetGroupRef(groupRef, datasetIndex, null, false);
    }

    public void
    setDatasetGroupRef(String groupRef, int datasetIndex, java.util.Map<String, String> __ctx)
    {
        setDatasetGroupRef(groupRef, datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDatasetGroupRef(String groupRef, int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDatasetGroupRef(groupRef, datasetIndex, __ctx);
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
    setDatasetID(String id, int datasetIndex)
    {
        setDatasetID(id, datasetIndex, null, false);
    }

    public void
    setDatasetID(String id, int datasetIndex, java.util.Map<String, String> __ctx)
    {
        setDatasetID(id, datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDatasetID(String id, int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDatasetID(id, datasetIndex, __ctx);
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
    setDatasetLocked(boolean locked, int datasetIndex)
    {
        setDatasetLocked(locked, datasetIndex, null, false);
    }

    public void
    setDatasetLocked(boolean locked, int datasetIndex, java.util.Map<String, String> __ctx)
    {
        setDatasetLocked(locked, datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDatasetLocked(boolean locked, int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDatasetLocked(locked, datasetIndex, __ctx);
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
    setDatasetName(String name, int datasetIndex)
    {
        setDatasetName(name, datasetIndex, null, false);
    }

    public void
    setDatasetName(String name, int datasetIndex, java.util.Map<String, String> __ctx)
    {
        setDatasetName(name, datasetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDatasetName(String name, int datasetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDatasetName(name, datasetIndex, __ctx);
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
    setDatasetRefID(String id, int imageIndex, int datasetRefIndex)
    {
        setDatasetRefID(id, imageIndex, datasetRefIndex, null, false);
    }

    public void
    setDatasetRefID(String id, int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx)
    {
        setDatasetRefID(id, imageIndex, datasetRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDatasetRefID(String id, int imageIndex, int datasetRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDatasetRefID(id, imageIndex, datasetRefIndex, __ctx);
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
    setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex)
    {
        setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorAmplificationGain(float amplificationGain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorGain(float gain, int instrumentIndex, int detectorIndex)
    {
        setDetectorGain(gain, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorGain(float gain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorGain(gain, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorGain(float gain, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorGain(gain, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorID(String id, int instrumentIndex, int detectorIndex)
    {
        setDetectorID(id, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorID(String id, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorID(id, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorID(String id, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorID(id, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex)
    {
        setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorModel(String model, int instrumentIndex, int detectorIndex)
    {
        setDetectorModel(model, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorModel(String model, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorModel(model, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorModel(String model, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorModel(model, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorOffset(float offset, int instrumentIndex, int detectorIndex)
    {
        setDetectorOffset(offset, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorOffset(offset, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorOffset(float offset, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorOffset(offset, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex)
    {
        setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex, __ctx);
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
    setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, __ctx);
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
    setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorSettingsGain(float gain, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, __ctx);
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
    setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorSettingsOffset(float offset, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, __ctx);
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
    setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorSettingsReadOutRate(float readOutRate, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex, __ctx);
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
    setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex)
    {
        setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorSettingsVoltage(float voltage, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex, __ctx);
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
    setDetectorType(String type, int instrumentIndex, int detectorIndex)
    {
        setDetectorType(type, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorType(String type, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorType(type, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorType(String type, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorType(type, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex)
    {
        setDetectorVoltage(voltage, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorVoltage(voltage, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorVoltage(float voltage, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorVoltage(voltage, instrumentIndex, detectorIndex, __ctx);
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
    setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex)
    {
        setDetectorZoom(zoom, instrumentIndex, detectorIndex, null, false);
    }

    public void
    setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx)
    {
        setDetectorZoom(zoom, instrumentIndex, detectorIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDetectorZoom(float zoom, int instrumentIndex, int detectorIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorZoom(zoom, instrumentIndex, detectorIndex, __ctx);
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
    setDichroicID(String id, int instrumentIndex, int dichroicIndex)
    {
        setDichroicID(id, instrumentIndex, dichroicIndex, null, false);
    }

    public void
    setDichroicID(String id, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        setDichroicID(id, instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDichroicID(String id, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDichroicID(id, instrumentIndex, dichroicIndex, __ctx);
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
    setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex)
    {
        setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex, null, false);
    }

    public void
    setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex, __ctx);
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
    setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex)
    {
        setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex, null, false);
    }

    public void
    setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex, __ctx);
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
    setDichroicModel(String model, int instrumentIndex, int dichroicIndex)
    {
        setDichroicModel(model, instrumentIndex, dichroicIndex, null, false);
    }

    public void
    setDichroicModel(String model, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx)
    {
        setDichroicModel(model, instrumentIndex, dichroicIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDichroicModel(String model, int instrumentIndex, int dichroicIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDichroicModel(model, instrumentIndex, dichroicIndex, __ctx);
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
    setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, null, false);
    }

    public void
    setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDimensionsPhysicalSizeX(float physicalSizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, __ctx);
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
    setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, null, false);
    }

    public void
    setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDimensionsPhysicalSizeY(float physicalSizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, __ctx);
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
    setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex)
    {
        setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, null, false);
    }

    public void
    setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDimensionsPhysicalSizeZ(float physicalSizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, __ctx);
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
    setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex)
    {
        setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, null, false);
    }

    public void
    setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDimensionsTimeIncrement(float timeIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, __ctx);
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
    setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex)
    {
        setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, null, false);
    }

    public void
    setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDimensionsWaveIncrement(int waveIncrement, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, __ctx);
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
    setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex)
    {
        setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, null, false);
    }

    public void
    setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDimensionsWaveStart(int waveStart, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, __ctx);
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
    setDisplayOptionsDisplay(String display, int imageIndex)
    {
        setDisplayOptionsDisplay(display, imageIndex, null, false);
    }

    public void
    setDisplayOptionsDisplay(String display, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setDisplayOptionsDisplay(display, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDisplayOptionsDisplay(String display, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsDisplay(display, imageIndex, __ctx);
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
    setDisplayOptionsID(String id, int imageIndex)
    {
        setDisplayOptionsID(id, imageIndex, null, false);
    }

    public void
    setDisplayOptionsID(String id, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setDisplayOptionsID(id, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDisplayOptionsID(String id, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsID(id, imageIndex, __ctx);
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
    setDisplayOptionsZoom(float zoom, int imageIndex)
    {
        setDisplayOptionsZoom(zoom, imageIndex, null, false);
    }

    public void
    setDisplayOptionsZoom(float zoom, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setDisplayOptionsZoom(zoom, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDisplayOptionsZoom(float zoom, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsZoom(zoom, imageIndex, __ctx);
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
    setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseCx(cx, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setEllipseCx(cx, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEllipseCx(cx, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseCy(cy, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setEllipseCy(cy, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEllipseCy(cy, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setEllipseID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEllipseID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseRx(rx, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setEllipseRx(rx, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEllipseRx(rx, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseRy(ry, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setEllipseRy(ry, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEllipseRy(ry, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null, false);
    }

    public void
    setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __ctx);
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
    setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null, false);
    }

    public void
    setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __ctx);
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
    setEmFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setEmFilterModel(model, instrumentIndex, filterIndex, null, false);
    }

    public void
    setEmFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setEmFilterModel(model, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEmFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEmFilterModel(model, instrumentIndex, filterIndex, __ctx);
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
    setEmFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setEmFilterType(type, instrumentIndex, filterIndex, null, false);
    }

    public void
    setEmFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setEmFilterType(type, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setEmFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setEmFilterType(type, instrumentIndex, filterIndex, __ctx);
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
    setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null, false);
    }

    public void
    setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __ctx);
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
    setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null, false);
    }

    public void
    setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __ctx);
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
    setExFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setExFilterModel(model, instrumentIndex, filterIndex, null, false);
    }

    public void
    setExFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setExFilterModel(model, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExFilterModel(model, instrumentIndex, filterIndex, __ctx);
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
    setExFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setExFilterType(type, instrumentIndex, filterIndex, null, false);
    }

    public void
    setExFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setExFilterType(type, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExFilterType(type, instrumentIndex, filterIndex, __ctx);
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
    setExperimentDescription(String description, int experimentIndex)
    {
        setExperimentDescription(description, experimentIndex, null, false);
    }

    public void
    setExperimentDescription(String description, int experimentIndex, java.util.Map<String, String> __ctx)
    {
        setExperimentDescription(description, experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimentDescription(String description, int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimentDescription(description, experimentIndex, __ctx);
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
    setExperimentExperimenterRef(String experimenterRef, int experimentIndex)
    {
        setExperimentExperimenterRef(experimenterRef, experimentIndex, null, false);
    }

    public void
    setExperimentExperimenterRef(String experimenterRef, int experimentIndex, java.util.Map<String, String> __ctx)
    {
        setExperimentExperimenterRef(experimenterRef, experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimentExperimenterRef(String experimenterRef, int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimentExperimenterRef(experimenterRef, experimentIndex, __ctx);
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
    setExperimentID(String id, int experimentIndex)
    {
        setExperimentID(id, experimentIndex, null, false);
    }

    public void
    setExperimentID(String id, int experimentIndex, java.util.Map<String, String> __ctx)
    {
        setExperimentID(id, experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimentID(String id, int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimentID(id, experimentIndex, __ctx);
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
    setExperimentType(String type, int experimentIndex)
    {
        setExperimentType(type, experimentIndex, null, false);
    }

    public void
    setExperimentType(String type, int experimentIndex, java.util.Map<String, String> __ctx)
    {
        setExperimentType(type, experimentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimentType(String type, int experimentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimentType(type, experimentIndex, __ctx);
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
    setExperimenterEmail(String email, int experimenterIndex)
    {
        setExperimenterEmail(email, experimenterIndex, null, false);
    }

    public void
    setExperimenterEmail(String email, int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        setExperimenterEmail(email, experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimenterEmail(String email, int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterEmail(email, experimenterIndex, __ctx);
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
    setExperimenterFirstName(String firstName, int experimenterIndex)
    {
        setExperimenterFirstName(firstName, experimenterIndex, null, false);
    }

    public void
    setExperimenterFirstName(String firstName, int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        setExperimenterFirstName(firstName, experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimenterFirstName(String firstName, int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterFirstName(firstName, experimenterIndex, __ctx);
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
    setExperimenterID(String id, int experimenterIndex)
    {
        setExperimenterID(id, experimenterIndex, null, false);
    }

    public void
    setExperimenterID(String id, int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        setExperimenterID(id, experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimenterID(String id, int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterID(id, experimenterIndex, __ctx);
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
    setExperimenterInstitution(String institution, int experimenterIndex)
    {
        setExperimenterInstitution(institution, experimenterIndex, null, false);
    }

    public void
    setExperimenterInstitution(String institution, int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        setExperimenterInstitution(institution, experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimenterInstitution(String institution, int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterInstitution(institution, experimenterIndex, __ctx);
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
    setExperimenterLastName(String lastName, int experimenterIndex)
    {
        setExperimenterLastName(lastName, experimenterIndex, null, false);
    }

    public void
    setExperimenterLastName(String lastName, int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        setExperimenterLastName(lastName, experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimenterLastName(String lastName, int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterLastName(lastName, experimenterIndex, __ctx);
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
    setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex)
    {
        setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, null, false);
    }

    public void
    setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx)
    {
        setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, __ctx);
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
    setExperimenterOMEName(String omeName, int experimenterIndex)
    {
        setExperimenterOMEName(omeName, experimenterIndex, null, false);
    }

    public void
    setExperimenterOMEName(String omeName, int experimenterIndex, java.util.Map<String, String> __ctx)
    {
        setExperimenterOMEName(omeName, experimenterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setExperimenterOMEName(String omeName, int experimenterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterOMEName(omeName, experimenterIndex, __ctx);
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
    setFilamentType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setFilamentType(type, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setFilamentType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setFilamentType(type, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilamentType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilamentType(type, instrumentIndex, lightSourceIndex, __ctx);
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
    setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex)
    {
        setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex, null, false);
    }

    public void
    setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex, __ctx);
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
    setFilterID(String id, int instrumentIndex, int filterIndex)
    {
        setFilterID(id, instrumentIndex, filterIndex, null, false);
    }

    public void
    setFilterID(String id, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setFilterID(id, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterID(String id, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterID(id, instrumentIndex, filterIndex, __ctx);
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
    setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
    {
        setFilterLotNumber(lotNumber, instrumentIndex, filterIndex, null, false);
    }

    public void
    setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterLotNumber(lotNumber, instrumentIndex, filterIndex, __ctx);
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
    setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
    {
        setFilterManufacturer(manufacturer, instrumentIndex, filterIndex, null, false);
    }

    public void
    setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterManufacturer(manufacturer, instrumentIndex, filterIndex, __ctx);
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
    setFilterModel(String model, int instrumentIndex, int filterIndex)
    {
        setFilterModel(model, instrumentIndex, filterIndex, null, false);
    }

    public void
    setFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setFilterModel(model, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterModel(String model, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterModel(model, instrumentIndex, filterIndex, __ctx);
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
    setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex, null, false);
    }

    public void
    setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex, __ctx);
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
    setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex, null, false);
    }

    public void
    setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex, __ctx);
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
    setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex, null, false);
    }

    public void
    setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex, __ctx);
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
    setFilterSetID(String id, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetID(id, instrumentIndex, filterSetIndex, null, false);
    }

    public void
    setFilterSetID(String id, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        setFilterSetID(id, instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterSetID(String id, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterSetID(id, instrumentIndex, filterSetIndex, __ctx);
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
    setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex, null, false);
    }

    public void
    setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex, __ctx);
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
    setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex, null, false);
    }

    public void
    setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex, __ctx);
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
    setFilterSetModel(String model, int instrumentIndex, int filterSetIndex)
    {
        setFilterSetModel(model, instrumentIndex, filterSetIndex, null, false);
    }

    public void
    setFilterSetModel(String model, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx)
    {
        setFilterSetModel(model, instrumentIndex, filterSetIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterSetModel(String model, int instrumentIndex, int filterSetIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterSetModel(model, instrumentIndex, filterSetIndex, __ctx);
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
    setFilterType(String type, int instrumentIndex, int filterIndex)
    {
        setFilterType(type, instrumentIndex, filterIndex, null, false);
    }

    public void
    setFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setFilterType(type, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setFilterType(String type, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilterType(type, instrumentIndex, filterIndex, __ctx);
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
    setGroupID(String id, int groupIndex)
    {
        setGroupID(id, groupIndex, null, false);
    }

    public void
    setGroupID(String id, int groupIndex, java.util.Map<String, String> __ctx)
    {
        setGroupID(id, groupIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setGroupID(String id, int groupIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setGroupID(id, groupIndex, __ctx);
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
    setGroupName(String name, int groupIndex)
    {
        setGroupName(name, groupIndex, null, false);
    }

    public void
    setGroupName(String name, int groupIndex, java.util.Map<String, String> __ctx)
    {
        setGroupName(name, groupIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setGroupName(String name, int groupIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setGroupName(name, groupIndex, __ctx);
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
    setImageAcquiredPixels(String acquiredPixels, int imageIndex)
    {
        setImageAcquiredPixels(acquiredPixels, imageIndex, null, false);
    }

    public void
    setImageAcquiredPixels(String acquiredPixels, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageAcquiredPixels(acquiredPixels, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageAcquiredPixels(String acquiredPixels, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageAcquiredPixels(acquiredPixels, imageIndex, __ctx);
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
    setImageCreationDate(String creationDate, int imageIndex)
    {
        setImageCreationDate(creationDate, imageIndex, null, false);
    }

    public void
    setImageCreationDate(String creationDate, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageCreationDate(creationDate, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageCreationDate(String creationDate, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageCreationDate(creationDate, imageIndex, __ctx);
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
    setImageDefaultPixels(String defaultPixels, int imageIndex)
    {
        setImageDefaultPixels(defaultPixels, imageIndex, null, false);
    }

    public void
    setImageDefaultPixels(String defaultPixels, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageDefaultPixels(defaultPixels, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageDefaultPixels(String defaultPixels, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageDefaultPixels(defaultPixels, imageIndex, __ctx);
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
    setImageDescription(String description, int imageIndex)
    {
        setImageDescription(description, imageIndex, null, false);
    }

    public void
    setImageDescription(String description, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageDescription(description, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageDescription(String description, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageDescription(description, imageIndex, __ctx);
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
    setImageExperimentRef(String experimentRef, int imageIndex)
    {
        setImageExperimentRef(experimentRef, imageIndex, null, false);
    }

    public void
    setImageExperimentRef(String experimentRef, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageExperimentRef(experimentRef, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageExperimentRef(String experimentRef, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageExperimentRef(experimentRef, imageIndex, __ctx);
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
    setImageExperimenterRef(String experimenterRef, int imageIndex)
    {
        setImageExperimenterRef(experimenterRef, imageIndex, null, false);
    }

    public void
    setImageExperimenterRef(String experimenterRef, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageExperimenterRef(experimenterRef, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageExperimenterRef(String experimenterRef, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageExperimenterRef(experimenterRef, imageIndex, __ctx);
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
    setImageGroupRef(String groupRef, int imageIndex)
    {
        setImageGroupRef(groupRef, imageIndex, null, false);
    }

    public void
    setImageGroupRef(String groupRef, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageGroupRef(groupRef, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageGroupRef(String groupRef, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageGroupRef(groupRef, imageIndex, __ctx);
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
    setImageID(String id, int imageIndex)
    {
        setImageID(id, imageIndex, null, false);
    }

    public void
    setImageID(String id, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageID(id, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageID(String id, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageID(id, imageIndex, __ctx);
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
    setImageInstrumentRef(String instrumentRef, int imageIndex)
    {
        setImageInstrumentRef(instrumentRef, imageIndex, null, false);
    }

    public void
    setImageInstrumentRef(String instrumentRef, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageInstrumentRef(instrumentRef, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageInstrumentRef(String instrumentRef, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageInstrumentRef(instrumentRef, imageIndex, __ctx);
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
    setImageName(String name, int imageIndex)
    {
        setImageName(name, imageIndex, null, false);
    }

    public void
    setImageName(String name, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImageName(name, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImageName(String name, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageName(name, imageIndex, __ctx);
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
    setImagingEnvironmentAirPressure(float airPressure, int imageIndex)
    {
        setImagingEnvironmentAirPressure(airPressure, imageIndex, null, false);
    }

    public void
    setImagingEnvironmentAirPressure(float airPressure, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImagingEnvironmentAirPressure(airPressure, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImagingEnvironmentAirPressure(float airPressure, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentAirPressure(airPressure, imageIndex, __ctx);
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
    setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex)
    {
        setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, null, false);
    }

    public void
    setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImagingEnvironmentCO2Percent(float cO2Percent, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, __ctx);
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
    setImagingEnvironmentHumidity(float humidity, int imageIndex)
    {
        setImagingEnvironmentHumidity(humidity, imageIndex, null, false);
    }

    public void
    setImagingEnvironmentHumidity(float humidity, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImagingEnvironmentHumidity(humidity, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImagingEnvironmentHumidity(float humidity, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentHumidity(humidity, imageIndex, __ctx);
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
    setImagingEnvironmentTemperature(float temperature, int imageIndex)
    {
        setImagingEnvironmentTemperature(temperature, imageIndex, null, false);
    }

    public void
    setImagingEnvironmentTemperature(float temperature, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setImagingEnvironmentTemperature(temperature, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setImagingEnvironmentTemperature(float temperature, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentTemperature(temperature, imageIndex, __ctx);
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
    setInstrumentID(String id, int instrumentIndex)
    {
        setInstrumentID(id, instrumentIndex, null, false);
    }

    public void
    setInstrumentID(String id, int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        setInstrumentID(id, instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setInstrumentID(String id, int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setInstrumentID(id, instrumentIndex, __ctx);
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
    setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex)
    {
        setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserFrequencyMultiplication(int frequencyMultiplication, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, __ctx);
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
    setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex)
    {
        setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, __ctx);
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
    setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex)
    {
        setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserPockelCell(boolean pockelCell, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex, __ctx);
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
    setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex)
    {
        setLaserPulse(pulse, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserPulse(pulse, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserPulse(pulse, instrumentIndex, lightSourceIndex, __ctx);
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
    setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex)
    {
        setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserRepetitionRate(boolean repetitionRate, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex, __ctx);
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
    setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex)
    {
        setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserTuneable(boolean tuneable, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, __ctx);
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
    setLaserType(String type, int instrumentIndex, int lightSourceIndex)
    {
        setLaserType(type, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserType(type, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserType(String type, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserType(type, instrumentIndex, lightSourceIndex, __ctx);
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
    setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex)
    {
        setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLaserWavelength(int wavelength, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, __ctx);
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
    setLightSourceID(String id, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceID(id, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceID(id, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceID(String id, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceID(id, instrumentIndex, lightSourceIndex, __ctx);
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
    setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, __ctx);
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
    setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceModel(model, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceModel(model, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceModel(model, instrumentIndex, lightSourceIndex, __ctx);
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
    setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourcePower(power, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourcePower(power, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourcePower(float power, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourcePower(power, instrumentIndex, lightSourceIndex, __ctx);
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
    setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null, false);
    }

    public void
    setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceRefAttenuation(float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx);
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
    setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null, false);
    }

    public void
    setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx);
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
    setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex)
    {
        setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, null, false);
    }

    public void
    setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceRefWavelength(int wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex, __ctx);
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
    setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
    {
        setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, __ctx);
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
    setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceSettingsAttenuation(float attenuation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, __ctx);
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
    setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, __ctx);
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
    setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex)
    {
        setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLightSourceSettingsWavelength(int wavelength, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, __ctx);
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
    setLineID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setLineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setLineID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLineID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setLineTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLineTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineX1(x1, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setLineX1(x1, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLineX1(x1, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineX2(x2, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setLineX2(x2, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLineX2(x2, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineY1(y1, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setLineY1(y1, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLineY1(y1, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex)
    {
        setLineY2(y2, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setLineY2(y2, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLineY2(y2, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelEmWave(int emWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelExWave(int exWave, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelID(id, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelID(id, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelID(id, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelName(name, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelName(name, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelName(name, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelNdFilter(float ndFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelPinholeSize(float pinholeSize, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelPockelCellSetting(int pockelCellSetting, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelSamplesPerPixel(int samplesPerPixel, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex, __ctx);
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
    setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex)
    {
        setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex, null, false);
    }

    public void
    setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx)
    {
        setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex, __ctx);
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
    setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskHeight(height, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskHeight(height, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskHeight(height, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskPixelsBigEndian(boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskPixelsSizeX(int sizeX, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskPixelsSizeY(int sizeY, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskWidth(width, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskWidth(width, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskWidth(width, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskX(x, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskX(x, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskX(x, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex)
    {
        setMaskY(y, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setMaskY(y, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMaskY(y, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex, null, false);
    }

    public void
    setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex, __ctx);
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
    setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex, null, false);
    }

    public void
    setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex, __ctx);
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
    setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex)
    {
        setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex, null, false);
    }

    public void
    setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx)
    {
        setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex, __ctx);
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
    setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex)
    {
        setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex, null, false);
    }

    public void
    setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx)
    {
        setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex, __ctx);
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
    setMicroscopeID(String id, int instrumentIndex)
    {
        setMicroscopeID(id, instrumentIndex, null, false);
    }

    public void
    setMicroscopeID(String id, int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        setMicroscopeID(id, instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicroscopeID(String id, int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicroscopeID(id, instrumentIndex, __ctx);
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
    setMicroscopeManufacturer(String manufacturer, int instrumentIndex)
    {
        setMicroscopeManufacturer(manufacturer, instrumentIndex, null, false);
    }

    public void
    setMicroscopeManufacturer(String manufacturer, int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        setMicroscopeManufacturer(manufacturer, instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicroscopeManufacturer(String manufacturer, int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicroscopeManufacturer(manufacturer, instrumentIndex, __ctx);
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
    setMicroscopeModel(String model, int instrumentIndex)
    {
        setMicroscopeModel(model, instrumentIndex, null, false);
    }

    public void
    setMicroscopeModel(String model, int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        setMicroscopeModel(model, instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicroscopeModel(String model, int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicroscopeModel(model, instrumentIndex, __ctx);
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
    setMicroscopeSerialNumber(String serialNumber, int instrumentIndex)
    {
        setMicroscopeSerialNumber(serialNumber, instrumentIndex, null, false);
    }

    public void
    setMicroscopeSerialNumber(String serialNumber, int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        setMicroscopeSerialNumber(serialNumber, instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicroscopeSerialNumber(String serialNumber, int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicroscopeSerialNumber(serialNumber, instrumentIndex, __ctx);
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
    setMicroscopeType(String type, int instrumentIndex)
    {
        setMicroscopeType(type, instrumentIndex, null, false);
    }

    public void
    setMicroscopeType(String type, int instrumentIndex, java.util.Map<String, String> __ctx)
    {
        setMicroscopeType(type, instrumentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setMicroscopeType(String type, int instrumentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setMicroscopeType(type, instrumentIndex, __ctx);
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
    setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex)
    {
        setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex, null, false);
    }

    public void
    setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex, __ctx);
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
    setOTFID(String id, int instrumentIndex, int otfIndex)
    {
        setOTFID(id, instrumentIndex, otfIndex, null, false);
    }

    public void
    setOTFID(String id, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        setOTFID(id, instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOTFID(String id, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFID(id, instrumentIndex, otfIndex, __ctx);
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
    setOTFObjective(String objective, int instrumentIndex, int otfIndex)
    {
        setOTFObjective(objective, instrumentIndex, otfIndex, null, false);
    }

    public void
    setOTFObjective(String objective, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        setOTFObjective(objective, instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOTFObjective(String objective, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFObjective(objective, instrumentIndex, otfIndex, __ctx);
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
    setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex)
    {
        setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, null, false);
    }

    public void
    setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOTFOpticalAxisAveraged(boolean opticalAxisAveraged, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, __ctx);
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
    setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex)
    {
        setOTFPixelType(pixelType, instrumentIndex, otfIndex, null, false);
    }

    public void
    setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        setOTFPixelType(pixelType, instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFPixelType(pixelType, instrumentIndex, otfIndex, __ctx);
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
    setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex)
    {
        setOTFSizeX(sizeX, instrumentIndex, otfIndex, null, false);
    }

    public void
    setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        setOTFSizeX(sizeX, instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOTFSizeX(int sizeX, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFSizeX(sizeX, instrumentIndex, otfIndex, __ctx);
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
    setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex)
    {
        setOTFSizeY(sizeY, instrumentIndex, otfIndex, null, false);
    }

    public void
    setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx)
    {
        setOTFSizeY(sizeY, instrumentIndex, otfIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setOTFSizeY(int sizeY, int instrumentIndex, int otfIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFSizeY(sizeY, instrumentIndex, otfIndex, __ctx);
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
    setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveCalibratedMagnification(float calibratedMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveID(String id, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveID(id, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveID(String id, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveID(id, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveID(String id, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveID(id, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveIris(iris, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveIris(iris, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveIris(boolean iris, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveIris(iris, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveLensNA(float lensNA, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveModel(String model, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveModel(model, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveModel(model, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveModel(String model, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveModel(model, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveNominalMagnification(int nominalMagnification, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, __ctx);
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
    setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex)
    {
        setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex, null, false);
    }

    public void
    setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveSettingsCorrectionCollar(float correctionCollar, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex, __ctx);
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
    setObjectiveSettingsMedium(String medium, int imageIndex)
    {
        setObjectiveSettingsMedium(medium, imageIndex, null, false);
    }

    public void
    setObjectiveSettingsMedium(String medium, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveSettingsMedium(medium, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveSettingsMedium(String medium, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveSettingsMedium(medium, imageIndex, __ctx);
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
    setObjectiveSettingsObjective(String objective, int imageIndex)
    {
        setObjectiveSettingsObjective(objective, imageIndex, null, false);
    }

    public void
    setObjectiveSettingsObjective(String objective, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveSettingsObjective(objective, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveSettingsObjective(String objective, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveSettingsObjective(objective, imageIndex, __ctx);
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
    setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex)
    {
        setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex, null, false);
    }

    public void
    setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveSettingsRefractiveIndex(float refractiveIndex, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex, __ctx);
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
    setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex)
    {
        setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, null, false);
    }

    public void
    setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx)
    {
        setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setObjectiveWorkingDistance(float workingDistance, int instrumentIndex, int objectiveIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, __ctx);
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
    setPathD(String d, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPathD(d, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPathD(String d, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPathD(d, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPathD(String d, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPathD(d, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPathID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPathID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPathID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPathID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPathID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPathID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex)
    {
        setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsBigEndian(boolean bigEndian, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, __ctx);
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
    setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex)
    {
        setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, __ctx);
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
    setPixelsID(String id, int imageIndex, int pixelsIndex)
    {
        setPixelsID(id, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsID(String id, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsID(id, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsID(String id, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsID(id, imageIndex, pixelsIndex, __ctx);
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
    setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex)
    {
        setPixelsPixelType(pixelType, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsPixelType(pixelType, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsPixelType(pixelType, imageIndex, pixelsIndex, __ctx);
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
    setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeC(sizeC, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsSizeC(sizeC, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsSizeC(int sizeC, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeC(sizeC, imageIndex, pixelsIndex, __ctx);
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
    setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeT(sizeT, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsSizeT(sizeT, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsSizeT(int sizeT, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeT(sizeT, imageIndex, pixelsIndex, __ctx);
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
    setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeX(sizeX, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsSizeX(sizeX, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsSizeX(int sizeX, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeX(sizeX, imageIndex, pixelsIndex, __ctx);
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
    setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeY(sizeY, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsSizeY(sizeY, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsSizeY(int sizeY, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeY(sizeY, imageIndex, pixelsIndex, __ctx);
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
    setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex)
    {
        setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, null, false);
    }

    public void
    setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx)
    {
        setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPixelsSizeZ(int sizeZ, int imageIndex, int pixelsIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, __ctx);
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
    setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneID(id, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setPlaneID(id, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneID(id, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlaneTheC(int theC, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlaneTheT(int theT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlaneTheZ(int theZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlaneTimingDeltaT(float deltaT, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlaneTimingExposureTime(float exposureTime, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex)
    {
        setPlateColumnNamingConvention(columnNamingConvention, plateIndex, null, false);
    }

    public void
    setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateColumnNamingConvention(columnNamingConvention, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateColumnNamingConvention(columnNamingConvention, plateIndex, __ctx);
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
    setPlateDescription(String description, int plateIndex)
    {
        setPlateDescription(description, plateIndex, null, false);
    }

    public void
    setPlateDescription(String description, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateDescription(description, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateDescription(String description, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateDescription(description, plateIndex, __ctx);
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
    setPlateExternalIdentifier(String externalIdentifier, int plateIndex)
    {
        setPlateExternalIdentifier(externalIdentifier, plateIndex, null, false);
    }

    public void
    setPlateExternalIdentifier(String externalIdentifier, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateExternalIdentifier(externalIdentifier, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateExternalIdentifier(String externalIdentifier, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateExternalIdentifier(externalIdentifier, plateIndex, __ctx);
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
    setPlateID(String id, int plateIndex)
    {
        setPlateID(id, plateIndex, null, false);
    }

    public void
    setPlateID(String id, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateID(id, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateID(String id, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateID(id, plateIndex, __ctx);
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
    setPlateName(String name, int plateIndex)
    {
        setPlateName(name, plateIndex, null, false);
    }

    public void
    setPlateName(String name, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateName(name, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateName(String name, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateName(name, plateIndex, __ctx);
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
    setPlateRefID(String id, int screenIndex, int plateRefIndex)
    {
        setPlateRefID(id, screenIndex, plateRefIndex, null, false);
    }

    public void
    setPlateRefID(String id, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
    {
        setPlateRefID(id, screenIndex, plateRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateRefID(String id, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateRefID(id, screenIndex, plateRefIndex, __ctx);
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
    setPlateRefSample(int sample, int screenIndex, int plateRefIndex)
    {
        setPlateRefSample(sample, screenIndex, plateRefIndex, null, false);
    }

    public void
    setPlateRefSample(int sample, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
    {
        setPlateRefSample(sample, screenIndex, plateRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateRefSample(int sample, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateRefSample(sample, screenIndex, plateRefIndex, __ctx);
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
    setPlateRefWell(String well, int screenIndex, int plateRefIndex)
    {
        setPlateRefWell(well, screenIndex, plateRefIndex, null, false);
    }

    public void
    setPlateRefWell(String well, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx)
    {
        setPlateRefWell(well, screenIndex, plateRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateRefWell(String well, int screenIndex, int plateRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateRefWell(well, screenIndex, plateRefIndex, __ctx);
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
    setPlateRowNamingConvention(String rowNamingConvention, int plateIndex)
    {
        setPlateRowNamingConvention(rowNamingConvention, plateIndex, null, false);
    }

    public void
    setPlateRowNamingConvention(String rowNamingConvention, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateRowNamingConvention(rowNamingConvention, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateRowNamingConvention(String rowNamingConvention, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateRowNamingConvention(rowNamingConvention, plateIndex, __ctx);
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
    setPlateStatus(String status, int plateIndex)
    {
        setPlateStatus(status, plateIndex, null, false);
    }

    public void
    setPlateStatus(String status, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateStatus(status, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateStatus(String status, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateStatus(status, plateIndex, __ctx);
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
    setPlateWellOriginX(double wellOriginX, int plateIndex)
    {
        setPlateWellOriginX(wellOriginX, plateIndex, null, false);
    }

    public void
    setPlateWellOriginX(double wellOriginX, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateWellOriginX(wellOriginX, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateWellOriginX(double wellOriginX, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateWellOriginX(wellOriginX, plateIndex, __ctx);
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
    setPlateWellOriginY(double wellOriginY, int plateIndex)
    {
        setPlateWellOriginY(wellOriginY, plateIndex, null, false);
    }

    public void
    setPlateWellOriginY(double wellOriginY, int plateIndex, java.util.Map<String, String> __ctx)
    {
        setPlateWellOriginY(wellOriginY, plateIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPlateWellOriginY(double wellOriginY, int plateIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateWellOriginY(wellOriginY, plateIndex, __ctx);
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
    setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointCx(cx, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPointCx(cx, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPointCx(cx, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointCy(cy, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPointCy(cy, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPointCy(cy, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPointID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPointID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPointID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPointID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPointID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPointR(String r, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointR(r, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPointR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPointR(r, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPointR(String r, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPointR(r, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPointTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPointTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPointTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPolygonID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPolygonID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonPoints(points, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPolygonPoints(points, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPolygonPoints(points, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylineID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPolylineID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPolylineID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylinePoints(points, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPolylinePoints(points, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPolylinePoints(points, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setProjectDescription(String description, int projectIndex)
    {
        setProjectDescription(description, projectIndex, null, false);
    }

    public void
    setProjectDescription(String description, int projectIndex, java.util.Map<String, String> __ctx)
    {
        setProjectDescription(description, projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setProjectDescription(String description, int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setProjectDescription(description, projectIndex, __ctx);
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
    setProjectExperimenterRef(String experimenterRef, int projectIndex)
    {
        setProjectExperimenterRef(experimenterRef, projectIndex, null, false);
    }

    public void
    setProjectExperimenterRef(String experimenterRef, int projectIndex, java.util.Map<String, String> __ctx)
    {
        setProjectExperimenterRef(experimenterRef, projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setProjectExperimenterRef(String experimenterRef, int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setProjectExperimenterRef(experimenterRef, projectIndex, __ctx);
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
    setProjectGroupRef(String groupRef, int projectIndex)
    {
        setProjectGroupRef(groupRef, projectIndex, null, false);
    }

    public void
    setProjectGroupRef(String groupRef, int projectIndex, java.util.Map<String, String> __ctx)
    {
        setProjectGroupRef(groupRef, projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setProjectGroupRef(String groupRef, int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setProjectGroupRef(groupRef, projectIndex, __ctx);
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
    setProjectID(String id, int projectIndex)
    {
        setProjectID(id, projectIndex, null, false);
    }

    public void
    setProjectID(String id, int projectIndex, java.util.Map<String, String> __ctx)
    {
        setProjectID(id, projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setProjectID(String id, int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setProjectID(id, projectIndex, __ctx);
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
    setProjectName(String name, int projectIndex)
    {
        setProjectName(name, projectIndex, null, false);
    }

    public void
    setProjectName(String name, int projectIndex, java.util.Map<String, String> __ctx)
    {
        setProjectName(name, projectIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setProjectName(String name, int projectIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setProjectName(name, projectIndex, __ctx);
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
    setProjectRefID(String id, int datasetIndex, int projectRefIndex)
    {
        setProjectRefID(id, datasetIndex, projectRefIndex, null, false);
    }

    public void
    setProjectRefID(String id, int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx)
    {
        setProjectRefID(id, datasetIndex, projectRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setProjectRefID(String id, int datasetIndex, int projectRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setProjectRefID(id, datasetIndex, projectRefIndex, __ctx);
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
    setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex)
    {
        setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex, null, false);
    }

    public void
    setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx)
    {
        setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex, __ctx);
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
    setROIID(String id, int imageIndex, int roiIndex)
    {
        setROIID(id, imageIndex, roiIndex, null, false);
    }

    public void
    setROIID(String id, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIID(id, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIID(String id, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIID(id, imageIndex, roiIndex, __ctx);
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
    setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex)
    {
        setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex, null, false);
    }

    public void
    setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx)
    {
        setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex, __ctx);
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
    setROIT0(int t0, int imageIndex, int roiIndex)
    {
        setROIT0(t0, imageIndex, roiIndex, null, false);
    }

    public void
    setROIT0(int t0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIT0(t0, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIT0(int t0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIT0(t0, imageIndex, roiIndex, __ctx);
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
    setROIT1(int t1, int imageIndex, int roiIndex)
    {
        setROIT1(t1, imageIndex, roiIndex, null, false);
    }

    public void
    setROIT1(int t1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIT1(t1, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIT1(int t1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIT1(t1, imageIndex, roiIndex, __ctx);
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
    setROIX0(int x0, int imageIndex, int roiIndex)
    {
        setROIX0(x0, imageIndex, roiIndex, null, false);
    }

    public void
    setROIX0(int x0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIX0(x0, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIX0(int x0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIX0(x0, imageIndex, roiIndex, __ctx);
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
    setROIX1(int x1, int imageIndex, int roiIndex)
    {
        setROIX1(x1, imageIndex, roiIndex, null, false);
    }

    public void
    setROIX1(int x1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIX1(x1, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIX1(int x1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIX1(x1, imageIndex, roiIndex, __ctx);
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
    setROIY0(int y0, int imageIndex, int roiIndex)
    {
        setROIY0(y0, imageIndex, roiIndex, null, false);
    }

    public void
    setROIY0(int y0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIY0(y0, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIY0(int y0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIY0(y0, imageIndex, roiIndex, __ctx);
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
    setROIY1(int y1, int imageIndex, int roiIndex)
    {
        setROIY1(y1, imageIndex, roiIndex, null, false);
    }

    public void
    setROIY1(int y1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIY1(y1, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIY1(int y1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIY1(y1, imageIndex, roiIndex, __ctx);
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
    setROIZ0(int z0, int imageIndex, int roiIndex)
    {
        setROIZ0(z0, imageIndex, roiIndex, null, false);
    }

    public void
    setROIZ0(int z0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIZ0(z0, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIZ0(int z0, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIZ0(z0, imageIndex, roiIndex, __ctx);
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
    setROIZ1(int z1, int imageIndex, int roiIndex)
    {
        setROIZ1(z1, imageIndex, roiIndex, null, false);
    }

    public void
    setROIZ1(int z1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx)
    {
        setROIZ1(z1, imageIndex, roiIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setROIZ1(int z1, int imageIndex, int roiIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIZ1(z1, imageIndex, roiIndex, __ctx);
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
    setReagentDescription(String description, int screenIndex, int reagentIndex)
    {
        setReagentDescription(description, screenIndex, reagentIndex, null, false);
    }

    public void
    setReagentDescription(String description, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        setReagentDescription(description, screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setReagentDescription(String description, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentDescription(description, screenIndex, reagentIndex, __ctx);
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
    setReagentID(String id, int screenIndex, int reagentIndex)
    {
        setReagentID(id, screenIndex, reagentIndex, null, false);
    }

    public void
    setReagentID(String id, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        setReagentID(id, screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setReagentID(String id, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentID(id, screenIndex, reagentIndex, __ctx);
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
    setReagentName(String name, int screenIndex, int reagentIndex)
    {
        setReagentName(name, screenIndex, reagentIndex, null, false);
    }

    public void
    setReagentName(String name, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        setReagentName(name, screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setReagentName(String name, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentName(name, screenIndex, reagentIndex, __ctx);
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
    setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex)
    {
        setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, null, false);
    }

    public void
    setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx)
    {
        setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, __ctx);
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
    setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectHeight(height, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setRectHeight(height, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRectHeight(height, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setRectID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setRectID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setRectID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRectID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRectID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectTransform(transform, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setRectTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRectTransform(transform, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectWidth(width, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setRectWidth(width, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRectWidth(width, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setRectX(String x, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectX(x, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setRectX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setRectX(x, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRectX(String x, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRectX(x, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setRectY(String y, int imageIndex, int roiIndex, int shapeIndex)
    {
        setRectY(y, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setRectY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setRectY(y, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRectY(String y, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRectY(y, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setRegionID(String id, int imageIndex, int regionIndex)
    {
        setRegionID(id, imageIndex, regionIndex, null, false);
    }

    public void
    setRegionID(String id, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
    {
        setRegionID(id, imageIndex, regionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRegionID(String id, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRegionID(id, imageIndex, regionIndex, __ctx);
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
    setRegionName(String name, int imageIndex, int regionIndex)
    {
        setRegionName(name, imageIndex, regionIndex, null, false);
    }

    public void
    setRegionName(String name, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
    {
        setRegionName(name, imageIndex, regionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRegionName(String name, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRegionName(name, imageIndex, regionIndex, __ctx);
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
    setRegionTag(String tag, int imageIndex, int regionIndex)
    {
        setRegionTag(tag, imageIndex, regionIndex, null, false);
    }

    public void
    setRegionTag(String tag, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx)
    {
        setRegionTag(tag, imageIndex, regionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRegionTag(String tag, int imageIndex, int regionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRegionTag(tag, imageIndex, regionIndex, __ctx);
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
    setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex, null, false);
    }

    public void
    setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
    {
        setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex, __ctx);
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
    setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex, null, false);
    }

    public void
    setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
    {
        setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex, __ctx);
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
    setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex)
    {
        setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex, null, false);
    }

    public void
    setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx)
    {
        setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex, __ctx);
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
    setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, null, false);
    }

    public void
    setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
    {
        setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, __ctx);
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
    setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, null, false);
    }

    public void
    setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
    {
        setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, __ctx);
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
    setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex)
    {
        setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, null, false);
    }

    public void
    setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx)
    {
        setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, __ctx);
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
    setScreenDescription(String description, int screenIndex)
    {
        setScreenDescription(description, screenIndex, null, false);
    }

    public void
    setScreenDescription(String description, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenDescription(description, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenDescription(String description, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenDescription(description, screenIndex, __ctx);
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
    setScreenExtern(String extern, int screenIndex)
    {
        setScreenExtern(extern, screenIndex, null, false);
    }

    public void
    setScreenExtern(String extern, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenExtern(extern, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenExtern(String extern, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenExtern(extern, screenIndex, __ctx);
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
    setScreenID(String id, int screenIndex)
    {
        setScreenID(id, screenIndex, null, false);
    }

    public void
    setScreenID(String id, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenID(id, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenID(String id, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenID(id, screenIndex, __ctx);
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
    setScreenName(String name, int screenIndex)
    {
        setScreenName(name, screenIndex, null, false);
    }

    public void
    setScreenName(String name, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenName(name, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenName(String name, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenName(name, screenIndex, __ctx);
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
    setScreenProtocolDescription(String protocolDescription, int screenIndex)
    {
        setScreenProtocolDescription(protocolDescription, screenIndex, null, false);
    }

    public void
    setScreenProtocolDescription(String protocolDescription, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenProtocolDescription(protocolDescription, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenProtocolDescription(String protocolDescription, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenProtocolDescription(protocolDescription, screenIndex, __ctx);
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
    setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex)
    {
        setScreenProtocolIdentifier(protocolIdentifier, screenIndex, null, false);
    }

    public void
    setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenProtocolIdentifier(protocolIdentifier, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenProtocolIdentifier(protocolIdentifier, screenIndex, __ctx);
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
    setScreenReagentSetDescription(String reagentSetDescription, int screenIndex)
    {
        setScreenReagentSetDescription(reagentSetDescription, screenIndex, null, false);
    }

    public void
    setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenReagentSetDescription(reagentSetDescription, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenReagentSetDescription(String reagentSetDescription, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenReagentSetDescription(reagentSetDescription, screenIndex, __ctx);
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
    setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex)
    {
        setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex, null, false);
    }

    public void
    setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex, __ctx);
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
    setScreenRefID(String id, int plateIndex, int screenRefIndex)
    {
        setScreenRefID(id, plateIndex, screenRefIndex, null, false);
    }

    public void
    setScreenRefID(String id, int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx)
    {
        setScreenRefID(id, plateIndex, screenRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenRefID(String id, int plateIndex, int screenRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenRefID(id, plateIndex, screenRefIndex, __ctx);
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
    setScreenType(String type, int screenIndex)
    {
        setScreenType(type, screenIndex, null, false);
    }

    public void
    setScreenType(String type, int screenIndex, java.util.Map<String, String> __ctx)
    {
        setScreenType(type, screenIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setScreenType(String type, int screenIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenType(type, screenIndex, __ctx);
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
    setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeDirection(direction, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeDirection(direction, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeDirection(direction, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFontSize(int fontSize, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeG(g, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeG(g, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeG(g, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeGlyphOrientationVertical(int glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeID(id, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeID(id, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeID(id, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeLocked(locked, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeLocked(locked, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeLocked(boolean locked, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeLocked(locked, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeMiterLimit(int strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeOpacity(float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeStrokeWidth(int strokeWidth, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeText(text, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeText(text, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeText(text, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTheT(theT, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeTheT(theT, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeTheT(int theT, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeTheT(theT, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeTheZ(int theZ, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeVisibility(boolean visibility, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex)
    {
        setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex, null, false);
    }

    public void
    setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx)
    {
        setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex, __ctx);
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
    setStageLabelName(String name, int imageIndex)
    {
        setStageLabelName(name, imageIndex, null, false);
    }

    public void
    setStageLabelName(String name, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setStageLabelName(name, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStageLabelName(String name, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelName(name, imageIndex, __ctx);
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
    setStageLabelX(float x, int imageIndex)
    {
        setStageLabelX(x, imageIndex, null, false);
    }

    public void
    setStageLabelX(float x, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setStageLabelX(x, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStageLabelX(float x, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelX(x, imageIndex, __ctx);
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
    setStageLabelY(float y, int imageIndex)
    {
        setStageLabelY(y, imageIndex, null, false);
    }

    public void
    setStageLabelY(float y, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setStageLabelY(y, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStageLabelY(float y, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelY(y, imageIndex, __ctx);
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
    setStageLabelZ(float z, int imageIndex)
    {
        setStageLabelZ(z, imageIndex, null, false);
    }

    public void
    setStageLabelZ(float z, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setStageLabelZ(z, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStageLabelZ(float z, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelZ(z, imageIndex, __ctx);
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
    setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStagePositionPositionX(float positionX, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStagePositionPositionY(float positionY, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex)
    {
        setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, null, false);
    }

    public void
    setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx)
    {
        setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setStagePositionPositionZ(float positionZ, int imageIndex, int pixelsIndex, int planeIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, __ctx);
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
    setThumbnailHref(String href, int imageIndex)
    {
        setThumbnailHref(href, imageIndex, null, false);
    }

    public void
    setThumbnailHref(String href, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setThumbnailHref(href, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setThumbnailHref(String href, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setThumbnailHref(href, imageIndex, __ctx);
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
    setThumbnailID(String id, int imageIndex)
    {
        setThumbnailID(id, imageIndex, null, false);
    }

    public void
    setThumbnailID(String id, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setThumbnailID(id, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setThumbnailID(String id, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setThumbnailID(id, imageIndex, __ctx);
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
    setThumbnailMIMEtype(String mimEtype, int imageIndex)
    {
        setThumbnailMIMEtype(mimEtype, imageIndex, null, false);
    }

    public void
    setThumbnailMIMEtype(String mimEtype, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setThumbnailMIMEtype(mimEtype, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setThumbnailMIMEtype(String mimEtype, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setThumbnailMIMEtype(mimEtype, imageIndex, __ctx);
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
    setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public void
    setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public void
    setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTiffDataFirstC(int firstC, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public void
    setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTiffDataFirstT(int firstT, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public void
    setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTiffDataFirstZ(int firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public void
    setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTiffDataIFD(int ifd, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public void
    setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTiffDataNumPlanes(int numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex)
    {
        setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, null, false);
    }

    public void
    setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx)
    {
        setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
    setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex, null, false);
    }

    public void
    setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTransmittanceRangeCutIn(int cutIn, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex, __ctx);
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
    setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex, null, false);
    }

    public void
    setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTransmittanceRangeCutInTolerance(int cutInTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex, __ctx);
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
    setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex, null, false);
    }

    public void
    setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTransmittanceRangeCutOut(int cutOut, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex, __ctx);
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
    setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex, null, false);
    }

    public void
    setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTransmittanceRangeCutOutTolerance(int cutOutTolerance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex, __ctx);
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
    setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex)
    {
        setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex, null, false);
    }

    public void
    setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx)
    {
        setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setTransmittanceRangeTransmittance(int transmittance, int instrumentIndex, int filterIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex, __ctx);
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
    setUUID(String uuid)
    {
        setUUID(uuid, null, false);
    }

    public void
    setUUID(String uuid, java.util.Map<String, String> __ctx)
    {
        setUUID(uuid, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setUUID(String uuid, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setUUID(uuid, __ctx);
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
    setWellColumn(int column, int plateIndex, int wellIndex)
    {
        setWellColumn(column, plateIndex, wellIndex, null, false);
    }

    public void
    setWellColumn(int column, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        setWellColumn(column, plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellColumn(int column, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellColumn(column, plateIndex, wellIndex, __ctx);
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
    setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex)
    {
        setWellExternalDescription(externalDescription, plateIndex, wellIndex, null, false);
    }

    public void
    setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        setWellExternalDescription(externalDescription, plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellExternalDescription(externalDescription, plateIndex, wellIndex, __ctx);
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
    setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex)
    {
        setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, null, false);
    }

    public void
    setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, __ctx);
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
    setWellID(String id, int plateIndex, int wellIndex)
    {
        setWellID(id, plateIndex, wellIndex, null, false);
    }

    public void
    setWellID(String id, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        setWellID(id, plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellID(String id, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellID(id, plateIndex, wellIndex, __ctx);
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
    setWellReagent(String reagent, int plateIndex, int wellIndex)
    {
        setWellReagent(reagent, plateIndex, wellIndex, null, false);
    }

    public void
    setWellReagent(String reagent, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        setWellReagent(reagent, plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellReagent(String reagent, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellReagent(reagent, plateIndex, wellIndex, __ctx);
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
    setWellRow(int row, int plateIndex, int wellIndex)
    {
        setWellRow(row, plateIndex, wellIndex, null, false);
    }

    public void
    setWellRow(int row, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        setWellRow(row, plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellRow(int row, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellRow(row, plateIndex, wellIndex, __ctx);
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
    setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public void
    setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public void
    setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public void
    setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellSampleIndex(int index, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public void
    setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellSamplePosX(float posX, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public void
    setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellSamplePosY(float posY, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex)
    {
        setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex, null, false);
    }

    public void
    setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx)
    {
        setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex, __ctx);
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
    setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
    {
        setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, null, false);
    }

    public void
    setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx)
    {
        setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellSampleTimepoint(int timepoint, int plateIndex, int wellIndex, int wellSampleIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
    setWellType(String type, int plateIndex, int wellIndex)
    {
        setWellType(type, plateIndex, wellIndex, null, false);
    }

    public void
    setWellType(String type, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx)
    {
        setWellType(type, plateIndex, wellIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setWellType(String type, int plateIndex, int wellIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellType(type, plateIndex, wellIndex, __ctx);
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

    public static MetadataStorePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        MetadataStorePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MetadataStorePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::MetadataStore"))
                {
                    MetadataStorePrxHelper __h = new MetadataStorePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MetadataStorePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        MetadataStorePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MetadataStorePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::formats::MetadataStore", __ctx))
                {
                    MetadataStorePrxHelper __h = new MetadataStorePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MetadataStorePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MetadataStorePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::MetadataStore"))
                {
                    MetadataStorePrxHelper __h = new MetadataStorePrxHelper();
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

    public static MetadataStorePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        MetadataStorePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::formats::MetadataStore", __ctx))
                {
                    MetadataStorePrxHelper __h = new MetadataStorePrxHelper();
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

    public static MetadataStorePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        MetadataStorePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (MetadataStorePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                MetadataStorePrxHelper __h = new MetadataStorePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static MetadataStorePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        MetadataStorePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            MetadataStorePrxHelper __h = new MetadataStorePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _MetadataStoreDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _MetadataStoreDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, MetadataStorePrx v)
    {
        __os.writeProxy(v);
    }

    public static MetadataStorePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            MetadataStorePrxHelper result = new MetadataStorePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
