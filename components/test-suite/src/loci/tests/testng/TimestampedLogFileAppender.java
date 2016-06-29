/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

import loci.common.DateTools;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

/**
 * A class for appending a timestamp to the log file name produced by the
 * logging framework.
 *
 * @author Blazej Pindelski <bpindelski at dundee dot ac dot uk>
 */
public class TimestampedLogFileAppender extends FileAppender<ILoggingEvent> {

    @Override
    public void start() {
        if (this.fileName != null) {
            this.setFile(this.getNewFileName());
        }
        super.start();
    }

    private String getNewFileName() {
        final int lastDotIndex = this.fileName.lastIndexOf(".");
        String newFileName = "";

        String timestamp = "-" + DateTools.getFileTimestamp();

        if (lastDotIndex != -1) {
            newFileName = this.fileName.substring(0, lastDotIndex) +
                    timestamp + this.fileName.substring(lastDotIndex);
        } else {
            newFileName = this.fileName + timestamp;
        }
        return newFileName;
    }
}
