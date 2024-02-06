package loci.formats.in.LeicaMicrosystemsMetadata.model;

public class Dye {
    public String name; //e.g. Nuclei, ALEXA 488
    public String fluochromeName; //e.g. Hoechst 33342, ALEXA 488
    public String lutName;
    public double excitationWavelength;
    public double emissionWavelength;
    public boolean isAutofluorescence;
}
