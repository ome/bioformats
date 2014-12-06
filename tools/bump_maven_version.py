#! /env/python
# Script for updating copyright headers across the code

import glob
import os
import re
import sys

version = sys.argv[1]
pattern = re.compile(r"(.*<groupId>ome</groupId>\n.*\n.*<version>)"
                     ".*(</version>)")

for pomfile in (glob.glob("pom.xml") + glob.glob("*/*/pom.xml") +
                glob.glob("*/*/*/pom.xml")):
    with open(pomfile, "r") as infile:
        instr = infile.read()
        with open(pomfile, "w") as output:
            output.write(pattern.sub(r"\g<1>%s\g<2>" % version, instr))
            output.close()
        infile.close()
    
