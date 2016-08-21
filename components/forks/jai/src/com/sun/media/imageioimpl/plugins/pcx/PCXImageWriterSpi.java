/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/*
 * $RCSfile: PCXImageWriterSpi.java,v $
 *
 * 
 * Copyright (c) 2007 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2007/09/05 00:21:08 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.pcx;

import java.awt.image.*;
import java.util.Locale;

import javax.imageio.*;
import javax.imageio.spi.*;

import com.sun.media.imageioimpl.common.PackageUtil;

public class PCXImageWriterSpi extends ImageWriterSpi {
    private static String [] readerSpiNames =
        {"com.sun.media.imageioimpl.plugins.pcx.PCXImageReaderSpi"};
    private static String[] formatNames = {"pcx", "PCX"};
    private static String[] extensions = {"pcx"};
    private static String[] mimeTypes = {
        "image/pcx", "image/x-pcx", "image/x-windows-pcx", "image/x-pc-paintbrush"
    };
    private boolean registered = false;

    public PCXImageWriterSpi() {
        super(PackageUtil.getVendor(),
              PackageUtil.getVersion(),
              formatNames,
              extensions,
              mimeTypes,
              "com.sun.media.imageioimpl.plugins.pcx.PCXImageWriter",
              STANDARD_OUTPUT_TYPE,
              readerSpiNames,
              false,
              null, null, null, null,
              true,
              null,
              null,
              null, null);
    }

    public String getDescription(Locale locale) {
	String desc = PackageUtil.getSpecificationTitle() + 
	    " PCX Image Writer";  
	return desc;
    }

    public void onRegistration(ServiceRegistry registry, Class category) {
        if (registered) {
            return;
        }
	
        registered = true;
    }

    public boolean canEncodeImage(ImageTypeSpecifier type) {
        int dataType= type.getSampleModel().getDataType();
        if (dataType < DataBuffer.TYPE_BYTE || dataType > DataBuffer.TYPE_INT)
            return false;
       
        SampleModel sm = type.getSampleModel();
        int numBands = sm.getNumBands();
        if (!(numBands == 1 || numBands == 3))
            return false;
        
        if (numBands == 1 && dataType != DataBuffer.TYPE_BYTE)
            return false;
        
        if (dataType > DataBuffer.TYPE_BYTE && !(sm instanceof SinglePixelPackedSampleModel))
            return false;
        
        return true;
    }

    public ImageWriter createWriterInstance(Object extension)
        throws IIOException {
        return new PCXImageWriter(this);
    }
}
