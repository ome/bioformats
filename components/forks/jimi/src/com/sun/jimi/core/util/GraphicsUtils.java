package com.sun.jimi.core.util;

import java.awt.*;
import java.awt.image.*;

/**
 * Class with some stateless static utility methods for common
 * graphics-related operations.
 * @author Luke Gorrie
 * @author Karl Avedal
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 **/

public class GraphicsUtils
{
	protected static Canvas waitComponent;

	public static void waitForImage(Image image)
	{
		if (waitComponent == null) {
			waitComponent = new Canvas();
		}
		waitForImage(waitComponent, image);
	}

	public static void waitForImage(Component comp, Image img) {

		MediaTracker mt = new MediaTracker(comp);
		mt.addImage(img, 0);
		try { mt.waitForAll(); } catch (InterruptedException e) {}

	}

	// asynchronous version using an ImageObserver
	// fast hack to try something

	public static void waitForImage(Component comp, Image img, ImageObserver imo) {

		final MediaTracker mt = new MediaTracker(comp);
		final Image image = img;
		final ImageObserver imageobserver = imo;
		final Component fcomp = comp;

		new Thread(new Runnable()
				   {
					   public void run() {
						   mt.addImage(image, 0);
						   try { mt.waitForAll(); } catch (InterruptedException e) {}
						   imageobserver.imageUpdate(image, 0, 0, 0, image.getWidth(null), image.getHeight(null));
					   }
				   }).start();

		return;

	}

}
