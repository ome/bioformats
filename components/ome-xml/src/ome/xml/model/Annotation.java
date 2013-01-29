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

public abstract class Annotation extends AbstractOMEModelObject
{
	// Base:  -- Name: Annotation -- Type: Annotation -- javaBase: AbstractOMEModelObject -- javaType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2012-06";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Annotation.class);

	// -- Instance variables --


	// Property
	private String namespace;

	// Property
	private String id;

	// Property
	private String description;

	// Reference AnnotationRef
	private List<Annotation> annotationLinks = new ArrayList<Annotation>();

	// Back reference Image_BackReference
	private List<Image> imageLinks = new ArrayList<Image>();

	// Back reference Pixels_BackReference
	private List<Pixels> pixelsLinks = new ArrayList<Pixels>();

	// Back reference Plane_BackReference
	private List<Plane> planeLinks = new ArrayList<Plane>();

	// Back reference Channel_BackReference
	private List<Channel> channelLinks = new ArrayList<Channel>();

	// Back reference Project_BackReference
	private List<Project> projectLinks = new ArrayList<Project>();

	// Back reference ExperimenterGroup_BackReference
	private List<ExperimenterGroup> experimenterGroupLinks = new ArrayList<ExperimenterGroup>();

	// Back reference Dataset_BackReference
	private List<Dataset> datasetLinks = new ArrayList<Dataset>();

	// Back reference Experimenter_BackReference
	private List<Experimenter> experimenterLinks = new ArrayList<Experimenter>();

	// Back reference ROI_BackReference
	private List<ROI> roiLinks = new ArrayList<ROI>();

	// Back reference Plate_BackReference
	private List<Plate> plateLinks = new ArrayList<Plate>();

	// Back reference Reagent_BackReference
	private List<Reagent> reagentLinks = new ArrayList<Reagent>();

	// Back reference Screen_BackReference
	private List<Screen> screenLinks = new ArrayList<Screen>();

	// Back reference PlateAcquisition_BackReference
	private List<PlateAcquisition> plateAcquisitionLinks = new ArrayList<PlateAcquisition>();

	// Back reference Well_BackReference
	private List<Well> wellLinks = new ArrayList<Well>();

	// Back reference WellSample_BackReference
	private List<WellSample> wellSampleLinks = new ArrayList<WellSample>();

	// -- Constructors --

	/** Default constructor. */
	public Annotation()
	{
		super();
	}

	/** 
	 * Constructs Annotation recursively from an XML DOM tree.
	 * @param element Root of the XML DOM tree to construct a model object
	 * graph from.
	 * @param model Handler for the OME model which keeps track of instances
	 * and references seen during object population.
	 * @throws EnumerationException If there is an error instantiating an
	 * enumeration during model object creation.
	 */
	public Annotation(Element element, OMEModel model)
	    throws EnumerationException
	{
		update(element, model);
	}

	// -- Custom content from Annotation specific template --


	// -- OMEModelObject API methods --

	/** 
	 * Updates Annotation recursively from an XML DOM tree. <b>NOTE:</b> No
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
		if (!"Annotation".equals(tagName))
		{
			LOGGER.debug("Expecting node name of Annotation got {}", tagName);
		}
		if (element.hasAttribute("Namespace"))
		{
			// Attribute property Namespace
			setNamespace(String.valueOf(
					element.getAttribute("Namespace")));
		}
		if (!element.hasAttribute("ID") && getID() == null)
		{
			// TODO: Should be its own exception
			throw new RuntimeException(String.format(
					"Annotation missing required ID property."));
		}
		if (element.hasAttribute("ID"))
		{
			// ID property
			setID(String.valueOf(
						element.getAttribute("ID")));
			// Adding this model object to the model handler
			model.addModelObject(getID(), this);
		}
		List<Element> Description_nodeList =
				getChildrenByTagName(element, "Description");
		if (Description_nodeList.size() > 1)
		{
			// TODO: Should be its own Exception
			throw new RuntimeException(String.format(
					"Description node list size %d != 1",
					Description_nodeList.size()));
		}
		else if (Description_nodeList.size() != 0)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			setDescription(
					String.valueOf(Description_nodeList.get(0).getTextContent()));
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
		// *** IGNORING *** Skipped back reference Pixels_BackReference
		// *** IGNORING *** Skipped back reference Plane_BackReference
		// *** IGNORING *** Skipped back reference Channel_BackReference
		// *** IGNORING *** Skipped back reference Project_BackReference
		// *** IGNORING *** Skipped back reference ExperimenterGroup_BackReference
		// *** IGNORING *** Skipped back reference Dataset_BackReference
		// *** IGNORING *** Skipped back reference Experimenter_BackReference
		// *** IGNORING *** Skipped back reference ROI_BackReference
		// *** IGNORING *** Skipped back reference Plate_BackReference
		// *** IGNORING *** Skipped back reference Reagent_BackReference
		// *** IGNORING *** Skipped back reference Screen_BackReference
		// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		// *** IGNORING *** Skipped back reference Well_BackReference
		// *** IGNORING *** Skipped back reference WellSample_BackReference
	}

	// -- Annotation API methods --

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
			if (!annotationLinks.contains(o_casted)) {
				annotationLinks.add(o_casted);
			}
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property
	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
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
		if (!annotationLinks.contains(o)) {
			return annotationLinks.add(o);
		}
		return false;
	}

	public boolean unlinkAnnotation(Annotation o)
	{
		return annotationLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedImageList()
	{
		return imageLinks.size();
	}

	public List<Image> copyLinkedImageList()
	{
		return new ArrayList<Image>(imageLinks);
	}

	public Image getLinkedImage(int index)
	{
		return imageLinks.get(index);
	}

	public Image setLinkedImage(int index, Image o)
	{
		return imageLinks.set(index, o);
	}

	public boolean linkImage(Image o)
	{
		if (!imageLinks.contains(o)) {
			return imageLinks.add(o);
		}
		return false;
	}

	public boolean unlinkImage(Image o)
	{
		return imageLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPixelsList()
	{
		return pixelsLinks.size();
	}

	public List<Pixels> copyLinkedPixelsList()
	{
		return new ArrayList<Pixels>(pixelsLinks);
	}

	public Pixels getLinkedPixels(int index)
	{
		return pixelsLinks.get(index);
	}

	public Pixels setLinkedPixels(int index, Pixels o)
	{
		return pixelsLinks.set(index, o);
	}

	public boolean linkPixels(Pixels o)
	{
		if (!pixelsLinks.contains(o)) {
			return pixelsLinks.add(o);
		}
		return false;
	}

	public boolean unlinkPixels(Pixels o)
	{
		return pixelsLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlaneList()
	{
		return planeLinks.size();
	}

	public List<Plane> copyLinkedPlaneList()
	{
		return new ArrayList<Plane>(planeLinks);
	}

	public Plane getLinkedPlane(int index)
	{
		return planeLinks.get(index);
	}

	public Plane setLinkedPlane(int index, Plane o)
	{
		return planeLinks.set(index, o);
	}

	public boolean linkPlane(Plane o)
	{
		if (!planeLinks.contains(o)) {
			return planeLinks.add(o);
		}
		return false;
	}

	public boolean unlinkPlane(Plane o)
	{
		return planeLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedChannelList()
	{
		return channelLinks.size();
	}

	public List<Channel> copyLinkedChannelList()
	{
		return new ArrayList<Channel>(channelLinks);
	}

	public Channel getLinkedChannel(int index)
	{
		return channelLinks.get(index);
	}

	public Channel setLinkedChannel(int index, Channel o)
	{
		return channelLinks.set(index, o);
	}

	public boolean linkChannel(Channel o)
	{
		if (!channelLinks.contains(o)) {
			return channelLinks.add(o);
		}
		return false;
	}

	public boolean unlinkChannel(Channel o)
	{
		return channelLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedProjectList()
	{
		return projectLinks.size();
	}

	public List<Project> copyLinkedProjectList()
	{
		return new ArrayList<Project>(projectLinks);
	}

	public Project getLinkedProject(int index)
	{
		return projectLinks.get(index);
	}

	public Project setLinkedProject(int index, Project o)
	{
		return projectLinks.set(index, o);
	}

	public boolean linkProject(Project o)
	{
		if (!projectLinks.contains(o)) {
			return projectLinks.add(o);
		}
		return false;
	}

	public boolean unlinkProject(Project o)
	{
		return projectLinks.remove(o);
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
		return datasetLinks.size();
	}

	public List<Dataset> copyLinkedDatasetList()
	{
		return new ArrayList<Dataset>(datasetLinks);
	}

	public Dataset getLinkedDataset(int index)
	{
		return datasetLinks.get(index);
	}

	public Dataset setLinkedDataset(int index, Dataset o)
	{
		return datasetLinks.set(index, o);
	}

	public boolean linkDataset(Dataset o)
	{
		if (!datasetLinks.contains(o)) {
			return datasetLinks.add(o);
		}
		return false;
	}

	public boolean unlinkDataset(Dataset o)
	{
		return datasetLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedExperimenterList()
	{
		return experimenterLinks.size();
	}

	public List<Experimenter> copyLinkedExperimenterList()
	{
		return new ArrayList<Experimenter>(experimenterLinks);
	}

	public Experimenter getLinkedExperimenter(int index)
	{
		return experimenterLinks.get(index);
	}

	public Experimenter setLinkedExperimenter(int index, Experimenter o)
	{
		return experimenterLinks.set(index, o);
	}

	public boolean linkExperimenter(Experimenter o)
	{
		if (!experimenterLinks.contains(o)) {
			return experimenterLinks.add(o);
		}
		return false;
	}

	public boolean unlinkExperimenter(Experimenter o)
	{
		return experimenterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedROIList()
	{
		return roiLinks.size();
	}

	public List<ROI> copyLinkedROIList()
	{
		return new ArrayList<ROI>(roiLinks);
	}

	public ROI getLinkedROI(int index)
	{
		return roiLinks.get(index);
	}

	public ROI setLinkedROI(int index, ROI o)
	{
		return roiLinks.set(index, o);
	}

	public boolean linkROI(ROI o)
	{
		if (!roiLinks.contains(o)) {
			return roiLinks.add(o);
		}
		return false;
	}

	public boolean unlinkROI(ROI o)
	{
		return roiLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlateList()
	{
		return plateLinks.size();
	}

	public List<Plate> copyLinkedPlateList()
	{
		return new ArrayList<Plate>(plateLinks);
	}

	public Plate getLinkedPlate(int index)
	{
		return plateLinks.get(index);
	}

	public Plate setLinkedPlate(int index, Plate o)
	{
		return plateLinks.set(index, o);
	}

	public boolean linkPlate(Plate o)
	{
		if (!plateLinks.contains(o)) {
			return plateLinks.add(o);
		}
		return false;
	}

	public boolean unlinkPlate(Plate o)
	{
		return plateLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedReagentList()
	{
		return reagentLinks.size();
	}

	public List<Reagent> copyLinkedReagentList()
	{
		return new ArrayList<Reagent>(reagentLinks);
	}

	public Reagent getLinkedReagent(int index)
	{
		return reagentLinks.get(index);
	}

	public Reagent setLinkedReagent(int index, Reagent o)
	{
		return reagentLinks.set(index, o);
	}

	public boolean linkReagent(Reagent o)
	{
		if (!reagentLinks.contains(o)) {
			return reagentLinks.add(o);
		}
		return false;
	}

	public boolean unlinkReagent(Reagent o)
	{
		return reagentLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedScreenList()
	{
		return screenLinks.size();
	}

	public List<Screen> copyLinkedScreenList()
	{
		return new ArrayList<Screen>(screenLinks);
	}

	public Screen getLinkedScreen(int index)
	{
		return screenLinks.get(index);
	}

	public Screen setLinkedScreen(int index, Screen o)
	{
		return screenLinks.set(index, o);
	}

	public boolean linkScreen(Screen o)
	{
		if (!screenLinks.contains(o)) {
			return screenLinks.add(o);
		}
		return false;
	}

	public boolean unlinkScreen(Screen o)
	{
		return screenLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedPlateAcquisitionList()
	{
		return plateAcquisitionLinks.size();
	}

	public List<PlateAcquisition> copyLinkedPlateAcquisitionList()
	{
		return new ArrayList<PlateAcquisition>(plateAcquisitionLinks);
	}

	public PlateAcquisition getLinkedPlateAcquisition(int index)
	{
		return plateAcquisitionLinks.get(index);
	}

	public PlateAcquisition setLinkedPlateAcquisition(int index, PlateAcquisition o)
	{
		return plateAcquisitionLinks.set(index, o);
	}

	public boolean linkPlateAcquisition(PlateAcquisition o)
	{
		if (!plateAcquisitionLinks.contains(o)) {
			return plateAcquisitionLinks.add(o);
		}
		return false;
	}

	public boolean unlinkPlateAcquisition(PlateAcquisition o)
	{
		return plateAcquisitionLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedWellList()
	{
		return wellLinks.size();
	}

	public List<Well> copyLinkedWellList()
	{
		return new ArrayList<Well>(wellLinks);
	}

	public Well getLinkedWell(int index)
	{
		return wellLinks.get(index);
	}

	public Well setLinkedWell(int index, Well o)
	{
		return wellLinks.set(index, o);
	}

	public boolean linkWell(Well o)
	{
		if (!wellLinks.contains(o)) {
			return wellLinks.add(o);
		}
		return false;
	}

	public boolean unlinkWell(Well o)
	{
		return wellLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedWellSampleList()
	{
		return wellSampleLinks.size();
	}

	public List<WellSample> copyLinkedWellSampleList()
	{
		return new ArrayList<WellSample>(wellSampleLinks);
	}

	public WellSample getLinkedWellSample(int index)
	{
		return wellSampleLinks.get(index);
	}

	public WellSample setLinkedWellSample(int index, WellSample o)
	{
		return wellSampleLinks.set(index, o);
	}

	public boolean linkWellSample(WellSample o)
	{
		if (!wellSampleLinks.contains(o)) {
			return wellSampleLinks.add(o);
		}
		return false;
	}

	public boolean unlinkWellSample(WellSample o)
	{
		return wellSampleLinks.remove(o);
	}

	public Element asXMLElement(Document document)
	{
		return asXMLElement(document, null);
	}

	protected Element asXMLElement(Document document, Element Annotation_element)
	{
		// Creating XML block for Annotation

		if (Annotation_element == null)
		{
			Annotation_element =
					document.createElementNS(NAMESPACE, "Annotation");
		}

		if (namespace != null)
		{
			// Attribute property Namespace
			Annotation_element.setAttribute("Namespace", namespace.toString());
		}
		if (id != null)
		{
			// Attribute property ID
			Annotation_element.setAttribute("ID", id.toString());
		}
		if (description != null)
		{
			// Element property Description which is not complex (has no
			// sub-elements)
			Element description_element = 
					document.createElementNS(NAMESPACE, "Description");
			description_element.setTextContent(description.toString());
			Annotation_element.appendChild(description_element);
		}
		if (annotationLinks != null)
		{
			// Reference property AnnotationRef which occurs more than once
			for (Annotation annotationLinks_value : annotationLinks)
			{
				AnnotationRef o = new AnnotationRef();
				o.setID(annotationLinks_value.getID());
				Annotation_element.appendChild(o.asXMLElement(document));
			}
		}
		if (imageLinks != null)
		{
			// *** IGNORING *** Skipped back reference Image_BackReference
		}
		if (pixelsLinks != null)
		{
			// *** IGNORING *** Skipped back reference Pixels_BackReference
		}
		if (planeLinks != null)
		{
			// *** IGNORING *** Skipped back reference Plane_BackReference
		}
		if (channelLinks != null)
		{
			// *** IGNORING *** Skipped back reference Channel_BackReference
		}
		if (projectLinks != null)
		{
			// *** IGNORING *** Skipped back reference Project_BackReference
		}
		if (experimenterGroupLinks != null)
		{
			// *** IGNORING *** Skipped back reference ExperimenterGroup_BackReference
		}
		if (datasetLinks != null)
		{
			// *** IGNORING *** Skipped back reference Dataset_BackReference
		}
		if (experimenterLinks != null)
		{
			// *** IGNORING *** Skipped back reference Experimenter_BackReference
		}
		if (roiLinks != null)
		{
			// *** IGNORING *** Skipped back reference ROI_BackReference
		}
		if (plateLinks != null)
		{
			// *** IGNORING *** Skipped back reference Plate_BackReference
		}
		if (reagentLinks != null)
		{
			// *** IGNORING *** Skipped back reference Reagent_BackReference
		}
		if (screenLinks != null)
		{
			// *** IGNORING *** Skipped back reference Screen_BackReference
		}
		if (plateAcquisitionLinks != null)
		{
			// *** IGNORING *** Skipped back reference PlateAcquisition_BackReference
		}
		if (wellLinks != null)
		{
			// *** IGNORING *** Skipped back reference Well_BackReference
		}
		if (wellSampleLinks != null)
		{
			// *** IGNORING *** Skipped back reference WellSample_BackReference
		}
		return super.asXMLElement(document, Annotation_element);
	}
}
