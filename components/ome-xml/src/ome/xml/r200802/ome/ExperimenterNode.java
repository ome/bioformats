/*
 * ome.xml.r200802.ome.ExperimenterNode
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
 * Created by curtis via xsd-fu on 2008-05-30 12:57:22-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200802.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import java.util.Vector;
import java.util.List;

import org.w3c.dom.Element;

public class ExperimenterNode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a Experimenter node with an associated DOM element. */
	public ExperimenterNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Experimenter node with an associated DOM element beneath
	 * a given parent.
	 */
	public ExperimenterNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Experimenter node with an associated DOM element beneath
	 * a given parent.
	 */
	public ExperimenterNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Experimenter", attach));
	}

	// -- Experimenter API methods --
                          
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
                        
	// Virtual, inferred back reference Image_BackReference
	public int getImageCount()
	{
		return getReferringCount("Image");
	}

	public List getImageList()
	{
		return getReferringNodes("Image");
	}
                                                        
	// Element which occurs more than once and is an OME XML "Ref"
	public int getGroupCount()
	{
		return getChildCount("GroupRef");
	}

	public Vector getGroupList()
	{
		return getReferencedNodes("Group", "GroupRef");
	}

	public Vector getGroupRefList()
	{
		return getChildNodes("GroupRef");
	}
                                                                
	// *** WARNING *** Unhandled or skipped property ID
            
	// Virtual, inferred back reference Project_BackReference
	public int getProjectCount()
	{
		return getReferringCount("Project");
	}

	public List getProjectList()
	{
		return getReferringNodes("Project");
	}
                                            
	// Virtual, inferred back reference Experiment_BackReference
	public int getExperimentCount()
	{
		return getReferringCount("Experiment");
	}

	public List getExperimentList()
	{
		return getReferringNodes("Experiment");
	}
                                            
	// Virtual, inferred back reference Dataset_BackReference
	public int getDatasetCount()
	{
		return getReferringCount("Dataset");
	}

	public List getDatasetList()
	{
		return getReferringNodes("Dataset");
	}
                                                                
	// Element which is not complex (has only a text node)
	public String getInstitution()
	{
		return getStringCData("Institution");
	}

	public void setInstitution(String institution)
	{
		setCData("Institution", institution);
	}
                        
	// Virtual, inferred back reference MicrobeamManipulation_BackReference
	public int getMicrobeamManipulationCount()
	{
		return getReferringCount("MicrobeamManipulation");
	}

	public List getMicrobeamManipulationList()
	{
		return getReferringNodes("MicrobeamManipulation");
	}
                                      
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}
}

