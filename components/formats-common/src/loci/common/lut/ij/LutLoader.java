// Public domain
 // Copied from https://github.com/imagej/imagej1/commit/71b92a294be18bdd188c772554f8935f222fa150
// with thanks to Wayne Rasband

package loci.common.lut.ij;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/** Opens NIH Image look-up tables (LUTs), 768 byte binary LUTs
	(256 reds, 256 greens and 256 blues), LUTs in text format,
	or generates the LUT specified by the string argument
	passed to the run() method. */
class LutLoader {

  private int lutSize;
  private byte[] reds;
  private byte[] greens;
  private byte[] blues;
  private String fileName;

	/** If 'arg'="", displays a file open dialog and opens the specified
		LUT. If 'arg' is a path, opens the LUT specified by the path. If
		'arg'="fire", "ice", etc., uses a method to generate the LUT. */
	public void run(String arg) {
		reds = new byte[256];
		greens = new byte[256];
		blues = new byte[256];
		lutSize = 256;
		int nColors = 0;

		if (arg.equals("fire")) {
			nColors = fire();
    }
		else if (arg.equals("grays")) {
			nColors = grays();
    }
		else if (arg.equals("ice")) {
			nColors = ice();
    }
		else if (arg.equals("spectrum")) {
			nColors = spectrum();
    }
		else if (arg.equals("3-3-2 RGB")) {
			nColors = rgb332();
    }
		else if (arg.equals("red")) {
			nColors = primaryColor(4);
    }
		else if (arg.equals("green")) {
			nColors = primaryColor(2);
    }
		else if (arg.equals("blue")) {
			nColors = primaryColor(1);
    }
		else if (arg.equals("cyan")) {
			nColors = primaryColor(3);
    }
		else if (arg.equals("magenta")) {
			nColors = primaryColor(5);
    }
		else if (arg.equals("yellow")) {
			nColors = primaryColor(6);
    }
		else if (arg.equals("redgreen")) {
			nColors = redGreen();
    }
		lutSize = nColors;
		if (nColors > 0) {
			if (nColors < 256) {
				interpolate(nColors);
      }
			fileName = arg;
		}
	}

	public int getLutSize() {
	    return lutSize;
	}

	public byte[] getReds() {
	    return reds;
	}

	public byte[] getGreens() {
	    return greens;
	}

	public byte[] getBlues() {
	    return blues;
	}

	// Remove void showLut(FileInfo, boolean)

	// Removed void invertLut

	int fire() {
		int[] r = {0,0,1,25,49,73,98,122,146,162,173,184,195,207,217,229,240,252,255,255,255,255,255,255,255,255,255,255,255,255,255,255};
		int[] g = {0,0,0,0,0,0,0,0,0,0,0,0,0,14,35,57,79,101,117,133,147,161,175,190,205,219,234,248,255,255,255,255};
		int[] b = {0,61,96,130,165,192,220,227,210,181,151,122,93,64,35,5,0,0,0,0,0,0,0,0,0,0,0,35,98,160,223,255};
    copyIntArrays(r, g, b);
		return r.length;
	}

	int grays() {
		for (int i=0; i<256; i++) {
			reds[i] = (byte)i;
			greens[i] = (byte)i;
			blues[i] = (byte)i;
		}
		return 256;
	}

	int primaryColor(int color) {
    boolean red = (color & 4) != 0;
    boolean green = (color & 2) != 0;
    boolean blue = (color & 1) != 0;
		for (int i=0; i<256; i++) {
      if (red) {
				reds[i] = (byte) i;
      }
      if (green) {
				greens[i] = (byte) i;
      }
      if (blue) {
				blues[i] = (byte) i;
      }
		}
		return 256;
	}

	int ice() {
		int[] r = {0,0,0,0,0,0,19,29,50,48,79,112,134,158,186,201,217,229,242,250,250,250,250,251,250,250,250,250,251,251,243,230};
		int[] g = {156,165,176,184,190,196,193,184,171,162,146,125,107,93,81,87,92,97,95,93,93,90,85,69,64,54,47,35,19,0,4,0};
		int[] b = {140,147,158,166,170,176,209,220,234,225,236,246,250,251,250,250,245,230,230,222,202,180,163,142,123,114,106,94,84,64,26,27};
    copyIntArrays(r, g, b);
		return r.length;
	}

	int spectrum() {
		Color c;
		for (int i=0; i<256; i++) {
			c = Color.getHSBColor(i/255f, 1f, 1f);
			reds[i] = (byte)c.getRed();
			greens[i] = (byte)c.getGreen();
			blues[i] = (byte)c.getBlue();
		}
		return 256;
	}

	int rgb332() {
		for (int i=0; i<256; i++) {
			reds[i] = (byte)(i&0xe0);
			greens[i] = (byte)((i<<3)&0xe0);
			blues[i] = (byte)((i<<6)&0xc0);
		}
		return 256;
	}

	int redGreen() {
		for (int i=0; i<128; i++) {
			reds[i] = (byte)(i*2);
			greens[i] = (byte)0;
			blues[i] = (byte)0;
		}
		for (int i=128; i<256; i++) {
			reds[i] = (byte)0;
			greens[i] = (byte)(i*2);
			blues[i] = (byte)0;
		}
		return 256;
	}

	void interpolate(int nColors) {
		byte[] r = new byte[nColors];
		byte[] g = new byte[nColors];
		byte[] b = new byte[nColors];
		System.arraycopy(reds, 0, r, 0, nColors);
		System.arraycopy(greens, 0, g, 0, nColors);
		System.arraycopy(blues, 0, b, 0, nColors);
		double scale = nColors/256.0;
		int i1, i2;
		double fraction;
		for (int i=0; i<256; i++) {
			i1 = (int)(i*scale);
			i2 = i1+1;
			if (i2==nColors) i2 = nColors-1;
			fraction = i*scale - i1;
			reds[i] = (byte)((1.0-fraction)*(r[i1]&255) + fraction*(r[i2]&255));
			greens[i] = (byte)((1.0-fraction)*(g[i1]&255) + fraction*(g[i2]&255));
			blues[i] = (byte)((1.0-fraction)*(b[i1]&255) + fraction*(b[i2]&255));
		}
	}

  void copyIntArrays(int[] r, int[] g, int[] b) {
    for (int i=0; i<r.length; i++) {
      reds[i] = (byte) r[i];
      greens[i] = (byte) g[i];
      blues[i] = (byte) b[i];
    }
  }

	// Removed: boolean openLut(String)
	// See comment on openTextLut

	// Removed: boolean openLut(FileInfo)
	// See comment on openTextLut

	// Removed: int openBinaryLut(FileInfo, boolean, boolean)
	// See comment on openTextLut

	// Removed int openTextLut(FileInfo)
	// TODO: This will need to be reinstated, but the
	// dependencies on TextReader make it tricky.

	// Removed void createImage(FileInfo, boolean)

	/** Opens the specified ImageJ LUT and returns
		it as an IndexColorModel. Since 1.43t. */
	public static IndexColorModel open(String path) throws IOException {
		return open(new FileInputStream(path));
	}

	/** Opens an ImageJ LUT using an InputStream
		and returns it as an IndexColorModel. Since 1.43t. */
	public static IndexColorModel open(InputStream stream) throws IOException {
		DataInputStream f = new DataInputStream(stream);
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		f.read(r, 0, 256);
		f.read(g, 0, 256);
		f.read(b, 0, 256);
		f.close();
		return new IndexColorModel(8, 256, r, g, b);
	}

	// Removed ByteProcessor createImage(IndexColorModel)

}
