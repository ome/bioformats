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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.createRoot(__ctx);
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setArcType(type, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorGain(gain, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorID(id, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorModel(model, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorOffset(offset, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorType(type, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDetectorVoltage(voltage, instrumentIndex, detectorIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsID(id, imageIndex, __ctx);
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
    setDisplayOptionsProjectionZStart(int zStart, int imageIndex)
    {
        setDisplayOptionsProjectionZStart(zStart, imageIndex, null, false);
    }

    public void
    setDisplayOptionsProjectionZStart(int zStart, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setDisplayOptionsProjectionZStart(zStart, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDisplayOptionsProjectionZStart(int zStart, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsProjectionZStart(zStart, imageIndex, __ctx);
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
    setDisplayOptionsProjectionZStop(int zStop, int imageIndex)
    {
        setDisplayOptionsProjectionZStop(zStop, imageIndex, null, false);
    }

    public void
    setDisplayOptionsProjectionZStop(int zStop, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setDisplayOptionsProjectionZStop(zStop, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDisplayOptionsProjectionZStop(int zStop, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsProjectionZStop(zStop, imageIndex, __ctx);
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
    setDisplayOptionsTimeTStart(int tStart, int imageIndex)
    {
        setDisplayOptionsTimeTStart(tStart, imageIndex, null, false);
    }

    public void
    setDisplayOptionsTimeTStart(int tStart, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setDisplayOptionsTimeTStart(tStart, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDisplayOptionsTimeTStart(int tStart, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsTimeTStart(tStart, imageIndex, __ctx);
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
    setDisplayOptionsTimeTStop(int tStop, int imageIndex)
    {
        setDisplayOptionsTimeTStop(tStop, imageIndex, null, false);
    }

    public void
    setDisplayOptionsTimeTStop(int tStop, int imageIndex, java.util.Map<String, String> __ctx)
    {
        setDisplayOptionsTimeTStop(tStop, imageIndex, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    setDisplayOptionsTimeTStop(int tStop, int imageIndex, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
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
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsTimeTStop(tStop, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setDisplayOptionsZoom(zoom, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimentDescription(description, experimentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimentID(id, experimentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimentType(type, experimentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterEmail(email, experimenterIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterFirstName(firstName, experimenterIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterID(id, experimenterIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterInstitution(institution, experimenterIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterLastName(lastName, experimenterIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setFilamentType(type, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageCreationDate(creationDate, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageDefaultPixels(defaultPixels, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageDescription(description, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageID(id, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageInstrumentRef(instrumentRef, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImageName(name, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentAirPressure(airPressure, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentHumidity(humidity, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setImagingEnvironmentTemperature(temperature, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setInstrumentID(id, instrumentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserPulse(pulse, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserType(type, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceID(id, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceModel(model, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourcePower(power, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelID(id, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelName(name, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFID(id, instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFObjective(objective, instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFPixelType(pixelType, instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFSizeX(sizeX, instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setOTFSizeY(sizeY, instrumentIndex, otfIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveID(id, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveModel(model, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsID(id, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsPixelType(pixelType, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeC(sizeC, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeT(sizeT, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeX(sizeX, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeY(sizeY, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateDescription(description, plateIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateExternalIdentifier(externalIdentifier, plateIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateID(id, plateIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateName(name, plateIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateRefID(id, screenIndex, plateRefIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setPlateStatus(status, plateIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIID(id, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIT0(t0, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIT1(t1, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIX0(x0, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIX1(x1, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIY0(y0, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIY1(y1, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIZ0(z0, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setROIZ1(z1, imageIndex, roiIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentDescription(description, screenIndex, reagentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentID(id, screenIndex, reagentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentName(name, screenIndex, reagentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenID(id, screenIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenName(name, screenIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenProtocolDescription(protocolDescription, screenIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenProtocolIdentifier(protocolIdentifier, screenIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenReagentSetDescription(reagentSetDescription, screenIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setScreenType(type, screenIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelName(name, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelX(x, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelY(y, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStageLabelZ(z, imageIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setUUID(uuid, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellColumn(column, plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellExternalDescription(externalDescription, plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellID(id, plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellRow(row, plateIndex, wellIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex, __ctx);
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
                __delBase = __getDelegate();
                _MetadataStoreDel __del = (_MetadataStoreDel)__delBase;
                __del.setWellType(type, plateIndex, wellIndex, __ctx);
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
            MetadataStorePrxHelper __h = new MetadataStorePrxHelper();
            __h.__copyFrom(__obj);
            __d = __h;
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
