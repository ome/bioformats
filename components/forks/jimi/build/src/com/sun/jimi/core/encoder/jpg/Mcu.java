package com.sun.jimi.core.encoder.jpg;

public class Mcu
{

	Shared shared;
	public Mcu(Shared shared)
	{
		this.shared = shared;
	}
	
	static final short	DCTSIZE=8;
	static final short	CENTERJSAMPLE=128;
	static final short  MAX_BLOCKS_IN_MCU=10;
		
	// ZZ[i] is the natural-order position of the i'th element of zigzag order. 

	static final short ZZ[] =
	{
		 0,  1,  8, 16,  9,  2,  3, 10,
		17, 24, 32, 25, 18, 11,  4,  5,
		12, 19, 26, 33, 40, 48, 41, 34,
		27, 20, 13,  6,  7, 14, 21, 28,
		35, 42, 49, 56, 57, 50, 43, 36,
		29, 22, 15, 23, 30, 37, 44, 51,
		58, 59, 52, 45, 38, 31, 39, 46,
		53, 60, 61, 54, 47, 55, 62, 63
	};


	int[] block_ = new int [DCTSIZE*DCTSIZE];

	/**
	 * Extract one 8x8 block from the specified location in the sample array; 
	 * perform forward DCT, quantization scaling, and zigzag reordering on it. 
	 **/
	void	extract_block (short[][] input_data, int start_row, 
						int start_col, int[] output_data, short [] quanttbl)
	{
	  	int temp;
		int [] block = block_;		// local handle to memory
	   	int elemr, pos, col;
		int i;
		short[] input_data_row;		// avoid an array dereference in the loop

		pos = 0;
	    for (elemr = DCTSIZE; --elemr >= 0; )
	    {
			// unroll the inner loop 
			col = start_col;
			input_data_row = input_data[start_row++];
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
			block[pos++] = input_data_row[col++] - CENTERJSAMPLE;
	    }

	  	Fwddct.fwd_dct(block);

		for (i = DCTSIZE*DCTSIZE; --i >= 0; )
		{
		    temp = block[ZZ[i]];
		    // divide by *quanttbl, ensuring proper rounding 
		    if (temp < 0)
		    {
				temp = -temp;
				temp += (int)(quanttbl[i]>>1);
				temp /= (int)(quanttbl[i]);
				temp = -temp;
		    }
		    else
		    {
				temp += quanttbl[i]>>1;
				temp /= quanttbl[i];
		    }
		  	output_data[i] = temp;
		}
	}

	int[][] MCU_data_ = new int[MAX_BLOCKS_IN_MCU][DCTSIZE*DCTSIZE];

	/**
	* Extract samples in MCU order, process & call output
	**/
	 void	extract_MCUs (CompressInfo cinfo, short [][][] image_data,
													      int num_mcu_rows)
	{
		int[][] MCU_data = MCU_data_;	// reduce memory allocation frequency
		int mcurow;
		int mcuindex;
		short blkn, ci, xpos, ypos;
		JpegComponentInfo  comp;
		short[] quanttbl;
		short[][] image_data_ci;	// avoid ci array lookup where can.

		for (mcurow = 0; mcurow < num_mcu_rows; mcurow++)
		{
			for (mcuindex = 0; mcuindex < cinfo.MCUs_per_row; mcuindex++)
			{
	    		// Extract data from the image array, DCT it, and quantize it 
    	    	blkn = 0;
    	    	for (ci = 0; ci < cinfo.comps_in_scan; ci++)
    	    	{
					image_data_ci = image_data[ci];
	    			comp = cinfo.cur_comp_info[ci];
	    			quanttbl = cinfo.quant_tbl[comp.quant_tbl_no];
	    			for (ypos = 0; ypos < comp.MCU_height; ypos++)
	    			{
	    				for (xpos = 0; xpos < comp.MCU_width; xpos++)
	    				{
		    				extract_block(image_data_ci,
			    				(mcurow * comp.MCU_height + ypos)*DCTSIZE,
			    				(mcuindex * comp.MCU_width + xpos)*DCTSIZE,
			    							  MCU_data[blkn], quanttbl);
		    				blkn++;
    					}
	    			}
    			}
		    	// Send the MCU whereever the pipeline controller wants it to go 
		    	shared.huffEncode.huff_encode (cinfo, MCU_data);
			}
		}
	}
}
