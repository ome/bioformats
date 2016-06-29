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
 * $RCSfile: TIFFRenderedImage.java,v $
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
 * $Date: 2006/02/22 22:06:23 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import com.sun.media.imageio.plugins.tiff.TIFFImageReadParam;
import com.sun.media.imageio.plugins.tiff.TIFFTagSet;

public class TIFFRenderedImage implements RenderedImage {

    TIFFImageReader reader;
    int imageIndex;
    ImageReadParam tileParam;

    int subsampleX;
    int subsampleY;

    boolean isSubsampling;

    int width;
    int height;
    int tileWidth;
    int tileHeight;

    ImageTypeSpecifier its;

    public TIFFRenderedImage(TIFFImageReader reader,
                             int imageIndex,
                             ImageReadParam readParam,
                             int width, int height) throws IOException {
        this.reader = reader;
        this.imageIndex = imageIndex;
        this.tileParam = cloneImageReadParam(readParam, false);

        this.subsampleX = tileParam.getSourceXSubsampling();
        this.subsampleY = tileParam.getSourceYSubsampling();

        this.isSubsampling = this.subsampleX != 1 || this.subsampleY != 1;

        this.width = width/subsampleX;
        this.height = height/subsampleY;

        // If subsampling is being used, we may not match the
        // true tile grid exactly, but everything should still work
        this.tileWidth = reader.getTileWidth(imageIndex)/subsampleX;
        this.tileHeight = reader.getTileHeight(imageIndex)/subsampleY;
        
        Iterator iter = reader.getImageTypes(imageIndex);
        this.its = (ImageTypeSpecifier)iter.next();
        tileParam.setDestinationType(its);
    }

    /**
     * Creates a copy of <code>param</code>. The source subsampling and
     * and bands settings and the destination bands and offset settings
     * are copied. If <code>param</code> is a <code>TIFFImageReadParam</code>
     * then the <code>TIFFDecompressor</code> and
     * <code>TIFFColorConverter</code> settings are also copied; otherwise
     * they are explicitly set to <code>null</code>.
     *
     * @param param the parameters to be copied.
     * @param copyTagSets whether the <code>TIFFTagSet</code> settings
     * should be copied if set.
     * @return copied parameters.
     */
    private ImageReadParam cloneImageReadParam(ImageReadParam param,
                                               boolean copyTagSets) {
        // Create a new TIFFImageReadParam.
        TIFFImageReadParam newParam = new TIFFImageReadParam();

        // Copy the basic settings.
        newParam.setSourceSubsampling(param.getSourceXSubsampling(),
                                      param.getSourceYSubsampling(),
                                      param.getSubsamplingXOffset(),
                                      param.getSubsamplingYOffset());
        newParam.setSourceBands(param.getSourceBands());
        newParam.setDestinationBands(param.getDestinationBands());
        newParam.setDestinationOffset(param.getDestinationOffset());

        // Set the decompressor and color converter.
        if(param instanceof TIFFImageReadParam) {
            // Copy the settings from the input parameter.
            TIFFImageReadParam tparam = (TIFFImageReadParam)param;
            newParam.setTIFFDecompressor(tparam.getTIFFDecompressor());
            newParam.setColorConverter(tparam.getColorConverter());

            if(copyTagSets) {
                List tagSets = tparam.getAllowedTagSets();
                if(tagSets != null) {
                    Iterator tagSetIter = tagSets.iterator();
                    if(tagSetIter != null) {
                        while(tagSetIter.hasNext()) {
                            TIFFTagSet tagSet = (TIFFTagSet)tagSetIter.next();
                            newParam.addAllowedTagSet(tagSet);
                        }
                    }
                }
            }
        } else {
            // Set the decompressor and color converter to null.
            newParam.setTIFFDecompressor(null);
            newParam.setColorConverter(null);
        }

        return newParam;
    }

    public Vector getSources() {
        return null;
    }

    public Object getProperty(String name) {
        return java.awt.Image.UndefinedProperty;
    }

    public String[] getPropertyNames() {
        return null;
    }

    public ColorModel getColorModel() {
        return its.getColorModel();
    }

    public SampleModel getSampleModel() {
        return its.getSampleModel();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMinX() {
        return 0;
    }

    public int getMinY() {
        return 0;
    }

    public int getNumXTiles() {
        return (width + tileWidth - 1)/tileWidth;
    }

    public int getNumYTiles() {
        return (height + tileHeight - 1)/tileHeight;
    }

    public int getMinTileX() {
        return 0;
    }

    public int getMinTileY() {
        return 0;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileGridXOffset() {
        return 0;
    }

    public int getTileGridYOffset() {
        return 0;
    }

    public Raster getTile(int tileX, int tileY) {
        Rectangle tileRect = new Rectangle(tileX*tileWidth,
                                           tileY*tileHeight,
                                           tileWidth,
                                           tileHeight);
        return getData(tileRect);
    }

    public Raster getData() {
        return read(new Rectangle(0, 0, getWidth(), getHeight()));
    }

    public Raster getData(Rectangle rect) {
        return read(rect);
    }

    // This method needs to be synchronized as it updates the instance
    // variable 'tileParam'.
    public synchronized WritableRaster read(Rectangle rect) {
        // XXX Does this need to consider the subsampling offsets or is
        // that handled implicitly by the reader?
        tileParam.setSourceRegion(isSubsampling ?
                                  new Rectangle(subsampleX*rect.x,
                                                subsampleY*rect.y,
                                                subsampleX*rect.width,
                                                subsampleY*rect.height) :
                                  rect);

        try {
            BufferedImage bi = reader.read(imageIndex, tileParam);
            WritableRaster ras = bi.getRaster();
            return ras.createWritableChild(0, 0,
                                           ras.getWidth(), ras.getHeight(),
                                           rect.x, rect.y,
                                           null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public WritableRaster copyData(WritableRaster raster) {
        if (raster == null) {
            return read(new Rectangle(0, 0, getWidth(), getHeight()));
        } else {
            Raster src = read(raster.getBounds());
            raster.setRect(src);
            return raster;
        }
    }
}
