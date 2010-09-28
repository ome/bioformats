//
// lwfDecoder.java
//

/*
Luratech LWF library stub classes.
Copyright (c) 2010-@year@ UW-Madison LOCI.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
* Neither the name of the UW-Madison LOCI nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.luratech.lwf;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stub of the Luratech LuraWave&reg; Java decoder class.
 * NOTE: This class contains <b>NO</b> real implementation.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/stubs/lwf-stubs/src/com/luratech/lwf/lwfDecoder.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/stubs/lwf-stubs/src/com/luratech/lwf/lwfDecoder.java">SVN</a></dd></dl>
 */
public class lwfDecoder {

  // NB: This field is used to distinguish between this stub class and the
  // actual lwfDecoder implementation.
  public static final boolean IS_STUB = true;

  /**
	 * @param stream
   * @param password
   * @param licenseCode
   * @throws IOException
	 */
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

  /**
	 * @param image
   * @param limit
   * @param quality
   * @param scale
	 */
  public void decodeToMemoryGray8(byte[] image, int limit,
    int quality, int scale) throws SecurityException
  {
  }

  /**
   * @param image
   * @param imageoffset
   * @param limit
   * @param quality
   * @param scale
   * @param pdx
   * @param pdy
   * @param clip_x
   * @param clip_y
   * @param clip_w
   * @param clip_h
   * @throws SecurityException
   */
  public void decodeToMemoryGray16(short[] image, int imageoffset, int limit,
    int quality, int scale, int pdx, int pdy, int clip_x, int clip_y,
    int clip_w, int clip_h) throws SecurityException
  {
  }

}
