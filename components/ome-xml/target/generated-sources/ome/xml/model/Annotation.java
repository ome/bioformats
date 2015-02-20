/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import ome.xml.model.enums.*;
import ome.xml.model.enums.handlers.*;
import ome.xml.model.primitives.*;

public abstract class Annotation extends AbstractOMEModelObject
{
	// Base:  -- Name: Annotation -- Type: Annotation -- modelBaseType: AbstractOMEModelObject -- langBaseType: Object

	// -- Constants --

	public static final String NAMESPACE = "http://www.openmicroscopy.org/Schemas/SA/2015-01";

	/** Logger for this class. */
	private static final Logger LOGGER =
		LoggerFactory.getLogger(Annotation.class);

	// -- Instance variables --

	// Namespace property
	private String namespace;

	// ID property
	private String id;

	// Annotator property
	private String annotator;

	// Description property
	private String description;

	// AnnotationRef reference (occurs more than once)
	private List<Annotation> annotationLinks = new ReferenceList<Annotation>();

	// Image_BackReference back reference (occurs more than once)
	private List<Image> imageLinks = new ReferenceList<Image>();

	// Plane_BackReference back reference (occurs more than once)
	private List<Plane> planeLinks = new ReferenceList<Plane>();

	// Channel_BackReference back reference (occurs more than once)
	private List<Channel> channelLinks = new ReferenceList<Channel>();

	// Instrument_BackReference back reference (occurs more than once)
	private List<Instrument> instrumentLinks = new ReferenceList<Instrument>();

	// Project_BackReference back reference (occurs more than once)
	private List<Project> projectLinks = new ReferenceList<Project>();

	// ExperimenterGroup_BackReference back reference (occurs more than once)
	private List<ExperimenterGroup> experimenterGroupLinks = new ReferenceList<ExperimenterGroup>();

	// Dataset_BackReference back reference (occurs more than once)
	private List<Dataset> datasetLinks = new ReferenceList<Dataset>();

	// Experimenter_BackReference back reference (occurs more than once)
	private List<Experimenter> experimenterLinks = new ReferenceList<Experimenter>();

	// Objective_BackReference back reference (occurs more than once)
	private List<Objective> objectiveLinks = new ReferenceList<Objective>();

	// Detector_BackReference back reference (occurs more than once)
	private List<Detector> detectorLinks = new ReferenceList<Detector>();

	// Filter_BackReference back reference (occurs more than once)
	private List<Filter> filterLinks = new ReferenceList<Filter>();

	// Dichroic_BackReference back reference (occurs more than once)
	private List<Dichroic> dichroicLinks = new ReferenceList<Dichroic>();

	// LightPath_BackReference back reference (occurs more than once)
	private List<LightPath> lightPathLinks = new ReferenceList<LightPath>();

	// LightSource_BackReference back reference (occurs more than once)
	private List<LightSource> lightSourceLinks = new ReferenceList<LightSource>();

	// ROI_BackReference back reference (occurs more than once)
	private List<ROI> roiLinks = new ReferenceList<ROI>();

	// Shape_BackReference back reference (occurs more than once)
	private List<Shape> shapeLinks = new ReferenceList<Shape>();

	// Plate_BackReference back reference (occurs more than once)
	private List<Plate> plateLinks = new ReferenceList<Plate>();

	// Reagent_BackReference back reference (occurs more than once)
	private List<Reagent> reagentLinks = new ReferenceList<Reagent>();

	// Screen_BackReference back reference (occurs more than once)
	private List<Screen> screenLinks = new ReferenceList<Screen>();

	// PlateAcquisition_BackReference back reference (occurs more than once)
	private List<PlateAcquisition> plateAcquisitionLinks = new ReferenceList<PlateAcquisition>();

	// Well_BackReference back reference (occurs more than once)
	private List<Well> wellLinks = new ReferenceList<Well>();

	// -- Constructors --

	/** Default constructor. */
	public Annotation()
	{
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

	/** Copy constructor. */
	public Annotation(Annotation orig)
	{
		namespace = orig.namespace;
		id = orig.id;
		annotator = orig.annotator;
		description = orig.description;
		annotationLinks = orig.annotationLinks;
		imageLinks = orig.imageLinks;
		planeLinks = orig.planeLinks;
		channelLinks = orig.channelLinks;
		instrumentLinks = orig.instrumentLinks;
		projectLinks = orig.projectLinks;
		experimenterGroupLinks = orig.experimenterGroupLinks;
		datasetLinks = orig.datasetLinks;
		experimenterLinks = orig.experimenterLinks;
		objectiveLinks = orig.objectiveLinks;
		detectorLinks = orig.detectorLinks;
		filterLinks = orig.filterLinks;
		dichroicLinks = orig.dichroicLinks;
		lightPathLinks = orig.lightPathLinks;
		lightSourceLinks = orig.lightSourceLinks;
		roiLinks = orig.roiLinks;
		shapeLinks = orig.shapeLinks;
		plateLinks = orig.plateLinks;
		reagentLinks = orig.reagentLinks;
		screenLinks = orig.screenLinks;
		plateAcquisitionLinks = orig.plateAcquisitionLinks;
		wellLinks = orig.wellLinks;
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
		if (element.hasAttribute("Annotator"))
		{
			// Attribute property Annotator
			setAnnotator(String.valueOf(
					element.getAttribute("Annotator")));
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
			annotationLinks.add(o_casted);
			return true;
		}
		LOGGER.debug("Unable to handle reference of type: {}", reference.getClass());
		return false;
	}


	// Property Namespace
	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	// Property ID
	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	// Property Annotator
	public String getAnnotator()
	{
		return annotator;
	}

	public void setAnnotator(String annotator)
	{
		this.annotator = annotator;
	}

	// Property Description
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
		return annotationLinks.add(o);
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
		return imageLinks.add(o);
	}

	public boolean unlinkImage(Image o)
	{
		return imageLinks.remove(o);
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
		return planeLinks.add(o);
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
		return channelLinks.add(o);
	}

	public boolean unlinkChannel(Channel o)
	{
		return channelLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedInstrumentList()
	{
		return instrumentLinks.size();
	}

	public List<Instrument> copyLinkedInstrumentList()
	{
		return new ArrayList<Instrument>(instrumentLinks);
	}

	public Instrument getLinkedInstrument(int index)
	{
		return instrumentLinks.get(index);
	}

	public Instrument setLinkedInstrument(int index, Instrument o)
	{
		return instrumentLinks.set(index, o);
	}

	public boolean linkInstrument(Instrument o)
	{
		return instrumentLinks.add(o);
	}

	public boolean unlinkInstrument(Instrument o)
	{
		return instrumentLinks.remove(o);
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
		return projectLinks.add(o);
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
		return experimenterGroupLinks.add(o);
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
		return datasetLinks.add(o);
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
		return experimenterLinks.add(o);
	}

	public boolean unlinkExperimenter(Experimenter o)
	{
		return experimenterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedObjectiveList()
	{
		return objectiveLinks.size();
	}

	public List<Objective> copyLinkedObjectiveList()
	{
		return new ArrayList<Objective>(objectiveLinks);
	}

	public Objective getLinkedObjective(int index)
	{
		return objectiveLinks.get(index);
	}

	public Objective setLinkedObjective(int index, Objective o)
	{
		return objectiveLinks.set(index, o);
	}

	public boolean linkObjective(Objective o)
	{
		return objectiveLinks.add(o);
	}

	public boolean unlinkObjective(Objective o)
	{
		return objectiveLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedDetectorList()
	{
		return detectorLinks.size();
	}

	public List<Detector> copyLinkedDetectorList()
	{
		return new ArrayList<Detector>(detectorLinks);
	}

	public Detector getLinkedDetector(int index)
	{
		return detectorLinks.get(index);
	}

	public Detector setLinkedDetector(int index, Detector o)
	{
		return detectorLinks.set(index, o);
	}

	public boolean linkDetector(Detector o)
	{
		return detectorLinks.add(o);
	}

	public boolean unlinkDetector(Detector o)
	{
		return detectorLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedFilterList()
	{
		return filterLinks.size();
	}

	public List<Filter> copyLinkedFilterList()
	{
		return new ArrayList<Filter>(filterLinks);
	}

	public Filter getLinkedFilter(int index)
	{
		return filterLinks.get(index);
	}

	public Filter setLinkedFilter(int index, Filter o)
	{
		return filterLinks.set(index, o);
	}

	public boolean linkFilter(Filter o)
	{
		return filterLinks.add(o);
	}

	public boolean unlinkFilter(Filter o)
	{
		return filterLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedDichroicList()
	{
		return dichroicLinks.size();
	}

	public List<Dichroic> copyLinkedDichroicList()
	{
		return new ArrayList<Dichroic>(dichroicLinks);
	}

	public Dichroic getLinkedDichroic(int index)
	{
		return dichroicLinks.get(index);
	}

	public Dichroic setLinkedDichroic(int index, Dichroic o)
	{
		return dichroicLinks.set(index, o);
	}

	public boolean linkDichroic(Dichroic o)
	{
		return dichroicLinks.add(o);
	}

	public boolean unlinkDichroic(Dichroic o)
	{
		return dichroicLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedLightPathList()
	{
		return lightPathLinks.size();
	}

	public List<LightPath> copyLinkedLightPathList()
	{
		return new ArrayList<LightPath>(lightPathLinks);
	}

	public LightPath getLinkedLightPath(int index)
	{
		return lightPathLinks.get(index);
	}

	public LightPath setLinkedLightPath(int index, LightPath o)
	{
		return lightPathLinks.set(index, o);
	}

	public boolean linkLightPath(LightPath o)
	{
		return lightPathLinks.add(o);
	}

	public boolean unlinkLightPath(LightPath o)
	{
		return lightPathLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedLightSourceList()
	{
		return lightSourceLinks.size();
	}

	public List<LightSource> copyLinkedLightSourceList()
	{
		return new ArrayList<LightSource>(lightSourceLinks);
	}

	public LightSource getLinkedLightSource(int index)
	{
		return lightSourceLinks.get(index);
	}

	public LightSource setLinkedLightSource(int index, LightSource o)
	{
		return lightSourceLinks.set(index, o);
	}

	public boolean linkLightSource(LightSource o)
	{
		return lightSourceLinks.add(o);
	}

	public boolean unlinkLightSource(LightSource o)
	{
		return lightSourceLinks.remove(o);
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
		return roiLinks.add(o);
	}

	public boolean unlinkROI(ROI o)
	{
		return roiLinks.remove(o);
	}

	// Reference which occurs more than once
	public int sizeOfLinkedShapeList()
	{
		return shapeLinks.size();
	}

	public List<Shape> copyLinkedShapeList()
	{
		return new ArrayList<Shape>(shapeLinks);
	}

	public Shape getLinkedShape(int index)
	{
		return shapeLinks.get(index);
	}

	public Shape setLinkedShape(int index, Shape o)
	{
		return shapeLinks.set(index, o);
	}

	public boolean linkShape(Shape o)
	{
		return shapeLinks.add(o);
	}

	public boolean unlinkShape(Shape o)
	{
		return shapeLinks.remove(o);
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
		return plateLinks.add(o);
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
		return reagentLinks.add(o);
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
		return screenLinks.add(o);
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
		return plateAcquisitionLinks.add(o);
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
		return wellLinks.add(o);
	}

	public boolean unlinkWell(Well o)
	{
		return wellLinks.remove(o);
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

		// Ensure any base annotations add their Elements first
		Annotation_element = super.asXMLElement(document, Annotation_element);

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
		if (annotator != null)
		{
			// Attribute property Annotator
			Annotation_element.setAttribute("Annotator", annotator.toString());
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
		if (planeLinks != null)
		{
			// *** IGNORING *** Skipped back reference Plane_BackReference
		}
		if (channelLinks != null)
		{
			// *** IGNORING *** Skipped back reference Channel_BackReference
		}
		if (instrumentLinks != null)
		{
			// *** IGNORING *** Skipped back reference Instrument_BackReference
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
		if (objectiveLinks != null)
		{
			// *** IGNORING *** Skipped back reference Objective_BackReference
		}
		if (detectorLinks != null)
		{
			// *** IGNORING *** Skipped back reference Detector_BackReference
		}
		if (filterLinks != null)
		{
			// *** IGNORING *** Skipped back reference Filter_BackReference
		}
		if (dichroicLinks != null)
		{
			// *** IGNORING *** Skipped back reference Dichroic_BackReference
		}
		if (lightPathLinks != null)
		{
			// *** IGNORING *** Skipped back reference LightPath_BackReference
		}
		if (lightSourceLinks != null)
		{
			// *** IGNORING *** Skipped back reference LightSource_BackReference
		}
		if (roiLinks != null)
		{
			// *** IGNORING *** Skipped back reference ROI_BackReference
		}
		if (shapeLinks != null)
		{
			// *** IGNORING *** Skipped back reference Shape_BackReference
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

		return Annotation_element;
	}
}
