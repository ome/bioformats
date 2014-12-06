#! /env/python
# Script for updating copyright headers across the code

import glob
import re
import sys

version = sys.argv[1]

# Define regular expression objects
pattern1 = r"(.*<groupId>ome</groupId>\n.*\n.*<version>).*(</version>)"
regexp1 = re.compile(pattern1)
pattern2 = r"(<release.version>).*(</release.version>)"
regexp2 = re.compile(pattern2)

for pomfile in (glob.glob("pom.xml") + glob.glob("*/*/pom.xml") +
                glob.glob("*/*/*/pom.xml")):
    with open(pomfile, "r") as infile:
        infile_str = infile.read()
        outfile_str = regexp1.sub(r"\g<1>%s\g<2>" % version, infile_str)
        outfile_str = regexp2.sub(r"\g<1>%s\g<2>" % version, outfile_str)
        with open(pomfile, "w") as output:
            output.write(outfile_str)
            output.close()
        infile.close()
