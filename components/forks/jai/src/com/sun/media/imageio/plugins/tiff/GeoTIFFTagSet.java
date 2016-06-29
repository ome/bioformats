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
 * $RCSfile: GeoTIFFTagSet.java,v $
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
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:17 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the tags found in a GeoTIFF IFD.  GeoTIFF is a
 * standard for annotating georeferenced or geocoded raster imagery.
 * The GeoTIFF specification may be found at <a
 * href="http://www.remotesensing.org/geotiff/spec/geotiffhome.html">
 * <code>http://www.remotesensing.org/geotiff/spec/geotiffhome.html</code>
 * </a>. This class does <i>not</i> handle the <i>GeoKey</i>s referenced
 * from a <i>GeoKeyDirectoryTag</i> as those are not TIFF tags per se.
 *
 * <p>The definitions of the data types referenced by the field
 * definitions may be found in the {@link TIFFTag
 * <code>TIFFTag</code>} class.</p>
 */
public class GeoTIFFTagSet extends TIFFTagSet {

    private static GeoTIFFTagSet theInstance = null;

    /**
     * A tag used to specify the size of raster pixel spacing in
     * model space units.
     */
    public static final int TAG_MODEL_PIXEL_SCALE = 33550;

    /**
     * A tag used to specify the transformation matrix between the raster
     * space and the model space.
     */
    public static final int TAG_MODEL_TRANSFORMATION = 34264;

    /** A tag used to store raster->model tiepoint pairs. */
    public static final int TAG_MODEL_TIE_POINT = 33922;

    /** A tag used to store the <i>GeoKey</i> directory. */
    public static final int TAG_GEO_KEY_DIRECTORY = 34735;

    /** A tag used to store all <code>double</code>-values <i>GeoKey</i>s. */
    public static final int TAG_GEO_DOUBLE_PARAMS = 34736;

    /** A tag used to store all ASCII-values <i>GeoKey</i>s. */
    public static final int TAG_GEO_ASCII_PARAMS = 34737;

    // GeoTIFF tags

    static class ModelPixelScale extends TIFFTag {
        public ModelPixelScale() {
            super("ModelPixelScaleTag",
                  TAG_MODEL_PIXEL_SCALE,
                  1 << TIFFTag.TIFF_DOUBLE);
        }
    }

    static class ModelTransformation extends TIFFTag {
        public ModelTransformation() {
            super("ModelTransformationTag",
                  TAG_MODEL_TRANSFORMATION,
                  1 << TIFFTag.TIFF_DOUBLE);
        }
    }

    static class ModelTiePoint extends TIFFTag {
        public ModelTiePoint() {
            super("ModelTiePointTag",
                  TAG_MODEL_TIE_POINT,
                  1 << TIFFTag.TIFF_DOUBLE);
        }
    }

    static class GeoKeyDirectory extends TIFFTag {
        public GeoKeyDirectory() {
            super("GeoKeyDirectory",
                  TAG_GEO_KEY_DIRECTORY,
                  1 << TIFFTag.TIFF_SHORT);
        }
    }

    static class GeoDoubleParams extends TIFFTag {
        public GeoDoubleParams() {
            super("GeoDoubleParams",
                  TAG_GEO_DOUBLE_PARAMS,
                  1 << TIFFTag.TIFF_DOUBLE);
        }
    }

    static class GeoAsciiParams extends TIFFTag {
        public GeoAsciiParams() {
            super("GeoAsciiParams",
                  TAG_GEO_ASCII_PARAMS,
                  1 << TIFFTag.TIFF_ASCII);
        }
    }

    private static List tags;

    private static void initTags() {
        tags = new ArrayList(42);

        tags.add(new GeoTIFFTagSet.ModelPixelScale());
        tags.add(new GeoTIFFTagSet.ModelTransformation());
        tags.add(new GeoTIFFTagSet.ModelTiePoint());
        tags.add(new GeoTIFFTagSet.GeoKeyDirectory());
        tags.add(new GeoTIFFTagSet.GeoDoubleParams());
        tags.add(new GeoTIFFTagSet.GeoAsciiParams());
    }

    private GeoTIFFTagSet() {
        super(tags);
    }

    /**
     * Returns a shared instance of a <code>GeoTIFFTagSet</code>.
     *
     * @return a <code>GeoTIFFTagSet</code> instance.
     */
    public synchronized static GeoTIFFTagSet getInstance() {
        if (theInstance == null) {
            initTags();
            theInstance = new GeoTIFFTagSet();
            tags = null;
        }
        return theInstance;
    }
}
