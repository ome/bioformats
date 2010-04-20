/*
 * ome.xml.r201004.Dataset
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

public class Dataset extends Object
{
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
	private Group group;

	// Property which occurs more than once
	private List<Project> projectList = new ArrayList<Project>();

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// -- Constructors --

	/** Constructs a Dataset. */
	public Dataset()
	{
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

	// Property
	public Experimenter getExperimenter()
	{
		return experimenter;
	}

	public void setExperimenter(Experimenter experimenter)
	{
		this.experimenter = experimenter;
	}

	// Property
	public Group getGroup()
	{
		return group;
	}

	public void setGroup(Group group)
	{
		this.group = group;
	}

	// Property which occurs more than once
	public int sizeOfProjectList()
	{
		return projectList.size();
	}

	public List<Project> copyProjectList()
	{
		return new ArrayList<Project>(projectList);
	}

	public Project getProject(int index)
	{
		return projectList.get(index);
	}

	public Project setProject(int index, Project project)
	{
		return projectList.set(index, project);
	}

	public void addProject(Project project)
	{
		projectList.add(project);
	}

	public void removeProject(Project project)
	{
		projectList.remove(project);
	}

	// Property which occurs more than once
	public int sizeOfAnnotationList()
	{
		return annotationList.size();
	}

	public List<Annotation> copyAnnotationList()
	{
		return new ArrayList<Annotation>(annotationList);
	}

	public Annotation getAnnotation(int index)
	{
		return annotationList.get(index);
	}

	public Annotation setAnnotation(int index, Annotation annotation)
	{
		return annotationList.set(index, annotation);
	}

	public void addAnnotation(Annotation annotation)
	{
		annotationList.add(annotation);
	}

	public void removeAnnotation(Annotation annotation)
	{
		annotationList.remove(annotation);
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
		// Creating XML block for Dataset
		Element Dataset_element = document.createElement("Dataset");
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
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			Dataset_element.appendChild(description_element);
		}
		if (experimenter != null)
		{
			// Element property ExperimenterRef which is complex (has
			// sub-elements)
			Dataset_element.appendChild(experimenter.asXMLElement(document));
		}
		if (group != null)
		{
			// Element property GroupRef which is complex (has
			// sub-elements)
			Dataset_element.appendChild(group.asXMLElement(document));
		}
		if (projectList != null)
		{
			// Element property ProjectRef which is complex (has
			// sub-elements) and occurs more than once
			for (Project projectList_value : projectList)
			{
				Dataset_element.appendChild(projectList_value.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Dataset_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		return Dataset_element;
	}

	public static Dataset fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"Dataset".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Dataset got %s",
					tagName));
		}
		Dataset instance = new Dataset();
		if (element.hasAttribute("Name"))
		{
			// Attribute property Name
			instance.setName(String.valueOf(
					element.getAttribute("Name")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
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
			instance.setDescription(Description_nodeList.item(0).getTextContent());
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
		NodeList GroupRef_nodeList = element.getElementsByTagName("GroupRef");
		if (GroupRef_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"GroupRef node list size %d != 1",
					GroupRef_nodeList.getLength()));
		}
		else if (GroupRef_nodeList.getLength() != 0)
		{
			// Element property GroupRef which is complex (has
			// sub-elements)
			instance.setGroup(Group.fromXMLElement(
					(Element) GroupRef_nodeList.item(0)));
		}
		// Element property ProjectRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList ProjectRef_nodeList = element.getElementsByTagName("ProjectRef");
		for (int i = 0; i < ProjectRef_nodeList.getLength(); i++)
		{
			instance.addProject(Project.fromXMLElement(
					(Element) ProjectRef_nodeList.item(i)));
		}
		// Element property AnnotationRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList AnnotationRef_nodeList = element.getElementsByTagName("AnnotationRef");
		for (int i = 0; i < AnnotationRef_nodeList.getLength(); i++)
		{
			instance.addAnnotation(Annotation.fromXMLElement(
					(Element) AnnotationRef_nodeList.item(i)));
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		return instance;
	}
}
