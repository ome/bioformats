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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;
import java.awt.image.CropImageFilter;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.Toolkit;

/** For getScaledInstance workround */
import java.awt.image.AreaAveragingScaleFilter;
import com.sun.jimi.core.filters.ReplicatingScaleFilter;
/* */

import java.awt.MediaTracker;

import java.io.File;

import java.net.URL;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiImage;
import com.sun.jimi.core.util.GraphicsUtils;

/**
  * An <code>java.awt.Canvas</code> for easily displaying any image in a </strong>J.I.M.I.</strong> supported
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
  *			</ol>
  *		<li>Specify a justification for the image based upon the cardinal
  *			directions of a compass.
  *		<li>Explicitly set a canvas size.
  *		<li>Automatically resize the canvas to fit a new image
  *		<li>Specify a background color for areas of the canvas unpainted by the
  *			image.
  *		<li>Specify a null image to display
  *	</ul>
  * 
  * @author Christian Lucas
  * @author Luke Gorrie
  * @version 1.0.$Revision: 1.1.1.1 $
  * @since Jimi1.0
  */

public class JimiCanvasLW extends Component {


	//////////		PUBLIC FIELDS		///////////

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int EAST 		= 0;

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int NORTH 		= 1;

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int NORTHEAST 	= 2;

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int NORTHWEST 	= 3;

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int SOUTH		= 4;

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int SOUTHEAST 	= 5;

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int SOUTHWEST 	= 6;

	/** One of nine possible justification policies.
	  *
	  * @see setJustificationPolicy
	  */
	public static final int WEST 		= 7;

	/** One of nine possilbe justification policies.
	  * 
	  * @see setJustificationPolicy
	  */
	public static final int CENTER		= 8;


	/** One of three possible resizing policies. Will scale the image so that one
	  * of the axis totally fills the available space, and the other maintains 
	  * aspect ratio.
	  *
	  * @see setResizePolicy
	  * @see CROP_AS_NECESSARY
	  * @see SCALE
	  */
	public static final int BEST_FIT = 0;

	/** One of three possible resizing policies.  No scaling is done.  instead, if the
	  * image will not fit within the available space, it is cropped.
	  *
	  * @see setResizePolicy
	  * @see BEST_FIT
	  * @see SCALE
	  */
	public static final int CROP_AS_NECESSARY = 1;

	/** One of three possible resizing policies.  Both axis are independently scaled to
	  * totally fill the available space.  This may cause distortion.
	  *
	  * @see setResizePolicy
	  * @see BEST_FIT
	  * @see CROP_AS_NECESSARY
	  */
	public static final int SCALE = 2;

	public static final int SCROLL = 3;

	public static final int FIT_TO_WIDTH = 4;

	/** One of two possible scaling mode policies. This policy will cause
	  * the JimiCanvas to use java.awt.image.AreaAveragingScaleFilter for
	  * rendering the scaled version of the target image. This yields the
	  * best looking results, but can be very time consuming.
	  *
	  * @see setScalingPolicy
	  * @see REPLICATE
	  */
	public static final int AREA_AVERAGING = 0;

	/** One of two possible scaling mode policies. This policy will cause
	  * the JimiCanvas to use java.awt.image.ReplicatingScaleFilter for
	  * rendering the scaled version of the target image. This filter
	  * is very fast, but does not gurantee good-looking results.
	  *
	  * @see setScalingPolicy
	  * @see AREA_AVERAGING
	  */
	public static final int REPLICATE = 1;



	//////////		PROTECTED FIELDS		///////////

	/** The <code>java.awt.Image</code> that this <code>JimiCanvas</code> represents.
	  *
	  * @see setImage
	  * @see loadImage
	  * @see getImage
	  */
	protected transient Image	myImage;

	/** Tells <strong>J.I.M.I.</strong> where to load <code>myImage</code> from.  This
	  * value may be null if you wish to explicitly set <code>myImage</code>.
	  *
	  * @see setImage
	  * @see setImageLocation
	  * @see getImageLocation
	  * @see loadImage
	  */
	protected URL myImageLocation;
	
	/** This <code>JimiCanvas</code> will automatically resize to fit the
	  * <code>Image</code> if true.
	  * 
	  * @see getWillResize
	  * @see setWillResize
	  */
	protected boolean willSizeToFit = false;

	/** One of eight possible imageJustifications.  If image does not fill the
	  * entire canvas, it will be drawn according to this justification policy.
	  *
	  * @see getImageJustification
	  * @see setJustificationPolicy
	  */
	protected int justificationPolicy = CENTER;

	/** One of three possible resize policies.  If an image needs to be modified
	  * to fill a canvas, or to fit within it, then this policy will be used to
	  * determine how to do this.
	  *
	  * @see getResizePolicy
	  * @see setResizePolicy
	  */
	protected int resizePolicy = SCROLL;

	/** One of two possible scaling policies.  If an image needs to be scaled,
	  * then this policy will be used to determine how to do this.
	  *
	  * @see getScalingPolicy
	  * @see setScalingPolicy
	  */
	protected int scalingPolicy = REPLICATE;


	//////////		PRIVATE FIELDS		//////////

	/** Used to cache the cropped image
	  */
	private transient Image cacheImage;
	
	/** The width at which the cached image was sized for
	  */
	private int lastWidth;

	/** The height at which the cached image was sized for
	  */
	private int lastHeight;

	/** The resize policy under which the cached image was created
	  */
	private int lastResizePolicy;

	/** The scaling policy under which the cached image was created
	  */
	private int lastScalingPolicy;

	/** The justification policy under which the cached image was created
	  */
	private int lastJustificationPolicy;

	// width to match with FIT_TO_WIDTH
	private int fitWidth = -1;

	//////////		CONSTRUCTORS		//////////

	/** No argument constructor for use in beanboxes.
	  */
	public JimiCanvasLW() {

		super();

	}

	/** Create me
	  *
	  * @param anImage The image to display
	  * @see	setImage
	  */
	public JimiCanvasLW(Image anImage) {

		this();

		myImage = anImage;
		myImageLocation = null;

	}

	/** Create me
	  *
	  * @param aLocation Where to load the image using <strong>J.I.M.I.</strong> from
	  * @see setImageLocation
	  */
	public JimiCanvasLW(URL aLocation) {

		this();

		myImage = null;
		myImageLocation = aLocation;

	}

	//////////		STATIC METHODS		//////////



	////////// 		PRIVATE METHODS		//////////

	/** We've been given an imageLocation, and we want to pull the image from that
	  * location by using <strong>J.I.M.I.</strong> to do it.
	  *
	  * @see setImageLocation
	  * @see setImage
	  */
	protected void LoadImage() {

		if(myImageLocation == null) {

			//Do nothing
			return;

		}

		Image tempImage 			= Jimi.getImage(myImageLocation);
		java.awt.MediaTracker mt 	= new java.awt.MediaTracker(this);

		mt.addImage(tempImage, 0);

		try {
		
			mt.waitForAll();

			if(!mt.isErrorAny()) {
						
				setImage(tempImage);

			}

		} catch(InterruptedException ie) { }

	}

	/** Check to see if we need to resize this canvas based upon the size of the
	  * image
	  *
	  * @see setWillSizeToFit
	  */
	private synchronized void doResize() {

		if(myImage == null) return;

		//Check to see if we resize to fit
		setSize(new Dimension(myImage.getWidth(this), myImage.getHeight(this)));

		Container p = getParent();
		if(p != null) {
			p.invalidate();
			p.layout();
			/* Called implicitly in doLayout -Chris
			repaint();
			*/
		}


	}

	/**
	 * Set the width to be used for FIT_TO_WIDTH.
	 */
	public void setFitWidth(int width)
	{
		fitWidth = width;
	}

	/** Given the size of our canvas, a resize policy and a justification, crop or scale the
	  * image so that it is appropriately fitted to the image and the policies.
	  *
	  * @param which	The base image which we will then format
	  * @param width	The width of the canvas we're going to show the resized image upon
	  * @param height	The height of the canvas we're going to show the resized image upon
	  * @param resizePolicy			Do we crop, scale, or best fit this image?
	  * @param justificationPolicy 	Centered, or one of the 8 cardinal directions?
	  * @return			A <code>java.awt.Image</code> that is just right for us.
	  * @see 	setResizePolicy
	  * @see 	setJustificationPolicy
	  */
	 Image getResizedImage(Image which, int width, int height, int resizePolicy, int justificationPolicy) {

		/* We have an issue.  Jdk1.02 doesn't support Image.getScaledImage.  There is a possible
		 * workaround.  The code for getScaledInstance is included.  What we may be able to do, is
		 * package the ReplicatingScaleFilter and the AreaAveragingScaleFilter along with the Jimi
		 * package.  These will both work in a 1.02 environment.  The following image code would have
		 * to be folded into this code.
		 *
		 *****
		 *
		 *  public Image getScaledInstance(int width, int height, int hints) {
		 *		ImageFilter filter;
		 *		if ((hints & (SCALE_SMOOTH | SCALE_AREA_AVERAGING)) != 0) {
		 *	    	filter = new AreaAveragingScaleFilter(width, height);
		 *		} else {
		 *	   	 	filter = new ReplicatingScaleFilter(width, height);
		 *		}
		 *		ImageProducer prod;
		 *		prod = new FilteredImageSource(getSource(), filter);
		 *		return Toolkit.getDefaultToolkit().createImage(prod);
		 *   }
		 *
		 */

		int imageWidth	= which.getWidth(this);
		int imageHeight = which.getHeight(this);

		if (resizePolicy == SCROLL) {
			return which;
		}

		else if(resizePolicy == CROP_AS_NECESSARY) {

			//Use the justification policy to determine the bounding points for
			//the cropping rectangle

			int xStart 	= 0;
			int yStart 	= 0;
			int xDiff 	= imageWidth  - width;
			int yDiff 	= imageHeight - height;

			if(xDiff < 0) {

				xDiff = 0;

			}

			if(yDiff < 0) {

				yDiff = 0;

			}
			
			if( xDiff == 0 && yDiff == 0 ) {
				return which;
			}

			int xHalf = xDiff / 2;
			int yHalf = yDiff / 2;

			int doneWidth  = width < imageWidth ? width : imageWidth;
			int doneHeight = height < imageHeight ? height : imageHeight;

			switch(justificationPolicy) {
				
				case CENTER :

					xStart = xHalf;
					yStart = yHalf;
					break;
					
				case NORTHWEST :

					xStart = 0;
					yStart = 0;
					break;
				
				case WEST :

					xStart = 0;
					yStart = yHalf;
					break;
				
				case SOUTHWEST :

					xStart = 0;
					yStart = yDiff;
					break;
				
				case SOUTH :

					xStart = xHalf;
					yStart = yDiff;
					break;

				case SOUTHEAST :

					xStart = xDiff;
					yStart = yDiff;
					break;

				case EAST :

					xStart = xDiff;
					yStart = yHalf;
					break;

				case NORTHEAST :

					xStart = xDiff;
					yStart = 0;
					break;
								
				case NORTH :

					xStart = xHalf;
					yStart = 0;
					break;
				
			}					

			//Do the crop
			ImageFilter filter = new CropImageFilter(xStart, yStart, imageWidth, imageHeight);
			ImageProducer prod = new FilteredImageSource(which.getSource(), filter);
			Image result =  Toolkit.getDefaultToolkit().createImage(prod);
			which.flush();
			GraphicsUtils.waitForImage(this, result);
			return result;

		} else if(resizePolicy == SCALE) {

			//Stretch the image to fit the width and height
			/* for 1.02 workaround

			return which.getScaledInstance(width, height, Image.SCALE_DEFAULT);

			*/
			
            ImageFilter filter;
            if( getScalingPolicy() == AREA_AVERAGING ) {
			    filter = new AreaAveragingScaleFilter(width, height);
			} else {
			    filter = new ReplicatingScaleFilter(width, height);
			}
		 	ImageProducer prod = new FilteredImageSource(which.getSource(), filter);
		 	return Toolkit.getDefaultToolkit().createImage(prod);


		} else if(resizePolicy == BEST_FIT) {

			//Determine which axis is the largest (ratio of image to axis)
			float xRatio = (float)width  / (float)imageWidth;
			float yRatio = (float)height / (float)imageHeight;

			int xDistance;
			int yDistance;

			float correctionValue;
			
			if(xRatio < yRatio) {

				xDistance = (int)(imageWidth * xRatio);
				yDistance = (int)(imageHeight * xRatio);
							
			} else {

				xDistance = (int)(imageWidth * yRatio);
				yDistance = (int)(imageHeight * yRatio);

			}

            ImageFilter filter;
            if( getScalingPolicy() == AREA_AVERAGING ) {
			    filter = new AreaAveragingScaleFilter(xDistance, yDistance);
			} else {
			    filter = new ReplicatingScaleFilter(xDistance, yDistance);
			}
		 	ImageProducer prod = new FilteredImageSource(which.getSource(), filter);
		 	return Toolkit.getDefaultToolkit().createImage(prod);


		}
		else { // FIT_TO_WIDTH
			if (imageWidth < fitWidth) {
				return which;
			}
			int w = (fitWidth == -1) ? width : fitWidth;
			float ratio = (float)w / (float)imageWidth;
			int ySize = (int)(imageHeight * ratio);

			// check if the cache already has a fit image
			if ((cacheImage != null) && (cacheImage.getWidth(null) == w) && (cacheImage.getHeight(null) == ySize)) {
				return cacheImage;
			}

            ImageFilter filter;
            if( getScalingPolicy() == AREA_AVERAGING ) {
			    filter = new AreaAveragingScaleFilter(w, ySize);
			} else {
			    filter = new ReplicatingScaleFilter(w, ySize);
			}
		 	ImageProducer prod = new FilteredImageSource(which.getSource(), filter);
		 	return Toolkit.getDefaultToolkit().createImage(prod);
		}
	}


	//////////		PUBLIC METHODS		//////////

	/** Which of the nine justification policies we use to place the image
	  * on the canvas
	  *
	  * @param newPolicy	The new justification policy
	  * @see	CENTER
	  * @see	NORTH
	  * @see	NORTHEAST
	  * @see	EAST
	  * @see	SOUTHEAST
	  * @see	SOUTH
	  * @see	SOUTHWEST
	  * @see	WEST
	  * @see	NORTHWEST
	  */
	public synchronized void setJustificationPolicy(int newPolicy) {

		justificationPolicy = newPolicy;

	}

	/** Which of the nine justification policies we use to place the image
	  * on the canvas
	  *
	  * return	One of nine justification policies
	  * @see	CENTER
	  * @see	NORTH
	  * @see	NORTHEAST
	  * @see	EAST
	  * @see	SOUTHEAST
	  * @see	SOUTH
	  * @see	SOUTHWEST
	  * @see	WEST
	  * @see	NORTHWEST
	  */
	public int getJustificationPolicy() {

		return justificationPolicy;

	}

	/** Do we crop the image, scale it, or scale it so that it fits best?
	  *
	  * @param	newPolicy	One of three policies which specify how to fit the 
	  *						image into the canvas
	  * @see	CROP_AS_NECESSARY
	  * @see	SCALE
	  * @see	BEST_FIT
	  */
	public synchronized void setResizePolicy(int newPolicy) {

		resizePolicy = newPolicy;

	}

	/** Do we crop the image, scale it, or scale it so that it fits best?
	  *
	  * @see	CROP_AS_NECESSARY
	  * @see	SCALE
	  * @see	BEST_FIT
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
	  * @see	AREA_AVERAGING
	  * @see	REPLICATE
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
	  * @see	AREA_AVERAGING
	  * @see	REPLICATE
	  */
	public int getScalingPolicy() {

		return scalingPolicy;

	}

	/** If true, the canvas will resize itself so that it exactly fits the
	  * the canvas.
	  *
	  * @param	newPolicy	If true, will resize the canvas to fit the new image
	  * @see setImage
	  * @see setImageLocation
	  */
	public synchronized void setWillSizeToFit(boolean newPolicy) {

		willSizeToFit = newPolicy;

	}

	/** If true, the canvas will resize itself so that it exactly fits the
	  * canvas.
	  * 
	  * @return	True if the canvas will be automatically sized to fit the image
	  */
	public boolean getWillSizeToFit() {

		return willSizeToFit;

	}

	/** Where should <strong>J.I.M.I.</strong> look to find the image? <br>
	  * This value need not be set, and is mutually exclusive with <code>setImage</code>.
	  * </code>setImage</code>takes precedence.
	  * 
	  * @param newLocation	Where <strong>J.I.M.I.</strong> should go to find
	  *						the image
	  * @see getImageLocation
	  * @see setImage
	  * @see getImage
	  */
	public synchronized void setImageLocation(URL newLocation) {

		myImageLocation = newLocation;

		LoadImage();

	}

	/** Where <strong>J.I.M.I.</strong> should look to find the image. <br>
	  * If the image was set with <code>setImage</code> this value may be <code>null</code>
	  * 
	  * @return	The URL location of the image
	  * @see setImageLocation
	  * @see setImage
	  * @see getImage
	  */
	public URL getImageLocation() {

		return myImageLocation;

	}

	/** We can explicitly set an Image instead of relying upon <strong>J.I.M.I.</strong>
	  * to load it.  The two ways of specifying the image location are mutually 
	  * exclusive.  <code>setImage</code> takes precedence over <code>setImageLocation</code>
	  *
	  * @param anImage	The <code>java.awt.Image</code> that we wish to display
	  * @see setImageLocation
	  * @see getImageLocation
	  * @see getImage
	  */
	public synchronized void setImage(Image anImage) {
	
		if(cacheImage != null) {

			cacheImage.flush();
			cacheImage 	= null;

		}

		if(myImage != null) {

			//Deallocate system resources for this image.  This may
			//mean that the image will need to be reloaded if used by
			//another object, however we're making the assumption that
			//for the majority of uses this is the only client of the
			//image
			myImage.flush();

		}

		myImage 		= anImage;

		/* We will simply ignore the imageLocation instead of setting 
		   it to null
		myImageLocation = null;
		*/

		//Repaint is called implicitly when we do setSize - Chris
		if((willSizeToFit) || (getResizePolicy() == FIT_TO_WIDTH)) {
			
			doResize();

		}

		// safety call to repaint(), some repaints were being dropped previously.
		repaint();

	}

	/** The image that this canvas is currently displaying.  This is not 
	  * the cropped nor resized image, but the base image that is being 
	  * used to create the cropped or scaled image.
	  * 
	  * @return Image	The base image that we're displaying currently
	  * @see setImage
	  * @see setImageLocation
	  * @see getImageLocation
	  */
	public Image getImage() {

		return myImage;

	}

	/**
	 * If 'willSizeToFit', ask for the size of the image.
	 */
	public Dimension getPreferredSize()
	{


		if (myImage != null) {
			if (getWillSizeToFit()) {
				if (getResizePolicy() == FIT_TO_WIDTH) {
					Image cache = getCacheImage();
					if (cache != null) {
						return new Dimension(cache.getWidth(null), cache.getHeight(null));
					}
				}
				else {
					return new Dimension(myImage.getWidth(null), myImage.getHeight(null));
				}
			}
		}
		return super.getPreferredSize();

	}

	// bypass screen-clear in Canvas
	public void update(Graphics g) {
		paint(g);
	}

	protected Image getCacheImage()
	{
		if (cacheImage == null) {
			cacheImage = getResizedImage(myImage, size().width, size().height, resizePolicy, justificationPolicy);
		}
		return cacheImage;
	}

	public synchronized void paint(Graphics g) {

		if(myImage == null) {

			g.setColor(getForeground());
			g.fillRect(0, 0, size().width, size().height);
			
			return;

		} else {

			int xStart = 0;
			int xWidth;

			int yStart = 0;
			int yHeight;

	
			xWidth  = size().width;		
			yHeight = size().height;

			//If we don't have an image ready, or it is the wrong size, prepare it
			if(cacheImage == null ||
			   xWidth 	!= lastWidth 	||
			   yHeight != lastHeight 	||
			   resizePolicy != lastResizePolicy ||
			   justificationPolicy != lastJustificationPolicy) {
				
				cacheImage 	= getResizedImage(myImage, xWidth, yHeight, resizePolicy, justificationPolicy);

				lastWidth 	= xWidth;
				lastHeight 	= yHeight;
				lastResizePolicy 		= resizePolicy;
				lastJustificationPolicy = justificationPolicy;

			}

			int imageWidth  = cacheImage.getWidth(this);
			int imageHeight = cacheImage.getHeight(this);

			//Check the justification policy
			switch(justificationPolicy) {

				case CENTER :

					xStart = (xWidth - imageWidth) / 2;
					yStart = (yHeight - imageHeight) / 2;
					break;

				case NORTHWEST :
				
					xStart = 0;
					yStart = 0;
					break;

				case NORTHEAST :
				
					xStart = xWidth - imageWidth;
					yStart = 0;
					break;

				case NORTH :

					xStart = (xWidth - imageWidth) / 2;
					yStart = 0;
					break;

				case SOUTH :

					xStart = (xWidth - imageWidth) / 2;
					yStart = yHeight - imageHeight;
					break;

				case SOUTHWEST :

					xStart = 0;
					yStart = yHeight - imageHeight;
					break;

				case SOUTHEAST :

					xStart = xWidth - imageWidth;
					yStart = yHeight - imageHeight;
					break;

				case EAST :

					xStart = xWidth - imageWidth;
					yStart = (yHeight - imageHeight) / 2;
					break;

				case WEST :

					xStart = 0;
					yStart = (yHeight - imageHeight) / 2;
					break;
					
			}

			//draw it
			g.setColor(getForeground());
			g.fillRect(0, 0, xWidth, yHeight);
			g.drawImage(cacheImage, xStart, yStart, imageWidth, imageHeight, this);

		}

	}

}
