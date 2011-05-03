/*
 * This code has been based on the libpng library as follows.
 * libpng 1.0 beta 6 - version 0.96
 * Copyright (c) 1995, 1996 Guy Eric Schalnat, Group 42, Inc.
 * Copyright (c) 1996, 1997 Andreas Dilger
 * May 12, 1997
 *
 */
package com.sun.jimi.core.encoder.png;

/**
 * Useful constants for encoding and decoding images to and from the 
 * PNG file format.
 *
 * These constants are from the png.c, png.h and pngconf.h files from libpng.
 * Practically all constants are here even if the Java implementation does
 * not yet use all of them.
 *
 * @author	Robin Luiten
 * @version	1.0	19/Nov/1997
 **/
public interface PNGConstants
{
    /* version information */
	public final static String PNG_LIBPNG_VER_STRING = "0.96";
	public final static int PNG_LIBPNG_VER = 96;

    /** place to hold the signature string for a png file. **/
    public final static byte[] png_sig = {(byte)137, 80, 78, 71, 13, 10, 26, 10};

	// Structures to facilitate easy interlacing
	/** start of interlace block **/
    public final static int[] png_pass_start   = {0, 4, 0, 2, 0, 1, 0};
	/** offset to next interlace block **/
    public final static int[] png_pass_inc     = {8, 8, 4, 4, 2, 2, 1};
	/** start of interlace block in the y direction **/
    public final static int[] png_pass_ystart  = {0, 0, 4, 0, 2, 0, 1};
	/** offset to next interlace block in the y direction **/
    public final static int[] png_pass_yinc    = {8, 8, 8, 4, 4, 2, 2};
	/** mask to determine which pixels are valid in a pass **/
    public final static int[] png_pass_mask    = {0x80, 0x08, 0x88, 0x22, 0xaa, 0x55, 0xff};
	/** mask to determine which pixels to overwrite while displaying **/
    public final static int[] png_pass_dsp_mask= {0xff, 0x0f, 0xff, 0x33, 0xff, 0x55, 0xff};

	/**
	 * Supported compression types for text in PNG files (tEXt, and zTXt).
	 * The values of the PNG_TEXT_COMPRESSION_ defines should NOT be changed.
	 **/
	public final static byte PNG_TEXT_COMPRESSION_NONE_WR = -3;
	public final static byte PNG_TEXT_COMPRESSION_zTXt_WR = -2;
	public final static byte PNG_TEXT_COMPRESSION_NONE    = -1;
	public final static byte PNG_TEXT_COMPRESSION_zTXt    = 0;
	public final static byte PNG_TEXT_COMPRESSION_LAST    = 1;  // Not a valid value

	// These describe the color_type field in png_info
    /** color type masks **/
    public final static byte PNG_COLOR_MASK_PALETTE = 1;
    public final static byte PNG_COLOR_MASK_COLOR   = 2;
    public final static byte PNG_COLOR_MASK_ALPHA   = 4;

    /** color types.  Note that not all combinations are legal **/
    public final static byte PNG_COLOR_TYPE_GRAY       = 0;
    public final static byte PNG_COLOR_TYPE_PALETTE    = (PNG_COLOR_MASK_COLOR | PNG_COLOR_MASK_PALETTE);
    public final static byte PNG_COLOR_TYPE_RGB        = PNG_COLOR_MASK_COLOR;
    public final static byte PNG_COLOR_TYPE_RGB_ALPHA  = (PNG_COLOR_MASK_COLOR | PNG_COLOR_MASK_ALPHA);
    public final static byte PNG_COLOR_TYPE_GRAY_ALPHA = PNG_COLOR_MASK_ALPHA;


	// This is for compression type. PNG 1.0 only defines the single type
	/** Deflate method 8, 32K window **/
	public final static byte PNG_COMPRESSION_TYPE_BASE    = 0;
	public final static byte PNG_COMPRESSION_TYPE_DEFAULT = PNG_COMPRESSION_TYPE_BASE;

	// This is for filter type. PNG 1.0 only defines the single type
	/** Single row per-byte filtering **/
	public final static byte PNG_FILTER_TYPE_BASE    = 0;
	public final static byte PNG_FILTER_TYPE_DEFAULT = PNG_FILTER_TYPE_BASE;

	// These are for the interlacing type.  These values should NOT be changed.
	/** Non-interlaced image **/
	public final static byte PNG_INTERLACE_NONE  = 0;
	/** Adam7 interlacing **/
	public final static byte PNG_INTERLACE_ADAM7 = 1;
	/** Not a valid value **/
	public final static byte PNG_INTERLACE_LAST  = 2;

	// These are for the oFFs chunk.  These values should NOT be changed.
	/** Offset in pixels **/
	public final static byte PNG_OFFSET_PIXEL      = 0;
	/** Offset in micrometers (1/10^6 meter) **/ 
	public final static byte PNG_OFFSET_MICROMETER = 1;
	/** Not a valid value **/
	public final static byte PNG_OFFSET_LAST       = 2;

	// These are for the pCAL chunk.  These values should NOT be changed
	/** Linear transformation **/
	public final static byte PNG_EQUATION_LINEAR     = 0;
	/** Exponential base e transform **/
	public final static byte PNG_EQUATION_BASE_E     = 1;
	/** Arbitrary base exponential transform **/
	public final static byte PNG_EQUATION_ARBITRARY  = 2;
	/** Hyperbolic sine transformation **/
	public final static byte PNG_EQUATION_HYPERBOLIC = 3;
	/** Not a valid value **/
	public final static byte PNG_EQUATION_LAST       = 4;

	// These are for the pHYs chunk.  These values should NOT be changed
	/** pixels/unknown unit (aspect ratio) **/
	public final static byte PNG_RESOLUTION_UNKNOWN    = 0;
	/** pixels/meter **/
	public final static byte PNG_RESOLUTION_METER      = 1; 
	/** Not a valid value **/
	public final static byte PNG_RESOLUTION_LAST       = 2;

	/**
	 * These determine if an ancillary chunk's data has been successfully read
	 * from the PNG header, or if the application has filled in the corresponding
	 * data in the info_struct to be written into the output file.  The values
	 * of the PNG_INFO_<chunk> defines should NOT be changed.
	 **/
    public final static int PNG_INFO_gAMA = 0x0001;
    public final static int PNG_INFO_sBIT = 0x0002;
    public final static int PNG_INFO_cHRM = 0x0004;
    public final static int PNG_INFO_PLTE = 0x0008;
    public final static int PNG_INFO_tRNS = 0x0010;
    public final static int PNG_INFO_bKGD = 0x0020;
    public final static int PNG_INFO_hIST = 0x0040;
    public final static int PNG_INFO_pHYs = 0x0080;
    public final static int PNG_INFO_oFFs = 0x0100;
    public final static int PNG_INFO_tIME = 0x0200;
    public final static int PNG_INFO_pCAL = 0x0400;


	/**
	 * Values for png_set_crc_action() to say how to handle CRC errors in
	 * ancillary and critical chunks, and whether to use the data contained
	 * therein.  Note that it is impossible to "discard" data in a critical
	 * chunk.  For versions prior to 0.90, the action was always error/quit,
	 * whereas in version 0.90 and later, the action for CRC errors in ancillary
	 * chunks is warn/discard.  These values should NOT be changed.
	 *
	 *      value                                          action:critical     action:ancillary
	 **/
	public final static int PNG_CRC_DEFAULT       = 0;  /* error/quit          warn/discard data */
	public final static int PNG_CRC_ERROR_QUIT    = 1;  /* error/quit          error/quit        */
	public final static int PNG_CRC_WARN_DISCARD  = 2;  /* (INVALID)           warn/discard data */
	public final static int PNG_CRC_WARN_USE      = 3;  /* warn/use data       warn/use data     */
	public final static int PNG_CRC_QUIET_USE     = 4;  /* quiet/use data      quiet/use data    */
	public final static int PNG_CRC_NO_CHANGE     = 5;  /* use current value   use current value */

	/**
	 * Flags for png_set_filter() to say which filters to use.  The flags
	 * are chosen so that they don't conflict with real filter types
	 * below, in case they are supplied instead of the #defined constants.
	 * These values should NOT be changed.
	 **/
	public final static int PNG_NO_FILTERS     = 0x00;
	public final static int PNG_FILTER_NONE    = 0x08;
	public final static int PNG_FILTER_SUB     = 0x10;
	public final static int PNG_FILTER_UP      = 0x20;
	public final static int PNG_FILTER_AVG     = 0x40;
	public final static int PNG_FILTER_PAETH   = 0x80;
	public final static int PNG_ALL_FILTERS    = PNG_FILTER_NONE | PNG_FILTER_SUB | 
								 PNG_FILTER_UP | PNG_FILTER_AVG | PNG_FILTER_PAETH;

	/**
	 * Filter values (not flags) - used in pngwrite.c, pngwutil.c for now.
	 * These defines should NOT be changed.
	 **/
	public final static byte PNG_FILTER_VALUE_NONE  = 0;
	public final static byte PNG_FILTER_VALUE_SUB   = 1;
	public final static byte PNG_FILTER_VALUE_UP    = 2;
	public final static byte PNG_FILTER_VALUE_AVG   = 3;
	public final static byte PNG_FILTER_VALUE_PAETH = 4;
	public final static byte PNG_FILTER_VALUE_LAST  = 5;

	/**
	 * Heuristic used for row filter selection.  These defines should NOT be
	 * changed.
	 **/
	public final static int PNG_FILTER_HEURISTIC_DEFAULT    = 0;  /* Currently "UNWEIGHTED" */
	public final static int PNG_FILTER_HEURISTIC_UNWEIGHTED = 1;  /* Used by libpng < 0.95 */
	public final static int PNG_FILTER_HEURISTIC_WEIGHTED   = 2;  /* Experimental feature */
	public final static int PNG_FILTER_HEURISTIC_LAST       = 3;  /* Not a valid value */

	/**
	 * Various modes of operation.  Note that after an init, mode is set to
	 *  zero automatically when the structure is created.
	 **/
	public final static int PNG_BEFORE_IHDR       = 0x00;
	public final static int PNG_HAVE_IHDR         = 0x01;
	public final static int PNG_HAVE_PLTE         = 0x02;
	public final static int PNG_HAVE_IDAT         = 0x04;
	public final static int PNG_AFTER_IDAT        = 0x08;
	public final static int PNG_HAVE_IEND         = 0x10;

	/** push model modes **/
	public final static int PNG_READ_SIG_MODE   = 0;
	public final static int PNG_READ_CHUNK_MODE = 1;
	public final static int PNG_READ_IDAT_MODE  = 2;
	public final static int PNG_SKIP_MODE       = 3;
	public final static int PNG_READ_tEXt_MODE  = 4;
	public final static int PNG_READ_zTXt_MODE  = 5;
	public final static int PNG_READ_DONE_MODE  = 6;
	public final static int PNG_ERROR_MODE      = 7;

	/** flags for the transformations the PNG library does on the image data **/
	public final static int PNG_BGR                = 0x0001;
	public final static int PNG_INTERLACE          = 0x0002;
	public final static int PNG_PACK               = 0x0004;
	public final static int PNG_SHIFT              = 0x0008;
	public final static int PNG_SWAP_BYTES         = 0x0010;
	public final static int PNG_INVERT_MONO        = 0x0020;
	public final static int PNG_DITHER             = 0x0040;
	public final static int PNG_BACKGROUND         = 0x0080;
	public final static int PNG_BACKGROUND_EXPAND  = 0x0100;
	public final static int PNG_RGB_TO_GRAY        = 0x0200; /* Not currently implemented */
	public final static int PNG_16_TO_8            = 0x0400;
	public final static int PNG_RGBA               = 0x0800;
	public final static int PNG_EXPAND             = 0x1000;
	public final static int PNG_GAMMA              = 0x2000;
	public final static int PNG_GRAY_TO_RGB        = 0x4000;
	public final static int PNG_FILLER             = 0x8000;
	public final static int PNG_PACKSWAP          = 0x10000;
	public final static int PNG_SWAP_ALPHA        = 0x20000;
	public final static int PNG_STRIP_ALPHA       = 0x40000;

	/** flags for png_create_struct **/
	public final static int PNG_STRUCT_PNG   = 0x0001;
	public final static int PNG_STRUCT_INFO  = 0x0002;

	/** Scaling factor for filter heuristic weighting calculations **/
	public final static int PNG_WEIGHT_SHIFT  = 8;
	public final static int PNG_WEIGHT_FACTOR = (1<<(PNG_WEIGHT_SHIFT));
	public final static int PNG_COST_SHIFT    = 3;
	public final static int PNG_COST_FACTOR   = (1<<(PNG_COST_SHIFT));

    /* flags for the png_ptr->flags rather than declaring a bye for each one */
    public final static int PNG_FLAG_ZLIB_CUSTOM_STRATEGY    = 0x0001;
    public final static int PNG_FLAG_ZLIB_CUSTOM_LEVEL       = 0x0002;
    public final static int PNG_FLAG_ZLIB_CUSTOM_MEM_LEVEL   = 0x0004;
    public final static int PNG_FLAG_ZLIB_CUSTOM_WINDOW_BITS = 0x0008;
    public final static int PNG_FLAG_ZLIB_CUSTOM_METHOD      = 0x0010;
    public final static int PNG_FLAG_ZLIB_FINISHED           = 0x0020;
    public final static int PNG_FLAG_ROW_INIT                = 0x0040;
    public final static int PNG_FLAG_FILLER_AFTER            = 0x0080;
    public final static int PNG_FLAG_CRC_ANCILLARY_USE       = 0x0100;
    public final static int PNG_FLAG_CRC_ANCILLARY_NOWARN    = 0x0200;
    public final static int PNG_FLAG_CRC_CRITICAL_USE        = 0x0400;
    public final static int PNG_FLAG_CRC_CRITICAL_IGNORE     = 0x0800;
    public final static int PNG_FLAG_FREE_PALETTE            = 0x1000;
    public final static int PNG_FLAG_FREE_TRANS              = 0x2000;
    public final static int PNG_FLAG_FREE_HIST               = 0x4000;
    public final static int PNG_FLAG_HAVE_CHUNK_HEADER       = 0x8000;
    public final static int PNG_FLAG_WROTE_tIME              = 0x10000;

    public final static int PNG_FLAG_CRC_ANCILLARY_MASK = PNG_FLAG_CRC_ANCILLARY_USE |
                                                          PNG_FLAG_CRC_ANCILLARY_NOWARN;

    public final static int PNG_FLAG_CRC_CRITICAL_MASK  = PNG_FLAG_CRC_CRITICAL_USE |
                                                          PNG_FLAG_CRC_CRITICAL_IGNORE;

    public final static int PNG_FLAG_CRC_MASK           = PNG_FLAG_CRC_ANCILLARY_MASK |
                                                          PNG_FLAG_CRC_CRITICAL_MASK;


    /** constant strings for known chunk types. **/
    public final static byte[] png_IHDR = { 73,  72,  68,  82};
	public final static int png_IHDR_len = 13;	// fixed length chunk
    public final static byte[] png_IDAT = { 73,  68,  65,  84};
    public final static byte[] png_IEND = { 73,  69,  78,  68};
    public final static byte[] png_PLTE = { 80,  76,  84,  69};
    public final static byte[] png_bKGD = { 98,  75,  71,  68};
    public final static byte[] png_cHRM = { 99,  72,  82,  77};
    public final static byte[] png_gAMA = {103,  65,  77,  65};
    public final static byte[] png_hIST = {104,  73,  83,  84};
    public final static byte[] png_oFFs = {111,  70,  70, 115};
    public final static byte[] png_pCAL = {112,  67,  65,  76};
    public final static byte[] png_pHYs = {112,  72,  89, 115};
    public final static byte[] png_sBIT = {115,  66,  73,  84};
    public final static byte[] png_tEXt = {116,  69,  88, 116};
    public final static byte[] png_tIME = {116,  73,  77,  69};
    public final static byte[] png_tRNS = {116,  82,  78,  83};
    public final static byte[] png_zTXt = {122,  84,  88, 116};


	// Contants from pngconf.h
	/**
	 * This is the size of the compression buffer, and thus the size of
	 * an IDAT chunk.  Make this whatever size you feel is best for your
	 * machine.  One of these will be allocated per png_struct.  When this
	 * is full, it writes the data to the disk, and does some other
	 * calculations.  Making this an extreamly small size will slow
	 * the library down, but you may want to experiment to determine
	 * where it becomes significant, if you are concerned with memory
	 * usage.  Note that zlib allocates at least 32Kb also.  For readers,
	 * this describes the size of the buffer available to read the data in.
	 * Unless this gets smaller then the size of a row (compressed),
	 * it should not make much difference how big this is.
	 **/
	public final static int PNG_ZBUF_SIZE = 8192;


	/**
	 * The maximum number of colors in a color palette.
	 **/
	public final static int MAX_PALETTE = 256;


}
