package com.sun.jimi.core.component;

import java.awt.*;

public class ScrollPaneLayout implements LayoutManager
{
	Component south;
	Component east;
	Component corner;
	Component center;
	
	public ScrollPaneLayout() {
	}
	
	/**
	 * interface methods
	 */
	public void removeLayoutComponent(Component comp) 
	{
		//System.out.println("trying to remove-> "+comp);
		
		if(comp == corner) {
			corner = null;
		}
		else
		if(comp == south) {
			south = null;
		}
		else
		if(comp == center) {
			center = null;
		}
		else
		if(comp == east) {
			east = null;
		}
		else {
			comp = null;
		}
	}
	
	/**
	 * not yet implemented
	 */
	public Dimension minimumLayoutSize(Container target)
	{
		//System.out.println("minimumLayoutSize called for target-> "+target);
		return new Dimension(50, 50);
	}

	/**
	 * not yet implemented
	 */
	public Dimension preferredLayoutSize(Container target)
	{
		//System.out.println("prefferedLayoutSize on "+target);
		
	   synchronized(target.getTreeLock())
		{
			Dimension dim = new Dimension(0, 0);

			if ((center != null) && center.isVisible())
			{
				//System.out.println("center!");
			    Dimension d = center.preferredSize();
			    dim.width += d.width;
			    dim.height = Math.max(d.height, dim.height);
			}
			else
			if ((east != null) && east.isVisible())
			{
				//System.out.println("east!");
				Dimension d = east.preferredSize();
				dim.width += d.width;
				dim.height = Math.max(d.height, dim.height);
			}
			else
			if((south != null) && south.isVisible())
			{
				//System.out.println("south!");
			    Dimension d = south.preferredSize();
			    dim.width = Math.max(d.width, dim.width);
			    dim.height += d.height;
			}
			else
			if((corner != null) && corner.isVisible())
			{
				//System.out.println("corner!");
			    Dimension d = corner.preferredSize();
			    dim.width = Math.max(d.width, dim.width);
			    dim.height += d.height;
			}
			else
			{
				dim.width  = 100;
				dim.height = 100;
			}

			Insets insets = target.insets();
			dim.width += insets.left + insets.right;
			dim.height += insets.top + insets.bottom;
			//System.out.println("returning "+ dim);
			return dim;
		}
	}

	public void layoutContainer(Container target)
	{
		synchronized(target.getTreeLock())
		{
			Insets inset = target.insets();
			int top = 0 - inset.top;
			int bottom = target.size().height - inset.bottom;
			int left = 0 - inset.left;
			int right = target.size().width - inset.right;

			// VERTICAL
			if((east != null) && (east.isVisible()))
			{
				//System.out.println("adding east");
				Dimension d = east.preferredSize();
				// if south isn't active determine size using no references to south
				if(south != null) {
					east.reshape(right-d.width, top, d.width, bottom-south.preferredSize().height);
				}
				else
				if(south == null) {
					east.reshape(right-d.width, top, d.width, bottom);
				}
			}

			// HORIZONTAL
			if((south != null) && (south.isVisible()))
			{
				//System.out.println("adding south");
				Dimension d = south.preferredSize();
				// if east isn't active determine size using no references to east
				if(east != null) {
					south.reshape(left, bottom-d.height, right-east.preferredSize().width, d.height);
				}
				else
				if(east == null) {
					south.reshape(left, bottom-d.height, right, d.height);
				}
			}

			// CORNER COMPONENT
			if((east != null) && (south != null) && (corner != null) && (corner.isVisible()) && (south.isVisible()) && (east.isVisible()))
			{
				Dimension d = corner.preferredSize();
				int startX = right - east.size().width;
				int startY = bottom - south.size().height;
				int width  = right - south.size().width;
				int height = bottom - east.size().height;

				corner.reshape(startX, startY, width, height);

				// color weirdness so im setting it to gray.
				corner.setBackground(Color.lightGray);
				corner.setForeground(Color.gray);
			}

			// CENTER COMPONENT
			if((center != null) && (center.isVisible()))
			{
				Dimension d = center.preferredSize();
				// WITH EAST (checking south for null)
				if(east != null && east.isVisible() && south == null)
				{
					// CONFIRMED
					//System.out.println("reshaping for vertical bar only "+east.size().width);
					center.reshape(left, top, right - east.size().width, d.height);
				}
				else
				// WITH SOUTH (checking east for null)
				if(south != null && south.isVisible() && east == null)
				{
					//System.out.println("reshaping for horinzontal only "+d.width);
					center.reshape(left, top, d.width, bottom - south.size().height);
				}
				// WITH SOUTH AND EAST (showing or not)
				else
				{
					//System.out.println("reshaping for everything");
					center.reshape(left, top, d.width, d.height);
				}
			}
		}
	}

	public void addLayoutComponent(String string, Component comp)
	{
		if(string.equals("corner")) {
			//System.out.println("adding corner");
			corner = comp;
		}
		if(string.equals("south")) {
			//System.out.println("adding south");
			south = comp;
		}
		if(string.equals("east")) {
			//System.out.println("adding east");
			east = comp;
		}
		if(string.equals("center")) {
			//System.out.println("adding center");
			center = comp;
		}
	}
}
