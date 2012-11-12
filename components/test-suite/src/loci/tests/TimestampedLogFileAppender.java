package loci.tests;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

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
                        "Error while adding timestamp to log name", e,
                        ErrorCode.FILE_OPEN_FAILURE);
            }
        }
    }

    private String getNewFileName() {
        final int lastDotIndex = this.fileName.lastIndexOf(".");
        String newFileName = "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy:HH-mm-ss");
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