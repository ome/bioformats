/*
 * Copyright 1998 by Sun Microsystems, Inc.
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

import com.sun.jimi.core.util.JimiUtil;

import java.util.Vector;

/**
 * Extension object for Jimi Professional formats.
 * @author  Luke Gorrie
 * @version $Revision: 1.3 $ $Date: 1999/04/07 14:34:00 $
 */
public class JimiProExtension implements JimiExtension
{
	private static final String VENDOR = "Sun Microsystems, Inc.";
	private static final String DESCRIPTION = "Jimi Professional formats";
	private static final String VERSION = "2.0";

	private static JimiDecoderFactory[] DECODERS;

	private static String[] decoderNames
	= {
		"com.sun.jimi.core.decoder.bmp.BMPDecoderFactory",
		"com.sun.jimi.core.decoder.cur.CURDecoderFactory",
		"com.sun.jimi.core.decoder.gif.GIFDecoderFactory",
		"com.sun.jimi.core.decoder.ico.ICODecoderFactory",
		"com.sun.jimi.core.decoder.png.PNGDecoderFactory",
		"com.sun.jimi.core.decoder.sunraster.SunRasterDecoderFactory",
		"com.sun.jimi.core.decoder.tga.TGADecoderFactory",
		"com.sun.jimi.core.decoder.tiff.TIFDecoderFactory",
		"com.sun.jimi.core.decoder.pcx.PCXDecoderFactory",
		"com.sun.jimi.core.decoder.pict.PICTDecoderFactory",
		"com.sun.jimi.core.decoder.psd.PSDDecoderFactory",
		"com.sun.jimi.core.decoder.xbm.XBMDecoderFactory",
		"com.sun.jimi.core.decoder.xpm.XPMDecoderFactory",
		"com.sun.jimi.core.decoder.builtin.BuiltinDecoderFactory"
	};

	private static JimiEncoderFactory[] ENCODERS;

	private static String[] encoderNames
	= {
		"com.sun.jimi.core.encoder.xpm.XPMEncoderFactory",
		"com.sun.jimi.core.encoder.xbm.XBMEncoderFactory",
		"com.sun.jimi.core.encoder.png.PNGEncoderFactory",
		"com.sun.jimi.core.encoder.sunraster.SunRasterEncoderFactory",
		"com.sun.jimi.core.encoder.bmp.BMPEncoderFactory",
		"com.sun.jimi.core.encoder.psd.PSDEncoderFactory",
		"com.sun.jimi.core.encoder.pict.PICTEncoderFactory",
		"com.sun.jimi.core.encoder.pcx.PCXEncoderFactory",
		"com.sun.jimi.core.encoder.tga.TGAEncoderFactory",
		null // last space reserved for jpeg
	};

	// find all available encoders and decoders
	static
	{
		// jdk 1.2 present?
		// if (JimiUtil.isCompatibleWithJavaVersion(1, 2)) {
		// 	encoderNames[encoderNames.length - 1] = "com.sun.jimi.core.encoder.jpg_codec.JPGEncoderFactory";
		// }
		// else {
		encoderNames[encoderNames.length - 1] = "com.sun.jimi.core.encoder.jpg.JPGEncoderFactory";
		// }
		Vector decoders = new Vector();
		for (int i = 0; i < decoderNames.length; i++) {
			try {
				Class klass = Class.forName(decoderNames[i]);
				JimiDecoderFactory factory = (JimiDecoderFactory)klass.newInstance();
				decoders.addElement(factory);
			}
			catch (Exception e) {
			}
		}
		DECODERS = new JimiDecoderFactory[decoders.size()];
		decoders.copyInto(DECODERS);

		Vector encoders = new Vector();
		for (int i = 0; i < encoderNames.length; i++) {
			try {
				Class klass = Class.forName(encoderNames[i]);
				JimiEncoderFactory factory = (JimiEncoderFactory)klass.newInstance();
				encoders.addElement(factory);
			}
			catch (Exception e) {
			}
		}
		ENCODERS = new JimiEncoderFactory[encoders.size()];
		encoders.copyInto(ENCODERS);
	}

	public String getVendor()
	{
		return VENDOR;
	}
	public String getDescription()
	{
		return DESCRIPTION;
	}
	public String getVersionString()
	{
		return VERSION;
	}
	public JimiDecoderFactory[] getDecoders()
	{
		return DECODERS;
	}
	public JimiEncoderFactory[] getEncoders()
	{
		return ENCODERS;
	}

}

