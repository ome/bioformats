/*
 * ome.xml.r201004.Experimenter
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

public class Experimenter extends Object
{
	// -- Instance variables --

	// Property
	private String userName;

	// Property
	private String displayName;

	// Property
	private String firstName;

	// Property
	private String middleName;

	// Property
	private String lastName;

	// Property
	private String email;

	// Property
	private String institution;

	// Property
	private String id;

	// Property which occurs more than once
	private List<Group> groupList = new ArrayList<Group>();

	// Property which occurs more than once
	private List<Annotation> annotationList = new ArrayList<Annotation>();

	// Back reference Image_BackReference
	private List<Image> image_BackReferenceList = new ArrayList<Image>();

	// Back reference MicrobeamManipulation_BackReference
	private List<MicrobeamManipulation> microbeamManipulation_BackReferenceList = new ArrayList<MicrobeamManipulation>();

	// Back reference Project_BackReference
	private List<Project> project_BackReferenceList = new ArrayList<Project>();

	// Back reference Dataset_BackReference
	private List<Dataset> dataset_BackReferenceList = new ArrayList<Dataset>();

	// Back reference Experiment_BackReference
	private List<Experiment> experiment_BackReferenceList = new ArrayList<Experiment>();

	// -- Constructors --

	/** Constructs a Experimenter. */
	public Experimenter()
	{
	}

	// -- Experimenter API methods --

	// Property
	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	// Property
	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	// Property
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	// Property
	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	// Property
	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	// Property
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	// Property
	public String getInstitution()
	{
		return institution;
	}

	public void setInstitution(String institution)
	{
		this.institution = institution;
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

	// Property which occurs more than once
	public int sizeOfGroupList()
	{
		return groupList.size();
	}

	public List<Group> copyGroupList()
	{
		return new ArrayList<Group>(groupList);
	}

	public Group getGroup(int index)
	{
		return groupList.get(index);
	}

	public Group setGroup(int index, Group group)
	{
		return groupList.set(index, group);
	}

	public void addGroup(Group group)
	{
		groupList.add(group);
	}

	public void removeGroup(Group group)
	{
		groupList.remove(group);
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

	// Back reference MicrobeamManipulation_BackReference
	public int sizeOfLinkedMicrobeamManipulationList()
	{
		return microbeamManipulation_BackReferenceList.size();
	}

	public List<MicrobeamManipulation> copyLinkedMicrobeamManipulationList()
	{
		return new ArrayList<MicrobeamManipulation>(microbeamManipulation_BackReferenceList);
	}

	public MicrobeamManipulation getLinkedMicrobeamManipulation(int index)
	{
		return microbeamManipulation_BackReferenceList.get(index);
	}

	public MicrobeamManipulation setLinkedMicrobeamManipulation(int index, MicrobeamManipulation microbeamManipulation_BackReference)
	{
		return microbeamManipulation_BackReferenceList.set(index, microbeamManipulation_BackReference);
	}

	public void linkMicrobeamManipulation(MicrobeamManipulation microbeamManipulation_BackReference)
	{
		this.microbeamManipulation_BackReferenceList.add(microbeamManipulation_BackReference);
	}

	public void unlinkMicrobeamManipulation(MicrobeamManipulation microbeamManipulation_BackReference)
	{
		this.microbeamManipulation_BackReferenceList.add(microbeamManipulation_BackReference);
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

	// Back reference Experiment_BackReference
	public int sizeOfLinkedExperimentList()
	{
		return experiment_BackReferenceList.size();
	}

	public List<Experiment> copyLinkedExperimentList()
	{
		return new ArrayList<Experiment>(experiment_BackReferenceList);
	}

	public Experiment getLinkedExperiment(int index)
	{
		return experiment_BackReferenceList.get(index);
	}

	public Experiment setLinkedExperiment(int index, Experiment experiment_BackReference)
	{
		return experiment_BackReferenceList.set(index, experiment_BackReference);
	}

	public void linkExperiment(Experiment experiment_BackReference)
	{
		this.experiment_BackReferenceList.add(experiment_BackReference);
	}

	public void unlinkExperiment(Experiment experiment_BackReference)
	{
		this.experiment_BackReferenceList.add(experiment_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		// Creating XML block for Experimenter
		Element Experimenter_element = document.createElement("Experimenter");
		if (userName != null)
		{
			// Attribute property UserName
			Experimenter_element.setAttribute("UserName", userName.toString());
		}
		if (displayName != null)
		{
			// Attribute property DisplayName
			Experimenter_element.setAttribute("DisplayName", displayName.toString());
		}
		if (firstName != null)
		{
			// Attribute property FirstName
			Experimenter_element.setAttribute("FirstName", firstName.toString());
		}
		if (middleName != null)
		{
			// Attribute property MiddleName
			Experimenter_element.setAttribute("MiddleName", middleName.toString());
		}
		if (lastName != null)
		{
			// Attribute property LastName
			Experimenter_element.setAttribute("LastName", lastName.toString());
		}
		if (email != null)
		{
			// Attribute property Email
			Experimenter_element.setAttribute("Email", email.toString());
		}
		if (institution != null)
		{
			// Attribute property Institution
			Experimenter_element.setAttribute("Institution", institution.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Experimenter_element.setAttribute("ID", id.toString());
		}
		if (groupList != null)
		{
			// Element property GroupRef which is complex (has
			// sub-elements) and occurs more than once
			for (Group groupList_value : groupList)
			{
				Experimenter_element.appendChild(groupList_value.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Element property AnnotationRef which is complex (has
			// sub-elements) and occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				Experimenter_element.appendChild(annotationList_value.asXMLElement(document));
			}
		}
		if (image_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (microbeamManipulation_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		}
		if (project_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Project_BackReference
		}
		if (dataset_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}
		if (experiment_BackReferenceList != null)
		{
			// *** IGNORING *** Skipped back reference Experiment_BackReference
		}
		return Experimenter_element;
	}

	public static Experimenter fromXMLElement(Element element)
		throws EnumerationException
	{
		String tagName = element.getTagName();
		if (!"Experimenter".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Experimenter got %s",
					tagName));
		}
		Experimenter instance = new Experimenter();
		if (element.hasAttribute("UserName"))
		{
			// Attribute property UserName
			instance.setUserName(String.valueOf(
					element.getAttribute("UserName")));
		}
		if (element.hasAttribute("DisplayName"))
		{
			// Attribute property DisplayName
			instance.setDisplayName(String.valueOf(
					element.getAttribute("DisplayName")));
		}
		if (element.hasAttribute("FirstName"))
		{
			// Attribute property FirstName
			instance.setFirstName(String.valueOf(
					element.getAttribute("FirstName")));
		}
		if (element.hasAttribute("MiddleName"))
		{
			// Attribute property MiddleName
			instance.setMiddleName(String.valueOf(
					element.getAttribute("MiddleName")));
		}
		if (element.hasAttribute("LastName"))
		{
			// Attribute property LastName
			instance.setLastName(String.valueOf(
					element.getAttribute("LastName")));
		}
		if (element.hasAttribute("Email"))
		{
			// Attribute property Email
			instance.setEmail(String.valueOf(
					element.getAttribute("Email")));
		}
		if (element.hasAttribute("Institution"))
		{
			// Attribute property Institution
			instance.setInstitution(String.valueOf(
					element.getAttribute("Institution")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			instance.setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// Element property GroupRef which is complex (has
		// sub-elements) and occurs more than once
		NodeList GroupRef_nodeList = element.getElementsByTagName("GroupRef");
		for (int i = 0; i < GroupRef_nodeList.getLength(); i++)
		{
			instance.addGroup(Group.fromXMLElement(
					(Element) GroupRef_nodeList.item(i)));
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
		// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		// *** IGNORING *** Skipped back reference Project_BackReference
		// *** IGNORING *** Skipped back reference Dataset_BackReference
		// *** IGNORING *** Skipped back reference Experiment_BackReference
		return instance;
	}
}
