#!/usr/bin/env python

#
# Generated Fri Jul  2 13:32:50 2004 by generateDS.py.
#

import sys
from xml.dom import minidom
from xml.sax import handler, make_parser

import ??? as supermod

class purchase_orderSub(supermod.purchase_order):
    def __init__(self, customer=None, date='', line_item=None, shipper=None):
        supermod.purchase_order.__init__(self, customer, date, line_item, shipper)

    #
    # XMLBehaviors
    #
    def calculate_average_price(self, date, category, *args):
        pass

    def calculate_average_price_precond(self, date, category, *args):
        pass

    def calculate_average_price_postcond(self, date, category, *args):
        pass

    def calculate_average_price_wrapper(self, date, category, *args):
        self.calculate_average_price_precond(*args)
        self.calculate_average_price(date, category, *args)
        self.calculate_average_price_postcond(*args)

    def get_daily_volume(self, date, category, *args):
        pass

    def get_daily_volume_wrapper(self, date, category, *args):
        self.get_daily_volume(date, category, *args)

supermod.purchase_order.subclass = purchase_orderSub
# end class purchase_orderSub


class customerSub(supermod.customer):
    def __init__(self, name='', address=''):
        supermod.customer.__init__(self, name, address)

    #
    # XMLBehaviors
    #
supermod.customer.subclass = customerSub
# end class customerSub


class line_itemSub(supermod.line_item):
    def __init__(self, description='', per_unit_ounces=0.0, price=0.0, quantity=-1):
        supermod.line_item.__init__(self, description, per_unit_ounces, price, quantity)

    #
    # XMLBehaviors
    #
supermod.line_item.subclass = line_itemSub
# end class line_itemSub


class shipperSub(supermod.shipper):
    def __init__(self, name='', per_ounce_rate=0.0):
        supermod.shipper.__init__(self, name, per_ounce_rate)

    #
    # XMLBehaviors
    #
supermod.shipper.subclass = shipperSub
# end class shipperSub



def saxParse(inFileName):
    parser = make_parser()
    documentHandler = supermod.SaxPurchase_orderHandler()
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
    rootObj = supermod.purchase_order.factory()
    rootObj.build(rootNode)
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    doc = None
    return rootObj


def parseString(inString):
    doc = minidom.parseString(inString)
    rootNode = doc.childNodes[0]
    rootObj = supermod.purchase_order.factory()
    rootObj.build(rootNode)
    doc = None
    #sys.stdout.write('<?xml version="1.0" ?>\n')
    #rootObj.export(sys.stdout, 0)
    return rootObj


def parseLiteral(inFilename):
    doc = minidom.parse(inFilename)
    rootNode = doc.childNodes[0]
    rootObj = supermod.purchase_order.factory()
    rootObj.build(rootNode)
    #sys.stdout.write('from po_sub import *\n\n')
    #sys.stdout.write('rootObj = purchase_order(\n')
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


