/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/*
 * $RCSfile: SegmentedImageInputStream.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.2 $
 * $Date: 2007/08/28 01:12:56 $
 * $State: Exp $
 */
package com.sun.media.imageio.stream;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/**
 * An implementation of the <code>StreamSegmentMapper</code> interface
 * that requires an explicit list of the starting locations and
 * lengths of the source segments.
 */
class StreamSegmentMapperImpl implements StreamSegmentMapper {

    private long[] segmentPositions;

    private int[] segmentLengths;

    public StreamSegmentMapperImpl(long[] segmentPositions,
                                   int[] segmentLengths) {
        this.segmentPositions = (long[])segmentPositions.clone();
        this.segmentLengths = (int[])segmentLengths.clone();
    }

    public StreamSegment getStreamSegment(long position, int length) {
        int numSegments = segmentLengths.length;
        for (int i = 0; i < numSegments; i++) {
            int len = segmentLengths[i];
            if (position < len) {
                return new StreamSegment(segmentPositions[i] + position,
                                         Math.min(len - (int)position,
                                                  length));
            }
            position -= len;
        }

        return null;
    }

    public void getStreamSegment(long position, int length,
                                 StreamSegment seg) {
        int numSegments = segmentLengths.length;
        for (int i = 0; i < numSegments; i++) {
            int len = segmentLengths[i];
            if (position < len) {
                seg.setStartPos(segmentPositions[i] + position);
                seg.setSegmentLength(Math.min(len - (int)position, length));
                return;
            }
            position -= len;
        }

        seg.setStartPos(-1);
        seg.setSegmentLength(-1);
        return;
    }

    long length() {
        int numSegments = segmentLengths.length;
        long len = 0L;

        for(int i = 0; i < numSegments; i++) {
            len += segmentLengths[i];
        }

        return len;
    }
}

/**
 * An implementation of the <code>StreamSegmentMapper</code> interface
 * for segments of equal length.
 */
class SectorStreamSegmentMapper implements StreamSegmentMapper {

    long[] segmentPositions;
    int segmentLength;
    int totalLength;
    int lastSegmentLength;

    public SectorStreamSegmentMapper(long[] segmentPositions,
                                     int segmentLength,
                                     int totalLength) {
        this.segmentPositions = (long[])segmentPositions.clone();
        this.segmentLength = segmentLength;
        this.totalLength = totalLength;
        this.lastSegmentLength = totalLength -
            (segmentPositions.length - 1)*segmentLength;
    }

    public StreamSegment getStreamSegment(long position, int length) {
        int index = (int) (position/segmentLength);

        // Compute segment length
        int len = (index == segmentPositions.length - 1) ?
            lastSegmentLength : segmentLength;

        // Compute position within the segment
        position -= index*segmentLength;

        // Compute maximum legal length
        len -= position;
        if (len > length) {
            len = length;
        }
        return new StreamSegment(segmentPositions[index] + position, len);
    }

    public void getStreamSegment(long position, int length,
                                 StreamSegment seg) {
        int index = (int) (position/segmentLength);

        // Compute segment length
        int len = (index == segmentPositions.length - 1) ?
            lastSegmentLength : segmentLength;

        // Compute position within the segment
        position -= index*segmentLength;

        // Compute maximum legal length
        len -= position;
        if (len > length) {
            len = length;
        }

        seg.setStartPos(segmentPositions[index] + position);
        seg.setSegmentLength(len);
    }

    long length() {
        return (long)totalLength;
    }
}

/**
 * A <code>SegmentedImageInputStream</code> provides a view of a
 * subset of another <code>ImageInputStream</code> consiting of a series
 * of segments with given starting positions in the source stream and
 * lengths.  The resulting stream behaves like an ordinary
 * <code>ImageInputStream</code>.
 *
 * <p> For example, given a <code>ImageInputStream</code> containing
 * data in a format consisting of a number of sub-streams stored in
 * non-contiguous sectors indexed by a directory, it is possible to
 * construct a set of <code>SegmentedImageInputStream</code>s, one for
 * each sub-stream, that each provide a view of the sectors comprising
 * a particular stream by providing the positions and lengths of the
 * stream's sectors as indicated by the directory.  The complex
 * multi-stream structure of the original stream may be ignored by
 * users of the <code>SegmentedImageInputStream</code>, who see a
 * separate <code>ImageInputStream</code> for each sub-stream and do not
 * need to understand the directory structure at all.
 *
 * <p> For further efficiency, a directory structure such as in the
 * example described above need not be fully parsed in order to build
 * a <code>SegmentedImageInputStream</code>.  Instead, the
 * <code>StreamSegmentMapper</code> interface allows the association
 * between a desired region of the output and an input segment to be
 * provided dynamically.  This mapping might be computed by reading
 * from a directory in piecemeal fashion in order to avoid consuming
 * memory resources.
 */
public class SegmentedImageInputStream extends ImageInputStreamImpl {

    private ImageInputStream stream;
    private StreamSegmentMapper mapper;
    
    /**
     * Constructs a <code>SegmentedImageInputStream</code>
     * given a <code>ImageInputStream</code> as input
     * and an instance of <code>StreamSegmentMapper</code>.
     *
     * @param stream A source <code>ImageInputStream</code>
     * @param mapper An instance of the <code>StreamSegmentMapper</code>
     *        interface.
     */
    public SegmentedImageInputStream(ImageInputStream stream,
                                     StreamSegmentMapper mapper) {
        super();

        this.stream = stream;
        this.mapper = mapper;
    }

    /**
     * Constructs a <code>SegmentedImageInputStream</code> given a
     * <code>ImageInputStream</code> as input and a list of the starting
     * positions and lengths of the segments of the source stream.
     *
     * @param stream A source <code>ImageInputStream</code>
     * @param segmentPositions An array of <code>long</code>s 
     *        giving the starting positions of the segments in the
     *        source stream.
     * @param segmentLengths  An array of <code>int</code>s 
     *        giving the lengths of segments in the source stream.
     */
    public SegmentedImageInputStream(ImageInputStream stream,
                                     long[] segmentPositions,
                                     int[] segmentLengths) {
        this(stream,
             new StreamSegmentMapperImpl(segmentPositions, segmentLengths));
    }

    /**
     * Constructs a <code>SegmentedImageInputStream</code> given a
     * <code>ImageInputStream</code> as input, a list of the starting
     * positions of the segments of the source stream, the common
     * length of each segment, and the total length of the segments.
     *
     * <p> This constructor is useful for selecting substreams
     *     of sector-oriented file formats in which each segment
     *     of the substream (except possibly the final segment)
     *     occupies a fixed-length sector.
     *
     * @param stream A source <code>ImageInputStream</code>
     * @param segmentPositions An array of <code>long</code>s 
     *        giving the starting positions of the segments in the
     *        source stream.
     * @param segmentLength  The common length of each segment.
     * @param totalLength  The total length of the source segments.
     */
    public SegmentedImageInputStream(ImageInputStream stream,
                                     long[] segmentPositions,
                                     int segmentLength,
                                     int totalLength) {
        this(stream,
             new SectorStreamSegmentMapper(segmentPositions,
                                           segmentLength,
                                           totalLength));
    }

    private StreamSegment streamSegment = new StreamSegment();
    
    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read() throws IOException {
        mapper.getStreamSegment(streamPos, 1, streamSegment);
        int streamSegmentLength = streamSegment.getSegmentLength();
        if(streamSegmentLength < 0) {
            return -1;
        }
        stream.seek(streamSegment.getStartPos());

        // XXX What happens if streamSegmentLength == 0? Should this
        // method also return -1 as above for streamSegmentLength < 0?
        int val = stream.read();
        ++streamPos;
        return val;
    }

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into
     * an array of bytes.  An attempt is made to read as many as
     * <code>len</code> bytes, but a smaller number may be read, possibly
     * zero. The number of bytes actually read is returned as an integer.
     *
     * <p> This method blocks until input data is available, end of stream is
     * detected, or an exception is thrown.
     *
     * <p> If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.
     *
     * <p> If <code>off</code> is negative, or <code>len</code> is negative, or
     * <code>off+len</code> is greater than the length of the array
     * <code>b</code>, then an <code>IndexOutOfBoundsException</code> is
     * thrown.
     *
     * <p> If <code>len</code> is zero, then no bytes are read and
     * <code>0</code> is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at end of
     * stream, the value <code>-1</code> is returned; otherwise, at least one
     * byte is read and stored into <code>b</code>.
     *
     * <p> The first byte read is stored into element <code>b[off]</code>, the
     * next one into <code>b[off+1]</code>, and so on. The number of bytes read
     * is, at most, equal to <code>len</code>. Let <i>k</i> be the number of
     * bytes actually read; these bytes will be stored in elements
     * <code>b[off]</code> through <code>b[off+</code><i>k</i><code>-1]</code>,
     * leaving elements <code>b[off+</code><i>k</i><code>]</code> through
     * <code>b[off+len-1]</code> unaffected.
     *
     * <p> In every case, elements <code>b[0]</code> through
     * <code>b[off]</code> and elements <code>b[off+len]</code> through
     * <code>b[b.length-1]</code> are unaffected.
     *
     * <p> If the first byte cannot be read for any reason other than end of
     * stream, then an <code>IOException</code> is thrown. In particular, an
     * <code>IOException</code> is thrown if the input stream has been closed.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in array <code>b</code>
     *                   at which the data is written.
     * @param      len   the maximum number of bytes to read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if ((off < 0) || (len < 0) || (off + len > b.length)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }

        mapper.getStreamSegment(streamPos, len, streamSegment);
        int streamSegmentLength = streamSegment.getSegmentLength();
        if(streamSegmentLength < 0) {
            return -1;
        }
        stream.seek(streamSegment.getStartPos());

        int nbytes = stream.read(b, off, streamSegmentLength);
        streamPos += nbytes;
        return nbytes;
    }

    public long length() {
        long len;
        if(mapper instanceof StreamSegmentMapperImpl) {
            len = ((StreamSegmentMapperImpl)mapper).length();
        } else if(mapper instanceof SectorStreamSegmentMapper) {
            len = ((SectorStreamSegmentMapper)mapper).length();
        } else if(mapper != null) {
            long pos = len = 0L;
            StreamSegment seg = mapper.getStreamSegment(pos, Integer.MAX_VALUE);
            while((len = seg.getSegmentLength()) > 0) {
                pos += len;
                seg.setSegmentLength(0);
                mapper.getStreamSegment(pos, Integer.MAX_VALUE, seg);
            }
            len = pos;
        } else {
            len = super.length();
        }

        return len;
    }
}
