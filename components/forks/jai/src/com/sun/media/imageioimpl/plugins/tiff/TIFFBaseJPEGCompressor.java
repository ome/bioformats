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
 * $RCSfile: TIFFBaseJPEGCompressor.java,v $
 *
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All  Rights Reserved.
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
 * $Date: 2007/09/01 00:27:20 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import com.sun.media.imageio.plugins.tiff.TIFFCompressor;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.w3c.dom.Node;

/**
 * Base class for all possible forms of JPEG compression in TIFF.
 */
public abstract class TIFFBaseJPEGCompressor extends TIFFCompressor {

    private static final boolean DEBUG = false; // XXX false for release.

    // Stream metadata format.
    protected static final String STREAM_METADATA_NAME =
        "javax_imageio_jpeg_stream_1.0";

    // Image metadata format.
    protected static final String IMAGE_METADATA_NAME =
        "javax_imageio_jpeg_image_1.0";

    // ImageWriteParam passed in.
    private ImageWriteParam param = null;

    /**
     * ImageWriteParam for JPEG writer.
     * May be initialized by {@link #initJPEGWriter()}.
     */
    protected JPEGImageWriteParam JPEGParam = null;

    /**
     * The JPEG writer.
     * May be initialized by {@link #initJPEGWriter()}.
     */
    protected ImageWriter JPEGWriter = null;

    /**
     * Whether to write abbreviated JPEG streams (default == false).
     * A subclass which sets this to <code>true</code> should also
     * initialized {@link #JPEGStreamMetadata}.
     */
    protected boolean writeAbbreviatedStream = false;

    /**
     * Stream metadata equivalent to a tables-only stream such as in
     * the <code>JPEGTables</code>. Default value is <code>null</code>.
     * This should be set by any subclass which sets
     * {@link writeAbbreviatedStream} to <code>true</code>.
     */
    protected IIOMetadata JPEGStreamMetadata = null;

    // A pruned image metadata object containing only essential nodes.
    private IIOMetadata JPEGImageMetadata = null;

    // Whether the codecLib native JPEG writer is being used.
    private boolean usingCodecLib;

    // Array-based output stream.
    private IIOByteArrayOutputStream baos;

    /**
     * Removes nonessential nodes from a JPEG native image metadata tree.
     * All nodes derived from JPEG marker segments other than DHT, DQT,
     * SOF, SOS segments are removed unless <code>pruneTables</code> is
     * <code>true</code> in which case the nodes derived from the DHT and
     * DQT marker segments are also removed.
     *
     * @param tree A <tt>javax_imageio_jpeg_image_1.0</tt> tree.
     * @param pruneTables Whether to prune Huffman and quantization tables.
     * @throws IllegalArgumentException if <code>tree</code> is
     * <code>null</code> or is not the root of a JPEG native image
     * metadata tree.
     */
    private static void pruneNodes(Node tree, boolean pruneTables) {
        if(tree == null) {
            throw new IllegalArgumentException("tree == null!");
        }
        if(!tree.getNodeName().equals(IMAGE_METADATA_NAME)) {
            throw new IllegalArgumentException
                ("root node name is not "+IMAGE_METADATA_NAME+"!");
        }
        if(DEBUG) {
            System.out.println("pruneNodes("+tree+","+pruneTables+")");
        }

        // Create list of required nodes.
        List wantedNodes = new ArrayList();
        wantedNodes.addAll(Arrays.asList(new String[] {
            "JPEGvariety", "markerSequence",
            "sof", "componentSpec",
            "sos", "scanComponentSpec"
        }));

        // Add Huffman and quantization table nodes if not pruning tables.
        if(!pruneTables) {
            wantedNodes.add("dht");
            wantedNodes.add("dhtable");
            wantedNodes.add("dqt");
            wantedNodes.add("dqtable");
        }

        IIOMetadataNode iioTree = (IIOMetadataNode)tree;

        List nodes = getAllNodes(iioTree, null);
        int numNodes = nodes.size();

        for(int i = 0; i < numNodes; i++) {
            Node node = (Node)nodes.get(i);
            if(!wantedNodes.contains(node.getNodeName())) {
                if(DEBUG) {
                    System.out.println("Removing "+node.getNodeName());
                }
                node.getParentNode().removeChild(node);
            }
        }
    }

    private static List getAllNodes(IIOMetadataNode root, List nodes) {
        if(nodes == null) nodes = new ArrayList();

        if(root.hasChildNodes()) {
            Node sibling = root.getFirstChild();
            while(sibling != null) {
                nodes.add(sibling);
                nodes = getAllNodes((IIOMetadataNode)sibling, nodes);
                sibling = sibling.getNextSibling();
            }
        }

        return nodes;
    }

    public TIFFBaseJPEGCompressor(String compressionType,
                                  int compressionTagValue,
                                  boolean isCompressionLossless,
                                  ImageWriteParam param) {
        super(compressionType, compressionTagValue, isCompressionLossless);

        this.param = param;
    }

    /**
     * A <code>ByteArrayOutputStream</code> which allows writing to an
     * <code>ImageOutputStream</code>.
     */
    private static class IIOByteArrayOutputStream extends ByteArrayOutputStream {
        IIOByteArrayOutputStream() {
            super();
        }

        IIOByteArrayOutputStream(int size) {
            super(size);
        }

        public synchronized void writeTo(ImageOutputStream ios)
            throws IOException {
            ios.write(buf, 0, count);
        }
    }

    /**
     * Initializes the JPEGWriter and JPEGParam instance variables.
     * This method must be called before encode() is invoked.
     *
     * @param supportsStreamMetadata Whether the JPEG writer must
     * support JPEG native stream metadata, i.e., be capable of writing
     * abbreviated streams.
     * @param supportsImageMetadata Whether the JPEG writer must
     * support JPEG native image metadata.
     */
    protected void initJPEGWriter(boolean supportsStreamMetadata,
                                  boolean supportsImageMetadata) {
        // Reset the writer to null if it does not match preferences.
        if(this.JPEGWriter != null &&
           (supportsStreamMetadata || supportsImageMetadata)) {
            ImageWriterSpi spi = this.JPEGWriter.getOriginatingProvider();
            if(supportsStreamMetadata) {
                String smName = spi.getNativeStreamMetadataFormatName();
                if(smName == null || !smName.equals(STREAM_METADATA_NAME)) {
                    this.JPEGWriter = null;
                }
            }
            if(this.JPEGWriter != null && supportsImageMetadata) {
                String imName = spi.getNativeImageMetadataFormatName();
                if(imName == null || !imName.equals(IMAGE_METADATA_NAME)) {
                    this.JPEGWriter = null;
                }
            }
        }

        // Set the writer.
        if(this.JPEGWriter == null) {
            Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");

            while(iter.hasNext()) {
                // Get a writer.
                ImageWriter writer = (ImageWriter)iter.next();

                // Verify its metadata support level.
                if(supportsStreamMetadata || supportsImageMetadata) {
                    ImageWriterSpi spi = writer.getOriginatingProvider();
                    if(supportsStreamMetadata) {
                        String smName =
                            spi.getNativeStreamMetadataFormatName();
                        if(smName == null ||
                           !smName.equals(STREAM_METADATA_NAME)) {
                            // Try the next one.
                            continue;
                        }
                    }
                    if(supportsImageMetadata) {
                        String imName =
                            spi.getNativeImageMetadataFormatName();
                        if(imName == null ||
                           !imName.equals(IMAGE_METADATA_NAME)) {
                            // Try the next one.
                            continue;
                        }
                    }
                }

                // Set the writer.
                this.JPEGWriter = writer;
                break;
            }

            if(this.JPEGWriter == null) {
                // XXX The exception thrown should really be an IIOException.
                throw new IllegalStateException
                    ("No appropriate JPEG writers found!");
            }
        }

        this.usingCodecLib =
            JPEGWriter.getClass().getName().startsWith("com.sun.media");
        if(DEBUG) System.out.println("usingCodecLib = "+usingCodecLib);

        // Initialize the ImageWriteParam.
        if(this.JPEGParam == null) {
            if(param != null && param instanceof JPEGImageWriteParam) {
                JPEGParam = (JPEGImageWriteParam)param;
            } else {
                JPEGParam =
                    new JPEGImageWriteParam(writer != null ?
                                            writer.getLocale() : null);
                if(param.getCompressionMode() ==
                   ImageWriteParam.MODE_EXPLICIT) {
                    JPEGParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    JPEGParam.setCompressionQuality(param.getCompressionQuality());
                }
            }
        }
    }

    /**
     * Retrieves image metadata with non-core nodes removed.
     */
    private IIOMetadata getImageMetadata(boolean pruneTables)
        throws IIOException {
        if(DEBUG) {
            System.out.println("getImageMetadata("+pruneTables+")");
        }
        if(JPEGImageMetadata == null &&
           IMAGE_METADATA_NAME.equals(JPEGWriter.getOriginatingProvider().getNativeImageMetadataFormatName())) {
            TIFFImageWriter tiffWriter = (TIFFImageWriter)this.writer;

            // Get default image metadata.
            JPEGImageMetadata =
                JPEGWriter.getDefaultImageMetadata(tiffWriter.imageType,
                                                   JPEGParam);

            // Get the DOM tree.
            Node tree = JPEGImageMetadata.getAsTree(IMAGE_METADATA_NAME);

            // Remove unwanted marker segments.
            try {
                pruneNodes(tree, pruneTables);
            } catch(IllegalArgumentException e) {
                throw new IIOException("Error pruning unwanted nodes", e);
            }

            // Set the DOM back into the metadata.
            try {
                JPEGImageMetadata.setFromTree(IMAGE_METADATA_NAME, tree);
            } catch(IIOInvalidTreeException e) {
                // XXX This should really be a warning that image data
                // segments will be written with tables despite the
                // present of JPEGTables field.
                throw new IIOException
                    ("Cannot set pruned image metadata!", e);
            }
        }

        return JPEGImageMetadata;
    }

    public final int encode(byte[] b, int off,
                            int width, int height,
                            int[] bitsPerSample,
                            int scanlineStride) throws IOException {
        if (this.JPEGWriter == null) {
            throw new IIOException
                ("JPEG writer has not been initialized!");
        }
        if (!((bitsPerSample.length == 3 &&
               bitsPerSample[0] == 8 &&
               bitsPerSample[1] == 8 &&
               bitsPerSample[2] == 8) ||
              (bitsPerSample.length == 1 &&
               bitsPerSample[0] == 8))) {
            throw new IIOException
                ("Can only JPEG compress 8- and 24-bit images!");
        }

        // Set the stream.
        ImageOutputStream ios;
        long initialStreamPosition; // usingCodecLib && !writeAbbreviatedStream
        if(usingCodecLib && !writeAbbreviatedStream) {
            ios = stream;
            initialStreamPosition = stream.getStreamPosition();
        } else {
            // If not using codecLib then the stream has to be wrapped as
            // 1) the core Java Image I/O JPEG ImageWriter flushes the
            // stream at the end of each write() and this causes problems
            // for the TIFF writer, or 2) the codecLib JPEG ImageWriter
            // is using a stream on the native side which cannot be reset.
            if(baos == null) {
                baos = new IIOByteArrayOutputStream();
            } else {
                baos.reset();
            }
            ios = new MemoryCacheImageOutputStream(baos);
            initialStreamPosition = 0L;
        }
        JPEGWriter.setOutput(ios);

        // Create a DataBuffer.
        DataBufferByte dbb;
        if(off == 0 || usingCodecLib) {
            dbb = new DataBufferByte(b, b.length);
        } else {
            //
            // Workaround for bug in core Java Image I/O JPEG
            // ImageWriter which cannot handle non-zero offsets.
            //
            int bytesPerSegment = scanlineStride*height;
            byte[] btmp = new byte[bytesPerSegment];
            System.arraycopy(b, off, btmp, 0, bytesPerSegment);
            dbb = new DataBufferByte(btmp, bytesPerSegment);
            off = 0;
        }

        // Set up the ColorSpace.
        int[] offsets;
        ColorSpace cs;
        if(bitsPerSample.length == 3) {
            offsets = new int[] { off, off + 1, off + 2 };
            cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        } else {
            offsets = new int[] { off };
            cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        }

        // Create the ColorModel.
        ColorModel cm = new ComponentColorModel(cs,
                                                false,
                                                false,
                                                Transparency.OPAQUE,
                                                DataBuffer.TYPE_BYTE);

        // Create the SampleModel.
        SampleModel sm =
            new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE,
                                            width, height,
                                            bitsPerSample.length,
                                            scanlineStride,
                                            offsets);

        // Create the WritableRaster.
        WritableRaster wras =
            Raster.createWritableRaster(sm, dbb, new Point(0, 0));

        // Create the BufferedImage.
        BufferedImage bi = new BufferedImage(cm, wras, false, null);

        // Get the pruned JPEG image metadata (may be null).
        IIOMetadata imageMetadata = getImageMetadata(writeAbbreviatedStream);

        // Compress the image into the output stream.
        int compDataLength;
        if(usingCodecLib && !writeAbbreviatedStream) {
            // Write complete JPEG stream
            JPEGWriter.write(null, new IIOImage(bi, null, imageMetadata),
                             JPEGParam);

            compDataLength =
                (int)(stream.getStreamPosition() - initialStreamPosition);
        } else {
            if(writeAbbreviatedStream) {
                // Write abbreviated JPEG stream

                // First write the tables-only data.
                JPEGWriter.prepareWriteSequence(JPEGStreamMetadata);
                ios.flush();

                // Rewind to the beginning of the byte array.
                baos.reset();

                // Write the abbreviated image data.
                IIOImage image = new IIOImage(bi, null, imageMetadata);
                JPEGWriter.writeToSequence(image, JPEGParam);
                JPEGWriter.endWriteSequence();
            } else {
                // Write complete JPEG stream
                JPEGWriter.write(null,
                                 new IIOImage(bi, null, imageMetadata),
                                 JPEGParam);
            }

            compDataLength = baos.size();
            baos.writeTo(stream);
            baos.reset();
        }

        return compDataLength;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if(JPEGWriter != null) {
            JPEGWriter.dispose();
        }
    }
}
