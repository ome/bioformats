/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

/**
 * A class for appending a timestamp to the log file name produced by the
 * logging framework.
 *
 * @author Blazej Pindelski <bpindelski at dundee dot ac dot uk>
 */
public class TimestampedLogFileAppender extends FileAppender {

    public TimestampedLogFileAppender() {
        
    }
    
    public TimestampedLogFileAppender(Layout layout, String filename,
                    boolean append, boolean bufferedIO, int bufferSize)
                    throws IOException {
        super(layout, filename, append, bufferedIO, bufferSize);
    }

    public TimestampedLogFileAppender(Layout layout, String filename,
                    boolean append) throws IOException {
        super(layout, filename, append);
    }

    public TimestampedLogFileAppender(Layout layout, String filename)
                    throws IOException {
        super(layout, filename);
    }

    @Override
    public void activateOptions() {
        if (this.fileName != null) {
            try {
                this.setFile(this.getNewFileName(), this.fileAppend,
                        this.bufferedIO, this.bufferSize);
            } catch (IOException e) {
                this.errorHandler.error(
                        "Error while adding timestamp to log file name", e,
                        ErrorCode.FILE_OPEN_FAILURE);
            }
        }
    }

    private String getNewFileName() {
        final int lastDotIndex = this.fileName.lastIndexOf(".");
        String newFileName = "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        String timestamp = "-" + sdf.format(new Date());

        if (lastDotIndex != -1) {
            newFileName = this.fileName.substring(0, lastDotIndex) +
                    timestamp + this.fileName.substring(lastDotIndex);
        } else {
            newFileName = this.fileName + timestamp;
        }
        return newFileName;
    }
}