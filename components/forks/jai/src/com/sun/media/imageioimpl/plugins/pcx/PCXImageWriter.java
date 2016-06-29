/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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
 * $RCSfile: PCXImageWriter.java,v $
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
 * $Revision: 1.2 $
 * $Date: 2007/09/11 20:45:42 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.pcx;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import com.sun.media.imageioimpl.common.ImageUtil;

public class PCXImageWriter extends ImageWriter implements PCXConstants {

    private ImageOutputStream ios;
    private Rectangle sourceRegion;
    private Rectangle destinationRegion;
    private int colorPlanes,bytesPerLine;
    private Raster inputRaster = null;
    private int scaleX,scaleY;

    public PCXImageWriter(PCXImageWriterSpi imageWriterSpi) {
	super(imageWriterSpi);
    }

    public void setOutput(Object output) {
	super.setOutput(output); // validates output
	if (output != null) {
	    if (!(output instanceof ImageOutputStream))
		throw new IllegalArgumentException("output not instance of ImageOutputStream");
	    ios = (ImageOutputStream) output;
	    ios.setByteOrder(ByteOrder.LITTLE_ENDIAN);
	} else
	    ios = null;
    }

    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
	if(inData instanceof PCXMetadata)
	    return inData;
	return null;
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
	return null;
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
	PCXMetadata md = new PCXMetadata();
	md.bitsPerPixel = (byte) imageType.getSampleModel().getSampleSize()[0];
	return md;
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
	return null;
    }

    public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
	if (ios == null) {
	    throw new IllegalStateException("output stream is null");
	}

	if (image == null) {
	    throw new IllegalArgumentException("image is null");
	}

	clearAbortRequest();
	processImageStarted(0);
	if (param == null)
	    param = getDefaultWriteParam();

        boolean writeRaster = image.hasRaster();
        
        sourceRegion = param.getSourceRegion();
        
        SampleModel sampleModel = null;
        ColorModel colorModel = null;

        if (writeRaster) {
            inputRaster = image.getRaster();
            sampleModel = inputRaster.getSampleModel();
            colorModel = ImageUtil.createColorModel(null, sampleModel);
            if (sourceRegion == null)
                sourceRegion = inputRaster.getBounds();
            else
                sourceRegion = sourceRegion.intersection(inputRaster.getBounds());
        } else {
            RenderedImage input = image.getRenderedImage();
            inputRaster = input.getData();
            sampleModel = input.getSampleModel();
            colorModel = input.getColorModel();
            Rectangle rect = new Rectangle(input.getMinX(), input.getMinY(),
                                           input.getWidth(), input.getHeight());
            if (sourceRegion == null)
                sourceRegion = rect;
            else
                sourceRegion = sourceRegion.intersection(rect);
        }
	
	if (sourceRegion.isEmpty())
	    throw new IllegalArgumentException("source region is empty");

        IIOMetadata imageMetadata = image.getMetadata();
        PCXMetadata pcxImageMetadata = null;
        
        ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
        if(imageMetadata != null) {
            // Convert metadata.
            pcxImageMetadata = (PCXMetadata)convertImageMetadata(imageMetadata, imageType, param);
        } else {
            // Use default.
            pcxImageMetadata = (PCXMetadata)getDefaultImageMetadata(imageType, param);
        }

	scaleX = param.getSourceXSubsampling();
	scaleY = param.getSourceYSubsampling();
	
	int xOffset = param.getSubsamplingXOffset();
	int yOffset = param.getSubsamplingYOffset();

	// cache the data type;
	int dataType = sampleModel.getDataType();

	sourceRegion.translate(xOffset, yOffset);
	sourceRegion.width -= xOffset;
	sourceRegion.height -= yOffset;

	int minX = sourceRegion.x / scaleX;
	int minY = sourceRegion.y / scaleY;
	int w = (sourceRegion.width + scaleX - 1) / scaleX;
	int h = (sourceRegion.height + scaleY - 1) / scaleY;
	
	xOffset = sourceRegion.x % scaleX;
	yOffset = sourceRegion.y % scaleY;

	destinationRegion = new Rectangle(minX, minY, w, h);
	
	boolean noTransform = destinationRegion.equals(sourceRegion);

	// Raw data can only handle bytes, everything greater must be ASCII.
	int[] sourceBands = param.getSourceBands();
	boolean noSubband = true;
	int numBands = sampleModel.getNumBands();

	if (sourceBands != null) {
	    sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
	    colorModel = null;
	    noSubband = false;
	    numBands = sampleModel.getNumBands();
	} else {
	    sourceBands = new int[numBands];
	    for (int i = 0; i < numBands; i++)
		sourceBands[i] = i;
	}
	
	ios.writeByte(MANUFACTURER);
	ios.writeByte(VERSION_3_0);
	ios.writeByte(ENCODING);
	
	int bitsPerPixel = sampleModel.getSampleSize(0);
	ios.writeByte(bitsPerPixel);
	
	ios.writeShort(destinationRegion.x); // xmin
	ios.writeShort(destinationRegion.y); // ymin
	ios.writeShort(destinationRegion.x+destinationRegion.width-1); // xmax
	ios.writeShort(destinationRegion.y+destinationRegion.height-1); // ymax
	
	ios.writeShort(pcxImageMetadata.hdpi);
	ios.writeShort(pcxImageMetadata.vdpi);
	
	byte[] smallpalette = createSmallPalette(colorModel);
	ios.write(smallpalette);
	ios.writeByte(0); // reserved
	
	colorPlanes = sampleModel.getNumBands();
	
	ios.writeByte(colorPlanes);
	
	bytesPerLine = destinationRegion.width*bitsPerPixel/8;
	bytesPerLine += bytesPerLine %2;
	
	ios.writeShort(bytesPerLine);
	
	if(colorModel.getColorSpace().getType()==ColorSpace.TYPE_GRAY)
	    ios.writeShort(PALETTE_GRAYSCALE);
	else
	    ios.writeShort(PALETTE_COLOR);
	
	ios.writeShort(pcxImageMetadata.hsize);
	ios.writeShort(pcxImageMetadata.vsize);
	
	for(int i=0;i<54;i++)
	    ios.writeByte(0);
	
	// write image data
	
	if(colorPlanes==1 && bitsPerPixel==1) {
	    write1Bit();
	}
	else if(colorPlanes==1 && bitsPerPixel==4) {
	    write4Bit();
	}
	else {
	    write8Bit();
	}
	
	// write 256 color palette if needed
	if(colorPlanes==1 && bitsPerPixel==8 &&
           colorModel.getColorSpace().getType()!=ColorSpace.TYPE_GRAY){
            ios.writeByte(12); // Magic number preceding VGA 256 Color Palette Information
	    ios.write(createLargePalette(colorModel));
	}
	
        if (abortRequested()) {
            processWriteAborted();
        } else {
            processImageComplete();
        }
    }
    
    private void write4Bit() throws IOException {
	int[] unpacked = new int[sourceRegion.width];
	int[] samples = new int[bytesPerLine];

	for (int line = 0; line < sourceRegion.height; line += scaleY) {
	    inputRaster.getSamples(sourceRegion.x, line + sourceRegion.y, sourceRegion.width, 1, 0, unpacked);
	    
	    int val=0,dst=0;
	    for(int x=0,nibble=0;x<sourceRegion.width;x+=scaleX){
		val = val | (unpacked[x] & 0x0F);
		if(nibble==1) {
		    samples[dst++]=val;
		    nibble=0;
		    val=0;
		} else {
		    nibble=1;
		    val = val << 4;
		}
	    }

	    int last = samples[0];
	    int count = 0;

	    for (int x = 0; x < bytesPerLine; x += scaleX) {
		int sample = samples[x];
		if (sample != last || count == 63) {
		    writeRLE(last, count);
		    count = 1;
		    last = sample;
		} else
		    count++;
	    }
	    if (count >= 1) {
		writeRLE(last, count);
	    }

            processImageProgress(100.0F * line / sourceRegion.height);
	}
    }
    
    private void write1Bit() throws IOException {
	int[] unpacked = new int[sourceRegion.width];
	int[] samples = new int[bytesPerLine];

	for (int line = 0; line < sourceRegion.height; line += scaleY) {
	    inputRaster.getSamples(sourceRegion.x, line + sourceRegion.y, sourceRegion.width, 1, 0, unpacked);
	    
	    int val=0,dst=0;
	    for(int x=0,bit=1<<7;x<sourceRegion.width;x+=scaleX){
		if(unpacked[x]>0)
		    val = val | bit;
		if(bit==1) {
		    samples[dst++]=val;
		    bit=1<<7;
		    val=0;
		} else {
		    bit = bit >> 1;
		}
	    }

	    int last = samples[0];
	    int count = 0;

	    for (int x = 0; x < bytesPerLine; x += scaleX) {
		int sample = samples[x];
		if (sample != last || count == 63) {
		    writeRLE(last, count);
		    count = 1;
		    last = sample;
		} else
		    count++;
	    }
	    if (count >= 1) {
		writeRLE(last, count);
	    }

            processImageProgress(100.0F * line / sourceRegion.height);
	}
    }
    
    private void write8Bit() throws IOException {
	int[][] samples = new int[colorPlanes][bytesPerLine];
	
	for(int line=0;line<sourceRegion.height;line+=scaleY) {
	    for(int band=0;band<colorPlanes;band++) {
		inputRaster.getSamples(sourceRegion.x, line+sourceRegion.y,sourceRegion.width,1,band,samples[band]);
	    }
	    
	    int last = samples[0][0];
	    int count=0;
	    
	    for(int band=0;band<colorPlanes;band++) {
		for(int x=0;x<bytesPerLine;x+=scaleX) {
		    int sample = samples[band][x];
		    if(sample!=last || count==63) {
			writeRLE(last,count);
			count=1;
			last=sample;
		    } else
			count++;
		}
	    }
	    if(count>=1) {
		writeRLE(last,count);
	    }

            processImageProgress(100.0F * line / sourceRegion.height);
	}
    }
    
    private void writeRLE(int val,int count) throws IOException{
	if(count==1 && (val & 0xC0) != 0xC0) {
	    ios.writeByte(val);
	} else {
	    ios.writeByte(0xC0 | count);
	    ios.writeByte(val);
	}
    }
    
    private byte[] createSmallPalette(ColorModel cm){
	byte[] palette = new byte[16*3];
	
	if(!(cm instanceof IndexColorModel))
	    return palette;
	
	IndexColorModel icm = (IndexColorModel) cm;
	if(icm.getMapSize()>16)
	    return palette;
	
	for(int i=0,offset=0;i<icm.getMapSize();i++) {
	    palette[offset++] = (byte) icm.getRed(i);
	    palette[offset++] = (byte) icm.getGreen(i);
	    palette[offset++] = (byte) icm.getBlue(i);
	}
	
	return palette;
    }
    private byte[] createLargePalette(ColorModel cm){
	byte[] palette = new byte[256*3];
	
	if(!(cm instanceof IndexColorModel))
	    return palette;
	
	IndexColorModel icm = (IndexColorModel) cm;
	
	for(int i=0,offset=0;i<icm.getMapSize();i++) {
	    palette[offset++] = (byte) icm.getRed(i);
	    palette[offset++] = (byte) icm.getGreen(i);
	    palette[offset++] = (byte) icm.getBlue(i);
	}
	
	return palette;
    }
}
