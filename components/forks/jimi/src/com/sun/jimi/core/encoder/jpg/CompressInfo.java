package com.sun.jimi.core.encoder.jpg;

import java.io.*;

import com.sun.jimi.core.compat.*;

public final class CompressInfo
{
  final short NUM_QUANT_TBLS =    4;	// quantization tables are numbered 0..3
  final short NUM_HUFF_TBLS =     4;	// Huffman tables are numbered 0..3
  final short NUM_ARITH_TBLS =    16;	// arith-coding tables are numbered 0..15 
  final short MAX_COMPS_IN_SCAN = 4;	// JPEG limit on # of components in one scan 
  final short MAX_SAMP_FACTOR =   4;	// JPEG limit on sampling factors 
  final short MAX_BLOCKS_IN_MCU = 10;	// JPEG limit on # of blocks in an MCU 
  final short DCTSIZE						= 8;	// dct size	


	public AdaptiveRasterImage ji;				// Image data source
	public DataOutputStream output_file;	// tells output routines where to write JPEG 

	public int image_width;	// input image width 
	public int image_height;	// input image height 
	public short input_components;	// # of color components in input image 

	public short data_precision = 8;	// bits of precision in image data 

	public short in_color_space; // colorspace of input file 
	public short jpeg_color_space; // colorspace of JPEG file 

	public short num_components;	// # of color components in JPEG image 
	public JpegComponentInfo   comp_info[];
	// comp_info[i] describes component that appears i'th in SOF 

	public short quant_tbl[][];
	// coefficient quantization tables, or NULL if not defined 

	public HuffTbl   dc_huff_tbl[];
	public HuffTbl   ac_huff_tbl[];
	// Huffman coding tables, or NULL if not defined 

	public boolean interleave;	// TRUE=interleaved output, FALSE=not 

	public short max_h_samp_factor; // largest h_samp_factor 
	public short max_v_samp_factor; // largest v_samp_factor 


	/*
	 * These fields are valid during any one scan
	 */
	public short comps_in_scan;	// # of JPEG components output this time 
	public JpegComponentInfo   cur_comp_info[] =
	        new JpegComponentInfo[MAX_COMPS_IN_SCAN];
	// cur_comp_info[i] describes component that appears i'th in SOS 

	public int MCUs_per_row;	// # of MCUs across the image 
	public int MCU_rows_in_scan;	// # of MCU rows in the image 

	public short blocks_in_MCU;	// # of DCT blocks per MCU 
	public short MCU_membership[] = new short[MAX_BLOCKS_IN_MCU];
	// MCU_membership[i] is index in cur_comp_info of component owning 
	// i'th block in an MCU 

	// these fields are private data for the entropy encoder 
	public int last_dc_val[] = new int[MAX_COMPS_IN_SCAN]; // last DC coef for each comp 
	
	/**
	 * @param quality the quality control setting for saving of jpg. This parametr
	 * ranges from 0 to poor to 100 for very good quality.
	 **/
	public CompressInfo(int quality)
	{
		Tables dctTables = new Tables(quality);

		int	len,i;
		
		quant_tbl = new short[NUM_QUANT_TBLS][DCTSIZE*DCTSIZE];
		dctTables.getLuminanceQuantTable(quant_tbl[0]);

		dctTables.getChrominanceQuantTable(quant_tbl[1]);
		
		quant_tbl[2] = null;
		quant_tbl[3] = null;
		
		// coefficient quantization tables, or NULL if not defined 

		dc_huff_tbl = new HuffTbl[NUM_HUFF_TBLS];
		dc_huff_tbl[0] = new HuffTbl(dctTables.dc_luminance_bits,dctTables.dc_luminance_val);
		dc_huff_tbl[1] = new HuffTbl(dctTables.dc_chrominance_bits,dctTables.dc_chrominance_val);
		dc_huff_tbl[2] = null;
		dc_huff_tbl[3] = null;
		
		ac_huff_tbl = new HuffTbl[NUM_HUFF_TBLS];
		ac_huff_tbl[0] = new HuffTbl(dctTables.ac_luminance_bits,dctTables.ac_luminance_val);
		ac_huff_tbl[1] = new HuffTbl(dctTables.ac_chrominance_bits,dctTables.ac_chrominance_val);
		ac_huff_tbl[2] = null;
		ac_huff_tbl[3] = null;
		// Huffman coding tables, or NULL if not defined 
	}
}
