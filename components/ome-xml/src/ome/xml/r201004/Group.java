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
 * Created by callan via xsd-fu on 2010-04-22 17:27:24+0100
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
	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2010-04";

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
		update(element);
	}

	/** 
	 * Updates Group recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Group".equals(tagName))
		{
			System.err.println(String.format(
					"WARNING: Expecting node name of Group got %s",
					tagName));
			// TODO: Should be its own Exception
			//throw new RuntimeException(String.format(
			//		"Expecting node name of Group got %s",
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
		// *** IGNORING *** Skipped back reference Leader
		// *** IGNORING *** Skipped back reference Contact
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

	// Reference
	public Leader getLinkedLeader()
	{
		return leader;
	}

	public void linkLeader(Leader o)
	{
		leader = o;
	}

	public void unlinkLeader(Leader o)
	{
		if (leader == o)
		{
			leader = null;
		}
	}

	// Reference
	public Contact getLinkedContact()
	{
		return contact;
	}

	public void linkContact(Contact o)
	{
		contact = o;
	}

	public void unlinkContact(Contact o)
	{
		if (contact == o)
		{
			contact = null;
		}
	}

	// Reference which occurs more than once
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

	public Image setLinkedImage(int index, Image o)
	{
		return image_BackReferenceList.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		return image_BackReferenceList.add(o);
	}

	public boolean unlinkImage(Image o)
	{
		return image_BackReferenceList.remove(o);
	}

	// Reference which occurs more than once
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

	public Project setLinkedProject(int index, Project o)
	{
		return project_BackReferenceList.set(index, o);
	}

	public boolean linkProject(Project o)
	{
		return project_BackReferenceList.add(o);
	}

	public boolean unlinkProject(Project o)
	{
		return project_BackReferenceList.remove(o);
	}

	// Reference which occurs more than once
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

	public Dataset setLinkedDataset(int index, Dataset o)
	{
		return dataset_BackReferenceList.set(index, o);
	}

	public boolean linkDataset(Dataset o)
	{
		return dataset_BackReferenceList.add(o);
	}

	public boolean unlinkDataset(Dataset o)
	{
		return dataset_BackReferenceList.remove(o);
	}

	// Reference which occurs more than once
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

	public Experimenter setLinkedExperimenter(int index, Experimenter o)
	{
		return experimenter_BackReferenceList.set(index, o);
	}

	public boolean linkExperimenter(Experimenter o)
	{
		return experimenter_BackReferenceList.add(o);
	}

	public boolean unlinkExperimenter(Experimenter o)
	{
		return experimenter_BackReferenceList.remove(o);
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
			Group_element =
					document.createElementNS(NAMESPACE, "Group");
		}

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
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description);
			Group_element.appendChild(description_element);
		}
		if (leader != null)
		{
			// Reference property Leader
			Element leader_element = 
					document.createElementNS(NAMESPACE, "Leader");
			leader_element.setAttribute(
					"ID", leader.getID());
			Group_element.appendChild(leader_element);
		}
		if (contact != null)
		{
			// Reference property Contact
			Element contact_element = 
					document.createElementNS(NAMESPACE, "Contact");
			contact_element.setAttribute(
					"ID", contact.getID());
			Group_element.appendChild(contact_element);
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
		return super.asXMLElement(document, Group_element);
	}
}
