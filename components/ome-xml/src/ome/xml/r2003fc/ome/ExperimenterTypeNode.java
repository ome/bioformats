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

public class ExperimenterTypeNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a ExperimenterType node with an associated DOM element. */
	public ExperimenterTypeNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a ExperimenterType node with an associated DOM element beneath
	 * a given parent.
	 */
	public ExperimenterTypeNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a ExperimenterType node with an associated DOM element beneath
	 * a given parent.
	 */
	public ExperimenterTypeNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "ExperimenterType", attach));
	}

	// -- ExperimenterType API methods --

	// Element which is not complex (has only a text node)
	public String getEmail()
	{
		return getStringCData("Email");
	}

	public void setEmail(String email)
	{
		setCData("Email", email);
	}

	// Element which is not complex (has only a text node)
	public String getOMEName()
	{
		return getStringCData("OMEName");
	}

	public void setOMEName(String omename)
	{
		setCData("OMEName", omename);
	}

	// Element which is not complex (has only a text node)
	public String getFirstName()
	{
		return getStringCData("FirstName");
	}

	public void setFirstName(String firstName)
	{
		setCData("FirstName", firstName);
	}

	// Element which is not complex (has only a text node)
	public String getLastName()
	{
		return getStringCData("LastName");
	}

	public void setLastName(String lastName)
	{
		setCData("LastName", lastName);
	}

	// Element which occurs more than once and is an OME XML "Ref"
	public int getGroupCount()
	{
		return getChildCount("GroupRef");
	}

	public java.util.Vector getGroupList()
	{
		return getReferencedNodes("Group", "GroupRef");
	}

	public int getGroupRefCount()
	{
		return getChildCount("GroupRef");
	}

	public java.util.Vector getGroupRefList()
	{
		return getChildNodes("GroupRef");
	}

	// *** WARNING *** Unhandled or skipped property ID

	// Element which is not complex (has only a text node)
	public String getInstitution()
	{
		return getStringCData("Institution");
	}

	public void setInstitution(String institution)
	{
		setCData("Institution", institution);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
