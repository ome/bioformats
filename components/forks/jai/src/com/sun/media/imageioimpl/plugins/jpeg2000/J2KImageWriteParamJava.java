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
 * $RCSfile: J2KImageWriteParamJava.java,v $
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
 * $Date: 2006/09/20 23:23:30 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.Locale;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import jj2000.j2k.*;
import jj2000.j2k.roi.*;
import jj2000.j2k.quantization.*;
import jj2000.j2k.wavelet.analysis.AnWTFilterSpec;
import jj2000.j2k.image.forwcomptransf.ForwCompTransfSpec;
import jj2000.j2k.entropy.CBlkSizeSpec;
import jj2000.j2k.entropy.PrecinctSizeSpec;
import jj2000.j2k.entropy.ProgressionSpec;
import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.entropy.encoder.LayersInfo;
import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;

/**
 * A subclass of <code>ImageWriteParam</code> for writing images in
 * the JPEG 2000 format.
 *
 * <p>JPEG 2000 plugin supports to losslessly or lossy compress gray-scale,
 * RGB, and RGBA images with byte, unsigned short or short data type.  It also
 * supports losslessly compress bilevel, and 8-bit indexed.  The result data
 * is in the format of JP2 (JPEG 2000 Part 1 or baseline format).
 *
 * <p>Many encoding parameters for JPEG 2000 can be tile-component specific.
 * These parameters are marked as <code>Yes</code> in the column <code>
 * TC_SPEC</code> in the following parameter table.
 * They must be provided according to the pattern:
 * [&lt;tile-component idx>] &lt;param&gt; (repeated as many time as needed),
 * where &lt;tile-component idx&gt; respect the following policy according to
 * the degree of priority:
 * <table>
 * <tr><td>(1) t&lt;idx&gt; c&lt;idx&gt; : Tile-component specification.</td></tr>
 * <tr><td>(2) t&lt;idx&gt; : Tile specification.</td></tr>
 * <tr><td>(3) c&lt;idx&gt; : Component specification.</td></tr>
 * <tr><td>(4) &lt;void&gt; : Default specification.</td></tr>
 * </table>
 * <p>Where the priorities of the specifications are:
 * (1) > (2) > (3) > (4), (">" means "overrides")
 *  &lt;idx&gt;: "," separates indexes, "-" separates bounds of indexes list.
 *  (for example, 0,2-4 means indexes 0,2,3 and  4).
 *
 * <p>The parameters for encoding JPEG 2000 are listed in the following table:
 *
 *  * <p><table border=1>
 * <caption><b>JPEG 2000 Plugin Decoding Parameters</b></caption>
 * <tr><th>Parameter Name</th> <th>Description</th><th>TC_SPEC</th></tr>
 * <tr>
 *    <td>encodingRate</td>
 *    <td> The bitrate in bits-per-pixel for encoding.  Should be set when
 *    lossy compression scheme is used.  With the default value
 *    <code>Double.MAX_VALUE</code>, a lossless compression will be done.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>lossless</td>
 *    <td> Indicates using the loseless scheme or not.  It is equivalent to
 *    use reversible quantization and 5x3 integer wavelet filters.  The
 *    default is <code>true</code>.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>componentTransformation</td>
 *    <td> Specifies to utilize the component transformation on some tiles.
 *    If the wavelet transform is reversible (w5x3 filter), the Reversible
 *    Component Transformation (RCT) is applied. If not reversible
 *    (w9x7 filter), the Irreversible Component Transformation (ICT) is used.
 *    </td>
 *    <td>Yes, Tile_Specific</td>
 * </tr>
 * <tr>
 *    <td>filters</td>
 *    <td> Specifies which wavelet filters to use for the specified
 *    tile-components.  JPEG 2000 part I only supports w5x3 and w9x7 filters.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>decompositionLevel</td>
 *    <td> Specifies the wavelet decomposition levels to apply to
 *    the image.  If it is 0, no wavelet transform is performed, in which
 *    case the original image data will be sent to the encoder and an example
 *    is the binary data.  All components and all tiles have the same number
 *    of decomposition levels.  The default value is 5.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>guardBits</td>
 *    <td> The number of bits used for each tile-component in the quantizer
 *    to avoid overflow.  It takes values in the range 0 through 7.  The
 *    default value is 2.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>quantizationStep</td>
 *    <td> This parameter specifies the base normalized quantization step
 *    size for the tiles/components.  It is normalized to a dynamic range
 *    of 1 in the image domain.  This parameter is ignored in reversible
 *    coding.  The default value is 0.0078125.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>quantizationType</td>
 *    <td> Specifies which quantization type to use for specified
 *    tiles/components.  Not specified for lossless compression.  By default,
 *    the quantization step size is "expounded".  Supported quantization
 *    types specification are : "reversible" (no quantization), "derived"
 *    (derived quantization step size) and "expounded".
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>codeBlockSize</td>
 *    <td> Specifies the maximum code-block size to use for tile-component.
 *    The maximum width and height is 1024, however the block size
 *    (i.e. width x height) must not exceed 4096.  The minimum width and
 *    height is 4.  The default values are (64, 64).
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>progressionType</td>
 *    <td> Specifies which type of progression should be used when generating
 *    the codestream.
 *    <p> The format is [&lt;tile index&gt;]
 *    res|layer|res-pos|pos-comp|comp-pos [res_start comp_start layer_end
 *    res_end comp_end prog] [[res_start comp_start layer_end res_end
 *    comp_end prog]...] [[&lt;tile-component idx]...].
 *    <p>The value "res" generates a resolution progressive
 *    codestream with the number of layers specified by "layers" parameter.
 *    The value "layer" generates a layer progressive codestream with
 *    multiple layers.  In any case, the rate-allocation algorithm optimizes
 *    for best quality in each layer.  The quality measure is mean squared
 *    error (MSE) or a weighted version of it (WMSE).  If no progression
 *    type is specified or imposed by other parameters, the default value
 *    is "layer".  It is also possible to describe progression order
 *    changes.  In this case, "res_start" is the index (from 0) of the
 *    first resolution level, "comp_start" is the index (from 0) of the
 *    first component, "layer_end" is the index (from 0) of the first layer
 *    not included, "res_end" is the index (from 0) of the first
 *    resolution level not included, "comp_end" is index (from 0) of
 *    the first component not included and "prog" is the progression type
 *    to be used for the rest of the tile/image.  Several progression
 *    order changes can be specified, one after the other.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>packPacketHeaderInTile</td>
 *    <td> Indicates that the packet headers are packed in the tiles' headers.
 *    The default is false.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>packPacketHeaderInMain</td>
 *    <td> Indicates that the packet headers are packed in the main header.
 *    The default is false.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>packetPerTilePart</td>
 *    <td> Specifies the maximum number of packets to be put into one tile-part.
 *    Zero means putting all packets in the first tile-part of each tile.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>ROIs</td>
 *    <td> Specifies ROIs shape and location.  The component index specifies
 *    which components contain the ROI.  If this parameter is used, the
 *    codestream is layer progressive by default unless it is overridden by
 *    the <code>progressionType</code>.  A rectanglar or circular ROI can be
 *    specified in the format: [&lt;component idx&gt;] R &lt;left&gt;
 *    &lt;top&gt; &lt;width&gt; &lt;height&gt; or [&lt;component idx&gt;] C
 *    &lt;center x&gt; &lt;center y&gt; &lt;radius&gt;. An arbitrary shape
 *    can be assigned by [&lt;component idx&gt;] A &lt;PGM file&gt;
 *    </td>
 *    <td>Yes, component-specified</td>
 * </tr>
 * <tr>
 *    <td>startLevelROI</td>
 *    <td> This parameter defines the lowest resolution levels to belong to
 *    the ROI.  By doing this, it is possible to avoid getting
 *    information for the ROI at an early stage of transmission.
 *    startLevelROI = 0 means the lowest resolution level belongs to
 *    the ROI, 1 means the second lowest etc.  The default values, -1,
 *    deactivates this parameter.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>alignROI</td>
 *    <td> By specifying this parameter, the ROI mask will be limited to
 *    covering only entire code-blocks.  The ROI coding can then be
 *    performed without any actual scaling of the coefficients but by
 *    instead scaling the distortion estimates.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>bypass</td>
 *    <td> Uses the lazy coding mode with the entropy coder.  This will bypass
 *    the MQ coder for some of the coding passes, where the distribution
 *    is often close to uniform.  Since the MQ codeword will be terminated
 *    at least once per lazy pass, it is important to use an efficient
 *    termination algorithm, <code>methodForMQTermination</code>.
 *    true enables, and false disables it.  The default value is false.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>resetMQ</td>
 *    <td> If this is enabled the probability estimates of the MQ coder are
 *    reset after each arithmetically coded (i.e. non-lazy) coding pass.
 *    true enables, and false disables it.  The default value is false.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>terminateOnByte</td>
 *    <td> If this is enabled the codeword (raw or MQ) is terminated on a byte
 *    boundary after each coding pass. In this case it is important to use
 *    an efficient termination algorithm, "methodForMQTermination".
 *    true enables, and false disables it.  The default value is false.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>causalCXInfo</td>
 *    <td> Uses vertically stripe causal context formation.  If this is
 *    enabled the context formation process in one stripe is independant of
 *    the next stripe (i.e. the one below it). true enables, and false
 *    disables it.  The default value is false.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>codeSegSymbol</td>
 *    <td> Inserts an error resilience segmentation symbol in the MQ codeword
 *    at the end of each bit-plane (cleanup pass). Decoders can use this
 *    information to detect and conceal errors. true enables, and false
 *    disables it.  The default value is false.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>methodForMQTermination</td>
 *    <td> Specifies the algorithm used to terminate the MQ codeword.  The
 *    most efficient one is "near_opt", which delivers a codeword which
 *    in almost all cases is the shortest possible.  The "easy" is a
 *    simpler algorithm that delivers a codeword length that is close
 *    to the previous one (in average 1 bit longer).  The "predict" is
 *    almost the same as the "easy" but it leaves error resilient
 *    information on the spare least significant bits (in average 3.5 bits),
 *    which can be used by a decoder to detect errors.  The "full" algorithm
 *    performs a full flush of the MQ coder and is highly inefficient.  It
 *    is important to use a good termination policy since the MQ codeword
 *    can be terminated quite often, specially if the "bypass" or
 *    "terminateOnByte" parameters are enabled (in the normal case it would
 *    be terminated once per code-block, while "terminateOnByte" is specified
 *    it will be done almost 3 times per bit-plane in each code-block).
 *    The default value is "near_opt".
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>methodForMQLengthCalc</td>
 *    <td> Specifies the algorithm to use in calculating the necessary MQ
 *    length for each decoding pass.  The best one is "near_opt", which
 *    performs a rather sophisticated calculation and provides the best
 *    results.  The "lazy_good" and "lazy" are very simple algorithms
 *    that provide rather conservative results. "lazy_good" performs
 *    slightly better.  Please use the default unless the experiments
 *    show the benefits of different length calculation algorithms.
 *    The default value is "near_opt".
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>precinctPartition</td>
 *    <td> Specifies precinct partition dimensions for tiles/components.  They
 *    are stored from those applied to the highest resolution to those
 *    applied to the remaining resolutions in decreasing order.  If less
 *    values than the number of decomposition levels are specified, then
 *    the last two values are used for the remaining resolutions.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>layers</td>
 *    <td> Explicitly specifies the codestream layer formation parameters.
 *    The rate (double) parameter specifies the bitrate to which the first
 *    layer should be optimized.  The layers (int) parameter, if present,
 *    specifies the number of extra layers that should be added for
 *    scalability.  These extra layers are not optimized.  Any extra rate
 *    and layers parameters add more layers, in the same way.  An
 *    additional layer is always added at the end, which is optimized
 *    to the overall target bitrate of the bit stream. Any layers
 *    (optimized or not) whose target bitrate is higher that the overall
 *    target bitrate are silently ignored. The bitrates of the extra layers
 *    that are added through the layers parameter are approximately
 *    log-spaced between the other target bitrates.  If several (rate, layers)
 *    constructs appear the rate parameters must appear in increasing order.
 *    The rate allocation algorithm ensures that all coded layers have a
 *    minimal reasonable size, if not these layers are silently ignored.
 *    Default: 0.015 +20 2.0 +10.
 *    </td>
 *    <td>No</td>
 * </tr>
 * <tr>
 *    <td>SOP</td>
 *    <td>Specifies whether start of packet (SOP) markers should be used.
 *    true enables, false disables it.  The default value is false.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * <tr>
 *    <td>EPH</td>
 *    <td>Specifies whether end of packet header (EPH) markers should be used.
 *    true enables, false disables it.  The default value is false.
 *    </td>
 *    <td>Yes</td>
 * </tr>
 * </table>
 */
public class J2KImageWriteParamJava extends ImageWriteParam {
    /**
     * Indicates that the packet headers are packed in the tiles' headers.
     */
    private boolean packPacketHeaderInTile = false;

    /**
     * Indicates that the packet headers are packed in the main header.
     */
    private boolean packPacketHeaderInMain = false;

    /**
     * Specifies the maximum number of packets to be put into one tile-part.
     * Zero means include all packets in first tile-part of each tile.
     */
    private int packetPerTilePart = 0;

    /**
     * The bitrate in bits-per-pixel for encoding.  Should be set when lossy
     * compression scheme is used.  The default is
     * <code>Double.MAX_VALUE</code>.
     */
    private double encodingRate = Double.MAX_VALUE;

    /**
     * Indicates using the loseless scheme or not.  It is equivalent to
     * use reversible quantization and 5x3 integer wavelet filters.
     */
    private boolean lossless = true;

    /** Specifies to utilize the component transformation with some tiles.
     *  If the wavelet transform is reversible (w5x3 filter), the
     *  Reversible Component Transformation (RCT) is applied. If not reversible
     *  (w9x7 filter), the Irreversible Component Transformation (ICT)
     *  is used.
     */
    private ForwCompTransfSpec componentTransformation = null;
    private boolean enableCT = true;

    /** Specifies which filters to use for the specified tile-components.
     *  JPEG 2000 part I only supports w5x3 and w9x7 filters.
     */
    private AnWTFilterSpec filters = null;

    /** Specifies the number of wavelet decomposition levels to apply to
     *  the image.  If it is 0, no wavelet transform is performed, in which
     *  case the original image data will be sent to the encoder and an
     *  example is the binary data.  All components and all tiles have
     *  the same number of decomposition levels. Default: 5.
     */
    private IntegerSpec decompositionLevel = null; // = 5;

    /** The number of bits used for each tile-component in
     *  the quantizer to avoid overflow.  It takes values in the range 0
     *  through 7.  Default: 2.
     */
    private GuardBitsSpec guardBits = null;

    /** This parameter specifies the base normalized quantization step
     *  size for the tiles/components.  It is normalized to a dynamic range
     *  of 1 in the image domain.  This parameter is ignored in reversible
     *  coding.  Default: 0.0078125.
     */
    private QuantStepSizeSpec quantizationStep = null;

    /** Specifies which quantization type to use for specified
     *  tiles/components.  Not specified for lossless compression.  By
     *  default , the quantization step size is "expounded".  Supported
     *  quantization types specification are : "reversible" (no quantization),
     *  "derived" (derived quantization step size) and "expounded".
     */
    private QuantTypeSpec quantizationType = null;

    /** This parameter defines the lowest resolution levels to belong to
     *  the ROI.  By doing this, it is possible to avoid only getting
     *  information for the ROI at an early stage of transmission.
     *  startLevelROI = 0 means the lowest resolution level belongs to
     *  the ROI, 1 means the second lowest etc.  The default values, -1,
     *  deactivates this parameter.
     */
    private int startLevelROI = -1;

    /** By specifying this parameter, the ROI mask will be limited to
     *  covering only entire code-blocks. The ROI coding can then be
     *  performed without any actual scaling of the coefficients but
     *  by instead scaling the distortion estimates.
     */
    private boolean alignROI = false;

    /** Specifies ROIs shape and location. The component index specifies
     *  which components contain the ROI.  If this parameter is used, the
     *  codestream is layer progressive by default unless it is
     *  overridden by the <code>progressionType</code>.
     */
    private MaxShiftSpec ROIs = null;

    /** Specifies the maximum code-block size to use for tile-component.
     *  The maximum width and height is 1024, however the image area
     *  (i.e. width x height) must not exceed 4096. The minimum
     *  width and height is 4.  Default: 64 64.
     */
    private CBlkSizeSpec codeBlockSize = null;

    /** Uses the lazy coding mode with the entropy coder.  This will bypass
     *  the MQ coder for some of the coding passes, where the distribution
     *  is often close to uniform.  Since the MQ codeword will be terminated
     *  at least once per lazy pass, it is important to use an efficient
     *  termination algorithm, <code>methodForMQTermination</code>.
     *  true enables, and false disables it.  Default: false.
     */
    private StringSpec bypass = null;

    /** If this is enabled the probability estimates of the MQ coder are
     *  reset after each arithmetically coded (i.e. non-lazy) coding pass.
     *  true enables, and false disables it.  Default: false.
     */
    private StringSpec resetMQ = null;

    /** If this is enabled the codeword (raw or MQ) is terminated on a byte
     *  boundary after each coding pass. In this case it is important to
     *  use an efficient termination algorithm, the "methodForMQTermination".
     *  true enables, and false disables it.  Default: false.
     */
    private StringSpec terminateOnByte = null;

    /** Uses vertically stripe causal context formation.  If this is
     *  enabled the context formation process in one stripe is independant
     *  of the next stripe (i.e. the one below it). true enables, and
     *  false disables it.  Default: false.
     */
     private StringSpec causalCXInfo = null;

    /** Inserts an error resilience segmentation symbol in the MQ codeword
     *  at the end of each bit-plane (cleanup pass). Decoders can use this
     *  information to detect and conceal errors. true enables,
     *  and false disables it.  Default: false.
     */
     private StringSpec codeSegSymbol = null;

    /** Specifies the algorithm used to terminate the MQ codeword.  The
     *  most efficient one is "near_opt", which delivers a codeword which
     *  in almost all cases is the shortest possible.  The "easy" is a
     *  simpler algorithm that delivers a codeword length that is close
     *  to the previous one (in average 1 bit longer).  The "predict" is
     *  almost the same as the "easy" but it leaves error resilient
     *  information on the spare least significant bits (in average
     *  3.5 bits), which can be used by a decoder to detect errors.
     *  The "full" algorithm performs a full flush of the MQ coder and
     *  is highly inefficient.  It is important to use a good termination
     *  policy since the MQ codeword can be terminated quite often,
     *  specially if the "bypass" or "terminateOnByte" parameters are
     *  enabled (in the normal case it would be terminated once per
     *  code-block, while "terminateOnByte" is specified it will be
     *  done almost 3 times per bit-plane in each code-block).
     *  Default: near_opt.
     */
    private StringSpec methodForMQTermination = null;

    /** Specifies the algorithm to use in calculating the necessary MQ
     *  length for each decoding pass.  The best one is "near_opt", which
     *  performs a rather sophisticated calculation and provides the best
     *  results.  The "lazy_good" and "lazy" are very simple algorithms
     *  that provide rather conservative results. "lazy_good" performs
     *  slightly better.  Please use the default unless the experiments
     *  show the benefits of different length calculation algorithms.
     *  Default: near_opt.
     */
    private StringSpec methodForMQLengthCalc = null;

    /** Specifies precinct partition dimensions for tiles/components.  They
     *  are stored from those applied to the highest resolution to those
     *  applied to the remaining resolutions in decreasing order.  If less
     *  values than the number of decomposition levels are specified, then
     *  the last two values are used for the remaining resolutions.
     */
    private PrecinctSizeSpec precinctPartition = null;

    /** Specifies which type of progression should be used when generating
     *  the codestream. The value "res" generates a resolution progressive
     *  codestream with the number of layers specified by "layers" parameter.
     *  The value "layer" generates a layer progressive codestream with
     *  multiple layers.  In any case, the rate-allocation algorithm optimizes
     *  for best quality in each layer.  The quality measure is mean squared
     *  error (MSE) or a weighted version of it (WMSE).  If no progression
     *  type is specified or imposed by other modules, the default value
     *  is "layer".  It is also possible to describe progression order
     *  changes.  In this case, "res_start" is the index (from 0) of the
     *  first resolution level, "comp_start" is the index (from 0) of the
     *  first component, "ly_end" is the index (from 0) of the first layer
     *  not included, "res_end" is the index (from 0) of the first
     *  resolution level not included, "comp_end" is index (from 0) of
     *  the first component not included and "prog" is the progression type
     *  to be used for the rest of the tile/image.  Several progression
     *  order changes can be specified, one after the other.
     */
    private ProgressionSpec progressionType = null;

    /**
     * The specified (tile-component) progression.  Will be used to generate
     * the progression type.
     */
    private String progressionName = null;

    /** Explicitly specifies the codestream layer formation parameters.
     *  The rate (double) parameter specifies the bitrate to which the first
     *  layer should be optimized.  The layers (int) parameter, if present,
     *  specifies the number of extra layers that should be added for
     *  scalability.  These extra layers are not optimized.  Any extra rate
     *  and layers parameters add more layers, in the same way.  An
     *  additional layer is always added at the end, which is optimized
     *  to the overall target bitrate of the bit stream. Any layers
     *  (optimized or not) whose target bitrate is higher that the
     *  overall target bitrate are silently ignored. The bitrates of
     *  the extra layers that are added through the layers parameter
     *  are approximately log-spaced between the other target bitrates.
     *  If several (rate, layers) constructs appear the rate parameters
     *  must appear in increasing order. The rate allocation algorithm
     *  ensures that all coded layers have a minimal reasonable size,
     *  if not these layers are silently ignored.  Default: 0.015 +20 2.0 +10.
     */
     private String layers = "0.015 +20 2.0 +10";

    /** Specifies whether end of packet header (EPH) markers should be used.
     *  true enables, false disables it.  Default: false.
     */
     private StringSpec EPH = null;

    /** Specifies whether start of packet (SOP) markers should be used.
     *  true enables, false disables it. Default: false.
     */
    private StringSpec SOP = null;

    private int numTiles;
    private int numComponents;

    private RenderedImage imgsrc;
    private Raster raster;

    private int minX;
    private int minY;

    /** Constructor to set locales. */
    public J2KImageWriteParamJava(RenderedImage imgsrc, Locale locale) {
        super(locale);
        setDefaults(imgsrc);
    }

    /** Constructor to set locales. */
    public J2KImageWriteParamJava(IIOImage image,  ImageWriteParam param) {
        super(param.getLocale());
        if(image != null) {
            if (image.hasRaster())
                setDefaults(image.getRaster());
            else
                setDefaults(image.getRenderedImage());
        }

        setSourceRegion(param.getSourceRegion());
        setSourceBands(param.getSourceBands());
        try {
            setTiling(param.getTileWidth(), param.getTileHeight(),
                      param.getTileGridXOffset(), param.getTileGridYOffset());
        } catch (IllegalStateException e) {
            // tileing is not set do nothing.
        }

        setDestinationOffset(param.getDestinationOffset());
        setSourceSubsampling(param.getSourceXSubsampling(),
                             param.getSourceYSubsampling(),
                             param.getSubsamplingXOffset(),
                             param.getSubsamplingYOffset());
        setDestinationType(param.getDestinationType());

        J2KImageWriteParam j2kParam;
        if(param instanceof J2KImageWriteParam) {
            j2kParam = (J2KImageWriteParam)param;
        } else {
            j2kParam = new J2KImageWriteParam();
        }

        setDecompositionLevel(""+j2kParam.getNumDecompositionLevels());
        setEncodingRate(j2kParam.getEncodingRate());
        setLossless(j2kParam.getLossless());
        setFilters(j2kParam.getFilter());
        setEPH("" + j2kParam.getEPH());
        setSOP("" + j2kParam.getSOP());
        setProgressionName(j2kParam.getProgressionType());
        int[] size = j2kParam.getCodeBlockSize();
        setCodeBlockSize("" + size[0] +" " + size[1]);
        enableCT = j2kParam.getComponentTransformation();
        setComponentTransformation("" + enableCT);
    }


    /**
     * Constructs a <code>J2KImageWriteParamJava</code> object with default
     * values for all parameters.
     */
    public J2KImageWriteParamJava() {
        super();
        setSuperProperties();
    }

    /**
     * Constructs a <code>J2KImageWriteParamJava</code> object with default
     * values for all parameters.
     */
    public J2KImageWriteParamJava(RenderedImage imgsrc) {
        super();
        setDefaults(imgsrc);
    }

    /**
     * Constructs a <code>J2KImageWriteParamJava</code> object with default
     * values for all parameters.
     */
    public J2KImageWriteParamJava(Raster raster) {
        super();
        setDefaults(raster);
    }

    private void setSuperProperties() {
        canOffsetTiles = true;
        canWriteTiles = true;
        canOffsetTiles = true;
        canWriteProgressive = true;
        tilingMode = MODE_EXPLICIT;
    }

    /** Set source */
    private void setDefaults(Raster raster) {
        // override the params in the super class
        setSuperProperties();

        if (raster != null) {
            this.raster = raster;
            tileGridXOffset = raster.getMinX();
            tileGridYOffset = raster.getMinY();
            tileWidth = raster.getWidth();
            tileHeight = raster.getHeight();
            tilingSet = true;

            numTiles = 1;
            numComponents = raster.getSampleModel().getNumBands();
        }
        setDefaults();
    }

    /** Set source */
    private void setDefaults(RenderedImage imgsrc) {
        // override the params in the super class
        setSuperProperties();

        tilingMode = MODE_EXPLICIT;

        if (imgsrc != null) {
            this.imgsrc = imgsrc;
            tileGridXOffset = imgsrc.getTileGridXOffset();
            tileGridYOffset = imgsrc.getTileGridYOffset();
            tileWidth = imgsrc.getTileWidth();
            tileHeight = imgsrc.getTileHeight();
            tilingSet = true;

            numTiles = imgsrc.getNumXTiles() * imgsrc.getNumYTiles();
            numComponents = imgsrc.getSampleModel().getNumBands();
        }
        setDefaults();
    }

    private void setDefaults() {
        setROIs(null);
        setQuantizationType(null);
        setQuantizationStep(null);
        setGuardBits(null);
        setFilters(null);
        setDecompositionLevel(null);
        setComponentTransformation(null);
        setMethodForMQLengthCalc(null);
        setMethodForMQTermination(null);
        setCodeSegSymbol(null);
        setCausalCXInfo(null);
        setTerminateOnByte(null);
        setResetMQ(null);
        setBypass(null);
        setCodeBlockSize(null);
        setPrecinctPartition(null);
        setSOP(null);
        setEPH(null);
    }

    /** Sets <code>encodingRate</code> */
    public void setEncodingRate(double rate) {
        this.encodingRate = rate;
    }

    /** Gets <code>encodingRate</code> */
    public double getEncodingRate() {
        return encodingRate;
    }

    /** Sets <code>lossless</code> */
    public void setLossless(boolean lossless) {
        this.lossless = lossless;
    }

    /** Gets <code>encodingRate</code> */
    public boolean getLossless() {
        return lossless;
    }
    /** Sets <code>packetPerTilePart</code> */
    public void setPacketPerTilePart(int packetPerTilePart) {
        if (packetPerTilePart < 0)
            throw new IllegalArgumentException(I18N.getString("J2KImageWriteParamJava0"));

        this.packetPerTilePart = packetPerTilePart;
        if (packetPerTilePart > 0) {
            setSOP("true");
            setEPH("true");
        }
    }

    /** Gets <code>packetPerTilePart</code> */
    public int getPacketPerTilePart() {
        return packetPerTilePart;
    }

    /** Sets <code>packPacketHeaderInTile</code> */
    public void setPackPacketHeaderInTile(boolean packPacketHeaderInTile) {
        this.packPacketHeaderInTile = packPacketHeaderInTile;
        if (packPacketHeaderInTile) {
            setSOP("true");
            setEPH("true");
        }
    }

    /** Gets <code>packPacketHeaderInTile</code> */
    public boolean getPackPacketHeaderInTile() {
        return packPacketHeaderInTile;
    }

    /** Sets <code>packPacketHeaderInMain</code> */
    public void setPackPacketHeaderInMain(boolean packPacketHeaderInMain) {
        this.packPacketHeaderInMain = packPacketHeaderInMain;
        if (packPacketHeaderInMain) {
            setSOP("true");
            setEPH("true");
        }
    }

    /** Gets <code>packPacketHeaderInMain</code> */
    public boolean getPackPacketHeaderInMain() {
        return packPacketHeaderInMain;
    }

    /** Sets <code>alignROI</code> */
    public void setAlignROI(boolean align) {
        alignROI = align;
    }

    /** Gets <code>alignROI</code> */
    public boolean getAlignROI() {
        return alignROI;
    }

    /** Sets <code>ROIs</code> */
    public void setROIs(String values) {
        ROIs = new MaxShiftSpec(numTiles, numComponents, ModuleSpec.SPEC_TYPE_TILE_COMP, values);
    }

    /** Gets <code>ROIs</code> */
    public MaxShiftSpec getROIs() {
        return ROIs;
    }

    /** Sets <code>quantizationType</code> */
    public void setQuantizationType(String values) {
        quantizationType = new QuantTypeSpec(numTiles, numComponents,
                ModuleSpec.SPEC_TYPE_TILE_COMP, this, values);
    }

    /** Gets <code>quantizationType</code> */
    public QuantTypeSpec getQuantizationType() {
        return quantizationType;
    }

    /** Sets <code>quantizationStep</code> */
    public void setQuantizationStep(String values) {
        quantizationStep = new QuantStepSizeSpec(numTiles,
                                                 numComponents,
                                                 ModuleSpec.SPEC_TYPE_TILE_COMP,
                                                 this,
                                                 values);
    }

    /** Gets <code>quantizationStep</code> */
    public QuantStepSizeSpec getQuantizationStep() {
        return quantizationStep;
    }

    /** Sets <code>guardBits</code> */
    public void setGuardBits(String values) {
        guardBits = new GuardBitsSpec(numTiles,
                                      numComponents,
                                      ModuleSpec.SPEC_TYPE_TILE_COMP,
                                      this,
                                      values);
    }

    /** Gets <code>guardBits</code> */
    public GuardBitsSpec getGuardBits() {
        return guardBits;
    }

    /** Sets <code>filters</code> */
    // NOTE This also sets quantizationType and componentTransformation.
    public void setFilters(String values) {
	if (J2KImageWriteParam.FILTER_97.equals(values))
	    setQuantizationType ("expounded");
	else
	    setQuantizationType("reversible");

        filters = new AnWTFilterSpec(numTiles,
                                     numComponents,
                                     ModuleSpec.SPEC_TYPE_TILE_COMP,
                                     (QuantTypeSpec)quantizationType,
                                     this,
                                     values);
        setComponentTransformation(""+enableCT);
    }

    /** Gets <code>filters</code> */
    public AnWTFilterSpec getFilters() {
        return filters;
    }

    /** Sets <code>decompositionLevel</code> */
    public void setDecompositionLevel(String values) {
        decompositionLevel = new IntegerSpec(numTiles,
                                             numComponents,
                                             ModuleSpec.SPEC_TYPE_TILE_COMP,
                                             this,
                                             values,
                                             "5");

        // NOTE The precinctPartition depends upon decompositionLevel
        // so it needs to be re-initialized. Note that the parameter of
        // setPrecinctPartition() is not used in the current implementation.
        setPrecinctPartition(null);
    }

    /** Gets <code>decompositionLevel</code> */
    public IntegerSpec getDecompositionLevel() {
        return decompositionLevel;
    }

    /** Sets <code>componentTransformation</code> */
    // NOTE This requires filters having been set previously.
    public void setComponentTransformation(String values) {
        componentTransformation =
            new ForwCompTransfSpec(numTiles,
                                   numComponents,
                                   ModuleSpec.SPEC_TYPE_TILE,
                                   (AnWTFilterSpec)filters,
                                   this,
                                   values);
    }

    /** Gets <code>componentTransformation</code> */
    public ForwCompTransfSpec getComponentTransformation() {
        return componentTransformation;
    }
    /** Sets <code>methodForMQLengthCalc</code> */
    public void setMethodForMQLengthCalc(String values) {
        String[] strLcs = {"near_opt","lazy_good","lazy"};
        methodForMQLengthCalc =
            new StringSpec(numTiles,
                           numComponents,
                           ModuleSpec.SPEC_TYPE_TILE_COMP,
                           "near_opt",
                           strLcs,
                           this,
                           values);
    }

    /** Gets <code>methodForMQLengthCalc</code> */
    public StringSpec getMethodForMQLengthCalc() {
        return methodForMQLengthCalc;
    }

    /** Sets <code>methodForMQTermination</code> */
    public void setMethodForMQTermination(String values) {
        String[] strTerm = {"near_opt","easy","predict","full"};
        methodForMQTermination =
            new StringSpec(numTiles,
                           numComponents,
                           ModuleSpec.SPEC_TYPE_TILE_COMP,
                           "near_opt",
                           strTerm,
                           this,
                           values);
    }

    /** Gets <code>methodForMQTermination</code> */
    public StringSpec getMethodForMQTermination() {
        return methodForMQTermination;
    }

    /** Sets <code>codeSegSymbol</code> */
    public void setCodeSegSymbol(String values) {
        String[] strBoolean = {"true","false"};
        codeSegSymbol =
            new StringSpec(numTiles,
                           numComponents,
                           ModuleSpec.SPEC_TYPE_TILE_COMP,
                           "false",
                           strBoolean,
                           this,
                           values);
    }

    /** Gets <code>codeSegSymbol</code> */
    public StringSpec getCodeSegSymbol() {
        return codeSegSymbol;
    }

    /** Sets <code>causalCXInfo</code> */
    public void setCausalCXInfo(String values) {
        String[] strBoolean = {"true","false"};
        causalCXInfo = new StringSpec(numTiles,
                                      numComponents,
                                      ModuleSpec.SPEC_TYPE_TILE_COMP,
                                      "false",
                                      strBoolean,
                                      this,
                                      values);
    }

    /** Gets <code>causalCXInfo</code> */
    public StringSpec getCausalCXInfo() {
        return causalCXInfo;
    }

    /** Sets <code>terminateOnByte</code> */
    public void setTerminateOnByte(String values) {
        String[] strBoolean = {"true","false"};
        terminateOnByte = new StringSpec(numTiles,
                                         numComponents,
                                         ModuleSpec.SPEC_TYPE_TILE_COMP,
                                         "false",
                                         strBoolean,
                                         this,
                                         values);
    }

    /** Gets <code>terminateOnByte</code> */
    public StringSpec getTerminateOnByte() {
        return terminateOnByte;
    }

    /** Sets <code>resetMQ</code> */
    public void setResetMQ(String values) {
        String[] strBoolean = {"true","false"};
        resetMQ = new StringSpec(numTiles,
                                 numComponents,
                                 ModuleSpec.SPEC_TYPE_TILE_COMP,
                                 "false",
                                 strBoolean,
                                 this,
                                 values);
    }

    /** Gets <code>resetMQ</code> */
    public StringSpec getResetMQ() {
        return resetMQ;
    }

    /** Sets <code>bypass</code> */
    public void setBypass(String values) {
        String[] strBoolean = {"true","false"};
        bypass = new StringSpec(numTiles,
                                numComponents,
                                ModuleSpec.SPEC_TYPE_TILE_COMP,
                                "false",
                                strBoolean,
                                this,
                                values);
    }

    /** Gets <code>bypass</code> */
    public StringSpec getBypass() {
        return bypass;
    }

    /** Sets <code>codeBlockSize</code> */
    public void setCodeBlockSize(String values) {
        codeBlockSize = new CBlkSizeSpec(numTiles,
                                         numComponents,
                                         ModuleSpec.SPEC_TYPE_TILE_COMP,
                                         this,
                                         values);
    }

    /** Gets <code>codeBlockSize</code> */
    public CBlkSizeSpec getCodeBlockSize() {
        return codeBlockSize;
    }

    /** Sets <code>precinctPartition</code> */
    public void setPrecinctPartition(String values) {
        String[] strBoolean = {"true","false"};
        if (imgsrc != null)
            precinctPartition =
                new PrecinctSizeSpec(numTiles,
                                     numComponents,
                                     ModuleSpec.SPEC_TYPE_TILE_COMP,
                                     new RenderedImageSrc(imgsrc, this, null),
                                     decompositionLevel,
                                     this,
                                     values);
        else if (raster != null)
            precinctPartition =
                new PrecinctSizeSpec(numTiles,
                                     numComponents,
                                     ModuleSpec.SPEC_TYPE_TILE_COMP,
                                     new RenderedImageSrc(raster, this, null),
                                     decompositionLevel,
                                     this,
                                     values);
    }

    /** Gets <code>precinctPartition</code> */
    public PrecinctSizeSpec getPrecinctPartition() {
        return precinctPartition;
    }

    /** Sets <code>SOP</code> */
    public void setSOP(String values) {
            String[] strBoolean = {"true","false"};
            SOP = new StringSpec(numTiles,
                                 numComponents,
                                 ModuleSpec.SPEC_TYPE_TILE_COMP,
                                 "false",
                                 strBoolean,
                                 this,
                                 values);
    }

    /** Gets <code>SOP</code> */
    public StringSpec getSOP() {
        return SOP;
    }

    /** Sets <code>EPH</code> */
    public void setEPH(String values) {
        String[] strBoolean = {"true","false"};
        EPH = new StringSpec(numTiles,
                             numComponents,
                             ModuleSpec.SPEC_TYPE_TILE_COMP,
                             "false",
                             strBoolean,
                             this,
                             values);
    }

    /** Gets <code>EPH</code> */
    public StringSpec getEPH() {
        return EPH;
    }

    /** Sets <code>progressionName</code> */
    public void setProgressionName(String values) {
        progressionName = values;
    }

    /** Gets <code>progressionType</code> */
    public String getProgressionName() {
        return progressionName;
    }

    /** Sets <code>progressionType</code> */
    public void setProgressionType(LayersInfo lyrs, String values) {
        String[] strBoolean = {"true","false"};
        progressionType = new ProgressionSpec(numTiles,
                                              numComponents,
                                              lyrs.getTotNumLayers(),
                                              decompositionLevel,
                                              ModuleSpec.SPEC_TYPE_TILE_COMP,
                                              this,
                                              values);
    }

    /** Gets <code>progressionType</code> */
    public ProgressionSpec getProgressionType() {
        return progressionType;
    }

    /** Sets the <code>startLevelROI</code> */
    public void setStartLevelROI(int value) {
        startLevelROI = value;
    }

    /** Gets <code>startLevel</code> */
    public int getStartLevelROI() {
        return startLevelROI;
    }

    /** Sets the <code>layers</code> */
    public void setLayers(String value) {
        layers = value;
    }

    /** Gets <code>layers</code> */
    public String getLayers() {
        return layers;
    }

    /** Sets <code>minX</code> */
    public void setMinX(int minX) {
        this.minX = minX;
    }

    /** Gets <code>minX</code> */
    public int getMinX() {
        return minX;
    }

    /** Sets <code>minY</code> */
    public void setMinY(int minY) {
        this.minY = minY;
    }

    /** Gets <code>minY</code> */
    public int getMinY() {
        return minY;
    }

    /** Gets the number of tiles */
    public int getNumTiles() {
        Rectangle sourceRegion = getSourceRegion();
        if (sourceRegion == null) {
            if (imgsrc != null)
                sourceRegion = new Rectangle(imgsrc.getMinX(),
                                         imgsrc.getMinY(),
                                         imgsrc.getWidth(),
                                         imgsrc.getHeight());
            else  sourceRegion = raster.getBounds();
        } else {
            if (imgsrc != null)
                sourceRegion =
                    sourceRegion.intersection(new Rectangle(imgsrc.getMinX(),
                                         imgsrc.getMinY(),
                                         imgsrc.getWidth(),
                                         imgsrc.getHeight()));
            else  sourceRegion = sourceRegion.intersection(raster.getBounds());
        }

        int scaleX = getSourceXSubsampling();
        int scaleY = getSourceYSubsampling();
        int xOffset = getSubsamplingXOffset();
        int yOffset = getSubsamplingYOffset();

        int w = (sourceRegion.width - xOffset + scaleX - 1) / scaleX;
        int h = (sourceRegion.height - yOffset + scaleY - 1) / scaleY;

        minX = (sourceRegion.x + xOffset) / scaleX;
        minY = (sourceRegion.y + yOffset) / scaleY;

        numTiles = (int)((Math.floor((minX + w + tileWidth - 1.0) / tileWidth) -
                   Math.floor((double)minX/tileWidth) ) *
                   (Math.floor((minY + h + tileHeight - 1.0) / tileHeight) -
                   Math.floor((double)minY/tileHeight) ) );
        tileGridXOffset += (minX - tileGridXOffset) / tileWidth * tileWidth;
        tileGridYOffset += (minY - tileGridYOffset) / tileHeight * tileHeight;

        return numTiles;
    }

    /** Gets the number of components */
    public int getNumComponents() {
        return numComponents;
    }

    /** Override the method setSourceBands in the super class.  This method
     *  should be called before any tile-specific parameter setting method
     *  to be called.
     */
    public void setSourceBands(int[] bands) {
        super.setSourceBands(bands);
        if (bands != null) {
            numComponents = bands.length;
            setDefaults();
        }
    }

    /** Override the method setTiling in the super class.  This method
     *  should be called before any tile-specific parameter setting method
     *  to be called.
     */
    public void setTiling(int tw, int th, int xOff, int yOff) {
        super.setTiling(tw, th, xOff, yOff);
        getNumTiles();
        setDefaults();
    }

    /** Override the method setSourceSubsampling in the super class.  This
     *  method should be called before any tile-specific parameter setting
     *  method to be called.
     */
    public void setSourceSubsampling(int sx, int sy, int xOff, int yOff) {
        super.setSourceSubsampling(sx, sy, xOff, yOff);
        getNumTiles();
        setDefaults();
    }
}
