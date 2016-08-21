/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2014 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.tests.testng;

import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ThreadNameBasedDiscriminator implements Discriminator<ILoggingEvent> {
 
    private static final String KEY = "threadName";
 
    private boolean started;
 
    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return Thread.currentThread().getName();
    }
 
    @Override
    public String getKey() {
        return KEY;
    }
 
    public void start() {
        started = true;
    }
 
    public void stop() {
        started = false;
    }
 
    public boolean isStarted() {
        return started;
    }
}
