/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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
 * $RCSfile: J2KImageWriterSpi.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
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
 * $Revision: 1.2 $
 * $Date: 2006/03/31 19:43:39 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;

import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.spi.IIORegistry;
import javax.imageio.ImageWriter;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.IIOException;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.util.Locale;

import com.sun.media.imageioimpl.common.PackageUtil;

public class J2KImageWriterSpi extends ImageWriterSpi {
    private static String [] readerSpiNames =
        {"com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReaderSpi"};
    private static String[] formatNames =
        {"jpeg 2000", "JPEG 2000", "jpeg2000", "JPEG2000"};
    private static String[] extensions =
        {"jp2"}; // Should add jpx or jpm
    private static String[] mimeTypes = {"image/jp2", "image/jpeg2000"};
    private boolean registered = false;

    public J2KImageWriterSpi() {
        super(PackageUtil.getVendor(),
              PackageUtil.getVersion(),
              formatNames,
              extensions,
              mimeTypes,
              "com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriter",
              STANDARD_OUTPUT_TYPE,
              readerSpiNames,
              false,
              null, null,
              null, null,
              true,
              "com_sun_media_imageio_plugins_jpeg2000_image_1.0",
              "com.sun.media.imageioimpl.plugins.jpeg2000.J2KMetadataFormat",
              null, null);
    }

    public String getDescription(Locale locale) {
	String desc = PackageUtil.getSpecificationTitle() + 
	    " JPEG 2000 Image Writer";
	return desc;
    }

    public void onRegistration(ServiceRegistry registry,
                               Class category) {
        if (registered) {
            return;
        }
	
        registered = true;

        // Set pairwise ordering to give codecLib writer precedence.
        Class codecLibWriterSPIClass = null;
        try {
            codecLibWriterSPIClass =
                Class.forName("com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriterCodecLibSpi");
        } catch(Throwable t) {
            // Ignore it.
        }

        if(codecLibWriterSPIClass != null) {
            Object codecLibWriterSPI =
                registry.getServiceProviderByClass(codecLibWriterSPIClass);
            if(codecLibWriterSPI != null) {
                registry.setOrdering(category, codecLibWriterSPI, this);
            }
        }
    }

    public boolean canEncodeImage(ImageTypeSpecifier type) {
        SampleModel sm = type.getSampleModel();
        if (sm.getNumBands() > 16384)
            return false;
        if (sm.getDataType() < DataBuffer.TYPE_BYTE ||
            sm.getDataType() > DataBuffer.TYPE_INT)
            return false;
        return true;
    }

    public ImageWriter createWriterInstance(Object extension)
        throws IIOException {
        return new J2KImageWriter(this);
    }
}
