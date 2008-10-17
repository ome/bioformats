/*
 * ome.xml.r200802.spw.WellSampleNode
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
 * Created by curtis via xsd-fu on 2008-10-16 05:38:14-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200802.spw;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200802.ome.*;
import ome.xml.r200802.spw.*;

import org.w3c.dom.Element;

public class WellSampleNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a WellSample node with an associated DOM element. */
	public WellSampleNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a WellSample node with an associated DOM element beneath
	 * a given parent.
	 */
	public WellSampleNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a WellSample node with an associated DOM element beneath
	 * a given parent.
	 */
	public WellSampleNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "SPW:WellSample", attach));
	}

	// -- WellSample API methods --

	// Attribute
	public Integer getIndex()
	{
		return getIntegerAttribute("Index");
	}

	public void setIndex(Integer index)
	{
		setAttribute("Index", index);
	}

	// Element which is complex and is an OME XML "Ref"
	public ImageNode getImage()
	{
		return (ImageNode)
			getReferencedNode("Image", "ImageRef");
	}

	public ImageRefNode getImageRef()
	{
		return (ImageRefNode)
			getChildNode("ImageRef", "ImageRef");
	}

	// Attribute
	public Integer getTimepoint()
	{
		return getIntegerAttribute("Timepoint");
	}

	public void setTimepoint(Integer timepoint)
	{
		setAttribute("Timepoint", timepoint);
	}

	// Attribute
	public Float getPosX()
	{
		return getFloatAttribute("PosX");
	}

	public void setPosX(Float posX)
	{
		setAttribute("PosX", posX);
	}

	// Attribute
	public Float getPosY()
	{
		return getFloatAttribute("PosY");
	}

	public void setPosY(Float posY)
	{
		setAttribute("PosY", posY);
	}

	// Virtual, inferred back reference ScreenAcquisition_BackReference
	public int getReferringScreenAcquisitionCount()
	{
		return getReferringCount("ScreenAcquisition");
	}

	public java.util.List getReferringScreenAcquisitionList()
	{
		return getReferringNodes("ScreenAcquisition");
	}

	// *** WARNING *** Unhandled or skipped property ID

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
