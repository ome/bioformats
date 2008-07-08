import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

public class JVMLinkXML {
	
	public static MetadataStore metadataStore;
	
	
	public static void createOMEXMLMetadata() {
		metadataStore = MetadataTools.createOMEXMLMetadata();
		metadataStore.createRoot();
	}
	
	public static void setDatasetRefID(String id) {
		// Not sure what goes here.
	}
	
	public static void setDatasetName(String id) {
		// Not sure what function to call here.
	}
	
	public static void setDatasetExperimenter(String id) {
		// Not sure what function to call here.
	}

	public static void setDatasetGroup(String id) {
		// Not sure what function to call here.
	}

	public static void setDatasetLocked(String id) {
		// Not sure what function to call here.
	}

	public static void setProjectID(String id) {
		// Not sure what function to call here.
	}

	public static void setProjectName(String id) {
		// Not sure what function to call here.
	}

	public static void setProjectDescription(String id) {
		// Not sure what function to call here.
	}

	public static void setProjectExperimenter(String id) {
		// Not sure what function to call here.
	}

	public static void setProjectGroup(String id) {
		// Not sure what function to call here.
	}

	public static void setImageID(String id) {
		// Not sure what function to call here.
	}
	
	public static void setImageName(String id) {
		// Not sure what function to call here.
	}

	public static void setImageCreationDate(String id) {
		// Not sure what function to call here.
	}

	public static void setImageDescription(String id) {
		// Not sure what function to call here.
	}

	public static void setImageExperimenter(String id) {
		// Not sure what function to call here.
	}

	public static void setImageDefaultPixels(String id) {
		// Not sure what function to call here.
	}

	public static void setAttributeKeyPair(String key, String value) {
		MetadataTools.populateOriginalMetadata(metadataStore, key, value);
	}


}
