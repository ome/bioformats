/*
 * ome.xml.r200706.ome.GroupNode
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

package ome.xml.r200706.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;
import ome.xml.r200706.spw.*;

import org.w3c.dom.Element;

public class GroupNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Group node with an associated DOM element. */
	public GroupNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Group node with an associated DOM element beneath
	 * a given parent.
	 */
	public GroupNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Group node with an associated DOM element beneath
	 * a given parent.
	 */
	public GroupNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Group", attach));
	}

	// -- Group API methods --

	// Attribute
	public String getName()
	{
		return getStringAttribute("Name");
	}

	public void setName(String name)
	{
		setAttribute("Name", name);
	}

	// Virtual, inferred back reference Image_BackReference
	public int getReferringImageCount()
	{
		return getReferringCount("Image");
	}

	public java.util.List getReferringImageList()
	{
		return getReferringNodes("Image");
	}

	// Virtual, inferred back reference Dataset_BackReference
	public int getReferringDatasetCount()
	{
		return getReferringCount("Dataset");
	}

	public java.util.List getReferringDatasetList()
	{
		return getReferringNodes("Dataset");
	}

	// Virtual, inferred back reference Project_BackReference
	public int getReferringProjectCount()
	{
		return getReferringCount("Project");
	}

	public java.util.List getReferringProjectList()
	{
		return getReferringNodes("Project");
	}

	// Element which is complex (has sub-elements)
	public ContactNode getContact()
	{
		return (ContactNode)
			getChildNode("Contact", "Contact");
	}

	// Virtual, inferred back reference Experimenter_BackReference
	public int getReferringExperimenterCount()
	{
		return getReferringCount("Experimenter");
	}

	public java.util.List getReferringExperimenterList()
	{
		return getReferringNodes("Experimenter");
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Element which is complex (has sub-elements)
	public ExperimenterRefNode getLeader()
	{
		return (ExperimenterRefNode)
			getChildNode("ExperimenterRef", "Leader");
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
