package com.sun.jimi.core.raster;

import java.awt.Rectangle;
import java.awt.image.*;
import java.util.*;

import com.sun.jimi.core.*;
import com.sun.jimi.core.util.MulticastImageConsumer;
import com.sun.jimi.core.options.*;

/**
 * Base support class for implementations of JimiRasterImage derivatives.
 * Primarily provides support for ImageProducer functionality.
 */

/*
 * The image production system is fairly simple, and it based around the idea of
 * consumers either being sent data as it is set in the storage, or if this isn't
 * possible putting them on a waiting list to receive all the data when it becomes
 * possible.  The former type of consumer is called a "direct consumer" and is sent
 * data as soon as it is set, the latter a "wating consumer" and is sent data when
 * the image production is finished.
 *
 * In implementation, these two categories of consumer each have a multicaster
 * which represents the whole group, so any operations on the "direct consumer"
 * are multicast to all consumers which were added as "direct", and waiting consumers
 * are treated similarly.
 *
 * For optimal performance with Java 2D's image pipeline, all images
 * are normalized to either 8-bit-per-channel DirectColorModels (for int-based)
 * or 8-bit IndexColorModels (for <= 8 bit).
 */

public abstract class JimiRasterImageSupport
	implements MutableJimiRasterImage, ImageProducer
{

	private static final int HINTS_UNSET = -1;

	protected boolean error = false;
	protected boolean abort = false;

	/** set to true when some image data is set */
	protected boolean productionStarted = false;

	/** set to true after first modification */
	protected boolean modified = false;

	/** set to true when production is allowed to be started */
	protected boolean productionAllowed = false;

	/** set to true when a full frame of image data is set, when there are is no unset data */
	protected boolean finished = false;

	/** true if new data has been set since a frame was completed */
	protected boolean newFrameData = false;

	/** color model for interpreting image data */
	protected ColorModel colorModel;

	protected Hashtable properties = new Hashtable();

	private boolean hasConsumer = false;

	// dimensions of storage, measured in pixels
	private int width, height;

	// state of image data for tracking progress of data setting
	private int state;

	// ImageConsumer hints
	private int DEFAULT_HINTS = ImageConsumer.TOPDOWNLEFTRIGHT |
	ImageConsumer.COMPLETESCANLINES |
	ImageConsumer.SINGLEPASS |
	ImageConsumer.SINGLEFRAME;

	private int hints = DEFAULT_HINTS;

	// multicasters to deal with both categories of consumer
	private MulticastImageConsumer directConsumer = new MulticastImageConsumer();
	private MulticastImageConsumer waitingConsumer = new MulticastImageConsumer();

	// controller object used to request decoding to start when the first consumer
	// is added
	private JimiDecodingController decodingController;

	private JimiImageFactory imageFactory = new MemoryJimiImageFactory();

	private FormatOptionSet options = new BasicFormatOptionSet(new FormatOption[] {});

	protected ColorModel sourceColorModel = null;
	protected boolean forceRGB = false;

	protected int[] rowBuf;

	protected boolean waitForOptions;

	/**
	 * Construct the storage with given dimensions
	 * @param w width in pixels of storage space
	 * @param h height in pixels of storage space
	 * @param cm the ColorModel to use
	 **/
	protected JimiRasterImageSupport(int w, int h, ColorModel cm)
	{
		width = w;
		height = h;
		setColorModel(cm);
	}

	/**
	 * Get the width of the storage space.
	 * @return width in pixels
	 **/
	public int getWidth()
	{
		return width;
	}

	/**
	 * Get the height of the storage space.
	 * @return height in pixels
	 **/
	public int getHeight()
	{
		return height;
	}

	/**
	 * Set the source ColorModel of the image.
	 */
	public void setColorModel(ColorModel cm)
	{
		if (!forceRGB) 
		{
			colorModel = getAppropriateColorModel(cm);
			forceRGB |= (cm != colorModel);
		}
		sourceColorModel = cm;
	}

	/**
	 * Set hints about how the data will be delivered.
	 * The hints constants from ImageConsumer are used.
	 * @see java.awt.ImageConsumer
	 */
	public void setImageConsumerHints(int hints)
	{
		hints |= ImageConsumer.SINGLEFRAME;
		this.hints = hints;
	}

	/**
	 * Check whether the image has been marked as an error.
	 */
	public boolean isError()
	{
		return error;
	}

	/**
	 * Declare that the image is an error, and should not be used.
	 */
	public synchronized void setError()
	{
		error = true;
		directConsumer.imageComplete(ImageConsumer.IMAGEERROR);
		waitingConsumer.imageComplete(ImageConsumer.IMAGEERROR);
		notifyAll();
	}

	/**
	 * Set the factory which the image belongs to.
	 */
	public void setFactory(JimiImageFactory factory)
	{
		imageFactory = factory;
	}

	/**
	 * Get the JimiImageFactory this image was created by.
	 */
	public JimiImageFactory getFactory()
	{
		return imageFactory;
	}

	/**
	 * Associate a FormatOptionSet with the image.
	 */
	public void setOptions(FormatOptionSet options)
	{
		this.options = options;
	}

	/**
	 * Get the FormatOptionSet associated with the image.
	 */
	public FormatOptionSet getOptions()
	{
		if (waitForOptions) {
			waitFinished();
		}
		return options;
	}

	/**
	 * Signal that the image is complete.
	 */
	public synchronized void setFinished()
	{
		// finished already?
		if (finished) {
			return;
		}
		// with a frame finished, the image has full coverage
		finished = true;
		// image is up to date - no new data
		newFrameData = false;

		directConsumer.imageComplete(ImageConsumer.STATICIMAGEDONE);

		directConsumer.removeAll();
		if (!waitingConsumer.isEmpty()) {
			try {
				sendToConsumerFully(waitingConsumer);
			}
			catch (ImageAccessException e) {
				setError();
				return;
			}
		}
		ImageConsumer[] waitingConsumers = waitingConsumer.getConsumers();

		waitingConsumer.imageComplete(ImageConsumer.STATICIMAGEDONE);

		waitingConsumer.removeAll();

		notifyAll();
	}

	/**
	 * Required information is known from creation time, so no need to wait.
	 */
	public void waitInfoAvailable()
	{
	}

	public synchronized void waitFinished()
	{
		productionStarted = true;
		if (decodingController != null) {
			decodingController.requestDecoding();
		}
		while ((!finished) && (!error)) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}

	public synchronized void setDecodingController(JimiDecodingController controller)
	{
		decodingController = controller;
		if (hasConsumer || productionStarted) {
			controller.requestDecoding();
		}
	}

	/**
	 * Add a new consumer.
	 * @param consumer the consumer
	 */
	public synchronized void addConsumer(ImageConsumer consumer)
	{
		hasConsumer = true;
		// initialize the consumer with hints, properties etc
		initConsumer(consumer);

		// if the production is unstarted, the consumer can be "direct"
		if (!productionStarted) {
			addDirectConsumer(consumer);
			productionStarted = true;
			if (decodingController != null) {
				decodingController.requestDecoding();
			}
		}
		// if production has started
		else {
			// if the image has full coverage it can be sent in its entirety
			if (finished) {
				// send all data
				addWaitingConsumer(consumer);
			}
			// if production is started, but not finished try to bring the consumer up
			// to date with the data that exists and then add it to the direct list,
			// or if it can't be brought up to date add it to the waiting list
			else {
				// try to bring the consumer up to date with all operations
				boolean consumerUpToDate = catchupConsumer(consumer);
				// if successful, add as a direct consumer
				if (consumerUpToDate) {
					addDirectConsumer(consumer);
				}
				// if the consumer is not entirely up to date, add as a waiting consumer
				else {
					addWaitingConsumer(consumer);
				}
			}
		}
	}

	protected void addDirectConsumer(ImageConsumer consumer)
	{
		consumer.setDimensions(getWidth(), getHeight());
		directConsumer.addConsumer(consumer);
		consumer.setHints(hints);
	}

	protected synchronized void addWaitingConsumer(ImageConsumer consumer)
	{
		consumer.setDimensions(getWidth(), getHeight());
		consumer.setHints(DEFAULT_HINTS);
		if (finished) {
			try {
				sendToConsumerFully(consumer);
				consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
			}
			catch (ImageAccessException e) {
				consumer.imageComplete(ImageConsumer.IMAGEERROR);
			}
		}
		else {
			waitingConsumer.addConsumer(consumer);
		}
	}

	/**
	 * Default implementation, does not attempt to catch up and returns failure.
	 * @return true if the consumer has been brought up-to-date
	 */
	protected synchronized boolean catchupConsumer(ImageConsumer consumer)
	{
		return false;
	}

	/**
	 * Get an ImageProducer to send image data from the storage.
	 * @return an ImageProducer to produce and image based on the data in the
	 * store
	 */
	public ImageProducer getImageProducer()
	{
		return this;
	}

	public boolean isConsumer(ImageConsumer consumer)
	{
		return directConsumer.contains(consumer) || waitingConsumer.contains(consumer);
	}

	public void removeConsumer(ImageConsumer consumer)
	{
		directConsumer.removeConsumer(consumer);
		waitingConsumer.removeConsumer(consumer);
	}

	public void startProduction(ImageConsumer consumer)
	{
		// get the consumer out of its current group and put it through
		// the add procedure
		removeConsumer(consumer);
		addConsumer(consumer);
	}

	public void requestTopDownLeftRightResend(ImageConsumer consumer)
	{
		addWaitingConsumer(consumer);
	}

	public ColorModel getColorModel()
	{
		return colorModel;
	}

	public Hashtable getProperties()
	{
		return properties;
	}

	public ImageProducer getCroppedImageProducer(int x, int y, int width, int height)
	{
		return new CroppedRasterImageProducer(this, x, y, width, height);
	}

	public synchronized void produceCroppedImage(ImageConsumer consumer, Rectangle region)
	{
		waitFinished();
		// initialize consumer
		initConsumer(consumer);
		consumer.setDimensions(region.width, region.height);
		consumer.setHints(DEFAULT_HINTS);
		try {
			sendRegionToConsumerFully(consumer,region);
			consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
		}
		catch (Exception e) {
				consumer.imageComplete(ImageConsumer.IMAGEERROR);
		}
	}

	protected MulticastImageConsumer getDirectConsumer()
	{
		return directConsumer;
	}

	protected boolean hasDirectConsumer()
	{
		return !directConsumer.isEmpty();
	}

	protected abstract void sendToConsumerFully(ImageConsumer consumer)
		throws ImageAccessException;

	protected abstract void sendRegionToConsumerFully(ImageConsumer consumer, Rectangle region)
		throws ImageAccessException;

	protected void initConsumer(ImageConsumer consumer)
	{
		consumer.setColorModel(getColorModel());
		consumer.setProperties(properties);
	}

	/**
	 * Return a ColorModel appropriate for use with image production.
	 */
	protected ColorModel getAppropriateColorModel(ColorModel cm)
	{
		return cm;
	}
	
	protected void setModified()
	{
		if (!modified) {
			modified = true;
			directConsumer.setHints(hints);
			productionStarted = true;
			if (decodingController != null) {
				decodingController.requestDecoding();
			}
		}
	}

	/*
	 * Some default implementations for common operations follow; should be
	 * overridden for storage types which can provide more optimal implementations
	 */

	public void getChannelRectangle(int channel, int x, int y, int width, int height, byte[] buffer,
									int offset, int scansize) throws ImageAccessException
	{
		try {
			if (rowBuf == null) 
			{
				rowBuf = new int[width];
			}

			for (int row = 0; row < height; row++) {
				getRowRGB(y + row, rowBuf, 0);
				for (int column = 0; column < width; column++) {
					buffer[offset + column + row * scansize] =
						(byte)(rowBuf[column] >>> channel);
				}
			}
		}
		catch (RuntimeException e) {
			throw new ImageAccessException(e.toString());
		}
	}
	public synchronized void getChannelRow(int channel, int y, byte[] buffer, int offset) throws ImageAccessException
	{
		getChannelRectangle(channel, 0, y, getWidth(), 1, buffer, offset, 0);
	}

	public synchronized void getRectangleRGBChannels(int x, int y, int width, int height, byte[] buffer,
										int offset, int scansize) throws ImageAccessException
	{
		if (rowBuf == null) 
		{
			rowBuf = new int[width];
		}
		for (int row = 0; row < height; row++) {
			getRowRGB(y + row, rowBuf, 0);
			int srcindex = 0;
			int destindex = 0;
			for (int column = 0; column < width; column++) {
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex] >>> 16);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex] >>> 8);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex++]);
			}
		}
	}

	public synchronized void getRectangleARGBChannels(int x, int y, int width, int height, byte[] buffer,
										 int offset, int scansize) throws ImageAccessException
	{
		if (rowBuf == null) 
		{
			rowBuf = new int[width];
		}
		for (int row = 0; row < height; row++) {
			getRowRGB(y + row, rowBuf, 0);
			int srcindex = 0;
			int destindex = 0;
			for (int column = 0; column < width; column++) {
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex] >>> 24);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex] >>> 16);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex] >>> 8);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex++]);
			}
		}
	}

	public synchronized void getRectangleRGBAChannels(int x, int y, int width, int height, byte[] buffer,
										 int offset, int scansize) throws ImageAccessException
	{
		if (rowBuf == null) 
		{
			rowBuf = new int[width];
		}
		for (int row = 0; row < height; row++) {
			getRowRGB(y + row, rowBuf, 0);
			int srcindex = 0;
			int destindex = 0;
			for (int column = 0; column < width; column++) {
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex] >>> 16);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex] >>> 8);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex]);
				buffer[(row * scansize) + (destindex++) + offset] = (byte)(rowBuf[srcindex++] >>> 24);
			}
		}
	}

	public void setWaitForOptions(boolean wait)
	{
		waitForOptions = wait;
	}

	public boolean mustWaitForOptions()
	{
		return waitForOptions;
	}

}

