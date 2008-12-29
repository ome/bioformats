/*
 * $RCSfile: PktInfo.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:02 $
 * $State: Exp $
 *
 * Class:                   PktInfo
 *
 * Description:             Object containing packet informations.
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
package jj2000.j2k.codestream.reader;

import java.util.*;

/**
 * This class defines an object used to countain informations about a packet
 * to which the current code-block belongs.
 *
 * @see CBlkInfo
 * */
public class PktInfo{

    /** Index of the packet */
    public int packetIdx;

    /** The layer associated with the current code-block in this packet. */
    public int layerIdx;

    /** The code-block offset in the codestream (for this packet) */
    public int cbOff = 0;

    /** The length of the code-block in this packet (in bytes) */
    public int cbLength;

    /**
     * The length of each terminated segment in the packet. The total is the
     * same as 'cbLength'. It can be null if there is only one terminated
     * segment, in which case 'cbLength' holds the legth of that segment
     * */
    public int segLengths[];

    /**
     * The number of truncation points that appear in this packet, and all
     * previous packets, for this code-block. This is the number of passes
     * that can be decoded with the information in this packet and all
     * previous ones.
     * */
    public int numTruncPnts;

    /**
     * Classe's constructor.
     *
     * @param lyIdx The layer index for the code-block in this packet
     *
     * @param pckIdx The packet index
     * */
    public PktInfo(int lyIdx, int pckIdx){
        layerIdx = lyIdx;
        packetIdx = pckIdx;
    }

    /**
     * Object information in a string.
     *
     * @return Object information
     * */
    public String toString(){
        return "packet "+packetIdx+" (lay:"+layerIdx+", off:"+cbOff+", len:"+
            cbLength+", numTruncPnts:"+numTruncPnts+")\n";
    }
}
