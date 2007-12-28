//
// OMECredentials.java
//

package loci.formats.ome;

/** Stores credentials for logging into an OME/OMERO server. */
public class OMECredentials {
  public String server;
  public String port;
  public String username;
  public String password;
  public long imageID;
  public boolean isOMERO;
}
