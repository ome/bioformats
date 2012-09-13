/*
 * ome.xml.model.Dataset
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
 * Created by melissa via xsd-fu on 2012-09-10 13:40:21-0400
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

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

public class Dataset extends AbstractOMEModelObject
{
	// Base:  -- Name: Dataset -- Type: Dataset -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Dataset.class);

	// -- Instance variables --


	// Property
	private String name;

	// Property
	private String id;

	// Property
	private String description;

	// Property
	private Experimenter experimenter;

	// Property
	private ExperimenterGroup experimenterGroup;

	// Reference ImageRef
	private List<Image> imageLinks = new ArrayList<Image>();

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Back reference Project_BackReference
	private List<Project> projectLinks = new ArrayList<Project>();

	// -- Constructors --

	/** Default constructor. */
	public Dataset()
	{
		super();
	}

	/** 
	 * Constructs Dataset recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Dataset(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Dataset specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Dataset recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Dataset".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Dataset got {}", tagName);
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
					"Dataset missing required ID property."));
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
		// Element reference ImageRef
		List<Element> ImageRef_nodeList =
				getChildrenByTagName(element, "ImageRef");
		for (Element ImageRef_element : ImageRef_nodeList)
		{
			ImageRef imageLinks_reference = new ImageRef();
			imageLinks_reference.setID(ImageRef_element.getAttribute("ID"));
			model.addReference(this, imageLinks_reference);
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
		// *** IGNORING *** Skipped back reference Project_BackReference
	}

	// -- Dataset API methods --

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
			o_casted.linkDataset(this);
			experimenter = o_casted;
			return true;
		}
		if (reference instanceof ExperimenterGroupRef)
		{
			ExperimenterGroup o_casted = (ExperimenterGroup) o;
			o_casted.linkDataset(this);
			experimenterGroup = o_casted;
			return true;
		}
		if (reference instanceof ImageRef)
		{
			Image o_casted = (Image) o;
			o_casted.linkDataset(this);
			if (!imageLinks.contains(o_casted)) {
				imageLinks.add(o_casted);
			}
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkDataset(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
	public int sizeOfLinkedImageList()
	{
		return imageLinks.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(imageLinks);
	}

	public Image getLinkedImage(int index)
	{
		return imageLinks.get(index);
	}

	public Image setLinkedImage(int index, Image o)
	{
		return imageLinks.set(index, o);
	}

	public boolean linkImage(Image o)
	{

			o.linkDataset(this);
		if (!imageLinks.contains(o)) {
			return imageLinks.add(o);
		}
		return false;
	}

	public boolean unlinkImage(Image o)
	{

			o.unlinkDataset(this);
		return imageLinks.remove(o);
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

			o.linkDataset(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkDataset(this);
		return annotationLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedProjectList()
	{
		return projectLinks.size();
	}

	public List<Project> copyLinkedProjectList()
	{
		return new ArrayList<Project>(projectLinks);
	}

	public Project getLinkedProject(int index)
	{
		return projectLinks.get(index);
	}

	public Project setLinkedProject(int index, Project o)
	{
		return projectLinks.set(index, o);
	}

	public boolean linkProject(Project o)
	{
		if (!projectLinks.contains(o)) {
			return projectLinks.add(o);
		}
		return false;
	}

	public boolean unlinkProject(Project o)
	{
		return projectLinks.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Dataset_element)
	{
		// Creating XML block for Dataset

		if (Dataset_element == null)
		{
			Dataset_element =
					document.createElementNS(NAMESPACE, "Dataset");
		}

		if (name != null)
		{
			// Attribute property Name
			Dataset_element.setAttribute("Name", name.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Dataset_element.setAttribute("ID", id.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			Dataset_element.appendChild(description_element);
		}
		if (experimenter != null)
		{
			// Reference property ExperimenterRef
			ExperimenterRef o = new ExperimenterRef();
			o.setID(experimenter.getID());
			Dataset_element.appendChild(o.asXMLElement(document));
		}
		if (experimenterGroup != null)
		{
			// Reference property ExperimenterGroupRef
			ExperimenterGroupRef o = new ExperimenterGroupRef();
			o.setID(experimenterGroup.getID());
			Dataset_element.appendChild(o.asXMLElement(document));
		}
		if (imageLinks != null)
		{
			// Reference property ImageRef which occurs more than once
			for (Image imageLinks_value : imageLinks)
			{
				ImageRef o = new ImageRef();
				o.setID(imageLinks_value.getID());
				Dataset_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Dataset_element.appendChild(o.asXMLElement(document));
			}
		}
		if (projectLinks != null)
		{
			// *** IGNORING *** Skipped back reference Project_BackReference
		}
		return super.asXMLElement(document, Dataset_element);
	}
}
