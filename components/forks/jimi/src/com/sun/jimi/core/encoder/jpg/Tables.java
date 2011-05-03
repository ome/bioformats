package com.sun.jimi.core.encoder.jpg;

/**
 * Updated to allow for quality setting of saved jpg quality.
 * The quality parameter and settings are the same as those used
 * in the Independent JPG groups implementation.
 *
 **/
public final class Tables
{
	// Set up the standard Huffman tables (cf. JPEG standard section K.3) 
  public static final short dc_luminance_bits[] =
    { /* 0-base */ 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
  public static final short dc_luminance_val[] =
    { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
  
  public static final short dc_chrominance_bits[] =
    { /* 0-base */ 0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
  public static final short dc_chrominance_val[] =
    { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
  
  public static final short ac_luminance_bits[] =
    { /* 0-base */ 0, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 0x7d };
  public static final short ac_luminance_val[] =
    { 0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12,
      0x21, 0x31, 0x41, 0x06, 0x13, 0x51, 0x61, 0x07,
      0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08,
      0x23, 0x42, 0xb1, 0xc1, 0x15, 0x52, 0xd1, 0xf0,
      0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16,
      0x17, 0x18, 0x19, 0x1a, 0x25, 0x26, 0x27, 0x28,
      0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
      0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49,
      0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59,
      0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69,
      0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79,
      0x7a, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89,
      0x8a, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98,
      0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7,
      0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6,
      0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5,
      0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4,
      0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1, 0xe2,
      0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea,
      0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
      0xf9, 0xfa };
  
  public static final short ac_chrominance_bits[] =
    { /* 0-base */ 0, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 0x77 };
  public static final short ac_chrominance_val[] =
    { 0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21,
      0x31, 0x06, 0x12, 0x41, 0x51, 0x07, 0x61, 0x71,
      0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91,
      0xa1, 0xb1, 0xc1, 0x09, 0x23, 0x33, 0x52, 0xf0,
      0x15, 0x62, 0x72, 0xd1, 0x0a, 0x16, 0x24, 0x34,
      0xe1, 0x25, 0xf1, 0x17, 0x18, 0x19, 0x1a, 0x26,
      0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38,
      0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48,
      0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58,
      0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
      0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78,
      0x79, 0x7a, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87,
      0x88, 0x89, 0x8a, 0x92, 0x93, 0x94, 0x95, 0x96,
      0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5,
      0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4,
      0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3,
      0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2,
      0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda,
      0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9,
      0xea, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
      0xf9, 0xfa };

  public static final short std_luminance_quant_tbl[] = {
    16,  11,  12,  14,  12,  10,  16,  14,
    13,  14,  18,  17,  16,  19,  24,  40,
    26,  24,  22,  22,  24,  49,  35,  37,
    29,  40,  58,  51,  61,  60,  57,  51,
    56,  55,  64,  72,  92,  78,  64,  68,
    87,  69,  55,  56,  80, 109,  81,  87,
    95,  98, 103, 104, 103,  62,  77, 113,
    121, 112, 100, 120,  92, 101, 103,  99
  };

  public static final short std_chrominance_quant_tbl[] = {
    17,  18,  18,  24,  21,  24,  47,  26,
    26,  47,  99,  66,  56,  66,  99,  99,
    99,  99,  99,  99,  99,  99,  99,  99,
    99,  99,  99,  99,  99,  99,  99,  99,
    99,  99,  99,  99,  99,  99,  99,  99,
    99,  99,  99,  99,  99,  99,  99,  99,
    99,  99,  99,  99,  99,  99,  99,  99,
    99,  99,  99,  99,  99,  99,  99,  99
  };

	int scaleFactor_;

	boolean forceBaseLine_;

	Tables()
	{
		this(75, true);		// default quality. NOTE: 50 is equivalent to default tables.
	}

	Tables(int quality)
	{
		this(quality, true);
	}

	/**
	 * @param quality the quality setting for DCT quantisation quality control
	 * of saved jpg image quality. Range 0 to 100 only.
	 * @param forceBaseline force baseline jpg. Currently this must be true.
	 **/
	Tables(int quality, boolean forceBaseline)
	{
		if (quality < 0 || quality > 100)
			throw new IllegalArgumentException("Invalid jpg quality setting");

		if (!forceBaseline)
			throw new IllegalArgumentException("Jpeg quantisation only supports Baseline currently");

		scaleFactor_ = getJpegQualityScaleFactor(quality);
		forceBaseLine_ = forceBaseline;
	}

	/** 
	 * @param table to be filled in with luminance quantisation info based on the
	 * quality setting for this object.
	 **/
	public void getLuminanceQuantTable(short[] table)
	{
		buildQuantTable(scaleFactor_, forceBaseLine_, std_luminance_quant_tbl, table);
	}

	/** 
	 * @param table to be filled in with chrominance quantisation info based on the
	 * quality setting for this object.
	 **/
	public void getChrominanceQuantTable(short[] table)
	{
		buildQuantTable(scaleFactor_, forceBaseLine_, std_chrominance_quant_tbl, table);
	}

	/**
	 * @param scale_factor the scale factor related to quality control
	 * @param base_line indicates base line mode in operation
	 * @param std the standard quantisation table to be modified
	 * @param scaled the table to filled in with the scaled quantisation table
	 **/
	void buildQuantTable(int scaleFactor, boolean baseLine, short[] base, short[] scaled)
	{
		int i;
		int temp;
		int scale = scaleFactor;

		for (i = base.length; --i >= 0; )
		{
			temp = (base[i] * scale + 50) / 100;
			if (temp <= 0)
				temp = 1;
			if (temp > 32767)
				temp = 32767;	// max quantizer for 12 bits
			if (baseLine && temp > 255)
				temp = 255;
			scaled[i] = (short)temp;
		}
	}

	/**
	 * @param quality jpeg quality control save value range 0 to 100.
	 * 0 is terrible, 100 is (very good)
	 * @return the scaling factor to apply to quantisation tables
	 **/
	int getJpegQualityScaleFactor(int quality)
	{
 		// Safety limit on quality factor.  Convert 0 to 1 to avoid zero divide
		if (quality <= 1)
			quality = 1;
		if (quality > 100)
			quality = 100;

		// The basic table is used as-is (scaling 100) for a quality of 50.
		// Qualities 50..100 are converted to scaling percentage 200 - 2*Q;
		// note that at Q=100 the scaling is 0, which will cause jpeg_add_quant_table
		// to make all the table entries 1 (hence, minimum quantization loss).
		// Qualities 1..50 are converted to scaling percentage 5000/Q.
		if (quality < 50)
			quality = 5000 / quality;
		else
			quality = 200 - quality * 2;
		return quality;
	}

}  

