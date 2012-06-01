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
