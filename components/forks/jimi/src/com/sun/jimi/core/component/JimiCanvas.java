/*
 * Copyright 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.component;

import java.awt.*;
import java.awt.image.*;

import java.io.File;
import java.net.URL;

import com.sun.jimi.core.*;
import com.sun.jimi.core.raster.JimiRasterImage;
import com.sun.jimi.core.util.GraphicsUtils;
import com.sun.jimi.core.util.ProgressListener;

/**
  * An <code>java.awt.Canvas</code> for easily displaying any image in a </b>JIMI.</b> supported
  * image format, or any <code>java.awt.Image</code>. Subclasses will support using this as a Bean.
  * <p>
  * The following features exist : <br>
  * <ul>
  *		<li>Specify the image to show based upon a <code>java.net.URL</code> or a <code>java.awt.Image</code>
  *		<li>Resizable with the following policies :
  *			<ol>
  *				<li>Crop if necessary
  *				<li>Scale the image
  *				<li>Best fit the image
  *				<li>Fit to Width
  *				<li>Scrolling modes.
  *			</ol>
  *		<li>Specify a justification for the image based upon the cardinal
  *			directions of a compass.
  *		<li>Explicitly set a canvas size.
  *		<li>Automatically resize the canvas to fit a new image
  *		<li>Specify a background color for areas of the canvas unpainted by the
  *			image.
  *		<li>"Paged" scrolling, where only the visible portion of the image is buffered in an Image
  *         object.  This preserves memory significantly, and when used in conjunction with VMM
  *         can be used to view images of any size.
  *	</ul>
  * 
  * @author Christian Lucas
  * @author Luke Gorrie
  * @author Thomas Isaksen
  * @version 2.0beta
  * @since Jimi1.0
  */

public class JimiCanvas extends Container
{
	// justification modes
	public static final int NORTH = 1 << 1;
	public static final int SOUTH = 1 << 2;
	public static final int EAST  = 1 << 3;
	public static final int WEST  = 1 << 4;
	public static final int NORTHEAST = NORTH | EAST;
	public static final int NORTHWEST = NORTH | WEST;
	public static final int SOUTHEAST = SOUTH | EAST;
	public static final int SOUTHWEST = SOUTH | WEST;
	public static final int CENTER = 0;

	// resize modes
	public static final int BEST_FIT   = 0;
	public static final int CROP       = 1;
	public static final int SCALE      = 2;
	public static final int SCROLL     = 3;
	public static final int FIT_WIDTH  = 4;
	public static final int AREA       = 5;
	public static final int MULTIPAGE  = 6;
	public static final int PAGED      = 7;

   /*
   * JimiCanvas 1.0 compability stuff goes here
   */
   public static final int CROP_AS_NECESSARY = CROP;
   public static final int FIT_TO_WIDTH      = FIT_WIDTH;


	// scaling modes
	public static final int AREA_AVERAGING = 0;
	public static final int REPLICATE      = 1;

	// image locations
	protected transient Image myImage;
	protected transient ImageProducer myImageProducer;
	protected transient URL myImageLocation;

	/** This <code>JimiCanvas</code> will automatically resize to fit the
	  * <code>Image</code> if true.
	  *
	  * @see JimiCanvas#getWillResize
	  * @see JimiCanvas#setWillResize
	  */
	protected boolean willSizeToFit = true;
	// width to match with FIT_WIDTH
	protected int fitWidth = -1;
	// Renderer.
	protected JimiImageRenderer renderer;

	/**
	 * initialized policy to a default value.
	 */
	protected int justificationPolicy = CENTER;
	protected int resizePolicy        = PAGED;
	protected int scalingPolicy       = REPLICATE;

	protected ProgressListener progressListener;

	protected int loadingFlags;

	protected boolean aspectAdjust = false;

	/**
	 * No argument constructor for use in beanboxes.
	 */
	public JimiCanvas()
	{
		super();
		this.setLayout(new BorderLayout());

		// renderer, using ScrollRenderer as default.
		setResizePolicy(PAGED);
	}

	/**
	 * Creates a JimiCanvas displaying the image passed.
	 *
	 * @param anImage The image to display
	 * @see JimiCanvas#setImage
	 */
	public JimiCanvas(Image myImage)
	{
		this();
		setImage(myImage);
	}

	/**
	 *  Creates a JimiCanvas and loads the Image from the URL specified.
	 *
	 * @param aLocation Where to load the image using <strong>J.I.M.I.</strong> from
	 * @see JimiCanvas#setImageLocation
	 */
	public JimiCanvas(URL myImageLocation)
	{
		this();
		this.myImageLocation = myImageLocation;

		setImageLocation(myImageLocation);
	}

	/**
	 * Creates a JimiCanvas and loads the Image form the path specified.
	 *
	 * @param path to the image
	 * @see JimiCanvas#setImage
	 */
	public JimiCanvas(String path)
	{
		this();
		setImagePath(path);
	}

	/**
	 * Creates a canvas with a specified initial resize policy.
	 * @param resizePolicy the resize policy
	 */
	public JimiCanvas(int resizePolicy)
	{
		setLayout(new BorderLayout());
		setResizePolicy(resizePolicy);
	}

	/**
	 * set the renderer to be used
	 */
	public void setRenderer(JimiImageRenderer renderer)
	{
		this.renderer = renderer;
		if (myImage != null) {
			renderer.setImage(myImage);
		}
		else if (myImageProducer != null) {
			renderer.setImageProducer(myImageProducer);
		}
		removeAll();
		this.add(renderer.getContentPane(), BorderLayout.CENTER);
		invalidate();
		validate();
	}

	/**
	 * @return current renderer
	 */
	public JimiImageRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Set a ProgressListener to be informed of image decoding events.
	 */

	/**
	 * Creates and returns a new renderer based on resize policy
	 *
	 * @see JimiCanvas#setResizePolicy
	 * @see JimiCanvas#getResizePolicy
	 */
	protected JimiImageRenderer getRenderer(int resizePolicy)
	{
		if(resizePolicy == SCROLL) {
			renderer = new ScrollRenderer(this);
		}
		else
		if(resizePolicy == CROP) {
			renderer = new CropRenderer(this);
		}
		else
		if(resizePolicy == SCALE) {
			renderer = new ScaleRenderer(this);
		}
		else
		if(resizePolicy == BEST_FIT) {
			renderer = new BestFitRenderer(this);
		}
		else
		if(resizePolicy == FIT_WIDTH) {
			renderer = new FitWidthRenderer(this);
		}
		else
		if(resizePolicy == AREA) {
			renderer = new AreaRenderer(this);
		}
		else if (resizePolicy == PAGED) {
			renderer = new SmartScrollingRenderer(this);
		}
		
		return renderer;
	}

	/**
	 * Set a ProgressListener to be informed of image loading status.
	 */
	public void setProgressListener(ProgressListener listener)
	{
		progressListener = listener;
	}

	/**
	 * Set the flags to use for image loading.  These are the same flags as
	 * used in Jimi.getImage( .. , flags )
	 */
	public void setLoadingFlags(int flags)
	{
		loadingFlags = flags;
		loadingFlags &= ~(Jimi.SYNCHRONOUS);
	}		

	/**
	 * Load an image.
	 */
	protected ImageProducer loadImageProducer(JimiReader reader)
	{
		if (progressListener != null) {
			reader.setProgressListener(progressListener);
		}
		imageCache = new ImageCache(reader, false);
		return imageCache.getNextImage().getImageProducer();
	}

	protected ImageProducer loadImageProducer(URL location) {
		try {
			return loadImageProducer(Jimi.createJimiReader(location, loadingFlags));
		} catch (Exception e) {
			showError(e.toString());
			return null;
		}
	}

	protected ImageProducer loadImageProducer(String filename) {
		try {
			return loadImageProducer(Jimi.createJimiReader(filename, loadingFlags));
		} catch (Exception e) {
			showError(e.toString());
			return null;
		}
	}

	protected void showError(String message)
	{
		if (progressListener != null) {
			progressListener.setAbort(message);
		}
	}

	/** Check to see if we need to resize this canvas based upon the size of the
	  * image
	  *
	  * @see JimiCanvas#setWillSizeToFit
	  */
	private synchronized void doResize()
	{
		if(myImage == null) return;

		//Check to see if we resize to fit
		resize(new Dimension(myImage.getWidth(this), myImage.getHeight(this)));

		Container p = getParent();
		if(p != null)
		{
			p.invalidate();
			p.layout();
			/* Called implicitly in doLayout -Chris
			repaint();
			*/
		}
	}

	/**
	 * Set the width to be used for FIT_WIDTH
	 */
	public void setFitWidth(int width) {
		fitWidth = width;
	}

	/**
	 * @return current fitWidth
	 */
	public int getFitWidth() {
		return fitWidth;
	}


	/** Which of the nine justification policies we use to place the image
	  * on the canvas
	  *
	  * @param newPolicy	The new justification policy
	  * @see	JimiCanvas#CENTER
	  * @see	JimiCanvas#NORTH
	  * @see	JimiCanvas#NORTHEAST
	  * @see	JimiCanvas#EAST
	  * @see	JimiCanvas#SOUTHEAST
	  * @see	JimiCanvas#SOUTH
	  * @see	JimiCanvas#SOUTHWEST
	  * @see	JimiCanvas#WEST
	  * @see	JimiCanvas#NORTHWEST
	  *
	  * @see JimiCanvas#getJustificationPolicy
	  */
	public synchronized void setJustificationPolicy(int newPolicy)
	{
		justificationPolicy = newPolicy;
		if (renderer != null) {
			renderer.render();
		}
	}

	/**
	 * @return current justification policy
	 */
	public int getJustificationPolicy() {
		return justificationPolicy;
	}

	/** Do we crop the image, scale it, or scale it so that it fits best?
	  *
	  * @param	newPolicy	One of policies which specify how to fit the  image into the canvas
	  * @see	JimiCanvas#CROP
	  * @see	JimiCanvas#SCALE
	  * @see	JimiCanvas#BEST_FIT
	  * @see JimiCanvas#FIT_WIDTH
	  * @see JimiCanvas#SCROLL
	  */
	public synchronized void setResizePolicy(int newPolicy)
	{
		// set new policy
		resizePolicy = newPolicy;
		renderer = getRenderer(resizePolicy);
		renderer.getContentPane().setBackground(getBackground());
		renderer.getContentPane().setForeground(getForeground());
		setRenderer(renderer);
		if (myImage != null) {
			renderer.setImage(myImage);
		}
		else if (myImageProducer != null) {
			renderer.setImageProducer(myImageProducer);
		}
	}

	/** Do we crop the image, scale it, or scale it so that it fits best?
	  *
	  * @see	JimiCanvas#CROP
	  * @see	JimiCanvas#SCALE
	  * @see	JimiCanvas#BEST_FIT
	  * @see    JimiCanvas#FIT_WIDTH
	  * @see    JimiCanvas#SCROLL
	  */
	public int getResizePolicy() {
		return resizePolicy;
	}

	/** Set the scaling mode for this canvas. There are currently
	  * two possibilities: AreaAveraging and Replicate. Each derived from
	  * a corresponding standard ImageFilter. The AreaAveraging mode will
	  * yield the best looking results, but it takes significantly longer
	  * to execute than the Replicate mode.
	  *
	  * @param	newPolicy	One of two policies which specify how to scale the
	  *						image into the canvas
	  * @see	JimiCanvas#AREA_AVERAGING
	  * @see	JimiCanvas#REPLICATE
	  */
	public synchronized void setScalingPolicy(int newPolicy) {
		scalingPolicy = newPolicy;
	}

	/** Retrieve the scaling mode currently in effect. There are currently
	  * two possibilities: AreaAveraging and Replicate. Each derived from
	  * a corresponding standard ImageFilter. The AreaAveraging mode will
	  * yield the best looking results, but it takes significantly longer
	  * to execute than the Replicate mode does.
	  *
	  * @see	JimiCanvas#AREA_AVERAGING
	  * @see	JimiCanvas#REPLICATE
	  */
	public int getScalingPolicy() {
		return scalingPolicy;
	}

	/** If true, the canvas will resize itself so that it exactly fits the
	  * the image.
	  *
	  * @param	newPolicy	If true, will resize the canvas to fit the new image
	  * @see JimiCanvas#getWillSizeToFit
	  */
	public synchronized void setWillSizeToFit(boolean newPolicy) {
		willSizeToFit = newPolicy;
	}

	/** If true, the canvas will resize itself so that it exactly fits the canvas.
	  *
	  * @return	true if the canvas will be automatically sized to fit the image
	  * @see JimiCanvas#setWillSizeToFit
	  */
	public boolean getWillSizeToFit() {
		return willSizeToFit;
	}

	/** Where should <b>JIMI</b> look to find the image? <br>
	  * This method loads the image from the URL then calls setImage.
	  *
	  * @param newLocation	Where <b>JIMI</b> should go to find the image
	  * @see JimiCanvas#getImageLocation
	  * @see JimiCanvas#getImage
	  * @see JimiCanvas#setImage
	  */
	public synchronized void setImageLocation(URL newLocation)
	{
/*
		myImageLocation = newLocation;
		if (multipage) {
			try {
				multipageRenderer.setReader(Jimi.createJimiReader(newLocation, loadingFlags));
			}
			catch (Exception e) {
				showError(e.toString());
			}
		}
		else {
			setImageProducer(loadImageProducer(newLocation));
		}
*/
		setImageProducer(loadImageProducer(newLocation));
	}

	/** Where <b>JIMI</b> should look to find the image. <br>
	  * If the image was set with <code>setImage</code> this value may be <code>null</code>
	  *
	  * @return	The URL location of the image
	  * @see JimiCanvas#setImageLocation
	  */
	public URL getImageLocation() {
		return myImageLocation;
	}

	/** We can explicitly set an Image instead of relying upon <b>JIMI</b> to load it.</code>
	  *
	  * @param myImage	The <code>java.awt.Image</code> that we wish to display
	  * @see JimiCanvas#getImage
	  * @see JimiCanvas#setImageLocation
	  * @see JimiCanvas#getImageLocation
	  */
	public synchronized void setImage(Image myImage)
	{
		this.myImage = myImage;
		if (renderer != null) {
			renderer.setImage(myImage);
		}

		validate();
	}

	/**
	 * Loads an image from the path specified and calls setImage(Image) with the returned Image object.
	 *
     * @param path The path to the image
	 * @see JimiCanvas#setImage
	 */
	public synchronized void setImagePath(String path)
	{
		setImageProducer(loadImageProducer(path));
	}

	/** The image that this canvas is currently displaying.  This is not
	  * the cropped nor resized image, but the base image that is being
	  * used to create the cropped or scaled image.
	  *
	  * @see JimiCanvas#setImage
	  * @see JimiCanvas#setImageLocation
	  * @see JimiCanvas#getImageLocation
	  *
	  * @return Image The base image that we're displaying currently
	  */
	public Image getImage() {
		if (myImage != null) {
			return myImage;
		}
		else if (myImageProducer != null) {
			myImage = createImage(myImageProducer);
			GraphicsUtils.waitForImage(myImage);
			return myImage;
		}
		else {
			return null;
		}

	}

   /**
   * displays an image created from the given ImageProducer
   *
   * @ producer the ImageProducer you want to create the image from
   */
   public void setImageProducer(ImageProducer producer)
   {
	   myImage = null;
	   if (renderer != null) {
		   if (producer != null && aspectAdjust) {
			   try {
				   producer = JimiUtils.aspectAdjust(Jimi.createRasterImage(producer));
			   }
			   catch (JimiException e) {
				   producer = null;
			   }
		   }
		   myImageProducer = producer;
		   renderer.setImageProducer(producer);
		   invalidate();
		   validate();
	   }
   }

   /**
   * display an image created from the given JimiRasterImage
   *
   * @param raster the JimiRasterImage you want to display
   */
   public void setRasterImage(JimiRasterImage raster)
   {
	   setImageProducer(raster == null ? null : raster.getImageProducer());
/*
	   ImageProducer prod = null;
	   if (raster != null) {
		   myImage = null;
		   myImageProducer = raster.getImageProducer();
		   prod = raster.getImageProducer();
	   }
	   else {
		   myImageProducer = null;
		   myImage = null;
		   if (aspectAdjust) {
			   prod = JimiUtils.aspectAdjust(raster);
		   }
		   else {
			   prod = raster.getImageProducer();
		   }
	   }
	   if (renderer != null) {
		   renderer.setImageProducer(prod);
		   invalidate();
		   validate();
	   }
*/
   }

	/*
	 * Multi-image caching.
	 */

	protected ImageCache imageCache;

	/**
	 * For multi-image files, progress to the next image.
	 */
	public void nextImage()
	{
		try {
			setRasterImage(imageCache.getNextImage());
		}
		catch (Exception e) {
		}
	}

	/**
	 * For multi-image files, regress to the previous image.
	 */
	public void previousImage()
	{
		try {
			setRasterImage(imageCache.getPreviousImage());
		}
		catch (Exception e) {
		}
	}

	/**
	 * For multi-image files, regress to the first image.
	 */
	public void firstImage()
	{
		if (imageCache == null) {
			return;
		}
		JimiRasterImage image = null;
		while (imageCache.hasPreviousImage()) {
			image = imageCache.getPreviousImage();
		}
		if (image != null) {
			setRasterImage(image);
		}
	}

	/**
	 * For multi-image files, progress to the last image.
	 */
	public void lastImage()
	{
		if (imageCache == null) {
			return;
		}
		JimiRasterImage image = null;
		while (imageCache.hasNextImage()) {
			image = imageCache.getNextImage();
		}
		if (image != null) {
			setRasterImage(image);
		}
	}

	/**
	 * For multi-image files, check if there is a next image to move to.
	 */
	public boolean hasNextImage()
	{
		return imageCache == null ? false : imageCache.hasNextImage();
	}

	/**
	 * For multi-image files, check if there is a previous image to mvoe to.
	 */
	public boolean hasPreviousImage()
	{
		return imageCache == null ? false : imageCache.hasPreviousImage();
	}

	/**
	 * Set whether the canvas should adjust aspect ratio for image formats
	 * which provide aspect information.
	 */
	public void setAspectAdjust(boolean flag)
	{
		aspectAdjust = flag;
	}

}
