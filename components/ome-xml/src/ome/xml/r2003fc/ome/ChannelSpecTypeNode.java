/*
 * ome.xml.r2003fc.ome.ChannelSpecTypeNode
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
 * Created by curtis via xsd-fu on 2008-10-15 12:13:43-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import org.w3c.dom.Element;

public class ChannelSpecTypeNode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a ChannelSpecType node with an associated DOM element. */
	public ChannelSpecTypeNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a ChannelSpecType node with an associated DOM element beneath
	 * a given parent.
	 */
	public ChannelSpecTypeNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a ChannelSpecType node with an associated DOM element beneath
	 * a given parent.
	 */
	public ChannelSpecTypeNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "ChannelSpecType", attach));
	}

	// -- ChannelSpecType API methods --
              
	// Attribute
	public Integer getChannelNumber()
	{
		return getIntegerAttribute("ChannelNumber");
	}

	public void setChannelNumber(Integer channelNumber)
	{
		setAttribute("ChannelNumber", channelNumber);
	}
                                            
	// Attribute
	public Float getWhiteLevel()
	{
		return getFloatAttribute("WhiteLevel");
	}

	public void setWhiteLevel(Float whiteLevel)
	{
		setAttribute("WhiteLevel", whiteLevel);
	}
                                            
	// Attribute
	public Float getBlackLevel()
	{
		return getFloatAttribute("BlackLevel");
	}

	public void setBlackLevel(Float blackLevel)
	{
		setAttribute("BlackLevel", blackLevel);
	}
                                            
	// Attribute
	public Float getGamma()
	{
		return getFloatAttribute("Gamma");
	}

	public void setGamma(Float gamma)
	{
		setAttribute("Gamma", gamma);
	}
                                            
	// Attribute
	public Boolean getisOn()
	{
		return getBooleanAttribute("isOn");
	}

	public void setisOn(Boolean isOn)
	{
		setAttribute("isOn", isOn);
	}
                              
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}
}

