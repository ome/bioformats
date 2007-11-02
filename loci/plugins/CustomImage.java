//
// CustomImage.java
//

package loci.plugins;

import ij.*;
import ij.plugin.frame.ContrastAdjuster;
import ij.process.*;
import java.awt.*;
import java.awt.image.*;
import loci.formats.FormatTools;

/**
 * Adapted from ij.CompositeImage - http://rsb.info.nih.gov/ij/
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/CustomImage.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/CustomImage.java">SVN</a></dd></dl>
 */
public class CustomImage extends ImagePlus {

	int[] awtImagePixels;
	boolean newPixels;
	MemoryImageSource imageSource;
	ColorModel imageColorModel;
	Image awtImage;
	int[][] pixels;
	ImageProcessor[] cip;
	Color[] colors = {Color.red, Color.green, Color.blue, Color.white, Color.cyan,
    Color.magenta, Color.yellow};
	int currentChannel = 0;
	static int count;
	boolean singleChannel;
  protected String order;
  protected int z, t, channels;
  boolean autoscale;

	public CustomImage(ImagePlus imp, String order, int z, int t, int channels,
    boolean autoscale)
  {
    this.z = z;
    this.t = t;
    this.channels = channels;
    this.order = "XYCTZ";
    this.autoscale = autoscale;

    if (!this.order.equals(order)) {
      // reorder stack
      ImageStack oldStack = imp.getStack();
      ImageStack newStack =
        new ImageStack(oldStack.getWidth(), oldStack.getHeight());

      for (int zz=0; zz<z; zz++) {
        for (int tt=0; tt<t; tt++) {
          for (int cc=0; cc<channels; cc++) {
            int ndx = FormatTools.getIndex(order, z, channels, t,
              oldStack.getSize(), zz, cc, tt) + 1;
            newStack.addSlice(oldStack.getSliceLabel(ndx),
              oldStack.getProcessor(ndx));
          }
        }
      }

      imp.setStack(imp.getTitle(), newStack);
    }

    ImageStack stack2;
		boolean isRGB = imp.getBitDepth() == 24;
		if (isRGB) stack2 = getRGBStack(imp);
		else stack2 = imp.getStack();
		int stackSize = stack2.getSize();
		if (channels < 2 || (stackSize % channels) != 0) {
			throw new IllegalArgumentException(
      "channels<2 or stacksize not multiple of channels");
	  }
    compositeImage = true;
    setDimensions(channels, stackSize / channels, 1);
		setup(channels, stack2);
		setStack(imp.getTitle(), stack2);
		setCalibration(imp.getCalibration());
		Object info = imp.getProperty("Info");
		if (info != null) setProperty("Info", imp.getProperty("Info"));
	}

  public int getStackSize() {
    return z * t * channels;
  }

  public int getImageStackSize() {
    return z * t * channels;
  }

	public Image getImage() {
		if (img == null) updateImage();
		return img;
	}

	public void updateChannelAndDraw() {
		singleChannel = true;
		updateAndDraw();
	}

	public ImageProcessor getChannelProcessor() {
		return cip[currentSlice];
	}

	void setup(int channels, ImageStack stack2) {
   	cip = new ImageProcessor[stack2.getSize()];
    for (int i=0; i<cip.length; ++i) {
			cip[i] = stack2.getProcessor(i + 1);
			cip[i].resetMinAndMax();
			cip[i].setColorModel(createModelFromColor(colors[i % channels]));
		}
	}

	public void updateImage() {
		int imageSize = width * height;
		int nChannels = channels;
		int redValue, greenValue, blueValue, alphaValue;
		int slice = getCurrentSlice();

		if (nChannels == 1) {
			pixels = null;
			awtImagePixels = null;
			if (ip != null) img = ip.createImage();
			return;
		}

		if (cip != null && cip[0].getWidth() != width ||
      cip[0].getHeight() != height || (pixels != null &&
      pixels.length != nChannels))
    {
			setup(nChannels, getStack());
			pixels = null;
			awtImagePixels = null;
			if (slice > nChannels) {
				setSlice(1);
				slice = 1;
			}
		}
		if (slice > nChannels) slice = nChannels;

		if (slice - 1 != currentChannel && autoscale) {
			currentChannel = slice - 1;
			getProcessor().setMinAndMax(cip[currentChannel].getMin(),
        cip[currentChannel].getMax());
			ContrastAdjuster.update();
		}

		if (awtImagePixels == null) {
			awtImagePixels = new int[imageSize];
			newPixels = true;
			imageSource = null;
		}
		if (pixels == null || pixels.length != nChannels ||
      pixels[0].length != imageSize)
    {
			pixels = new int[nChannels][imageSize];
		}

		ImageProcessor iip = getProcessor();
		if (autoscale) cip[currentChannel].setMinAndMax(iip.getMin(), iip.getMax());
		if (singleChannel) {
			PixelGrabber pg = new PixelGrabber(cip[currentChannel].createImage(),
        0, 0, width, height, pixels[currentChannel], 0, width);
			try { pg.grabPixels(); }
			catch (InterruptedException e) { };
		}
    else {
      int[] coords = FormatTools.getZCTCoords(order, z, nChannels, t,
        cip.length, currentSlice - 1);
      coords[1] = 0;
      for (int i=0; i<nChannels; ++i) {
        int ndx = FormatTools.getIndex(order, z, nChannels, t, cip.length,
          coords[0], i, coords[2]);
        PixelGrabber pg = new PixelGrabber(cip[ndx].createImage(),
          0, 0, width, height, pixels[i], 0, width);
  			try { pg.grabPixels(); }
  		  catch (InterruptedException e){ };
      }
    }
		if (singleChannel && nChannels <= 3) {
			switch (currentChannel) {
				case 0:
					for (int i=0; i<imageSize; ++i) {
						redValue = (pixels[0][i] >> 16) & 0xff;
						awtImagePixels[i] =
              (awtImagePixels[i] & 0xff00ffff) | (redValue << 16);
					}
					break;
				case 1:
					for (int i=0; i<imageSize; ++i) {
						greenValue = (pixels[1][i] >> 8) & 0xff;
						awtImagePixels[i] = (awtImagePixels[i] & 0xffff00ff) |
              (greenValue << 8);
					}
					break;
				case 2:
					for (int i=0; i<imageSize; ++i) {
						blueValue = pixels[2][i] & 0xff;
						awtImagePixels[i] = (awtImagePixels[i] & 0xffffff00) | blueValue;
					}
					break;
		    case 3:
          for (int i=0; i<imageSize; ++i) {
            alphaValue = pixels[3][i] & 0xff;
            awtImagePixels[i] =
              (awtImagePixels[i] & 0xffffff) | (alphaValue << 24);
          }
          break;
      }
		}
    else {
			for (int i=0; i<imageSize; ++i) {
				redValue = 0; greenValue = 0; blueValue = 0; alphaValue = 0;
				for (int j=0; j<nChannels; ++j) {
					redValue += (pixels[j][i] >> 16) & 0xff;
					greenValue += (pixels[j][i]>>8) & 0xff;
					blueValue += (pixels[j][i]) & 0xff;
				  alphaValue += ((pixels[j][i]) >> 24) & 0xff;
          if (redValue > 255) redValue = 255;
					if (greenValue > 255) greenValue = 255;
					if (blueValue > 255) blueValue = 255;
          if (alphaValue > 255) alphaValue = 255;
        }
				awtImagePixels[i] = (alphaValue << 24) | (redValue << 16) |
          (greenValue << 8) | (blueValue);
			}
		}
		if (imageSource == null) {
      if (channels == 4) {
        imageColorModel = new DirectColorModel(32, 0xff0000, 0xff00, 0xff,
          0xff000000);
      }
      else imageColorModel = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
			imageSource = new MemoryImageSource(width, height, imageColorModel,
        awtImagePixels, 0, width);
			imageSource.setAnimated(true);
			imageSource.setFullBufferUpdates(true);
			awtImage = Toolkit.getDefaultToolkit().createImage(imageSource);
			newPixels = false;
		}
    else if (newPixels){
			imageSource.newPixels(awtImagePixels, imageColorModel, 0, width);
			newPixels = false;
		}
    else imageSource.newPixels();
		if (img == null && awtImage != null) img = awtImage;
		singleChannel = false;
	}

	ImageStack getRGBStack(ImagePlus imp) {
		ImageProcessor iip = imp.getProcessor();
		int w = iip.getWidth();
		int h = iip.getHeight();
		int size = w*h;
		byte[] r = new byte[size];
		byte[] g = new byte[size];
		byte[] b = new byte[size];
    ((ColorProcessor) iip).getRGB(r, g, b);
    ImageStack stack = new ImageStack(w, h);
		stack.addSlice("Red", r);
		stack.addSlice("Green", g);
		stack.addSlice("Blue", b);
		stack.setColorModel(iip.getDefaultColorModel());
		return stack;
	}

	public static IndexColorModel createModelFromColor(Color color) {
		byte[] rLut = new byte[256];
		byte[] gLut = new byte[256];
		byte[] bLut = new byte[256];
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		double rIncr = ((double) red) / 255d;
		double gIncr = ((double) green) / 255d;
		double bIncr = ((double) blue) / 255d;
		for (int i=0; i<256; ++i) {
			rLut[i] = (byte) (i * rIncr);
			gLut[i] = (byte) (i * gIncr);
			bLut[i] = (byte) (i * bIncr);
		}
		return new IndexColorModel(8, 256, rLut, gLut, bLut);
	}

	public Color getChannelColor() {
		int index = getCurrentSlice() - 1;
		if (index < colors.length && colors[index] != Color.white) {
      return colors[index];
	  }
  	return Color.black;
	}

	public ImageProcessor getProcessor(int channel) {
		if (cip == null || channel > cip.length) return null;
		return cip[channel-1];
	}

	public double getMin(int channel) {
		if (cip == null || channel > cip.length) return 0.0;
		return cip[channel - 1].getMin();
	}

	public double getMax(int channel) {
		if (cip == null || channel > cip.length) return 0.0;
		return cip[channel - 1].getMax();
	}

}
