package com.sun.jimi.core.encoder.jpg;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;

/*
 * The conversion equations implemented are 
 *	Y  =  0.29900 * R + 0.58700 * G + 0.11400 * B
 *	Cb = -0.16874 * R - 0.33126 * G + 0.50000 * B  + MAXJSAMPLE/2
 *	Cr =  0.50000 * R - 0.41869 * G - 0.08131 * B  + MAXJSAMPLE/2
 */


public class ConvertColor
{

	protected Shared shared;
	public ConvertColor(Shared shared)
	{
		this.shared = shared;
	}

	 short pixel_row[][];

	/** buffer to retieve JimiImage pixel data **/
	 int[]  jiIntBuf_;
	 byte[]  jiBufByt;
	
  // defines color spaces 
	static final short CS_UNKNOWN=0;	// error/unspecified 
	static final short CS_GRAYSCALE=1;// monochrome (only 1 component)
	static final short CS_RGB=2;		// red/green/blue 
	static final short CS_YCbCr=3;	// Y/Cb/Cr (also known as YUV) 

	static final short SCALEBITS=16;
	static final int ONE_HALF=(1<<(SCALEBITS-1));

	static final short MAXJSAMPLE = 256;
	 int rgb_ycc_tab[];	// => table for RGB to YCbCr conversion 
	static final int R_Y_OFF=0;			// offset to R => Y section 
	static final int G_Y_OFF=(1*MAXJSAMPLE);	// offset to G => Y section 
	static final int B_Y_OFF=(2*MAXJSAMPLE);	// etc. 
	static final int R_CB_OFF=(3*MAXJSAMPLE);
	static final int G_CB_OFF=(4*MAXJSAMPLE);
	static final int B_CB_OFF=(5*MAXJSAMPLE);
	static final int R_CR_OFF=B_CB_OFF;		// B=>Cb, R=>Cr are the same 
	static final int G_CR_OFF=(6*MAXJSAMPLE);
	static final int B_CR_OFF=(7*MAXJSAMPLE);
	static final int TABLE_SIZE=(8*MAXJSAMPLE);

	/*
	 * Left shift to use int representation instead of float
	 */
	public static int	
	scaleToInt(double f)
	{
		return (int)(f * ((int)(1<<SCALEBITS) + 0.5));
	}

	
	/*
	 * Initialize for colorspace conversion.
	 */
	public  void rgb_ycc_init (CompressInfo cinfo)
	{
		int i;

		// Allocate a workspace for the result of get_input_row. 
		pixel_row = new short[cinfo.input_components][cinfo.image_width];

		// Allocate and fill in the conversion tables. 
		rgb_ycc_tab = new int[TABLE_SIZE];

		// original. using multiply for each array being initialised
		for (i = 0; i < MAXJSAMPLE; i++)
		{
		    rgb_ycc_tab[i+R_Y_OFF] = scaleToInt(0.29900) * i;
		    rgb_ycc_tab[i+G_Y_OFF] = scaleToInt(0.58700) * i;
		    rgb_ycc_tab[i+B_Y_OFF] = scaleToInt(0.11400) * i     + ONE_HALF;
		    rgb_ycc_tab[i+R_CB_OFF] = (-scaleToInt(0.16874)) * i;
		    rgb_ycc_tab[i+G_CB_OFF] = (-scaleToInt(0.33126)) * i;
		    rgb_ycc_tab[i+B_CB_OFF] = scaleToInt(0.50000) * i    + ONE_HALF*MAXJSAMPLE;
		//  B=>Cb and R=>Cr tables are the same
		//  rgb_ycc_tab[i+R_CR_OFF] = FIX(0.50000) * i    + ONE_HALF*MAXJSAMPLE;
		//
		    rgb_ycc_tab[i+G_CR_OFF] = (-scaleToInt(0.41869)) * i;
		    rgb_ycc_tab[i+B_CR_OFF] = (-scaleToInt(0.08131)) * i;
		}

        row_idx_ = 0;    // first row
		jiIntBuf_ = new int[cinfo.image_width];
	}


    /**
     * track which row of data they are returning next
     */
     int row_idx_; // initialised in rgb_ycc_init()
    
	/**
	 * Patch method to make Jpg saver work with JimiImage
	 * This can be more efficient once JimiImage is more integrated
	 * into the JPG saver.
	 */
	 /*	kept just in case get_noconvert_rows() is ever used in future.
		but this is not really worth keeping.

	static void Patching_get_rgb_row(CompressInfo cinfo, short[][] pixel_row) throws JimiException
	{
		int col;

		if (jiIntBuf_ == null)
			jiIntBuf_ = new int[cinfo.image_width];

		cinfo.ji.getChannel(row_idx_, jiIntBuf_, 0);
        ++row_idx_;

		for (col = 0; col < cinfo.image_width; col++)
		{
			pixel_row[0][col] = (short)((jiIntBuf_[col] & 0xFF0000) >> 16);// R
			pixel_row[1][col] = (short)((jiIntBuf_[col] & 0x00FF00) >>  8);// G
			pixel_row[2][col] = (short)((jiIntBuf_[col] & 0x0000FF)      );// B
		}
	} */

	/** another patch method */
	 void Patching_get_gray_row(CompressInfo cinfo, short[] pixel_row) throws JimiException
	{
		int col;

		if (jiBufByt == null)
			jiBufByt = new byte[cinfo.image_width];

		cinfo.ji.getChannel(0, row_idx_, jiBufByt, 0);
        ++row_idx_;

		for (col = 0; col < cinfo.image_width; col++)
		{
			pixel_row[col] = jiBufByt[col];
		}
	}

	public  void get_rgb_ycc_rows (CompressInfo cinfo, 
				int rows_to_read, short[][][] image_data) throws JimiException
	{
		int r, g, b;
		int col;
		int row;
		int rowSource;
		AdaptiveRasterImage ji = cinfo.ji;
		int rowEnd = row_idx_ + rows_to_read;
		int width = cinfo.image_width;
		int val;
		int[] buf = jiIntBuf_;
		int i;

		// local variable copy of reference
		int[] rgb_ycc_tabL = rgb_ycc_tab;	// small gain :)

		rowSource = row_idx_;
		for (row = 0; row < rows_to_read; ++row)
		{
			ji.getChannel(rowSource, buf, 0);

		    for (col = 0; col < width; col++)
			{
				val = buf[col];
				r = (val >> 16) & 0xFF;
				g = (val >> 8) & 0xFF;
				b = val & 0xFF;

				// If the inputs are 0..MAXJSAMPLE, the outputs of these equations
				// must be too; we do not need an explicit range-limiting operation.
				// Hence the value being shifted is never negative, and we don't
				// need the general RIGHT_SHIFT macro.

				// Y 
				image_data[0][row][col] = (short)
						((rgb_ycc_tabL[r+R_Y_OFF] +
						  rgb_ycc_tabL[g+G_Y_OFF] +
						  rgb_ycc_tabL[b+B_Y_OFF]) >> SCALEBITS);
				// Cb 
				image_data[1][row][col] = (short)
						((rgb_ycc_tabL[r+R_CB_OFF] + 
						  rgb_ycc_tabL[g+G_CB_OFF] +
						  rgb_ycc_tabL[b+B_CB_OFF]) >> SCALEBITS);
				// Cr 
				image_data[2][row][col] = (short)
						((rgb_ycc_tabL[r+R_CR_OFF] +
						  rgb_ycc_tabL[g+G_CR_OFF] +
						  rgb_ycc_tabL[b+B_CR_OFF]) >> SCALEBITS);
			}
			++rowSource;
		}

		row_idx_ += rows_to_read;
	}




	/*
	 * Fetch some rows of pixels and convert to the
	 * JPEG colorspace.
	 * This version handles grayscale output with no conversion.
	 */
	public  void
	get_grayscale_rows (CompressInfo cinfo, int rows_to_read, short [][][] image_data)
	 throws JimiException
	{
	  int row,col;

	  for (row = 0; row < rows_to_read; row++)
	  {
	    // Read one row from the source file 
		Patching_get_gray_row(cinfo, pixel_row[0]);

	    // Convert colorspace  
	    for (col = 0; col < cinfo.image_width; col++)
	    	image_data[0][row][col] = pixel_row[0][col];
	  }
	}

}
