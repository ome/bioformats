#!/usr/bin/python

from PIL import Image
import os
import sys
percent = .9

def resize (filename):
  im = Image.open(filename);
  size = im.size
  newWidth = int(size[0] * percent)
  newHeight = int(size[1] * percent)
  newSize = newWidth, newHeight
  newIm = im.resize(newSize)
  im.save("bak." + filename);
  newIm.save(filename);

# ask about resizing file
print sys.argv[1]
s = raw_input("resize image? ")
if s == "y":
  resize(sys.argv[1]);
else:
  print sys.argv[1] + "skipped"

