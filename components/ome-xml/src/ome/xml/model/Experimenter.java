/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:35.744474
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

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/OME/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Experimenter.class);

	// -- Instance variables --


	// Property
	private String email;

	// Property
	private String userName;

	// Property
	private String firstName;

	// Property
	private String middleName;

	// Property
	private String lastName;

	// Property
	private String id;

	// Property
	private String institution;

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Back reference Image_BackReference
	private List<Image> images = new ArrayList<Image>();

	// Back reference MicrobeamManipulation_BackReference
	private List<MicrobeamManipulation> microbeamManipulations = new ArrayList<MicrobeamManipulation>();

	// Back reference Project_BackReference
	private List<Project> projects = new ArrayList<Project>();

	// Back reference ExperimenterGroup_BackReference
	private List<ExperimenterGroup> experimenterGroupLinks = new ArrayList<ExperimenterGroup>();

	// Back reference Dataset_BackReference
	private List<Dataset> datasets = new ArrayList<Dataset>();

	// Back reference Experiment_BackReference
	private List<Experiment> experiments = new ArrayList<Experiment>();

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
		if (element.hasAttribute("Email"))
		{
			// Attribute property Email
			setEmail(String.valueOf(
					element.getAttribute("Email")));
		}
		if (element.hasAttribute("UserName"))
		{
			// Attribute property UserName
			setUserName(String.valueOf(
					element.getAttribute("UserName")));
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
		if (element.hasAttribute("Institution"))
		{
			// Attribute property Institution
			setInstitution(String.valueOf(
					element.getAttribute("Institution")));
		}
		// Element reference AnnotationRef
		List<Element> AnnotationRef_nodeList =
				getChildrenByTagName(element, "AnnotationRef");
		for (Element AnnotationRef_element : AnnotationRef_nodeList)
		{
			AnnotationRef annotationLinks_reference = new AnnotationRef();
			annotationLinks_reference.setID(AnnotationRef_element.getAttribute("ID"));
			model.addReference(this, annotationLinks_reference);
		}
		// *** IGNORING *** Skipped back reference Image_BackReference
		// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		// *** IGNORING *** Skipped back reference Project_BackReference
		// *** IGNORING *** Skipped back reference ExperimenterGroup_BackReference
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
		if (reference instanceof AnnotationRef)
		{
			Annotation o_casted = (Annotation) o;
			o_casted.linkExperimenter(this);
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
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
	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
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
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
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

	// Reference which occurs more than once
	public int sizeOfLinkedAnnotationList()
	{
		return annotationLinks.size();
	}

	public List<Annotation> copyLinkedAnnotationList()
	{
		return new ArrayList<Annotation>(annotationLinks);
	}

	public Annotation getLinkedAnnotation(int index)
	{
		return annotationLinks.get(index);
	}

	public Annotation setLinkedAnnotation(int index, Annotation o)
	{
		return annotationLinks.set(index, o);
	}

	public boolean linkAnnotation(Annotation o)
	{

			o.linkExperimenter(this);
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{

			o.unlinkExperimenter(this);
		return annotationLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedImageList()
	{
		return images.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(images);
	}

	public Image getLinkedImage(int index)
	{
		return images.get(index);
	}

	public Image setLinkedImage(int index, Image o)
	{
		return images.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		if (!images.contains(o)) {
			return images.add(o);
		}
		return false;
	}

	public boolean unlinkImage(Image o)
	{
		return images.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedMicrobeamManipulationList()
	{
		return microbeamManipulations.size();
	}

	public List<MicrobeamManipulation> copyLinkedMicrobeamManipulationList()
	{
		return new ArrayList<MicrobeamManipulation>(microbeamManipulations);
	}

	public MicrobeamManipulation getLinkedMicrobeamManipulation(int index)
	{
		return microbeamManipulations.get(index);
	}

	public MicrobeamManipulation setLinkedMicrobeamManipulation(int index, MicrobeamManipulation o)
	{
		return microbeamManipulations.set(index, o);
	}

	public boolean linkMicrobeamManipulation(MicrobeamManipulation o)
	{
		if (!microbeamManipulations.contains(o)) {
			return microbeamManipulations.add(o);
		}
		return false;
	}

	public boolean unlinkMicrobeamManipulation(MicrobeamManipulation o)
	{
		return microbeamManipulations.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedProjectList()
	{
		return projects.size();
	}

	public List<Project> copyLinkedProjectList()
	{
		return new ArrayList<Project>(projects);
	}

	public Project getLinkedProject(int index)
	{
		return projects.get(index);
	}

	public Project setLinkedProject(int index, Project o)
	{
		return projects.set(index, o);
	}

	public boolean linkProject(Project o)
	{
		if (!projects.contains(o)) {
			return projects.add(o);
		}
		return false;
	}

	public boolean unlinkProject(Project o)
	{
		return projects.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedExperimenterGroupList()
	{
		return experimenterGroupLinks.size();
	}

	public List<ExperimenterGroup> copyLinkedExperimenterGroupList()
	{
		return new ArrayList<ExperimenterGroup>(experimenterGroupLinks);
	}

	public ExperimenterGroup getLinkedExperimenterGroup(int index)
	{
		return experimenterGroupLinks.get(index);
	}

	public ExperimenterGroup setLinkedExperimenterGroup(int index, ExperimenterGroup o)
	{
		return experimenterGroupLinks.set(index, o);
	}

	public boolean linkExperimenterGroup(ExperimenterGroup o)
	{
		if (!experimenterGroupLinks.contains(o)) {
			return experimenterGroupLinks.add(o);
		}
		return false;
	}

	public boolean unlinkExperimenterGroup(ExperimenterGroup o)
	{
		return experimenterGroupLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedDatasetList()
	{
		return datasets.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(datasets);
	}

	public Dataset getLinkedDataset(int index)
	{
		return datasets.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset o)
	{
		return datasets.set(index, o);
	}

	public boolean linkDataset(Dataset o)
	{
		if (!datasets.contains(o)) {
			return datasets.add(o);
		}
		return false;
	}

	public boolean unlinkDataset(Dataset o)
	{
		return datasets.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedExperimentList()
	{
		return experiments.size();
	}

	public List<Experiment> copyLinkedExperimentList()
	{
		return new ArrayList<Experiment>(experiments);
	}

	public Experiment getLinkedExperiment(int index)
	{
		return experiments.get(index);
	}

	public Experiment setLinkedExperiment(int index, Experiment o)
	{
		return experiments.set(index, o);
	}

	public boolean linkExperiment(Experiment o)
	{
		if (!experiments.contains(o)) {
			return experiments.add(o);
		}
		return false;
	}

	public boolean unlinkExperiment(Experiment o)
	{
		return experiments.remove(o);
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

		if (email != null)
		{
			// Attribute property Email
			Experimenter_element.setAttribute("Email", email.toString());
		}
		if (userName != null)
		{
			// Attribute property UserName
			Experimenter_element.setAttribute("UserName", userName.toString());
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
		if (id != null)
		{
			// Attribute property ID
			Experimenter_element.setAttribute("ID", id.toString());
		}
		if (institution != null)
		{
			// Attribute property Institution
			Experimenter_element.setAttribute("Institution", institution.toString());
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Experimenter_element.appendChild(o.asXMLElement(document));
			}
		}
		if (images != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (microbeamManipulations != null)
		{
			// *** IGNORING *** Skipped back reference MicrobeamManipulation_BackReference
		}
		if (projects != null)
		{
			// *** IGNORING *** Skipped back reference Project_BackReference
		}
		if (experimenterGroupLinks != null)
		{
			// *** IGNORING *** Skipped back reference ExperimenterGroup_BackReference
		}
		if (datasets != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}
		if (experiments != null)
		{
			// *** IGNORING *** Skipped back reference Experiment_BackReference
		}
		return super.asXMLElement(document, Experimenter_element);
	}
}
