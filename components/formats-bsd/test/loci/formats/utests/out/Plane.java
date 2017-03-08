/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package loci.formats.utests.out;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import loci.common.Constants;
import loci.formats.FormatTools;

public class Plane {
  public ByteBuffer backingBuffer;
  public boolean rgbPlanar;
  public int rgbChannels;
  public String pixelType;

  public Plane(byte[] buffer, boolean littleEndian, boolean planar,
    int rgbChannels, String pixelType)
  {
    backingBuffer = ByteBuffer.wrap(buffer);
    backingBuffer.order(
      littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
    this.rgbPlanar = planar;
    this.pixelType = pixelType;
    this.rgbChannels = rgbChannels;
  }

  public boolean equals(Plane other) {
    backingBuffer.position(0);
    other.backingBuffer.position(0);

    int bytes = FormatTools.getBytesPerPixel(pixelType);
    boolean fp =
      FormatTools.isFloatingPoint(FormatTools.pixelTypeFromString(pixelType));

    while (backingBuffer.position() < backingBuffer.capacity()) {
      int otherPos = backingBuffer.position();
      if (rgbPlanar != other.rgbPlanar) {
        int channel = -1;
        int pixel = -1;

        int pos = backingBuffer.position() / bytes;
        int capacity = backingBuffer.capacity();

        if (rgbPlanar) {
          pixel = pos % (capacity / (rgbChannels * bytes));
          channel = pos / (capacity / (rgbChannels * bytes));
        }
        else {
          channel = pos % rgbChannels;
          pixel = pos / rgbChannels;
        }

        if (other.rgbPlanar) {
          otherPos = channel * (capacity / rgbChannels) + pixel * bytes;
        }
        else {
          otherPos = (pixel * rgbChannels + channel) * bytes;
        }
      }

      if (otherPos >= other.backingBuffer.capacity()) {
        break;
      }

      other.backingBuffer.position(otherPos);

      switch (bytes) {
        case 1:
          byte thisB = backingBuffer.get();
          byte otherB = other.backingBuffer.get();

          if (thisB != otherB) {
            if (!pixelType.equals(other.pixelType)) {
              if ((byte) (thisB - 128) != otherB) {
                return false;
              }
            }
            else {
              return false;
            }
          }
          break;
        case 2:
          short thisS = backingBuffer.getShort();
          short otherS = other.backingBuffer.getShort();

          if (thisS != otherS) {
            if (!pixelType.equals(other.pixelType)) {
              if ((short) (thisS - 32768) != otherS) {
                return false;
              }
            }
            else {
              return false;
            }
          }
          break;
        case 4:
          if (fp) {
            float thisF = backingBuffer.getFloat();
            float otherF = other.backingBuffer.getFloat();

            if (Math.abs(thisF - otherF) > Constants.EPSILON) {
              return false;
            }
          }
          else {
            int thisI = backingBuffer.getInt();
            int otherI = other.backingBuffer.getInt();

            if (thisI != otherI) {
              return false;
            }
          }
          break;
        case 8:
          if (fp) {
            double thisD = backingBuffer.getDouble();
            double otherD = other.backingBuffer.getDouble();

            if (Math.abs(thisD - otherD) > Constants.EPSILON) {
              return false;
            }
          }
          else {
            long thisL = backingBuffer.getLong();
            long otherL = other.backingBuffer.getLong();

            if (thisL != otherL) {
              return false;
            }
          }
          break;
      }
    }

    return true;
  }
}
