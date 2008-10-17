/*
 * ome.xml.r200706.ome.DetectorNode
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
 * Created by curtis via xsd-fu on 2008-10-16 05:38:13-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200706.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;
import ome.xml.r200706.spw.*;

import org.w3c.dom.Element;

public class DetectorNode extends ManufactSpecNode
{

	// -- Constructors --

	/** Constructs a Detector node with an associated DOM element. */
	public DetectorNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Detector node with an associated DOM element beneath
	 * a given parent.
	 */
	public DetectorNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Detector node with an associated DOM element beneath
	 * a given parent.
	 */
	public DetectorNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Detector", attach));
	}

	// -- Detector API methods --

	// Attribute
	public Float getZoom()
	{
		return getFloatAttribute("Zoom");
	}

	public void setZoom(Float zoom)
	{
		setAttribute("Zoom", zoom);
	}

	// Attribute
	public Float getAmplificationGain()
	{
		return getFloatAttribute("AmplificationGain");
	}

	public void setAmplificationGain(Float amplificationGain)
	{
		setAttribute("AmplificationGain", amplificationGain);
	}

	// Attribute
	public Float getGain()
	{
		return getFloatAttribute("Gain");
	}

	public void setGain(Float gain)
	{
		setAttribute("Gain", gain);
	}

	// Attribute
	public Float getOffset()
	{
		return getFloatAttribute("Offset");
	}

	public void setOffset(Float offset)
	{
		setAttribute("Offset", offset);
	}

	// Virtual, inferred back reference LogicalChannel_BackReference
	public int getReferringLogicalChannelCount()
	{
		return getReferringCount("LogicalChannel");
	}

	public java.util.List getReferringLogicalChannelList()
	{
		return getReferringNodes("LogicalChannel");
	}

	// Attribute
	public String getType()
	{
		return getStringAttribute("Type");
	}

	public void setType(String type)
	{
		setAttribute("Type", type);
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Attribute
	public Float getVoltage()
	{
		return getFloatAttribute("Voltage");
	}

	public void setVoltage(Float voltage)
	{
		setAttribute("Voltage", voltage);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
