
/*
 * ome.xml.r201004.Experiment
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) @year@ Open Microscopy Environment
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
 * Created by callan via xsd-fu on 2010-04-20 18:27:32+0100
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

public class Experiment extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property
	private ExperimentType type;

	// Property
	private String id;

	// Property
	private String description;

	// Back reference ExperimenterRef
	private List<Experimenter> experimenter = new ArrayList<Experimenter>();

	// Property which occurs more than once
	private List<MicrobeamManipulation> microbeamManipulationList = new ArrayList<MicrobeamManipulation>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// -- Constructors --

	/** Default constructor. */
	public Experiment()
	{
		super();
	}

	/** 
	 * Constructs Experiment recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Experiment(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"Experiment".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Experiment got %s",
					tagName));
		}
		// Model object: None
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(ExperimentType.fromString(
					element.getAttribute("Type")));
		}
		// Model object: None
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// Model object: None
		NodeList Description_nodeList = element.getElementsByTagName("Description");
		if (Description_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.getLength()));
		}
		else if (Description_nodeList.getLength() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			setDescription(Description_nodeList.item(0).getTextContent());
		}
		// Model object: None
		// *** IGNORING *** Skipped back reference ExperimenterRef
		// Model object: None
		// Element property MicrobeamManipulation which is complex (has
		// sub-elements) and occurs more than once
		NodeList MicrobeamManipulation_nodeList = element.getElementsByTagName("MicrobeamManipulation");
		for (int i = 0; i < MicrobeamManipulation_nodeList.getLength(); i++)
		{
			addMicrobeamManipulation(new MicrobeamManipulation(
					(Element) MicrobeamManipulation_nodeList.item(i)));
		}
		// Model object: None
		// *** IGNORING *** Skipped back reference Image_BackReference
	}

	// -- Experiment API methods --

	// Property
	public ExperimentType getType()
	{
		return type;
	}

	public void setType(ExperimentType type)
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

	// Property
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Reference ExperimenterRef
	public int sizeOfLinkedExperimenterList()
	{
		return experimenter.size();
	}

	public List<Experimenter> copyLinkedExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenter);
	}

	public Experimenter getLinkedExperimenter(int index)
	{
		return experimenter.get(index);
	}

	public Experimenter setLinkedExperimenter(int index, Experimenter o)
	{
		return experimenter.set(index, o);
	}

	public void linkExperimenter(Experimenter o)
	{
		this.experimenter.add(o);
	}

	public void unlinkExperimenter(Experimenter o)
	{
		this.experimenter.add(o);
	}

	// Property which occurs more than once
	public int sizeOfMicrobeamManipulationList()
	{
		return microbeamManipulationList.size();
	}

	public List<MicrobeamManipulation> copyMicrobeamManipulationList()
	{
		return new ArrayList<MicrobeamManipulation>(microbeamManipulationList);
	}

	public MicrobeamManipulation getMicrobeamManipulation(int index)
	{
		return microbeamManipulationList.get(index);
	}

	public MicrobeamManipulation setMicrobeamManipulation(int index, MicrobeamManipulation microbeamManipulation)
	{
		return microbeamManipulationList.set(index, microbeamManipulation);
	}

	public void addMicrobeamManipulation(MicrobeamManipulation microbeamManipulation)
	{
		microbeamManipulationList.add(microbeamManipulation);
	}

	public void removeMicrobeamManipulation(MicrobeamManipulation microbeamManipulation)
	{
		microbeamManipulationList.remove(microbeamManipulation);
	}

	// Property which occurs more than once
	public int sizeOfImageList()
	{
		return image_BackReferenceList.size();
	}

	public List<Image> copyImageList()
	{
		return new ArrayList<Image>(image_BackReferenceList);
	}

	public Image getImage(int index)
	{
		return image_BackReferenceList.get(index);
	}

	public Image setImage(int index, Image image_BackReference)
	{
		return image_BackReferenceList.set(index, image_BackReference);
	}

	public void addImage(Image image_BackReference)
	{
		image_BackReferenceList.add(image_BackReference);
	}

	public void removeImage(Image image_BackReference)
	{
		image_BackReferenceList.remove(image_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Experiment
		Element Experiment_element = document.createElement("Experiment");
		if (type != null)
		{
			// Attribute property Type
			Experiment_element.setAttribute("Type", type.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Experiment_element.setAttribute("ID", id.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			Experiment_element.appendChild(description_element);
		}
		if (experimenter != null)
		{
			// *** IGNORING *** Skipped back reference ExperimenterRef
		}
		if (microbeamManipulationList != null)
		{
			// Element property MicrobeamManipulation which is complex (has
			// sub-elements) and occurs more than once
			for (MicrobeamManipulation microbeamManipulationList_value : microbeamManipulationList)
			{
				Experiment_element.appendChild(microbeamManipulationList_value.asXMLElement(document));
			}
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		return Experiment_element;
	}
}
