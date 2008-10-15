/*
 * ome.xml.r200706.ome.LightSourceNode
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

package ome.xml.r200706.ome;

import java.util.Vector;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import org.w3c.dom.Element;

public class LightSourceNode extends ManufactSpecNode
{
	// -- Constructors --

	/** Constructs a LightSource node with an associated DOM element. */
	public LightSourceNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a LightSource node with an associated DOM element beneath
	 * a given parent.
	 */
	public LightSourceNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a LightSource node with an associated DOM element beneath
	 * a given parent.
	 */
	public LightSourceNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "LightSource", attach));
	}

	// -- LightSource API methods --
                                  
	// Element which is complex (has sub-elements)
	public LaserNode getLaser()
	{
		return (LaserNode)
			getChildNode("Laser", "Laser");
	}
                        
	// Attribute
	public Float getPower()
	{
		return getFloatAttribute("Power");
	}

	public void setPower(Float power)
	{
		setAttribute("Power", power);
	}
                                                                
	// Element which is complex (has sub-elements)
	public ArcNode getArc()
	{
		return (ArcNode)
			getChildNode("Arc", "Arc");
	}
                
	// Virtual, inferred back reference LogicalChannel_BackReference
	public int getLogicalChannelCount()
	{
		return getReferringCount("LogicalChannel");
	}

	public java.util.List getLogicalChannelList()
	{
		return getReferringNodes("LogicalChannel");
	}
                                                                            
	// *** WARNING *** Unhandled or skipped property ID
                                        
	// Element which is complex (has sub-elements)
	public FilamentNode getFilament()
	{
		return (FilamentNode)
			getChildNode("Filament", "Filament");
	}
                
	// Virtual, inferred back reference MicrobeamManipulation_BackReference
	public int getMicrobeamManipulationCount()
	{
		return getReferringCount("MicrobeamManipulation");
	}

	public java.util.List getMicrobeamManipulationList()
	{
		return getReferringNodes("MicrobeamManipulation");
	}
                                      
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}
}

