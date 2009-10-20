/*
 * ome.xml.r200809.ome.DetectorNode
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
	public Double getZoom()
	{
		return getDoubleAttribute("Zoom");
	}

	public void setZoom(Double zoom)
	{
		setAttribute("Zoom", zoom);
	}

	// Attribute
	public Double getAmplificationGain()
	{
		return getDoubleAttribute("AmplificationGain");
	}

	public void setAmplificationGain(Double amplificationGain)
	{
		setAttribute("AmplificationGain", amplificationGain);
	}

	// Attribute
	public Double getGain()
	{
		return getDoubleAttribute("Gain");
	}

	public void setGain(Double gain)
	{
		setAttribute("Gain", gain);
	}

	// Attribute
	public Double getOffset()
	{
		return getDoubleAttribute("Offset");
	}

	public void setOffset(Double offset)
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
	public Double getVoltage()
	{
		return getDoubleAttribute("Voltage");
	}

	public void setVoltage(Double voltage)
	{
		setAttribute("Voltage", voltage);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
