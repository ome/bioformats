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

public final class _IFormatReaderDelD extends Ice._ObjectDelD implements _IFormatReaderDel
{
    public void
    close(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "close", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.close(__current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    closeFile(boolean fileOnly, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "closeFile", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.closeFile(fileOnly, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    fileGroupOption(String id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "fileGroupOption", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.fileGroupOption(id, __current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public short[][]
    get16BitLookupTable(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "get16BitLookupTable", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.get16BitLookupTable(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public byte[][]
    get8BitLookupTable(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "get8BitLookupTable", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.get8BitLookupTable(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int[]
    getChannelDimLengths(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getChannelDimLengths", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getChannelDimLengths(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public String[]
    getChannelDimTypes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getChannelDimTypes", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getChannelDimTypes(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public String
    getCurrentFile(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getCurrentFile", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getCurrentFile(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public String
    getDimensionOrder(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getDimensionOrder", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getDimensionOrder(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getEffectiveSizeC(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getEffectiveSizeC", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getEffectiveSizeC(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public String
    getFormat(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getFormat", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getFormat(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getImageCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getImageCount", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getImageCount(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getIndex(int z, int c, int t, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getIndex", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getIndex(z, c, t, __current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getPixelType(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getPixelType", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getPixelType(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getRGBChannelCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getRGBChannelCount", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getRGBChannelCount(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getSeries(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSeries", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSeries(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getSeriesCount(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSeriesCount", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSeriesCount(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getSizeC(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSizeC", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSizeC(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getSizeT(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSizeT", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSizeT(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getSizeX(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSizeX", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSizeX(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getSizeY(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSizeY", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSizeY(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getSizeZ(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSizeZ", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSizeZ(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public String[]
    getSuffixes(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getSuffixes", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getSuffixes(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getThumbSizeX(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getThumbSizeX", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getThumbSizeX(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int
    getThumbSizeY(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getThumbSizeY", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getThumbSizeY(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public String[]
    getUsedFiles(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getUsedFiles", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getUsedFiles(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public int[]
    getZCTCoords(int index, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "getZCTCoords", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.getZCTCoords(index, __current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isFalseColor(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isFalseColor", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isFalseColor(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isGroupFiles(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isGroupFiles", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isGroupFiles(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isIndexed(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isIndexed", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isIndexed(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isInterleaved(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isInterleaved", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isInterleaved(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isInterleavedSubC(int subC, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isInterleavedSubC", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isInterleavedSubC(subC, __current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isLittleEndian(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isLittleEndian", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isLittleEndian(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isMetadataCollected(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isMetadataCollected", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isMetadataCollected(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isMetadataComplete(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isMetadataComplete", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isMetadataComplete(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isMetadataFiltered(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isMetadataFiltered", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isMetadataFiltered(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isNormalized(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isNormalized", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isNormalized(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isOrderCertain(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isOrderCertain", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isOrderCertain(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isOriginalMetadataPopulated(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isOriginalMetadataPopulated", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isOriginalMetadataPopulated(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isRGB(java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isRGB", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isRGB(__current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public boolean
    isThisType(String name, boolean open, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "isThisType", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.isThisType(name, open, __current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public byte[]
    openBytes(int no, int x, int y, int width, int height, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "openBytes", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.openBytes(no, x, y, width, height, __current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public byte[]
    openThumbBytes(int no, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "openThumbBytes", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    return __servant.openThumbBytes(no, __current);
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setGroupFiles(boolean group, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setGroupFiles", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setGroupFiles(group, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setId(String id, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setId", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setId(id, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setMetadataCollected(boolean collect, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setMetadataCollected", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setMetadataCollected(collect, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setMetadataFiltered(boolean filter, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setMetadataFiltered", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setMetadataFiltered(filter, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setMetadataStore(MetadataStorePrx store, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setMetadataStore", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setMetadataStore(store, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setNormalized(boolean normalize, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setNormalized", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setNormalized(normalize, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setOriginalMetadataPopulated(boolean populate, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setOriginalMetadataPopulated", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setOriginalMetadataPopulated(populate, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setRetrieveAsStore(MetadataRetrievePrx retrieve, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setRetrieveAsStore", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setRetrieveAsStore(retrieve, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }

    public void
    setSeries(int no, java.util.Map<String, String> __ctx)
        throws IceInternal.LocalExceptionWrapper
    {
        Ice.Current __current = new Ice.Current();
        __initCurrent(__current, "setSeries", Ice.OperationMode.Normal, __ctx);
        while(true)
        {
            IceInternal.Direct __direct = new IceInternal.Direct(__current);
            try
            {
                IFormatReader __servant = null;
                try
                {
                    __servant = (IFormatReader)__direct.servant();
                }
                catch(ClassCastException __ex)
                {
                    Ice.OperationNotExistException __opEx = new Ice.OperationNotExistException();
                    __opEx.id = __current.id;
                    __opEx.facet = __current.facet;
                    __opEx.operation = __current.operation;
                    throw __opEx;
                }
                try
                {
                    __servant.setSeries(no, __current);
                    return;
                }
                catch(Ice.LocalException __ex)
                {
                    throw new IceInternal.LocalExceptionWrapper(__ex, false);
                }
            }
            finally
            {
                __direct.destroy();
            }
        }
    }
}
