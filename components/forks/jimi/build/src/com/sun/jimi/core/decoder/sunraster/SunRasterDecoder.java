/*
 * Copyright (c) 1997 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.decoder.sunraster;

import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;

/** Issues :
  * <ol><li>What the hell is a RAW color map?  Do we support this?  Our docs say it
  * is any color map not definied by the SunRaster format!
  * </ol>
  */
public class SunRasterDecoder extends JimiDecoderBase
{

	DataInputStream lin;
	AdaptiveRasterImage sinkImage;
	SunRasterHeader header;
	SunRasterColorMap colormap;
	int state;

	private static final int RLE_ESCAPE = 0x80;

	/** Initialize this decoder.  
	  * @param in The source of the encoded image data
	  * @param ji The data structure that will hold the image data produced
	  */
	public void initDecoder(InputStream in, AdaptiveRasterImage ji) throws JimiException {

		if(in == null || ji == null) {

			throw new IllegalArgumentException("Null values to constructor.");

		}

		lin         = new DataInputStream(in);
		sinkImage 	= ji;

		state = 0; //START STATE

	}


	public void freeDecoder() throws JimiException {
	
		lin 		= null;
		sinkImage 	= null;
		header		= null;
		colormap	= null;

	}

	public boolean driveDecoder() throws JimiException {

		try {

			sinkImage = getJimiImage();

			if(state == 0) {

				header 	 = new SunRasterHeader(lin);
				if(header.ColorMapType != header.NO_COLOR_MAP) {

					colormap = new SunRasterColorMap(lin, header);

				} else if(header.ColorMapType == header.RAW_COLOR_MAP) {

					throw new JimiException("Unsupported Format Subtype");

				}

				initJimiImage();
				state |= INFOAVAIL;

				return true;

			} else {



				if(decodeImage()) {

					state |= IMAGEAVAIL;

				} else {

					//No change in state

				}
				sinkImage.addFullCoverage();

			}

		} catch(JimiException je) {

			state |= ERROR;
			throw je;

		} catch(IOException ioe) { 

			state |= ERROR;
			throw new JimiException("IOException while reading file : " + ioe.toString());

		}

		return false;

	}

	public int getState() {

		return state;

	}

	public AdaptiveRasterImage getJimiImage()
	{
		return this.sinkImage;
	}

	private void initJimiImage() throws JimiException {

		try {

		//set the size
		sinkImage.setSize((int)header.Width, (int)header.Height);

		//set the color model
		if(colormap == null) {

			//Direct Color

			int rmask;
			int gmask;
			int bmask;

			int mask = 0;
			for(int i = (int)header.Depth / 3; i > 0; i--) {
			
				mask = mask << 1;
				mask++;
				
			}
			sinkImage.setColorModel(new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff));
 
		} else {

			if(colormap.RGBType) {
			
				sinkImage.setColorModel(new IndexColorModel(8,
					colormap.tableLength, colormap.r, colormap.g, colormap.b));

			} else { //RAW

				sinkImage.setColorModel(new IndexColorModel(8,
					colormap.tableLength, colormap.raw, 0, false));

			}

		}

		//allocate space
		sinkImage.setPixels();

		} catch(JimiException je) {

			throw je;

		}

	}

	private boolean decodeImage() throws IOException, JimiException {

		try {

		//Is it RLE compressed?
		if(header.Type == header.BYTE_ENCODED_TYPE) {

			RLEDecodeImage();

		} else {

			int scanlineSize = (header.Width * header.Depth);
			if(scanlineSize % 16 != 0) {

				scanlineSize += 16 - (scanlineSize % 16);

			}
			scanlineSize /= 8;

			if(header.Depth == 8) {
			
				byte[] data = new byte[header.Width];
				for(int lineNum = 0; lineNum < header.Height; lineNum++) {
		
					lin.readFully(data, 0, header.Width);
					lin.skipBytes(scanlineSize - data.length);
					sinkImage.setChannel(0, lineNum, data);
					setProgress((lineNum * 100) / header.Height);
				}

			} else if(header.Depth == 4) {

				byte[] data = new byte[header.Width];
				for(int lineNum = 0; lineNum < header.Height; lineNum++) {

					for(int i = 0; i < data.length; i += 2) {

						byte t1 = lin.readByte();
						data[i] = (byte)(t1 >> 4);
						data[i + 1] = (byte)(t1 & 0xF);

					}
					lin.skipBytes(scanlineSize - (data.length / 2));
					sinkImage.setChannel(0, lineNum, data);
					setProgress((lineNum * 100) / header.Height);

				}
			
			} else if(header.Depth < 8) {


				/* INPROGRESS CODE
				   This won't work for a few reasons, 1 we grab too many bytes.
				   and 2 the shifting of the masks is foobar.

			   	//pull it in and store it into byte array
				byte[] data = new byte[header.Width];

				byte oldByte = 0x00;
				int oldByteMask = 0xFF;
				int thisByteMask = 0xFF;

				int mask = 0;

								for(int i = 0; i < header.Depth; i++) {

					mask = mask << i;
					mask |= 1;

				}

				for(int lineNum = 0; lineNum < header.Height; lineNum++) {

					int bytesRead = 0;

					//grab a byte.  Since it is less than 8 bytes
					//we've got to split it up and combine it with
					//the next byte to get the appropriate data
					for(int i = 0; i < data.length; i++) {
											
						byte thisByte = lin.readByte();
						data[i] = (byte)((oldByte & oldByteMask) | (thisByte & thisByteMask));
						oldByte = thisByte;

						//Shift the mask
						oldByteMask = mask << (bytesRead % header.Depth);
						bytesRead++;
						//Shift the mask
						thisByteMask = mask << (bytesRead % header.Depth);
			
					}

					lin.skipBytes(scanlineSize - bytesRead);
					sinkImage.setChannel(0, lineNum, data);

				}				
				*/

			} else if(header.Depth == 16) {

				int[] data = new int[header.Width];
				for(int lineNum = 0; lineNum < header.Height; lineNum++) {

					for(int i = 0; i < data.length; i++) {
					
						data[i] = lin.readInt();

					}
					
					lin.skipBytes(scanlineSize - (data.length * 2));
					sinkImage.setChannel(lineNum, data);
					setProgress((lineNum * 100) / header.Height);

				}
					
			} else if(header.Depth < 16) {

				//pull it in and store it into short array

					//grab a byte.  Since it is less than 16 bytes
					//we've got to split it up and combine it with
					//the next byte to get the appropriate data

			} else if(header.Depth == 24) {

				int[] data = new int[header.Width];
				for(int lineNum = 0; lineNum < header.Height; lineNum++) {
					
					if (header.Type == header.RGB_FORMAT_TYPE) 
					{
						
						for(int i =0; i < data.length; i++) {
							
							int t1 = lin.readUnsignedByte();
							int t2 = lin.readUnsignedByte();
							int t3 = lin.readUnsignedByte();
							
							data[i] = (t1 << 16) | (t2 << 8) | t3;
							
						}
					}
					else 
					{
						
						for(int i =0; i < data.length; i++) {
							
							int t1 = lin.readUnsignedByte();
							int t2 = lin.readUnsignedByte();
							int t3 = lin.readUnsignedByte();
							
						data[i] = (t3 << 16) | (t2 << 8) | t1;
						
						}
					}
					
					lin.skipBytes(scanlineSize - (data.length * 3));
					sinkImage.setChannel(lineNum, data);
					setProgress((lineNum * 100) / header.Height);

				}

			} else if(header.Depth == 32) {

				int[] data = new int[header.Width];
				for(int lineNum = 0; lineNum < header.Height; lineNum++) {

					if (header.Type == header.RGB_FORMAT_TYPE) {

						for(int i = 0; i < data.length; i++) {
							
							lin.readByte(); //throw away pad
							int t1 = lin.readUnsignedByte();
							int t2 = lin.readUnsignedByte();
							int t3 = lin.readUnsignedByte();
							
							data[i] = (t1 << 16) | (t2 << 8) | t3;
							
						}
					}
					
					else 
					{
						for(int i = 0; i < data.length; i++) 
						{
							
							lin.readByte(); //throw away pad
							int t1 = lin.readUnsignedByte();
							int t2 = lin.readUnsignedByte();
							int t3 = lin.readUnsignedByte();
							
							data[i] = (t3 << 16) | (t2 << 8) | t1;
							
						}
					}

					lin.skipBytes(scanlineSize - (data.length * 4));

					sinkImage.setChannel(lineNum, data);
					setProgress((lineNum * 100) / header.Height);

				}
			
			} else {

				//pull it in and store it into int array

			}
					
		}


		} catch(JimiException e) {
			throw e;

		} catch(IOException ioe) {
			throw ioe;

		}

		state |= IMAGEAVAIL;
		return true;

	}

	protected void RLEDecodeImage() throws IOException, JimiException
	{
		InputStream in = new RLEInputStream(lin);
		byte[] buf = new byte[(int)header.Width];
		boolean pad = buf.length % 2 != 0;
		int height = (int)header.Height;
		for (int line = 0; line < height; line++)
		{
			in.read(buf);
			// read a "pad" byte if necessary to word-align the lines
			if (pad)
				in.read();
			flushOut(buf, sinkImage, line);
		}
	}

	private static void flushOut(byte[] data, AdaptiveRasterImage sink, int lineNum) throws JimiException {

		sink.setChannel(0, lineNum, data);

	}

	public boolean usesChanneledData()
	{
		return true;
	}

}	

