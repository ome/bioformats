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
 * $RCSfile: FileFormatWriter.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/08/21 22:58:22 $
 * $State: Exp $
 *
 * Class:                   FileFormatWriter
 *
 * Description:             Writes the file format
 *
 *
 * COPYRIGHT:
 *
 * This software module was originally developed by Raphaël Grosbois and
 * Diego Santa Cruz (Swiss Federal Institute of Technology-EPFL); Joel
 * Askelöf (Ericsson Radio Systems AB); and Bertrand Berthelot, David
 * Bouchard, Félix Henry, Gerard Mozelle and Patrice Onno (Canon Research
 * Centre France S.A) in the course of development of the JPEG2000
 * standard as specified by ISO/IEC 15444 (JPEG 2000 Standard). This
 * software module is an implementation of a part of the JPEG 2000
 * Standard. Swiss Federal Institute of Technology-EPFL, Ericsson Radio
 * Systems AB and Canon Research Centre France S.A (collectively JJ2000
 * Partners) agree not to assert against ISO/IEC and users of the JPEG
 * 2000 Standard (Users) any of their rights under the copyright, not
 * including other intellectual property rights, for this software module
 * with respect to the usage by ISO/IEC and Users of this software module
 * or modifications thereof for use in hardware or software products
 * claiming conformance to the JPEG 2000 Standard. Those intending to use
 * this software module in hardware or software products are advised that
 * their use may infringe existing patents. The original developers of
 * this software module, JJ2000 Partners and ISO/IEC assume no liability
 * for use of this software module or modifications thereof. No license
 * or right to this software module is granted for non JPEG 2000 Standard
 * conforming products. JJ2000 Partners have full right to use this
 * software module for his/her own purpose, assign or donate this
 * software module to any third party and to inhibit third parties from
 * using this software module for non JPEG 2000 Standard conforming
 * products. This copyright notice must be included in all copies or
 * derivative works of this software module.
 *
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *  */
package jj2000.j2k.fileformat.writer;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;

import jj2000.j2k.codestream.*;
import jj2000.j2k.fileformat.*;
import jj2000.j2k.io.*;

import java.io.*;

import javax.imageio.stream.ImageOutputStream;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.NodeList;
import com.sun.media.imageioimpl.plugins.jpeg2000.Box;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KMetadata;
import com.sun.media.imageioimpl.plugins.jpeg2000.J2KMetadataFormat;

/**
 * This class writes the file format wrapper that may or may not exist around
 * a valid JPEG 2000 codestream. This class writes the simple possible legal
 * fileformat
 *
 * @see jj2000.j2k.fileformat.reader.FileFormatReader
 * */
public class FileFormatWriter implements FileFormatBoxes {

    /** The name of the file from which to read the codestream and to write
     * the JP2 file*/
    private File file;

    private ImageOutputStream stream;

    /** Image height */
    private int height;

    /** Image width */
    private int width;

    /** Number of components */
    private int nc;

    /** Bits per component */
    private int bpc[];

    /** Flag indicating whether number of bits per component varies */
    private boolean bpcVaries;

    /** Length of codestream */
    private int clength;

    /** Length of Colour Specification Box */
    private static final int CSB_LENGTH = 15;

    /** Length of File Type Box */
    private static final int FTB_LENGTH = 20;

    /** Length of Image Header Box */
    private static final int IHB_LENGTH = 22;

    /** base length of Bits Per Component box */
    private static final int BPC_LENGTH = 8;

    /** The color model of the image to be compressed. */
    private ColorModel colorModel;

    /** The sample model of the image to be compressed. */
    private SampleModel sampleModel;

    /** The sample model of the image to be compressed. */
    private J2KMetadata metadata;

    /** Indicates that the <code>colorModel</code> is a
     *  <code>IndexColorModel</code>
     */
    private boolean isIndexed = false;

    /** The length not counted by the orginal JJ2000 packages. */
    private int otherLength;

    /** cache the <code>J2KMetadataFormat</code> */
    J2KMetadataFormat format ;

    /**
     * The constructor of the FileFormatWriter. It receives all the
     * information necessary about a codestream to generate a legal JP2 file
     *
     * @param file The name of the file that is to be made a JP2 file
     *
     * @param height The height of the image
     *
     * @param width The width of the image
     *
     * @param nc The number of components
     *
     * @param bpc The number of bits per component
     *
     * @param clength Length of codestream
     * @param colorModel The color model of the image to be compressed.
     */
    public FileFormatWriter(File file, ImageOutputStream stream,
                            int height, int width, int nc,
                            int[] bpc, int clength,
                            ColorModel colorModel,
                            SampleModel sampleModel,
                            J2KMetadata metadata){
        this.height = height;
        this.width = width;
        this.nc = nc;
        this.bpc = bpc;
        this.file=file;
        this.stream = stream;
        this.clength = clength;
        this.colorModel = colorModel;
        this.sampleModel = sampleModel;
        this.metadata = metadata;

        if (colorModel instanceof IndexColorModel)
            isIndexed = true;

        bpcVaries=false;
        int fixbpc = bpc[0];
        for(int i=nc-1; i>0 ; i--) {
            if(bpc[i] != fixbpc)
                bpcVaries = true;
        }
    }



    /**
     * This method reads the codestream and writes the file format wrapper and
     * the codestream to the same file
     *
     * @return The number of bytes increases because of the file format
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public int writeFileFormat() throws IOException {
        writeMetadata(metadata);

        // Write the Codestream box
        writeContiguousCodeStreamBox();

        return CSB_LENGTH + otherLength;
    }

    private void writeMetadata(J2KMetadata metadata) throws IOException {
        if (metadata == null)
            return;

        IIOMetadataNode root =
            (IIOMetadataNode)metadata.getAsTree("com_sun_media_imageio_plugins_jpeg2000_image_1.0");
        if (root == null)
            return;
        format = (J2KMetadataFormat)metadata.getMetadataFormat("com_sun_media_imageio_plugins_jpeg2000_image_1.0");
        writeSuperBox(root);
    }

    private void writeSuperBox(IIOMetadataNode node) throws IOException {
        NodeList list = node.getChildNodes();

        String name = node.getNodeName();
        if (name.startsWith("JPEG2000")) {
            stream.writeInt(computeLength(node));
            stream.writeInt(Box.getTypeInt((String)Box.getTypeByName(name)));
            otherLength += 8;
        }

        for (int i = 0; i < list.getLength(); i++) {
            IIOMetadataNode child = (IIOMetadataNode)list.item(i);

            name = child.getNodeName();
            if (name.startsWith("JPEG2000") && format.isLeaf(name))
                writeBox(child);
            else
                writeSuperBox(child);
        }
    }

    private void writeBox(IIOMetadataNode node) throws IOException {
        int type = Box.getTypeInt((String)Box.getAttribute(node, "Type"));
        int length = new Integer((String)Box.getAttribute(node, "Length")).intValue();
        Box box = Box.createBox(type, node);
        otherLength += length;
        stream.writeInt(length);
        stream.writeInt(type);
        byte[] data = box.getContent();
        stream.write(data, 0, data.length);
    }

    private int computeLength(IIOMetadataNode root) {
        NodeList list = root.getChildNodes();
        int length = 0;
        for (int i = 0; i < list.getLength(); i++) {
            IIOMetadataNode node = (IIOMetadataNode)list.item(i);
            String name = node.getNodeName();

            if (format.isLeaf(name))
                length += new Integer((String)Box.getAttribute(node, "Length")).intValue();
            else
                length += computeLength(node);

        }

        return length + (root.getNodeName().startsWith("JPEG2000") ? 8 : 0) ;
    }

    /**
     * This method writes the Contiguous codestream box
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public void writeContiguousCodeStreamBox()throws IOException {

        //when write a jp2 file
        if (metadata != null) {
            // Write box length (LBox)
            // This value is set to 0 since in this implementation, this box is
            // always last
            stream.writeInt(clength+8);

            // Write contiguous codestream box name (TBox)
            stream.writeInt(CONTIGUOUS_CODESTREAM_BOX);
        }
            // Read and buffer the codestream
        BEBufferedRandomAccessFile fi =
            new BEBufferedRandomAccessFile(file,"rw+");
        int remainder = clength;
        byte[] codestream = new byte[1024];

        while(remainder >0) {
            int len = remainder > 1024 ? 1024 : remainder;
            fi.readFully(codestream, 0, len);

            // Write codestream
            stream.write(codestream, 0, len);
            remainder -= len;
        }

        // Close the file.
        fi.close();
    }
}
