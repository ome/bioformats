/*
 * ome.xml.r2003fc.ome.OMENode
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
 * Created by curtis via xsd-fu on 2008-10-15 11:46:17-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import java.util.Vector;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import org.w3c.dom.Element;

public class OMENode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a OME node with an associated DOM element. */
	public OMENode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a OME node with an associated DOM element beneath
	 * a given parent.
	 */
	public OMENode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a OME node with an associated DOM element beneath
	 * a given parent.
	 */
	public OMENode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "OME", attach));
	}

	// -- OME API methods --
                      
	// Element which occurs more than once
	public int getPlateCount()
	{
		return getChildCount("Plate");
	}

	public Vector getPlateList()
	{
		return getChildNodes("Plate");
	}
                                            
	// Element which occurs more than once
	public int getGroupCount()
	{
		return getChildCount("Group");
	}

	public Vector getGroupList()
	{
		return getChildNodes("Group");
	}
                                                
	// Element which is not complex (has only a text node)
	public String getSemanticTypeDefinitions()
	{
		return getStringCData("SemanticTypeDefinitions");
	}

	public void setSemanticTypeDefinitions(String semanticTypeDefinitions)
	{
		setCData("SemanticTypeDefinitions", semanticTypeDefinitions);
	}
                                        
	// Element which occurs more than once
	public int getScreenCount()
	{
		return getChildCount("Screen");
	}

	public Vector getScreenList()
	{
		return getChildNodes("Screen");
	}
                                                
	// Element which is not complex (has only a text node)
	public String getAnalysisModuleLibrary()
	{
		return getStringCData("AnalysisModuleLibrary");
	}

	public void setAnalysisModuleLibrary(String analysisModuleLibrary)
	{
		setCData("AnalysisModuleLibrary", analysisModuleLibrary);
	}
                                        
	// Element which occurs more than once
	public int getDatasetCount()
	{
		return getChildCount("Dataset");
	}

	public Vector getDatasetList()
	{
		return getChildNodes("Dataset");
	}
                                            
	// Element which occurs more than once
	public int getProjectCount()
	{
		return getChildCount("Project");
	}

	public Vector getProjectList()
	{
		return getChildNodes("Project");
	}
                                            
	// Element which occurs more than once
	public int getInstrumentCount()
	{
		return getChildCount("Instrument");
	}

	public Vector getInstrumentList()
	{
		return getChildNodes("Instrument");
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
                                        
	// Element which occurs more than once
	public int getExperimentCount()
	{
		return getChildCount("Experiment");
	}

	public Vector getExperimentList()
	{
		return getChildNodes("Experiment");
	}
                                            
	// Element which occurs more than once
	public int getExperimenterCount()
	{
		return getChildCount("Experimenter");
	}

	public Vector getExperimenterList()
	{
		return getChildNodes("Experimenter");
	}
                                            
	// Element which occurs more than once
	public int getImageCount()
	{
		return getChildCount("Image");
	}

	public Vector getImageList()
	{
		return getChildNodes("Image");
	}
                      
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}
}

