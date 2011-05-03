package com.sun.jimi.core.encoder.sunraster;

import java.io.OutputStream;
import java.io.IOException;

/**
 * OutputStream wrapper for writing RAS-style RLE compressed data.
 * @author Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:57 $
 **/
public class RLEOutputStream extends OutputStream
{

	/** Value of the "Flag" indication an RLE run **/
	protected static final byte FLAG_VALUE = (byte)0x80;

	/** Stream to write to. **/
	protected OutputStream out_;

	/** Value currently beingn used in a run. **/
	protected byte runValue_;

	/** Number of occurances of the value in the run. **/
	protected int runCount_;

	/**
	 * Constructs an RLE-compressed wrapper for a given OutputStream.
	 * @param out The underlying stream to write to.
	 **/
	public RLEOutputStream(OutputStream out)
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
		int run_count = runCount_;
		byte run_value = runValue_;
		byte value;
		OutputStream out = out_;

		for (int idx = offset; idx < max_length; idx++)
		{
			value = data[idx];
			// if this is the first pass
			if (run_count == 0)
			{
				run_value = value;
				run_count = 1;
			}
			// if this is another byte in the run
			else if ((value == run_value) && (run_count < 255))
			{
				// increment the run count
				run_count++;
			}
			else
			{
				// write the run to the stream
				writeRun(run_count, run_value);
				// save new run-value
				run_value = value;
				run_count = 1;
			}
		}
		// commit local-variable caches to object state
		runCount_ = run_count;
		runValue_ = run_value;
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
		byte byte_array[] = new byte[1];
		byte_array[0] = (byte) value;
		write(byte_array);
	}

	public void flush() throws IOException
	{
		writeRun();
		runCount_ = 0;
		runValue_ = -1;
	}

	protected void writeRun(int run_count, byte run_value) throws IOException
	{
		// if the run is sufficiently long to set a run-count for
		if (run_count > 2)
		{
			// flag code
			out_.write(FLAG_VALUE);
			out_.write((byte)run_count - 1);
			out_.write(run_value);
		}
		// not worth creating a run for, write as literals
		else
		{
			while (run_count-- > 0)
				writeLiteral(run_value);
		}
	}

	protected void writeRun() throws IOException
	{
		writeRun(runCount_, runValue_);
	}

	protected void writeLiteral(byte value)
		throws IOException
	{
		out_.write(value);

		// if the literal matches the flag value, a 0 must be written afterwards
		if (value == FLAG_VALUE)
			out_.write(0);
	}

}
