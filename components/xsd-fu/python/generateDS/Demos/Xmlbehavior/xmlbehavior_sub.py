#!/usr/bin/env python

#
# Generated Tue Jun 29 16:14:16 2004 by generateDS.py.
#

import sys
from xml.dom import minidom
from xml.sax import handler, make_parser

import xmlbehavior as supermod

class xml_behaviorSub(supermod.xml_behavior):
    def __init__(self, base_impl_url='', behaviors=None):
        supermod.xml_behavior.__init__(self, base_impl_url, behaviors)

    def get_class_dictionary(self):
        return self.classDictionary

    #
    # Make a dictionary whose keys are class names and whose
    #   values are the behaviors for that class.
    def make_class_dictionary(self, cleanupNameFunc):
        self.classDictionary = {}
        self.behaviors.make_class_dictionary(self.classDictionary, cleanupNameFunc)

supermod.xml_behavior.subclass = xml_behaviorSub
# end class xml_behaviorSub


class behaviorsSub(supermod.behaviors):
    def __init__(self, behavior=None):
        supermod.behaviors.__init__(self, behavior)

    def make_class_dictionary(self, classDictionary, cleanupNameFunc):
        for behavior in self.behavior:
            behavior.make_class_dictionary(classDictionary, cleanupNameFunc)

supermod.behaviors.subclass = behaviorsSub
# end class behaviorsSub


class behaviorSub(supermod.behavior):
    def __init__(self, klass='', name='', return_type='', args=None, impl_url=''):
        supermod.behavior.__init__(self, klass, name, return_type, args, impl_url)

    def make_class_dictionary(self, classDictionary, cleanupNameFunc):
        className = cleanupNameFunc(self.klass)
        if className not in classDictionary:
            classDictionary[className] = []
        classDictionary[className].append(self)

supermod.behavior.subclass = behaviorSub
# end class behaviorSub


class argsSub(supermod.args):
    def __init__(self, arg=None):
        supermod.args.__init__(self, arg)
supermod.args.subclass = argsSub
# end class argsSub


class argSub(supermod.arg):
    def __init__(self, name='', data_type=''):
        supermod.arg.__init__(self, name, data_type)
supermod.arg.subclass = argSub
# end class argSub


class ancillariesSub(supermod.ancillaries):
    def __init__(self, ancillary=None):
        supermod.ancillaries.__init__(self, ancillary)

    #
    # XMLBehaviors
    #
supermod.ancillaries.subclass = ancillariesSub
# end class ancillariesSub


class ancillarySub(supermod.ancillary):
    def __init__(self, klass='', role='', return_type='', name='', args=None, impl_url=''):
        supermod.ancillary.__init__(self, klass, role, return_type, name, args, impl_url)
supermod.ancillary.subclass = ancillarySub
# end class ancillarySub



def saxParse(inFileName):
    parser = make_parser()
    documentHandler = supermod.SaxXml_behaviorHandler()
    parser.setDocumentHandler(documentHandler)
    parser.parse('file:%s' % inFileName)
    rootObj = documentHandler.getRoot()
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def saxParseString(inString):
    parser = make_parser()
    documentHandler = supermod.SaxContentHandler()
    parser.setDocumentHandler(documentHandler)
    parser.feed(inString)
    parser.close()
    rootObj = documentHandler.getRoot()
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def parse(inFilename):
    doc = minidom.parse(inFilename)
    rootNode = doc.childNodes[0]
    rootObj = supermod.xml_behavior.factory()
    rootObj.build(rootNode)
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    doc = None
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.childNodes[0]
    rootObj = supermod.xml_behavior.factory()
    rootObj.build(rootNode)
    doc = None
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def parseLiteral(inFilename):
    doc = minidom.parse(inFilename)
    rootNode = doc.childNodes[0]
    rootObj = supermod.xml_behavior.factory()
    rootObj.build(rootNode)
    #sys.stdout.write('from xmlbehavior_sub import *\n\n')
    #sys.stdout.write('rootObj = xml_behavior(\n')
    #rootObj.exportLiteral(sys.stdout, 0)
    #sys.stdout.write(')\n')
    doc = None
    return rootObj


USAGE_TEXT = """
Usage: python ???.py <infilename>
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
    root.export(sys.stdout, 0)


if __name__ == '__main__':
    main()
    #import pdb
    #pdb.run('main()')


