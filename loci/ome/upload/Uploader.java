//
// Uploader.java
//

/*
OME Upload package for uploading data to an OME server.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.ome.upload;

import java.awt.image.BufferedImage;
import loci.formats.MetadataStore;

public interface Uploader {

  /** Log in to the specified server, with the specified credentials. */
  void login(String server, String user, String pass) throws UploadException;

  /** Log all users out of current server. */
  void logout();

  /** 
   * Upload the planes in the given file to the server, creating a new
   * image in the process.  If the "stitch" flag is set to true, then all files
   * that are similar to the given file will be uploaded as well.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadFile(String file, boolean stitch) throws UploadException;

  /**
   * Upload the planes in the given file to the server, placing them in the
   * given image, and optionally the given dataset.  If the "stitch" flag is
   * set to true, then all files that are similar to the given file will be
   * uploaded as well.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadFile(String file, boolean stitch, Integer image, Integer dataset)
    throws UploadException;

  /** 
   * Upload a single BufferedImage to the server, creating a new 
   * image in the process.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlane(BufferedImage plane, MetadataStore store) 
    throws UploadException;

  /** 
   * Upload a single BufferedImage to the server, placing it in the 
   * given image, and optionally the given dataset.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlane(BufferedImage plane, MetadataStore store, Integer image,
    Integer dataset) throws UploadException;

  /**
   * Upload a single byte array to the server, creating a new
   * image in the process.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlane(byte[] plane, MetadataStore store) throws UploadException;

  /**
   * Upload a single byte array to the server, placing it in the given image,
   * and optionally the given dataset.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlane(byte[] plane, MetadataStore store, Integer image, 
    Integer dataset) throws UploadException;

  /**
   * Upload an array of BufferedImages to the server, creating a new image
   * in the process.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlanes(BufferedImage[] planes, MetadataStore store)
    throws UploadException;

  /**
   * Upload an array of BufferedImages to the server, placing them in the given
   * image, and optionally the given dataset.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlanes(BufferedImage[] planes, MetadataStore store, 
    Integer image, Integer dataset) throws UploadException;

  /**
   * Upload an array of byte arrays to the server, creating a new image
   * in the process.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlanes(byte[][] planes, MetadataStore store) throws UploadException;

  /**
   * Upload an array of byte arrays to the server, placing them in the given
   * image, and optionally the given dataset.
   * 
   * @return the number of pixel bytes uploaded
   */
  int uploadPlanes(byte[][] planes, MetadataStore store, Integer image, 
    Integer dataset) throws UploadException;

}
