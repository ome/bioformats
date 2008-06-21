//
// FCTest.java
//

/*
Server application for flow cytometry with WiscScan using JVMLink.
Copyright (c) 2008 Hidayath Ansari. All rights reserved.

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

package loci.apps.flow;

import ij.*;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/apps/flow/FCTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/apps/flow/FCTest.java">SVN</a></dd></dl>
 */
public class FCTest {

  public static void main(String[] args) {
    FCTest fc = new FCTest();
    fc.doStuff();
  }

  public void doStuff() {
    ImagePlus imp = IJ.openImage("particles.tiff");
    ImageStack stack = imp.getStack();
    int size = 512;

    JVMLinkFlowCytometry.init(size, size, 1);
    JVMLinkFlowCytometry.setIntensityThreshold(30);
    JVMLinkFlowCytometry.showParticles(true);
    for (int i=1; i<=stack.getSize(); i++) {
      byte[] imageData = (byte[]) stack.getPixels(i);
      JVMLinkFlowCytometry.incrementSlices();
      JVMLinkFlowCytometry.showImage(size, size, imageData);
      JVMLinkFlowCytometry.newestProcessFrame(i);
      JVMLinkFlowCytometry.updateGraph();
    }
  }

}
