/*
 * $RCSfile: CLibJPEGImageWriterSpi.java,v $
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
 * $Date: 2006/04/26 00:45:06 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageWriter;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import com.sun.media.imageioimpl.common.PackageUtil;
import com.sun.media.imageioimpl.common.ImageUtil;

/**
 */
public class CLibJPEGImageWriterSpi extends ImageWriterSpi {

    private static final String[] names =
    {"jpeg", "JPEG", "jpg", "JPG", "jfif", "JFIF",
     "jpeg-lossless", "JPEG-LOSSLESS", "jpeg-ls", "JPEG-LS"};

    private static final String[] suffixes = {"jpeg", "jpg", "jfif", "jls"};
    
    private static final String[] MIMETypes = { "image/jpeg" };
    
    private static final String writerClassName =
        "com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageWriter";

    private static final String[] readerSpiNames = {
        "com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReaderSpi"
    };

    private boolean registered = false;

    public CLibJPEGImageWriterSpi() {
        super(PackageUtil.getVendor(),
              PackageUtil.getVersion(),
              names,
              suffixes,
              MIMETypes,
              writerClassName,
              STANDARD_OUTPUT_TYPE,
              readerSpiNames,
              false,
              null, null,
              null, null,
              false,
              null, null,
              null, null);
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
		ImageUtil.getJDKImageReaderWriterSPI(registry, "JPEG", false);

	    for (int i=0; i<list.size(); i++) {
		// Set pairwise ordering to give codecLib writer precedence
		// over Sun core J2SE writer.
		registry.setOrdering(category, this, list.get(i));
	    }
        }
    }

    public boolean canEncodeImage(ImageTypeSpecifier type) {
        ColorModel colorModel = type.getColorModel();

        if (colorModel instanceof IndexColorModel) {
            // No need to check further: writer converts to 8-8-8 RGB.
            return true;
        }

        SampleModel sampleModel = type.getSampleModel();

        // Ensure all channels have the same bit depth
        int bitDepth;
        if(colorModel != null) {
            int[] componentSize = colorModel.getComponentSize(); 
            bitDepth = componentSize[0];
            for (int i = 1; i < componentSize.length; i++) {
                if (componentSize[i] != bitDepth) {
                    return false;
                }
            }
        } else {
            int[] sampleSize = sampleModel.getSampleSize(); 
            bitDepth = sampleSize[0];
            for (int i = 1; i < sampleSize.length; i++) {
                if (sampleSize[i] != bitDepth) {
                    return false;
                }
            }
        }

        // Ensure bitDepth is no more than 16
        if (bitDepth > 16) {
            return false;
        }

        // Check number of bands.
        int numBands = sampleModel.getNumBands();
        if (numBands < 1 || numBands > 4) {
            return false;
        }

        return true;
    }

    public String getDescription(Locale locale) {
	String desc = PackageUtil.getSpecificationTitle() + 
	    " natively-accelerated JPEG Image Writer";
	return desc;
    }

    public ImageWriter createWriterInstance(Object extension)
        throws IOException {
        return new CLibJPEGImageWriter(this);
    }
}
