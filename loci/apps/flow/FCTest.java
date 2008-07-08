import ij.*;

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
