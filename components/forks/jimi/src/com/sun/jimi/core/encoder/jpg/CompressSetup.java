package com.sun.jimi.core.encoder.jpg;

public class CompressSetup {
	
	public static final short MAX_COMPS_IN_SCAN=4;
	public static final short DCTSIZE=8;
	public static final short MAX_BLOCKS_IN_MCU=10;
	public static final short BITS_IN_SAMPLE=8;
	public static final	short	MAX_SAMP_FACTOR=4;
  // defines color spaces 
	public static final byte CS_UNKNOWN=0;	// error/unspecified 
	public static final byte CS_GRAYSCALE=1;// monochrome (only 1 component)
	public static final byte CS_RGB=2;		// red/green/blue 
	public static final byte CS_YCbCr=3;	// Y/Cb/Cr (also known as YUV) 



	/*
	 * Utility routines: common code for pipeline controllers
	 */

	static void
	interleaved_scan_setup (CompressInfo cinfo)
	// Compute all derived info for an interleaved (multi-component) scan 
	{
	  short ci, mcublks;
	  JpegComponentInfo comp;

	  if (cinfo.comps_in_scan > MAX_COMPS_IN_SCAN)
	    util.errexit("Too many components for interleaved scan");

	  cinfo.MCUs_per_row = (cinfo.image_width
				 + cinfo.max_h_samp_factor*DCTSIZE - 1)
				/ (cinfo.max_h_samp_factor*DCTSIZE);

	  cinfo.MCU_rows_in_scan = (cinfo.image_height
				     + cinfo.max_v_samp_factor*DCTSIZE - 1)
				    / (cinfo.max_v_samp_factor*DCTSIZE);
	  
	  cinfo.blocks_in_MCU = 0;

	  for (ci = 0; ci < cinfo.comps_in_scan; ci++) {
	    comp = cinfo.cur_comp_info[ci];
	    // for interleaved scan, sampling factors give # of blocks per component 
	    comp.MCU_width = comp.h_samp_factor;
	    comp.MCU_height = comp.v_samp_factor;
	    comp.MCU_blocks = (short) (comp.MCU_width * comp.MCU_height);
	    // compute physical dimensions of component 
	    comp.downsampled_width = util.roundUp(comp.true_comp_width,
						   (int) (comp.MCU_width*DCTSIZE));
	    comp.downsampled_height = util.roundUp(comp.true_comp_height,
						    (int) (comp.MCU_height*DCTSIZE));
	    // Prepare array describing MCU composition 
	    mcublks = comp.MCU_blocks;
	    if (cinfo.blocks_in_MCU + mcublks > MAX_BLOCKS_IN_MCU)
	      util.errexit("Sampling factors too large for interleaved scan");
	    while (mcublks-- > 0) {
	      cinfo.MCU_membership[cinfo.blocks_in_MCU++] = ci;
	    }
	  }

	}


	static void
	noninterleaved_scan_setup (CompressInfo cinfo)
	// Compute all derived info for a noninterleaved (single-component) scan 
	// On entry, cinfo->comps_in_scan = 1 and cinfo->cur_comp_info[0] is set up 
	{
	  JpegComponentInfo comp = cinfo.cur_comp_info[0];

	  // for noninterleaved scan, always one block per MCU 
	  comp.MCU_width = 1;
	  comp.MCU_height = 1;
	  comp.MCU_blocks = 1;
	  // compute physical dimensions of component 
	  comp.downsampled_width = util.roundUp(comp.true_comp_width,
						 (int) DCTSIZE);
	  comp.downsampled_height = util.roundUp(comp.true_comp_height,
						  (int) DCTSIZE);

	  cinfo.MCUs_per_row = comp.downsampled_width / DCTSIZE;
	  cinfo.MCU_rows_in_scan = comp.downsampled_height / DCTSIZE;

	  // Prepare array describing MCU composition 
	  cinfo.blocks_in_MCU = 1;
	  cinfo.MCU_membership[0] = 0;

	}


	// Default parameter setup for compression.
	public static void
	setCDefaults (CompressInfo cinfo)
	{
	  short i;
	  JpegComponentInfo comp;

	  // Initialize pointers as needed to mark stuff unallocated. 
	  cinfo.comp_info = null;

	  cinfo.data_precision = BITS_IN_SAMPLE; // default; can be overridden by input_init 

	  // Prepare three color components; first is luminance which is also usable 
	  // for grayscale.  The others are assumed to be UV or similar chrominance. 
	  cinfo.jpeg_color_space = CS_YCbCr;
	  cinfo.num_components = 3;
	  cinfo.comps_in_scan = 3;
	  cinfo.comp_info = new JpegComponentInfo[3];

	  cinfo.comp_info[0] = new JpegComponentInfo();
	  cinfo.comp_info[0].component_index = 0;
	  cinfo.comp_info[0].component_id = 1;	
	  cinfo.comp_info[0].h_samp_factor = 2;	// default to 2x2 subsamples of chrominance 
	  cinfo.comp_info[0].v_samp_factor = 2;
	  cinfo.comp_info[0].quant_tbl_no = 0;	// use tables 0 for luminance 
	  cinfo.comp_info[0].dc_tbl_no = 0;
	  cinfo.comp_info[0].ac_tbl_no = 0;

	  cinfo.comp_info[1] = new JpegComponentInfo();
	  cinfo.comp_info[1].component_index = 1;
	  cinfo.comp_info[1].component_id = 2;
	  cinfo.comp_info[1].h_samp_factor = 1;
	  cinfo.comp_info[1].v_samp_factor = 1;
	  cinfo.comp_info[1].quant_tbl_no = 1;	// use tables 1 for chrominance 
	  cinfo.comp_info[1].dc_tbl_no = 1;
	  cinfo.comp_info[1].ac_tbl_no = 1;

	  cinfo.comp_info[2] = new JpegComponentInfo();
	  cinfo.comp_info[2].component_index = 2;
	  cinfo.comp_info[2].component_id = 3;
	  cinfo.comp_info[2].h_samp_factor = 1;
	  cinfo.comp_info[2].v_samp_factor = 1;
	  cinfo.comp_info[2].quant_tbl_no = 1;	// use tables 1 for chrominance 
	  cinfo.comp_info[2].dc_tbl_no = 1;
	  cinfo.comp_info[2].ac_tbl_no = 1;

		cinfo.cur_comp_info = new JpegComponentInfo[MAX_COMPS_IN_SCAN];
		cinfo.cur_comp_info[0] = cinfo.comp_info[0];
		cinfo.cur_comp_info[1] = cinfo.comp_info[1];
		cinfo.cur_comp_info[2] = cinfo.comp_info[2];
		
	  cinfo.interleave = true;
	}



	public static void
	setMDefaults (CompressInfo cinfo)
	// write a monochrome JPEG file. 
	{
	  cinfo.jpeg_color_space = CS_GRAYSCALE;
	  cinfo.num_components = 1;
	  // Set single component to 1x1 subsampling 
	  cinfo.comp_info[0].h_samp_factor = 1;
	  cinfo.comp_info[0].v_samp_factor = 1;
	 	cinfo.comps_in_scan = 1;
	  cinfo.interleave = false;
	}


	public static void
	initial_setup (CompressInfo cinfo)
	// Do computations that are needed before initial method selection 
	{
	  short ci;
	  JpegComponentInfo comp;

	  // Compute maximum sampling factors; check factor validity 
	  cinfo.max_h_samp_factor = 1;
	  cinfo.max_v_samp_factor = 1;
	  for (ci = 0; ci < cinfo.num_components; ci++) {
	    comp = cinfo.comp_info[ci];
	    if (comp.h_samp_factor<=0 || comp.h_samp_factor>MAX_SAMP_FACTOR ||
				comp.v_samp_factor<=0 || comp.v_samp_factor>MAX_SAMP_FACTOR)
	      util.errexit("Bogus sampling factors");
	    cinfo.max_h_samp_factor = (short)Math.max(cinfo.max_h_samp_factor,
					   comp.h_samp_factor);
	    cinfo.max_v_samp_factor = (short)Math.max(cinfo.max_v_samp_factor,
					   comp.v_samp_factor);

	  }

	  // Compute logical downsampled dimensions of components 
	  for (ci = 0; ci < cinfo.num_components; ci++) {
	    comp = cinfo.comp_info[ci];
	    comp.true_comp_width = (cinfo.image_width * comp.h_samp_factor
					+ cinfo.max_h_samp_factor - 1)
					/ cinfo.max_h_samp_factor;
	    comp.true_comp_height = (cinfo.image_height * comp.v_samp_factor
					 + cinfo.max_v_samp_factor - 1)
					 / cinfo.max_v_samp_factor;
	  }
	}
}
