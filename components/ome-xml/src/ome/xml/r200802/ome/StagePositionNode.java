/*
 * ome.xml.r200802.ome.StagePositionNode
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
 * Created by curtis via xsd-fu on 2008-10-16 06:18:36-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200802.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200802.ome.*;
import ome.xml.r200802.spw.*;

import org.w3c.dom.Element;

public class StagePositionNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a StagePosition node with an associated DOM element. */
	public StagePositionNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a StagePosition node with an associated DOM element beneath
	 * a given parent.
	 */
	public StagePositionNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a StagePosition node with an associated DOM element beneath
	 * a given parent.
	 */
	public StagePositionNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "StagePosition", attach));
	}

	// -- StagePosition API methods --

	// Attribute
	public Double getPositionZ()
	{
		return getDoubleAttribute("PositionZ");
	}

	public void setPositionZ(Double positionZ)
	{
		setAttribute("PositionZ", positionZ);
	}

	// Attribute
	public Double getPositionX()
	{
		return getDoubleAttribute("PositionX");
	}

	public void setPositionX(Double positionX)
	{
		setAttribute("PositionX", positionX);
	}

	// Attribute
	public Double getPositionY()
	{
		return getDoubleAttribute("PositionY");
	}

	public void setPositionY(Double positionY)
	{
		setAttribute("PositionY", positionY);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
