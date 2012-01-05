/*
 * ome.xml.model.Experimenter
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
 * Created by melissa via xsd-fu on 2012-01-05 15:21:37-0500
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

public class Experimenter extends AbstractOMEModelObject
{
	// Base:  -- Name: Experimenter -- Type: Experimenter -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2011-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Experimenter.class);

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

	// Reference GroupRef
	private List<Group> groupList = new ArrayList<Group>();

	// Reference AnnotationRef
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
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Experimenter(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Experimenter specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Experimenter recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Experimenter".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Experimenter got {}", tagName);
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
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Experimenter missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		// Element reference GroupRef
		List<Element> GroupRef_nodeList =
				getChildrenByTagName(element, "GroupRef");
		for (Element GroupRef_element : GroupRef_nodeList)
		{
			GroupRef groupList_reference = new GroupRef();
			groupList_reference.setID(GroupRef_element.getAttribute("ID"));
			model.addReference(this, groupList_reference);
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationList_reference = new AnnotationRef();
			annotationList_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationList_reference);
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		// *** IGNORING *** Skipped back reference Project_BackReference
		// *** IGNORING *** Skipped back reference Dataset_BackReference
		// *** IGNORING *** Skipped back reference Experiment_BackReference
	}

	// -- Experimenter API methods --

	public boolean link(Reference reference, OMEModelObject o)
	{
		boolean wasHandledBySuperClass = super.link(reference, o);
		if (wasHandledBySuperClass)
		{
			return true;
		}
		if (reference instanceof GroupRef)
		{
			Group o_casted = (Group) o;
			o_casted.linkExperimenter(this);
			if (!copyLinkedGroupList().contains(o_casted)) {
				groupList.add(o_casted);
			}
			return true;
		}
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkExperimenter(this);
			if (!copyLinkedAnnotationList().contains(o_casted)) {
				annotationList.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


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

	// Reference which occurs more than once
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

	public boolean linkGroup(Group o)
	{
		o.linkExperimenter(this);
		return groupList.add(o);
	}

	public boolean unlinkGroup(Group o)
	{
		o.unlinkExperimenter(this);
		return groupList.remove(o);
	}

	// Reference which occurs more than once
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

	public boolean linkAnnotation(Annotation o)
	{
		o.linkExperimenter(this);
		return annotationList.add(o);
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		o.unlinkExperimenter(this);
		return annotationList.remove(o);
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

	public MicrobeamManipulation setLinkedMicrobeamManipulation(int index, MicrobeamManipulation o)
	{
		return microbeamManipulation_BackReferenceList.set(index, o);
	}

	public boolean linkMicrobeamManipulation(MicrobeamManipulation o)
	{
		return microbeamManipulation_BackReferenceList.add(o);
	}

	public boolean unlinkMicrobeamManipulation(MicrobeamManipulation o)
	{
		return microbeamManipulation_BackReferenceList.remove(o);
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

	public Experiment setLinkedExperiment(int index, Experiment o)
	{
		return experiment_BackReferenceList.set(index, o);
	}

	public boolean linkExperiment(Experiment o)
	{
		return experiment_BackReferenceList.add(o);
	}

	public boolean unlinkExperiment(Experiment o)
	{
		return experiment_BackReferenceList.remove(o);
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
			Experimenter_element =
					document.createElementNS(NAMESPACE, "Experimenter");
		}

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
			// Reference property GroupRef which occurs more than once
			for (Group groupList_value : groupList)
			{
				GroupRef o = new GroupRef();
				o.setID(groupList_value.getID());
				Experimenter_element.appendChild(o.asXMLElement(document));
			}
		}
		if (annotationList != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationList_value : annotationList)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationList_value.getID());
				Experimenter_element.appendChild(o.asXMLElement(document));
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
		return super.asXMLElement(document, Experimenter_element);
	}
}
