
package com.sun.jimi.core.encoder.psd;

import java.io.DataOutputStream;

import com.sun.jimi.core.compat.*;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiImage;

/**
 * Interface for each class which handles variants of PSD image file format
 * encoding of image data.
 **/
public interface EncodeImageIfc
{
	public void encodeImage(AdaptiveRasterImage ji, DataOutputStream out, int compression) throws JimiException;
}


