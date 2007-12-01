//
// ND_to_Image6D.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

// NB: This class is intentionally packageless.

import java.io.IOException;

public class ND_to_Image6D {
  public native boolean openNd2(String dir, String name)  throws IOException;
  public native int getNd2Param(String name)  throws IOException;
  public native boolean copyNd2ImageByte(byte[] pixels, int ch, int z, int t, int n)  throws IOException;
  public native boolean copyNd2ImageShort(short[] pixels, int ch, int z, int t, int n)  throws IOException;
  public native boolean copyNd2ImageInt(int[] pixels, int ch, int z, int t, int n)  throws IOException;
  public native boolean copyNd2ImageRGB(byte[] pixelsR, byte[] pixelsG, byte[] pixelsB, int ch, int z, int t, int n)  throws IOException;
  public native String getNd2ImagePoint(int ch, int z, int t, int n)  throws IOException;
  public native double getNd2ImageTime(int ch, int z, int t, int n)  throws IOException;
  public native double getNd2ImageZPos(int ch, int z, int t, int n)  throws IOException;
  public native String getNd2ImageWave(int ch, int z, int t, int n)  throws IOException;
  public native String getNd2ImageObjective(int ch, int z, int t, int n)  throws IOException;
  public native String getNd2ImageModality(int ch, int z, int t, int n)  throws IOException;
  public native boolean closeNd2()  throws IOException;
}
