/*
 * $Id$
 *
 *   Copyright 2010 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package coverage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;

import junit.framework.TestCase;
import ome.formats.OMEROMetadataStoreClient;
import ome.formats.importer.ImportConfig;
import ome.formats.importer.ImportContainer;
import ome.formats.importer.ImportLibrary;
import ome.formats.importer.OMEROWrapper;
import omero.ApiUsageException;
import omero.ServerError;
import omero.api.IAdminPrx;
import omero.api.IDeletePrx;
import omero.api.IQueryPrx;
import omero.api.IUpdatePrx;
import omero.api.ServiceFactoryPrx;
import omero.api.delete.DeleteCommand;
import omero.api.delete.DeleteHandlePrx;
import omero.api.delete.DeleteReport;
import omero.grid.DeleteCallbackI;
import omero.model.ChannelBinding;
import omero.model.Experiment;
import omero.model.Experimenter;
import omero.model.ExperimenterGroup;
import omero.model.ExperimenterGroupI;
import omero.model.ExperimenterI;
import omero.model.IObject;
import omero.model.Image;
import omero.model.Permissions;
import omero.model.PermissionsI;
import omero.model.Pixels;
import omero.model.QuantumDef;
import omero.model.RenderingDef;
import omero.model.Well;
import omero.model.WellSample;
import omero.sys.EventContext;
import omero.sys.ParametersI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

//Application-internal dependencies
import integration.SchemaResolver;

/**
 * Base test for integration tests.
 *
 * @since Beta4.2
 */
@Test(groups = { "client", "integration", "blitz" })
public class AbstractTest
	extends TestCase
{

	/** Path the schema language. */
	public static final String JAXP_SCHEMA_LANGUAGE = 
		"http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	/** W3C. */
	public static final String W3C_XML_SCHEMA = 
		"http://www.w3.org/2001/XMLSchema";

	/** The source. */
	public static final String JAXP_SCHEMA_SOURCE = 
		"http://java.sun.com/xml/jaxp/properties/schemaSource";

    /** The OME-XML format. */
    public static final String OME_FORMAT = "ome";
    
    /** The OME-XML format. */
    public static final String OME_XML_FORMAT = "ome.xml";

    /** Identifies the <code>system</code> group. */
	public String SYSTEM_GROUP = "system";
	
	/** Identifies the <code>user</code> group. */
	public String USER_GROUP = "user";
	
	/** Identifies the <code>guest</code> group. */
	public String GUEST_GROUP = "guest";
	
	/** Holds the error, info, warning. */
	protected Log log = LogFactory.getLog(getClass());

	/** The client object, this is the entry point to the Server. */
    protected omero.client client;

    /** A root-client object. */
    protected omero.client root;

    /** Helper reference to the <code>Service factory</code>. */
    protected ServiceFactoryPrx factory;

    /** Helper reference to the <code>IQuery</code> service. */
    protected IQueryPrx iQuery;

    /** Helper reference to the <code>IUpdate</code> service. */
    protected IUpdatePrx iUpdate;

    /** Helper reference to the <code>IAdmin</code> service. */
    protected IAdminPrx iAdmin;
    
    /** Helper reference to the <code>IDelete</code> service. */
    protected IDeletePrx iDelete;

    /** Reference to the importer store. */
    protected OMEROMetadataStoreClient importer;

    /** Helper class creating mock object. */
    protected ModelMockFactory mmFactory;
    
    protected String rootpass;

    /**
     * {@link omero.client} instances which are created via the newUser* methods.
     * These will be forcifully closed at the end of the test. "new omero.client(...)"
     * should be strictly avoided except for in the method {@link #newOmeroClient()}.
     *
     * @see #newUserAndGroup(Permissions)
     * @see #newUserAndGroup(String)
     * @see #newUserInGroup()
     * @see #newUserInGroup(EventContext)
     * @see #newUserInGroup(ExperimenterGroup)
     */
    private final Set<omero.client> clients = new HashSet<omero.client>();

    /**
     * Sole location where {@link omero.client#client()} 
     * or any other {@link omero.client}
     * constructor should be called.
     */
    protected omero.client newOmeroClient()
    {
        omero.client client = new omero.client(); // OK
        clients.add(client);
        return client;
    }

    /**
     * Creates a client for the root user.
     * 
     * @return See above.
     * @throws Exception Thrown if an error occurred.
     */
    protected omero.client newRootOmeroClient() 
    	throws Exception
    {
        omero.client client = newOmeroClient();
        client.createSession("root", rootpass);
        return client;
    }

    /**
     * Calculates a SHA-1 digest.
     * @param data Data to calcluate a SHA-1 digest for.
     * @param offset Offset within the byte buffer to calculate from.
     * @param len Number of bytes from <code>offset</code> to calculate with.
     * @return Hex string of the SHA-1 digest.
     */
    protected String sha1(byte[] data, int offset, int len)
    {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "Required SHA-1 message digest algorithm unavailable.");
        }
        md.update(data, offset, len);
        return byteArrayToHexString(md.digest());
    }

    /**
     * Calculates a SHA-1 digest.
     * @param data Data to calcluate a SHA-1 digest for.
     * @return Hex string of the SHA-1 digest.
     */
    protected String sha1(byte[] data)
    {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    "Required SHA-1 message digest algorithm unavailable.");
        }
        md.update(data);
        return byteArrayToHexString(md.digest());
    }

    /**
     * Converts a byte array to a hex string.
     * @param in Byte array to convert.
     * @return Hex string representing the byte array.
     */
    public static String byteArrayToHexString(byte in[]) {

        byte ch = 0x00;
        int i = 0;

        if (in == null || in.length <= 0) {
            return null;
        }

        String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a", "b", "c", "d", "e", "f" };

        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0);
            ch = (byte) (ch >>> 4);
            ch = (byte) (ch & 0x0F);
            out.append(pseudo[ch]);
            ch = (byte) (in[i] & 0x0F);
            out.append(pseudo[ch]);
            i++;
        }

        String rslt = new String(out);
        return rslt;
    }

    /**
     * Initializes the various services.
     * @throws Exception Thrown if an error occurred.
     */
    @Override
    @BeforeClass
    protected void setUp() 
    	throws Exception
    {
        // administrator client
        omero.client tmp = newOmeroClient();
        rootpass = tmp.getProperty("omero.rootpass");
        root = newRootOmeroClient();
        tmp.__del__();

        newUserAndGroup("rw----");
    }

    /**
     * Closes the session.
     * @throws Exception Thrown if an error occurred.
     */
    @Override
    @AfterClass
    public void tearDown() 
    	throws Exception
    {
        for (omero.client c : clients) {
            if (c != null) {
                c.__del__();
            }
        }
    }

    /**
     * Creates a new group and experimenter and returns the event context.
     * 
     * @param perms The permissions level.
     * @return See above.
     * @throws Exception Thrown if an error occurred.
     */
    protected EventContext newUserAndGroup(String perms) 
    	throws Exception
    {
        return newUserAndGroup(new PermissionsI(perms));
    }

    /**
     * Creates a new group and experimenter and returns the event context.
     * 
     * @param perms The permissions level.
     * @return See above.
     * @throws Exception Thrown if an error occurred.
     */
    protected EventContext newUserAndGroup(Permissions perms) 
    	throws Exception
    {
        IAdminPrx rootAdmin = root.getSession().getAdminService();
        String uuid = UUID.randomUUID().toString();
        ExperimenterGroup g = new ExperimenterGroupI();
        g.setName(omero.rtypes.rstring(uuid));
        g.getDetails().setPermissions(perms);
        g = new ExperimenterGroupI(rootAdmin.createGroup(g), false);
        return newUserInGroup(g);
    }

    /**
     * Changes the permissions of the group.
     * 
     * @param perms The permissions level.
     * @param groupId The identifier of the group to handle.
     * @throws Exception Thrown if an error occurred.
     */
    protected void resetGroupPerms(String perms, long groupId)
    	throws Exception
    {
    	 IAdminPrx rootAdmin = root.getSession().getAdminService();
    	 ExperimenterGroup g = rootAdmin.getGroup(groupId);
    	 g.getDetails().setPermissions(new PermissionsI(perms));
    	 rootAdmin.updateGroup(g);
    }
    
    /**
     * Creates a new user in the current group.
     * @return
     */
    protected EventContext newUserInGroup() throws Exception {
        EventContext ec = 
        	client.getSession().getAdminService().getEventContext();
        return newUserInGroup(ec);
    }

    /**
     * Takes the {@link EventContext} from another user and creates a new user
     * in the same group as that user is currently logged in to.
     * 
     * @param previousUser The context of the previous user.
     * @throws Exception Thrown if an error occurred.
     */
    protected EventContext newUserInGroup(EventContext previousUser)
    throws Exception
    {
        ExperimenterGroup eg = new ExperimenterGroupI(previousUser.groupId, 
        		false);
        return newUserInGroup(eg);
    }
    
    /**
     * Creates a new user in the specified group.
     * 
     * @param group The group to add the user to.
     * @return The context.
     * @throws Exception Thrown if an error occurred.
     */
    protected EventContext newUserInGroup(ExperimenterGroup group)
    	throws Exception
    {
        
        IAdminPrx rootAdmin = root.getSession().getAdminService();
        group = rootAdmin.getGroup(group.getId().getValue());

        String uuid = UUID.randomUUID().toString();
        Experimenter e = new ExperimenterI();
        e.setOmeName(omero.rtypes.rstring(uuid));
        e.setFirstName(omero.rtypes.rstring("integeration"));
        e.setLastName(omero.rtypes.rstring("tester"));
        long id = rootAdmin.createUser(e, group.getName().getValue());
        e = rootAdmin.getExperimenter(id);
        rootAdmin.addGroups(e, Arrays.asList(group));
        omero.client client = newOmeroClient();
        client.createSession(uuid, uuid);
        return init(client);
    }

    /**
     * Logs in the user.
     * 
     * @param ownerEc The context of the user.
     * @throws Exception Thrown if an error occurred.
     */
    protected void loginUser(EventContext ownerEc) 
    	throws Exception
    {
        omero.client client = newOmeroClient();
        client.createSession(ownerEc.userName, "dummy");
        init(client);
    }

    /**
     * Creates a new {@link omero.client} for root based on the current group.
     */
    protected void logRootIntoGroup() throws Exception {
        EventContext ec = iAdmin.getEventContext();
        logRootIntoGroup(ec);
    }

    /**
     * Creates a new {@link omero.client} for root based on the {@link EventContext}
     */
    protected void logRootIntoGroup(EventContext ec) throws Exception {
        omero.client rootClient = newRootOmeroClient();
        rootClient.getSession().setSecurityContext(new ExperimenterGroupI(
        		ec.groupId, false));
        init(rootClient);
    }

    /**
     * Makes the current user an owner of the current group.
     */
    protected void makeGroupOwner() throws Exception {
        EventContext ec = client.getSession().getAdminService().getEventContext();
        IAdminPrx rootAdmin = root.getSession().getAdminService();
        rootAdmin.setGroupOwner(new ExperimenterGroupI(ec.groupId, false),
                new ExperimenterI(ec.userId, false));

        disconnect();
        init(ec); // Create new session with the added privileges
    }

    /**
     * Saves the current client before calling {@link #clean()} and returns
     * it to the user.
     */
    protected omero.client disconnect() throws Exception {
        omero.client oldClient = client;
        client = null;
        clean();
        return oldClient;
    }

    /**
     * If {@link #client} is non-null, destroys the client and nulls all
     * fields which were set on creation.
     */
    protected void clean() throws Exception {
        if (client != null) {
            client.__del__();
        }
        client = null;
        factory = null;
        iQuery = null;
        iUpdate = null;
        iAdmin = null;
        iDelete = null;
        mmFactory = null;
        importer = null;
    }

    /**
     */
    protected EventContext init(EventContext ec) throws Exception {
        omero.client c = newOmeroClient();
        c.createSession(ec.userName, "");
        return init(c);
    }

    /**
     * Resets the client and return the event context.
     * 
     * @param client The client to handle.
     * @return The event context to handle.
     * @throws Exception
     */
    protected EventContext init(omero.client client) throws Exception {

        clean();
        
        this.client = client;
        factory = client.getSession();
        iQuery = factory.getQueryService();
        iUpdate = factory.getUpdateService();
        iAdmin = factory.getAdminService();
        iDelete = factory.getDeleteService();
        mmFactory = new ModelMockFactory(factory.getPixelsService());

        importer = new OMEROMetadataStoreClient();
        importer.initialize(factory);

        return iAdmin.getEventContext();
    }
    
    /**
     * Compares the passed rendering definitions.
     * 
     * @param def1 The first rendering definition to handle.
     * @param def2 The second rendering definition to handle.
     * @throws Exception Thrown if an error occurred.
     */
    protected void compareRenderingDef(RenderingDef def1, RenderingDef def2)
		throws Exception 
	{
		assertNotNull(def1);
		assertNotNull(def2);
		assertTrue(def1.getDefaultZ().getValue() == 
				def2.getDefaultZ().getValue());
		assertTrue(def1.getDefaultT().getValue() == 
			def2.getDefaultT().getValue());
		assertTrue(def1.getModel().getValue().getValue().equals( 
			def2.getModel().getValue().getValue()));
		QuantumDef q1 = def1.getQuantization();
		QuantumDef q2 = def2.getQuantization();
		assertNotNull(q1);
		assertNotNull(q2);
		assertTrue(q1.getBitResolution().getValue() == 
			q2.getBitResolution().getValue());
		assertTrue(q1.getCdStart().getValue() == 
			q2.getCdStart().getValue());
		assertTrue(q1.getCdEnd().getValue() == 
			q2.getCdEnd().getValue());
		List<ChannelBinding> channels1 = def1.copyWaveRendering();
		List<ChannelBinding> channels2 = def2.copyWaveRendering();
		assertNotNull(channels1);
		assertNotNull(channels2);
		assertTrue(channels1.size() == channels2.size());
		Iterator<ChannelBinding> i = channels1.iterator();
		ChannelBinding c1, c2;
		int index = 0;
		while (i.hasNext()) {
			c1 = i.next();
			c2 = channels2.get(index);
			assertTrue(c1.getAlpha().getValue() == c2.getAlpha().getValue());
			assertTrue(c1.getRed().getValue() == c2.getRed().getValue());
			assertTrue(c1.getGreen().getValue() == c2.getGreen().getValue());
			assertTrue(c1.getBlue().getValue() == c2.getBlue().getValue());
			assertTrue(c1.getCoefficient().getValue() 
					== c2.getCoefficient().getValue());
			assertTrue(c1.getFamily().getValue().getValue().equals(
					c2.getFamily().getValue().getValue()) );
			assertTrue(c1.getInputStart().getValue() == 
				c2.getInputStart().getValue());
			assertTrue(c1.getInputEnd().getValue() == 
				c2.getInputEnd().getValue());
			Boolean b1 = Boolean.valueOf(c1.getActive().getValue());
			Boolean b2 = Boolean.valueOf(c2.getActive().getValue());
			assertTrue(b1.equals(b2));
			b1 = Boolean.valueOf(c1.getNoiseReduction().getValue());
			b2 = Boolean.valueOf(c2.getNoiseReduction().getValue());
			assertTrue(b1.equals(b2));
		}
	}

	/**
	 * Helper method to the wells the wells.
	 * 
	 * @param plateID The identifier of the plate.
	 * @param pixels  Pass <code>true</code> to load the pixels, 
	 * 					<code>false</code> otherwise.
	 * @return See above.
	 * @throws Exception  Thrown if an error occurred.
	 */
    @SuppressWarnings("unchecked")
	protected List<Well> loadWells(long plateID, boolean pixels)
		throws Exception 
	{
		StringBuilder sb = new StringBuilder();
		ParametersI param = new ParametersI();
		param.addLong("plateID", plateID);
		sb.append("select well from Well as well ");
		sb.append("left outer join fetch well.plate as pt ");
		sb.append("left outer join fetch well.wellSamples as ws ");
		sb.append("left outer join fetch ws.plateAcquisition as pa ");
		sb.append("left outer join fetch ws.image as img ");
		if (pixels) {
			sb.append("left outer join fetch img.pixels as pix ");
	        sb.append("left outer join fetch pix.pixelsType as pixType ");
		}
        sb.append("where pt.id = :plateID");
        return (List<Well>) (List<?>) 
        	iQuery.findAllByQuery(sb.toString(), param);
	}

    /**
     * Helper method to load a well sample with its well and plate intact
     * (and possibly a screen if one exists) for the given pixels.
     * @param p
     * @return
     * @throws ServerError
     */
    protected WellSample getWellSample(Pixels p) throws ServerError {
        long id = p.getImage().getId().getValue();
        String sql = "select ws from WellSample as ws ";
        sql += "join fetch ws.well as w ";
        sql += "left outer join fetch ws.plateAcquisition as pa ";
        sql += "join fetch w.plate as p ";
        sql += "left outer join fetch p.screenLinks sl ";
        sql += "left outer join fetch sl.parent s ";
        sql += "where ws.image.id = :id";
        ParametersI param = new ParametersI();
        param.addId(id);
        List<IObject> results = iQuery.findAllByQuery(sql, param);
        assertTrue(results.size() == 1);
        WellSample ws = (WellSample) results.get(0);
        assertNotNull(ws);
        return ws;
    }

    /**
     * Helper method to load the Experiment which is is associated
     * with the pixels argument via Image.
     * @param p
     * @return
     * @throws ServerError
     */
    protected Experiment getExperiment(Pixels p) throws ServerError {
        long id = p.getImage().getId().getValue();
        String sql = "select e from Image i ";
        sql += "join i.experiment e ";
        sql += "where i.id = :id";
        ParametersI param = new ParametersI();
        param.addId(id);
        List<IObject> results = iQuery.findAllByQuery(sql, param);
        assertTrue(results.size() == 1);
        Experiment e = (Experiment) results.get(0);
        assertNotNull(e);
        return e;
    }

    /**
     * Makes sure that the passed object exists.
     * 
     * @param obj The object to handle.
     *  @throws Exception  Thrown if an error occurred.
     */
    protected void assertExists(IObject obj)
    	throws Exception
    {
    	IObject copy = iQuery.find(
    			obj.getClass().getSimpleName(), obj.getId().getValue());
    	assertNotNull(String.format("%s:%s",
    			obj.getClass().getName(), obj.getId().getValue())
    			+ " is missing!", copy);
    }

    protected void assertAllExist(IObject...obj) throws Exception
    {
        for (IObject iObject : obj) {
            assertExists(iObject);
        }
    }

    /**
     * Makes sure that the passed object does not exist.
     * 
     * @param obj The object to handle.
     *  @throws Exception  Thrown if an error occurred.
     */
    protected void assertDoesNotExist(IObject obj)
    	throws Exception
    {
    	IObject copy = iQuery.find(
    			obj.getClass().getSimpleName(), obj.getId().getValue());
    	assertNull(String.format("%s:%s",
    			obj.getClass().getName(), obj.getId().getValue())
    			+ " still exists!", copy);
    }

    protected void assertNoneExist(IObject... obj) throws Exception
    {
        for (IObject iObject : obj) {
            assertDoesNotExist(iObject);
        }
    }


    /**
     * Imports the specified OME-XML file and returns the pixels set
     * if successfully imported.
     *
     * @param file The file to import.
     * @param format The format of the file to import.
     * @return The collection of imported pixels set.
     * @throws Exception Thrown if an error occurred while encoding the image.
     */
    protected List<Pixels> importFile(File file, String format)
        throws Throwable
    {
        return importFile(importer, file, format, false);
    }

    /**
     * Imports the specified OME-XML file and returns the pixels set
     * if successfully imported.
     *
     * @param file The file to import.
     * @param format The format of the file to import.
     * @return The collection of imported pixels set.
     * @throws Throwable Thrown if an error occurred while encoding the image.
     */
    protected List<Pixels> importFile(File file, String format, boolean metadata)
        throws Throwable
    {
        return importFile(importer, file, format, metadata);
    }

    /**
	 * Imports the specified OME-XML file and returns the pixels set
	 * if successfully imported.
	 * 
	 * @param importer The metadataStore to use.
	 * @param file The file to import.
	 * @param format The format of the file to import.
	 * @return The collection of imported pixels set.
	 * @throws Throwable Thrown if an error occurred while encoding the image.
	 */
	protected List<Pixels> importFile(OMEROMetadataStoreClient importer,
			File file, String format)
		throws Throwable
	{
		return importFile(importer, file, format, false);
	}
	
	/**
	 * Imports the specified OME-XML file and returns the pixels set
	 * if successfully imported.
	 * 
	 * @param importer The metadataStore to use.
	 * @param file The file to import.
	 * @param format The format of the file to import.
	 * @param metadata Pass <code>true</code> to only import the metadata,
	 *                 <code>false</code> otherwise.
	 * @return The collection of imported pixels set.
	 * @throws Throwable Thrown if an error occurred while encoding the image.
	 */
	protected List<Pixels> importFile(OMEROMetadataStoreClient importer,
			File file, String format, boolean metadata)
		throws Throwable
	{
		ImportLibrary library = new ImportLibrary(importer, 
				new OMEROWrapper(new ImportConfig()));
		library.setMetadataOnly(metadata);
		ImportContainer container = new ImportContainer(
                file, null, null, false, null, null, null, null);
		container.setUseMetadataFile(true);
		container.setCustomImageName(format);
		List<Pixels> pixels = library.importImage(container, 0, 0, 1);
		assertNotNull(pixels);
		assertTrue(pixels.size() > 0);
		return pixels;
	} 
	
    protected String delete(omero.client c, DeleteCommand...dc)
    throws ApiUsageException, ServerError,
    InterruptedException
    {
        return delete(true, c.getSession().getDeleteService(), c, dc);
    }

    /**
     * Basic asynchronous delete command. Used in order to reduce the number
     * of places that we do the same thing in case the API changes.
     *
     * @param dc The command to handle.
     * @throws ApiUsageException
     * @throws ServerError
     * @throws InterruptedException
     */
    protected String delete(IDeletePrx proxy, omero.client c, DeleteCommand...dc)
    throws ApiUsageException, ServerError,
    InterruptedException
    {
        return delete(true, proxy, c, dc);
    }

    /**
     * Basic asynchronous delete command. Used in order to reduce the number
     * of places that we do the same thing in case the API changes.
     * 
     * @param passes Pass <code>true</code> to indicate that no error
     *               found in report, <code>false</code> otherwise.
     * @param dc The command to handle.
     * @param strict whether or not the method should succeed.
     * @throws ApiUsageException
     * @throws ServerError
     * @throws InterruptedException
     */
    protected String delete(boolean passes, IDeletePrx proxy, omero.client c, 
            DeleteCommand...dc)
    throws ApiUsageException, ServerError,
    InterruptedException
    {

        DeleteHandlePrx handle = proxy.queueDelete(dc);
        DeleteCallbackI cb = new DeleteCallbackI(c, handle);
        int count = 10 * dc.length;
        while (null == cb.block(500)) {
            count--;
            if (count == 0) {
                throw new RuntimeException("Waiting on delete timed out");
            }
        }
        StringBuilder sb = new StringBuilder();
        for (DeleteReport report : handle.report()) {
            if (report.error != null && report.error.length() > 0) {
                sb.append(report.error);
            } else {
                sb.append(report.warning);
            }
        }
        String report = sb.toString();
        if (passes) {
            assertEquals(report, 0, handle.errors());
        } else {
            assertTrue(report, 0 < handle.errors());
        }
        return report;
    }

    /**
     * Asynchronous command for a single delete, this means a single 
     * report is returned for testing. 
     * 
     * @param dc The SINGLE command to handle.
     * @throws ApiUsageException
     * @throws ServerError
     * @throws InterruptedException
     */
    protected DeleteReport singleDeleteWithReport(IDeletePrx proxy, omero.client c, DeleteCommand dc)
    throws ApiUsageException, ServerError,
    InterruptedException
    {
        return deleteWithReports(proxy, c, dc)[0];
    }
    /**
     * Asynchronous command for delete, report array is returned. 
     * 
     * @param dc The command to handle.
     * @throws ApiUsageException
     * @throws ServerError
     * @throws InterruptedException
     */
    private DeleteReport[] deleteWithReports(IDeletePrx proxy, omero.client c, 
    		DeleteCommand...dc)
    throws ApiUsageException, ServerError,
    InterruptedException
    {
        DeleteHandlePrx handle = proxy.queueDelete(dc);
        DeleteCallbackI cb = new DeleteCallbackI(c, handle);
        int count = 10 * dc.length;
        while (null == cb.block(500)) {
            count--;
            if (count == 0) {
                throw new RuntimeException("Waiting on delete timed out");
            }
        }
        if (handle.errors() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Errors during delete!\n");
            DeleteReport[] reports = handle.report();
            for (DeleteReport report : reports) {
                if (report.error.length() > 0) {
                    sb.append(report.error);
                    sb.append("\n");
                } else if (report.warning.length() > 0) {
                    sb.append(report.warning);
                    sb.append("\n");
                }
            }
            fail(sb.toString());
        }
        return handle.report();
    }
    
    /**
     * Transforms the input file using the specified stylesheet file.
     * 
     * @param input  The file to transform.
     * @param output The destination file.
     * @param xslt   The stylesheet file to use.
     * @throws Exception Thrown if an error occurred while encoding the image.
     */
    protected void transformFile(File input, File output, File xslt)
    	throws Exception
    {
    	if (input == null) 
    		throw new IllegalArgumentException("No file to transform.");
    	if (output == null) 
    		throw new IllegalArgumentException("No destination file.");
    	if (xslt == null) 
    		throw new IllegalArgumentException("No stylesheet provided.");
    	TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(
				new StreamSource(xslt));
		StreamResult result = new StreamResult(new FileOutputStream(output));
		transformer.transform(new StreamSource(input), result);
    }
    
    /**
     * Parses the specified file and returns the document.
     * 
     * @param file The file to parse.
     * @param schema The schema used to validate the specified file.
     * @return
     * @throws Exception Thrown if an error occurred.
     */
    protected Document parseFile(File file, File schema)
    	throws Exception
    {
    	if (file == null) 
    		throw new IllegalArgumentException("No file to parse.");
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	if (schema != null) {
    		dbf.setValidating(true);   
			dbf.setNamespaceAware(true);
			dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
	        // Set the schema file
	        dbf.setAttribute(JAXP_SCHEMA_SOURCE, schema);
    	}
		DocumentBuilder builder = dbf.newDocumentBuilder();
		return builder.parse(file);
    }
    

    /**
     * Parses the specified file and returns the document.
     * 
     * @param file The file to parse.
     * @param schemaStreamArray The schema as array of stream sources used to validate the specified file.
     * @return
     * @throws Exception Thrown if an error occurred.
     */
    protected Document parseFileWithStreamArray(File file, StreamSource[] schemaStreamArray)
        throws Exception
    {
        if (file == null) 
            throw new IllegalArgumentException("No file to parse.");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true); // This must be set to avoid error : cvc-elt.1: Cannot find the declaration of element 'OME'.
        SchemaFactory sFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        SchemaResolver theTestClassResolver = new SchemaResolver();
        sFactory.setResourceResolver(theTestClassResolver);
        
        Schema theSchema = sFactory.newSchema( schemaStreamArray );

        /*
        // Version - one step parse and validate (print error to stdErr)
        dbf.setSchema(theSchema);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document theDoc = builder.parse(file);
        */

        // Version - two step parse then validate (throws error as exception)
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document theDoc = builder.parse(file);
        Validator validator=theSchema.newValidator();
        validator.validate(new DOMSource(theDoc));
        return theDoc;
    }
    
    /**
     * Transforms the input file using the specified stylesheet stream.
     * 
     * @param input  The file to transform.
     * @param output The destination file.
     * @param xslt   The stylesheet InputStream to use.
     * @throws Exception Thrown if an error occurred.
     */
    protected void transformFileWithStream(File input, File output, InputStream xslt)
        throws Exception
    {
        if (input == null) 
            throw new IllegalArgumentException("No file to transform.");
        if (output == null) 
            throw new IllegalArgumentException("No destination file.");
        if (xslt == null) 
            throw new IllegalArgumentException("No stylesheet provided.");

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(
                new StreamSource(xslt));
        StreamResult result = new StreamResult(new FileOutputStream(output));
        transformer.transform(new StreamSource(input), result);
    }
    
    /**
     * Create a single image with binary.
     *
     * After recent changes on the server to check for existing
     * binary data for pixels, many resetDefaults methods tested
     * below began returning null since {@link omero.LockTimeout}
     * exceptions were being thrown server-side. By using
     * omero.client.forEachTile, we can set the necessary data easily.
     *
     * @see ticket:5755
     */
    protected Image createBinaryImage() throws Exception {
        Image image = mmFactory.createImage();
        image = (Image) iUpdate.saveAndReturnObject(image);
        return createBinaryImage(image);
    }

    /**
     * Create the binary data for the given image.
     */
    protected Image createBinaryImage(Image image) throws Exception {
        Pixels pixels = image.getPrimaryPixels();
        long id = pixels.getId().getValue();
        //Image
        List<Long> ids = new ArrayList<Long>();
        ids.add(image.getId().getValue());
        //method already tested

        // first write to the image
        omero.util.RPSTileLoop loop =
            new omero.util.RPSTileLoop(client.getSession(), pixels);
        loop.forEachTile(256, 256, new omero.util.TileLoopIteration(){
            public void run(omero.util.TileData data, int z, int c, int t, 
            		int x, int y, int tileWidth,
                    int tileHeight, int tileCount) {
                data.setTile(new byte[tileWidth*tileHeight*8], z, c, t, x, y, 
                		tileWidth, tileHeight);
            }
        });
        // This block will change the updateEvent on the pixels
        // therefore we're going to reload the pixels.

        image.setPixels(0, loop.getPixels());
        return image;
    }

}
