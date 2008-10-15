/*
 * ome.xml.r2003fc.ome.ExperimenterNode
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

public class ExperimenterNode extends ExperimenterTypeNode
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
      
	// Virtual, inferred back reference Project_BackReference
	public int getProjectCount()
	{
		return getReferringCount("Project");
	}

	public java.util.List getProjectList()
	{
		return getReferringNodes("Project");
	}
                                            
	// Virtual, inferred back reference Image_BackReference
	public int getImageCount()
	{
		return getReferringCount("Image");
	}

	public java.util.List getImageList()
	{
		return getReferringNodes("Image");
	}
                                            
	// Virtual, inferred back reference Experiment_BackReference
	public int getExperimentCount()
	{
		return getReferringCount("Experiment");
	}

	public java.util.List getExperimentList()
	{
		return getReferringNodes("Experiment");
	}
                                            
	// Virtual, inferred back reference Dataset_BackReference
	public int getDatasetCount()
	{
		return getReferringCount("Dataset");
	}

	public java.util.List getDatasetList()
	{
		return getReferringNodes("Dataset");
	}
                                      
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}
}

