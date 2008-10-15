/*
 * ome.xml.r2003fc.ome.StageLabelNode
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
 * Created by curtis via xsd-fu on 2008-10-15 11:46:17-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import java.util.Vector;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import org.w3c.dom.Element;

public class StageLabelNode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a StageLabel node with an associated DOM element. */
	public StageLabelNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a StageLabel node with an associated DOM element beneath
	 * a given parent.
	 */
	public StageLabelNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a StageLabel node with an associated DOM element beneath
	 * a given parent.
	 */
	public StageLabelNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "StageLabel", attach));
	}

	// -- StageLabel API methods --
              
	// Attribute
	public Float getY()
	{
		return getFloatAttribute("Y");
	}

	public void setY(Float y)
	{
		setAttribute("Y", y);
	}
                                            
	// Attribute
	public Float getX()
	{
		return getFloatAttribute("X");
	}

	public void setX(Float x)
	{
		setAttribute("X", x);
	}
                                            
	// Attribute
	public Float getZ()
	{
		return getFloatAttribute("Z");
	}

	public void setZ(Float z)
	{
		setAttribute("Z", z);
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
                              
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}
}

