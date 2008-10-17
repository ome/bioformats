/*
 * ome.xml.r2003fc.ome.DatasetNode
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
 * Created by curtis via xsd-fu on 2008-10-16 05:38:12-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

import org.w3c.dom.Element;

public class DatasetNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Dataset node with an associated DOM element. */
	public DatasetNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Dataset node with an associated DOM element beneath
	 * a given parent.
	 */
	public DatasetNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Dataset node with an associated DOM element beneath
	 * a given parent.
	 */
	public DatasetNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Dataset", attach));
	}

	// -- Dataset API methods --

	// Attribute
	public Boolean getLocked()
	{
		return getBooleanAttribute("Locked");
	}

	public void setLocked(Boolean locked)
	{
		setAttribute("Locked", locked);
	}

	// Element which is not complex (has only a text node)
	public String getDescription()
	{
		return getStringCData("Description");
	}

	public void setDescription(String description)
	{
		setCData("Description", description);
	}

	// Element which is complex and is an OME XML "Ref"
	public ExperimenterNode getExperimenter()
	{
		return (ExperimenterNode)
			getReferencedNode("Experimenter", "ExperimenterRef");
	}

	public ExperimenterRefNode getExperimenterRef()
	{
		return (ExperimenterRefNode)
			getChildNode("ExperimenterRef", "ExperimenterRef");
	}

	// Element which occurs more than once and is an OME XML "Ref"
	public int getProjectCount()
	{
		return getChildCount("ProjectRef");
	}

	public java.util.Vector getProjectList()
	{
		return getReferencedNodes("Project", "ProjectRef");
	}

	public int getProjectRefCount()
	{
		return getChildCount("ProjectRef");
	}

	public java.util.Vector getProjectRefList()
	{
		return getChildNodes("ProjectRef");
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

	// Element which is complex and is an OME XML "Ref"
	public GroupNode getGroup()
	{
		return (GroupNode)
			getReferencedNode("Group", "GroupRef");
	}

	public GroupRefNode getGroupRef()
	{
		return (GroupRefNode)
			getChildNode("GroupRef", "GroupRef");
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

	// *** WARNING *** Unhandled or skipped property ID

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
		return true;
	}

}
