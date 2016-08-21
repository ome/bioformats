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
 * $RCSfile: CodestreamManipulator.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:24 $
 * $State: Exp $
 *
 * Class:                   CodestreamManipulator
 *
 * Description:             Manipulates codestream to create tile-parts etc
 *
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
package jj2000.j2k.util;

import jj2000.j2k.codestream.*;
import jj2000.j2k.io.*;

import java.util.*;
import java.io.*;

/**
 * This class takes a legal JPEG 2000 codestream and performs some
 * manipulation on it. Currently the manipulations supported are: Tile-parts
 * */
public class CodestreamManipulator{

    /** Flag indicating whether packed packet headers in main header is used
     *  */
    private boolean ppmUsed;

    /** Flag indicating whether packed packet headers in tile headers is used
     *  */
    private boolean pptUsed;

    /** Flag indicating whether SOP marker was only intended for parsing in
     * This class and should be removed */
    private boolean tempSop;

    /** Flag indicating whether EPH marker was only intended for parsing in
     * This class and should be removed */
    private boolean tempEph;

    /** The number of tiles in the image */
    private int nt;

    /** The number of packets per tile-part */
    private int pptp;

    /** The name of the outfile */
    private File file;

    /** The length of a SOT plus a SOD marker */
    private static int TP_HEAD_LEN = 14;

    /** The maximum number of a tile part index (TPsot) */
    private static int MAX_TPSOT = 16;

    /** The maximum number of tile parts in any tile */
    private int maxtp;

    /** The number of packets per tile */
    private int[] ppt = new int[nt];

    /** The positions of the SOT, SOP and EPH markers */
    private Integer[] positions;

    /** The main header */
    private byte[] mainHeader;

    /** Buffers containing the tile parts */
    private byte[][][] tileParts;

    /** Buffers containing the original tile headers */
    private byte[][]   tileHeaders;

    /** Buffers contaning the packet headers */
    private byte[][][] packetHeaders;

    /** Buffers containing the packet data */
    private byte[][][] packetData;

    /** Buffers containing the SOP marker segments */
    private byte[][][] sopMarkSeg;

    /**
     * Instantiates a codestream manipulator..
     *
     * @param outname The name of the original outfile
     *
     * @param nt The number of tiles in the image
     *
     * @param pptp Packets per tile-part. If zero, no division into tileparts
     * is performed
     *
     * @param ppm Flag indicating that PPM marker is used
     *
     * @param ppt Flag indicating that PPT marker is used
     *
     * @param tempSop Flag indicating whether SOP merker should be removed
     *
     * @param tempEph Flag indicating whether EPH merker should be removed
     * */
    public CodestreamManipulator(File file, int nt, int pptp, boolean ppm,
                                 boolean ppt, boolean tempSop,
                                 boolean tempEph) {
	this.file = file;
        this.nt=nt;
        this.pptp = pptp;
        this.ppmUsed = ppm;
        this.pptUsed = ppt;
        this.tempSop = tempSop;
        this.tempEph = tempEph;
    }

    /**
     * This method performs the actual manipulation of the codestream which is
     * the reparsing for tile parts and packed packet headers
     *
     * @return The number of bytes that the file has increased by
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public int doCodestreamManipulation() throws IOException{
        int addedHeaderBytes=0;
        ppt = new int[nt];
        tileParts     = new byte[nt][][];
        tileHeaders   = new byte[nt][];
        packetHeaders = new byte[nt][][];
        packetData    = new byte[nt][][];
        sopMarkSeg    = new byte[nt][][];

        // If neither packed packet header nor tile parts are used, return 0
        if( ppmUsed == false && pptUsed == false && pptp == 0)
            return 0;

        // Open file for reading and writing
        BEBufferedRandomAccessFile fi =
            new BEBufferedRandomAccessFile(file, "rw+");
        addedHeaderBytes -= fi.length();

        // Parse the codestream for SOT, SOP and EPH markers
        parseAndFind(fi);

        // Read and buffer the tile headers, packet headers and packet data
        readAndBuffer(fi);

        // Close file and overwrite with new file
        fi.close();
        fi = new  BEBufferedRandomAccessFile(file, "rw");

        // Create tile-parts
        createTileParts();

        // Write new codestream
        writeNewCodestream(fi);

        // Close file
        fi.flush();
        addedHeaderBytes += fi.length();
        fi.close();

        return addedHeaderBytes;
    }

    /**
     * This method parses the codestream for SOT, SOP and EPH markers and
     * removes header header bits signalling SOP and EPH markers if packed
     * packet headers are used
     *
     * @param fi The file to parse the markers from
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    private void parseAndFind(BufferedRandomAccessFile fi) throws IOException{
        int length,pos,i,t,sop=0,eph=0;
        short marker;
        int halfMarker;
        int tileEnd;
        Vector markPos = new Vector();

        // Find position of first SOT marker
        marker = (short)fi.readUnsignedShort(); // read SOC marker
        marker = (short)fi.readUnsignedShort();
        while(marker != Markers.SOT){
            pos = fi.getPos();
            length = fi.readUnsignedShort();

            // If SOP and EPH markers were only used for parsing in this
            // class remove SOP and EPH markers from Scod field
            if(marker == Markers.COD){
                int scod = fi.readUnsignedByte();
                if(tempSop)
                    scod &= 0xfd; // Remove bits indicating SOP
                if(tempEph)
                    scod &= 0xfb; // Remove bits indicating SOP
                fi.seek(pos +2);
                fi.write(scod);
            }

            fi.seek(pos + length);
            marker = (short)fi.readUnsignedShort();
        }
        pos = fi.getPos();
        fi.seek(pos-2);

        // Find all packet headers, packed data and tile headers
        for(t=0;t<nt;t++){
            // Read SOT marker
            fi.readUnsignedShort(); // Skip SOT
            pos = fi.getPos();
            markPos.addElement(new Integer(fi.getPos()));
            fi.readInt();           // Skip Lsot and Isot
            length = fi.readInt();  // Read Psot
            fi.readUnsignedShort(); // Skip TPsot & TNsot
            tileEnd = pos + length-2; // Last byte of tile

            // Find position of SOD marker
            marker = (short)fi.readUnsignedShort();
            while(marker != Markers.SOD){
                pos = fi.getPos();
                length = fi.readUnsignedShort();

                // If SOP and EPH markers were only used for parsing in this
                // class remove SOP and EPH markers from Scod field
                if(marker == Markers.COD){
                    int scod = fi.readUnsignedByte();
                    if(tempSop)
                        scod &= 0xfd; // Remove bits indicating SOP
                    if(tempEph)
                        scod &= 0xfb; // Remove bits indicating SOP
                    fi.seek(pos +2);
                    fi.write(scod);
                }
                fi.seek(pos + length);
                marker = (short)fi.readUnsignedShort();
            }

            // Find all SOP and EPH markers in tile
            sop = 0;
            eph = 0;

            i = fi.getPos();
            while(i<tileEnd){
                halfMarker = (short) fi.readUnsignedByte();
                if(halfMarker == (short)0xff){
                    marker = (short)((halfMarker<<8) +
                                        fi.readUnsignedByte());
                    i++;
                    if(marker == Markers.SOP){
                        markPos.addElement(new Integer(fi.getPos()));
                        ppt[t]++;
                        sop++;
			fi.skipBytes(4);
			i+=4;
                    }

                    if(marker == Markers.EPH){
                        markPos.addElement(new Integer(fi.getPos()));
                        eph++;
                    }
                }
                i++;
            }
        }
        markPos.addElement(new Integer(fi.getPos()+2));
        positions = new Integer[markPos.size()];
        markPos.copyInto(positions);
    }

    /**
     * This method reads and buffers the tile headers, packet headers and
     * packet data.
     *
     * @param fi The file to read the headers and data from
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    private void readAndBuffer(BufferedRandomAccessFile fi)throws IOException{
        int p,prem,length,t,markIndex;

        // Buffer main header
        fi.seek(0);
        length = ((Integer)positions[0]).intValue()-2;
        mainHeader = new byte[length];
        fi.readFully(mainHeader,0,length);
        markIndex = 0;

        for(t=0; t<nt; t++){
            prem = ppt[t];

            packetHeaders[t] = new byte[prem][];
            packetData[t] = new byte[prem][];
            sopMarkSeg[t] = new byte[prem][];

            // Read tile header
            length = positions[ markIndex+1 ].intValue() -
                positions[ markIndex ].intValue();
            tileHeaders[t] = new byte[length];
            fi.readFully(tileHeaders[t],0,length);
            markIndex++;

            for(p=0; p<prem; p++){
                // Read packet header
                length = positions[ markIndex+1 ].intValue() -
                    positions[ markIndex ].intValue();

                if(tempSop){ // SOP marker is skipped
                    length -= Markers.SOP_LENGTH;
                    fi.skipBytes(Markers.SOP_LENGTH);
                }
                else{ // SOP marker is read and buffered
                    length -= Markers.SOP_LENGTH;
                    sopMarkSeg[t][p] = new byte[Markers.SOP_LENGTH];
                    fi.readFully(sopMarkSeg[t][p],0,Markers.SOP_LENGTH);
                }

                if(!tempEph){ // EPH marker is kept in header
                    length += Markers.EPH_LENGTH;
                }

                packetHeaders[t][p] = new byte[length];
                fi.readFully(packetHeaders[t][p],0,length);
                markIndex++;

                // Read packet data
                length = positions[ markIndex+1 ].intValue() -
                    positions[ markIndex ].intValue();

                length -= Markers.EPH_LENGTH;
                 if(tempEph){ // EPH marker is used and is skipped
                    fi.skipBytes(Markers.EPH_LENGTH);
                }

                packetData[t][p] = new byte[length];
                fi.readFully(packetData[t][p],0,length);
                markIndex++;
            }
        }
    }

    /**
     * This method creates the tileparts from the buffered tile headers,
     * packet headers and packet data
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    private void createTileParts() throws IOException{
        int i,prem,t,length;
        int pIndex,phIndex;
        int tppStart;
        int tilePart;
        int p,np, nomnp;
        int numTileParts;
        int numPackets;
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        byte[] tempByteArr;

        // Create tile parts
        tileParts = new byte[nt][][];
        maxtp = 0;

        for(t=0; t<nt; t++){
            // Calculate number of tile parts. If tileparts are not used,
            // put all packets in the first tilepart
            if(pptp == 0)
                pptp = ppt[t];
            prem = ppt[t];
            numTileParts = (int)Math.ceil(((double)prem)/pptp);
            numPackets = packetHeaders[t].length;
            maxtp = (numTileParts>maxtp) ? numTileParts:maxtp;
            tileParts[t] = new byte[numTileParts][];

            // Create all the tile parts for tile t
            tppStart = 0;
            pIndex = 0;
            p=0;
            phIndex = 0;
            for(tilePart=0;tilePart<numTileParts;tilePart++){

                // Calculate number of packets in this tilepart
                nomnp = (pptp > prem) ? prem:pptp;
		np = nomnp;

                // Write tile part header
                if(tilePart == 0){
                    // Write original tile part header up to SOD marker
                    temp.write(tileHeaders[t],0,
                               tileHeaders[t].length-2);
                }else{
                    // Write empty header of length TP_HEAD_LEN-2
                    temp.write(new byte[TP_HEAD_LEN-2],0,TP_HEAD_LEN-2);
                }

                // Write PPT marker segments if PPT used
                if(pptUsed){
                    int pptLength = 3; // Zppt and Lppt
                    int pptIndex = 0;
                    int phLength;

                    p = pIndex;
                    while(np > 0){
                        phLength = packetHeaders[t][p].length;

                        // If the total legth of the packet headers is greater
                        // than MAX_LPPT, several PPT markers are needed
                        if(pptLength + phLength > Markers.MAX_LPPT){
                            temp.write(Markers.PPT>>>8);
                            temp.write(Markers.PPT);
                            temp.write(pptLength>>>8);
                            temp.write(pptLength);
                            temp.write(pptIndex++);
                            for(i=pIndex; i < p ; i++){
                                temp.write(packetHeaders[t][i],0,
                                           packetHeaders[t][i].length);
                            }
                            pptLength = 3; // Zppt and Lppt
                            pIndex = p;
                        }
                        pptLength += phLength;
                        p++;
			np--;
                    }
                    // Write last PPT marker
                    temp.write(Markers.PPT>>>8);
                    temp.write(Markers.PPT);
                    temp.write(pptLength>>>8);
                    temp.write(pptLength);
                    temp.write(pptIndex);
                    for(i=pIndex; i < p ; i++){

                        temp.write(packetHeaders[t][i],0,
                                   packetHeaders[t][i].length);
                    }
                }
                pIndex = p;
		np = nomnp;

                // Write SOD marker
                temp.write(Markers.SOD>>>8);
                temp.write(Markers.SOD);

                // Write packet data and packet headers if PPT and PPM not used
                for(p=tppStart; p<tppStart+np ; p++){
                  if(!tempSop){
                        temp.write(sopMarkSeg[t][p],0,Markers.SOP_LENGTH);
                  }

                    if(!(ppmUsed || pptUsed)){
                        temp.write(packetHeaders[t][p],0,
                                   packetHeaders[t][p].length);
                    }

                    temp.write(packetData[t][p], 0, packetData[t][p].length);
                }
                tppStart += np;

                // Edit tile part header
                tempByteArr = temp.toByteArray();
                tileParts[t][tilePart] = tempByteArr;
                length = temp.size();

                if(tilePart == 0){
                    // Edit first tile part header
                    tempByteArr[6] = (byte)(length>>>24);         // Psot
                    tempByteArr[7] = (byte)(length>>>16);
                    tempByteArr[8] = (byte)(length>>>8);
                    tempByteArr[9] = (byte)(length);
                    tempByteArr[10] = (byte)(0);                  // TPsot
                    tempByteArr[11] = (byte)(numTileParts);       // TNsot
                }else{
                    // Edit tile part header
                    tempByteArr[0] = (byte)(Markers.SOT>>>8);     // SOT
                    tempByteArr[1] = (byte)(Markers.SOT);
                    tempByteArr[2] = (byte)(0);                   // Lsot
                    tempByteArr[3] = (byte)(10);
                    tempByteArr[4] = (byte)(t >> 8);                   // Lsot
                    tempByteArr[5] = (byte)(t);                   // Isot
                    tempByteArr[6] = (byte)(length>>>24);         // Psot
                    tempByteArr[7] = (byte)(length>>>16);
                    tempByteArr[8] = (byte)(length>>>8);
                    tempByteArr[9] = (byte)(length);
                    tempByteArr[10] = (byte)(tilePart);           //TPsot
                    tempByteArr[11] = (byte)(numTileParts);       // TNsot
                }
                temp.reset();
                prem -= np;
            }
        }
        temp.close();
    }

    /**
     * This method writes the new codestream to the file.
     *
     * @param fi The file to write the new codestream to
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    private void writeNewCodestream(BufferedRandomAccessFile fi)
        throws IOException{
        int i,t,p,tp;
        int numTiles = tileParts.length;
        int[][] packetHeaderLengths = new int[numTiles][maxtp];
        byte[] temp;
        int length;

        // Write main header up to SOT marker
        fi.write(mainHeader,0,mainHeader.length);

        // If PPM used write all packet headers in PPM markers
        if(ppmUsed){
            ByteArrayOutputStream ppmMarkerSegment =
                new ByteArrayOutputStream();
            int numPackets;
            int totNumPackets;
            int ppmIndex=0;
            int ppmLength;
            int pStart,pStop;
            int prem[] = new int[numTiles];

            // Set number of remaining packets
            for(t=0 ; t<numTiles ; t++)
                prem[t] = packetHeaders[t].length;

            // Calculate Nppm values
            for(tp=0; tp<maxtp ; tp++){
                for(t=0 ; t<numTiles ; t++){

                    if(tileParts[t].length > tp){
                        totNumPackets = packetHeaders[t].length;
                        // Calculate number of packets in this tilepart
                        numPackets =
                            (tp == tileParts[t].length-1) ?
                            prem[t] : pptp;

                        pStart = totNumPackets - prem[t];
                        pStop = pStart + numPackets;

                        // Calculate number of packet header bytes for this
                        // tile part
                        for(p=pStart ; p < pStop ; p++)
                            packetHeaderLengths[t][tp] +=
                                packetHeaders[t][p].length;

                        prem[t] -= numPackets;
                    }
                }
            }

            // Write first PPM marker
            ppmMarkerSegment.write(Markers.PPM >>> 8);
            ppmMarkerSegment.write(Markers.PPM);
            ppmMarkerSegment.write(0); // Temporary Lppm value
            ppmMarkerSegment.write(0); // Temporary Lppm value
            ppmMarkerSegment.write(0); // zppm
            ppmLength = 3;
            ppmIndex++;

            // Set number of remaining packets
            for(t=0 ; t<numTiles ; t++)
                prem[t] = packetHeaders[t].length;

            // Write all PPM markers and information
            for(tp=0; tp<maxtp ; tp++){
                for(t=0 ; t<numTiles ; t++){

                    if(tileParts[t].length > tp){
                        totNumPackets = packetHeaders[t].length;

                        // Calculate number of packets in this tilepart
                        numPackets =
                            (tp == tileParts[t].length-1) ? prem[t] : pptp;

                        pStart = totNumPackets - prem[t];
                        pStop = pStart + numPackets;

                        // If Nppm value wont fit in current PPM marker segment
                        // write current PPM marker segment and start new
                        if(ppmLength + 4 > Markers.MAX_LPPM){
                            // Write current PPM marker
                            temp = ppmMarkerSegment.toByteArray();
                            length = temp.length-2;
                            temp[2] = (byte)(length >>> 8);
                            temp[3] = (byte)length;
                            fi.write(temp,0,length+2);

                            // Start new PPM marker segment
                            ppmMarkerSegment.reset();
                            ppmMarkerSegment.write(Markers.PPM >>> 8);
                            ppmMarkerSegment.write(Markers.PPM);
                            ppmMarkerSegment.write(0); // Temporary Lppm value
                            ppmMarkerSegment.write(0); // Temporary Lppm value
                            ppmMarkerSegment.write(ppmIndex++); // zppm
                            ppmLength = 3;
                        }

                        // Write Nppm value
                        length = packetHeaderLengths[t][tp];
                        ppmMarkerSegment.write(length>>>24);
                        ppmMarkerSegment.write(length>>>16);
                        ppmMarkerSegment.write(length>>>8);
                        ppmMarkerSegment.write(length);
                        ppmLength += 4;

                        // Write packet headers
                        for(p=pStart ; p < pStop ; p++){
                            length = packetHeaders[t][p].length;

                            // If next packet header value wont fit in
                            // current PPM marker segment write current PPM
                            // marker segment and start new
                            if(ppmLength + length > Markers.MAX_LPPM){
                                // Write current PPM marker
                                temp = ppmMarkerSegment.toByteArray();
                                length = temp.length-2;
                                temp[2] = (byte)(length >>> 8);
                                temp[3] = (byte)length;
                                fi.write(temp,0,length+2);

                                // Start new PPM marker segment
                                ppmMarkerSegment.reset();
                                ppmMarkerSegment.write(Markers.PPM >>> 8);
                                ppmMarkerSegment.write(Markers.PPM);
                                ppmMarkerSegment.write(0); // Temp Lppm value
                                ppmMarkerSegment.write(0); // Temp Lppm value
                                ppmMarkerSegment.write(ppmIndex++); // zppm
                                ppmLength = 3;
                            }

                            // write packet header
                            ppmMarkerSegment.write(packetHeaders[t][p],0,
                                                   packetHeaders[t][p].length);
                            ppmLength+=packetHeaders[t][p].length;
                        }
                        prem[t]-=numPackets;
                    }
                }
            }
            // Write last PPM marker segment
            temp = ppmMarkerSegment.toByteArray();
            length = temp.length-2;
            temp[2] = (byte)(length >>> 8);
            temp[3] = (byte)length;
            fi.write(temp,0,length+2);
        }

        // Write tile parts interleaved
        for(tp=0;tp<maxtp;tp++)
            for(t=0 ; t<nt ;t++){
                if(tileParts[t].length >= tp){
                    temp = tileParts[t][tp];
                    length = temp.length;
                    fi.write(temp,0,length);
                }
            }
        fi.writeShort(Markers.EOC);
    }
}





















