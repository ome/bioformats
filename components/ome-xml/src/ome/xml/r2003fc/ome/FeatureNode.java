/*
 * ome.xml.r2003fc.ome.FeatureNode
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
 * Created by curtis via xsd-fu on 2008-05-31 10:06:36-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import java.util.Vector;
import java.util.List;

import org.w3c.dom.Element;

public class FeatureNode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a Feature node with an associated DOM element. */
	public FeatureNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Feature node with an associated DOM element beneath
	 * a given parent.
	 */
	public FeatureNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Feature node with an associated DOM element beneath
	 * a given parent.
	 */
	public FeatureNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Feature", attach));
	}

	// -- Feature API methods --
                      
	// Element which occurs more than once
	public int getFeatureCount()
	{
		return getChildCount("Feature");
	}

	public Vector getFeatureList()
	{
		return getChildNodes("Feature");
	}
                                                
	// Element which is not complex (has only a text node)
	public String getCustomAttributes()
	{
		return getStringCData("CustomAttributes");
	}

	public void setCustomAttributes(String customAttributes)
	{
		setCData("CustomAttributes", customAttributes);
	}
                                
	// Attribute
	public String getTag()
	{
		return getStringAttribute("Tag");
	}

	public void setTag(String tag)
	{
		setAttribute("Tag", tag);
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
                                                                    
	// *** WARNING *** Unhandled or skipped property ID
      
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}
}

