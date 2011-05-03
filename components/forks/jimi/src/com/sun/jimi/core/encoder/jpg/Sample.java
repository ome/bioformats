package com.sun.jimi.core.encoder.jpg;

public class Sample{

	/*
	 * Downsample pixel values of a single component.
	 * This method handles the common case of 2:1 horizontal and 1:1 vertical.
	 */

	public static void
	h2v1_downsample (int output_cols, int output_rows,
			 short [][] input_data, short [][] output_data)
	{
	  int outrow;
	  int outcol,incol;

	  for (outrow = 0; outrow < output_rows; outrow++) {
	    for (outcol = 0, incol=0; outcol < output_cols; outcol++, incol+=2) {
	      output_data[outrow][outcol] = (short)
	      	((input_data[outrow][incol] + input_data[outrow][incol+1] + 1)/2);
	    }
	  }
	}


	/*
	 * Downsample pixel values of a single component.
	 * This version handles the standard case of 2:1 horizontal and 2:1 vertical
	 */

	public static void
	h2v2_downsample (int output_cols, int output_rows,
			 short [][] input_data, short [][] output_data)
	{
	  int inrow, outrow;
	  int outcol, incol;

	  inrow = 0;
	  for (outrow = 0; outrow < output_rows; outrow++) {
	    for (outcol = 0, incol = 0; outcol < output_cols; outcol++, incol+=2) {
	    	output_data[outrow][outcol] = (short)((input_data[inrow][incol] + 
	    				input_data[inrow][incol+1] + input_data[inrow+1][incol] + 
	    				input_data[inrow+1][incol+1] + 2) / 4);
	    }
	    inrow += 2;
	  }
	}


	/*
	 * Downsample pixel values of a single component.
	 * This version handles the special case of a full-size component,
	 */

	public static void
	fullsize_downsample (int output_cols, int output_rows,
			     short [][] input_data, short [][] output_data)
	{
		int outcol, outrow;
		
		for (outrow = 0; outrow < output_rows; outrow++)
			for (outcol = 0; outcol < output_cols; outcol++)
				output_data[outrow][outcol] = input_data[outrow][outcol];
	}

	public static void
	downsample(CompressInfo cinfo, int ci, int	output_cols, int output_rows,
	                short [][] input_data, short [][] output_data)
	{
		int	v, h;

		v = cinfo.max_v_samp_factor /
			cinfo.comp_info[ci].v_samp_factor;
		h = cinfo.max_h_samp_factor /
			cinfo.comp_info[ci].h_samp_factor;

		if (v == 1 && h == 1){	// full size sample
			fullsize_downsample(output_cols, output_rows, input_data, output_data);
		} else if (h == 2 && v == 2){		// 4:2:0
			h2v2_downsample(output_cols, output_rows, input_data, output_data);
		}else if (h == 2 && v == 1){		// 4:2:2
			h2v1_downsample(output_cols, output_rows, input_data, output_data);
		}else
			util.errexit("Down sample ratio not supported.");
	}
		
	// expand at the right and bottom when the width and height is not an
	// integeral multiple of MCU block width/height
	public static void
	edge_expand (CompressInfo cinfo, int input_cols, int input_rows,
		     int output_cols, int output_rows,
		     short [][][] image_data)
	{
	  // Expand horizontally 
	  if (input_cols < output_cols) {
	    short pixval;
	    int count;
	    int row;
	    short ci;
	    int numcols = output_cols - input_cols;

	    for (ci = 0; ci < cinfo.num_components; ci++) {
	      for (row = 0; row < input_rows; row++) {
					pixval = image_data[ci][row][input_cols-1];
					for (count = numcols; count > 0; count--)
					  image_data[ci][row][output_cols-count] = pixval;
	      }
	    }
	  }

	  // Expand vertically 
	  // This happens only once at the bottom of the image.
	  if (input_rows < output_rows) {
	    int row, col;
	    short ci;

	    for (ci = 0; ci < cinfo.num_components; ci++) {
	      for (row = input_rows; row < output_rows; row++) {
	      	for (col = 0; col < output_cols; col++) {
	      		image_data[ci][row][col] = image_data[ci][input_rows-1][col];
	      	}
	      }
	    }
	  }
	}
}
