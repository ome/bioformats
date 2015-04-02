#!/usr/bin/env python

# Find manual page dependencies
# argv1 = doc dir containing conf.py

import imp
import os.path
import sys

if __name__ == "__main__":

    if len(sys.argv) != 3:
        sys.exit("Usage: %s sphinx-confdir sphinx-srcdir" % (sys.argv[0]))

    dir = os.path.abspath(sys.argv[1])
    conf = imp.load_source('conf', os.path.join(dir, 'conf.py'))

    for man in conf.man_pages:
        print os.path.join(sys.argv[2], "%s%s" %
                           (man[0], conf.source_suffix))
    print os.path.join(dir, 'conf.py')
