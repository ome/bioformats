/*
 * #%L
 * The OME Data Model specification
 * %%
 * Copyright (C) 2003 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
/*
 * $Id$
 *
 *   Copyright 2006-2014 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.specification;

import java.io.InputStream;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * A resolver for Schema locations that pulls them from jar resources.
 *
 * @author Andrew Patterson &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:ajpatterson@lifesci.dundee.ac.uk">ajpatterson@lifesci.dundee.ac.uk</a>
 * @version 1.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
public class SchemaResolver implements LSResourceResolver
{
    private DOMImplementationLS theDOMImplementationLS;

    // the static string to strip when mapping schema locations
    private static String GIT_MASTER_PATH  = "http://git.openmicroscopy.org/src/master/components/specification";
    private static String GIT_DEVELOP_PATH = "http://git.openmicroscopy.org/src/develop/components/specification";
    private static String MAIN_PATH = "http://www.openmicroscopy.org/Schemas/";
    private static String MAIN_SEARCH_PATH = "/released-schema/";
    private static String LEGACY_AC_PATH = "http://www.openmicroscopy.org/XMLschemas/AnalysisChain/RC1/";
    private static String LEGACY_AM_PATH = "http://www.openmicroscopy.org/XMLschemas/AnalysisModule/RC1/";
    private static String LEGACY_BF_PATH = "http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/";
    private static String LEGACY_CA_PATH = "http://www.openmicroscopy.org/XMLschemas/CA/RC1/";
    private static String LEGACY_CL_PATH = "http://www.openmicroscopy.org/XMLschemas/CLI/RC1/";
    private static String LEGACY_DH_PATH = "http://www.openmicroscopy.org/XMLschemas/DataHistory/IR3/";
    private static String LEGACY_ML_PATH = "http://www.openmicroscopy.org/XMLschemas/MLI/IR2/";
    private static String LEGACY_OM_PATH = "http://www.openmicroscopy.org/XMLschemas/OME/FC/";
    private static String LEGACY_ST_PATH = "http://www.openmicroscopy.org/XMLschemas/STD/RC2/";
    private static String LEGACY_SEARCH_PATH = "/released-schema/2003-FC/";

    public SchemaResolver() throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        // Create the objects necessary to make the correct LSInput return types
        theDOMImplementationLS = null;
        
        // if version 7 ?
        try {
        DOMImplementationRegistry theDOMImplementationRegistryA =
            DOMImplementationRegistry.newInstance();
        theDOMImplementationLS =
            (DOMImplementationLS) theDOMImplementationRegistryA.getDOMImplementation("XML 3.0 LS 3.0");
        } catch  (Exception e) {
            // theDOMImplementationLS should still be null
        }

        // if version 6 ?
        if (theDOMImplementationLS == null) {
          try {
            DOMImplementationRegistry theDOMImplementationRegistryB =
                DOMImplementationRegistry.newInstance();
            theDOMImplementationLS =
                (DOMImplementationLS) theDOMImplementationRegistryB.getDOMImplementation("LS");
          } catch (Exception e) {
            // theDOMImplementationLS should still be null
          }
        }

        // if verson 4 & 5 ?
        if (theDOMImplementationLS == null) {
          try {
            System.setProperty(
                DOMImplementationRegistry.PROPERTY,
                "org.apache.xerces.dom.DOMImplementationSourceImpl");
            DOMImplementationRegistry theDOMImplementationRegistryC =
                DOMImplementationRegistry.newInstance();
            theDOMImplementationLS =
                (DOMImplementationLS) theDOMImplementationRegistryC.getDOMImplementation("LS");
          } catch (Exception e) {
            // theDOMImplementationLS should still be null
          }
        }
        if (theDOMImplementationLS == null) {
          throw new InstantiationException("SchemaResolver could not create suitable DOMImplementationLS");
        }
    }


    /**
     * Resolves known namespace locations to their appropriate jar resource
     *
     * @param type Not used by function.
     * @param namespaceURI Not used by function.
     * @param publicId Not used by function.
     * @param systemId The schema location that will be used to choose the resource to return.
     * @param baseURI Not used by function.
     * @return The requested resource.
     */
     public LSInput  resolveResource(
        String type, String namespaceURI, String publicId,
        String systemId, String baseURI)
    {
        LSInput theResult = null;

        // Match the requested schema locations and create the appropriate LSInput object
        if (systemId.equals("http://www.w3.org/2001/xml.xsd"))
        {
            theResult = makeSubstutionStream("/released-schema/additions/jar/xml.xsd", systemId);
        }
        else if (systemId.startsWith(GIT_MASTER_PATH))
        {
            theResult = makeSubstutionStream(systemId.substring(GIT_MASTER_PATH.length()), systemId);
        }
        else if (systemId.startsWith(GIT_DEVELOP_PATH))
        {
            theResult = makeSubstutionStream(systemId.substring(GIT_DEVELOP_PATH.length()), systemId);
        }
        else if (systemId.startsWith(MAIN_PATH))
        {
            theResult = makeSubstutionStream(MAIN_SEARCH_PATH + systemId.substring(MAIN_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_AC_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_AC_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_AM_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_AM_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_BF_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_BF_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_CA_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_CA_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_CL_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_CL_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_DH_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_DH_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_ML_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_ML_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_OM_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_OM_PATH.length()), systemId);
        }
        else if (systemId.startsWith(LEGACY_ST_PATH))
        {
            theResult = makeSubstutionStream(LEGACY_SEARCH_PATH + systemId.substring(LEGACY_ST_PATH.length()), systemId);
        }
        else
        {
            throw new RuntimeException("SchemaResolver does not know path to resolve: [" + systemId + "] from OME specification jar.");
        }

        return theResult;
    }

    /**
     * Creates the LSInput object from the resource path
     *
     * @param theResourcePath Path to the schema in the Specification jar.
     * @param systemId
     * @return The requested LSInput object.
     */
    private LSInput makeSubstutionStream(
        String theResourcePath, String systemId)
    {
        LSInput theResult = null;
        theResult = theDOMImplementationLS.createLSInput();
        InputStream theResourcesStream = getClass().getResourceAsStream(theResourcePath);
        theResult.setByteStream(theResourcesStream);
        theResult.setSystemId(systemId);
        return theResult;
    }
}
