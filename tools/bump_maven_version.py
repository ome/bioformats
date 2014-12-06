#! /env/python
# Script for updating copyright headers across the code

import glob
import re
import sys

version = sys.argv[1]

# Define regular expression objects
artifact_pattern = r"(<groupId>ome</groupId>\n.*\n.*<version>).*(</version>)"
release_version_pattern = r"(<release.version>).*(</release.version>)"


def replace_file(input_path, pattern):
    with open(input_path, "r") as infile:
        regexp = re.compile(pattern)
        new_content = regexp.sub(r"\g<1>%s\g<2>" % version, infile.read())
        with open(input_path, "w") as output:
            output.write(new_content)
            output.close()
        infile.close()

# Replace versions in components pom.xml
for pomfile in (glob.glob("*/*/pom.xml") + glob.glob("*/*/*/pom.xml")):
    replace_file(pomfile, artifact_pattern)

# Replace versions in top-level pom.xml
toplevelpomfile = "pom.xml"
replace_file(toplevelpomfile, artifact_pattern)
replace_file(toplevelpomfile, release_version_pattern)

# Replace STABLE_VERSION in UpgradeChecker
stableversion_pattern = r"(STABLE_VERSION = \").*(\";)"
upgradecheck = "components/formats-bsd/src/loci/formats/UpgradeChecker.java"
replace_file(upgradecheck, stableversion_pattern)
