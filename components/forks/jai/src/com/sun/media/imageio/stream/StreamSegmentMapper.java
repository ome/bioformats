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
 * $RCSfile: StreamSegmentMapper.java,v $
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
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:21 $
 * $State: Exp $
 */
package com.sun.media.imageio.stream;

/**
 * An interface for use with the <code>SegmentedImageInputStream</code>
 * class.  An instance of the <code>StreamSegmentMapper</code>
 * interface provides the location and length of a segment of a source
 * <code>ImageInputStream</code> corresponding to the initial portion of
 * a desired segment of the output stream.
 *
 * <p> As an example, consider a mapping between a source
 * <code>ImageInputStream src</code> and a <code>SegmentedImageInputStream
 * dst</code> comprising bytes 100-149 and 200-249 of the source
 * stream.  The <code>dst</code> stream has a reference to an instance
 * <code>mapper</code> of <code>StreamSegmentMapper</code>.
 *
 * <p> A call to <code>dst.seek(0); dst.read(buf, 0, 10)</code> will
 * result in a call to <code>mapper.getStreamSegment(0, 10)</code>,
 * returning a new <code>StreamSegment</code> with a starting
 * position of 100 and a length of 10 (or less).  This indicates that
 * in order to read bytes 0-9 of the segmented stream, bytes 100-109
 * of the source stream should be read.
 *
 * <p> A call to <code>dst.seek(10); int nbytes = dst.read(buf, 0,
 * 100)</code> is somewhat more complex, since it will require data
 * from both segments of <code>src</code>.  The method <code>
 * mapper.getStreamSegment(10, 100)</code> will be called.  This
 * method will return a new <code>StreamSegment</code> with a starting
 * position of 110 and a length of 40 (or less).  The length is
 * limited to 40 since a longer value would result in a read past the
 * end of the first segment.  The read will stop after the first 40
 * bytes and an addition read or reads will be required to obtain the
 * data contained in the second segment.
 */
public interface StreamSegmentMapper {

    /**
     * Returns a <code>StreamSegment</code> object indicating the
     * location of the initial portion of a desired segment in the
     * source stream.  The length of the returned
     * <code>StreamSegment</code> may be smaller than the desired
     * length.
     *
     * @param pos The desired starting position in the
     * <code>SegmentedImageInputStream</code>, as a <code>long</code>.
     * @param length The desired segment length.
     * @return a StreamSegment object
     */
    StreamSegment getStreamSegment(long pos, int length);

    /**
     * Sets the values of a <code>StreamSegment</code> object
     * indicating the location of the initial portion of a desired
     * segment in the source stream.  The length of the returned
     * <code>StreamSegment</code> may be smaller than the desired
     * length.
     *
     * @param pos The desired starting position in the
     * <code>SegmentedImageInputStream</code>, as a <code>long</code>.
     * @param length The desired segment length.
     * @param seg A <code>StreamSegment</code> object to be overwritten.
     */
    void getStreamSegment(long pos, int length, StreamSegment seg);
}
