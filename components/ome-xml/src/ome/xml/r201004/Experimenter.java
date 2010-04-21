/*
 * ome.xml.r201004.Experimenter
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

public class Experimenter extends AbstractOMEModelObject
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

	// Back reference GroupRef
	private List<Group> groupList = new ArrayList<Group>();

	// Back reference AnnotationRef
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

	/** Default constructor. */
	public Experimenter()
	{
		super();
	}

	/** 
	 * Constructs Experimenter recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Experimenter(Element element) throws EnumerationException
	{
		super(element);
		String tagName = element.getTagName();
		if (!"Experimenter".equals(tagName))
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Expecting node name of Experimenter got %s",
					tagName));
		}
		if (element.hasAttribute("UserName"))
		{
			// Attribute property UserName
			setUserName(String.valueOf(
					element.getAttribute("UserName")));
		}
		if (element.hasAttribute("DisplayName"))
		{
			// Attribute property DisplayName
			setDisplayName(String.valueOf(
					element.getAttribute("DisplayName")));
		}
		if (element.hasAttribute("FirstName"))
		{
			// Attribute property FirstName
			setFirstName(String.valueOf(
					element.getAttribute("FirstName")));
		}
		if (element.hasAttribute("MiddleName"))
		{
			// Attribute property MiddleName
			setMiddleName(String.valueOf(
					element.getAttribute("MiddleName")));
		}
		if (element.hasAttribute("LastName"))
		{
			// Attribute property LastName
			setLastName(String.valueOf(
					element.getAttribute("LastName")));
		}
		if (element.hasAttribute("Email"))
		{
			// Attribute property Email
			setEmail(String.valueOf(
					element.getAttribute("Email")));
		}
		if (element.hasAttribute("Institution"))
		{
			// Attribute property Institution
			setInstitution(String.valueOf(
					element.getAttribute("Institution")));
		}
		if (element.hasAttribute("ID"))
		{
			// Attribute property ID
			setID(String.valueOf(
					element.getAttribute("ID")));
		}
		// *** IGNORING *** Skipped back reference GroupRef
		// *** IGNORING *** Skipped back reference AnnotationRef
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		// *** IGNORING *** Skipped back reference Project_BackReference
		// *** IGNORING *** Skipped back reference Dataset_BackReference
		// *** IGNORING *** Skipped back reference Experiment_BackReference
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

	// Reference GroupRef
	public int sizeOfLinkedGroupList()
	{
		return groupList.size();
	}

	public List<Group> copyLinkedGroupList()
	{
		return new ArrayList<Group>(groupList);
	}

	public Group getLinkedGroup(int index)
	{
		return groupList.get(index);
	}

	public Group setLinkedGroup(int index, Group o)
	{
		return groupList.set(index, o);
	}

	public void linkGroup(Group o)
	{
		this.groupList.add(o);
	}

	public void unlinkGroup(Group o)
	{
		this.groupList.add(o);
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
	public int sizeOfExperimentList()
	{
		return experiment_BackReferenceList.size();
	}

	public List<Experiment> copyExperimentList()
	{
		return new ArrayList<Experiment>(experiment_BackReferenceList);
	}

	public Experiment getExperiment(int index)
	{
		return experiment_BackReferenceList.get(index);
	}

	public Experiment setExperiment(int index, Experiment experiment_BackReference)
	{
		return experiment_BackReferenceList.set(index, experiment_BackReference);
	}

	public void addExperiment(Experiment experiment_BackReference)
	{
		experiment_BackReferenceList.add(experiment_BackReference);
	}

	public void removeExperiment(Experiment experiment_BackReference)
	{
		experiment_BackReferenceList.remove(experiment_BackReference);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Experimenter_element)
	{
		// Creating XML block for Experimenter
		if (Experimenter_element == null)
		{
			Experimenter_element = document.createElement("Experimenter");
		}
		Experimenter_element = super.asXMLElement(document, Experimenter_element);

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
			// *** IGNORING *** Skipped back reference GroupRef
		}
		if (annotationList != null)
		{
			// *** IGNORING *** Skipped back reference AnnotationRef
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
}
