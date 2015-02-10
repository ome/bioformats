/*
 * #%L
 * Common package for lookup tables
 * %%
 * Copyright (C) 2015 Open Microscopy Environment:
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

package loci.common.lut;


/**
 * Generic source for loading a lookup table (LUT). The primary responsibility
 * of implementations is to take a byte array from some call to openBytes or
 * similar, apply a lookup table, and return a (possibly) transformed byte
 * array of compatible size.
 *
 * @author Josh Moore, josh at glencoesoftware.com
 */
public interface LutSource {

    /**
     * Takes a {@link byte[]} which has been loaded elsewhere (e.g. from a
     * call to {@link loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)})
     * and converts each byte based on the associated lookup table in this source.
     *
     * @param no
     * @param buf
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    byte[] applyLut(int no, byte[] buf, int x, int y, int w, int h);

    byte[][] get8BitLookupTable();

}
