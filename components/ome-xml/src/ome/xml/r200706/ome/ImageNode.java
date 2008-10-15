/*
 * ome.xml.r200706.ome.ImageNode
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

package ome.xml.r200706.ome;

import java.util.Vector;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;

import org.w3c.dom.Element;

public class ImageNode extends OMEXMLNode
{
	// -- Constructors --

	/** Constructs a Image node with an associated DOM element. */
	public ImageNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Image node with an associated DOM element beneath
	 * a given parent.
	 */
	public ImageNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Image node with an associated DOM element beneath
	 * a given parent.
	 */
	public ImageNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Image", attach));
	}

	// -- Image API methods --
                                  
	// Element which is complex (has sub-elements)
	public ImagingEnvironmentNode getImagingEnvironment()
	{
		return (ImagingEnvironmentNode)
			getChildNode("ImagingEnvironment", "ImagingEnvironment");
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
                                            
	// Element which is complex and is an OME XML "Ref"
	public ObjectiveSettingsNode getObjectiveSettings()
	{
		return (ObjectiveSettingsNode)
			getReferencedNode("ObjectiveSettings", "ObjectiveSettingsRef");
	}

	public ObjectiveSettingsRefNode getObjectiveSettingsRef()
	{
		return (ObjectiveSettingsRefNode)
			getChildNode("ObjectiveSettingsRef", "ObjectiveSettingsRef");
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
	public int getLogicalChannelCount()
	{
		return getChildCount("LogicalChannel");
	}

	public Vector getLogicalChannelList()
	{
		return getChildNodes("LogicalChannel");
	}
                                                        
	// Element which is complex (has sub-elements)
	public ThumbnailNode getThumbnail()
	{
		return (ThumbnailNode)
			getChildNode("Thumbnail", "Thumbnail");
	}
                                
	// Element which occurs more than once
	public int getROICount()
	{
		return getChildCount("ROI");
	}

	public Vector getROIList()
	{
		return getChildNodes("ROI");
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
                                                    
	// Element which is complex (has sub-elements)
	public StageLabelNode getStageLabel()
	{
		return (StageLabelNode)
			getChildNode("StageLabel", "StageLabel");
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
                                            
	// Element which is complex and is an OME XML "Ref"
	public InstrumentNode getInstrument()
	{
		return (InstrumentNode)
			getReferencedNode("Instrument", "InstrumentRef");
	}

	public InstrumentRefNode getInstrumentRef()
	{
		return (InstrumentRefNode)
			getChildNode("InstrumentRef", "InstrumentRef");
	}
                                    
	// Element which occurs more than once
	public int getPixelsCount()
	{
		return getChildCount("Pixels");
	}

	public Vector getPixelsList()
	{
		return getChildNodes("Pixels");
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
                                                    
	// Element which occurs more than once
	public int getRegionCount()
	{
		return getChildCount("Region");
	}

	public Vector getRegionList()
	{
		return getChildNodes("Region");
	}
                                                            
	// *** WARNING *** Unhandled or skipped property ID
                            
	// Element which occurs more than once
	public int getMicrobeamManipulationCount()
	{
		return getChildCount("MicrobeamManipulation");
	}

	public Vector getMicrobeamManipulationList()
	{
		return getChildNodes("MicrobeamManipulation");
	}
                                                    
	// Element which is complex and is an OME XML "Ref"
	public ExperimentNode getExperiment()
	{
		return (ExperimentNode)
			getReferencedNode("Experiment", "ExperimentRef");
	}

	public ExperimentRefNode getExperimentRef()
	{
		return (ExperimentRefNode)
			getChildNode("ExperimentRef", "ExperimentRef");
	}
                        
	// Attribute which is an OME XML "ID"
	public PixelsNode getDefaultPixels()
	{
		return (PixelsNode)
			getAttrReferencedNode("Pixels", "DefaultPixels");
	}
                                                    
	// Element which occurs more than once and is an OME XML "Ref"
	public int getDatasetCount()
	{
		return getChildCount("DatasetRef");
	}

	public Vector getDatasetList()
	{
		return getReferencedNodes("Dataset", "DatasetRef");
	}

	public Vector getDatasetRefList()
	{
		return getChildNodes("DatasetRef");
	}
                                                        
	// Element which is complex and is an OME XML "Ref"
	public PixelsNode getAcquiredPixels()
	{
		return (PixelsNode)
			getReferencedNode("AcquiredPixels", "AcquiredPixelsRef");
	}

	public PixelsNode getAcquiredPixelsRef()
	{
		return (PixelsNode)
			getChildNode("Pixels", "AcquiredPixelsRef");
	}
                                        
	// Element which is not complex (has only a text node)
	public String getCreationDate()
	{
		return getStringCData("CreationDate");
	}

	public void setCreationDate(String creationDate)
	{
		setCData("CreationDate", creationDate);
	}
                                                    
	// Element which is complex (has sub-elements)
	public DisplayOptionsNode getDisplayOptions()
	{
		return (DisplayOptionsNode)
			getChildNode("DisplayOptions", "DisplayOptions");
	}
          
	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}
}

