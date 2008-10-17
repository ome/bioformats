/*
 * ome.xml.r2003fc.ome.ROINode
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via xsd-fu on 2008-10-16 06:18:35-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

import org.w3c.dom.Element;

public class ROINode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a ROI node with an associated DOM element. */
	public ROINode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a ROI node with an associated DOM element beneath
	 * a given parent.
	 */
	public ROINode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a ROI node with an associated DOM element beneath
	 * a given parent.
	 */
	public ROINode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "ROI", attach));
	}

	// -- ROI API methods --

	// Attribute
	public Integer getT0()
	{
		return getIntegerAttribute("T0");
	}

	public void setT0(Integer t0)
	{
		setAttribute("T0", t0);
	}

	// Attribute
	public Integer getT1()
	{
		return getIntegerAttribute("T1");
	}

	public void setT1(Integer t1)
	{
		setAttribute("T1", t1);
	}

	// Attribute
	public Integer getY1()
	{
		return getIntegerAttribute("Y1");
	}

	public void setY1(Integer y1)
	{
		setAttribute("Y1", y1);
	}

	// Attribute
	public Integer getY0()
	{
		return getIntegerAttribute("Y0");
	}

	public void setY0(Integer y0)
	{
		setAttribute("Y0", y0);
	}

	// Attribute
	public Integer getX0()
	{
		return getIntegerAttribute("X0");
	}

	public void setX0(Integer x0)
	{
		setAttribute("X0", x0);
	}

	// Attribute
	public Integer getX1()
	{
		return getIntegerAttribute("X1");
	}

	public void setX1(Integer x1)
	{
		setAttribute("X1", x1);
	}

	// Attribute
	public Integer getZ0()
	{
		return getIntegerAttribute("Z0");
	}

	public void setZ0(Integer z0)
	{
		setAttribute("Z0", z0);
	}

	// Attribute
	public Integer getZ1()
	{
		return getIntegerAttribute("Z1");
	}

	public void setZ1(Integer z1)
	{
		setAttribute("Z1", z1);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
