package com.sun.jimi.core.util;

public class StdoutProgressListener implements ProgressListener
{
	/**
	 * Indicate that the task being monitored has begun.
	 */
	public void setStarted()
	{
		System.out.println("PROGRESS: started");
	}

	/**
	 * Set the progress-level as a percentage.
	 * @param progress a number between 0 and 100 representing the current
	 * level of progress
	 */
	public void setProgressLevel(int progress)
	{
		System.out.println("PROGRESS: " + progress);
	}

	/**
	 * Indicate that the task being monitored has completed.
	 */
	public void setFinished()
	{
		System.out.println("PROGRESS: finished");
	}


	/**
	 * Indicate that the operation has been aborted.
	 */
	public void setAbort()
	{
		System.out.println("PROGRESS: aborted");
	}


	/**
	 * Indicate that the operation has been aborted.
	 * @param reason the reason the operation was aborted
	 */
	public void setAbort(String reason)
	{
		System.out.println("PROGRESS: aborted, " + reason);
	}
}
