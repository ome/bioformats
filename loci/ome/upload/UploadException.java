//
// UploadException.java
//

/*
OME Upload package for uploading data to an OME server.
Copyright (C) 2006-@year@ Melissa Linkert.

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

/**
 * UploadException is the exception thrown when something goes wrong
 * when performing an upload operation.
 */
public class UploadException extends Exception {

  public UploadException() { super(); }
  public UploadException(String s) { super(s); }
  public UploadException(String s, Throwable cause) { super(s, cause); }
  public UploadException(Throwable cause) { super(cause); }

}
