package com.sun.jimi.core.encoder.tga;

import com.sun.jimi.core.*;
import com.sun.jimi.core.compat.AdaptiveRasterImage;
import com.sun.jimi.core.util.LEDataOutputStream;

public interface TGAEncoderIfc
{
	public void encodeTGA(AdaptiveRasterImage ji, LEDataOutputStream out) throws JimiException;
}


