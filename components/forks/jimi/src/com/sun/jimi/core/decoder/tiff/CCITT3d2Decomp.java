//
// 26/Jan/1998	RL
// New source version from Alfonso
//
package com.sun.jimi.core.decoder.tiff;

import com.sun.jimi.core.JimiException;

//
//
// CCITT3d2Decomp
//
//
class CCITT3d2Decomp extends CCITT3d1Decomp
{
	private byte LastLine[];

	CCITT3d2Decomp(TiffNumberReader r, int aBitDirection)
	{
		super(r, aBitDirection);
	}

	public void begOfStrip()
	{
		bitOffset = 0;
	}

	// The table gives the number of consecutive zeros
	// starting from the msb and is indexed by byte value.
	final static byte[] zeroRuns =
	{
	    8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4,	// 0x00 - 0x0f
	    3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,	// 0x10 - 0x1f
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,	// 0x20 - 0x2f
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,	// 0x30 - 0x3f
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0x40 - 0x4f
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0x50 - 0x5f
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0x60 - 0x6f
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0x70 - 0x7f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x80 - 0x8f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x90 - 0x9f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0xa0 - 0xaf
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0xb0 - 0xbf
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0xc0 - 0xcf
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0xd0 - 0xdf
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0xe0 - 0xef
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0xf0 - 0xff
	};

	// The table gives the number of consecutive ones 
	// starting from the msb and is indexed by byte value.
	final static byte[] oneRuns =
	{
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x00 - 0x0f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x10 - 0x1f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x20 - 0x2f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x30 - 0x3f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x40 - 0x4f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x50 - 0x5f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x60 - 0x6f
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,	// 0x70 - 0x7f
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0x80 - 0x8f
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0x90 - 0x9f
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0xa0 - 0xaf
	    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,	// 0xb0 - 0xbf
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,	// 0xc0 - 0xcf
	    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,	// 0xd0 - 0xdf
	    3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,	// 0xe0 - 0xef
	    4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 7, 8,	// 0xf0 - 0xff
	};

	/**
	 * 
	 * @param lineSize	size of line in bits in LastLine
	 * @param color		the color (0 or 1) to search for change in starting at bs
	 * @param bs		start bit to search for different color from
	 * @return the number of the bit where the color has changed from param 'color'
	 **/
    public int findBitColorChange(int lineSize, int color, int bs)
	{
		byte [] runs;

		if (invertOut_)
			runs = (color == 0) ? oneRuns : zeroRuns;
		else
			runs = (color == 0) ? zeroRuns : oneRuns;

		int bits = lineSize - bs;		// number of bits to search through
		int bsIdx = bs >> 3;				// index into array of bytes to search
		int span;							// count of contiguous bits found
		int bsBit = bs & 0x7;

		// check partial byte on left hand size
		if (bits > 0 && bsBit != 0)
		{
			span = runs[(LastLine[bsIdx] << bsBit) & 0xFF];
			if (span > (8 - bsBit))		// table value too generous
				span = 8 - bsBit;
			if (span > bits)			// constrain span to bit range
				span = bits;
			if (bsBit + span < 8)		// doesn't extend to edge of byte
				return bs + span;		// return length of continuous bits found

			bits -= span;				// this many less bits to go through after this byte
			++bsIdx;					// get to next byte in search buffer
		}
		else
			span = 0;

		// scan through full bytes of all 1's or all 0's
		int bitCount;
		while (bits >= 8)
		{
			bitCount = runs[LastLine[bsIdx++] & 0xFF];
			span += bitCount;
			bits -= bitCount;
			if (bitCount < 8)			// end of continuous run
				return bs + span;		// done.
		}

		// check partial byte on right hand side
		if (bits > 0)
		{
			bitCount = runs[LastLine[bsIdx++] & 0xFF];
			span += (bitCount > bits ? bits : bitCount);
		}
		return bs + span;
	}

	// used by findB1() 
	final static int[] bitMask_ = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

    public int findB1(int A0, int bitLineSize, int A0Color)
	{
		int B1 = A0 + 1;
		if (B1 < bitLineSize)
		{
			int B1Color;
			if (A0 < 0)
				B1Color = 0;
			else
			{
				if (invertOut_)
				{
					B1Color = 1;
					if ((LastLine[A0 >> 3] & bitMask_[A0 & 0x7]) != 0)
						B1Color = 0;
				}
				else
				{
					B1Color = 0;
					if ((LastLine[A0 >> 3] & bitMask_[A0 & 0x7]) != 0)
						B1Color = 1;
				}
			}

			B1 = findBitColorChange(bitLineSize, B1Color, B1);

			if (B1 < bitLineSize)
			{
				if (invertOut_)
				{
					B1Color = 1;
					if ((LastLine[B1 >> 3] & bitMask_[B1 & 0x7]) != 0)
						B1Color = 0;
				}
				else
				{
					B1Color = 0;
					if ((LastLine[B1 >> 3] & bitMask_[B1 & 0x7]) != 0)
						B1Color = 1;
				}

				if (A0Color == B1Color)
					B1 = findBitColorChange(bitLineSize, A0Color, ++B1);
			}
		}
		return B1;
	}

	public int decode2DWord()
	{
		int curNode = 0;
		int dictWord = Dim2dDict[0];
		do
		{
			if ((bitOffset++ & 7) == 0)
				byteSource = readByte();
			else
				byteSource <<= 1;

			if (0 != (byteSource & 0x80))
				curNode = (dictWord & 0xFF00) >>> 8;
			else
				curNode = dictWord & 0xFF;

			dictWord = Dim2dDict[curNode];
		} while (0 != (dictWord & 0x10000));
		return dictWord ;
	}


	public void decodeLine(byte Dest[], int numPixels) throws JimiException
	{
		if ((LastLine == null) || (LastLine.length != Dest.length))
		{
			LastLine = new byte[Dest.length];
			if (invertOut_)
				setArrayFF(LastLine);
		}

		// initialise the output buffer to all Zero's as outputBitRun() only writes 1 bits!!!
		setArrayZero(Dest);

		// Empezamos donde no hay papel 
		int A0 = -1;
		byte curColor = (byte)0;
		int runLen;			// for modeHorizontal

		if (invertOut_)
		{
			// inverted output conditions to other branch.
			while (A0 < numPixels)
			{
				int B1 = findB1(A0, numPixels, curColor);

				if (A0 == -1)
					A0 = 0;

				int dictWord = decode2DWord();

				switch (dictWord)
				{
					case modePass:
						curColor = (byte)(curColor^1);
						B1 = findBitColorChange(numPixels, curColor , ++B1);
						if (curColor == 1)
							outputBitRun(Dest, A0, B1 - A0);
						A0 += B1 - A0;
						break;
					case modeHorizontal:
						runLen = getRunLength(curColor);
						if (curColor == 0)
							outputBitRun(Dest, A0, runLen);
						A0 += runLen;
						curColor = (byte)(curColor ^ 1);
						runLen = getRunLength(curColor);
						if (curColor == 0)
							outputBitRun(Dest, A0, runLen);
						A0 += runLen;
						break;
					case modeV0:
						if (curColor == 0)
							outputBitRun(Dest, A0, B1 - A0);
						A0 += (B1 - A0);
						break;
					case modeVR1:
						if (curColor == 0)
							outputBitRun(Dest, A0, 1 + B1 - A0);
						A0 += (1 + B1 - A0);
						break;
					case modeVR2:
						if (curColor == 0)
							outputBitRun(Dest, A0, 2 + B1 - A0);
						A0 += (2 + B1 - A0);
						break;
					case modeVR3:
						if (curColor == 0)
							outputBitRun(Dest, A0, 3 + B1 - A0);
						A0 += (3 + B1 - A0);
						break;
					case modeVL1:
						if (curColor == 0)
							outputBitRun(Dest, A0, B1 - A0 - 1);
						A0 += (B1 - A0 - 1);
						break;
					case modeVL2:
						if (curColor == 0)
							outputBitRun(Dest, A0, B1 - A0 - 2);
						A0 += (B1 - A0 - 2);
						break;
					case modeVL3:
						if (curColor == 0)
							outputBitRun(Dest, A0, B1 - A0 - 3);
						A0 += (B1 - A0 - 3);
						break;
				}
				curColor = (byte)(curColor ^ 1);
			}
		}
		else
		{
			while (A0 < numPixels)
			{
				/* Busca el primer elemento que cambia de color a la derecha de A0 
				   y de color opuesto que A0 */
				int B1 = findB1(A0, numPixels, curColor);

				// No puedo escribir donde no hay papel 
				if (A0 == -1)
					A0 = 0;

				int dictWord = decode2DWord();

				switch (dictWord)
				{
					case modePass:
						curColor = (byte)(curColor^1);
						B1 = findBitColorChange(numPixels, curColor , ++B1);
						if (curColor == 0)  // this is same as (curColor ^ 1) == 1
							outputBitRun(Dest, A0, B1 - A0);
						A0 += B1 - A0;
						break;
					case modeHorizontal:
						runLen = getRunLength(curColor);
						if (curColor == 1)
							outputBitRun(Dest, A0, runLen);
						A0 += runLen;
						curColor = (byte)(curColor ^ 1);
						runLen = getRunLength(curColor);
						if (curColor == 1)
							outputBitRun(Dest, A0, runLen);
						A0 += runLen;
						break;
					case modeV0:
						if (curColor == 1)
							outputBitRun(Dest, A0, B1 - A0);
						A0 += (B1 - A0);
						break;
					case modeVR1:
						if (curColor == 1)
							outputBitRun(Dest, A0, 1 + B1 - A0);
						A0 += (1 + B1 - A0);
						break;
					case modeVR2:
						if (curColor == 1)
							outputBitRun(Dest, A0, 2 + B1 - A0);
						A0 += (2 + B1 - A0);
						break;
					case modeVR3:
						if (curColor == 1)
							outputBitRun(Dest, A0, 3 + B1 - A0);
						A0 += (3 + B1 - A0);
						break;
					case modeVL1:
						if (curColor == 1)
							outputBitRun(Dest, A0, B1 - A0 - 1);
						A0 += (B1 - A0 - 1);
						break;
					case modeVL2:
						if (curColor == 1)
							outputBitRun(Dest, A0, B1 - A0 - 2);
						A0 += (B1 - A0 - 2);
						break;
					case modeVL3:
						if (curColor == 1)
							outputBitRun(Dest, A0, B1 - A0 - 3);
						A0 += (B1 - A0 - 3);
						break;
				}
				curColor = (byte)(curColor ^ 1);
			}
		}

		// save current line as previous for next time
		System.arraycopy(Dest, 0, LastLine , 0, Dest.length);
	}

	static public final int modePass		= 1 ;
	static public final int modeHorizontal  = 2 ;
	static public final int modeV0          = 3 ;
	static public final int modeVR1         = 4 ;
	static public final int modeVR2         = 5 ;
	static public final int modeVR3         = 6 ;
	static public final int modeVL1         = 7 ;
	static public final int modeVL2         = 8 ;
	static public final int modeVL3         = 9 ;

	/*
		La codificacin del diccionario en 2 dimensiones
		es como sigue:

			Bit 16 : 1 = Nodo no terminal
					 0 = Nodo terminal
			
			Bit 0-15 : Palabra del diccionario si Bit 16 = 0
				Bits 0-7  : Direccin de salto para 1
				Bits 8-15 : Direccin de salto para 0
	*/
	private static final int Dim2dDict[] =
	{
		0x10601,
		0x10702,
		0x10503,
		0x10409,
		modePass,
		modeHorizontal,
		modeV0,
		0x1080F,
		modeVR1,
		0x10A0C,
		0x10B10,
		modeVR2,
		0x10D00,
		0x10E11,
		modeVR3,
		modeVL1,
		modeVL2,
		modeVL3
	} ;

}

