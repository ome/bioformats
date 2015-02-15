#!/usr/bin/env python
# encoding: utf-8

from optparse import OptionParser
import os
import sys
import shutil

if __name__ == "__main__":

    parser = OptionParser()
    parser.add_option("--basefile", action="store", type="string", dest="basefile")
    parser.add_option("--prefix", action="store", type="string", dest="platePrefix")
    parser.add_option("--rows", action="store", type="int", dest="plateRows", default=1)
    parser.add_option("--columns", action="store", type="int", dest="plateColumns", default=1)
    parser.add_option("--fields", action="store", type="int", dest="fields", default=1)
    parser.add_option("--channels", action="store", type="int", dest="channels", default=1)

    (options, args) = parser.parse_args(sys.argv)

    suffix = options.basefile[options.basefile.index('.'):]

    for row in (1, options.plateRows):
        for col in (1, options.plateColumns):
            for field in (1, options.fields):
                for channel in (0, options.channels - 1):
                  destFile = '{:s}_{:c}{:02d}f{:02d}d{:1d}{:s}'.format(options.platePrefix, row + 64, col, field, channel, suffix)
                  shutil.copyfile(options.basefile, destFile)
