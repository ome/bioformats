/*
 * ome.xml.r2003fc.ome.FilterNode
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

public class FilterNode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a Filter node with an associated DOM element. */
	public FilterNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Filter node with an associated DOM element beneath
	 * a given parent.
	 */
	public FilterNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Filter node with an associated DOM element beneath
	 * a given parent.
	 */
	public FilterNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Filter", attach));
	}

	// -- Filter API methods --
                                  
	// Element which is complex (has sub-elements)
	public DichroicNode getDichroic()
	{
		return (DichroicNode)
			getChildNode("Dichroic", "Dichroic");
	}
                                            
	// Element which is complex (has sub-elements)
	public ExFilterNode getExFilter()
	{
		return (ExFilterNode)
			getChildNode("ExFilter", "ExFilter");
	}
                
	// Virtual, inferred back reference OTF_BackReference
	public int getOTFCount()
	{
		return getReferringCount("OTF");
	}

	public java.util.List getOTFList()
	{
		return getReferringNodes("OTF");
	}
                                                                        
	// Element which is complex (has sub-elements)
	public EmFilterNode getEmFilter()
	{
		return (EmFilterNode)
			getChildNode("EmFilter", "EmFilter");
	}
                                                
	// *** WARNING *** Unhandled or skipped property ID
            
	// Virtual, inferred back reference ChannelInfo_BackReference
	public int getChannelInfoCount()
	{
		return getReferringCount("ChannelInfo");
	}

	public java.util.List getChannelInfoList()
	{
		return getReferringNodes("ChannelInfo");
	}
                                                                        
	// Element which is complex (has sub-elements)
	public FilterSetNode getFilterSet()
	{
		return (FilterSetNode)
			getChildNode("FilterSet", "FilterSet");
	}
          
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}
}

