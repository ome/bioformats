/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core;

import com.sun.jimi.core.decoder.bmp.BMPDecoderFactory;
import com.sun.jimi.core.decoder.cur.CURDecoderFactory;
import com.sun.jimi.core.decoder.gif.GIFDecoderFactory;
import com.sun.jimi.core.decoder.ico.ICODecoderFactory;
import com.sun.jimi.core.decoder.png.PNGDecoderFactory;
import com.sun.jimi.core.decoder.sunraster.SunRasterDecoderFactory;
import com.sun.jimi.core.decoder.tga.TGADecoderFactory;
import com.sun.jimi.core.decoder.tiff.TIFDecoderFactory;
import com.sun.jimi.core.decoder.pcx.PCXDecoderFactory;
import com.sun.jimi.core.decoder.pict.PICTDecoderFactory;
import com.sun.jimi.core.decoder.psd.PSDDecoderFactory;
import com.sun.jimi.core.decoder.xbm.XBMDecoderFactory;
import com.sun.jimi.core.decoder.xpm.XPMDecoderFactory;
import com.sun.jimi.core.decoder.builtin.BuiltinDecoderFactory;

import com.sun.jimi.core.encoder.xpm.XPMEncoderFactory;
import com.sun.jimi.core.encoder.png.PNGEncoderFactory;
import com.sun.jimi.core.encoder.jpg.JPGEncoderFactory;
import com.sun.jimi.core.encoder.sunraster.SunRasterEncoderFactory;
import com.sun.jimi.core.encoder.bmp.BMPEncoderFactory;
import com.sun.jimi.core.encoder.psd.PSDEncoderFactory;
import com.sun.jimi.core.encoder.pict.PICTEncoderFactory;
import com.sun.jimi.core.encoder.pcx.PCXEncoderFactory;
import com.sun.jimi.core.encoder.cur.CUREncoderFactory;
import com.sun.jimi.core.encoder.xbm.XBMEncoderFactory;
import com.sun.jimi.core.encoder.tga.TGAEncoderFactory;

import com.sun.jimi.core.JimiSingleImageRasterDecoder;
import com.sun.jimi.core.JimiSingleImageRasterEncoder;
import com.sun.jimi.core.JimiMultiImageRasterDecoder;
import com.sun.jimi.core.JimiMultiImageRasterEncoder;

import com.sun.jimi.core.raster.JimiRasterImageCategorizer;

import com.sun.jimi.core.component.JimiCanvas;
import com.sun.jimi.core.component.JimiCanvasLW;
import com.sun.jimi.core.component.CroppedPreviewCanvas;

// storage
import com.sun.jimi.core.VMemJimiImageFactory;
import com.sun.jimi.core.OneshotJimiImageFactory;

import com.sun.jimi.core.util.StaticPalettes;
import com.sun.jimi.core.NoImageAvailException;

/**
 * Blank class with "import" statements to create static dependencies on all
 * the encoders and decoders.
 */
class JimiProDependencies
{
}
