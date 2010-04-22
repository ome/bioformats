/*
 * ome.xml.r201004.Dataset
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
 * Created by callan via xsd-fu on 2010-04-22 12:27:38+0100
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

public class Dataset extends AbstractOMEModelObject
{
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

	// -- Instance variables --

	// Property
	private String name;

	// Property
	private String id;

	// Property
	private String description;

	// Back reference ExperimenterRef
	private List<Experimenter> experimenter = new ArrayList<Experimenter>();

	// Back reference GroupRef
	private List<Group> group = new ArrayList<Group>();

	// Back reference ProjectRef
	private List<Project> projectList = new ArrayList<Project>();

	// Back reference AnnotationRef
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

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
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Dataset(Element element) throws EnumerationException
	{
		update(element);
	}

	/** 
	 * Updates Dataset recursively from an XML DOM tree. <b>NOTE:</b> No
	 * properties are removed, only added or updated.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public void update(Element element) throws EnumerationException
	{	
		super.update(element);
		String tagName = element.getTagName();
		if (!"Dataset".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Dataset got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Dataset got %s",
			//		tagName));
		}
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			setName(String.valueOf(
					element.getAttribute("Name")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
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
		// *** IGNORING *** Skipped back reference ExperimenterRef
		// *** IGNORING *** Skipped back reference GroupRef
		// *** IGNORING *** Skipped back reference ProjectRef
		// *** IGNORING *** Skipped back reference AnnotationRef
		// *** IGNORING *** Skipped back reference Image_BackReference
	}

	// -- Dataset API methods --

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

	// Reference GroupRef
	public int sizeOfLinkedGroupList()
	{
		return group.size();
	}

	public List<Group> copyLinkedGroupList()
	{
		return new ArrayList<Group>(group);
	}

	public Group getLinkedGroup(int index)
	{
		return group.get(index);
	}

	public Group setLinkedGroup(int index, Group o)
	{
		return group.set(index, o);
	}

	public void linkGroup(Group o)
	{
		this.group.add(o);
	}

	public void unlinkGroup(Group o)
	{
		this.group.add(o);
	}

	// Reference ProjectRef
	public int sizeOfLinkedProjectList()
	{
		return projectList.size();
	}

	public List<Project> copyLinkedProjectList()
	{
		return new ArrayList<Project>(projectList);
	}

	public Project getLinkedProject(int index)
	{
		return projectList.get(index);
	}

	public Project setLinkedProject(int index, Project o)
	{
		return projectList.set(index, o);
	}

	public void linkProject(Project o)
	{
		this.projectList.add(o);
	}

	public void unlinkProject(Project o)
	{
		this.projectList.add(o);
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
			description_element.setTextContent(description);
			Dataset_element.appendChild(description_element);
		}
		if (experimenter != null)
		{
			// *** IGNORING *** Skipped back reference ExperimenterRef
		}
		if (group != null)
		{
			// *** IGNORING *** Skipped back reference GroupRef
		}
		if (projectList != null)
		{
			// *** IGNORING *** Skipped back reference ProjectRef
		}
		if (annotationList != null)
		{
			// *** IGNORING *** Skipped back reference AnnotationRef
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		return super.asXMLElement(document, Dataset_element);
	}
}
