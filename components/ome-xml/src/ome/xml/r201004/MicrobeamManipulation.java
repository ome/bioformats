/*
 * ome.xml.r201004.MicrobeamManipulation
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2010 Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-20 12:31:20+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.xml.r201004.enums.*;

public class MicrobeamManipulation extends Object
{
	// -- Instance variables --

	// Property
	private MicrobeamManipulationType type;

	// Property
	private String id;

	// Property which occurs more than once
	private List<ROI> roiList = new ArrayList<ROI>();

	// Property
	private Experimenter experimenter;

	// Property which occurs more than once
	private List<LightSourceSettings> lightSourceSettingsList = new ArrayList<LightSourceSettings>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// -- Constructors --

	/** Constructs a MicrobeamManipulation. */
	public MicrobeamManipulation()
	{
	}

	// -- MicrobeamManipulation API methods --

	// Property
	public MicrobeamManipulationType getType()
	{
		return type;
	}

	public void setType(MicrobeamManipulationType type)
	{
		this.type = type;
	}

	// Property
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property which occurs more than once
	public int sizeOfROIList()
	{
		return roiList.size();
	}

	public List<ROI> copyROIList()
	{
		return new ArrayList<ROI>(roiList);
	}

	public ROI getROI(int index)
	{
		return roiList.get(index);
	}

	public ROI setROI(int index, ROI roi)
	{
		return roiList.set(index, roi);
	}

	public void addROI(ROI roi)
	{
		roiList.add(roi);
	}

	public void removeROI(ROI roi)
	{
		roiList.remove(roi);
	}

	// Property
	public Experimenter getExperimenter()
	{
		return experimenter;
	}

	public void setExperimenter(Experimenter experimenter)
	{
		this.experimenter = experimenter;
	}

	// Property which occurs more than once
	public int sizeOfLightSourceSettingsList()
	{
		return lightSourceSettingsList.size();
	}

	public List<LightSourceSettings> copyLightSourceSettingsList()
	{
		return new ArrayList<LightSourceSettings>(lightSourceSettingsList);
	}

	public LightSourceSettings getLightSourceSettings(int index)
	{
		return lightSourceSettingsList.get(index);
	}

	public LightSourceSettings setLightSourceSettings(int index, LightSourceSettings lightSourceSettings)
	{
		return lightSourceSettingsList.set(index, lightSourceSettings);
	}

	public void addLightSourceSettings(LightSourceSettings lightSourceSettings)
	{
		lightSourceSettingsList.add(lightSourceSettings);
	}

	public void removeLightSourceSettings(LightSourceSettings lightSourceSettings)
	{
		lightSourceSettingsList.remove(lightSourceSettings);
	}

	// Back reference Image_BackReference
	public int sizeOfLinkedImageList()
	{
		return image_BackReferenceList.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(image_BackReferenceList);
	}

	public Image getLinkedImage(int index)
	{
		return image_BackReferenceList.get(index);
	}

	public Image setLinkedImage(int index, Image image_BackReference)
	{
		return image_BackReferenceList.set(index, image_BackReference);
	}

	public void linkImage(Image image_BackReference)
	{
		this.image_BackReferenceList.add(image_BackReference);
	}

	public void unlinkImage(Image image_BackReference)
	{
		this.image_BackReferenceList.add(image_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for MicrobeamManipulation
		Element MicrobeamManipulation_element = document.createElement("MicrobeamManipulation");
		if (type != null)
		{
			// Attribute property Type
			MicrobeamManipulation_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			MicrobeamManipulation_element.setAttribute("ID", id.toString());
		}
		if (roiList != null)
		{
			// Element property ROIRef which is complex (has
			// sub-elements) and occurs more than once
			for (ROI roiList_value : roiList)
			{
				MicrobeamManipulation_element.appendChild(roiList_value.asXMLElement(document));
			}
		}
		if (experimenter != null)
		{
			// Element property ExperimenterRef which is complex (has
			// sub-elements)
			MicrobeamManipulation_element.appendChild(experimenter.asXMLElement(document));
		}
		if (lightSourceSettingsList != null)
		{
			// Element property LightSourceSettings which is complex (has
			// sub-elements) and occurs more than once
			for (LightSourceSettings lightSourceSettingsList_value : lightSourceSettingsList)
			{
				MicrobeamManipulation_element.appendChild(lightSourceSettingsList_value.asXMLElement(document));
			}
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		return MicrobeamManipulation_element;
	}

	public static MicrobeamManipulation fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"MicrobeamManipulation".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of MicrobeamManipulation got %s",
					tagName));
		}
		MicrobeamManipulation instance = new MicrobeamManipulation();
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			instance.setType(MicrobeamManipulationType.fromString(
					element.getAttribute("Type")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// Element property ROIRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList ROIRef_nodeList = element.getElementsByTagName("ROIRef");
		for (int i = 0; i < ROIRef_nodeList.getLength(); i++)
		{
			instance.addROI(ROI.fromXMLElement(
					(Element) ROIRef_nodeList.item(i)));
		}
		NodeList ExperimenterRef_nodeList = element.getElementsByTagName("ExperimenterRef");
		if (ExperimenterRef_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"ExperimenterRef node list size %d != 1",
					ExperimenterRef_nodeList.getLength()));
		}
		else if (ExperimenterRef_nodeList.getLength() != 0)
		{
			// Element property ExperimenterRef which is complex (has
			// sub-elements)
			instance.setExperimenter(Experimenter.fromXMLElement(
					(Element) ExperimenterRef_nodeList.item(0)));
		}
		// Element property LightSourceSettings which is complex (has
		// sub-elements) and occurs more than once
		NodeList LightSourceSettings_nodeList = element.getElementsByTagName("LightSourceSettings");
		for (int i = 0; i < LightSourceSettings_nodeList.getLength(); i++)
		{
			instance.addLightSourceSettings(LightSourceSettings.fromXMLElement(
					(Element) LightSourceSettings_nodeList.item(i)));
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		return instance;
	}
}
