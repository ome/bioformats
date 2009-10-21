/*
 * ome.xml.r200706.ome.OMENode
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

	public java.util.Vector getPlateList()
	{
		return getChildNodes("Plate");
	}

	public PlateNode getPlate(int index)
	{
		return (PlateNode) getChildNode("Plate", index);
	}

	// Element which occurs more than once
	public int getGroupCount()
	{
		return getChildCount("Group");
	}

	public java.util.Vector getGroupList()
	{
		return getChildNodes("Group");
	}

	public GroupNode getGroup(int index)
	{
		return (GroupNode) getChildNode("Group", index);
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

	public java.util.Vector getScreenList()
	{
		return getChildNodes("Screen");
	}

	public ScreenNode getScreen(int index)
	{
		return (ScreenNode) getChildNode("Screen", index);
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

	public java.util.Vector getDatasetList()
	{
		return getChildNodes("Dataset");
	}

	public DatasetNode getDataset(int index)
	{
		return (DatasetNode) getChildNode("Dataset", index);
	}

	// Element which occurs more than once
	public int getProjectCount()
	{
		return getChildCount("Project");
	}

	public java.util.Vector getProjectList()
	{
		return getChildNodes("Project");
	}

	public ProjectNode getProject(int index)
	{
		return (ProjectNode) getChildNode("Project", index);
	}

	// Element which occurs more than once
	public int getInstrumentCount()
	{
		return getChildCount("Instrument");
	}

	public java.util.Vector getInstrumentList()
	{
		return getChildNodes("Instrument");
	}

	public InstrumentNode getInstrument(int index)
	{
		return (InstrumentNode) getChildNode("Instrument", index);
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

	public java.util.Vector getExperimentList()
	{
		return getChildNodes("Experiment");
	}

	public ExperimentNode getExperiment(int index)
	{
		return (ExperimentNode) getChildNode("Experiment", index);
	}

	// Element which occurs more than once
	public int getExperimenterCount()
	{
		return getChildCount("Experimenter");
	}

	public java.util.Vector getExperimenterList()
	{
		return getChildNodes("Experimenter");
	}

	public ExperimenterNode getExperimenter(int index)
	{
		return (ExperimenterNode) getChildNode("Experimenter", index);
	}

	// Element which occurs more than once
	public int getImageCount()
	{
		return getChildCount("Image");
	}

	public java.util.Vector getImageList()
	{
		return getChildNodes("Image");
	}

	public ImageNode getImage(int index)
	{
		return (ImageNode) getChildNode("Image", index);
	}

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return false;
	}

}
