/*
 * $RCSfile: CLibJPEGImageReaderSpi.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2006/04/24 20:53:01 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.IIOException;
import com.sun.media.imageioimpl.common.PackageUtil;
import com.sun.media.imageioimpl.common.ImageUtil;

public class CLibJPEGImageReaderSpi extends ImageReaderSpi {

    private static final String[] names =
    {"jpeg", "JPEG", "jpg", "JPG", "jfif", "JFIF",
     "jpeg-lossless", "JPEG-LOSSLESS", "jpeg-ls", "JPEG-LS"};

    private static final String[] suffixes = {"jpeg", "jpg", "jfif", "jls"};
    
    private static final String[] MIMETypes = {"image/jpeg"};

    private static final String readerClassName =
        "com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReader";

    private static final String[] writerSpiNames = {
        "com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageWriterSpi"
    };

    private boolean registered = false;

    public CLibJPEGImageReaderSpi() {
        super(PackageUtil.getVendor(),
              PackageUtil.getVersion(),
              names,
              suffixes,
              MIMETypes,
              readerClassName,
              STANDARD_INPUT_TYPE,
              writerSpiNames,
              false, // supportsStandardStreamMetadataFormat
              null,  // nativeStreamMetadataFormatName
              null,  // nativeStreamMetadataFormatClassName
              null,  // extraStreamMetadataFormatNames
              null,  // extraStreamMetadataFormatClassNames
              true,  // supportsStandardImageMetadataFormat
              CLibJPEGMetadata.NATIVE_FORMAT,
              CLibJPEGMetadata.NATIVE_FORMAT_CLASS,
              new String[] {CLibJPEGMetadata.TIFF_FORMAT},
              new String[] {CLibJPEGMetadata.TIFF_FORMAT_CLASS});
    }

    public void onRegistration(ServiceRegistry registry,
                               Class category) {
        if (registered) {
            return;
        }
	
        registered = true;

        // Branch as a function of codecLib availability.
        if(!PackageUtil.isCodecLibAvailable()) {
            // Deregister provider.
            registry.deregisterServiceProvider(this);
        } else {

	    List list = 
		ImageUtil.getJDKImageReaderWriterSPI(registry, "JPEG", true);

	    for (int i=0; i<list.size(); i++) {
		// Set pairwise ordering to give codecLib reader precedence
		// over Sun core J2SE reader.
		registry.setOrdering(category, this, list.get(i));
	    }
        }
    }

    public String getDescription(Locale locale) {
	String desc = PackageUtil.getSpecificationTitle() + 
	    " natively-accelerated JPEG Image Reader";
	return desc;
    }

    public boolean canDecodeInput(Object source) throws IOException {
        if (!(source instanceof ImageInputStream)) {
            return false;
        }
        ImageInputStream iis = (ImageInputStream) source;
        iis.mark();
        // If the first two bytes are a JPEG SOI marker, it's probably
        // a JPEG file.  If they aren't, it definitely isn't a JPEG file.
        int byte1 = iis.read();
        int byte2 = iis.read();
        if ((byte1 != 0xFF) || (byte2 != 0xD8)) {
            iis.reset();
            return false;
        }
        do {
            byte1 = iis.read();
            byte2 = iis.read();
            if (byte1 != 0xFF) break; // something wrong, but probably readable
            if (byte2 == 0xDA) break; // Start of scan
            if (byte2 == 0xC2) { // progressive mode, can't decode
                iis.reset();
                return false;
            }
            if ((byte2 >= 0xC0) && (byte2 <= 0xC3)) // not progressive, can decode
                break;
            int length = iis.read() << 8;
            length += iis.read();
            length -= 2;
	    while (length > 0) length -= iis.skipBytes(length);
        } while(true);
        iis.reset();
        return true;
    }
    
    public ImageReader createReaderInstance(Object extension) 
        throws IIOException {
        return new CLibJPEGImageReader(this);
    }
}
