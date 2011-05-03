package com.sun.jimi.core.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.ImageProducer;
import java.util.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.JimiRasterImage;

/**
 * This class contains several general utility methods which didnt
 * really fit anywhere else.<p>
 * The methods in this class are all static and hold no state.<p>
 * This method uses an odd filename to create Error Image objects and
 * Error ImageProducers which is a bit of a kludgey way to do it.<p>
 *
 * @author	Robin Luiten
 * @vesion	$Revision: 1.4 $
 **/
public class JimiUtil
{
	/**
	 * This string is used to specify a file that doesnt exist for the
	 * builtin java loader to return an Image and ImageProducer which
	 * produce an error case image.
	 */
	private final static String nonFileName = "X#jimi#X";

	public static final boolean TRUE = true;

	/**
	 * Currently this method is a bit dumb in that it assumes that
	 * the rgb default color model is a DirectColorModel.
	 * @param cm color model to check if its rgb default color model
	 * @return true of the parameter color model is the same as the
	 * rgb default color model
	 */
	public static boolean isRGBDefault(ColorModel cm)
	{
		if (cm instanceof DirectColorModel)
		{
			DirectColorModel dcm = (DirectColorModel)cm;
			DirectColorModel rgbDefault = (DirectColorModel)ColorModel.getRGBdefault();

			int defAMask = rgbDefault.getAlphaMask();
			int defRMask = rgbDefault.getRedMask();
			int defGMask = rgbDefault.getGreenMask();
			int defBMask = rgbDefault.getBlueMask();

			int dcmAMask = dcm.getAlphaMask();
			int dcmRMask = dcm.getRedMask();
			int dcmGMask = dcm.getGreenMask();
			int dcmBMask = dcm.getBlueMask();

			return (defAMask == dcmAMask && defRMask == dcmRMask &&
							defGMask == dcmGMask &&	defBMask == dcmBMask);
		}
		return false;
	}

	/**
	 * @param cm color model to retrieve the individual channel widths.
	 * @return an array filled in with the widths of the channels.
	 * in the color model. Always returns 4 element array with widths
	 * of the ALPHA, RED, GREEN, BLUE channels respectively filled in
	 * in order.
	 **/

	public static byte[] getChannelWidths(ColorModel cm)
	{
		byte[] widths = new byte[8];

		if (cm instanceof DirectColorModel)
		{
			DirectColorModel dcm = (DirectColorModel)cm;
			widths[0] = countBitsSet(dcm.getAlphaMask());
			widths[1] = countBitsSet(dcm.getRedMask());
			widths[2] = countBitsSet(dcm.getGreenMask());
			widths[3] = countBitsSet(dcm.getBlueMask());
		}
		else if (cm instanceof IndexColorModel)
		{
			widths[0] = 8;
			widths[1] = 8;
			widths[2] = 8;
			widths[3] = 8;
		}
		return widths;
	}

	/**
	 * This assumes that bits are set contiguously
	 * @param val count the bits set in this variable
	 * @return the number of bits set in the integer val
	 **/
	public static byte countBitsSet(int val)
	{
		// this is a dumb slow way to do it.
		byte count = 0;
		int mask = 0x00000001;

		for (int i = 0; i < 32; ++i)
		{
			if ((val & mask) != 0)
				++count;
			mask = mask << 1;
		}
		return count;
	}

	/**
	 * @return Image which was created with an Error state.
	 */
	public static Image getErrorImage()
	{
		return Toolkit.getDefaultToolkit().createImage(getErrorImageProducer());
	}

	/**
	 * @return Image Producer which can only output an error to any
	 * Image Consumer registered with it.
	 */
	public static ImageProducer getErrorImageProducer()
	{
		return new ErrorImageProducer();
	}

	/**
	 * @param bitSize the number of bits each pixel takes up in the
	 * bitpacked input array. can only be 1, 2 or 4.
	 * @param in input array of bit packed pixels
	 * @param out output array for expanded pixels. The size of this
	 * array determines how many pixels are retrieved from in and
	 * expanded.
	 * @param outLen number of bytes to expand into the the out buffer.
	 **/
	public static void expandPixels(int bitSize, byte[] in, byte[] out, int outLen)
	{

		if (bitSize == 1)
		{
			expandOneBitPixels(in, out, outLen);
			return;
		}

		int i;	// input index
		int o;	// output index
		int t;
		byte v;
		int count = 0;	// number of pixels in a byte
		int maskshift = 1;	// num bits to shift mask for next use
		int pixelshift = 0; // num bits to shift pixel to bottom of byte
		int tpixelshift = 0;
		int pixelshiftdelta = 0;
		int mask = 0;
		int tmask; 	// temp mask for processing

		switch (bitSize)
		{
		 case 1:
			 mask = 0x80;
			 maskshift = 1;
			 count = 8;
			 pixelshift = 7;
			 pixelshiftdelta = 1;
			 break;
		 case 2:
			 mask = 0xC0;
			 maskshift = 2;
			 count = 4;
			 pixelshift = 6;
			 pixelshiftdelta = 2;
			 break;
		 case 4:
			 mask = 0xF0;
			 maskshift = 4;
			 count = 2;
			 pixelshift = 4;
			 pixelshiftdelta = 4;
			 break;
		 default:
			 throw new RuntimeException("support only expand for 1, 2, 4");
		}

		// for all outputs required
		i = 0;
		for (o = 0; o < out.length; )
		{
			tmask = mask;
			tpixelshift = pixelshift;
			v = in[i];
			for (t = 0; t < count && o < out.length; ++t, ++o)
			{
				// this only works for 1 bit
				out[o] = (byte)(((v & tmask) >>> tpixelshift) & 0xFF);
				tmask = (byte)((tmask & 0xFF) >>> maskshift);
				tpixelshift -= pixelshiftdelta;
			}
			++i;
		}
	}

	/** COMMENT! 
	 */
	static byte[] expansionTable = new byte[256 * 8];
	static int[] intExpansionTable = new int[256 * 8];

	static
	{
	
		int index = 0;
		for (int i = 0; i < 256; i++)
		{
			expansionTable[index++] = (i & 128) == 0 ? (byte)0 : (byte)1;
			expansionTable[index++] = (i & 64) == 0 ? (byte)0 : (byte)1;
			expansionTable[index++] = (i & 32) == 0 ? (byte)0 : (byte)1;
			expansionTable[index++] = (i & 16) == 0 ? (byte)0 : (byte)1;
			expansionTable[index++] = (i & 8) == 0 ? (byte)0 : (byte)1;
			expansionTable[index++] = (i & 4) == 0 ? (byte)0 : (byte)1;
			expansionTable[index++] = (i & 2) == 0 ? (byte)0 : (byte)1;
			expansionTable[index++] = (i & 1) == 0 ? (byte)0 : (byte)1;
		}
	}

	static
	{
	
		int index = 0;
		for (int i = 0; i < 256; i++)
		{
			intExpansionTable[index++] = (i & 128) == 0 ? 0xff000000 : 0xffffffff;
			intExpansionTable[index++] = (i & 64) == 0 ? 0xff000000 : 0xffffffff;
			intExpansionTable[index++] = (i & 32) == 0 ? 0xff000000 : 0xffffffff;
			intExpansionTable[index++] = (i & 16) == 0 ? 0xff000000 : 0xffffffff;
			intExpansionTable[index++] = (i & 8) == 0 ? 0xff000000 : 0xffffffff;
			intExpansionTable[index++] = (i & 4) == 0 ? 0xff000000 : 0xffffffff;
			intExpansionTable[index++] = (i & 2) == 0 ? 0xff000000 : 0xffffffff;
			intExpansionTable[index++] = (i & 1) == 0 ? 0xff000000 : 0xffffffff;
		}
	}

	// Version which supports an offset into input[] to fetch data.
	// inputOffset is now specified in pixels
	public static void expandOneBitPixels(byte[] input, byte[] output, 
										  int count, int inputOffset, int outputOffset)
	{
		// if not byte-aligned, how many bits of the leading byte should be unpacked
		int leadingBits = inputOffset % 8;
		inputOffset /= 8;
		// if we're not starting byte-aligned
		if (leadingBits != 0) {
			// total bits to unpack
			leadingBits = 8 - leadingBits;
			// unpack the leading pixels
			System.arraycopy(expansionTable, ((((int)input[inputOffset++]) & 0xff) << 3) + 8 - leadingBits,
											 output, outputOffset, leadingBits);
			outputOffset += leadingBits;
		}
		// if the end is not byte-aligned, how many bits of the trailing byte should be unpacked
 		int remainder = count % 8;
		int max = inputOffset + count / 8;

		// unpack complete bytes
 		for (int i = inputOffset; i < max; i++)
 		{
			System.arraycopy(expansionTable, (input[i] & 0xFF) << 3, output, outputOffset, 8);
			outputOffset += 8;
 		}
 		if (remainder != 0)
 		{
			System.arraycopy(expansionTable, (input[max - 1] & 0xff) << 3, output, outputOffset, remainder);
		}
	}

	// Version which supports an offset into input[] to fetch data.
	// inputOffset is now specified in pixels
	public static void expandOneBitPixelsToBW(byte[] input, int[] output, 
											  int count, int inputOffset, int outputOffset)
	{
		// if not byte-aligned, how many bits of the leading byte should be unpacked
		int leadingBits = inputOffset % 8;
		inputOffset /= 8;
		// if we're not starting byte-aligned
		if (leadingBits != 0) {
			// total bits to unpack
			leadingBits = 8 - leadingBits;
			// unpack the leading pixels
			System.arraycopy(intExpansionTable, ((input[inputOffset++]) << 3) + 8 - leadingBits,
											 output, outputOffset, leadingBits);
			outputOffset += leadingBits;
		}
		// if the end is not byte-aligned, how many bits of the trailing byte should be unpacked
 		int remainder = count % 8;
		int max = inputOffset + count / 8;

		// unpack complete bytes
 		for (int i = inputOffset; i < max; i++)
 		{
			System.arraycopy(intExpansionTable, (input[i] & 0xFF) << 3, output, outputOffset, 8);
			outputOffset += 8;
 		}
 		if (remainder != 0)
 		{
			System.arraycopy(intExpansionTable, (input[max] & 0xff) << 3, output, outputOffset, remainder);
		}
	}

	public static void expandOneBitPixels(byte[] input, byte[] output, int count)
	{

 		int remainder = count % 8;
		int max = count / 8;
 		for (int i = 0; i < max; i++) {
		
			int lookup = (input[i] & 0xFF) * 8;
			System.arraycopy(expansionTable, lookup, output, i * 8, 8);
			
 		}
 		if (remainder != 0)
 		{
			System.arraycopy(expansionTable, (input[max] & 0xff) * 8, output, max * 8, remainder);
		}

	}

	/**
	 * This is more general version of JimiUtil.packPixels() but restricted
	 * for 1 bit pixels.
	 * This method copies 1 bit pixels from in[] to out[] and putting 8
	 * 1 bit pixels into each out[] byte.
	 *
	 * @param in the source array containing one-pixel-per-byte
	 * @param inByte starting byte-index in source
	 * @param out the buffer to pack 8-bit-per-pixel bytes into
	 * @param base the starting byte-index in out
	 * @param outPixel the bit-index of the first pixel to copy to
	 * @param len how many pixels [1 pixel per byte] of data from 'in' to copy
	 **/
	public static void packOneBitPixels(byte[] in, int inByte, byte[] out, int base, int outPixel, int len)
	{

		int src_index = inByte;
		// allow for outPixel >= 8
		base += outPixel / 8;
		outPixel %= 8;

		// number of full bytes to copy, excluding possible partial bytes at the start/end
		int count = outPixel == 0 ? len / 8 : (len - (8 - outPixel)) / 8;

		int work = 0;

		// first index to start filling whole bytes at
		// if the input is not byte-aligned, this will be the second byte
		int start_idx = outPixel == 0 ? 0 : 1;

		int dest_index = base + (outPixel == 0 ? 0 : 1);

		// the number of bits to fill in the leading byte are 'outPixel' and all
		// bits to its right, or the length - whichever is smaller
		int leading_bits = (len < (8 - outPixel)) ? len : 8 - outPixel;
		if (outPixel != 0)
		{
			// first pixel to set is 'outPixel' bits from the MSB
			int first_pixel = 7 - outPixel;
			// fill in bits in leading byte if the first pixel isn't byte-aligned
			for (int i = 0; i < leading_bits; i++)
			{
				if (in[src_index++] == 0)
					out[base] &= ~(1 << (first_pixel - i));
				else
					out[base] |= 1 << (first_pixel - i);
			}
		}

		for (int i = start_idx; i < count; i++)
		{
			work = 0;
			
			if((in[src_index++]) != 0) {
				work += 128;
			}
			if((in[src_index++]) != 0) {
				work += 64;
			}
			if((in[src_index++]) != 0) {
				work += 32;
			}
			if((in[src_index++]) != 0) {
				work += 16;
			}
			if((in[src_index++]) != 0) {
				work += 8;
			}
			if((in[src_index++]) != 0) {
				work += 4;
			}
			if((in[src_index++]) != 0) {
				work += 2;
			}
			if((in[src_index++]) != 0) {
				work += 1;
			}

			out[dest_index + i] = (byte)work;
		}

		// number of pixels beyond the last full byte
		int remainder;
		if (outPixel == 0)
			remainder = len % 8;
		else
			remainder = len - (8 - outPixel) % 8;

		remainder %= 8;

		// index of the last byte to fill in
		int last_index = base + count;

		// fill in bits
		if (remainder > 0)
		{
			for (int i = 0; i < remainder; i++)
			{
				if (in[src_index++] == 0)
					out[last_index] &= ~(1 << (7 - i));
				else
					out[last_index] |= 1 << (7 - i);
			}
		}
	}

	/**
	 * Take pixels from buf each of depth 'depth' and stored in the
	 * least significant bits of byte. Then pack several of them 
	 * into each output byte array. This method only handles pixel
	 * depths that pack evenly into bytes - 1, 2, 4, 8
	 * pixel depth of 8 is supported as a straight copy.
	 * The pixels are packed to most significant bits of outputs bytes first.
	 *
	 * @param depth depth of each pixel in buffer buf
	 * @param in input set of pixels
	 * @param out the output buffer for packed pixels. The processing
	 * continues until all pixels in the input buf are output to out[]
	 * @return the number of bytes used of in output to out. This includes any
	 * trailing bytes which may only be partially used.
	 **/
	public static void packPixels(int[] in, int sindex, byte[] out, int dindex, int len)
	{
		int idx = sindex;
		int fullbytes = len / 8;
		for (int i = 0; i < fullbytes; i++) {
			int code = 0;

			if (in[idx++] == -1) code |= 1 << 7;
			if (in[idx++] == -1) code |= 1 << 6;
			if (in[idx++] == -1) code |= 1 << 5;
			if (in[idx++] == -1) code |= 1 << 4;
			if (in[idx++] == -1) code |= 1 << 3;
			if (in[idx++] == -1) code |= 1 << 2;
			if (in[idx++] == -1) code |= 1 << 1;
			if (in[idx++] == -1) code |= 1;

			out[dindex + i] = (byte)code;
		}
		int remainder = len % 8;
		int ridx = 7;
		int rval = 0xff;
		while (remainder-- > 0) {
			if (in[idx++] != -1) rval &= ~(1 << ridx--);
		}
		out[dindex + fullbytes] = (byte)rval;

	}

	/**
	 * Take pixels from buf each of depth 'depth' and stored in the
	 * least significant bits of byte. Then pack several of them 
	 * into each output byte array. This method only handles pixel
	 * depths that pack evenly into bytes - 1, 2, 4, 8
	 * pixel depth of 8 is supported as a straight copy.
	 * The pixels are packed to most significant bits of outputs bytes first.
	 *
	 * @param depth depth of each pixel in buffer buf
	 * @param in input set of pixels
	 * @param out the output buffer for packed pixels. The processing
	 * continues until all pixels in the input buf are output to out[]
	 * @return the number of bytes used of in output to out. This includes any
	 * trailing bytes which may only be partially used.
	 **/
	public static int packPixels(int depth, byte[] in, byte[] out)
	{
		int inLen;
		int retVal;
		int mask = (1 << depth) - 1;
		int rem;        // remainder of bytes in that dont perfectly fit in a byte
		int end;        // length of wholes bytes to output to out
		int endIdx;
		int val;

		inLen = in.length;
		switch (depth)
		{
		 case 1: // 8 per byte
			 rem = inLen % 8;	// remainder
			 end = inLen / 8;	// number of whole bytes to fill output
			 retVal = end + 1;		// number bytes used of out[]

			 // handle remainder
			 endIdx = inLen;
			 val = 0;
			 switch (rem)
			 {
				case 7:
					val |= ((in[--endIdx] & mask) << 1);
					// no break;
				case 6:
					val |= ((in[--endIdx] & mask) << 2);
					// no break;
				case 5:
					val |= ((in[--endIdx] & mask) << 3);
					// no break;
				case 4:
					val |= ((in[--endIdx] & mask) << 4);
					// no break;
				case 3:
					val |= ((in[--endIdx] & mask) << 5);
					// no break;
				case 2:
					val |= ((in[--endIdx] & mask) << 6);
					// no break;
				case 1:
					val |= ((in[--endIdx] & mask) << 7);
					out[end] = (byte)val;
					break;
				case 0:	// ?
					--retVal;	// no remainder so one less
					break;
			 }

			 // handle full bytes to out[] from in[]
			 endIdx = inLen - rem;
			 for (int outIdx = end; --outIdx >= 0; )
			 {
				 out[outIdx] = (byte)(
					 ((in[--endIdx] & mask)     ) |
					 ((in[--endIdx] & mask) << 1) |
					 ((in[--endIdx] & mask) << 2) |
					 ((in[--endIdx] & mask) << 3) |
					 ((in[--endIdx] & mask) << 4) |
					 ((in[--endIdx] & mask) << 5) |
					 ((in[--endIdx] & mask) << 6) |
					 ((in[--endIdx] & mask) << 7) );
			 }
			 return retVal;

		 case 2:	// 4 per byte
			 rem = inLen % 4;	// remainder
			 end = inLen / 4;	// number of whole bytes to fill output
			 retVal = end + 1;		// number bytes used of out[]

			 // handle remainder
			 endIdx = inLen;
			 val = 0;
			 switch (rem)
			 {
				case 3:
					val |= ((in[--endIdx] & mask) << 2);
					// no break;
				case 2:
					val |= ((in[--endIdx] & mask) << 4);
					// no break;
				case 1:
					val |= ((in[--endIdx] & mask) << 6);
					out[end] = (byte)val;
					break;
				case 0:	// ?
					--retVal;	// no remainder so one less
					break;
			 }

			 // handle full bytes to out[] from in[]
			 endIdx = inLen - rem;
			 for (int outIdx = end; --outIdx >= 0; )
			 {
				 out[outIdx] = (byte) (
					 ((in[--endIdx] & mask)     ) |
					 ((in[--endIdx] & mask) << 2) |
					 ((in[--endIdx] & mask) << 4) |
					 ((in[--endIdx] & mask) << 6) );
			 }
			 return retVal;

		 case 4:	// 2 per byte
			 rem = inLen % 2;	// remainder
			 end = inLen / 2;	// number of whole bytes to fill output
			 retVal = end + 1;		// number bytes used of out[]

			 // handle remainder
			 endIdx = inLen;
			 val = 0;
			 switch (rem)
			 {
				case 1:
					val |= ((in[--endIdx] & mask) << 4);
					out[end] = (byte)val;
					break;
				case 0:	// ?
					--retVal;	// no remainder so one less byte
					break;
			 }

			 // handle full bytes to out[] from in[]
			 endIdx = inLen - rem;
			 for (int outIdx = end; --outIdx >= 0; )
			 {
				 out[outIdx] = (byte) (
					 ((in[--endIdx] & mask)     ) |
					 ((in[--endIdx] & mask) << 4) );
			 }
			 return retVal;

		 case 8:	// simple case
			 System.arraycopy(in, 0, out, 0, inLen);
			 return inLen;

		 default:
			 throw new IllegalArgumentException("depth must be 1 2 4 or 8, not " + depth);
		}
	}

	/**
	 * Designed to change the bit depth representation of pixel data.
	 * Convert from depth bits per pixel representations in a byte upto
	 * newDepth bits per pixel representation. 
	 * depth must be < newDepth.
	 * Example use for converting 2 bit grayscale image pixels into 8 bit
	 * grayscale image pixels equivalent.
	 *
	 * @param depth size of the pixel data stored in each entry in buf
	 * @param buf buffer of one pixel per byte[] data to be modified so that
	 * pixel
	 * @param newDepth the depth to convert each pixel to in buf.
	 **/
	public static void pixelDepthChange(int depth, byte[] buf, int newDepth)
	{
		if (newDepth <= depth)
			throw new IllegalArgumentException("pixelDepth Must < newPixelSize");

		int i;
		int pixelShift = newDepth - depth;
		int newDepthVal = (1 << newDepth) - 1;
		int oldDepthVal = (1 << depth) - 1;
		for (i = buf.length; --i >= 0; )
			buf[i] = (byte)((buf[i] * newDepthVal) / oldDepthVal);
	}


	/**
	 * Take a JimiImage which is really a raster image and return the JimiRasterImage
	 * object for it.  This is useful when the object itself is a proxy and you want
	 * to get to the wrapped image.
	 */
	public static JimiRasterImage asJimiRasterImage(JimiImage ji)
	{
		// if this is a JimiRasterImage
		if (ji instanceof JimiRasterImage) {
			return (JimiRasterImage)ji;
		}
		// if this is a proxy
		else if (ji instanceof JimiImageHandle) {
			try {
				return asJimiRasterImage(((JimiImageHandle)ji).getWrappedJimiImage());
			}
			catch (JimiException je) {
				return null;
			}
		}
		// if there is no JimiRasterImage here
		else {
			return null;
		}
	}

	public static boolean flagSet(int flaggedValue, int flag)
	{
		return (flaggedValue & flag) != 0;
	}

	public static boolean flagsSet(int flaggedValue, int flags)
	{
		return (flaggedValue & flags) == flags;
	}

	public static JimiImageFactory stripStamping(JimiImageFactory imageFactory)
	{
		if (JimiLicenseInformation.isCrippled()) {
			return imageFactory;
		}
		JimiImageFactory realFactory = imageFactory;
		//while ((realFactory instanceof JimiImageFactoryProxy) &&
		//(!(realFactory instanceof VMemStampedJimiImageFactory))) {
		while (realFactory instanceof JimiImageFactoryProxy) {
			realFactory = ((JimiImageFactoryProxy)realFactory).getProxiedFactory();
		}
		if ((realFactory instanceof MemoryJimiImageFactory) ||
			//(realFactory instanceof VMemStampedJimiImageFactory)) {
			(realFactory.getClass().getName().equals("com.sun.jimi.core.VMemStampedJimiImageFactory"))) {
			imageFactory = realFactory;
		}
		return imageFactory;
	}

	public static boolean isCompatibleWithJavaVersion(int major, int minor)
	{
		try {
			String version = System.getProperty("java.version");
			int vmMajor = Integer.parseInt(version.substring(0, version.indexOf('.')));
			String minorString = version.substring(version.indexOf(".") + 1);
			if (minorString.indexOf('.') != -1) {
				minorString = minorString.substring(0, minorString.indexOf('.'));
			}
			int vmMinor = Integer.parseInt(minorString);

			boolean result =  (vmMajor > major) || ((vmMajor == major) && vmMinor >= minor);
			return result;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Run commands contained in a Vector of Runnables.
	 */
	public static void runCommands(Vector commands)
	{
		Enumeration enum = commands.elements();
		while (enum.hasMoreElements()) {
			((Runnable)enum.nextElement()).run();
		}
	}

}
