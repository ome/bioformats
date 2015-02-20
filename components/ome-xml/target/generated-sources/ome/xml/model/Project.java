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

public class Project extends AbstractOMEModelObject
{
	// Base:  -- Name: Project -- Type: Project -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Project.class);

	// -- Instance variables --

	// Name property
	private String name;

	// ID property
	private String id;

	// Description property
	private String description;

	// ExperimenterRef reference
	private Experimenter experimenter;

	// ExperimenterGroupRef reference
	private ExperimenterGroup experimenterGroup;

	// DatasetRef reference (occurs more than once)
	private List<Dataset> datasetLinks = new ReferenceList<Dataset>();

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// -- Constructors --

	/** Default constructor. */
	public Project()
	{
	}

	/**
	 * Constructs Project recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Project(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public Project(Project orig)
	{
		name = orig.name;
		id = orig.id;
		description = orig.description;
		experimenter = orig.experimenter;
		experimenterGroup = orig.experimenterGroup;
		datasetLinks = orig.datasetLinks;
		annotationLinks = orig.annotationLinks;
	}

	// -- Custom content from Project specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates Project recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Project".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Project got {}", tagName);
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Project missing required ID property."));
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
		// Element reference ExperimenterGroupRef
		List<Element> ExperimenterGroupRef_nodeList =
				getChildrenByTagName(element, "ExperimenterGroupRef");
		for (Element ExperimenterGroupRef_element : ExperimenterGroupRef_nodeList)
		{
			ExperimenterGroupRef experimenterGroup_reference = new ExperimenterGroupRef();
			experimenterGroup_reference.setID(ExperimenterGroupRef_element.getAttribute("ID"));
			model.addReference(this, experimenterGroup_reference);
		}
		// Element reference DatasetRef
		List<Element> DatasetRef_nodeList =
				getChildrenByTagName(element, "DatasetRef");
		for (Element DatasetRef_element : DatasetRef_nodeList)
		{
			DatasetRef datasetLinks_reference = new DatasetRef();
			datasetLinks_reference.setID(DatasetRef_element.getAttribute("ID"));
			model.addReference(this, datasetLinks_reference);
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationLinks_reference = new AnnotationRef();
			annotationLinks_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationLinks_reference);
		}
	}

	// -- Project API methods --

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
			o_casted.linkProject(this);
			experimenter = o_casted;
			return true;
		}
		if (reference instanceof ExperimenterGroupRef)
		{
			ExperimenterGroup o_casted = (ExperimenterGroup) o;
			o_casted.linkProject(this);
			experimenterGroup = o_casted;
			return true;
		}
		if (reference instanceof DatasetRef)
		{
			Dataset o_casted = (Dataset) o;
			o_casted.linkProject(this);
			datasetLinks.add(o_casted);
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkProject(this);
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property Name
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	// Reference
	public ExperimenterGroup getLinkedExperimenterGroup()
	{
		return experimenterGroup;
	}

	public void linkExperimenterGroup(ExperimenterGroup o)
	{
		experimenterGroup = o;
	}

	public void unlinkExperimenterGroup(ExperimenterGroup o)
	{
		if (experimenterGroup == o)
		{
			experimenterGroup = null;
		}
	}

	// Reference which occurs more than once
	public int sizeOfLinkedDatasetList()
	{
		return datasetLinks.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(datasetLinks);
	}

	public Dataset getLinkedDataset(int index)
	{
		return datasetLinks.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset o)
	{
		return datasetLinks.set(index, o);
	}

	public boolean linkDataset(Dataset o)
	{

			o.linkProject(this);
		return datasetLinks.add(o);
	}

	public boolean unlinkDataset(Dataset o)
	{

			o.unlinkProject(this);
		return datasetLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationLinks.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationLinks);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationLinks.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationLinks.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{

			o.linkProject(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkProject(this);
		return annotationLinks.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Project_element)
	{
		// Creating XML block for Project

		if (Project_element == null)
		{
			Project_element =
					document.createElementNS(NAMESPACE, "Project");
		}


		if (name != null)
		{
			// Attribute property Name
			Project_element.setAttribute("Name", name.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Project_element.setAttribute("ID", id.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element =
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			Project_element.appendChild(description_element);
		}
		if (experimenter != null)
		{
			// Reference property ExperimenterRef
			ExperimenterRef o = new ExperimenterRef();
			o.setID(experimenter.getID());
			Project_element.appendChild(o.asXMLElement(document));
		}
		if (experimenterGroup != null)
		{
			// Reference property ExperimenterGroupRef
			ExperimenterGroupRef o = new ExperimenterGroupRef();
			o.setID(experimenterGroup.getID());
			Project_element.appendChild(o.asXMLElement(document));
		}
		if (datasetLinks != null)
		{
			// Reference property DatasetRef which occurs more than once
			for (Dataset datasetLinks_value : datasetLinks)
			{
				DatasetRef o = new DatasetRef();
				o.setID(datasetLinks_value.getID());
				Project_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Project_element.appendChild(o.asXMLElement(document));
			}
		}

		return super.asXMLElement(document, Project_element);
	}
}
