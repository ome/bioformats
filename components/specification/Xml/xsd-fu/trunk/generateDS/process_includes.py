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
Examples:
    python process_includes.py infile.xsd
    python process_includes.py infile.xsd outfile.xsd
    python process_includes.py infile.xsd > outfile.xsd
    cat infile.py | python process_includes.py > outfile.xsd
"""

#
# Imports

import sys
import os
import getopt
import re

WhichElementTree = ''
try:
    from xml.etree import ElementTree as etree
    WhichElementTree = 'xml.etree'
except ImportError, e:
    try:
        from lxml import etree
        WhichElementTree = 'lxml'
    except ImportError, e:
        try:
            from elementtree import ElementTree as etree
            WhichElementTree = 'elementtree'
        except ImportError, e:
            print '***'
            print '*** Error: Must install >=Python-2.5 or ElementTree or lxml.'
            print '***'
            raise
#print WhichElementTree, etree


#
# Globals and constants

FORCE = False
NAMESPACE_PAT = re.compile(r'\{.*\}')


#
# Classes



#
# Functions


def process_include(inpath, outpath):
    if inpath:
        doc = etree.parse(inpath)
        root = doc.getroot()
        process_include_tree(root)
    else:
        s1 = sys.stdin.read()
        root = etree.fromstring(s1)
        process_include_tree(root)
        doc = etree.ElementTree(root)
    if outpath:
        outfile = make_file(outpath)
        if outfile:
            doc.write(outfile)
            outfile.close()
    else:
        doc.write(sys.stdout)


def process_include_tree(root):
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
            path = child.attrib['schemaLocation']
            if os.path.exists(path):
                doc = etree.parse(path)
                node = doc.getroot()
                process_include_tree(node)
                children1 = node.getchildren()
                for child1 in children1:
                    root.insert(idx, child1)
                    idx += 1
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
    sys.exit(-1)


def main():
    global FORCE
    args = sys.argv[1:]
    try:
        opts, args = getopt.getopt(args, 'hf', ['help', 'force', ])
    except:
        usage()
    name = 'nobody'
    for opt, val in opts:
        if opt in ('-h', '--help'):
            usage()
        elif opt in ('-f', '--force'):
            FORCE = True
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
    process_include(inpath, outpath)


if __name__ == '__main__':
    #import pdb; pdb.set_trace()
    main()


