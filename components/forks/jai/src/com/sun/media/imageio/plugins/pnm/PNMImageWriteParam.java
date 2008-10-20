/*
 * $RCSfile: PNMImageWriteParam.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:16 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.pnm;

import javax.imageio.ImageWriteParam;

/**
 * A subclass of <code>ImageWriteParam</code> for writing images in
 * the PNM format.
 *
 * <p> This class allows for the specification of whether to write
 * in the ASCII or raw variants of the PBM, PGM, and PPM formats;
 * by default, the raw variant is used.
 */
public class PNMImageWriteParam extends ImageWriteParam {

    private boolean raw = true;

    /**
     * Constructs a <code>PNMImageWriteParam</code> object with default values
     * for parameters.
     */
    public PNMImageWriteParam() {}

    /**
     * Sets the representation to be used.  If the <code>raw</code>
     * parameter is <code>true</code>, the raw representation will be used;
     * otherwise the ASCII representation will be used.
     *
     * @param raw <code>true</code> if raw format is to be used.
     * @see #getRaw()
     */
    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    /**
     * Returns the value of the <code>raw</code> parameter.  The default
     * value is <code>true</code>.
     *
     * @return whether the data are written in raw representation.
     * @see #setRaw(boolean)
     */
    public boolean getRaw() {
        return raw;
    }
}
