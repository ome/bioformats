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
package com.sun.jimi.core.util;

import java.util.Random;

/**
 * Floyd-Steinberg Dither class
 * This is based on the netpbm utility ppmquant.c
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.2 $
 **/
public class FSDither
{
	/**
	 * FS errors are stored with this fixed bit depth offset to handle 
	 * fractional errors.
	 **/
	static final int FS_SCALE = 1024;

	/**
	 * when reference for column add 1 to reference as this buffer is offset
	 * from actual column position by one to allow FS to not check left/right 
	 * edge conditions.
	 **/
	int[] thisRerr_;
	int[] nextRerr_;
	int[] thisGerr_;
	int[] nextGerr_;
	int[] thisBerr_;
	int[] nextBerr_;

	/** we traverse forward and backward over succesive rows **/
	boolean forward_;	// true if forward left to right error propogation

	/** max value of color channel in 8 bit RGB color model **/
	static final int MAXVAL = 255;

	/** packed RGB color map **/
	byte[] rgbCMap_;

	int numColors_;

	int width_;

	InverseColorMap invCM_;

	/**
	 * @param rgbCMap color map to reduce colors to
	 * @param width the width of the image being processed
	 *
	 * Experiment useing coloroctree to lookup colors ?
	 **/
	FSDither(byte[] rgbCMap, int numColors, int width)
	{
		rgbCMap_ = rgbCMap;
		numColors_ = numColors;
		width_ = width;
		invCM_ = new InverseColorMap(rgbCMap_);
		init();
	}

	void init()
	{
		// Initialize Floyd-Steinberg error vectors.
		// +2 to handle the previous pixel and next pixel case minimally
		thisRerr_ = new int[width_ + 2];
		nextRerr_ = new int[width_ + 2];
		thisGerr_ = new int[width_ + 2];
		nextGerr_ = new int[width_ + 2];
		thisBerr_ = new int[width_ + 2];
		nextBerr_ = new int[width_ + 2];
		Random r = new Random();

	    // random errors in [-1 .. 1] - for first row
		for (int col = 0; col < width_ + 2; ++col )
		{
			thisRerr_[col] = (Math.abs(r.nextInt()) % (FS_SCALE * 2)) - FS_SCALE;
			thisGerr_[col] = (Math.abs(r.nextInt()) % (FS_SCALE * 2)) - FS_SCALE;
			thisBerr_[col] = (Math.abs(r.nextInt()) % (FS_SCALE * 2)) - FS_SCALE;
		}
		forward_ = true;
	}

	/**
	 * This must be called with each row of pixels in succession.
	 * Reordering and or skipping of rows will cause odd results.
	 **/
	void ditherRow(int[] pixels, byte[] out)
	{
		int col;
		int limitcol;
		int o;				// output index

		// clear out next error rows for colour errors
		for (col = nextRerr_.length; --col >= 0; )
			nextRerr_[col] = 0;
		System.arraycopy(nextRerr_, 0, nextGerr_, 0, nextRerr_.length);
		System.arraycopy(nextRerr_, 0, nextBerr_, 0, nextRerr_.length);

		// init index and limit
		if (forward_)
		{
			o = 0;
		    col = 0;
		    limitcol = width_;
		}
		else
		{
			o = width_ - 1;
		    col = width_ - 1;
		    limitcol = -1;
		}

		int idx;
		int p;
		int sr, sr2;
		int sg, sg2;
		int sb, sb2;
		long err;

		while (true)
		{
			p = pixels[col];
			if ((p & 0xff000000) == 0) {
				if (forward_) {
					out[o++] = (byte)numColors_;
					col++;
					if (col >= limitcol) break;
				}
				else {
					out[o--] = (byte)numColors_;
					col--;
					if (col <= limitcol) break;
				}
				continue;
			}
			sr = (int) (((p & 0x00FF0000) >> 16));
			sg = (int) (((p & 0x0000FF00) >> 8) );
			sb = (int) ( (p & 0x000000FF)       );
			sr = (int) (((p & 0x00FF0000) >> 16) + (thisRerr_[col + 1] / FS_SCALE));
			sg = (int) (((p & 0x0000FF00) >> 8)  + (thisGerr_[col + 1] / FS_SCALE));
			sb = (int) ( (p & 0x000000FF)        + (thisBerr_[col + 1] / FS_SCALE));
			
			// clamp red/green/blue to min & max
			if (sr < 0)
				sr = 0;
			else if (sr > MAXVAL)
				sr = MAXVAL;
			if (sg < 0)
				sg = 0;
			else if (sg > MAXVAL)
				sg = MAXVAL;
			if (sb < 0)
				sb = 0;
			else if (sb > MAXVAL)
				sb = MAXVAL;
			
			// find color in color map that is closest to  sr/sg/sb color
			//			idx = findIdx(sr, sg, sb);
			idx = invCM_.getIndexNearest(sr, sg, sb);
			
			// get the color that will represent current color
			sr2 = rgbCMap_[idx*4] & 0xFF;
			sg2 = rgbCMap_[idx*4+1] & 0xFF;
			sb2 = rgbCMap_[idx*4+2] & 0xFF;
			
			// update Floyd-Steinberg errors
			if (forward_)
			{
				out[o++] = (byte)idx;				// output color index to buffer
				err = (long)(sr - sr2) * FS_SCALE;
			    thisRerr_[col + 2] += ( err * 7 ) / 16;
			    nextRerr_[col    ] += ( err * 3 ) / 16;
			    nextRerr_[col + 1] += ( err * 5 ) / 16;
			    nextRerr_[col + 2] += ( err     ) / 16;
				err = (long)(sg - sg2) * FS_SCALE;
			    thisGerr_[col + 2] += ( err * 7 ) / 16;
			    nextGerr_[col    ] += ( err * 3 ) / 16;
			    nextGerr_[col + 1] += ( err * 5 ) / 16;
			    nextGerr_[col + 2] += ( err     ) / 16;
				err = (long)(sb - sb2) * FS_SCALE;
			    thisBerr_[col + 2] += ( err * 7 ) / 16;
			    nextBerr_[col    ] += ( err * 3 ) / 16;
			    nextBerr_[col + 1] += ( err * 5 ) / 16;
    		    nextBerr_[col + 2] += ( err     ) / 16;
				++col;
				if (col >= limitcol)
					break;					// done row
			}
			else
			{
				out[o--] = (byte)idx;				// output color index to buffer
				err = (long)(sr - sr2) * FS_SCALE;
			    thisRerr_[col    ] += ( err * 7 ) / 16;
			    nextRerr_[col + 2] += ( err * 3 ) / 16;
			    nextRerr_[col + 1] += ( err * 5 ) / 16;
			    nextRerr_[col    ] += ( err     ) / 16;
				err = (long)(sg - sg2) * FS_SCALE;
			    thisGerr_[col    ] += ( err * 7 ) / 16;
			    nextGerr_[col + 2] += ( err * 3 ) / 16;
			    nextGerr_[col + 1] += ( err * 5 ) / 16;
			    nextGerr_[col    ] += ( err     ) / 16;
				err = (long)(sb - sb2) * FS_SCALE;
			    thisBerr_[col    ] += ( err * 7 ) / 16;
			    nextBerr_[col + 2] += ( err * 3 ) / 16;
			    nextBerr_[col + 1] += ( err * 5 ) / 16;
			    nextBerr_[col    ] += ( err     ) / 16;
				--col;
				if (col <= limitcol)
					break;					// done row
			}
		}

		// make next error info current for next call to this method
		int[] temperr;
	    temperr = thisRerr_;
	    thisRerr_ = nextRerr_;
	    nextRerr_ = temperr;
	    temperr = thisGerr_;
	    thisGerr_ = nextGerr_;
	    nextGerr_ = temperr;
	    temperr = thisBerr_;
	    thisBerr_ = nextBerr_;
	    nextBerr_ = temperr;
	    forward_ = !forward_;		// disable different direction scanning
	}

	// brute force finding of closest color
	int findIdx(int sr, int sg, int sb) throws ArrayIndexOutOfBoundsException
	{
		// add some sort of cache / hash system to
		// speed things up ?

	    // search colormap for closest match
		int i, r2, g2, b2;
		int idx = 0;
		long dist, newdist;
		dist = 2000000000;
		for (i = numColors_; --i >= 0; )
		{
			r2 = rgbCMap_[i*4] & 0xFF;
			g2 = rgbCMap_[i*4+1] & 0xFF;
			b2 = rgbCMap_[i*4+2] & 0xFF;

		    newdist = (sr - r2) * (sr - r2) +
	 				  (sg - g2) * (sg - g2) +
					  (sb - b2) * (sb - b2);
		    if (newdist < dist)
		    {
				idx = i;
				dist = newdist;
		    }
		}
		return idx;
	}

}


