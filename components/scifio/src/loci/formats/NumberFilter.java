/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats;

import java.io.File;
import java.io.FileFilter;
import java.math.BigInteger;

/**
 * NumberFilter is a helper filter for FilePattern.findPattern().
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/NumberFilter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/NumberFilter.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class NumberFilter implements FileFilter {

  // -- Fields --

  /** String appearing before the numerical block. */
  private String pre;

  /** String appearing after the numerical block. */
  private String post;

  // -- Constructor --

  /**
   * Creates a filter for files containing a numerical block,
   * sandwiched between the given strings.
   */
  public NumberFilter(String pre, String post) {
    this.pre = pre;
    this.post = post;
  }

  // -- NumberFilter API methods --

  /** Gets numbers filling the asterisk positions. */
  public BigInteger getNumber(String name) {
    if (!name.startsWith(pre) || !name.endsWith(post)) return null;
    int ndx = pre.length();
    int end = name.length() - post.length();
    try { return new BigInteger(name.substring(ndx, end)); }
    catch (NumberFormatException exc) { return null; }
    catch (IndexOutOfBoundsException exc) { return null; }
  }

  /** Tests if a specified file should be included in a file list. */
  public boolean accept(String name) {
    return getNumber(name) != null;
  }

  // -- FileFilter API methods --

  /** Tests if a specified file should be included in a file list. */
  public boolean accept(File pathname) {
    return accept(pathname.getName());
  }

}
