//
// CCITT3d1Decomp
//
// 26/Jan/1998 RL
// Modifications to update from Alfonso
//
package com.sun.jimi.core.decoder.tiff;

//import TiffNumberReader ;
//import Decompressor ;
//import TiffFormatException ;

import com.sun.jimi.core.JimiException;


class CCITT3d1Decomp extends Decompressor
{

	protected byte byteSource ;		// Byte procesado actualmente
	protected int  bitOffset ;		// Offset del bit procesado


	CCITT3d1Decomp(TiffNumberReader r ,int aBitOrder)
	{
		super( r, aBitOrder );
	}		

	final static int[] trailMask  = { 0, 0x80, 0xc0, 0xe0, 0xf0, 0xf8, 0xfc, 0xfe, 0xff };
	final static int[] leadMask = { 0, 0x7f, 0x3f, 0x1f, 0x0f, 0x07, 0x03, 0x01 };

	/**
	 * Output a row of 1 bits to the given offset for the given count.
	 *
	 * @param dest	destination bit packed byte array to output run of pixels to
	 * @param off	offset in dest to start placing run of pixels
	 * @param count	the number of pixels to output in run
	 **/
	public void outputBitRun(byte[] dest, int off, int count)
	{
		if (count <= 0)
			return;

		int i = off >> 3;
		int bit = off & 0x7;	// value range 0 to 7.

		if (bit != 0)			// partial byte covered at start
		{
			if (count < (8 - bit))	// count less than bits left to end of byte
			{
				dest[i] |= trailMask[count] >> bit;
				return;
			}
			count -= (8 - bit);
			dest[i++] |= leadMask[bit];
		}

		// fill in complete bytes for required length
		while (count >= 8)
		{
			dest[i++] = (byte)0xFF;
			count -= 8;
		}

        if (count != 0)
    		dest[i] |= trailMask[count];
		return;
	}

	public int decode1DWord(byte pixColor) throws JimiException // throws TiffFormatException
	{
		int colorDict[] = Dim1Dict[pixColor];
		int curNode = 0;
		int dictWord = colorDict[0];
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

			if (curNode == 0)
 				throw new JimiException("Code Not Found In Dict");

			dictWord = colorDict[curNode] ;
		} while (0 != (dictWord & 0x20000));

		return dictWord;
	}

	public int getRunLength( byte pixColor ) throws JimiException // throws TiffFormatException
	{
		int dictWord   ;
		int runLength = 0 ;
		do
		{
			dictWord = decode1DWord( pixColor );
			runLength += dictWord & 0xFFFF;
		} while(0 != (dictWord & 0x10000));
		return runLength ;
	}

	public void begOfStrip()
	{
		bitOffset = 0 ;
	}
	//
	//  La siguiente lçnea siempre empieza
	// en un byte
	//
	public void endOfLine()
	{
		if ((bitOffset & 7) != 0)
			bitOffset = 0;
	}

	public void decodeLine( byte[] dest,
							int numPixels ) throws JimiException // throws TiffFormatException 
	{
		byte pixColor = 0;
		int  runLength;
		int  pixOffset = 0;

		// clear output array first, allows us to write only the 1 bit pixels. as required
		setArrayZero(dest);
		if (invertOut_)
		{
			while (pixOffset < numPixels)
			{
				runLength = getRunLength(pixColor);
				if (pixColor == 0)
					outputBitRun(dest, pixOffset, runLength);
			    pixOffset += runLength;
				pixColor ^= 1;
			}
		}
		else
		{
			while (pixOffset < numPixels)
			{
				runLength = getRunLength(pixColor);
				if (pixColor != 0)
					outputBitRun(dest, pixOffset, runLength);
			    pixOffset += runLength;
				pixColor ^= 1;
			}
		}

		endOfLine();

	}

	/*
		There are 213 Entries in white table
		There are 212 Entries in black table

			Byte 0xFF00 - next index into dictionary if next bitinput 1
			Byte 0x00FF - next index into dictionary if next bitinput 0

		Codificacin del diccionario:

			X1 X2 NN1 NN2

			NN1, NN2 = 8 bits

			X2 = 1 bit

				1 = Nodo no terminal
				0 = Nodo terminal

			X1 = 1 bit

				1 = Cdigo extendido
				0 = Cdigo final

		Si X1 = 1
			NN1 = direccin del nodo cuando
				 el scanner encuentra un 1
			NN2 = direccin del nodo cuando
				 el scanner encuentra un 0
		Si X1 = 0 
		
			NN1NN2 = Longitud de la tira de ceros o unos

	*/
	static int Dim1Dict[][] =
	{
		/* Color blanco White */
		{
			0x21001 , 
			0x20D02 , 
			0x20309 , 
			0x20424 , 
			0x22005 , 
			0x2064B , 
			0x28D07 , 
			0x20885 , 
			0x00000 , 
			0x20A27 , 
			0x20B36 , 
			0x20C34 , 
			0x00001 , 
			0x20E21 , 
			0x20F89 , 
			0x00002 , 
			0x21611 , 
			0x21412 , 
			0x21C13 , 
			0x00003 , 
			0x2151E , 
			0x00004 , 
			0x21917 , 
			0x22A18 , 
			0x00005 , 
			0x21B1A , 
			0x00006 , 
			0x00007 , 
			0x21D87 , 
			0x00008 , 
			0x22E1F , 
			0x00009 , 
			0x0000A , 
			0x24122 , 
			0x23123 , 
			0x0000B , 
			0x23925 , 
			0x24726 , 
			0x0000C , 
			0x2283C , 
			0x2293F , 
			0x0000D , 
			0x2862B , 
			0x22D2C , 
			0x0000E , 
			0x0000F , 
			0x2302F , 
			0x00010 , 
			0x00011 , 
			0x23249 , 
			0x233AE , 
			0x00012 , 
			0x25135 , 
			0x00013 , 
			0x25737 , 
			0x25438 , 
			0x00014 , 
			0x23A5E , 
			0x23B65 , 
			0x00015 , 
			0x23D4D , 
			0x23E68 , 
			0x00016 , 
			0x26B40 , 
			0x00017 , 
			0x27742 , 
			0x24543 , 
			0x26E44 , 
			0x00018 , 
			0x24671 , 
			0x00019 , 
			0x24874 , 
			0x0001A , 
			0x27F4A , 
			0x0001B , 
			0x2824C , 
			0x0001C , 
			0x24EB6 , 
			0x2504F , 
			0x0001D , 
			0x0001E , 
			0x25352 , 
			0x0001F , 
			0x00020 , 
			0x25655 , 
			0x00021 , 
			0x00022 , 
			0x25B58 , 
			0x25A59 , 
			0x00023 , 
			0x00024 , 
			0x25D5C , 
			0x00025 , 
			0x00026 , 
			0x2625F , 
			0x26160 , 
			0x00027 , 
			0x00028 , 
			0x26463 , 
			0x00029 , 
			0x0002A , 
			0x26766 , 
			0x0002B , 
			0x0002C , 
			0x26A69 , 
			0x0002D , 
			0x0002E , 
			0x26D6C , 
			0x0002F , 
			0x00030 , 
			0x2706F , 
			0x00031 , 
			0x00032 , 
			0x27372 , 
			0x00033 , 
			0x00034 , 
			0x27675 , 
			0x00035 , 
			0x00036 , 
			0x28878 , 
			0x27C79 , 
			0x27B7A , 
			0x00037 , 
			0x00038 , 
			0x27E7D , 
			0x00039 , 
			0x0003A , 
			0x28180 , 
			0x0003B , 
			0x0003C , 
			0x28483 , 
			0x0003D , 
			0x0003E , 
			0x0003F , 
			0x10040 , 
			0x10080 , 
			0x100C0 , 
			0x28A90 , 
			0x28B95 , 
			0x28CA7 , 
			0x10100 , 
			0x28F8E , 
			0x10140 , 
			0x10180 , 
			0x291B4 , 
			0x29892 , 
			0x29493 , 
			0x101C0 , 
			0x10200 , 
			0x2A096 , 
			0x29D97 , 
			0x10240 , 
			0x2999A , 
			0x10280 , 
			0x29C9B , 
			0x102C0 , 
			0x10300 , 
			0x29F9E , 
			0x10340 , 
			0x10380 , 
			0x2A4A1 , 
			0x2A3A2 , 
			0x103C0 , 
			0x10400 , 
			0x2A6A5 , 
			0x10440 , 
			0x10480 , 
			0x2ABA8 , 
			0x2AAA9 , 
			0x104C0 , 
			0x10500 , 
			0x2ADAC , 
			0x10540 , 
			0x10580 , 
			0x2B2AF , 
			0x2B1B0 , 
			0x105C0 , 
			0x10600 , 
			0x2B5B3 , 
			0x10640 , 
			0x10680 , 
			0x106C0 , 
			0x2B7D0 , 
			0x2BBB8 , 
			0x2C2B9 , 
			0x2BFBA , 
			0x10700 , 
			0x2C9BC , 
			0x2BEBD , 
			0x10740 , 
			0x10780 , 
			0x2C1C0 , 
			0x107C0 , 
			0x10800 , 
			0x2C6C3 , 
			0x2C5C4 , 
			0x10840 , 
			0x10880 , 
			0x2C8C7 , 
			0x108C0 , 
			0x10900 , 
			0x2CDCA , 
			0x2CCCB , 
			0x10940 , 
			0x10980 , 
			0x2CFCE , 
			0x109C0 , 
			0x10A00 , 
			0x200D1 , 
			0x200D2 , 
			0x200D3 , 
			0x2D400 , 
			0x10000 
		},
		{
			/* Color negro Black */
			0x20D01 , 
			0x20B02 , 
			0x21103 , 
			0x21404 , 
			0x2051D , 
			0x20619 , 
			0x21C07 , 
			0x20823 , 
			0x20933 , 
			0x20A36 , 
			0x00000 , 
			0x2100C , 
			0x00001 , 
			0x20E0F , 
			0x00002 , 
			0x00003 , 
			0x00004 , 
			0x21213 , 
			0x00005 , 
			0x00006 , 
			0x21516 , 
			0x00007 , 
			0x21718 , 
			0x00008 , 
			0x00009 , 
			0x21B1A , 
			0x0000A , 
			0x0000B , 
			0x0000C , 
			0x21E2B , 
			0x2211F , 
			0x22520 , 
			0x0000D , 
			0x22228 , 
			0x0000E , 
			0x23024 , 
			0x0000F , 
			0x2263B , 
			0x22780 , 
			0x00010 , 
			0x23829 , 
			0x26A2A , 
			0x00011 , 
			0x22CB6 , 
			0x2412D , 
			0x23E2E , 
			0x2712F , 
			0x00012 , 
			0x23145 , 
			0x23249 , 
			0x00013 , 
			0x25634 , 
			0x25335 , 
			0x00014 , 
			0x26037 , 
			0x00015 , 
			0x2394C , 
			0x23A5D , 
			0x00016 , 
			0x2633C , 
			0x26E3D , 
			0x00017 , 
			0x23F7D , 
			0x24086 , 
			0x00018 , 
			0x27742 , 
			0x27443 , 
			0x29244 , 
			0x00019 , 
			0x2468E , 
			0x24847 , 
			0x0001A , 
			0x0001B , 
			0x24B4A , 
			0x0001C , 
			0x0001D , 
			0x2504D , 
			0x24F4E , 
			0x0001E , 
			0x0001F , 
			0x25251 , 
			0x00020 , 
			0x00021 , 
			0x25554 , 
			0x00022 , 
			0x00023 , 
			0x25A57 , 
			0x25958 , 
			0x00024 , 
			0x00025 , 
			0x25C5B , 
			0x00026 , 
			0x00027 , 
			0x25F5E , 
			0x00028 , 
			0x00029 , 
			0x26261 , 
			0x0002A , 
			0x0002B , 
			0x26764 , 
			0x26665 , 
			0x0002C , 
			0x0002D , 
			0x26968 , 
			0x0002E , 
			0x0002F , 
			0x28A6B , 
			0x26D6C , 
			0x00030 , 
			0x00031 , 
			0x2706F , 
			0x00032 , 
			0x00033 , 
			0x27B72 , 
			0x29A73 , 
			0x00034 , 
			0x27594 , 
			0x27697 , 
			0x00035 , 
			0x28D78 , 
			0x2A379 , 
			0x2A07A , 
			0x00036 , 
			0x27C9D , 
			0x00037 , 
			0x2847E , 
			0x2AA7F , 
			0x00038 , 
			0x28881 , 
			0x28382 , 
			0x00039 , 
			0x0003A , 
			0x285AD , 
			0x0003B , 
			0x2B087 , 
			0x0003C , 
			0x29189 , 
			0x0003D , 
			0x28C8B , 
			0x0003E , 
			0x0003F , 
			0x10040 , 
			0x2908F , 
			0x10080 , 
			0x100C0 , 
			0x10100 , 
			0x293B3 , 
			0x10140 , 
			0x29695 , 
			0x10180 , 
			0x101C0 , 
			0x29998 , 
			0x10200 , 
			0x10240 , 
			0x29C9B , 
			0x10280 , 
			0x102C0 , 
			0x29F9E , 
			0x10300 , 
			0x10340 , 
			0x2A2A1 , 
			0x10380 , 
			0x103C0 , 
			0x2A7A4 , 
			0x2A6A5 , 
			0x10400 , 
			0x10440 , 
			0x2A9A8 , 
			0x10480 , 
			0x104C0 , 
			0x2ACAB , 
			0x10500 , 
			0x10540 , 
			0x2AFAE , 
			0x10580 , 
			0x105C0 , 
			0x2B2B1 , 
			0x10600 , 
			0x10640 , 
			0x2B5B4 , 
			0x10680 , 
			0x106C0 , 
			0x2B7D0 , 
			0x2BBB8 , 
			0x2C2B9 , 
			0x2BFBA , 
			0x10700 , 
			0x2C9BC , 
			0x2BEBD , 
			0x10740 , 
			0x10780 , 
			0x2C1C0 , 
			0x107C0 , 
			0x10800 , 
			0x2C6C3 , 
			0x2C5C4 , 
			0x10840 , 
			0x10880 , 
			0x2C8C7 , 
			0x108C0 , 
			0x10900 , 
			0x2CDCA , 
			0x2CCCB , 
			0x10940 , 
			0x10980 , 
			0x2CFCE , 
			0x109C0 , 
			0x10A00 , 
			0x200D1 , 
			0x200D2 , 
			0x200D3 , 
			0x10000 
		}
	} ;


	final static void setArrayZero(byte[] array)
	{
		int zLen = array.length;
		int offset = 0;
		while (zLen >= ABYTECOUNT)
		{
			System.arraycopy(zeroBytes_, 0, array, offset, ABYTECOUNT);
			offset += ABYTECOUNT;
			zLen -= ABYTECOUNT;
		}
		if (zLen > 0)
			System.arraycopy(zeroBytes_, 0, array, offset, zLen);
	}

	final static void setArrayFF(byte[] array)
	{
		int zLen = array.length;
		int offset = 0;
		while (zLen >= ABYTECOUNT)
		{
			System.arraycopy(ffBytes_, 0, array, offset, ABYTECOUNT);
			offset += ABYTECOUNT;
			zLen -= ABYTECOUNT;
		}
		if (zLen > 0)
			System.arraycopy(ffBytes_, 0, array, offset, zLen);
	}

	final static int ABYTECOUNT = 128;
	final static byte[] zeroBytes_ =
	{
		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,

		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,
		0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0
	};
	final static byte[] ffBytes_ =
	{
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,

       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
       (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF
	};

}
