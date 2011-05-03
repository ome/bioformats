package com.sun.jimi.core.util.lzw;

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;

/**
 * Class to retrieve sets of bits from an input stream.
 * This class only tested so far with bit sizes varying from 1 to 12
 * After construction <code>setNumBits</code> must be caleed before any
 * call <code>read</code>.
 *
 * @author	Robin Luiten
 * @version	$Revision: 1.1.1.1 $
 **/
public class BitInput
{
	/** data source for lzw compressed data **/
	InputStream in_;

	/** 32 bit buffer for reading bit values **/
	int	bits_;

	/** number of bits available in bits_ **/
	int bitsCount_;

	/** for GIF number of bytes to next compressed chunk of bytes **/
	int byteCount_;

	/** number of bits to read for a single value returned **/
	int numBits_;

	/** bit mask for masking off read value in our bit buffer **/
	int numBitsMask_;

	/** if compressed data stream contains byte count blocks as in GIF **/
	boolean blocks_;

	/**
	 * BitInput consturctor. <code>setNumBits</code> must be called before any calls to <code>
	 * read</code>.
	 *
	 * @param in input source for data to read as bits
	 * @param blocks if true then first byte red is treated
	 * as a byte count of following bytes which are compressed.
	 **/
	public BitInput(InputStream in, boolean blocks)
	{
		in_ = in;
		blocks_ = blocks;

		bits_ = 0;
		bitsCount_ = 0;
		byteCount_ = 0;
	}

	/**
	 * @param setCodeSize the number of bits to read to get code
	 **/
	public void setNumBits(int numBits)
	{
		numBits_ = numBits;
		numBitsMask_ = (1 << numBits_) - 1;
	}

	/**
	 * @return a value of length bits as last set by setCodeSize
	 * @exception IOException thrown if error reading underlying input stream
	 * @exception EOFException thrown if end of file reached.
	 **/
	public int read() throws IOException
	{
		// only retreive more data if current full fill code request
		while (bitsCount_ < numBits_)
		{
			if (blocks_)	// GIF
			{
				if (byteCount_ == 0)
				{
					byteCount_ = in_.read();
					if (byteCount_ == -1)
						throw new EOFException();
					byteCount_ &= 0xFF;
				}
				--byteCount_;
			}

			int b = in_.read();
			if (b == -1)
				throw new EOFException();

			if (blocks_)	// GIF
				bits_ = (bits_ & ((1 << bitsCount_) -1)) | ((b & 0xFF) << bitsCount_);
			else
				bits_ = ((bits_ << 8) & 0xFFFFFF00) | (b & 0xFF);
			bitsCount_ += 8;
		}

		// extract code from the bit buffer
		int code = 0;
		if (blocks_)	// GIF
		{
			code = bits_ & numBitsMask_;
			bits_ >>>= numBits_;
		}
		else
			code = (bits_ >>> (bitsCount_ - numBits_)) & numBitsMask_;

		bitsCount_ -= numBits_;
		return code;
	}

	/**
	 * This method allows the LZWDecompressor to skip past the end of
	 * input compressed GIF block data.
	 *
	 * When called byteCount_ is always non-zero.
	 **/
	public void gifFinishBlocks() throws IOException
	{
		if (blocks_)	// ie in GIF mode
		{
			while (true)
			{
				if (byteCount_ == 0)		// get next block count
				{
					byteCount_ = in_.read();
					if (byteCount_ == -1)
						throw new EOFException();
					byteCount_ &= 0xFF;

					if (byteCount_ == 0)	// end of blocks
						break;				// finished
				}

				if (byteCount_ != 0)
				{
					int dummy = in_.read();	// eat a byte.
					--byteCount_;
				}
			}
		}
	}

}
