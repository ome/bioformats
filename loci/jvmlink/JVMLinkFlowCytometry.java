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
