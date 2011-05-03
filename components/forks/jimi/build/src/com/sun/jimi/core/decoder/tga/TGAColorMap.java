package com.sun.jimi.core.decoder.tga;

import java.io.IOException;

import com.sun.jimi.core.decoder.tga.TGAFileHeader;
import com.sun.jimi.core.util.LEDataInputStream;

/**
 * TGA color map format section.
 * This class reads in the color map data and converts it
 * to a java array of color integers.
 * This converts 16 bit colors by scaling them upto 24 bit.
 * [ though i think i should make a 16 bit ColorModel ]
 * This 16 bit color maps should be very rare....
 *
 * @author	Robin Luiten
 * @date	03/Sep/1997
 * @version 1.1
 */
public class TGAColorMap
{
	/** color map */
	byte[] cmap;

	public TGAColorMap(LEDataInputStream in, TGAFileHeader tgaFH) throws IOException
	{
		short val;
		byte b;
		byte red;
		byte green;
		byte blue;
		byte alpha;
		int i;
		int o;

		if (tgaFH.colorMapType == 1)	// color map exists
		{
			switch (tgaFH.colorMapEntrySize)
			{
			case 8:
				cmap = new byte[tgaFH.colorMapLength];
				o = 0;
				for (i = tgaFH.firstEntryIndex; i < tgaFH.colorMapLength; ++i)
				{
					b = in.readByte();
					cmap[o] = b;	// red
					++o;
					cmap[o] = b;	// green
					++o;
					cmap[o] = b;	// blue
					++o;
				}
				break;

			case 15:
			case 16:	// format short integer in bits: ARRRRRGG GGGBBBBB
				cmap = new byte[tgaFH.colorMapLength * 3];
				o = tgaFH.firstEntryIndex * 3;
				for (i = tgaFH.firstEntryIndex; i < tgaFH.colorMapLength; ++i)
				{
					val = in.readShort();

					// need to scale input 0 -> 0x1F to output 0 -> 0xFF
					// therefore  * (0xFF / 0x1F)

					// red mask binary 01111100 00000000
					cmap[o] = (byte)((((val>>10) & 0x1F) * 0xFF) / 0x1F);
					++o;
					// green mask binary 00000011 11100000
					cmap[o] = (byte)((((val>>5) & 0x1F) * 0xFF) / 0x1F);
					++o;
					// blue mask binary 00000000 00011111
					cmap[o] = (byte)(((val & 0x1F) * 0xFF) / 0x1F);
					++o;
/*
P.rt("" + tgaFH.colorMapEntrySize + ":b" + Integer.toBinaryString((int)val & 0xFFFF) +
" REDx" + Integer.toHexString((int)cmap[o-3] & 0xFFFF) +
" GRNx" + Integer.toHexString((int)cmap[o-2] & 0xFFFF) +
" BLUx" + Integer.toHexString((int)cmap[o-1] & 0xFFFF) +
"");
*/
				}
				break;

			case 24:	// format 3 bytes Blue, Green, Red
				cmap = new byte[tgaFH.colorMapLength * 3];
				o = tgaFH.firstEntryIndex * 3;
				for (i = tgaFH.firstEntryIndex; i < tgaFH.colorMapLength; ++i)
				{
					blue = in.readByte();
					green = in.readByte();
					red = in.readByte();

					cmap[o] = red;
					++o;
					cmap[o] = green;
					++o;
					cmap[o] = blue;
					++o;
				}
				break;

			case 32:	// format 4 bytes Blue, Green, Red, Alpha
				cmap = new byte[tgaFH.colorMapLength * 4];
				o = tgaFH.firstEntryIndex * 4;
				for (i = tgaFH.firstEntryIndex; i < tgaFH.colorMapLength; ++i)
				{
					blue = in.readByte();
					green = in.readByte();
					red = in.readByte();
					alpha = in.readByte();

					cmap[o] = red;
					++o;
					cmap[o] = green;
					++o;
					cmap[o] = blue;
					++o;
					cmap[o] = alpha;
					++o;
				}
				break;

			default:
				break;
			}
		}
	}
}

