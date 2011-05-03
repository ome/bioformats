/*
 * Copyright (c) 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package com.sun.jimi.core.encoder.ico;

import com.sun.jimi.core.JimiImage;

import com.sun.jimi.core.InvalidOptionException;
import com.sun.jimi.core.OptionsObject;
import com.sun.jimi.core.JimiException;

import com.sun.jimi.core.util.LEDataOutputStream;

import java.io.OutputStream;
import java.io.IOException;

import java.awt.image.IndexColorModel;

import com.sun.jimi.tools.Debug;

import com.sun.jimi.util.PropertyOwner;

import com.sun.jimi.core.compat.*;


/**
 *  Encoder pallette images into ICO format
 *  ICO is based upon DIB (Device Independent Bitmaps) a generalization of the BMP file 
 *  format.
 *  ICO files consist of a directory of icons each of which points to a separate icon image
 *
 */
public class ICOEncoder extends JimiEncoderBase {

	protected static final int BMPINFOHEADER_SIZE = 40;

	private boolean isEncodingFirstImage = true;
	
	protected LEDataOutputStream destination;
	
	/** The "resource id" flag **/
	protected short TYPE_FLAG = 0x0001;
	
	/** Is this encoder in a valid state, has it errored? 
	  */
	protected int stateFlag = NEXTIMAGE;
	
	protected int currentOffset = 0;
	
	/* COMMENTED OUT BECAUSE THE ARCHITECTURE WON'T SUPPORT MULTIPLE IMAGES THAT AREN'T 
	 * THE SAME SIZE 
	 
	public static final int UNSET_NUMBER_OF_IMAGES = -1;
	
	// The number of images that will be encoded to the destination
	protected int numberOfImages = UNSET_NUMBER_OF_IMAGES;
	
	// This is a multiple image encoder so it should say so.
	public int getCapabilities() {
	
		return MULTIMAGE;
		
	}
	
	// How many images should I encode?
	public void setNumberOfImages(int count) {
	
		numberOfImages = count;
	
	}
	*/
		
	/** Called by JimiEncoderBase (This is VERY Poor OOD since we should in reality
	  * override the initEncoder method in the base class and call super.initEncoder!
	  */
	public void initSpecificEncoder(OutputStream out, AdaptiveRasterImage ji) {
	
		destination = new LEDataOutputStream(out);
			
	}
	
	public boolean driveEncoder() throws JimiException {	

		AdaptiveRasterImage currentImage = getJimiImage();

		/* COMMENTED OUT BECAUSE THE ARCHITECTURE WON'T SUPPORT MULTIPLE IMAGES THAT AREN'T 
		 * THE SAME SIZE 
		 
		if (isEncodingFirstImage) {
		
			//write the header
			
		} else {
		
			//write the image
		}
		
		if (++imageCount < numberOfImages) {
		
			state |= NEXT_IMAGE;
			return false;
			
		} else {
		
			state |= DONE;
			return false;
			
		}
		*/
		
		 try {
		 
		 //	writeICOCURDirectory(destination, new JimiImage[] { currentImage } );
		 // ^^^^^^^^ 1.0 incompatible
		 	AdaptiveRasterImage jimi_image_array[] = new AdaptiveRasterImage[1];
		 	jimi_image_array[0] = currentImage;
		 	writeICOCURDirectory(destination,  jimi_image_array );
			writeDIBImage(destination, currentImage);
			
		} catch(IOException ioe) {
		
			stateFlag = ERROR;
			throw new JimiException(ioe.toString());
			
		} catch(JimiException je) {
		
			stateFlag = ERROR;
			throw je;
			
		}
		
		stateFlag = DONE;
		return false;
		
	}
	
	public int getState() {
	
		return stateFlag;
		
	}

	public void freeEncoder() throws JimiException {
	
	}

	/** Write the ICODIR with entries for each image
	  * Each of which has the format :<br>
	  * <pre>
	  * 	WORD 		idReserved
	  *		WORD 		idType
	  *		WORD 		idCount
	  *		ICONDIRENTRY idEntries[]
	  * </pre>
	  */
	protected void writeICOCURDirectory(LEDataOutputStream destination, AdaptiveRasterImage[] images) throws JimiException, IOException {
	
		destination.writeShort(0x0000); //"idReserved" always 0
		destination.writeShort(0x0001); //"idType" alwasy 1
		destination.writeShort((short)images.length); //idEntries - number of images

		currentOffset = 6;	 				//3 WORDS for the fact that it is a start index
		currentOffset += images.length * 16; //size of ICONDIRENTRY
		
		for (int i = 0; i < images.length; i++) {
		
			writeICOCURDIREntry(destination, images[i]);
		}
	}

	/** Write the ICODIRENTRY for an image
	  * Each of which has the following format : <br>
	  * <pre>
	  *		BYTE	bWidth
	  *		BYTE	bHeight
	  *		BYTE	bColorCount
	  *		BYTE	bReserved
	  *		WORD	wPlanes
	  *		WORD	wBitCount
	  *		DWORD	dwBytesInRes
	  *		DWORD	dwImageOffset
	  * </pre>
	  */
	protected void writeICOCURDIREntry(LEDataOutputStream destination, AdaptiveRasterImage anImage) throws JimiException, IOException {
	
		IndexColorModel cm;
		try {

			cm = (IndexColorModel)anImage.getColorModel();

		} catch(ClassCastException cce) {
		
			throw new JimiException("image/x-ico formats can only be created from palette images");
		}

		int mapSize = cm.getMapSize();
		if (mapSize > 256) {
		
			throw new JimiException("image/x-ico formats can only support palette with up to 256 colors");
			
		}
		int bitCount = computeBitCount(mapSize);
		
		int width = anImage.getWidth();
		int height = anImage.getHeight();
		if (width > 256 || height > 256) {
			throw new JimiException("image/x-ico formats can only encode images up to 256 x 256 pixels");
		}
		
		int imageSize = computeImageSize(mapSize, bitCount, width, height);
		
		destination.writeByte((byte)width); 	//bWidth
		destination.writeByte((byte)height); 	//bHeight
		destination.writeByte((byte)mapSize); 				//bColorCount
		destination.writeByte((byte)0x00); 					//bReserved - must be 0
		destination.writeShort((short)0); 							//wPlanes - the number of color plans
															//	in the icon bitmap - Only 1 RGB plane
		destination.writeShort((short)bitCount);					//wBitCount - the number of bits in 
															//	the icon bitmap
		int resourceSize = BMPINFOHEADER_SIZE + (imageSize * bitCount / 8) + (int)Math.pow(2, bitCount);
		
		destination.writeInt(resourceSize);
															//dwBytesInRes - the size of the 
															//	resource in bytes
		destination.writeInt(currentOffset);   				//dwImageOffset - where in the file the image begins
		currentOffset += imageSize;
		
	}	
	
	/* Each icon image consists of the following :<br>
	 * <pre>
	 *	BITMAPINFOHEADER icHeader
	 * 	RGBQUAD			 icColors[]
	 *	BYTE			 icXOR[]
	 *	BYTE			 icAND[]
	 * </pre>
	 */
	protected void writeDIBImage(LEDataOutputStream destination, AdaptiveRasterImage ji) throws JimiException, IOException {
	
		IndexColorModel icm = (IndexColorModel)ji.getColorModel();
		int mapSize = icm.getMapSize();
		int bitCount = computeBitCount(mapSize);	

		writeBitmapInfoHeader(destination, bitCount, mapSize, ji, icm);
		writeRGBQuads(destination, icm);	
		writePixels(destination, bitCount, ji);
		
	}

	/* <pre>
	 * 	DWORD	biSize
	 * 	LONG  	biWidth
	 * 	LONG  	biHeight
	 * 	WORD  	biPlanes
	 * 	WORD  	biBitCount
	 *	DWORD	biCompression
	 *	DWORD	biSizeImage
	 *	LONG	biXPelsPerMeter
	 *	LONG	biYPelsPerMeter
	 *	DWORD	biClrUsed
	 *	DWORD	biClrImportant
	 * </pre>
	 */
	protected void writeBitmapInfoHeader(LEDataOutputStream destination, int bitCount, int mapSize, AdaptiveRasterImage ji, IndexColorModel icm) throws JimiException, IOException {

		int width = ji.getWidth();
		int height = ji.getHeight() * 2;
		
		int imageSize = computeImageSize(mapSize, bitCount, width, height);
		
		destination.writeInt(BMPINFOHEADER_SIZE); //biSize - the number of bytes the header needs
		destination.writeInt(width); //biWidth
		destination.writeInt(height); //biHeight
		destination.writeShort((short)1); //biPlanes - only one RGBA plane :)
		destination.writeShort((short)bitCount); 
		destination.writeInt(0x00); //UNUSED
		destination.writeInt(imageSize); //biSizeImage
		destination.writeInt(0x00); //UNUSED
		destination.writeInt(0x00); //UNUSED
		destination.writeInt(0x00); //UNUSED
		destination.writeInt(0x00); //UNUSED
		
	}
	
	/**	<pre>
	  * 	BYTE red
	  *		BYTE green
	  *		BYTE blue
	  *		BYTE reserved - must be 0
	  * </pre>
	  **/
	protected void writeRGBQuads(LEDataOutputStream destination, IndexColorModel icm) throws JimiException, IOException {
		//final
		int mapSize = icm.getMapSize();
		
		int bitCount = computeBitCount(mapSize);
		int pad = (int)Math.pow(2, bitCount) - mapSize;
		
		byte[] red = new byte[mapSize];
		byte[] blue = new byte[mapSize];
		byte[] green = new byte[mapSize];
		
		icm.getReds(red);
		icm.getBlues(blue);
		icm.getGreens(green);
		
		for (int i = 0; i < mapSize; i++) {
		
			//Byte order is "wrong" because the LEDataOutputStream doesn't handle single bytes well,
			//we're simulating LED
			 destination.writeByte((byte)blue[i]);
			 destination.writeByte((byte)green[i]);
			 destination.writeByte((byte)red[i]);
			 destination.writeByte((byte)0);
			
		}
		
		byte[] padbuff = new byte[4];
		
		for (int i = 0; i < pad; i++) {
			destination.write(padbuff);
		}
		
	}
	
	protected void writePixels(LEDataOutputStream destination, int bitCount, AdaptiveRasterImage anImage) throws JimiException, IOException {
	
		anImage.setRGBDefault(false);
		int height = anImage.getHeight();
		int scansize = anImage.getWidth();
		
		int[] buff = new int[scansize];
		
		//write AND's
		if (bitCount == 2) {
		
			for (int i = height - 1; i > -1; i--) {
			
				anImage.getChannel(i, buff, 0);
				for (int j = 0; j < scansize; j++) { //HIDDEN LOOP INCREMENTS BY 4
					byte t = (byte)(buff[j] << 6);
					t |= (byte)(buff[++j] << 4);
					t |= (byte)(buff[++j] << 2);
					t |= (byte)(buff[++j]);
				}
				
			}
			//write XOR's
			//if alpha channel is trivial rock on with 0's
			byte [] pad = new byte[scansize / 4];
			for (int i = 0; i < height; i++) {
				destination.write(pad);
			}
			
		} else if (bitCount == 4) {
			
			for (int i = height - 1; i > -1; i--) {
			
				anImage.getChannel(i, buff, 0);
				for (int j = 0; j < scansize; j++) {  //HIDDEN LOOP INCREMENTS BY 2!!!!
					byte t = (byte)(buff[j] << 4);
					t |= (byte)buff[++j];
					destination.writeByte(t);
				}
				
			}
			byte[] pad = new byte[scansize / 2];
			for (int i = 0; i < height; i++) {
				destination.write(pad);
			}
			
		} else if(bitCount == 8) {
			
			for (int i = height - 1; i > -1; i--) {
			
				anImage.getChannel(i, buff, 0);
				for (int j = 0; j < scansize; j++) {
					destination.writeByte((byte)buff[j]);
				}
				
			}
			
	
			//write XOR's
			//if alpha channel is trivial rock on with 0's
			byte [] pad = new byte[scansize];
			for (int i = 0; i < height; i++) {
		
				destination.write(pad);
				
			}
				
		} else {
		
			//ERROR!
			
		}
		
	}
		
	protected int computeImageSize(int mapSize, int bitCount, int width, int height) {
			
		return (width * height); //XOR and AND
		
	}

	protected int computeBitCount(int mapSize) {
			
		if (mapSize <= 4) {
			return 2;
		} else if ( mapSize <= 16) {
			return 4;
		} else {
			return 8;
		}
	
	}
	
			
}




/*
$Log: ICOEncoder.java,v $
Revision 1.2  1999/01/20 11:03:22  luke
*** empty log message ***

Revision 1.1.1.1  1998/12/01 12:21:56  luke
imported

Revision 1.1.1.1  1998/09/01 02:48:49  luke
Imported from jimi_1_0_Release codebase

Revision 1.3  1998/08/07 03:07:02  luke
1.0 compatibility changes by Max.

Revision 1.2  1998/06/30 21:03:51  chris
Updated ICO and CUR encoders.
Still no support for transparency, but the CUR encoder can have an
arbitrary "HotSpot" set.

Revision 1.1  1998/06/30 18:22:05  chris
ICO support added

*/
