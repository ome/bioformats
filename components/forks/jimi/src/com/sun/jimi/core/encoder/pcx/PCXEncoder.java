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


/**
 * Save an image in a PCX format.
 *
 * @author Max Hrabrov
 * @version	1.0
 */

package com.sun.jimi.core.encoder.pcx;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.LEDataOutputStream;


public class PCXEncoder extends JimiEncoderBase  //  implements JimiEncoder
{
    
	OutputStream myOut;
	RLEOutputStreamForPCX dataOut;
	LEDataOutputStream LEDOut;
	private int nState;	         // encoder return state

	int nBitsPerPixel = 8;		 /** default to 8 bits per pixel **/
	int nBitPlanes = 1;			 /** default to 1 bit plane (for a 256 color image) **/
	int nBytesPerLine = 0;
	int nPaletteType = 1;		 /** 1 or 2 , default to 1 (color/grayscale) **/
	int nVersion = 5;			 

	AdaptiveRasterImage myJimiImage;

	protected int COLOR_MODEL = 0;    // 0 = IndexColorModel  1 = DirectColorModel 	
		
	
	
	public void initSpecificEncoder(OutputStream anOutputStream, AdaptiveRasterImage aJimiImage) throws JimiException
	{
			nState = 0;
			myOut = anOutputStream;
			if (aJimiImage.getColorModel() instanceof DirectColorModel) 
			{
				nBitPlanes = 3;				// store as a 24-bit image
				COLOR_MODEL = 1;
			}

			/** RLE compression is theoretically an option, but in reality _always_ used **/

			LEDOut = new LEDataOutputStream(new BufferedOutputStream(myOut));
			dataOut = new RLEOutputStreamForPCX(LEDOut);
	}	
	
	
    public void writePcxHeader(LEDataOutputStream aDataOutStream, AdaptiveRasterImage aJimiImage)
	{
	
	    byte[] padding = new byte[54]; 
		byte[] palette = new byte[48];
		byte[] zero = new byte[2];
		
		int Xstart = 0;				// x of left upper corner
		int Ystart = 0;				// y of left upper corner	
		int Xend = 0;				// x of bottom right corner
		int Yend = 0;				// y of bottom right corner		
		
    	// first get some required information from JimiImage
	  	
		int nWidth = aJimiImage.getWidth();
		int nHeight = aJimiImage.getHeight();
		
		int nBytesPerLine = nWidth;
		
		Xend = Xstart + nWidth - 1;
		Yend = Xstart + nHeight - 1;
		
		
		try
		{

		// now write the header for the PCX file

			aDataOutStream.write(0x0A); 	  				// PCX id number	
			aDataOutStream.write((byte)(nVersion));		    // version number
			aDataOutStream.write((byte)(1));				// encoding format - RLE 
			aDataOutStream.write((byte)(nBitsPerPixel));	// Bits per pixel 
			aDataOutStream.writeShort(0);		    		// x of left upper corner
			aDataOutStream.writeShort(0);		    		// y of left upper corner

			aDataOutStream.writeShort(Xend);				// x of bottom right corner
			aDataOutStream.writeShort(Yend);				// y of bottom right corner
			aDataOutStream.writeShort(nWidth);				// horisontal res
			aDataOutStream.writeShort(nHeight);				// vertical res
			
			/** an EGA palette is only used for 16 color images or worse. Larger (256 c) palettes
			 are appended at the end of the PCX file.  You can always include a 
			 16 color EGA Palette anyway, though, as long as the image is an IndexColorModel  **/

			if (COLOR_MODEL == 0)  {
				IndexColorModel icm = (IndexColorModel)aJimiImage.getColorModel();
				palette = makePalette(icm, 16);			    // 16 Color EGA Palette 
			}
			aDataOutStream.write(palette);					// zero's if DirectColorModel

			aDataOutStream.write(0x00);						// reserved - always 0
			aDataOutStream.write((byte)(nBitPlanes));		// number of bit planes 
			aDataOutStream.writeShort(nBytesPerLine);		// Bytes per Scan-line	
			// palette type

			aDataOutStream.writeShort(nPaletteType);		

			/** the next 2 values are the screen rez at which the image was created.  JimiImage cannot currently 
			provide this information, so the current screen size of the host computer is being stored.  **/

			Toolkit MyToolkit = Toolkit.getDefaultToolkit();

			aDataOutStream.writeShort(MyToolkit.getScreenSize().width);
			aDataOutStream.writeShort(MyToolkit.getScreenSize().height);

			// that's it for the header - just need to fill out the rest of the 128 byte space (54 bytes) with // 0's.

			aDataOutStream.write(padding);	
			aDataOutStream.flush();			
		}		
		catch (IOException e){
			nState = ERROR;
		}
			
 	}
	
	public byte[] makePalette (IndexColorModel icm, int nColors) 
	
	/**  this method creates and writes a palette with nColors number of colors. It only gets called for
	IndexColorModel images.  **/
	
	{

		int paletteSize = nColors * 3;  
		byte[] palette = new byte[paletteSize];

		ByteArrayOutputStream localOut = new ByteArrayOutputStream(paletteSize); 
		// output stream to write to palette byte array
	
		int count = icm.getMapSize();		
				
		byte[] red = new byte[count];
		byte[] green = new byte[count];
		byte[] blue = new byte[count];

		int my_count;
		
		icm.getReds(red);
		icm.getGreens(green);
		icm.getBlues(blue);
				
		try
		{
			/** write out as many colors as are available as long as <= nColors
			 otherwise write nColors colors.   **/

			if (nColors < count){
				my_count = nColors;
			} else {
				my_count = count;
			}

			for (int i = 0; i < my_count; i++)
			{
				localOut.write(red[i]);
				localOut.write(green[i]);
				localOut.write(blue[i]);
			}

			// fill the rest with 0's, up to nColors

			if (nColors > count) {
				for (int i = 0; i < nColors - my_count; ++i) {
					localOut.write(0x00);
					localOut.write(0x00);
					localOut.write(0x00);
    			}	
			}

			// now write out to palette[] and return

			palette = localOut.toByteArray();
			localOut.close();
			localOut = null;   					// deallocate
		}
		catch (IOException e)
		{
			nState = ERROR;
		}			
		return palette;
	}


	public void writePcxImage(RLEOutputStreamForPCX aDataOutStream, AdaptiveRasterImage aJimiImage) throws JimiException
	{

   	int nWidth = aJimiImage.getWidth();
	int nHeight = aJimiImage.getHeight();
	nBytesPerLine = (nWidth + 1);  			  // RLE encoder will run from 0 to nWidth - 1
				
				
	byte buffer[] = new byte[nBytesPerLine];	
					
	try {
	
			for (int i = 0; i < nHeight; i++)  {
				if (COLOR_MODEL == 0)        // IndexColorModel
				{		
					/** If a palette is being used (all cases except 24 bit color or monochrome) 
					get pixel value of JimiImage in the form R,G,B.   **/

					aJimiImage.getChannel(0, i, buffer, 0);   // this is a row of pixels in the form we need.
					aDataOutStream.write(buffer);
				}
				else  			/** DirectColorModel - 24 bit color **/
				{
					aJimiImage.setRGBDefault(true);
					aJimiImage.getChannel(1, i, buffer, 0);
					aDataOutStream.write(buffer);
					aJimiImage.getChannel(2, i , buffer, 0);
					aDataOutStream.write(buffer);
					aJimiImage.getChannel(3, i, buffer, 0);
					aDataOutStream.write(buffer);
				}
				setProgress((i * 100) / nHeight);
			}
		}
		catch (IOException e) {
			nState = ERROR;
		}	
    }	
	
	
    public void writePcxFooter(LEDataOutputStream aDataOutStream, AdaptiveRasterImage aJimiImage)
	{
	/** if a VGA palette at the end is required, write it.  **/
		byte[] palette = new byte[768];	

		IndexColorModel icm = (IndexColorModel)aJimiImage.getColorModel();

		palette = makePalette(icm, 256);

		try 
		{
			aDataOutStream.write(0x0C); 					  /** 0xC0 is the palette marker  **/
			aDataOutStream.write(palette, 0, palette.length);
		}
		catch (IOException e)
		{
			nState = ERROR;
		}
	}
	
	public boolean driveEncoder() throws JimiException
	{

		// retrieve image to be saved
		AdaptiveRasterImage myJimiImage = getJimiImage();
		
		try
		{
			writePcxHeader(LEDOut, myJimiImage);         // 128 bytes
			writePcxImage(dataOut, myJimiImage);    
			if (COLOR_MODEL == 0) {
				writePcxFooter(LEDOut, myJimiImage);	
			}
			
			
            // DONE!  Flush the streams
			
			LEDOut.flush();
			dataOut.flush();
			myOut.flush();
			nState = DONE;			
		}
		
		catch (IOException e)
		{
			nState = ERROR;
		}
		return false;	// state change - Completed operation in actuality.	 	
	}
	
	
	public void freeEncoder() throws JimiException
	{
		myOut = null;
		dataOut = null;
		LEDOut = null;
		super.freeEncoder();
	}	
	
		
	public int getState()
	{
		return nState;	
	}
}
