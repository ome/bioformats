/*
 * ome.xml.r201004.Project
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

public class Project extends Object
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
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference Dataset_BackReference
	private List<Dataset> dataset_BackReferenceList = new ArrayList<Dataset>();

	// -- Constructors --

	/** Constructs a Project. */
	public Project()
	{
	}

	// -- Project API methods --

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

	// Back reference Dataset_BackReference
	public int sizeOfLinkedDatasetList()
	{
		return dataset_BackReferenceList.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(dataset_BackReferenceList);
	}

	public Dataset getLinkedDataset(int index)
	{
		return dataset_BackReferenceList.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset dataset_BackReference)
	{
		return dataset_BackReferenceList.set(index, dataset_BackReference);
	}

	public void linkDataset(Dataset dataset_BackReference)
	{
		this.dataset_BackReferenceList.add(dataset_BackReference);
	}

	public void unlinkDataset(Dataset dataset_BackReference)
	{
		this.dataset_BackReferenceList.add(dataset_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Project
		Element Project_element = document.createElement("Project");
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
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			Project_element.appendChild(description_element);
		}
		if (experimenter != null)
		{
			// Element property ExperimenterRef which is complex (has
			// sub-elements)
			Project_element.appendChild(experimenter.asXMLElement(document));
		}
		if (group != null)
		{
			// Element property GroupRef which is complex (has
			// sub-elements)
			Project_element.appendChild(group.asXMLElement(document));
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Project_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (dataset_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}
		return Project_element;
	}
}
