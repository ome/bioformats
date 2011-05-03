package com.sun.jimi.core.util;

import java.awt.image.*;
import java.util.*;

/**
 * ImageProducer implementation which holds added consumers in a queue until a
 * new ImageProducer provided to proxy to.  Useful for tracking addition of
 * ImageConsumers for an ImageProducer which doesn't yet exist.
 * @author  Luke Gorrie
 * @version $Revision: 1.1.1.1 $ $Date: 1998/12/01 12:21:58 $
 */
public class QueuedImageProducerProxy implements ImageProducer
{
	protected Vector queuedConsumers = new Vector();
	protected ImageProducer realProducer;

	/**
	 * Set the producer to proxy calls to.  Any queued consumers will be added to this
	 * producer immediately.
	 * @param producer the ImageProducer to proxy to
	 */
	public synchronized void setImageProducer(ImageProducer producer)
	{
		realProducer = producer;
		Enumeration consumers = queuedConsumers.elements();
		while (consumers.hasMoreElements()) {
			realProducer.addConsumer((ImageConsumer)consumers.nextElement());
		}
	}

	public ImageProducer getImageProducer()
	{
		return realProducer;
	}

	public synchronized ImageConsumer[] getConsumers()
	{
		ImageConsumer[] consumers = new ImageConsumer[queuedConsumers.size()];
		queuedConsumers.copyInto(consumers);

		return consumers;
	}

	public synchronized void addConsumer(ImageConsumer consumer)
	{
		if (realProducer == null) {
			queuedConsumers.addElement(consumer);
		}
		else {
			realProducer.addConsumer(consumer);
		}
	}

	public synchronized boolean isConsumer(ImageConsumer consumer)
	{
		if (realProducer == null) {
			return queuedConsumers.contains(consumer);
		}
		else {
			return realProducer.isConsumer(consumer);
		}
	}

	public synchronized void removeConsumer(ImageConsumer consumer)
	{
		if (realProducer == null) {
			queuedConsumers.removeElement(consumer);
		}
		else {
			realProducer.removeConsumer(consumer);
		}
	}

	/**
	 * Ignored if not proxying.
	 */
	public void startProduction(ImageConsumer consumer)
	{
		addConsumer(consumer);
	}

	/**
	 * Ignored if not proxying.
	 */
	public void requestTopDownLeftRightResend(ImageConsumer consumer)
	{
		removeConsumer(consumer);
		addConsumer(consumer);
	}

}


