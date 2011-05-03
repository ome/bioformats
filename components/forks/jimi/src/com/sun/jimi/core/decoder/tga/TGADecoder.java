package com.sun.jimi.core.decoder.tga;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataInputStream;

/**
 * Image decoder for image data stored in TGA file format.
 * Currently only the original TGA file format is supported. This is
 * because the new TGA format has data at the end of the file, getting
 * to the end of a file in an InputStream orient environment presents
 * several difficulties which are avoided at the moment.
 *
 * This is a simple decoder and is only setup to load a single image
 * from the input stream
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.3 $
 */
public class TGADecoder extends JimiDecoderBase
{
	/** jimi image to fill in */
	private AdaptiveRasterImage ji;

	/** underlying input stream */
	private InputStream in;

	/** little endian data input stream to read the tga data */
	private LEDataInputStream dIn;

	/** decoder return state */
	private int state;

	/** the file header */
	TGAFileHeader tgaFH;

	/** the color map */
	TGAColorMap tgaCM;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		this.in = in; 
		this.dIn = new LEDataInputStream(new BufferedInputStream(in));
		this.ji = ji;
		state = 0;
	}

	public boolean driveDecoder() throws JimiException
	{
		try
		{

			tgaFH = new TGAFileHeader(dIn);
			tgaCM = new TGAColorMap(dIn, tgaFH);
			initJimiImage();
			state |= INFOAVAIL;			// got image info

			decodeImage(dIn);
			state |= IMAGEAVAIL;		// got image
			ji.addFullCoverage();
		}
		catch (IOException e) {
			throw new JimiException("IO error reading TGA file");
		}

		return false;	// state change - Done actually.
	}

	public void freeDecoder() throws JimiException
	{
		in = null;
		dIn = null;
		ji = null;
	}

	public int getState()
	{
		// This does not access or reference the JimiImage therefore no lock is required
		return state;
	}

	public AdaptiveRasterImage getJimiImage()
	{
		return this.ji;
	}

	/**
	 * Initialise the JimiImage structure for loading of the image data.
	 * Sets the size and allocates the memory for the image.
	 * Creates the ColorModel for the image data as required for
	 * production of image through an image producer model.
	 */
	private void initJimiImage() throws JimiException
	{
		switch (tgaFH.imageType)
		{
		case TGAFileHeader.NO_IMAGE:
			throw new JimiException("TGADecoder no image found.");

		case TGAFileHeader.COLORMAPPED:
		case TGAFileHeader.UCOLORMAPPED:
			if (tgaFH.colorMapType == 0)
				throw new JimiException("TGADecoder color mapped images require a color map.");

			ji.setColorModel(new IndexColorModel(8, tgaCM.cmap.length/3, 
				tgaCM.cmap,	0, (tgaFH.colorMapEntrySize == 32 ? true : false)));
			break;

		case TGAFileHeader.BLACKWHITE:
		case TGAFileHeader.UBLACKWHITE:
			if (tgaFH.colorMapType != 0)
				throw new JimiException("TGADecoder gray scale should not have color map.");

			ji.setColorModel(new DirectColorModel(8, 0xFF, 0xFF, 0xFF));
			break;

		case TGAFileHeader.TRUECOLOR:
		case TGAFileHeader.UTRUECOLOR:	// pixelDepth 15, 16, 24 and 32
			// silently ignoring any color map data
			switch (tgaFH.pixelDepth)
			{
			case 16:
				ji.setColorModel(new DirectColorModel(16, (0x1F << 10), (0x1F << 5), 0x1F));
				break;

			case 24:
			case 32:
				ji.setColorModel(new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff));
				break;
			}
			break;
		}
		ji.setSize(tgaFH.width, tgaFH.height);
		ji.setPixels();
	}

	/**
	 * Identifies the image type of the tga image data and loads
	 * it into the JimiImage structure. This was taken from the
	 * prototype and modified for the new Jimi structure
	 *
	 * @author	Robin Luiten
	 * @date	03/Sep/1997
	 * @version	1.1
	 */
	private void decodeImage(LEDataInputStream dIn) throws JimiException, IOException
	{
		switch (tgaFH.imageType)
		{
		case TGAFileHeader.UCOLORMAPPED:
			decodeImageU8(dIn);
			break;

		case TGAFileHeader.UTRUECOLOR:	// pixelDepth 15, 16, 24 and 32
			switch (tgaFH.pixelDepth)
			{
			case 16:
				decodeRGBImageU16(dIn);
				break;
			case 24:
			case 32:
				decodeRGBImageU24_32(dIn);
				break;
			}
			break;

		case TGAFileHeader.UBLACKWHITE:
			decodeImageU8(dIn);
			break;

		case TGAFileHeader.COLORMAPPED:
			throw new JimiException("TGADecoder Compressed Colormapped images not supported");

		case TGAFileHeader.TRUECOLOR:
			throw new IOException("TGADecoder Compressed True Color images not supported");

		case TGAFileHeader.BLACKWHITE:
			throw new IOException("TGADecoder Compressed Grayscale images not supported");
		}
	}

	/**
	 * Load the image data body of a TGA file and fill in JimiImage.
	 * This assumes that there is 8 bits of data for each pixel which
	 * references the colour map.
	 */
	private void decodeImageU8(LEDataInputStream dIn) throws IOException, JimiException
	{
		int i;
		int y;
		byte[] scanLine = new byte[tgaFH.width];

		for (i = 0; i < tgaFH.height; ++i)
		{
			dIn.readFully(scanLine, 0, tgaFH.width);

			if (tgaFH.topToBottom)
				y = i;
			else
				y = tgaFH.height - i - 1; // range 0 to (tgaFH.height - 1)

			ji.setChannel(0, y, scanLine);
			setProgress((i * 100) / tgaFH.height);
		}
	}

	/**
	 * This assumes that the body is for a 24 bit or 32 bit for a
	 * RGB or ARGB image respectively.
	 */
	private void decodeRGBImageU24_32(LEDataInputStream dIn) throws IOException, JimiException
	{
		int i;	// row index
		int j;	// column index
		int y;	// output row index
		int raw;		// index through the raw input buffer
		int rawWidth = tgaFH.width * (tgaFH.pixelDepth / 8);
		byte[] rawBuf = new byte[rawWidth];
		int[] scanLine = new int[tgaFH.width];

		for (i = 0; i < tgaFH.height; ++i)
		{
			dIn.readFully(rawBuf, 0, rawWidth);

			if (tgaFH.pixelDepth == 24)	// no alpha
			{
				raw = 0;
				for (j = 0; j < tgaFH.width; ++j)
				{	// byte order is  Green, Blue, Red
					// repack from raw to scanLine
					scanLine[j] =	0xFF000000 + 
									((rawBuf[raw+2]&0xFF) << 16) +
									((rawBuf[raw+1]&0xFF) << 8) +
									(rawBuf[raw]&0xFF);
					raw += 3;
				}
			}
			else if (tgaFH.pixelDepth == 32)	// with alpha channel
			{
				raw = 0;
				for (j = 0; j < tgaFH.width; ++j)
				{	// byte order is  Green, Blue, Red, Alpha
					// repack from raw to scanLine
					scanLine[j] =	((rawBuf[raw+3]&0xFF) << 24) +
									((rawBuf[raw+2]&0xFF) << 16) +
									((rawBuf[raw+1]&0xFF) << 8) + 
									(rawBuf[raw]&0xFF);
					raw += 4;
				}
			}
			else
				throw new JimiException("TGADecoder pixelDepth not 24 or 32");

			if (tgaFH.topToBottom)
				y = i;
			else
				y = tgaFH.height - i - 1; // range 0 to (tgaFH.height - 1)

			ji.setChannel(y, scanLine);
			setProgress((i * 100) / tgaFH.height);
		}
	}


	/**
	 * This assumes that the body is for a 16 bit image with binary
	 * encoding of colors as follows  ARRRRRGG GGGBBBBB
	 */
	private void decodeRGBImageU16(LEDataInputStream dIn) throws IOException, JimiException
	{
		int i;	// row index
		int j;	// column index
		int y;	// output row index
		int[] scanLine = new int[tgaFH.width];

		for (i = 0; i < tgaFH.height; ++i)
		{
			// could optimise here by reading array of bytes then
			// swapping byte to fix to Little Endian two byte shorts.
			for (j = 0; j < tgaFH.width; ++j)
			{
				// format binary ARRRRRGG GGGBBBBB  - mask of the A bit.
				scanLine[j] = dIn.readUnsignedShort() & 0x7FFF;
			}

			if (tgaFH.topToBottom)
				y = i;
			else
				y = tgaFH.height - i - 1; // range 0 to (tgaFH.height - 1)

			ji.setChannel(y, scanLine);
			setProgress((i * 100) / tgaFH.height);
		}
	}


} // end of class TGADecoder

