/*
 * $RCSfile: CLibJPEGImageReader.java,v $
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
 * $Revision: 1.10 $
 * $Date: 2006/04/24 20:53:01 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import com.sun.media.imageioimpl.common.InvertedCMYKColorSpace;
import com.sun.media.imageioimpl.plugins.clib.CLibImageReader;
import com.sun.media.imageioimpl.plugins.clib.InputStreamAdapter;
import com.sun.medialib.codec.jpeg.Decoder;
import com.sun.medialib.codec.jiio.mediaLibImage;

final class CLibJPEGImageReader extends CLibImageReader {
    private static final boolean DEBUG = false; // XXX false for release

    private mediaLibImage infoImage = null;
    private int infoImageIndex = -1;
    private byte[] iccProfileData = null;
    private IIOMetadata imageMetadata = null;
    private int imageMetadataIndex = -1;
    private HashMap imageTypes = new HashMap();
    private int bitDepth; // XXX Should depend on imageIndex.

    CLibJPEGImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    // Implement abstract method defined in superclass.
    protected final synchronized mediaLibImage decode(InputStream stream)
        throws IOException {
        if(DEBUG) System.out.println("In decode()");

        mediaLibImage mlImage = null;
        Decoder decoder = null;
        try {
            if(stream instanceof InputStreamAdapter) {
                ImageInputStream iis =
                    ((InputStreamAdapter)stream).getWrappedStream();
                decoder = new Decoder(iis);
            } else {
                decoder = new Decoder(stream);
            }
            //decoder.setType(Decoder.JPEG_TYPE_UNKNOWN);
            mlImage = decoder.decode(null);

            // Set the ICC profile data.
            iccProfileData = decoder.getEmbeddedICCProfile();

            // If there is a profile need to invert the data if they
            // are YCCK or CMYK originally.
            if(iccProfileData != null &&
               mlImage.getType() == mediaLibImage.MLIB_BYTE) {
                int format = mlImage.getFormat();
                if(format == mediaLibImage.MLIB_FORMAT_CMYK ||
                   format == mediaLibImage.MLIB_FORMAT_YCCK) {
                    long t0 = System.currentTimeMillis();
                    byte[] data = mlImage.getByteData();
                    int len = data.length;
                    for(int i = mlImage.getOffset(); i < len; i++) {
                        data[i] = (byte)(255 - data[i]&0xff);
                    }
                }
            }

        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }

        if(mlImage == null) {
            throw new IIOException(I18N.getString("CLibJPEGImageReader0"));
        }

        // Set variable indicating bit depth.
        try {
            bitDepth = decoder.getDepth();
        } catch(Throwable t) {
            throw new IIOException("codecLib error", t);
        }

        // Free native resources.
        decoder.dispose();

        if(DEBUG) {
            System.out.println("type = "+mlImage.getType());
            System.out.println("channels = "+mlImage.getChannels());
            System.out.println("width = "+mlImage.getWidth());
            System.out.println("height = "+mlImage.getHeight());
            System.out.println("stride = "+mlImage.getStride());
            System.out.println("offset = "+mlImage.getOffset());
            System.out.println("bitOffset = "+mlImage.getBitOffset());
            System.out.println("format = "+mlImage.getFormat());
        }

        return mlImage;
    }

    // Retrieve mediaLibImage containing everything except possibly the
    // decoded image data. If the real image has already been decoded
    // then it will be returned.
    private synchronized mediaLibImage getInfoImage(int imageIndex)
        throws IOException {
        if(DEBUG) System.out.println("In getInfoImage()");
        if(infoImage == null || imageIndex != infoImageIndex) {
            // Use the cached image if it has the correct index.
            if(imageIndex == getImageIndex()) {
                if(DEBUG) {
                    System.out.println("Using cached image.");
                }
                infoImage = getImage(imageIndex);
                infoImageIndex = imageIndex;
                return infoImage;
            }

            if(input == null) {
                throw new IllegalStateException("input == null");
            }

            // Check the input and set local variable.
            ImageInputStream iis = null;
            if(input instanceof ImageInputStream) {
                iis = (ImageInputStream)input;
            } else {
                throw new IllegalArgumentException
                    ("!(input instanceof ImageInputStream)");
            }

            seekToImage(imageIndex);

            // Mark the input.
            iis.mark();

            Decoder decoder = null;
            try {
                // Create the decoder
                decoder = new Decoder(iis);

                // Set the informational image.
                infoImage = decoder.getSize();

                // Set the ICC profile data.
                iccProfileData = decoder.getEmbeddedICCProfile();
            } catch(Throwable t) {
                throw new IIOException("codecLib error", t);
            }

            // XXX The lines marked "XXX" are a workaround for getSize()
            // not correctly setting the format of infoImage.
            if(infoImage == null ||
               (infoImage.getFormat() == // XXX
                mediaLibImage.MLIB_FORMAT_UNKNOWN && // XXX
                ((infoImage = getImage(imageIndex)) == null))) { // XXX
                throw new IIOException(I18N.getString("CLibJPEGImageReader0"));
            }

            infoImageIndex = imageIndex;

            try {
                // Set variable indicating bit depth.
                bitDepth = decoder.getDepth();
            } catch(Throwable t) {
                throw new IIOException("codecLib error", t);
            }

            // Reset the input to the marked position.
            iis.reset();

            // Free native resources.
            decoder.dispose();

            if(DEBUG) {
                System.out.println("type = "+infoImage.getType());
                System.out.println("channels = "+infoImage.getChannels());
                System.out.println("width = "+infoImage.getWidth());
                System.out.println("height = "+infoImage.getHeight());
                System.out.println("stride = "+infoImage.getStride());
                System.out.println("offset = "+infoImage.getOffset());
                System.out.println("bitOffset = "+infoImage.getBitOffset());
                System.out.println("format = "+infoImage.getFormat());
            }
        }

        return infoImage;
    }

    public int getWidth(int imageIndex) throws IOException {
        if(DEBUG) System.out.println("In getWidth()");

        return getInfoImage(imageIndex).getWidth();
    }

    public int getHeight(int imageIndex) throws IOException {
        if(DEBUG) System.out.println("In getHeight()");

        return getInfoImage(imageIndex).getHeight();
    }

    public Iterator getImageTypes(int imageIndex) throws IOException {
        if(DEBUG) System.out.println("In getImageTypes()");
        seekToImage(imageIndex);

        ArrayList types = null;
        synchronized(imageTypes) {
            Integer key = new Integer(imageIndex);
            if(imageTypes.containsKey(key)) {
                types = (ArrayList)imageTypes.get(key);
            } else {
                types = new ArrayList();

                // Get the informational image.
                mediaLibImage mlImage = getInfoImage(imageIndex);

                ColorSpace cs;

                // Add profile-based type if an ICC profile is present.
                if(iccProfileData != null) {
                    ICC_Profile profile =
                        ICC_Profile.getInstance(iccProfileData);
                    cs = new ICC_ColorSpace(profile);
                    types.add(createImageType(mlImage, cs, bitDepth,
                                              null, null, null, null));
                }

                // Add a standard type.
                cs = mlImage.getFormat() == mediaLibImage.MLIB_FORMAT_CMYK ?
                    InvertedCMYKColorSpace.getInstance() : null;
                types.add(createImageType(mlImage, cs, bitDepth,
                                          null, null, null, null));
            }
        }

        return types.iterator();
    }

    public synchronized IIOMetadata getImageMetadata(int imageIndex)
        throws IOException {
        if(input == null) {
            throw new IllegalStateException("input == null");
        }

        if(imageMetadata == null || imageIndex != imageMetadataIndex) {
            seekToImage(imageIndex);

            ImageInputStream stream = (ImageInputStream)input;
            long pos = stream.getStreamPosition();

            try {
                imageMetadata = new CLibJPEGMetadata(stream);
                imageMetadataIndex = imageIndex;
            } catch(IIOException e) {
                throw e;
            } finally {
                stream.seek(pos);
            }
        }

        return imageMetadata;
    }

    // Override thumbnail methods.

    public boolean readerSupportsThumbnails() {
        return true;
    }

    public int getNumThumbnails(int imageIndex) throws IOException {
        CLibJPEGMetadata metadata =
            (CLibJPEGMetadata)getImageMetadata(imageIndex);
        return metadata.getNumThumbnails();
    }

    public BufferedImage readThumbnail(int imageIndex,
                                       int thumbnailIndex) throws IOException {
        CLibJPEGMetadata metadata =
            (CLibJPEGMetadata)getImageMetadata(imageIndex);
        return metadata.getThumbnail(thumbnailIndex);
    }

    // Override superclass method.
    protected void resetLocal() {
        infoImage = null;
        infoImageIndex = -1;
        iccProfileData = null;
        imageMetadata = null;
        imageMetadataIndex = -1;
        imageTypes.clear();
        super.resetLocal();
    }
}
