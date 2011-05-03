package com.sun.jimi.core.util;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Packbits compression utility class.<p>
 * Contains both unpack and packing method for the Macintosh Packbits compression standard.<p>
 * Contains Variation for 16 bit RLE packing used in PICT files.<p>
 * Contains a main() method with some test code for the pack unpack methods.
 *
 * @author  Robin Luiten
 * @version $Revision: 1.1.1.1 $
 */
public class Packbits
{
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	   Reference Notes on RLE packbits compression algorithm. From TIFF6 spec.

	   Description

	   In choosing a simple byte-oriented run-length compression scheme, we arbitrarily
	   chose the Apple Macintosh PackBits scheme. It has a good worst case behavior
	   (at most 1 extra byte for every 128 input bytes). For Macintosh users, the toolbox
	   utilities PackBits and UnPackBits will do the work for you, but it is easy to imple-ment
	   your own routines.

	   A pseudo code fragment to unpack might look like this:

	   Loop until you get the number of unpacked bytes you are expecting:
	   Read the next source byte into n.
	   If n is between 0 and 127 inclusive, copy the next n+1 bytes literally.
	   Else if n is between -127 and -1 inclusive, copy the next byte -n+1 times.
	   Else if n is -128, noop.
	   Endloop

	   In the inverse routine, it is best to encode a 2-byte repeat run as a replicate run
	   except when preceded and followed by a literal run. In that case, it is best to merge
	   the three runs into one literal run. Always encode 3-byte repeats as replicate runs.
	   That is the essence of the algorithm. Here are some additional rules:
	   # Pack each row separately. Do not compress across row boundaries.
	   # The number of uncompressed bytes per row is defined to be (ImageWidth + 7) / 8.
	   If the uncompressed bitmap is required to have an even number of bytes per
	   row, decompress into word-aligned buffers.
	   # If a run is larger than 128 bytes, encode the remainder of the run as one or more
	   additional replicate runs.
	   When PackBits data is decompressed, the result should be interpreted as per com-pression
	   type 1 (no compression).
	* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * More general. [ not really required so unimplemented at the moment.
	 * @param inb	input buffer
	 * @param ini	where compressed data in input buffer starts
	 * @param outb	start output index
	 * @param outi	where to uncompress data to in output buffer
	 * @param len	length of the data after decompression.
	 */
	public static void unpackbits(byte[] inb, int ini, byte[] outb, int outi, int len)
	{
	}


	/**
	 * Variation for PICT - which provides short inb[] for a give outb[]
	 * This method when it exhausts input byte buffer it clears the remainder
	 * of the output buffer oub.
	 **/
	public static void unpackbitsLimit(byte[] inb, int inbLen, byte[] outb)
		throws ArrayStoreException, ArrayIndexOutOfBoundsException
	{
		int i;		// input index
		int o;		// output index
		int b;		// RLE compression marker byte
		byte rep;	// byte to replicate as required
		int end;	// end of byte replication run index

		i = 0;
		o = 0;
		for (o = 0; o < outb.length;)	// for all output data required
		{
			if (i == inbLen)		// finished input get out
				break;

			b = inb[i++]; //P.rt(" b:"+b);
			if (b >= 0)					// duplicate bytes
			{
				++b;	//P.rt(" copy:"+b);// convert to copy length
				System.arraycopy(inb, i, outb, o, b);
				i += b;					// new input location
				o += b;					// new output location
			}
			else if (b != -128) 		// replicate a byte
			{
				//P.rt(" rep:"+(-b+1));	// -b + 1  is repetition count				
				rep = inb[i++];	//P.rt(" r:"+rep);	// repetition byte
				end = o - b + 1;		// end of replication index
				for (; o < end; ++o)
					outb[o] = rep;
			}
			// if b == -128 do nothing
		}

		// clear any remaining output space left in outb
		while (o < outb.length)
			outb[o++] = 0;
	}

	/**
	 * Decompress an RLE compressed buffer of bytes as per format as
	 * described in source or in a document on TIFF packbits compression.
	 *
	 * Worst case for compression is that for every 128 bytes to
	 * compress 1 extra byte is required to encode as a copy run.
	 * therefore the input buffer should be large enough to accomodate
	 * the worst case compression to the output buffer.
	 *
	 * Throws ArrayStoreException if the array is not large enough for the
	 * unpacked data, which could be caused by corrupt input data or
	 * the array not be allocated large enough.
	 *
	 * @param inb input buffer with compressed data
	 * @param outb output buffer for decompressed data
	 * the length of the output buffer must be exact decompressed
	 * lenght of compressed input.
	 */
	public static void unpackbits(byte[] inb, byte[] outb) 
		throws ArrayStoreException, ArrayIndexOutOfBoundsException
	{
		int i;		// input index
		int o;		// output index
		int b;		// RLE compression marker byte
		byte rep;	// byte to replicate as required
		int end;	// end of byte replication run index

		i = 0;
		o = 0;
		for (o = 0; o < outb.length;)	// for all output data required
		{
			b = inb[i++]; // P.rt(" b:"+b);
			if (b >= 0)					// duplicate bytes
			{
				++b;	//P.rt(" copy:"+b);// convert to copy length
				System.arraycopy(inb, i, outb, o, b);
				i += b;					// new input location
				o += b;					// new output location
			}
			else if (b != -128) 		// replicate a byte
			{
				rep = inb[i++];	//P.rt(" r:"+rep);	// repetition byte
				end = o - b + 1;		// end of replication index
				for (; o < end; ++o)
					outb[o] = rep;
			}
			// if b == -128 do nothing
		}
	}

	/**
	 * Variation for 16 bit RLE packing used in PICT files.
	 **/
	public static void unpackbits(byte[] inb, int[] outb) 
		throws ArrayStoreException, ArrayIndexOutOfBoundsException
	{
		int i;		// input index
		int o;		// output index
		int b;		// RLE compression marker byte
		int rep;	// int [ok 16 bits] to replicate as required
		int end;	// end of byte replication run index

		i = 0;
		o = 0;
		for (o = 0; o < outb.length;)	// for all output data required
		{
			b = inb[i++]; 			//P.rt(" b:"+b);
			if (b >= 0)				// duplicate 16 bit quantities
			{
				++b;			// convert to copy length
				end = o + b;	// end of replication for 16 bits
				for (; o < end; ++o, i += 2)
					outb[o] = (((inb[i] & 0xFF) << 8) + (inb[i + 1] & 0xFF)) & 0xFFFF;
			}
			else if (b != -128) 		// replicate 16 bit quantities
			{
				//P.rt(" rep:"+(-b+1));	// -b + 1  is repetition count				
				rep = (((inb[i] & 0xff) << 8) + (inb[i+1] & 0xFF)) & 0xFFFF; // repetition value
				i +=2;

				end = o - b + 1;		// end of replication index
				for (; o < end; ++o)
					outb[o] = rep;
			}
			// if b == -128 do nothing
		}
	}

	/**
	 * @param in data input stream to read packbits data stream from
	 * @param outb byte buffer to place unpacked byte data to.
	 * It is assumed that unpackbits is called with outb big enough
	 * for a single sequence of compressed bytes
	 **/
	public static void unpackbits(DataInputStream in, byte[] outb) 
		throws ArrayStoreException, ArrayIndexOutOfBoundsException,	IOException
	{
		//		int i;		// input index
		int o;		// output index
		int b;		// RLE compression marker byte
		byte rep;	// byte to replicate as required
		int end;	// end of byte replication run index

		//		i = 0;
		o = 0;
		for (o = 0; o < outb.length;)	// for all input compressed data
		{
			//b = inb[i++];
			b = in.readByte();
			if (b >= 0)					// duplicate bytes
			{
				++b;	//P.rt(" copy:"+b);// convert to copy length
				//System.arraycopy(inb, i, outb, o, b);
				for (int j = 0; j < b; ++j)
					outb[o + j] = in.readByte();
				//				i += b;					// new input location
				o += b;					// new output location
			}
			else if (b != -128) 		// replicate a byte
			{
				//P.rt(" rep:"+(-b+1));	// -b + 1  is repetition count				
				//				rep = inb[i++];	//P.rt(" r:"+rep);	// repetition byte
				rep = in.readByte();
				end = o - b + 1;		// end of replication index
				for (; o < end; ++o)
					outb[o] = rep;
			}
			// if b == -128 do nothing
		}
	}

	/**
	 * Calculate length of every byte run in the input buffer
	 * and store into runb[] buffer. Note that the count - 1
	 * value is stored so data stored ranges from 0 to 127 which
	 * represents the values 1 to 128.
	 * e.g
	 * INPUT:  03 04 05 05 06 07 08 09 09 09
	 * OUTPUT: 00 00 01 00 00 00 02
	 * Note that a runlength of > 128 bytes is encoded as
	 * multiple runs of 128 bytes.
	 * Writing byte value of 128 into a byte array in java
	 * actually casts it to a signed value of -128.
	 *
	 * @param inb	raw data to calculate byte runs on
	 * @param runs	output to contain length of each byterun
	 *				this buffer  must be as long as input buffer
	 *				to handle worst case.
	 */
	public static int getAllRuns(byte[] inb)
	{
		int i;		// input index
		int o;		// output index in runb
		byte b;
		int repLen;
		int start;

		i = 0;
		o = 0;
		do
		{
			start = i;					// start of replicate run
			b = inb[i++];
			while (i < inb.length && b == inb[i])	// find replicate runs
				++i;
			repLen = i - start;
			while (repLen > 128)
			{

				runb[o++] = (byte)127;	// 128 - 1
				repLen -= 128;
			}
			runb[o++] = (byte)(repLen - 1);
		} while (i < inb.length);

		return o;	// number of bytes in runb[]
	}


	// states.
	final static int INITIAL = 0;
	final static int LITERAL = 1;
	final static int UNDECIDED = 2;

	/**
	 * Temp buffer for compression
	 * see getAllRuns() for size buffer required
	 */
	static byte[] runb;

	/**
	 * Compress input buffer using RLE packbits compression.
	 *
	 * The second part
	 * of the code passes through the output compressed bytes and converts
	 * any 2 by replicate runs which are between literal runs into a
	 * literal run.. [ shit cant do if literal gets > 128 then i have
	 * start inserting bytes what a suck... ]
	 *
	 *
	 * @param inb	input buffer containing bytes to be compressed
	 *				using RLE packbits algorithm. buffer is assumed
	 *				to be completely filled.
	 * @param outb	output buffer containing compressed data.
	 *				This must be large enough for worst case compression
	 *				of input buffer. [ worst case is 1 additional byte
	 *				for every 128 bytes of input buffer with a minimum
	 *				size of 2 bytes ]
	 * @return	the number of bytes in the output buffer.
	 *			It should not be possible to return a value < 2.
	 *
	 * <pre>
	 * Example Input buffer.
	 *   03 04 05 05 06 07 08
	 * Example Stoopid encoding
	 *   01 03 04 -01 05 02 06 07 08
	 * Example Proper encoding
	 *   06 03 04 05 05 06 07 08
	 * </pre>
	 */
	public static int packbits(byte[] inb, byte[] outb)
		throws ArrayStoreException, ArrayIndexOutOfBoundsException
	{
		int runbLen;		// length of replicate run data in rub[]
		int runI;			// index into replicate run buffer
		int repLen;			// length of current replicate run
		int state;			// compressor state
		int i;				// input buffer index
		int o;				// output buffer index
		int runcount;		// count of literals so far covered
		// maybe literal made up of pairs of replicate
		// bytes if state UNDECIDED

		if (runb == null || runb.length != inb.length)
			runb = new byte[inb.length];

		runbLen = getAllRuns(inb);

		runcount = 0;
		state = INITIAL;
		i = 0;
		o = 0;
		for (runI = 0; runI < runbLen; ++runI)
		{
			repLen = runb[runI] + 1;
			//P.rt("repLen " + repLen + " input " + i);
			switch (state)
			{
			case INITIAL:
				while (repLen > 128)		// encode replicates > 128
				{
					outb[o++] = (byte)(-(128 - 1));
					outb[o++] = inb[i];
					i += 128;
					repLen -= 128;
				}
				if (repLen == 1)
				{
					state = LITERAL;
					runcount = 1;
				}
				else if (repLen == 2)
				{
					state = UNDECIDED;
					runcount = 2;
				}
				else // repLen >= 3 and repLen < 128
				{
					// state = INITIAL
					outb[o++] = (byte)(-(repLen - 1));
					outb[o++] = inb[i];
					i += repLen; // advance to byte after replicate in input
				}
				break;

			case LITERAL:
				if (repLen < 3)
				{
					// state = LITERAL
					runcount += repLen;
				}
				else
				{
					state = INITIAL;

					// check for LITERAL runcount > 128, dice up as required
					while (runcount > 128)
					{
						outb[o++] = (byte)(128 - 1);// encode literal
						System.arraycopy(inb, i, outb, o, 128);
						i += 128;
						o += 128;
						runcount -= 128;
					}

					outb[o++] = (byte)(runcount - 1);// encode literal
					System.arraycopy(inb, i, outb, o, runcount);
					i += runcount;
					o += runcount;

					// check for repLen > 128, dice up into 128 chunks.
					while (repLen > 128)		// encode replicates > 128
					{
						outb[o++] = (byte)(-(128 - 1));
						outb[o++] = inb[i];
						i += 128;
						repLen -= 128;
					}
					// damn if repLen == 1 or 2 then need to handle as in INITIAL
					if (repLen == 1)
					{
						state = LITERAL;
						runcount = 1;
					}
					else if (repLen == 2)
					{
						state = UNDECIDED;
						runcount = 2;
					}
					else // repLen >= 3 and repLen < 128
					{
						outb[o++] = (byte)(-(repLen - 1));	// encode replicate
						outb[o++] = inb[i];
						i += repLen;
					}
				}
				break;

			case UNDECIDED:
				if (repLen == 1)
				{
					state = LITERAL;
					++runcount;
				}
				else if (repLen == 2)
				{
					// state = UNDECIDED;
					runcount += 2;
				}
				else // repLen > 2
				{
					state = INITIAL;
					
					// at this point may have multiple pairs of replicate
					// bytes all added into runcount followed by a replicate
					// run of > 2 bytes. So output each of the twin replicate 
					// runs then output the > 2 length replicate run.
					for (; runcount > 0; runcount -= 2)
					{
						outb[o++] = (byte)(-(2 - 1));
						outb[o++] = inb[i];
						i += 2;	// skip over replicate run just output
					}

					// check for repLen > 128, dice up into 128 chunks.
					while (repLen > 128)		// encode replicates > 128
					{
						outb[o++] = (byte)(-(128 - 1));
						outb[o++] = inb[i];
						i += 128;
						repLen -= 128;
					}
					// damn if repLen == 1 or 2 then need to handle as in INITIAL
					if (repLen == 1)
					{
						state = LITERAL;
						runcount = 1;
					}
					else if (repLen == 2)
					{
						state = UNDECIDED;
						runcount = 2;
					}
					else // repLen >= 3 and repLen < 128
					{
						// now output the > 2 length replicate run
						outb[o++] = (byte)(-(repLen - 1));
						outb[o++] = inb[i];
						i += repLen;	// skip over replicate run just output
					}
				}
				break;
			}
		}

		// finalise states
		switch (state)
		{
		case LITERAL:
			while (runcount > 128)
			{
                outb[o++] = (byte)(128 - 1);// encode literal
				System.arraycopy(inb, i, outb, o, 128);
				i += 128;
				o += 128;
				runcount -= 128;
			}
            if (runcount > 0)
        	{
    			outb[o++] = (byte)(runcount - 1);// encode literal
    			System.arraycopy(inb, i, outb, o, runcount);
    			i += runcount;
    			o += runcount;
        	}
			break;

		case UNDECIDED:
			// dump the added replicate pairs as replicates
			// to finish.
			for (; runcount > 0; runcount -= 2)
			{
				outb[o++] = (byte)(-(2 - 1));
				outb[o++] = inb[i];
				i += 2;	// skip over replicate run just output
			}
			break;
		}

		return o;
	}

}
