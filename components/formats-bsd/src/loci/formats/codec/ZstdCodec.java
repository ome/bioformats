package loci.formats.codec;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import java.io.EOFException;
import java.io.IOException;
import io.airlift.compress.zstd.ZstdDecompressor;
import loci.formats.UnsupportedCompressionException;

/**
 * This class implements Zstandard decompression.
 *
 * @author Wim Pomp w.pomp at nki.nl
 */
public class ZstdCodec extends BaseCodec {

    @Override
    public byte[] compress(byte[] data, CodecOptions options)
        throws FormatException
    {
        if (data == null || data.length == 0)
            throw new IllegalArgumentException("No data to compress");
        // TODO: Add compression support.
        throw new UnsupportedCompressionException("Zstandard Compression not currently supported.");
    }

    @Override
    public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
        throws FormatException, IOException
    {
        ByteVector bytes = new ByteVector();
        byte[] buf = new byte[8192];
        int r;
        // read until eof reached
        try {
            while ((r = in.read(buf, 0, buf.length)) > 0) bytes.add(buf, 0, r);
        }
        catch (EOFException ignored) { }

        byte[] data = bytes.toByteArray();
        return decompress(data);
    }
    
    @Override
    public byte[] decompress(byte[] data)
        throws FormatException
    {
        return decompress(data, 0, data.length);
    }
    
    public byte[] decompress(byte[] data, int inputOffset, int length)
        throws FormatException
    {
        ZstdDecompressor decompressor = new ZstdDecompressor();
        byte[] output = new byte[(int) ZstdDecompressor.getDecompressedSize(data, inputOffset, length)];
        decompressor.decompress(data, inputOffset, length, output, 0, output.length);
        return output;
    }
}