/*
 * Copyright 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.decoder.gif;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.ImageConsumer;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataInputStream;
import com.sun.jimi.core.util.lzw.LZWDecompressor;
import com.sun.jimi.core.options.GIFOptions;

/**
 * Image decoder for image data stored in GIF file format.
 *
 * This is a simple decoder and is only setup to load a single image
 * from the input stream initially.
 *
 * The Aspect Ratio field of the header is not supported.
 * Plane text blocks are not supported.
 * Aplication extensions blocks are not supported.
 * Comments blocks are not supported.
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.3 $
 */
public class GIFDecoder extends JimiDecoderBase
{
	// various GIF field constants
	final static byte IMAGE_SEPERATOR = (byte)0x2C;
	final static byte EXTENSION_INTRODUCER = (byte)0x21;

	final static byte GRAPHIC_CONTROL_EXTENSION = (byte)0xF9;
	final static byte COMMENT_EXTENSION = (byte)0xFE;
	final static byte APPLICATION_EXTENSION = (byte)0xFF;
	final static byte PLAINTEXT_EXTENSION = (byte)0x01;

	final static byte TRAILER = (byte)0x3B;

	/** jimi image to fill in */
	private AdaptiveRasterImage ji_;

	/** underlying input stream */
	private InputStream in_;

	/** little endian data input stream to read the gif data */
	private LEDataInputStream dIn_;

	/** decoder return state */
	private volatile int state_;

	/** the file header */
	GIFFileHeader gifFH_;

	GIFGraphicExt gifGraphicExt_;

	protected int numberOfLoops_ = 1;

	/** 
	 * flag indicates that getNextImage has gotten upto loading next GIFImageDescriptor
	 * previously. So dont read the byte which contains the id IMAGE_SEPERATOR
	 **/
	boolean gotPrevImageSeperator_;

	/** contents of the first comment string found in GIF file **/
	String comment_;

	/**
	 * The base frame upon witch deltas are placed to return full frames.
	 * It is initialised to the first Image
	 **/
	AdaptiveRasterImage baseJI_;

	GIFImageDescriptor imageDescriptor_;

	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException
	{
		this.in_ = in; 
		this.dIn_ = new LEDataInputStream(in);
		this.ji_ = ji;
		state_ = MOREIMAGES;    // multi image is possible
		gifFH_ = null;
		gifGraphicExt_ = null;
		gotPrevImageSeperator_ = false;
		baseJI_ = null;
	}

	synchronized public boolean driveDecoder() throws JimiException
	{
		try
		{
			if (gifFH_ == null)
				gifFH_ = new GIFFileHeader(dIn_);


			getNextImage(dIn_, ji_);
			ji_.addFullCoverage();

			state_ |= INFOAVAIL | IMAGEAVAIL;
		}
		catch (IOException e)
		{
			state_ &= ~MOREIMAGES;	// indicate no more images
			throw new JimiException(e.getMessage());
		}
		catch (JimiException e)
		{
			state_ &= ~MOREIMAGES;	// indicate no more images
			throw new JimiException(e.getMessage());
		}

		return false;	// state change - Done actually.
	}

	void setOptions(AdaptiveRasterImage ji)
		throws JimiException
	{
		GIFOptions options = new GIFOptions();
		options.setInterlaced(imageDescriptor_.interlace);
		options.setNumberOfLoops(numberOfLoops_);
		if (gifGraphicExt_ != null) {
			options.setFrameDelay(gifGraphicExt_.delayTime);
			if ((gifGraphicExt_.packed & GIFGraphicExt.GCE_TRANSPARENT) != 0) {
				options.setTransparentIndex(gifGraphicExt_.colorIndex);
			}
			if ((imageDescriptor_.packed & GIFImageDescriptor.LOCAL_CT_FLAG) != 0) {
				options.setUseLocalPalettes(true);
			}
		}
		
		ji.setOptions(options);
	}

	public void freeDecoder() throws JimiException
	{
		in_ = null;
		dIn_ = null;
		ji_ = null;
	}

	public int getState()
	{
		// This does not access or reference the JimiImage therefore no lock is required
		return state_;
	}

	public AdaptiveRasterImage getJimiImage()
	{
		return ji_;
	}

	public int getNumberOfImages()
	{
		return UNKNOWNCOUNT;
	}

	public void getNextImage(LEDataInputStream in, AdaptiveRasterImage ji) throws JimiException, IOException
	{
		byte b;
		boolean imageDecoded = false; // indicates have decoded image
		AdaptiveRasterImage loadingJI = createAdaptiveRasterImage();
		int len;

		while (true)
		{
			if (gotPrevImageSeperator_)	// if inside loop
				b = IMAGE_SEPERATOR;
			else
	    		b = in.readByte();

			switch (b)
			{
			case IMAGE_SEPERATOR:
				if (imageDecoded)	// allready decode image - next is waiting now.
				{
					gotPrevImageSeperator_ = true;
					return;
				}
				GIFImageDescriptor gifImageD = new GIFImageDescriptor(in);
				imageDescriptor_ = gifImageD;

				initJimiImage(gifImageD, loadingJI);
				decodeImage(in, gifImageD, loadingJI);
				loadingJI.addFullCoverage();
				addImageDelta(gifImageD, loadingJI, ji_);
				gifGraphicExt_ = null;
				imageDecoded = true;
				gotPrevImageSeperator_ = false;
				break;
				// do not return from method here we want to read forward
				// until the next IMAGE_SEPERATOR

			case TRAILER:				// all done.
				state_ &= ~MOREIMAGES;	// indicate no more images
				gotPrevImageSeperator_ = false;
				gifGraphicExt_ = null;
				return;

			case EXTENSION_INTRODUCER:
				b = dIn_.readByte();		// get Lable on Extensions

				// handle label
				switch (b)
				{
				case PLAINTEXT_EXTENSION:	// ignored
				case APPLICATION_EXTENSION: // ignored
					int blockSize = in.readByte();
					byte[] block = new byte[blockSize];
					in.readFully(block);
					String blockString = new String(block);
					// netscape extension block?
					if (blockString.equals("NETSCAPE2.0")) {
						int subBlockSize = in.read();
						byte[] subBlock = new byte[subBlockSize];
						for (int i = 0; i < subBlock.length; i++) {
							subBlock[i] = (byte)in.read();
						}
						// check for number of loops extension
						if ((subBlockSize == 3) && (subBlock[0] == 1)) {
							numberOfLoops_ = (((int)subBlock[2]) & 0xff) << 8;
							numberOfLoops_ |= ((int)subBlock[1]) & 0xff;
							if (numberOfLoops_ != 0) {
								numberOfLoops_++;
							}
						}
					}
					while ((len = in.readUnsignedByte()) != 0)	// skip data blocks
						in.skip(len);
					break;
				case COMMENT_EXTENSION:
					if (comment_ == null)	// only pickup first comment
					{
						byte[] tmp;
						while ((len = in.readUnsignedByte()) != 0)	// skip data blocks
						{
							tmp = new byte[len];
							in.readFully(tmp);
							comment_ = comment_ + new String(tmp, 0); // 1.0 compatibility	
						}					
					}
					else
					{
						// skip comment data
    					while ((len = in.readUnsignedByte()) != 0)
    						in.skip(len);
    				}
					break;

				case GRAPHIC_CONTROL_EXTENSION:
					gifGraphicExt_ = new GIFGraphicExt(in);
					break;
				}
				break;

			default:
			}
		}
	}


	/** 
	 * the disposal_ method to be used to remove the delta from the 
	 * previous frame before adding current delta.
	 **/
	int disposal_ = 0;
	int xOffset_ = 0;		// x offset for delta in pixels
	int yOffset_ = 0;		// y offset for delta in pixels
	int dWidth_ = 0;		// width delta in pixels
	int dHeight_ = 0;		// height of delta in pixels

	byte[] prevSaved_;		// data saved under previous applied delta

	/** the image delay for produced frame. set as property on JimiImage **/
	int imageDelay_ = 0;

	// support routine for addImageDelta
	void disposePreviousDelta() throws JimiException
	{
		int row;
		byte[] buf;
		
		// use disposal method to remove previous delta applied
		switch (disposal_)
		{
		case GIFGraphicExt.GCE_DISP_NOTSPECIFIED:
		case GIFGraphicExt.GCE_DISP_LEAVE:
			// do nothing
			break;

		case GIFGraphicExt.GCE_DISP_BACKGROUND:
			// clear previous delta area to background
			buf = new byte[dWidth_];	// default to 0 background 
			/*
 			if ((gifFH_.packed & GIFFileHeader.CT_GLOBAL) != 0)
 				for (int i = dWidth_; --i >= 0; )
 					buf[i] = gifFH_.backgroundColor;
 
			*/
			if (gifGraphicExt_ != null &&
				((gifGraphicExt_.packed & GIFGraphicExt.GCE_TRANSPARENT) != 0)) {
				for (int i = 0; i < buf.length; i++) {
					buf[i] = (byte)gifGraphicExt_.colorIndex;
				}
			}
			if ((gifFH_.packed & GIFFileHeader.CT_GLOBAL) != 0) {
				if (gifGraphicExt_ != null &&
					((gifGraphicExt_.packed & GIFGraphicExt.GCE_TRANSPARENT) != 0)) {
					for (int i = 0; i < buf.length; i++) {
						buf[i] = (byte)gifGraphicExt_.colorIndex;
					}
				}
				else {
					for (int i = dWidth_; --i >= 0; )
						buf[i] = gifFH_.backgroundColor;
				}
			}

			for (row = yOffset_; row < yOffset_ + dHeight_; ++row) {
				baseJI_.setChannel(0, xOffset_, row, dWidth_, 1, buf, 0, 0);
			}
			break;

		case GIFGraphicExt.GCE_DISP_PREVGRAPHIC:
			// restore saved background to delta area
			if (prevSaved_ == null)
				throw new RuntimeException("Internal GIFDecoder Error");
			baseJI_.setChannel(0, xOffset_, yOffset_, dWidth_, dHeight_, prevSaved_, 0, dWidth_);
			break;
		}
	}

	// support routine for addImageDelta
	void saveForNextDelta() throws JimiException
	{
		int row;
		byte[] buf;

		// check if we need to save anything before we apply new delta
		switch (disposal_)
		{
		case GIFGraphicExt.GCE_DISP_NOTSPECIFIED:
			// do nothing
			break;
		case GIFGraphicExt.GCE_DISP_LEAVE:
			// do nothing
			break;
		case GIFGraphicExt.GCE_DISP_BACKGROUND:
			// do nothing
			break;

		case GIFGraphicExt.GCE_DISP_PREVGRAPHIC:
			// save current image area delta would affect
			prevSaved_ = new byte[dWidth_ * dHeight_];
			int savedOffset = 0;
			buf = new byte[baseJI_.getWidth()];
			for (row = yOffset_; row < (yOffset_ + dHeight_); ++row)
			{
				baseJI_.getChannel(0, row, buf, 0);
				System.arraycopy(buf, xOffset_, prevSaved_, savedOffset, dWidth_);
				savedOffset += dWidth_;
			}
			break;
		}
	}


	/**
	 * This method adds loaded deltas "in" and fills in out with the
	 * current full frame of GIF. It works in single image or multi image
	 * cases.
	 *
	 * @param gifID GIFIMageDescriptor holding offsets, width, height etc.
	 * @param in the JimiImage that was loaded from the GIF file. The Delta
	 * @param out the JimiImage to be returned as this frame of multi image gif
	 * @exception JimiException thrown if operations on in and out JimiImages cause them
	 **/
	void addImageDelta(GIFImageDescriptor gifID, AdaptiveRasterImage in, AdaptiveRasterImage out) throws JimiException
	{
		int row;
		byte[] buf;

		setOptions(out);

		boolean first = baseJI_ == null;

		if (baseJI_ == null)	// no base yet.
		{
			// set width/height based on screen descriptor
			// use color model from first frame of image.
			baseJI_ = createAdaptiveRasterImage(gifFH_.screenWidth, gifFH_.screenHeight, in.getColorModel());

			setOptions(baseJI_);
			baseJI_.setPixels();		// allocate

			// force image to background color if global color table
			if ((gifFH_.packed & GIFFileHeader.CT_GLOBAL) != 0)
				baseJI_.setChannel(gifFH_.backgroundColor);

			// set coverage for all data due to way base JI is used for deltas
			baseJI_.addFullCoverage();
		}
		
		disposePreviousDelta();

		// examine GIFGraphicExt for information
		if (gifGraphicExt_ != null)
		{
			// pickup if delay set and the disposal method if set.
			disposal_   = (gifGraphicExt_.packed & GIFGraphicExt.GCE_DISPOSAL) >> GIFGraphicExt.GCE_DISPOSAL_BITOFFSET;
			imageDelay_ = gifGraphicExt_.delayTime;
		}

		// Get delta settings for saveForNextDelta()
		xOffset_ = gifID.left;
		yOffset_ = gifID.top;
		dWidth_ = gifID.width;
		dHeight_ = gifID.height;
		saveForNextDelta();

		// apply new delta
		buf = new byte[dWidth_];
		int rowOut = yOffset_;
		for (int rowIn = 0; rowIn < dHeight_; ++rowIn)
		{
			in.getChannel(0, rowIn, buf, 0);
			// handle transparency
			if (first || (gifGraphicExt_ == null) || (gifGraphicExt_.packed & GIFGraphicExt.GCE_TRANSPARENT) == 0) {
				baseJI_.setChannel(0, xOffset_, rowOut, dWidth_, 1, buf, 0, 0);
			}
			else {
				int transIndex = gifGraphicExt_.colorIndex;
				int lastIndex = 0;
				boolean hasPixels = false;

				for (int currentIndex = 0; currentIndex < buf.length; currentIndex++) {
					if (buf[currentIndex] == transIndex || (currentIndex == buf.length - 1)) {
						if (hasPixels) {
							baseJI_.setChannel(0, xOffset_ + lastIndex, rowOut, currentIndex - lastIndex,
											   1, buf, lastIndex, 0);
							hasPixels = false;
						}
					}
					else {
						if (!hasPixels) {
							lastIndex = currentIndex;
							hasPixels = true;
						}
					}
				}
			}
			// no coverage set here becuase the baseJI_ is always marked fully covered
			++rowOut;
		}

		// ensure that ColorModel is set on baseJI_ to that of in.
		baseJI_.setColorModel(in.getColorModel());

		// set any properties for this image
		if (comment_ != null)
		{
			comment_ = null;
		}

		// copy baseJI_ to out.
		buf = new byte[baseJI_.getWidth()];
		out.setSize(baseJI_.getWidth(), baseJI_.getHeight());
		out.setColorModel(baseJI_.getColorModel());
		setOptions(out);
		out.setPixels();
		for (row = 0; row < baseJI_.getHeight(); ++row)
		{
			baseJI_.getChannel(0, row, buf, 0);
			out.setChannel(0, row, buf);
			// coverage not set here, because full coverage is set for entire image
			// at a higher level in calling stack.
		}
	}

	/**
	 * Initialise the JimiImage structure for loading of the image data.
	 * Sets the size and allocates the memory for the image.
	 * Creates the ColorModel for the image data as required for
	 * production of image through an image producer model.
	 */
	private void initJimiImage(GIFImageDescriptor gifImageD, AdaptiveRasterImage ji) throws JimiException
	{
		ColorModel cm;
		GIFColorTable ct;
		int colorTableNumBits;

		if ((gifImageD.packed & GIFImageDescriptor.LOCAL_CT_FLAG) != 0)
		{
			ct = gifImageD.colorTable;
			colorTableNumBits = gifImageD.colorTableNumBits;
		}
		else
		{
			ct = gifFH_.colorTable;
			colorTableNumBits = gifFH_.colorTableNumBits;
		}


		if (gifGraphicExt_ != null &&
			((gifGraphicExt_.packed & GIFGraphicExt.GCE_TRANSPARENT) != 0))
		{
			cm = new IndexColorModel(8,	ct.red.length,
					ct.red, ct.green, ct.blue,
					gifGraphicExt_.colorIndex);	// transparency
		}
		else
		{
			cm = new IndexColorModel(8,	ct.red.length,
					ct.red, ct.green, ct.blue);
		}

		ji.setColorModel(cm);
		ji.setSize(gifImageD.width, gifImageD.height);
		setOptions(ji);
		ji.setPixels();
	}

	/**
	 * Loads decoded image data from input stream to jimi image object
	 */
	private void decodeImage(LEDataInputStream in, GIFImageDescriptor gifID, AdaptiveRasterImage ji) throws JimiException, IOException
	{
		int row;
        int height = gifID.height;
		int codeSize = in.readByte();
		LZWDecompressor lzwD = new LZWDecompressor(in, codeSize, false);// GIF Mode
		byte[] buf = new byte[gifID.width];
        
		int rowProgress = 0;

		if (gifID.interlace)
		{
			// interlaced so let the JimiImage know that the data will be sent in multiple passes
			ji.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEFRAME);
			for (row = 0; row < height; row += 8)
			{
				lzwD.decompress(buf);
				ji.setChannel(0, row, buf);
				setProgress((rowProgress++ * 100) / gifID.height);
			}

			for (row = 4; row < height; row += 8)
			{
				lzwD.decompress(buf);
				ji.setChannel(0, row, buf);
				setProgress((rowProgress++ * 100) / gifID.height);
			}

			for (row = 2; row < height; row += 4)
			{
				lzwD.decompress(buf);
				ji.setChannel(0, row, buf);
				setProgress((rowProgress++ * 100) / gifID.height);
			}

			for (row = 1; row < height; row += 2)
			{
				lzwD.decompress(buf);
				ji.setChannel(0, row, buf);
				setProgress((rowProgress++ * 100) / gifID.height);
			}
			setProgress(100);
		}
		else
		{
        	for (row = 0; row < height; ++row)
			{
				lzwD.decompress(buf);
				ji.setChannel(0, row, buf);
				setProgress((row * 100) / height);
			}
		}

		// completed decompression
		lzwD.gifFinishBlocks();		// ensure past end of GIF data blocks
	}

	/**
	 */
	public int getCapabilities()
	{
		return MULTIIMAGE;		// no capabilities
	}

	/**
	 **/
	public void setJimiImage(AdaptiveRasterImage ji)
	{
		ji_= ji;
	}

	/**
	 * This needs to decompress image and add to history because of the
	 * way that multi frame GIF is created.
	 **/
	public void skipImage() throws JimiException
	{
		AdaptiveRasterImage ji = createAdaptiveRasterImage();
	    try
	    {
			if (gifFH_ == null)
				gifFH_ = new GIFFileHeader(dIn_);

    	    getNextImage(dIn_, ji);
    	}
    	catch (IOException e)
    	{
    	    throw new JimiException(e.getMessage());
    	}
	}


	public boolean usesChanneledData()
	{
		return true;
	}

	public boolean mustWaitForOptions()
	{
		return true;
	}

} // end of class GIFDecoder
