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

public class ExperimenterGroup extends AbstractOMEModelObject
{
	// Base:  -- Name: ExperimenterGroup -- Type: ExperimenterGroup -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(ExperimenterGroup.class);

	// -- Instance variables --

	// Name property
	private String name;

	// ID property
	private String id;

	// Description property
	private String description;

	// ExperimenterRef reference (occurs more than once)
	private List<Experimenter> experimenterLinks = new ReferenceList<Experimenter>();

	// Leader reference (occurs more than once)
	private List<Experimenter> leaders = new ReferenceList<Experimenter>();

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Image_BackReference back reference (occurs more than once)
	private List<Image> images = new ReferenceList<Image>();

	// Project_BackReference back reference (occurs more than once)
	private List<Project> projects = new ReferenceList<Project>();

	// Dataset_BackReference back reference (occurs more than once)
	private List<Dataset> datasets = new ReferenceList<Dataset>();

	// -- Constructors --

	/** Default constructor. */
	public ExperimenterGroup()
	{
	}

	/**
	 * Constructs ExperimenterGroup recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ExperimenterGroup(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	/** Copy constructor. */
	public ExperimenterGroup(ExperimenterGroup orig)
	{
		name = orig.name;
		id = orig.id;
		description = orig.description;
		experimenterLinks = orig.experimenterLinks;
		leaders = orig.leaders;
		annotationLinks = orig.annotationLinks;
		images = orig.images;
		projects = orig.projects;
		datasets = orig.datasets;
	}

	// -- Custom content from ExperimenterGroup specific template --


	// -- OMEModelObject API methods --

	/**
	 * Updates ExperimenterGroup recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"ExperimenterGroup".equals(tagName))
		{
			LOGGER.debug("Expecting node name of ExperimenterGroup got {}", tagName);
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
					"ExperimenterGroup missing required ID property."));
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
			ExperimenterRef experimenterLinks_reference = new ExperimenterRef();
			experimenterLinks_reference.setID(ExperimenterRef_element.getAttribute("ID"));
			model.addReference(this, experimenterLinks_reference);
		}
		// Element reference Leader
		List<Element> Leader_nodeList =
				getChildrenByTagName(element, "Leader");
		for (Element Leader_element : Leader_nodeList)
		{
			Leader leaders_reference = new Leader();
			leaders_reference.setID(Leader_element.getAttribute("ID"));
			model.addReference(this, leaders_reference);
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

	// -- ExperimenterGroup API methods --

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
			o_casted.linkExperimenterGroup(this);
			experimenterLinks.add(o_casted);
			return true;
		}
		if (reference instanceof Leader)
		{
			Experimenter o_casted = (Experimenter) o;
			o_casted.linkExperimenterGroup(this);
			leaders.add(o_casted);
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkExperimenterGroup(this);
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

	// Reference which occurs more than once
	public int sizeOfLinkedExperimenterList()
	{
		return experimenterLinks.size();
	}

	public List<Experimenter> copyLinkedExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenterLinks);
	}

	public Experimenter getLinkedExperimenter(int index)
	{
		return experimenterLinks.get(index);
	}

	public Experimenter setLinkedExperimenter(int index, Experimenter o)
	{
		return experimenterLinks.set(index, o);
	}

	public boolean linkExperimenter(Experimenter o)
	{

			o.linkExperimenterGroup(this);
		return experimenterLinks.add(o);
	}

	public boolean unlinkExperimenter(Experimenter o)
	{

			o.unlinkExperimenterGroup(this);
		return experimenterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedLeaderList()
	{
		return leaders.size();
	}

	public List<Experimenter> copyLinkedLeaderList()
	{
		return new ArrayList<Experimenter>(leaders);
	}

	public Experimenter getLinkedLeader(int index)
	{
		return leaders.get(index);
	}

	public Experimenter setLinkedLeader(int index, Experimenter o)
	{
		return leaders.set(index, o);
	}

	public boolean linkLeader(Experimenter o)
	{

			o.linkExperimenterGroup(this);
		return leaders.add(o);
	}

	public boolean unlinkLeader(Experimenter o)
	{

			o.unlinkExperimenterGroup(this);
		return leaders.remove(o);
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

			o.linkExperimenterGroup(this);
		return annotationLinks.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkExperimenterGroup(this);
		return annotationLinks.remove(o);
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

	// Reference which occurs more than once
	public int sizeOfLinkedProjectList()
	{
		return projects.size();
	}

	public List<Project> copyLinkedProjectList()
	{
		return new ArrayList<Project>(projects);
	}

	public Project getLinkedProject(int index)
	{
		return projects.get(index);
	}

	public Project setLinkedProject(int index, Project o)
	{
		return projects.set(index, o);
	}

	public boolean linkProject(Project o)
	{
		return projects.add(o);
	}

	public boolean unlinkProject(Project o)
	{
		return projects.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedDatasetList()
	{
		return datasets.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(datasets);
	}

	public Dataset getLinkedDataset(int index)
	{
		return datasets.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset o)
	{
		return datasets.set(index, o);
	}

	public boolean linkDataset(Dataset o)
	{
		return datasets.add(o);
	}

	public boolean unlinkDataset(Dataset o)
	{
		return datasets.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element ExperimenterGroup_element)
	{
		// Creating XML block for ExperimenterGroup

		if (ExperimenterGroup_element == null)
		{
			ExperimenterGroup_element =
					document.createElementNS(NAMESPACE, "ExperimenterGroup");
		}


		if (name != null)
		{
			// Attribute property Name
			ExperimenterGroup_element.setAttribute("Name", name.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			ExperimenterGroup_element.setAttribute("ID", id.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element =
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			ExperimenterGroup_element.appendChild(description_element);
		}
		if (experimenterLinks != null)
		{
			// Reference property ExperimenterRef which occurs more than once
			for (Experimenter experimenterLinks_value : experimenterLinks)
			{
				ExperimenterRef o = new ExperimenterRef();
				o.setID(experimenterLinks_value.getID());
				ExperimenterGroup_element.appendChild(o.asXMLElement(document));
			}
		}
		if (leaders != null)
		{
			// Reference property Leader which occurs more than once
			for (Experimenter leaders_value : leaders)
			{
				Leader o = new Leader();
				o.setID(leaders_value.getID());
				ExperimenterGroup_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				ExperimenterGroup_element.appendChild(o.asXMLElement(document));
			}
		}
		if (images != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (projects != null)
		{
			// *** IGNORING *** Skipped back reference Project_BackReference
		}
		if (datasets != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}

		return super.asXMLElement(document, ExperimenterGroup_element);
	}
}
