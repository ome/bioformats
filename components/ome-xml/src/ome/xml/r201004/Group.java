/*
 * ome.xml.r201004.Group
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

public class Group extends Object
{
	// -- Instance variables --

	// Property
	private String name;

	// Property
	private String id;

	// Property
	private String description;

	// Property
	private Leader leader;

	// Property
	private Contact contact;

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// Back reference Project_BackReference
	private List<Project> project_BackReferenceList = new ArrayList<Project>();

	// Back reference Dataset_BackReference
	private List<Dataset> dataset_BackReferenceList = new ArrayList<Dataset>();

	// Back reference Experimenter_BackReference
	private List<Experimenter> experimenter_BackReferenceList = new ArrayList<Experimenter>();

	// -- Constructors --

	/** Constructs a Group. */
	public Group()
	{
	}

	// -- Group API methods --

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
	public Leader getLeader()
	{
		return leader;
	}

	public void setLeader(Leader leader)
	{
		this.leader = leader;
	}

	// Property
	public Contact getContact()
	{
		return contact;
	}

	public void setContact(Contact contact)
	{
		this.contact = contact;
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

	// Back reference Project_BackReference
	public int sizeOfLinkedProjectList()
	{
		return project_BackReferenceList.size();
	}

	public List<Project> copyLinkedProjectList()
	{
		return new ArrayList<Project>(project_BackReferenceList);
	}

	public Project getLinkedProject(int index)
	{
		return project_BackReferenceList.get(index);
	}

	public Project setLinkedProject(int index, Project project_BackReference)
	{
		return project_BackReferenceList.set(index, project_BackReference);
	}

	public void linkProject(Project project_BackReference)
	{
		this.project_BackReferenceList.add(project_BackReference);
	}

	public void unlinkProject(Project project_BackReference)
	{
		this.project_BackReferenceList.add(project_BackReference);
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

	// Back reference Experimenter_BackReference
	public int sizeOfLinkedExperimenterList()
	{
		return experimenter_BackReferenceList.size();
	}

	public List<Experimenter> copyLinkedExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenter_BackReferenceList);
	}

	public Experimenter getLinkedExperimenter(int index)
	{
		return experimenter_BackReferenceList.get(index);
	}

	public Experimenter setLinkedExperimenter(int index, Experimenter experimenter_BackReference)
	{
		return experimenter_BackReferenceList.set(index, experimenter_BackReference);
	}

	public void linkExperimenter(Experimenter experimenter_BackReference)
	{
		this.experimenter_BackReferenceList.add(experimenter_BackReference);
	}

	public void unlinkExperimenter(Experimenter experimenter_BackReference)
	{
		this.experimenter_BackReferenceList.add(experimenter_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Group
		Element Group_element = document.createElement("Group");
		if (name != null)
		{
			// Attribute property Name
			Group_element.setAttribute("Name", name.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Group_element.setAttribute("ID", id.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = document.createElement("Description");
			description_element.setTextContent(description);
			Group_element.appendChild(description_element);
		}
		if (leader != null)
		{
			// Element property Leader which is complex (has
			// sub-elements)
			Group_element.appendChild(leader.asXMLElement(document));
		}
		if (contact != null)
		{
			// Element property Contact which is complex (has
			// sub-elements)
			Group_element.appendChild(contact.asXMLElement(document));
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (project_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Project_BackReference
		}
		if (dataset_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}
		if (experimenter_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Experimenter_BackReference
		}
		return Group_element;
	}
}
