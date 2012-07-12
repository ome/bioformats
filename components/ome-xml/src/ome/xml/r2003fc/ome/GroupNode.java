/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via xsd-fu on 2008-10-16 06:18:35-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

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

	// Virtual, inferred back reference Project_BackReference
	public int getReferringProjectCount()
	{
		return getReferringCount("Project");
	}

	public java.util.List getReferringProjectList()
	{
		return getReferringNodes("Project");
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

	// Virtual, inferred back reference Image_BackReference
	public int getReferringImageCount()
	{
		return getReferringCount("Image");
	}

	public java.util.List getReferringImageList()
	{
		return getReferringNodes("Image");
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Virtual, inferred back reference ExperimenterType_BackReference
	public int getReferringExperimenterTypeCount()
	{
		return getReferringCount("ExperimenterType");
	}

	public java.util.List getReferringExperimenterTypeList()
	{
		return getReferringNodes("ExperimenterType");
	}

	// Element which is complex (has sub-elements)
	public ContactNode getContact()
	{
		return (ContactNode)
			getChildNode("Contact", "Contact");
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
