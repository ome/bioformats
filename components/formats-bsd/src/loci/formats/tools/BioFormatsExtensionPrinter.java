/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
*/

package loci.formats.tools;

import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import loci.common.Constants;

/**
 * This class is used to generate a text file containing all of the
 * image file extensions supported by Bio-Formats.
 *
 * @author Mark Hiner hiner at wisc.edu
 */
public class BioFormatsExtensionPrinter {

	public static void main(String[] args) throws IOException {
		System.out.println("Generating list of Bio-Formats supported suffixes...");
		IFormatReader reader = new ImageReader();
		String[] suffixes = reader.getSuffixes();

		PrintWriter fo = null;

		fo =
      new PrintWriter(new File("BioFormatsSuffixes.txt"), Constants.ENCODING);

		for (String s : suffixes) fo.println("*." + s);

		fo.close();
    reader.close();
		System.out.println(suffixes.length + " suffixes discovered.");
	}

}
