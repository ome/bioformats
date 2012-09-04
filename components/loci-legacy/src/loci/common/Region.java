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

/**
 * A legacy delegator class for ome.scifio.common.Region.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/Region.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/Region.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Region extends ome.scifio.common.Region {

  // -- Constructors --
  
  public Region() {
    super();
  }
  
  public Region(int x, int y, int w, int h) {
    super(x,y,w,h);
  }
  
  // -- Wrapper API --
  
  public Region intersection(Region r) {
    return convertRegion(super.intersection(r));
  }
  
  // -- Object delegators --

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
  
  @Override
  public int hashCode() {
    return super.hashCode();
  }
  
  @Override
  public String toString() {
    return super.toString();
  }
  
  // -- Helper Methods --
  
  /*
   * ---HACK---
   * Returns a loci.common Region based on the provided
   *  {@link ome.scifio.common.Region}.
   *  
   *  This is necessary because loci.common.Region has to directly
   *  extend ome.scifio.common.Region, because of the public field
   *  contract that was established in this class.
   *  
   *  Extension was not sufficient for full backwards compatibility,
   *  as ome.scifio.common.Region#intersection always returns an
   *  ome.scifio.common.Region which can not be safely cast to a loci.common
   *  Region.
   *  
   *  Thus this method constructs a loci.common Region assuming the base
   *  ome.scifio.common.Region was provided.
   */
  private Region convertRegion(ome.scifio.common.Region r) {
    return new Region(r.x, r.y, r.width, r.height);
  }

}
