#!/usr/bin/env python
# -*- mode: pymode; coding: latin1; -*-
"""
Synopsis:
    Recusively process the include elements in an XML Schema file.
    Produce a single file that contains all included content.
    Input is read either from a file or from stdin.
    Output is written either to a file or to stdout.
Usage:
    python process_includes.py [options] [ infile [ outfile ] ]
Options:
    -h, --help      Display this help message.
    -f, --force     Force.  If outfile exists, overwrite without asking.
    -s, --search    Search path for schemas.  Colon separated list of directorys where schemas may be found.
    
Examples:
    python process_includes.py infile.xsd
    python process_includes.py infile.xsd outfile.xsd
    python process_includes.py infile.xsd > outfile.xsd
    cat infile.xsd | python process_includes.py > outfile.xsd
"""

#
# Imports

import sys
import os
import getopt
import re
import urllib

#
# Try to import lxml first, and if that fails try ElementTree.
# lxml preserves namespace prefixes, but ElemenTree does not.
#
WhichElementTree = ''
try:
    from lxml import etree
    WhichElementTree = 'lxml'
except ImportError, e:
    from xml.etree import ElementTree as etree
    WhichElementTree = 'elementtree'
if WhichElementTree != 'lxml' or etree.LXML_VERSION[0] < 2:
    print '***'
    print '*** Error: Must install lxml (v. >= 2.0) or use "--no-process-includes".'
    print '***     Override this error by modifying the above test.'
    print '***     But, see the docs before doing so:'
    print '***       http://www.rexx.com/~dkuhlman/generateDS.html#include-file-processing'
    print '***'
    raise RuntimeError, 'Must install lxml (v. >= 2.0) or use "--no-process-includes".'
#print WhichElementTree, etree


#
# Globals and constants

FORCE = False
NAMESPACE_PAT = re.compile(r'\{.*\}')
DIRPATH = []


#
# Classes



#
# Functions


def process_includes(inpath, outpath):
    if inpath:
        infile = open(inpath, 'r')
    else:
        infile = sys.stdin
    if outpath:
        outfile = make_file(outpath)
    else:
        outfile = sys.stdout
    process_include_files(infile, outfile)
    if inpath:
        infile.close()
    if outpath:
        outfile.close()


def process_include_files(infile, outfile):
    doc = etree.parse(infile)
    root = doc.getroot()
    process_include_tree(root)
    doc.write(outfile)


def process_path(root, idx, path):
    count = idx
    doc = etree.parse(path)
    node = doc.getroot()
    process_include_tree(node)
    children1 = node.getchildren()
    for child1 in children1:
        root.insert(count, child1)
        count += 1
    return count

def process_include_tree(root):
    global DIRPATH
    
    idx = 0
    children = root.getchildren()
    while idx < len(children):
        child = children[idx]
        tag = child.tag
        if type(tag) == type(""):
            tag = NAMESPACE_PAT.sub("", tag)
        else:
            tag = None
        if tag == 'include' and 'schemaLocation' in child.attrib:
            root.remove(child)
            locn = child.attrib['schemaLocation']
            path = child.attrib['schemaLocation']
            if os.path.exists(path):
                idx = process_path(root, idx, path)
            else:
                for d in DIRPATH:
                    path = os.path.join(d,locn)
                    if os.path.exists(path):
                        idx = process_path(root, idx, path)
                        break
                else:
                    msg = "Can't find include file %s.  Aborting." % (path, )
                    raise IOError(msg)
        elif tag == 'import' and 'schemaLocation' in child.attrib:
            root.remove(child)
            locn = child.attrib['schemaLocation']
            if locn.startswith('ftp:') or locn.startswith('http:'):
                try:
                    path, msg = urllib.urlretrieve(locn)
                    idx = process_path(root, idx, path)
                except:
                    msg = "Can't retrieve import file %s.  Aborting." % (locn, )
                    raise IOError(msg)
            else:
                if os.path.exists(locn):
                    idx = process_path(root, idx, locn)
                else:
                    for d in DIRPATH:
                        path = os.path.join(d,locn)
                        if os.path.exists(path):
                            idx = process_path(root, idx, path)
                            break
                    else:
                        msg = "Can't find import file %s.  Aborting." % (locn, )
                        raise IOError(msg)
        else:
            process_include_tree(child)
            idx += 1
        children = root.getchildren()


def make_file(outFileName):
    global FORCE
    outFile = None
    if (not FORCE) and os.path.exists(outFileName):
        reply = raw_input('File %s exists.  Overwrite? (y/n): ' % outFileName)
        if reply == 'y':
            outFile = open(outFileName, 'w')
    else:
        outFile = open(outFileName, 'w')
    return outFile


USAGE_TEXT = __doc__

def usage():
    print USAGE_TEXT
    sys.exit(1)


def main():
    global FORCE
    global DIRPATH
    args = sys.argv[1:]
    try:
        opts, args = getopt.getopt(args, 'hfs:', ['help', 'force', 'search=',])
    except:
        usage()
    name = 'nobody'
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-f', '--force'):
            FORCE = True
        elif opt in ('-s', '--search'):
            DIRPATH = val.split(':')
    if len(args) == 2:
        inpath = args[0]
        outpath = args[1]
    elif len(args) == 1:
        inpath = args[0]
        outpath = None
    elif len(args) == 0:
        inpath = None
        outpath = None
    else:
        usage()
    process_includes(inpath, outpath)


if __name__ == '__main__':
    #import pdb; pdb.set_trace()
    main()


