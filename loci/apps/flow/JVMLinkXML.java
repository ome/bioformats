import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

public class JVMLinkXML {
	
	public static MetadataStore metadataStore;
	
	
	public static void createOMEXMLMetadata() {
		metadataStore = MetadataTools.createOMEXMLMetadata();
		metadataStore.createRoot();
	}
	
	

}
