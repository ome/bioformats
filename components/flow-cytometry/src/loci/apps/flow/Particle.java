//
// Particle.java
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

package loci.apps.flow;

public class Particle {

  private int pixelArea;
  private int micronArea;
  private int num;
  private int totalIntensity;
  private int sliceNum;
  private double pixelMicronSquared;
  private int centroid_x;
  private int centroid_y;
  private int size_x;
  private int size_y;
  private boolean active;

  public Particle(int a, int i) {
    pixelArea = a;
    totalIntensity = i;
    active = true;
  }

  public Particle(int a, int i, int minx, int maxx, int miny, int maxy) {
    pixelArea = a;
    totalIntensity = i;
    size_x = maxx - minx;
    centroid_x = (maxx+minx)/2;
    size_y = maxy - miny;
    centroid_y = (maxy+miny)/2;
    active = true;
  }

  public void setPixelsPerMicron(double ppm) {
    pixelMicronSquared = ppm;
    micronArea = (int) Math.round(pixelArea/pixelMicronSquared);
    //System.out.println("pixelMicronSquared is "+ppm);
  }

  public void print() {
    System.out.println((active?"Active":"Inactive")+": Particle "+num+
      " on slice "+sliceNum+" with area (in pixels) "+pixelArea+
      " and total intensity "+totalIntensity);
  }

  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }

  public int getSliceNum() {
    return sliceNum;
  }

  public void setSliceNum(int sliceNum) {
    this.sliceNum = sliceNum;
  }

  public int getPixelArea() {
    return pixelArea;
  }

  public int getMicronArea() {
    return micronArea;
  }

  public int getIntensity() {
    return totalIntensity;
  }

  public int getMeanIntensity() {
    if (pixelArea==0) return 0;
    else return totalIntensity/pixelArea;
  }

  public int getCentroidY() {
    return centroid_y;
  }

  public int getSizeY() {
    return size_y;
  }

  public int getCentroidX() {
    return centroid_x;
  }

  public int getSizeX() {
    return size_x;
  }

  public void deactivate() {
    active = false;
  }

  public boolean getStatus() {
    return active;
  }

}
