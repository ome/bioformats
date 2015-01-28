// Public domain
// Copied from https://github.com/imagej/imagej1/blob/26fb68109dda7b429526d93bf2368ce28b8b07f9/ij/process/LUT.java
// with thanks to Wayne Rasband

package loci.common.lut.ij;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

	/** This is an indexed color model that allows an
		lower and upper bound to be specified. */
    public class LUT extends IndexColorModel implements Cloneable {
        public double min, max;
	
    /** Constructs a LUT from red, green and blue byte arrays, which must have a length of 256. */
    public LUT(byte r[], byte g[], byte b[]) {
    	this(8, 256, r, g, b);
	}
	
    /** Constructs a LUT from red, green and blue byte arrays, where 'bits' 
    	must be 8 and 'size' must be less than or equal to 256. */
    public LUT(int bits, int size, byte r[], byte g[], byte b[]) {
    	super(bits, size, r, g, b);
	}
	
	public LUT(IndexColorModel cm, double min, double max) {
		super(8, cm.getMapSize(), getReds(cm), getGreens(cm), getBlues(cm));
		this.min = min;
		this.max = max;
	}
	
	static byte[] getReds(IndexColorModel cm) {
		byte[] reds=new byte[256]; cm.getReds(reds); return reds;
	}
	
	static byte[] getGreens(IndexColorModel cm) {
		byte[] greens=new byte[256]; cm.getGreens(greens); return greens;
	}
	
	static byte[] getBlues(IndexColorModel cm) {
		byte[] blues=new byte[256]; cm.getBlues(blues); return blues;
	}
	
	public byte[] getBytes() {
		int size = getMapSize();
		if (size!=256) return null;
		byte[] bytes = new byte[256*3];
		for (int i=0; i<256; i++) bytes[i] = (byte)getRed(i);
		for (int i=0; i<256; i++) bytes[256+i] = (byte)getGreen(i);
		for (int i=0; i<256; i++) bytes[512+i] = (byte)getBlue(i);
		return bytes;
	}
	
	public LUT createInvertedLut() {
		int mapSize = getMapSize();
		byte[] reds = new byte[mapSize];
		byte[] greens = new byte[mapSize];
		byte[] blues = new byte[mapSize];	
		byte[] reds2 = new byte[mapSize];
		byte[] greens2 = new byte[mapSize];
		byte[] blues2 = new byte[mapSize];	
		getReds(reds); 
		getGreens(greens); 
		getBlues(blues);
		for (int i=0; i<mapSize; i++) {
			reds2[i] = (byte)(reds[mapSize-i-1]&255);
			greens2[i] = (byte)(greens[mapSize-i-1]&255);
			blues2[i] = (byte)(blues[mapSize-i-1]&255);
		}
		return new LUT(8, mapSize, reds2, greens2, blues2);
	}
	
	/** Creates a color LUT from a Color. */
	public static LUT createLutFromColor(Color color) {
		byte[] rLut = new byte[256];
		byte[] gLut = new byte[256];
		byte[] bLut = new byte[256];
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		double rIncr = ((double)red)/255d;
		double gIncr = ((double)green)/255d;
		double bIncr = ((double)blue)/255d;
		for (int i=0; i<256; ++i) {
			rLut[i] = (byte)(i*rIncr);
			gLut[i] = (byte)(i*gIncr);
			bLut[i] = (byte)(i*bIncr);
		}
		return new LUT(rLut, gLut, bLut);
	}

	public synchronized Object clone() {
		try {return super.clone();}
		catch (CloneNotSupportedException e) {return null;}
	}
	
	public  String toString() {
		return "rgb[0]="+colorToString(new Color(getRGB(0)))+", rgb[255]="
			+colorToString(new Color(getRGB(255)))+", min="+d2s(min,4)+", max="+d2s(max,4);
	}

	// Copied from ij.plugins.Colors
	/** Converts a Color into a string ("red", "green", #aa55ff, etc.). */
	public static String colorToString(Color color) {
		String str = color!=null?"#"+Integer.toHexString(color.getRGB()):"none";
		if (str.length()==9 && str.startsWith("#ff"))
			str = "#"+str.substring(3);
		String str2 = hexToColor(str);
		return str2!=null?str2:str;
	}

	// Copied from ij.plugins.Colors
	/** Converts a hex color (e.g., "ffff00") into "red", "green", "yellow", etc.
	Returns null if the color is not one of the eight primary colors. */
	public static String hexToColor(String hex) {
		if (hex==null) return null;
		if (hex.startsWith("#"))
			hex = hex.substring(1);
		String color = null;
		if (hex.equals("ff0000")) color = "red";
		else if (hex.equals("00ff00")) color = "green";
		else if (hex.equals("0000ff")) color = "blue";
		else if (hex.equals("000000")) color = "black";
		else if (hex.equals("ffffff")) color = "white";
		else if (hex.equals("ffff00")) color = "yellow";
		else if (hex.equals("00ffff")) color = "cyan";
		else if (hex.equals("ff00ff")) color = "magenta";
		return color;
	}
	

	// Copied from IJ
    /** Converts a number to a rounded formatted string.
        The 'decimalPlaces' argument specifies the number of
        digits to the right of the decimal point (0-9). Uses
        scientific notation if 'decimalPlaces is negative. */
    public static String d2s(double n, int decimalPlaces) {
        if (Double.isNaN(n)||Double.isInfinite(n))
            return ""+n;
        if (n==Float.MAX_VALUE) // divide by 0 in FloatProcessor
            return "3.4e38";
        double np = n;

        // TODO: Use these statically?
        DecimalFormat[] df = null;
        DecimalFormat[] sf = null;
        DecimalFormatSymbols dfs = null;

        if (n<0.0) np = -n;
        if (decimalPlaces<0) {
            decimalPlaces = -decimalPlaces;
            if (decimalPlaces>9) decimalPlaces=9;
            if (sf==null) {
                if (dfs==null)
                    dfs = new DecimalFormatSymbols(Locale.US);
                sf = new DecimalFormat[10];
                sf[1] = new DecimalFormat("0.0E0",dfs);
                sf[2] = new DecimalFormat("0.00E0",dfs);
                sf[3] = new DecimalFormat("0.000E0",dfs);
                sf[4] = new DecimalFormat("0.0000E0",dfs);
                sf[5] = new DecimalFormat("0.00000E0",dfs);
                sf[6] = new DecimalFormat("0.000000E0",dfs);
                sf[7] = new DecimalFormat("0.0000000E0",dfs);
                sf[8] = new DecimalFormat("0.00000000E0",dfs);
                sf[9] = new DecimalFormat("0.000000000E0",dfs);
            }
            return sf[decimalPlaces].format(n); // use scientific notation
        }
        if (decimalPlaces<0) decimalPlaces = 0;
        if (decimalPlaces>9) decimalPlaces = 9;
        if (df==null) {
            dfs = new DecimalFormatSymbols(Locale.US);
            df = new DecimalFormat[10];
            df[0] = new DecimalFormat("0", dfs);
            df[1] = new DecimalFormat("0.0", dfs);
            df[2] = new DecimalFormat("0.00", dfs);
            df[3] = new DecimalFormat("0.000", dfs);
            df[4] = new DecimalFormat("0.0000", dfs);
            df[5] = new DecimalFormat("0.00000", dfs);
            df[6] = new DecimalFormat("0.000000", dfs);
            df[7] = new DecimalFormat("0.0000000", dfs);
            df[8] = new DecimalFormat("0.00000000", dfs);
            df[9] = new DecimalFormat("0.000000000", dfs);
        }
        return df[decimalPlaces].format(n);
    }

    // Copied from IJ
    /** Converts a number to a rounded formatted string.
    * The 'significantDigits' argument specifies the minimum number
    * of significant digits, which is also the preferred number of
    * digits behind the decimal. Fewer decimals are shown if the 
    * number would have more than 'maxDigits'.
    * Exponential notation is used if more than 'maxDigits' would be needed.
    */
    public static String d2s(double x, int significantDigits, int maxDigits) {
        double log10 = Math.log10(Math.abs(x));
        double roundErrorAtMax = 0.223*Math.pow(10, -maxDigits);
        int magnitude = (int)Math.ceil(log10+roundErrorAtMax);
        int decimals = x==0 ? 0 : maxDigits - magnitude;
        if (decimals<0 || magnitude<significantDigits+1-maxDigits)
            return d2s(x, -significantDigits); // exp notation for large and small numbers
        else {
            if (decimals>significantDigits)
                decimals = Math.max(significantDigits, decimals-maxDigits+significantDigits);
            return d2s(x, decimals);
        }
    }
}
