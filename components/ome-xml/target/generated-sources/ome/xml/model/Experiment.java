/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.*;
import ome.xml.model.enums.handlers.*;
import ome.xml.model.primitives.*;

public class Experiment extends AbstractOMEModelObject
{
	// Base:  -- Name: Experiment -- Type: Experiment -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Experiment.class);

	// -- Instance variables --

	// Type property
	private ExperimentType type;

	// ID property
	private String id;

	// Description property
	private String description;

	// ExperimenterRef reference
	private Experimenter experimenter;

	// MicrobeamManipulation property (occurs more than once)
	private List<MicrobeamManipulation> microbeamManipulations = new ArrayList<MicrobeamManipulation>();

	// Image_BackReference back reference (occurs more than once)
	private List<Image> images = new ReferenceList<Image>();

	// -- Constructors --

	/** Default constructor. */
	public Experiment()
	{
	}

	/**
	 * Constructs Experiment recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Experiment(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Experiment(Experiment orig)
	{
		type = orig.type;
		id = orig.id;
		description = orig.description;
		experimenter = orig.experimenter;
		microbeamManipulations = orig.microbeamManipulations;
		images = orig.images;
	}

	// -- Custom content from Experiment specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Experiment recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element, OMEModel model)
	    throws EnumerationException
	{
		super.update(element, model);
		String tagName = element.getTagName();
		if (!"Experiment".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Experiment got {}", tagName);
		}
		if (element.hasAttribute("Type"))
		{
			// Attribute property which is an enumeration Type
			setType(ExperimentType.fromString(
					element.getAttribute("Type")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Experiment missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		List<Element> Description_nodeList =
				getChildrenByTagName(element, "Description");
		if (Description_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.size()));
		}
		else if (Description_nodeList.size() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			setDescription(
					String.valueOf(Description_nodeList.get(0).getTextContent()));
		}
		// Element reference ExperimenterRef
		List<Element> ExperimenterRef_nodeList =
				getChildrenByTagName(element, "ExperimenterRef");
		for (Element ExperimenterRef_element : ExperimenterRef_nodeList)
		{
			ExperimenterRef experimenter_reference = new ExperimenterRef();
			experimenter_reference.setID(ExperimenterRef_element.getAttribute("ID"));
			model.addReference(this, experimenter_reference);
		}
		// Element property MicrobeamManipulation which is complex (has
		// sub-elements) and occurs more than once
		List<Element> MicrobeamManipulation_nodeList =
				getChildrenByTagName(element, "MicrobeamManipulation");
		for (Element MicrobeamManipulation_element : MicrobeamManipulation_nodeList)
		{
			addMicrobeamManipulation(
					new MicrobeamManipulation(MicrobeamManipulation_element, model));
		}
	}

	// -- Experiment API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof ExperimenterRef)
		{
			Experimenter o_casted = (Experimenter) o;
			o_casted.linkExperiment(this);
			experimenter = o_casted;
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property Type
	public ExperimentType getType()
	{
		return type;
	}

	public void setType(ExperimentType type)
	{
		this.type = type;
	}

	// Property ID
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property Description
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	// Reference
	public Experimenter getLinkedExperimenter()
	{
		return experimenter;
	}

	public void linkExperimenter(Experimenter o)
	{
		experimenter = o;
	}

	public void unlinkExperimenter(Experimenter o)
	{
		if (experimenter == o)
		{
			experimenter = null;
		}
	}

	// Property which occurs more than once
	public int sizeOfMicrobeamManipulationList()
	{
		return microbeamManipulations.size();
	}

	public List<MicrobeamManipulation> copyMicrobeamManipulationList()
	{
		return new ArrayList<MicrobeamManipulation>(microbeamManipulations);
	}

	public MicrobeamManipulation getMicrobeamManipulation(int index)
	{
		return microbeamManipulations.get(index);
	}

	public MicrobeamManipulation setMicrobeamManipulation(int index, MicrobeamManipulation microbeamManipulation)
	{
        microbeamManipulation.setExperiment(this);
		return microbeamManipulations.set(index, microbeamManipulation);
	}

	public void addMicrobeamManipulation(MicrobeamManipulation microbeamManipulation)
	{
        microbeamManipulation.setExperiment(this);
		microbeamManipulations.add(microbeamManipulation);
	}

	public void removeMicrobeamManipulation(MicrobeamManipulation microbeamManipulation)
	{
		microbeamManipulations.remove(microbeamManipulation);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedImageList()
	{
		return images.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(images);
	}

	public Image getLinkedImage(int index)
	{
		return images.get(index);
	}

	public Image setLinkedImage(int index, Image o)
	{
		return images.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		return images.add(o);
	}

	public boolean unlinkImage(Image o)
	{
		return images.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Experiment_element)
	{
		// Creating XML block for Experiment

		if (Experiment_element == null)
		{
			Experiment_element =
					document.createElementNS(NAMESPACE, "Experiment");
		}


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
			Element description_element =
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			Experiment_element.appendChild(description_element);
		}
		if (experimenter != null)
		{
			// Reference property ExperimenterRef
			ExperimenterRef o = new ExperimenterRef();
			o.setID(experimenter.getID());
			Experiment_element.appendChild(o.asXMLElement(document));
		}
		if (microbeamManipulations != null)
		{
			// Element property MicrobeamManipulation which is complex (has
			// sub-elements) and occurs more than once
			for (MicrobeamManipulation microbeamManipulations_value : microbeamManipulations)
			{
				Experiment_element.appendChild(microbeamManipulations_value.asXMLElement(document));
			}
		}
		if (images != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}

		return super.asXMLElement(document, Experiment_element);
	}
}
