/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.common;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A legacy delegator class for ome.scifio.common.IniParser.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/IniParser.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/IniParser.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IniParser {

  // -- Fields --
  
  private ome.scifio.common.IniParser parser;
  
  // -- Constructor --
  
  public IniParser() {
    parser = new ome.scifio.common.IniParser();
  }

  // -- IniParser API methods --

  /**
   * Set the String that identifies a comment.  Defaults to "#".
   */
  public void setCommentDelimiter(String delimiter) {
    parser.setCommentDelimiter(delimiter);
  }

  /**
   * Set whether or not a '\' at the end of a line signifies that the
   * line continues on the following line.
   *
   * By default, a '\' does continue the line.
   */
  public void setBackslashContinuesLine(boolean slashContinues) {
    parser.setBackslashContinuesLine(slashContinues);
  }

  /** Parses the INI-style configuration data from the given resource. */
  public IniList parseINI(String path)
    throws IOException
  {
    return parseINI(openTextResource(path));
  }

  /**
   * Parses the INI-style configuration data from the given resource,
   * using the given class to find the resource.
   */
  public IniList parseINI(String path, Class<?> c)
    throws IOException
  {
    IniList iList = new IniList();
    iList.list =  parser.parseINI(path, c);
    return iList;
  }

  /** Parses the INI-style configuration data from the given input stream. */
  public IniList parseINI(BufferedReader in)
    throws IOException
  {
    IniList iList = new IniList();
    iList.list =  parser.parseINI(in);
    return iList;
  }

  // -- Utility methods --

  /** Opens a buffered reader for the given resource. */
  public static BufferedReader openTextResource(String path) {
    return ome.scifio.common.IniParser.openTextResource(path, IniParser.class);
  }

  /** Opens a buffered reader for the given resource. */
  public static BufferedReader openTextResource(String path, Class<?> c) {
    return ome.scifio.common.IniParser.openTextResource(path, c);
  }
  
  // -- Object delegators --

  @Override
  public boolean equals(Object obj) {
    return parser.equals(obj);
  }
  
  @Override
  public int hashCode() {
    return parser.hashCode();
  }
  
  @Override
  public String toString() {
    return parser.toString();
  }
}
