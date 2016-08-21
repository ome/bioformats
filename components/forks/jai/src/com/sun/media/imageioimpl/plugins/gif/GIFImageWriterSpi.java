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
 * $RCSfile: GIFImageWriterSpi.java,v $
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
 * $Revision: 1.3 $
 * $Date: 2006/03/31 19:43:39 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.gif;

import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import com.sun.media.imageioimpl.common.PaletteBuilder;
import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.media.imageioimpl.common.PackageUtil;

public class GIFImageWriterSpi extends ImageWriterSpi {

    private static final String vendorName = "Sun Microsystems, Inc.";

    private static final String version = "1.0";

    private static final String[] names = { "gif", "GIF" };

    private static final String[] suffixes = { "gif" };

    private static final String[] MIMETypes = { "image/gif" };

    private static final String writerClassName =
    "com.sun.media.imageioimpl.plugins.gif.GIFImageWriter";

    private static final String[] readerSpiNames = {
        "com.sun.imageio.plugins.gif.GIFImageReaderSpi"
    };

    private boolean registered = false;

    public GIFImageWriterSpi() {
        super(vendorName,
              version,
              names,
              suffixes,
              MIMETypes,
              writerClassName,
              STANDARD_OUTPUT_TYPE,
              readerSpiNames,
              true,
              GIFWritableStreamMetadata.NATIVE_FORMAT_NAME,
              "com.sun.media.imageioimpl.plugins.gif.GIFStreamMetadataFormat",
              null, null,
              true,
              GIFWritableImageMetadata.NATIVE_FORMAT_NAME,
              "com.sun.media.imageioimpl.plugins.gif.GIFStreamMetadataFormat",
              null, null
              );
    }

    public boolean canEncodeImage(ImageTypeSpecifier type) {
        if (type == null) {
            throw new IllegalArgumentException("type == null!");
        }

        SampleModel sm = type.getSampleModel();
        ColorModel cm = type.getColorModel();

        boolean canEncode = sm.getNumBands() == 1 &&
            sm.getSampleSize(0) <= 8 &&
            sm.getWidth() <= 65535 &&
            sm.getHeight() <= 65535 &&
            (cm == null || cm.getComponentSize()[0] <= 8);

        if (canEncode) {
            return true;
        } else {
            return PaletteBuilder.canCreatePalette(type);
        }
    }

    public String getDescription(Locale locale) {
	String desc = PackageUtil.getSpecificationTitle() + 
	    " GIF Image Writer";  
	return desc;
    }

    public void onRegistration(ServiceRegistry registry,
                               Class category) {
        if (registered) {
            return;
        }
	
        registered = true;
	
	// By JDK 1.8, the GIFImageWriter will have been in JDK core for 
	// atleast two FCS releases, so we can set JIIO's to lower priority
	// With JDK 1.9, we can entirely de-register the JIIO one
	ImageUtil.processOnRegistration(registry, category, "GIF", this,
					9, 8); // JDK version 1.9, 1.8
    }

    public ImageWriter createWriterInstance(Object extension) {
        return new GIFImageWriter(this);
    }
}
