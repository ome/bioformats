/*
 * ome.xml.r2003fc.ome.ExperimenterTypeNode
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
