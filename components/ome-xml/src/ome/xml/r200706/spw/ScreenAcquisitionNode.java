/*
 * ome.xml.r200706.spw.ScreenAcquisitionNode
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

package ome.xml.r200706.spw;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;
import ome.xml.r200706.spw.*;

import org.w3c.dom.Element;

public class ScreenAcquisitionNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a ScreenAcquisition node with an associated DOM element. */
	public ScreenAcquisitionNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a ScreenAcquisition node with an associated DOM element beneath
	 * a given parent.
	 */
	public ScreenAcquisitionNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a ScreenAcquisition node with an associated DOM element beneath
	 * a given parent.
	 */
	public ScreenAcquisitionNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "SPW:ScreenAcquisition", attach));
	}

	// -- ScreenAcquisition API methods --

	// Attribute
	public String getEndTime()
	{
		return getStringAttribute("EndTime");
	}

	public void setEndTime(String endTime)
	{
		setAttribute("EndTime", endTime);
	}

	// Element which occurs more than once and is an OME XML "Ref"
	public int getWellSampleCount()
	{
		return getChildCount("WellSampleRef");
	}

	public java.util.Vector getWellSampleList()
	{
		return getReferencedNodes("WellSample", "WellSampleRef");
	}

	public int getWellSampleRefCount()
	{
		return getChildCount("WellSampleRef");
	}

	public java.util.Vector getWellSampleRefList()
	{
		return getChildNodes("WellSampleRef");
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Attribute
	public String getStartTime()
	{
		return getStringAttribute("StartTime");
	}

	public void setStartTime(String startTime)
	{
		setAttribute("StartTime", startTime);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
