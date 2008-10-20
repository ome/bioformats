/*
 * $RCSfile: CLibPNGImageReader.java,v $
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
 * $Revision: 1.5 $
 * $Date: 2006/02/24 01:03:28 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.png;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import com.sun.media.imageioimpl.plugins.clib.CLibImageReader;
import com.sun.medialib.codec.png.Decoder;
import com.sun.medialib.codec.jiio.mediaLibImage;

final class CLibPNGImageReader extends CLibImageReader {
    private Decoder decoder;
    private HashMap imageTypes = new HashMap();

    CLibPNGImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    // Implement abstract method defined in superclass.
    protected final synchronized mediaLibImage decode(InputStream stream)
        throws IOException {
        try {
            decoder = new Decoder(stream);
            decoder.decode();
        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }

        /* XXX Get significant bits (sBIT chunk).
        byte[] bits = decoder.getSignificantBits();
        if(bits != null) {
            System.out.println("getSignificantBits():");
            for(int i = 0; i < bits.length; i++) {
                System.out.println((bits[i]&0xff));
            }
        }
        */

        mediaLibImage mlImage = null;
        try {
            mlImage = decoder.getImage();
        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }

        if(mlImage == null) {
            throw new IIOException(I18N.getString("CLibPNGImageReader0"));
        }

        return mlImage;
    }

    public synchronized Iterator getImageTypes(int imageIndex)
        throws IOException {
        seekToImage(imageIndex);

        ArrayList types = null;
        Integer key = new Integer(imageIndex);
        if(imageTypes.containsKey(key)) {
            types = (ArrayList)imageTypes.get(key);
        } else {
            types = new ArrayList();

            // Get the mediaLibImage from the Decoder.
            mediaLibImage image = getImage(imageIndex);

            // Get the palette.
            byte[] rgbPalette = null;
            try {
                // Note: the 'decoder' instance variable is set by
                // decode() which is called by getImage() above.
                rgbPalette = decoder.getPalette();
            } catch(Throwable t) {
                throw new IIOException("codecLib error", t);
            }

            if(rgbPalette != null) {
                // Indexed image: set up the RGB palette arrays.
                int paletteLength = rgbPalette.length/3;
                byte[] r = new byte[paletteLength];
                byte[] g = new byte[paletteLength];
                byte[] b = new byte[paletteLength];
                for(int i = 0, j = 0; i < paletteLength; i++) {
                    r[i] = rgbPalette[j++];
                    g[i] = rgbPalette[j++];
                    b[i] = rgbPalette[j++];
                }

                // Set up the alpha palette array if needed.
                int[] alphaPalette = null;
                try {
                    alphaPalette = decoder.getTransparency();
                } catch(Throwable t) {
                    throw new IIOException("codecLib error", t);
                }
                byte[] a = null;
                if(alphaPalette != null) {
                    // Load beginning of palette from the chunk
                    a = new byte[paletteLength];
                    for(int i = 0; i < alphaPalette.length; i++) {
                        a[i] = (byte)(alphaPalette[i] & 0x000000ff);
                    }

                    // Fill rest of palette with 255
                    for(int i = alphaPalette.length; i < paletteLength; i++) {
                        a[i] = (byte)0xff;
                    }
                }

                types.add(createImageType(image, null, decoder.getBitDepth(),
                                          r, g, b, a));
            } else {
                // Attempt to use the iCCP chunk if present, no sRGB
                // chunk is present, and the ICC color space type matches
                // the image type.
                ColorSpace cs = null;
                if(decoder.getStandardRGB() ==
                   Decoder.PNG_sRGB_NOT_DEFINED) {
                    // Get the profile data.
                    byte[] iccProfileData =
                        decoder.getEmbeddedICCProfile();
                    if(iccProfileData != null) {
                        // Create the ColorSpace.
                        ICC_Profile iccProfile =
                            ICC_Profile.getInstance(iccProfileData);
                        ICC_ColorSpace icccs =
                            new ICC_ColorSpace(iccProfile);

                        // Check the color space type against the
                        // number of bands and the palette.
                        int numBands = image.getChannels();
                        if((icccs.getType() == ColorSpace.TYPE_RGB &&
                            (numBands >= 3 || rgbPalette != null)) ||
                           (icccs.getType() == ColorSpace.TYPE_GRAY &&
                            numBands < 3 && rgbPalette == null)) {
                            cs = icccs;
                        }
                    }
                }

                int bitDepth = decoder.getBitDepth();

                ImageTypeSpecifier type =
                    createImageType(image, cs, bitDepth,
                                    null, null, null, null);
                types.add(type);

                if(type.getColorModel().getColorSpace().equals(cs)) {
                    types.add(createImageType(image, null, bitDepth,
                                              null, null, null, null));
                }
            }

            imageTypes.put(key, types);
        }

        // XXX Need also to use getBackground() to save the background
        // color somewhere, eventually as an image property with the
        // name "background_color" and with a java.awt.Color value.
        // See PNGImageDecoder or the PNG ImageReader for more info.
        // Looks like this needs to be set as a metadata entry. It is
        // obtained from the decoder using getBackground().

        return types.iterator();
    }

    // Override superclass method.
    protected void resetLocal() {
        decoder = null;
        imageTypes.clear();
        super.resetLocal();
    }

    public synchronized IIOMetadata getImageMetadata(int imageIndex)
        throws IIOException {
        if(input == null) {
            throw new IllegalStateException("input == null");
        }
        seekToImage(imageIndex);

        CLibPNGMetadata im = new CLibPNGMetadata();
        try {
            getImage(imageIndex);
        } catch(IOException e) {
            throw new IIOException("codecLib error", e);
        }
        im.readMetadata(this, decoder);
        return im;
    }

    /**
     * Package scope method to classes in package to emit warning messages.
     */
    void forwardWarningMessage(String warning) {
        processWarningOccurred(warning);
    }
}
