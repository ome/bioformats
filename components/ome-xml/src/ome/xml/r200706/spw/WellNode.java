/*
 * ome.xml.r200706.spw.WellNode
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
 * Created by curtis via xsd-fu on 2008-05-23 11:22:11-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200706.spw;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;

import java.util.Vector;
import java.util.List;

import org.w3c.dom.Element;

public class WellNode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a Well node with an associated DOM element. */
	public WellNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Well node with an associated DOM element beneath
	 * a given parent.
	 */
	public WellNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Well node with an associated DOM element beneath
	 * a given parent.
	 */
	public WellNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "SPW:Well", attach));
	}

	// -- Well API methods --
              
	// Attribute
	public String getExternalIdentifier()
	{
		return getStringAttribute("ExternalIdentifier");
	}

	public void setExternalIdentifier(String externalIdentifier)
	{
		setAttribute("ExternalIdentifier", externalIdentifier);
	}
                                            
	// Attribute
	public Integer getColumn()
	{
		return getIntegerAttribute("Column");
	}

	public void setColumn(Integer column)
	{
		setAttribute("Column", column);
	}
                                            
	// Attribute
	public String getExternalDescription()
	{
		return getStringAttribute("ExternalDescription");
	}

	public void setExternalDescription(String externalDescription)
	{
		setAttribute("ExternalDescription", externalDescription);
	}
                                                            
	// Element which is complex and is an OME XML "Ref"
	public ReagentNode getReagent()
	{
		return (ReagentNode)
			getReferencedNode("Reagent", "ReagentRef");
	}

	public ReagentRefNode getReagentRef()
	{
		return (ReagentRefNode)
			getChildNode("ReagentRef", "ReagentRef");
	}
                                    
	// Element which occurs more than once
	public int getWellSampleCount()
	{
		return getChildCount("WellSample");
	}

	public Vector getWellSampleList()
	{
		return getChildNodes("WellSample");
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
	public Integer getRow()
	{
		return getIntegerAttribute("Row");
	}

	public void setRow(Integer row)
	{
		setAttribute("Row", row);
	}
                              
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}
}

