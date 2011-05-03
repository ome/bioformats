/*
Tabstops	4
History
18/Feb/1998	RL
	Moved out of PNGImageProducer source file to here
*/
package com.sun.jimi.core.decoder.png;

import java.util.Enumeration;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Support class, used to eat the IDAT headers dividing up the deflated stream
 **/
class IDATEnumeration implements Enumeration
{
    InputStream underlyingStream;
    PNGReader owner;
    boolean firstStream = true;

    public IDATEnumeration(PNGReader owner)
    {
        this.owner = owner;
        this.underlyingStream = owner.underlyingStream_;
    }

    public Object nextElement()
    {
        firstStream = false;
        return new MeteredInputStream(underlyingStream, owner.chunkLength);
    }

    public boolean hasMoreElements()
    {
        DataInputStream dis = new DataInputStream(underlyingStream);
        if (!firstStream)
        {
            try
            {
                int crc = dis.readInt();
                owner.needChunkInfo = false;
                owner.chunkLength = dis.readInt();
                owner.chunkType = dis.readInt();
            }
            catch (IOException ioe)
            {
                return false;
            }
        }
        if (owner.chunkType == PNGReader.CHUNK_IDAT)
            return true;
        return false;
    }
}
