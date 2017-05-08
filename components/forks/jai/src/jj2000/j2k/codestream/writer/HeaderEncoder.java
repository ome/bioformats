/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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
 * $RCSfile: HeaderEncoder.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:02 $
 * $State: Exp $
 *
 * Class:                   HeaderEncoder
 *
 * Description:             Write codestream headers.
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
import java.awt.Point;

import jj2000.j2k.quantization.quantizer.*;
import jj2000.j2k.wavelet.analysis.*;
import jj2000.j2k.entropy.encoder.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.image.input.*;
import jj2000.j2k.roi.encoder.*;
import jj2000.j2k.codestream.*;
import jj2000.j2k.wavelet.*;
import jj2000.j2k.entropy.*;
import jj2000.j2k.image.*;
import jj2000.j2k.util.*;
import jj2000.j2k.io.*;
import jj2000.j2k.*;

import java.util.*;
import java.io.*;

import com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageWriteParamJava;
/**
 * This class writes almost of the markers and marker segments in main header
 * and in tile-part headers. It is created by the run() method of the Encoder
 * instance.
 *
 * <p>A marker segment includes a marker and eventually marker segment
 * parameters. It is designed by the three letter code of the marker
 * associated with the marker segment. JPEG 2000 part I defines 6 types of
 * markers: <ul> <li> Delimiting : SOC,SOT,SOD,EOC (written in
 * FileCodestreamWriter).</li> <li> Fixed information: SIZ.</li> <li>
 * Functional: COD,COC,RGN,QCD,QCC,POC.</li> <li> In bit-stream: SOP,EPH.</li>
 * <li> Pointer: TLM,PLM,PLT,PPM,PPT.</li> <li> Informational:
 * CRG,COM.</li></ul>
 *
 * <p>Main Header is written when Encoder instance calls encodeMainHeader
 * whereas tile-part headers are written when the EBCOTRateAllocator instance
 * calls encodeTilePartHeader.
 *
 * @see Markers
 * @see EBCOTRateAllocator
 * */
public class HeaderEncoder implements Markers, StdEntropyCoderOptions {

    /** Nominal range bit of the component defining default values in QCD for
     * main header */
    private int defimgn;

    /** Nominal range bit of the component defining default values in QCD for
     * tile headers */
    private int deftilenr;

    /** The number of components in the image */
    protected int nComp;

    /** Whether or not to write the JJ2000 COM marker segment */
    private boolean enJJ2KMarkSeg = true;

    /** Other COM marker segments specified in the command line */
    private String otherCOMMarkSeg = null;

    /** The ByteArrayOutputStream to store header data. This handler
     * is kept in order to use methods not accessible from a general
     * DataOutputStream. For the other methods, it's better to use
     * variable hbuf.
     *
     * @see #hbuf */
    protected ByteArrayOutputStream baos;

    /** The DataOutputStream to store header data. This kind of object
     * is useful to write short, int, .... It's constructor takes
     * baos as parameter.
     *
     * @see #baos
     **/
    protected DataOutputStream hbuf;

    /** The image data reader. Source of original data info */
    protected ImgData origSrc;

    /** An array specifying, for each component,if the data was signed
        or not */
    protected boolean isOrigSig[];

    /** Reference to the rate allocator */
    protected PostCompRateAllocator ralloc;

    /** Reference to the DWT module */
    protected ForwardWT dwt;

    /** Reference to the tiler module */
    protected Tiler tiler;

    /** Reference to the ROI module */
    protected ROIScaler roiSc;

    /** The encoder specifications */
    protected J2KImageWriteParamJava wp;

    /**
     * Initializes the header writer with the references to the coding chain.
     *
     * @param origsrc The original image data (before any component mixing,
     * tiling, etc.)
     *
     * @param isorigsig An array specifying for each component if it was
     * originally signed or not.
     *
     * @param dwt The discrete wavelet transform module.
     *
     * @param tiler The tiler module.
     *
     * @param wp The encoder specifications
     *
     * @param roiSc The ROI scaler module.
     *
     * @param ralloc The post compression rate allocator.
     * */
    public HeaderEncoder(ImgData origsrc, boolean isorigsig[],
                         ForwardWT dwt, Tiler tiler,J2KImageWriteParamJava wp,
			 ROIScaler roiSc, PostCompRateAllocator ralloc) {
        if (origsrc.getNumComps() != isorigsig.length) {
            throw new IllegalArgumentException();
        }
        this.origSrc   = origsrc;
        this.isOrigSig = isorigsig;
        this.dwt       = dwt;
        this.tiler     = tiler;
        this.wp   = wp;
        this.roiSc     = roiSc;
        this.ralloc    = ralloc;

        baos = new ByteArrayOutputStream();
        hbuf = new DataOutputStream(baos);
        nComp = origsrc.getNumComps();
//        enJJ2KMarkSeg = wp.getHjj2000_COM();
//        otherCOMMarkSeg = wp.getHCOM();
    }

    /**
     * Resets the contents of this HeaderEncoder to its initial state. It
     * erases all the data in the header buffer and reactualizes the
     * headerLength field of the bit stream writer.
     * */
    public void reset() {
        baos.reset();
        hbuf = new DataOutputStream(baos);
    }

    /**
     * Returns the byte-buffer used to store the codestream header.
     *
     * @return A byte array countaining codestream header
     * */
    protected byte[] getBuffer(){
        return baos.toByteArray();
    }

    /**
     * Returns the length of the header.
     *
     * @return The length of the header in bytes
     * */
    public int getLength() {
        return hbuf.size();
    }

    /**
     * Writes the header to the specified BinaryDataOutput.
     *
     * @param out Where to write the header.
     * */
    public void writeTo(BinaryDataOutput out) throws IOException {
        int i,len;
        byte buf[];

        buf = getBuffer();
        len = getLength();

        for (i=0; i<len; i++) {
            out.writeByte(buf[i]);
        }
    }

    /**
     * Returns the number of bytes used in the codestream header's
     * buffer.
     *
     * @return Header length in buffer (without any header
     * overhead)
     * */
    protected int getBufferLength(){
        return baos.size();
    }

    /**
     * Writes the header to the specified OutputStream.
     *
     * @param out Where to write the header.
     * */
    public void writeTo(OutputStream out) throws IOException {
        out.write(getBuffer(),0,getBufferLength());
    }

    /**
     * Start Of Codestream marker (SOC) signalling the beginning of a
     * codestream.
     * */
    private void writeSOC() throws IOException {
        hbuf.writeShort(SOC);
    }

    /**
     * Writes SIZ marker segment of the codestream header. It is a fixed
     * information marker segment containing informations about image and tile
     * sizes. It is required in the main header immediately after SOC marker
     * segment.
     * */
    private void writeSIZ() throws IOException {
        int tmp;

        // SIZ marker
        hbuf.writeShort(SIZ);

        // Lsiz (Marker length) corresponding to
        // Lsiz(2 bytes)+Rsiz(2)+Xsiz(4)+Ysiz(4)+XOsiz(4)+YOsiz(4)+
	// XTsiz(4)+YTsiz(4)+XTOsiz(4)+YTOsiz(4)+Csiz(2)+
        // (Ssiz(1)+XRsiz(1)+YRsiz(1))*nComp
        // markSegLen = 38 + 3*nComp;
        int markSegLen = 38 + 3*nComp;
        hbuf.writeShort(markSegLen);

        // Rsiz (codestream capabilities)
	hbuf.writeShort(0); // JPEG 2000 - Part I

        // Xsiz (original image width)
        hbuf.writeInt(tiler.getImgWidth()+tiler.getImgULX());

        // Ysiz (original image height)
        hbuf.writeInt(tiler.getImgHeight()+tiler.getImgULY());

	// XOsiz (horizontal offset from the origin of the reference
	// grid to the left side of the image area)
	hbuf.writeInt(tiler.getImgULX());

	// YOsiz (vertical offset from the origin of the reference
	// grid to the top side of the image area)
	hbuf.writeInt(tiler.getImgULY());

        // XTsiz (nominal tile width)
        hbuf.writeInt(tiler.getNomTileWidth());

        // YTsiz (nominal tile height)
        hbuf.writeInt(tiler.getNomTileHeight());

        Point torig = tiler.getTilingOrigin(null);
	// XTOsiz (Horizontal offset from the origin of the reference
	// grid to the left side of the first tile)
	hbuf.writeInt(torig.x);

	// YTOsiz (Vertical offset from the origin of the reference
	// grid to the top side of the first tile)
	hbuf.writeInt(torig.y);

        // Csiz (number of components)
        hbuf.writeShort(nComp);

        // Bit-depth and downsampling factors.
        for(int c=0; c<nComp; c++){ // Loop on each component

            // Ssiz bit-depth before mixing
            tmp = origSrc.getNomRangeBits(c)-1;

            tmp |= ( (isOrigSig[c]?1:0)<<SSIZ_DEPTH_BITS );
            hbuf.write(tmp);

            // XRsiz (component sub-sampling value x-wise)
            hbuf.write(tiler.getCompSubsX(c));

            // YRsiz (component sub-sampling value y-wise)
            hbuf.write(tiler.getCompSubsY(c));

        } // End loop on each component

    }

    /**
     * Writes COD marker segment. COD is a functional marker segment
     * containing the code style default (coding style, decomposition,
     * layering) used for compressing all the components in an image.
     *
     * <p>The values can be overriden for an individual component by a COC
     * marker in either the main or the tile header.
     *
     * @param mh Flag indicating whether this marker belongs to the main
     * header
     *
     * @param tileIdx Tile index if the marker belongs to a tile-part header
     *
     * @see #writeCOC
     * */
    protected void writeCOD(boolean mh, int tileIdx)
	throws IOException {
	AnWTFilter[][] filt;
        boolean precinctPartitionUsed;
        int tmp;
        int mrl=0,a=0;
        int ppx=0, ppy=0;
	Progression[] prog;

        if (mh) {
	    mrl = ((Integer)wp.getDecompositionLevel().getDefault()).intValue();
            // get default precinct size
            ppx = wp.getPrecinctPartition().getPPX(-1,-1,mrl);
            ppy = wp.getPrecinctPartition().getPPY(-1,-1,mrl);
	    prog = (Progression[])(wp.getProgressionType().getDefault());
        }
        else {
	    mrl = ((Integer)wp.getDecompositionLevel().getTileDef(tileIdx)).intValue();
            // get precinct size for specified tile
            ppx = wp.getPrecinctPartition().getPPX(tileIdx,-1,mrl);
            ppy = wp.getPrecinctPartition().getPPY(tileIdx,-1,mrl);
	    prog = (Progression[])(wp.getProgressionType().getTileDef(tileIdx));
        }

        if ( ppx != PRECINCT_PARTITION_DEF_SIZE ||
             ppy != PRECINCT_PARTITION_DEF_SIZE ) {
            precinctPartitionUsed = true;
        }
        else {
            precinctPartitionUsed = false;
        }

        if ( precinctPartitionUsed ) {
	    // If precinct partition is used we add one byte per resolution
	    // level i.e. mrl+1 (+1 for resolution 0).
            a = mrl+1;
        }

        // Write COD marker
	hbuf.writeShort(COD);

        // Lcod (marker segment length (in bytes)) Basic : Lcod(2
        // bytes)+Scod(1)+SGcod(4)+SPcod(5+a)  where:
        // a=0 if no precinct partition is used
	// a=mrl+1 if precinct partition used
        int markSegLen = 12+a;
        hbuf.writeShort(markSegLen);

        // Scod (coding style parameter)
	tmp=0;
        if ( precinctPartitionUsed )
            tmp=SCOX_PRECINCT_PARTITION;

        // Are SOP markers used ?
	if (mh) {
            if( ((String)wp.getSOP().getDefault().toString())
                 .equalsIgnoreCase("true") ) {
                tmp |= SCOX_USE_SOP;
            }
        }
        else {
            if ( ((String)wp.getSOP().getTileDef(tileIdx).toString())
                 .equalsIgnoreCase("true") ) {
                tmp |= SCOX_USE_SOP;
            }
        }

        // Are EPH markers used ?
        if(mh){
            if ( ((String)wp.getEPH().getDefault().toString())
                 .equalsIgnoreCase("true") ) {
                tmp |= SCOX_USE_EPH;
            }
        }
        else{
            if ( ((String)wp.getEPH().getTileDef(tileIdx).toString())
                 .equalsIgnoreCase("true") ) {
                tmp |= SCOX_USE_EPH;
            }
        }
        if (dwt.getCbULX()!=0) tmp |= SCOX_HOR_CB_PART;
        if (dwt.getCbULY()!=0) tmp |= SCOX_VER_CB_PART;
	hbuf.write(tmp);

        // SGcod
        // Progression order
        hbuf.write(prog[0].type);

        // Number of layers
        hbuf.writeShort(ralloc.getNumLayers());

        // Multiple component transform
        // CSsiz (Color transform)
        String str = null;
        if(mh)
            str = (String)wp.getComponentTransformation().getDefault();
        else
            str = (String)wp.getComponentTransformation().getTileDef(tileIdx);

        if(str.equals("none"))
            hbuf.write(0);
        else
            hbuf.write(1);

        // SPcod
        // Number of decomposition levels
        hbuf.write(mrl);

        // Code-block width and height
        if ( mh ) {
            // main header, get default values
            tmp = wp.getCodeBlockSize().
                getCBlkWidth(ModuleSpec.SPEC_DEF,-1,-1);
            hbuf.write(MathUtil.log2(tmp)-2);
            tmp = wp.getCodeBlockSize().
                getCBlkHeight(ModuleSpec.SPEC_DEF,-1,-1);
            hbuf.write(MathUtil.log2(tmp)-2);
        }
        else {
            // tile header, get tile default values
            tmp = wp.getCodeBlockSize().
                getCBlkWidth(ModuleSpec.SPEC_TILE_DEF,tileIdx,-1);
            hbuf.write(MathUtil.log2(tmp)-2);
            tmp = wp.getCodeBlockSize().
                getCBlkHeight(ModuleSpec.SPEC_TILE_DEF,tileIdx,-1);
            hbuf.write(MathUtil.log2(tmp)-2);
        }

	// Style of the code-block coding passes
        tmp = 0;
	if(mh){ // Main header
	    // Selective arithmetic coding bypass ?
	    if( ((String)wp.getBypass().getDefault()).equals("true")) {
		tmp |= OPT_BYPASS;
	    }
	    // MQ reset after each coding pass ?
	    if( ((String)wp.getResetMQ().getDefault()).equals("true")) {
		tmp |= OPT_RESET_MQ;
	    }
	    // MQ termination after each arithmetically coded coding pass ?
	    if(  ((String)wp.getTerminateOnByte().getDefault()).equals("true") ) {
		tmp |= OPT_TERM_PASS;
	    }
	    // Vertically stripe-causal context mode ?
	    if(  ((String)wp.getCausalCXInfo().getDefault()).equals("true") ) {
		tmp |= OPT_VERT_STR_CAUSAL;
	    }
            // Predictable termination ?
            if( ((String)wp.getMethodForMQTermination().getDefault()).equals("predict")){
                tmp |= OPT_PRED_TERM;
            }
	    // Error resilience segmentation symbol insertion ?
	    if(  ((String)wp.getCodeSegSymbol().getDefault()).equals("true")) {
		tmp |= OPT_SEG_SYMBOLS;
	    }
	}
	else{ // Tile header

	    // Selective arithmetic coding bypass ?
	    if( ((String)wp.getBypass().getTileDef(tileIdx)).equals("true")) {
		tmp |= OPT_BYPASS;
	    }
	    // MQ reset after each coding pass ?
	    if( ((String)wp.getResetMQ().getTileDef(tileIdx)).equals("true")) {
		tmp |= OPT_RESET_MQ;
	    }
	    // MQ termination after each arithmetically coded coding pass ?
	    if(  ((String)wp.getTerminateOnByte().getTileDef(tileIdx)).equals("true") ) {
		tmp |= OPT_TERM_PASS;
	    }
	    // Vertically stripe-causal context mode ?
	    if(  ((String)wp.getCausalCXInfo().getTileDef(tileIdx)).equals("true") ) {
		tmp |= OPT_VERT_STR_CAUSAL;
	    }
            // Predictable termination ?
            if( ((String)wp.getMethodForMQTermination().getTileDef(tileIdx)).equals("predict")){
                tmp |= OPT_PRED_TERM;
            }
	    // Error resilience segmentation symbol insertion ?
	    if(  ((String)wp.getCodeSegSymbol().getTileDef(tileIdx)).equals("true")) {
		tmp |= OPT_SEG_SYMBOLS;
	    }
	}
        hbuf.write(tmp);

        // Wavelet transform
        // Wavelet Filter
	if(mh){
	    filt=((AnWTFilter[][])wp.getFilters().getDefault());
	    hbuf.write(filt[0][0].getFilterType());
	}else{
	    filt=((AnWTFilter[][])wp.getFilters().getTileDef(tileIdx));
	    hbuf.write(filt[0][0].getFilterType());
	}

        // Precinct partition
        if ( precinctPartitionUsed ) {
            // Write the precinct size for each resolution level + 1
            // (resolution 0) if precinct partition is used.
            Vector v[] = null;
            if ( mh ) {
                v = (Vector[])wp.getPrecinctPartition().getDefault();
            }
            else {
                v = (Vector[])wp.getPrecinctPartition().getTileDef(tileIdx);
            }
            for (int r=mrl ; r>=0 ; r--) {
                if ( r>=v[1].size() ) {
                    tmp = ((Integer)v[1].elementAt(v[1].size()-1)).
                        intValue();
                }
                else {
                    tmp = ((Integer)v[1].elementAt(r)).intValue();
                }
                int yExp = (MathUtil.log2(tmp)<< 4) & 0x00F0;

                if ( r>=v[0].size() ) {
                    tmp = ((Integer)v[0].elementAt(v[0].size()-1)).
                        intValue();
                }
                else {
                    tmp = ((Integer)v[0].elementAt(r)).intValue();
                }
                int xExp = MathUtil.log2(tmp) & 0x000F;
                hbuf.write(yExp|xExp);
            }
        }
    }

    /**
     * Writes COC marker segment . It is a functional marker containing the
     * coding style for one component (coding style, decomposition, layering).
     *
     * <P>Its values overrides any value previously set in COD in the main
     * header or in the tile header.
     *
     * @param mh Flag indicating whether the main header is to be written
     *
     * @param tileIdx Tile index
     *
     * @param compIdx index of the component which need use of the COC marker
     * segment.
     *
     * @see #writeCOD
     * */
    protected void writeCOC(boolean mh, int tileIdx, int compIdx)
	throws IOException {
	AnWTFilter[][] filt;
        boolean precinctPartitionUsed;
        int tmp;
        int mrl=0,a=0;
        int ppx=0, ppy=0;
	Progression[] prog;

        if (mh) {
	    mrl = ((Integer)wp.getDecompositionLevel().getCompDef(compIdx)).intValue();
            // Get precinct size for specified component
            ppx = wp.getPrecinctPartition().getPPX(-1, compIdx, mrl);
            ppy = wp.getPrecinctPartition().getPPY(-1, compIdx, mrl);
	    prog = (Progression[])(wp.getProgressionType().getCompDef(compIdx));
        }
        else {
            mrl = ((Integer)wp.getDecompositionLevel().getTileCompVal(tileIdx,compIdx)).
		intValue();
            // Get precinct size for specified component/tile
            ppx = wp.getPrecinctPartition().getPPX(tileIdx, compIdx, mrl);
            ppy = wp.getPrecinctPartition().getPPY(tileIdx, compIdx, mrl);
	    prog = (Progression[])(wp.getProgressionType().getTileCompVal(tileIdx,compIdx));
        }

        if ( ppx != Markers.PRECINCT_PARTITION_DEF_SIZE ||
             ppy != Markers.PRECINCT_PARTITION_DEF_SIZE ) {
            precinctPartitionUsed = true;
        }
        else {
            precinctPartitionUsed = false;
        }
        if ( precinctPartitionUsed ) {
            // If precinct partition is used we add one byte per resolution
	    // level  i.e. mrl+1 (+1 for resolution 0).
            a = mrl+1;
        }

        // COC marker
        hbuf.writeShort(COC);

        // Lcoc (marker segment length (in bytes))
        // Basic: Lcoc(2 bytes)+Scoc(1)+ Ccoc(1 or 2)+SPcod(5+a)
        int markSegLen = 8 + ((nComp < 257) ? 1 : 2)+a;

        // Rounded to the nearest even value greater or equals
        hbuf.writeShort(markSegLen);

        // Ccoc
        if(nComp < 257) {
            hbuf.write(compIdx);
        }
        else {
            hbuf.writeShort(compIdx);
        }

        // Scod (coding style parameter)
	tmp=0;
        if ( precinctPartitionUsed ) {
            tmp=SCOX_PRECINCT_PARTITION;
        }
	hbuf.write(tmp);


        // SPcoc

        // Number of decomposition levels
        hbuf.write(mrl);

        // Code-block width and height
        if ( mh ) {
            // main header, get component default values
            tmp = wp.getCodeBlockSize().
                getCBlkWidth(ModuleSpec.SPEC_COMP_DEF, -1, compIdx);
            hbuf.write(MathUtil.log2(tmp)-2);
            tmp = wp.getCodeBlockSize().
                getCBlkHeight(ModuleSpec.SPEC_COMP_DEF, -1, compIdx);
            hbuf.write(MathUtil.log2(tmp)-2);
        }
        else {
            // tile header, get tile component values
            tmp = wp.getCodeBlockSize().
                getCBlkWidth(ModuleSpec.SPEC_TILE_COMP, tileIdx, compIdx);
            hbuf.write(MathUtil.log2(tmp)-2);
            tmp = wp.getCodeBlockSize().
                getCBlkHeight(ModuleSpec.SPEC_TILE_COMP, tileIdx, compIdx);
            hbuf.write(MathUtil.log2(tmp)-2);
        }

        // Entropy coding mode options
        tmp = 0;
	if(mh){ // Main header
	    // Lazy coding mode ?
	    if( ((String)wp.getBypass().getCompDef(compIdx)).equals("true")) {
		tmp |= OPT_BYPASS;
	    }
	    // MQ reset after each coding pass ?
	    if( ((String)wp.getResetMQ().getCompDef(compIdx)).
		equalsIgnoreCase("true")) {
		tmp |= OPT_RESET_MQ;
	    }
	    // MQ termination after each arithmetically coded coding pass ?
	    if(  ((String)wp.getTerminateOnByte().getCompDef(compIdx)).equals("true") ) {
		tmp |= OPT_TERM_PASS;
	    }
	    // Vertically stripe-causal context mode ?
	    if(  ((String)wp.getCausalCXInfo().getCompDef(compIdx)).equals("true") ) {
		tmp |= OPT_VERT_STR_CAUSAL;
	    }
            // Predictable termination ?
            if( ((String)wp.getMethodForMQTermination().getCompDef(compIdx)).equals("predict")){
                tmp |= OPT_PRED_TERM;
            }
	    // Error resilience segmentation symbol insertion ?
	    if(  ((String)wp.getCodeSegSymbol().getCompDef(compIdx)).equals("true")) {
		tmp |= OPT_SEG_SYMBOLS;
	    }
	}
	else{ // Tile Header
	    if( ((String)wp.getBypass().getTileCompVal(tileIdx,compIdx)).
		equals("true")) {
		tmp |= OPT_BYPASS;
	    }
	    // MQ reset after each coding pass ?
	    if( ((String)wp.getResetMQ().getTileCompVal(tileIdx,compIdx)).
		equals("true")) {
		tmp |= OPT_RESET_MQ;
	    }
	    // MQ termination after each arithmetically coded coding pass ?
	    if(  ((String)wp.getTerminateOnByte().getTileCompVal(tileIdx,compIdx)).
		 equals("true") ) {
		tmp |= OPT_TERM_PASS;
	    }
	    // Vertically stripe-causal context mode ?
	    if(  ((String)wp.getCausalCXInfo().getTileCompVal(tileIdx,compIdx)).
		 equals("true") ) {
		tmp |= OPT_VERT_STR_CAUSAL;
	    }
            // Predictable termination ?
            if( ((String)wp.getMethodForMQTermination().getTileCompVal(tileIdx,compIdx)).
                equals("predict")){
                tmp |= OPT_PRED_TERM;
            }
	    // Error resilience segmentation symbol insertion ?
	    if(  ((String)wp.getCodeSegSymbol().getTileCompVal(tileIdx,compIdx)).
		 equals("true")) {
		tmp |= OPT_SEG_SYMBOLS;
	    }
	}
	hbuf.write(tmp);

        // Wavelet transform
        // Wavelet Filter
	if(mh){
	    filt=((AnWTFilter[][])wp.getFilters().getCompDef(compIdx));
	    hbuf.write(filt[0][0].getFilterType());
	}else{
	    filt=((AnWTFilter[][])wp.getFilters().getTileCompVal(tileIdx,compIdx));
	    hbuf.write(filt[0][0].getFilterType());
	}

        // Precinct partition
        if ( precinctPartitionUsed ) {
            // Write the precinct size for each resolution level + 1
            // (resolution 0) if precinct partition is used.
            Vector v[] = null;
            if ( mh ) {
                v = (Vector[])wp.getPrecinctPartition().getCompDef(compIdx);
            }
            else {
                v = (Vector[])wp.getPrecinctPartition().getTileCompVal(tileIdx, compIdx);
            }
            for (int r=mrl ; r>=0 ; r--) {
                if ( r>=v[1].size() ) {
                    tmp = ((Integer)v[1].elementAt(v[1].size()-1)).
                        intValue();
                }
                else {
                    tmp = ((Integer)v[1].elementAt(r)).intValue();
                }
                int yExp = (MathUtil.log2(tmp)<< 4) & 0x00F0;

                if ( r>=v[0].size() ) {
                    tmp = ((Integer)v[0].elementAt(v[0].size()-1)).
                        intValue();
                }
                else {
                    tmp = ((Integer)v[0].elementAt(r)).intValue();
                }
                int xExp = MathUtil.log2(tmp) & 0x000F;
                hbuf.write(yExp|xExp);
            }
        }

    }

    /**
     * Writes QCD marker segment in main header. QCD is a functional marker
     * segment countaining the quantization default used for compressing all
     * the components in an image. The values can be overriden for an
     * individual component by a QCC marker in either the main or the tile
     * header.
     * */
    protected void writeMainQCD() throws IOException{
        float step;

        String qType = (String)wp.getQuantizationType().getDefault();
        float baseStep = ((Float)wp.getQuantizationStep().getDefault()).floatValue();
        int gb = ((Integer)wp.getGuardBits().getDefault()).intValue();

        boolean isDerived   = qType.equals("derived");
        boolean isReversible = qType.equals("reversible");
        int mrl = ((Integer)wp.getDecompositionLevel().getDefault()).intValue();

        int nt = dwt.getNumTiles();
        int nc = dwt.getNumComps();
        int tmpI;
        int[] tcIdx = new int[2];
        String tmpStr;
        boolean notFound = true;
        for(int t=0; t<nt && notFound; t++) {
            for(int c=0; c<nc && notFound; c++) {
                tmpI = ((Integer)wp.getDecompositionLevel().getTileCompVal(t,c)).intValue();
                tmpStr = (String)wp.getQuantizationType().getTileCompVal(t,c);
                if(tmpI==mrl && tmpStr.equals(qType)) {
                    tcIdx[0] = t; tcIdx[1] = c;
                    notFound = false;
                }
            }
        }
        if(notFound) {
            throw new Error("Default representative for quantization type "+
                            " and number of decomposition levels not found "+
                            " in main QCD marker segment. "+
                            "You have found a JJ2000 bug.");
        }
        SubbandAn sb,csb,
            sbRoot = dwt.getAnSubbandTree(tcIdx[0],tcIdx[1]);
        defimgn = dwt.getNomRangeBits(tcIdx[1]);

	int nqcd; // Number of quantization step-size to transmit

        // Get the quantization style
        int qstyle = (isReversible) ? SQCX_NO_QUANTIZATION :
	    ((isDerived) ? SQCX_SCALAR_DERIVED : SQCX_SCALAR_EXPOUNDED);

        // QCD marker
        hbuf.writeShort(QCD);


        // Compute the number of steps to send
        switch (qstyle) {
        case SQCX_SCALAR_DERIVED:
            nqcd = 1; // Just the LL value
            break;
        case SQCX_NO_QUANTIZATION:
        case SQCX_SCALAR_EXPOUNDED:
            // One value per subband
            nqcd=0;

            sb=sbRoot;

            // Get the subband at first resolution level
            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            // Count total number of subbands
            for (int j=0; j<=mrl; j++) {
                csb = sb;
                while (csb != null) {
                    nqcd++;
                    csb = (SubbandAn) csb.nextSubband();
                }
                // Go up one resolution level
                sb = (SubbandAn) sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }

        // Lqcd (marker segment length (in bytes))
        // Lqcd(2 bytes)+Sqcd(1)+ SPqcd (2*Nqcd)
        int markSegLen = 3 + ((isReversible) ? nqcd : 2*nqcd);

        // Rounded to the nearest even value greater or equals
        hbuf.writeShort(markSegLen);

        // Sqcd
        hbuf.write(qstyle+(gb<<SQCX_GB_SHIFT));

        // SPqcd
        switch (qstyle) {
        case SQCX_NO_QUANTIZATION:
	    sb = sbRoot;
	    sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Output one exponent per subband
            for (int j=0; j<=mrl; j++) {
		csb = sb;
                while(csb != null) {
                    int tmp = (defimgn + csb.anGainExp);
                    hbuf.write(tmp<<SQCX_EXP_SHIFT);

		    csb = (SubbandAn)csb.nextSubband();
		    // Go up one resolution level
		}
		sb = (SubbandAn)sb.getNextResLevel();
	    }
	    break;
        case SQCX_SCALAR_DERIVED:
	    sb = sbRoot;
	    sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Calculate subband step (normalized to unit
            // dynamic range)
            step = baseStep/(1<<sb.level);

            // Write exponent-mantissa, 16 bits
            hbuf.writeShort(StdQuantizer.
                            convertToExpMantissa(step));
            break;
        case SQCX_SCALAR_EXPOUNDED:
	    sb = sbRoot;
	    sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Output one step per subband
            for (int j=0; j<=mrl; j++) {
		csb = sb;
                while(csb != null) {
                    // Calculate subband step (normalized to unit
                    // dynamic range)
                    step = baseStep/(csb.l2Norm*(1<<csb.anGainExp));

                    // Write exponent-mantissa, 16 bits
                    hbuf.writeShort(StdQuantizer.
                                    convertToExpMantissa(step));

		    csb = (SubbandAn)csb.nextSubband();
		}
                // Go up one resolution level
		sb = (SubbandAn)sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }
    }

    /**
     * Writes QCC marker segment in main header. It is a functional
     * marker segment countaining the quantization used for
     * compressing the specified component in an image. The values
     * override for the specified component what was defined by a QCC
     * marker in either the main or the tile header.
     *
     * @param compIdx Index of the component which needs QCC marker
     * segment.
     * */
    protected void writeMainQCC(int compIdx)
	throws IOException{

        int mrl;
        int qstyle;
	int tIdx = 0;
        float step;

        SubbandAn sb,sb2;
	SubbandAn sbRoot;

        int imgnr = dwt.getNomRangeBits(compIdx);
        String qType = (String)wp.getQuantizationType().getCompDef(compIdx);
        float baseStep = ((Float)wp.getQuantizationStep().getCompDef(compIdx)).floatValue();
        int gb = ((Integer)wp.getGuardBits().getCompDef(compIdx)).intValue();

        boolean isReversible = qType.equals("reversible");
        boolean isDerived   = qType.equals("derived");

        mrl = ((Integer)wp.getDecompositionLevel().getCompDef(compIdx)).intValue();

        int nt = dwt.getNumTiles();
        int nc = dwt.getNumComps();
        int tmpI;
        String tmpStr;
        boolean notFound = true;
        for(int t=0; t<nt && notFound; t++) {
            for(int c=0; c<nc && notFound; c++) {
                tmpI = ((Integer)wp.getDecompositionLevel().getTileCompVal(t,c)).intValue();
                tmpStr = (String)wp.getQuantizationType().getTileCompVal(t,c);
                if(tmpI==mrl && tmpStr.equals(qType)) {
                    tIdx = t;
                    notFound = false;
                }
            }
        }
        if(notFound) {
            throw new Error("Default representative for quantization type "+
                            " and number of decomposition levels not found "+
                            " in main QCC (c="+compIdx+") marker segment. "+
                            "You have found a JJ2000 bug.");
        }
        sbRoot = dwt.getAnSubbandTree(tIdx,compIdx);

        int nqcc; // Number of quantization step-size to transmit

        // Get the quantization style
        if(isReversible) {
            qstyle = SQCX_NO_QUANTIZATION;
        }
        else if (isDerived) {
            qstyle = SQCX_SCALAR_DERIVED;
        }
        else {
            qstyle = SQCX_SCALAR_EXPOUNDED;
        }

        // QCC marker
        hbuf.writeShort(QCC);

        // Compute the number of steps to send
        switch (qstyle) {
        case SQCX_SCALAR_DERIVED:
            nqcc = 1; // Just the LL value
            break;
        case SQCX_NO_QUANTIZATION:
        case SQCX_SCALAR_EXPOUNDED:
            // One value per subband
            nqcc = 0;

            sb = sbRoot;
            mrl = sb.resLvl;

            // Get the subband at first resolution level
            sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Find root element for LL subband
            while (sb.resLvl != 0) {
                sb = sb.subb_LL;
            }

            // Count total number of subbands
            for (int j=0; j<=mrl; j++) {
                sb2 = sb;
                while (sb2 != null) {
                    nqcc++;
                    sb2 = (SubbandAn) sb2.nextSubband();
                }
                // Go up one resolution level
                sb = (SubbandAn) sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }

        // Lqcc (marker segment length (in bytes))
        // Lqcc(2 bytes)+Cqcc(1 or 2)+Sqcc(1)+ SPqcc (2*Nqcc)
        int markSegLen = 3 + ((nComp < 257) ? 1 : 2) +
	    ((isReversible) ? nqcc : 2*nqcc);
        hbuf.writeShort(markSegLen);

        // Cqcc
        if (nComp < 257) {
            hbuf.write(compIdx);
        }
        else {
            hbuf.writeShort(compIdx);
        }

        // Sqcc (quantization style)
        hbuf.write(qstyle+(gb<<SQCX_GB_SHIFT));

        // SPqcc
        switch (qstyle) {
        case SQCX_NO_QUANTIZATION:
            // Get resolution level 0 subband
	    sb = sbRoot;
            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            // Output one exponent per subband
            for (int j=0; j<=mrl; j++) {
		sb2 = sb;
                while (sb2 != null) {
                    int tmp = (imgnr+sb2.anGainExp);
                    hbuf.write(tmp<<SQCX_EXP_SHIFT);

		    sb2 = (SubbandAn)sb2.nextSubband();
		}
                // Go up one resolution level
		sb = (SubbandAn)sb.getNextResLevel();
            }
            break;
        case SQCX_SCALAR_DERIVED:
            // Get resolution level 0 subband
            sb = sbRoot;
            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            // Calculate subband step (normalized to unit
            // dynamic range)
            step = baseStep/(1<<sb.level);

            // Write exponent-mantissa, 16 bits
            hbuf.writeShort(StdQuantizer.
                            convertToExpMantissa(step));
            break;
        case SQCX_SCALAR_EXPOUNDED:
            // Get resolution level 0 subband
            sb = sbRoot;
            mrl = sb.resLvl;

            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            for (int j=0; j<=mrl; j++) {
                sb2 = sb;
                while (sb2 != null) {
                    // Calculate subband step (normalized to unit
                    // dynamic range)
                    step = baseStep/(sb2.l2Norm*(1<<sb2.anGainExp));

                    // Write exponent-mantissa, 16 bits
                    hbuf.writeShort(StdQuantizer.
                                    convertToExpMantissa(step));
                    sb2 = (SubbandAn)sb2.nextSubband();
                }
                // Go up one resolution level
                sb = (SubbandAn) sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }
    }

    /**
     * Writes QCD marker segment in tile header. QCD is a functional
     * marker segment countaining the quantization default used for
     * compressing all the components in an image. The values can be
     * overriden for an individual component by a QCC marker in either
     * the main or the tile header.
     *
     * @param tIdx Tile index
     * */
    protected void writeTileQCD(int tIdx) throws IOException{
        int mrl;
        int qstyle;

        float step;
        SubbandAn sb,csb,sbRoot;

        String qType = (String)wp.getQuantizationType().getTileDef(tIdx);
        float baseStep = ((Float)wp.getQuantizationStep().getTileDef(tIdx)).floatValue();
        mrl = ((Integer)wp.getDecompositionLevel().getTileDef(tIdx)).intValue();

        int nc = dwt.getNumComps();
        int tmpI;
        String tmpStr;
        boolean notFound = true;
        int compIdx = 0;
        for(int c=0; c<nc && notFound; c++) {
            tmpI = ((Integer)wp.getDecompositionLevel().getTileCompVal(tIdx,c)).intValue();
            tmpStr = (String)wp.getQuantizationStep().getTileCompVal(tIdx,c);
            if(tmpI==mrl && tmpStr.equals(qType)) {
                compIdx = c;
                notFound = false;
            }
        }
        if(notFound) {
            throw new Error("Default representative for quantization type "+
                            " and number of decomposition levels not found "+
                            " in tile QCD (t="+tIdx+") marker segment. "+
                            "You have found a JJ2000 bug.");
        }

        sbRoot = dwt.getAnSubbandTree(tIdx,compIdx);
        deftilenr = dwt.getNomRangeBits(compIdx);
        int gb = ((Integer)wp.getGuardBits().getTileDef(tIdx)).intValue();

        boolean isDerived   = qType.equals("derived");
        boolean isReversible = qType.equals("reversible");

        int nqcd; // Number of quantization step-size to transmit

        // Get the quantization style
        qstyle = (isReversible) ? SQCX_NO_QUANTIZATION :
	    ((isDerived) ? SQCX_SCALAR_DERIVED : SQCX_SCALAR_EXPOUNDED);

        // QCD marker
        hbuf.writeShort(QCD);

        // Compute the number of steps to send
        switch (qstyle) {
        case SQCX_SCALAR_DERIVED:
            nqcd = 1; // Just the LL value
            break;
        case SQCX_NO_QUANTIZATION:
        case SQCX_SCALAR_EXPOUNDED:
            // One value per subband
            nqcd=0;

            sb=sbRoot;

            // Get the subband at first resolution level
            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            // Count total number of subbands
            for (int j=0; j<=mrl; j++) {
                csb = sb;
                while (csb != null) {
                    nqcd++;
                    csb = (SubbandAn) csb.nextSubband();
                }
                // Go up one resolution level
                sb = (SubbandAn) sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }

        // Lqcd (marker segment length (in bytes))
        // Lqcd(2 bytes)+Sqcd(1)+ SPqcd (2*Nqcd)
        int markSegLen = 3 + ((isReversible) ? nqcd : 2*nqcd);

        // Rounded to the nearest even value greater or equals
        hbuf.writeShort(markSegLen);

        // Sqcd
        hbuf.write(qstyle+(gb<<SQCX_GB_SHIFT));

        // SPqcd
        switch (qstyle) {
        case SQCX_NO_QUANTIZATION:
	    sb = sbRoot;
	    sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Output one exponent per subband
            for (int j=0; j<=mrl; j++) {
		csb = sb;
                while(csb != null) {
                    int tmp = (deftilenr+csb.anGainExp);
                    hbuf.write(tmp<<SQCX_EXP_SHIFT);

		    csb = (SubbandAn)csb.nextSubband();
		    // Go up one resolution level
		}
		sb = (SubbandAn)sb.getNextResLevel();
	    }
	    break;
        case SQCX_SCALAR_DERIVED:
	    sb = sbRoot;
	    sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Calculate subband step (normalized to unit
            // dynamic range)
            step = baseStep/(1<<sb.level);

            // Write exponent-mantissa, 16 bits
            hbuf.writeShort(StdQuantizer.
                            convertToExpMantissa(step));
            break;
        case SQCX_SCALAR_EXPOUNDED:
	    sb = sbRoot;
	    sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Output one step per subband
            for (int j=0; j<=mrl; j++) {
		csb = sb;
                while(csb != null) {
                    // Calculate subband step (normalized to unit
                    // dynamic range)
                    step = baseStep/(csb.l2Norm*(1<<csb.anGainExp));

                    // Write exponent-mantissa, 16 bits
                    hbuf.writeShort(StdQuantizer.
                                    convertToExpMantissa(step));

		    csb = (SubbandAn)csb.nextSubband();
		}
                // Go up one resolution level
		sb = (SubbandAn)sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }
    }

    /**
     * Writes QCC marker segment in tile header. It is a functional
     * marker segment countaining the quantization used for
     * compressing the specified component in an image. The values
     * override for the specified component what was defined by a QCC
     * marker in either the main or the tile header.
     *
     * @param t Tile index
     *
     * @param compIdx Index of the component which needs QCC marker
     * segment.
     * */
    protected void writeTileQCC(int t,int compIdx)
	throws IOException{

        int mrl;
        int qstyle;
        float step;

        SubbandAn sb,sb2;
        int nqcc; // Number of quantization step-size to transmit

        SubbandAn sbRoot = dwt.getAnSubbandTree(t,compIdx);
        int imgnr = dwt.getNomRangeBits(compIdx);
        String qType = (String)wp.getQuantizationType().getTileCompVal(t,compIdx);
        float baseStep = ((Float)wp.getQuantizationStep().getTileCompVal(t,compIdx)).
            floatValue();
        int gb = ((Integer)wp.getGuardBits().getTileCompVal(t,compIdx)).intValue();

        boolean isReversible = qType.equals("reversible");
        boolean isDerived   = qType.equals("derived");

        mrl = ((Integer)wp.getDecompositionLevel().getTileCompVal(t,compIdx)).intValue();

        // Get the quantization style
        if(isReversible) {
            qstyle = SQCX_NO_QUANTIZATION;
        }
        else if (isDerived) {
            qstyle = SQCX_SCALAR_DERIVED;
        }
        else {
            qstyle = SQCX_SCALAR_EXPOUNDED;
        }

        // QCC marker
        hbuf.writeShort(QCC);

        // Compute the number of steps to send
        switch (qstyle) {
        case SQCX_SCALAR_DERIVED:
            nqcc = 1; // Just the LL value
            break;
        case SQCX_NO_QUANTIZATION:
        case SQCX_SCALAR_EXPOUNDED:
            // One value per subband
            nqcc = 0;

            sb = sbRoot;
            mrl = sb.resLvl;

            // Get the subband at first resolution level
            sb = (SubbandAn)sb.getSubbandByIdx(0,0);

            // Find root element for LL subband
            while (sb.resLvl != 0) {
                sb = sb.subb_LL;
            }

            // Count total number of subbands
            for (int j=0; j<=mrl; j++) {
                sb2 = sb;
                while (sb2 != null) {
                    nqcc++;
                    sb2 = (SubbandAn) sb2.nextSubband();
                }
                // Go up one resolution level
                sb = (SubbandAn) sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }

        // Lqcc (marker segment length (in bytes))
        // Lqcc(2 bytes)+Cqcc(1 or 2)+Sqcc(1)+ SPqcc (2*Nqcc)
        int markSegLen = 3 + ((nComp < 257) ? 1 : 2) +
	    ((isReversible) ? nqcc : 2*nqcc);
        hbuf.writeShort(markSegLen);

        // Cqcc
        if (nComp < 257) {
            hbuf.write(compIdx);
        }
        else {
            hbuf.writeShort(compIdx);
        }

        // Sqcc (quantization style)
        hbuf.write(qstyle+(gb<<SQCX_GB_SHIFT));

        // SPqcc
        switch (qstyle) {
        case SQCX_NO_QUANTIZATION:
            // Get resolution level 0 subband
	    sb = sbRoot;
            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            // Output one exponent per subband
            for (int j=0; j<=mrl; j++) {
		sb2 = sb;
                while (sb2 != null) {
                    int tmp = (imgnr+sb2.anGainExp);
                    hbuf.write(tmp<<SQCX_EXP_SHIFT);

		    sb2 = (SubbandAn)sb2.nextSubband();
		}
                // Go up one resolution level
		sb = (SubbandAn)sb.getNextResLevel();
            }
            break;
        case SQCX_SCALAR_DERIVED:
            // Get resolution level 0 subband
            sb = sbRoot;
            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            // Calculate subband step (normalized to unit
            // dynamic range)
            step = baseStep/(1<<sb.level);

            // Write exponent-mantissa, 16 bits
            hbuf.writeShort(StdQuantizer.
                            convertToExpMantissa(step));
            break;
        case SQCX_SCALAR_EXPOUNDED:
            // Get resolution level 0 subband
            sb = sbRoot;
            mrl = sb.resLvl;

            sb = (SubbandAn) sb.getSubbandByIdx(0,0);

            for (int j=0; j<=mrl; j++) {
                sb2 = sb;
                while (sb2 != null) {
                    // Calculate subband step (normalized to unit
                    // dynamic range)
                    step = baseStep/(sb2.l2Norm*(1<<sb2.anGainExp));

                    // Write exponent-mantissa, 16 bits
                    hbuf.writeShort(StdQuantizer.
                                    convertToExpMantissa(step));
                    sb2 = (SubbandAn)sb2.nextSubband();
                }
                // Go up one resolution level
                sb = (SubbandAn) sb.getNextResLevel();
            }
            break;
        default:
            throw new Error("Internal JJ2000 error");
        }
    }

    /**
     * Writes POC marker segment. POC is a functional marker segment
     * containing the bounds and progression order for any progression order
     * other than default in the codestream.
     *
     * @param mh Flag indicating whether the main header is to be written
     *
     * @param tileIdx Tile index
     * */
    protected void writePOC(boolean mh, int tileIdx) throws IOException {
        int markSegLen=0;        // Segment marker length
        int lenCompField;        // Holds the size of any component field as
                                 // this size depends on the number of
                                 //components
        Progression[] prog = null; // Holds the progression(s)
        int npoc;                // Number of progression order changes

        // Get the progression order changes, their number and checks
        // if it is ok
        if(mh){
            prog = (Progression[])(wp.getProgressionType().getDefault());
        }
        else {
            prog = (Progression[])(wp.getProgressionType().getTileDef(tileIdx));
        }

        // Calculate the length of a component field (depends on the number of
        // components)
        lenCompField = (nComp<257 ? 1 : 2);

        // POC marker
        hbuf.writeShort(POC);

        // Lpoc (marker segment length (in bytes))
        // Basic: Lpoc(2 bytes) + npoc * [ RSpoc(1) + CSpoc(1 or 2) + LYEpoc(2)
        // + REpoc(1) + CEpoc(1 or 2) + Ppoc(1) ]
	npoc = prog.length;
        markSegLen = 2 + npoc * (1+lenCompField+2+1+lenCompField+1);
        hbuf.writeShort(markSegLen);

        // Write each progression order change
        for (int i=0 ; i<npoc ; i++){
            // RSpoc(i)
            hbuf.write(prog[i].rs);
            // CSpoc(i)
            if ( lenCompField==2 ) {
                hbuf.writeShort(prog[i].cs);
            }
            else {
                hbuf.write(prog[i].cs);
            }
            // LYEpoc(i)
            hbuf.writeShort(prog[i].lye);
            // REpoc(i)
            hbuf.write(prog[i].re);
            // CEpoc(i)
            if ( lenCompField==2 ) {
                hbuf.writeShort(prog[i].ce);
            }
            else {
                hbuf.write(prog[i].ce);
            }
            // Ppoc(i)
            hbuf.write(prog[i].type);
        }
    }


    /**
     * Write main header. JJ2000 main header corresponds to the following
     * sequence of marker
     * segments:<ol><li>SOC</li><li>SIZ</li><li>COD</li><li>COC (if
     * needed)</li><li>QCD</li><li>QCC (if needed)</li><li>POC (if
     * needed)</li></ol>
     * */
    public void encodeMainHeader() throws IOException {
        int i;


        // +---------------------------------+
        // |    SOC marker segment           |
        // +---------------------------------+
        writeSOC();

        // +---------------------------------+
        // |    Image and tile SIZe (SIZ)    |
        // +---------------------------------+
        writeSIZ();

        // +-------------------------------+
        // |   COding style Default (COD)  |
        // +-------------------------------+
        boolean isEresUsed = ((String)wp.getTerminateOnByte().getDefault()).
            equals("predict");
        writeCOD(true,0);

        // +---------------------------------+
        // |   COding style Component (COC)  |
        // +---------------------------------+
	for (i= 0; i<nComp; i++) {
            boolean isEresUsedinComp = ((String)wp.getTerminateOnByte().getCompDef(i)).
                equals("predict");
	    if(wp.getFilters().isCompSpecified(i) ||
               wp.getDecompositionLevel().isCompSpecified(i) ||
               wp.getBypass().isCompSpecified(i) ||
               wp.getResetMQ().isCompSpecified(i) ||
               wp.getMethodForMQTermination().isCompSpecified(i) ||
               wp.getCodeSegSymbol().isCompSpecified(i) ||
               wp.getCausalCXInfo().isCompSpecified(i) ||
               wp.getPrecinctPartition().isCompSpecified(i) ||
               wp.getCodeBlockSize().isCompSpecified(i) ||
               (isEresUsed != isEresUsedinComp ) )
		// Some component non-default stuff => need COC
		writeCOC(true,0,i);
        }

        // +-------------------------------+
        // |   Quantization Default (QCD)  |
        // +-------------------------------+
        writeMainQCD();

        // +-------------------------------+
        // | Quantization Component (QCC)  |
        // +-------------------------------+
        // Write needed QCC markers
        for(i=0; i<nComp; i++){
	    if(dwt.getNomRangeBits(i)!= defimgn ||
	       wp.getQuantizationType().isCompSpecified(i) ||
               wp.getQuantizationStep().isCompSpecified(i) ||
               wp.getDecompositionLevel().isCompSpecified(i) ||
               wp.getGuardBits().isCompSpecified(i)){
                writeMainQCC(i);
	    }
        }

        // +--------------------------+
        // |    POC maker segment     |
	// +--------------------------+
	Progression[] prog = (Progression[])(wp.getProgressionType().getDefault());
        if(prog.length>1)
            writePOC(true, 0);

        // +--------------------------+
        // |      Comment (COM)       |
        // +--------------------------+
        writeCOM();
    }

    /**
     * Write a COM marker segment adding some comments to the codestream.
     *
     * <p> This marker is currently written in main header and indicates the
     * JJ2000 encoder's version that has created the codestream.
     * */
    private void writeCOM() throws IOException {
        // JJ2000 COM marker segment
        if(enJJ2KMarkSeg) {
            String str = "Created by: JJ2000 version "+JJ2KInfo.version;
            int markSegLen; // the marker segment length

            // COM marker
            hbuf.writeShort(COM);

            // Calculate length: Lcom(2) + Rcom (2) + string's length;
            markSegLen = 2 + 2 + str.length();
            hbuf.writeShort(markSegLen);

            // Rcom 
            hbuf.writeShort(1); // General use (IS 8859-15:1999(Latin) values)

            byte[] chars = str.getBytes();
            for(int i=0; i<chars.length; i++) {
                hbuf.writeByte(chars[i]);
            }
        }
        // other COM marker segments
        if(otherCOMMarkSeg!=null) {
            StringTokenizer stk = new StringTokenizer(otherCOMMarkSeg,"#");
            while(stk.hasMoreTokens()) {
                String str = stk.nextToken();
                int markSegLen; // the marker segment length

                // COM marker
                hbuf.writeShort(COM);

                // Calculate length: Lcom(2) + Rcom (2) + string's length;
                markSegLen = 2 + 2 + str.length();
                hbuf.writeShort(markSegLen);

                // Rcom 
                hbuf.writeShort(1); // General use (IS 8859-15:1999(Latin)
                // values)

                byte[] chars = str.getBytes();
                for(int i=0; i<chars.length; i++) {
                    hbuf.writeByte(chars[i]);
                }
            }
        }
    }

    /**
     * Writes the RGN marker segment in the tile header. It describes the
     * scaling value in each tile component
     *
     * <P>May be used in tile or main header. If used in main header, it
     * refers to a ROI of the whole image, regardless of tiling. When used in
     * tile header, only the particular tile is affected.
     *
     * @param tIdx The tile index
     *
     * @exception IOException If an I/O error occurs while reading from the
     * encoder header stream
     * */
    private void writeRGN(int tIdx) throws IOException {
        int i;
        int markSegLen;    // the marker length

        // Write one RGN marker per component
        for(i=0;i<nComp;i++){
            // RGN marker
            hbuf.writeShort(RGN);

            // Calculate length (Lrgn)
            // Basic: Lrgn (2) + Srgn (1) + SPrgn + one byte
            // or two for component number
            markSegLen = 4+((nComp<257)? 1:2);
            hbuf.writeShort(markSegLen);

            // Write component (Crgn)
            if(nComp<257)
                hbuf.writeByte(i);
            else
                hbuf.writeShort(i);

            // Write type of ROI (Srgn)
            hbuf.writeByte(SRGN_IMPLICIT);

            // Write ROI info (SPrgn)
            hbuf.writeByte(((Integer)(wp.getROIs().
			      getTileCompVal(tIdx,i))).intValue());
        }
    }
    /**
     * Writes tile-part header. JJ2000 tile-part header corresponds to the
     * following sequence of marker segments:<ol> <li>SOT</li> <li>COD (if
     * needed)</li> <li>COC (if needed)</li> <li>QCD (if needed)</li> <li>QCC
     * (if needed)</li> <li>RGN (if needed)</li> <li>POC (if needed)</li>
     * <li>SOD</li> </ol>
     *
     * @param tileLength The length of the current tile-part.
     *
     * @param tileIdx Index of the tile to write
     * */
    public void encodeTilePartHeader(int tileLength,int tileIdx)
        throws IOException {

        int tmp;
        Point numTiles = ralloc.getNumTiles(null);
	ralloc.setTile(tileIdx%numTiles.x,tileIdx/numTiles.x);

	// +--------------------------+
        // |    SOT maker segment     |
	// +--------------------------+
        // SOT marker
        hbuf.writeByte(SOT>>8);
        hbuf.writeByte(SOT);

        // Lsot (10 bytes)
        hbuf.writeByte(0);
        hbuf.writeByte(10);

        // Isot
        if(tileIdx>65534){
            throw new IllegalArgumentException("Trying to write a tile-part "+
                                               "header whose tile index is too"+
                                               " high");
        }
        hbuf.writeByte(tileIdx>>8);
        hbuf.writeByte(tileIdx);

        // Psot
        tmp = tileLength;
        hbuf.writeByte(tmp>>24);
        hbuf.writeByte(tmp>>16);
        hbuf.writeByte(tmp>>8);
        hbuf.writeByte(tmp);

        // TPsot
        hbuf.writeByte(0); // Only one tile-part currently supported !

	// TNsot
	hbuf.writeByte(1); // Only one tile-part currently supported !

	// +--------------------------+
        // |    COD maker segment     |
	// +--------------------------+
        boolean isEresUsed = ((String)wp.getMethodForMQTermination().getDefault()).
            equals("predict");
        boolean isEresUsedInTile = ((String)wp.getMethodForMQTermination().getTileDef(tileIdx)).
            equals("predict");
        boolean tileCODwritten = false;
	if(wp.getFilters().isTileSpecified(tileIdx) ||
           wp.getComponentTransformation().isTileSpecified(tileIdx) ||
           wp.getDecompositionLevel().isTileSpecified(tileIdx) ||
           wp.getBypass().isTileSpecified(tileIdx) ||
           wp.getResetMQ().isTileSpecified(tileIdx) ||
           wp.getTerminateOnByte().isTileSpecified(tileIdx) ||
           wp.getCausalCXInfo().isTileSpecified(tileIdx) ||
           wp.getPrecinctPartition().isTileSpecified(tileIdx) ||
           wp.getSOP().isTileSpecified(tileIdx) ||
           wp.getCodeSegSymbol().isTileSpecified(tileIdx) ||
           wp.getProgressionType().isTileSpecified(tileIdx) ||
           wp.getEPH().isTileSpecified(tileIdx) ||
           wp.getCodeBlockSize().isTileSpecified(tileIdx) ||
           ( isEresUsed != isEresUsedInTile ) ) {
	    writeCOD(false,tileIdx);
            tileCODwritten = true;
	}

	// +--------------------------+
        // |    COC maker segment     |
	// +--------------------------+
	for(int c=0; c<nComp; c++){
            boolean isEresUsedInTileComp = ((String)wp.getMethodForMQTermination().
                                            getTileCompVal(tileIdx,c)).
		equals("predict");

	    if(wp.getFilters().isTileCompSpecified(tileIdx,c) ||
               wp.getDecompositionLevel().isTileCompSpecified(tileIdx,c) ||
               wp.getBypass().isTileCompSpecified(tileIdx,c) ||
               wp.getResetMQ().isTileCompSpecified(tileIdx,c) ||
               wp.getTerminateOnByte().isTileCompSpecified(tileIdx,c) ||
               wp.getCausalCXInfo().isTileCompSpecified(tileIdx,c) ||
               wp.getPrecinctPartition().isTileCompSpecified(tileIdx,c) ||
               wp.getCodeSegSymbol().isTileCompSpecified(tileIdx,c) ||
               wp.getCodeBlockSize().isTileCompSpecified(tileIdx,c) ||
               ( isEresUsedInTileComp != isEresUsed ) ) {
		writeCOC(false,tileIdx,c);
	    }
            else if(tileCODwritten){
                if(wp.getFilters().isCompSpecified(c) ||
                   wp.getDecompositionLevel().isCompSpecified(c) ||
                   wp.getBypass().isCompSpecified(c) ||
                   wp.getResetMQ().isCompSpecified(c) ||
                   wp.getTerminateOnByte().isCompSpecified(c) ||
                   wp.getCodeSegSymbol().isCompSpecified(c) ||
                   wp.getCausalCXInfo().isCompSpecified(c) ||
                   wp.getPrecinctPartition().isCompSpecified(c) ||
                   wp.getCodeBlockSize().isCompSpecified(c) ||
                   (wp.getMethodForMQTermination().isCompSpecified(c)&&
                    ((String)wp.getMethodForMQTermination().getCompDef(c)).equals("predict"))){
                    writeCOC(false,tileIdx,c);
                }
            }
        }

	// +--------------------------+
        // |    QCD maker segment     |
	// +--------------------------+
        boolean tileQCDwritten = false;
	if(wp.getQuantizationType().isTileSpecified(tileIdx) ||
           wp.getQuantizationStep().isTileSpecified(tileIdx) ||
           wp.getDecompositionLevel().isTileSpecified(tileIdx) ||
           wp.getGuardBits().isTileSpecified(tileIdx)){
	    writeTileQCD(tileIdx);
            tileQCDwritten = true;
	} else {
            deftilenr = defimgn;
        }

	// +--------------------------+
        // |    QCC maker segment     |
	// +--------------------------+
	for(int c=0; c<nComp; c++){
	    if(dwt.getNomRangeBits(c)!= deftilenr ||
               wp.getQuantizationType().isTileCompSpecified(tileIdx,c) ||
               wp.getQuantizationStep().isTileCompSpecified(tileIdx,c) ||
               wp.getDecompositionLevel().isTileCompSpecified(tileIdx,c) ||
               wp.getGuardBits().isTileCompSpecified(tileIdx,c)){
		writeTileQCC(tileIdx,c);
	    }
            else if(tileQCDwritten){
                if(wp.getQuantizationType().isCompSpecified(c) ||
                   wp.getQuantizationStep().isCompSpecified(c) ||
                   wp.getDecompositionLevel().isCompSpecified(c) ||
                   wp.getGuardBits().isCompSpecified(c)){
                    writeTileQCC(tileIdx,c);
                }
            }
	}

	// +--------------------------+
        // |    RGN maker segment     |
	// +--------------------------+
        if(roiSc.useRoi() &&(!roiSc.getBlockAligned()))
            writeRGN(tileIdx);

	// +--------------------------+
        // |    POC maker segment     |
	// +--------------------------+
	Progression[] prog;
        if( wp.getProgressionType().isTileSpecified(tileIdx) ){
	    prog = (Progression[])(wp.getProgressionType().getTileDef(tileIdx));
	    if(prog.length>1)
                writePOC(false,tileIdx);
        }

	// +--------------------------+
        // |         SOD maker        |
	// +--------------------------+
        hbuf.writeByte(SOD>>8);
        hbuf.writeByte(SOD);
    }
}

