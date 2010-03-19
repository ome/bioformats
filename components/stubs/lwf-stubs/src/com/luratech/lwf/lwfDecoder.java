//
// lwfDecoder.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.luratech.lwf;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stub of the Luratech LuraWave&reg; Java decoder class.
 * NOTE: This class contains <b>NO</b> real implementation.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/stubs/lwf-stubs/src/com/luratech/lwf/lwfDecoder.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/stubs/lwf-stubs/src/com/luratech/lwf/lwfDecoder.java">SVN</a></dd></dl>
 */
public class lwfDecoder {

  public lwfDecoder(InputStream stream, String password, String licenseCode)
    throws IOException, SecurityException
  {
  }

  public int getWidth() {
    return -1;
  }

  public int getHeight() {
    return -1;
  }

  public void decodeToMemoryGray8(byte[] image, int limit,
    int quality, int scale) throws SecurityException
  {
  }

  public void decodeToMemoryGray16(short[] image, int imageoffset, int limit,
    int quality, int scale, int pdx, int pdy, int clip_x, int clip_y,
    int clip_w, int clip_h) throws SecurityException
  {
  }

}
