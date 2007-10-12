#!/usr/bin/env python

#
# Generated Mon Aug 18 17:12:21 2003 by generateDS.py.
#

import sys
from xml.dom import minidom

import outline as supermod

#
# Support/utility functions.
#

def showIndent(outfile, level):
    for idx in range(level):
        outfile.write('    ')


class outlineSub(supermod.outline):
    def __init__(self, name='', description='', children=None):
        supermod.outline.__init__(self, name, description, children)
    def show(self, outfile, level):
        outfile.write('==========================================\n')
        outfile.write('Outline name: %s\n' % self.name)
        outfile.write('Outline description: %s\n' % self.description)
        outfile.write('==========================================\n')
        for child in self.children:
            child.show(outfile, level)
        outfile.write('==========================================\n')
supermod.outline.subclass = outlineSub
# end class outlineSub


class nodeSub(supermod.node):
    def __init__(self, label='', text='', children=None, hidden=''):
        supermod.node.__init__(self, label, text, children, hidden)
    def show(self, outfile, level):
        if self.hidden == 't':
            return
        showIndent(outfile, level)
        outfile.write('%s.  %s\n' % (self.label, self.text))
        for child in self.children:
            child.show(outfile, level + 1)
supermod.node.subclass = nodeSub
# end class nodeSub



def parse(inFilename):
    doc = minidom.parse(inFilename)
    rootNode = doc.childNodes[0]
    rootObj = supermod.outline.factory()
    rootObj.build(rootNode)
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.childNodes[0]
    rootObj = supermod.outline.factory()
    rootObj.build(rootNode)
    return rootObj


USAGE_TEXT = """
Usage: python outline_extended.py <infilename>
"""

def usage():
    print USAGE_TEXT
    sys.exit(-1)


def main():
    args = sys.argv[1:]
    if len(args) != 1:
        usage()
    infilename = args[0]
    root = parse(infilename)
    sys.stdout.write('<?xml version="1.0" ?>\n')
    root.show(sys.stdout, 0)


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')


