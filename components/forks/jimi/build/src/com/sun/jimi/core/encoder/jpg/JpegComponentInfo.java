package com.sun.jimi.core.encoder.jpg;

public final class JpegComponentInfo{


  // Basic info about one component 
  // These values are fixed over the whole image 
  // For compression, they must be supplied by the user interface; 
	short component_id;	// identifier for this component (0..255) 
	short component_index;	// its index in SOF or cinfo.comp_info[] 
	short h_samp_factor;	// horizontal sampling factor (1..4) 
	short v_samp_factor;	// vertical sampling factor (1..4) 
	short quant_tbl_no;	// quantization table selector (0..3) 
  // These values may vary between scans 
  // For compression, they must be supplied by the user interface; 
  // for decompression, they are read from the SOS marker. 
	short dc_tbl_no;	// DC entropy table selector (0..3) 
	short ac_tbl_no;	// AC entropy table selector (0..3) 
  // These values are computed during compression or decompression startup 
	int true_comp_width;	// component's image width in samples 
	int true_comp_height;	// component's image height in samples 
	// the above are the logical dimensions of the downsampled image 
  // These values are computed before starting a scan of the component 
	short MCU_width;	// number of blocks per MCU, horizontally 
	short MCU_height;	// number of blocks per MCU, vertically 
	short MCU_blocks;	// MCU_width * MCU_height 
	int downsampled_width;	// image width in samples, after expansion 
	int downsampled_height; // image height in samples, after expansion 
	// the above are the true_comp_xxx values rounded up to multiples of 
	// the MCU dimensions. 
}

