/*
 * ome.xml.r200809.ome.LineNode
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

public class LineNode extends BasicSvgShapeNode
{

	// -- Constructors --

	/** Constructs a Line node with an associated DOM element. */
	public LineNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Line node with an associated DOM element beneath
	 * a given parent.
	 */
	public LineNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Line node with an associated DOM element beneath
	 * a given parent.
	 */
	public LineNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Line", attach));
	}

	// -- Line API methods --

	// Attribute
	public String gety1()
	{
		return getStringAttribute("y1");
	}

	public void sety1(String y1)
	{
		setAttribute("y1", y1);
	}

	// Attribute
	public String getx2()
	{
		return getStringAttribute("x2");
	}

	public void setx2(String x2)
	{
		setAttribute("x2", x2);
	}

	// Attribute
	public String getx1()
	{
		return getStringAttribute("x1");
	}

	public void setx1(String x1)
	{
		setAttribute("x1", x1);
	}

	// Attribute
	public String gety2()
	{
		return getStringAttribute("y2");
	}

	public void sety2(String y2)
	{
		setAttribute("y2", y2);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
