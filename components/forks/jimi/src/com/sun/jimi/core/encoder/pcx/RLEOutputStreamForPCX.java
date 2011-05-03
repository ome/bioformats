package com.sun.jimi.core.encoder.pcx;

import java.io.OutputStream;
import java.io.IOException;

/**
 * OutputStream wrapper for writing PCX-style RLE compressed data (1 / 2 byte run code)
 * @author Luke Gorrie
 * @author Max Hrabrov
 * @version 1.0  $Date: 1998/12/01 12:21:56 $
 **/
 
public class RLEOutputStreamForPCX extends OutputStream
{

	protected static final byte ONEFLAG = (byte)0xC0;    // OR with this to set 2 MSBs to 1
	protected static final byte ZEROFLAG = (byte)0x3F;	 // AND with this to set 2 MSBs to 0

	/** Stream to write to. **/
	protected OutputStream out_;

	/** Value currently being used in a run. **/
	protected byte runValue_;

	/** Whethere or not the end of scan line has been reached **/
	protected boolean endOfLine = false;
	
	/** Number of occurances of the value in the run. **/
	protected int runCount_;

	/**
	 * Constructs an RLE-compressed wrapper for a given OutputStream.
	 * @param out The underlying stream to write to.
	 **/
	public RLEOutputStreamForPCX(OutputStream out)
	{
		out_ = out;
	}

	/**
	 * Compresses a byte array and writes it to the stream.
	 * @param data The data to compress.
	 **/
	protected synchronized void compressBytes(byte[] data, int offset, int len)
		throws IOException
	{
		// cache instance fields in local variables
		int max_length = len;
		int count = runCount_;
		byte runValue = runValue_;
		byte newValue;
		OutputStream out = out_;
		
	
/** 

	The encoding scheme:

	Use a 2-byte run code scheme. The first byte of the 2-byte run code is as follows: 2 MSBs = 1, 
	6 LSBs = run count (number of pixels of the same color).  The second byte of the 2-byte run code is 
	the color value in the range of 0 to 256.

**/	

		count = 0;
		runValue = data[offset];

		for (int idx = offset; idx < max_length; idx++)
		{
			newValue = data[idx];

			// if this is another byte in the run
			if ((newValue == runValue) && (count < 63) && (idx != (max_length-1)))
			{
				// increment the run count
				count++;
			}
			else
			{
				// write the run to the stream
				writeRun(count, runValue);
				// save new run-value
				runValue = newValue;
				count = 1;
			}
		}
		// commit local-variable caches to object state
		runCount_ = count;
		runValue_ = runValue;
	}

	/**
	 * Compresses a byte array and writes it to the stream.
	 * @param data The data to compress.
	 **/
	public void write(byte[] data) throws IOException
	{
		 write(data, 0, data.length);
	}

	/**
	 * Compresses a byte array and writes it to the stream.
	 * @param data The data to compress.
	 * @param offset The offset in the data to start from.
	 * @param length The length of data to write.
	 **/
	 
	public void write(byte[] data, int offset, int length)
		throws IOException
	{
		compressBytes(data, offset, length);
	}

	/**
	 * Compresses a byte into the stream.  This method is not optimal, use of
	 * the array-based write where possible is encouraged.
	 * @param value The data to compress.
	 **/
	public void write(int value) throws IOException
	{
		write(new byte[] { (byte)value });
	}

	public void flush() throws IOException
	{
		writeRun();  
		runCount_ = 0;
		runValue_ = -1;
	}

	protected void writeRun(int run_count, byte value) throws IOException
	{


		byte count  = ((byte)(run_count));


		count |= ONEFLAG;			
		out_.write(count);         // 2 first bits are 11 - FLAG, last 6 bits - count
		out_.write(value);
				
	}
	

	protected void writeRun() throws IOException
	{
		writeRun(runCount_, runValue_);
	}

}

