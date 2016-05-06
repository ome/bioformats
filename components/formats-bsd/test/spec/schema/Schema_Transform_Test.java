/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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
 * #L%
 */

package spec.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import loci.common.Constants;
import loci.common.services.ServiceFactory;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import ome.specification.OmeValidator;
import ome.specification.XMLMockObjects;
import ome.specification.XMLWriter;
import spec.AbstractTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Collections of tests for the upgrade and downgrade of schema transforms.
 *
 * @author David Gault &nbsp;&nbsp;&nbsp;&nbsp; <a
 *         href="mailto:d.gault@dundee.ac.uk">d.gault@dundee.ac.uk</a>
 */
public class Schema_Transform_Test extends AbstractTest {

    /** Possible file extension. */
    public static final String OME_XML = "ome.xml";

    /** The catalog file to find. */
    private static final String CATALOG = "ome-transforms.xml";

    /** The conversion file to find.*/
    private static final String UNITS_CONVERSION = "units-conversion.xsl";

    /** The <i>name</i> attribute. */
    private static final String CURRENT = "current";

    /** The <i>schema</i> attribute. */
    private static final String SCHEMA = "schema";

    /** The <i>target</i> name. */
    private static final String TARGET = "target";

    /** The <i>transform</i> name. */
    private static final String TRANSFORM = "transform";

    /** The <i>source</i> node. */
    private static final String SOURCE = "source";

    /** The <i>file</i> attribute. */
    private static final String FILE = "file";

    /** Flag indicating to create an image using XML mock and import it.*/
    private static final int IMAGE = 0;

    /** Flag indicating to create a simple image.*/
    private static final int SIMPLE_IMAGE = 1;

    /**
     * Flag indicating to create an image with ROI using XML mock and
     * import it.
     */
    private static final int IMAGE_ROI = 2;

    /**
     * Flag indicating to create an image with annotated acquisition data
     * using XML mock and import it.
     */
    private static final int IMAGE_ANNOTATED_DATA = 2;

    /** The various transforms read from the configuration file.*/
    private Map<String, List<String>> downgrades;

    /** The collection of transforms to perform upgrade.*/
    private Map<String, List<String>> upgrades;

    /** The current schema.*/
    private String currentSchema;

    /** Flag indicating if the platform is Windows or not.*/
    private boolean windowsOS;
    
    private MetadataStore metadata;
    private ServiceFactory factory;
    private OMEXMLService service;
    private IFormatReader reader;

    /**
     * Validates the specified input.
     *
     * @param input
     *            The input to validate
     * @throws Exception
     *             Thrown if an error occurred during the validation
     */
    private void validate(File input) throws Exception {
        OmeValidator validator = new OmeValidator();
        validator.parseFile(input);
    }

    /**
     * Applies the transforms to the specified XML file.
     *
     * @param inputXML
     *            The file to transforms.
     * @param transforms
     *            The collection of transforms.
     * @return See above.
     * @throws Exception
     *             Thrown if an error occurred during the transformations.
     */
    private File applyTransforms(File inputXML, List<InputStream> transforms)
            throws Exception {
        TransformerFactory factory;
        Transformer transformer;
        InputStream stream;
        Iterator<InputStream> i = transforms.iterator();
        File output;
        InputStream in = null;
        OutputStream out = null;
        Resolver resolver = null;
        while (i.hasNext()) {
            stream = i.next();
            try {
                factory = TransformerFactory.newInstance();
                resolver = new Resolver();
                factory.setURIResolver(resolver);
                output = File.createTempFile("tempFileName","."+ OME_XML);
                output.deleteOnExit();
                Source src = new StreamSource(stream);
                Templates template = factory.newTemplates(src);
                transformer = template.newTransformer();
                transformer.setParameter(OutputKeys.ENCODING, Constants.ENCODING);
                out = new FileOutputStream(output);
                in = new FileInputStream(inputXML);
                transformer.transform(new StreamSource(in),
                        new StreamResult(out));
                inputXML = output;
            } catch (Exception e) {
                throw new Exception("Cannot apply transform", e);
            } finally {
                if (stream != null) stream.close();
                if (out != null) out.close();
                if (in != null) in.close();
                if (resolver != null) resolver.close();
            }
        }
        return inputXML;
    }

    /**
     * Overridden to initialize the list.
     *
     * @see AbstractServerTest#setUp()
     */
    @Override
    @BeforeClass
    protected void setUp() throws Exception {
        super.setUp();
        upgrades = new HashMap<String, List<String>>();
        downgrades = currentSchema();
        windowsOS = (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1);
        factory = new ServiceFactory();
        service = factory.getInstance(OMEXMLService.class);
        metadata = service.createOMEXMLMetadata();

        reader = new ImageReader();
        reader.setMetadataStore(metadata);
    }

    /**
     * Overridden to delete the files.
     *
     * @see AbstractServerTest#tearDown()
     */
    @Override
    @AfterClass
    public void tearDown() throws Exception {
        downgrades.clear();
        upgrades.clear();
        reader.close();
    }

    /**
     * Generates an <code>OME-XML</code> file.
     * 
     * @param index The type of image to import. One of the constants defined
     *              by this class.
     * @return The image file.
     * @throws Exception Thrown if an error occurred.
     */
    private File createImageFile(int index)
            throws Exception
    {
        // First create an image
        File f = File.createTempFile("tempFileName","." + OME_XML);
        XMLMockObjects xml = new XMLMockObjects();
        XMLWriter writer = new XMLWriter();
        if (index == IMAGE_ROI) {
            writer.writeFile(f, xml.createImageWithROI(), true);
        } else if (index == IMAGE_ANNOTATED_DATA) {
            writer.writeFile(f, xml.createImageWithAnnotatedAcquisitionData(),
                    true);
        } else {
            writer.writeFile(f, xml.createImageWithAcquisitionData(), true);
        }
        return f;
    }

    /**
     * Parse the target node.
     *
     * @param node The node to parse.
     * @param sheets Hosts the result.
     */
    private void parseTarget(Element node, Map<String, List<String>> sheets)
    {
        Node attribute;
        NamedNodeMap map;
        NodeList transforms;
        map = node.getAttributes();
        String schema = null;
        List<String> list = null;
        for (int j = 0; j < map.getLength(); j++) {
            attribute = map.item(j);
            schema = attribute.getNodeValue();
            transforms = node.getElementsByTagName(TRANSFORM);
            list = new ArrayList<String>();
            for (int i = 0; i < transforms.getLength(); i++) {
                NamedNodeMap m = transforms.item(i).getAttributes();
                for (int k = 0; k < m.getLength(); k++) {
                    attribute = m.item(k);
                    if (FILE.equals(attribute.getNodeName()))
                        list.add(attribute.getNodeValue());
                }
            }
        }
        if (!schema.trim().isEmpty() && !list.isEmpty()) {
            sheets.put(schema, list);
        }
    }

    /**
     * Extracts the value of the current schema.
     *
     * @param schema The current value.
     * @throws Exception Thrown when an error occurred while parsing the file.
     */
    private Map<String, List<String>> extractCurrentSchema(String schema,
            Document document)
        throws Exception
    {
        NodeList list = document.getElementsByTagName(SOURCE);
        Element n;
        Node attribute;
        NamedNodeMap map;
        NodeList t;
        Map<String, List<String>> transforms =
                new HashMap<String, List<String>>();
        Map<String, List<String>> umap;
        for (int i = 0; i < list.getLength(); ++i) {
            n = (Element) list.item(i);
            map = n.getAttributes();
            for (int j = 0; j < map.getLength(); j++) {
                attribute = map.item(j);
                if (SCHEMA.equals(attribute.getNodeName())) {
                    if (schema.equals(attribute.getNodeValue())) {
                        t = n.getElementsByTagName(TARGET);
                        for (int k = 0; k < t.getLength(); k++) {
                            parseTarget((Element) t.item(k), transforms);
                        }
                    } else {
                        NodeList nl = n.getElementsByTagName("upgrades");
                        umap = new HashMap<String, List<String>>();
                        String src = attribute.getNodeValue();
                        for (int k = 0; k < nl.getLength(); k++) {
                            Element node = (Element) nl.item(k);
                            NodeList tt = node.getElementsByTagName(TARGET);
                            for (int l = 0; l < tt.getLength(); l++) {
                                parseTarget((Element) tt.item(l), umap);
                            }
                            //parse the map
                            Iterator<Entry<String, List<String>>> kk = 
                                    umap.entrySet().iterator();
                            Entry<String, List<String>> e;
                            while (kk.hasNext()) {
                                e = kk.next();
                                if (e.getKey().equals(schema)) {
                                    upgrades.put(src, e.getValue());
                                }
                            }
                        }
                    }
                }
            }
        }
        return transforms;
    }

    /**
     * Reads the current schema.
     *
     * @return See above.
     * @throws Exception Thrown if an error occurred while reading.
     */
    private Map<String, List<String>> currentSchema() throws Exception
    {
        InputStream stream = getStream(CATALOG);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(stream);
            currentSchema = doc.getDocumentElement().getAttribute(CURRENT);
            if (currentSchema.trim().isEmpty())
                throw new Exception("No schema specified.");
            return extractCurrentSchema(currentSchema, doc);
        } catch (Exception e) {
            throw new Exception("Unable to parse the catalog.", e);
        } finally {
            if (stream != null) stream.close();
        }
    }

    /**
     * Prepares elements used to perform downgrade or upgrade.
     *
     * @param values The map to create the transform from.
     * @return See above.
     * @throws Exception Thrown if an error occurred.
     */
    private Object[][] createList(Map<String, List<String>> values)
            throws Exception
    {
        List<Target> targets = new ArrayList<Target>();
        Object[][] data = null;
        List<String> l;
        Iterator<String> j;
        Entry<String, List<String>> e;
        Iterator<Entry<String, List<String>>> i = values.entrySet().iterator();
        while (i.hasNext()) {
            e = i.next();
            l = e.getValue();
            List<InputStream> streams = new ArrayList<InputStream>();
            j = l.iterator();
            while (j.hasNext()) {
                streams.add(getStream(j.next()));
            }
            targets.add(new Target(streams, e.getKey()));
        }
        int index = 0;
        Iterator<Target> k = targets.iterator();
        data = new Object[targets.size()][1];
        while (k.hasNext()) {
            data[index][0] = k.next();
            index++;
        }
        return data;
    }
    /**
     * Creates the transformations.
     * @return Object[][] data.
     */
    @DataProvider(name = "createTransform")
    public Object[][] createTransform() throws Exception {
        return createList(downgrades);
    }

    /**
     * Creates the upgrade transformation.
     * @return Object[][] data.
     */
    @DataProvider(name = "createUpgrade")
    public Object[][] createUpgrade() throws Exception {
        return createList(upgrades);
    }

    /**
     * Retrieve the input stream.
     *
     * @param name The name of the stream.
     * @return See above.
     */
    private InputStream getStream(String name)
    {
        if (windowsOS) {
            return this.getClass().getClassLoader().getResourceAsStream(
                    "transforms/"+name);
        }
        return this.getClass().getResourceAsStream("/transforms/"+name);
    }

    /**
     * Returns the list of transformations to generate the file to upgrade.
     *
     * @param target The schema to start from for the upgrade.
     * @return See above.
     */
    private List<InputStream> retrieveDowngrade(String target)
    {
        List<String> list = downgrades.get(target);
        if (list == null || list.isEmpty()) return null;
        List<InputStream> streams = new ArrayList<InputStream>();
        Iterator<String> j = list.iterator();
        while (j.hasNext()) {
            streams.add(getStream(j.next()));
        }
        return streams;
    }

    /**
     * Test the downgrade of an image with acquisition data.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(dataProvider = "createTransform")
    public void testDowngradeImageWithAcquisition(Target target) throws Exception {
        File f = null;
        File transformed = null;
        try {
            f = createImageFile(IMAGE);
            //transform
            transformed = applyTransforms(f, target.getTransforms());
            //validate the file
            validate(transformed);
            reader.setId(transformed.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot downgrade image: "+target.getSource(),
                    e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
        }
    }

    /**
     * Test the downgrade of an image with ROi.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(dataProvider = "createTransform")
    public void testDowngradeImageWithROI(Target target) throws Exception {
        File f = null;
        File transformed = null;
        try {
            f = createImageFile(IMAGE_ROI);
            //transform
            transformed = applyTransforms(f, target.getTransforms());
            //validate the file
            validate(transformed);
            
            reader.setId(transformed.getPath());
            
        } catch (Throwable e) {
            throw new Exception("Cannot downgrade image: "+target.getSource(),
                    e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
        }
    }

    /**
     * Test the downgrade of an image with annotated acquisition data 
     * @throws Exception Thrown if an error occurred.
     */
    @Test(dataProvider = "createTransform")
    public void testDowngradeImageWithAnnotatedAcquisitionData(Target target) throws Exception {
        File f = null;
        File transformed = null;
        try {
            f = createImageFile(IMAGE_ANNOTATED_DATA);
            //transform
            transformed = applyTransforms(f, target.getTransforms());
            //validate the file
            validate(transformed);
            reader.setId(transformed.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot downgrade image: "+target.getSource(),
                    e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
        }
    }
    
    /**
     * Test if an image built with current schema validates.
     * @throws Exception Thrown if an error occurred.
     */
    public void testValidateImageWithAcquisition() throws Exception {
        File f = null;
        try {
            f = createImageFile(IMAGE);
            validate(f);
            reader.setId(f.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot validate the image: ", e);
        } finally {
            if (f != null) f.delete();
        }
    }

    /**
     * Test if an image with ROI built with current schema validates.
     * @throws Exception Thrown if an error occurred.
     */
    public void testValidateImageWithROI() throws Exception {
        File f = null;
        try {
            f = createImageFile(IMAGE_ROI);
            validate(f);
            reader.setId(f.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot validate the image: ", e);
        } finally {
            if (f != null) f.delete();
        }
    }

    /**
     * Test if an image with annotated acquisition data built with current
     * schema validates.
     * @throws Exception Thrown if an error occurred.
     */
    public void testValidateImageWithAnnotatedAcquisition() throws Exception {
        File f = null;
        try {
            f = createImageFile(IMAGE_ANNOTATED_DATA);
            validate(f);
            reader.setId(f.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot validate the image: ", e);
        } finally {
            if (f != null) f.delete();
        }
    }

    /**
     * Test the upgrade of an image with acquisition data.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(dataProvider = "createUpgrade")
    public void testUpgradeImageWithAcquisition(Target target) throws Exception {
        File f = null;
        File transformed = null;
        File upgraded = null;
        try {
            f = createImageFile(IMAGE); //2015 image
            List<InputStream> transforms = retrieveDowngrade(target.getSource());
            //Create file to upgrade
            transformed = applyTransforms(f, transforms);
            //now upgrade the file.
            upgraded = applyTransforms(transformed, target.getTransforms());
            //validate the file
            validate(upgraded);
            reader.setId(upgraded.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot upgrade image: "+target.getSource(),
                    e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
            if (upgraded != null) upgraded.delete();
        }
    }

    /**
     * Test the upgrade of an image with ROI.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(dataProvider = "createUpgrade")
    public void testUpgradeImageWithROI(Target target) throws Exception {
        File f = null;
        File transformed = null;
        File upgraded = null;
        try {
            f = createImageFile(IMAGE_ROI); //2015 image
            List<InputStream> transforms = retrieveDowngrade(target.getSource());
            //Create file to upgrade
            transformed = applyTransforms(f, transforms);
            //now upgrade the file.
            upgraded = applyTransforms(transformed, target.getTransforms());
            //validate the file
            validate(upgraded);
            reader.setId(upgraded.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot upgrade image: "+target.getSource(),
                    e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
            if (upgraded != null) upgraded.delete();
        }
    }

    /**
     * Test the upgrade of an image with annotated acquisition.
     * @throws Exception Thrown if an error occurred.
     */
    @Test(dataProvider = "createUpgrade")
    public void testUpgradeImageWithAnnotatedAcquisition(Target target) throws Exception {
        File f = null;
        File transformed = null;
        File upgraded = null;
        try {
            f = createImageFile(IMAGE_ANNOTATED_DATA); //2015 image
            List<InputStream> transforms = retrieveDowngrade(target.getSource());
            //Create file to upgrade
            transformed = applyTransforms(f, transforms);
            //now upgrade the file.
            upgraded = applyTransforms(transformed, target.getTransforms());
            //validate the file
            validate(upgraded);
            reader.setId(upgraded.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot upgrade image: "+target.getSource(),
                    e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
            if (upgraded != null) upgraded.delete();
        }
    }

    // Path not in ome-transforms but stylesheets are available
    /**
     * Test the upgrade of an image from 2003-FC to 2008-09.
     * @throws Exception Thrown if an error occurred.
     */
    public void testUpgradeImage2003FCto200809() throws Exception
    {
        File f = null;
        File transformed = null;
        File upgraded = null;
        try {
            f = createImageFile(IMAGE); //2015 image
            List<InputStream> transforms = retrieveDowngrade("2003-FC");
            //Create file to upgrade
            transformed = applyTransforms(f, transforms);
            //now upgrade the file to 2008-09
            List<InputStream> upgrades = new ArrayList<InputStream>();
            upgrades.add(getStream("2003-FC-to-2008-09.xsl"));
            upgraded = applyTransforms(transformed, upgrades);
            //validate the file
            validate(upgraded);
            reader.setId(upgraded.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot transform image to 2008-09", e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
            if (upgraded != null) upgraded.delete();
        }
    }

    /**
     * Test the upgrade of an image from 2007-06 to 2008-02.
     * @throws Exception Thrown if an error occurred.
     */
    public void testUpgradeImage200706to200802() throws Exception
    {
        File f = null;
        File transformed = null;
        File upgraded = null;
        try {
            f = createImageFile(IMAGE); //2015 image
            List<InputStream> transforms = retrieveDowngrade("2007-06");
            //Create file to upgrade
            transformed = applyTransforms(f, transforms);
            //now upgrade the file to 2008-02
            List<InputStream> upgrades = new ArrayList<InputStream>();
            upgrades.add(getStream("2007-06-to-2008-02.xsl"));
            upgraded = applyTransforms(transformed, upgrades);
            //validate the file
            validate(upgraded);
            reader.setId(upgraded.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot transform image to 2008-02", e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
            if (upgraded != null) upgraded.delete();
        }
    }

    /**
     * Test the upgrade of an image from 2007-06 to 2008-09.
     * @throws Exception Thrown if an error occurred.
     */
    public void testUpgradeImage200706to200809() throws Exception
    {
        File f = null;
        File transformed = null;
        File upgraded = null;
        try {
            f = createImageFile(IMAGE); //2015 image
            List<InputStream> transforms = retrieveDowngrade("2007-06");
            //Create file to upgrade
            transformed = applyTransforms(f, transforms);
            //now upgrade the file to 2008-09
            List<InputStream> upgrades = new ArrayList<InputStream>();
            upgrades.add(getStream("2007-06-to-2008-09.xsl"));
            upgraded = applyTransforms(transformed, upgrades);
            //validate the file
            validate(upgraded);
            reader.setId(upgraded.getPath());
        } catch (Throwable e) {
            throw new Exception("Cannot transform image to 2008-09", e);
        } finally {
            if (f != null) f.delete();
            if (transformed != null) transformed.delete();
            if (upgraded != null) upgraded.delete();
        }
    }

    class Target {

        /** The transforms to apply.*/
        private List<InputStream> transforms;

        /** The source schema.*/
        private String source;

        /**
         * Creates a new instance.
         *
         * @param transforms The transforms to apply.
         * @param source The source schema.
         */
        Target(List<InputStream> transforms, String source)
        {
            this.transforms = transforms;
            this.source = source;
        }

        /**
         * Returns the transforms to apply.
         *
         * @return See above.
         */
        List<InputStream> getTransforms() { return transforms; }

        /**
         * Returns the source schema.
         *
         * @return See above.
         */
        String getSource() { return source; }

    }

    class Resolver implements URIResolver {

        /** The stream.*/
        private InputStream stream;

        /** Close the input stream if not <code>null</code>.*/
        public void close()
            throws Exception
        {
            if (stream != null) stream.close();
        }

        @Override
        public Source resolve(String href, String base)
                throws TransformerException {
            stream = getStream(UNITS_CONVERSION);
            return new StreamSource(stream);
        }
    }
}
