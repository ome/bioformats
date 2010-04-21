/*
 * ome.xml.r201004.ROI
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
 * Created by callan via xsd-fu on 2010-04-21 11:45:19+0100
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

public class ROI extends AbstractOMEModelObject
{
	// -- Instance variables --

	// Property
	private String namespace;

	// Property
	private String id;

	// Property
	private String name;

	// Property
	private Union union;

	// Back reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Property
	private String description;

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// Back reference MicrobeamManipulation_BackReference
	private List<MicrobeamManipulation> microbeamManipulation_BackReferenceList = new ArrayList<MicrobeamManipulation>();

	// -- Constructors --

	/** Default constructor. */
	public ROI()
	{
		super();
	}

	/** 
	 * Constructs ROI recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public ROI(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"ROI".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of ROI got %s",
					tagName));
		}
		if (element.hasAttribute("Namespace"))
		{
			// Attribute property Namespace
			setNamespace(String.valueOf(
					element.getAttribute("Namespace")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		NodeList Union_nodeList = element.getElementsByTagName("Union");
		if (Union_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Union node list size %d != 1",
					Union_nodeList.getLength()));
		}
		else if (Union_nodeList.getLength() != 0)
		{
			// Element property Union which is complex (has
			// sub-elements)
			setUnion(new Union(
					(Element) Union_nodeList.item(0)));
		}
		// *** IGNORING *** Skipped back reference AnnotationRef
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
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
	}

	// -- ROI API methods --

	// Property
	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
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
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// Property
	public Union getUnion()
	{
		return union;
	}

	public void setUnion(Union union)
	{
		this.union = union;
	}

	// Reference AnnotationRef
	public int sizeOfLinkedAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationList.set(index, o);
	}

	public void linkAnnotation(Annotation o)
	{
		this.annotationList.add(o);
	}

	public void unlinkAnnotation(Annotation o)
	{
		this.annotationList.add(o);
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

	// Property which occurs more than once
	public int sizeOfMicrobeamManipulationList()
	{
		return microbeamManipulation_BackReferenceList.size();
	}

	public List<MicrobeamManipulation> copyMicrobeamManipulationList()
	{
		return new ArrayList<MicrobeamManipulation>(microbeamManipulation_BackReferenceList);
	}

	public MicrobeamManipulation getMicrobeamManipulation(int index)
	{
		return microbeamManipulation_BackReferenceList.get(index);
	}

	public MicrobeamManipulation setMicrobeamManipulation(int index, MicrobeamManipulation microbeamManipulation_BackReference)
	{
		return microbeamManipulation_BackReferenceList.set(index, microbeamManipulation_BackReference);
	}

	public void addMicrobeamManipulation(MicrobeamManipulation microbeamManipulation_BackReference)
	{
		microbeamManipulation_BackReferenceList.add(microbeamManipulation_BackReference);
	}

	public void removeMicrobeamManipulation(MicrobeamManipulation microbeamManipulation_BackReference)
	{
		microbeamManipulation_BackReferenceList.remove(microbeamManipulation_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element ROI_element)
	{
		// Creating XML block for ROI
		if (ROI_element == null)
		{
			ROI_element = document.createElement("ROI");
		}
		ROI_element = super.asXMLElement(document, ROI_element);

		if (namespace != null)
		{
			// Attribute property Namespace
			ROI_element.setAttribute("Namespace", namespace.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			ROI_element.setAttribute("ID", id.toString());
		}
		if (name != null)
		{
			// Attribute property Name
			ROI_element.setAttribute("Name", name.toString());
		}
		if (union != null)
		{
			// Element property Union which is complex (has
			// sub-elements)
			ROI_element.appendChild(union.asXMLElement(document));
		}
		if (annotationList != null)
		{
			// *** IGNORING *** Skipped back reference AnnotationRef
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			ROI_element.appendChild(description_element);
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (microbeamManipulation_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		}
		return ROI_element;
	}
}
