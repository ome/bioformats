/*
 * ome.xml.r201004.Experiment
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
 * Created by callan via xsd-fu on 2010-04-19 19:23:58+0100
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r201004;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ome.xml.r201004.enums.*;

public class Experiment extends Object
{
	// -- Instance variables --

	// Property
	private ExperimentType type;

	// Property
	private String id;

	// Property
	private String description;

	// Property
	private Experimenter experimenter;

	// Property which occurs more than once
	private List<MicrobeamManipulation> microbeamManipulationList = new ArrayList<MicrobeamManipulation>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// -- Constructors --

	/** Constructs a Experiment. */
	public Experiment()
	{
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
			// Element property ExperimenterRef which is complex (has
			// sub-elements)
			Experiment_element.appendChild(experimenter.asXMLElement(document));
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
