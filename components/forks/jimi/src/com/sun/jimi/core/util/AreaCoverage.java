package com.sun.jimi.core.util;

import java.awt.Shape;
import java.awt.Rectangle;
import com.sun.jimi.util.ExpandableArray;

/**
 * This class is used to track the area of coverage of a given area
 * (width x height) of a cartesian coordinate system.
 * A single point may be represented as a coordinate with a width of 1
 * and a height of 1 though this class is not likely to be efficient 
 * for point tracking of area coverage
 *
 * No concurrency of access issues are dealt with in this class.
 *
 * @author	Robin Luiten
 * @version	1.1	05/Jan/1998
 **/
public class AreaCoverage
{
	// The following methods are not implemented as they are not currently required
	//	void remove(int x)
	//	void remove(int x, int y, int w, int h)

	int width;

	int height;

	ExpandableArray rectangles;

	/**
	 * @param w width of area to track coverage on
	 * @param h height of area to track coverage on
	 **/
	public AreaCoverage(int w, int h)
	{
		width = w;
		height = h;
		reset();
	}

	/**
	 * Reset the area covered to none.
	 **/
	public void reset()
	{
		rectangles = new ExpandableArray();
	}

	/**
	 * Add coverage for specified region.
	 * @param x	horizontal coordinate for area to cover
	 * @param y vertical coordinate for area to cover
	 * @param width width of area to cover
	 * @param height height of area to cover
	 **/
	public void add(int x, int y, int width, int height)
	{
		if (contains(x, y, width, height))
			return;

		Rectangle newR = new Rectangle(x, y, width, height);
		int newIndex = rectangles.addElement(newR);
		mergeRecords(newIndex);
	}

	/**
	 * @param row the row that is being covered
	 **/
	public void add(int row)
	{
		add(0, row, width, 1);
	}

	/**
	 * @param the row to check for coverage on
	 * @return True if row is covered
	 **/
	public boolean contains(int row)
	{
		return contains(0, row, width, 1);
	}

	/**
	 * @param x	horizontal coordinate for area to check for coverage
	 * @param y vertical coordinate for area to check for coverage
	 * @param width width of area to check for coverage
	 * @param height height of area to check for coverage
	 * @return True if area specified is allready covered by rectangles
	 **/
	public boolean contains(int x, int y, int w, int h)
	{

		int limit = rectangles.size();
		for (int i = rectangles.size() - 1; i >= 0; i--)
		{
			Rectangle r = (Rectangle)rectangles.elementAt(i);

			if (r.contains(x, y) && r.contains(x + w - 1, y + h - 1))
				return true;
		}

		return false;

	}

	/**
	 * Process the covered rectangles collection and merge records 
	 * where it is possible to.
	 * @param idx is the record that has changed and is the only 
	 * rectangle that might be merged with adjacent rectangles.
	 **/
	protected void mergeRecords(int idx)
	{
		if (rectangles.size() < 2)
			return;

		int i;
		Rectangle r;
		Rectangle mr = (Rectangle)rectangles.elementAt(idx);
		rectangles.removeElementAt(idx);

		// assumes no rectangles overlap currently
		for (i = 0; i < rectangles.size(); ++i)
		{
			r = (Rectangle)rectangles.elementAt(i);

			if ((mr.x + mr.width) == r.x)	// rectangle left of current one
			{
				if (mr.height == r.height && mr.y == r.y)
				{	// adjacent and same height so merge
					r.setBounds(mr.x, r.y, r.width + mr.width, r.height);
					mergeRecords(i);
					return;
				}
			}
			else if ((mr.x - 1) == r.x + r.width - 1)	// rectangle right of current one
			{
				if (mr.height == r.height && mr.y == r.y)
				{	// adjacent and same height so merge
					r.setBounds(r.x, r.y, r.width + mr.width, r.height);
					mergeRecords(i);
					return;
				}
			}
			else if ((mr.y + mr.height) == r.y)	// rectangle above current one
			{
				if (mr.width == r.width && mr.x == r.x)
				{	// adjacent vertically and same width so merge
					r.setBounds(r.x, mr.y, r.width, r.height + mr.height);
					mergeRecords(i);
					return;
				}
			}
			else if ((mr.y - 1) == r.y + r.height - 1) // rectangle below current
			{
				if (mr.width == r.width && mr.x == r.x)
				{	// adjacent vertically and same width so merge
					r.setBounds(r.x, r.y, r.width, r.height + mr.height);
					mergeRecords(i);
					return;
				}
			}
		}
		// add back the record as we couldnt merge it anywhere
		rectangles.addElement(mr);     
	}

	/**
	 * @return the bounding rectangle representing the area that
	 * has been covered.
	 **/
	public Rectangle getBounds()
	{
		int minX = width;
		int maxX = 0;
		int minY = height;
		int maxY = 0;
		int i;
		Rectangle r = new Rectangle();

		for (i = rectangles.size(); --i >= 0; )
			r.add((Rectangle)rectangles.elementAt(i));
		return r;
	}

} // end of AreaCoverage

