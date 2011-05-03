package com.sun.jimi.core.util.lzw;

import com.sun.jimi.core.util.*;
import com.sun.jimi.core.*;
import java.io.*;

/**
 * Generic LZW decompressor, with support for the TIFF variation.
 * @author Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 **/
public final class LZWDecompressor
{

	private int CLEAR_CODE;
	private int END_OF_INPUT;

	protected BitInput input_;
	protected int initialCodeSize_;
	protected int codeSize_;
	protected int limit_;
	protected boolean tiff_;
	protected LZWDecompressionStringTable table_;

	protected int count_;

	// old code
	protected int oldCode_;
	// first character for string from old code
	protected byte oldCodeFirstChar_;

	// leftovers
	protected int leftOverCode_;
	// index of first left-over character in the left-over string
	protected int leftOverIndex_;
	// leftover old code
	protected int leftOverOldCode_;
	// true if there are leftover bytes to write
	protected boolean isLeftOver_;

	/**
	 * Constructs an LZW decompressor to decode data from a stream.
	 * @param in The input stream to read LZW-encoded data from.
	 * @param codeSize The number of bits in each value.
	 * @param TIFF True if the TIFF variation of LZW is to be used.
	 **/
	public LZWDecompressor(InputStream in, int codeSize, boolean TIFF)
	{
		// input for arbitrary bit sizes
		initialCodeSize_ = codeSize;
		tiff_ = TIFF;

		CLEAR_CODE = 1 << codeSize;
		END_OF_INPUT = (1 << codeSize) + 1;

		table_ = new LZWDecompressionStringTable(codeSize);
		setInputStream(in);
		clearTable();
	}

	/**
	 * Sets a new InputStream as the LZW source and re-initializes the decoder.
	 * @param in The InputStream to read LZW-encoded data from.
	 **/
	public void setInputStream(InputStream in)
	{
 		input_ = new BitInput(in, !tiff_);
		clearTable();
		resetCodeSize();
	}

	/**
	 * Decompresses into a byte-array buffer.
	 * @param buf The buffer to fill with decoded data.
	 * @return The index in the buffer where the last value was written.
	 **/
	// I've tried to use the pseudo-code in the TIFF specification as a rough
	// template here to make them visually map onto each other to some degree
	public int decompress(byte[] buf) throws IOException
	{
		// current index in the buffer
		int index = 0;
		// value of code being processed
		int code;

		// flush out leftovers
		int leftover_len = writeLeftOver(buf);
		// incase there are leftovers from writing the previous leftovers
		if (isLeftOver_)
		{
			return buf.length;
		}
		else
			index += leftover_len;

		while (index < buf.length)
		{
			code = getNextCode();

			if (code == END_OF_INPUT) {
				break;
			}

			if (code == CLEAR_CODE)
			{
				clearTable();

				code = getNextCode();
				if (code == END_OF_INPUT)
					break;

				// store the code in the table at next index
				buf[index++] = (byte)code;
				// remember code for next pass
				oldCode_ = code;
				oldCodeFirstChar_ = (byte)code;
			}
			else // Not clear-code
			{
				// if the code is already in the table
				if (table_.contains(code))
				{
					int	len = writeCode(buf, index, code);

					// add the code to the table
					int code_index = table_.addCharString(oldCode_, buf[index]);
					// check if this code brings the number of entries to the limit for this
					// bit-size of the input
					if (code_index == limit_)
						incrementCodeSize();

					// remember code for next pass
					oldCode_ = code;
					oldCodeFirstChar_ = buf[index];

					// if the string was too large to fit into the table
					if (len < 0)
					{
						return buf.length;
					}
					else
					{
						index += len;
					}
				}
				else // not in table yet
				{
					int code_index = table_.addCharString(oldCode_, oldCodeFirstChar_);
					int len = writeCode(buf, index, code_index);
					oldCode_ = code;
					oldCodeFirstChar_ = buf[index];
					if (code_index == limit_)
						incrementCodeSize();
					if (len < 0) {
						return buf.length;
					}
					else
						index += len;
				}
			}
		}
		return index;
	}

	/**
	 * Writes a code into the buffer at a specified index.  Left-over codes
	 * not written will be tracked, and can later be flushed into a buffer
	 * using writeLeftOver.
	 * @see #writeLeftOver
	 * @param buf The buffer to write into.
	 * @param index The starting index in the buffer.
	 * @param code The code to write a string for.
	 * @return The return value has a double-meaning.  If the value is positive,
	 * it indicates how many bytes were written into the buffer.
	 * If the code is negative, it indicates how many bytes remain to be written
	 * for the code.
	 **/
	protected int writeCode(byte[] buf, int index, int code)
	{
		return writeCode(buf, index, code, 0);
	}

	/**
	 * Writes a code into the buffer at a specified index.  Left-over codes
	 * not written will be tracked, and can later be flushed into a buffer
	 * using writeLeftOver.
	 * @see #writeLeftOver
	 * @param buf The buffer to write into.
	 * @param index The starting index in the buffer.
	 * @param code The code to write a string for.
	 * @param skip The number of bytes to skip from the start of the string.
	 * @return The return value has a double-meaning.  If the value is positive,
	 * it indicates how many bytes were written into the buffer.
	 * If the code is negative, it indicates how many bytes remain to be written
	 * for the code.
	 **/
	protected int writeCode(byte[] buf, int index, int code, int skip)
	{
		int len = table_.expandCode(buf, index, code, skip);
		if (len < 0)
		{
			leftOverCode_ = code;
			leftOverOldCode_ = oldCode_;
			isLeftOver_ = true;
			leftOverIndex_ = -len;
		}
		else
			isLeftOver_ = false;

		return len;
	}

	/**
	 * Writes leftover codes into a buffer.  If there are further leftovers
	 * from the write, two things happen:  The bytes are stored as leftovers again,
	 * queuing them for write by a subsequent call to writeLeftOver, and secondly
	 * a negative value is returned.
	 * @see #writeCode
	 * @param buf The buffer to write into.
	 * @return Returns a value as described in writeCode.
	 **/
	protected int writeLeftOver(byte[] buf)
	{
		if (!isLeftOver_)
			return 0;

		int len = writeCode(buf, 0, leftOverCode_, leftOverIndex_);

		return len;
	}

	/**
	 * Reads the next code from the encrypted input.
	 * @return The code read.
	 **/
	protected int getNextCode() throws IOException
	{
		int code = input_.read();

		return code;
	}

	/**
	 * Clears the table and resets the bit-size of input data.
	 **/
	protected void clearTable()
	{
		//table_ = new LZWDecompressionStringTable();
		table_.clearTable();
		resetCodeSize();
	}

	/**
	 * Resets the bit-size of input data.
	 **/
	protected void resetCodeSize()
	{
		count_ = 0;
		codeSize_ = initialCodeSize_;
		incrementCodeSize();
	}

	/**
	 * Increments the bit-size of input data by one bit.
	 **/
	protected void incrementCodeSize()
	{
		if (codeSize_ != 12)
		{
			codeSize_++;
			limit_ = (1 << codeSize_) - 1;
			// tiff sets the limit one byte early.
			if (tiff_)
				limit_--;

			input_.setNumBits(codeSize_);
		}
	}

	/**
	 * Due to the way that decompress() is written to just retreive
	 * the required data when the end of the compressed data is reached
	 * the trailing END OF INPUT code will never be read and the Zero
	 * lenght block as the last block will never be read. This method
	 * drives the BitInput class to get past these 
	 **/
	public void gifFinishBlocks()  throws IOException
	{
		if (!tiff_)
			input_.gifFinishBlocks();
	}

}

