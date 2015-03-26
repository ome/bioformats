#!/usr/bin/env python

# Find manual pages to be generated
# argv1 = doc dir containing conf.py
# argv2 = manpage directory to contain generated manpages

import imp
import os.path
import sys

if __name__ == "__main__":

    if len(sys.argv) != 3:
        sys.exit("Usage: %s sphinx-dir manpage-dir" % (sys.argv[0]))

    dir = os.path.abspath(sys.argv[1])
    conf = imp.load_source('conf', os.path.join(dir, 'conf.py'))

    for man in conf.man_pages:
        print os.path.join(sys.argv[2], "%s.%s" % (man[1], man[4]))
