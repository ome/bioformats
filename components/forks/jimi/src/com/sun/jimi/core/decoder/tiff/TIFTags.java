package com.sun.jimi.core.decoder.tiff;

public class TIFTags
{
//	public static final int BATCHID						= 33901;
	public static final int PREDICTOR_NONE				= 1;
	public static final int PREDICTOR_HORIZONTAL		= 2;

	public static final int CLEANFAXDATA_CLEAN			= 0;	
	public static final int CLEANFAXDATA_REGENERATED	= 1;	
	public static final int CLEANFAXDATA_UNCLEAN		= 2;	

	public static final int COLORRESPONSEUNIT_10S		= 1;
	public static final int COLORRESPONSEUNIT_100S		= 2;
	public static final int COLORRESPONSEUNIT_1000S		= 3;
	public static final int COLORRESPONSEUNIT_10000S	= 4;
	public static final int COLORRESPONSEUNIT_100000S	= 5;

	public static final int COMPRESSION_NONE			= 1;
	public static final int COMPRESSION_CCITT1D			= 2;
	public static final int COMPRESSION_CCITTFAX3		= 3;
	public static final int COMPRESSION_CCITTFAX4		= 4;
	public static final int COMPRESSION_LZW				= 5;
	public static final int COMPRESSION_OJPEG			= 6;	// old jpeg
	public static final int COMPRESSION_JPEG			= 7;	// jpeg dct ?
	public static final int COMPRESSION_CCITTRLEW		= 32771;
	public static final int COMPRESSION_PACKBITS		= 32773;
	public static final int COMPRESSION_NEXT			= 32766;
	public static final int COMPRESSION_THUNDERSCAN		= 32809;

	public static final int EXTRASAMPLE_UNSPECIFIED		= 0;
	public static final int EXTRASAMPLE_ASSOCALPHA		= 1;
	public static final int EXTRASAMPLE_UNASSALPHA		= 2;

	public static final int FILLORDER_MSB2LSB			= 1;
	public static final int FILLORDER_LSB2MSB			= 2;

	// t4 options
//	public static final int GROUP3OPT_2DENCODING		= 0x1;
	public static final int T4OPT_2DENCODING			= 0x1;
//	public static final int GROUP3OPT_UNCOMPRESSED		= 0x2;
	public static final int T4OPT_UNCOMPRESSED			= 0x2;
//	public static final int GROUP3OPT_FILLBITS			= 0x4;
	public static final int T4OPT_FILLBITS				= 0x4;

	// t6 options
//	public static final int GROUP4OPT_UNCOMPRESSED		= 0x2;
	public static final int T6OPT_UNCOMPRESSED			= 0x2;

	public static final int GRAYRESPONSEUNIT_10S		= 1;
	public static final int GRAYRESPONSEUNIT_100S		= 2;
	public static final int GRAYRESPONSEUNIT_1000S		= 3;
	public static final int GRAYRESPONSEUNIT_10000S		= 4;
	public static final int GRAYRESPONSEUNIT_100000S	= 5;

	public static final int JPEGPROC_BASELINE			= 1;
	public static final int JPEGPROC_LOSSLESS			= 14;

	/**
	 * 1 = The 0th row represents the visual top of the image,
	 * 		and the 0th column represents the visual left-hand side.
	 * 2 = The 0th row represents the visual top of the image,
	 * 		and the 0th column represents the visual right-hand side.
	 * 3 = The 0th row represents the visual bottom of the image,
	 * 		and the 0th column repre-sents the visual right-hand side.
	 * 4 = The 0th row represents the visual bottom of the image,
	 * 		and the 0th column repre-sents the visual left-hand side.
	 * 5 = The 0th row represents the visual left-hand side of the image,
	 * 		and the 0th column represents the visual top.
	 * 6 = The 0th row represents the visual right-hand side of the image,
	 * 		and the 0th column represents the visual top.
	 * 7 = The 0th row represents the visual right-hand side of the image,
	 *		and the 0th column represents the visual bottom.
	 * 8 = The 0th row represents the visual left-hand side of the image,
	 *		and the 0th column represents the visual bottom.
	 **/
	public static final int ORIENTATION_TOPLEFT			= 1;
	public static final int ORIENTATION_TOPRIGHT		= 2;
	public static final int ORIENTATION_BOTRIGHT		= 3;
	public static final int ORIENTATION_BOTLEFT			= 4;
	public static final int ORIENTATION_LEFTTOP			= 5;
	public static final int ORIENTATION_RIGHTTOP		= 6;
	public static final int ORIENTATION_RIGHTBOT		= 7;
	public static final int ORIENTATION_LEFTBOT			= 8;

	public static final int PHOTOMETRIC_WHITEISZERO		= 0;
	public static final int PHOTOMETRIC_BLACKISZERO		= 1;
	public static final int PHOTOMETRIC_RGB				= 2;
	public static final int PHOTOMETRIC_PALETTE			= 3;
	public static final int PHOTOMETRIC_MASK			= 4;
	public static final int PHOTOMETRIC_SEPERATED		= 5;
	public static final int PHOTOMETRIC_YCBCR			= 6;
	public static final int PHOTOMETRIC_CIELAB			= 8;

	public static final int PLANARCONFIG_CONTIG			= 1;
	public static final int PLANARCONFIG_SEPARATE		= 2;

	public static final int RESUNIT_NONE				= 1;
	public static final int RESUNIT_INCH				= 2;
	public static final int RESUNIT_CENTIMETER			= 3;

	public static final int SAMPLEFORMAT_UINT			= 1;
	public static final int SAMPLEFORMAT_INT			= 2;
	public static final int SAMPLEFORMAT_IEEEFP			= 3;
	public static final int SAMPLEFORMAT_VOID			= 4;

	public static final int THRESHHOLD_BILEVEL			= 1;
	public static final int THRESHHOLD_HALFTONE			= 2;
	public static final int THRESHOLD_ERRORDIFFUSE		= 3;

	public static final int OFILETYPE_IMAGE				= 1;  // not sure
	public static final int OFILETYPE_REDUCEDIMAGE		= 2;
	public static final int OFILETYPE_PAGE				= 3;

	public static final int FILETYPE_MASK				= 0x4; // not sure
	public static final int FILETYPE_PAGE				= 0x2;
	public static final int FILETYPE_REDUCEDIMAGE		= 0x1;


	// TIFF tags
	public static final int ARTIST						= 315;
	public static final int BADFAXLINES					= 326; // class F
	public static final int BITSPERSAMPLE				= 258;
	public static final int CELLLENGTH					= 265;
	public static final int CELLWIDTH					= 264;
	public static final int CLEANFAXDATA				= 327; // class F
	public static final int COLORMAP					= 320;
//	public static final int COLORRESPONSECURVE			= 301; //old
	public static final int COLORRESPONSEUNIT			= 300;
	public static final int COMPRESSION					= 259;
	public static final int CONSECUTIVEBADFAXLINES		= 328; // class F
	public static final int COPYRIGHT					= 33432;
	public static final int DATETIME					= 306;
	public static final int DOCUMENTNAME				= 269;
	public static final int DOTRANGE					= 336;
	public static final int EXTRASAMPLES				= 338;
	public static final int FILLORDER					= 266;
	public static final int FREEBYTECOUNTS				= 289;
	public static final int FREEOFFSETS					= 288;
//	public static final int GROUP3OPTIONS				= 292; // old
//	public static final int GROUP4OPTIONS				= 293; // old
	public static final int GRAYRESPONSECURVE			= 291;
	public static final int GRAYRESPONSEUNIT			= 290;
	public static final int HALFTONEHINTS				= 321;
	public static final int HOSTCOMPUTER				= 316;
	public static final int IMAGEDESCRIPTION			= 270;
	public static final int IMAGELENGTH					= 257;
	public static final int IMAGEWIDTH					= 256;
	public static final int INKNAMES					= 333;
	public static final int INKSET						= 332;
	public static final int JPEGACTABLES				= 521;
	public static final int JPEGDCTABLES				= 520;
	public static final int JPEGINTERCHANGEFORMAT		= 513;
	public static final int JPEGINTERCHANGEFORMATLENGTH= 514;
	public static final int JPEGLOSSLESSPREDICTORS		= 517;
	public static final int JPEGPOINTTRANSFORMS			= 518;
	public static final int JPEGPROC					= 512;
	public static final int JPEGRESTARTINTERVAL			= 515;
	public static final int JPEGQTABLES					= 519;
	public static final int MAKE						= 271;
	public static final int MAXSAMPLEVALUE				= 281;
	public static final int MINSAMPLEVALUE				= 280;
	public static final int MODEL						= 272;
	public static final int NEWSUBFILETYPE				= 254;
	public static final int NUMBEROFINKS				= 334;
	public static final int ORIENTATION					= 274;
	public static final int PAGENAME					= 285;
	public static final int PAGENUMBER					= 297;
	public static final int PHOTOMETRICINTERPRETATION	= 262;
	public static final int PLANARCONFIGURATION			= 284;
	public static final int PREDICTOR					= 317;
	public static final int PRIMARYCHROMATICITIES		= 319;
	public static final int REFERENCEBLACKWHITE			= 532;
	public static final int RESOLUTIONUNIT				= 296;
	public static final int ROWSPERSTRIP				= 278;
	public static final int SAMPLEFORMAT				= 339;
	public static final int SAMPLESPERPIXEL				= 277;
	public static final int SMAXSAMPLEVALUE				= 341;
	public static final int SMINSAMPLEVALUE				= 340;
	public static final int SOFTWARE					= 305;
	public static final int STRIPBYTECOUNTS				= 279;
	public static final int STRIPOFFSETS				= 273;
	public static final int SUBFILETYPE					= 255; // ?
	public static final int T4OPTIONS					= 292;
	public static final int T6OPTIONS					= 293;
	public static final int TARGETPRINTER				= 337;
	public static final int THRESHHOLDING				= 263;
	public static final int TILEBYTECOUNTS				= 325;
	public static final int TILELENGTH					= 323;
	public static final int TILEOFFSETS					= 324;
	public static final int TILEWIDTH					= 322;
	public static final int TRANSFERFUNCTION			= 301;
	public static final int TRANSFERRANGE				= 342;
	public static final int XPOSITION					= 286;
	public static final int XRESOLUTION					= 282;
	public static final int YCBCRCOEFFICIENTS			= 529;
	public static final int YCBCRPOSITIONING			= 531;
	public static final int YCBCRSUBSAMPLING			= 530;
	public static final int YPOSITION					= 287;
	public static final int YRESOLUTION					= 283;
	public static final int WHITEPOINT					= 318;

}
