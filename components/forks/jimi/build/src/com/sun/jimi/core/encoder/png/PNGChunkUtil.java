package com.sun.jimi.core.encoder.png;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

/**
 * Generally useful methods for writing png chunks.
 * Provides the CRC routine necessary and a generic chunk write facility
 *
 * @author	Robin Luiten
 * @version	1.0	22/Nov/1997
 **/
class PNGChunkUtil
{
	/** calculation of CRC's */
	protected CRC32 crcEngine = new CRC32();

	int getCRC()
	{
		return (int)crcEngine.getValue();
	}

	void resetCRC()
	{
		crcEngine.reset();
	}

	void updateCRC(int d)
	{
		crcEngine.update((d & 0xFF000000) >> 24);
		crcEngine.update((d & 0x00FF0000) >> 16);
		crcEngine.update((d & 0x0000FF00) >>  8);
		crcEngine.update((d & 0xFF));
	}

	void updateCRC(short d)
	{
		crcEngine.update((d & 0xFF00) >>  8);
		crcEngine.update((d & 0xFF));
	}

	void updateCRC(byte d)
	{
		crcEngine.update(d);
	}

	void updateCRC(byte[] d)
	{
		crcEngine.update(d, 0, d.length);
	}

	void updateCRC(byte[] d, int len)
	{
		crcEngine.update(d, 0, len);
	}

	void write(DataOutputStream out, byte[] id, byte[] data, int len) throws IOException
	{
		out.writeInt(len);			// chunk Length
		out.write(id);				// chunk ID
		out.write(data, 0, len);

		resetCRC();
		updateCRC(id);
		updateCRC(data, len);
		out.writeInt(getCRC());
	}
}


