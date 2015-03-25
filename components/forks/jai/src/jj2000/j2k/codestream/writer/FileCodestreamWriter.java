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
 * $RCSfile: FileCodestreamWriter.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:02 $
 * $State: Exp $
 *
 * Class:                   FileCodestreamWriter
 *
 * Description:             Implementation of the bit stream writer for streams.
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
 * */
package jj2000.j2k.codestream.writer;

import jj2000.j2k.codestream.*;

import java.io.*;

/**
 * This class implements a CodestreamWriter for Java streams. The streams can
 * be files or network connections, or any other resource that presents itself
 * as a OutputStream. See the CodestreamWriter abstract class for more details
 * on the implementation of the CodestreamWriter abstract class.
 *
 * <P>Before any packet data is written to the bit stream (even in simulation
 * mode) the complete header should be written to the HeaderEncoder object
 * supplied to the constructor, following the procedure explained in the
 * HeaderEncoder class. Otherwise incorrect estimates are given by
 * getMaxAvailableBytes() for rate allocation.
 *
 * @see CodestreamWriter
 *
 * @see HeaderEncoder
 * */
public class FileCodestreamWriter extends CodestreamWriter
    implements Markers {

    /** The upper limit for the value of the Nsop field of the SOP marker */
    private final static int SOP_MARKER_LIMIT = 65535;

    /** Index of the current tile */
    private int tileIdx = 0;

    /** The file to write */
    private OutputStream out;

    /** The number of bytes already written to the bit stream, excluding the
     * header length, magic number and header length info. */
    int ndata=0;

    /** The default buffer length, 1024 bytes */
    public static int DEF_BUF_LEN = 1024;

    /** Array used to store the SOP markers values */
    byte sopMarker[];

    /** Array used to store the EPH markers values */
    byte ephMarker[];

    /** The packet index (when start of packet markers i.e. SOP markers) are
     *  used. */
    int packetIdx=0;

    /** Offset of end of last packet containing ROI information */
    private int offLastROIPkt = 0;

    /** Length of last packets containing no ROI information */
    private int lenLastNoROI = 0;

    /**
     * Opens the file 'file' for writing the bit stream, using the 'he' header
     * encoder. The magic number is written to the bit stream. Normally, the
     * header encoder must be empty (i.e. no data has been written to it
     * yet). A BufferedOutputStream is used on top of the file to increase
     * throughput, the length of the buffer is DEF_BUF_LEN.
     *
     * @param file The file where to write the bit stream
     *
     * @param mb The maximum number of bytes that can be written to the bit
     * stream.
     *
     * @exception IOException If an error occurs while trying to open the file
     * for writing or while writing the magic number.
     * */
    public FileCodestreamWriter(File file, int mb)
        throws IOException {

        super(mb);
        out = new BufferedOutputStream(new FileOutputStream(file),DEF_BUF_LEN);
        initSOP_EPHArrays();
    }

    /**
     * Opens the file named 'fname' for writing the bit stream, using the 'he'
     * header encoder. The magic number is written to the bit
     * stream. Normally, the header encoder must be empty (i.e. no data has
     * been written to it yet). A BufferedOutputStream is used on top of the
     * file to increase throughput, the length of the buffer is DEF_BUF_LEN.
     *
     * @param fname The name of file where to write the bit stream
     *
     * @param mb The maximum number of bytes that can be written to the bit
     * stream.
     *
     * @exception IOException If an error occurs while trying to open the file
     * for writing or while writing the magic number.
     * */
    public FileCodestreamWriter(String fname, int mb)
        throws IOException {

        super(mb);
        out = new BufferedOutputStream(new FileOutputStream(fname),
                                       DEF_BUF_LEN);
        initSOP_EPHArrays();
    }

    /**
     * Uses the output stream 'os' for writing the bit stream, using the 'he'
     * header encoder. The magic number is written to the bit
     * stream. Normally, the header encoder must be empty (i.e. no data has
     * been written to it yet). No BufferedOutputStream is used on top of the
     * output stream 'os'.
     *
     * @param os The output stream where to write the bit stream.
     *
     * @param mb The maximum number of bytes that can be written to the bit
     * stream.
     *
     * @exception IOException If an error occurs while writing the magic
     * number to the 'os' output stream.
     * */
    public FileCodestreamWriter(OutputStream os, int mb)
        throws IOException {

        super(mb);
        out = os;
        initSOP_EPHArrays();
    }

    /**
     * Returns the number of bytes remaining available in the bit stream. This
     * is the maximum allowed number of bytes minus the number of bytes that
     * have already been written to the bit stream. If more bytes have been
     * written to the bit stream than the maximum number of allowed bytes,
     * then a negative value is returned.
     *
     * @return The number of bytes remaining available in the bit stream.
     * */
    public final int getMaxAvailableBytes() {
        return maxBytes-ndata;
    }

    /**
     * Returns the current length of the entire bit stream.
     *
     * @return the current length of the bit stream
     * */
    public int getLength() {
        if (getMaxAvailableBytes() >= 0) {
            return ndata;
        }
        else {
            return maxBytes;
        }
    }

    /**
     * Writes a packet head to the bit stream and returns the number of bytes
     * used by this header. It returns the total number of bytes that the
     * packet head takes in the bit stream. If in simulation mode then no data
     * is written to the bit stream but the number of bytes is
     * calculated. This can be used for iterative rate allocation.
     *
     * <P>If the length of the data that is to be written to the bit stream is
     * more than the space left (as returned by getMaxAvailableBytes()) only
     * the data that does not exceed the allowed length is written, the rest
     * is discarded. However the value returned by the method is the total
     * length of the packet, as if all of it was written to the bit stream.
     *
     * <P>If the bit stream header has not been commited yet and 'sim' is
     * false, then the bit stream header is automatically commited (see
     * commitBitstreamHeader() method) before writting the packet.
     *
     * @param head The packet head data.
     *
     * @param hlen The number of bytes in the packet head.
     *
     * @param sim Simulation mode flag. If true nothing is written to the bit
     * stream, but the number of bytes that would be written is returned.
     *
     * @param sop Start of packet header marker flag. This flag indicates
     * whether or not SOP markers should be written. If true, SOP markers
     * should be written, if false, they should not.
     *
     * @param eph End of Packet Header marker flag. This flag indicates
     * whether or not EPH markers should be written. If true, EPH markers
     * should be written, if false, they should not.
     *
     * @return The number of bytes spent by the packet head.
     *
     * @exception IOException If an I/O error occurs while writing to the
     * output stream.
     *
     * @see #commitBitstreamHeader
     * */
    public int writePacketHead(byte head[],int hlen,boolean sim,
			       boolean sop, boolean eph) throws IOException{
        int len = hlen
	    + (sop?Markers.SOP_LENGTH:0)
	    + (eph?Markers.EPH_LENGTH:0);

        // If not in simulation mode write the data
        if(!sim){
	    // Write the head bytes
	    if(getMaxAvailableBytes()<len){
		len = getMaxAvailableBytes();
	    }

            if(len > 0){
                // Write Start Of Packet header markers if necessary
                if(sop){
                    // The first 4 bytes of the array have been filled in the
                    // classe's constructor.
                    sopMarker[4] = (byte)(packetIdx>>8);
                    sopMarker[5] = (byte)(packetIdx);
                    out.write(sopMarker, 0, Markers.SOP_LENGTH);
                    packetIdx++;
                    if(packetIdx>SOP_MARKER_LIMIT){
                        // Reset SOP value as we have reached its upper limit
                        packetIdx = 0;
                    }
                }
                out.write(head,0,hlen);
		// Update data length
		ndata += len;

                // Write End of Packet Header markers if necessary
                if(eph){
                    out.write(ephMarker,0,Markers.EPH_LENGTH);
                }

                // Deal with ROI Information
                lenLastNoROI += len;
            }
        }
        return len;
    }

    /**
     * Writes a packet body to the bit stream and returns the number of bytes
     * used by this body .If in simulation mode then no data is written to the
     * bit stream but the number of bytes is calculated. This can be used for
     * iterative rate allocation.
     *
     * <P>If the length of the data that is to be written to the bit stream is
     * more than the space left (as returned by getMaxAvailableBytes()) only
     * the data that does not exceed the allowed length is written, the rest
     * is discarded. However the value returned by the method is the total
     * length of the packet body , as if all of it was written to the bit
     * stream.
     *
     * @param body The packet body data.
     *
     * @param blen The number of bytes in the packet body.
     *
     * @param sim Simulation mode flag. If true nothing is written to the bit
     * stream, but the number of bytes that would be written is returned.
     *
     * @param roiInPkt Whether or not this packet contains ROI information
     *
     * @param roiLen Number of byte to read in packet body to get all the ROI
     * information
     *
     * @return The number of bytes spent by the packet body.
     *
     * @exception IOException If an I/O error occurs while writing to the
     * output stream.
     *
     * @see #commitBitstreamHeader
     * */
    public int writePacketBody(byte body[],int blen,boolean sim,
                               boolean roiInPkt, int roiLen)
        throws IOException{

        int len = blen;

        // If not in simulation mode write the data
        if (!sim) {
            // Write the body bytes
            len = blen;
            if(getMaxAvailableBytes() < len){
                len = getMaxAvailableBytes();
            }
            if(blen > 0){
                out.write(body,0,len);
            }
            // Update data length
            ndata += len;

            // Deal with ROI information
            if(roiInPkt) {
                offLastROIPkt += lenLastNoROI + roiLen;
                lenLastNoROI = len-roiLen;
            } else {
                lenLastNoROI += len;
            }
        }
        return len;
    }

    /**
     * Writes the EOC marker and closes the underlying stream.
     *
     * @exception IOException If an error occurs while closing the underlying
     * stream.
     * */
    public void close() throws IOException {

	// Write the EOC marker and close the codestream.
        out.write(EOC>>8);
        out.write(EOC);

        ndata += 2; // Add two to length of codestream for EOC marker

        out.close();
    }

    /**
     * Gives the offset of the end of last packet containing ROI information
     *
     * @return End of last ROI packet
     * */
    public int getOffLastROIPkt(){
        return offLastROIPkt;
    }

    /**
     * Writes the header data in the codestream and actualize ndata with the
     * header length. The header is either a MainHeaderEncoder or a
     * TileHeaderEncoder.
     *
     * @param he The current header encoder.
     *
     * @exception IOException If an I/O error occurs while writing the data.
     * */
    public void commitBitstreamHeader(HeaderEncoder he) throws IOException {
        // Actualize ndata
        ndata += he.getLength();
        he.writeTo(out);         // Write the header
        // Reset packet index used for SOP markers
        packetIdx = 0;

        // Deal with ROI information
        lenLastNoROI += he.getLength();
    }

    /**
     * Performs the initialisation of the arrays that are used to store the
     * values used to write SOP and EPH markers
     * */
    private void initSOP_EPHArrays() {

        // Allocate and set first values of SOP marker as they will not be
        // modified
        sopMarker = new byte[Markers.SOP_LENGTH];
        sopMarker[0] = (byte)(Markers.SOP>>8);
        sopMarker[1] = (byte)Markers.SOP;
        sopMarker[2] = (byte)0x00;
        sopMarker[3] = (byte)0x04;

        // Allocate and set values of EPH marker as they will not be
        // modified
        ephMarker = new byte[Markers.EPH_LENGTH];
        ephMarker[0] = (byte)(Markers.EPH>>8);
        ephMarker[1] = (byte)Markers.EPH;
    }
}
