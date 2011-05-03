/*
 * Copyright (c) 1997 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.decoder.sunraster;

import java.io.IOException;
import java.io.DataInputStream;

import com.sun.jimi.core.JimiException;

class SunRasterColorMap {

	int MapLength;
	int tableLength;
	byte[] r;
	byte[] g;
	byte[] b;
	byte[] raw;
	boolean RGBType;

	SunRasterColorMap(DataInputStream lin, SunRasterHeader header) throws IOException, JimiException {

		MapLength = header.ColorMapLength;

		if(header.ColorMapType == header.RAW_COLOR_MAP) {

			RGBType = false;
			tableLength = MapLength;
			raw = new byte[tableLength];
			lin.readFully(raw, 0, tableLength);

		} else {

			RGBType = true;

			tableLength = MapLength / 3;

			r = new byte[tableLength];
			g = new byte[tableLength];
			b = new byte[tableLength];
		
			lin.readFully(r, 0, tableLength);
			lin.readFully(g, 0, tableLength);
			lin.readFully(b, 0, tableLength);

		}
		
	}
}
