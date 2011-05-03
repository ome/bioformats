package com.sun.jimi.core.decoder.tiff;

import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.io.*;
import java.util.Enumeration;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.JimiUtil;
import com.sun.jimi.core.util.SeekInputStream;

/**
 * This class handles the decoding of the image given the IFD.
 * required Tags decoded - other tags ignored for now.
 *
 * 
 * TODO:
 * Can replace the TIFFNumberReader with my InputStream eventually as only
 * readByte() is ever called....
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.7 
 **/
class IFDDecode
{
	AdaptiveRasterImage ji;

	IFD ifd;

	SeekInputStream sis;

	// Minimum required Tags for Each TIFF Class	
	int	newsubfiletype;				// info flags on this image
	int imagewidth;					// short or long
	int imagelength;				// this is width // short or long
	short[] bitspersample;			// short
	int compression;				// short
	int photometricinterpretation;	// short
	int[] stripoffsets;				// short or long
	int samplesperpixel;			// short
	int rowsperstrip;				// short or long
	int[] stripbytecounts;			// short or long
	float xres;						// rational
	float yres;						// rational
	int resolutionunit;				// short

	// Palette-Color TIFF Class
	short[] colormap;				// short

	// RGB TIFF Class
	int planarconfiguration; 		// short
	int extrasamples;				// short - optional for RGB

	// YCbCr TIFF Class
	float ycbcrcoefficients;		// rational
	int ycbcrpositioning;			// short
	int[] ycbcrsubsampling;			// short
	int referenceblackwhite;		// long

	// Class F - Fax
	int	badfaxlines;				// short or long
	int cleanfaxdata;				// short or long
	int consecutivebadfaxlines;		// short or long

	// tiled images
	int tilewidth;					// short or long
	int tilelength;					// short or long
	int[] tilebytecounts;			// short or long
	int[] tileoffsets;				// long

	int	fillorder;					// short
	int	orientation;				// short
	int	t4options;					// long for compression type 3
	int	t6options;					// long for compression type 4

	int	predictor;					// short

	// re-usable LZW decompressor
	LZWDecomp lzwDecomp_;

	//
	// Derived fields from loaded fields above
	//
	/** number of strips in image **/
	int stripsperimage;

	int tilesacross;
	int tilesdown;
	int tilesperimage;

	TIFDecoder decoder;

	/**
	 * @param ji JimiImage uses as requied to load image any data values
	 * allready set will be lost except for Properties.
	 * @param sis SeekInputStream source of TIFF encoded image data
	 **/
	IFDDecode(TIFDecoder decoder, AdaptiveRasterImage ji, SeekInputStream sis) throws IOException
	{
		initDefaults();
		this.ji = ji;
		this.sis = sis;
		this.decoder = decoder;
	}

	void decodeTags(IFD ifd) throws IOException
	{
		TIFField f;

		Enumeration enum = ifd.getFields();
		TIFField tf;
		while (enum.hasMoreElements())
		{
			tf = (TIFField)enum.nextElement();
			decodeField(tf);
		}
	}

	/**
	 * @param tf tiff field is decoded and value extracted into IFDDecode
	 * member values for use.
	 **/
	void decodeField(TIFField tf) throws IOException
	{
		switch (tf.id)
		{
		// first group Minimum Required Tags for each TIFF Class
		case TIFTags.NEWSUBFILETYPE:
			newsubfiletype = tf.getInt(sis);
			break;
		case TIFTags.IMAGEWIDTH:
			imagewidth = tf.getInt(sis);
			break;
		case TIFTags.IMAGELENGTH:
			imagelength = tf.getInt(sis);
			break;
		case TIFTags.BITSPERSAMPLE:
			bitspersample = tf.getShortArray(sis);
			break;
	  case TIFTags.COMPRESSION:
			compression = tf.getInt(sis);
			break;
		case TIFTags.PHOTOMETRICINTERPRETATION:
			photometricinterpretation = tf.getInt(sis);
			break;
		case TIFTags.STRIPOFFSETS:
			stripoffsets = tf.getIntArray(sis);
			break;
		case TIFTags.SAMPLESPERPIXEL:
			samplesperpixel = tf.getInt(sis);
			break;
		case TIFTags.ROWSPERSTRIP:
			rowsperstrip = tf.getInt(sis);
			break;
		case TIFTags.STRIPBYTECOUNTS:
			stripbytecounts = tf.getIntArray(sis);
			break;
		case TIFTags.XRESOLUTION:
			xres = tf.getRational(sis);
			break;
		case TIFTags.YRESOLUTION:
			yres = tf.getRational(sis);
			break;
		case TIFTags.RESOLUTIONUNIT:
			resolutionunit = tf.getInt(sis);
			break;

		// Required Tags for Palette Color Class
		case TIFTags.COLORMAP:
			colormap = tf.getShortArray(sis);
			break;

		// Required Tags for RGB Class
		case TIFTags.PLANARCONFIGURATION:
			planarconfiguration = tf.getInt(sis);
			break;
		case TIFTags.EXTRASAMPLES:
			extrasamples = tf.getInt(sis);
			break;

		// Required Tags for YCbCr
		case TIFTags.YCBCRCOEFFICIENTS:
			ycbcrcoefficients = tf.getRational(sis);
			break;
		case TIFTags.YCBCRSUBSAMPLING:
			ycbcrsubsampling = tf.getIntArray(sis);
			break;
		case TIFTags.YCBCRPOSITIONING:
			ycbcrpositioning = tf.getInt(sis);
			break;

		// Required Tags for Class F
		case TIFTags.BADFAXLINES:
			badfaxlines = tf.getInt(sis);
			break;
		case TIFTags.CLEANFAXDATA:
			cleanfaxdata = tf.getInt(sis);
			break;
		case TIFTags.CONSECUTIVEBADFAXLINES:
			consecutivebadfaxlines = tf.getInt(sis);
			break;

		// Tile format TIFF file
		case TIFTags.TILEWIDTH:
			tilewidth = tf.getInt(sis);
			break;
		case TIFTags.TILELENGTH:
			tilelength = tf.getInt(sis);
			break;
		case TIFTags.TILEOFFSETS:
			tileoffsets = tf.getIntArray(sis);
			break;
		case TIFTags.TILEBYTECOUNTS:
			tilebytecounts = tf.getIntArray(sis);
			break;

		// Control options
		case TIFTags.FILLORDER:
			fillorder = tf.getInt(sis);
			break;
		case TIFTags.ORIENTATION:
			orientation = tf.getInt(sis);
			break;
		case TIFTags.T4OPTIONS:
			t4options = tf.getInt(sis);
			break;
		case TIFTags.T6OPTIONS:
			t6options = tf.getInt(sis);
			break;

		case TIFTags.PREDICTOR:	// for LZW at least.
			predictor = tf.getInt(sis);
			break;

		default:
			break;
		}
	}

	/**
	 * Initialise the Image Decoder so that all TIF Fields have
	 * there values set to the default value in case the TIFF file
	 * does not set a Field as it assumes default.
	 **/
	void initDefaults()
	{
		newsubfiletype = 0;
		bitspersample = new short[1];
		bitspersample[0] = 1;
		compression = 1;
		samplesperpixel = 1;
		rowsperstrip = 0xFFFFFFFF;					// unsigned MAX
		resolutionunit = TIFTags.RESUNIT_INCH;

		planarconfiguration = TIFTags.PLANARCONFIG_CONTIG;

		tilewidth = 0;
		tilelength = 0;

		fillorder = TIFTags.FILLORDER_MSB2LSB;
		orientation = TIFTags.ORIENTATION_TOPLEFT;
		t6options = 0;
		t4options = 0;
		predictor = TIFTags.PREDICTOR_NONE;
	}

	/**
	 * @return an Index color model which is created from the loaded
	 * TIF color map.
	 **/
	ColorModel createPaleteColorModel() throws JimiException
	{
		int numColors = colormap.length/3;	// num colors in palette
		byte[] red;
		byte[] green;
		byte[] blue;
		int i;

		red   = new byte[numColors];
		green = new byte[numColors];
		blue  = new byte[numColors];

		// convert colormap to useful form for Java
		for (i = 0; i < numColors; ++i)
		{
			// scale 16 bit entries down to 8 bit
			red[i] = (byte)(colormap[i] >> 8);
			green[i] = (byte)(colormap[i + numColors] >> 8);
			blue[i] = (byte)(colormap[i +  2 * numColors] >> 8);
		}

		return new IndexColorModel(8, numColors, red, green, blue);
	}


	/**
	 * @return a color model dependent on the data encoded in the TIFF file.
	 **/
	ColorModel createColorModel() throws JimiException
	{
		int bitMask;
		
		switch (photometricinterpretation)
		{
		case TIFTags.PHOTOMETRIC_WHITEISZERO:
			bitMask = (1 << bitspersample[0]) - 1;
			return new DirectColorModel(bitspersample[0], bitMask, bitMask, bitMask);

		case TIFTags.PHOTOMETRIC_BLACKISZERO:
			bitMask = (1 << bitspersample[0]) - 1;
			return new DirectColorModel(bitspersample[0], bitMask, bitMask, bitMask);

		case TIFTags.PHOTOMETRIC_PALETTE:
			return createPaleteColorModel();

		case TIFTags.PHOTOMETRIC_RGB:
			if (samplesperpixel != 3 || !(bitspersample[0] == 8 &&
										  bitspersample[1] == 8 &&
										  bitspersample[2] == 8))
				throw new JimiException("RGB image not 3 x 8 bits");
			return ColorModel.getRGBdefault();

		default:
			return null;
		}
	}

	void decodeImage() throws JimiException, IOException
	{

		if (planarconfiguration == TIFTags.PLANARCONFIG_SEPARATE)
			throw new JimiException("Seperate planar config not supported");

		if (compression == TIFTags.COMPRESSION_OJPEG ||
			compression == TIFTags.COMPRESSION_JPEG)
			throw new JimiException("TIFF JPG format not supported");

		// assuming that never get rows per strip > 2^31 - 1
		// therefore if in int and negative assume rowsperstrip is == imagelength
		if ((rowsperstrip & 0x80000000) != 0)
			rowsperstrip = imagelength;

		if (tilewidth == 0)
		{
			stripsperimage = ((imagelength + rowsperstrip - 1) / rowsperstrip);
		}
		else // tiled image
		{
			tilesacross = (imagewidth + tilewidth - 1) / tilewidth;
			tilesdown = (imagelength + tilelength - 1) / tilelength;
			tilesperimage = tilesacross * tilesdown;

			if (tileoffsets == null)
			{
				tileoffsets = stripoffsets;
				tilebytecounts = stripbytecounts;
			}
		}

		// check required information for given image type available.
		switch (photometricinterpretation)
		{
		case TIFTags.PHOTOMETRIC_WHITEISZERO:
			break;

		case TIFTags.PHOTOMETRIC_BLACKISZERO:
			break;

		case TIFTags.PHOTOMETRIC_RGB:
			break;

		case TIFTags.PHOTOMETRIC_PALETTE:
			break;

		case TIFTags.PHOTOMETRIC_MASK:
			throw new JimiException("photometric MASK unsupported");

		case TIFTags.PHOTOMETRIC_SEPERATED:
			throw new JimiException("photometric SEPERATED unsupported");

		case TIFTags.PHOTOMETRIC_YCBCR:
			throw new JimiException("photometric YCBCR unsupported");

		case TIFTags.PHOTOMETRIC_CIELAB:
			throw new JimiException("photometric CIELAB unsupported");

		default:
			throw new JimiException("Photometric Interpretation invalid " + photometricinterpretation);
		}

		// check image output dimensions for orientation of image.
		switch (orientation)
		{
		case TIFTags.ORIENTATION_TOPLEFT:
		case TIFTags.ORIENTATION_TOPRIGHT:
		case TIFTags.ORIENTATION_BOTRIGHT:
		case TIFTags.ORIENTATION_BOTLEFT:
			ji.setSize(imagewidth, imagelength);
			break;

		case TIFTags.ORIENTATION_LEFTTOP:
		case TIFTags.ORIENTATION_RIGHTTOP:
		case TIFTags.ORIENTATION_RIGHTBOT:
		case TIFTags.ORIENTATION_LEFTBOT:
			ji.setSize(imagelength, imagewidth);
			ji.setHints(ImageConsumer.RANDOMPIXELORDER);
			ji.setProperty("fixedaspect", Boolean.TRUE);
			break;
		}

		ji.setProperty("xres", new Float(xres));
		ji.setProperty("yres", new Float(yres));
		ColorModel cm = createColorModel();
		ji.setColorModel(cm);
		ji.setPixels();			// create space in JimiImage

		if ((photometricinterpretation == TIFTags.PHOTOMETRIC_BLACKISZERO ||
			 photometricinterpretation == TIFTags.PHOTOMETRIC_WHITEISZERO ||
			 photometricinterpretation == TIFTags.PHOTOMETRIC_PALETTE) &&
			samplesperpixel == 1)
		{
			if (tilewidth == 0)
				decodeStrips();
			else
				decodeTiles();
		}
		else
		{
			// only suport LZW or no compression for true color images
			if (compression == TIFTags.COMPRESSION_NONE ||
				compression == TIFTags.COMPRESSION_PACKBITS ||
				compression == TIFTags.COMPRESSION_LZW)
			{
				// no compression
				if (tilewidth == 0)
					decodeStrips();
				else
					decodeTiles();
			}
			else
				throw new JimiException("Unsupported compression " + compression);
		}
	}

	/**
	 * Accumlate all the strips into a buffer and feed to toolkit then
	 * grab back image data into JimiImage.
	 * this has some overhead as we allready have returned a handle to
	 * our own ImageProducer but we are going to use the Toolkit JPG
	 * decoder.
	 **/
	void decodeJPG() throws JimiException, IOException
	{
		int bitsPerSample = bitspersample[0];
		byte[] strip;		// strip buffer
		int s;

		// accumulate total length of compressed data in all strips
		int totalLen = 0;
		for (s = stripsperimage; --s >= 0; )
			totalLen += stripbytecounts[0];

		byte[] totalBuf = new byte[totalLen];
		int offset = 0;
		for (s = 0; s < stripsperimage; ++s)
		{
			strip = new byte[stripbytecounts[s]];
			sis.seek(stripoffsets[s]);
			sis.readFully(strip, 0, stripbytecounts[s]);
			System.arraycopy(strip, 0, totalBuf, offset, stripbytecounts[s]);
			offset += stripbytecounts[s];
		}

	}


	/**
	 * @param i the strip we want to know then number of pixel rows.
	 * @return the number of rows in this strip
	 **/
	int numRows(int i)
	{
		if (i == (stripsperimage - 1))	// last strip
		{
			int val = imagelength % rowsperstrip;	// number rows in last strip
			return val != 0 ? val : rowsperstrip;
		}
		else
			return rowsperstrip;
	}

	/**
	 * @return the number of bytes in a single scanline row of image for the
	 * required bits pers sample and imagewidth
	 **/
	public int rowSize()
	{
		int scanline = bitspersample[0] * imagewidth;
		if (planarconfiguration == TIFTags.PLANARCONFIG_CONTIG)
			scanline *= samplesperpixel;
		return (scanline + 7) / 8;
	}


	/** 
	 * Create a decompressor for the given strip or tile of TIFF image data.
	 * 
	 *
	 * @param buf byte buffer of data to be decompressed. This buffer is 1
	 * strip or [tile of the image] - no tile support yet.
	 * @return a TIFF decompressor which decompresses the data in the given
	 * buffer. One of non compressed, CCITT 3 d1, CCITT 3 d2, CCITT Class F
	 **/
	Decompressor createDecompressor(byte[] buf) throws JimiException
	{
		Decompressor dcmpr = null;

		TiffNumberReader rdr = new TiffNumberReader(buf);

		switch (compression)
		{
		case TIFTags.COMPRESSION_NONE:
			dcmpr = new Decompressor(rdr, fillorder, bitspersample[0]);
			break;

		case TIFTags.COMPRESSION_CCITT1D:
			dcmpr = new CCITT3d1Decomp(rdr, fillorder);
			break;

		case TIFTags.COMPRESSION_CCITTFAX3:
			dcmpr = new CCITTClassFDecomp(rdr, fillorder, ((t4options  & TIFTags.T4OPT_FILLBITS) != 0));
			break;

		case TIFTags.COMPRESSION_CCITTFAX4:
			dcmpr = new CCITT3d2Decomp(rdr, fillorder);
			break;

		case TIFTags.COMPRESSION_PACKBITS:
			dcmpr = new Packbits(rdr, fillorder, bitspersample[0]);
			break;

		case TIFTags.COMPRESSION_LZW:
			if (predictor == TIFTags.PREDICTOR_HORIZONTAL && bitspersample[0] != 8)
				throw new JimiException("horizontal difference only supported for 8 bits currently");
			// compensate - 2 for the extra length of buffer required for G3
			if (lzwDecomp_ == null)
			{
				lzwDecomp_ = new LZWDecomp(new ByteArrayInputStream(buf, 0, buf.length - 2),
																	 fillorder, bitspersample[0], predictor);
			}
			else
				lzwDecomp_.setInputStream(new ByteArrayInputStream(buf, 0, buf.length - 2));
			dcmpr = lzwDecomp_;
			break;
		}
		return dcmpr;
	}

	/**
	 * Decoding of TIFF Striped images with samples per pixel = 1 or 3 and 
	 * bitspersample = 1, 4, or 8.
	 **/
	void decodeStrips() throws JimiException, IOException
	{
		byte[] strip;				// strip buffer
		int[] pixelBufI = null;		// stop compiler complaining about initialise
		byte[] pixelBuf = null;
		boolean startPage = true;
		int bitsPerSample = bitspersample[0];
		int row = 0;

		if (samplesperpixel != 1)
		{
			pixelBufI = new int[imagewidth];
			pixelBuf = new byte[imagewidth * samplesperpixel];
		}
		else	// samplesperpixel == 1
		{
			if (bitsPerSample == 1)
				pixelBuf = new byte[(imagewidth + 7) / 8]; // bit packed
			else
				pixelBuf = new byte[imagewidth];
		}

		// allocate enough space for the largest strip
		int max_strip_size = 0;
		for (int i = 0; i < stripsperimage; i++)
			if (stripbytecounts[i] > max_strip_size)
				max_strip_size = stripbytecounts[i];
		strip = new byte[max_strip_size + 2];

		for (int s = 0; s < stripsperimage; ++s)
		{

			sis.seek(stripoffsets[s]);
			sis.readFully(strip, 0, stripbytecounts[s]);
			Decompressor dcmpr = createDecompressor(strip);
			dcmpr.setRowsPerStrip(rowsperstrip);

			if (photometricinterpretation == TIFTags.PHOTOMETRIC_WHITEISZERO && samplesperpixel == 1)
				dcmpr.setInvert(true);

			dcmpr.begOfStrip();
			if (startPage)
				dcmpr.begOfPage();
			startPage = false;

			if (samplesperpixel == 1)
			{
				if (bitsPerSample == 1)
				{
					for (int i = numRows(s); --i >= 0; )
					{
						dcmpr.decodeLine(pixelBuf, imagewidth);
						// LZW predictor is not implemented as it appears that there
						// is no way to create Black & White LZW compressed images.
						setPackedChannel_Oriented(row, pixelBuf);
						++row;
						decoder.setProgress((row * 100) / imagelength);
					}
				}
				else
				{
					for (int i = numRows(s); --i >= 0; )
					{
						dcmpr.decodeLine(pixelBuf, pixelBuf.length);
						if (compression == TIFTags.COMPRESSION_LZW)
						{
							if (predictor == TIFTags.PREDICTOR_HORIZONTAL)
							{
								for (int k = 0; k < pixelBuf.length - 1; ++k)
									pixelBuf[k + 1] += pixelBuf[k];
							}
						}
						
						setChannel_Oriented(0, row, pixelBuf);
						++row;
						decoder.setProgress((row * 100) / imagelength);
					}
				}
			}
			else
			{
				for (int i = numRows(s); --i >= 0; )
				{
					dcmpr.decodeLine(pixelBuf, pixelBuf.length);
					if (compression == TIFTags.COMPRESSION_LZW)
					{   // different for R/G/B byte data
						if (predictor == TIFTags.PREDICTOR_HORIZONTAL)
						{
							for (int k = 0; k < (pixelBuf.length - 1)/3; ++k)
							{
								pixelBuf[(k+1)*3]   += pixelBuf[k*3];
								pixelBuf[(k+1)*3+1] += pixelBuf[k*3+1];
								pixelBuf[(k+1)*3+2] += pixelBuf[k*3+2];
							}
						}
					}
					for (int j = 0; j < imagewidth; ++j)
					{
						pixelBufI[j] = 0xFF000000 |
						((pixelBuf[j * 3]     & 0xff) << 16) |
						((pixelBuf[j * 3 + 1] & 0xff) <<  8) |
						((pixelBuf[j * 3 + 2] & 0xff));
					}
					
					setChannel_Oriented(row, pixelBufI);
					++row;
					decoder.setProgress((row * 100) / imagelength);
				}
			}
		}
	}

	/**
	 * Decoding of TIFF tiled images with samples per pixel = 1 or 3 and 
	 * bitspersample = 1, 2, 4, or 8.
	 **/
	void decodeTiles() throws JimiException, IOException
	{
		byte[] tile;		// tile buffer
		int bitsPerSample = bitspersample[0];
		byte[] pixelBuf = null;
		int[] pixelBufI = null;		// stop compiler complaining about initialise
		boolean startPage = true;
		int baseTileXoff = 0;
		int baseTileYoff = 0;
		int tileYoff;
		int clippedWidth;

		if (samplesperpixel != 1)
		{
			pixelBufI = new int[tilewidth];
			pixelBuf = new byte[tilewidth * samplesperpixel];
		}
		else	// samplesperpixel == 1
		{
			if (bitsPerSample == 1)
				pixelBuf = new byte[(tilewidth + 7) / 8]; // bit packed
			else
				pixelBuf = new byte[tilewidth];
		}

		// allocate enough space for the largest tile
		int max_tile_size = 0;
		for (int i = 0; i < tilesperimage; i++)
			if (tilebytecounts[i] > max_tile_size)
				max_tile_size = tilebytecounts[i];
		tile = new byte[max_tile_size + 2];	// + 2 for end of array exception in decopmressor in G3

		for (int t = 0; t < tilesperimage; ++t)
		{
			sis.seek(tileoffsets[t]);
			sis.readFully(tile, 0, tilebytecounts[t]);
			Decompressor dcmpr = createDecompressor(tile);

			if (photometricinterpretation == TIFTags.PHOTOMETRIC_WHITEISZERO)
				dcmpr.setInvert(true);

			dcmpr.begOfStrip();
			if (startPage)
				dcmpr.begOfPage();
			startPage = false;

			tileYoff = baseTileYoff;
			for (int i = tilelength; --i >= 0; )
			{
				dcmpr.decodeLine(pixelBuf, pixelBuf.length);

				// clip against right border of image
				if ((baseTileXoff + tilewidth) > imagewidth)
					clippedWidth = imagewidth - baseTileXoff;
				else
					clippedWidth = tilewidth;

				// clip against bottom border of image
				if (tileYoff < imagelength)
				{
					if (samplesperpixel == 1)
					{
						if (bitsPerSample == 1)
						{
							// LZW predictor is not implemented as it appears that there
							// is no way to create Black & White LZW compressed images.
							setPackedChannel_Oriented(baseTileXoff, tileYoff, clippedWidth, pixelBuf);
						}
						else
						{
							if (compression == TIFTags.COMPRESSION_LZW)
							{
								if (predictor == TIFTags.PREDICTOR_HORIZONTAL)
								{
									for (int k = 0; k < pixelBuf.length - 1; ++k)
										pixelBuf[k + 1] += pixelBuf[k];
								}
							}
							setChannel_Oriented(baseTileXoff, tileYoff, clippedWidth, pixelBuf);
						}
					}
					else
					{
						if (compression == TIFTags.COMPRESSION_LZW)
						{   // different for R/G/B byte data
							if (predictor == TIFTags.PREDICTOR_HORIZONTAL)
							{
								for (int k = 0; k < (pixelBuf.length - 1)/3; ++k)
								{
									pixelBuf[(k+1)*3]   += pixelBuf[k*3];
									pixelBuf[(k+1)*3+1] += pixelBuf[k*3+1];
									pixelBuf[(k+1)*3+2] += pixelBuf[k*3+2];
								}
							}
						}

						for (int j = 0; j < imagewidth; ++j)
						{
							pixelBufI[j] = 0xFF000000 |
										((pixelBuf[j * 3]     & 0xff) << 16) |
										((pixelBuf[j * 3 + 1] & 0xff) <<  8) |
										((pixelBuf[j * 3 + 2] & 0xff));
						}

						setChannel_Oriented(baseTileXoff, tileYoff, clippedWidth, pixelBufI);
					}
					++tileYoff;
				}
				else
					i = 0;		// exit loop done with this tile.
			}

			baseTileXoff += tilewidth;
			if (baseTileXoff > ((tilesacross - 1) * tilewidth))
			{
				baseTileXoff = 0;
				baseTileYoff += tilelength;
			}
		}
	}


	// buffer for setPackedChannel_Oriented
	byte[] spc_o_;

	void setPackedChannel_Oriented(int x, int y, int w, byte[] pixels) throws JimiException
	{
		if (spc_o_ == null)
			spc_o_ = new byte[pixels.length * 8];
		JimiUtil.expandOneBitPixels(pixels, spc_o_, imagewidth);
		setChannel_Oriented(x, y, w, spc_o_);
	}

	// Isolates and speeds up normal orientation only all others end up
	// being expanded to 1 bit/byte and use other methods.
	void setPackedChannel_Oriented(int row, byte[] pixels) throws JimiException
	{
		if (orientation == TIFTags.ORIENTATION_TOPLEFT)
		{
			ji.setPackedChannel(row, pixels);
		}
		else
		{
			if (spc_o_ == null)
				spc_o_ = new byte[pixels.length * 8];
			JimiUtil.expandOneBitPixels(pixels, spc_o_, imagewidth);
			setChannel_Oriented(0, row, spc_o_);
		}
	}

	/**
	 * This method takes into account the TIF Field "orientation" and
	 * places the image data into the JimiImage in the correct orientation.
	 **/
    // channel = 0, h = 1, off = 0, scansize = w;
	void setChannel_Oriented(int x, int y, int w, byte[] pixels) throws JimiException
	{
		byte[] buf;
		int offset;
		int i;
		int row;
		int idx;
		int jiWidth;
		int jiHeight;

		switch (orientation)
		{
		case TIFTags.ORIENTATION_TOPLEFT: // 1
			ji.setChannel(0, x, y, w, 1, pixels, 0, 0);
			break;

		case TIFTags.ORIENTATION_TOPRIGHT: // 2	// flip horizontally
			jiWidth = ji.getWidth();
			offset = 0;
			buf = new byte[w];
			// only the pixels that need to be flipped horizontally
			for (i = w; --i >= 0;)
				buf[i] = pixels[offset++];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			ji.setChannel(0, x, y, w, 1, buf, 0, 0);
			break;

		case TIFTags.ORIENTATION_BOTRIGHT: // 3	// flip horizontally and vertically
			// input y is distance relative to bottom row 0
			jiHeight = ji.getHeight();
			y = jiHeight - y - 1;
			jiWidth = ji.getWidth();
			buf = new byte[w];
			offset = w;             	// to end of all data

			// only the pixels that need to be flipped horizontally
			for (i = 0; i < w; ++i)
				buf[i] = pixels[--offset];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			ji.setChannel(0, x, y, w, 1, buf, 0, 0);
			break;

		case TIFTags.ORIENTATION_BOTLEFT: // 4	// flip vertically
			// input y is distance relative to bottom row 0
			jiHeight = ji.getHeight();
			y = jiHeight - y - 1;
			ji.setChannel(0, x, y, w, 1, pixels, 0, 0);
			break;

		case TIFTags.ORIENTATION_LEFTTOP: // 5
			// using getWidth() for height because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiHeight = ji.getWidth();
			y = jiHeight - y - 1;
			setChannel_RotateCW90(x, y, w, pixels);
			break;

		case TIFTags.ORIENTATION_RIGHTTOP: // 6
			setChannel_RotateCW90(x, y, w, pixels);
			break;

		case TIFTags.ORIENTATION_RIGHTBOT: // 7
			// using getHeight for width because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiWidth = ji.getHeight();
			offset = 0;
			buf = new byte[w];

			// only the pixels that need to be flipped horizontally
			for (i = w; --i >= 0;)
				buf[i] = pixels[offset++];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			setChannel_RotateCW90(x, y, w, buf);
			break;

		case TIFTags.ORIENTATION_LEFTBOT: // 8
			// input y is distance relative to bottom row 0
			// using getWidth for height because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiHeight = ji.getWidth();
			y = jiHeight - y - 1;
			// using getHeight for width because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiWidth = ji.getHeight();
			buf = new byte[w];
			offset = w;	// to end of all data

			// only the pixels that need to be flipped horizontally
			for (i = 0; i < w; ++i)
				buf[i] = pixels[--offset];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			setChannel_RotateCW90(x, y, w, buf);
			break;
		}
	}

	/**
	 * takes one row of data that needs to be put into the JimiImage
	 * object rotate 90 clockwise.
	 **/
	final void setChannel_RotateCW90(
		int x, int y, int w, byte[] pixels) throws JimiException
	{
		// using getWidth() for height because width/height of output image ji have been
		// swapped for these cases.
		ji.setChannel(0, ji.getWidth() - y - 1, x, 1, w, pixels, 0, 1);

	}

	void setChannel_Oriented(int channel, int row, byte[] pixels) throws JimiException
	{
		switch (orientation)
		{
		case TIFTags.ORIENTATION_TOPLEFT:
			ji.setChannel(channel, row, pixels);
			break;

		case TIFTags.ORIENTATION_TOPRIGHT:
		case TIFTags.ORIENTATION_BOTRIGHT:
		case TIFTags.ORIENTATION_BOTLEFT:
			setChannel_Oriented(0, row, ji.getWidth(), pixels);
			break;

		case TIFTags.ORIENTATION_LEFTTOP:
		case TIFTags.ORIENTATION_RIGHTTOP:
		case TIFTags.ORIENTATION_RIGHTBOT:
		case TIFTags.ORIENTATION_LEFTBOT:
			// using getHeight() for width because width/height of output image ji have been
			// swapped for these cases.
			setChannel_Oriented(0, row, ji.getHeight(), pixels);
			break;
		}
	}

	/**
	 * This method takes into account the TIF Field "orientation" and
	 * places the image data into the JimiImage in the correct orientation.
	 **/
	void setChannel_Oriented(int x, int y, int w, int[] pixels) throws JimiException
	{
		int[] buf;
		int offset;
		int i;
		int row;
		int idx;
		int jiWidth;
		int jiHeight;

		switch (orientation)
		{
		case TIFTags.ORIENTATION_TOPLEFT: // 1
			ji.setChannel(x, y, w, 1, pixels, 0, 0);
			break;

		case TIFTags.ORIENTATION_TOPRIGHT: // 2	// flip horizontally
			jiWidth = ji.getWidth();
			offset = 0;
			buf = new int[w];

			// only the pixels that need to be flipped horizontally
			for (i = w; --i >= 0;)
				buf[i] = pixels[offset++];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			ji.setChannel(x, y, w, 1, buf, 0, 0);
			break;

		case TIFTags.ORIENTATION_BOTRIGHT: // 3	// flip horizontally and vertically
			// input y is distance relative to bottom row 0
			jiHeight = ji.getHeight();
			y = jiHeight - y - 1;
			jiWidth = ji.getWidth();
			buf = new int[w];
			offset = w;	// to end of all data

			// only the pixels that need to be flipped horizontally
			for (i = 0; i < w; ++i)
				buf[i] = pixels[--offset];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			ji.setChannel(x, y, w, 1, buf, 0, 0);
			break;

		case TIFTags.ORIENTATION_BOTLEFT: // 4	// flip vertically
			// input y is distance relative to bottom row 0
			jiHeight = ji.getHeight();
			y = jiHeight - y - 1;
			ji.setChannel(x, y, w, 1, pixels, 0, 0);
			break;

		case TIFTags.ORIENTATION_LEFTTOP: // 5
			// using getWidth() for height because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiHeight = ji.getWidth();
			y = jiHeight - y - 1;
			setChannel_RotateCW90(x, y, w, pixels);
			break;

		case TIFTags.ORIENTATION_RIGHTTOP: // 6
			setChannel_RotateCW90(x, y, w, pixels);
			break;

		case TIFTags.ORIENTATION_RIGHTBOT: // 7
			// using getHeight for width because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiWidth = ji.getHeight();
			offset = 0;
			buf = new int[w];

			// only the pixels that need to be flipped horizontally
			for (i = w; --i >= 0;)
				buf[i] = pixels[offset++];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			setChannel_RotateCW90(x, y, w, buf);
			break;

		case TIFTags.ORIENTATION_LEFTBOT: // 8
			// input y is distance relative to bottom row 0
			// using getWidth for height because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiHeight = ji.getWidth();
			y = jiHeight - y - 1;
			// using getHeight for width because image dimensions are 
			// swapped for output JimiIMage due to orientation.
			jiWidth = ji.getHeight();
			buf = new int[w];
			offset = w;	// to end of all data

			// only the pixels that need to be flipped horizontally
			for (i = 0; i < w; ++i)
				buf[i] = pixels[--offset];

			// now place the flipped pixel data into flipped position
			x = jiWidth - x - w;  
			setChannel_RotateCW90(x, y, w, buf);
			break;
		}
	}

	/**
	 * takes one row of data that needs to be put into the JimiImage
	 * object rotate 90 clockwise.
	 **/
	final void setChannel_RotateCW90(
		int x, int y, int w, int[] pixels) throws JimiException
	{
		// using getWidth() for height because width/height of output image ji have been
		// swapped for these cases.
		ji.setChannel(ji.getWidth() - y - 1, x, 1, w, pixels, 0, 1);
	}

	void setChannel_Oriented(int row, int[] pixels) throws JimiException
	{
		switch (orientation)
		{
		case TIFTags.ORIENTATION_TOPLEFT:
			ji.setChannel(row, pixels);
			break;

		case TIFTags.ORIENTATION_TOPRIGHT:
		case TIFTags.ORIENTATION_BOTRIGHT:
		case TIFTags.ORIENTATION_BOTLEFT:
			setChannel_Oriented(0, row, ji.getWidth(), pixels);
			break;

		case TIFTags.ORIENTATION_LEFTTOP:
		case TIFTags.ORIENTATION_RIGHTTOP:
		case TIFTags.ORIENTATION_RIGHTBOT:
		case TIFTags.ORIENTATION_LEFTBOT:
			// using getHeight() for width because width/height of output image ji have been
			// swapped for these cases.
			setChannel_Oriented(0, row, ji.getHeight(), pixels);
			break;
		}
	}

}

