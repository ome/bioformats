/*
 * ome.xml.r200802.spw.PlateNode
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
 * Created by curtis via xsd-fu on 2008-10-16 06:18:37-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200802.spw;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200802.ome.*;
import ome.xml.r200802.spw.*;

import org.w3c.dom.Element;

public class PlateNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Plate node with an associated DOM element. */
	public PlateNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Plate node with an associated DOM element beneath
	 * a given parent.
	 */
	public PlateNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Plate node with an associated DOM element beneath
	 * a given parent.
	 */
	public PlateNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "SPW:Plate", attach));
	}

	// -- Plate API methods --

	// Attribute
	public String getStatus()
	{
		return getStringAttribute("Status");
	}

	public void setStatus(String status)
	{
		setAttribute("Status", status);
	}

	// Attribute
	public String getName()
	{
		return getStringAttribute("Name");
	}

	public void setName(String name)
	{
		setAttribute("Name", name);
	}

	// Attribute
	public String getExternalIdentifier()
	{
		return getStringAttribute("ExternalIdentifier");
	}

	public void setExternalIdentifier(String externalIdentifier)
	{
		setAttribute("ExternalIdentifier", externalIdentifier);
	}

	// Virtual, inferred back reference Screen_BackReference
	public int getReferringScreenCount()
	{
		return getReferringCount("Screen");
	}

	public java.util.List getReferringScreenList()
	{
		return getReferringNodes("Screen");
	}

	// Element which occurs more than once
	public int getWellCount()
	{
		return getChildCount("Well");
	}

	public java.util.Vector getWellList()
	{
		return getChildNodes("Well");
	}

	public WellNode getWell(int index)
	{
		return (WellNode) getChildNode("Well", index);
	}

	// Element which occurs more than once and is an OME XML "Ref"
	public int getScreenCount()
	{
		return getChildCount("ScreenRef");
	}

	public java.util.Vector getScreenList()
	{
		return getReferencedNodes("Screen", "ScreenRef");
	}

	public int getScreenRefCount()
	{
		return getChildCount("ScreenRef");
	}

	public java.util.Vector getScreenRefList()
	{
		return getChildNodes("ScreenRef");
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Attribute
	public String getDescription()
	{
		return getStringAttribute("Description");
	}

	public void setDescription(String description)
	{
		setAttribute("Description", description);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
