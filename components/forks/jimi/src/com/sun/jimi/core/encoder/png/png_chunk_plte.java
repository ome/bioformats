package com.sun.jimi.core.encoder.png;

import java.io.*;
import java.awt.image.IndexColorModel;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.JimiException;

/**
 * PLTE Critical chunk.
 *
 * @author	Robin Luiten
 * @version	1.0	22/Nov/1997
 **/
class png_chunk_plte implements PNGConstants
{
	byte[] reds;
	byte[] greens;
	byte[] blues;

	int mapSize;

	IndexColorModel cm = null;

	PNGChunkUtil pcu;

	png_chunk_plte(AdaptiveRasterImage ji, PNGChunkUtil pcu, PNGEncoder encoder) throws JimiException
	{
		if (ji.getColorModel() instanceof IndexColorModel)
		{
			cm = (IndexColorModel)ji.getColorModel();
			mapSize = cm.getMapSize();

			// Write pallete chunk
			reds = new byte[mapSize];
			greens = new byte[mapSize];
			blues = new byte[mapSize];

			cm.getReds(reds);
			cm.getBlues(blues);
			cm.getGreens(greens);
		}
		this.pcu = pcu;
	}

	/**
	 * Write the PLTE chunk to outputstream. If there is no valid palette 
	 * data to be written then no chunk is written.
	 **/
	void write(DataOutputStream out) throws IOException
	{
		if (cm != null)
		{
			// Write pallete chunk
			out.writeInt(mapSize * 3);	// chunk length
			out.write(png_PLTE);		// chunk ID

			pcu.resetCRC();
			pcu.updateCRC(png_PLTE);

			for (int i = 0; i < mapSize; ++i)
			{
				out.writeByte(reds[i]);
				out.writeByte(greens[i]);
				out.writeByte(blues[i]);

				pcu.updateCRC(reds[i]);
				pcu.updateCRC(greens[i]);
				pcu.updateCRC(blues[i]);
			}
			out.writeInt(pcu.getCRC());
		}
	}
}
