package com.sun.jimi.core.decoder.png;

//
// Copyright (c) 1997, Jason Marshall.  All Rights Reserved
//
// The author makes no representations or warranties regarding the suitability,
// reliability or stability of this code.  This code is provided AS IS.  The
// author shall not be liable for any damages suffered as a result of using,
// modifying or redistributing this software or any derivitives thereof.
// Permission to use, reproduce, modify and/or (re)distribute this software is
// hereby granted.

import java.io.*;

public class MeteredInputStream extends FilterInputStream
{
    int bytesLeft;
    int marked;

    public MeteredInputStream(InputStream in, int size)
    {
        super(in);
        bytesLeft = size;
    }

    /**
     * Reads a byte. Will block if no input is available.
     * @return 	the byte read, or -1 if the end of the
     *		stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public final int read() throws IOException
    {
        if (bytesLeft > 0)
        {
            int val = in.read();
            if (val != -1)
                bytesLeft--;
    	    return val;
    	}
    	return -1;
    }

    /**
     * Reads into an array of bytes.
     * Blocks until some input is available.
     * @param b	the buffer into which the data is read
     * @return  the actual number of bytes read. Returns -1
     * 		when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public final int read(byte b[]) throws IOException
    {
    	return read(b, 0, b.length);
    }

    /**
     * Reads into an array of bytes.
     * Blocks until some input is available.
     * This method should be overridden in a subclass for
     * efficiency (the default implementation reads 1 byte
     * at a time).
     * @param b	the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return  the actual number of bytes read. Returns -1
     * 		when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public final int read(byte b[], int off, int len) throws IOException
    {
        if (bytesLeft > 0)
        {
            len = (len > bytesLeft ? bytesLeft : len);
            int read = in.read(b, off, len);
            if (read > 0)
                bytesLeft -= read;
            return read;
        }
        return -1;
    }

    /**
     * Skips bytes of input.
     * @param n 	bytes to be skipped
     * @return	actual number of bytes skipped
     * @exception IOException If an I/O error has occurred.
     */
    public final long skip(long n) throws IOException
    {
        n = (n > bytesLeft ? bytesLeft : n);
        long skipped = in.skip(n);
        if (skipped > 0)
            bytesLeft -= skipped;
        return skipped;
    }

    /**
     * Returns the number of bytes that can be read
     * without blocking.
     * @return the number of available bytes
     */
    public final int available() throws IOException
    {
        int n = in.available();
    	return (n > bytesLeft ? bytesLeft : n);
    }

    /**
     * Does NOT close the input stream. More data is to be expected
     * @exception IOException If an I/O error has occurred.
     */
    public final void close() throws IOException
    {
        // Eat this
    }

    /**
     * Marks the current position in the input stream.  A subsequent
     * call to reset() will reposition the stream at the last
     * marked position so that subsequent reads will re-read
     * the same bytes.  The stream promises to allow readlimit bytes
     * to be read before the mark position gets invalidated.
     * @param readlimit the maximum limit of bytes allowed tobe read before the
     * mark position becomes invalid.
     */
    public final void mark(int readlimit)
    {
        marked = bytesLeft;
	    in.mark(readlimit);
    }

    /**
     * Repositions the stream to the last marked position.  If the
     * stream has not been marked, or if the mark has been invalidated,
     * an IOException is thrown.  Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream.  Often this is most easily done by invoking some
     * general parser.  If the stream is of the type handled by the
     * parse, it just chugs along happily.  If the stream is not of
     * that type, the parser should toss an exception when it fails.
     * If this happens within readlimit bytes,  it allows the outer
     * code to reset the stream and try another parser.
     */
    public final void reset() throws IOException
    {
        in.reset();
        bytesLeft = marked;
    }

    /**
     * Returns true if this stream type supports mark/reset.
     */
    public final boolean markSupported()
    {
	    return in.markSupported();
    }
}
