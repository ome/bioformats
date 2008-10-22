/*
 * ome.xml.r200809.ome.ShapeNode
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

public class ShapeNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Shape node with an associated DOM element. */
	public ShapeNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Shape node with an associated DOM element beneath
	 * a given parent.
	 */
	public ShapeNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Shape node with an associated DOM element beneath
	 * a given parent.
	 */
	public ShapeNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Shape", attach));
	}

	// -- Shape API methods --

	// Element which is complex (has sub-elements)
	public PolygonNode getPolygon()
	{
		return (PolygonNode)
			getChildNode("Polygon", "Polygon");
	}

	// Element which is complex (has sub-elements)
	public PointNode getPoint()
	{
		return (PointNode)
			getChildNode("Point", "Point");
	}

	// Element which is complex (has sub-elements)
	public MaskNode getMask()
	{
		return (MaskNode)
			getChildNode("Mask", "Mask");
	}

	// Element which is complex (has sub-elements)
	public EllipseNode getEllipse()
	{
		return (EllipseNode)
			getChildNode("Ellipse", "Ellipse");
	}

	// Element which is complex (has sub-elements)
	public ChannelsNode getChannels()
	{
		return (ChannelsNode)
			getChildNode("Channels", "Channels");
	}

	// Element which is complex (has sub-elements)
	public PolylineNode getPolyline()
	{
		return (PolylineNode)
			getChildNode("Polyline", "Polyline");
	}

	// Attribute
	public Integer gettheZ()
	{
		return getIntegerAttribute("theZ");
	}

	public void settheZ(Integer theZ)
	{
		setAttribute("theZ", theZ);
	}

	// Element which is complex (has sub-elements)
	public LineNode getLine()
	{
		return (LineNode)
			getChildNode("Line", "Line");
	}

	// Element which is complex (has sub-elements)
	public CircleNode getCircle()
	{
		return (CircleNode)
			getChildNode("Circle", "Circle");
	}

	// Attribute
	public Integer gettheT()
	{
		return getIntegerAttribute("theT");
	}

	public void settheT(Integer theT)
	{
		setAttribute("theT", theT);
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Element which is complex (has sub-elements)
	public RectNode getRect()
	{
		return (RectNode)
			getChildNode("Rect", "Rect");
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
