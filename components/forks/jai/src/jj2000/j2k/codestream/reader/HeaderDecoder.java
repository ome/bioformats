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
 * $RCSfile: HeaderDecoder.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/09/28 00:55:20 $
 * $State: Exp $
 *
 * Class:                   HeaderDecoder
 *
 * Description:             Reads main and tile-part headers.
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
import java.awt.Point;

import jj2000.j2k.quantization.dequantizer.*;
import jj2000.j2k.wavelet.synthesis.*;
import jj2000.j2k.entropy.decoder.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.entropy.*;
import jj2000.j2k.decoder.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.roi.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;

import java.io.*;
import java.util.*;

//import colorspace.*;
//import icc.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReadParamJava;

/**
 * This class reads Main and Tile-part headers from the codestream. It is
 * created by the run() method of the Decoder instance.
 *
 * <p>A marker segment includes a marker and eventually marker segment
 * parameters. It is designed by the three letters code of the marker
 * associated with the marker segment. JPEG 2000 part 1 defines 6 types of
 * markers:
 *
 * <ul>
 * <li> Delimiting : SOC,SOT (read in FileBitstreamReaderAgent),SOD,EOC
 * (read in FileBitstreamReaderAgent).</li> <li> Fixed information: SIZ.</li>
 *
 * <li> Functional: COD,COC,RGN,QCD,QCC,POC.</li> <li> In bit-stream:
 * SOP,EPH.</li>
 *
 * <li> Pointer: TLM,PLM,PLT,PPM,PPT.</li>
 *
 * <li> Informational: CRG,COM.</li>
 * </ul>
 *
 * <p>The main header is read when the constructor is called whereas tile-part
 * headers are read when the FileBitstreamReaderAgent instance is created. The
 * reading is done in 2 passes:
 *
 * <ul>
 * <li>All marker segments are buffered and their corresponding flag is
 * activated (extractMainMarkSeg and extractTilePartMarkSeg methods).</li>
 *
 * <li>Buffered marker segment are analyzed in a logical way and
 * specifications are stored in appropriate member of DecoderSpecs instance
 * (readFoundMainMarkSeg and readFoundTilePartMarkSeg methods).</li>
 * </ul>
 *
 * <p>Whenever a marker segment is not recognized a warning message is
 * displayed and its length parameter is used to skip it.
 *
 * @see DecoderSpecs
 * @see FileBitstreamReaderAgent
 * */
public class HeaderDecoder implements ProgressionType, Markers,
                                      StdEntropyCoderOptions {

    /** The prefix for header decoder options: 'H' */
    public final static char OPT_PREFIX = 'H';

    /** The list of parameters that is accepted for quantization. Options
     * for quantization start with 'Q'. */
    private final static String [][] pinfo = null;

    /** The reference to the HeaderInfo instance holding the information found
     * in headers */
    private HeaderInfo hi;

    /** Current header information in a string */
    private String hdStr = "";

    /** The J2KImageReadParamJava instance of the decoder */
    private J2KImageReadParamJava j2krparam;

    /** The number of tiles within the image */
    private int nTiles;

    /** The number of tile parts per tile */
    public int[] nTileParts;

    /** Used to store which markers have been already read, by using flag
     * bits. The different markers are marked with XXX_FOUND flags, such as
     * SIZ_FOUND */
    private int nfMarkSeg = 0;

    /** Counts number of COC markers found in the header */
    private int nCOCMarkSeg = 0;

    /** Counts number of QCC markers found in the header */
    private int nQCCMarkSeg = 0;

    /** Counts number of COM markers found in the header */
    private int nCOMMarkSeg = 0;

    /** Counts number of RGN markers found in the header */
    private int nRGNMarkSeg = 0;

    /** Counts number of PPM markers found in the header */
    private int nPPMMarkSeg = 0;

    /** Counts number of PPT markers found in the header */
    private int[][] nPPTMarkSeg = null;

    /** Flag bit for SIZ marker segment found */
    private static final int SIZ_FOUND = 1;

    /** Flag bit for COD marker segment found */
    private static final int COD_FOUND = 1<<1;

    /** Flag bit for COC marker segment found */
    private static final int COC_FOUND = 1<<2;

    /** Flag bit for QCD marker segment found */
    private static final int QCD_FOUND = 1<<3;

    /** Flag bit for TLM marker segment found */
    private static final int TLM_FOUND = 1<<4;

    /** Flag bit for PLM marker segment found */
    private static final int PLM_FOUND = 1<<5;

    /** Flag bit for SOT marker segment found */
    private static final int SOT_FOUND = 1<<6;

    /** Flag bit for PLT marker segment found */
    private static final int PLT_FOUND = 1<<7;

    /** Flag bit for QCC marker segment found */
    private static final int QCC_FOUND = 1<<8;

    /** Flag bit for RGN marker segment found */
    private static final int RGN_FOUND = 1<<9;

    /** Flag bit for POC marker segment found */
    private static final int POC_FOUND = 1<<10;

    /** Flag bit for COM marker segment found */
    private static final int COM_FOUND = 1<<11;

    /** Flag bit for SOD marker segment found */
    public static final int SOD_FOUND = 1<<13;

    /** Flag bit for SOD marker segment found */
    public static final int PPM_FOUND = 1<<14;

    /** Flag bit for SOD marker segment found */
    public static final int PPT_FOUND = 1<<15;

    /** Flag bit for CRG marker segment found */
    public static final int CRG_FOUND = 1<<16;

    /** The reset mask for new tiles */
    private static final int TILE_RESET = ~(PLM_FOUND|SIZ_FOUND|RGN_FOUND);

    /** HashTable used to store marker segment byte buffers */
    private Hashtable ht = null;

    /** The number of components in the image */
    private int nComp;

    /** The horizontal code-block partition origin */
    private int cb0x = -1;

    /** The vertical code-block partition origin */
    private int cb0y = -1;

    /** The decoder specifications */
    private DecoderSpecs decSpec;

    /** Is the precinct partition used */
    boolean precinctPartitionIsUsed;

    /** The offset of the main header in the input stream */
    public int mainHeadOff;

    /** Vector containing info as to which tile each tilepart belong */
    public Vector tileOfTileParts;

    /** Array containing the Nppm and Ippm fields of the PPM marker segments*/
    private byte[][] pPMMarkerData;

    /** Array containing the Ippm fields of the PPT marker segments */
    private byte[][][][] tilePartPkdPktHeaders;

    /** The packed packet headers if the PPM or PPT markers are used */
    private ByteArrayOutputStream[] pkdPktHeaders;

    /** 
     * Return the maximum height among all components 
     *
     * @return Maximum component height
     * */
    public int getMaxCompImgHeight() { return hi.siz.getMaxCompHeight(); }

    /** 
     * Return the maximum width among all components 
     *
     * @return Maximum component width
     * */
    public int getMaxCompImgWidth() { return hi.siz.getMaxCompWidth(); }

    /**
     * Returns the image width in the reference grid.
     *
     * @return The image width in the reference grid
     * */
    public final int getImgWidth() { return hi.siz.xsiz-hi.siz.x0siz; }

    /**
     * Returns the image height in the reference grid.
     *
     * @return The image height in the reference grid
     * */
    public final int getImgHeight() { return hi.siz.ysiz-hi.siz.y0siz; }

    /**
     * Return the horizontal upper-left coordinate of the image in the
     * reference grid.
     *
     * @return The horizontal coordinate of the image origin.
     * */
    public final int getImgULX() { return hi.siz.x0siz; }

    /**
     * Return the vertical upper-left coordinate of the image in the reference
     * grid.
     *
     * @return The vertical coordinate of the image origin.
     * */
    public final int getImgULY() { return hi.siz.y0siz; }

    /**
     * Returns the nominal width of the tiles in the reference grid.
     *
     * @return The nominal tile width, in the reference grid.
     * */
    public final int getNomTileWidth() { return hi.siz.xtsiz; }

    /**
     * Returns the nominal width of the tiles in the reference grid.
     *
     * @return The nominal tile width, in the reference grid.
     * */
    public final int getNomTileHeight() { return hi.siz.ytsiz; }

    /**
     * Returns the tiling origin, referred to as '(Px,Py)' in the 'ImgData'
     * interface.
     *
     * @param co If not null this object is used to return the information. If
     * null a new one is created and returned.
     *
     * @return The coordinate of the tiling origin, in the canvas system, on
     * the reference grid.
     *
     * @see jj2000.j2k.image.ImgData
     * */
    public final Point getTilingOrigin(Point co) {
        if (co != null) {
            co.x = hi.siz.xt0siz;
            co.y = hi.siz.yt0siz;
            return co;
        }
        else {
            return new Point(hi.siz.xt0siz,hi.siz.yt0siz);
        }
    }

    /**
     * Returns true if the original data of the specified component was
     * signed. If the data was not signed a level shift has to be applied at
     * the end of the decompression chain.
     *
     * @param c The index of the component
     *
     * @return True if the original image component was signed.
     * */
    public final boolean isOriginalSigned(int c) {
        return hi.siz.isOrigSigned(c);
    }

    /**
     * Returns the original bitdepth of the specified component.
     *
     * @param c The index of the component
     *
     * @return The bitdepth of the component
     * */
    public final int getOriginalBitDepth(int c) {
        return hi.siz.getOrigBitDepth(c);
    }

    /**
     * Returns the number of components in the image.
     *
     * @return The number of components in the image.
     * */
    public final int getNumComps() {
        return nComp;
    }

    /**
     * Returns the component sub-sampling factor, with respect to the
     * reference grid, along the horizontal direction for the specified
     * component.
     *
     * @param c The index of the component
     *
     * @return The component sub-sampling factor X-wise.
     * */
    public final int getCompSubsX(int c) { return hi.siz.xrsiz[c]; }

    /**
     * Returns the component sub-sampling factor, with respect to the
     * reference grid, along the vertical direction for the specified
     * component.
     *
     * @param c The index of the component
     *
     * @return The component sub-sampling factor Y-wise.
     * */
    public final int getCompSubsY(int c) { return hi.siz.yrsiz[c]; }

    /**
     * Returns the dequantizer parameters. Dequantizer parameters normally are
     * the quantization step sizes, see DequantizerParams.
     *
     * @param src The source of data for the dequantizer.
     *
     * @param rb The number of range bits for each component. Must be
     * the number of range bits of the mixed components.
     *
     * @param decSpec2 The DecoderSpecs instance after any image manipulation.
     *
     * @return The dequantizer
     * */
    public final Dequantizer createDequantizer(CBlkQuantDataSrcDec src,
                                               int rb[],
                                               DecoderSpecs decSpec2) {
        return new StdDequantizer(src,rb,decSpec2);
    }

    /**
     * Returns the horizontal code-block partition origin.Allowable values are
     * 0 and 1, nothing else.
     * */
    public final int getCbULX() {
        return cb0x;
    }

    /**
     * Returns the vertical code-block partition origin. Allowable values are
     * 0 and 1, nothing else.
     * */
    public final int getCbULY() {
        return cb0y;
    }

    /**
     * Returns the precinct partition width for the specified tile-component
     * and resolution level.
     *
     * @param c the component index
     *
     * @param t the tile index
     *
     * @param rl the resolution level
     *
     * @return The precinct partition width for the specified tile-component
     * and resolution level
     * */
    public final int getPPX(int t,int c,int rl) {
        return decSpec.pss.getPPX(t,c,rl);
    }

    /**
     * Returns the precinct partition height for the specified component, tile
     * and resolution level.
     *
     * @param c the component
     *
     * @param t the tile index
     *
     * @param rl the resolution level
     *
     * @return The precinct partition height for the specified component,
     * tile and resolution level
     * */
    public final int getPPY(int t, int c, int rl) {
        return decSpec.pss.getPPY(t, c, rl);
    }

    /**
     * Returns the boolean used to know if the precinct partition is used
     **/
    public final boolean precinctPartitionUsed() {
        return precinctPartitionIsUsed;
    }

    /**
     * Reads a wavelet filter from the codestream and returns the filter
     * object that implements it.
     *
     * @param ehs The encoded header stream from where to read the info
     *
     * @param filtIdx Int array of one element to return the type of the
     * wavelet filter.
     * */
    private SynWTFilter readFilter(DataInputStream ehs,int[] filtIdx)
        throws IOException {
        int kid; // the filter id

        kid = filtIdx[0] = ehs.readUnsignedByte();
        if (kid >= (1<<7)) {
            throw new NotImplementedError("Custom filters not supported");
        }
        // Return filter based on ID
        switch (kid) {
        case FilterTypes.W9X7:
            return new SynWTFilterFloatLift9x7();
        case FilterTypes.W5X3:
            return new SynWTFilterIntLift5x3();
        default:
            throw new CorruptedCodestreamException("Specified wavelet filter "+
                                                  "not"+
                                                  " JPEG 2000 part I "+
                                                  "compliant");
        }
    }

    /**
     * Checks that the marker segment length is correct. 
     *
     * @param ehs The encoded header stream
     *
     * @param str The string identifying the marker, such as "SIZ marker"
     *
     * @exception IOException If an I/O error occurs
     * */
    public void checkMarkerLength(DataInputStream ehs, String str)
        throws IOException {
        if (ehs.available()!=0) {
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.WARNING,
                         str+" length was short, attempting to resync.");
        }
    }

    /**
     * Reads the SIZ marker segment and realigns the codestream at the point
     * where the next marker segment should be found. 
     *
     * <p>SIZ is a fixed information marker segment containing informations
     * about image and tile sizes. It is required in the main header
     * immediately after SOC.</p>
     *
     * @param ehs The encoded header stream
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoded header stream
     * */
    private void readSIZ (DataInputStream ehs) throws IOException {
        HeaderInfo.SIZ ms = hi.getNewSIZ();
        hi.siz = ms;

        // Read the length of SIZ marker segment (Lsiz)
        ms.lsiz = ehs.readUnsignedShort();

        // Read the capability of the codestream (Rsiz)
        ms.rsiz = ehs.readUnsignedShort();
        if (ms.rsiz > 2) {
            throw new Error("Codestream capabiities not JPEG 2000 - Part I"+
                            " compliant");
        }

        // Read image size
        ms.xsiz = ehs.readInt();
        ms.ysiz = ehs.readInt();
        if ( ms.xsiz<=0 || ms.ysiz<=0 ) {
            throw new IOException("JJ2000 does not support images whose "+
                                  "width and/or height not in the "+
                                  "range: 1 -- (2^31)-1");
        }

        // Read image offset
        ms.x0siz = ehs.readInt();
        ms.y0siz = ehs.readInt();
        if ( ms.x0siz<0 || ms.y0siz<0 ) {
            throw new IOException("JJ2000 does not support images offset "+
                                  "not in the range: 0 -- (2^31)-1");
        }

        // Read size of tile
        ms.xtsiz = ehs.readInt();
        ms.ytsiz = ehs.readInt();
        if ( ms.xtsiz<=0 || ms.ytsiz<=0 ) {
            throw new IOException("JJ2000 does not support tiles whose "+
                                  "width and/or height are not in  "+
                                  "the range: 1 -- (2^31)-1");
        }

        // Read upper-left tile offset
        ms.xt0siz = ehs.readInt();
        ms.yt0siz = ehs.readInt();
        if ( ms.xt0siz<0 || ms.yt0siz<0 ){
            throw new IOException("JJ2000 does not support tiles whose "+
                                  "offset is not in  "+
                                  "the range: 0 -- (2^31)-1");
        }

        // Read number of components and initialize related arrays
        nComp = ms.csiz = ehs.readUnsignedShort();
        if (nComp<1 || nComp>16384) {
            throw new IllegalArgumentException("Number of component out of "+
                                               "range 1--16384: "+nComp);
        }

        ms.ssiz = new int[nComp];
        ms.xrsiz = new int[nComp];
        ms.yrsiz = new int[nComp];

        // Read bit-depth and down-sampling factors of each component
        for(int i = 0; i<nComp; i++) {
            ms.ssiz[i] = ehs.readUnsignedByte();
            ms.xrsiz[i] = ehs.readUnsignedByte();
            ms.yrsiz[i] = ehs.readUnsignedByte();
        }

        // Check marker length
        checkMarkerLength(ehs,"SIZ marker");

        // Create needed ModuleSpec
        nTiles = ms.getNumTiles();

        // Finish initialization of decSpec
        decSpec = new DecoderSpecs(nTiles,nComp);
    }

    /** 
     * Reads a CRG marker segment and checks its length. CRG is an
     * informational marker segment that allows specific registration of
     * components with respect to each other.
     *
     * @param ehs The encoded header stream
     * */
    private void readCRG (DataInputStream ehs) throws IOException {
        HeaderInfo.CRG ms = hi.getNewCRG();
        hi.crg = ms;

        ms.lcrg = ehs.readUnsignedShort();
        ms.xcrg = new int[nComp];
        ms.ycrg = new int[nComp];

        FacilityManager.getMsgLogger().
            printmsg(MsgLogger.WARNING,"Information in CRG marker segment "+
                     "not taken into account. This may affect the display "+
                     "of the decoded image.");
        for(int c=0; c<nComp; c++) {
            ms.xcrg[c] = ehs.readUnsignedShort();
            ms.ycrg[c] = ehs.readUnsignedShort();
        }

        // Check marker length
        checkMarkerLength(ehs,"CRG marker");
    }


    /**
     * Reads a COM marker segments and realigns the bit stream at the point
     * where the next marker segment should be found. COM is an informational
     * marker segment that allows to include unstructured data in the main and
     * tile-part headers.
     *
     * @param ehs The encoded header stream
     *
     * @param mainh Flag indicating whether or not this marker segment is read
     * from the main header.
     *
     * @param tileIdx The index of the current tile
     *
     * @param comIdx Occurence of this COM marker in eith main or tile-part
     * header 
     * 
     * @exception IOException If an I/O error occurs while reading from the
     * encoded header stream
     * */
    private void readCOM (DataInputStream ehs, boolean mainh, int tileIdx,
                          int comIdx) throws IOException {
        HeaderInfo.COM ms = hi.getNewCOM();

        // Read length of COM field
        ms.lcom = ehs.readUnsignedShort();

        // Read the registration value of the COM marker segment
        ms.rcom = ehs.readUnsignedShort();
        switch(ms.rcom) {
        case RCOM_GEN_USE:
            ms.ccom = new byte[ms.lcom-4];
            for(int i=0; i<ms.lcom-4; i++) {
                ms.ccom[i] = ehs.readByte();
            }
            break;
        default:
            // --- Unknown or unsupported markers ---
            // (skip them and see if we can get way with it)
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.WARNING,
                         "COM marker registered as 0x"+Integer.
                         toHexString(ms.rcom)+
                         " unknown, ignoring (this might crash the "+
                         "decoder or decode a quality degraded or even "+
                         "useless image)");
            ehs.skipBytes(ms.lcom-4); //Ignore this field for the moment
            break;
        }

        if (mainh) {
            hi.com.put("main_"+comIdx,ms);
        } else {
            hi.com.put("t"+tileIdx+"_"+comIdx,ms);
        }

        // Check marker length
        checkMarkerLength(ehs,"COM marker");
    }

    /**
     * Reads a QCD marker segment and realigns the codestream at the point
     * where the next marker should be found. QCD is a functional marker
     * segment that describes the quantization default.
     * 
     * @param ehs The encoded stream.
     *
     * @param mainh Flag indicating whether or not this marker segment is read
     * from the main header.
     *
     * @param tileIdx The index of the current tile
     *
     * @param tpIdx Tile-part index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoded header stream.
     * */
    private void readQCD (DataInputStream ehs, boolean mainh, int tileIdx,
                          int tpIdx) throws IOException {
        StdDequantizerParams qParms;
        int guardBits;
        int[][] exp;
        float[][] nStep = null;
        HeaderInfo.QCD ms = hi.getNewQCD();

        // Lqcd (length of QCD field)
        ms.lqcd = ehs.readUnsignedShort();

        // Sqcd (quantization style)
        ms.sqcd = ehs.readUnsignedByte();

        guardBits = ms.getNumGuardBits();
        int qType = ms.getQuantType();

        if(mainh){
            hi.qcd.put("main",ms);
            // If the main header is being read set default value of
            // dequantization spec
            switch (qType) {
            case SQCX_NO_QUANTIZATION:
                decSpec.qts.setDefault("reversible");
                break;
            case SQCX_SCALAR_DERIVED:
                decSpec.qts.setDefault("derived");
                break;
            case SQCX_SCALAR_EXPOUNDED:
                decSpec.qts.setDefault("expounded");
                break;
            default:
                throw new CorruptedCodestreamException("Unknown or "+
                                                       "unsupported "+
                                                       "quantization style "+
                                                       "in Sqcd field, QCD "+
                                                       "marker main header");
            }
        } else {
            hi.qcd.put("t"+tileIdx,ms);
            // If the tile header is being read set default value of
            // dequantization spec for tile
            switch (qType) {
            case SQCX_NO_QUANTIZATION:
                decSpec.qts.setTileDef(tileIdx, "reversible");
                break;
            case SQCX_SCALAR_DERIVED:
                decSpec.qts.setTileDef(tileIdx, "derived");
                break;
            case SQCX_SCALAR_EXPOUNDED:
                decSpec.qts.setTileDef(tileIdx, "expounded");
                break;
            default:
                throw new CorruptedCodestreamException("Unknown or "+
                                                       "unsupported "+
                                                       "quantization style "+
                                                       "in Sqcd field, QCD "+
                                                       "marker, tile header");
            }
        }

        qParms = new StdDequantizerParams();

        if(qType == SQCX_NO_QUANTIZATION) {
            int maxrl =
                ( mainh ?
                  ((Integer)decSpec.dls.getDefault()).intValue() :
                  ((Integer)decSpec.dls.getTileDef(tileIdx)).intValue());
            int i,j,rl;
            int minb,maxb,hpd;
            int tmp;

            exp = qParms.exp = new int[maxrl+1][];
            ms.spqcd = new int[maxrl+1][4];

            for (rl=0; rl <= maxrl; rl++) { // Loop on resolution levels
                // Find the number of subbands in the resolution level
                if (rl == 0) { // Only the LL subband
                    minb = 0;
                    maxb = 1;
                } else {
                    // Dyadic decomposition
                    hpd = 1;

                    // Adapt hpd to resolution level
                    if (hpd > maxrl-rl) {
                        hpd -= maxrl-rl;
                    }
                    else {
                        hpd = 1;
                    }
                    // Determine max and min subband index
                    minb = 1<<((hpd-1)<<1); // minb = 4^(hpd-1)
                    maxb = 1<<(hpd<<1); // maxb = 4^hpd
                }
                // Allocate array for subbands in resolution level
                exp[rl] = new int[maxb];

                for(j=minb; j<maxb; j++) {
                    tmp = ms.spqcd[rl][j] = ehs.readUnsignedByte();
                    exp[rl][j] = (tmp>>SQCX_EXP_SHIFT)&SQCX_EXP_MASK;
                }
            }// end for rl
        } else {
            int maxrl = (qType == SQCX_SCALAR_DERIVED) ? 0 :
                ( mainh ?
                 ((Integer)decSpec.dls.getDefault()).intValue() :
                 ((Integer)decSpec.dls.getTileDef(tileIdx)).intValue());
            int i,j,rl;
            int minb,maxb,hpd;
            int tmp;

            exp = qParms.exp = new int[maxrl+1][];
            nStep = qParms.nStep = new float[maxrl+1][];
            ms.spqcd = new int[maxrl+1][4];

            for (rl=0; rl <= maxrl; rl++) { // Loop on resolution levels
                // Find the number of subbands in the resolution level
                if (rl == 0) { // Only the LL subband
                    minb = 0;
                    maxb = 1;
                } else {
                    // Dyadic decomposition
                    hpd = 1;

                    // Adapt hpd to resolution level
                    if (hpd > maxrl-rl) {
                        hpd -= maxrl-rl;
                    } else {
                        hpd = 1;
                    }
                    // Determine max and min subband index
                    minb = 1<<((hpd-1)<<1); // minb = 4^(hpd-1)
                    maxb = 1<<(hpd<<1); // maxb = 4^hpd
                }
                // Allocate array for subbands in resolution level
                exp[rl] = new int[maxb];
                nStep[rl] = new float[maxb];

                for(j=minb; j<maxb; j++) {
                    tmp = ms.spqcd[rl][j] = ehs.readUnsignedShort();
                    exp[rl][j] = (tmp>>11) & 0x1f;
                    // NOTE: the formula below does not support more than 5
                    // bits for the exponent, otherwise (-1<<exp) might
                    // overflow (the - is used to be able to represent 2**31)
                    nStep[rl][j] =
                        (-1f-((float)(tmp & 0x07ff))/(1<<11))/
                        (-1<<exp[rl][j]);
                }
            }// end for rl
        } // end if (qType != SQCX_NO_QUANTIZATION)

        // Fill qsss, gbs
        if(mainh){
            decSpec.qsss.setDefault(qParms);
            decSpec.gbs.setDefault(new Integer(guardBits));
        }
        else{
            decSpec.qsss.setTileDef(tileIdx,qParms);
            decSpec.gbs.setTileDef(tileIdx,new Integer(guardBits));
        }

        // Check marker length
        checkMarkerLength(ehs,"QCD marker");
    }

    /**
     * Reads a QCC marker segment and realigns the codestream at the point
     * where the next marker should be found. QCC is a functional marker
     * segment that describes the quantization of one component.
     * 
     * @param ehs The encoded stream.
     *
     * @param mainh Flag indicating whether or not this marker segment is read
     * from the main header.
     *
     * @param tileIdx The index of the current tile
     *
     * @param tpIdx Tile-part index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoded header stream.
     * */
    private void readQCC (DataInputStream ehs, boolean mainh, int tileIdx,
                          int tpIdx) throws IOException {
        int cComp;          // current component
        int tmp;
        StdDequantizerParams qParms;
        int[][] expC;
        float[][] nStepC = null;
        HeaderInfo.QCC ms = hi.getNewQCC();

        // Lqcc (length of QCC field)
        ms.lqcc = ehs.readUnsignedShort();

        // Cqcc
        if (nComp < 257) {
            cComp = ms.cqcc = ehs.readUnsignedByte();
        } else {
            cComp = ms.cqcc = ehs.readUnsignedShort();
        }
        if (cComp >= nComp) {
            throw new CorruptedCodestreamException("Invalid component "+
                                                   "index in QCC marker");
        }

        // Sqcc (quantization style)
        ms.sqcc = ehs.readUnsignedByte();
        int guardBits = ms.getNumGuardBits();
        int qType = ms.getQuantType();

        if(mainh) {
            hi.qcc.put("main_c"+cComp,ms);
            // If main header is being read, set default for component in all
            // tiles
            switch (qType) {
            case SQCX_NO_QUANTIZATION:
                decSpec.qts.setCompDef(cComp,"reversible");
                break;
            case SQCX_SCALAR_DERIVED:
                decSpec.qts.setCompDef(cComp,"derived");
                break;
            case SQCX_SCALAR_EXPOUNDED:
                decSpec.qts.setCompDef(cComp,"expounded");
                break;
            default:
                throw new CorruptedCodestreamException("Unknown or "+
                                                       "unsupported "+
                                                       "quantization style "+
                                                       "in Sqcd field, QCD "+
                                                       "marker, main header");
            }
        } else {
            hi.qcc.put("t"+tileIdx+"_c"+cComp,ms);
            // If tile header is being read, set value for component in
            // this tiles
            switch (qType) {
            case SQCX_NO_QUANTIZATION:
                decSpec.qts.setTileCompVal(tileIdx, cComp,"reversible");
                break;
            case SQCX_SCALAR_DERIVED:
                decSpec.qts.setTileCompVal(tileIdx, cComp,"derived");
                break;
            case SQCX_SCALAR_EXPOUNDED:
                decSpec.qts.setTileCompVal(tileIdx, cComp,"expounded");
                break;
            default:
                throw new CorruptedCodestreamException("Unknown or "+
                                                       "unsupported "+
                                                       "quantization style "+
                                                       "in Sqcd field, QCD "+
                                                       "marker, main header");
            }
        }

        // Decode all dequantizer params
        qParms = new StdDequantizerParams();

        if (qType == SQCX_NO_QUANTIZATION) {
            int maxrl = ( mainh ?
                          ((Integer)decSpec.dls.getCompDef(cComp)).intValue() :
                          ((Integer)decSpec.dls.getTileCompVal(tileIdx,cComp)).
                          intValue());
            int i,j,rl;
            int minb,maxb,hpd;

            expC = qParms.exp = new int[maxrl+1][];
            ms.spqcc = new int[maxrl+1][4];

            for (rl=0; rl <= maxrl; rl++) { // Loop on resolution levels
                // Find the number of subbands in the resolution level
                if (rl == 0) { // Only the LL subband
                    minb = 0;
                    maxb = 1;
                } else {
                    // Dyadic decomposition
                    hpd = 1;

                    // Adapt hpd to resolution level
                    if (hpd > maxrl-rl) {
                        hpd -= maxrl-rl;
                    } else {
                        hpd = 1;
                    }
                    // Determine max and min subband index
                    minb = 1<<((hpd-1)<<1); // minb = 4^(hpd-1)
                    maxb = 1<<(hpd<<1); // maxb = 4^hpd
                }
                // Allocate array for subbands in resolution level
                expC[rl] = new int[maxb];

                for(j=minb; j<maxb; j++) {
                    tmp = ms.spqcc[rl][j] = ehs.readUnsignedByte();
                    expC[rl][j] = (tmp>>SQCX_EXP_SHIFT)&SQCX_EXP_MASK;
                }
            }// end for rl
        } else {
            int maxrl = (qType == SQCX_SCALAR_DERIVED) ? 0 :
                ( mainh ?
                 ((Integer)decSpec.dls.getCompDef(cComp)).intValue() :
                 ((Integer)decSpec.dls.getTileCompVal(tileIdx,cComp)).
                  intValue());
            int i,j,rl;
            int minb,maxb,hpd;

            nStepC = qParms.nStep = new float[maxrl+1][];
            expC =  qParms.exp = new int[maxrl+1][];
            ms.spqcc = new int[maxrl+1][4];

            for (rl=0; rl <= maxrl; rl++) { // Loop on resolution levels
                // Find the number of subbands in the resolution level
                if (rl == 0) { // Only the LL subband
                    minb = 0;
                    maxb = 1;
                } else {
                    // Dyadic decomposition
                    hpd = 1;

                    // Adapt hpd to resolution level
                    if (hpd > maxrl-rl) {
                        hpd -= maxrl-rl;
                    } else {
                        hpd = 1;
                    }
                    // Determine max and min subband index
                    minb = 1<<((hpd-1)<<1); // minb = 4^(hpd-1)
                    maxb = 1<<(hpd<<1); // maxb = 4^hpd
                }
                // Allocate array for subbands in resolution level
                expC[rl] = new int[maxb];
                nStepC[rl] = new float[maxb];

                for(j=minb; j<maxb; j++) {
                    tmp = ms.spqcc[rl][j] = ehs.readUnsignedShort();
                    expC[rl][j] = (tmp>>11) & 0x1f;
                    // NOTE: the formula below does not support more than 5
                    // bits for the exponent, otherwise (-1<<exp) might
                    // overflow (the - is used to be able to represent 2**31)
                    nStepC[rl][j] =
                        (-1f-((float)(tmp & 0x07ff))/(1<<11))/
                        (-1<<expC[rl][j]);
                }
            }// end for rl
        } // end if (qType != SQCX_NO_QUANTIZATION)

        // Fill qsss, gbs
        if(mainh){
            decSpec.qsss.setCompDef(cComp,qParms);
            decSpec.gbs.setCompDef(cComp,new Integer(guardBits));
        }
        else{
            decSpec.qsss.setTileCompVal(tileIdx,cComp,qParms);
            decSpec.gbs.setTileCompVal(tileIdx,cComp,new Integer(guardBits));
        }

        // Check marker length
        checkMarkerLength(ehs,"QCC marker");
    }

    /**
     * Reads a COD marker segment and realigns the codestream where the next
     * marker should be found. 
     *
     * @param ehs The encoder header stream.
     *
     * @param mainh Flag indicating whether or not this marker segment is read
     * from the main header.
     *
     * @param tileIdx The index of the current tile
     *
     * @param tpIdx Tile-part index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readCOD (DataInputStream ehs, boolean mainh, int tileIdx,
                          int tpIdx) throws IOException {
        int cstyle;         // The block style
        SynWTFilter hfilters[],vfilters[];
        int l;
        Integer cblk[];
        String errMsg;
        boolean sopUsed = false;
        boolean ephUsed = false;
        HeaderInfo.COD ms = hi.getNewCOD();

        // Lcod (marker length)
        ms.lcod = ehs.readUnsignedShort();

        // Scod (block style)
        // We only support wavelet transformed data
        cstyle = ms.scod = ehs.readUnsignedByte();

        if( (cstyle&SCOX_PRECINCT_PARTITION) != 0 ){
            precinctPartitionIsUsed = true;
            // Remove flag
            cstyle &= ~(SCOX_PRECINCT_PARTITION);
        } else {
            precinctPartitionIsUsed = false;
        }

        // SOP markers
        if (mainh) {
            hi.cod.put("main",ms);

            if( (cstyle&SCOX_USE_SOP) != 0 ){
                // SOP markers are used
                decSpec.sops.setDefault(new Boolean("true"));
                sopUsed = true;
                // Remove flag
                cstyle &= ~(SCOX_USE_SOP);
            } else {
                // SOP markers are not used
                decSpec.sops.setDefault(new Boolean("false"));
            }
        } else {
            hi.cod.put("t"+tileIdx,ms);

            if( (cstyle&SCOX_USE_SOP) != 0 ){
                // SOP markers are used
                decSpec.sops.setTileDef(tileIdx, new Boolean("true"));
                sopUsed = true;
                // Remove flag
                cstyle &= ~(SCOX_USE_SOP);
            }
            else {
                // SOP markers are not used
                decSpec.sops.setTileDef(tileIdx, new Boolean("false"));
            }
        }

        // EPH markers
        if (mainh) {
            if( (cstyle&SCOX_USE_EPH) != 0 ){
                // EPH markers are used
                decSpec.ephs.setDefault(new Boolean("true"));
                ephUsed = true;
                // Remove flag
                cstyle &= ~(SCOX_USE_EPH);
            } else {
                // EPH markers are not used
                decSpec.ephs.setDefault(new Boolean("false"));
            }
        } else {
            if( (cstyle&SCOX_USE_EPH) != 0 ){
                // EPH markers are used
                decSpec.ephs.setTileDef(tileIdx, new Boolean("true"));
                ephUsed = true;
                // Remove flag
                cstyle &= ~(SCOX_USE_EPH);
            } else {
                // EPH markers are not used
                decSpec.ephs.setTileDef(tileIdx, new Boolean("false"));
            }
        }

        // Code-block partition origin
        if( (cstyle&(SCOX_HOR_CB_PART|SCOX_VER_CB_PART)) != 0) {
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.WARNING,"Code-block partition origin "+
                         "different from (0,0). This is defined in JPEG 2000"+
                         " part 2 and may not be supported by all JPEG "+
                         "2000 decoders.");
        }
        if( (cstyle&SCOX_HOR_CB_PART)!= 0) {
            if(cb0x!=-1 && cb0x==0) {
                throw new IllegalArgumentException("Code-block partition "+
                                                   "origin redefined in new"+
                                                   " COD marker segment. Not"+
                                                   " supported by JJ2000");
            }
            cb0x = 1;
            cstyle &= ~(SCOX_HOR_CB_PART);
        } else {
            if(cb0x!=-1 && cb0x==1) {
                throw new IllegalArgumentException("Code-block partition "+
                                                   "origin redefined in new"+
                                                   " COD marker segment. Not"+
                                                   " supported by JJ2000");
            }
            cb0x = 0;
        }
        if( (cstyle&SCOX_VER_CB_PART)!= 0) {
            if(cb0y!=-1 && cb0y==0) {
                throw new IllegalArgumentException("Code-block partition "+
                                                   "origin redefined in new"+
                                                   " COD marker segment. Not"+
                                                   " supported by JJ2000");
            }
            cb0y = 1;
            cstyle &= ~(SCOX_VER_CB_PART);
        } else {
            if(cb0y!=-1 && cb0y==1) {
                throw new IllegalArgumentException("Code-block partition "+
                                                   "origin redefined in new"+
                                                   " COD marker segment. Not"+
                                                   " supported by JJ2000");
            }
            cb0y = 0;
        }

        // SGcod
        // Read the progressive order
        ms.sgcod_po = ehs.readUnsignedByte();

        // Read the number of layers
        ms.sgcod_nl = ehs.readUnsignedShort();
        if (ms.sgcod_nl<=0 || ms.sgcod_nl>65535 ) {
            throw new CorruptedCodestreamException("Number of layers out of "+
                                                   "range: 1--65535");
        }

        // Multiple component transform
        ms.sgcod_mct = ehs.readUnsignedByte();

        // SPcod
        // decomposition levels
        int mrl = ms.spcod_ndl = ehs.readUnsignedByte();
        if( mrl>32 ){
            throw new CorruptedCodestreamException("Number of decomposition "+
                                                   "levels out of range: "+
                                                   "0--32");
        }

        // Read the code-blocks dimensions
        cblk = new Integer[2];
        ms.spcod_cw = ehs.readUnsignedByte();
        cblk[0] = new Integer(1<<(ms.spcod_cw+2));
        if ( cblk[0].intValue() < StdEntropyCoderOptions.MIN_CB_DIM ||
             cblk[0].intValue() > StdEntropyCoderOptions.MAX_CB_DIM  ) {
            errMsg = "Non-valid code-block width in SPcod field, "+
                "COD marker";
            throw new CorruptedCodestreamException(errMsg);
        }
        ms.spcod_ch = ehs.readUnsignedByte();
        cblk[1] = new Integer(1<<(ms.spcod_ch+2));
        if ( cblk[1].intValue() < StdEntropyCoderOptions.MIN_CB_DIM ||
             cblk[1].intValue() > StdEntropyCoderOptions.MAX_CB_DIM ) {
            errMsg = "Non-valid code-block height in SPcod field, "+
                "COD marker";
            throw new CorruptedCodestreamException(errMsg);
        }
        if ( (cblk[0].intValue()*cblk[1].intValue()) >
             StdEntropyCoderOptions.MAX_CB_AREA ) {
            errMsg = "Non-valid code-block area in SPcod field, "+
                "COD marker";
            throw new CorruptedCodestreamException(errMsg);
        }
        if ( mainh ) {
            decSpec.cblks.setDefault(cblk);
        }
        else {
            decSpec.cblks.setTileDef(tileIdx, cblk);
        }

        // Style of the code-block coding passes
        int ecOptions = ms.spcod_cs = ehs.readUnsignedByte();
        if ((ecOptions &
             ~(OPT_BYPASS|OPT_RESET_MQ|OPT_TERM_PASS|
               OPT_VERT_STR_CAUSAL|OPT_PRED_TERM | OPT_SEG_SYMBOLS)) != 0){
            throw
                new CorruptedCodestreamException("Unknown \"code-block "+
                                                "style\" in SPcod field, "+
                                                "COD marker: 0x"+
                                                Integer.
                                                 toHexString(ecOptions));
        }

        // Read wavelet filter for tile or image
        hfilters = new SynWTFilter[1];
        vfilters = new SynWTFilter[1];
        hfilters[0] = readFilter(ehs,ms.spcod_t);
        vfilters[0] = hfilters[0];

        // Fill the filter spec
        // If this is the main header, set the default value, if it is the
        // tile header, set default for this tile 
        SynWTFilter[][] hvfilters = new SynWTFilter[2][];
        hvfilters[0]=hfilters;
        hvfilters[1]=vfilters;

        // Get precinct partition sizes
        Vector v[] = new Vector[2];
        v[0] = new Vector();
        v[1] = new Vector();
        int val = PRECINCT_PARTITION_DEF_SIZE;
        if ( !precinctPartitionIsUsed ) {
            Integer w, h;
            w = new Integer(1<<(val & 0x000F));
            v[0].addElement(w);
            h = new Integer(1<<(((val & 0x00F0)>>4)));
            v[1].addElement(h);
        } else {
            ms.spcod_ps = new int[mrl+1];
            for (int rl=mrl ;rl>=0 ;rl--) {
                Integer w, h;
                val = ms.spcod_ps[mrl-rl] = ehs.readUnsignedByte();
                w = new Integer(1<<(val & 0x000F));
                v[0].insertElementAt(w,0);
                h = new Integer(1<<(((val & 0x00F0)>>4)));
                v[1].insertElementAt(h,0);
            }
        }
        if (mainh) {
            decSpec.pss.setDefault(v);
        } else {
            decSpec.pss.setTileDef(tileIdx, v);
        }
        precinctPartitionIsUsed = true;

        // Check marker length
        checkMarkerLength(ehs,"COD marker");

        // Store specifications in decSpec
        if(mainh){
            decSpec.wfs.setDefault(hvfilters);
            decSpec.dls.setDefault(new Integer(mrl));
            decSpec.ecopts.setDefault(new Integer(ecOptions));
            decSpec.cts.setDefault(new Integer(ms.sgcod_mct));
            decSpec.nls.setDefault(new Integer(ms.sgcod_nl));
            decSpec.pos.setDefault(new Integer(ms.sgcod_po));
        }
        else{
            decSpec.wfs.setTileDef(tileIdx, hvfilters);
            decSpec.dls.setTileDef(tileIdx,new Integer(mrl));
            decSpec.ecopts.setTileDef(tileIdx,new Integer(ecOptions));
            decSpec.cts.setTileDef(tileIdx,new Integer(ms.sgcod_mct));
            decSpec.nls.setTileDef(tileIdx,new Integer(ms.sgcod_nl));
            decSpec.pos.setTileDef(tileIdx,new Integer(ms.sgcod_po));
        }
    }

    /**
     * Reads the COC marker segment and realigns the codestream where the next
     * marker should be found.
     *
     * @param ehs The encoder header stream.
     *
     * @param mainh Flag indicating whether or not this marker segment is read
     * from the main header.
     *
     * @param tileIdx The index of the current tile
     *
     * @param tpIdx Tile-part index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readCOC (DataInputStream ehs, boolean mainh, int tileIdx,
                          int tpIdx) throws IOException {
        int cComp;         // current component
        SynWTFilter hfilters[],vfilters[];
        int tmp,l;
        int ecOptions;
        Integer cblk[];
        String errMsg;
        HeaderInfo.COC ms = hi.getNewCOC();

        // Lcoc (marker length)
        ms.lcoc = ehs.readUnsignedShort();

        // Ccoc
        if (nComp < 257) {
            cComp = ms.ccoc = ehs.readUnsignedByte();
        } else {
            cComp = ms.ccoc = ehs.readUnsignedShort();
        }
        if (cComp >= nComp) {
            throw new CorruptedCodestreamException("Invalid component index "+
                                                   "in QCC marker");
        }

        // Scoc (block style)
        int cstyle = ms.scoc = ehs.readUnsignedByte();
        if( (cstyle&SCOX_PRECINCT_PARTITION) != 0 ){
            precinctPartitionIsUsed = true;
            // Remove flag
            cstyle &= ~(SCOX_PRECINCT_PARTITION);
        } else {
            precinctPartitionIsUsed = false;
        }

        // SPcoc

        // decomposition levels
        int mrl = ms.spcoc_ndl = ehs.readUnsignedByte();

        // Read the code-blocks dimensions
        cblk = new Integer[2];
        ms.spcoc_cw = ehs.readUnsignedByte();
        cblk[0] = new Integer(1<<(ms.spcoc_cw+2));
        if ( cblk[0].intValue() < StdEntropyCoderOptions.MIN_CB_DIM ||
             cblk[0].intValue() > StdEntropyCoderOptions.MAX_CB_DIM  ) {
            errMsg = "Non-valid code-block width in SPcod field, "+
                "COC marker";
            throw new CorruptedCodestreamException(errMsg);
        }
        ms.spcoc_ch = ehs.readUnsignedByte();
        cblk[1] = new Integer(1<<(ms.spcoc_ch+2));
        if ( cblk[1].intValue() < StdEntropyCoderOptions.MIN_CB_DIM ||
             cblk[1].intValue() > StdEntropyCoderOptions.MAX_CB_DIM ) {
            errMsg = "Non-valid code-block height in SPcod field, "+
                "COC marker";
            throw new CorruptedCodestreamException(errMsg);
        }
        if ( (cblk[0].intValue()*cblk[1].intValue()) >
             StdEntropyCoderOptions.MAX_CB_AREA ) {
            errMsg = "Non-valid code-block area in SPcod field, "+
                "COC marker";
            throw new CorruptedCodestreamException(errMsg);
        }
        if ( mainh ) {
            decSpec.cblks.setCompDef(cComp,cblk);
        } else {
            decSpec.cblks.setTileCompVal(tileIdx,cComp,cblk);
        }

        // Read entropy block mode options
        // NOTE: currently OPT_SEG_SYMBOLS is not included here
        ecOptions = ms.spcoc_cs = ehs.readUnsignedByte();
        if ((ecOptions &
             ~(OPT_BYPASS|OPT_RESET_MQ|OPT_TERM_PASS|
               OPT_VERT_STR_CAUSAL|OPT_PRED_TERM|OPT_SEG_SYMBOLS)) != 0){
            throw
                new CorruptedCodestreamException("Unknown \"code-block "+
                                                 "context\" in SPcoc field, "+
                                                 "COC marker: 0x"+
                                                 Integer.
                                                 toHexString(ecOptions));
        }

        // Read wavelet filter for tile or image
        hfilters = new SynWTFilter[1];
        vfilters = new SynWTFilter[1];
        hfilters[0] = readFilter(ehs,ms.spcoc_t);
        vfilters[0] = hfilters[0];

        // Fill the filter spec
        // If this is the main header, set the default value, if it is the
        // tile header, set default for this tile 
        SynWTFilter[][] hvfilters = new SynWTFilter[2][];
        hvfilters[0]=hfilters;
        hvfilters[1]=vfilters;

        // Get precinct partition sizes
        Vector v[] = new Vector[2];
        v[0] = new Vector();
        v[1] = new Vector();
        int val = PRECINCT_PARTITION_DEF_SIZE;
        if ( !precinctPartitionIsUsed ) {
            Integer w, h;
            w = new Integer(1<<(val & 0x000F));
            v[0].addElement(w);
            h = new Integer(1<<(((val & 0x00F0)>>4)));
            v[1].addElement(h);
        } else {
            ms.spcoc_ps = new int[mrl+1];
            for ( int rl=mrl ; rl>=0 ; rl-- ) {
                Integer w, h;
                val = ms.spcoc_ps[rl] = ehs.readUnsignedByte();
                w = new Integer(1<<(val & 0x000F));
                v[0].insertElementAt(w,0);
                h = new Integer(1<<(((val & 0x00F0)>>4)));
                v[1].insertElementAt(h,0);
            }
        }
        if (mainh) {
            decSpec.pss.setCompDef(cComp,v);
        } else {
            decSpec.pss.setTileCompVal(tileIdx,cComp,v);
        }
        precinctPartitionIsUsed = true;

        // Check marker length
        checkMarkerLength(ehs,"COD marker");

        if(mainh){
            hi.coc.put("main_c"+cComp,ms);
            decSpec.wfs.setCompDef(cComp,hvfilters);
            decSpec.dls.setCompDef(cComp,new Integer(mrl));
            decSpec.ecopts.setCompDef(cComp,new Integer(ecOptions));
        } else {
            hi.coc.put("t"+tileIdx+"_c"+cComp,ms);
            decSpec.wfs.setTileCompVal(tileIdx,cComp,hvfilters);
            decSpec.dls.setTileCompVal(tileIdx,cComp,new Integer(mrl));
            decSpec.ecopts.setTileCompVal(tileIdx,cComp,
                                          new Integer(ecOptions));
        }
    }

    /** 
     * Reads the POC marker segment and realigns the codestream where the next
     * marker should be found.
     *
     * @param ehs The encoder header stream.
     *
     * @param mainh Flag indicating whether or not this marker segment is read
     * from the main header.
     *
     * @param t The index of the current tile
     *
     * @param tpIdx Tile-part index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readPOC(DataInputStream ehs,boolean mainh,int t,int tpIdx)
        throws IOException {

        boolean useShort = (nComp>=256) ? true : false;
        int tmp;
        int nOldChg = 0;
        HeaderInfo.POC ms;
        if(mainh || hi.poc.get("t"+t)==null) {
            ms = hi.getNewPOC();
        } else {
            ms = (HeaderInfo.POC)hi.poc.get("t"+t);
            nOldChg = ms.rspoc.length;
        }

        // Lpoc
        ms.lpoc = ehs.readUnsignedShort();

        // Compute the number of new progression changes
        // newChg = (lpoc - Lpoc(2)) / (RSpoc(1) + CSpoc(2) +
        //  LYEpoc(2) + REpoc(1) + CEpoc(2) + Ppoc (1) )
        int newChg = (ms.lpoc-2)/(5+ (useShort?4:2));
        int ntotChg = nOldChg+newChg;

        int[][] change;
        if(nOldChg!=0) {
            // Creates new arrays
            change = new int[ntotChg][6];
            int[] tmprspoc = new int[ntotChg];
            int[] tmpcspoc = new int[ntotChg];
            int[] tmplyepoc = new int[ntotChg];
            int[] tmprepoc = new int[ntotChg];
            int[] tmpcepoc = new int[ntotChg];
            int[] tmpppoc = new int[ntotChg];

            // Copy old values
            int[][] prevChg = (int[][])decSpec.pcs.getTileDef(t);
            for(int chg=0; chg<nOldChg; chg++) {
                change[chg] = prevChg[chg];
                tmprspoc[chg] = ms.rspoc[chg];
                tmpcspoc[chg] = ms.cspoc[chg];
                tmplyepoc[chg] = ms.lyepoc[chg];
                tmprepoc[chg] = ms.repoc[chg];
                tmpcepoc[chg] = ms.cepoc[chg];
                tmpppoc[chg] = ms.ppoc[chg];
            }
            ms.rspoc = tmprspoc;
            ms.cspoc = tmpcspoc;
            ms.lyepoc = tmplyepoc;
            ms.repoc = tmprepoc;
            ms.cepoc = tmpcepoc;
            ms.ppoc = tmpppoc;
        } else {
            change = new int[newChg][6];
            ms.rspoc = new int[newChg];
            ms.cspoc = new int[newChg];
            ms.lyepoc = new int[newChg];
            ms.repoc = new int[newChg];
            ms.cepoc = new int[newChg];
            ms.ppoc = new int[newChg];
        }

        for(int chg=nOldChg; chg<ntotChg; chg++) {
            // RSpoc
            change[chg][0] = ms.rspoc[chg] = ehs.readUnsignedByte();

            // CSpoc
            if(useShort) {
                change[chg][1] = ms.cspoc[chg] = ehs.readUnsignedShort();
            } else {
                change[chg][1] = ms.cspoc[chg] = ehs.readUnsignedByte();
            }

            // LYEpoc
            change[chg][2] = ms.lyepoc[chg] = ehs.readUnsignedShort();
            if(change[chg][2]<1) {
                throw new CorruptedCodestreamException
                    ("LYEpoc value must be greater than 1 in POC marker "+
                     "segment of tile "+t+", tile-part "+tpIdx);
            }

            // REpoc
            change[chg][3] = ms.repoc[chg] = ehs.readUnsignedByte();
            if(change[chg][3]<=change[chg][0]) {
                throw new CorruptedCodestreamException
                    ("REpoc value must be greater than RSpoc in POC marker "+
                     "segment of tile "+t+", tile-part "+tpIdx);
            }

            // CEpoc
            if(useShort) {
                change[chg][4] = ms.cepoc[chg] = ehs.readUnsignedShort();
            } else {
                tmp = ms.cepoc[chg] = ehs.readUnsignedByte();
                if(tmp==0) {
                    change[chg][4] = 0;
                } else {
                    change[chg][4] = tmp;
                }
            }
            if(change[chg][4]<=change[chg][1]) {
                throw new CorruptedCodestreamException
                    ("CEpoc value must be greater than CSpoc in POC marker "+
                     "segment of tile "+t+", tile-part "+tpIdx);
            }

            // Ppoc
            change[chg][5] = ms.ppoc[chg] = ehs.readUnsignedByte();
        }

        // Check marker length
        checkMarkerLength(ehs,"POC marker");

        // Register specifications
        if(mainh) {
            hi.poc.put("main",ms);
            decSpec.pcs.setDefault(change);
        } else {
            hi.poc.put("t"+t,ms);
            decSpec.pcs.setTileDef(t,change);
        }
    }

    /**
     * Reads TLM marker segment and realigns the codestream where the next
     * marker should be found. Informations stored in these fields are
     * currently NOT taken into account.
     *
     * @param ehs The encoder header stream.
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readTLM(DataInputStream ehs) throws IOException {
	int length;

	length = ehs.readUnsignedShort();
	//Ignore all informations contained
	ehs.skipBytes(length-2);

        FacilityManager.getMsgLogger().
            printmsg(MsgLogger.INFO,"Skipping unsupported TLM marker");
    }

    /**
     * Reads PLM marker segment and realigns the codestream where the next
     * marker should be found. Informations stored in these fields are
     * currently not taken into account.
     *
     * @param ehs The encoder header stream.
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readPLM(DataInputStream ehs) throws IOException{
	int length;

	length = ehs.readUnsignedShort();
	//Ignore all informations contained
	ehs.skipBytes(length-2);

        FacilityManager.getMsgLogger().
            printmsg(MsgLogger.INFO,"Skipping unsupported PLM marker");
    }

    /**
     * Reads the PLT fields and realigns the codestream where the next marker
     * should be found. Informations stored in these fields are currently NOT
     * taken into account.
     *
     * @param ehs The encoder header stream.
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readPLTFields(DataInputStream ehs) throws IOException{
	int length;

	length = ehs.readUnsignedShort();
	//Ignore all informations contained
	ehs.skipBytes(length-2);

        FacilityManager.getMsgLogger().
            printmsg(MsgLogger.INFO,"Skipping unsupported PLT marker");
    }

    /**
     * Reads the RGN marker segment of the codestream header.
     *
     * <p>May be used in tile or main header. If used in main header, it
     * refers to the maxshift value of a component in all tiles. When used in
     * tile header, only the particular tile-component is affected.</p>
     *
     * @param ehs The encoder header stream.
     *
     * @param mainh Flag indicating whether or not this marker segment is read
     * from the main header.
     *
     * @param tileIdx The index of the current tile
     *
     * @param tpIdx Tile-part index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readRGN(DataInputStream ehs, boolean mainh, int tileIdx,
                         int tpIdx) throws IOException {
        int comp;           // ROI component
        int i;              // loop variable
        int tempComp;       // Component for
        HeaderInfo.RGN ms = hi.getNewRGN();

        // Lrgn (marker length)
        ms.lrgn = ehs.readUnsignedShort();

        // Read component
        ms.crgn = comp = (nComp < 257) ? ehs.readUnsignedByte():
            ehs.readUnsignedShort();
        if (comp >= nComp) {
            throw new CorruptedCodestreamException("Invalid component "+
                                                  "index in RGN marker"+
                                                  comp);
        }

        // Read type of RGN.(Srgn) 
        ms.srgn = ehs.readUnsignedByte();

        // Check that we can handle it.
        if(ms.srgn != SRGN_IMPLICIT)
            throw new CorruptedCodestreamException("Unknown or unsupported "+
                                                  "Srgn parameter in ROI "+
                                                  "marker");

        if(decSpec.rois==null) { // No maxshift spec defined
            // Create needed ModuleSpec
            decSpec.rois=new MaxShiftSpec(nTiles,nComp,
                                          ModuleSpec.SPEC_TYPE_TILE_COMP, "null");
        }

        // SPrgn
        ms.sprgn = ehs.readUnsignedByte();

        if(mainh) {
            hi.rgn.put("main_c"+comp,ms);
            decSpec.rois.setCompDef(comp, new Integer(ms.sprgn));
        } else {
            hi.rgn.put("t"+tileIdx+"_c"+comp,ms);
            decSpec.rois.setTileCompVal(tileIdx,comp,new Integer(ms.sprgn));
        }

        // Check marker length
        checkMarkerLength(ehs,"RGN marker");
    }

    /**
     * Reads the PPM marker segment of the main header.
     *
     * @param ehs The encoder header stream.
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readPPM(DataInputStream ehs) throws IOException {
        int curMarkSegLen;
        int i,indx,len,off;
        int remSegLen;
        byte[] b;

        // If first time readPPM method is called allocate arrays for packed
        // packet data
        if(pPMMarkerData==null) {
            pPMMarkerData = new byte[nPPMMarkSeg][];
            tileOfTileParts = new Vector();
            decSpec.pphs.setDefault(new Boolean(true));
        }

        // Lppm (marker length)
        curMarkSegLen = ehs.readUnsignedShort();
        remSegLen = curMarkSegLen - 3;

        // Zppm (index of PPM marker)
        indx = ehs.readUnsignedByte();

        // Read Nppm and Ippm data 
        pPMMarkerData[indx] = new byte[remSegLen];
        ehs.read(pPMMarkerData[indx],0,remSegLen);

        // Check marker length
        checkMarkerLength(ehs,"PPM marker");
    }

    /**
     * Teads the PPT marker segment of the main header.
     *
     * @param ehs The encoder header stream.
     *
     * @param tile The tile to which the current tile part belongs
     *
     * @param tpIdx Tile-part index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void readPPT(DataInputStream ehs,int tile,int tpIdx)
        throws IOException {
        int curMarkSegLen;
        int indx,len=0;
        byte[] temp;

        if(tilePartPkdPktHeaders == null){
            tilePartPkdPktHeaders = new byte[nTiles][][][];
        }

        if(tilePartPkdPktHeaders[tile] == null){
            tilePartPkdPktHeaders[tile] = new byte[nTileParts[tile]][][];
        }

        if(tilePartPkdPktHeaders[tile][tpIdx] == null){
            tilePartPkdPktHeaders[tile][tpIdx] =
                new byte[nPPTMarkSeg[tile][tpIdx]][];
        }

        // Lppt (marker length)
        curMarkSegLen = ehs.readUnsignedShort();

        // Zppt (index of PPT marker)
        indx = ehs.readUnsignedByte();

        // Ippt (packed packet headers)
        temp = new byte[curMarkSegLen-3];
        ehs.read(temp);
        tilePartPkdPktHeaders[tile][tpIdx][indx]=temp;

        // Check marker length
        checkMarkerLength(ehs,"PPT marker");

        decSpec.pphs.setTileDef(tile, new Boolean(true));
    }

    /** 
     * This method extract a marker segment from the main header and stores it
     * into a byte buffer for the second pass. The marker segment is first
     * identified. Then its flag is activated. Finally, its content is
     * buffered into a byte array stored in an hashTable.
     *
     * <p>If the marker is not recognized, it prints a warning and skips it
     * according to its length.</p>
     *
     * <p>SIZ marker segment shall be the first encountered marker segment.</p>
     *
     * @param marker The marker segment to process
     *
     * @param ehs The encoded header stream
     * */
    private void extractMainMarkSeg(short marker,RandomAccessIO ehs)
        throws IOException {
        if(nfMarkSeg == 0) { // First non-delimiting marker of the header
            // JPEG 2000 part 1 specify that it must be SIZ
            if(marker != SIZ) {
                throw new CorruptedCodestreamException("First marker after "+
                                                       "SOC "+
                                                       "must be SIZ "+
                                                       Integer.
                                                       toHexString(marker));
            }
        }

        String htKey=""; // Name used as a key for the hash-table
        if(ht==null) {
            ht = new Hashtable();
        }

        switch(marker){
        case SIZ:
            if ((nfMarkSeg & SIZ_FOUND) != 0) {
                throw
                  new CorruptedCodestreamException("More than one SIZ marker "+
                                                   "segment found in main "+
                                                   "header");
            }
            nfMarkSeg |= SIZ_FOUND;
            htKey = "SIZ";
            break;
        case SOD:
            throw new CorruptedCodestreamException("SOD found in main header");
        case EOC:
            throw new CorruptedCodestreamException("EOC found in main header");
        case SOT:
            if ((nfMarkSeg & SOT_FOUND) != 0) {
                throw new CorruptedCodestreamException("More than one SOT "+
                                                       "marker "+
                                                       "found right after "+
                                                       "main "+
                                                       "or tile header");
            }
            nfMarkSeg |= SOT_FOUND;
            return;
        case COD:
            if((nfMarkSeg & COD_FOUND) != 0) {
                throw new CorruptedCodestreamException("More than one COD "+
                                                      "marker "+
                                                      "found in main header");
            }
            nfMarkSeg |= COD_FOUND;
            htKey = "COD";
            break;
        case COC:
            nfMarkSeg |= COC_FOUND;
            htKey = "COC"+(nCOCMarkSeg++);
            break;
        case QCD:
            if((nfMarkSeg & QCD_FOUND) != 0) {
                throw new CorruptedCodestreamException("More than one QCD "+
                                                      "marker "+
                                                      "found in main header");
            }
            nfMarkSeg |= QCD_FOUND;
            htKey = "QCD";
            break;
        case QCC:
            nfMarkSeg |= QCC_FOUND;
            htKey = "QCC"+(nQCCMarkSeg++);
            break;
        case RGN:
            nfMarkSeg |= RGN_FOUND;
            htKey = "RGN"+(nRGNMarkSeg++);
            break;
        case COM:
            nfMarkSeg |= COM_FOUND;
            htKey = "COM"+(nCOMMarkSeg++);
            break;
        case CRG:
            if((nfMarkSeg & CRG_FOUND) != 0) {
                throw new CorruptedCodestreamException("More than one CRG "+
                                                      "marker "+
                                                      "found in main header");
            }
            nfMarkSeg |= CRG_FOUND;
            htKey = "CRG";
            break;
        case PPM:
            nfMarkSeg |= PPM_FOUND;
            htKey = "PPM"+(nPPMMarkSeg++);
            break;
        case TLM:
            if((nfMarkSeg & TLM_FOUND) != 0) {
                FacilityManager.getMsgLogger().
                    printmsg(MsgLogger.INFO,
                             "More than one TLM "+
                             "marker "+
                             "found in main header");
                /** XXX It is legal to have multiple TLM segments.
                throw new CorruptedCodestreamException("More than one TLM "+
                                                      "marker "+
                                                      "found in main header");
                */
            }
            nfMarkSeg |= TLM_FOUND;
            break;
        case PLM:
            if((nfMarkSeg & PLM_FOUND) != 0) {
                throw new CorruptedCodestreamException("More than one PLM "+
                                                      "marker "+
                                                      "found in main header");
            }
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.WARNING,"PLM marker segment found but "+
                         "not used by by JJ2000 decoder.");
            nfMarkSeg |= PLM_FOUND;
            htKey = "PLM";
            break;
        case POC:
            if( (nfMarkSeg&POC_FOUND)!=0) {
                throw new CorruptedCodestreamException("More than one POC "+
                                                       "marker segment found "+
                                                       "in main header");
            }
            nfMarkSeg |= POC_FOUND;
            htKey = "POC";
            break;
        case PLT:
            throw new CorruptedCodestreamException("PLT found in main header");
        case PPT:
            throw new CorruptedCodestreamException("PPT found in main header");
        default:
            htKey = "UNKNOWN";
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.WARNING,"Non recognized marker segment (0x"+
                         Integer.toHexString(marker)+") in main header!");
            break;
        }

        if(marker < 0xffffff30 || marker > 0xffffff3f){
            // Read marker segment length and create corresponding byte buffer
            int markSegLen = ehs.readUnsignedShort();
            byte[] buf = new byte[markSegLen];

            // Copy data (after re-insertion of the marker segment length);
            buf[0]= (byte)((markSegLen>>8) & 0xFF);
            buf[1]= (byte)(markSegLen & 0xFF);
            ehs.readFully(buf,2,markSegLen-2);

            if(!htKey.equals("UNKNOWN")) {
                // Store array in hashTable
                ht.put(htKey,buf);
            }
        }
    }

    /**     
     * This method extracts a marker segment in a tile-part header and stores
     * it into a byte buffer for the second pass. The marker is first
     * recognized, then its flag is activated and, finally, its content is
     * buffered in an element of byte arrays accessible thanks to a hashTable.
     * If a marker segment is not recognized, it prints a warning and skip it
     * according to its length.
     *  
     * @param marker The marker to process
     *  
     * @param ehs The encoded header stream
     *                                            
     * @param tileIdx The index of the current tile
     *
     * @param tilePartIdx The index of the current tile part
     * */   
    public void extractTilePartMarkSeg(short marker, RandomAccessIO ehs,
                                       int tileIdx, int tilePartIdx)
        throws IOException {

        String htKey=""; // Name used as a hash-table key
        if(ht==null) {
            ht = new Hashtable();
        }

        switch(marker) {
        case SOT:
            throw new CorruptedCodestreamException("Second SOT marker "+
                                                   "segment found in tile-"+
                                                   "part header");
        case SIZ:
            throw new CorruptedCodestreamException("SIZ found in tile-part"+
                                                   " header");
        case EOC:
            throw new CorruptedCodestreamException("EOC found in tile-part"+
                                                   " header");
        case TLM:                                      
            throw new CorruptedCodestreamException("TLM found in tile-part"+
                                                   " header");
        case PPM:
            throw new CorruptedCodestreamException("PPM found in tile-part"+
                                                   " header");
        case COD:
            if((nfMarkSeg & COD_FOUND) != 0) {
                throw new CorruptedCodestreamException("More than one COD "+
                                                       "marker "+
                                                       "found in tile-part"+
                                                       " header");
            }
            nfMarkSeg |= COD_FOUND;
            htKey = "COD";
            break;
        case COC:
            nfMarkSeg |= COC_FOUND;
            htKey = "COC"+(nCOCMarkSeg++);
            break;
        case QCD:
            if((nfMarkSeg & QCD_FOUND) != 0) {
                throw new CorruptedCodestreamException("More than one QCD "+
                                                       "marker "+
                                                       "found in tile-part"+
                                                       " header");
            }
            nfMarkSeg |= QCD_FOUND;
            htKey = "QCD";
            break;
        case QCC:
            nfMarkSeg |= QCC_FOUND;
            htKey = "QCC"+(nQCCMarkSeg++);
            break;
        case RGN:
            nfMarkSeg |= RGN_FOUND;
            htKey = "RGN"+(nRGNMarkSeg++);
            break;
        case COM:
            nfMarkSeg |= COM_FOUND;
            htKey = "COM"+(nCOMMarkSeg++);
            break;
        case CRG:
            throw new CorruptedCodestreamException("CRG marker found in "+
                                                   "tile-part header");
        case PPT:
            nfMarkSeg |= PPT_FOUND;
            if(nPPTMarkSeg == null){
                nPPTMarkSeg = new int[nTiles][];
            }
            if(nPPTMarkSeg[tileIdx] == null){
                nPPTMarkSeg[tileIdx] = new int[nTileParts[tileIdx]];
            }
            htKey = "PPT"+(nPPTMarkSeg[tileIdx][tilePartIdx]++);
            break;
        case SOD:
            nfMarkSeg |= SOD_FOUND;
            return;
        case POC:
            if( (nfMarkSeg&POC_FOUND) != 0)
                throw new CorruptedCodestreamException("More than one POC "+
                                                       "marker segment found "+
                                                       "in tile-part"+
                                                       " header");
            nfMarkSeg |= POC_FOUND;
            htKey = "POC";
            break;
        case PLT:
            if((nfMarkSeg & PLM_FOUND) != 0) {
                throw new CorruptedCodestreamException("PLT marker found even"+
                                                       "though PLM marker "+
                                                       "found in main header");
            }
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.WARNING,"PLT marker segment found but "+
                         "not used by JJ2000 decoder.");
            htKey = "UNKNOWN";
            break;
        default:
            htKey = "UNKNOWN";
            FacilityManager.getMsgLogger().
                printmsg(MsgLogger.WARNING,"Non recognized marker segment (0x"+
                         Integer.toHexString(marker)+") in tile-part header"+
                         " of tile "+tileIdx+" !");
            break;
        }

        // Read marker segment length and create corresponding byte buffer
        int markSegLen = ehs.readUnsignedShort();
        byte[] buf = new byte[markSegLen];

        // Copy data (after re-insertion of marker segment length);
        buf[0]= (byte)((markSegLen>>8) & 0xFF);
        buf[1]= (byte)(markSegLen & 0xFF);
        ehs.readFully(buf,2,markSegLen-2);

        if(!htKey.equals("UNKNOWN")) {
            // Store array in hashTable
            ht.put(htKey,buf);
        }
    }



    /** 
     * Retrieves and reads all marker segments found in the main header during
     * the first pass.
     * */
    private void readFoundMainMarkSeg() throws IOException {
        DataInputStream dis;
        ByteArrayInputStream bais;

        // SIZ marker segment
        if((nfMarkSeg&SIZ_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("SIZ")));
            readSIZ(new DataInputStream(bais));
        }

        // COM marker segments
        if((nfMarkSeg&COM_FOUND) != 0) {
            for(int i=0; i<nCOMMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("COM"+i)));
                readCOM(new DataInputStream(bais),true,0,i);
            }
        }

        // CRG marker segment
        if((nfMarkSeg&CRG_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("CRG")));
            readCRG(new DataInputStream(bais));
        }

        // COD marker segment
        if((nfMarkSeg&COD_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("COD")));
            readCOD(new DataInputStream(bais),true,0,0);
        }

        // COC marker segments
        if((nfMarkSeg&COC_FOUND) != 0) {
            for(int i=0; i<nCOCMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("COC"+i)));
                readCOC(new DataInputStream(bais),true,0,0);
            }
        }

        // RGN marker segment
        if((nfMarkSeg&RGN_FOUND) != 0) {
            for(int i=0; i<nRGNMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("RGN"+i)));
                readRGN(new DataInputStream(bais),true,0,0);
            }
        }

        // QCD marker segment
        if((nfMarkSeg&QCD_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("QCD")));
            readQCD(new DataInputStream(bais),true,0,0);
        }

        // QCC marker segments
        if((nfMarkSeg&QCC_FOUND) != 0) {
            for(int i=0;i<nQCCMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("QCC"+i)));
                readQCC(new DataInputStream(bais),true,0,0);
            }
        }

        // POC marker segment
        if( (nfMarkSeg&POC_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("POC")));
            readPOC(new DataInputStream(bais),true,0,0);
        }

        // PPM marker segments
        if((nfMarkSeg&PPM_FOUND) != 0) {
            for(int i=0;i<nPPMMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("PPM"+i)));
                readPPM(new DataInputStream(bais));
            }
        }

        // Reset the hashtable
        ht = null;
    }


    /**
     * Return the DecoderSpecs instance filled when reading the headers
     *
     * Retrieves and reads all marker segments previously found in the
     * tile-part header.
     *
     * @param tileIdx The index of the current tile
     * 
     * @param tpIdx Index of the current tile-part
     * */
    public void readFoundTilePartMarkSeg(int tileIdx,int tpIdx)
        throws IOException {

        DataInputStream dis;
        ByteArrayInputStream bais;

        // COD marker segment
        if((nfMarkSeg&COD_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("COD")) );
            readCOD(new DataInputStream(bais),false,tileIdx,tpIdx);
        }

        // COC marker segments
        if((nfMarkSeg&COC_FOUND) != 0) {
            for(int i=0; i<nCOCMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("COC"+i)) );
                readCOC(new DataInputStream(bais),false,tileIdx,tpIdx);
            }
        }

        // RGN marker segment
        if((nfMarkSeg&RGN_FOUND) != 0) {
            for(int i=0; i<nRGNMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("RGN"+i)) );
                readRGN(new DataInputStream(bais),false,tileIdx,tpIdx);
            }
        }

        // QCD marker segment
        if((nfMarkSeg&QCD_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("QCD")) );
            readQCD(new DataInputStream(bais),false,tileIdx,tpIdx);
        }

        // QCC marker segments
        if((nfMarkSeg&QCC_FOUND) != 0) {
            for(int i=0;i<nQCCMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("QCC"+i)) );
                readQCC(new DataInputStream(bais),false,tileIdx,tpIdx);
            }
        }
        // POC marker segment
        if( (nfMarkSeg&POC_FOUND) != 0) {
            bais = new ByteArrayInputStream( (byte[])(ht.get("POC")));
            readPOC(new DataInputStream(bais),false,tileIdx,tpIdx);
        }

        // COM marker segments
        if((nfMarkSeg&COM_FOUND) != 0) {
            for(int i=0; i<nCOMMarkSeg; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("COM"+i)) );
                readCOM(new DataInputStream(bais),false,tileIdx,i);
            }
        }

        // PPT marker segments
        if((nfMarkSeg&PPT_FOUND) != 0) {
            for(int i=0;i<nPPTMarkSeg[tileIdx][tpIdx]; i++) {
                bais = new ByteArrayInputStream( (byte[])(ht.get("PPT"+i)) );
                readPPT(new DataInputStream(bais),tileIdx,tpIdx);
            }
        }

        // Reset ht
        ht = null;
    }


    /** 
     * Return the DecoderSpecs instance filled when reading the headers
     * 
     * @return The DecoderSpecs of the decoder
     * */
    public DecoderSpecs getDecoderSpecs(){
        return decSpec;
    }

    /**
     * Creates a HeaderDecoder instance and read in two passes the main header
     * of the codestream. The first and last marker segments shall be
     * respectively SOC and SOT.
     *
     * @param ehs The encoded header stream where marker segment are
     * extracted.
     *
     * @param j2krparam The parameter list of the decoder
     *
     * @param hi The HeaderInfo holding information found in marker segments
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoded header stream.
     *
     * @exception EOFException If the end of the encoded header stream is
     * reached before getting all the data.
     *
     * @exception CorruptedCodestreamException If invalid data is found in the
     * codestream main header.
     * */
    public HeaderDecoder(RandomAccessIO ehs, 
			 J2KImageReadParamJava j2krparam,
			 HeaderInfo hi)
        throws IOException {

	this.hi = hi;
        this.j2krparam = j2krparam;
        mainHeadOff = ehs.getPos();
        if( ((short)ehs.readShort()) != Markers.SOC ) {
            throw new CorruptedCodestreamException("SOC marker segment not "+
                                                   " found at the "+
                                                   "beginning of the "+
                                                   "codestream.");
        }

        // First Pass: Decode and store main header information until the SOT
        // marker segment is found
        nfMarkSeg = 0;
        do {
            extractMainMarkSeg(ehs.readShort(),ehs);
        } while ((nfMarkSeg & SOT_FOUND)==0); //Stop when SOT is found
        ehs.seek(ehs.getPos()-2); // Realign codestream on SOT marker

        // Second pass: Read each marker segment previously found
        readFoundMainMarkSeg();
    }

    /**
     * Creates and returns the entropy decoder corresponding to the
     * information read from the codestream header and with the special
     * additional parameters from the parameter list.
     *
     * @param src The bit stream reader agent where to get code-block data
     * from.
     *
     * @param j2krparam The parameter list containing parameters applicable to the
     * entropy decoder (other parameters can also be present).
     *
     * @return The entropy decoder
     * */
    public EntropyDecoder createEntropyDecoder(CodedCBlkDataSrcDec src,
                                               J2KImageReadParamJava j2krparam) {
        // Get error detection option
        // boolean doer = j2krparam.getCer();;
        boolean doer = true;
        // Get verbose error detection option
        //boolean verber = j2krparam.getVerbose();
        boolean verber = false;

        // Get maximum number of bit planes from m quit condition
//        int mMax = j2krparam.getMQuit();
        int mMax = -1;
        return new StdEntropyDecoder(src,decSpec,doer,verber,mMax);
    }


    /**
     * Creates and returns the EnumeratedColorSpaceMapper
     * corresponding to the information read from the JP2 image file
     * via the ColorSpace parameter.
     *
     * @param src The bit stream reader agent where to get code-block
     * data from.
     * @param csMap provides color space information from the image file
     *
     * @return The color space mapping object
     * @exception IOException image access exception
     * @exception ICCProfileException if image contains a bad icc profile
     * @exception ColorSpaceException if image contains a bad colorspace box
     **/
/*
    public BlkImgDataSrc createColorSpaceMapper(BlkImgDataSrc src,
                                                ColorSpace csMap)
        throws IOException, ICCProfileException, ColorSpaceException {
        return ColorSpaceMapper.createInstance(src,csMap);
    }
*/
     /**
      * Creates and returns the ChannelDefinitonMapper which maps the
      * input channels to the channel definition for the appropriate
      * colorspace.
      *
      * @param src The bit stream reader agent where to get code-block
      * data from.
      * @param csMap provides color space information from the image file
      *
      * @return The channel definition mapping object
      * @exception IOException image access exception
      * @exception ColorSpaceException if image contains a bad colorspace box
      **/
/*
     public BlkImgDataSrc createChannelDefinitionMapper(BlkImgDataSrc src,
                                                        ColorSpace csMap)
         throws IOException, ColorSpaceException {
         return ChannelDefinitionMapper.createInstance(src,csMap);
     }
*/
    /**
     * Creates and returns the PalettizedColorSpaceMapper which uses
     * the input samples as indicies into a sample palette to
     * construct the output.
     *
     * @param src The bit stream reader agent where to get code-block
     * data from.
     * @param csMap provides color space information from the image file
     *
     * @return a  PalettizedColorSpaceMapper instance
     * @exception IOException image access exception
     * @exception ColorSpaceException if image contains a bad colorspace box
     **/
/*
    public BlkImgDataSrc createPalettizedColorSpaceMapper(BlkImgDataSrc src,
                                                          ColorSpace csMap)
        throws IOException, ColorSpaceException {
        return PalettizedColorSpaceMapper.createInstance(src, csMap); }
*/
    /**
     * Creates and returns the Resampler which converts the input
     * source to one in which all channels have the same number of
     * samples.  This is required for colorspace conversions.
     *
     * @param src The bit stream reader agent where to get code-block
     * data from.
     * @param csMap provides color space information from the image file
     *
     * @return The resampled BlkImgDataSrc
     * @exception IOException image access exception
     * @exception ColorSpaceException if image contains a bad colorspace box
     **/
/*
    public BlkImgDataSrc createResampler(BlkImgDataSrc src,
                                         ColorSpace csMap)
        throws IOException, ColorSpaceException {
        return Resampler.createInstance(src, csMap); }
*/
    /**
     * Creates and returns the ROIDeScaler corresponding to the information
     * read from the codestream header and with the special additional
     * parameters from the parameter list.
     *
     * @param src The bit stream reader agent where to get code-block data
     * from.
     *
     * @param j2krparam The parameters applicable to the entropy decoder.
     *
     * @return The ROI descaler
     * */
    public ROIDeScaler createROIDeScaler(CBlkQuantDataSrcDec src,
                                         J2KImageReadParamJava j2krparam,
					 DecoderSpecs decSpec2){
        return ROIDeScaler.createInstance(src, j2krparam, decSpec2);
    }

    /** 
     * Method that resets members indicating which markers have already been
     * found
     * */
    public void resetHeaderMarkers() {
        // The found status of PLM remains since only PLM OR PLT allowed
        // Same goes for PPM and PPT
        nfMarkSeg = nfMarkSeg & (PLM_FOUND | PPM_FOUND);
        nCOCMarkSeg = 0;
        nQCCMarkSeg = 0;
        nCOMMarkSeg = 0;
        nRGNMarkSeg = 0;
    }


    /**
     * Print information about the current header.
     *
     * @return Information in a String
     * */
    public String toString(){
        return hdStr;
    }

    /**
     * Returns the parameters that are used in this class. It returns a 2D
     * String array. Each of the 1D arrays is for a different option, and they
     * have 3 elements. The first element is the option name, the second one
     * is the synopsis and the third one is a long description of what the
     * parameter is. The synopsis or description may be 'null', in which case
     * it is assumed that there is no synopsis or description of the option,
     * respectively.
     *
     * @return the options name, their synopsis and their explanation.
     * */
    public static String[][] getParameterInfo() {
        return pinfo;
    }

    /**
     * Return the number of tiles in the image
     *
     * @return The number of tiles
     * */
    public int getNumTiles(){
        return nTiles;
    }

    /**
     * Return the packed packet headers for a given tile.
     *
     * @return An input stream containing the packed packet headers for a
     * particular tile
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
     public ByteArrayInputStream getPackedPktHead(int tile)
         throws IOException {

        if(pkdPktHeaders==null) {
            int i,t;
            pkdPktHeaders = new ByteArrayOutputStream[nTiles];
            for(i=nTiles-1; i>=0; i--) {
                pkdPktHeaders[i] = new ByteArrayOutputStream();
            }
            if(nPPMMarkSeg!=0) {
                // If this is first time packed packet headers are requested,
                // create packed packet headers from Nppm and Ippm fields
                int nppm;
                int nTileParts = tileOfTileParts.size();
                byte[] temp;
                ByteArrayInputStream pph;
                ByteArrayOutputStream allNppmIppm =
                    new ByteArrayOutputStream();

                // Concatenate all Nppm and Ippm fields
                for(i=0 ; i<nPPMMarkSeg ; i++) {
                    allNppmIppm.write(pPMMarkerData[i]);
                }
                pph = new ByteArrayInputStream(allNppmIppm.toByteArray());

                // Read all packed packet headers and concatenate for each
                // tile part
                for(i=0; i<nTileParts ; i++) {
                    t = ((Integer)tileOfTileParts.elementAt(i)).intValue();
                    // get Nppm value
                    nppm = (pph.read()<<24)|(pph.read()<<16)|
                        (pph.read()<<8)|(pph.read());

                    temp = new byte[nppm];
                    // get ippm field
                    pph.read(temp);
                    pkdPktHeaders[t].write(temp);
                }
            } else {
                int tp;
                // Write all packed packet headers to pkdPktHeaders
                for(t=nTiles-1; t>=0; t--) {
                    for(tp=0; tp<nTileParts[t]; tp++){
                        for(i=0 ; i<nPPTMarkSeg[t][tp] ; i++) {
                           pkdPktHeaders[t].
                                write(tilePartPkdPktHeaders[t][tp][i]);
                        }
                    }
                }
            }
        }

        return new ByteArrayInputStream(pkdPktHeaders[tile].toByteArray());
    }

    /**
     * Sets the tile of each tile part in order. This information is needed
     * for identifying which packet header belongs to which tile when using
     * the PPM marker.
     *
     * @param tile The tile number that the present tile part belongs to.
     * */
    public void setTileOfTileParts(int tile) {
        if(nPPMMarkSeg!=0) {
            tileOfTileParts.addElement(new Integer(tile));
        }
    }

    /**
     * Returns the number of found marker segments in the current header.
     *
     * @return The number of marker segments found in the current header.
     * */
    public int getNumFoundMarkSeg() {
        return nfMarkSeg;
    }

}
