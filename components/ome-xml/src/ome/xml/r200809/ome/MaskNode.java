/*
 * ome.xml.r200809.ome.MaskNode
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
 * Created by curtis via xsd-fu on 2008-10-17 01:41:39-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200809.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200809.ome.*;
import ome.xml.r200809.spw.*;

import org.w3c.dom.Element;

public class MaskNode extends BasicSvgShapeNode
{

	// -- Constructors --

	/** Constructs a Mask node with an associated DOM element. */
	public MaskNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Mask node with an associated DOM element beneath
	 * a given parent.
	 */
	public MaskNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Mask node with an associated DOM element beneath
	 * a given parent.
	 */
	public MaskNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Mask", attach));
	}

	// -- Mask API methods --

	// Attribute
	public String gety()
	{
		return getStringAttribute("y");
	}

	public void sety(String y)
	{
		setAttribute("y", y);
	}

	// Attribute
	public String getx()
	{
		return getStringAttribute("x");
	}

	public void setx(String x)
	{
		setAttribute("x", x);
	}

	// Element which is complex (has sub-elements)
	public MaskPixelsNode getMaskPixels()
	{
		return (MaskPixelsNode)
			getChildNode("MaskPixels", "MaskPixels");
	}

	// Attribute
	public String getwidth()
	{
		return getStringAttribute("width");
	}

	public void setwidth(String width)
	{
		setAttribute("width", width);
	}

	// Attribute
	public String getheight()
	{
		return getStringAttribute("height");
	}

	public void setheight(String height)
	{
		setAttribute("height", height);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
