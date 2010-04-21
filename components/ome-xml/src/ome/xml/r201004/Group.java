/*
 * ome.xml.r201004.Group
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

public class Group extends AbstractOMEModelObject
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

	/** Default constructor. */
	public Group()
	{
		super();
	}

	/** 
	 * Constructs Group recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Group(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"Group".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Group got %s",
					tagName));
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
		NodeList Leader_nodeList = element.getElementsByTagName("Leader");
		if (Leader_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Leader node list size %d != 1",
					Leader_nodeList.getLength()));
		}
		else if (Leader_nodeList.getLength() != 0)
		{
			// Element property Leader which is complex (has
			// sub-elements)
			setLeader(new Leader(
					(Element) Leader_nodeList.item(0)));
		}
		NodeList Contact_nodeList = element.getElementsByTagName("Contact");
		if (Contact_nodeList.getLength() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Contact node list size %d != 1",
					Contact_nodeList.getLength()));
		}
		else if (Contact_nodeList.getLength() != 0)
		{
			// Element property Contact which is complex (has
			// sub-elements)
			setContact(new Contact(
					(Element) Contact_nodeList.item(0)));
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference Project_BackReference
		// *** IGNORING *** Skipped back reference Dataset_BackReference
		// *** IGNORING *** Skipped back reference Experimenter_BackReference
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
	public int sizeOfProjectList()
	{
		return project_BackReferenceList.size();
	}

	public List<Project> copyProjectList()
	{
		return new ArrayList<Project>(project_BackReferenceList);
	}

	public Project getProject(int index)
	{
		return project_BackReferenceList.get(index);
	}

	public Project setProject(int index, Project project_BackReference)
	{
		return project_BackReferenceList.set(index, project_BackReference);
	}

	public void addProject(Project project_BackReference)
	{
		project_BackReferenceList.add(project_BackReference);
	}

	public void removeProject(Project project_BackReference)
	{
		project_BackReferenceList.remove(project_BackReference);
	}

	// Property which occurs more than once
	public int sizeOfDatasetList()
	{
		return dataset_BackReferenceList.size();
	}

	public List<Dataset> copyDatasetList()
	{
		return new ArrayList<Dataset>(dataset_BackReferenceList);
	}

	public Dataset getDataset(int index)
	{
		return dataset_BackReferenceList.get(index);
	}

	public Dataset setDataset(int index, Dataset dataset_BackReference)
	{
		return dataset_BackReferenceList.set(index, dataset_BackReference);
	}

	public void addDataset(Dataset dataset_BackReference)
	{
		dataset_BackReferenceList.add(dataset_BackReference);
	}

	public void removeDataset(Dataset dataset_BackReference)
	{
		dataset_BackReferenceList.remove(dataset_BackReference);
	}

	// Property which occurs more than once
	public int sizeOfExperimenterList()
	{
		return experimenter_BackReferenceList.size();
	}

	public List<Experimenter> copyExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenter_BackReferenceList);
	}

	public Experimenter getExperimenter(int index)
	{
		return experimenter_BackReferenceList.get(index);
	}

	public Experimenter setExperimenter(int index, Experimenter experimenter_BackReference)
	{
		return experimenter_BackReferenceList.set(index, experimenter_BackReference);
	}

	public void addExperimenter(Experimenter experimenter_BackReference)
	{
		experimenter_BackReferenceList.add(experimenter_BackReference);
	}

	public void removeExperimenter(Experimenter experimenter_BackReference)
	{
		experimenter_BackReferenceList.remove(experimenter_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Group_element)
	{
		// Creating XML block for Group
		if (Group_element == null)
		{
			Group_element = document.createElement("Group");
		}
		Group_element = super.asXMLElement(document, Group_element);

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
