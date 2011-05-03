/*
Tabstops	4
History
18/Feb/1998	RL
	Created this based on PNGImageProducer by Jason Marshal.
	Modifed to use the Jimi architecture. 
19/Mar/1998	RL
	Renabled Transparency chunk support for loading of palette PNG images
	Incorrectly reported TRNSE before PLTE error for Grayscale and RGB images.
	When setting the default value for transparency for palette entries which
	do not have transparency values set code was starting at palette length index
	instead of chunkLength
07/Apr/1998	RL
	IndexColorModel constructor first parameter forced to 8. This has same
	affect anyway and allows it to operate properly in JDK 1.2 beta 3.
*/
package com.sun.jimi.core.decoder.png;

//
// Copyright (c) 1997, Jason Marshall.  All Rights Reserved
//
// The author makes no representations or warranties regarding the suitability,
// reliability or stability of this code.  This code is provided AS IS.  The
// author shall not be liable for any damages suffered as a result of using,
// modifying or redistributing this software or any derivitives thereof.
// Permission to use, reproduce, modify and/or (re)distribute this software is
// hereby granted.

/**
 * @(#)PNGImageProducer.java	0.88 97/4/14 Jason Marshall
 **/

import java.awt.image.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.zip.*;
//import com.sun.jimi.util.zip.*;

import com.sun.jimi.core.compat.*;

import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.util.JimiUtil;

/**
 * ImageProducer which produces an Image from a PNG image
 * Note that making this implement the Runnable interface it but one way to
 * handle the problems inherent with asynchronous image production.  This class
 * is provided more as a proof of concept, and sample implementation of the PNG
 * decoder in Java.
 *
 * @version	$Revision: 1.3 $
 * @author 	Jason Marshall
 */
public class PNGReader
{
	private int dataWidth;
	private int dataHeight;
	private int width = -1;
	private int height = -1;
	private int sigmask = 0xffff;
	private ColorModel model;
	private Object pixels;
	private int ipixels[];
	private byte bpixels[];
	private Hashtable properties;
	private Vector theConsumers;
	private boolean multipass;
	private boolean complete;
	private boolean error;
	
	// TODO: make private when inner class created -JDM
	InputStream underlyingStream_;
	// TODO: make private when inner class created -JDM
	DataInputStream inputStream;
	
	private Thread controlThread;
	private boolean infoAvailable = false;
	private boolean completePasses = false;
	
	//TODO: set from system properties -JDM
	private int updateDelay = 750;
	
	// Image decoding state variables
	
	private boolean headerFound = false;
	private int compressionMethod = -1;
	private int depth = -1;
	private int colorType = -1;
	private int filterMethod = -1;
	private int interlaceMethod = -1;
	private int pass;
	private byte palette[];
	private boolean transparency;
	
	// TODO: make private when innerclass created -JDM
	int chunkLength;
	// TODO: make private when innerclass created -JDM
	int chunkType;
	// TODO: make private when innerclass created -JDM
	boolean needChunkInfo = true;
	
	static final int CHUNK_bKGD = 0x624B4744;   // "bKGD"
	static final int CHUNK_cHRM = 0x6348524D;   // "cHRM"
	static final int CHUNK_gAMA = 0x67414D41;   // "gAMA"
	static final int CHUNK_hIST = 0x68495354;   // "hIST"
	static final int CHUNK_IDAT = 0x49444154;   // "IDAT"
	static final int CHUNK_IEND = 0x49454E44;   // "IEND"
	static final int CHUNK_IHDR = 0x49484452;   // "IHDR"
	static final int CHUNK_PLTE = 0x504C5445;   // "PLTE"
	static final int CHUNK_pHYs = 0x70485973;   // "pHYs"
	static final int CHUNK_sBIT = 0x73424954;   // "sBIT"
	static final int CHUNK_tEXt = 0x74455874;   // "tEXt"
	static final int CHUNK_tIME = 0x74494D45;   // "tIME"
	static final int CHUNK_tRNS = 0x74524E53;   // "tIME"
	static final int CHUNK_zTXt = 0x7A545874;   // "zTXt"
	
	static final int startingRow[]  =  { 0, 0, 0, 4, 0, 2, 0, 1 };
	static final int startingCol[]  =  { 0, 0, 4, 0, 2, 0, 1, 0 };
	static final int rowInc[]       =  { 1, 8, 8, 8, 4, 4, 2, 2 };
	static final int colInc[]       =  { 1, 8, 8, 4, 4, 2, 2, 1 };
	static final int blockHeight[]  =  { 1, 8, 8, 4, 4, 2, 2, 1 };
	static final int blockWidth[]   =  { 1, 8, 4, 4, 2, 2, 1, 1 };
	
	/** ColorType definitions **/
	static final int CT_PALETTE = 1;
	static final int CT_COLOR = 2;
	static final int CT_ALPHA = 4;
	
	/** Used values of Color type to values for image type **/
	static final int IT_GRAYSCALE = 0;
	static final int IT_RGB = CT_COLOR;	// 2
	static final int IT_PALETTE = CT_COLOR | CT_PALETTE; // 3
	static final int IT_GRAYALPHA = CT_ALPHA; // 4
	static final int IT_RGBA = CT_COLOR | CT_ALPHA; // 6

	/** the object to fill data into **/
	AdaptiveRasterImage ji_;

	/**
	 *
	 **/
	PNGReader(DataInputStream in, AdaptiveRasterImage ji)
	{
		underlyingStream_ = in;	// required for IDATEnumeration
		inputStream = in;
		ji_ = ji;
	}
	
	/**
	 * Loads image data into JimiImage object
	 **/
	void decodeImage() throws JimiException
	{
		try
		{
			// Verify signature
			handleSignature();
			while (!complete && !error)
				handleChunk();
		}
		catch (IOException e)
		{
			throw new JimiException("IO Error");
		}
	}
	
	private void handleSignature() throws JimiException, IOException
	{
		if ((inputStream.read() != 137) ||
				(inputStream.read() != 80) ||
				(inputStream.read() != 78) ||
				(inputStream.read() != 71) ||
				(inputStream.read() != 13) ||
				(inputStream.read() != 10) ||
				(inputStream.read() != 26) ||
				(inputStream.read() != 10))
		{
			throw new JimiException("Not a PNG File");
		}
	}
	
	private void handleChunk() throws JimiException, IOException
	{
		if (needChunkInfo)
		{
			chunkLength = inputStream.readInt();
			chunkType = inputStream.readInt();
			needChunkInfo = false;
		}
		
		// Guarantee that chunks can't overread their bounds
		/*inputStream = new DataInputStream(
			new MeteredInputStream(underlyingStream,
			chunkLength));
			*/
		switch (chunkType)
		{
		 case CHUNK_bKGD:
			 handlebKGD();
			 break;
		 case CHUNK_cHRM:
			 handlecHRM();
			 break;
		 case CHUNK_gAMA:
			 handlegAMA();
			 break;
		 case CHUNK_hIST:
			 handlehIST();
			 break;
		 case CHUNK_IDAT:
			 handleIDAT();
			 break;
		 case CHUNK_IEND:
			 handleIEND();
			 break;
		 case CHUNK_IHDR:
			 handleIHDR();
			 break;
		 case CHUNK_pHYs:
			 handlepHYs();
			 break;
		 case CHUNK_PLTE:
			 handlePLTE();
			 break;
		 case CHUNK_sBIT:
			 handlesBIT();
			 break;
		 case CHUNK_tEXt:
			 handletEXt();
			 break;
		 case CHUNK_tIME:
			 handletIME();
			 break;
		 case CHUNK_tRNS:
			 handletRNS();
			 break;
		 case CHUNK_zTXt:
			 handlezTXt();
			 break;
		 default:
//			 System.err.println("unrecognized chunk type " +
//													Integer.toHexString(chunkType) + ". skipping");
			 inputStream.skip(chunkLength);
		}
		//inputStream = new DataInputStream(underlyingStream);
		int crc = inputStream.readInt();
		needChunkInfo = true;
	}
	
	private void handleIHDR() throws JimiException, IOException
	{
		if (headerFound)
			throw new JimiException("Extraneous IHDR chunk encountered.");
		if (chunkLength != 13)
			throw new JimiException("IHDR chunk length wrong: " + chunkLength);
		dataWidth = inputStream.readInt();
		dataHeight = inputStream.readInt();
		depth = inputStream.read();
		colorType = inputStream.read();
		compressionMethod = inputStream.read();
		filterMethod = inputStream.read();
		interlaceMethod = inputStream.read();
		headerFound = true;
// Trace
// System.err.println("colorType " + colorType);
	}
	
	private void handlePLTE() throws IOException
	{
		if (colorType == IT_PALETTE)
		{
			palette = new byte[chunkLength];
			inputStream.readFully(palette);
		}
		else
		{
			// Ignore suggested palette
			inputStream.skip(chunkLength);
		}
	}
	
	private void handletRNS() throws JimiException, IOException
	{
		int chunkLen = chunkLength;
		
		switch (colorType)
		{
		 case IT_PALETTE:
			 if (palette == null)
				 throw new JimiException("tRNS chunk encountered before pLTE");
			 int len = palette.length;
			 transparency = true;
			 int transLength = len/3;
			 byte[] trans = new byte[transLength];
			 inputStream.readFully(trans, 0, chunkLength);
			 
			 // set to fully opaque any part of transparency array not set by
			 // the data in the transparency chunk
			 for (int i = chunkLength; i < transLength; i++)
				 trans[i] = (byte) 0xff;
			 
			 byte[] newPalette = new byte[len + transLength];
			 for (int i = newPalette.length; i > 0;)
			 {
				 newPalette[--i] = trans[--transLength];
				 newPalette[--i] = palette[--len];
				 newPalette[--i] = palette[--len];
				 newPalette[--i] = palette[--len];
			 }
			 palette = newPalette;
			 break;
			 
		 default:
			 inputStream.skip(chunkLength);
		}
	}
	
	private void handlezTXt() throws IOException
	{
		inputStream.skip(chunkLength);
	}
	
	private void handlebKGD() throws IOException
	{
		inputStream.skip(chunkLength);
	}
	
	private void handlecHRM() throws IOException
	{
		inputStream.skip(chunkLength);
	}
	
	private void handlegAMA() throws IOException
	{
		inputStream.skip(chunkLength);
	}
	
	private void handlehIST() throws IOException
	{
		inputStream.skip(chunkLength);
	}
	
	private void handleIEND() throws IOException
	{
		complete = true;
	}
	
	private void handlepHYs() throws IOException
	{
		/*  Not yet implemented -JDM
        int w = inputStream.readInt();
        int h = inputStream.readInt();
        inputStream.read();
        width = dataWidth * w / h;
        height = dataHeight * h / w;
        */
		inputStream.skip(chunkLength);
	}
	
	private void handlesBIT() throws IOException
	{
		inputStream.skip(chunkLength);
	}
	
	private void handletEXt() throws IOException
	{
		inputStream.skip(chunkLength);
	}
	
	private void handletIME() throws IOException
	{
//		if (chunkLength != 7)
//			System.err.println("tIME chunk length incorrect: " + chunkLength);
		inputStream.skip(chunkLength);
	}
	
	/**
	 * Handle the image data chunks.  Note that sending info to ImageConsumers
	 * is delayed until the first IDAT chunk is seen.  This allows for any
	 * changes in ancilliary chunks that may alter the overall properties of
	 * the image, such as aspect ratio altering the image dimensions.  We
	 * assume that all ancilliary chunks which have these effects will appear
	 * before the first IDAT chunk, and once seen, image properties are set in
	 * stone.
	 **/
	private void handleIDAT() throws JimiException, IOException
	{
		if (!infoAvailable)
		{
			if (height == -1 || width == -1)
			{
				width = dataWidth;
				height = dataHeight;
			}
			else
				throw new JimiException("IDAT before IHDR");
			
			createColorModel();
			ji_.setSize(width, height);
			ji_.setColorModel(model);
			ji_.setPixels();
			
			if (interlaceMethod != 0) {
				multipass = true;
//				ji_.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.SINGLEFRAME);
			}
			infoAvailable = true;
		}
		readImageData();
	}
	
	/**
	 * Read Image data in off of a compression stream
	 **/
	private void readImageData() throws JimiException, IOException
	{
		InputStream dataStream = new SequenceInputStream(new IDATEnumeration(this));
/*
		DataInputStream dis = new DataInputStream(
			new BufferedInputStream(
				new InflaterInputStream(dataStream,
																new Inflater())));
*/
		DataInputStream dis = new DataInputStream(
				new InflaterInputStream(dataStream,
//																new Inflater(true)));
																new Inflater()));
		int bps = 0;
		int filterOffset;
		
		switch (colorType)
		{
		 case IT_GRAYSCALE:
		 case IT_PALETTE:
			 bps = depth;
			 break;
			 
		 case IT_RGB:
			 bps = 3 * depth;
			 break;
			 
		 case IT_GRAYALPHA:
			 bps = depth<<1;
			 break;
			 
		 case IT_RGBA:
			 bps = depth<<2;
			 break;
		}
		
		filterOffset = (bps + 7)>>3;
		
		// Coverage tracking in JimiImage is very very expensive
		// for single pixel writes therefore disable it here
		// for multipass until we get to writing whole rows at a time
//		if (multipass)
		
		
		for (pass = (multipass ? 1 : 0); pass < 8; pass++)
		{
			int pass = this.pass;
			int rInc = rowInc[pass];
			int cInc = colInc[pass];
			int sCol = startingCol[pass];
			// val number of pixels to be read for row in current pass
			int val = ((dataWidth - sCol + cInc - 1) / cInc);
			int samples = val * filterOffset;
			int rowSize = ((val * bps) + 7) >> 3;
			int sRow = startingRow[pass];
			
			if ((dataHeight <= sRow) || (rowSize == 0))
				continue;
			
			int sInc = rInc * dataWidth;
			
			byte inbuf[] = new byte[rowSize];
			int pix[] = new int[rowSize];
			int upix[] = null;
			int temp[] = new int[rowSize];
			
			// next Y value and number of rows to report to sendPixels
			int nextY = sRow;
			int rows = 0;
//            int rowStart = sRow * dataWidth;
			
			for (int y = sRow; y < dataHeight; y += rInc) // , rowStart += sInc)
			{
// next line was for the blockfill and animated display of deinterlace loading
//                rows += rInc;
				
				int rowFilter = dis.read();
				dis.readFully(inbuf);
				
				if (!filterRow(inbuf, pix, upix, rowFilter, filterOffset))
					throw new JimiException("Unknown filter type: " + rowFilter);
				
//				if (pass == 7)	// renable coverage tracking
//				{
				
				// Also add coverage for the row previous to the current
				// one because the coverage tracking was disabled for the
				// previous row due to inefficient tracking for single pixels
//					ji_.addCoverage(y - 1);
//				}
				
				insertJimiPixels(pix, samples, sCol, y);
// disable blockfill its pretty expensive and not worth converting
//                if (multipass && (pass < 6))
//                    blockFill(rowStart);
				upix = pix;
				pix = temp;
				temp = upix;
			}
			if (!multipass)
				break;
		}
		// pick up the case where the last line of image is interlace decode
		// and mark it as being covered because the internal check only covers
		// interlace decoded lines as the next full line is written.
//		if (multipass && (dataHeight & 0x1) != 0)
//		{
		
//			ji_.addCoverage(dataHeight - 1);
//		}
		
		while (dis.read() != -1);
//		while(dis.read() != -1)
//			System.err.println("Leftover data encountered.");
	}
	
	
	/**
	 * This method identifies cases of writing data to a JimiImage that can
	 * be heavilly optimised over the per pixel writes done in the general
	 * case. This method isolates the cases where a whole row at a time can 
	 * be written to JimiImage and performs it as a row write.
	 **/
	private void insertJimiPixels(int pix[], int samples, int col, int row) throws JimiException
	{
		if (startingCol[pass] == 0 && colInc[pass] == 1)
		{
// Debug Trace comment out for working case useful in future maybe
//		    System.out.println("pass " + pass 
//		                        + " startingCol[pass] "  + startingCol[pass]
//		                        + " colInc[pass] " + colInc[pass]
//		                        + " samples " + samples);
			
			switch (colorType)
			{
			 case IT_GRAYSCALE:	// int pixels - depths 1 to 16.
			 {
				 byte[] buf2;
				 if (depth == 16)	// 2 bytes data - keep high
				 {
					 buf2 = new byte[width];
					 int l = pix.length;
					 for (int k = width; --k >= 0; )
						 buf2[k] = (byte)pix[l-=2];
					 ji_.setChannel(3, row, buf2);   // 3 = channel for colormodel
//					 ji_.addCoverage(row);
				 }
				 else // depth 1 to 8
				 {
					 buf2 = new byte[pix.length];
					 for (int k = pix.length; --k >= 0; )
						 buf2[k] = (byte)pix[k];
					 
					 if (depth == 8)
					 {
						 ji_.setChannel(3, row, buf2);   // 3 = channel for colormodel
//						 ji_.addCoverage(row);
					 }
					 else
					 {
						 byte[] buf = new byte[width];
						 JimiUtil.expandPixels(depth, buf2, buf, buf.length);
						 ji_.setChannel(3, row, buf);   // 3 = channel for colormodel
//						 ji_.addCoverage(row);
					 }
				 }
				 break;
			 }
			 case IT_GRAYALPHA:
			 {
				 int[] buf2 = new int[width];
				 int l;
				 if (depth == 16)	// 4 bytes - 2 of alpha then gray
				 {
					 l = pix.length;
					 for (int k = width; --k >= 0; )
						 buf2[k] = pix[l-=2] | (pix[l-=2] << 8);
					 ji_.setChannel(row, buf2);
//					 ji_.addCoverage(row);
				 }
				 else // (depth == 8) // 2 bytes - 1 of alpha then gray
				 {
					 l = pix.length;
					 for (int k = width; --k >= 0; )
						 buf2[k] = pix[--l] | (pix[--l] << 8);
					 ji_.setChannel(row, buf2);
//					 ji_.addCoverage(row);
				 }
				 break;
			 }
			 case IT_RGB:	// assume int pixels
			 {
				 int[] buf = new int[width];
				 int j;
				 int i;
				 if (depth == 8)
				 {
					 j = pix.length;
					 for (i = width; --i >= 0; )
						 buf[i] = pix[--j] | (pix[--j]<<8) | (pix[--j]<<16);
					 ji_.setChannel(row, buf);
//					 ji_.addCoverage(row);
				 }
				 else // (depth == 16)
				 {
					 j = pix.length;
					 for (i = width; --i >= 0; )
						 buf[i] = pix[j-=2] | (pix[j-=2]<<8) | (pix[j-=2]<<16);
					 ji_.setChannel(row, buf);
//					 ji_.addCoverage(row);
				 }
				 break;
			 }
			 
			 case IT_PALETTE:	// assume byte pixels
			 {
				 byte[] buf2 = new byte[pix.length];
				 for (int k = pix.length; --k >= 0; )
					 buf2[k] = (byte)pix[k];
				 
				 if (depth == 8)
				 {
					 ji_.setChannel(0, row, buf2);
//					 ji_.addCoverage(row);
				 }
				 else // depth 1, 2, 4
				 {
					 byte[] buf = new byte[width];
					 JimiUtil.expandPixels(depth, buf2, buf, buf.length);
					 ji_.setChannel(0, row, buf);
//					 ji_.addCoverage(row);
				 }
				 break;
			 }
			 case IT_RGBA:		// assume int pixels
			 {
				 int j;
				 int i;
				 if (depth == 8)
				 {
					 int[] buf = new int[width];
					 j = pix.length;
					 for (i = width; --i >= 0; )
						 buf[i] = (pix[--j] << 24) | pix[--j] | (pix[--j]<<8) | (pix[--j]<<16);
					 ji_.setChannel(row, buf);
//					 ji_.addCoverage(row);
				 }
				 else // (depth == 16)
				 {
					 int[] buf = new int[width];
					 j = pix.length;
					 for (i = width; --i >= 0; )
						 buf[i] = (pix[j-=2]<<24) | pix[j-=2] | (pix[j-=2]<<8) | (pix[j-=2]<<16);
					 ji_.setChannel(row, buf);
//					 ji_.addCoverage(row);
				 }
				 break;
			 }
			}
		}
		else
			insertPixels(pix, samples, col, row);
	}
	
	/**
	 * @param pix the bytes to place into image
	 * @param offset offset into image data to place
	 * @param samples the actual number of pixels in buf pix[]
	 *
	 * @param col current column to start putting data into image modified
	 * by the column increment as required for the pass.
	 * @param row current row to place data into
	 **/
	private void insertPixels(int pix[], int samples, int col, int row) throws JimiException
	{
		switch (colorType)
		{
		 case IT_GRAYSCALE:
		 case IT_GRAYALPHA:
			 insertGreyPixels(pix, samples, col, row);
			 break;
			 
		 case IT_RGB:	// assume int pixels
		 {
			 int j = 0;
			 int cInc = colInc[pass];
			 if (depth == 8)
			 {
				 for (j = 0; j < samples; col += cInc)
				 {
					 ji_.setChannel(col, row, ((pix[j++]<<16) | (pix[j++]<<8) | pix[j++]));
				 }
//				 ji_.addCoverage(row);
			 }
			 else // (depth == 16)
			 {
				 for (j = 0; j < samples; j +=2, col += cInc)
					 ji_.setChannel(col, row, ((pix[j]<<16) | (pix[j+=2]<<8) | pix[j+=2]));
//				 ji_.addCoverage(row);
			 }
			 break;
		 }
		 case IT_PALETTE:	// assume byte pixels
			 insertPalettedPixels(pix, samples, col, row);
			 break;
			 
		 case IT_RGBA:		// assume int pixels
		 {
			 int j = 0;
			 int ipix[] = ipixels;
			 int cInc = colInc[pass];
			 if (depth == 8)
			 {
				 for (j = 0; j < samples; col += cInc)
					 ji_.setChannel(col, row, ((pix[j++]<<16) | (pix[j++]<<8) | pix[j++] | (pix[j++]<<24)));
//				 ji_.addCoverage(row);
			 }
			 else // (depth == 16)
			 {
				 for (j = 0; j < samples; j +=2, col += cInc)
					 ji_.setChannel(col, row, ((pix[j]<<16) | (pix[j+=2]<<8) | pix[j+=2] | (pix[j+=2]<<24)));
//				 ji_.addCoverage(row);
			 }
			 break;
		 }
		}
	}
	
	private void insertGreyPixels(int pix[], int samples, int col, int row) throws JimiException
	{
		int p = pix[0];
		int cInc = colInc[pass];
		int rs = 0;
		
		switch (colorType)
		{
		 case IT_GRAYSCALE:
			 switch (depth)
			 {
				case 1:
					for (int j = 0; j < samples; j++, col += cInc)
					{
						if (rs != 0)
							rs--;
						else
						{
							rs = 7;
							p = pix[j>>3];
						}
						ji_.setChannel(col, row, ((p>>rs) & 0x1));
					}
//					ji_.addCoverage(row);
					break;
					
				case 2:
					for (int j = 0; j < samples; j++, col += cInc)
					{
						if (rs != 0)
							rs -= 2;
						else
						{
							rs = 6;
							p = pix[j>>2];
						}
						ji_.setChannel(col, row, ((p>>rs) & 0x3));
					}
//					ji_.addCoverage(row);
					break;
					
				case 4:
					for (int j = 0; j < samples; j++, col += cInc)
					{
						if (rs != 0)
							rs = 0;
						else
						{
							rs = 4;
							p = pix[j>>1];
						}
						ji_.setChannel(col, row, ((p>>rs) & 0xf));
					}
//					ji_.addCoverage(row);
					break;
					
				case 8:
					for (int j = 0; j < samples; j++, col += cInc)
						ji_.setChannel(col, row, pix[j]);
//					ji_.addCoverage(row);
					break;
					
				case 16:
					for (int j = 0; j < samples; j += 2, col += cInc)
						ji_.setChannel(col, row, pix[j]);
//					ji_.addCoverage(row);
					break;
					
				default:
					break;
			 }
			 break;
			 
		 case IT_GRAYALPHA:
			 if (depth == 8)
			 {
				 for (int j = 0; j < samples; col += cInc)
					 ji_.setChannel(col, row, ((pix[j++]<<8) | pix[j++]));
//					ji_.addCoverage(row);
			 }
			 else // (depth == 16)
			 {
				 for (int j = 0; j < samples; j += 2, col += cInc)
					 ji_.setChannel(col, row, (pix[j]<<8) | pix[j+=2]);
//					ji_.addCoverage(row);
			 }
			 break;
		}
	}
	
	private void insertPalettedPixels(int pix[], int samples, int col, int row) throws JimiException
	{
		int rs = 0;
		int p = pix[0];
		byte bpix[] = bpixels;
		int cInc = colInc[pass];
		
		switch (depth)
		{
		 case 1:
			 for (int j = 0; j < samples; j++, col += cInc)
			 {
				 if (rs != 0)
					 rs--;
				 else
				 {
					 rs = 7;
					 p = pix[j>>3];
				 }
				 ji_.setChannel(col, row, ((p>>rs) & 0x1));
			 }
//			 ji_.addCoverage(row);
			 break;
			 
		 case 2:
			 for (int j = 0; j < samples; j++, col += cInc)
			 {
				 if (rs != 0)
					 rs -= 2;
				 else
				 {
					 rs = 6;
					 p = pix[j>>2];
				 }
				 ji_.setChannel(col, row, ((p>>rs) & 0x3));
			 }
//			 ji_.addCoverage(row);
			 break;
			 
		 case 4:
			 for (int j = 0; j < samples; j++, col += cInc)
			 {
				 if (rs != 0)
					 rs = 0;
				 else
				 {
					 rs = 4;
					 p = pix[j>>1];
				 }
				 ji_.setChannel(col, row, ((p>>rs) & 0xf));
			 }
//			 ji_.addCoverage(row);
			 break;
			 
		 case 8:
			 for (int j = 0; j < samples; j++, col += cInc)
				 ji_.setChannel(col, row, pix[j]);
//			 ji_.addCoverage(row);
			 break;
		}
	}
	
	
	/**
	 * this method fills in blocks of color between pixels for a given
	 * scan produced when reading an interlace format file
	 **/
/*
 This is block commented out as converting it to JimiImage use was not
 considered worthwhile because the JimiSource does not support outputting
 multiple frames of an image as it loads yet
 
    private void blockFill(int rowStart)
    {
        int counter;
        int dw = dataWidth;
        int pass = this.pass;
        int w = blockWidth[pass];
        int sCol = startingCol[pass];
        int cInc = colInc[pass];
        int wInc = cInc - w;
        int maxW = rowStart + dw - w;
        int len;

        int h = blockHeight[pass];
        int maxH = rowStart + (dw * h);
        int startPos = rowStart + sCol;

        counter = startPos;

        if (colorType == IT_PALETTE)
        {
            byte bpix[] = bpixels;
            byte pixel;
            len = bpix.length;

            for (; counter <= maxW;)
            {
                int end = counter + w;
                pixel = bpix[counter++];
                for (; counter < end; counter++)
                    bpix[counter] = pixel;
                counter += wInc;
            }
            maxW += w;

            if (counter < maxW)
            {
                for (pixel = bpix[counter++]; counter < maxW; counter++)
                    bpix[counter] = pixel;
            }

            if (len < maxH)
                maxH = len;

            for (counter = startPos + dw; counter < maxH; counter += dw)
                System.arraycopy(bpix, startPos, bpix, counter, dw - sCol);
        }
        else	// everything but palette is integer storage
        {
            int ipix[] = ipixels;
            int pixel;
            len = ipix.length;

            for (; counter <= maxW;)
            {
                int end = counter + w;
                pixel = ipix[counter++];
                for (; counter < end; counter++)
                    ipix[counter] = pixel;
                counter += wInc;
            }

            maxW += w;

            if (counter < maxW)
            {
                for (pixel = ipix[counter++]; counter < maxW; counter++)
                    ipix[counter] = pixel;
            }

            if (len < maxH)
                maxH = len;

            for (counter = startPos + dw; counter < maxH; counter += dw)
                System.arraycopy(ipix, startPos, ipix, counter, dw - sCol);
        }
    }
*/

	/**
	 * handle PNG row filtering
	 *
	 * @param inbuf data source
	 * @param pix	data output
	 * @param upix - previous buffer ? filtering stuff ?
	 * @param rowFilter the filter type ID
	 * @param boff filter offset ?
	 **/
	private boolean filterRow(byte inbuf[], int pix[], int upix[],
														int rowFilter, int boff)
	{
		int rowWidth = pix.length;
		
		switch (rowFilter)
		{
		 case 0:
		 {
			 for (int x = 0; x < rowWidth; x++)
				 pix[x] = 0xff & inbuf[x];
			 break;
		 }
		 case 1:
		 {
			 int x = 0;
			 for ( ; x < boff; x++)
				 pix[x] = 0xff & inbuf[x];
			 for ( ; x < rowWidth; x++)
				 pix[x] = 0xff & (inbuf[x] + pix[x - boff]);
			 break;
		 }
		 case 2:
		 {
			 if (upix != null)
			 {
				 for (int x = 0; x < rowWidth; x++)
					 pix[x] = 0xff & (upix[x] + inbuf[x]);
			 }
			 else
			 {
				 for (int x = 0; x < rowWidth; x++)
					 pix[x] = 0xff & inbuf[x];
			 }
			 break;
		 }
		 case 3:
		 {
			 if (upix != null)
			 {
				 int x = 0;
				 for ( ; x < boff; x++)
				 {
					 int rval = upix[x];
					 pix[x] = 0xff & ((rval>>1) + inbuf[x]);
				 }
				 for ( ; x < rowWidth; x++)
				 {
					 int rval = upix[x] + pix[x - boff];
					 pix[x] = 0xff & ((rval>>1) + inbuf[x]);
				 }
			 }
			 else
			 {
				 int x = 0;
				 for ( ; x < boff; x++)
					 pix[x] = 0xff & inbuf[x];
				 
				 for ( ; x < rowWidth; x++)
				 {
					 int rval = pix[x - boff];
					 pix[x] = 0xff & ((rval>>1) + inbuf[x]);
				 }
			 }
			 break;
		 }
		 case 4:
		 {
			 if (upix != null)
			 {
				 int x = 0;
				 for ( ; x < boff; x++)
					 pix[x] = 0xff & (upix[x] + inbuf[x]);
				 
				 for ( ; x < rowWidth; x++)
				 {
					 int a, b, c, p, pa, pb, pc, rval;
					 a = pix[x - boff];
					 b = upix[x];
					 c = upix[x - boff];
					 p = a + b - c;
					 pa = p > a ? p - a : a - p;
					 pb = p > b ? p - b : b - p;
					 pc = p > c ? p - c : c - p;
					 
					 if ((pa <= pb) && (pa <= pc))
						 rval = a;
					 else if (pb <= pc)
						 rval = b;
					 else
						 rval = c;
					 pix[x] = 0xff & (rval + inbuf[x]);
				 }
			 }
			 else
			 {
				 int x = 0;
				 for ( ; x < boff; x++)
					 pix[x] = 0xff & inbuf[x];
				 
				 for ( ; x < rowWidth; x++)
				 {
					 int rval = pix[x - boff];
					 pix[x] = 0xff & (rval + inbuf[x]);
				 }
			 }
			 break;
		 }
		 default:
			 return false;
		}
		return true;
	}
	
	private void createColorModel() throws JimiException, IOException
	{
		int mask = 0;
		if (depth == 16)	// explicitly truncate down to 8
			mask = 0xff;
		else
			mask = (1 << depth) - 1;
		
		switch (colorType)
		{
		 case IT_PALETTE:
			 if (palette == null)
				 throw new JimiException("No palette located");
			 if (transparency)
				 model = new IndexColorModel(8, palette.length/4, palette, 0, true);
			 else
				 model = new IndexColorModel(8, palette.length/3, palette, 0, false);
			 break;
			 
		 case IT_GRAYSCALE:
			 if (depth == 16)	// explicitly truncate down to 8
				 model = new DirectColorModel(8, mask, mask, mask);
			 else
				 model = new DirectColorModel(depth, mask, mask, mask);
			 break;
			 
		 case IT_RGB:
			 model = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
			 break;
			 
		 case IT_GRAYALPHA: // GRAYALPHA can only have 8 or 16 bit depth
			 int smask;
			 smask = mask << 8;	// this makes alpha low 8 bits, gray upper 8
			 model = new DirectColorModel(16, smask, smask, smask, mask);
			 break;
			 
		 case IT_RGBA:
			 model = new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
			 break;
			 
		 default:
			 throw new JimiException("Image has unknown color type " + colorType);
		}
	}
}

