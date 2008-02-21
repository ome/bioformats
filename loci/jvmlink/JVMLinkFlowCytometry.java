//
// JVMLinkFlowCytometry.java
//

/*
JVMLink client/server architecture for communicating between Java and
non-Java programs using sockets.
Copyright (c) 2008 Hidayath Ansari and Curtis Rueden. All rights reserved.

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

package loci.jvmlink;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import loci.formats.ImageTools;

public class JVMLinkFlowCytometry {

  private static ImageJ ij;
  private static ImagePlus ip;

  public static void startImageJ() {
    ij = new ImageJ();
    ip = new ImagePlus("Islet images", new ByteProcessor(256,256));
  }

  public static void showImage(int width, int height, byte[] imageData) {
    //byte[] testImage = new byte[10000];
    //for (int i=0; i<5000; i++) testImage[i] = 50;
    //for (int i=0; i<5000; i++) testImage[5000+i] = 120;
    ColorModel cm = ImageTools.makeColorModel(1, DataBuffer.TYPE_BYTE);
    ByteProcessor bp = new ByteProcessor(width,height,imageData, cm);
    bp.createImage();
    ip.setProcessor("Islet images", bp);
    ip.show();
  }

}
