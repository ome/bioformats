/*
 * ome.xml.r200706.ome.ObjectiveNode
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2007 Open Microscopy Environment
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
 * Created by curtis via xsd-fu on 2008-10-16 06:18:36-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r200706.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r200706.ome.*;
import ome.xml.r200706.spw.*;

import org.w3c.dom.Element;

public class ObjectiveNode extends ManufactSpecNode
{

	// -- Constructors --

	/** Constructs a Objective node with an associated DOM element. */
	public ObjectiveNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Objective node with an associated DOM element beneath
	 * a given parent.
	 */
	public ObjectiveNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Objective node with an associated DOM element beneath
	 * a given parent.
	 */
	public ObjectiveNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Objective", attach));
	}

	// -- Objective API methods --

	// Element which is not complex (has only a text node)
	public Double getWorkingDistance()
	{
		return getDoubleCData("WorkingDistance");
	}

	public void setWorkingDistance(Double workingDistance)
	{
		setCData("WorkingDistance", workingDistance);
	}

	// Virtual, inferred back reference ObjectiveSettings_BackReference
	public int getReferringObjectiveSettingsCount()
	{
		return getReferringCount("ObjectiveSettings");
	}

	public java.util.List getReferringObjectiveSettingsList()
	{
		return getReferringNodes("ObjectiveSettings");
	}

	// Element which is not complex (has only a text node)
	public String getImmersion()
	{
		return getStringCData("Immersion");
	}

	public void setImmersion(String immersion)
	{
		setCData("Immersion", immersion);
	}

	// Element which is not complex (has only a text node)
	public String getCorrection()
	{
		return getStringCData("Correction");
	}

	public void setCorrection(String correction)
	{
		setCData("Correction", correction);
	}

	// Virtual, inferred back reference OTF_BackReference
	public int getReferringOTFCount()
	{
		return getReferringCount("OTF");
	}

	public java.util.List getReferringOTFList()
	{
		return getReferringNodes("OTF");
	}

	// Element which is not complex (has only a text node)
	public Double getLensNA()
	{
		return getDoubleCData("LensNA");
	}

	public void setLensNA(Double lensNA)
	{
		setCData("LensNA", lensNA);
	}

	// Element which is not complex (has only a text node)
	public Integer getNominalMagnification()
	{
		return getIntegerCData("NominalMagnification");
	}

	public void setNominalMagnification(Integer nominalMagnification)
	{
		setCData("NominalMagnification", nominalMagnification);
	}

	// Element which is not complex (has only a text node)
	public Double getCalibratedMagnification()
	{
		return getDoubleCData("CalibratedMagnification");
	}

	public void setCalibratedMagnification(Double calibratedMagnification)
	{
		setCData("CalibratedMagnification", calibratedMagnification);
	}

	// *** WARNING *** Unhandled or skipped property ID

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}
