/*
 * Copyright 1998 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.jimi.core.util;

import com.sun.jimi.core.JimiImageFactory;

/**
 * Interface for proxied JimiImageFactories, providing a means to
 * access the underlying factory.
 * @author  Luke Gorrie
 * @version $Revision: 1.1 $ $Date: 1999/01/20 11:29:15 $
 */
public interface JimiImageFactoryProxy extends JimiImageFactory
{
	public JimiImageFactory getProxiedFactory();
	public void setProxiedFactory(JimiImageFactory factory);
}

