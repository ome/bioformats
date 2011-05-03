package com.sun.jimi.core.decoder.pcx;

import java.io.IOException;
import java.awt.image.*;



import com.sun.jimi.core.*;
import com.sun.jimi.core.util.P;
import com.sun.jimi.core.util.LEDataInputStream;

public class PCXColorMap
{

    private byte[] red;
    private byte[] green;
    private byte[] blue;

    private ColorModel colorModel;


    public PCXColorMap(LEDataInputStream dis, int fileLength, byte version)
    {
        //get the correct colormap for this version

        if (version == PCXHeader.V3_0p)
        {
            // 256 color map
            // read to the end of file-769 bytes
            // do this in a more optimal way...

            try
            {
        		dis.mark(fileLength);
                dis.skip(fileLength-16-769);

                int control = dis.readByte();

                if (control==12)
                {
                    // 256 color palette
                    // read colors

                    red = new byte[256];
                    green = new byte[256];
                    blue = new byte[256];

                    for (int i=0;i<256;i++)
                    {
                        red[i] = dis.readByte();
                        green[i] = dis.readByte();
                        blue[i] = dis.readByte();
                    }

                    colorModel = new IndexColorModel(8, 256, red, green, blue);

                    dis.reset();
                    dis.skip(48);
                }
                else
                {
                    // no 256 color palette, use 16 color palette
                    dis.reset();
                    red = new byte[16];
                    green = new byte[16];
                    blue = new byte[16];

                    for (int i=0;i<16;i++)
                    {
                        red[i] = dis.readByte();
                        green[i] = dis.readByte();
                        blue[i] = dis.readByte();

                    }

                    colorModel = new IndexColorModel(4, 16, red, green, blue);


                }



            }
            catch (Exception e)
            {
            }

        }


    }

    public ColorModel getColorModel()
    {
        return colorModel;
    }
}
